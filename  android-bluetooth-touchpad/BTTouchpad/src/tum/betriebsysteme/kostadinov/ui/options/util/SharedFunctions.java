package tum.betriebsysteme.kostadinov.ui.options.util;

import tum.betriebsysteme.kostadinov.btframework.report.HIDReportKeyboard;

public class SharedFunctions {

	public static HIDReportKeyboard getReportFromKey(int sign){
		
		HIDReportKeyboard report = new HIDReportKeyboard();
		
		if (49 <= sign && sign<=57 ){
			
			report.setSingleKeycode(sign-19);
			return report;
			
		}
		
		else if (65 <= sign && sign<=90 ){
			
			report.setModifier(HIDReportKeyboard.LEFT_SHIFT_MODIFIER);
			report.setSingleKeycode(sign-61);
			return report; 
		
		}
		
		else if (97 <= sign && sign<=122 ){
			
			report.setSingleKeycode(sign-93);
			return report; 
		
		} else {
		
			
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
			
			return report;
			
		}
		
		
		
	}
	
	
	
	
	
}
