/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.database.db.importer;

import java.sql.SQLException;

import com.cannontech.database.db.NestedDBPersistent;
import java.util.Date;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportFail extends NestedDBPersistent 
{
	private String address;
	private String name;
	private String routeName;
	private String meterNumber;
	private String collectionGrp;
	private String altGrp;
	private String templateName;
	private String errorMsg;
	private Date dateTime;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ADDRESS", "NAME", "ROUTENAME", 
		"METERNUMBER", "COLLECTIONGROUP", 
		"ALTGROUP", "TEMPLATENAME",
		"ERRORMSG", "DATETIME"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ADDRESS" };

	public static final String TABLE_NAME = "ImportFail";
	
	public ImportFail()
	{
		super();
	}
	
	public ImportFail(String addy, String _name, String rName, String mNum, String colGrp, String _altGrp, String tn, String _errorMsg, Date _dateTime) 
	{
		super();
		name = _name;
		address = addy;
		routeName = rName;
		meterNumber = mNum;
		collectionGrp = colGrp;
		altGrp = _altGrp;
		templateName = tn;
		errorMsg = _errorMsg;
		dateTime = _dateTime;
	}

	public void add() throws java.sql.SQLException
	{
		Object addValues[] = 
		{ 
			getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getErrorMsg(), 
			getDateTime() 
		};

		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getAddress());
	}

   	public String getTemplateName() 
	{
		return templateName;
	}

	public String getName() 
	{
		return name;
	}

	public String getAddress() 
	{
		return address;
	}

	public String getRouteName() 
	{
		return routeName;
	}

	public String getMeterNumber() 
	{
		return meterNumber;
	}

	public String getCollectionGrp() 
	{
		return collectionGrp;
	}

	public String getAltGrp() 
	{
		return altGrp;
	}

	public void retrieve() 
	{
		String constraintValues[] = { getAddress() };	
	
		try
		{
			Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
			if( results.length == SETTER_COLUMNS.length )
			{
				setName( (String) results[1] );
				setRouteName( (String) results[2] );
				setMeterNumber( (String) results[3] );
				setCollectionGrp( (String) results[4] );
				setAltGrp( (String) results[5] );
				setTemplateName( (String) results[6] );
				setErrorMsg( (String) results[7] );
			}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

	public void setTemplateName(String _templateName) 
	{
		templateName = _templateName;
	}

	public void setName(String _name) 
	{
		name = _name;
	}

	public void setAddress(String _address) 
	{
		address = _address;
	}

	public void setRouteName(String _routeName) 
	{
		routeName = _routeName;
	}

	public void setMeterNumber(String _meterNumber) 
	{
		meterNumber = _meterNumber;
	}

	public void setCollectionGrp(String _collectionGrp) 
	{
		collectionGrp = _collectionGrp;
	}

	public void setAltGrp(String _altGrp) 
	{
		altGrp = _altGrp;
	}

	public void update() 
	{
		Object setValues[] =
		{ 
			getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getErrorMsg(), 
			getDateTime()
		};
	
		Object constraintValues[] = { getAddress() };
	
		try
		{
			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	/**
	 * @return
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * @return
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param date
	 */
	public void setDateTime(Date date) {
		dateTime = date;
	}

	/**
	 * @param string
	 */
	public void setErrorMsg(String string) {
		errorMsg = string;
	}

}