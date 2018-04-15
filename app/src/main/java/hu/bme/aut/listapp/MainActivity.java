package hu.bme.aut.listapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.aut.listapp.list.ListActivity;
import hu.bme.aut.listapp.statistics.PieChartFragment;

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

    @OnClick({R.id.shoppingListsBtn, R.id.mapBtn, R.id.statisticsBtn})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.shoppingListsBtn:
                intent = new Intent(this, ListActivity.class);
                break;
            case R.id.mapBtn:
                // Search for restaurants nearby
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=nearby grocery stores");
                intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                break;
            case R.id.statisticsBtn:
                intent = new Intent(this, PieChartFragment.class);
                break;
        }
        if(intent != null)
            startActivity(intent);
    }
}