package com.cannontech.database.db.stars;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: CustomerSelectionList.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 12, 2002 12:07:21 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CustomerSelectionList extends DBPersistent {

    public static final int NONE_INT = 0;
    
    public static final String ORDERING_ALPHABETIC = "A";
    public static final String ORDERING_ORDER = "O";
    public static final String ORDERING_NATURAL = "N";
    
    public static final String LISTNAME_LMCUSTOMEREVENT = "LMCustomerEvent";
    public static final String LISTNAME_LMCUSTOMERACTION = "LMCustomerAction";
    public static final String LISTNAME_CUSTOMERTYPE = "CustomerType";
    public static final String LISTNAME_INVENTORYCATEGORY = "InventoryCategory";
    public static final String LISTNAME_INVENTORYVOLTAGE = "InventoryVoltage";
    public static final String LISTNAME_DEVICETYPE = "DeviceType";
    public static final String LISTNAME_APPLIANCECATEGORY = "ApplianceCategory";
    public static final String LISTNAME_CALLTYPE = "CallType";
    public static final String LISTNAME_SERVICETYPE = "ServiceType";
    public static final String LISTNAME_SERVICESTATUS = "ServiceStatus";
    public static final String LISTNAME_SEARCHBY = "SearchBy";
    public static final String LISTNAME_DEVICESTATUS = "DeviceStatus";

    private Integer listID = null;
    private String ordering = ORDERING_NATURAL;
    private String selectionLabel = "";
    private String whereIsList = "";
    private String listName = "";
    private String userUpdateAvailable = "N";

    public static final String[] CONSTRAINT_COLUMNS = { "ListID" };

    public static final String[] SETTER_COLUMNS = {
    	"Ordering", "SelectionLabel", "WhereIsList", "ListName", "UserUpdateAvailable"
    };

    public static final String TABLE_NAME = "CustomerSelectionList";

    public static final String GET_NEXT_LIST_ID_SQL =
        "SELECT MAX(ListID) FROM " + TABLE_NAME;

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getListID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getListID() == null)
    		setListID( getNextListID() );
    		
        Object[] addValues = {
            getListID(), getOrdering(), getSelectionLabel(), getWhereIsList(),
            getListName(), getUserUpdateAvailable()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getOrdering(), getSelectionLabel(), getWhereIsList(),
            getListName(), getUserUpdateAvailable()
        };

        Object[] constraintValues = { getListID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getListID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setOrdering( (String) results[0] );
            setSelectionLabel( (String) results[1] );
            setWhereIsList( (String) results[2] );
            setListName( (String) results[3] );
            setUserUpdateAvailable( (String) results[4] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextListID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextListID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_LIST_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextListID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextListID );
    }
    
    public static CustomerSelectionList[] getAllSelectionLists(Integer energyCompanyID) {
    	com.cannontech.database.db.stars.ECToGenericMapping[] items =
    			com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( energyCompanyID, TABLE_NAME );
    	if (items == null || items.length == 0)
    		return new CustomerSelectionList[0];
    		
    	StringBuffer sql = new StringBuffer( "SELECT * FROM " + TABLE_NAME + " WHERE ListID = " + items[0].getItemID().toString() );
    	for (int i = 1; i < items.length; i++)
    		sql.append( " OR ListID = " ).append( items[i].getItemID() );
    	
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql.toString(), com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try
        {
        	stmt.execute();
        	CustomerSelectionList[] lists = new CustomerSelectionList[ stmt.getRowCount() ];
        	
        	for (int i = 0; i < lists.length; i++) {
        		Object[] row = stmt.getRow(i);
        		lists[i] = new CustomerSelectionList();
        		
        		lists[i].setListID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
        		lists[i].setOrdering( (String) row[1] );
        		lists[i].setSelectionLabel( (String) row[2] );
        		lists[i].setWhereIsList( (String) row[3] );
        		lists[i].setListName( (String) row[4] );
        		lists[i].setUserUpdateAvailable( (String) row[5] );
        	}
        	
        	return lists;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

	/**
	 * Returns the listID.
	 * @return Integer
	 */
	public Integer getListID() {
		return listID;
	}

	/**
	 * Returns the listName.
	 * @return String
	 */
	public String getListName() {
		return listName;
	}

	/**
	 * Returns the ordering.
	 * @return Character
	 */
	public String getOrdering() {
		return ordering;
	}

	/**
	 * Returns the selectionLabel.
	 * @return String
	 */
	public String getSelectionLabel() {
		return selectionLabel;
	}

	/**
	 * Returns the userUpdateAvailable.
	 * @return Character
	 */
	public String getUserUpdateAvailable() {
		return userUpdateAvailable;
	}

	/**
	 * Returns the whereIsList.
	 * @return String
	 */
	public String getWhereIsList() {
		return whereIsList;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(Integer listID) {
		this.listID = listID;
	}

	/**
	 * Sets the listName.
	 * @param listName The listName to set
	 */
	public void setListName(String listName) {
		this.listName = listName;
	}

	/**
	 * Sets the ordering.
	 * @param ordering The ordering to set
	 */
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	/**
	 * Sets the selectionLabel.
	 * @param selectionLabel The selectionLabel to set
	 */
	public void setSelectionLabel(String selectionLabel) {
		this.selectionLabel = selectionLabel;
	}

	/**
	 * Sets the userUpdateAvailable.
	 * @param userUpdateAvailable The userUpdateAvailable to set
	 */
	public void setUserUpdateAvailable(String userUpdateAvailable) {
		this.userUpdateAvailable = userUpdateAvailable;
	}

	/**
	 * Sets the whereIsList.
	 * @param whereIsList The whereIsList to set
	 */
	public void setWhereIsList(String whereIsList) {
		this.whereIsList = whereIsList;
	}

}
