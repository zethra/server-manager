package com.zethratech.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
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

    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int MIN_VALUE = 0;

    private LinearLayout linearLayout;
    private NumberPicker minuetsPicker;
    private NumberPicker secondsPicker;
    private int value;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams pickerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pickerParams.setMargins(20, 0, 0, 0);

        minuetsPicker = new NumberPicker(getContext());
        minuetsPicker.setLayoutParams(layoutParams);
        secondsPicker = new NumberPicker(getContext());
        secondsPicker.setLayoutParams(layoutParams);

        linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.addView(minuetsPicker, pickerParams);
        linearLayout.addView(secondsPicker);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(linearLayout);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        minuetsPicker.setMinValue(MIN_VALUE);
        minuetsPicker.setMaxValue(MAX_VALUE);
        minuetsPicker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            setValue(minuetsPicker.getValue());
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
