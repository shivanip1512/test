package com.cannontech.amr.meter.search.model;

import java.util.ArrayList;
import java.util.List;

public class StandardFilterByGenerator {

    public static List<StandardFilterBy> getStandardFilterByList() {

        List<StandardFilterBy> filterByList = new ArrayList<StandardFilterBy>();

        filterByList.add(new StandardFilterBy("Quick Search", MeterSearchField.METERNUMBER, MeterSearchField.PAONAME));

        filterByList.add(new StandardFilterBy("Meter Number", MeterSearchField.METERNUMBER));
        filterByList.add(new StandardFilterBy("Device Name", MeterSearchField.PAONAME));
        filterByList.add(new StandardFilterBy("Device Type", MeterSearchField.TYPE));
        filterByList.add(new StandardFilterBy("Address", MeterSearchField.ADDRESS));
        filterByList.add(new StandardFilterBy("Route", MeterSearchField.ROUTE));

        return filterByList;
    }
}
