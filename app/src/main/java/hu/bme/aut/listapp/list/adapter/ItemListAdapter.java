package hu.bme.aut.listapp.list.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.bme.aut.listapp.list.ItemListActivity;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.model.Item;
import hu.bme.aut.listapp.model.ItemList;

/**
 * Created by Zsiga Tibor on 2018. 04. 04..
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private ItemListActivity activity;
    private List<Item> items;

    private ViewHolder viewHolder;

    public ItemListAdapter(ItemListActivity activity, List<Item> list) {
        this.activity = activity;
        this.items = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemrow, parent, false);

        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);

        holder.itemNameTV.setText(item.getName());
        holder.setQuantity(item.getQuantity());
        holder.setEstimatedPriceTV(item.calculateEstimatedPrice());

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final ItemList containingList = item.getContainingList();
                containingList.refreshLastModifiedDate();
                containingList.save();

                item.delete();

                activity.refresh();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;

        public final TextView itemNameTV;
        private final TextView itemQuantityTV;
        private final TextView estimatedPriceTV;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            itemNameTV = (TextView) view.findViewById(R.id.itemNameTV);
            itemQuantityTV = (TextView) view.findViewById(R.id.itemQuantityTV);
            estimatedPriceTV = (TextView) view.findViewById(R.id.listEstimatedPriceTV);
        }

        public void setQuantity(double quantity) {
            final String str = String.format("%.2f", quantity);
            String text = str + " " + activity.getString(R.string.pieces);
            itemQuantityTV.setText(text);
        }

        public void setEstimatedPriceTV(double price){
            final String str = String.format("%.2f", price);
            String text = str + " " + activity.getString(R.string.currency);

            estimatedPriceTV.setText(text);
        }
    }

}
