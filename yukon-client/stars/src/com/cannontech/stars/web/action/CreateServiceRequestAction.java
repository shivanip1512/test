package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.StarsServiceRequestFactory;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: CreateServiceRequestAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 7, 2002 1:10:51 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CreateServiceRequestAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator == null) return null;
			Hashtable selectionLists = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );

			StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();

			createOrder.setOrderNumber( req.getParameter("OrderNumber") );
			createOrder.setDateReported( new Date() );
			
			ServiceType servType = new ServiceType();
			servType.setEntryID( Integer.parseInt(req.getParameter("ServiceType")) );
			StarsCustSelectionList servTypeList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICETYPE );
			for (int i = 0; i < servTypeList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = servTypeList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == servType.getEntryID()) {
					servType.setContent( entry.getContent() );
					break;
				}
			}
			createOrder.setServiceType( servType );
			
			ServiceCompany company = new ServiceCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICECOMPANY );
			for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == company.getEntryID()) {
					company.setContent( entry.getContent() );
					break;
				}
			}			
			createOrder.setServiceCompany( company );
			
			createOrder.setOrderedBy( req.getParameter("OrderedBy") );
			createOrder.setDescription( req.getParameter("Notes") );
            
            StarsCustSelectionList statusList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICESTATUS );
            for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
            	if (entry.getContent().equalsIgnoreCase( "To be scheduled" )) {
            		CurrentState status = (CurrentState) StarsCustListEntryFactory.newStarsCustListEntry( entry, CurrentState.class );
            		createOrder.setCurrentState( status );
            		break;
            	}
            }
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateServiceRequest( createOrder );
			
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
            		
            StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            com.cannontech.database.db.stars.report.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();

            StarsServiceRequestFactory.setWorkOrderBase( workOrderDB, createOrder );
            workOrderDB.setCustomerID( account.getCustomerBase().getCustomerBase().getCustomerID() );
            workOrderDB.setSiteID( account.getAccountSite().getAccountSite().getAccountSiteID() );            

            workOrder.setCustomerBase( account.getCustomerBase() );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, workOrder );
            workOrder = (com.cannontech.database.data.stars.report.WorkOrderBase)transaction.execute();
            
            StarsSuccess success = new StarsSuccess();
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
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
			
            if (operation.getStarsSuccess() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
			StarsServiceRequestHistory orderHist = (StarsServiceRequestHistory) StarsServiceRequestFactory.newStarsServiceRequest( createOrder, StarsServiceRequestHistory.class );
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsGetServiceRequestHistoryResponse orderHists = (StarsGetServiceRequestHistoryResponse) operator.getAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "SERVICE_HISTORY" );
			if (orderHists == null) {
				orderHists = new StarsGetServiceRequestHistoryResponse();
				operator.setAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "SERVICE_HISTORY", orderHists );
			}
			orderHists.addStarsServiceRequestHistory( 0, orderHist );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
