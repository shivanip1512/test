package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: LMProgramWebPublishing.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 12:31:35 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LMProgramWebPublishing extends DBPersistent {

    private Integer applianceCategoryID = null;
    private Integer lmProgramID = null;
    private Integer webSettingsID = new Integer(0);

    public static final String[] SETTER_COLUMNS = { "WebSettingsID" };

    public static final String[] CONSTRAINT_COLUMNS = {
    	"ApplianceCategoryID", "LMProgramID"
    };

    public static final String TABLE_NAME = "LMProgramWebPublishing";

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceCategoryID(), getLMProgramID(), getWebSettingsID()
		};
		
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		
        if (results.length == SETTER_COLUMNS.length) {
            setWebSettingsID( (Integer) results[0] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = { getWebSettingsID() };
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}
	
	public static LMProgramWebPublishing getLMProgramWebPublishing(Integer programID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE LMProgramID = ?";

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
                pstmt.setInt( 1, programID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    LMProgramWebPublishing webPub = new LMProgramWebPublishing();

					webPub.setApplianceCategoryID( new Integer(rset.getInt("ApplianceCategoryID")) );
					webPub.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );
					webPub.setWebSettingsID( new Integer(rset.getInt("WebSettingsID")) );

                    return webPub;
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

    public static LMProgramWebPublishing[] getALLLMProgramWebPublishing(Integer appCatID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList webPubList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, appCatID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    LMProgramWebPublishing webPub = new LMProgramWebPublishing();

					webPub.setApplianceCategoryID( new Integer(rset.getInt("ApplianceCategoryID")) );
					webPub.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );
					webPub.setWebSettingsID( new Integer(rset.getInt("WebSettingsID")) );

                    webPubList.add(webPub);
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

        LMProgramWebPublishing[] webPubs = new LMProgramWebPublishing[ webPubList.size() ];
        webPubList.toArray( webPubs );
        return webPubs;
    }

    public static void deleteAllLMProgramWebPublishing(Integer appCatID, java.sql.Connection conn) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = ?";

        java.sql.PreparedStatement pstmt = null;
        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, appCatID.intValue() );
                pstmt.execute();
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
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }
    }

	/**
	 * Returns the applianceCategoryID.
	 * @return Integer
	 */
	public Integer getApplianceCategoryID() {
		return applianceCategoryID;
	}

	/**
	 * Returns the lmProgramID.
	 * @return Integer
	 */
	public Integer getLMProgramID() {
		return lmProgramID;
	}

	/**
	 * Returns the webSettingsID.
	 * @return Integer
	 */
	public Integer getWebSettingsID() {
		return webSettingsID;
	}

	/**
	 * Sets the applianceCategoryID.
	 * @param applianceCategoryID The applianceCategoryID to set
	 */
	public void setApplianceCategoryID(Integer applianceCategoryID) {
		this.applianceCategoryID = applianceCategoryID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLMProgramID(Integer lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

	/**
	 * Sets the webSettingsID.
	 * @param webSettingsID The webSettingsID to set
	 */
	public void setWebSettingsID(Integer webSettingsID) {
		this.webSettingsID = webSettingsID;
	}

}
