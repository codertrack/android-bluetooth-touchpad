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
import tum.betriebsysteme.kostadinov.util.CONSTANTS;
import tum.betriebsysteme.kostadinov.util.State;
import android.bluetooth.BluetoothDevice;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class OptionList {
	
	private static final int DISABLED_OPTION_TEXT_COLOR = 0xFFF46F6F;
	
	public static interface OptionListListener{
		void onOptionChoosen(String optionName, int index);
	}
	
	private OptionListListener optionListListener;
	
	public OptionList(OptionListListener optionListListener){
		this.optionListListener = optionListListener;
	}
	
	
	public void showOptions(){
		
		State.setUIState(State.UI_STATE_OPTION_LIST);
	
		ViewGroup mainView = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		ViewGroup optionsView = (ViewGroup) ActivityResource.inflate(R.layout.option_list);
		ViewGroup optionListView = (ViewGroup) optionsView.findViewById(R.id.option_list);
		
		for(int i=0; i<CONSTANTS.OPTIONS.length; i++){
			optionListView.addView(toOptionItem(CONSTANTS.OPTIONS[i], i),
					new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		mainView.removeAllViews();
		mainView.addView(optionsView,
				new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	private View toOptionItem(final String optionName,final int index){
		
		View optionView = ActivityResource.inflate(R.layout.option);
		TextView optionText = (TextView) optionView.findViewById(R.id.option_name);
		
		if(!State.configIncludesOption(index)){
			((TextView) optionView.findViewById(R.id.option_name)).setTextColor(DISABLED_OPTION_TEXT_COLOR);
		}
		
		optionText.setText(optionName);
		
		
		optionView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(State.configIncludesOption(index))
				
					optionListListener.onOptionChoosen(optionName, index);
				
				else {
					//TODO: Show dialog, explaining disabled options
				}
			}
			
		});	
		return optionView;
		
		
	}
	
}
