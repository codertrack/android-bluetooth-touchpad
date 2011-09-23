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

import tum.betriebsysteme.kostadinov.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogController {
	
	private static final String LOADING_MESSAGE = "Loading...";
	private static final String FATAL_ERROR_TITLE = "ERROR";
	
	private static final long MIN_LOADING_DURATION = 1500; // 1.5 Sceonds
	
	private static ProgressDialog progressDialog;
	private static AlertDialog alertDialog;
	
	private static long momentOfPopup;
	
	
	public static void showFatalErrorDialog(String message){
		
		View errorDialog = ActivityResource.inflate(R.layout.error_dialog);
		
		TextView errorTextView  = (TextView) errorDialog.findViewById(R.id.error_message);
		errorTextView.setText(message);
		
		View errorButton = errorDialog.findViewById(R.id.error_button);
		errorButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogController.hideAlertDialog();
				
				if(State.getUIState() == State.UI_STATE_OPTION){
					ActivityResource.get().onBackPressed();
					ActivityResource.get().onBackPressed();
				}else if(State.getUIState() == State.UI_STATE_OPTION_LIST){
					ActivityResource.get().onBackPressed();
				}else if(State.getUIState() == State.UI_STATE_DEVICE_LIST){
					//do nothing, user can choose different device
				}else if(State.getUIState() == State.UI_STATE_SDP_CONFIGURATION){
					//do nothing, user can try again.
				}else if(State.getUIState() == State.INITIAL){
					//User didn't activate Bluetooth.
					//Quit app, so he can activate it from the phone's menu.
					ActivityResource.get().finish();
				}
				
			}
			
		});
		
		showAlertDialog(errorDialog, FATAL_ERROR_TITLE, false );
	}
	
	
	public static void showAlertDialog(View content, String title, boolean cancelable){
		
		if((progressDialog == null || !progressDialog.isShowing()) &&
				(alertDialog == null || !alertDialog.isShowing()) ){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(ActivityResource.get());
			builder.setView(content);
			builder.setTitle(title);
			builder.setCancelable(cancelable);
			alertDialog = builder.create();
			alertDialog.show();
		}
		
	}
	
	public static void hideAlertDialog(){
		
		if(alertDialog != null && alertDialog.isShowing()){
			alertDialog.dismiss();
			alertDialog = null;
		}
		
	}
	
	
	public static void showLoadingDialog(){
		
		if(progressDialog == null){
		
		progressDialog = new ProgressDialog(ActivityResource.get());
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(LOADING_MESSAGE);
		
		momentOfPopup = System.currentTimeMillis();
		
		progressDialog.show();
		
		}
		
	}
	
	public static void hideLoadingDialog(){
		
		if(progressDialog != null && progressDialog.isShowing()){
			
			long timeElapsed = System.currentTimeMillis() - momentOfPopup;
			
			if(timeElapsed < MIN_LOADING_DURATION){
				try {
					Thread.sleep(MIN_LOADING_DURATION - timeElapsed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			progressDialog.dismiss();
			progressDialog = null;
			
		}
		
	}
	
	
	
	
}
