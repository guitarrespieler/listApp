package hu.bme.aut.listapp.statistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.bme.aut.listapp.R;

public class StatisticsActivity extends AppCompatActivity {

    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

        (new AsyncStatisticDataCollector()).execute();//gather information
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stat, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listPriceMenuBtn:
                break;
            case R.id.numberOfItemsMenuBtn:
                break;
            case R.id.ListSizeMenuBtn:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        StatisticsData.deleteDataFromMemory();
    }
}
