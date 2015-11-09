package com.devin.islowramdevice;

import android.app.*;
import android.preference.*;
import android.os.*;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.Context;
import android.support.v4.app.ActivityManagerCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.graphics.drawable.Drawable;
import android.content.res.TypedArray;
import android.widget.TextView;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.FrameLayout;

import java.io.File;
import java.io.Scanner;

public class MainActivity extends PreferenceActivity {
    
    @SuppressLint("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        getPreferenceManager().setDefaultValues(this, R.xml.prefs, false);
		
		
        Preference pref_info = findPreference("info_is_low_ram");
        pref_info.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.pref_info_is_low_ram)
                        .setMessage(isExpertMode() ? R.string.pref_info_is_low_ram_msg_expert :R.string.pref_info_is_low_ram_msg)
                        .setPositiveButton(android.R.string.ok, null);
                    if (isExpertMode())
                        // Link to javadoc.
						builder.setNeutralButton(R.string.pref_info_is_low_ram_javadoc, new DialogInterface.OnClickListener()  {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                i.setAction(Intent.ACTION_VIEW);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.setData(Uri.parse(getString(R.string.javadoc_url)));
                                startActivity(i);
                            }
                        });
                        // Show the icon on KK and below.
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                            Drawable drawable = DrawableCompat.wrap(getResources().getDrawable(android.R.drawable.ic_dialog_info));
                            // Tint the icon on the light dialogs of Holo Light so they don't look awkward.
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                int iconColor = 0x222222;
                                TypedArray themeArray = getTheme().obtainStyledAttributes(new int[] {android.R.attr.textColorSecondaryNoDisable});
								 try {
									int index = 0;
									int defaultColorValue = 0;
									iconColor = themeArray.getColor(index, defaultColorValue);
								}
								finally { 
								themeArray.recycle();
							}
                            DrawableCompat.setTint(drawable, iconColor);
                            }
                        builder.setIcon(drawable);
                        }
                    builder.show();
                    return true;
                }
        });
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Preference pref_status = new Preference(this);
        pref_status.setKey("isLowRamDeviceResult");
        pref_status.setTitle(R.string.pref_current_result);
        pref_status.setSummary(ActivityManagerCompat.isLowRamDevice(am) ? getString(R.string.pref_this_is_low_ram) : getString(R.string.pref_not_is_low_ram));
        if (isExpertMode())
            pref_status.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference p1) {
                    String msg = XIsLowRamDevice.isLowRamDevice(MainActivity.this, MainActivity.this.getClassLoader());
                    TextView tv = new TextView(MainActivity.this);
                    tv.setHorizontallyScrolling(true);
                    
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.pref_current_result) + ":")
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                        
                    return true;
                }
        });
        getPreferenceScreen().addPreference(pref_status);
        final ListPreference pref_is_low_ram = (ListPreference) findPreference("is_low_ram");
        pref_is_low_ram.setOnPreferenceChangeListener(new ListPreference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                pref_is_low_ram.setSummary(pref_is_low_ram.getEntries()[pref_is_low_ram.findIndexOfValue((String) newValue)]);
                return true;
            }
        });
    }

    private boolean isExpertMode() {
        return getPreferenceManager().getDefaultSharedPreferences(this).getBoolean("expert_mode", false);
    }
    /**
     * Checking whether the module is enabled.
     */
    private boolean isModuleEnabled() {
        return false;
    }
}
