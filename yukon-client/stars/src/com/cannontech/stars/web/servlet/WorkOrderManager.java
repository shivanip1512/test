/*
 * Created on Oct 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.UpdateServiceRequestAction;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSrvReq;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderManager extends HttpServlet {
	
	public static final String STARS_WORK_ORDER_OPER_REQ = "STARS_WORK_ORDER_OPERATION_REQUEST";
	
	public static final String WORK_ORDER_SET = "WORK_ORDER_SET";
	public static final String WORK_ORDER_SET_DESC = "WORK_ORDER_SET_DESC";
	
	private String referer = null;
	private String redirect = null;

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL ); return;
		}
    	
		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
    	
		referer = req.getHeader( "referer" );
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
		
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("SearchWorkOrder"))
			searchWorkOrder( user, req, session );
		else if (action.equalsIgnoreCase("CreateWorkOrder")) {
			redirect = req.getContextPath() + "/servlet/SOAPClient?action=" + action +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			preCreateWorkOrder( user, req, session );
		}
		else if (action.equalsIgnoreCase("SearchCustAccount")) {
			redirect = req.getContextPath() + "/servlet/SOAPClient?action=UpdateWorkOrder" +
					"&REDIRECT=" + req.getParameter(ServletUtils.ATT_REDIRECT) +
					"&REFERRER=" + req.getParameter(ServletUtils.ATT_REFERRER);
			searchCustAccount( user, req, session );
		}
		
		resp.sendRedirect( redirect );
	}
	
	private void searchWorkOrder(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		session.removeAttribute( WORK_ORDER_SET );
		
		int searchBy = Integer.parseInt( req.getParameter("SearchBy") );
		String searchValue = req.getParameter( "SearchValue" );
		if (searchValue.trim().length() == 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
			return;
		}
		
		int[] orderIDs = null; 
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO) {
			if (AuthFuncs.checkRoleProperty(user.getYukonUser(), ConsumerInfoRole.ORDER_NUMBER_AUTO_GEN))
				searchValue = ServerUtils.AUTO_GEN_NUM_PREC + searchValue;
			orderIDs = WorkOrderBase.searchByOrderNumber( searchValue, user.getEnergyCompanyID() );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
			int[] acctIDs = CustomerAccount.searchByAccountNumber(energyCompany.getEnergyCompanyID(), searchValue);
			if (acctIDs != null && acctIDs.length > 0)
				orderIDs = WorkOrderBase.searchByAccountID( acctIDs[0] ); 
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
			int[] acctIDs = CustomerAccount.searchByPhoneNumber( energyCompany.getEnergyCompanyID(), searchValue );
			if (acctIDs != null && acctIDs.length > 0) {
				ArrayList orderIDList = new ArrayList();
				for (int i = 0; i < acctIDs.length; i++) {
					int[] woIDs = WorkOrderBase.searchByAccountID( acctIDs[i] );
					for (int j = 0; j < woIDs.length; j++)
						orderIDList.add( new Integer(woIDs[j]) );
				}
				
				orderIDs = new int[ orderIDList.size() ];
				for (int i = 0; i< orderIDList.size(); i++)
					orderIDs[i] = ((Integer) orderIDList.get(i)).intValue();
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME) {
			int[] acctIDs = CustomerAccount.searchByLastName(energyCompany.getEnergyCompanyID(), searchValue);
			if (acctIDs != null && acctIDs.length > 0) {
				ArrayList orderIDList = new ArrayList();
				for (int i = 0; i < acctIDs.length; i++) {
					int[] woIDs = WorkOrderBase.searchByAccountID( acctIDs[i] );
					for (int j = 0; j < woIDs.length; j++)
						orderIDList.add( new Integer(woIDs[j]) );
				}
				
				orderIDs = new int[ orderIDList.size() ];
				for (int i = 0; i< orderIDList.size(); i++)
					orderIDs[i] = ((Integer) orderIDList.get(i)).intValue();
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
			// TODO: The WorkOrderBase table doesn't have InventoryID column, maybe should be added
		}
		
		if (orderIDs == null || orderIDs.length == 0) {
			session.setAttribute(WORK_ORDER_SET_DESC, "<div class='ErrorMsg' align='center'>No service order found matching the search criteria.</div>");
		}
		else if (orderIDs.length == 1) {
			redirect = req.getContextPath() + "/operator/WorkOrder/WorkOrder.jsp?OrderId=" + orderIDs[0];
		}
		else {
			ArrayList soList = new ArrayList();
			for (int i = 0; i < orderIDs.length; i++)
				soList.add( energyCompany.getWorkOrderBase(orderIDs[i], true) );
			session.setAttribute(WORK_ORDER_SET, soList);
			session.setAttribute(WORK_ORDER_SET_DESC, "Click on a Order # to view the service order details.");
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		}
	}
	
	private void preCreateWorkOrder(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsOperation operation = null;
		try {
			operation = CreateServiceRequestAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
		}
		catch (WebClientException se) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, se.getMessage());
			redirect = referer;
			return;
		}
		
		if (req.getParameter("AcctNo").trim().length() > 0) {
			LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( req.getParameter("AcctNo") );
			if (liteAcctInfo == null) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified account # doesn't exist");
				redirect = referer;
				return;
			}
			
			operation.getStarsCreateServiceRequest().setAccountID( liteAcctInfo.getAccountID() );
		}
		else
			operation.getStarsCreateServiceRequest().setAccountID( CtiUtilities.NONE_ID );
		
		session.setAttribute(STARS_WORK_ORDER_OPER_REQ, operation);
	}
	
	private void searchCustAccount(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( req.getParameter("AcctNo") );
		if (liteAcctInfo == null) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified account # doesn't exist");
			redirect = referer;
			return;
		}
		
		StarsOperation operation = null;
		try {
			operation = UpdateServiceRequestAction.getRequestOperation(req, energyCompany.getDefaultTimeZone());
		}
		catch (WebClientException se) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, se.getMessage());
			redirect = referer;
			return;
		}
		
		operation.getStarsUpdateServiceRequest().setAccountID( liteAcctInfo.getAccountID() );
		session.setAttribute(STARS_WORK_ORDER_OPER_REQ, operation);
	}
	
	public static void setStarsServiceRequest(StarsSrvReq starsOrder, HttpServletRequest req, java.util.TimeZone tz)
		throws WebClientException
	{
		if (req.getParameter("OrderID") != null)
			starsOrder.setOrderID( Integer.parseInt(req.getParameter("OrderID")) );
		if (req.getParameter("AccountID") != null)
			starsOrder.setAccountID( Integer.parseInt(req.getParameter("AccountID")) );
		if (req.getParameter("OrderNo") != null)
			starsOrder.setOrderNumber( req.getParameter("OrderNo") );
		if (req.getParameter("ActionTaken") != null)
			starsOrder.setActionTaken( req.getParameter("ActionTaken").replaceAll(System.getProperty("line.separator"), "<br>") );
		starsOrder.setOrderedBy( req.getParameter("OrderedBy") );
		starsOrder.setDescription( req.getParameter("Description").replaceAll(System.getProperty("line.separator"), "<br>") );
			
		ServiceType servType = new ServiceType();
		servType.setEntryID( Integer.parseInt(req.getParameter("ServiceType")) );
		starsOrder.setServiceType( servType );
			
		ServiceCompany company = new ServiceCompany();
		company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany") ) );
		starsOrder.setServiceCompany( company );
		
		if (req.getParameter("CurrentState") != null) {
			CurrentState status = new CurrentState();
			status.setEntryID( Integer.parseInt(req.getParameter("CurrentState")) );
			starsOrder.setCurrentState( status );
		}
		
		if (req.getParameter("DateReported").length() > 0) {
			Date dateReported = ServletUtils.parseDateTime(
					req.getParameter("DateReported"), req.getParameter("TimeReported"), tz );
			if (dateReported == null)
				throw new WebClientException("Invalid report date format '" + req.getParameter("DateReported") + " " + req.getParameter("TimeReported") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateReported( dateReported );
		}
		
		if (req.getParameter("DateScheduled") != null && req.getParameter("DateScheduled").length() > 0) {
			Date dateScheduled = ServletUtils.parseDateTime(
					req.getParameter("DateScheduled"), req.getParameter("TimeScheduled"), tz );
			if (dateScheduled == null)
				throw new WebClientException("Invalid scheduled date format '" + req.getParameter("DateScheduled") + " " + req.getParameter("TimeScheduled") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateScheduled( dateScheduled );
		}
		
		if (req.getParameter("DateCompleted") != null && req.getParameter("DateCompleted").length() > 0) {
			Date dateCompleted = ServletUtils.parseDateTime(
					req.getParameter("DateCompleted"), req.getParameter("TimeCompleted"), tz );
			if (dateCompleted == null)
				throw new WebClientException("Invalid close date format '" + req.getParameter("DateCompleted") + " " + req.getParameter("TimeCompleted") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			starsOrder.setDateCompleted( dateCompleted );
		}
	}

}
