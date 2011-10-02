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
 * Data report pattern for event of pointer. 
 * @author Nikolay Kostadinov
 *
 */
public class HIDReportMouseAbsolute extends HIDReportMouse {
	
	/**
	 * The max value for screen coordinate you could send. It is determined by the HIDDescriptor for pointer.
	 */
	public static final float MAX_VALUE_COORDINATE = 1023.0f;

	
	private byte[] report = new byte[]{
			
			(byte) 0xa1, //0
			(byte) 0x02, //1	REPORT ID (2 for Mouse)
			(byte) 0x00, //2 	Button
			(byte) 0x00, //3 	Movement X (first byte)
	 		(byte) 0x00, //4 	Movement X (second byte)
	 		(byte) 0x00, //5 	Movement Y (first byte)
	 		(byte) 0x00, //6 	Movement Y (second byte) 
		};    
		
		@Override
		public void setButton(int button){
			report[2] = (byte) button;  
		}
		
		/**
		 * Set the exact position of the mouse cursor. 
		 * @param x_1 Coordinates on the x-axis.
		 * @param x_2 Plus x_2*0xff to the coordinates on the x-axis. 
		 * @param y_1 Coordinates on the y-axis.
		 * @param y_2 Plus y_2*0xff to the coordinates on the y-axis. 
		 */
		public void setPosition(byte x_1, byte x_2, byte y_1, byte y_2){
			report[3] =  x_1;
			report[4] =  x_2;
			report[5] =  y_1;
			report[6] =  y_2;
		}    


		@Override 
		public byte[] getReportPayload() {
			return report;
		}

}
