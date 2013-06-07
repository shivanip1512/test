package com.cannontech.database.data.pao;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CCYukonPAOFactory;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.spring.YukonSpringHook;

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
	if( litePAObject == null ) return null;
	
	YukonPAObject returnObject = null;
	
	//decide what kind of PAObject to create by the category
	PaoCategory paoCategory = litePAObject.getPaoType().getPaoCategory();
	if (paoCategory == PaoCategory.DEVICE)
	{
		returnObject = DeviceFactory.createDevice(litePAObject.getPaoType());
		((com.cannontech.database.data.device.DeviceBase)returnObject).setDeviceID( new Integer( litePAObject.getYukonId() ) );
		returnObject.setPAOName( litePAObject.getPaoName() );
	}
	else if (paoCategory == PaoCategory.LOADMANAGEMENT)
	{
		returnObject = LMFactory.createLoadManagement(litePAObject.getPaoType().getDeviceTypeId());
		
		if( returnObject instanceof com.cannontech.database.data.device.DeviceBase )
			((com.cannontech.database.data.device.DeviceBase)returnObject).setDeviceID( new Integer( litePAObject.getYukonId() ) );
		else
			returnObject.setPAObjectID( new Integer( litePAObject.getYukonId() ) );
		
		returnObject.setPAOName( litePAObject.getPaoName() );
	}
	else if (paoCategory == PaoCategory.PORT)
	{
		returnObject = PortFactory.createPort(litePAObject.getPaoType().getDeviceTypeId());
		((com.cannontech.database.data.port.DirectPort)returnObject).setPortID( new Integer( litePAObject.getYukonId() ) );
		((com.cannontech.database.data.port.DirectPort)returnObject).setPortName( litePAObject.getPaoName() );
	}
	else if (paoCategory == PaoCategory.ROUTE)
	{
		returnObject = RouteFactory.createRoute(litePAObject.getPaoType().getDeviceTypeId());
		((com.cannontech.database.data.route.RouteBase)returnObject).setRouteID( new Integer( litePAObject.getYukonId() ) );
		((com.cannontech.database.data.route.RouteBase)returnObject).setRouteName( litePAObject.getPaoName() );
	}
	else if (paoCategory == PaoCategory.CAPCONTROL)
	{
		returnObject = CCYukonPAOFactory.createCapControlPAO(litePAObject.getPaoType().getDeviceTypeId());
		((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setCapControlPAOID( new Integer( litePAObject.getYukonId() ) );
		((com.cannontech.database.data.capcontrol.CapControlYukonPAOBase)returnObject).setName( litePAObject.getPaoName() );
	}
	else
		returnObject = null;
		
	return returnObject;
}


/**
 * Creates a fat PAObject with a given PAOid
 */
public static YukonPAObject createPAObject( int paoID )
{
	return createPAObject( YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(paoID) );
}

}