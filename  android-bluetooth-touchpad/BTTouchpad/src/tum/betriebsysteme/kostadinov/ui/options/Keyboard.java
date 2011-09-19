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

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;

public class Keyboard extends Option implements TextWatcher {

	public Keyboard(OptionListener optionListener) {
		super(optionListener);
	}

	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
	
ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		View keyboardView = ActivityResource.inflate(R.layout.keyboard);
		EditText keyboardInput = (EditText) keyboardView.findViewById(R.id.keyboard_input);
		keyboardInput.addTextChangedListener(this);
		main.removeAllViews();
		main.addView(keyboardView);
		main.invalidate();
		
		optionActive = true;   
		  
	}

	@Override
	public void destroyOptionUI() {
		optionActive = false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		//do nothing
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		//do nothing
	}

	@Override
	public void onTextChanged(CharSequence sequence, int start, int before, int count) {
	
		if(!optionActive) return;
		
		if(before > 0) {
			HIDReportKeyboard report = new HIDReportKeyboard();
			report.setSingleKeycode(0x2A);
			this.optionListener.onOptionEvent(report);
			this.optionListener.onOptionEvent(new HIDReportKeyboard());
			return;
		}
		
		int sign = sequence.charAt(start);

		Log.d("KEYBOARD", "Char code: "+sign);
		
		if (49 <= sign && sign<=57 ){
			HIDReportKeyboard report = new HIDReportKeyboard();
			report.setSingleKeycode(sign-19);
			this.optionListener.onOptionEvent(report);
		}
		
		else if (65 <= sign && sign<=90 ){
			HIDReportKeyboard report = new HIDReportKeyboard();
			report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
			report.setSingleKeycode(sign-61);
			this.optionListener.onOptionEvent(report); 
		}
		
		else if (97 <= sign && sign<=122 ){
			
			HIDReportKeyboard report = new HIDReportKeyboard();
			report.setSingleKeycode(sign-93);
			this.optionListener.onOptionEvent(report); 
		
		} else {
			
			HIDReportKeyboard report = new HIDReportKeyboard();
			
			switch (sign){
			
			case 64: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x1f);
				break;
			}
			
			case 35: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x20);
				break;
			}
			
			case 36: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x21);
				break;
			}
			
			case 37: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x22);
				break;
			}
			
			case 38: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x24);
				break;
			}
			
			case 42: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x25);
				break;
			}
			
			case 45: { 
				report.setSingleKeycode(0x2D);
				break;
			}
			
			case 43: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x2E);
				break;
			}
			
			case 40: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x26);
				break;
			}
			
			case 41: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x27);
				break;
			}
			
			case 33: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x1E);
				break;
			}
			
			case 34: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x34);
				break;
			}
			
			case 39: { 
				report.setSingleKeycode(0x34);
				break;
			}
			
			case 58: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x33);
				break;
			}
			
			case 59: { 
				report.setSingleKeycode(0x33);
				break;
			}
			
			case 47: { 
				report.setSingleKeycode(0x38);
				break;
			}
			
			case 63: { 
				report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
				report.setSingleKeycode(0x38);
				break;
			}
			
			case 44: { 
				report.setSingleKeycode(0x36);
				break;
			}
			
			case 32: { 
				report.setSingleKeycode(0x2C);
				break;
			}
			
			case 46: { 
				report.setSingleKeycode(0x37);
				break;
			}
			
			case 10: { 
				report.setSingleKeycode(0x28);
				break;
			}
			
			
			}
			
			this.optionListener.onOptionEvent(report);
			
		}
		
		this.optionListener.onOptionEvent(new HIDReportKeyboard());
	}

	
	
	
}
