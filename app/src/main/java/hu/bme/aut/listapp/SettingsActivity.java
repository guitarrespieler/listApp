package hu.bme.aut.listapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import hu.bme.aut.listapp.map.ServiceLocation;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_WITH_FLOATING = "with_floating";

    public static final String EXTRA_NO_HEADERS = ":android:no_headers";
    public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";

    public static final String KEY_START_SERVICE = "start_service";
    public static final String KEY_IS_OPEN = "is_open";
    public static final String KEY_PLACE_TYPE = "place_type";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_LANGUAGE = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(
                this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(
                this).unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_START_SERVICE.equals(key)) {
            startServiceWhenEnabled(sharedPreferences, getApplicationContext());
        }
        if (KEY_LANGUAGE.equals(key))
            Toast.makeText(this, "További nyelvek később...", Toast.LENGTH_LONG).show();
    }

    static void startServiceWhenEnabled(SharedPreferences sharedPreferences, Context ctx) {
        boolean startService = sharedPreferences.getBoolean(KEY_START_SERVICE, false);

        Intent i = new Intent(ctx, ServiceLocation.class);

        if (startService) {
            ctx.startService(i);
        } else {
            ctx.stopService(i);
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.fragmentsettings, target);
    }

    public static class FragmentSettingsBasic extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mainsettings);
        }
    }


}