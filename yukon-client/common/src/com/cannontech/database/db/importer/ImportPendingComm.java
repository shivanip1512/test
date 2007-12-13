package com.cannontech.database.db.importer;

import java.sql.SQLException;

import com.cannontech.database.TransactionException;
import com.cannontech.database.db.NestedDBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ImportPendingComm extends NestedDBPersistent implements ImportDataBase {
	private Integer deviceID;
    private String address;
	private String name;
	private String routeName;
	private String meterNumber;
	private String collectionGrp;
	private String altGrp;
	private String templateName;
    private String billGrp;
    private String substationName;
	
	public static final String SETTER_COLUMNS[] = { 
		"DEVICEID", "ADDRESS", "NAME", 
        "ROUTENAME", "METERNUMBER", 
        "COLLECTIONGRP", "ALTGRP", 
        "TEMPLATENAME", "BILLGRP", "SUBSTATIONNAME"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DEVICEID" };

	public static final String TABLE_NAME = "ImportPendingComm";
	
	public ImportPendingComm() {
		super();
	}
	
	public ImportPendingComm(Integer _pendingID, String addy, String _name, String rName, 
                                String mNum, String colGrp, String _altGrp,
                                String tn, String _billGrp, String sn) {
		super();
        deviceID = _pendingID;
		name = _name;
		address = addy;
		routeName = rName;
		meterNumber = mNum;
		collectionGrp = colGrp;
		altGrp = _altGrp;
		templateName = tn;
        billGrp = _billGrp;
        substationName = sn;
	}

	public void add() throws java.sql.SQLException {
		Object addValues[] = { 
			getPendingID(), getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getBillGrp(), getSubstationName() 
		};

		add( TABLE_NAME, addValues );
	}

	public void delete() throws java.sql.SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPendingID());
	}

    public Integer getPendingID() {
        return deviceID;
    }
    
    public String getSubstationName() {
        return substationName;
    }
    
    public String getTemplateName() {
		return templateName;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getRouteName() {
		return routeName;
	}

	public String getMeterNumber() {
		return meterNumber;
	}

	public String getCollectionGrp() {
		return collectionGrp;
	}

	public String getAltGrp() {
		return altGrp;
	}

	public void retrieve() {
		Integer constraintValues[] = { getPendingID() };	
	
		try {
			Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
			if( results.length == SETTER_COLUMNS.length ) {
				setAddress( (String) results[1] );
                setName( ((String) results[2]).trim() );
				setRouteName( ((String) results[3]).trim() );
				setMeterNumber( ((String) results[4]).trim() );
				setCollectionGrp( ((String) results[5]).trim() );
				setAltGrp( ((String) results[6]).trim() );
				setTemplateName( ((String) results[7]).trim() );
                setBillGrp( ((String) results[8]).trim() );
                setSubstationName( ((String) results[9]).trim() );
			}
			else
			    throw new TransactionException("Pending communication not found for paoID " + getPendingID());
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

    public void setPendingID(Integer _pendingID) {
        deviceID = _pendingID;
    }
    
    public void setSubstationName(String _substationName) {
        substationName = _substationName;
    }
    
	public void setTemplateName(String _templateName) {
		templateName = _templateName;
	}

	public void setName(String _name) {
		name = _name;
	}

	public void setAddress(String _address) {
		address = _address;
	}

	public void setRouteName(String _routeName) {
		routeName = _routeName;
	}

	public void setMeterNumber(String _meterNumber) {
		meterNumber = _meterNumber;
	}

	public void setCollectionGrp(String _collectionGrp) {
		collectionGrp = _collectionGrp;
	}

	public void setAltGrp(String _altGrp) {
		altGrp = _altGrp;
	}

	public void update() {
		Object setValues[] = { 
			getPendingID(), getAddress(), getName(), 
			getRouteName(), getMeterNumber(), 
			getCollectionGrp(), getAltGrp(), 
			getTemplateName(), getBillGrp(), getSubstationName()
		};
	
		Object constraintValues[] = { getPendingID() };
	
		try {
			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

    public String getBillGrp() {
        return billGrp;
    }

    public void setBillGrp(String billGrp) {
        this.billGrp = billGrp;
    }
}