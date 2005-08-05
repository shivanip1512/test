package com.cannontech.database.data.pao;

import com.cannontech.database.cache.functions.PAOFuncs;

/**
 * Insert the type's description here.
 * Creation date: (9/12/2001 2:49:48 PM)
 * @author: 
 */

public final class PAOFactory 
{
/**
 * PAOFactory constructor comment.
 */
private PAOFactory() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 2:51:30 PM)
 * @param paoType java.lang.String
 */
public static YukonPAObject createPAObject( com.cannontech.database.data.lite.LiteYukonPAObject litePAObject )
{
	YukonPAObject returnObject = null;

	//decide what kind of PAObject to create by the category
	String liteCategory = PAOGroups.getCategory( litePAObject.getCategory() );

	if( liteCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_DEVICE) )
	{
		returnObject = com.cannontech.database.data.device.DeviceFactory.createDevice( litePAObject.getType() );
		((com.cannontech.database.data.device.DeviceBase)returnObject).setDeviceID( new Integer( litePAObject.getYukonID() ) );
		returnObject.setPAOName( litePAObject.getPaoName() );
	}
	else if( liteCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_LOADMANAGEMENT) )
	{
		returnObject = com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( litePAObject.getType() );
		
		if( returnObject instanceof com.cannontech.database.data.device.DeviceBase )
			((com.cannontech.database.data.device.DeviceBase)returnObject).setDeviceID( new Integer( litePAObject.getYukonID() ) );
		else
			returnObject.setPAObjectID( new Integer( litePAObject.getYukonID() ) );
		
		returnObject.setPAOName( litePAObject.getPaoName() );
	}
	else if( liteCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_PORT) )
	{
		returnObject = com.cannontech.database.data.port.PortFactory.createPort( litePAObject.getType() );
		((com.cannontech.database.data.port.DirectPort)returnObject).setPortID( new Integer( litePAObject.getYukonID() ) );
		((com.cannontech.database.data.port.DirectPort)returnObject).setPortName( litePAObject.getPaoName() );
	}
	else if( liteCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_ROUTE) )
	{
		returnObject = com.cannontech.database.data.route.RouteFactory.createRoute( litePAObject.getType() );
		((com.cannontech.database.data.route.RouteBase)returnObject).setRouteID( new Integer( litePAObject.getYukonID() ) );
		((com.cannontech.database.data.route.RouteBase)returnObject).setRouteName( litePAObject.getPaoName() );
	}
	else if( liteCategory.equalsIgnoreCase(PAOGroups.STRING_CAT_CAPCONTROL) )
	{
		returnObject = com.cannontech.database.data.capcontrol.CCYukonPAOFactory.createCapControlPAO( litePAObject.getType() );
		((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setCapControlPAOID( new Integer( litePAObject.getYukonID() ) );
		((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setName( litePAObject.getPaoName() );
	}
	else
		returnObject = null;

		
	//our PAO fields are set inside each factory
	//returnObject.setPAOCategory( litePAObject.getCategory() );
	//returnObject.setPAOClass( litePAObject.getPaoClass() );
	//returnObject.setPAOType( litePAObject.getType() );
	
	return returnObject;
}


/**
 * Creates a fat PAObject with a given PAOid
 */
public static YukonPAObject createPAObject( int paoID )
{
	return createPAObject( PAOFuncs.getLiteYukonPAO(paoID) );
}

}