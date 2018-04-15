package hu.bme.aut.listapp.statistics;

import android.os.AsyncTask;
import android.view.View;

class AsyncStatisticDataCollector extends AsyncTask<Void, Void, Void> {

    StatisticsActivity activity;

    @Override
    protected void onPreExecute() {
        activity.progressbar.setVisibility(View.VISIBLE); //show
    }

    @Override
    protected Void doInBackground(Void... voids) {
        StatisticsData data = StatisticsData.getInstance();

        data.gatherInformation();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activity.progressbar.setVisibility(View.GONE); // hide
    }
}