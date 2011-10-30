package tum.betriebsysteme.kostadinov.ui.options;

import tum.betriebsysteme.kostadinov.R;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReport;
import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;
import tum.betriebsysteme.kostadinov.util.ActivityResource;
import tum.betriebsysteme.kostadinov.util.State;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class Prezi extends Option implements OnClickListener{

	HIDReportKeyboard report;
	
	public Prezi(OptionListener optionListener) {
		super(optionListener);
	}

	@Override
	public void initOptionUI() {
		
		State.setUIState(State.UI_STATE_OPTION);
		
		ActivityResource.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		ViewGroup main = (ViewGroup) ActivityResource.get().findViewById(R.id.main);
		
		report = new HIDReportKeyboard();
		
		View gamepadView = ActivityResource.inflate(R.layout.gamepad);
		
		gamepadView.findViewById(R.id.gamepad_upper_left).setOnClickListener(this);
		gamepadView.findViewById(R.id.gamepad_upper_right).setOnClickListener(this);
		gamepadView.findViewById(R.id.gamepad_bottom_left).setOnClickListener(this);
		gamepadView.findViewById(R.id.gamepad_bottom_right).setOnClickListener(this);
		
		main.removeAllViews();
		main.addView(gamepadView);
		main.invalidate();
		
		optionActive = true; 
		
	}

	@Override
	public void destroyOptionUI() {
		optionActive = false;
		
	}

	@Override
	public void onClick(View v) {
		if(!optionActive) return;
		
		switch(v.getId()){
		
		case R.id.gamepad_upper_left:{
			report.setSingleKeycode(0x4F);
			break;
		}
		case R.id.gamepad_upper_right:{
			report.setSingleKeycode(0x52);
			break;
		}
		case R.id.gamepad_bottom_left:{
			report.setSingleKeycode(0x50);
			break;
		}
		case R.id.gamepad_bottom_right:{
			report.setSingleKeycode(0x51);
			break;
		}
		
		}
		
		this.optionListener.onOptionEvent(report);
		report.setSingleKeycode(HIDReport.EMPTY_KEYCODE);
		this.optionListener.onOptionEvent(report);
		
	}

	
	
	
	
	
//	public Prezi(OptionListener optionListener) {
//		super(optionListener);
//	}
//	
//	@Override
//	public void onEvent(float[] values) {}
//	
//
//	@Override
//	public void handleEvent(MotionEvent event) {
//		
//		if(!this.optionActive) return;
//		
//		if(
//				
//				event.getAction() == MotionEvent.ACTION_DOWN ||
//				event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN ||
//				event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN ||
//				event.getAction() == MotionEvent.ACTION_POINTER_3_DOWN
//				
//				){
//					
//			Log.v(TAG, "DOWN");
//			
//			for(int i=0; i < event.getPointerCount(); i++){
//				
//				int x = (int) event.getX(i);
//				int y = (int) event.getY(i);
//				
//				boolean[] pressedButtons = pressedButtons(x,y,true);
//				
//				report.setKeycodes(
//						
//						// Right
//						(pressedButtons[0]) ? 0x4F : report.getKeycode(0),
//						//0x11 -> left control
//						(pressedButtons[1]) ? 0x52 : report.getKeycode(1),
//						// Down arrow
//						(pressedButtons[2]) ? 0x50 : report.getKeycode(2),
//						//0x2c -> spacebar
//						(pressedButtons[3]) ? 0x51 : report.getKeycode(3),
//						     
//						report.getKeycode(4),
//						report.getKeycode(5)
//						
//				);	
//				
//				this.optionListener.onOptionEvent(report);
//				
//			}
// 							
//		}else if(
// 				
// 				event.getAction() == MotionEvent.ACTION_UP ||
// 				event.getAction() == MotionEvent.ACTION_POINTER_1_UP ||
//				event.getAction() == MotionEvent.ACTION_POINTER_2_UP ||
//	  			event.getAction() == MotionEvent.ACTION_POINTER_3_UP
//				
//				){
//					
//		for(int i=0; i < event.getPointerCount(); i++){
//	 			
//				int x = (int) event.getX(i);
//				int y = (int) event.getY(i);
//				
//				boolean[] pressedButtons = pressedButtons(x,y,false);
//				
//				report.setKeycodes(
//						
//						// Up arrow
//						(pressedButtons[0]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(0),
//						//0x11 -> left control
//						(pressedButtons[1]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(1),
//						// Down arrow
//						(pressedButtons[2]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(2),
//						//0x20 -> spacebar
//						(pressedButtons[3]) ? HIDReportKeyboard.EMPTY_KEYCODE : report.getKeycode(3),
//						
//						report.getKeycode(4),
//						report.getKeycode(5)
//						
//				);	
//				
//				this.optionListener.onOptionEvent(report);
//			}
//					
//				}
//
//	}
	
	

}
