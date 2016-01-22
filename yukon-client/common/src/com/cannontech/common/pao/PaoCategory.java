package com.cannontech.common.pao;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@SuppressWarnings("deprecation")
public enum PaoCategory implements DatabaseRepresentationSource {
    DEVICE(PAOGroups.STRING_CAT_DEVICE, PAOGroups.CAT_DEVICE),
    ROUTE(PAOGroups.STRING_CAT_ROUTE, PAOGroups.CAT_ROUTE),
    PORT(PAOGroups.STRING_CAT_PORT, PAOGroups.CAT_PORT),
    CUSTOMER(PAOGroups.STRING_CAT_CUSTOMER, PAOGroups.CAT_CUSTOMER),
    CAPCONTROL(PAOGroups.STRING_CAT_CAPCONTROL, PAOGroups.CAT_CAPCONTROL),
    LOADMANAGEMENT(PAOGroups.STRING_CAT_LOADMANAGEMENT, PAOGroups.CAT_LOADCONTROL),
    SCHEDULE("Schedule", 6); // not real

    private final int categoryId;
    private final String dbString;
    private static final Logger log = YukonLogManager.getLogger(PaoCategory.class);
    
    private final static ImmutableMap<String, PaoCategory> lookupByDbString;
    private final static ImmutableMap<Integer, PaoCategory> lookupById;

    static {
        try {
            Builder<String, PaoCategory> stringBuilder = ImmutableMap.builder();
            Builder<Integer, PaoCategory> integerBuilder = ImmutableMap.builder();
            for (PaoCategory paoCategory : values()) {
                stringBuilder.put(paoCategory.dbString.toUpperCase(), paoCategory);
                integerBuilder.put(paoCategory.categoryId, paoCategory);
            }
            lookupByDbString = stringBuilder.build();
            lookupById = integerBuilder.build();

        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps.  Look for duplicate name or db string in PaoCategory.");
            throw e;
        }
    }

    private PaoCategory(String dbString, int categoryId) {
        this.dbString = dbString;
        this.categoryId = categoryId;
    }

    public String getDbString() {
        return dbString;
    }

    public int getPaoCategoryId() {
        return categoryId;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

    /**
     * Looks up the the PaoCategory based on the string that is stored in the
     * PAObject table.
     * @param dbString - type name to lookup
     * @return
     * @throws IllegalArgumentException - if no match
     * @deprecated It is suggested to use YukonJdbcConnection and YukonResultSet.getEnum instead.
     */
    @Deprecated
    public static PaoCategory getForDbString(String dbString) throws IllegalArgumentException {
        String lookupString = dbString.toUpperCase().trim();
        PaoCategory paoCategory = lookupByDbString.get(lookupString);
        Validate.notNull(paoCategory, dbString);
        return paoCategory;
    }

    /**
     * Returns PaoCategory string of specified integer Id.
     * @param categoryId
     * @return If categoryId does not match an integer ID, an IllegalArgumentException
     * will be thrown by getForId().
     */
    public static String getPaoCategory(int categoryId) {
        PaoCategory foundCategory = lookupById.get(categoryId);
        return foundCategory.getDbString();
    }
}
