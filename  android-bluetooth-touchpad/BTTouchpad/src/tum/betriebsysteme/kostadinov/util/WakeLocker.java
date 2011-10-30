package tum.betriebsysteme.kostadinov.util;


import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class WakeLocker {
	
	private static WakeLock wakeLock = null;
	public static void acquireWakeLocker(){
		
		 PowerManager powerManager = (PowerManager) ActivityResource.get().getSystemService(Context.POWER_SERVICE);
		 wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TOUCHPAD");
		 wakeLock.acquire();
		
	}
	
	public static void releaseWakeLocker(){
		if(wakeLock != null){
			wakeLock.release();
			wakeLock=null;
		}
		
	}
	

}
