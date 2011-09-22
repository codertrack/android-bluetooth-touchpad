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
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Gamepad extends Option implements SensorEventListener {

	public enum Direction{
		LEFT,RIGHT,NEUTRAL
	}
	
	private View[] button;
	private View urButton;
	private View blButton;
	private View brButton;
	
	private Direction direction = Direction.NEUTRAL;
	
	private static final String TAG = "GAMEPAD";
	
	private static HIDReportKeyboard report;
	private SensorManager sensorManager;
	
	public Gamepad(OptionListener optionListener) {
		super(optionListener);	
	}

	@Override
	public void initOptionUI() {
		State.setUIState(State.UI_STATE_OPTION);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		
		report = new HIDReportKeyboard();
		
		View gamepadView = ActivityResource.inflate(R.layout.gamepad);
		
		button = new View[4];
		
		button[0] = gamepadView.findViewById(R.id.gamepad_upper_left);
		button[1] = gamepadView.findViewById(R.id.gamepad_upper_right);
		button[2] = gamepadView.findViewById(R.id.gamepad_bottom_left);
		button[3] = gamepadView.findViewById(R.id.gamepad_bottom_right);
		
		sensorManager = (SensorManager) ActivityResource.get().getSystemService(Context.SENSOR_SERVICE);
        
        sensorManager.registerListener(this,
		sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
		SensorManager.SENSOR_DELAY_UI);
		
		main.removeAllViews();
		main.addView(gamepadView);
		main.invalidate();
		
		optionActive = true;   
	}

	@Override
	public void destroyOptionUI() {
		sensorManager.unregisterListener(this);
		sensorManager = null;
		optionActive = false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if(!this.optionActive) return;
		
		int value = (int) event.values[1];
		
		if(value<-8 && !this.direction.equals(Direction.RIGHT)){
			this.direction = Direction.RIGHT;
			
			report.setKeycodes(
					report.getKeycode(0),
					report.getKeycode(1),
					report.getKeycode(2),
					report.getKeycode(3),
					0x4F,
					report.getKeycode(5));
			
			this.optionListener.onOptionEvent(report);
		
		}else if(value>8 && !this.direction.equals(Direction.LEFT)){
			this.direction = Direction.LEFT;
			
			report.setKeycodes(
					report.getKeycode(0),
					report.getKeycode(1),
					report.getKeycode(2),
					report.getKeycode(3),
					0x50,
					report.getKeycode(5));
			
			this.optionListener.onOptionEvent(report);
			
		}else if (Math.abs(value)<9 && !this.direction.equals(Direction.NEUTRAL)){
			this.direction = Direction.NEUTRAL;
			
			report.setKeycodes(
					report.getKeycode(0),
					report.getKeycode(1),
					report.getKeycode(2),
					report.getKeycode(3),
					HIDReportKeyboard.EMPTY_KEYCODE,
					report.getKeycode(5));
			
			this.optionListener.onOptionEvent(report);
			
		}
	}


	public void handleEvent(MotionEvent event) {
		
		if(!this.optionActive) return;
		
		if(
				
				event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_3_DOWN
				
				){
					
			Log.v(TAG, "DOWN");
			
			for(int i=0; i < event.getPointerCount(); i++){
				
				int x = (int) event.getX(i);
				int y = (int) event.getY(i);
				
				boolean[] pressedButtons = pressedButtons(x,y,true);
				
				report.setKeycodes(
						
						// Up arrow
						(pressedButtons[0]) ? 0x52 : report.getKeycode(0),
						//0x11 -> left control
						(pressedButtons[1]) ? 0xE0 : report.getKeycode(1),
						// Down arrow
						(pressedButtons[2]) ? 0x51 : report.getKeycode(2),
						//0x2c -> spacebar
						(pressedButtons[3]) ? 0x2C : report.getKeycode(3),
						     
						report.getKeycode(4),
						report.getKeycode(5)
						
				);	
				
				this.optionListener.onOptionEvent(report);
				
			}
 							
		}else if(
 				
 				event.getAction() == MotionEvent.ACTION_UP ||
 				event.getAction() == MotionEvent.ACTION_POINTER_1_UP ||
				event.getAction() == MotionEvent.ACTION_POINTER_2_UP ||
	  			event.getAction() == MotionEvent.ACTION_POINTER_3_UP
				
				){
					
		for(int i=0; i < event.getPointerCount(); i++){
	 			
				int x = (int) event.getX(i);
				int y = (int) event.getY(i);
				
				boolean[] pressedButtons = pressedButtons(x,y,false);
				
				report.setKeycodes(
						
						// Up arrow
						(pressedButtons[0]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(0),
						//0x11 -> left control
						(pressedButtons[1]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(1),
						// Down arrow
						(pressedButtons[2]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(2),
						//0x20 -> spacebar
						(pressedButtons[3]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(3),
						
						report.getKeycode(4),
						report.getKeycode(5)
						
				);	
				
				this.optionListener.onOptionEvent(report);
			}
					
				}

	}
	
	public boolean[] pressedButtons(int x, int y, boolean checkDown){

		boolean buttonsTouched[] = new boolean[]{
				false, false, false, false
		};
		
		for(int i = 0; i<4;i++){
			
			boolean isIn = (x > button[i].getLeft() &&
							x < button[i].getRight() &&
							y > button[i].getTop() &&
							y < button[i].getBottom());
			
			buttonsTouched[i] = isIn;
			
			if(isIn) button[i].setPressed(checkDown);
			
		}
		
		return buttonsTouched;
		
	}

}
