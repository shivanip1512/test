/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.database.db.importer;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.NestedDBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportFail extends NestedDBPersistent implements ImportDataBase
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
    private String billGrp;
    private String substationName;
    private String failType;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ADDRESS", "NAME", "ROUTENAME", 
		"METERNUMBER", "COLLECTIONGRP", 
		"ALTGRP", "TEMPLATENAME",
		"ERRORMSG", "DATETIME",
        "BILLGRP", "SUBSTATIONNAME", "FAILTYPE"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ADDRESS" };

	public static final String TABLE_NAME = "ImportFail";
	
	public ImportFail()
	{
		super();
	}
	
	public ImportFail(String addy, String _name, String rName, String mNum, String colGrp, String _altGrp, 
            String tn, String _errorMsg, Date _dateTime, String _billGrp, String _substationName, String _failType) 
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
        billGrp = _billGrp;
        substationName = _substationName;
        failType = _failType;
	}

	public void add() throws java.sql.SQLException
	{
		Object addValues[] = 
		{ 
			getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getErrorMsg(), 
			getDateTime(), getBillGrp(), 
            getSubstationName(), getFailType()
		};

		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getAddress());
	}

    public String getFailType() 
    {
        return failType;
    }
    
    public String getSubstationName() 
    {
        return substationName;
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
				setName( ((String) results[1]).trim() );
				setRouteName( ((String) results[2]).trim() );
				setMeterNumber( ((String) results[3]).trim() );
				setCollectionGrp( ((String) results[4]).trim() );
				setAltGrp( ((String) results[5]).trim() );
				setTemplateName( ((String) results[6]).trim() );
				setErrorMsg( ((String) results[7]).trim() );
                setDateTime( (java.util.Date) results[8] );
                setBillGrp( ((String) results[9]).trim() );
                setSubstationName( ((String) results[10]).trim() );
                setFailType( ((String) results[11]).trim() );
			}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
		}
		catch (Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}
	}

    public void setFailType(String _failType) 
    {
        failType = _failType;
    }
    
    public void setSubstationName(String _substationName) 
    {
        substationName = _substationName;
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
			getDateTime(), getBillGrp(), 
            getSubstationName(), getFailType()
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

    public String getBillGrp() {
        return billGrp;
    }

    public void setBillGrp(String billGrp) {
        this.billGrp = billGrp;
    }

}