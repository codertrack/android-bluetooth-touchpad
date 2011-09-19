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

import java.util.Arrays;

import tum.betriebsysteme.kostadinov.btframework.sdp.SDPRegister;
import android.content.pm.ActivityInfo;

public class State {

	public static final int UI_STATE_SDP_CONFIGURATION = 0;
	public static final int UI_STATE_DEVICE_LIST = 1;
	public static final int UI_STATE_OPTION_LIST = 2;
	public static final int UI_STATE_OPTION = 3;
	
	
	
	private static int orientation;
	
	private static int uiState;
	
	private static int sdpConfig;
	
	private static boolean trackBallDown = false;

	public static void setOrientation(int orentation) {
		State.orientation = orentation;
	}

	public static int getOrientation() {
		return orientation;
	}

	public static void setUIState(int uiState) {
		State.uiState = uiState;
	}

	public static int getUIState() {
		return uiState;
	}

	public static void setSdpConfig(int sdpConfig) {
		State.sdpConfig = sdpConfig;
	}

	public static int getSdpConfig() {
		return sdpConfig;
	}

	public static boolean configIncludesOption(int index){
		
		return (sdpConfig == SDPRegister.SDP_CONFIG_MOUSE_RELATIVE && CONSTANTS.SDP_CONFIG_RELATIVE_OPTIONS.contains(Integer.toString(index))) ||
		       (sdpConfig == SDPRegister.SDP_CONFIG_MOUSE_ABSOLUTE && CONSTANTS.SDP_CONFIG_ABSOLUTE_OPTIONS.contains(Integer.toString(index))) ;
	}

	public static void setTrackBallDown(boolean trackBallDown) {
		State.trackBallDown = trackBallDown;
	}

	public static boolean isTrackBallDown() {
		return trackBallDown;
	}
	
	
}
