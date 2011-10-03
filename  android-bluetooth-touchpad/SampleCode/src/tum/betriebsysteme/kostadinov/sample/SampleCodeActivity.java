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

package tum.betriebsysteme.kostadinov.sample;

import tum.betriebsysteme.kostadinov.btframework.l2cap.SocketThread;
import tum.betriebsysteme.kostadinov.btframework.l2cap.SocketThread.EventListener;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import tum.betriebsysteme.kostadinov.btframework.sdp.SDPRegister;
import tum.betriebsysteme.kostadinov.btframework.sdp.SDPRegister.SDPStateListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SampleCodeActivity extends Activity implements OnClickListener, SDPStateListener, EventListener {
  
	private boolean registered = false;
	private Context context;
	
	private SDPRegister sdpRegister;
	private SocketThread l2capSocket1;
	private SocketThread l2capSocket2;
	private Handler handler;
		
	private static final CharSequence hello_message = "helloandroidbluetooth";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sdpRegister = new SDPRegister(this,this);
        l2capSocket1 = new SocketThread(this);
        l2capSocket2 = new SocketThread(this);
        
        findViewById(R.id.add_service_button).setOnClickListener(this);
        findViewById(R.id.connect_to_device_button).setOnClickListener(this);
        findViewById(R.id.send_message_button).setOnClickListener(this);
        
        handler = new Handler();
        context = this;
    }

	@Override
	public void onClick(View view) {
		
		if(view.getId() == R.id.add_service_button){
			
			if(registered){
				toastLog("Service is already registered.");
				return;
			}
			
			// For keyboard and regular mouse
			sdpRegister.registerHID(SDPRegister.SDP_CONFIG_MOUSE_RELATIVE);
			// or for keyboard and pointer
			// sdpRegister.registerHID(SDPRegister.SDP_CONFIG_MOUSE_ABSOLUTE);
			view.setEnabled(false);
		}
		
		else if(view.getId() == R.id.connect_to_device_button){
			if(!registered){
				toastLog("Service hasn't been registered. Connection will fail.");
				return;
			}
			/*	ATTENTION !!!
			 *  MAKE SHURE TO PAIR YOUR PHONE WITH THE LINUX COMPUTER 
			 *  AND MARK THE PHONE AS TRUSTED !!!
			 *  
			 *  ENTER YOUR COMPUTER'S BLUETOOTH ADDRESS HERE !!!
			 *  TO FIND IT OUT, TYPE "hciconfig" IN THE LINUX CONSOLE.
			 */
			String address = "00:09:DD:50:86:5B";
			
			BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
			
			l2capSocket1.connect(device, 0x11);
			
			l2capSocket2.connect(device, 0x13);
			
			view.setEnabled(false);
		}
		
		else if(view.getId() == R.id.send_message_button){
			
			if(l2capSocket1.isConnected() && l2capSocket2.isConnected()){
				
				HIDReportKeyboard keyboardEvent = new HIDReportKeyboard();
				
				for(int i=0;i<hello_message.length();i++){
					int sign = hello_message.charAt(i);
					keyboardEvent.setSingleKeycode(sign-93);
					l2capSocket1.write(keyboardEvent.getReportPayload());
					keyboardEvent.setSingleKeycode(HIDReportKeyboard.EMPTY_KEYCODE);
					l2capSocket1.write(keyboardEvent.getReportPayload());
				}
				
			}else{
				toastLog("Connection on 0x11 and 0x13 port is not established.");
			}
			
			
		}
		
		
	}

	@Override
	public void onRegisterComplete(int code) {
		
		if(SDPRegister.isSuccess(code)){
		
			toastLog( "The new service record has been registered successfuly!");
			
			registered = true;
		
		}else{
			
			toastLog("The Registration failed on your phone. Try with Nexus One.");
			
		}
		
	}

	@Override
	public void onUnregisterComplete(int state) {
	
		if(SDPRegister.isSuccess(state)){
			
			toastLog("The service was deleted from registry!");
		
		}else{
			
			toastLog("The service could not be deleted.");
			
		}
		
	}

	@Override
	public void onSocketConnectionFailed(int port) {
		toastLog("The connection could not be established. Port: "+port);
		
	}

	@Override
	public void onBytesRead(int port, int bytesRead, byte[] buffer) {
		
		Log.v(this.getClass().getSimpleName(), "Some bytes are read !!!");
		
	}

	@Override
	public void onSocketConnected(int port) {
		
		
		toastLog("Connection on "+port+" established.");
		
	}
	
	private void toastLog(final String message){
		handler.post(new Runnable(){

			@Override
			public void run() {
				
				Toast.makeText(context, message,
						Toast.LENGTH_LONG).show();
			
			}
			
		});
	}
	
	@Override
	public void onPause(){
		super.onPause();
		l2capSocket1.cancel();
		l2capSocket2.cancel();
		sdpRegister.unregisterHID();
		this.finish();
	}
    
 
}