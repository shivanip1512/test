package com.cannontech.database.data.route;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.pao.RouteTypes;

public final class RouteFactory 
{
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.route.RouteBase
 * @param routeType int
 */
public final static RouteBase createRoute(int routeType) 
{
 
	RouteBase returnRoute = null;

	switch( routeType )
	{
		case RouteTypes.ROUTE_CCU:
			returnRoute = new CCURoute();
			returnRoute.setRouteType( RouteTypes.STRING_CCU );
			break;
		case RouteTypes.ROUTE_TCU:
			returnRoute = new TCURoute();
			returnRoute.setRouteType( RouteTypes.STRING_TCU );
			break;
		case RouteTypes.ROUTE_LCU:
			returnRoute = new LCURoute();
			returnRoute.setRouteType( RouteTypes.STRING_LCU );
			break;
		case RouteTypes.ROUTE_MACRO:
			returnRoute = new MacroRoute();
			returnRoute.setRouteType( RouteTypes.STRING_MACRO );
			returnRoute.setDeviceID( new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) );
			returnRoute.setDefaultRoute("N");
			break;
		case RouteTypes.ROUTE_VERSACOM:
			returnRoute = new VersacomRoute();
			returnRoute.setRouteType( RouteTypes.STRING_VERSACOM );
			break;
		case RouteTypes.ROUTE_TAP_PAGING:
			returnRoute = new TapPagingRoute();
			returnRoute.setRouteType( RouteTypes.STRING_TAP_PAGING );
			break;
		case RouteTypes.ROUTE_WCTP_TERMINAL:
			returnRoute = new WCTPTerminalRoute();
			returnRoute.setRouteType( RouteTypes.STRING_WCTP_TERMINAL_ROUTE );
			break;
		case RouteTypes.ROUTE_SERIES_5_LMI:
			returnRoute = new Series5LMIRoute();
			returnRoute.setRouteType( RouteTypes.STRING_SERIES_5_LMI_ROUTE );
			break;
		case RouteTypes.ROUTE_RTC:
			returnRoute = new RTCRoute();
			returnRoute.setRouteType( RouteTypes.STRING_RTC_ROUTE );
			break;
		default:

	}

	returnRoute.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_ROUTE );
	returnRoute.setPAOClass( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_ROUTE );
	return returnRoute;	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.route.RouteBase
 * @param routeType int
 */
public final static RouteBase createRoute( String routeType) 
{
 
	RouteBase returnRoute = null;

	if( routeType.equalsIgnoreCase(RouteTypes.STRING_CCU) )
	{
		returnRoute = new CCURoute();
		returnRoute.setRouteType( RouteTypes.STRING_CCU );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_TCU) )
	{
		returnRoute = new TCURoute();
		returnRoute.setRouteType( RouteTypes.STRING_TCU );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_LCU) )
	{
		returnRoute = new LCURoute();
		returnRoute.setRouteType( RouteTypes.STRING_LCU );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_MACRO) )
	{
		returnRoute = new MacroRoute();
		returnRoute.setRouteType( RouteTypes.STRING_MACRO );
		returnRoute.setDeviceID( new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) );
		returnRoute.setDefaultRoute("N");		
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_VERSACOM) )
	{	
		returnRoute = new VersacomRoute();
		returnRoute.setRouteType( RouteTypes.STRING_VERSACOM );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_TAP_PAGING) )
	{
		returnRoute = new TapPagingRoute();
		returnRoute.setRouteType( RouteTypes.STRING_TAP_PAGING );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_WCTP_TERMINAL_ROUTE) )
	{
		returnRoute = new WCTPTerminalRoute();
		returnRoute.setRouteType( RouteTypes.STRING_WCTP_TERMINAL_ROUTE );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_SERIES_5_LMI_ROUTE) )
	{
		returnRoute = new Series5LMIRoute();
		returnRoute.setRouteType( RouteTypes.STRING_SERIES_5_LMI_ROUTE );
	}
	else if( routeType.equalsIgnoreCase(RouteTypes.STRING_RTC_ROUTE) )
	{
		returnRoute = new RTCRoute();
		returnRoute.setRouteType( RouteTypes.STRING_RTC_ROUTE );
	}
	else
		throw new IllegalStateException( "*** " + routeType + " is not a valid route type." );


	returnRoute.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_ROUTE );
	returnRoute.setPAOClass( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_ROUTE );
	return returnRoute;	
}
}
