package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCallReportFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateServiceRequestAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			String[] changed = req.getParameterValues( "changed" );
			String[] orderIDs = req.getParameterValues( "OrderID" );
			String[] orderNos = req.getParameterValues( "OrderNo" );
			String[] servTypes = req.getParameterValues( "ServiceType" );
			String[] status = req.getParameterValues( "Status" );
			String[] companies = req.getParameterValues( "ServiceCompany" );
			String[] descriptions = req.getParameterValues( "Description" );
			
			StarsUpdateServiceRequest updateOrders = new StarsUpdateServiceRequest();
			for (int i = 0; i < changed.length; i++) {
				if (changed[i].equals("true")) {
					StarsServiceRequest order = new StarsServiceRequest();
					order.setOrderID( Integer.parseInt(orderIDs[i]) );
					
					if (orderNos != null) order.setOrderNumber( orderNos[i] );
					if (descriptions != null) order.setDescription( descriptions[i].replaceAll("\r\n", "<br>") );
					
					if (servTypes != null) {
						ServiceType servType = new ServiceType();
						servType.setEntryID( Integer.parseInt(servTypes[i]) );
						StarsCustSelectionList servTypeList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICETYPE );
						for (int j = 0; j < servTypeList.getStarsSelectionListEntryCount(); j++) {
							StarsSelectionListEntry entry = servTypeList.getStarsSelectionListEntry(j);
							if (entry.getEntryID() == servType.getEntryID()) {
								servType.setContent( entry.getContent() );
								break;
							}
						}
						order.setServiceType( servType );
					}
					
					if (status != null) {
						CurrentState state = new CurrentState();
						state.setEntryID( Integer.parseInt(status[i]) );
						StarsCustSelectionList statusList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICESTATUS );
						for (int j = 0; j < statusList.getStarsSelectionListEntryCount(); j++) {
							StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(j);
							if (entry.getEntryID() == state.getEntryID()) {
								state.setContent( entry.getContent() );
								break;
							}
						}
						order.setCurrentState( state );
					}
					
					if (companies != null) {
						ServiceCompany company = new ServiceCompany();
						company.setEntryID( Integer.parseInt(companies[i]) );
						StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
						for (int j = 0; j < companyList.getStarsSelectionListEntryCount(); j++) {
							StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(j);
							if (entry.getEntryID() == company.getEntryID()) {
								company.setContent( entry.getContent() );
								break;
							}
						}
						order.setServiceCompany( company );
					}
					
					updateOrders.addStarsServiceRequest( order );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateServiceRequest( updateOrders );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
        	ArrayList orderHist = accountInfo.getServiceRequestHistory();
        	StarsUpdateServiceRequest updateOrders = reqOper.getStarsUpdateServiceRequest();
        	StarsUpdateServiceRequestResponse resp = new StarsUpdateServiceRequestResponse();
        	
        	for (int i = 0; i < updateOrders.getStarsServiceRequestCount(); i++) {
        		StarsServiceRequest newOrder = updateOrders.getStarsServiceRequest(i);
        		for (int j = 0; j < orderHist.size(); j++) {
        			int orderID = ((Integer) orderHist.get(j)).intValue();
        			if (orderID == newOrder.getOrderID()) {
        				LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( orderID );
        				
        				if (newOrder.getServiceType() != null)
        					liteOrder.setWorkTypeID( newOrder.getServiceType().getEntryID() );
        				if (newOrder.getCurrentState() != null)
        					liteOrder.setCurrentStateID( newOrder.getCurrentState().getEntryID() );
        				if (newOrder.getServiceCompany() != null)
        					liteOrder.setServiceCompanyID( newOrder.getServiceCompany().getEntryID() );
        				if (newOrder.getDescription() != null)
        					liteOrder.setDescription( newOrder.getDescription() );
        				
        				if (newOrder.getOrderNumber() != null) {
        					if (!liteOrder.getOrderNumber().equals( newOrder.getOrderNumber() )
        						&& ServerUtils.orderNumberExists( newOrder.getOrderNumber(), user.getEnergyCompanyID() )) {
				            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
				            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order number already exists, please choose a different one") );
				            	return SOAPUtil.buildSOAPMessage( respOper );
        					}
        					liteOrder.setOrderNumber( newOrder.getOrderNumber() );
        				}
        				
        				com.cannontech.database.db.stars.report.WorkOrderBase orderDB =
        						(com.cannontech.database.db.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
						Transaction.createTransaction(Transaction.UPDATE, orderDB).execute();
			            
			            boolean newServiceCompany = true;
			            if (accountInfo.getServiceCompanies() == null)
			            	accountInfo.setServiceCompanies( new ArrayList() );
			            for (int k = 0; k < accountInfo.getServiceCompanies().size(); k++) {
			            	int companyID = ((Integer) accountInfo.getServiceCompanies().get(k)).intValue();
			            	if (liteOrder.getServiceCompanyID() == companyID) {
			            		newServiceCompany = false;
			            		break;
			            	}
			            }
			            
			            if (newServiceCompany) {
			            	LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
			            	StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, user.getEnergyCompanyID() );
			            	resp.addStarsServiceCompany( starsCompany );
			            	ServerUtils.updateServiceCompanies( accountInfo, user.getEnergyCompanyID() );
			            }
			            
        				break;
        			}
        		}
        	}
            
            respOper.setStarsUpdateServiceRequestResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the service requests") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }
        
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateServiceRequest updateOrders = reqOper.getStarsUpdateServiceRequest();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsServiceRequestHistory orderHist = accountInfo.getStarsServiceRequestHistory();
			
			for (int i = 0; i < updateOrders.getStarsServiceRequestCount(); i++) {
				StarsServiceRequest newOrder = updateOrders.getStarsServiceRequest(i);
				for (int j = 0; j < orderHist.getStarsServiceRequestCount(); j++) {
					StarsServiceRequest order = orderHist.getStarsServiceRequest(j);
					if (order.getOrderID() == newOrder.getOrderID()) {
						if (newOrder.getOrderNumber() != null) order.setOrderNumber( newOrder.getOrderNumber() );
						if (newOrder.getServiceType() != null) order.setServiceType( newOrder.getServiceType() );
						if (newOrder.getCurrentState() != null) order.setCurrentState( newOrder.getCurrentState() );
						if (newOrder.getServiceCompany() != null) order.setServiceCompany( newOrder.getServiceCompany() );
						if (newOrder.getDescription() != null) order.setDescription( newOrder.getDescription() );
						System.out.println( "*** Description: " + newOrder.getDescription() );
						System.out.println( "### Description: " + order.getDescription() );
						break;
					}
				}
			}
			
			StarsUpdateServiceRequestResponse resp = operation.getStarsUpdateServiceRequestResponse();
			for (int i = 0; i < resp.getStarsServiceCompanyCount(); i++)
				accountInfo.getStarsServiceCompanies().addStarsServiceCompany( resp.getStarsServiceCompany(i) );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
