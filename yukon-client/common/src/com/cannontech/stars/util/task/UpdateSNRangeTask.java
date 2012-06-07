/*
 * Created on Apr 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UpdateSNRangeTask extends TimeConsumingTask {
	
	LiteStarsEnergyCompany energyCompany = null;
	long snFrom = 0;
	long snTo = 0;
	Integer devTypeID = null;
	Integer newDevTypeID = null;
	Date recvDate = null;
	Integer voltageID = null;
	Integer companyID = null;
	Integer routeID = null;
	private final boolean confirmOnMessagePage;
	private final String redirect;
	private final HttpSession session;
	
	List<LiteStarsLMHardware> hardwareSet = new ArrayList<LiteStarsLMHardware>();
	int numSuccess = 0, numFailure = 0;
	int numToBeUpdated = 0;
	
	public UpdateSNRangeTask(LiteStarsEnergyCompany energyCompany, long snFrom, long snTo, Integer devTypeID, Integer newDevTypeID,
		Date recvDate, Integer voltageID, Integer companyID, Integer routeID, 
		    boolean confirmOnMessagePage, String redirect, HttpSession session)
	{
		this.energyCompany = energyCompany;
		this.snFrom = snFrom;
		this.snTo = snTo;
		this.devTypeID = devTypeID;
		this.newDevTypeID = newDevTypeID;
		this.recvDate = recvDate;
		this.voltageID = voltageID;
		this.companyID = companyID;
		this.routeID = routeID;
		this.confirmOnMessagePage = confirmOnMessagePage;
		this.redirect = redirect;
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	@Override
	public String getProgressMsg() {
		if (numToBeUpdated > 0) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				String snRange = InventoryManagerUtil.getSNRange( snFrom, snTo );
				if (snRange != null)
					snRange = "The serial numbers " + snRange;
				else
					snRange = "All serial numbers";
				return snRange + " have been updated successfully.";
			}
			else
				return numSuccess + " of " + numToBeUpdated + " hardware entries have been updated.";
		}
		else
			return "Updating hardware entries in inventory...";
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
		
		boolean devTypeChanged = newDevTypeID != null && newDevTypeID.intValue() != devTypeID.intValue();
		
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getYukonDefID();
        List<LiteStarsLMHardware> hwList = null;
        try {
            hwList = InventoryUtils.getLMHardwareInRange( energyCompany, devTypeDefID, snFrom, snTo );
        } catch (PersistenceException e){
            status = STATUS_ERROR;
            errorMsg = e.getMessage();
            return;
        }        
		
		numToBeUpdated = hwList.size();
		if (numToBeUpdated == 0) {
			status = STATUS_ERROR;
			errorMsg = "There was no " + DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getEntryText() + " found in the given range of serial numbers.";
			return;
		}
		
		for (int i = 0; i < hwList.size(); i++) {
			LiteStarsLMHardware liteHw = hwList.get(i);
			
			if (devTypeChanged) {
				boolean hwExist = false;
				try {
					StarsSearchDao starsSearchDao = YukonSpringHook.getBean("starsSearchDao", StarsSearchDao.class);
					hwExist = starsSearchDao.searchLMHardwareBySerialNumber(liteHw.getManufacturerSerialNumber(), energyCompany) != null;
				}
				catch (ObjectInOtherEnergyCompanyException e) {
					hwExist = true;
				}
				
				if (hwExist) {
					hardwareSet.add( liteHw );
					numFailure++;
					continue;
				}
			}
			
			try {
				com.cannontech.stars.database.data.hardware.LMHardwareBase hardware =
						new com.cannontech.stars.database.data.hardware.LMHardwareBase();
				com.cannontech.stars.database.db.hardware.InventoryBase invDB = hardware.getInventoryBase();
				com.cannontech.stars.database.db.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
				
				StarsLiteFactory.setLMHardwareBase( hardware, liteHw );
				if (newDevTypeID != null) {
					invDB.setCategoryID( new Integer(InventoryUtils.getInventoryCategoryID(newDevTypeID.intValue(), energyCompany)) );
					hwDB.setLMHardwareTypeID( newDevTypeID );
				}
				if (companyID != null)
					invDB.setInstallationCompanyID( companyID );
				if (recvDate != null)
					invDB.setReceiveDate( recvDate );
				if (voltageID != null)
					invDB.setVoltageID( voltageID );
				if (routeID != null)
					hwDB.setRouteID( routeID );
				
				hardware = Transaction.createTransaction( Transaction.UPDATE, hardware ).execute();
					StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				
				if (devTypeChanged && liteHw.isExtended()) {
					liteHw.updateThermostatType();
					if (liteHw.isThermostat())
						liteHw.setThermostatSettings( energyCompany.getThermostatSettings(liteHw) );
					else
						liteHw.setThermostatSettings( null );
				}
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						if (!liteHw.isExtended()) StarsLiteFactory.extendLiteInventoryBase( liteHw, energyCompany );
						
						for (int j = 0; j < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); j++) {
							StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(j);
							if (starsInv.getInventoryID() == liteHw.getInventoryID()) {
								StarsLiteFactory.setStarsInv( starsInv, liteHw, energyCompany );
								break;
							}
						}
					}
				}
				
				numSuccess++;
			}
			catch (com.cannontech.database.TransactionException e) {
				CTILogger.error( e.getMessage(), e );
				hardwareSet.add( liteHw );
				numFailure++;
			}
			
			if (isCanceled) {
				status = STATUS_CANCELED;
				return;
			}
		}
		
		String snRange = InventoryManagerUtil.getSNRange( snFrom, snTo );
		if (snRange == null) snRange = "all serial numbers";
		String logMsg = "Serial Range:" + snRange
				+ ",Old Device Type:" + DaoFactory.getYukonListDao().getYukonListEntry(devTypeID.intValue()).getEntryText();
		if (newDevTypeID != null)
			logMsg += ",New Device Type:" + DaoFactory.getYukonListDao().getYukonListEntry(newDevTypeID.intValue()).getEntryText();
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_UPDATE_RANGE, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
					"Those serial numbers may already exist in the new device type.</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			if (confirmOnMessagePage)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}

}
