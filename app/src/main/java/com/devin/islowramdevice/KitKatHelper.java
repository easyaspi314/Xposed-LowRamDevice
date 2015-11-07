package com.devin.islowramdevice;

import android.app.ActivityManager;
import android.content.Context;
import android.annotation.TargetApi;

public class KitKatHelper 
{
	@TargetApi(19)
	public static boolean isLowRamDevice(ActivityManager am) {
		return am.isLowRamDevice();
	}
}
