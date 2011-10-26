package tum.betriebsysteme.kostadinov.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class SensorUtility implements SensorEventListener {

	private SensorUtility(){}
	
	public static SensorUtility createInstance(){
		return new SensorUtility();
	}
	
	private SensorManager sensorManager;
	
	private boolean computeX = false;
	private boolean computeY = false;
	private boolean computeZ = false;
	
	private SensorListener sensorListener;
	
	public static interface SensorListener{
		public void onEvent(float[] values);
		public void onAccuracyChange(int accuracy);
	}
	
	
	private float[] xValues = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
	private float[] yValues = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
	private float[] zValues = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
	
	private float[] fibonaci = new float[]{1f, 1f, 1.2f, 1.3f, 1.5f, 1.8f, 2.3f, 3.3f, 4.4f, 5.5f };
	private float fiboSum = 23.3f;

	
	public void startListening(boolean computeX, boolean computeY, boolean computeZ, SensorListener sensorListener){
		
		this.computeX = computeX;
		this.computeY = computeY;
		this.computeZ = computeZ;
		
		this.sensorListener = sensorListener;
		
		sensorManager = (SensorManager) ActivityResource.get().getSystemService(Context.SENSOR_SERVICE);
        
        sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_FASTEST);
		
		
		
	}


	public void cancel(){
		sensorManager.unregisterListener(this);
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		this.sensorListener.onAccuracyChange(accuracy);
	}




	@Override
	public void onSensorChanged(SensorEvent event) {
		
		this.sensorListener.onEvent(new float[]{
			
			computeX ? calc(0, event.values[0]) : 0f,
					computeY ? calc(1, event.values[1]): 0f,
							computeZ ? calc(2, event.values[2]) : 0f,
			
		});
		
	}
	
	private float calc(int index, float value){
		
		float[] vals = (index == 0 ) ? this.xValues : (index == 1) ? this.yValues : this.zValues;
		
		for ( int i=0 ; i< vals.length-1; i++){
			vals[i] = vals[i+1];
		}
		
		vals[vals.length-1] = value;
		
		float result = 0f;
		
		for(int i=0; i<vals.length;i++){
			result += vals[i]*fibonaci[i];
		}
		
		return result/fiboSum;
		
	}
	
	
	
	
	
	
}
