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

package tum.betriebsysteme.kostadinov.ui.options;

import tum.betriebsysteme.kostadinov.btframework.report.HIDReport;

public abstract class Option {
	
	public static interface OptionListener {

		public void onOptionEvent(HIDReport hidReport);
		
	}
	
	public static final int OPTION_KEYBOARD_INDEX 	= 0;
	public static final int OPTION_TOUCHPAD_INDEX 	= 1;
	public static final int OPTION_POINTER_R_INDEX 	= 2;
	public static final int OPTION_POINTER_A_INDEX 	= 3;
	public static final int OPTION_PAINTPAD_INDEX 	= 4;
	public static final int OPTION_VOICE_INDEX 		= 5;
	public static final int OPTION_GAMEPAD_INDEX 	= 6;
	
	public static boolean optionActive;
	
	public OptionListener optionListener;

	public Option(OptionListener optionListener){
		this.optionListener = optionListener;
	}
	
	public abstract void initOptionUI();
	
	public abstract void destroyOptionUI();
}
