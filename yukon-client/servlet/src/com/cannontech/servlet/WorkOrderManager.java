/*
 * Created on Oct 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.xml.utils.IntVector;
import org.jfree.report.JFreeReport;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ManipulateInventoryTask;
import com.cannontech.stars.util.task.ManipulateWorkOrderTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UpdateSNRangeTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.UpdateServiceRequestAction;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrderManager extends HttpServlet {
	
	private String lastFileID1 = "";
	private int lastFileID2 = 0;
	
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
    	
		java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
			String ele = enum1.nextElement().toString();
			 CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
		}

		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
    	
		referer = req.getParameter( ServletUtils.ATT_REFERRER );
		if (referer == null) referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		if (redirect == null) redirect = referer;
		
		// If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
		if (req.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, redirect );
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, referer );
			redirect = referer = req.getContextPath() + "/operator/Admin/Message.jsp";
		}
		
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
		else if (action.equalsIgnoreCase("SendWorkOrder"))
			sendWorkOrder( user, req, session );
        else if (action.equalsIgnoreCase("UpdateFilters"))
            updateFilters( user, req, session );
        else if (action.equalsIgnoreCase("ManipulateSelectedResults"))
        	manipulateSelectedResults( user, req, session );
        else if (action.equalsIgnoreCase("ManipulateResults"))
        	manipulateResults( user, req, session );
        else if (action.equalsIgnoreCase("ApplyActions"))
        	applyActions( user, req, session );		
        else if( action.equalsIgnoreCase("ViewAllResults"))
        	viewAllResults(req, session, true);
        else if( action.equalsIgnoreCase("HideAllResults"))
        	viewAllResults(req, session, false);

		resp.sendRedirect( redirect );
	}
	private void manipulateSelectedResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		
				java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
			String ele = enum1.nextElement().toString();
			 CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
		}


		String[] selections = req.getParameterValues("checkWorkOrder");
		int [] selectionIDs = new int[selections.length];
		for ( int i = 0; i < selections.length; i++)
			selectionIDs[i] = Integer.valueOf(selections[i]).intValue();
		
		WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
		ArrayList<LiteWorkOrderBase> liteWorkOrderList = new ArrayList<LiteWorkOrderBase>(); 
		for ( int i = 0; i < workOrderBean.getWorkOrderList().size(); i ++)
		{
			LiteWorkOrderBase liteWorkOrderBase = workOrderBean.getWorkOrderList().get(i);
			for (int j = 0; j < selectionIDs.length; j++)
			{
				if( liteWorkOrderBase.getOrderID() == selectionIDs[j])
				{
					liteWorkOrderList.add(liteWorkOrderBase);
					break;
				}
			}
		}
		workOrderBean.setWorkOrderList(liteWorkOrderList);
		
		manipulateResults(user, req, session);
		
	}
	private void applyActions(StarsYukonUser user, HttpServletRequest req, HttpSession session) 
    {

        
        String[] selectionIDs = req.getParameterValues("SelectionIDs");
        String[] actionTexts = req.getParameterValues("ActionTexts");
        String[] actionTypeIDs = req.getParameterValues("ActionTypeIDs");
        List<String> appliedActions = new ArrayList();
        
        Integer changeServiceCompanyID = null;
        Integer changeServiceStatusID = null;
        Integer changeServiceTypeID = null;        
//        Integer newEnergyCompanyID = null;
        
        if(selectionIDs != null && selectionIDs.length > 0 && selectionIDs.length == actionTypeIDs.length)
        {
            for(int j = 0; j < selectionIDs.length; j++)
            {
                if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_TOSERVICECOMPANY)
                    changeServiceCompanyID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() == ServletUtils.ACTION_CHANGE_WO_SERVICE_STATUS)
                    changeServiceStatusID = new Integer(selectionIDs[j]);
                else if(new Integer(actionTypeIDs[j]).intValue() ==  ServletUtils.ACTION_CHANGE_WO_SERVICE_TYPE)
                    changeServiceTypeID = new Integer(selectionIDs[j]);    
                
                appliedActions.add(actionTexts[j]);
            }
            
//            InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
            WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
            ManipulationBean mBean = (ManipulationBean) session.getAttribute("woManipulationBean");
//            ArrayList theWares = iBean.getInventoryList();
            if(workOrderBean.getNumberOfRecords() < 1)
            {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no Work Orders selected to update.");
                redirect = referer;
                return;
            }
            
            session.removeAttribute( ServletUtils.ATT_REDIRECT );
    		LiteYukonUser liteYukonUser = (LiteYukonUser ) session.getAttribute( ServletUtils.ATT_YUKON_USER );

            TimeConsumingTask  task = new ManipulateWorkOrderTask( liteYukonUser, workOrderBean.getWorkOrderList(), changeServiceCompanyID, changeServiceStatusID, changeServiceTypeID, req );
            long id = ProgressChecker.addTask( task );
            String redir = req.getContextPath() + "/operator/WorkOrder/WorkOrderResultSet.jsp";
            
            //Wait 2 seconds for the task to finish (or error out), if not, then go to the progress page
            for (int i = 0; i < 2; i++) 
            {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
                
                task = ProgressChecker.getTask(id);
                
                if (task.getStatus() == UpdateSNRangeTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    if (redir != null) redirect = redir;
                    return;
                }
                
                if (task.getStatus() == UpdateSNRangeTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                    ProgressChecker.removeTask( id );
                    redirect = referer;
                    return;
                }
            }
            
            mBean.setActionsApplied(appliedActions);
//            session.setAttribute("woManipulationBean", mBean);
            session.setAttribute(ServletUtils.ATT_REDIRECT, redir);
            session.setAttribute(ServletUtils.ATT_REFERRER, redir);
            redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        }
    }
	private void manipulateResults(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		redirect = req.getContextPath() + "/operator/WorkOrder/ChangeWorkOrders.jsp";		
	}

	private void viewAllResults(HttpServletRequest req, HttpSession session, boolean viewResults) {
		WorkOrderBean workOrderBean = (WorkOrderBean)session.getAttribute("workOrderBean");
		workOrderBean.setViewAllResults(viewResults);
	}

	private void updateFilters(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
    {
    	String start = req.getParameter("start");
    	String stop = req.getParameter("stop");
    	WorkOrderBean woBean = (WorkOrderBean)session.getAttribute("workOrderBean");
    	woBean.setStart(start);
    	woBean.setStop(stop);

        String[] selectionIDs = req.getParameterValues("SelectionIDs");
        String[] filterTexts = req.getParameterValues("FilterTexts");
        String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");

        List<FilterWrapper> filterWrappers = new ArrayList<FilterWrapper>();
        if( filterTexts != null)
        {
	        for(int j = 0; j < filterTexts.length; j++)
	        {
	            FilterWrapper wrapper = new FilterWrapper(yukonDefIDs[j], filterTexts[j], selectionIDs[j] );
	            filterWrappers.add(wrapper);
	        }
        }
        session.setAttribute( ServletUtil.FILTER_WORKORDER_LIST, filterWrappers );
        redirect = req.getContextPath() + "/operator/WorkOrder/WorkOrder.jsp";
    };
	}

	private void searchWorkOrder(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		session.removeAttribute( WorkOrderManagerUtil.WORK_ORDER_SET );
		
		int searchBy = Integer.parseInt( req.getParameter("SearchBy") );
		String searchValue = req.getParameter( "SearchValue" );
		if (searchValue.trim().length() == 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
			return;
		}
		
		// Remember the last search option
		session.setAttribute( ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION, new Integer(searchBy) );
		
		int[] orderIDs = null; 
		
		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO) {
			ArrayList orderList = energyCompany.searchWorkOrderByOrderNo( searchValue, false );
			orderIDs = new int[ orderList.size() ];
			for (int i = 0; i < orderList.size(); i++)
				orderIDs[i] = ((LiteWorkOrderBase)orderList.get(i)).getOrderID();
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
			ArrayList accounts = energyCompany.searchAccountByAccountNo( searchValue, false );
			orderIDs = getOrderIDsByAccounts( accounts );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
			try {
				String phoneNo = ServletUtils.formatPhoneNumber( searchValue );
				ArrayList accounts = energyCompany.searchAccountByPhoneNo( phoneNo, false );
				orderIDs = getOrderIDsByAccounts( accounts );
			}
			catch (WebClientException e) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
				return;
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME) {
			ArrayList accounts = energyCompany.searchAccountByLastName( searchValue, false );
			orderIDs = getOrderIDsByAccounts( accounts );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
			ArrayList accounts = energyCompany.searchAccountBySerialNo( searchValue, false );
			orderIDs = getOrderIDsByAccounts( accounts );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ADDRESS) {
			ArrayList accounts = energyCompany.searchAccountByAddress( searchValue, false );
			orderIDs = getOrderIDsByAccounts( accounts );
		}
		
		if (orderIDs == null || orderIDs.length == 0) {
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "<div class='ErrorMsg' align='center'>No service order found matching the search criteria.</div>");
		}
		else if (orderIDs.length == 1) {
			redirect = req.getContextPath() + "/operator/WorkOrder/ModifyWorkOrder.jsp?OrderId=" + orderIDs[0];
		}
		else {
			ArrayList soList = new ArrayList();
			for (int i = 0; i < orderIDs.length; i++)
				soList.add( energyCompany.getWorkOrderBase(orderIDs[i], true) );
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, soList);
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "Click on a Order # to view the service order details.");
			session.setAttribute(ServletUtils.ATT_REFERRER, referer);
		}
	}
	
	private void preCreateWorkOrder(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
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
			operation.getStarsCreateServiceRequest().setAccountID( CtiUtilities.NONE_ZERO_ID );
		
		session.setAttribute(WorkOrderManagerUtil.STARS_WORK_ORDER_OPER_REQ, operation);
	}
	
	private void searchCustAccount(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
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
		session.setAttribute(WorkOrderManagerUtil.STARS_WORK_ORDER_OPER_REQ, operation);
	}
	
	private void sendWorkOrder(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		int orderID = Integer.parseInt( req.getParameter("OrderID") );
		LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( orderID, true );
		
		if (liteOrder.getServiceCompanyID() == 0) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "You have not assigned this work order to a service company yet." );
			return;
		}
		
		LiteServiceCompany sc = energyCompany.getServiceCompany( liteOrder.getServiceCompanyID() );
		String email = null;
		if (sc.getPrimaryContactID() > 0) {
			LiteContact contact = ContactFuncs.getContact( sc.getPrimaryContactID() );
			LiteContactNotification emailNotif = ContactFuncs.getContactNotification( contact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			if (emailNotif != null) email = emailNotif.getNotification();
		}
		
		if (email == null) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "There is no email address assigned to service company \"" + sc.getCompanyName() + "\"." );
			return;
		}
		
		try {
		    ReportBean reportBean = new ReportBean();
		    reportBean.setType(ReportTypes.EC_WORK_ORDER_DATA);
		    reportBean.getModel().setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			((WorkOrderModel)reportBean.getModel()).setOrderID( new Integer(orderID) );
			
			JFreeReport report = reportBean.createReport();;
			
			File tempDir = new File( ServerUtils.getStarsTempDir(), "/WorkOrder" );
			if (!tempDir.exists()) tempDir.mkdirs();
			File tempFile = File.createTempFile("WorkOrder", ".pdf", tempDir);
			tempFile.deleteOnExit();
			
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream( tempFile );
				ReportFuncs.outputYukonReport( report, "pdf", fos );
			}
			catch (Exception e) {
				// There will always be an exception because the PDF encoder will try to write two versions
				// of data into the file, while the output stream will stop accepting data after the first
				// EOF is met. The exception is simply ignored here, could miss some other exceptions.
			}
			finally {
				if (fos != null) fos.close();
			}
			
			Date now = new Date();
			String fileName = "WorkOrder_" + StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".pdf";
			
			EmailMessage emailMsg = new EmailMessage();
			emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
			emailMsg.setTo( email );
			emailMsg.setSubject( "Work Order" );
			emailMsg.setBody( "" );
			emailMsg.addAttachment( tempFile, fileName );
			
			emailMsg.send();
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Sent work order to the service company successfully." );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to send the work order." );
		}
	}
	
	private int[] getOrderIDsByAccounts(ArrayList accounts) {
		if (accounts != null && accounts.size() > 0) {
			ArrayList orderIDList = new ArrayList();
			
			for (int i = 0; i < accounts.size(); i++) {
				LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(i);
				if (liteAcctInfo.isExtended()) {
					orderIDList.addAll( liteAcctInfo.getServiceRequestHistory() );
				}
				else {
					int[] woIDs = WorkOrderBase.searchByAccountID( liteAcctInfo.getAccountID() );
					for (int j = 0; j < woIDs.length; j++)
						orderIDList.add( new Integer(woIDs[j]) );
				}
			}
			
			int[] orderIDs = new int[ orderIDList.size() ];
			for (int i = 0; i< orderIDList.size(); i++)
				orderIDs[i] = ((Integer) orderIDList.get(i)).intValue();
			return orderIDs;
		}
		
		return null;
	}
	
	private File createTempFile() throws IOException {
		Date now = new Date();
		String fileID = StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now);
		if (fileID.equals( lastFileID1 ))
			fileID += "_" + (++lastFileID2);
		else
			lastFileID1 = fileID;
		
		File tempDir = new File(ServerUtils.getStarsTempDir() + "/WorkOrder");
		if (!tempDir.exists()) tempDir.mkdirs();
		
		return new File(tempDir, "WorkOrder_" + fileID + ".pdf");
	}
	
}
