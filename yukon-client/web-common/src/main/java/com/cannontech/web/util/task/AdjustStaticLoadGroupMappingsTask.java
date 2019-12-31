package com.cannontech.web.util.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.hardware.LMHardwareConfiguration;
import com.cannontech.stars.database.db.hardware.StaticLoadGroupMapping;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.dao.StaticLoadGroupMappingDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.hardware.model.StarsStaticLoadGroupMapping;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandBuilder;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Lists;

public class AdjustStaticLoadGroupMappingsTask extends TimeConsumingTask {
    private LiteStarsEnergyCompany energyCompany = null;
    private boolean fullReset = false;
    private boolean sendConfig = true;
    private String redirect;
    private final HttpSession session;

    private List<StaticLoadGroupMapping> mappingsToAdjust = null;
    private List<LiteLmHardwareBase> hwsToAdjust = null;
    private final List<LiteLmHardwareBase> configurationSet = new ArrayList<>();
    private final List<String> failureInfo = new ArrayList<>();
    //don't need since liteHw already has the config loaded....HashMap<Integer, com.cannontech.database.db.stars.hardware.LMHardwareConfiguration> existingConfigEntries;
    private int numSuccess = 0;
    private int numFailure = 0;
    private int numToBeConfigured = 0;

    public AdjustStaticLoadGroupMappingsTask(LiteStarsEnergyCompany energyCompany,
            boolean fullReset, boolean sendConfig, String redirect, HttpSession session) {
        this.energyCompany = energyCompany;
        this.fullReset = fullReset;
        this.sendConfig = sendConfig;
        this.redirect = redirect;
        this.session = session;
    }

    @Override
    public String getProgressMsg() {
        if (numToBeConfigured == 0) {
            return "Mapping task is sorting static mappings and inventory items";
        } else if (fullReset) {
            if (status == STATUS_FINISHED && numFailure == 0) {
                return numSuccess + " switches have been mapped successfully to addressing groups.";
            } else {
                return numSuccess + " of " + numToBeConfigured + " switches have been mapped to addressing groups.";
            }
        } else {
            if (status == STATUS_FINISHED && numFailure == 0) {
                return numSuccess + " switches mapped to addressing groups.";
            } else {
                return numSuccess + " of " + numToBeConfigured + " switches mapped to addressing groups.";
            }
        }
    }

    @Override
    public void run() {
        StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);

        StringBuffer logEntry = new StringBuffer();
        if (fullReset) {
            logEntry.append("Full reset ");
        } else {
            logEntry.append("Adjusting all zero entries ");
        }
        if (sendConfig) {
            logEntry.append("and configs will be sent out if possible.");
        } else {
            logEntry.append("but configs will NOT be attempted.");
        }

        ActivityLogger.logEvent(user.getUserID(), "STATIC LOAD GROUP MAPPING ACTIVITY", logEntry.toString());

        mappingsToAdjust = StaticLoadGroupMapping.getAllStaticLoadGroups();
        if (mappingsToAdjust == null || mappingsToAdjust.size() < 1) {
            status = STATUS_ERROR;
            errorMsg = "There are no static load group mappings available.";
            return;
        }

        status = STATUS_RUNNING;

        boolean searchMembers = YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS,  user.getYukonUser())
                && energyCompany.hasChildEnergyCompanies();
        hwsToAdjust = new ArrayList<LiteLmHardwareBase>();

        InventoryBaseDao inventoryBaseDao = YukonSpringHook.getBean(InventoryBaseDao.class);
        LMHardwareControlGroupDao lmHardwareControlGroupDao = YukonSpringHook.getBean(LMHardwareControlGroupDao.class);

        List<YukonEnergyCompany> energyCompanyList = Lists.newArrayList();
        if (!searchMembers) {
            energyCompanyList.add(energyCompany);
        } else {
            energyCompanyList.addAll(ECUtils.getAllDescendants(energyCompany));
        }

        if (!fullReset) {
            // If not a full reset, we will want to only look for those with an addressing group ID of zero
            hwsToAdjust = inventoryBaseDao.getAllLMHardwareWithoutLoadGroups(energyCompanyList);
        } else {
            hwsToAdjust = inventoryBaseDao.getAllLMHardware(energyCompanyList);
        }

        numToBeConfigured = hwsToAdjust.size();
        if (numToBeConfigured == 0) {
            status = STATUS_ERROR;
            errorMsg = "No inventory could be loaded.";
            return;
        }

        String options = null;
        StaticLoadGroupMappingDao staticLoadGroupMappingDao = YukonSpringHook.getBean(StaticLoadGroupMappingDao.class);
        for (int i = 0; i < hwsToAdjust.size(); i++) {
            if (isCanceled) {
                status = STATUS_CANCELED;
                return;
            }

            LiteLmHardwareBase liteHw = hwsToAdjust.get(i);
            LiteStarsEnergyCompany company = energyCompany;

            //get the current Configuration
            LMHardwareConfiguration configDB = LMHardwareConfiguration.getLMHardwareConfigurationFromInvenID(liteHw.getInventoryID());

            if (configDB == null) {
                configurationSet.add(hwsToAdjust.get(i));
                numFailure++;
                failureInfo.add("An LMHardwareConfiguration entry for switch " + liteHw.getManufacturerSerialNumber()
                                + " could not be found.  It is likely this switch has not been added to an account.");
                continue;
            }

            StarsCustAccountInformationDao custAccountDao = YukonSpringHook.getBean(StarsCustAccountInformationDao.class);
            LiteAccountInfo liteAcctInfo = custAccountDao.getById(liteHw.getAccountID(), energyCompany.getEnergyCompanyId());
            //get zipCode
            LiteAddress address = YukonSpringHook.getBean(AddressDao.class).getByAddressId(liteAcctInfo.getAccountSite().getStreetAddressID());
            DrJmsMessagingService drJmsMessagingService = YukonSpringHook.getBean(DrJmsMessagingService.class);

            String zip = address.getZipCode();
            if (zip.length() > 5) {
                zip = zip.substring(0, 5);
            }
            //get ConsumptionType
            LiteCustomer cust = liteAcctInfo.getCustomer();
            Integer consumptionType = -1;
            if (cust instanceof LiteCICustomer && cust.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
                consumptionType = ((LiteCICustomer)cust).getCICustType();
            }
            //get ApplianceCategoryID
            Integer applianceCatID = -1;
            int programId = -1;
            for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
                if (liteApp.getApplianceID() == configDB.getApplianceID().intValue()){
                    applianceCatID = liteApp.getApplianceCategory().getApplianceCategoryId();
                    programId = liteApp.getProgramID();
                    break;
                }
            }
            if (applianceCatID == -1) {
                configurationSet.add(hwsToAdjust.get(i));
                numFailure++;
                failureInfo.add("An appliance could not be detected for serial number " + liteHw.getManufacturerSerialNumber()
                                + ".  It is likely that this switch is not assigned to an account, or no appliance was created on that account.");
                continue;
            }

            //get SwitchTypeID
            Integer devType = liteHw.getLmHardwareTypeID();
            StarsStaticLoadGroupMapping criteria = new StarsStaticLoadGroupMapping();
            criteria.setApplianceCategoryID(applianceCatID);
            criteria.setZipCode(zip);
            criteria.setConsumptionTypeID(consumptionType);
            criteria.setSwitchTypeID(devType);
            StarsStaticLoadGroupMapping groupMapping = staticLoadGroupMappingDao.getStaticLoadGroupMapping(criteria);
            if (groupMapping == null) {
                configurationSet.add(hwsToAdjust.get(i));
                numFailure++;
                failureInfo.add("A static mapping could not be determined for serial number " + liteHw.getManufacturerSerialNumber()
                                + ".  ApplianceCategoryID=" + applianceCatID + ", ZipCode=" + zip + ", ConsumptionTypeID="
                                + consumptionType + ",SwitchTypeID=" + devType);
                continue;
            }

            try {
                configDB.setAddressingGroupID(groupMapping.getLoadGroupID());
                try {
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();

                    LMHardwareControlGroup existingEnrollment =
                        lmHardwareControlGroupDao.findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(liteHw.getInventoryID(), programId, liteHw.getAccountID());
                    if (existingEnrollment != null) {
                        existingEnrollment.setLmGroupId(groupMapping.getLoadGroupID());
                        lmHardwareControlGroupDao.update(existingEnrollment);
                        
                        drJmsMessagingService.publishEnrollmentNotice(existingEnrollment);
                    } else {
                        configurationSet.add(hwsToAdjust.get(i));
                        numFailure++;
                        failureInfo.add("An LMHardwareControlGroup entry for switch " + liteHw.getManufacturerSerialNumber()
                                        + " could not be found.  It is likely this switch has not been added to an account.");
                        continue;
                    }
                } catch(TransactionException e) {
                    throw new WebClientException(e.getMessage());
                }
                options = "GroupID:" + groupMapping.getLoadGroupID();

                if (sendConfig) {
                    PorterExpressComCommandBuilder xcomCommandBuilder = YukonSpringHook.getBean(PorterExpressComCommandBuilder.class);
                    xcomCommandBuilder.fileWriteConfigCommand(company, liteHw, false, options);

                    if (liteHw.getAccountID() > 0) {
                        StarsCustAccountInformation starsAcctInfo = company.getStarsCustAccountInformation(liteHw.getAccountID());
                        if (starsAcctInfo != null) {
                            StarsInventory starsInv = StarsLiteFactory.createStarsInventory(liteHw, company);
                            StarsUtils.populateInventoryFields(starsAcctInfo, starsInv);
                        }
                    }
                }

                numSuccess++;
            } catch (WebClientException e) {
                CTILogger.error(e.getMessage() , e);
                if (errorMsg == null) {
                    errorMsg = e.getMessage();
                }
                configurationSet.add(hwsToAdjust.get(i));
                numFailure++;
                failureInfo.add("A static mapping could not be saved for serial number " + liteHw.getManufacturerSerialNumber()
                                    + ".  ApplianceCategoryID=" + applianceCatID + ", ZipCode=" + zip + ", ConsumptionTypeID="
                                    + consumptionType + ",SwitchTypeID=" + devType);
            }
        }

        status = STATUS_FINISHED;
         ActivityLogger.logEvent(user.getUserID(), "STATIC LOAD GROUP MAPPING ACTIVITY", "Attempt succeeded.");
        session.removeAttribute(InventoryManagerUtil.INVENTORY_SET);
        session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Addressing Groups were reset to static mapped values for " + numSuccess + " switches");

        if (numFailure > 0) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Addressing Groups adjustment failed for " + numFailure + " switches");
            session.setAttribute(InventoryManagerUtil.INVENTORY_SET, configurationSet);
            session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        }
    }
}
