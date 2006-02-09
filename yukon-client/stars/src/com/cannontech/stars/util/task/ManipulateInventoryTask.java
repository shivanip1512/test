package com.cannontech.stars.util.task;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMHardwareBase;
import com.cannontech.database.db.stars.hardware.InventoryBase;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;

/**
 * @author jdayton
 */
public class ManipulateInventoryTask extends TimeConsumingTask {
	
	LiteStarsEnergyCompany currentCompany = null;
    Integer newEnergyCompanyID = null;
	Integer newDevTypeID = null;
    ArrayList selectedInventory = new ArrayList();
    String invenStatus = null;
	Integer newDevStateID = null;
	Integer newServiceCompanyID = null;
    Integer newWarehouseID = null;
	HttpServletRequest request = null;
    String serialFrom = null;
    String serialTo = null;
    boolean devTypeChanged = false;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeUpdated = 0;
	
    public ManipulateInventoryTask(LiteStarsEnergyCompany currentCompany, Integer newEnergyCompanyID, ArrayList selectedInventory, Integer newDevTypeID,
        Integer newDevStateID, Integer newServiceCompanyID, Integer newWarehouseID, HttpServletRequest request)
    {
        this.currentCompany = currentCompany;
        this.newEnergyCompanyID = newEnergyCompanyID;
        this.selectedInventory = selectedInventory;
        this.newDevTypeID = newDevTypeID;
        this.newServiceCompanyID = newServiceCompanyID;
        this.newWarehouseID = newWarehouseID;
        this.newDevStateID = newDevStateID;
        this.request = request;
    }
    
    public ManipulateInventoryTask(LiteStarsEnergyCompany currentCompany, Integer newEnergyCompanyID, ArrayList selectedInventory, Integer newDevTypeID,
		Integer newDevStateID, Integer newServiceCompanyID, Integer newWarehouseID, String serialTo, String serialFrom, HttpServletRequest request)
	{
        this.currentCompany = currentCompany;
        this.newEnergyCompanyID = newEnergyCompanyID;
		this.selectedInventory = selectedInventory;
		this.newDevTypeID = newDevTypeID;
		this.newServiceCompanyID = newServiceCompanyID;
		this.newWarehouseID = newWarehouseID;
        this.newDevStateID = newDevStateID;
		this.request = request;
        this.serialTo = serialTo;
        this.serialFrom = serialFrom;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.TimeConsumingTask#getProgressMsg()
	 */
	public String getProgressMsg() {
		if (numToBeUpdated > 0) {
			if (status == STATUS_FINISHED && numFailure == 0) {
				if (invenStatus != null)
					invenStatus = "Failures detected.  The remainder of the hardware entries";
				else
                    invenStatus = "All hardware entries";
				return invenStatus + " have been updated successfully.";
			}
			else
				return numSuccess + " of " + numToBeUpdated + " hardware entries have been updated.";
		}
		else
			return "Updating hardware entries in selected inventory...";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
    {
		status = STATUS_RUNNING;
		
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		ArrayList descendants = ECUtils.getAllDescendants( currentCompany );
        ArrayList hwList = selectedInventory;
        /*boolean devTypeChanged = newDevTypeID != null && newDevTypeID.intValue() != devTypeID.intValue();
		int devTypeDefID = YukonListFuncs.getYukonListEntry(devTypeID.intValue()).getYukonDefID();*/
		
		numToBeUpdated = hwList.size();
		if (numToBeUpdated == 0) 
        {
			status = STATUS_ERROR;
			errorMsg = "There are no inventory entries selected on which to apply these changes.";
			return;
		}
		
		for (int i = 0; i < hwList.size(); i++) 
        {
			Pair pair = (Pair)hwList.get(i);
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware)pair.getFirst();
			LiteStarsEnergyCompany oldMember = (LiteStarsEnergyCompany)pair.getSecond();
            LiteStarsEnergyCompany newMember = null;
            if(newEnergyCompanyID == null || newEnergyCompanyID.intValue() == oldMember.getLiteID())
                newMember = oldMember;
            else
            {
                for(int j = 0; j < descendants.size(); j++)
                {
                    if(((LiteStarsEnergyCompany)descendants.get(j)).getEnergyCompanyID().intValue() == newEnergyCompanyID)
                    {
                        newMember = ((LiteStarsEnergyCompany)descendants.get(j));
                    }
                    break;
                }
            }
            /*if (devTypeChanged) 
            {
				boolean hwExist = false;
				try {
					hwExist = energyCompany.searchForLMHardware( newDevTypeID.intValue(), liteHw.getManufacturerSerialNumber() ) != null;
				}
				catch (ObjectInOtherEnergyCompanyException e) {
					hwExist = true;
				}
				
				if (hwExist) {
					hardwareSet.add( liteHw );
					numFailure++;
					continue;
				}
			}*/
			
			try 
            {
				LMHardwareBase hardware = new LMHardwareBase();
				InventoryBase invDB = hardware.getInventoryBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
                
				StarsLiteFactory.setLMHardwareBase( hardware, liteHw );
				
                if(newDevTypeID != null && hwDB.getLMHardwareTypeID().intValue() == newDevTypeID)
                    devTypeChanged = true;
                else
                    devTypeChanged = false;
                
                if (devTypeChanged) 
                {
					invDB.setCategoryID( new Integer(InventoryUtils.getInventoryCategoryID(newDevTypeID.intValue(), newMember)) );
					hwDB.setLMHardwareTypeID( newDevTypeID );
				}
				if (newServiceCompanyID != null)
					invDB.setInstallationCompanyID( newServiceCompanyID );
				
				/*if (routeID != null)
					hwDB.setRouteID( routeID );*/
				
				hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
						Transaction.createTransaction( Transaction.UPDATE, hardware ).execute();
				StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
				
                //liteHw.getDeviceStatus()
                /*
                 * Device status is going to be a pain.  It is generated by events.  For Xcel,
                 * we will need a static field.
                 */
                
				if (devTypeChanged && liteHw.isExtended()) {
					liteHw.updateThermostatType();
					if (liteHw.isThermostat())
						liteHw.setThermostatSettings( newMember.getThermostatSettings(liteHw) );
					else
						liteHw.setThermostatSettings( null );
				}
				
				if (liteHw.getAccountID() > 0) {
					StarsCustAccountInformation starsAcctInfo = newMember.getStarsCustAccountInformation( liteHw.getAccountID() );
					if (starsAcctInfo != null) {
						if (!liteHw.isExtended()) StarsLiteFactory.extendLiteInventoryBase( liteHw, newMember );
						
						for (int j = 0; j < starsAcctInfo.getStarsInventories().getStarsInventoryCount(); j++) {
							StarsInventory starsInv = starsAcctInfo.getStarsInventories().getStarsInventory(j);
							if (starsInv.getInventoryID() == liteHw.getInventoryID()) {
								StarsLiteFactory.setStarsInv( starsInv, liteHw, newMember );
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
		
		if (invenStatus == null) invenStatus = "all selected inventory entries";
		String logMsg = "Inventory Altered:" + invenStatus + "updated.";
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_MASS_SELECTION, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
		
		if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
					"Those serial numbers may already exist with existing settings.</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/ResultSet.jsp");
			if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}

}
