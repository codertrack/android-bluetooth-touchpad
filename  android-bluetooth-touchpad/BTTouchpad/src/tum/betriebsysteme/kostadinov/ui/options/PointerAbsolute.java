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

import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouse;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseAbsolute;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseRelative;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;

public class PointerAbsolute extends Pointer {

	private float movementXFirstByte = 0.0f;
	private  int movementXSecondByte = 0;
	private float movementYFirstByte = 0.0f;
	private  int movementYSecondByte = 0;
	
	public PointerAbsolute(OptionListener optionListener) {
		super(optionListener);
	}	

//	@Override
//	public void onSensorChanged(SensorEvent event) {
//	
//		if (!optionActive) return;
//		
//		x = event.values[0];
//		y = event.values[1];
//		
//		Log.v("POINTER", "x: "+ x+ " y: "+y);
//			
//		// Format y
//		float formY = (Math.abs(y) > MAX_DEGREE_HANDLED) ? 
//				 (y < 0) ? -MAX_DEGREE_HANDLED: MAX_DEGREE_HANDLED : y;
//		
//		float coordY = (HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2) +
//		((formY/MAX_DEGREE_HANDLED)*(HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2));
//		
//		movementYFirstByte = coordY % 0xff;
//		movementYSecondByte = (((int) coordY) / 0xff);
//		
//		float formX;
//		
//		if( initialX < MAX_DEGREE_HANDLED && x > MAX_DEGREE-MAX_DEGREE_HANDLED )
//			formX = (x - (initialX+MAX_DEGREE));
//		
//		else if( initialX > MAX_DEGREE-MAX_DEGREE_HANDLED && x < MAX_DEGREE_HANDLED )
//			formX = ((x+MAX_DEGREE) -initialX);
//		else			
//			formX = x-initialX;
//		
//		formX = (Math.abs(formX) > MAX_DEGREE_HANDLED) ? 
//				 (formX < 0) ? -MAX_DEGREE_HANDLED: MAX_DEGREE_HANDLED : formX;
//		
//		float coordX = (HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2) +
//		((formX/MAX_DEGREE_HANDLED)*(HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2));
//		
//		movementXFirstByte = coordX % 0xff;
//		movementXSecondByte = (((int) coordX) / 0xff);
//		
//		HIDReportMouseAbsolute mouseReport = new HIDReportMouseAbsolute();
//		mouseReport.setPosition(
//				(byte) movementXFirstByte,
//				(byte) movementXSecondByte,
//				(byte) movementYFirstByte,
//				(byte) movementYSecondByte);
//		
//		this.optionListener.onOptionEvent(mouseReport);
//	
//	}
	
	@Override
	public void onEvent(float[] values) {
		
		if (!optionActive) return;
		
		x = values[0];
		y = values[1];
		
		Log.v("POINTER", "x: "+ x+ " y: "+y);
			
		// Format y
		float formY = (Math.abs(y) > MAX_DEGREE_HANDLED) ? 
				 (y < 0) ? -MAX_DEGREE_HANDLED: MAX_DEGREE_HANDLED : y;
		
		float coordY = (HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2) +
		((formY/MAX_DEGREE_HANDLED)*(HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2));
		
		movementYFirstByte = coordY % 0xff;
		movementYSecondByte = (((int) coordY) / 0xff);
		
		float formX;
		
		if( initialX < MAX_DEGREE_HANDLED && x > MAX_DEGREE-MAX_DEGREE_HANDLED )
			formX = (x - (initialX+MAX_DEGREE));
		
		else if( initialX > MAX_DEGREE-MAX_DEGREE_HANDLED && x < MAX_DEGREE_HANDLED )
			formX = ((x+MAX_DEGREE) -initialX);
		else			
			formX = x-initialX;
		
		formX = (Math.abs(formX) > MAX_DEGREE_HANDLED) ? 
				 (formX < 0) ? -MAX_DEGREE_HANDLED: MAX_DEGREE_HANDLED : formX;
		
		float coordX = (HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2) +
		((formX/MAX_DEGREE_HANDLED)*(HIDReportMouseAbsolute.MAX_VALUE_COORDINATE/2));
		
		movementXFirstByte = coordX % 0xff;
		movementXSecondByte = (((int) coordX) / 0xff);
		
		HIDReportMouseAbsolute mouseReport = new HIDReportMouseAbsolute();
		mouseReport.setPosition(
				(byte) movementXFirstByte,
				(byte) movementXSecondByte,
				(byte) movementYFirstByte,
				(byte) movementYSecondByte);
		
		this.optionListener.onOptionEvent(mouseReport);
		
		
	}
	
	@Override
	public void onClick(View view) {
		
		HIDReportMouseAbsolute mouseReport = new HIDReportMouseAbsolute();
		mouseReport.setButton(
		
				(view.getId() == R.id.pointer_button_left)	?	
				HIDReportMouseRelative.MOUSE_LEFT_BUTTON			:
				HIDReportMouseRelative.MOUSE_RIGHT_BUTTON
				
		);
		
		mouseReport.setPosition(
				(byte) movementXFirstByte,
				(byte) movementXSecondByte,
				(byte) movementYFirstByte,
				(byte) movementYSecondByte);
		
		this.optionListener.onOptionEvent(mouseReport);
		
		mouseReport.setButton(HIDReportMouse.EMPTY_KEYCODE);
		this.optionListener.onOptionEvent(mouseReport);
		
		ActivityResource.vibrate(ActivityResource.VIB_VERY_SHORT);
		
	}
	
	
}
