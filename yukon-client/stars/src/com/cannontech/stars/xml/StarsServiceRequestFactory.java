package com.cannontech.stars.xml;

import com.cannontech.stars.xml.serialize.StarsSrvReq;
import com.cannontech.database.db.stars.report.WorkOrderBase;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsServiceRequestFactory {

	public static StarsSrvReq newStarsServiceRequest(StarsSrvReq order, Class type) {
		try {
			StarsSrvReq starsOrder = (StarsSrvReq) type.newInstance();
			
			starsOrder.setOrderNumber( order.getOrderNumber() );
			starsOrder.setServiceType( order.getServiceType() );
			starsOrder.setDateReported( order.getDateReported() );
			starsOrder.setServiceCompany( order.getServiceCompany() );
			starsOrder.setOrderedBy( order.getOrderedBy() );
			starsOrder.setDescription( order.getDescription() );
			starsOrder.setCurrentState( order.getCurrentState() );
			starsOrder.setDateScheduled( order.getDateScheduled() );
			starsOrder.setDateCompleted( order.getDateCompleted() );
			starsOrder.setActionTaken( order.getActionTaken() );
			
			return starsOrder;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void setWorkOrderBase(WorkOrderBase orderDB, StarsSrvReq order) {
		//orderDB.setOrderID( new Integer(order.getOrderID()) );
		orderDB.setOrderNumber( order.getOrderNumber() );
		orderDB.setWorkTypeID( new Integer(order.getServiceType().getEntryID()) );
		orderDB.setDateReported( order.getDateReported() );
		orderDB.setServiceCompanyID( new Integer(order.getServiceCompany().getEntryID()) );
		orderDB.setOrderedBy( order.getOrderedBy() );
		orderDB.setDescription( order.getDescription() );
		
		if (order.getCurrentState() != null)
			orderDB.setCurrentStateID( new Integer(order.getCurrentState().getEntryID()) );
		if (order.getDateScheduled() != null)
			orderDB.setDateScheduled( order.getDateScheduled() );
		if (order.getDateCompleted() != null)
			orderDB.setDateCompleted( order.getDateCompleted() );
		if (order.getActionTaken() != null)
			orderDB.setActionTaken( order.getActionTaken() );
	}
}
