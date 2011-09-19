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


import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReport;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouse;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseRelative;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;

public class Touchpad extends Option implements OnTouchListener, OnGestureListener, OnClickListener {

	private GestureDetector gestureDetector;
	
	private static final long HALF_A_SECOND = 500;
	private long lastTapUp = 0;
	private boolean markingScroll = false;
	private static int currentViewID;
	private static int scrollBuffer;
	private static final int SCROLL_BUFFER_SLOWER = 20;
	
	public Touchpad(OptionListener optionListener) {
		super(optionListener);
	}

	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
		
		this.gestureDetector = new GestureDetector(this);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		View touchpad = ActivityResource.inflate(R.layout.touchpad);
		
		View touchview = touchpad.findViewById(R.id.touchpad_touchview);
		View touchscroll = touchpad.findViewById(R.id.touchpad_scorllview);
		
		touchpad.findViewById(R.id.touchpad_button_left).setOnClickListener(this);
		touchpad.findViewById(R.id.touchpad_button_right).setOnClickListener(this);
		
		touchview.setOnTouchListener(this);
		touchscroll.setOnTouchListener(this);	
		
		main.removeAllViews();
		main.addView(touchpad);
		main.invalidate();
		
		optionActive = true;
		
		Log.v("DISPLAY", "W "+ActivityResource.DISPLAY_WIDTH + " H "+ActivityResource.DISPLAY_HEIGHT);
		
	}

	@Override
	public void destroyOptionUI() {
		this.gestureDetector = null;
		optionActive = false;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if(!optionActive) 
			return false;
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			this.markingScroll = false;
		}
			
			currentViewID = view.getId();
			
			return this.gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent event) {
		//do nothing
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
			float velocityY) {	
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent event, float distanceX,
			float distanceY) {
			
			if(	event.getEventTime() - this.lastTapUp < HALF_A_SECOND){
				
				this.markingScroll = true;
			}
			
			int x = 0 - (int) distanceX;
			int y = 0 - (int) distanceY;
			
			int x_over = x / HIDReportMouseRelative.MAX_MOUSE_MOVEMENT;
			int y_over = y / HIDReportMouseRelative.MAX_MOUSE_MOVEMENT;
		
			Log.v("TOUCHPAD", "ON_SCROLL " + x+ " " + y + " "+x_over+" "+y_over);
			
			int count = Math.max(Math.abs(x_over), Math.abs(y_over));
			
			while(count > 0){
				
				HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
				
				if ( currentViewID == R.id.touchpad_scorllview && Math.abs(y) > Math.abs(x) ){
					
					 int comulate = transferToWheelBuffer(-y_over);
					 
					 if(comulate != 0)
					 mouseReport.setWheel(comulate * HIDReportMouseRelative.MAX_MOUSE_MOVEMENT);
				
				}
				else mouseReport.setMovement(x_over * HIDReportMouseRelative.MAX_MOUSE_MOVEMENT,
										y_over * HIDReportMouseRelative.MAX_MOUSE_MOVEMENT);
				
				if(x_over > 0) x--;
				else if(x_over < 0) x++;
				
				if(y_over > 0) x--;
				else if(y_over < 0) x++;
				
				
				this.optionListener.onOptionEvent(mouseReport);
				
				count--;
			}
			
			
			HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
			if ( currentViewID == R.id.touchpad_scorllview && Math.abs(y) > Math.abs(x) ){
				 
				int comulate = transferToWheelBuffer(-y);
				
				 if(comulate != 0)
				 mouseReport.setWheel(comulate % HIDReportMouseRelative.MAX_MOUSE_MOVEMENT);
				
			}
			else{ 
				mouseReport.setMovement( 	x % HIDReportMouseRelative.MAX_MOUSE_MOVEMENT,
											y % HIDReportMouseRelative.MAX_MOUSE_MOVEMENT);
			
			if(markingScroll) mouseReport.setButton(HIDReportMouse.MOUSE_LEFT_BUTTON);
			}
			
			this.optionListener.onOptionEvent(mouseReport);

		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		//do nothing
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		
			HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
			mouseReport.setButton(HIDReportMouseRelative.MOUSE_LEFT_BUTTON);
			this.optionListener.onOptionEvent(mouseReport);
			this.optionListener.onOptionEvent(new HIDReportMouseRelative());
			this.lastTapUp = event.getEventTime();
			
		return true;
	}

	@Override
	public void onClick(View view) {
		
		HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
		mouseReport.setButton(
		
				(view.getId() == R.id.touchpad_button_left)	?	
				HIDReportMouseRelative.MOUSE_LEFT_BUTTON			:
				HIDReportMouseRelative.MOUSE_RIGHT_BUTTON
				
		);
		
		this.optionListener.onOptionEvent(mouseReport);
		this.optionListener.onOptionEvent(new HIDReportMouseRelative());
		ActivityResource.vibrate(ActivityResource.VIB_VERY_SHORT);
	}
	
	private int transferToWheelBuffer(int value){
		scrollBuffer += value;
		int comulate = scrollBuffer / SCROLL_BUFFER_SLOWER;
		if(comulate != 0) scrollBuffer = 0;
		return comulate;
	}

}
