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

package tum.betriebsysteme.kostadinov.test;

import android.widget.EditText;

public class TestC extends TestBasics {

	/*

	Preassertions:  Bluetooth is enabled
					The computer host has name MasterComputer-0 (you could change that in utils)
					The computer host is reachable.
					The phone has unmodified Bluez stack and can add new service records in the SDP registry. 
					(HTC Desire will fail this test)
					The user interrupts the Bluetooth connection manually,
					once the mouse starts running on the screen.
					
					
	On success: 	The phone is able to connect. Chooses the PAINTPAD option.
					Starts moving the mouse cursor of the computer. (Then User interrupts connetion)
					Shows "Connection failure dialog".
					Shows list of available hosts.
	
	Note:			Runs successful on Nexus One.
					
				
	 */

	@Override
	public void testDisplayBlackBox() {
		
		solo.clickOnText("Absolute");
		
		solo.sleep(2000);
		
		assertFalse(solo.searchText("Failed to register new service"));
		
		assertTrue(solo.searchText("Choose paired Bluetooth device"));
		
		solo.clickOnText(Utils.COMPUTER_HOST_NAME);
		
		solo.sleep(5000);
		
		assertFalse(solo.searchText("Connection failure"));
		
		assertTrue(solo.searchText("KEYBOARD"));
		
		solo.clickOnText("PAINTPAD");
		
		boolean interrupted = false;
		
		for(int i=0;i<10;i++){
			solo.clickOnScreen(40*i, 40*i);
			if(solo.searchText("Connection failure")){
				interrupted = true;
				break;
			}
			solo.sleep(1000);
		}
		
		if(interrupted){
			assertTrue(solo.searchText("Connection failure"));
			solo.clickOnButton(0);
			assertTrue(solo.searchText("Choose paired Bluetooth device"));
		}
		
	}
	
}
