package com.cannontech.common.bulk.collection.inventory;

import java.util.Iterator;
import java.util.List;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.google.common.collect.Lists;

public abstract class ListBasedInventoryCollection implements YukonCollection {

    @Override
    public Iterator<InventoryIdentifier> iterator() {
        return getList().iterator();
    }

    public List<InventoryIdentifier> getSubList(int start, int size) {

        List<InventoryIdentifier> list = this.getList();

        int end = start + size;
        List<InventoryIdentifier> subList = list.subList(start, Math.min(end, list.size()));

        return Lists.newArrayList(subList);
    }
    
    @Override
    public long getCount() {
        return getList().size();
    }
    
}