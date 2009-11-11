package com.cannontech.common.pao;

import com.cannontech.database.data.pao.PAOGroups;

public enum PaoCategory {
   	DEVICE(PAOGroups.CAT_DEVICE),
   	ROUTE(PAOGroups.CAT_ROUTE),
   	PORT(PAOGroups.CAT_PORT),
   	CUSTOMER(PAOGroups.CAT_CUSTOMER),
   	CAPCONTROL(PAOGroups.CAT_CAPCONTROL),
   	LOADMANAGEMENT(PAOGroups.CAT_LOADCONTROL),
   	SCHEDULE(6), // not real
   	;
   	
   	private final int categoryId;

    private PaoCategory(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
}
