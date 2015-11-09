package com.devin.islowramdevice;

import android.app.ActivityManager;
import android.content.Context;
import android.annotation.TargetApi;
import static de.robv.android.xposed.XposedHelpers.*;
import android.support.v4.app.ActivityManager;

@TargetApi(19)
class KitKatHelper 
{
	protected static boolean isLowRamDevice(ActivityManager am) {
		return am.isLowRamDevice();
	}
    /**
     * Convienently get all the values we need for the Settings Advanced result dialog.
     * @return A list of the results.
     */
    protected static String isLowRamDevice(Context context, ClassLoader loader) throws Throwable {
        StringBuilder sb = new StringBuilder("")
        
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        Class<?> amClass = am.getClass();

        Class<?> amCompatKKClass = findClass("android.support.v4.app.ActivityManagerCompatKitKat", lpparam.classLoader);
        
        sb.append("ActivityManager.isLowRamDevice() = ");
        sb.append(am.isLowRamDevice());
        sb.append("\n");

        sb.append("ActivityManager.isLowRamDeviceStatic() = ");
        sb.append((Boolean) callStaticMethod(amClass, "isLowRamDeviceStatic", null));
        sb.append("\n");

        sb.append("System.getProperty(\"ro.config.low_ram\", \"false\") = ");
        sb.append(System.getProperty("ro.config.low_ram", "false"));
        sb.append("\n");

        sb.append("ActivityManagerCompat.isLowRamDevice() = ");
        sb.append(ActivityManagerCompat.isLowRamDevice(am));
        sb.append("\n");

        sb.append("ActivityManagerCompatKitKat.isLowRamDevice() = ");
        sb.append((Boolean) callStaticMethod(amCompatKKClass, "isLowRamDevice", am));

        return sb.toString();
    }
}
