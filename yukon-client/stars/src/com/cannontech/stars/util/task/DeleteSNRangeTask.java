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
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.InventoryManager;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteSNRangeTask implements TimeConsumingTask {
	
	int status = STATUS_NOT_INIT;
	boolean isCanceled = false;
	String errorMsg = null;
	
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
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#cancel()
	 */
	public void cancel() {
		if (status == STATUS_RUNNING) {
			isCanceled = true;
			status = STATUS_CANCELING;
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (numToBeDeleted > 0) {
			String snToStr = (snTo != null)? " to " + snTo : " and above";
			if (status == STATUS_FINISHED)
				return "The SN range " + snFrom + snToStr + " has been deleted successfully";
			else
				return numSuccess + " of " + numToBeDeleted + " hardwares deleted";
		}
		else {
			if (status == STATUS_FINISHED)
				return "No hardware found in the given SN range";
			else
				return "Deleting hardwares from inventory...";
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getErrorMsg()
	 */
	public String getErrorMsg() {
		return errorMsg;
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
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID.intValue(), energyCompany );
		
		status = STATUS_RUNNING;
		
		if (ECUtils.isMCT(categoryID)) {
			ArrayList mctList = new ArrayList();
			
			ArrayList inventory = energyCompany.loadAllInventory();
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
			ArrayList hwList = ECUtils.getLMHardwareInRange( energyCompany, devTypeID.intValue(), snFrom, snTo );
			numToBeDeleted = hwList.size();
			
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
		
		String logMsg = "Serial Range:" + snFrom + ((snTo != null)? " - " + snTo : " and above")
				+ ",Device Type:" + YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_DELETE_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManager.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardwares deleted successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardwares failed (If a hardware is assigned to an account, you must remove it from the account before deleting it). " +
					"They are listed below:</span><br>";
			
			session.setAttribute(InventoryManager.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManager.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REFERRER, request.getHeader("referer"));
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
		}
	}

}
