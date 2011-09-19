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

public class HIDReportMouseRelative extends HIDReportMouse {
	
	public static final int MAX_MOUSE_MOVEMENT = 127;

	
	private byte[] report = new byte[]{
			
			(byte) 0xa1, //0
			(byte) 0x02, //1	REPORT ID (2 for Mouse)
			(byte) 0x00, //2 	Button
			(byte) 0x00, //3 	Movement X
	 		(byte) 0x00, //4 	Movement Y
			(byte) 0x00,  // 5	Wheel
			  
		};     
		
		@Override
		public void setButton(int button){
			report[2] = (byte) button;  
		} 
		
		public void setMovement(int x, int y){
			report[3] = (byte) x;
			report[4] = (byte) y;
		}    
		
		public void setMovement(float x, float y){
			report[3] = (byte) x;
			report[4] = (byte) y; 
		}
		
		public void setWheel(int wheel){
			report[5] = (byte) wheel; 
		} 
		 

		@Override 
		public byte[] getReportPayload() {
			return report;
		}
 
	
	
	
}
