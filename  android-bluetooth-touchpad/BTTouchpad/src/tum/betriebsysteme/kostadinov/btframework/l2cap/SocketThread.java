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

package tum.betriebsysteme.kostadinov.btframework.l2cap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class SocketThread extends Thread {
 
	public static interface EventListener {

		public void onSocketConnectionFailed(int port);

		public void onBytesRead(int port, int bytesRead, byte[] buffer);

		public void onSocketConnected(int port);

	}
	
	private BluetoothSocket socket = null;
	private OutputStream outputStream;
	private InputStream inputStream;
    
	private EventListener eventListener;
	private int port;
	
	private boolean isListening = false;

    public SocketThread(EventListener eventListener) {
    	this.eventListener = eventListener;
    }
    
    public void connect(BluetoothDevice device, int port){
    	
        try {
        	
        	this.port = port;
			socket = SocketFactory.instantateL2CAPSocket(device, port);
		
        } catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
        
		if (socket == null){
			
			 eventListener.onSocketConnectionFailed(port);
			
		}else{
			
			this.start();
			
		}
    	
    }
    
    

    public void run() {
    	
    	try {
    		
    		 socket.connect();
    		 this.outputStream = socket.getOutputStream();
    	     this.inputStream  = socket.getInputStream();
    	
    	}catch(IOException e){
    		
    		e.printStackTrace();
    		
    		eventListener.onSocketConnectionFailed(port);
    	
    		return;
    		
    	}
    	
    	(new Thread(){
    		
    		@Override
    		public void run(){
    			eventListener.onSocketConnected(port);
    		}
    		
    	}).start();
    	
    	isListening = true;
    	
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytesRead; // bytes returned from read()
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
        // Keep listening to the InputStream until an exception occurs
        while (isListening) {
            try {
                
            	bytesRead = dataInputStream.read(buffer);
            	eventListener.onBytesRead(port, bytesRead, buffer);
               
            } catch (IOException e) {
            	//eventListener.onSocketConnectionFailed(port);
                break;
            }
        }
    }

    

	/* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
        	eventListener.onSocketConnectionFailed(port);
        }
    }

    /* Call this from the main Activity to shutdown the connection */
    public void cancel() {
        try {
        	isListening = false;
        	if( outputStream != null ) 	outputStream.close();
        	if( inputStream != null ) 	inputStream.close();
            if( socket != null ) 		socket.close();
        } catch (IOException e) { }
    }
    
}

