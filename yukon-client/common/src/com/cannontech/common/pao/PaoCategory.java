package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum PaoCategory implements DatabaseRepresentationSource{
   	DEVICE(PAOGroups.CAT_DEVICE),
   	ROUTE(PAOGroups.CAT_ROUTE),
   	PORT(PAOGroups.CAT_PORT),
   	CUSTOMER(PAOGroups.CAT_CUSTOMER),
   	CAPCONTROL(PAOGroups.CAT_CAPCONTROL),
   	LOADMANAGEMENT(PAOGroups.CAT_LOADCONTROL),
   	SCHEDULE(6), // not real
   	;
   	
   	
   	private final static ImmutableMap<String, PaoCategory> lookupByDbString;
    static {
        Builder<String, PaoCategory> dbBuilder = ImmutableMap.builder();
        for (PaoCategory paoCategory : values()) {
            dbBuilder.put(paoCategory.name().toLowerCase(), paoCategory);
        }
        lookupByDbString = dbBuilder.build();
    }
   	
   	private final int categoryId;

    private PaoCategory(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    /**
     * Looks up the the PaoCategory based on the string that is stored in the
     * PAObject table.
     * @param dbString - type name to lookup, case insensitive
     * @return
     * @throws IllegalArgumentException - if no match
     */
    public static PaoCategory getForDbString(String dbString) throws IllegalArgumentException {
        PaoCategory paoCategory = lookupByDbString.get(dbString.toLowerCase());
        Validate.notNull(paoCategory, dbString);
        return paoCategory;
    }
    
}