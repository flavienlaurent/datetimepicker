package com.fourmob.datetimepicker;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.View;

public class Utils {
	public static int getDaysInMonth(int month, int year) {
		switch (month) {
		default:
			throw new IllegalArgumentException("Invalid Month");
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			return 31;
		case 3:
		case 5:
		case 8:
		case 10:
			return 30;
		case 1:
			if (year % 4 == 0)
				return 29;
			return 28;
		}
	}

	public static ObjectAnimator getPulseAnimator(View view, float animVal1, float animVal2) {
		Keyframe keyframe1 = Keyframe.ofFloat(0.0F, 1.0F);
		Keyframe keyframe2 = Keyframe.ofFloat(0.275F, animVal1);
		Keyframe keyframe3 = Keyframe.ofFloat(0.69F, animVal2);
		Keyframe keyframe4 = Keyframe.ofFloat(1.0F, 1.0F);
		ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[] { PropertyValuesHolder.ofKeyframe("scaleX", new Keyframe[] { keyframe1, keyframe2, keyframe3, keyframe4 }),
				PropertyValuesHolder.ofKeyframe("scaleY", new Keyframe[] { keyframe1, keyframe2, keyframe3, keyframe4 }) });
		animator.setDuration(544L);
		return animator;
	}

	public static boolean isJellybeanOrLater() {
		return Build.VERSION.SDK_INT >= 16;
	}

	@SuppressLint({ "NewApi" })
	public static void tryAccessibilityAnnounce(View view, CharSequence charSequence) {
		if ((isJellybeanOrLater()) && (view != null) && (charSequence != null))
			view.announceForAccessibility(charSequence);
	}
}