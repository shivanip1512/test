/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.database.data.hardware.InventoryBase;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteSNRangeTask extends TimeConsumingTask {
	
	private final LiteStarsEnergyCompany energyCompany;
	private final long snFrom;
	private final long snTo;
	private final Integer devTypeID;
	private final boolean confirmOnMessagePage;
	private final String redirect;
	private final HttpSession session;
	
	List<LiteInventoryBase> hardwareSet = new ArrayList<LiteInventoryBase>();
	int numSuccess = 0, numFailure = 0;
	int numToBeDeleted = 0;
	
	public DeleteSNRangeTask(LiteStarsEnergyCompany energyCompany, long snFrom, long snTo, Integer devTypeID, 
	        boolean confirmOnMessagePage, String redirect, HttpSession session) {
		this.energyCompany = energyCompany;
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.confirmOnMessagePage = confirmOnMessagePage;
		this.redirect = redirect;
		this.session = session;
	}

	@Override
    public String getProgressMsg() {
		if (numToBeDeleted > 0) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				String snRange = InventoryManagerUtil.getSNRange( snFrom, snTo );
				if (snRange != null) {
					snRange = "The serial numbers " + snRange;
				} else {
					snRange = "All serial numbers";
				}	
				return snRange + " have been deleted successfully.";
			}
			return numSuccess + " of " + numToBeDeleted + " hardware entries have been deleted.";
		}
		return "Deleting hardware entries from inventory...";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (devTypeID == null) {
			status = STATUS_ERROR;
			errorMsg = "Device type cannot be null";
			return;
		}
		
		status = STATUS_RUNNING;
		
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		int categoryID = InventoryUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		if (InventoryUtils.isMCT(categoryID)) {
			List<LiteInventoryBase> mctList = new ArrayList<LiteInventoryBase>();
			
			StarsInventoryBaseDao starsInventoryBaseDao = 
				YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
			
			// Get MCTs with account assigned
			List<LiteInventoryBase> mctWithAccountList = 
				starsInventoryBaseDao.getAllMCTsWithAccount(Collections.singletonList(energyCompany));
			hardwareSet.addAll(mctWithAccountList);
			numFailure += mctWithAccountList.size();
			
			// Get MCTs without account assigned
			List<LiteInventoryBase> mctWithoutAccountList = 
				starsInventoryBaseDao.getAllMCTsWithoutAccount(Collections.singletonList(energyCompany));
			mctList.addAll(mctWithoutAccountList);
			numToBeDeleted = mctList.size();
			
			// Remove MCTs without account
			for (LiteInventoryBase liteInv : mctList) {
				
				try {
					InventoryBase inv = new InventoryBase();
					inv.setInventoryID( new Integer(liteInv.getInventoryID()) );
					
					Transaction.createTransaction( Transaction.DELETE, inv ).execute();
					
					numSuccess++;
				}
				catch (TransactionException e) {
					CTILogger.error( e.getMessage(), e );
					hardwareSet.add( liteInv );
					numFailure++;
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
		}
		else {
			int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getYukonDefID();
            List<LiteStarsLMHardware> hwList = null;
			try {
			    hwList = InventoryUtils.getLMHardwareInRange( energyCompany, devTypeDefID, snFrom, snTo );
	        } catch (PersistenceException e){
	            status = STATUS_ERROR;
	            errorMsg = e.getMessage();
	            return;
	        }
			numToBeDeleted = hwList.size();
			if (numToBeDeleted == 0) {
				status = STATUS_ERROR;
				errorMsg = "There was no " + DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getEntryText() + " found in the given range of serial numbers.";
				return;
			}
			
			for (int i = 0; i < hwList.size(); i++) {
				LiteStarsLMHardware liteHw = hwList.get(i);
				
				if (liteHw.getAccountID() > 0) {
					hardwareSet.add( liteHw );
					numFailure++;
					continue;
				}
				
				try {
					com.cannontech.stars.database.data.hardware.LMHardwareBase hardware =
							new com.cannontech.stars.database.data.hardware.LMHardwareBase();
					hardware.setInventoryID( new Integer(liteHw.getInventoryID()) );
					Transaction.createTransaction( Transaction.DELETE, hardware ).execute();
					
					numSuccess++;
				}
				catch (TransactionException e) {
					CTILogger.error( e.getMessage(), e );
					hardwareSet.add( liteHw );
					numFailure++;
				}
				
				if (isCanceled) {
					status = STATUS_CANCELED;
					return;
				}
			}
		}
		
		String snRange = InventoryManagerUtil.getSNRange( snFrom, snTo );
		if (snRange == null) snRange = "all serial numbers";
		String logMsg = "Serial Range:" + snRange
				+ ",Device Type:" + DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_DELETE_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries deleted successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
					"If a hardware is assigned to a customer account, you must remove it from the account before deleting it.</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			if (confirmOnMessagePage)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}

}
