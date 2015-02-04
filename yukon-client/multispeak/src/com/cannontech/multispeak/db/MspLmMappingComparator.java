package com.cannontech.multispeak.db;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class MspLmMappingComparator implements Comparator<MspLmMapping> {

    private MspLmMappingColumn column;
    private boolean ascending;

    public MspLmMappingComparator(MspLmMappingColumn column, boolean ascending) {

        this.column = column;
        this.ascending = ascending;
    }

    @Override
    public int compare(MspLmMapping o1, MspLmMapping o2) {

        MspLmMapping obj1 = o1;
        MspLmMapping obj2 = o2;
        if (ascending) {
            obj1 = o2;
            obj2 = o1;
        }

        if (column.equals(MspLmMappingColumn.STRATEGY)) {

            return new CompareToBuilder().append(obj1.getStrategyName().toLowerCase(),
                obj2.getStrategyName().toLowerCase()).toComparison();

        } else if (column.equals(MspLmMappingColumn.SUBSTATION)) {

            return new CompareToBuilder().append(obj1.getSubstationName().toLowerCase(),
                obj2.getSubstationName().toLowerCase()).toComparison();

        } else if (column.equals(MspLmMappingColumn.PAO)) {

            return new CompareToBuilder().append(obj1.getPaoName().toLowerCase(), obj2.getPaoName().toLowerCase()).toComparison();
        } else {

            throw new IllegalArgumentException("Unsupported column type: " + column);
        }
    }
}
