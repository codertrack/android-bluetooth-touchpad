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

package tum.betriebsysteme.kostadinov.ui.options;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import tum.betriebsysteme.kostadinov.ui.options.util.SharedFunctions;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;

public class Voice extends Option implements OnClickListener {

	public static final int RECOGNITION_SUCCESS_CODE = 0x112;
	
	public Voice(OptionListener optionListener) {
		super(optionListener);
	}

	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		View voiceView = ActivityResource.inflate(R.layout.voice);
		voiceView.findViewById(R.id.voice_button).setOnClickListener(this);
		main.removeAllViews();
		main.addView(voiceView);
		main.invalidate();
		
		optionActive = true;   
		  
		
	}

	@Override
	public void destroyOptionUI() {
		optionActive = false;
		
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
	    ActivityResource.get().startActivityForResult(intent, RECOGNITION_SUCCESS_CODE);	
		
	}

	public void sendText(String message) {
		
		//Set the proper end of sentance.
		message += ". "; 
		
		for(int i=0; i<message.length(); i++){
			
			int sign = message.charAt(i);
			HIDReportKeyboard report = SharedFunctions.getReportFromKey(sign);
			
			if(i == 0){
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
			}
			
			this.optionListener.onOptionEvent(report);
			
		}
		
		this.optionListener.onOptionEvent(new HIDReportKeyboard());
	
	}

	
	
	
}
