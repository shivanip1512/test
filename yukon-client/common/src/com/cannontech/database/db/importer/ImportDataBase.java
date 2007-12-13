package com.cannontech.database.db.importer;

public interface ImportDataBase {

    public abstract String getSubstationName();

    public abstract String getTemplateName();

    public abstract String getName();

    public abstract String getAddress();

    public abstract String getRouteName();

    public abstract String getMeterNumber();

    public abstract String getCollectionGrp();

    public abstract String getAltGrp();

    public abstract String getBillGrp();

}