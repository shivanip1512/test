package com.cannontech.database.data.lite;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.db.state.State;

public class LiteStateGroup extends LiteBase {
    private String stateGroupName;
    private List<LiteState> statesList;
    private String groupType;

    public LiteStateGroup(int sgID) {
        super();
        setLiteID(sgID);
        setLiteType(LiteTypes.STATEGROUP);
    }

    public LiteStateGroup(int sgID, String sgName) {
        this(sgID);
        setStateGroupName( sgName );
    }

    public LiteStateGroup(int sgID, String sgName, String groupType) {
        super();
        setLiteID(sgID);
        stateGroupName = new String(sgName);
        setLiteType(LiteTypes.STATEGROUP);
        setGroupType(groupType);
    }

    public LiteStateGroup( int sgID, String sgName, List<LiteState> stList ) {
        super();
        setLiteID(sgID);
        stateGroupName = new String(sgName);
        statesList = new ArrayList<LiteState>(stList);
        setLiteType(LiteTypes.STATEGROUP);
    }
    
    public int getStateGroupID() {
        return getLiteID();
    }
    
    public String getStateGroupName() {
        return stateGroupName;
    }
    
    public List<LiteState> getStatesList() {
        if( statesList == null )
            statesList = new ArrayList<LiteState>(6);
        return statesList;
    }

    public void retrieve(String databaseAlias) {

        com.cannontech.database.SqlStatement stmt =
            new com.cannontech.database.SqlStatement(
                                                     "SELECT Name,GroupType FROM StateGroup WHERE StateGroupID = " + Integer.toString(getStateGroupID()), databaseAlias);

        try
        {
            stmt.execute();
            stateGroupName = ((String) stmt.getRow(0)[0]);
            groupType = ((String) stmt.getRow(0)[1]);

            stmt = new com.cannontech.database.SqlStatement(
                                                            "SELECT RawState, Text, ForegroundColor, BackgroundColor, ImageID " + 
                                                            "FROM " + State.TABLE_NAME + " WHERE StateGroupID = " + getStateGroupID() + " " + 
                                                            "AND RAWSTATE >= 0", databaseAlias);

            stmt.execute();

            statesList = null;
            LiteState ls = null;
            for(int i=0;i<stmt.getRowCount();i++)
            {
                ls = new LiteState(
                                   ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue(), 
                                   ((String) stmt.getRow(i)[1]),
                                   ((java.math.BigDecimal)stmt.getRow(i)[2]).intValue(),
                                   ((java.math.BigDecimal)stmt.getRow(i)[3]).intValue(),
                                   ((java.math.BigDecimal)stmt.getRow(i)[4]).intValue() );

                if( ls.getStateRawState() >= 0 )
                    getStatesList().add(ls);
            }
        }
        catch( Exception e )
        {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
    }
    
    public void setStateGroupID(int newValue) {
        setLiteID(newValue);
    }

    public void setStateGroupName(String newValue) {
        this.stateGroupName = new String(newValue);
    }

    public void setStatesList(List<LiteState> newList) {
        this.statesList = new ArrayList<LiteState>(newList);
    }

    @Override
    public String toString() {
        return stateGroupName;
    }
    /**
     * Returns the groupType.
     * @return String
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * Sets the groupType.
     * @param groupType The groupType to set
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

}
