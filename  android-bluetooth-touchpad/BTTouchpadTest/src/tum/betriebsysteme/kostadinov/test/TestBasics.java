package tum.betriebsysteme.kostadinov.test;

import tum.betriebsysteme.kostadinov.TouchActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public abstract class TestBasics extends ActivityInstrumentationTestCase2<TouchActivity>{

	protected Solo solo;

	public static final String TAG = "TEST";
	
	public TestBasics() {
		super("tum.betriebsysteme.kostadinov", TouchActivity.class);
	}

	protected void setUp() throws Exception {
		
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

	}

	protected void tearDown() throws Exception {
		
		try {
			solo.finalize();
			} catch (Throwable e) {
			e.printStackTrace();
			}
			getActivity().finish();
			super.tearDown();

	}

	public abstract void testDisplayBlackBox();

	
	
}
