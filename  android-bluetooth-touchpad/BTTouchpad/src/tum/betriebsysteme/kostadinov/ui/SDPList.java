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

import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.sdp.SDPRegister;
import tum.betriebsysteme.kostadinov.ui.OptionList.OptionListListener;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.CONSTANTS;
import tum.betriebsysteme.kostadinov.util.State;

public class SDPList implements OnClickListener {


	private SDPListListener sdpListListener;
	
	public static interface SDPListListener{
			
		public void onSDPChoosen(int sdpType);
		
	}

	public SDPList(SDPListListener sdpListListener){
		this.sdpListListener = sdpListListener;
	}
	
	public void showConfigurationOptions(){
		
		State.setUIState(State.UI_STATE_SDP_CONFIGURATION);
		
		ViewGroup mainView = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		ViewGroup sdpView = (ViewGroup) ActivityResource.inflate(R.layout.sdplist);
		
		sdpView.findViewById(R.id.sdp_list_relative).setOnClickListener(this);
		sdpView.findViewById(R.id.sdp_list_absolute).setOnClickListener(this);
		sdpView.findViewById(R.id.sdp_list_help).setOnClickListener(this);

		mainView.removeAllViews();
		mainView.addView(sdpView);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mainView.invalidate();
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId())
		{
		
		case R.id.sdp_list_relative : {
			this.sdpListListener.onSDPChoosen(SDPRegister.SDP_CONFIG_MOUSE_RELATIVE);
			break;
		}
		
		case R.id.sdp_list_absolute : {
			this.sdpListListener.onSDPChoosen(SDPRegister.SDP_CONFIG_MOUSE_ABSOLUTE);
			break;
		}
		
		case R.id.sdp_list_help : {
			//TODO: Implement help button
			break;
		}
		
		}
		
	}
	
	
	
	
}
