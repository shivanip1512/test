package com.cannontech.stars.util.task;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.Pair;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMHardwareBase;
import com.cannontech.database.data.stars.report.WorkOrderBase;
import com.cannontech.database.db.stars.hardware.InventoryBase;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.util.*;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.ManipulationBean;
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
    boolean hasChanged = false;
    boolean devTypeChanged = false;
    boolean stateChanged = false;
	
	ArrayList hardwareSet = new ArrayList();
	int numSuccess = 0, numFailure = 0;
	int numToBeUpdated = 0;
    
    ArrayList failedSerialNumbers = new ArrayList();
	
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
                    invenStatus = "Selected hardware entries";
                else
                    invenStatus = "All hardware entries";
                return invenStatus + " have been updated successfully.";
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
	public void run() 
    {
		status = STATUS_RUNNING;
		
		HttpSession session = request.getSession(false);
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        ManipulationBean mBean = (ManipulationBean) session.getAttribute("manipBean"); 
        
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
            
			try 
            {
				LMHardwareBase hardware = new LMHardwareBase();
				InventoryBase invDB = hardware.getInventoryBase();
				com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
                
				StarsLiteFactory.setLMHardwareBase( hardware, liteHw );
				
                if(newDevTypeID != null && hwDB.getLMHardwareTypeID().intValue() != newDevTypeID)
                {
					invDB.setCategoryID( new Integer(InventoryUtils.getInventoryCategoryID(newDevTypeID.intValue(), newMember)) );
					hwDB.setLMHardwareTypeID( newDevTypeID );
                    hasChanged = true;
				}
                
                /**
                 * We might have a problem here if the selected are not all in the same energy company.
                 * Service company IDs would then not be valid for all of them.
                 */
				if (newServiceCompanyID != null && invDB.getInstallationCompanyID().intValue() != newServiceCompanyID)
                {
					invDB.setInstallationCompanyID( newServiceCompanyID );
                    hasChanged = true;
                }
                
                /**
                 * Device status can be confusing right now.  Xcel needs a static field, but previously,
                 * our code expected to take the state from the old event processing.
                 */
                if (newDevStateID != null && invDB.getCurrentStateID().intValue() != newDevStateID)
                {
                    invDB.setCurrentStateID(newDevStateID);
                    hasChanged = true;
                    stateChanged = true;
                }
                
                if (newWarehouseID != null && Warehouse.getWarehouseFromInventoryID(invDB.getInventoryID()) != newDevStateID)
                {
                    Warehouse.moveInventoryToAnotherWarehouse(invDB.getInventoryID().intValue(), newWarehouseID.intValue());
                    hasChanged = true;
                }

                if( hasChanged)
                {
                    hardware = (com.cannontech.database.data.stars.hardware.LMHardwareBase)
                    Transaction.createTransaction( Transaction.UPDATE, hardware ).execute();
                    StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
                    
                    
                    if (liteHw.isExtended()) {
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
                    if( stateChanged )
                        EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_INVENTORY, invDB.getCurrentStateID().intValue(), invDB.getInventoryID().intValue());
                        numSuccess++;
                        /*DBChangeMsg dbChangeMessage = new DBChangeMsg(
                                workOrderBase.getWorkOrderBase().getOrderID(),
                                DBChangeMsg.,
                                DBChangeMsg.CAT_LM_HARDWARE,
                                DBChangeMsg.CAT_LM_HARDWARE,
                                DBChangeMsg.CHANGE_TYPE_UPDATE
                            );
                        ServerUtils.handleDBChangeMsg(dbChangeMessage);*/
                }
                
                
            }
            catch (Exception e) 
            {
                CTILogger.error( e.getMessage(), e );
                hardwareSet.add( liteHw );
                failedSerialNumbers.add(liteHw.getManufacturerSerialNumber());
                numFailure++;
            }
                
            if (isCanceled) 
            {
                status = STATUS_CANCELED;
                return;
            }
        }
		
		if (invenStatus == null) invenStatus = "Selected inventory entries";
		String logMsg = "Inventory Altered:" + invenStatus + "updated.";
		ActivityLogger.logEvent( user.getUserID(), ActivityLogActions.INVENTORY_MASS_SELECTION, logMsg );
		
		status = STATUS_FINISHED;
		session.removeAttribute( InventoryManagerUtil.INVENTORY_SET );
        mBean.setFailures(numFailure);
        mBean.setSuccesses(numSuccess);
        mBean.setFailedSerialNumbers(failedSerialNumbers);
        session.setAttribute("manipBean", mBean);
		
        /**
         * Some of this also needs to be cleaned up.  Much of this is left over from the original plan
         * of doing this part like the old UpdateSNTask, using the same JSP, etc.
         */
        if (numFailure > 0) {
			String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries updated successfully.</span><br>" +
					"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
					"Those serial numbers may already exist with specified settings.</span><br>";
			
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
			session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
			session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/InvenResultSet.jsp");
			if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null)
				session.setAttribute(ServletUtils.ATT_REFERRER, session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
		}
	}

}
