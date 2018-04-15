package hu.bme.aut.listapp.list;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.adapter.ListAdapter;
import hu.bme.aut.listapp.list.model.ItemList;

public class ListActivity extends AppCompatActivity implements AddNewListFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mainList)
    RecyclerView recyclerView;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ListAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ItemList> itemLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList();
    }

    public void refreshList() {
        String orderby = "last_modified_date desc";
        itemLists = ItemList.listAll(ItemList.class, orderby);
        adapter = new ListAdapter(this, itemLists);

        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddNewListFragment fragment = new AddNewListFragment();
                fragment.show(fragmentManager, AddNewListFragment.TAG);

                break;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void listModified() {
        refreshList();
    }
}
