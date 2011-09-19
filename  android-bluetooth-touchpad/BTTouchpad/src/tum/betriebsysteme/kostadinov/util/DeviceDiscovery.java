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


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class DeviceDiscovery {
	
	public static interface DiscoveryListener {

		public void onDiscoveryFinished(List<BluetoothDevice> device);
		
		public void onBluetoothEnabled(int state);
	}

	
	public static final int STATE_BLUETOOTH_NOT_SUPPORTED = 11;
	public static final int STATE_BLUETOOTH_ENABLED = 0;
	
		
	private static final int REQUEST_ENABLE_BT = 3;
	
	DiscoveryListener discoveryListener;

	public DeviceDiscovery(DiscoveryListener discoveryListener){
		this.discoveryListener = discoveryListener;
	}

	
	public void enableBluetooth(){
		
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//if bluetoothAdapter is null, the device is not supporting bluetooth
		if (bluetoothAdapter == null) {
			discoveryListener.onBluetoothEnabled(STATE_BLUETOOTH_NOT_SUPPORTED);
			return;
		}
		
		//if bluetooth is enabled we are done
		if (!bluetoothAdapter.isEnabled()) {
		
		//Hide Loading Dialog
		DialogController.hideLoadingDialog();
		
		//else we start bluetooth-enabling intent
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		ActivityResource.get().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		return;
		
		}else{
		//Show Loading Dialog again
		DialogController.showLoadingDialog();
		}
		
		discoveryListener.onBluetoothEnabled(STATE_BLUETOOTH_ENABLED);
	}
		
	
	
	public void discoverDevices(){
		
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(bluetoothAdapter == null || bluetoothAdapter.isEnabled()){
			discoveryListener.onDiscoveryFinished(null);
		}
			
		
		List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        deviceList.add(device);
		    }
		}
		
		discoveryListener.onDiscoveryFinished(deviceList);
		
	}
	
}
