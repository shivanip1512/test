package com.cannontech.common.pao.definition.model;

import com.cannontech.common.pao.PaoType;

/**
 * Interface which represents the default definition for a pao
 */
public interface PaoDefinition extends Comparable<PaoDefinition> {

    public abstract String getDisplayName();

    public abstract PaoType getType();

    public abstract boolean isChangeable();

    public abstract String getDisplayGroup();

    public abstract String getJavaConstant();

    public abstract String getChangeGroup();

}