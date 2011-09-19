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
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseAbsolute;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseRelative;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;

public class Paintpad extends Option implements OnGestureListener, OnTouchListener {

	private float movementXFirstByte = 0.0f;
	private  int movementXSecondByte = 0;
	private float movementYFirstByte = 0.0f;
	private  int movementYSecondByte = 0;
	
	private GestureDetector gestureDetector;
	
		public Paintpad(OptionListener optionListener) {
		super(optionListener);
	}

	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
		
		this.gestureDetector = new GestureDetector(this);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		View touchpad = new View(ActivityResource.get());
		 
		touchpad.setOnTouchListener(this);
			
		main.removeAllViews();
		main.addView(touchpad, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		main.invalidate();
		
		optionActive = true;
		
		Log.v("DISPLAY PAINTPAD", "W "+ActivityResource.DISPLAY_WIDTH + " H "+ActivityResource.DISPLAY_HEIGHT);
		
	}

	@Override
	public void destroyOptionUI() {
		this.gestureDetector = null;
		optionActive = false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if( !optionActive ) return false;
		
		else if(event.getAction() == MotionEvent.ACTION_UP)
		
		{
		
			updateMovementBytes(event);
			
			HIDReportMouseAbsolute mouseReport = new HIDReportMouseAbsolute();
			mouseReport.setMovement(
					(byte) movementXFirstByte,
					(byte) movementXSecondByte,
					(byte) movementYFirstByte,
					(byte) movementYSecondByte);
			
			optionListener.onOptionEvent(mouseReport);
			
			return true;
		}
		
		else return this.gestureDetector.onTouchEvent(event);
	
	}

	@Override
	public boolean onDown(MotionEvent event) {
		return true;
	}
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent event, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent event, float distanceX,
			float distanceY) {
		
		updateMovementBytes(event);
		
		HIDReportMouseAbsolute mouseReport = new HIDReportMouseAbsolute();
		
		if(State.isTrackBallDown())
			mouseReport.setButton(HIDReportMouseRelative.MOUSE_LEFT_BUTTON);
		
		mouseReport.setMovement(
				(byte) movementXFirstByte,
				(byte) movementXSecondByte,
				(byte) movementYFirstByte,
				(byte) movementYSecondByte);
		
		optionListener.onOptionEvent(mouseReport);
		
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
 
	private void updateMovementBytes(MotionEvent event){
		
		
		
		float x = event.getX();
		float y = event.getY();
		
		Log.v("XY", "x "+x+" y "+y);
		
		float coordX = ( 1-x/ActivityResource.DISPLAY_WIDTH ) * HIDReportMouseAbsolute.MAX_VALUE_COORDINATE;
		float coordY = ( 1-y/ActivityResource.DISPLAY_HEIGHT ) * HIDReportMouseAbsolute.MAX_VALUE_COORDINATE;
		
		movementXFirstByte = coordX % 0xff;
		movementXSecondByte = (((int) coordX) / 0xff);
		
		movementYFirstByte = coordY % 0xff;
		movementYSecondByte = (((int) coordY) / 0xff);
	}
	
	
	
}
