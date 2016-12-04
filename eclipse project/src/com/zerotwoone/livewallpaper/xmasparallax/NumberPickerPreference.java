package com.zerotwoone.livewallpaper.xmasparallax;
import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class NumberPickerPreference extends DialogPreference {

	private int mNumber;
	//private int DEFAULT_VALUE;
	private NumberPicker mNumberPicker;

	public NumberPickerPreference(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		//context.getResources().getInteger(R.integer.def_randomnumber);

		//setDialogIcon(null);

		//setLayoutResource(R.layout.pref_number_picker);
		//setPositiveButtonText(android.R.string.ok);
		//setNegativeButtonText(android.R.string.cancel);

		//setPersistent(true);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		//context.getResources().getInteger(R.integer.def_randomnumber);

		//setDialogIcon(null);

		//setLayoutResource(R.layout.pref_number_picker);
		//setPositiveButtonText(android.R.string.ok);
		//setNegativeButtonText(android.R.string.cancel);

		//setPersistent(true);
	}
	
	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		mNumberPicker = (NumberPicker) view.findViewById(R.id.numberpicker);
		mNumberPicker.setMaxValue(10);
		mNumberPicker.setMinValue(1);
		
		mNumberPicker.setValue(mNumber);
		
		mNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				mNumber = newVal;
			}
		});
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		
		if (restorePersistedValue)
			mNumber = PreferenceManager.getDefaultSharedPreferences(getContext())
					.getInt(getKey(), getContext().getResources()
					.getInteger(R.integer.def_bgtimer));
		
		else
			mNumber = getContext().getResources().getInteger(R.integer.def_bgtimer);
		
		
		
		super.onSetInitialValue(restorePersistedValue, defaultValue);
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {

		if (positiveResult){
			mNumber = mNumberPicker.getValue();
			persistInt(mNumber);
		}
		super.onDialogClosed(positiveResult);
	}

}