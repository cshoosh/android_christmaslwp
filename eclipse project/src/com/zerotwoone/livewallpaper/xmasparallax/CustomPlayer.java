package com.zerotwoone.livewallpaper.xmasparallax;

import javax.microedition.khronos.opengles.GL10;

import android.content.ContextWrapper;

import com.unity3d.player.UnityPlayer;

public class CustomPlayer extends UnityPlayer {

	private static CustomPlayer inst;
	
	public static CustomPlayer getInstance(ContextWrapper context){
		if (inst == null){
			inst = new CustomPlayer(context);
			inst.init(2, true);
		}
		
		return inst;
	}
	
	public CustomPlayer(ContextWrapper arg0) {
		super(arg0);
	}

	private static final int FPS_MAX = 10;
	private static final float FRAME_PERIODS = 1000f / FPS_MAX;

	//private long TimeDelta;
	//private int fpsCurrent;
	private boolean isBatteryCheckEnabled;
	private boolean isWaitTimeOver = false;

	

	@Override
	public void onDrawFrame(GL10 arg0) {
		long TimeBegin = System.currentTimeMillis();
		super.onDrawFrame(arg0);
		
		if (!isWaitTimeOver){
			arg0.glLoadIdentity();
			arg0.glClearColor(0, 0, 0, 1);
			arg0.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);
			
		}
		
		if (BatteryIndicator.LOW_BATTERY && isBatteryCheckEnabled){
			long TimeDiff = System.currentTimeMillis() - TimeBegin;
			long TimeSleep = (int) (FRAME_PERIODS - TimeDiff);

			if (TimeSleep > 0) {
				try {
					Thread.sleep(TimeSleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// DEBUG
		/*if (System.currentTimeMillis() - TimeDelta >= 1000) {
			TimeDelta = System.currentTimeMillis();
			Log.d("FPS", "" + fpsCurrent);
			fpsCurrent = 0;
		}
		fpsCurrent++;*/
	}
	
	public void setBatteryCheck(boolean check){
		isBatteryCheckEnabled = check;
	}
	
	public void setWaitTimeOverCheck(boolean check){
		isWaitTimeOver = check;
	}
}
