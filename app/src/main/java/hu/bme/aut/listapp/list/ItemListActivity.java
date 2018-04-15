package hu.bme.aut.listapp.list;

import android.content.DialogInterface;
import android.net.Uri;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.AddNewItemFragment;
import hu.bme.aut.listapp.list.adapter.ItemListAdapter;
import hu.bme.aut.listapp.list.model.Item;
import hu.bme.aut.listapp.list.model.ItemList;

public class ItemListActivity extends AppCompatActivity implements AddNewItemFragment.OnFragmentInteractionListener, ModifyListFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.itemListRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ItemList itemList;
    private List<Item> items;

    private ItemListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Long itemListId = (long) getIntent().getSerializableExtra(getString(R.string.intentParamStr));
        itemList = ItemList.findById(ItemList.class, itemListId);



        toolbar.setTitle(itemList.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList() {
        try {
            itemList.refreshLastModifiedDate();
            itemList.save();

            items = itemList.findAllContainedItem();

            adapter = new ItemListAdapter(this, items);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                ModifyListFragment fragment = new ModifyListFragment();
                fragment.show(fragmentManager, ModifyListFragment.TAG);
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

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void listModified() {
        refreshList();
        toolbar.setTitle(itemList.getName());
    }
}
