package com.cannontech.dr.model;

import java.text.Collator;
import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.user.YukonUserContext;


public class DisplayablePaoComparator implements Comparator<DisplayablePao> {
    private boolean isDescending;
    private Collator collator;

    public DisplayablePaoComparator(YukonUserContext userContext,
            boolean isDescending) {
        this.isDescending = isDescending;
        collator = Collator.getInstance(userContext.getLocale());
    }

    @Override
    public int compare(DisplayablePao pao1, DisplayablePao pao2) {
        int retVal = collator.compare(pao1.getName(), pao2.getName());
        return isDescending ? (0 - retVal) : retVal;
    }
}
