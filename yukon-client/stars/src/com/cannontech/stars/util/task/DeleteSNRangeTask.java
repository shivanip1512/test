/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ECUtils;
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
	
	Integer snFrom = null;
	Integer snTo = null;
	Integer devTypeID = null;
	HttpServletRequest request = null;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeDeleted = 0;
	
	public DeleteSNRangeTask(Integer snFrom, Integer snTo, Integer devTypeID, HttpServletRequest request) {
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (numToBeDeleted > 0) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				String snRange = InventoryManagerUtil.getSNRange( snFrom, snTo );
				if (snRange != null)
					snRange = "The serial numbers " + snRange;
				else
					snRange = "All serial numbers";
				return snRange + " have been deleted successfully.";
			}
			else
				return numSuccess + " of " + numToBeDeleted + " hardwares have been deleted.";
		}
		else
			return "Deleting hardwares from inventory...";
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
		
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		status = STATUS_RUNNING;
		
		if (ECUtils.isMCT(categoryID)) {
			ArrayList mctList = new ArrayList();
			
			ArrayList inventory = energyCompany.loadAllInventory( true );
			synchronized (inventory) {
				for (int i = 0; i < inventory.size(); i++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(i);
					if (ECUtils.isMCT( liteInv.getCategoryID() )) {
						if (liteInv.getAccountID() > 0) {
							hardwareSet.add( liteInv );
							numFailure++;
						}
						else
							mctList.add( liteInv );
					}
				}
			}
			
			numToBeDeleted = mctList.size();
			
			for (int i = 0; i < mctList.size(); i++) {
				LiteInventoryBase liteInv = (LiteInventoryBase) mctList.get(i);
				
				try {
					com.cannontech.database.data.stars.hardware.InventoryBase inv =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inv.setInventoryID( new Integer(liteInv.getInventoryID()) );
					
					Transaction.createTransaction( Transaction.DELETE, inv ).execute();
					
					energyCompany.deleteInventory( liteInv.getInventoryID() );
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
			ArrayList hwList = ECUtils.getLMHardwareInRange( energyCompany, devTypeID, snFrom, snTo );
			
			numToBeDeleted = hwList.size();
			if (numToBeDeleted == 0) {
				status = STATUS_ERROR;
				errorMsg = "There was no " + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText() + " found in the given range of serial numbers.";
				return;
			}
			
			for (int i = 0; i < hwList.size(); i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hwList.get(i);
				
				if (liteHw.getAccountID() > 0) {
					hardwareSet.add( liteHw );
					numFailure++;
					continue;
				}
				
				try {
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					hardware.setInventoryID( new Integer(liteHw.getInventoryID()) );
					Transaction.createTransaction( Transaction.DELETE, hardware ).execute();
					
					energyCompany.deleteInventory( liteHw.getInventoryID() );
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
				+ ",Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_DELETE_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares deleted successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (listed below).<br>" +
					"If a hardware is assigned to a customer account, you must remove it from the account before deleting it.</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
