package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMHardwareBase;
import com.cannontech.database.db.stars.hardware.InventoryBase;
import com.cannontech.database.db.stars.hardware.Warehouse;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.bean.ManipulationBean;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Maps;

public class ManipulateInventoryTask extends TimeConsumingTask {
    LiteStarsEnergyCompany currentCompany = null;
    Integer newEnergyCompanyID = null;
    Integer newDevTypeID = null;
    List<LiteInventoryBase> selectedInventory = new ArrayList<LiteInventoryBase>();
    String invenStatus = null;
    Integer newDevStateID = null;
    Integer newServiceCompanyID = null;
    Integer newWarehouseID = null;
    String serialFrom = null;
    String serialTo = null;
    boolean hasChanged = false;
    boolean devTypeChanged = false;
    boolean stateChanged = false;
    boolean warehouseChanged = false;
    private final boolean confirmOnMessagePage;
    private final String redirect;
    private final HttpSession session;

    List<LiteStarsLMHardware> hardwareSet = new ArrayList<LiteStarsLMHardware>();
    int numSuccess = 0, numFailure = 0;
    int numToBeUpdated = 0;

    List<String> failedSerialNumbers = new ArrayList<String>();

    public ManipulateInventoryTask(LiteStarsEnergyCompany currentCompany, Integer newEnergyCompanyID,
                                   List<LiteInventoryBase> selectedInventory, Integer newDevTypeID,
                                   Integer newDevStateID, Integer newServiceCompanyID,
                                   Integer newWarehouseID, boolean confirmOnMessagePage,
                                   String redirect, HttpSession session) {
        this.currentCompany = currentCompany;
        this.newEnergyCompanyID = newEnergyCompanyID;
        this.selectedInventory = selectedInventory;
        this.newDevTypeID = newDevTypeID;
        this.newServiceCompanyID = newServiceCompanyID;
        this.newWarehouseID = newWarehouseID;
        this.newDevStateID = newDevStateID;
        this.confirmOnMessagePage = confirmOnMessagePage;
        this.redirect = redirect;
        this.session = session;
    }

    @Override
    public String getProgressMsg() {
        if (numToBeUpdated > 0) {
            if (status == STATUS_FINISHED && numFailure == 0) {
                if (invenStatus != null) {
                    invenStatus = "Selected hardware entries";
                } else {
                    invenStatus = "All hardware entries";
                }
                return invenStatus + " have been updated successfully.";
            } else {
                return numSuccess + " of " + numToBeUpdated + " hardware entries have been updated.";
            }
        } else {
            return "Updating hardware entries in inventory...";
        }
    }

    public void run() {
        status = STATUS_RUNNING;

        StarsYukonUser user =
            (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
        ManipulationBean mBean = (ManipulationBean) session.getAttribute("manipBean");

        List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants(currentCompany);
        List<LiteInventoryBase> hwList = selectedInventory;

        numToBeUpdated = hwList.size();
        if (numToBeUpdated == 0) {
            status = STATUS_ERROR;
            errorMsg = "There are no inventory entries selected on which to apply these changes.";
            return;
        }

        YukonSelectionList deviceTypeList =
            currentCompany.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
        Map<Integer, YukonListEntry> deviceTypesByHardwareType = Maps.newHashMap();
        for (YukonListEntry listEntry : deviceTypeList.getYukonListEntries()) {
            deviceTypesByHardwareType.put(listEntry.getEntryID(), listEntry);
        }

        for (int i = 0; i < hwList.size(); i++) {
            LiteStarsLMHardware liteHw = null;
            LiteStarsEnergyCompany oldMember = null;

            Map<Integer, LiteStarsEnergyCompany> ecMap =
                StarsDatabaseCache.getInstance().getAllEnergyCompanyMap();

            LiteInventoryBase liteInventoryBase = hwList.get(i);
            liteHw = (LiteStarsLMHardware) liteInventoryBase;
            oldMember = ecMap.get(liteHw.getEnergyCompanyId());

            LiteStarsEnergyCompany newMember = null;
            if (newEnergyCompanyID == null
                || newEnergyCompanyID.intValue() == oldMember.getLiteID()) {
                newMember = oldMember;
            } else {
                for (int j = 0; j < descendants.size(); j++) {
                    if (descendants.get(j).getEnergyCompanyId() == newEnergyCompanyID) {
                        newMember = descendants.get(j);
                    }
                    break;
                }
            }

            YukonListEntry listEntry = deviceTypesByHardwareType.get(liteHw.getLmHardwareTypeID());
            if (InventoryBean.unsupportedDeviceTypes.containsKey(listEntry.getYukonDefID())) {
                String msg = "Manipulation not supported for device type " +
                    listEntry.getEntryText() + ".";
                CTILogger.error(msg);
                hardwareSet.add(liteHw);
                failedSerialNumbers.add("Serial #: " + liteHw.getManufacturerSerialNumber() +
                                        " - " + msg);
                numFailure++;
                continue;
            }

            try {
                LMHardwareBase hardware = new LMHardwareBase();
                
                InventoryBase invDB = hardware.getInventoryBase();
                com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB =
                    hardware.getLMHardwareBase();

                StarsLiteFactory.setLMHardwareBase(hardware, liteHw);

                if (newDevTypeID != null && hwDB.getLMHardwareTypeID().compareTo(newDevTypeID) != 0) {
                    invDB.setCategoryID(new Integer(InventoryUtils
                        .getInventoryCategoryID(newDevTypeID.intValue(), newMember)));
                    hwDB.setLMHardwareTypeID(newDevTypeID);
                    hasChanged = true;
                }

                /**
                 * TODO
                 * We might have a problem here if the selected are not all in the same energy
                 * company. Service company IDs would then not be valid for all of them.
                 */
                if (newServiceCompanyID != null
                    && invDB.getInstallationCompanyID().compareTo(newServiceCompanyID) != 0) {
                    // see if newServiceCompanyID is available for the energyCompany of the device
                    if (newMember.getServiceCompany(newServiceCompanyID) == null) {
                        String msg =
                            "Service company [" + newServiceCompanyID
                                    + "] is not available to energy company ["
                                    + newMember.getName() + "]";
                        CTILogger.error(msg);
                        hardwareSet.add(liteHw);
                        failedSerialNumbers.add("Serial #: " + liteHw.getManufacturerSerialNumber()
                                                + " - " + msg);
                        numFailure++;
                        continue;
                    }

                    invDB.setInstallationCompanyID(newServiceCompanyID);
                    hasChanged = true;
                }

                /**
                 * Device status can be confusing right now. Xcel needs a static field, but
                 * previously, our code expected to take the state from the old event processing.
                 */
                if (newDevStateID != null
                    && invDB.getCurrentStateID().compareTo(newDevStateID) != 0) {
                    invDB.setCurrentStateID(newDevStateID);
                    hasChanged = true;
                    stateChanged = true;
                }

                Integer oldWarehouseID =
                    Warehouse.getWarehouseIDFromInventoryID(invDB.getInventoryID());
                if (newWarehouseID != null && oldWarehouseID.compareTo(newWarehouseID) != 0) {
                    // see if newWarehouseID is available for the energyCompany of the device
                    if (newMember.getWarehouse(newWarehouseID) == null) {
                        String msg =
                            "Warehouse [" + newWarehouseID
                                    + "] is not available to energy company ["
                                    + newMember.getName() + "]";
                        CTILogger.error(msg);
                        hardwareSet.add(liteHw);
                        failedSerialNumbers.add("Serial #: " + liteHw.getManufacturerSerialNumber()
                                                + " - " + msg);
                        numFailure++;
                        continue;
                    }
                    warehouseChanged = true;
                }

                if (hasChanged) {
                    hardware =
                        Transaction.createTransaction(Transaction.UPDATE, hardware).execute();
                    StarsLiteFactory.setLiteStarsLMHardware(liteHw, hardware);

                    if (liteHw.isExtended()) {
                        liteHw.updateThermostatType();
                        if (liteHw.isThermostat())
                            liteHw.setThermostatSettings(newMember.getThermostatSettings(liteHw));
                        else
                            liteHw.setThermostatSettings(null);
                    }

                    if (liteHw.getAccountID() > 0) {
                        StarsCustAccountInformation starsAcctInfo =
                            newMember.getStarsCustAccountInformation(liteHw.getAccountID());
                        if (starsAcctInfo != null) {
                            if (!liteHw.isExtended())
                                StarsLiteFactory.extendLiteInventoryBase(liteHw, newMember);

                            for (int j = 0; j < starsAcctInfo.getStarsInventories()
                                .getStarsInventoryCount(); j++) {
                                StarsInventory starsInv =
                                    starsAcctInfo.getStarsInventories().getStarsInventory(j);
                                if (starsInv.getInventoryID() == liteHw.getInventoryID()) {
                                    StarsLiteFactory.setStarsInv(starsInv, liteHw, newMember);
                                    break;
                                }
                            }
                        }
                    }
                    if (stateChanged)
                        EventUtils.logSTARSEvent(user.getUserID(),
                                                 EventUtils.EVENT_CATEGORY_INVENTORY,
                                                 invDB.getCurrentStateID().intValue(),
                                                 invDB.getInventoryID().intValue(),
                                                 session);
                }

                if (warehouseChanged) {
                    Warehouse.moveInventoryToAnotherWarehouse(invDB.getInventoryID(),
                                                              newWarehouseID);
                }

                numSuccess++;
            } catch (Exception e) {
                CTILogger.error(e.getMessage(), e);
                hardwareSet.add(liteHw);
                failedSerialNumbers.add("Serial #: " + liteHw.getManufacturerSerialNumber() + " - "
                                        + e.getMessage());
                numFailure++;
            }

            if (isCanceled) {
                status = STATUS_CANCELED;
                return;
            }
        }

        if (invenStatus == null)
            invenStatus = "Selected inventory entries";
        String logMsg = "Inventory Altered:" + invenStatus + "updated.";
        ActivityLogger.logEvent(user.getUserID(),
                                ActivityLogActions.INVENTORY_MASS_SELECTION,
                                logMsg);

        status = STATUS_FINISHED;
        session.removeAttribute(InventoryManagerUtil.INVENTORY_SET);
        mBean.setFailures(numFailure);
        mBean.setSuccesses(numSuccess);
        mBean.setFailedManipulateResults(failedSerialNumbers);
        session.setAttribute("manipBean", mBean);

        /**
         * Some of this also needs to be cleaned up. Much of this is left over from the original
         * plan of doing this part like the old UpdateSNTask, using the same JSP, etc.
         */
        if (numFailure > 0) {
            String resultDesc = "<span class='ConfirmMsg'>" + numSuccess + " hardware entries " +
            		"updated successfully.</span><br>" +
            		"<span class='ErrorMsg'>" + numFailure + " hardware entries failed (listed below).<br>" +
            		"Those serial numbers may already exist with specified settings.</span><br>";

            session.setAttribute(InventoryManagerUtil.INVENTORY_SET_DESC, resultDesc);
            session.setAttribute(InventoryManagerUtil.INVENTORY_SET, hardwareSet);
            session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
            if (confirmOnMessagePage)
                session.setAttribute(ServletUtils.ATT_REFERRER,
                                     session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
        }
    }
}
