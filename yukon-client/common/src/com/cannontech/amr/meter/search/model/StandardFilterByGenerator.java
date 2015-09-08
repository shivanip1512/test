package com.cannontech.amr.meter.search.model;

import java.util.ArrayList;
import java.util.List;

public class StandardFilterByGenerator {

    public static List<StandardFilterBy> getStandardFilterByList() {

        List<StandardFilterBy> filterByList = new ArrayList<StandardFilterBy>();

        filterByList.add(new StandardFilterBy("quickSearch", MeterSearchField.METERNUMBER, MeterSearchField.PAONAME));

        filterByList.add(new StandardFilterBy("meterNumber", MeterSearchField.METERNUMBER));
        filterByList.add(new StandardFilterBy("deviceName", MeterSearchField.PAONAME));
        filterByList.add(new StandardFilterBy("deviceType", MeterSearchField.TYPE));
        filterByList.add(new StandardFilterBy("address", MeterSearchField.ADDRESS, MeterSearchField.SERIALNUMBER));
        filterByList.add(new StandardFilterBy("route", MeterSearchField.ROUTE));

        return filterByList;
    }
}
