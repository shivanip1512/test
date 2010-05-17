package com.cannontech.common.pao;

import java.util.Comparator;

import com.cannontech.dr.model.ControllablePao;
import com.cannontech.util.NaturalOrderComparator;


public class ControllablePaoComparator implements Comparator<ControllablePao> {
    private final static NaturalOrderComparator comparator =
        new NaturalOrderComparator();

    public ControllablePaoComparator() {
    }

    @Override
    public int compare(ControllablePao pao1, ControllablePao pao2) {
        int retVal = comparator.compare(pao1.getName(), pao2.getName());
        return retVal;
    }
}
