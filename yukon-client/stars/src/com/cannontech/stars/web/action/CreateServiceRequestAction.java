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
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.*;
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();
			
			if (req.getParameter("OrderNo") != null)
				createOrder.setOrderNumber( req.getParameter("OrderNo") );
			createOrder.setDateReported( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("DateReported")) );
			
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
			StarsCustSelectionList companyList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.report.ServiceCompany.LISTNAME_SERVICECOMPANY );
			for (int i = 0; i < companyList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = companyList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == company.getEntryID()) {
					company.setContent( entry.getContent() );
					break;
				}
			}			
			createOrder.setServiceCompany( company );
			
			createOrder.setOrderedBy( req.getParameter("OrderedBy") );
			createOrder.setDescription( req.getParameter("Notes").replaceAll("\r\n", "<br>") );
            
            StarsCustSelectionList statusList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_SERVICESTATUS );
            for (int i = 0; i < statusList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = statusList.getStarsSelectionListEntry(i);
            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_SERVSTAT_TOBESCHED )) {
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
        	
            StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            if (createOrder.getOrderNumber() != null) {
            	if (ServerUtils.orderNumberExists( createOrder.getOrderNumber(), user.getEnergyCompanyID() )) {
	            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Track number already exists, please choose a different one") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
            	}
            }
            else {
            	String orderNo = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() ).getNextOrderNumber();
            	createOrder.setOrderNumber( orderNo );
            }
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            Hashtable selectionLists = energyCompany.getAllSelectionLists();
            
            com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            com.cannontech.database.db.stars.report.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();

            StarsServiceRequestFactory.setWorkOrderBase( workOrderDB, createOrder );
            workOrderDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            workOrder.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, workOrder );
            workOrder = (com.cannontech.database.data.stars.report.WorkOrderBase)transaction.execute();
            
            LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( workOrderDB );
            ArrayList workOrderList = energyCompany.getAllWorkOrders();
            synchronized (workOrderList) { workOrderList.add( liteOrder ); }
            accountInfo.getServiceRequestHistory().add( 0, new Integer(liteOrder.getOrderID()) );
            
            StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, selectionLists );
            StarsCreateServiceRequestResponse resp = new StarsCreateServiceRequestResponse();
            resp.setStarsServiceRequest( starsOrder );
            
            boolean newServiceCompany = true;
            if (accountInfo.getServiceCompanies() == null)
            	accountInfo.setServiceCompanies( new ArrayList() );
            for (int i = 0; i < accountInfo.getServiceCompanies().size(); i++) {
            	int companyID = ((Integer) accountInfo.getServiceCompanies().get(i)).intValue();
            	if (liteOrder.getServiceCompanyID() == companyID) {
            		newServiceCompany = false;
            		break;
            	}
            }
            
            if (newServiceCompany) {
            	LiteServiceCompany liteCompany = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
            	StarsServiceCompany starsCompany = StarsLiteFactory.createStarsServiceCompany( liteCompany, energyCompanyID );
            	resp.setStarsServiceCompany( starsCompany );
            	ServerUtils.updateServiceCompanies( accountInfo, energyCompanyID );
            }

            respOper.setStarsCreateServiceRequestResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the service request") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
				
			// Serivce request history must already be retrieved before, e.g. when the account info is loaded
			StarsCreateServiceRequestResponse resp = operation.getStarsCreateServiceRequestResponse();
			accountInfo.getStarsServiceRequestHistory().addStarsServiceRequest( 0, resp.getStarsServiceRequest() );
			
			StarsServiceCompany company = resp.getStarsServiceCompany();
			if (company != null)
				accountInfo.getStarsServiceCompanies().addStarsServiceCompany( company );

            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
