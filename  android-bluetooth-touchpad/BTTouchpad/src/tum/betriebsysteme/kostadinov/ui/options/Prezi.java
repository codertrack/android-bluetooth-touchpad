package tum.betriebsysteme.kostadinov.ui.options;

import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import android.util.Log;
import android.view.MotionEvent;

public class Prezi extends Gamepad {

	public Prezi(OptionListener optionListener) {
		super(optionListener);
	}
	
	@Override
	public void onEvent(float[] values) {}
	

	@Override
	public void handleEvent(MotionEvent event) {
		
		if(!this.optionActive) return;
		
		if(
				
				event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN ||
				event.getAction() == MotionEvent.ACTION_POINTER_3_DOWN
				
				){
					
			Log.v(TAG, "DOWN");
			
			for(int i=0; i < event.getPointerCount(); i++){
				
				int x = (int) event.getX(i);
				int y = (int) event.getY(i);
				
				boolean[] pressedButtons = pressedButtons(x,y,true);
				
				report.setKeycodes(
						
						// Right
						(pressedButtons[0]) ? 0x4F : report.getKeycode(0),
						//0x11 -> left control
						(pressedButtons[1]) ? 0x52 : report.getKeycode(1),
						// Down arrow
						(pressedButtons[2]) ? 0x50 : report.getKeycode(2),
						//0x2c -> spacebar
						(pressedButtons[3]) ? 0x51 : report.getKeycode(3),
						     
						report.getKeycode(4),
						report.getKeycode(5)
						
				);	
				
				this.optionListener.onOptionEvent(report);
				
			}
 							
		}else if(
 				
 				event.getAction() == MotionEvent.ACTION_UP ||
 				event.getAction() == MotionEvent.ACTION_POINTER_1_UP ||
				event.getAction() == MotionEvent.ACTION_POINTER_2_UP ||
	  			event.getAction() == MotionEvent.ACTION_POINTER_3_UP
				
				){
					
		for(int i=0; i < event.getPointerCount(); i++){
	 			
				int x = (int) event.getX(i);
				int y = (int) event.getY(i);
				
				boolean[] pressedButtons = pressedButtons(x,y,false);
				
				report.setKeycodes(
						
						// Up arrow
						(pressedButtons[0]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(0),
						//0x11 -> left control
						(pressedButtons[1]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(1),
						// Down arrow
						(pressedButtons[2]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(2),
						//0x20 -> spacebar
						(pressedButtons[3]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(3),
						
						report.getKeycode(4),
						report.getKeycode(5)
						
				);	
				
				this.optionListener.onOptionEvent(report);
			}
					
				}

	}
	
	

}
