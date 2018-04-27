package hu.bme.aut.listapp.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.bme.aut.listapp.R;

public class StatisticsActivity extends AppCompatActivity {


    @BindView(R.id.statisticsToolbar)
    Toolbar toolbar;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.piechart)
    PieChart piechart;

    PieData listPrices;
    PieData numberOfItems;
    PieData ListsSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        
        initPieChart();
    }

    private void initPieChart() {
        piechart.setUsePercentValues(false);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);

        piechart.setDragDecelerationFrictionCoef(0.95f);

        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);

        piechart.setTransparentCircleColor(Color.WHITE);
        piechart.setTransparentCircleAlpha(110);

        piechart.setHoleRadius(58f);
        piechart.setTransparentCircleRadius(61f);


        piechart.setRotationAngle(0);
        // enable rotation of the chart by touch
        piechart.setRotationEnabled(true);
        piechart.setHighlightPerTapEnabled(true);

        Legend l = piechart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        piechart.setEntryLabelColor(Color.BLACK);
        piechart.setEntryLabelTextSize(12f);
        piechart.setNoDataTextColor(Color.BLACK);
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new AsyncStatisticDataCollector(this)).execute();//gather information
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stat, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        handleMenuClick(item.getItemId());

        return true;
    }

    public void handleMenuClick(int itemId){
        switch (itemId) {
            case R.id.listPriceMenuBtn:
                piechart.setData(listPrices);
                break;
            case R.id.numberOfItemsMenuBtn:
                piechart.setData(numberOfItems);
                break;
            case R.id.ListSizeMenuBtn:
                piechart.setData(ListsSize);
                break;
        }
        piechart.highlightValues(null);
        piechart.invalidate();

        piechart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        StatisticsData.deleteDataFromMemory();
    }
}
