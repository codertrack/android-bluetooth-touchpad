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

package tum.betriebsysteme.kostadinov.btframework.sdp;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.util.Log;


/**
 * The SDP component is responsible for deploying the compiled executable and
 * running it from the console. There only two functions supported - for adding
 * the service record to the registry of the SDP server (registerHID) and
 * deleting the same record from the registry. (unregisterHID) 
 * 
 * @author Nikolay Kostadinov
 */
public class SDPRegister {
	
	/**
	 * Pass to the register function in order to register a service for mouse and keyboard. 
	 */
	public static final int SDP_CONFIG_MOUSE_RELATIVE = 0;
	
	/**
	 * Pass to the register function in order to register a service for pointer and mouse.
	 */
	public static final int SDP_CONFIG_MOUSE_ABSOLUTE = 1;
	
	/**
	 * Listener will return a code describing whether 
	 * the register or unregister asynchronous call succeeded. 
	 * @author Nikolay Kostadinov
	 */
	public static interface SDPStateListener {
		
		/**
		 * Called when the register operation is finished.
		 * @param code describes either success or failure.
		 */
		public void onRegisterComplete(int code);
		
		/**
		 * Called when the unregister operation is finished.
		 * @param code describes either success or failure.
		 */
		public void onUnregisterComplete(int state);
		
	}
	
	/**
	 * Code for registration success, the service record is added in the SDP server's registry.
	 */
	public static final int CODE_SDP_REGISTRATION_SUCCESS = 0;
	
	/**
	 * Code for unregistration success, the service record is removed from the SDP server's registry.
	 */
	public static final int CODE_SDP_UNREGISTRATION_SUCCESS = 1;
	
	/**
	 * Error code, invalid commands or parameters are passed to the executable when running.
	 */
	public static final int CODE_EXECUTABLE_INVALID_COMMAND_PARAMS_ENTERED = 11;
	
	/**
	 * Error code, connection to the local SDP server could not be established. Probably,
	 * the phone has modified Bluetooth stack. (Usually the case with HTC)
	 */
	public static final int CODE_CONNECTION_TO_SDP_FAILED = 12;
	
	/**
	 * Error code, the service record could not be added in the SDP server's registry.
	 */
	public static final int CODE_SDP_REGISTRATION_FAILED = 13;
	
	/**
	 * Error code, the service record is not in the SDP server.
	 */
	public static final int CODE_SDP_RECORD_REQUEST_FAILED = 14;
	
	/**
	 * Error code, the service record could not be deleted from the SDP server's registry.
	 */
	public static final int CODE_SDP_UNREGISTER_FAILED = 15;
	
	/**
	 * Error code, deploying the executable to the phone's internal memory failed.  
	 */
	public static final int CODE_EXECUTALBE_DEPLOING_FAILED = 31;
	
	/**
	 * Error code, the commands passed to the console returned an error.  
	 */
	public static final int CODE_OS_COMMAND_ERROR = 32;
	
	/**
	 * Check if a state is success or error. 
	 * @param state pass a state to check 
	 * @return true if the code is for success and false if it is error.
	 */
	public static boolean isSuccess(int state){
		return (state<10) ? true : false;
	}
	
		
	private static final String TAG = "SDP_SERVICE_REGISTER";
	
	private SDPStateListener sdpStateListener;
	private Context context;
	
	private boolean registerSuccess = false;
	
	/**
	 * Instantiating  the SDP component. 
	 * 
	 * @param context An instance of the application context. 
	 * @param sdpStateListener The state listener will report for results after register 
	 * and unregister asynchronous calls.
	 */
	public SDPRegister(Context context, SDPStateListener sdpStateListener){
		this.sdpStateListener = sdpStateListener;
		this.context = context;
	}
	

	/**
	 * Attempts to register the new service record.
	 * Call is asynchronous and runs in a separate Thread. 
	 * Result will be sent by the SDPStateListener.
	 * 
	 * @param recordType the type of record to add. 
	 * Either SDP_CONFIG_MOUSE_RELATIVE for mouse + keyboard 
	 * or SDP_CONFIG_MOUSE_ABSOLUTE for pointer + keyboard. 
	 */
	public void registerHID(final int recordType){

		(new Thread(){
			
			@Override
			public void run(){
				
				InputStream inputStream = null;
				FileOutputStream fileOutputStream = null;
				
				try{
				
				inputStream = context.getAssets().open(SDPCommands.EXECUTABLE_FILE_NAME);
				fileOutputStream = context.openFileOutput(SDPCommands.EXECUTABLE_FILE_NAME, Context.MODE_PRIVATE);
				
				byte[] buffer = new byte[4096];
	        	int read;
	        	
	        	while((read = inputStream.read(buffer)) != -1){
	        		fileOutputStream.write(buffer, 0, read);
	        	}
	        	
	        	fileOutputStream.close();
				
				}catch(IOException e){
					
					e.printStackTrace();
					sdpStateListener.onRegisterComplete(CODE_EXECUTALBE_DEPLOING_FAILED);
					return;
					
				}
				
				Log.v(TAG, "EXECUTABLE FILE DEPLOYED SUCCESSFULY");
				
				try { 
            		
        			Process process = Runtime.getRuntime().exec(SDPCommands.COMMAND_EXEC_SU);
        			DataOutputStream osOut = new DataOutputStream(process.getOutputStream());
        			
        			osOut.writeBytes(SDPCommands.navigateToExecutableFolder(context.getPackageName()));
        			osOut.flush(); 
        			
        			osOut.writeBytes(SDPCommands.changeModeToExecutable(SDPCommands.EXECUTABLE_FILE_NAME));
        			osOut.flush(); 
        			
        			osOut.writeBytes(SDPCommands.runExecutable(	SDPCommands.EXECUTABLE_FILE_NAME,
        														SDPCommands.COMMAND_VALUE_REGISTER,
        			(recordType == SDP_CONFIG_MOUSE_RELATIVE )  ?  SDPCommands.PARAM_VALUE_RELATIVE : SDPCommands.PARAM_VALUE_ABSOULTE ));
        			osOut.flush();
        			
        			osOut.writeBytes(SDPCommands.COMMAND_EXIT); 
        			osOut.flush();
        			
        			//<OS CONSOLE OUTPUT
        			BufferedReader reader = new BufferedReader(
        		            new InputStreamReader(process.getInputStream()));
        			
        			int read;
        		    char[] buffer = new char[4096];
        		    StringBuffer output = new StringBuffer();
        		    while ((read = reader.read(buffer)) > 0) {
        		        output.append(buffer, 0, read);
        		    }
        		    reader.close();
        		    
        		    Log.v(TAG, output.toString());
        			//OS CONSOLE OUTPUT > 
        			
        			process.waitFor(); 
        				
        			int exitValue = process.exitValue();
        			
        			Log.v(TAG, "Exit value: " + exitValue);
        			
        			registerSuccess = (exitValue == CODE_SDP_REGISTRATION_SUCCESS);
        			
        			sdpStateListener.onRegisterComplete(exitValue);
        			
        			osOut.close();
  
        			
        		} catch (IOException e) {
    				e.printStackTrace();
    				sdpStateListener.onRegisterComplete(CODE_OS_COMMAND_ERROR);
    				return;
    				
    				
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				sdpStateListener.onRegisterComplete(CODE_OS_COMMAND_ERROR);
    				return;
    			} 
				
				
			}
		
		}).start();

	} 
	
	/**
	 * Attempts to delete the service record, which was previously registered.
	 * Call is asynchronous and runs in a separate Thread. Result will be
	 * delivered by the SDPStateListener. Will do nothing if the registerHID call
	 * was not successful.
	 */
	public void unregisterHID(){
		
		if(!registerSuccess) return;
		
		(new Thread(){
			
			@Override
			public void run(){  
								
				try { 
            		
        			Process process = Runtime.getRuntime().exec(SDPCommands.COMMAND_EXEC_SU);
        			DataOutputStream osOut = new DataOutputStream(process.getOutputStream());
        			
        			osOut.writeBytes(SDPCommands.navigateToExecutableFolder(context.getPackageName()));
        			osOut.flush(); 
        			osOut.writeBytes(SDPCommands.runExecutable(SDPCommands.EXECUTABLE_FILE_NAME, SDPCommands.COMMAND_VALUE_UNREGISTER, null));
        			osOut.flush();
        			osOut.writeBytes(SDPCommands.deleteExecutableFile(SDPCommands.EXECUTABLE_FILE_NAME));
        			osOut.flush();
        			osOut.writeBytes(SDPCommands.COMMAND_EXIT); 
        			osOut.flush();
        			
        			//<OS CONSOLE OUTPUT
        			BufferedReader reader = new BufferedReader(
        		            new InputStreamReader(process.getInputStream()));
        			
        			int read;
        		    char[] buffer = new char[4096];
        		    StringBuffer output = new StringBuffer();
        		    while ((read = reader.read(buffer)) > 0) {
        		        output.append(buffer, 0, read);
        		    }
        		    reader.close();
        		    
        		    Log.v(TAG, output.toString());
        			//OS CONSOLE OUTPUT > 
        		    
        			process.waitFor(); 
        				
        			sdpStateListener.onUnregisterComplete(process.exitValue());
        			
        			osOut.close();
         			
        		} catch (IOException e) {
    				e.printStackTrace();
    				sdpStateListener.onUnregisterComplete(CODE_OS_COMMAND_ERROR);
    				return;
    				
    				
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				sdpStateListener.onUnregisterComplete(CODE_OS_COMMAND_ERROR);
    				return;
    			} 
				
				
			}
		
		}).start();
		
		
	}
	
	
	
}
