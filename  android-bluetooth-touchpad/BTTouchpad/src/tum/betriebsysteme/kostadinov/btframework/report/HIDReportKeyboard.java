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

package tum.betriebsysteme.kostadinov.btframework.report;

/**
 * Data report pattern for keyboard event. Contains up to 6 key presses.
 * @author Nikolay Kostadinov
 *
 */
public class HIDReportKeyboard extends HIDReport {
	

	/**
	 * Modifier for left control button. 
	 */
	public static final int LEFT_CONTROL_MODIFIER = 0x01;
	
	/**
	 * Modifier for left shift button. 
	 */
	public static final int LEFT_SHIFT_MODIFIER = 0x02;
	
	/**
	 * Modifier for left alt button. 
	 */
	public static final int LEFT_ALT_MODIFIER = 0x04;
	
	private byte[] report = new byte[]{
		
		(byte) 0xa1, //0
		(byte) 0x01, //1	REPORT ID 
		(byte) 0x00, //2 	MODIFIER
		(byte) 0x00, //3 	RESERVED, DO NOT USE
 		(byte) 0x00, //4 	KEYCODE 0
		(byte) 0x00, //5	KEYCODE 1
		(byte) 0x00, //6	KEYCODE 2
		(byte) 0x00, //7	KEYCODE 3
		(byte) 0x00, //8	KEYCODE 4
		(byte) 0x00  //9	KEYCODE 5
		
	};
	
	/**
	 * Can set modifiers as left or right control or shift buttons. 
	 * @param modifier the value representing the modifier button id.
	 */
	public void setModifier(int modifier){
		report[2] = (byte) modifier; 
	}
	/**
	 * The modifier encoded with the data pattern. Default is 0x00.
	 * @return the modifier's code. 
	 */
	public int getModifier(){
		return report[2];
	}
	
	/**
	 * Returns the key codes, already encoded in the data pattern.
	 * @param index the index of the key code. Must be between 0 and 5.
	 * @return the key code or 0xffff if index < 0 or index > 5
	 */
	public int getKeycode(int index){
		if(index >= 0 && index < 6 ){
			return report[index+4];
		}else return 0xffff;
	}
	
	/**
	 * Set a single key code for a single key press. The default value in the pattern is EMPTY_KEYCODE.
	 * @param keyCode the key code's integer value
	 */
	public void setSingleKeycode(int keyCode){
		report[4] = (byte) keyCode;
	}
	
	/**
	 * Set multiple key codes for multiple presses at the same time. The default value in the pattern is EMPTY_KEYCODE.
	 * @param keyCode_1  the key code's integer value
	 * @param keyCode_2  the key code's integer value
	 * @param keyCode_3  the key code's integer value
	 * @param keyCode_4  the key code's integer value
	 * @param keyCode_5  the key code's integer value
	 * @param keyCode_6  the key code's integer value
	 */
	public void setKeycodes(
							int keyCode_1,
							int keyCode_2,
							int keyCode_3,
							int keyCode_4,
							int keyCode_5,
							int keyCode_6 ){
		
		report[4] = (byte) keyCode_1;
		report[5] = (byte) keyCode_2;
		report[6] = (byte) keyCode_3;
		report[7] = (byte) keyCode_4;
		report[8] = (byte) keyCode_5;
		report[9] = (byte) keyCode_6;
		
	}
	

	@Override
	public byte[] getReportPayload() {
		return report;
	}
	
}
