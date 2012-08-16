package com.cannontech.common.bulk.model;

import com.cannontech.database.data.point.UnitOfMeasure;

public class ImportUnitOfMeasure {
    public static int valueOf(String string) {
        int id = UnitOfMeasure.valueOf(string).getId();
        if(id < 1) throw new IllegalArgumentException();
        return id;
    }
}
