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

package tum.betriebsysteme.kostadinov.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class ActivityResource {

	public static final long VIB_VERY_SHORT = 50;
	public static final long VIB_MIDDLE = 2000;
	
	public static int DISPLAY_WIDTH;
	public static int DISPLAY_HEIGHT;
	
	private static Activity activityResource;
	private static Handler uiHandler;
	

	//execute set in OnCreate
	public static void set(Activity activityResource) {
		
		ActivityResource.activityResource = activityResource;
		uiHandler = new Handler();
		
		activityResource.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		State.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Display display = ((WindowManager) activityResource.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		
		DISPLAY_WIDTH = Math.max(display.getWidth(), display.getHeight());
		DISPLAY_HEIGHT =  Math.min(display.getWidth(), display.getHeight());
	
	}

	public static Activity get() {
		return activityResource;
	}

	
	
	
	public static View inflate(int id){
		
		LayoutInflater layoutInflater = 
			(LayoutInflater) activityResource.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
		return layoutInflater.inflate(id, null);

	}
	
	public static void postUIRunnable(Runnable runnable){
		
		uiHandler.post(runnable);
		
	}
	
	public static void setOrientation(int orientation){
		activityResource.setRequestedOrientation(orientation);
		State.setOrientation(orientation);
	}
	
	public static void vibrate(long duration){
	((Vibrator) activityResource
			.getSystemService(Context.VIBRATOR_SERVICE))
			.vibrate(duration); 
	}
	
}
