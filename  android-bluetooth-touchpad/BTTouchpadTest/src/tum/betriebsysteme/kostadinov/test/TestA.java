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

public class TestA extends TestBasics{

	/*

	Preassertions:  Bluetooth is enabled
	
	On success: 	The phone is unable to add new service record 
					in the SDP registry.
	
	Note:			Runs successful on HTC Desire.
					Fails on Nexus One. (The target Phone)
				
	 */
	
	@Override
	public void testDisplayBlackBox() {
		
		solo.clickOnText("Relative");
		
		solo.sleep(2000);
		
		assertTrue(solo.searchText("Failed to register new service"));
		
		solo.clickOnButton(0);
		
		solo.sleep(1000);
		
		assertTrue(solo.searchText("Choose your configuration"));
		
		solo.clickOnText("Absolute");
		
		solo.sleep(2000);
		
		assertTrue(solo.searchText("Failed to register new service"));
		
		solo.clickOnButton(0);
		
		solo.sleep(1000);
		
		assertTrue(solo.searchText("Choose your configuration"));
		
		
	}

	
	
}
