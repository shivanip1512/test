package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;

public class LMGroupExpressCom extends LMGroup implements IGroupRoute
{
	private com.cannontech.database.db.device.lm.LMGroupExpressCom lmGroupExpressComm = null;

	private LMGroupExpressComAddress serviceProviderAddress = null;
	private LMGroupExpressComAddress geoAddress = null;
	private LMGroupExpressComAddress substationAddress = null;
	private LMGroupExpressComAddress feederAddress = null;
	private LMGroupExpressComAddress programAddress = null;
	private LMGroupExpressComAddress zipCodeAddress = null;
	private LMGroupExpressComAddress userAddress = null;
	private LMGroupExpressComAddress splinterAddress = null;
	
/**
 * LMGroupVersacom constructor comment.
 */
public LMGroupExpressCom() 
{
	super();

	//we must always have our ExpressComm group not null
	lmGroupExpressComm = new com.cannontech.database.db.device.lm.LMGroupExpressCom();

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
		
	getSplinterAddress().add();
	if( getLMGroupExpressComm().getSplinterID() == null )
		getLMGroupExpressComm().setSplinterID( getSplinterAddress().getAddressID() );
		
	getUserAddress().add();
	if( getLMGroupExpressComm().getUserID() == null )
		getLMGroupExpressComm().setUserID( getUserAddress().getAddressID() );
		
	getZipCodeAddress().add();
	if( getLMGroupExpressComm().getZipID() == null )
		getLMGroupExpressComm().setZipID( getZipCodeAddress().getAddressID() );

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
	getSplinterAddress().add();
	getUserAddress().add();
	getZipCodeAddress().add();

}

public void setRouteID( Integer rtID_ )
{
	getLMGroupExpressComm().setRouteID( rtID_ );
}	

public Integer getRouteID()
{
	return getLMGroupExpressComm().getRouteID();
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
	com.cannontech.database.db.device.lm.LMGroupExpressCom.purgeUnusedAddresses( getDbConnection() );
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2002 11:08:05 AM)
 */
private void deleteAddresses() throws java.sql.SQLException
{
	//Only delete the addresses if they are not used anymore
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getServiceProviderAddress().getAddressID().intValue()) )
	{
		getServiceProviderAddress().delete();
	}
		
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getSubstationAddress().getAddressID().intValue()) )
	{
		getSubstationAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getFeederAddress().getAddressID().intValue()) )
	{
		getFeederAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getGeoAddress().getAddressID().intValue()) )
	{
		getGeoAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getProgramAddress().getAddressID().intValue()) )
	{
		getProgramAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getSplinterAddress().getAddressID().intValue()) )
	{
		getSplinterAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getUserAddress().getAddressID().intValue()) )
	{
		getUserAddress().delete();
	}
	
	if( !com.cannontech.database.db.device.lm.LMGroupExpressCom.isAddressUsed(
			getDbConnection(), getZipCodeAddress().getAddressID().intValue()) )
	{
		getZipCodeAddress().delete();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getFeederAddress() 
{
	if( feederAddress == null )
	{
		feederAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_FEEDER );

		getFeederAddress().setAddressID( getLMGroupExpressComm().getFeederID() );
	}

	return feederAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getGeoAddress() 
{
	if( geoAddress == null )
	{
		geoAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_GEO );

		getGeoAddress().setAddressID( getLMGroupExpressComm().getGeoID() );
	}

	return geoAddress;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.lm.LMGroupExpressCom
 */
public com.cannontech.database.db.device.lm.LMGroupExpressCom getLMGroupExpressComm() 
{
	if( lmGroupExpressComm == null )
		lmGroupExpressComm = new com.cannontech.database.db.device.lm.LMGroupExpressCom();

	return lmGroupExpressComm;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getProgramAddress() 
{
	if( programAddress == null )
	{
		programAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_PROGRAM );

		getProgramAddress().setAddressID( getLMGroupExpressComm().getProgramID() );
	}

	return programAddress;
}

public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getSplinterAddress() 
{
	if( splinterAddress == null )
	{
		splinterAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_SPLINTER );

		getSplinterAddress().setAddressID( getLMGroupExpressComm().getSplinterID() );
	}

	return splinterAddress;
}

public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getUserAddress() 
{
	if( userAddress == null )
	{
		userAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_USER );

		getUserAddress().setAddressID( getLMGroupExpressComm().getUserID() );
	}

	return userAddress;
}

public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getZipCodeAddress() 
{
	if( zipCodeAddress == null )
	{
		zipCodeAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_ZIP );

		getZipCodeAddress().setAddressID( getLMGroupExpressComm().getZipID() );
	}

	return zipCodeAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getServiceProviderAddress() 
{
	if( serviceProviderAddress == null )
	{
		serviceProviderAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_SERVICE );

		getServiceProviderAddress().setAddressID( getLMGroupExpressComm().getServiceProviderID() );	
	}

	return serviceProviderAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public com.cannontech.database.db.device.lm.LMGroupExpressComAddress getSubstationAddress() 
{
	if( substationAddress == null )
	{
		substationAddress = new LMGroupExpressComAddress( IlmDefines.TYPE_SUBSTATION );

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
	
	if( !getLMGroupExpressComm().getServiceProviderID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getServiceProviderAddress().setAddressID( getLMGroupExpressComm().getServiceProviderID() );
		getServiceProviderAddress().retrieve();
	}

	if( !getLMGroupExpressComm().getSubstationID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getSubstationAddress().setAddressID( getLMGroupExpressComm().getSubstationID() );
		getSubstationAddress().retrieve();
	}

	if( !getLMGroupExpressComm().getFeederID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getFeederAddress().setAddressID( getLMGroupExpressComm().getFeederID() );
		getFeederAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getGeoID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getGeoAddress().setAddressID( getLMGroupExpressComm().getGeoID() );
		getGeoAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getProgramID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getProgramAddress().setAddressID( getLMGroupExpressComm().getProgramID() );
		getProgramAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getSplinterID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getSplinterAddress().setAddressID( getLMGroupExpressComm().getSplinterID() );
		getSplinterAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getUserID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getUserAddress().setAddressID( getLMGroupExpressComm().getUserID() );
		getUserAddress().retrieve();
	}
	
	if( !getLMGroupExpressComm().getZipID().equals(IlmDefines.NONE_ADDRESS_ID) )
	{
		getZipCodeAddress().setAddressID( getLMGroupExpressComm().getZipID() );
		getZipCodeAddress().retrieve();
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
	getSplinterAddress().setDbConnection(conn);
	getUserAddress().setDbConnection(conn);
	getZipCodeAddress().setDbConnection(conn);
	
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
 * @param newFeederAddress com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public void setFeederAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newFeederAddress) 
{
	feederAddress = newFeederAddress;

	getLMGroupExpressComm().setFeederID( getFeederAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newGeoAddress com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public void setGeoAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newGeoAddress) 
{
	geoAddress = newGeoAddress;

	getLMGroupExpressComm().setGeoID( getGeoAddress().getAddressID() );

}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.lm.lmGroupExpressComm
 */
public void setLMGroupExpressComm(com.cannontech.database.db.device.lm.LMGroupExpressCom newValue)
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
 * @param newProgramAddress com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public void setProgramAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newProgramAddress) 
{
	programAddress = newProgramAddress;

	getLMGroupExpressComm().setProgramID( getProgramAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newServiceProviderAddress com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public void setServiceProviderAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newServiceProviderAddress) 
{
	serviceProviderAddress = newServiceProviderAddress;

	getLMGroupExpressComm().setServiceProviderID( getServiceProviderAddress().getAddressID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2002 10:51:39 AM)
 * @param newSubstationAddress com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 */
public void setSubstationAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newSubstationAddress) 
{
	substationAddress = newSubstationAddress;

	getLMGroupExpressComm().setSubstationID( getSubstationAddress().getAddressID() );
}

public void setSplinterAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newSplinterAddress) 
{
	splinterAddress = newSplinterAddress;

	getLMGroupExpressComm().setSplinterID( getSplinterAddress().getAddressID() );
}

public void setUserAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newUserAddress) 
{
	userAddress = newUserAddress;

	getLMGroupExpressComm().setUserID( getUserAddress().getAddressID() );
}

public void setZipCodeAddress(com.cannontech.database.db.device.lm.LMGroupExpressComAddress newZipCodeAddress) 
{
	zipCodeAddress = newZipCodeAddress;

	getLMGroupExpressComm().setZipID( getZipCodeAddress().getAddressID() );
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
	
	if( getSplinterAddress().getAddressID() == null )
		getSplinterAddress().add();
	else
		getSplinterAddress().update();
	if( getLMGroupExpressComm().getSplinterID() == null )
		getLMGroupExpressComm().setSplinterID( getSplinterAddress().getAddressID() );
	
	if( getUserAddress().getAddressID() == null )
		getUserAddress().add();
	else
		getUserAddress().update();	
	if( getLMGroupExpressComm().getUserID() == null )
		getLMGroupExpressComm().setUserID( getUserAddress().getAddressID() );
	
	if( getZipCodeAddress().getAddressID() == null )
		getZipCodeAddress().add();
	else
		getZipCodeAddress().update();	
	if( getLMGroupExpressComm().getZipID() == null )
		getLMGroupExpressComm().setZipID( getZipCodeAddress().getAddressID() );

	getLMGroupExpressComm().update();

	//remove all the unused addresses
	com.cannontech.database.db.device.lm.LMGroupExpressCom.purgeUnusedAddresses( getDbConnection() );
}
}
