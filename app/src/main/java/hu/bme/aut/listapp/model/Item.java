package hu.bme.aut.listapp.model;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Model class that represents an item in the item list.
 * Created by Zsiga Tibor on 2018. 04. 01..
 */
@Table
public class Item extends SugarRecord implements Serializable{

    private Long id;

    private String name = "";

    private double quantity = 1;

    private double unitPrice = 0.0;

    private ItemList containingList = null;

    public Item(){}

    public Item(String name, double quantity, double unitPrice, ItemList containingList) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.containingList = containingList;
    }

    /**
     * Tells how many items are in the database with the same name
     * @return number of items with the same name
     */
    public long numberOfItemsWithTheSameName(){
        return Item.count(Item.class, "name = ?", new String[]{name});
    }

    public List<Item> findItemsWithTheSameName(){
        String whereClause = "name = ?";
        List<Item> items = Item.find(Item.class, whereClause, name);

        return items;
    }

    public double calculateEstimatedPrice(){
        return quantity * unitPrice;
    }

    //generated methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (Double.compare(item.quantity, quantity) != 0) return false;
        if (Double.compare(item.unitPrice, unitPrice) != 0) return false;
        if (id != null ? !id.equals(item.id) : item.id != null) return false;
        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        return containingList != null ? containingList.equals(item.containingList) : item.containingList == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(quantity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(unitPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (containingList != null ? containingList.hashCode() : 0);
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if(quantity > 0)
            this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public ItemList getContainingList() {
        return containingList;
    }

    public void setContainingList(ItemList containingList) {
        this.containingList = containingList;
    }
}