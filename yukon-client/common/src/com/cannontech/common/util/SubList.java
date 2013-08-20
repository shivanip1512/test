package com.cannontech.common.util;

import java.util.List;

import com.google.common.collect.ForwardingList;

/**
 * The SubList class simply extends List with an extra parameter
 * to keep track of the original list size.
 * 
 * Example use:
 * 
 * List list = originalList.subList(fromIndex, toIndex);
 * int size = originialList.size();
 * 
 * SubList subList = new SubList(list, size);
 */
public class SubList<T> extends ForwardingList<T> {

    private int startIndex;
    private int originialSize;
    private List<T> subList;

    public SubList(List<T> subList, int startIndex, int originialSize) {
        this.originialSize = originialSize;
        this.startIndex = startIndex;
        this.subList = subList;
    }

    /**
     * @return size of the original list which this sublist is from.
     */
    public int getOriginalSize() {
        return originialSize;
    }

    @Override
    protected List<T> delegate() {
        return subList;
    }

    public int getStartIndex() {
        return startIndex;
    }
}
