package com.zethratech.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.zethratech.servermanager.R;

public class TimePickerPreference extends DialogPreference{

    public static final int MAX_VALUE = 60;
    public static final int MIN_VALUE = 0;

    private LinearLayout linearLayout;
    private NumberPicker minuetsPicker;
    private NumberPicker secondsPicker;
    private int value;
    private int minVal;
    private int secVal;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.widget_time_picker, null);

        minuetsPicker = (NumberPicker) layout.findViewById(R.id.minute);
        secondsPicker = (NumberPicker) layout.findViewById(R.id.seconds);
        return layout;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        minuetsPicker.setMinValue(MIN_VALUE);
        minuetsPicker.setMaxValue(MAX_VALUE);
        secondsPicker.setMinValue(MIN_VALUE);
        secondsPicker.setMaxValue(MAX_VALUE);

        int val = getValue();
        secVal = val % 60;
        minVal = (int)((val - secVal) / 60);

        minuetsPicker.setValue(minVal);
        secondsPicker.setValue(secVal);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            minVal = minuetsPicker.getValue();
            secVal = secondsPicker.getValue();
            setValue(minVal * 60 + secVal);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(MIN_VALUE) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }

    public int getValue() {
        return this.value;
    }
}
