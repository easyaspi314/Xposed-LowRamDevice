package com.devin.islowramdevice;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.*;
import android.os.Build;
import android.app.ActivityManager;
import android.content.Context;
import de.robv.android.xposed.XposedBridge;

public class XIsLowRamDevice implements IXposedHookLoadPackage {

    private String mode;
    
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XSharedPreferences prefs = new XSharedPreferences("com.devin.islowramdevice");
        mode = prefs.getString("is_low_ram", "default");

        if (mode == null) {
            prefs.edit().putString("is_low_ram", "default").apply();
            mode = "default";
        }
        if (lpparam.packageName.equals("com.devin.islowramdevice") {
            /**
             * Checking whether the module is enabled.
             */
            findAndHookMethod("com.devin.islowramdevice.MainActivity", lpparam.classLoader, "isModuleEnabled", new XC_MethodReplacement(){
                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                    return true;
                }
            });
            
            boolean mDefault = prefs.getString("default_is_low_ram_device", "");
            if (mDefault == null || (!mDefault.equals("true") && !mDefault.equals("false"))) {
                boolean value = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    value = System.getProperty("ro.build.low_ram", "false").equals("true");
                prefs.edit().putString("default_is_low_ram_device", value ? "true" : "false");
            }
            
        }
        if (mode.equals("true") || mode.equals("false")) {
            try {
                Class<?> amCompat = findClass("android.support.v4.app.ActivityManagerCompat", lpparam.classLoader);
                Class<?> amCompatKK = findClass("android.support.v4.app.ActivityManagerCompatKitKat", lpparam.classLoader);
                
                if (Build.VERSION.SDK_INT >= 19) {
                    findAndHookMethod("android.app.ActivityManager", lpparam.classLoader, "isLowRamDevice", new XC_MethodReplacement() {
    
                            @Override
                            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                                log(lpparam);
                                if (mode.equals("true"))
                                    return true;
                                else
                                    return false;
                            }
                        });
                    findAndHookMethod("android.app.ActivityManager", lpparam.classLoader, "isLowRamDeviceStatic", new XC_MethodReplacement() {

                            @Override
                            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                                log(lpparam);
                                if (mode.equals("true"))
                                    return true;
                                else
                                    return false;
                            }
                        });
                    /**
                     * {@see https://github.com/android/platform_frameworks_support/blob/master/v4/kitkat/android/support/v4/app/ActivityManagerCompatKitKat.java#L22}
                     */
                    findAndHookMethod(amCompatKK, "isLowRamDevice", ActivityManager.class /* am */, new XC_MethodReplacement() {

                            @Override
                            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                                log(lpparam);
                                if (mode.equals("true"))
                                    return true;
                                else
                                    return false;
                            }
                        });
                    /**
                     * Hook SystemProperties.get(String, String) to ensure we get the result we want. <i>Obviously</i>, we don't want to replace the whole method,
                     * that would be <u><b>INCREDIBLY</b></u> stupid. Seriously. 
                     */
                    findAndHookMethod("android.os.SystemProperties", lpparam.classLoader, "get", String.class /* key */, String.class /* def */, new XC_MethodHook() {

                            @Override
                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                                if (((String) p1.args[0] /* key */).equals("ro.config.low_ram")) {
                                    log(lpparam);
                                    if (mode.equals("true"))
                                        p1.setResult("true");
                                    else
                                        p1.setResult("false");
                                }
                            }
                        });
                    /**
                     * Hook System.getProperty(String, String) to ensure we get the result we want. <i>Obviously</i>, we don't want to replace the whole method,
                     * that would be <u><b>INCREDIBLY</b></u> stupid. Seriously. 
                     */
                    findAndHookMethod("java.lang.System", lpparam.classLoader, "getProperty", String.class /* name */, String.class /* defaultValue */, new XC_MethodHook() {

                            @Override
                            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                                if (((String) p1.args[0] /* name */).equals("ro.config.low_ram")) {
                                    log(lpparam);
                                    if (mode.equals("true"))
                                        p1.setResult("true");
                                    else
                                        p1.setResult("false");
                                }
                            }
                        });
                }

                /**
                 * {@see https://github.com/android/platform_frameworks_support/blob/master/v4/java/android/support/v4/app/ActivityManagerCompat.java#L38}
                 */
                findAndHookMethod(amCompat, "isLowRamDevice", ActivityManager.class /* am */, new XC_MethodReplacement() {

                        @Override
                        protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam p1) throws Throwable {
                            log(lpparam);
                            if (mode.equals("true"))
                                return true;
                            else
                                return false;
                        }
                    });
            }
            
            catch (Throwable t) {
                // We don't want to spam the logs.
                // If we don't do this, Xposed will spit out CNF errors left and
                // right because only a few apps use ActivityManagerCompat.isLowRamDevice()
                // which causes Proguard to trim it out.
                //
                // We will log if we our own app throws it, because the 
                // v4 support lib should be included. 
                if (lpparam.packageName.equals("com.devin.islowramdevice")) {
                    XposedBridge.log("**** ERROR: Xposed-LowRamDevice **** \n" +
                                        t.getStackTrace().toString() + "\n" +
                                        "(End Xposed-LowRamDevice stack trace.)");
                }
            }
        }
    }
    private void log(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log("Xposed-LowRamDevice: " + lpparam.packageName + " called an isLowRamDevice() variation!");
    }
    /**
     * Convienently get all the values we need for the Settings Advanced result dialog.
     * @result A list of the results.
     */
    protected static String isLowRamDevice(Context context, ClassLoader loader) {
        try {
            String str = "";
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return KitKatHelper.isLowRamDevice(context, loader);
            } else {
                return str + "ActivityManagerCompat.isLowRamDevice() = " + ActivityManagerCompat.isLowRamDevice(am) ? "true" : "false";
            }
        } catch (Throwable t) {
            XposedBridge.log("Xposed-LowRamDevice: " + t.getStackTrace().toString());
            return "Error. See the Xposed logs.";
        }
    }
}
