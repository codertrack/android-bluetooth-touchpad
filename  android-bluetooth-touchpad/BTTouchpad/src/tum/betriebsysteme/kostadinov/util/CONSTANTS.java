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
		 "KEYBOARD", "TOUCHPAD", "POINTER (R)", "POINTER (A)", "PAINTPAD", "VOICE"
	};
	
	public static final String SDP_CONFIG_RELATIVE_OPTIONS = "0125";
	public static final String SDP_CONFIG_ABSOLUTE_OPTIONS = "0345";
	
	public static final String BLUETOOTH_NOT_SUPPORTED_MESSAGE 		= "Your Device does not support Bluetooth.";
	public static final String BLUETOOTH_NOT_ENABLED_BY_USER 		= "Bluetooth was not enabled by the user.";
	public static final String SDP_FAILED_ERROR_MESSAGE 			= "Failed to register new record in service discovery protocol. Error Code: ";
	public static final String SOCKET_CONNECTION_ERROR_MESSAGE 		= "Failed to connect to remote device."; 
} 
  