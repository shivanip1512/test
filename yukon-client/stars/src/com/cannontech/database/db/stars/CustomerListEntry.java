package com.cannontech.database.db.stars;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: CustomerListEntry.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 12, 2002 12:38:09 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CustomerListEntry extends DBPersistent {

    public static final int NONE_INT = 0;
    
    public static final String YUKONDEF_LMPROGRAMEVENT = "LMPROGRAMEVENT";
    public static final String YUKONDEF_LMHARDWAREEVENT = "LMHARDWAREEVENT";
    public static final String YUKONDEF_ACT_SIGNUP = "ACT_SIGNUP";
    public static final String YUKONDEF_ACT_PENDING = "ACT_PENDING";
    public static final String YUKONDEF_ACT_COMPLETED = "ACT_COMPLETED";
    public static final String YUKONDEF_ACT_TERMINATION = "ACT_TERMINATION";
    public static final String YUKONDEF_ACT_TEMPTERMINATION = "ACT_TEMPTERMINATION";
    public static final String YUKONDEF_ACT_FUTUREACTIVATION = "ACT_FUTUREACTIVATION";
    public static final String YUKONDEF_CUSTTYPE_RES = "CUSTTYPE_RES";
    public static final String YUKONDEF_CUSTTYPE_COMM = "CUSTTYPE_COMM";
    public static final String YUKONDEF_INVCAT_ONEWAYREC = "INVCAT_ONEWAYREC";
    public static final String YUKONDEF_APPCAT_AC = "APPCAT_AC";
    public static final String YUKONDEF_APPCAT_WH = "APPCAT_WH";
    public static final String YUKONDEF_SEARCHBY_ACCTNO = "SEARCHBY_ACCTNO";
    public static final String YUKONDEF_SEARCHBY_PHONENO = "SEARCHBY_PHONENO";
    public static final String YUKONDEF_SEARCHBY_NAME = "SEARCHBY_NAME";

    private Integer entryID = null;
    private Integer listID = null;
    private Integer entryOrder = new Integer(0);
    private String entryText = "";
    private String yukonDefinition = "";

    public static final String[] CONSTRAINT_COLUMNS = { "EntryID" };

    public static final String[] SETTER_COLUMNS = {
    	"ListID", "EntryOrder", "EntryText", "YukonDefinition"
    };

    public static final String TABLE_NAME = "CustomerListEntry";

    public static final String GET_NEXT_ENTRY_ID_SQL =
        "SELECT MAX(EntryID) FROM " + TABLE_NAME;

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getEntryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getEntryID() == null)
    		setEntryID( getNextEntryID() );
    		
        Object[] addValues = {
            getEntryID(), getListID(), getEntryOrder(), getEntryText(), getYukonDefinition()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
        	getListID(), getEntryOrder(), getEntryText(), getYukonDefinition()
        };

        Object[] constraintValues = { getEntryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getEntryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
        	setListID( (Integer) results[0] );
        	setEntryOrder( (Integer) results[1] );
        	setEntryText( (String) results[2] );
        	setYukonDefinition( (String) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextEntryID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextEntryID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_ENTRY_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextEntryID = rset.getInt(1) + 1;
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

        return new Integer( nextEntryID );
    }
    
    public static CustomerListEntry[] getAllListEntries(Integer listID, java.sql.Connection conn) {
    	CustomerSelectionList list = new CustomerSelectionList();
    	list.setListID( listID );
    	list.setOrdering( CustomerSelectionList.ORDERING_NATURAL );
    	
    	return getAllListEntries(list, conn);
    }
    
    public static CustomerListEntry[] getAllListEntries(CustomerSelectionList list, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ListID = ?";
        if (list.getOrdering().equalsIgnoreCase( CustomerSelectionList.ORDERING_ALPHABETIC ))
        	sql += " ORDER BY EntryText";
        else if (list.getOrdering().equalsIgnoreCase( CustomerSelectionList.ORDERING_ORDER ))
        	sql += " ORDER BY EntryOrder";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList entryList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, list.getListID().intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                	CustomerListEntry entry = new CustomerListEntry();
                	
                	entry.setEntryID( new Integer(rset.getInt("EntryID")) );
                	entry.setListID( new Integer(rset.getInt("ListID")) );
                	entry.setEntryOrder( new Integer(rset.getInt("EntryOrder")) );
                	entry.setEntryText( rset.getString("EntryText") );
                	entry.setYukonDefinition( rset.getString("YukonDefinition") );
                	
                	entryList.add(entry);
                }
            }
        }
        catch( java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        CustomerListEntry[] entries = new CustomerListEntry[ entryList.size() ];
        entryList.toArray( entries );
        return entries;
    }
    
    public static Integer getListEntryID(Integer listID, String yukonDef, java.sql.Connection conn) {
        String sql = "SELECT EntryID FROM " + TABLE_NAME + " WHERE ListID = ? AND YukonDefinition = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, listID.intValue() );
                pstmt.setString( 2, yukonDef );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                	return new Integer(rset.getInt( "EntryID" ));
                }
            }
        }
        catch( java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        return null;
    }

	/**
	 * Returns the entryID.
	 * @return Integer
	 */
	public Integer getEntryID() {
		return entryID;
	}

	/**
	 * Returns the entryOrder.
	 * @return Integer
	 */
	public Integer getEntryOrder() {
		return entryOrder;
	}

	/**
	 * Returns the entryText.
	 * @return String
	 */
	public String getEntryText() {
		return entryText;
	}

	/**
	 * Returns the listID.
	 * @return Integer
	 */
	public Integer getListID() {
		return listID;
	}

	/**
	 * Returns the yukonDefinition.
	 * @return String
	 */
	public String getYukonDefinition() {
		return yukonDefinition;
	}

	/**
	 * Sets the entryID.
	 * @param entryID The entryID to set
	 */
	public void setEntryID(Integer entryID) {
		this.entryID = entryID;
	}

	/**
	 * Sets the entryOrder.
	 * @param entryOrder The entryOrder to set
	 */
	public void setEntryOrder(Integer entryOrder) {
		this.entryOrder = entryOrder;
	}

	/**
	 * Sets the entryText.
	 * @param entryText The entryText to set
	 */
	public void setEntryText(String entryText) {
		this.entryText = entryText;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(Integer listID) {
		this.listID = listID;
	}

	/**
	 * Sets the yukonDefinition.
	 * @param yukonDefinition The yukonDefinition to set
	 */
	public void setYukonDefinition(String yukonDefinition) {
		this.yukonDefinition = yukonDefinition;
	}

}
