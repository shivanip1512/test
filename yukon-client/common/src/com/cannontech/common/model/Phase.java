package com.cannontech.common.model;

import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Lists;

public enum Phase implements DisplayableEnum {
    A,
    B,
    C,
    ALL; // i.e. Gang Operated Regulator

    private final static String baseKey = "yukon.common.phase."; 
    
    public static List<Phase> getRealPhases() {
        return Lists.newArrayList(A, B, C);
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + this;
    }
}