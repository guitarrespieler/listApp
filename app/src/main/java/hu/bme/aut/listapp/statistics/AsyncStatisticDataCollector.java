package hu.bme.aut.listapp.statistics;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.model.Item;
import hu.bme.aut.listapp.list.model.ItemList;

class AsyncStatisticDataCollector extends AsyncTask<Void, Void, Void> {

    private StatisticsActivity activity;

    private LinkedList<Integer> chartColors;

    public AsyncStatisticDataCollector(StatisticsActivity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        activity.progressbar.setVisibility(View.VISIBLE); //show
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StatisticsData stat = StatisticsData.getInstance();

        stat.gatherInformation();

        chartColors = createColors();

        final CountDownLatch allThreadsDone = new CountDownLatch(4);

        //wait ~1-1.5 sec so the user can see the progress bar
        new Thread(() -> {try {Thread.sleep(new Random().nextInt(500) + 1000);allThreadsDone.countDown();} catch (Exception e) {}}).start();

        new Thread(() -> {createListPricesPieChartData(stat);allThreadsDone.countDown();}).start();

        new Thread(() -> {createNumberOfItemsPieChartData(stat);allThreadsDone.countDown();}).start();

        new Thread(() -> {createListSizePieChartData(stat);allThreadsDone.countDown();}).start();


        try {
            allThreadsDone.await(); //wait until data collection finishes and ~1 sec passes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createListPricesPieChartData(StatisticsData stat) {
        Map<ItemList, Double> map = stat.getItemListsPricesMap();

        LinkedList<PieEntry> entries = new LinkedList<>();

        for (ItemList itemList: map.keySet()){
            entries.add(new PieEntry(map.get(itemList).floatValue(), itemList.getName()));
        }

        activity.listPrices = createPieData(entries, activity.getString(R.string.priceOfLists));
    }

    private void createNumberOfItemsPieChartData(StatisticsData stat) {
        Map<String, Double> map = stat.getItemCountMap();

        LinkedList<PieEntry> entries = new LinkedList<>();

        for(String name : map.keySet()){
            entries.add(new PieEntry(map.get(name).floatValue(), name));
        }

        activity.numberOfItems = createPieData(entries, activity.getString(R.string.numberOfItems));
    }


    private void createListSizePieChartData(StatisticsData stat) {
        Map<ItemList, List<Item>> map = stat.getItemlistsMap();

        LinkedList<PieEntry> entries = new LinkedList<>();

        for(ItemList itemlist : map.keySet()){
            entries.add(new PieEntry(map.get(itemlist).size(), itemlist.getName()));
        }

        activity.ListsSize = createPieData(entries, activity.getString(R.string.SizeOfLists));
    }

    private PieData createPieData(LinkedList<PieEntry> entries, String dataSetLabel) {
        PieDataSet dataSet = new PieDataSet(entries, dataSetLabel);
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setColors(chartColors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.BLACK);

        return pieData;
    }



    private LinkedList<Integer> createColors(){
        // add a lot of colors
        LinkedList<Integer> colors = new LinkedList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        return colors;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activity.progressbar.setVisibility(View.GONE); // hide

        activity.handleMenuClick(R.id.listPriceMenuBtn);//draw chart with the collected data
    }
}