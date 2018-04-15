package hu.bme.aut.listapp.statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.bme.aut.listapp.list.model.Item;
import hu.bme.aut.listapp.list.model.ItemList;

/**
 * Created by Zsiga Tibor on 2018. 04. 05..
 */

public class StatisticsData {

    /**
     * Only one instance is accessible - it is enough.
     */
    private static StatisticsData instance;

    /**
     * tells which itemlist contains which items
     */
    private Map<ItemList, List<Item>> itemlistsMap;

    /**
     * Tells the number of the items
     */
    private Map<String, Double> itemCountMap;

    private List<Item> items;

    private int numberOfLists;

    private int numberOfItems;

    private Map<ItemList, Double> itemListsPricesMap;


    private StatisticsData(){
        itemlistsMap = new HashMap<>();
        itemCountMap = new HashMap<>();
        itemListsPricesMap = new HashMap<>();

        items = new LinkedList<>();

        numberOfLists = 0;
        numberOfItems = 0;
    }

    public static StatisticsData getInstance(){
        if(instance == null)
            instance = new StatisticsData();

        return instance;
    }

    public static void deleteDataFromMemory(){
        instance = null;
    }

    public void gatherInformation(){

        //find itemlists
        List<ItemList> lists = ItemList.listAll(ItemList.class);

        for (ItemList itemListObject : lists) {
            itemlistsMap.put(itemListObject, itemListObject.findAllContainedItem());

            itemListsPricesMap.put(itemListObject, itemListObject.calculateTheWholeListsPrice());
        }

        //get items
        items = Item.listAll(Item.class);

        //get item names
        for(Item item : items){
            if(itemCountMap.containsKey(item.getName()))
                continue;

            long count = item.numberOfItemsWithTheSameName();

            if(count > 1){
                List<Item> itemsWithSameName = item.findItemsWithTheSameName();

                double quantity = 0;
                for(Item temp : itemsWithSameName){
                    quantity += temp.getQuantity();
                }

                itemCountMap.put(item.getName(), quantity);
            }
        }

        numberOfLists = itemlistsMap.size();
        numberOfItems = items.size();
    }




}
