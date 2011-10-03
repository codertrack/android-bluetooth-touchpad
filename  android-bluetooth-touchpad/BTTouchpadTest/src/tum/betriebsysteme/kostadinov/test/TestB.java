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

public class TestB extends TestBasics {

	/*

	Preassertions:  Bluetooth is enabled
					The computer host has name MasterComputer-0 (you could change that in utils)
					The computer host is unreachable.
					The phone has unmodified Bluez stack and can add new service records in the SDP registry. 
					(HTC Desire will fail this test)
					
	On success: 	The phone is unable to connect and goes back to the list of available hosts.
	
	Note:			Runs successful on Nexus One.
					
				
	 */
	
	
	@Override
	public void testDisplayBlackBox() {
		
		
		solo.clickOnText("Relative");
		
		solo.sleep(2000);
		
		assertFalse(solo.searchText("Failed to register new service"));
		
		assertTrue(solo.searchText("Choose paired Bluetooth device"));
		
		solo.clickOnText(Utils.COMPUTER_HOST_NAME);
		
		solo.sleep(5000);
		
		assertTrue(solo.searchText("Connection failure"));
		
		solo.clickOnButton(0);
		
		assertTrue(solo.searchText("Choose paired Bluetooth device"));
		
		assertTrue(solo.searchText(Utils.COMPUTER_HOST_NAME));
		
	}

	
	
	
}
