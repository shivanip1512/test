package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteGraphDefinition;

public interface GraphDao {

    public LiteGraphDefinition getLiteGraphDefinition(int id);

    public java.util.List getLiteYukonPaobjects(int gDefID);

    public java.util.List getAllGraphDataSeries();

    public java.util.List getAllGraphDataSeries(int type_);

}