/*
 * Created on Jan 31, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.database.db.importer;

import com.cannontech.database.db.NestedDBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportData extends NestedDBPersistent 
{
	private String address;
	private String name;
	private String routeName;
	private String meterNumber;
	private String collectionGrp;
	private String altGrp;
	private String templateName;
    private String billingGroup;
    private String substationName;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"ADDRESS", "NAME", "ROUTENAME", 
		"METERNUMBER", "COLLECTIONGRP", 
		"ALTGRP", "TEMPLATENAME", "BILLGRP",
        "SUBSTATIONNAME"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "ADDRESS" };

	public static final String TABLE_NAME = "ImportData";
	
	public ImportData()
	{
		super();
	}
	
	public ImportData(String addy, String _name, String rName, 
                        String mNum, String colGrp, String _altGrp, 
                        String tn, String _billGrp, String sn) 
	{
		super();
		name = _name;
		address = addy;
		routeName = rName;
		meterNumber = mNum;
		collectionGrp = colGrp;
		altGrp = _altGrp;
		templateName = tn;
        billingGroup = _billGrp;
        substationName = sn;
	}

	public void add() throws java.sql.SQLException
	{
		Object addValues[] = 
		{ 
			getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getBillingGroup(), getSubstationName() 
		};

		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException
	{
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getAddress());
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
                setBillingGroup( ((String) results[7]).trim() );
                setSubstationName( ((String) results[8]).trim() );
			}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
		}
		catch (Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
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
			getTemplateName(), getBillingGroup(), getSubstationName()
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

    public String getBillingGroup() {
        return billingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        this.billingGroup = billingGroup;
    }
}