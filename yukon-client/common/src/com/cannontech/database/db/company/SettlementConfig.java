package com.cannontech.database.db.company;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * This type was created in VisualAge.
 */

public class SettlementConfig extends com.cannontech.database.db.DBPersistent implements CTIDbChange 
{
	/* Set attributes to null when a user must enter them*/
	private Integer configID = null;
	private String fieldName = CtiUtilities.STRING_NONE;
	private String fieldValue = CtiUtilities.STRING_NONE;	
	private String description = "";
	private Integer refEntryID = null;

	public static final int HECO_CDI_RATE = -1;
	public static final int HECO_ERI_RATE = -2;
	public static final int HECO_UF_DELAY = -3;
	public static final int HECO_DISPATCHED_DELAY = -4;
	public static final int HECO_EMERGENCY_DELAY = -5;
	public static final int HECO_ALLOWED_VIOLATIONS = -6;
	public static final int HECO_RESTORE_DURATION = -7;
	public static final int HECO_RATE_DEMAND_CHARGE = -8;
//	public static final int HECO_RATE_PENALTY_CHARGE = -9;	//FAKE, DELETE
	 
	public static final String HECO_CDI_RATE_STRING = "CDI Rate";
	public static final String HECO_ERI_RATE_STRING = "ERI Rate";
	public static final String HECO_UF_DELAY_STRING = "UF Delay";
	public static final String HECO_DISPATCHED_DELAY_STRING = "Dispatched Delay";
	public static final String HECO_EMERGENCY_DELAY_STRING = "Emergency Delay";
	public static final String HECO_ALLOWED_VIOLATIONS_STRING = "Allowed Violations";
	public static final String HECO_RESTORE_DURATION_STRING = "Restore Duration";
	public static final String HECO_RATE_DEMAND_CHARGE_STRING = "Demand Charge";
//	public static final String HECO_RATE_PENALTY_CHARGE_STRING = "Penalty Fee";	//FAKE, DELETE


	public static final String SETTER_COLUMNS[] = 
	{ 
		"FieldName", "FieldValue", "Description", "RefEntryID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ConfigID" };

	public static final String TABLE_NAME = "SettlementConfig";


	/**
	 * SettlementConfig constructor comment.
	 */
	public SettlementConfig() {
		super();
	}
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException 
	{
		Object addValues[] = 
		{ 
			getConfigID(), getFieldName(), getFieldValue(), 
			getDescription(), getRefEntryID()
		};
	
		add( TABLE_NAME, addValues );
	}
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		Integer values[] = { getConfigID() };
	
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/99 10:31:33 AM)
	 * @return java.lang.Integer
	 */
	public static synchronized Integer getNextConfigID()
	{
		SqlStatement stmt = new SqlStatement(" SELECT MAX(CONFIGID)+1 FROM " + TABLE_NAME, CtiUtilities.getDatabaseAlias());
		try 
		{		
			stmt.execute();
			if( stmt.getRowCount() > 0 )
				return new Integer(stmt.getRow(0)[0].toString());
			else
				return new Integer(1);
		}
		catch( Exception e )
		{
		   CTILogger.warn( e.getMessage(), e );
		   return new Integer(1);
		}
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException 
	{
		Integer constraintValues[] = { getConfigID() };	
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setFieldName( (String) results[0] );
			setFieldValue( (String) results[1] );
			setDescription( (String) results[2] );
			setRefEntryID( (Integer) results[3]);
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	
	
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException 
	{
		Object setValues[] =
		{ 
			getFieldName(), getFieldValue(), 
			getDescription(), getRefEntryID()
		};
	
		Object constraintValues[] = { getConfigID()};
	
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getConfigID()
	{
		return configID;
	}

	/**
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @return
	 */
	public Integer getRefEntryID()
	{
		return refEntryID;
	}

	/**
	 * @return
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 * @return
	 */
	public String getFieldValue()
	{
		return fieldValue;
	}

	/**
	 * @param integer
	 */
	public void setConfigID(Integer configID_)
	{
		configID = configID_;
	}

	/**
	 * @param string
	 */
	public void setDescription(String desc_)
	{
		description = desc_;
	}

	/**
	 * @param integer
	 */
	public void setRefEntryID(Integer refEntryID_)
	{
		refEntryID = refEntryID_;
	}

	/**
	 * @param string
	 */
	public void setFieldName(String fieldName_)
	{
		fieldName = fieldName_;
	}

	/**
	 * @param string
	 */
	public void setFieldValue(String fieldValue_)
	{
		fieldValue = fieldValue_;
	}

	/**
	 * Generates a DBChange msg.
	 * 
	 */
	public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType)
	{
		DBChangeMsg[] dbChange = new DBChangeMsg[1];

		//add the basic change method
		dbChange[0] = new DBChangeMsg(
						getConfigID().intValue(),
						DBChangeMsg.CHANGE_SETTLEMENT_DB,
						DBChangeMsg.CAT_SETTLEMENT,
						dbChangeType);

		return dbChange;
	}
}
