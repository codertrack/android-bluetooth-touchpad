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
 * Both data reports of regular mouse and a pointer are implementing this pattern.
 * @author Nikolay Kostadinov
 *
 */
public abstract class HIDReportMouse extends HIDReport {
	
	/**
	 * Key code for the mouse left button.
	 */
	public static final int MOUSE_LEFT_BUTTON = 0x01;
	
	/**
	 * Key code for the mouse right button
	 */
	public static final int MOUSE_RIGHT_BUTTON = 0x02;
	
	/**
	 * Set the button, which is pressed.
	 * @param button Either MOUSE_LEFT_BUTTON or MOUSE_RIGHT_BUTTON. The default value in the pattern is EMPTY_KEYCODE.
	 */
	public abstract void setButton(int button);
}
