package hu.bme.aut.listapp.list.async;

import android.os.AsyncTask;

import java.util.List;

import hu.bme.aut.listapp.list.ItemListActivity;
import hu.bme.aut.listapp.list.adapter.ItemListAdapter;
import hu.bme.aut.listapp.model.Item;
import hu.bme.aut.listapp.model.ItemList;

/**
 * Created by Zsiga Tibor on 2018. 04. 22..
 */

public class AsyncItemListLoader extends AsyncTask<Long, Void, Void> {

    private ItemListActivity activity;

    private ItemListAdapter adapter;

    public AsyncItemListLoader(ItemListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        if (longs.length != 1)
            return null;

        long itemlistID = longs[0];

        ItemList itemList = ItemList.findById(ItemList.class, itemlistID);
        activity.setItemListObject(itemList);

        try {
            itemList.refreshLastModifiedDate();
            itemList.save();

            List<Item> items = activity.getItems();
            items.clear();
            items.addAll(itemList.findAllContainedItem());

            adapter = new ItemListAdapter(activity, items);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activity.setAdapter(adapter);
        activity.updateTitle();
    }
}
