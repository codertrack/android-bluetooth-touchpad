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

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class SDPRegister {
	
	public static final int SDP_CONFIG_MOUSE_RELATIVE = 0;
	public static final int SDP_CONFIG_MOUSE_ABSOLUTE = 1;
	
	public static interface SDPStateListener {
		
		public void onRegisterComplete(int state);
		
		public void onUnregisterComplete(int state);
		
	}
	
	//< State Constants
	public static final int SDP_REGISTRATION_SUCCESS = 0;
	
	public static final int SDP_UNREGISTRATION_SUCCESS = 1;
	
	public static final int EXECUTABLE_INVALID_COMMAND_PARAMS_ENTERED = 11;
	
	public static final int CONNECTION_TO_SDP_FAILED = 12;
	
	public static final int SDP_REGISTRATION_FAILED = 13;
	
	public static final int SDP_RECORD_REQUEST_FAILED = 14;
	
	public static final int SDP_UNREGISTER_FAILED = 15;
	
	public static final int EXECUTALBE_DEPLOING_FAILED = 31;
	
	public static final int OS_COMMAND_ERROR = 32;
	
	public static boolean isSuccess(int state){
		return (state<10) ? true : false;
	}
	
	// State Constants>
	
		
	private static final String TAG = "SDP_SERVICE_REGISTER";
	
	private SDPStateListener sdpStateListener;
	private Activity activityInstance;
	
	private boolean registerSuccess = false;
	
	public SDPRegister(Activity activityInstance, SDPStateListener sdpStateListener){
		this.sdpStateListener = sdpStateListener;
		this.activityInstance = activityInstance;
	}
	
	public void registerHID(final int recordType){

		(new Thread(){
			
			@Override
			public void run(){
				
				InputStream inputStream = null;
				FileOutputStream fileOutputStream = null;
				
				try{
				
				inputStream = activityInstance.getAssets().open(SDPCommands.EXECUTABLE_FILE_NAME);
				fileOutputStream = activityInstance.openFileOutput(SDPCommands.EXECUTABLE_FILE_NAME, Context.MODE_PRIVATE);
				
				byte[] buffer = new byte[4096];
	        	int read;
	        	
	        	while((read = inputStream.read(buffer)) != -1){
	        		fileOutputStream.write(buffer, 0, read);
	        	}
	        	
	        	fileOutputStream.close();
				
				}catch(IOException e){
					
					e.printStackTrace();
					sdpStateListener.onRegisterComplete(EXECUTALBE_DEPLOING_FAILED);
					return;
					
				}
				
				Log.v(TAG, "EXECUTABLE FILE DEPLOYED SUCCESSFULY");
				
				try { 
            		
        			Process process = Runtime.getRuntime().exec(SDPCommands.COMMAND_EXEC_SU);
        			DataOutputStream osOut = new DataOutputStream(process.getOutputStream());
        			
        			osOut.writeBytes(SDPCommands.navigateToExecutableFolder(activityInstance.getPackageName()));
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
        			
        			registerSuccess = (exitValue == SDP_REGISTRATION_SUCCESS);
        			
        			sdpStateListener.onRegisterComplete(exitValue);
        			
        			osOut.close();
  
        			
        		} catch (IOException e) {
    				e.printStackTrace();
    				sdpStateListener.onRegisterComplete(OS_COMMAND_ERROR);
    				return;
    				
    				
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				sdpStateListener.onRegisterComplete(OS_COMMAND_ERROR);
    				return;
    			} 
				
				
			}
		
		}).start();

	} 
	
	public void unregisterHID(){
		
		if(!registerSuccess) return;
		
		(new Thread(){
			
			@Override
			public void run(){  
								
				try { 
            		
        			Process process = Runtime.getRuntime().exec(SDPCommands.COMMAND_EXEC_SU);
        			DataOutputStream osOut = new DataOutputStream(process.getOutputStream());
        			
        			osOut.writeBytes(SDPCommands.navigateToExecutableFolder(activityInstance.getPackageName()));
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
    				sdpStateListener.onUnregisterComplete(OS_COMMAND_ERROR);
    				return;
    				
    				
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    				sdpStateListener.onUnregisterComplete(OS_COMMAND_ERROR);
    				return;
    			} 
				
				
			}
		
		}).start();
		
		
	}
	
	
	
}
