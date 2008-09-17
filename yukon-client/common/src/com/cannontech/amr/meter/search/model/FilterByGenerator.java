package com.cannontech.amr.meter.search.model;

import java.util.ArrayList;
import java.util.List;

public class FilterByGenerator {

    public static List<FilterBy> getFilterByList() {

        List<FilterBy> filterByList = new ArrayList<FilterBy>();

        filterByList.add(new FilterBy("Quick Search", MeterSearchField.METERNUMBER, MeterSearchField.PAONAME));

        filterByList.add(new FilterBy("Meter Number", MeterSearchField.METERNUMBER));
        filterByList.add(new FilterBy("Device Name", MeterSearchField.PAONAME));
        filterByList.add(new FilterBy("Device Type", MeterSearchField.TYPE));
        filterByList.add(new FilterBy("Address", MeterSearchField.ADDRESS));
        filterByList.add(new FilterBy("Route", MeterSearchField.ROUTE));

        return filterByList;
    }
}
