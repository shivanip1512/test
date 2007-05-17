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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ManipulateWorkOrderTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.UpdateServiceRequestAction;
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
        referer = req.getParameter( ServletUtils.ATT_REFERRER );
        if(referer == null)
            referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
        redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
        if (redirect == null) redirect = referer;
            
		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
    	int memberCompanyID = user.getEnergyCompanyID();
        
        if (req.getParameter("SwitchContext") != null) {
            try{
                int memberID = Integer.parseInt( req.getParameter("SwitchContext") );
                StarsAdmin.switchContext( user, req, session, memberID );
                session = req.getSession( false );
                memberCompanyID = memberID;
            }
            catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                resp.sendRedirect( referer );
                return;
            }
        }
        
//		referer = req.getParameter( ServletUtils.ATT_REFERRER );
//		if (referer == null) referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
//		redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
//		if (redirect == null) redirect = referer;
		
		// If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
		if (req.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, redirect );
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, referer );
			redirect = referer = req.getContextPath() + "/operator/Admin/Message.jsp";
		}
		
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("SearchWorkOrder"))
			searchWorkOrder( req, session, memberCompanyID );
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
            updateFilters( req, session );
        else if (action.equalsIgnoreCase("ManipulateSelectedResults"))
        	manipulateSelectedResults( req, session );
        else if (action.equalsIgnoreCase("ManipulateResults"))
        	manipulateResults( req, session );
        else if (action.equalsIgnoreCase("ApplyActions"))
        	applyActions( req, session );		
        else if( action.equalsIgnoreCase("ViewAllResults"))
        	viewAllResults(req, session, true);
        else if( action.equalsIgnoreCase("HideAllResults"))
        	viewAllResults(req, session, false);
        else if( action.equalsIgnoreCase("SortWorkOrders"))
        	sortWorkOrders(req, session);
        else if( action.equalsIgnoreCase("CreateReport"))
        {
    		//A filename for downloading the report to.
        	String ext = "pdf";
        	String param = req.getParameter("ext");
        	if(param != null)
        		ext = param;
    		String fileName = "WorkOrders";
    		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    		fileName += "_"+format.format(new Date()) + "." + ext;        	
    		resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);

        	JFreeReport report = createReport(user, req, session);
    		final ServletOutputStream out = resp.getOutputStream();
    		try {
    			if( ext.equalsIgnoreCase("pdf"))
    			{
    	    		resp.setContentType("application/pdf");
    				resp.addHeader("Content-Type", "application/pdf");    				
    				ReportFuncs.outputYukonReport( report, ext, out );
    			}
    			else if( ext.equalsIgnoreCase("csv")){
                    ReportFuncs.outputYukonReport( report, ext, out);
    				out.flush();
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

		resp.sendRedirect( redirect );
	}
	
	private JFreeReport createReport(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		
		WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
		LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		
		ReportBean reportBean = new ReportBean();
		reportBean.setType(ReportTypes.EC_WORK_ORDER_DATA);
		reportBean.setUserID(user.getUserID());
		((WorkOrderModel)reportBean.getModel()).loadData(liteStarsEC, workOrderBean.getWorkOrderList());

		reportBean.getModel().setTimeZone(workOrderBean.getEnergyCompany().getDefaultTimeZone());
		reportBean.getModel().setEnergyCompanyID(workOrderBean.getEnergyCompany().getEnergyCompanyID());

		JFreeReport report = null;
		//Create the report
		try {
			//NOTE:  Don't use the reportBean.createReport() method.  We don't want to call collectData() since we are using the filtered list instead!
//			Create an instance of JFreeReport from the YukonReportBase
		    YukonReportBase reportBase = ReportFuncs.createYukonReport(reportBean.getModel());
		    report = reportBase.createReport();
		    report.setData(reportBase.getModel());
		} catch (FunctionInitializeException e) {
			e.printStackTrace();
		}
		return report;
	}

	private void sortWorkOrders(HttpServletRequest req, HttpSession session) {
		
		WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
		String param = req.getParameter("SortBy");
		if( param != null)
			workOrderBean.setSortBy(Integer.valueOf(param) );
		
		param = req.getParameter("SortOrder");
		if( param != null)
			workOrderBean.setSortOrder(Integer.valueOf(param) );

	}
	private void manipulateSelectedResults( HttpServletRequest req, HttpSession session) {
		
		String[] selections = req.getParameterValues("checkWorkOrder");
        if( selections == null)//none selected
        {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "There are no individual Work Orders checked to manipulate.");
            redirect = referer;
            return;
        }
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
		
		manipulateResults(req, session);
		
	}
	private void applyActions(HttpServletRequest req, HttpSession session) 
    {        
        String[] selectionIDs = req.getParameterValues("SelectionIDs");
        String[] actionTexts = req.getParameterValues("ActionTexts");
        String[] actionTypeIDs = req.getParameterValues("ActionTypeIDs");
        List<String> appliedActions = new ArrayList<String>();
        
        Integer changeServiceCompanyID = null;
        Integer changeServiceStatusID = null;
        Integer changeServiceTypeID = null;        
        
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
            
            WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
            ManipulationBean mBean = (ManipulationBean) session.getAttribute("woManipulationBean");
            mBean.setActionsApplied(appliedActions);
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
                
                if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
                    ProgressChecker.removeTask( id );
                    if (redir != null) redirect = redir;
                    return;
                }
                
                if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
                    session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
                    ProgressChecker.removeTask( id );
                    redirect = referer;
                    return;
                }
            }
            session.setAttribute(ServletUtils.ATT_REDIRECT, redir);
            session.setAttribute(ServletUtils.ATT_REFERRER, redir);
            redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
        }
    }
	private void manipulateResults(HttpServletRequest req, HttpSession session) {
		redirect = req.getContextPath() + "/operator/WorkOrder/ChangeWorkOrders.jsp";		
	}

	private void viewAllResults(HttpServletRequest req, HttpSession session, boolean viewResults) {
		WorkOrderBean workOrderBean = (WorkOrderBean)session.getAttribute("workOrderBean");
		workOrderBean.setViewAllResults(viewResults);
	}

	private void updateFilters(HttpServletRequest req, HttpSession session) {
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
        woBean.setViewAllResults(false);    //reset the view option
        session.setAttribute( ServletUtil.FILTER_WORKORDER_LIST, filterWrappers );
        redirect = req.getContextPath() + "/operator/WorkOrder/WorkOrder.jsp";
    };
	}

	private void searchWorkOrder(HttpServletRequest req, HttpSession session, int memberCompanyID) {
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( memberCompanyID );
		
		session.removeAttribute( WorkOrderManagerUtil.WORK_ORDER_SET );
		
		int searchBy = -1;
        if( req.getParameter("SearchBy") != null)
            searchBy = Integer.parseInt( req.getParameter("SearchBy") );
        
		String searchValue = req.getParameter( "SearchValue" );
		if (searchValue.trim().length() == 0) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search value cannot be empty");
			return;
		}
        
        ArrayList<LiteWorkOrderBase> liteWorkOrderList = new ArrayList<LiteWorkOrderBase>();		
        if( searchBy < 0)//we use -1 for search by orderID.  This is a shortcut! and is useful when having member searches.
        {   //We are short cutting a big loop through all energy companies to find the EC with this OrderID by passing in the ECID.
            liteWorkOrderList.add(energyCompany.getWorkOrderBase(Integer.valueOf(searchValue), true));
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            redirect = req.getContextPath() + "/operator/WorkOrder/ModifyWorkOrder.jsp";
            return;
        }
        
        
		// Remember the last search option
		session.setAttribute( ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION, new Integer(searchBy) );
        session.setAttribute( ServletUtils.ATT_LAST_SERVICE_SEARCH_VALUE, new String(searchValue) );
        StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		boolean searchMembers = DaoFactory.getAuthDao().checkRoleProperty( user.getYukonUser(), AdministratorRole.ADMIN_MANAGE_MEMBERS ) && 
								(energyCompany.getChildren().size() > 0);

		if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO) {
			liteWorkOrderList = energyCompany.searchWorkOrderByOrderNo( searchValue, searchMembers );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ACCT_NO) {
			ArrayList accounts = energyCompany.searchAccountByAccountNumber( searchValue, searchMembers, true );
			liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_PHONE_NO) {
			try {
				String phoneNo = ServletUtils.formatPhoneNumberForSearch( searchValue );
				ArrayList accounts = energyCompany.searchAccountByPhoneNo( phoneNo, searchMembers );
                liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
			}
			catch (WebClientException e) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
				return;
			}
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_LAST_NAME) {
			ArrayList accounts = energyCompany.searchAccountByLastName( searchValue, searchMembers , true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_SERIAL_NO) {
			ArrayList accounts = energyCompany.searchAccountBySerialNo( searchValue, searchMembers );
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
		}
		else if (searchBy == YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ADDRESS) {
			ArrayList accounts = energyCompany.searchAccountByAddress( searchValue, searchMembers, true);
            liteWorkOrderList.addAll(getOrderIDsByAccounts( accounts, energyCompany ) );
		}
		
		if (liteWorkOrderList.size() == 0) {
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, "<div class='ErrorMsg' align='center'>No service order found matching the search criteria.</div>");
		}
        //Don't be smart about going directly to the web page, this way we can go through the context switch of EC if needed
		/*else if (liteWorkOrderList.size() == 1) {   
            session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
            redirect = req.getContextPath() + "/operator/WorkOrder/ModifyWorkOrder.jsp";
		}*/
		else {
//			ArrayList soList = new ArrayList();
//			for (int i = 0; i < orderIDs.length; i++)
//				soList.add( energyCompany.getWorkOrderBase(orderIDs[i], true) );
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, liteWorkOrderList);
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
			LiteContact contact = DaoFactory.getContactDao().getContact( sc.getPrimaryContactID() );
			LiteContactNotification emailNotif = DaoFactory.getContactDao().getContactNotification( contact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
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
	
    private ArrayList<LiteWorkOrderBase> getOrderIDsByAccounts(ArrayList accounts, LiteStarsEnergyCompany defaultEnergyCompany) {
//	private int[] getOrderIDsByAccounts(ArrayList accounts) {
		if (accounts != null && accounts.size() > 0) {
			ArrayList liteWorkOrderList = new ArrayList<LiteWorkOrderBase>();
			for (int i = 0; i < accounts.size(); i++) {
                LiteStarsCustAccountInformation liteAcctInfo = null;             
                LiteStarsEnergyCompany liteStarsEnergyCompany = null;
                if (accounts.get(i) instanceof Pair){
                    liteAcctInfo = (LiteStarsCustAccountInformation)((Pair)accounts.get(i)).getFirst();
                    liteStarsEnergyCompany = (LiteStarsEnergyCompany)((Pair)accounts.get(i)).getSecond();
                }
                else
                {
                    liteAcctInfo = (LiteStarsCustAccountInformation) accounts.get(i);
                    liteStarsEnergyCompany = defaultEnergyCompany;
                }
                
				if (liteAcctInfo.isExtended()) {
                    for(int j = 0; j < liteAcctInfo.getServiceRequestHistory().size(); j++)
                        liteWorkOrderList.add(liteStarsEnergyCompany.getWorkOrderBase(liteAcctInfo.getServiceRequestHistory().get(j).intValue(), true));
				}
				else {
					int[] woIDs = WorkOrderBase.searchByAccountID( liteAcctInfo.getAccountID() );
					for (int j = 0; j < woIDs.length; j++)
                        liteWorkOrderList.add(liteStarsEnergyCompany.getWorkOrderBase(woIDs[j], true));
				}
			}
			
			return liteWorkOrderList;
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
