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

package tum.betriebsysteme.kostadinov.ui;

import java.util.List;

import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;


import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceList {
	
	public static interface DeviceListListener {

		public void onDeviceChoosen(BluetoothDevice device);
		
	}

	private DeviceListListener deviceListListener;
	
	public DeviceList(DeviceListListener deviceListListener){
		this.deviceListListener = deviceListListener;
	}
	
	public void showList(List<BluetoothDevice> devices){
		
		State.setUIState(State.UI_STATE_DEVICE_LIST);
		
		ViewGroup mainView = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		ViewGroup devicesView = (ViewGroup) ActivityResource.inflate(R.layout.device_list);
		ViewGroup deviceListView = (ViewGroup) devicesView.findViewById(R.id.device_list);
		
		for(int i=0; i<devices.size();i++){
			deviceListView.addView(toDeviceItem(devices.get(i)));
		}
		
		mainView.removeAllViews();
		mainView.addView(devicesView);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mainView.invalidate();
		
	}
	
	public View toDeviceItem(final BluetoothDevice device){
		
		View deviceView = ActivityResource.inflate(R.layout.device);
		TextView deviceName = (TextView) deviceView.findViewById(R.id.device_name);
		TextView deviceAddress = (TextView) deviceView.findViewById(R.id.device_address);
		
		deviceName.setText(device.getName());
		deviceAddress.setText(device.getAddress());
		
		deviceView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				deviceListListener.onDeviceChoosen(device);
			}
			
		});		
		return deviceView;
		
	}
	
}
