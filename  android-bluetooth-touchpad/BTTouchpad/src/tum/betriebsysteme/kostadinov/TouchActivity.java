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

package tum.betriebsysteme.kostadinov;

import java.util.ArrayList;
import java.util.List;
import tum.betriebsysteme.kostadinov.btframework.l2cap.SocketThread;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReport;
import tum.betriebsysteme.kostadinov.btframework.sdp.SDPRegister;
import tum.betriebsysteme.kostadinov.ui.DeviceList;
import tum.betriebsysteme.kostadinov.ui.OptionList;
import tum.betriebsysteme.kostadinov.ui.SDPList;
import tum.betriebsysteme.kostadinov.ui.SDPList.SDPListListener;
import tum.betriebsysteme.kostadinov.ui.options.Gamepad;
import tum.betriebsysteme.kostadinov.ui.options.Keyboard;
import tum.betriebsysteme.kostadinov.ui.options.Option;
import tum.betriebsysteme.kostadinov.ui.options.Paintpad;
import tum.betriebsysteme.kostadinov.ui.options.Pointer;
import tum.betriebsysteme.kostadinov.ui.options.PointerAbsolute;
import tum.betriebsysteme.kostadinov.ui.options.Touchpad;
import tum.betriebsysteme.kostadinov.ui.options.Voice;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.CONSTANTS;
import tum.betriebsysteme.kostadinov.util.DeviceDiscovery;
import tum.betriebsysteme.kostadinov.util.DialogController;
import tum.betriebsysteme.kostadinov.util.State;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;


public class TouchActivity extends Activity implements 
	SDPRegister.SDPStateListener, SocketThread.EventListener, 
	DeviceList.DeviceListListener, DeviceDiscovery.DiscoveryListener, OptionList.OptionListListener, Option.OptionListener, SDPListListener {

	
	private static final String TAG = "TOUCH_ACTIVITY";
	
	private SDPRegister sdpRegister;
	private DeviceDiscovery deviceDiscovery;
	private DeviceList deviceList;
	private Option currentOption;
	private OptionList optionList;
	private SDPList sdpList;
	private SocketThread controlChannelThread;
	private SocketThread interruptionChannelThread;
	
	private boolean controlChannelEstablished 		= false;
	private boolean interruptionChannelEstablished 	= false;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ActivityResource.set(this);
         
        Log.v(TAG, "onCreate()");
        
        deviceDiscovery 	= new DeviceDiscovery(this);
        sdpRegister 		= new SDPRegister(this,this);
        deviceList 			= new DeviceList(this);
        optionList 			= new OptionList(this);
        sdpList				= new SDPList(this);
        
    	DialogController.showLoadingDialog();
    	deviceDiscovery.enableBluetooth();
    	
    }  
     
    @Override
    public void onStart(){
    	super.onStart();
    	Log.v(TAG, "onStart()");
    }
    
    @Override 
    public void onPause(){
    	super.onPause();
    	Log.v(TAG, "onPuase()");
    	
    	
    }  
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    	if(sdpRegister != null) 				sdpRegister.unregisterHID();
    	if(controlChannelThread != null) 		controlChannelThread.cancel();
    	if(interruptionChannelThread != null) 	interruptionChannelThread.cancel();
    }
    
    @Override 
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	
    	Log.v(TAG, "onActivityResult OK: " + (resultCode == Activity.RESULT_OK));
    	
    	if (requestCode == Voice.RECOGNITION_SUCCESS_CODE && resultCode == RESULT_OK) {
            
    		Log.v(TAG, "onActivityResult VOICE INPUT: ");
    		
    		ArrayList<String> matches = data.getStringArrayListExtra(
    				RecognizerIntent.EXTRA_RESULTS);
            
    		if( currentOption != null && currentOption instanceof Voice){
    			((Voice) currentOption).sendText(matches.get(0).toString());	
    		}
        }
    	
    	else if(resultCode == Activity.RESULT_OK){
    		Log.v(TAG, "onActivityResult ROOT PERMISSIONS: ");
    		
    		//Bluetooth has been enabled, go on with SDP
    		sdpList.showConfigurationOptions();
    		
    	}else{
    		//User didn't enable Bluetooth
			DialogController.showFatalErrorDialog(
					CONSTANTS.BLUETOOTH_NOT_ENABLED_BY_USER);
    		
    	}
    }
        

	@Override 
	public void onRegisterComplete(final int state) {
		
		Log.v(TAG, "onRegisterComplete State: " +state);
		
			ActivityResource.postUIRunnable(new Runnable(){

				@Override
				public void run() {
					
					if(SDPRegister.isSuccess(state) ){ 
					
						deviceDiscovery.discoverDevices();
					
					} else {
						
						DialogController.hideLoadingDialog();
						
						DialogController.showFatalErrorDialog(
								CONSTANTS.SDP_FAILED_ERROR_MESSAGE + state);
						
					}
					
				}
				
			});	
		
	}

	@Override
	public void onUnregisterComplete(final int state) {
		Log.v(TAG, "onUnregisterComplete State: " +state);
		
		ActivityResource.postUIRunnable(new Runnable(){

			@Override
			public void run() {
				
				DialogController.hideLoadingDialog();
				
				if(SDPRegister.isSuccess(state)){
					sdpList.showConfigurationOptions();
				}else DialogController.showFatalErrorDialog("Unregister failed.");
				
			}
			
		});	
		
		
		
	}

	@Override
	public void onSocketConnectionFailed(int port) {
		Log.v(TAG, "onSocketConnectionFailed: " + port);

		ActivityResource.postUIRunnable(new Runnable(){

			@Override
			public void run() {
				DialogController.hideLoadingDialog();
				DialogController.showFatalErrorDialog(CONSTANTS.SOCKET_CONNECTION_ERROR_MESSAGE);
			}
			
		});
			
	}

	@Override
	public void onBytesRead(int port, int bytesRead, byte[] buffer) {
		
		String byteMessage = "onBytesRead on Port: " + port+" Bytes:";
		
		for(int i=0;i<bytesRead;i++){
			byteMessage += " " + (int)buffer[i];
		}
		
		Log.v(TAG, byteMessage);
		
	}

	@Override
	public void onSocketConnected(int port) {
		
		if( port == CONSTANTS.CONTROL_CHANNEL_PORT ) 		controlChannelEstablished 		= true;
		if( port == CONSTANTS.INTERRUPTION_CHANNEL_PORT ) interruptionChannelEstablished 	= true;
		
		if (controlChannelEstablished && interruptionChannelEstablished){

			ActivityResource.postUIRunnable(new Runnable(){
							
				@Override
				public void run() {
					DialogController.hideLoadingDialog();
					optionList.showOptions();
				}
				
			});
			
		}
	}

	@Override
	public void onDeviceChoosen(BluetoothDevice device) {
		
		controlChannelThread 		= new SocketThread(this);
		interruptionChannelThread 	= new SocketThread(this);
		
		controlChannelThread.connect(device, CONSTANTS.CONTROL_CHANNEL_PORT);
		interruptionChannelThread.connect(device, CONSTANTS.INTERRUPTION_CHANNEL_PORT);
		
		DialogController.showLoadingDialog();
		
	}

	@Override
	public void onDiscoveryFinished(List<BluetoothDevice> devices) {
		
		if(devices != null){
			
			DialogController.hideLoadingDialog();
			deviceList.showList(devices);
			
		}
		
	}

	@Override
	public void onOptionChoosen(String optionName, int index) {
	
		Log.v(TAG, "onOptionsChoosen " + optionName); 
		
		switch (index) {
		
		case Option.OPTION_TOUCHPAD_INDEX :{
		
			this.currentOption = new Touchpad(this);
			this.currentOption.initOptionUI();
			
			break;
			
		}
		
		case Option.OPTION_POINTER_R_INDEX :{
			
			this.currentOption = new Pointer(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		
		case Option.OPTION_KEYBOARD_INDEX :{
			
			this.currentOption = new Keyboard(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		
		case Option.OPTION_POINTER_A_INDEX:{
			
			this.currentOption = new PointerAbsolute(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		
		case Option.OPTION_PAINTPAD_INDEX: {
			
			this.currentOption = new Paintpad(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		
		case Option.OPTION_VOICE_INDEX: {
			
			this.currentOption = new Voice(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		
		case Option.OPTION_GAMEPAD_INDEX: {
			
			this.currentOption = new Gamepad(this);
			this.currentOption.initOptionUI();
			
			break;
		}
		 
		}
		
	} 
     
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(State.getOrientation());
	}

	@Override
	public void onOptionEvent(HIDReport hidReport) {
		
		if(this.controlChannelThread != null) 
			controlChannelThread.write(hidReport.getReportPayload());
		
	}

	@Override
	public void onBluetoothEnabled(int state) {
		
		Log.v(TAG, "onBluetoothEnabled " + state);
		
		DialogController.hideLoadingDialog();
		
		if(state == DeviceDiscovery.STATE_BLUETOOTH_ENABLED){
			
			sdpList.showConfigurationOptions();
			
			
		}else if(state == DeviceDiscovery.STATE_BLUETOOTH_NOT_SUPPORTED){
			
			DialogController.showFatalErrorDialog(
					CONSTANTS.BLUETOOTH_NOT_SUPPORTED_MESSAGE);
		
		}
		
	}
	
	@Override
	 public void onBackPressed() {
         Log.v(TAG, "onBackPressed");
        
         switch(State.getUIState()){
         
         case State.UI_STATE_SDP_CONFIGURATION: {
        	 
        	 super.onBackPressed();
        	 break;
         }
         
         case State.UI_STATE_DEVICE_LIST: {
        
        	 sdpRegister.unregisterHID();
        	 DialogController.showLoadingDialog();
        	 break;
         }
         
         case State.UI_STATE_OPTION_LIST: {
        	 
        	 if ( controlChannelEstablished ){
        		 
        		 controlChannelThread.cancel();
        	 	 controlChannelEstablished = false;
        	 
        	 }
        	 
        	 if( interruptionChannelEstablished ){
        		 
        		interruptionChannelThread.cancel();
        	    interruptionChannelEstablished = false;
        	 
        	 }
        	 
        	    deviceDiscovery.discoverDevices();
        	 
        	 break;
         }
         
         case State.UI_STATE_OPTION: {
        	 	
        	 	currentOption.destroyOptionUI();
        	 	optionList.showOptions();
				
        	 	break;
        	 
         }
         
         }
         
       
     }

	@Override
	public void onSDPChoosen(int sdpType) {
		
		State.setSdpConfig(sdpType);
		
		DialogController.showLoadingDialog();
		
		sdpRegister.registerHID(sdpType);
		
	}
	
	@Override 
	public boolean onTrackballEvent(MotionEvent event) {
		
		if( event.getAction() == MotionEvent.ACTION_DOWN)
			State.setTrackBallDown(true);
		else if ( event.getAction() == MotionEvent.ACTION_UP )
			State.setTrackBallDown(false);
		
		return true;
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		
		if (		currentOption != null &&
					currentOption instanceof Gamepad	){
			
		((Gamepad) currentOption).handleEvent(event);	
		
		}
		
		return super.dispatchTouchEvent(event);
		
	}
	
}