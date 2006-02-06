package com.cannontech.database.db.stars.report;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.stars.LiteServiceCompanyDesignationCode;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.ECToGenericMapping;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ServiceCompany extends DBPersistent {

    public static final int NONE_INT = 0;
    
    public static final String LISTNAME_SERVICECOMPANY = "ServiceCompany";

    private Integer companyID = null;
    private String companyName = "";
    private Integer addressID = new Integer( 0 );
    private String mainPhoneNumber = "";
    private String mainFaxNumber = "";
    private Integer primaryContactID = new Integer( NONE_INT );
    private String hiType = "";

    public static final String[] SETTER_COLUMNS = {
        "CompanyName", "AddressID", "MainPhoneNumber", "MainFaxNumber", "PrimaryContactID", "HIType"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "CompanyID" };

    public static final String TABLE_NAME = "ServiceCompany";

    public static final String GET_NEXT_COMPANY_ID_SQL =
        "SELECT MAX(CompanyID) FROM " + TABLE_NAME;

    public ServiceCompany() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getCompanyID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getCompanyID() == null)
    		setCompanyID( getNextCompanyID() );
    		
        Object[] addValues = {
            getCompanyID(), getCompanyName(), getAddressID(), getMainPhoneNumber(),
            getMainFaxNumber(), getPrimaryContactID(), getHIType()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getCompanyName(), getAddressID(), getMainPhoneNumber(),
            getMainFaxNumber(), getPrimaryContactID(), getHIType()
        };

        Object[] constraintValues = { getCompanyID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getCompanyID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setCompanyName( (String) results[0] );
            setAddressID( (Integer) results[1] );
            setMainPhoneNumber( (String) results[2] );
            setMainFaxNumber( (String) results[3] );
            setPrimaryContactID( (Integer) results[4] );
            setHIType( (String) results[5] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCompanyID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextCompanyID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_COMPANY_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextCompanyID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
        }

        return new Integer( nextCompanyID );
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer newCompanyID) {
        companyID = newCompanyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String newCompanyName) {
        companyName = newCompanyName;
    }

    public Integer getAddressID() {
        return addressID;
    }

    public void setAddressID(Integer newAddressID) {
        addressID = newAddressID;
    }

    public String getMainPhoneNumber() {
        return mainPhoneNumber;
    }

    public void setMainPhoneNumber(String newMainPhoneNumber) {
        mainPhoneNumber = newMainPhoneNumber;
    }

    public String getMainFaxNumber() {
        return mainFaxNumber;
    }

    public void setMainFaxNumber(String newMainFaxNumber) {
        mainFaxNumber = newMainFaxNumber;
    }

    public Integer getPrimaryContactID() {
        return primaryContactID;
    }

    public void setPrimaryContactID(Integer newPrimaryContactID) {
        primaryContactID = newPrimaryContactID;
    }
	/**
	 * Returns the hiType.
	 * @return String
	 */
	public String getHIType() {
		return hiType;
	}

	/**
	 * Sets the hiType.
	 * @param hiType The hiType to set
	 */
	public void setHIType(String hiType) {
		this.hiType = hiType;
	}

	/*public ArrayList getDesignationCodes() {
		if( designationCodes == null)
			designationCodes = new ArrayList();
		return designationCodes;
	}

	public void setDesignationCodes(ArrayList designationCodes) {
		this.designationCodes = designationCodes;
	}*/

}