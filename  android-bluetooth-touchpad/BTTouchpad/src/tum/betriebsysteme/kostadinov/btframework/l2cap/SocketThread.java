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

/**
 * Connects to Bluetooth device asynchronously, in a separate Thread. Use this component to establish the L2CAP connection to the remote device. 
 * @author Nikolay Kostadinov
 *
 */
public class SocketThread extends Thread {
	
	/**
	 * Notifies of events connected with the connection establishment on L2CAP level. 
	 * @author freeman
	 *
	 */
	public static interface EventListener {
		
		/**
		 * Called if the connection fails. 
		 * @param port the psm port of the socket, which failed.
		 */
		public void onSocketConnectionFailed(int port);

		/**
		 * Notifies if a message from the remote Bluetooth party is received.
		 * @param port the psm port of the socket, delivering the message.
		 * @param bytesRead the number of bytes read.
		 * @param buffer byte array containing the message.
		 */
		public void onBytesRead(int port, int bytesRead, byte[] buffer);
		
		/**
		 * Called if the connection succeeds. 
		 * @param port the psm port of the socket, which was successfuly opened. 
		 */
		public void onSocketConnected(int port);

	}
	
	private BluetoothSocket socket = null;
	private OutputStream outputStream;
	private InputStream inputStream;
    
	private EventListener eventListener;
	private int port;
	
	private boolean isListening = false;
	
	/**
	 * 
	 * @param eventListener the listener, which will notify of new events.
	 */
    public SocketThread(EventListener eventListener) {
    	this.eventListener = eventListener;
    }
    
    /**
     * Asynchronously connect to the remote Bluetooth device.
     * Will run on separate Thread and will safely free the resources if not successful.
     * Wait for notification through the EventListener to find out if it is successful or not.
     * @param device The Bluetooth device to connect to.
     * @param port The psm port oh which the L2CAP channel is established.
     */
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
    
    
    @Override
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

    
    /**
     * Call this method to send data to the remote device,
     * once the connection is established. Call is synchronous.
     * The Thread calling it will wait until all bytes
     * are send. Use with caution in Android.
     */
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
            
        } catch (IOException e) {
        	eventListener.onSocketConnectionFailed(port);
        } catch (Exception e){
        	eventListener.onSocketConnectionFailed(port);
        }
    }

    /**
     * Call this to safely close the connection to the Bluetooth device.
     */
    public void cancel() {
        try {
        	isListening = false;
        	if( outputStream != null ) 	outputStream.close();
        	if( inputStream != null ) 	inputStream.close();
            if( socket != null ) 		socket.close();
        } catch (IOException e) { }
    }
    
}

