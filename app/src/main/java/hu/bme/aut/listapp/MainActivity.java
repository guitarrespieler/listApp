package hu.bme.aut.listapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.aut.listapp.list.ListActivity;
import hu.bme.aut.listapp.map.MapsActivity;
import hu.bme.aut.listapp.map.ServiceLocation;
import hu.bme.aut.listapp.statistics.StatisticsActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.shoppingListsBtn)
    Button shoppingListsBtn;
    @BindView(R.id.mapBtn)
    Button mapBtn;
    @BindView(R.id.statisticsBtn)
    Button statisticsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        if (isLocationServiceEnabled(sharedPreferences))
            SettingsActivity.startServiceWhenEnabled(sharedPreferences, getApplicationContext());
    }

    private boolean isLocationServiceEnabled(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(SettingsActivity.KEY_START_SERVICE, false);
    }

    @OnClick({R.id.shoppingListsBtn, R.id.mapBtn, R.id.statisticsBtn})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.shoppingListsBtn:
                intent = new Intent(this, ListActivity.class);
                break;
            case R.id.mapBtn:
                final SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());

                if (isLocationServiceEnabled(sharedPreferences))
                    intent = new Intent(this, MapsActivity.class);
                else {
                    final Snackbar snackbar = Snackbar.make
                            (view, getString(R.string.noLocationServiceMsg), Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.action_settings,
                            (View v) -> {
                                handleMenuButtonOnClick(R.id.action_settings);
                            });

                    snackbar.show();
                }
                break;
            case R.id.statisticsBtn:
                intent = new Intent(this, StatisticsActivity.class);
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuButtonOnClick(item.getItemId());
    }

    private boolean handleMenuButtonOnClick(int itemId) {
        switch (itemId) {
            case R.id.action_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                intentSettings.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT,
                        SettingsActivity.FragmentSettingsBasic.class.getName());
                intentSettings.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
                startActivity(intentSettings);
                break;
        }

        return true;
    }
}
