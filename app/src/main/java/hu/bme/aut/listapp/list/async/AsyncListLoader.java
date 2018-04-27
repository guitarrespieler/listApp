package hu.bme.aut.listapp.list.async;

import android.os.AsyncTask;

import java.util.List;

import hu.bme.aut.listapp.list.ListActivity;
import hu.bme.aut.listapp.list.adapter.ListAdapter;
import hu.bme.aut.listapp.model.ItemList;

/**
 * The class is responsible for loading the list from the db
 * asynchronously, and refresh the activity when it is done.
 * Created by Zsiga Tibor on 2018. 04. 22..
 */

public class AsyncListLoader extends AsyncTask<List<ItemList>, Void, List<ItemList>> {

    private ListActivity activity;

    private ListAdapter adapter;

    public AsyncListLoader(ListActivity activity){
        this.activity = activity;
    }

    @Override
    protected List<ItemList> doInBackground(List<ItemList>[] lists) {
        if(lists.length != 1)
            return null;

        List<ItemList> itemLists = lists[0];

        if(itemLists == null)
            return null;

        refreshList(itemLists);//use the reference, don't create new object!

        adapter = new ListAdapter(activity, itemLists);//same here

        return itemLists;
    }

    @Override
    protected void onPostExecute(List<ItemList> itemLists) {
        if(itemLists == null)
            return;

        activity.setAdapter(adapter);
    }

    private void refreshList(List<ItemList> lists) {
        String orderby = "last_modified_date desc";

        lists.clear();
        lists.addAll(ItemList.listAll(ItemList.class, orderby));
    }
}
