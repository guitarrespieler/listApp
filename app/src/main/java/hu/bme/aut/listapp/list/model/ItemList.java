package hu.bme.aut.listapp.list.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zsiga Tibor on 2018. 04. 01..
 */

@Table
public class ItemList extends SugarRecord implements Serializable{

    private Long id;

    private String name = "";

    private Date lastModifiedDate = Calendar.getInstance().getTime();

    public ItemList(){}

    public ItemList(String name){
        this.name = name;
    }

    public List<Item> findAllContainedItem(){
        String whereClause = "containing_list = ?";

        List<Item> items = new LinkedList<>();
        try {
            String id = getId().toString();
            items = Item.find(Item.class, whereClause, id);
        }catch (Exception e){}

        return items;
    }

    public void refreshLastModifiedDate(){
        lastModifiedDate = Calendar.getInstance().getTime();
    }

    public double calculateTheWholeListsPrice(){
        List<Item> items = findAllContainedItem();

        double sum = 0;

        if(items != null || !items.isEmpty()) {
            for (Item item : items) {
                sum += item.calculateEstimatedPrice();
            }
        }

        return sum;
    }

    public void deleteItem(Item item){
        item.delete();
    }

    public void deleteAllItemFromThisList(){
        String whereClause = "containing_list = ?";

        List<Item> items = null;

        try {
            items = Item.find(Item.class, whereClause, this.id.toString());
        }catch (Exception e){
            return;
        }
        for (Item item: items) {
            item.delete();
            item.save();
        }
    }

    //generated methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemList itemList = (ItemList) o;

        if (id != null ? !id.equals(itemList.id) : itemList.id != null) return false;
        if (name != null ? !name.equals(itemList.name) : itemList.name != null) return false;
        return lastModifiedDate != null ? lastModifiedDate.equals(itemList.lastModifiedDate) : itemList.lastModifiedDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lastModifiedDate != null ? lastModifiedDate.hashCode() : 0);
        return result;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
