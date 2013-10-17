package com.cannontech.common.util;

import java.util.List;

import com.google.common.collect.ForwardingList;

/**
 * The SubList class simply extends List with parameters
 * to keep track of the original list size and start index.
 * 
 * Example use:
 * 
 * List newList = originalList.subList(fromIndex, toIndex);
 * SubList subList = new SubList(newList, fromIndex, originialList.size());
 * 
 * @deprecated Use {@link #pageBasedForSublist(List, int, int, int)}
 */
@Deprecated
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
