package hu.bme.aut.listapp.list.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.bme.aut.listapp.list.ItemListActivity;
import hu.bme.aut.listapp.list.ListActivity;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.model.ItemList;

/**
 * Created by Zsiga Tibor on 2018. 04. 04..
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private ListActivity activity;
    private List<ItemList> list;

    private ViewHolder viewHolder;

    public ListAdapter(ListActivity activity, List<ItemList> list){
        this.activity = activity;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listrow, parent, false);

        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemList itemlist = list.get(position);

        holder.listNameTV.setText("" + itemlist.getName());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ItemListActivity.class);
                intent.putExtra(context.getString(R.string.intentParamStr), itemlist.getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View view;

        public final TextView listNameTV;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            listNameTV = (TextView)view.findViewById(R.id.listRowTV);
        }
    }
}
