package hu.bme.aut.listapp.statistics;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import hu.bme.aut.listapp.model.Item;
import hu.bme.aut.listapp.model.ItemList;

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

    private double priceOfAllLists = 0.0;

    private Map<ItemList, Double> itemListsPricesMap;

    private StatisticsData(){
        itemlistsMap = new HashMap<>();
        itemCountMap = new HashMap<>();
        itemListsPricesMap = new HashMap<>();

        items = new LinkedList<>();

        numberOfLists = 0;
        numberOfItems = 0;
    }

    public static synchronized StatisticsData getInstance(){
        if(instance == null)
            instance = new StatisticsData();

        return instance;
    }

    public static synchronized void deleteDataFromMemory(){
        instance = null;
    }

    public synchronized void gatherInformation(){
        final CountDownLatch allThreadsDone = new CountDownLatch(2);

        //find itemlists
        new Thread(() -> {collectItemlistPricesData();allThreadsDone.countDown();}).start();

        //get items
        new Thread(() -> {collectItemData();allThreadsDone.countDown();}).start();

        try {allThreadsDone.await();} catch (Exception e) {}

        numberOfLists = itemlistsMap.size();
        numberOfItems = items.size();
    }

    private void collectItemData() {
        items = Item.listAll(Item.class);

        //get item names
        for(Item item : items){
            if(itemCountMap.containsKey(item.getName()))
                continue;

            double quantity = item.getQuantity();

            long count = item.numberOfItemsWithTheSameName();
            if(count > 1){
                List<Item> itemsWithSameName = item.findItemsWithTheSameName();

                for(Item temp : itemsWithSameName){
                    if(item != temp)
                        quantity += temp.getQuantity();
                }
            }
            itemCountMap.put(item.getName(), quantity);
        }
    }

    private void collectItemlistPricesData() {
        List<ItemList> lists = ItemList.listAll(ItemList.class);

        for (ItemList itemListObject : lists) {
            itemlistsMap.put(itemListObject, itemListObject.findAllContainedItem());

            final double listPrice = itemListObject.calculateTheWholeListsPrice();

            priceOfAllLists += listPrice;

            itemListsPricesMap.put(itemListObject, listPrice);
        }
    }

    public Map<ItemList, List<Item>> getItemlistsMap() {
        return Collections.unmodifiableMap(itemlistsMap);
    }

    public Map<String, Double> getItemCountMap() {
        return Collections.unmodifiableMap(itemCountMap);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getNumberOfLists() {
        return numberOfLists;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public double getPriceOfAllLists() {
        return priceOfAllLists;
    }

    public Map<ItemList, Double> getItemListsPricesMap() {
        return Collections.unmodifiableMap(itemListsPricesMap);
    }
}
