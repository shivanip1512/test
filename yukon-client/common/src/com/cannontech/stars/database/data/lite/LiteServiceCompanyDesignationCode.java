package com.cannontech.stars.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.stars.database.db.report.ServiceCompany;
import com.cannontech.stars.database.db.report.ServiceCompanyDesignationCode;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteServiceCompanyDesignationCode extends LiteBase {
	private String designationCodeValue = null;
	private int serviceCompanyID = ServiceCompany.NONE_INT;
	
	public LiteServiceCompanyDesignationCode() {
		super();
		setLiteType( LiteTypes.STARS_SERVICE_COMPANY_DESIGNATION_CODE );
	}
	
	public LiteServiceCompanyDesignationCode(int designCodeID) {
		super();
		setDesignationCodeID( designCodeID );
		setLiteType( LiteTypes.STARS_SERVICE_COMPANY );
	}

	public LiteServiceCompanyDesignationCode(int designCodeID, String designCodeValue, int servCompID) {
		super();
		setDesignationCodeID( designCodeID );
		setDesignationCodeValue(designCodeValue);
		setServiceCompanyID(servCompID);
		setLiteType( LiteTypes.STARS_SERVICE_COMPANY );
	}

	public int getDesignationCodeID() {
		return getLiteID();
	}
	
	public void setDesignationCodeID(int designCodeID) {
		setLiteID( designCodeID );
	}

	public String getDesignationCodeValue() {
		return designationCodeValue;
	}

	public void setDesignationCodeValue(String designationCodeValue) {
		this.designationCodeValue = designationCodeValue;
	}

	public int getServiceCompanyID() {
		return serviceCompanyID;
	}

	public void setServiceCompanyID(int serviceCompanyID) {
		this.serviceCompanyID = serviceCompanyID;
	}
	
	public void retrieve(String dbAlias) {
		java.sql.Connection conn = null;
		try {
			conn = PoolManager.getInstance().getConnection( dbAlias );
			
			SqlStatement stat = new SqlStatement( "SELECT DESIGNATIONCODEID, DESIGNATIONCODEVALUE, SERVICECOMPANYID " + 
												" FROM " + ServiceCompanyDesignationCode.TABLE_NAME  + 
												" WHERE DESIGNATIONCODEID = " + getDesignationCodeID(), conn );
			
			stat.execute();
			
			if( stat.getRowCount() <= 0 )
				throw new IllegalStateException("Unable to find the ServiceCompanyDesignationCode with DesignatoinCodeID = " + getDesignationCodeID() );
			
			Object[] objs = stat.getRow(0);
			
			setDesignationCodeID( ((java.math.BigDecimal) objs[0]).intValue() );
			setDesignationCodeValue( objs[1].toString() );
			setServiceCompanyID( ((java.math.BigDecimal) objs[2]).intValue() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
}
