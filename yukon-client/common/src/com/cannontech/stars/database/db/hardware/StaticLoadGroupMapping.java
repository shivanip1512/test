package com.cannontech.stars.database.db.hardware;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

public class StaticLoadGroupMapping extends DBPersistent {

    private Integer loadGroupID;
    private Integer applianceCategoryID = new Integer(CtiUtilities.NONE_ZERO_ID);
    private String zipCode = "";
    private Integer consumptionTypeID = new Integer(CtiUtilities.NONE_ZERO_ID);
    private Integer switchTypeID = new Integer(CtiUtilities.NONE_ZERO_ID);
    
    public static final String CONSTRAINT_COLUMNS[] = { "LoadGroupID" };

    public static final String SETTER_COLUMNS[] = { "LoadGroupID", "ApplianceCategoryID", "ZipCode", "ConsumptionTypeID", "SwitchTypeID" };

    public static final String TABLE_NAME = "StaticLoadGroupMapping";
    
    /*Not a db column*/
    private String loadGroupName = "";

public StaticLoadGroupMapping() {
    super();
}

@Override
public void add() throws java.sql.SQLException 
{
    /*
     * Not necessary for this table's current use
     */
}

@Override
public void delete() throws java.sql.SQLException 
{
    /*
     * Not necessary for this table's current use
     */
}

@Override
public void retrieve() throws java.sql.SQLException 
{
    /*
     * Not necessary for this table's current use
     */
}

@Override
public void update() throws java.sql.SQLException 
{
    /*
     * Not necessary for this table's current use
     */
}

public static List<StaticLoadGroupMapping> getAllLoadGroupsForApplianceCat(int appCategoryID)
{
    List<StaticLoadGroupMapping> groups = new ArrayList<StaticLoadGroupMapping>();
    
    /*
     *TODO
     *find out if we need to use programID here; it would need to be added to the table 
     */
    SqlStatement stmt = new SqlStatement("Select distinct slg.LoadGroupID, yp.PAOName from " + TABLE_NAME 
                                         + " slg, YukonPAObject yp where ApplianceCategoryID = " + appCategoryID + " and yp.paobjectID = slg.loadGroupID", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                StaticLoadGroupMapping group = new StaticLoadGroupMapping();
                group.setLoadGroupID(new Integer(stmt.getRow(i)[0].toString()));
                /*Including the other fields was nullifying the usefulness of the "distinct" call in the query
                 * and I'm trying to avoid an additional query overall.  We only actually need ID and name for
                 * this method since it is used only to populate pulldowns on the relevant JSPs.
                 * group.setApplianceCategoryID(new Integer(stmt.getRow(i)[1].toString()));
                group.setZipCode(stmt.getRow(i)[2].toString());
                group.setConsumptionTypeID(new Integer(stmt.getRow(i)[3].toString()));
                group.setSwitchTypeID(new Integer(stmt.getRow(i)[4].toString()));*/
                group.setLoadGroupName(stmt.getRow(i)[1].toString());
                
                groups.add(group);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return groups;
}

public static List<StaticLoadGroupMapping> getAllStaticLoadGroups()
{
    List<StaticLoadGroupMapping> groups = new ArrayList<StaticLoadGroupMapping>();
    
    /*
     *TODO
     *find out if we need to use programID here; it would need to be added to the table 
     */
    SqlStatement stmt = new SqlStatement("Select distinct slg.LoadGroupID, slg.ApplianceCategoryID, slg.ZipCode, slg.ConsumptionTypeID, slg.SwitchTypeID, yp.PAOName from " + TABLE_NAME 
                                         + " slg, YukonPAObject yp where yp.paobjectID = slg.loadGroupID", CtiUtilities.getDatabaseAlias());
    
    try
    {
        stmt.execute();
        
        if( stmt.getRowCount() > 0 )
        {
            for( int i = 0; i < stmt.getRowCount(); i++ )
            {
                StaticLoadGroupMapping group = new StaticLoadGroupMapping();
                group.setLoadGroupID(new Integer(stmt.getRow(i)[0].toString()));
                group.setApplianceCategoryID(new Integer(stmt.getRow(i)[1].toString()));
                group.setZipCode(stmt.getRow(i)[2].toString());
                group.setConsumptionTypeID(new Integer(stmt.getRow(i)[3].toString()));
                group.setSwitchTypeID(new Integer(stmt.getRow(i)[4].toString()));
                group.setLoadGroupName(stmt.getRow(i)[5].toString());
                
                groups.add(group);
            }
        }
    }
    catch( Exception e )
    {
        e.printStackTrace();
    }
    
    return groups;
}

public Integer getApplianceCategoryID() {
    return applianceCategoryID;
}

public void setApplianceCategoryID(Integer applianceCategoryID) {
    this.applianceCategoryID = applianceCategoryID;
}

public Integer getConsumptionTypeID() {
    return consumptionTypeID;
}

public void setConsumptionTypeID(Integer consumptionTypeID) {
    this.consumptionTypeID = consumptionTypeID;
}

public Integer getLoadGroupID() {
    return loadGroupID;
}

public void setLoadGroupID(Integer loadGroupID) {
    this.loadGroupID = loadGroupID;
}

public Integer getSwitchTypeID() {
    return switchTypeID;
}

public void setSwitchTypeID(Integer switchTypeID) {
    this.switchTypeID = switchTypeID;
}

public String getZipCode() {
    return zipCode;
}

public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
}

public String getLoadGroupName() {
    return loadGroupName;
}

public void setLoadGroupName(String loadGroupName) {
    this.loadGroupName = loadGroupName;
}

}
