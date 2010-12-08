package com.cannontech.common.bulk.collection.inventory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;

public interface YukonCollection<T> extends Iterable<T> {

    /**
     * Method to get the complete list of inventory in this collection - for large
     * collections this method may take a while to return the list
     * @return List of inventory indentifiers in this collection
     */
    public List<T> getList();
    
    /**
     * Method to get just the count of how many inventory are in the collection. Should be faster
     * than creating actual inventory and returning them if all you want is the count.
     * @return Count of how many inventory are in the collection
     */
    public int getCount();

    /**
     * Method to get an iterator for the inventory in this collection.
     * 
     * @return An iterator for the inventory in this collection
     */
    public Iterator<T> iterator();
    
    /**
     * Method to get a list of inventory from this collection
     * @param start - Index of first inventory to get
     * @param size - Number of inventory to get including the start index
     * @return A list of inventory from this collection
     */
    public List<T> getSubList(int start, int size);

    /**
     * Method used to get the current map of parameters for this inventory
     * collection
     * @return Map of parameters
     */
    public Map<String, String> getCollectionParameters();

    /**
     * Method to get the description I18N key of the collection of inventory
     * @return String description key
     */
    public MessageSourceResolvable getDescription();
    
}