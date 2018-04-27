package hu.bme.aut.listapp.list;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.adapter.ItemListAdapter;
import hu.bme.aut.listapp.list.async.AsyncItemListLoader;
import hu.bme.aut.listapp.model.Item;
import hu.bme.aut.listapp.model.ItemList;

public class ItemListActivity extends AppCompatActivity implements AddNewItemFragment.OnFragmentInteractionListener, ModifyListNameFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.itemListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ItemList itemList;
    private List<Item> items = new LinkedList<>();

    private ItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    public void setAdapter(ItemListAdapter itemListAdapter){
        adapter = itemListAdapter;

        recyclerView.setAdapter(adapter);
    }

    public void updateTitle(){
        toolbar.setTitle(itemList.getName());
    }

    @OnClick(R.id.fab)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddNewItemFragment fragment = new AddNewItemFragment();
                fragment.show(fragmentManager, AddNewItemFragment.TAG);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteListBtn:
                createAlertDialog().show();
                break;
            case R.id.renameListBtn:
                FragmentManager fragmentManager = getSupportFragmentManager();
                ModifyListNameFragment fragment = new ModifyListNameFragment();
                fragment.show(fragmentManager, ModifyListNameFragment.TAG);
                break;
            case R.id.calculatePrice:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        double sum = itemList.calculateTheWholeListsPrice();

                        StringBuilder sb = new StringBuilder();

                        sb.append(getString(R.string.sumOfPrices))
                                .append(" ")
                                .append(sum)
                                .append(" ")
                                .append(getString(R.string.currency));

                        String str = sb.toString();

                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show());

                    }
                }).start();

                break;
        }

        return true;
    }

    private AlertDialog createAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.confirmDeleteMessage))
                .setTitle(getString(R.string.deleteListTitle))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        itemList.deleteAllItemFromThisList();
                        itemList.delete();

                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }


    public ItemList getItemListObject() {
        return itemList;
    }

    public List<Item> getItems(){return items;}

    public void setItemListObject(ItemList itemlist){ this.itemList = itemlist;}

    @Override
    public void refresh() {
        Long itemListId = (long) getIntent().getSerializableExtra(getString(R.string.intentParamStr));

        (new AsyncItemListLoader(this)).execute(itemListId);
    }

}
