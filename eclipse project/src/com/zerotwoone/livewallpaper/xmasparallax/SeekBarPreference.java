package com.zerotwoone.livewallpaper.xmasparallax;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends DialogPreference {

	private SeekBar mSeekBar;
	private int mValue;
	
	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		
		mSeekBar = (SeekBar) view.findViewById(R.id.progressbar);
		mSeekBar.setProgress(mValue);
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				mValue = arg1;
			}
		});
		
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		
		if (restorePersistedValue)
			mValue = PreferenceManager.getDefaultSharedPreferences(getContext())
						.getInt(getKey(), getContext().getResources()
						.getInteger(R.integer.def_sensi));
		else
			mValue = getContext().getResources().getInteger(R.integer.def_sensi);
		
		
		super.onSetInitialValue(restorePersistedValue, defaultValue);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult)
			persistInt(mValue);	
		
		super.onDialogClosed(positiveResult);
	}
}
