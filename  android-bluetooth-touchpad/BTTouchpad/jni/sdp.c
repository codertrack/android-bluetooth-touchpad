/**
    Copyright (C) 2011 Nikolay Kostadinov
   
    This file is part of BTTouchpad.

    BTTouchpad is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BTTouchpad is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BTTouchpad.  If not, see <http://www.gnu.org/licenses/>. 
    
 */


#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <bluetooth/l2cap.h>
#include <bluetooth/sdp.h>
#include <bluetooth/sdp_lib.h>


/*
 * 		RESOURCE USED:
 *
 * 		[1] HUMAN INTERFACE DESIGN (HID) PROFILE (https://www.bluetooth.org/docman/handlers/DownloadDoc.ashx?doc_id=7108)
 * 		[2]	BLUETOOTH ASSIGNED NUMBERS (http://netlab.cs.ucla.edu/wiki/files/class_of_device.pdf)
 * 		[3] DEVICE CLASS DEFINITION for HID (http://www.usb.org/developers/devclass_docs/HID1_11.pdf)
 * 		[4] USB LANGIDs (http://www.usb.org/developers/docs/USB_LANGIDs.pdf)
 *
 *
 */



//	ERROR CODES:
//	Used to return results up to java code with exit(code)
// 	In Java Code, codes are obtained with process.exitValue()
//	Success codes:
#define CODE_RECORD_REGISTERED 				0
#define CODE_RECORD_UNREGISTERED 			1
//	Failure codes:
#define CODE_COMMAND_PARAMS_ERROR 			11
#define CODE_SDP_SESSION_FAILED 			12
#define CODE_RECORD_REGISTRATION_FAILED 	13
#define CODE_RECORD_REQUEST_FAILED 			14
#define CODE_RECORD_UNREGISTER_FAILED 		15

//	The HANDLE is used as un unique identifier for
static const unsigned int HANDLE = 0xffffffff;

//	LOG-TAG
static const char* TAG = "SDP_EXECUTABLE";


// SUPPORTED COMMANDS:
//	Only register and unregister of one record are supported
static const char* COMMAND_REGISTER 	= "register";
static const char* COMMAND_UNREGISTER 	= "unregister";
static const char* PARAM_RELATIVE 		= "-relative";
static const char* PARAM_ABSOLUTE 		= "-absolute";


//	DEVICE INFORMATION:
static const char* SERVICE_NAME 			= "Android Bluetooth Touchpad";
static const char* AUTHOR_INFO 				= "Kostadinov, Nikolay; nkkostadinov@gmail.com";
static const char* SERVICE_INFO 			= "Simulates bluetooth mouse and bluetooth keyboard via android phone.";

//	PSM: Control and Interruption Channel
static const uint16_t control_channel_psm 			= 0x11;
static const uint16_t interruption_channel_psm 		= 0x13;

//	The record handle is used to identify
static const uint32_t handle = 0x10029;

// 	HID Profile Attributes:
// 	Described in the same order in HID Spec[1], page 84
//	Attribute				ID		Type & Size		required

//	HIDDeviceReleaseNumber	0x0200	uint16			optional
static const uint16_t attr_release_number = 0x0100; 				//	(0x0100) as v. 1.00

//	HIDParserVersion		0x0201	uint16			mandatory
static const uint16_t attr_parser_version = 0x0111; 				//	(0x100) as v. 1.11 (Current HID Parser, part of the USB driver classes)

//	HIDDeviceSubclass		0x0202	uint8			mandatory
static const uint8_t attr_device_subclass = 0xcc;				//	Combo keyboard/pointing device && Remote control. See [2] Page 4
//	HIDCountryCode			0x0203	uint8			mandatory
static const uint8_t attr_country_code = 0x21;					//	0x21 -> U.S. Country Codes are found on [3] Page 23
//	HIDVirtualCable			0x0204	Bool 8			mandatory
static const uint8_t attr_virtual_cable = 1;					//  see [1] Page 36
//	HIDReconnectInitiate	0x0205	Bool 8			mandatory
static const uint8_t attr_reconnect_initiate = 1;			// The device restores the connection, see [1] Page 35

//Following the correct order of definition from the HID specificatioin,
//HIDDescriptorList should be defined here, but for convenience and
//better readability it will be defined at last.

//	HIDLANGIDBaseList		0x0207 Sequence			mandatory
static uint16_t attr_language_base[] = { 0x409, 0x100 }; 		//	First element is HIDLANGID, on [4] Page 5, second is HIDLanguageBase, see [1] Page 88

//	HIDSDPDisable			0x0208	Bool 8			optional
static const uint8_t attr_sdp_disable = 0;					//	SDP, Control and Interruption Channels could be opened at the same time.
//	HIDBatteryPower			0x0209	Bool 8			optional
static const uint8_t attr_battery_power = 0;					//	Battery info is already available for the android user, so no management through HID is required
//	HIDRemoteWake			0x020A	Bool 8			optional
static const uint8_t attr_remote_wake = 1;					//	Just like mouse and keyboard, the device should be able to wake a sleeping/hibernating host.
//	HIDProfileVersion		0x020B	uint16			mandatory
static const uint16_t attr_profile_version = 0x0100;				//	We use the HID Specification V. 1.0.0. (Which is the current spec)
//	HIDSupervisionTimeout	0x020C	uint16			optional
static const uint16_t attr_supervision_timeout = 0x1f40;		// Probvai bez tazi tupnq!
//	HIDNormallyConnectable	0x020D	Bool 8 			optional
static const uint8_t attr_normally_connectable = 0;			//	The device restores the connection, see [1] Page 35
//	HIDBootDevice			0x020E	Bool 8			mandatory
static const uint8_t attr_boot_device = 0;					// We won't support Set_Protocol and Get_Protocol Commands

//	We define two descriptors. Both of them contain the keyboard functionality.
//	A) One contains a mouse sending RELATIVE values
//	B) and other contains a mouse sending ABSOLUTE values,
//	when changing position.

//	A) Keyboard + Mouse (RELATIVE)
//	HIDDescriptorList		0x0206 Sequence			mandatory
static const uint8_t attr_hid_descriptor_relative[] = {

						0x05, 0x01, // usage page
						0x09, 0x06, // keyboard
						0xa1, 0x01, // key codes
						0x85, 0x01, // minimum
						0x05, 0x07, // max
						0x19, 0xe0, // logical min
						0x29, 0xe7, // logical max
						0x15, 0x00, // report size
						0x25, 0x01, // report count
						0x75, 0x01, // input data variable absolute
						0x95, 0x08, // report count
						0x81, 0x02, // report size
						0x75, 0x08,
						0x95, 0x01,
						0x81, 0x01,
						0x75, 0x01,
						0x95, 0x05,
						0x05, 0x08,
						0x19, 0x01,
						0x29, 0x05,
						0x91, 0x02,
						0x75, 0x03,
						0x95, 0x01,
						0x91, 0x01,
						0x75, 0x08,
						0x95, 0x06,
						0x15, 0x00,
						0x26, 0xff,
						0x00, 0x05,
						0x07, 0x19,
						0x00, 0x2a,
						0xff, 0x00,
						0x81, 0x00,
						0x75, 0x01,
						0x95, 0x01,
						0x15, 0x00,
						0x25, 0x01,
						0x05, 0x0c,
						0x09, 0xb8,
						0x81, 0x06,
						0x09, 0xe2,
						0x81, 0x06,
						0x09, 0xe9,
						0x81, 0x02,
						0x09, 0xea,
						0x81, 0x02,
						0x75, 0x01,
						0x95, 0x04,
						0x81, 0x01,
						0xc0,         // end tag

						0x05, 0x01, // Usage Page (Generic Desktop)
						0x09, 0x02, // Usage (Mouse)
						0xa1, 0x01, // Collection (Application)
						0x85, 0x02, // REPORT ID 2
						0x09, 0x01, // Usage (Pointer)
						0xa1, 0x00, // Collection (Physical)
						0x05, 0x09, // Usage Page (Buttons)
						0x19, 0x01, // Usage Minimum (01)
						0x29, 0x03, // Usage Maximun (03)
						0x15, 0x00, // Logical Minimum (0)
						0x25, 0x01, // Logical Maximum (1)
						0x95, 0x03, // Report Count (3)
						0x75, 0x01, // Report Size (1)
						0x81, 0x02, // Input (Data, Variable, Absolute), ;3 button bits
						0x95, 0x01, // Report Count (1)
						0x75, 0x05, // Report Size (5)
						0x81, 0x01, // Input (Constant),   ;5 bit padding
						0x05, 0x01, // Usage Page (Generic Desktop)
						0x09, 0x30, // Usage (X)
						0x09, 0x31, // Usage (Y)
						0x09, 0x38, // Usage (Scroll Wheel)
						0x15, 0x81, // Logical Minimum (-127)
						0x25, 0x7f, // Logical Maximum (127)
						0x75, 0x08, // Report Size (8)
						0x95, 0x03, // Report Count (3) ?
						0x81, 0x06, // Input (Data, Variable, Relative), ;2 position bytes (X & Y)
						0xc0, 0xc0  // End Collection, End Collection


					};

//	B) Keyboard + Mouse (ABSOLUTE)
//	HIDDescriptorList		0x0206 Sequence			mandatory
static const uint8_t attr_hid_descriptor_absolute[] = {

						0x05, 0x01, // usage page
						0x09, 0x06, // keyboard
						0xa1, 0x01, // key codes
						0x85, 0x01, // minimum
						0x05, 0x07, // max
						0x19, 0xe0, // logical min
						0x29, 0xe7, // logical max
						0x15, 0x00, // report size
						0x25, 0x01, // report count
						0x75, 0x01, // input data variable absolute
						0x95, 0x08, // report count
						0x81, 0x02, // report size
						0x75, 0x08,
						0x95, 0x01,
						0x81, 0x01,
						0x75, 0x01,
						0x95, 0x05,
						0x05, 0x08,
						0x19, 0x01,
						0x29, 0x05,
						0x91, 0x02,
						0x75, 0x03,
						0x95, 0x01,
						0x91, 0x01,
						0x75, 0x08,
						0x95, 0x06,
						0x15, 0x00,
						0x26, 0xff,
						0x00, 0x05,
						0x07, 0x19,
						0x00, 0x2a,
						0xff, 0x00,
						0x81, 0x00,
						0x75, 0x01,
						0x95, 0x01,
						0x15, 0x00,
						0x25, 0x01,
						0x05, 0x0c,
						0x09, 0xb8,
						0x81, 0x06,
						0x09, 0xe2,
						0x81, 0x06,
						0x09, 0xe9,
						0x81, 0x02,
						0x09, 0xea,
						0x81, 0x02,
						0x75, 0x01,
						0x95, 0x04,
						0x81, 0x01,
						0xc0,         // end tag

						0x05, 0x01, // Usage Page (Generic Desktop)
						0x09, 0x02, // Usage (Mouse)
						0xa1, 0x01, // Collection (Application)
						0x85, 0x02, // REPORT ID 2
						0x09, 0x01, // Usage (Pointer)
						0xa1, 0x00, // Collection (Physical)
						0x05, 0x09, // Usage Page (Buttons)
						0x19, 0x01, // Usage Minimum (01)
						0x29, 0x03, // Usage Maximun (03)
						0x15, 0x00, // Logical Minimum (0)
						0x25, 0x01, // Logical Maximum (1)
						0x95, 0x03, // Report Count (3)
						0x75, 0x01, // Report Size (1)
						0x81, 0x02, // Input (Data, Variable, Absolute), ;3 button bits
						0x95, 0x01, // Report Count (1)
						0x75, 0x05, // Report Size (5)
						0x81, 0x01, // Input (Constant),   ;5 bit padding
						0x05, 0x01, // Usage Page (Generic Desktop)
						0x09, 0x30, // Usage (X)
						0x09, 0x31, // Usage (Y)
						0x36, 0x00, 0x00, // 		Physical minimum  = 0
					    0x46, 0xFF, 0x03, // Physical maximum = 1023 or 0x03ff
					    0x16, 0x00, 0x00,/*      Logical Minimum = 0              */
					    0x26, 0xFF, 0x03, // Logical Maximum = 1023 or 0x03ff            */
					    0x75, 0x10, /*      Report Size (16)                     */
					    0x95, 0x02, /*      Report Count (2)                    */
					    0x81, 0x62, /*      Input (Data, Variable, Relative) 0x06 orig  02-absolute  */
						0xc0, 0xc0  // End Collection, End Collection

					};

// We also define the Decriptor Type: Type = Report Descriptor (0x22)
static uint8_t hid_descriptor_type = 0x22;




void main(int argc, char *argv[]){

	//	At least one command must be given: register or unregister
	if(argc < 2)
		exit(CODE_COMMAND_PARAMS_ERROR);


	if (0 == strcmp(argv[1], COMMAND_REGISTER ))
		{

		//	If the register command is passed, there must be at least one param,
		//	and it must be either -relative or -absolute
		if(argc < 3 && (0 != strcmp(argv[2], PARAM_RELATIVE) || 0 != strcmp(argv[2], PARAM_ABSOLUTE) ) )
			exit(CODE_COMMAND_PARAMS_ERROR);

		//	LOGCAT:
		__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Starting registration.");

		//	We pass the descriptor type (relative or absolute) as bool to
		//	the register function
		exit( register_record ( ( 0 == strcmp( argv[2], PARAM_RELATIVE ) ) ) );

		}
	
	//	If the unregister command is given we execute the unregister function
	//	no params are expected
	if (0 == strcmp(argv[1], COMMAND_UNREGISTER ))
			{

			//	LOGCAT:
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Starting unregistration.");

			exit(unregister_record());

			}
	
	exit(CODE_COMMAND_PARAMS_ERROR);
}

int register_record(int descriptor_is_relative){

		// Connect to SDP-Registry on localhost, to publish service information
		// Session will be established.
		sdp_session_t *session;

		session = sdp_connect ( BDADDR_ANY, BDADDR_LOCAL, SDP_RETRY_IF_BUSY );

			if ( ! session )
			{

				//	LOGCAT:
				__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Connection to SDP failed.");

				return CODE_SDP_SESSION_FAILED;
			}

				//	LOGCAT:
				__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Connection to SDP established.");

				//Creating the record, to be included in the SDP registry
				sdp_record_t record;

				//To create the record, several lists must be put together
				sdp_list_t *languages, *service_class_id, *profile_sequence, *access_protocol_sequence, *root_list, *access_protocol, *protocol_list[3];

				//	The uuid_t data type is used to represent the 128-bit UUID that
				//	identifies the service on different levels of the stack: root, l2cap, hid
				uuid_t root_uuid, hid_uuid, l2cap_uuid, hidp_uuid;

				//	SDP Profile description
				sdp_profile_desc_t sdp_profile[1];

				sdp_lang_attr_t base_language;

				//	The sdp_data_t structure stores each element of information in a service record
				sdp_data_t *psm, *hid_descriptor_list, *hid_descriptor_formatted_list, *language_base_list, *language_base_formatted_list;

				//	SDP-specific type needed when adding the descriptor and the language base
				uint8_t data_type_uint8 	= SDP_UINT8;
				uint8_t data_type_uint16 	= SDP_UINT16;
				uint8_t data_type_str8	 	= SDP_TEXT_STR8;

				//	length of the HID descriptor
				int length[2];

				//	arrays used to form the sdp specific strcuture of sequence attribites: descriptor and language base
				void *data_types[2] , *descriptor_array[2], *language_base_array[2];

				memset(&record, 0, sizeof(sdp_record_t));

				//Assign the
				record.handle = handle;

				//	To create the record, several lists must be put together.
				// 	Each list describes the record on different level of the
				//	bluetooth layered architecture.
				//	The only documentation on this is found on: http://people.csail.mit.edu/albert/bluez-intro/x604.html

			    // Setting the Browse Group and make the service record publicly browsable.
			    sdp_uuid16_create(&root_uuid, PUBLIC_BROWSE_GROUP);
			    root_list = sdp_list_append(0, &root_uuid);
			    sdp_set_browse_groups( &record, root_list );


				/* UTF-8 MIBenum (http://www.iana.org/assignments/character-sets) */
				base_language.code_ISO639 = (0x65 << 8) | 0x6e;
				base_language.base_offset = SDP_PRIMARY_LANG_BASE;
				base_language.encoding = 106;

				languages = sdp_list_append(0, &base_language);
				sdp_set_lang_attr(&record, languages);
				sdp_list_free(languages, 0);


				//	Set a HID Service Class
				sdp_uuid16_create(&hid_uuid, HID_SVCLASS_ID);
				service_class_id = sdp_list_append(0, &hid_uuid);
				sdp_set_service_classes(&record, service_class_id);

				//	Create HID Profile UUID, adjust the profile version
				//	and set it to the record
				sdp_uuid16_create(&sdp_profile[0].uuid, HID_PROFILE_ID);
				sdp_profile[0].version = 0x0100;
				profile_sequence = sdp_list_append(0, sdp_profile);
				sdp_set_profile_descs(&record, profile_sequence);

				//	We set information about:
				//	A) Control Channel
				//	B) Interruption Channel
				//	We use the protocol_list to bind information
				//	about L2CAP level and HID Level


				//	A) Control Channel PSM: 0x11
				sdp_uuid16_create(&l2cap_uuid, L2CAP_UUID);
				protocol_list[1] = sdp_list_append(0, &l2cap_uuid);
				psm = sdp_data_alloc(SDP_UINT16, &control_channel_psm);
				protocol_list[1] = sdp_list_append(protocol_list[1], psm);
				access_protocol_sequence = sdp_list_append(0, protocol_list[1]);
					
				sdp_uuid16_create(&hidp_uuid, HIDP_UUID);
				protocol_list[2] = sdp_list_append(0, &hidp_uuid);
				access_protocol_sequence = sdp_list_append(access_protocol_sequence, protocol_list[2]);

				access_protocol = sdp_list_append(0, access_protocol_sequence);
				sdp_set_access_protos(&record, access_protocol);

				//	B) Interruption Channel PSM: 0x13
				protocol_list[1] = sdp_list_append(0, &l2cap_uuid);
				psm = sdp_data_alloc(SDP_UINT16, &interruption_channel_psm);
				protocol_list[1] = sdp_list_append(protocol_list[1], psm);
				access_protocol_sequence = sdp_list_append(0, protocol_list[1]);

				sdp_uuid16_create(&hidp_uuid, HIDP_UUID);
				protocol_list[2] = sdp_list_append(0, &hidp_uuid);
				access_protocol_sequence = sdp_list_append(access_protocol_sequence, protocol_list[2]);

				access_protocol = sdp_list_append(0, access_protocol_sequence);
				sdp_set_add_access_protos(&record, access_protocol);


				//	Set the addition information about device and author
				sdp_set_info_attr(&record, SERVICE_NAME, SERVICE_INFO, AUTHOR_INFO);



				//	Adding all HID attributes (mandatory or optional) specified to the service record
				//	in the same order as they are given in [1] Bluetooth HID Specification

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_DEVICE_RELEASE_NUMBER,
										SDP_UINT16,
										&attr_release_number);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_PARSER_VERSION,
										SDP_UINT16,
										&attr_parser_version);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_DEVICE_SUBCLASS,
										SDP_UINT8,
										&attr_device_subclass);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_COUNTRY_CODE,
										SDP_UINT8,
										&attr_country_code);


					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_VIRTUAL_CABLE,
										SDP_BOOL,
										&attr_virtual_cable);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_RECONNECT_INITIATE,
										SDP_BOOL,
										&attr_reconnect_initiate);


					//	Adding the HID Descriptor attribute is more complex,
					//  thus it is sequence.
					//
					//	The attribute contains two things:
					// 	A) The descriptor type (In our case it is a report descriptor)
					//  B) The descriptor itself

					//  The decriptor type is a simple 8-bit-intger
					//  The descriptor itself is a text string
					//  We put both types (str8 and int8) in a array
					data_types[0] = &data_type_uint8;
					data_types[1] = &data_type_str8;

					// After that we put them itself in another array
					if(descriptor_is_relative){

					descriptor_array[0] = &hid_descriptor_type;
					descriptor_array[1] =(uint8_t *) attr_hid_descriptor_relative;
					length[0] = 0;
					length[1] = sizeof(attr_hid_descriptor_relative);

					}else{

					descriptor_array[0] = &hid_descriptor_type;
					descriptor_array[1] =(uint8_t *) attr_hid_descriptor_absolute;
					length[0] = 0;
					length[1] = sizeof(attr_hid_descriptor_absolute);

					}
					//	We bind the array containing the types and the array containing the values
					//  with the  sdp_seq_alloc_with_length function
					hid_descriptor_list = sdp_seq_alloc_with_length( data_types, descriptor_array, length, 2 );

					//	The newly created array contains all information, so it is used to create
					//  the list from the right SDP-specific type (SDP_SEQ8)
					hid_descriptor_formatted_list = sdp_data_alloc( SDP_SEQ8, hid_descriptor_list );
					//	Finally the list is added as un SDP-attribute
					sdp_attr_add( &record, SDP_ATTR_HID_DESCRIPTOR_LIST, hid_descriptor_formatted_list );



					//	The same is also done with the language base attribute
					data_types[0] = &data_type_uint16;
					data_types[1] = &data_type_uint16;
					language_base_array[0] = &attr_language_base[0];
					language_base_array[1] = &attr_language_base[1];

					//sizeof(attr_language_base) is devided by two, since values are 16-bit integers (not 8-bit like with the decriptor)
					language_base_list = sdp_seq_alloc(data_types, language_base_array, sizeof(attr_language_base) / 2);
					language_base_formatted_list = sdp_data_alloc(SDP_SEQ8, language_base_list);
					sdp_attr_add(&record, SDP_ATTR_HID_LANG_ID_BASE_LIST, language_base_formatted_list);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_SDP_DISABLE,
										SDP_BOOL,
										&attr_sdp_disable);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_BATTERY_POWER,
										SDP_BOOL,
										&attr_battery_power);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_REMOTE_WAKEUP,
										SDP_BOOL,
										&attr_remote_wake);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_PROFILE_VERSION,
										SDP_UINT16,
										&attr_profile_version);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_SUPERVISION_TIMEOUT,
										SDP_UINT16,
										&attr_supervision_timeout);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_NORMALLY_CONNECTABLE,
										SDP_BOOL,
										&attr_normally_connectable);

					sdp_attr_add_new(	&record,
										SDP_ATTR_HID_BOOT_DEVICE,
										SDP_BOOL,
										&attr_boot_device);

					//At last the record could be added to the sdp registry!
					if (sdp_record_register(session, &record, SDP_RECORD_PERSIST) < 0) {

						//	LOGCAT:
						__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Registration of new record failed.");
						return CODE_RECORD_REGISTRATION_FAILED;

					}

					//	LOGCAT:
					__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Registration of new record successful.");

			sdp_close(session);

			return CODE_RECORD_REGISTERED;
	
}

int unregister_record(){

		uint32_t	range=0x0000ffff;
		sdp_list_t    *	attr;
		sdp_session_t *	session;
		sdp_record_t  *	rec;

		// Connect to the local SDP daemon
		session = sdp_connect(BDADDR_ANY, BDADDR_LOCAL, SDP_RETRY_IF_BUSY);

		if ( !session ){

			//	LOGCAT:
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Connection to SDP failed.");
			return CODE_SDP_SESSION_FAILED;

		}

		attr = sdp_list_append(0, &range);

		rec = sdp_service_attr_req(session, handle, SDP_ATTR_REQ_RANGE, attr);

		sdp_list_free(attr, 0);

		if ( !rec ) {

			//	LOGCAT:
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Record request has failed.");

			sdp_close(session);
			return CODE_RECORD_REQUEST_FAILED;
		}



		if (sdp_device_record_unregister(session, BDADDR_ANY, rec)) {

			//	LOGCAT:
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Record unregistration has failed.");

			sdp_close(session);
			return CODE_RECORD_UNREGISTER_FAILED;

		}

		//	LOGCAT:
		__android_log_print(ANDROID_LOG_DEBUG, TAG, "SDP: Unregistration successful.");

		sdp_close(session);
		return CODE_RECORD_UNREGISTERED;
	
}

