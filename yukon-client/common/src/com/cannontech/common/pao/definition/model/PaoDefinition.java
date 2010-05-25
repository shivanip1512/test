package com.cannontech.common.pao.definition.model;

import com.cannontech.common.pao.PaoType;

/**
 * Interface which represents the default definition for a pao
 */
public interface PaoDefinition extends Comparable<PaoDefinition> {

    public String getDisplayName();

    public PaoType getType();

    public boolean isChangeable();

    public String getDisplayGroup();

    public String getJavaConstant();

    public String getChangeGroup();
    
    public boolean isCreatable();

}