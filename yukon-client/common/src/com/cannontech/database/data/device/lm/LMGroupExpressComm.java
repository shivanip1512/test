package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.device.lm.LMGroupExpressCommAddress;

public class LMGroupExpressComm extends LMGroup
{
	private com.cannontech.database.db.device.lm.LMGroupExpressComm lmGroupExpressComm = null;

	private LMGroupExpressCommAddress serviceProviderAddress = null;
	private LMGroupExpressCommAddress geoAddress = null;
	private LMGroupExpressCommAddress substationAddress = null;
	private LMGroupExpressCommAddress feederAddress = null;
	private LMGroupExpressCommAddress programAddress = null;
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupExpressComm() 
{
	super();

	//we must always have our ExpressComm group not null
	lmGroupExpressComm = new com.cannontech.database.db.device.lm.LMGroupExpressComm();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_EXPRESSCOMM_GROUP[0] );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	super.add();

	//these adds only execute if their addressID == null
	getServiceProviderAddress().add();
	if( getLMGroupExpressComm().getServiceProviderID() == null )
		getLMGroupExpressComm().setServiceProviderID( getServiceProviderAddress().getAddressID() );

	getSubstationAddress().add();
	if( getLMGroupExpressComm().getSubstationID() == null )
		getLMGroupExpressComm().setSubstationID( getSubstationAddress().getAddressID() );

	getFeederAddress().add();
	if( getLMGroupExpressComm().getFeederID() == null )
		getLMGroupExpressComm().setFeederID( getFeederAddress().getAddressID() );

	getGeoAddress().add();
	if( getLMGroupExpressComm().getGeoID() == null )
		getLMGroupExpressComm().setGeoID( getGeoAddress().getAddressID() );

	getProgramAddress().add();
	if( getLMGroupExpressComm().getProgramID() == null )
		getLMGroupExpressComm().setProgramID( getProgramAddress().getAddressID() );

	//add the real object
	getLMGroupExpressComm().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 2:28:11 PM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();

	getLMGroupExpressComm().add();

	//these adds only execute if their addressID == null
	getServiceProviderAddress().add();
	getSubstationAddress().add();
	getFeederAddress().add();
	getGeoAddress().add();
	getProgramAddress().add();

}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	getLMGroupExpressComm().delete();

	deleteAddresses();
			 
	super.delete();

	//remove all the unused addresses
	com.cannontech.database.db.device.lm.LMGroupExpressComm.purgeUnusedAddresses( getDbConnection() );
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2002 11:08:05 AM)
 */
private void deleteAddresses() throws java.sql.SQLException
{
	//Only delete the addresses if they are not used anymore
	if( !com.cannontech.database.db.device.lm.LMGroupExpressComm.isAddressUsed(
			getDbConnection(), getServiceProviderAddress().getAddressID().intValue()) )
	{
		getServiceProviderAddress().delete();
	}
		
	if( !com.cannontech.database.db.device.lm.LMGroupExpressComm.isAddressUsed(
			getDbConnection(), getSubstationAddress().getAddressID().intValue()) )
	{
		getSubstationAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressComm.isAddressUsed(
			getDbConnection(), getFeederAddress().getAddressID().intValue()) )
	{
		getFeederAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressComm.isAddressUsed(
			getDbConnection(), getGeoAddress().getAddressID().intValue()) )
	{
		getGeoAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressComm.isAddressUsed(
			getDbConnection(), getProgramAddress().getAddressID().intValue()) )
	{
		getProgramAddress().delete();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCommAddress getFeederAddress() 
{
	if( feederAddress == null )
	{
		feederAddress = new LMGroupExpressCommAddress( LMGroupExpressCommAddress.TYPE_FEEDER );

		getFeederAddress().setAddressID( getLMGroupExpressComm().getFeederID() );
	}

	return feederAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCommAddress getGeoAddress() 
{
	if( geoAddress == null )
	{
		geoAddress = new LMGroupExpressCommAddress( LMGroupExpressCommAddress.TYPE_GEO );

		getGeoAddress().setAddressID( getLMGroupExpressComm().getGeoID() );
	}

	return geoAddress;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComm
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComm getLMGroupExpressComm() 
{
	if( lmGroupExpressComm == null )
		lmGroupExpressComm = new com.cannontech.database.db.device.lm.LMGroupExpressComm();

	return lmGroupExpressComm;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCommAddress getProgramAddress() 
{
	if( programAddress == null )
	{
		programAddress = new LMGroupExpressCommAddress( LMGroupExpressCommAddress.TYPE_PROGRAM );

		getProgramAddress().setAddressID( getLMGroupExpressComm().getProgramID() );
	}

	return programAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCommAddress getServiceProviderAddress() 
{
	if( serviceProviderAddress == null )
	{
		serviceProviderAddress = new LMGroupExpressCommAddress( LMGroupExpressCommAddress.TYPE_SERVICE );

		getServiceProviderAddress().setAddressID( getLMGroupExpressComm().getServiceProviderID() );	
	}

	return serviceProviderAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCommAddress getSubstationAddress() 
{
	if( substationAddress == null )
	{
		substationAddress = new LMGroupExpressCommAddress( LMGroupExpressCommAddress.TYPE_SUBSTATION );

		getSubstationAddress().setAddressID( getLMGroupExpressComm().getSubstationID() );
	}

	return substationAddress;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
	getLMGroupExpressComm().retrieve();
	
	if( !getLMGroupExpressComm().getServiceProviderID().equals(LMGroupExpressCommAddress.NONE_ADDRESS_ID) )
	{
		getServiceProviderAddress().setAddressID( getLMGroupExpressComm().getServiceProviderID() );
		getServiceProviderAddress().retrieve();
	}

	if( !getLMGroupExpressComm().getSubstationID().equals(LMGroupExpressCommAddress.NONE_ADDRESS_ID) )
	{
		getSubstationAddress().setAddressID( getLMGroupExpressComm().getSubstationID() );
		getSubstationAddress().retrieve();
	}

	if( !getLMGroupExpressComm().getFeederID().equals(LMGroupExpressCommAddress.NONE_ADDRESS_ID) )
	{
		getFeederAddress().setAddressID( getLMGroupExpressComm().getFeederID() );
		getFeederAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getGeoID().equals(LMGroupExpressCommAddress.NONE_ADDRESS_ID) )
	{
		getGeoAddress().setAddressID( getLMGroupExpressComm().getGeoID() );
		getGeoAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getProgramID().equals(LMGroupExpressCommAddress.NONE_ADDRESS_ID) )
	{
		getProgramAddress().setAddressID( getLMGroupExpressComm().getProgramID() );
		getProgramAddress().retrieve();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getLMGroupExpressComm().setDbConnection(conn);

	getServiceProviderAddress().setDbConnection(conn);
	getSubstationAddress().setDbConnection(conn);
	getFeederAddress().setDbConnection(conn);
	getGeoAddress().setDbConnection(conn);
	getProgramAddress().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);

	getLMGroupExpressComm().setLmGroupID(deviceID);
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newFeederAddress com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public void setFeederAddress(com.cannontech.database.db.device.lm.LMGroupExpressCommAddress newFeederAddress) 
{
	feederAddress = newFeederAddress;

	getLMGroupExpressComm().setFeederID( getFeederAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newGeoAddress com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public void setGeoAddress(com.cannontech.database.db.device.lm.LMGroupExpressCommAddress newGeoAddress) 
{
	geoAddress = newGeoAddress;

	getLMGroupExpressComm().setGeoID( getGeoAddress().getAddressID() );

}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.lmGroupExpressComm
 */
public void setLMGroupExpressComm(com.cannontech.database.db.device.lm.LMGroupExpressComm newValue)
{
	lmGroupExpressComm = newValue;
}
/**
 * This method was created in VisualAge.
 * @param id java.lang.Integer
 */
public void setLMGroupID(Integer id)
{
	super.setDeviceID(id);

	getLMGroupExpressComm().setLmGroupID( id );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newProgramAddress com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public void setProgramAddress(com.cannontech.database.db.device.lm.LMGroupExpressCommAddress newProgramAddress) 
{
	programAddress = newProgramAddress;

	getLMGroupExpressComm().setProgramID( getProgramAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newServiceProviderAddress com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public void setServiceProviderAddress(com.cannontech.database.db.device.lm.LMGroupExpressCommAddress newServiceProviderAddress) 
{
	serviceProviderAddress = newServiceProviderAddress;

	getLMGroupExpressComm().setServiceProviderID( getServiceProviderAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newSubstationAddress com.cannontech.database.db.device.lm.LMGroupExpressCommAddress
 */
public void setSubstationAddress(com.cannontech.database.db.device.lm.LMGroupExpressCommAddress newSubstationAddress) 
{
	substationAddress = newSubstationAddress;

	getLMGroupExpressComm().setSubstationID( getSubstationAddress().getAddressID() );
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	super.update();

	//these adds only execute if their addressID == null
	if( getServiceProviderAddress().getAddressID() == null )
		getServiceProviderAddress().add();
	else
		getServiceProviderAddress().update();

	if( getLMGroupExpressComm().getServiceProviderID() == null )
		getLMGroupExpressComm().setServiceProviderID( getServiceProviderAddress().getAddressID() );


	if( getSubstationAddress().getAddressID() == null )
		getSubstationAddress().add();
	else
		getSubstationAddress().update();

	if( getLMGroupExpressComm().getSubstationID() == null )
		getLMGroupExpressComm().setSubstationID( getSubstationAddress().getAddressID() );


	if( getFeederAddress().getAddressID() == null )
		getFeederAddress().add();
	else
		getFeederAddress().update();

	if( getLMGroupExpressComm().getFeederID() == null )
		getLMGroupExpressComm().setFeederID( getFeederAddress().getAddressID() );


	if( getGeoAddress().getAddressID() == null )
		getGeoAddress().add();
	else
		getGeoAddress().update();

	if( getLMGroupExpressComm().getGeoID() == null )
		getLMGroupExpressComm().setGeoID( getGeoAddress().getAddressID() );


	if( getProgramAddress().getAddressID() == null )
		getProgramAddress().add();
	else
		getProgramAddress().update();

	if( getLMGroupExpressComm().getProgramID() == null )
		getLMGroupExpressComm().setProgramID( getProgramAddress().getAddressID() );
	

	getLMGroupExpressComm().update();

	//remove all the unused addresses
	com.cannontech.database.db.device.lm.LMGroupExpressComm.purgeUnusedAddresses( getDbConnection() );
}
}
