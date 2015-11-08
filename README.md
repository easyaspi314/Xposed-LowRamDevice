# Xposed-LowRamDevice
Is your device a low RAM device? You decide! Requires Xposed.

[Download on Xposed](https://repo.xposed.info/module/com.devin.islowramdevice)

###How it works:
This all revolves around KitKat's new feature,
[`ActivityManager.isLowRamDevice();`](http://developer.android.com/reference/android/app/ActivityManager.html#isLowRamDevice())
and its Support Library wrapper,
[`ActivityManagerCompat.isLowRamDevice(ActivityManager am)`](http://developer.android.com/reference/android/support/v4/app/ActivityManagerCompat.html#isLowRamDevice(android.app.ActivityManager))

So in 4.4+, the code looks like this:

android.app.ActivityManager
[(source)](https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/app/ActivityManager.java#L436-L450)

```java
    /**
     * Returns true if this is a low-RAM device.  Exactly whether a device is low-RAM
     * is ultimately up to the device configuration, but currently it generally means
     * something in the class of a 512MB device with about a 800x480 or less screen.
     * This is mostly intended to be used by apps to determine whether they should turn
     * off certain features that require more RAM.
     */
    public boolean isLowRamDevice() {
        return isLowRamDeviceStatic();
    }

    /** @hide */
    public static boolean isLowRamDeviceStatic() {
        return "true".equals(SystemProperties.get("ro.config.low_ram", "false"));
    }
```

And the Support Library wrapper looks like this: 

android.support.v4.app.ActivityManagerCompat 
[(source)](https://github.com/android/platform_frameworks_support/blob/master/v4/java/android/support/v4/app/ActivityManagerCompat.java#L31-L43)

```java
    /**
     * Returns true if this is a low-RAM device.  Exactly whether a device is low-RAM
     * is ultimately up to the device configuration, but currently it generally means
     * something in the class of a 512MB device with about a 800x480 or less screen.
     * This is mostly intended to be used by apps to determine whether they should turn
     * off certain features that require more RAM.
     */
    public static boolean isLowRamDevice(@NonNull ActivityManager am) {
        if (Build.VERSION.SDK_INT >= 19) {
            return ActivityManagerCompatKitKat.isLowRamDevice(am);
        }
        return false;
    }
```

android.support.v4.app.ActivityManagerCompatKitKat 
[(source)](https://github.com/android/platform_frameworks_support/blob/master/v4/kitkat/android/support/v4/app/ActivityManagerCompatKitKat.java#L22-L24)
```java
    public static boolean isLowRamDevice(ActivityManager am) {
        return am.isLowRamDevice();
    }
```

-

Notice how any device below KitKat **always returns false**. Obviously, it makes sense, because wouldn't it slow down
the device if you are constantly checking the screen size and RAM? And on KitKat+, it checks `build.prop` for
`ro.config.low_ram`. While you can always change the build.prop since you're likely already rooted, it is a pain to
do so.

This changes that!!

Now, you can toggle that to be true, false, or just use the default implementation. Just go to the Xposed Modules tab
and click on isLowRamDevice to open the settings. No `build.prop` editing, and it works on every version of Android
(but many apps don't actually call that on older devices and just use it to prevent that nasty `VerifyError`, because
it will always return false). However, it is very useful on KitKat and above. All changes *should be* made live once a
package is (re)started, but you are always better off rebooting, just like all Xposed modules.

####Warning!
Obviously, just like other Xposed modules and device modifications, use this at your own risk. I'm not going to go on
about not being responsible for bricked devices, thermonuclear war, or failed alarm clock apps, but you get the gist.
I don't think there will be any negative side effects to this, however, the worst I see could potentially happen is a
system app for an OEM ROM going beserk because it expects isLowRamDevice() to return true or false, but worst case
scenario, you can disable it.

####TODO:
* Per-app toggling
* Failproofing. Would be hard because we are already catching all Throwables without logging. We really don't want to 
throw each time because... it would throw a stack in the logs on every app we start sans ones that actually implement
it in the Support Library and it would spam so much it would be impossible to find a useful log.
