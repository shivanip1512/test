package com.cannontech.common.pao;

import java.util.Comparator;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public enum LiteYukonPaoSortableField {
    PAO_NAME("PAOName", (LiteYukonPAObject pao1, LiteYukonPAObject pao2) -> {
        return pao1.getPaoName().compareTo(pao2.getPaoName());
    }),
    DISABLE_FLAG("DisableFlag", (LiteYukonPAObject pao1, LiteYukonPAObject pao2) -> {
        return pao1.getDisableFlag().compareTo(pao2.getDisableFlag());
    });

    private final String dbString;
    private final Comparator<LiteYukonPAObject> comparator;

    private LiteYukonPaoSortableField(String dbString, Comparator<LiteYukonPAObject> comparator) {
        this.dbString = dbString;
        this.comparator = comparator;
    }

    public String getDbString() {
        return this.dbString;
    }

    public Comparator<LiteYukonPAObject> getComparator() {
        return this.comparator;
    }
}
