package com.cannontech.amr.csr.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterByGenerator {

    public static List<FilterBy> getFilterByList() {

        List<FilterBy> filterByList = new ArrayList<FilterBy>();

        ArrayList<CsrSearchField> fieldList = new ArrayList<CsrSearchField>();
        fieldList.add(CsrSearchField.METERNUMBER);
        fieldList.add(CsrSearchField.PAONAME);
        filterByList.add(new FilterBy("Quick Search", fieldList));

        filterByList.add(new FilterBy("Meter Number", CsrSearchField.METERNUMBER));
        filterByList.add(new FilterBy("Device Name", CsrSearchField.PAONAME));
        filterByList.add(new FilterBy("Device Type", CsrSearchField.TYPE));
        filterByList.add(new FilterBy("Address", CsrSearchField.ADDRESS));
        filterByList.add(new FilterBy("Route", CsrSearchField.ROUTE));

        return filterByList;
    }
}
