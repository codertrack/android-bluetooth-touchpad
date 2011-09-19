#    Copyright (C) 2011 Nikolay Kostadinov
   
#    This file is part of BTTouchpad.

#    BTTouchpad is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.

#    BTTouchpad is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.

#    You should have received a copy of the GNU General Public License
#    along with BTTouchpad.  If not, see <http://www.gnu.org/licenses/>. 
    

 
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Copy the executable from <project>/lib/armeabi/ to <project>/assets/
# From where it could be easily deployed and executed ot the device
LOCAL_MODULE := sdp

#	The C-executable source file
LOCAL_SRC_FILES := sdp.c 

#	To compile, you need to:
#	1) set the headers from the android bluez source, at LOCAL_C_INCLUDES
#	2) copy the ./system/lib/libbluetooth.so from rooted android phone 
#	to <ndk>/platforms/android-X/arch-arm/usr/lib
	
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../android_source/external/bluetooth/bluez/lib/ \
					$(LOCAL_PATH)/../../../android_source/system/core/include

LOCAL_LDLIBS := -llog -lbluetooth

include $(BUILD_EXECUTABLE)