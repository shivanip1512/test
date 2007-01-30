package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.database.data.stars.report.WorkOrderBase;
import com.cannontech.database.db.stars.integration.SAMToCRS_PTJ;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;

/**
 * @author jdayton
 */
public class ManipulateWorkOrderTask extends TimeConsumingTask {
	
	LiteYukonUser liteYukonUser = null;
    ArrayList selectedWorkOrders = new ArrayList();
    Integer changeServiceCompanyID = null;
    Integer changeServiceStatusID = null;
    Integer changeServiceTypeID = null;
    String statusMsg = null;

	HttpServletRequest request = null;
	
	ArrayList<String> failedWorkOrderMessages = new ArrayList<String>();
	int numSuccess = 0, numFailure = 0;

	public ManipulateWorkOrderTask(LiteYukonUser liteYukonuser, ArrayList<LiteWorkOrderBase> workOrderList, Integer changeServiceCompanyID, Integer changeServiceStatusID, Integer changeServiceTypeID, HttpServletRequest req) {
		this.liteYukonUser = liteYukonuser;
		this.selectedWorkOrders = workOrderList;
		this.changeServiceCompanyID = changeServiceCompanyID;
		this.changeServiceStatusID = changeServiceStatusID;
		this.changeServiceTypeID = changeServiceTypeID;
		this.request = req;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (selectedWorkOrders.size() > 0) {
            if (status == STATUS_FINISHED && numFailure == 0) {
                if (statusMsg != null)
                	statusMsg = "Selected Work Order entries";
                else
                	statusMsg = "All Work Order entries";
                return statusMsg + " have been updated successfully.";
            }
            else
                return numSuccess + " of " + selectedWorkOrders.size() + " Work Orders have been updated.";
        }
        else
            return "Updating Work Order entries...";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
    {
		status = STATUS_RUNNING;
		
		HttpSession session = request.getSession(false);
        ManipulationBean mBean = (ManipulationBean) session.getAttribute("woManipulationBean"); 
        WorkOrderBean woBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        ArrayList<LiteWorkOrderBase> workOrderList = woBean.getWorkOrderList();
		
		if (workOrderList.size() == 0) 
        {
			status = STATUS_ERROR;
			errorMsg = "There are no Work Orders selected by the filter process.  No changes made.";
			return;
		}
		
		for (int i = 0; i < workOrderList.size(); i++) 
        {
			boolean isChanged = false;			//flag if workOrder changed at all
			boolean isStatusChanged = false;	//flag if status change, need to write to EventWorkOrderBase
			
			LiteWorkOrderBase liteWorkOrder = workOrderList.get(i);
			WorkOrderBase workOrderBase = (WorkOrderBase)StarsLiteFactory.createDBPersistent(liteWorkOrder);
			//Do not retrieve the whole work order again, it is already all there since the lite and the heavy are the same.  This may change though!
			
			if( changeServiceCompanyID != null && workOrderBase.getWorkOrderBase().getServiceCompanyID().intValue() != changeServiceCompanyID.intValue())
			{
				workOrderBase.getWorkOrderBase().setServiceCompanyID(changeServiceCompanyID);
				isChanged = true;
			}
			
			YukonListEntry listEntry = null;
			if( changeServiceStatusID != null && workOrderBase.getWorkOrderBase().getCurrentStateID().intValue() != changeServiceStatusID.intValue())
			{
				listEntry = DaoFactory.getYukonListDao().getYukonListEntry(changeServiceStatusID.intValue());
				workOrderBase.getWorkOrderBase().setCurrentStateID(changeServiceStatusID);
				isChanged = true;
				isStatusChanged = true;
			}
			if( changeServiceTypeID != null && workOrderBase.getWorkOrderBase().getWorkTypeID().intValue() != changeServiceTypeID.intValue())
			{
				workOrderBase.getWorkOrderBase().setWorkTypeID(changeServiceTypeID);
				isChanged = true;
			}

			try 
            {
				if( isChanged)
				{
					Date eventDate = new Date();
					workOrderBase.getWorkOrderBase().setDateReported(eventDate);	//set the work order DateReported with the most recent event date.
					workOrderBase = (WorkOrderBase)Transaction.createTransaction( Transaction.UPDATE, workOrderBase).execute();
					if( isStatusChanged)
					{
		           		EventWorkOrder eventWorkOrder  = (EventWorkOrder)EventUtils.logSTARSDatedEvent(liteYukonUser.getUserID(), EventUtils.EVENT_CATEGORY_WORKORDER, workOrderBase.getWorkOrderBase().getCurrentStateID().intValue(), workOrderBase.getWorkOrderBase().getOrderID().intValue(), eventDate);
		           		workOrderBase.getEventWorkOrders().add(0, eventWorkOrder);
    					
    					if ( VersionTools.crsPtjIntegrationExists())
    					{
    						LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(workOrderBase.getEnergyCompanyID());
    						LiteStarsCustAccountInformation liteStarsCustAcctInfo = liteStarsEC.getBriefCustAccountInfo(workOrderBase.getWorkOrderBase().getAccountID().intValue(), true);
    						SAMToCRS_PTJ.handleCRSIntegration(listEntry.getYukonDefID(), workOrderBase, liteStarsCustAcctInfo, liteStarsEC, liteYukonUser.getUserID(), null);
    					}
                    }

					DBChangeMsg dbChangeMessage = new DBChangeMsg(
		    				workOrderBase.getWorkOrderBase().getOrderID(),
		    				DBChangeMsg.CHANGE_WORK_ORDER_DB,
		    				DBChangeMsg.CAT_WORK_ORDER,
		    				DBChangeMsg.CAT_WORK_ORDER,
		    				DBChangeMsg.CHANGE_TYPE_UPDATE
		    			);
		    		//Change the dbChangeMessage source so the stars message handler will handle it instead of ignore it.
					dbChangeMessage.setSource("ManipulateWorkOrder:ForceHandleDBChange");
	                ServerUtils.handleDBChangeMsg(dbChangeMessage);
	                StarsLiteFactory.setLiteWorkOrderBase(liteWorkOrder, workOrderBase);
				}
				numSuccess++;
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				failedWorkOrderMessages.add("Work Order#: " + liteWorkOrder.getOrderNumber() + " - " + e.getMessage());
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		if (statusMsg == null) statusMsg = "Selected Work Orders";
		
		status = STATUS_FINISHED;
        mBean.setFailures(numFailure);
        mBean.setSuccesses(numSuccess);
        mBean.setFailedManipulateResults(failedWorkOrderMessages);

        woBean.setWorkOrderList(null);	//force the list to be reloaded!
        
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " Work Orders updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " Work Orders failed (listed below).</span><br>";
			
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, resultDesc);
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, workOrderList);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/WorkOrder/WorkOrderResultSet.jsp");
			if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}
}