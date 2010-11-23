package com.cannontech.common.bulk.collection.inventory;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.inventory.YukonInventory;
import com.google.common.collect.Lists;

public abstract class ListBasedInventoryCollection implements InventoryCollection {

    @Override
    public Iterator<YukonInventory> iterator() {
        return (Iterator<YukonInventory>) getInventoryList().iterator();
    }

    public List<YukonInventory> getInventory(int start, int size) {

        List<? extends YukonInventory> list = this.getInventoryList();

        int end = start + size;
        List<? extends YukonInventory> subList = list.subList(start, Math.min(end, list.size()));

        return Lists.newArrayList(subList);
    }
    
    @Override
    public long getInventoryCount() {
        return getInventoryList().size();
    }
    
}