package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: GetServiceRequestHistoryAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 7, 2002 5:52:47 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class GetServiceHistoryAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetServiceRequestHistory getServHist = new StarsGetServiceRequestHistory();
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetServiceRequestHistory( getServHist );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		java.sql.Connection conn = null;
		
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	StarsFailure failure = new StarsFailure();
            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
            	failure.setDescription( "Session invalidated, please login again" );
            	respOper.setStarsFailure( failure );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            com.cannontech.database.data.stars.customer.CustomerAccount account =
            		(com.cannontech.database.data.stars.customer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
            		com.cannontech.database.db.stars.report.WorkOrderBase.getAllSiteWorkOrders(
            			account.getAccountSite().getAccountSite().getAccountSiteID(), conn );
            if (orders == null) return null;
        	
        	Hashtable selectionLists = com.cannontech.stars.util.ServerUtils.getSelectionListTable(
        			new Integer((int) operator.getEnergyCompanyID()) );        			
        	StarsCustSelectionList servTypeList = (StarsCustSelectionList)
        			selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICETYPE );
        	StarsCustSelectionList statusList = (StarsCustSelectionList)
        			selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICESTATUS );
        	StarsCustSelectionList servCompanyList = (StarsCustSelectionList)
        			selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
            
            StarsGetServiceRequestHistoryResponse getServHistResp = new StarsGetServiceRequestHistoryResponse();
            for (int i = 0; i < orders.length; i++) {
            	StarsServiceRequestHistory servHist = new StarsServiceRequestHistory();
            	
            	for (int j = 0; j < servTypeList.getStarsSelectionListEntryCount(); j++) {
            		StarsSelectionListEntry entry = servTypeList.getStarsSelectionListEntry(j);
            		if (entry.getEntryID() == orders[i].getWorkTypeID().intValue()) {
            			ServiceType servType = (ServiceType) StarsCustListEntryFactory.newStarsCustListEntry( entry, ServiceType.class );
            			servHist.setServiceType( servType );
            			break;
            		}
            	}
            	
            	for (int j = 0; j < statusList.getStarsSelectionListEntryCount(); j++) {
            		StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(j);
            		if (entry.getEntryID() == orders[i].getCurrentStateID().intValue()) {
            			CurrentState status = (CurrentState) StarsCustListEntryFactory.newStarsCustListEntry( entry, CurrentState.class );
            			servHist.setCurrentState( status );
            			break;
            		}
            	}

            	for (int j = 0; j < servCompanyList.getStarsSelectionListEntryCount(); j++) {
            		StarsSelectionListEntry entry = servCompanyList.getStarsSelectionListEntry(j);
            		if (entry.getEntryID() == orders[i].getServiceCompanyID().intValue()) {
            			ServiceCompany servCompany = (ServiceCompany) StarsCustListEntryFactory.newStarsCustListEntry( entry, ServiceCompany.class );
            			servHist.setServiceCompany( servCompany );
            			break;
            		}
            	}
            	
            	servHist.setOrderNumber( orders[i].getOrderNumber().toString() );
            	servHist.setDateReported( orders[i].getDateReported() );
            	servHist.setOrderedBy( orders[i].getOrderedBy() );
            	servHist.setDescription( orders[i].getDescription() );
            	servHist.setDateScheduled( orders[i].getDateScheduled() );
            	servHist.setDateCompleted( orders[i].getDateCompleted() );
            	servHist.setActionTaken( orders[i].getActionTaken() );
            	
            	getServHistResp.addStarsServiceRequestHistory( servHist );
            }
            
            respOper.setStarsGetServiceRequestHistoryResponse( getServHistResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
            StarsFailure failure = operation.getStarsFailure();
            if (failure != null) return failure.getStatusCode();
            
            StarsGetServiceRequestHistoryResponse getServHistResp = operation.getStarsGetServiceRequestHistoryResponse();
            if (getServHistResp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            operator.setAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "SERVICE_HISTORY", getServHistResp);
            
			return 0;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
