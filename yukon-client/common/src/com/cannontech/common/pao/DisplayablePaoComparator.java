package com.cannontech.common.pao;

import java.util.Comparator;

import com.cannontech.util.NaturalOrderComparator;


public class DisplayablePaoComparator implements Comparator<DisplayablePao> {
    private final static NaturalOrderComparator comparator =
        new NaturalOrderComparator();

    public DisplayablePaoComparator() {
    }

    @Override
    public int compare(DisplayablePao pao1, DisplayablePao pao2) {
        int retVal = comparator.compare(pao1.getName(), pao2.getName());
        return retVal;
    }
}
