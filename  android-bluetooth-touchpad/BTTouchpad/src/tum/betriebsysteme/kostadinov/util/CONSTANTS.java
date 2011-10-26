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

package tum.betriebsysteme.kostadinov.util;

public class CONSTANTS {

	public static final int CONTROL_CHANNEL_PORT 		= 0x11;
	public static final int INTERRUPTION_CHANNEL_PORT 	= 0x13; 
	
	public static final String[] OPTIONS = {
		 "KEYBOARD", "TOUCHPAD", "POINTER (R)", "POINTER (A)", "PAINTPAD", "VOICE", "GAMEPAD", "PREZI"
	};
	
	public static final String SDP_CONFIG_RELATIVE_OPTIONS = "012567";
	public static final String SDP_CONFIG_ABSOLUTE_OPTIONS = "034567";
	
	public static final String BLUETOOTH_NOT_SUPPORTED_MESSAGE 		= "Your Device does not support Bluetooth.\n\nPlease quit the application with the back button.";
	public static final String BLUETOOTH_NOT_ENABLED_BY_USER 		= "Bluetooth was not enabled.\n\nApplication will quit, so you could enable Bluetooth from the phone's menu.";
	public static final String SDP_FAILED_ERROR_MESSAGE 			= "Failed to register new service record in SDP.\n\nAre you root?\n\nYour phone might not allow registration of new services.\n\nError Code: ";
	public static final String SOCKET_CONNECTION_ERROR_MESSAGE 		= "Connection failure.\n\nIs the device running and in range?\n\nPlease, check if the device has Bluetooth enabled and allows the connection."; 
} 
  