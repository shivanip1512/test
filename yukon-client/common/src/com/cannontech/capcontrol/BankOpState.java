package com.cannontech.capcontrol;

import org.apache.commons.lang.Validate;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;

public enum BankOpState implements DisplayableEnum, DatabaseRepresentationSource {
    FIXED("Fixed"),
    SWITCHED("Switched"),
    STANDALONE("StandAlone"),
    UNINSTALLED("Uninstalled"),
    ;
    
    private String dbString;
    
    private final static ImmutableMap<String, BankOpState> lookupByString;
    
    static {
        ImmutableMap.Builder<String, BankOpState> stringBuilder = ImmutableMap.builder();
        for (BankOpState state : values()) {
            stringBuilder.put(state.getDbString(), state);
        }
        lookupByString = stringBuilder.build();
    }
    
    public static BankOpState getStateByName(String name) {
        BankOpState state = lookupByString.get(name);
        Validate.notNull(state, name);
        return state;
    }
    
    private BankOpState(String dbString) {
        this.dbString = dbString;
    }
    
    public String getDbString() {
        return dbString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.bankOpState." + name();
    }
}