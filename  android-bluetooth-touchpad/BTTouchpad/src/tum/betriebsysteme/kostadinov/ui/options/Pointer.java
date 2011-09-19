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
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportMouseRelative;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;

public class Pointer extends Option implements SensorEventListener, OnClickListener, OnLongClickListener{

	private SensorManager sensorManager;
	
	protected final float MAX_DEGREE = 360.0f;
	protected static final float MAX_DEGREE_HANDLED = 30.0f;
	
	protected float initialX = 0.0f;
	protected final float initialY = 0.0f;
	protected float x;
	protected float y;
	
	private static int accuracy = 5;
	
	public Pointer(OptionListener optionListener) {
		super(optionListener);
	}
	
	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		View pointer = ActivityResource.inflate(R.layout.pointer);
		
		pointer.findViewById(R.id.pointer_button_left).setOnClickListener(this);
		pointer.findViewById(R.id.pointer_button_right).setOnClickListener(this);
		pointer.findViewById(R.id.pointer_reset_button).setOnLongClickListener(this);
			
		main.removeAllViews();
		main.addView(pointer, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		main.invalidate();
		
		
		sensorManager = (SensorManager) ActivityResource.get().getSystemService(Context.SENSOR_SERVICE);
        
        sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_UI);
		
        optionActive = true;
		
	}

	@Override
	public void destroyOptionUI() {
		sensorManager.unregisterListener(this);
		sensorManager = null;
		optionActive = false;
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Pointer.accuracy = accuracy;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	
		if (!optionActive) return;
		
		x = event.values[0];
		y = event.values[1];
		
		
		if(Math.abs(x) < accuracy && Math.abs(y) < accuracy) return;
		
		HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
		mouseReport.setMovement( (Math.abs(x) < accuracy) ? 0 : (x - initialX) % HIDReportMouseRelative.MAX_MOUSE_MOVEMENT ,
								 (Math.abs(y) < accuracy) ? 0 : (y - initialY) % HIDReportMouseRelative.MAX_MOUSE_MOVEMENT );
		
		this.optionListener.onOptionEvent(mouseReport);
		
	}

	@Override
	public void onClick(View view) {
		
		HIDReportMouseRelative mouseReport = new HIDReportMouseRelative();
		mouseReport.setButton(
		
				(view.getId() == R.id.pointer_button_left)	?	
				HIDReportMouseRelative.MOUSE_LEFT_BUTTON			:
				HIDReportMouseRelative.MOUSE_RIGHT_BUTTON
				
		);
		
		this.optionListener.onOptionEvent(mouseReport);
		this.optionListener.onOptionEvent(new HIDReportMouseRelative());
		ActivityResource.vibrate(ActivityResource.VIB_VERY_SHORT);
		
	}

	@Override
	public boolean onLongClick(View v) {
		
		this.initialX = x;
		ActivityResource.vibrate(ActivityResource.VIB_MIDDLE);
		
		return true;
	}

}
