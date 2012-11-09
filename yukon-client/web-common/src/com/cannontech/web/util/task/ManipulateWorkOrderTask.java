package com.cannontech.web.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.EventWorkOrder;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.data.report.WorkOrderBase;
import com.cannontech.stars.database.db.integration.SAMToCRS_PTJ;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.web.bean.WorkOrderBean;

public class ManipulateWorkOrderTask extends TimeConsumingTask {
	
    private LiteYukonUser liteYukonUser = null;
    private List<LiteWorkOrderBase> selectedWorkOrders = new ArrayList<LiteWorkOrderBase>();
    private Integer changeServiceCompanyID = null;
    private Integer changeServiceStatusID = null;
    private Integer changeServiceTypeID = null;
    private String statusMsg = null;
    private final boolean confirmOnMessagePage;
    private final String redirect;
    private final HttpSession session;

    private final ArrayList<String> failedWorkOrderMessages = new ArrayList<String>();
    private int numSuccess = 0, numFailure = 0;

	private final StarsCustAccountInformationDao starsCustAccountInformationDao = 
	    YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
	private final DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
	
	public ManipulateWorkOrderTask(LiteYukonUser liteYukonuser, List<LiteWorkOrderBase> workOrderList, Integer changeServiceCompanyID, Integer changeServiceStatusID, Integer changeServiceTypeID, 
	        boolean confirmOnMessagePage, String redirect, HttpSession session) {
		this.liteYukonUser = liteYukonuser;
		this.selectedWorkOrders = workOrderList;
		this.changeServiceCompanyID = changeServiceCompanyID;
		this.changeServiceStatusID = changeServiceStatusID;
		this.changeServiceTypeID = changeServiceTypeID;
		this.confirmOnMessagePage = confirmOnMessagePage;
		this.redirect = redirect;
		this.session = session;
	}

	@Override
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
	@Override
    public void run() 
    {
		status = STATUS_RUNNING;
		
        ManipulationBean mBean = (ManipulationBean) session.getAttribute("woManipulationBean"); 
        WorkOrderBean woBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        List<LiteWorkOrderBase> workOrderList = woBean.getWorkOrderList();
		
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
            LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(liteWorkOrder.getEnergyCompanyID());			
			//Do not retrieve the whole work order again, it is already all there since the lite and the heavy are the same.  This may change though!
			
			if( changeServiceCompanyID != null && workOrderBase.getWorkOrderBase().getServiceCompanyID().intValue() != changeServiceCompanyID.intValue())
			{
                //see if newServiceCompanyID is available for the energyCompany of the device
                if (energyCompany.getServiceCompany(changeServiceCompanyID) == null) {
                    String msg = "Service company [" + changeServiceCompanyID + "] is not available to energy company [" + energyCompany.getName() + "]";
                    CTILogger.error( msg );
                    failedWorkOrderMessages.add("Work Order#: " + liteWorkOrder.getOrderNumber() + " - " + msg);
                    numFailure++;                    
                    continue;
                }			    
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
					workOrderBase = Transaction.createTransaction( Transaction.UPDATE, workOrderBase).execute();
					if( isStatusChanged)
					{
		           		EventWorkOrder eventWorkOrder  = (EventWorkOrder)EventUtils.logSTARSDatedEvent(liteYukonUser.getUserID(), EventUtils.EVENT_CATEGORY_WORKORDER, workOrderBase.getWorkOrderBase().getCurrentStateID().intValue(), workOrderBase.getWorkOrderBase().getOrderID().intValue(), eventDate);
		           		workOrderBase.getEventWorkOrders().add(0, eventWorkOrder);
    					
    					if ( VersionTools.crsPtjIntegrationExists())
    					{
    						LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(workOrderBase.getEnergyCompanyID());
    						LiteAccountInfo liteStarsCustAcctInfo =
    						    starsCustAccountInformationDao.getById(workOrderBase.getWorkOrderBase().getAccountID(), liteStarsEC.getEnergyCompanyId());
    						SAMToCRS_PTJ.handleCRSIntegration(listEntry.getYukonDefID(), workOrderBase, liteStarsCustAcctInfo, liteStarsEC, liteYukonUser.getUserID(), null);
    					}
                    }

					DBChangeMsg dbChangeMessage = new DBChangeMsg(
		    				workOrderBase.getWorkOrderBase().getOrderID(),
		    				DBChangeMsg.CHANGE_WORK_ORDER_DB,
		    				DBChangeMsg.CAT_WORK_ORDER,
		    				DBChangeMsg.CAT_WORK_ORDER,
		    				DbChangeType.UPDATE
		    			);
		    		//Change the dbChangeMessage source so the stars message handler will handle it instead of ignore it.
					dbChangeMessage.setSource("ManipulateWorkOrder:ForceHandleDBChange");
					dbPersistentDao.processDBChange(dbChangeMessage);
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
        
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " Work Orders updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " Work Orders failed (listed below).</span><br>";
			
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET_DESC, resultDesc);
			session.setAttribute(WorkOrderManagerUtil.WORK_ORDER_SET, workOrderList);
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			if (confirmOnMessagePage)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}
}