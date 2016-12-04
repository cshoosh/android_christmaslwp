package com.zerotwoone.livewallpaper.xmasparallax;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.unity3d.player.UnityPlayer;

/***
 * 
 * @see http
 *      ://developer.android.com/reference/android/service/wallpaper/package-
 *      summary.html
 */
public class XmasWallpaperService extends WallpaperService {
	private CustomPlayer player;
	private int mLimit = 5;
	private int glesVersion = 2;

	/**
	 * Called by the system when the service is first created. Do not call this
	 * method directly.
	 */
	public void onCreate() {

		super.onCreate();
		player = CustomPlayer.getInstance(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		player.quit();
	}

	class WallpaperEngine extends Engine implements SensorEventListener,
			OnSharedPreferenceChangeListener {

		private static final int MS_IN_MINUTES = 1000 * 60;
		private static final int MS_IN_HOUR = MS_IN_MINUTES * 60;

		private static final int NUMBER_OF_SCENES = 3;
		
		private static final int MAX_SENSI = 30;
		private static final int MIN_SENSI = 10;

		private static final float MAX_SHAKE = 1.5f;
		private static final float MIN_SHAKE = 0f;
		
		private float SHAKE_SENSI = MIN_SHAKE;
		
		private SensorManager mManager;
		private Sensor mSensor;
		private float[] mLastValues = new float[3];
		private float[] mDiff = new float[3];
		private float[] values = new float[3];

		private int count;
		private boolean isRandomEnable;
		private int mTime;

		private Handler mHandler;

		private GLWallSurface mSurfaceView;

		private Runnable mScheduleRandomBGTask = new Runnable() {

			@Override
			public void run() {
				int current = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(
						XmasWallpaperService.this).getString(getString(
						R.string.key_sceneselection), "0"));
				int rand = 0;
				
				do{
					rand = new Random().nextInt(NUMBER_OF_SCENES);
				}while(rand == current);
				
				if (player != null) {
					if (isRandomEnable) {
						
						PreferenceManager.getDefaultSharedPreferences(XmasWallpaperService.this)
							.edit().putString(getString(R.string.key_sceneselection), 
							"" + rand).commit();
						
						if (mHandler != null)
							mHandler.postDelayed(this, mTime);
					}
				}
			}
		};

		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			mHandler = new Handler();
			mManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			if (!isPreview() && player != null) {
				mSurfaceView = new GLWallSurface(XmasWallpaperService.this);
				mSurfaceView.setRenderer(player);
				
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						player.setWaitTimeOverCheck(true);
						initPreferences();
					}
				}, 5000L);
			}
		};
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
			if (isPreview()){
				String no = PreferenceManager.getDefaultSharedPreferences(
						XmasWallpaperService.this).getString(
						getString(R.string.key_sceneselection), "0");

				int noInt = Integer.valueOf(no);
				drawBitmap(noInt);
			}
			
			PreferenceManager.getDefaultSharedPreferences(XmasWallpaperService.this)
				.registerOnSharedPreferenceChangeListener(this);
		}
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			
			PreferenceManager.getDefaultSharedPreferences(XmasWallpaperService.this)
			.unregisterOnSharedPreferenceChangeListener(this);
		}
		
		private boolean drawBitmap(int no) {
			Canvas c = null;
			try {
				c = getSurfaceHolder().lockCanvas();				
				if (c != null) {
					boolean portrait = getResources().getConfiguration().orientation
							== Configuration.ORIENTATION_PORTRAIT;
					
					String mode = portrait ? "p" : "l";
					Bitmap bmp = BitmapFactory
							.decodeStream(XmasWallpaperService.this.getAssets()
									.open("preview/0" + no + mode + ".png"));
					Rect boundsframe = getSurfaceHolder().getSurfaceFrame();
					
					c.drawBitmap(bmp, null, boundsframe,null);
					bmp.recycle();
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (c != null){
					getSurfaceHolder().unlockCanvasAndPost(c);
					return true;
				}
			}
			
			return false;
		}
		
		public void onVisibilityChanged(boolean visible) {

			super.onVisibilityChanged(visible);
			changePlayerState(visible);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				mDiff[0] = mLastValues[0] - event.values[0];
				mDiff[1] = mLastValues[1] - event.values[1];
				mDiff[2] = mLastValues[2] - event.values[2];

				if (Math.abs(mDiff[0]) > SHAKE_SENSI
						|| Math.abs(mDiff[1]) > SHAKE_SENSI) {
					for (int i = 0; i < values.length; i++) {
						values[i] += mDiff[i];
						values[i] = Math.max(Math.min(mLimit, values[i]),
								-mLimit);
					}

					/*
					 * Log.d("value", "" + mDiff[0] + " : " + event.values[0] +
					 * " : " + mLastValues[0]);
					 */
					count = 0;
				} else {
					if (count > 60)
						for (int i = 0; i < values.length; i++)
							values[i] *= 0.5f;
					else
						count++;
				}

				UnityPlayer.UnitySendMessage("GameObject", "rotate", ""
						+ -values[1] + ":" + values[0] + ":0");

				// Log.d("values", "" + values[1] + "  " + values[0]);
				System.arraycopy(event.values, 0, mLastValues, 0, 3);
			}
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key == getString(R.string.key_sceneselection)) {
				String no = sharedPreferences.getString(key, "0");
				if (player != null) {
					UnityPlayer.UnitySendMessage("GameObject", "changescene",
							no);
					
					//setsnow(sharedPreferences);
				}
				
				if (isPreview()){
					drawBitmap(Integer.valueOf(no));
				}
			} else if (key == getString(R.string.key_snow)) {
				setsnow(sharedPreferences);
			} else if (key == getString(R.string.key_bgtimercheck)) {
				if (isRandomEnable = sharedPreferences.getBoolean(key,
						getResources().getBoolean(R.bool.def_bgtimercheck)))
					mHandler.postDelayed(mScheduleRandomBGTask, mTime);
				else
					mHandler.removeCallbacks(mScheduleRandomBGTask);
			} else if (key == getString(R.string.key_sensi)) {
				if (player != null)
					UnityPlayer.UnitySendMessage("GameObject", "changesmooth", ""
							+ getSensi(sharedPreferences.getInt(key,
							getResources().getInteger(R.integer.def_sensi))));
				
			} else if (key == getString(R.string.key_shake)){
				SHAKE_SENSI = getShake(sharedPreferences.getInt(key, 80));
			}
			else if (key == getString(R.string.key_bgtimercount)) {
				mTime = sharedPreferences.getInt(key, getResources()
						.getInteger(R.integer.def_bgtimer)) * MS_IN_HOUR;
			} else if (key == getString(R.string.key_lowbattery)) {
				if (player != null)
					player.setBatteryCheck(sharedPreferences.getBoolean(
						key, getResources().getBoolean(R.bool.def_lowbatterycheck)));
			}
		}

		private void initPreferences() {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			isRandomEnable = pref.getBoolean(
					getString(R.string.key_bgtimercheck), getResources()
							.getBoolean(R.bool.def_bgtimercheck));
			
			mTime = pref.getInt(getString(R.string.key_bgtimercount),
					getResources().getInteger(R.integer.def_bgtimer))
					* MS_IN_HOUR;
			SHAKE_SENSI = getShake(pref.getInt(getString(R.string.key_shake), 80));

			
			
			if (player != null) {
				UnityPlayer.UnitySendMessage("GameObject", "changescene", pref
						.getString(getString(R.string.key_sceneselection), "0"));
				
				setsnow(pref);
				
				UnityPlayer.UnitySendMessage("GameObject",	"changesmooth",
						"" + getSensi(pref.getInt(getString(R.string.key_sensi),
						getResources().getInteger(R.integer.def_sensi))));
				
				player.setBatteryCheck(pref.getBoolean(
						getString(R.string.key_lowbattery), getResources()
								.getBoolean(R.bool.def_lowbatterycheck)));
				
				if (isRandomEnable && mHandler != null)
					mHandler.postDelayed(mScheduleRandomBGTask, mTime);
			}
		}

		private int getSensi(int progress) {
			float per = progress / 100f;
			return (int) (MIN_SENSI + (per * (MAX_SENSI - MIN_SENSI)));
		}
		
		private int getShake(int progress) {
			float per = progress / 100f;
			return (int) (MIN_SHAKE + (per * (MAX_SHAKE - MIN_SHAKE)));
		}
		
		private void setsnow(final SharedPreferences pref){
			if (mHandler != null && player != null){
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						UnityPlayer.UnitySendMessage("GameObject", "snow", String
								.valueOf(pref.getBoolean(getString(R.string.key_snow),
										getResources().getBoolean(R.bool.def_snow))));
					}
				});
				
			}
		}
		private void changePlayerState(boolean state) {
			if (player != null) {
				if (state) {
					if (!isPreview()) {
						if (mManager != null)
							mManager.registerListener(this, mSensor,
									SensorManager.SENSOR_DELAY_GAME);
						
						if (mSurfaceView != null)
							mSurfaceView.onResume();
						
						player.resume();
					}
				} else {
					if (!isPreview()) {
						if (mManager != null)
							mManager.unregisterListener(this, mSensor);
						
						if (mSurfaceView != null)
							mSurfaceView.onPause();
						
						player.pause();
					}
				}
			}
		}

		public class GLWallSurface extends GLSurfaceView {
			public GLWallSurface(Context context) {
				super(context);
				setEGLContextClientVersion(glesVersion);
				setPreserveEGLContextOnPause(true);
			}

			@Override
			public SurfaceHolder getHolder() {
				return getSurfaceHolder();
			}
		}
	}

	/**
	 * Return a new instance of the wallpaper's engine.
	 */
	@Override
	public Engine onCreateEngine() {
		//Debug.waitForDebugger();
		final Engine myEngine = new WallpaperEngine();
		return myEngine;
	}

	/**
	 * Starts new activity (settings menu).
	 */
	/*
	 * public void StartActivity() {
	 * 
	 * WallpaperInfo localWallpaperInfo = ((WallpaperManager)
	 * getSystemService("wallpaper")) .getWallpaperInfo(); if
	 * (localWallpaperInfo != null) { String str1 =
	 * localWallpaperInfo.getSettingsActivity();
	 * 
	 * if (str1 != null) { String str2 = localWallpaperInfo.getPackageName();
	 * Intent localIntent1 = new Intent(); ComponentName localComponentName =
	 * new ComponentName(str2, str1);
	 * localIntent1.setComponent(localComponentName);
	 * localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * startActivity(localIntent1); } }
	 * 
	 * }
	 */
}
