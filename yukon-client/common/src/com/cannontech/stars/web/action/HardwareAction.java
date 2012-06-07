package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLMConfiguration;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfigResponse;

public class HardwareAction {

    /* from UpdateLMHardwareConfigAction */
    public static boolean isToConfig(LiteStarsLMHardware liteHw, LiteStarsCustAccountInformation liteAcctInfo) {
    	for (LiteStarsAppliance liteApp : liteAcctInfo.getAppliances()) {
            if (liteApp.getInventoryID() == liteHw.getInventoryID()) {
                return true;
            }
        }
        
        return false;
    }

    /* from deleteLMHardwareAction */
    public static void removeInventory(StarsDeleteLMHardware deleteHw, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
        throws WebClientException
    {
        ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao", ApplianceDao.class);
        
        try {
            StarsInventoryBaseDao starsInventoryBaseDao = 
                YukonSpringHook.getBean("starsInventoryBaseDao", StarsInventoryBaseDao.class);
            LiteInventoryBase liteInv = starsInventoryBaseDao.getByInventoryId(deleteHw.getInventoryID());
            
            try {
                // Unenrolls the inventory from all its programs (inside below catch block as well)
                EnrollmentHelperService enrollmentHelperService = YukonSpringHook.getBean("enrollmentService", EnrollmentHelperService.class);
                EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
                
                CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao", CustomerAccountDao.class);
                CustomerAccount customerAccount = customerAccountDao.getById(liteAcctInfo.getAccountID());
                enrollmentHelper.setAccountNumber(customerAccount.getAccountNumber());
    
                LMHardwareBaseDao lmHardwareBaseDao = YukonSpringHook.getBean("hardwareBaseDao", LMHardwareBaseDao.class);
                LMHardwareBase lmHardwareBase = lmHardwareBaseDao.getById(deleteHw.getInventoryID());
                enrollmentHelper.setSerialNumber(lmHardwareBase.getManufacturerSerialNumber());
                
                enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.UNENROLL, energyCompany.getUser());
                
            } catch (NotFoundException e) {
                // able to ignore because it is possible that we don't have an LMHardwareBase but that we have a reference to a yukonPaobject instead
            }
            
            if (deleteHw.getDeleteFromInventory()) {
                InventoryManagerUtil.deleteInventory( liteInv, energyCompany, deleteHw.getDeleteFromYukon() );
            }
            else {
                java.util.Date removeDate = deleteHw.getRemoveDate();
                if (removeDate == null) {
                    removeDate = new java.util.Date();
                }
                
                // Add "Uninstall" to hardware events
                com.cannontech.stars.database.data.event.LMHardwareEvent event = new com.cannontech.stars.database.data.event.LMHardwareEvent();
                com.cannontech.stars.database.db.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
                com.cannontech.stars.database.db.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
                
                int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
                int uninstallActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
                
                eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
                eventBaseDB.setActionID( new Integer(uninstallActID) );
                eventBaseDB.setEventDateTime( removeDate );
                if (liteAcctInfo != null) {
                    eventBaseDB.setNotes( "Removed from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() );
                }
                eventDB.setInventoryID( new Integer(liteInv.getInventoryID()) );
                event.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
                
                event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
                
                if (liteInv instanceof LiteStarsLMHardware) {
                    applianceDao.deleteAppliancesByAccountIdAndInventoryId(liteAcctInfo.getAccountID(), liteInv.getInventoryID());
                }
                
                // Removes any entries found in the inventoryBase Table
                com.cannontech.stars.database.db.hardware.InventoryBase invDB =
                        new com.cannontech.stars.database.db.hardware.InventoryBase();
                StarsLiteFactory.setInventoryBase( invDB, liteInv );
                
                invDB.setAccountID( new Integer(CtiUtilities.NONE_ZERO_ID) );
                invDB.setRemoveDate( removeDate );
                invDB.setDeviceLabel( "" );
                Transaction.createTransaction( Transaction.UPDATE, invDB ).execute();
                
            }
            
            if (liteAcctInfo != null) {
                if (InventoryUtils.isLMHardware( liteInv.getCategoryID() )) {
                    List<LiteStarsAppliance> liteApps = liteAcctInfo.getAppliances();
                    
                    for (int i = 0; i < liteApps.size(); i++) {
                        LiteStarsAppliance liteApp = liteApps.get(i);
                        
                        if (liteApp.getInventoryID() == liteInv.getInventoryID()) {
                            liteApp.setInventoryID( 0 );
                            
                            for (int j = 0; j < liteAcctInfo.getPrograms().size(); j++) {
                                LiteStarsLMProgram liteProg = liteAcctInfo.getPrograms().get(j);
                                
                                if (liteProg.getProgramID() == liteApp.getProgramID()) {
                                    liteApp.setProgramID(0);
                                    liteProg.setGroupID( 0 );
                                    break;
                                }
                            }
                        }
                    }
                }
                
                liteAcctInfo.getInventories().remove( new Integer(liteInv.getInventoryID()) );
            }
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            if (e instanceof WebClientException)
                throw (WebClientException)e;
    
            throw new WebClientException( "Failed to remove the hardware", e );
        }
    }

    /* from UpdateLMHardwareAction
     * formerly name parseResponse()
     */
    public static void removeRouteResponse(int origInvID, StarsInventory starsInv, StarsCustAccountInformation starsAcctInfo, HttpSession session) {
              StarsInventories starsInvs = starsAcctInfo.getStarsInventories();
    
              for (int i = 0; i < starsInvs.getStarsInventoryCount(); i++) {
                  if (starsInvs.getStarsInventory(i).getInventoryID() == origInvID) {
                      starsInvs.removeStarsInventory(i);
                      break;
                  }
              }
    
              StarsAppliances starsApps = starsAcctInfo.getStarsAppliances();
              if (starsApps != null) {
                  for (int i = 0; i < starsApps.getStarsApplianceCount(); i++) {
                      if (starsApps.getStarsAppliance(i).getInventoryID() == origInvID) {
                        starsApps.getStarsAppliance(i).setInventoryID(starsInv.getInventoryID());
                    }
                  }
              }
    
              String deviceLabel = ServletUtils.getInventoryLabel(starsInv);
              int invNo = 0;
              for (; invNo < starsInvs.getStarsInventoryCount(); invNo++) {
                  String label = ServletUtils.getInventoryLabel(starsInvs.getStarsInventory(invNo));
                  if (label.compareTo(deviceLabel) > 0) {
                    break;
                }
              }
    
              starsInvs.addStarsInventory(invNo, starsInv);
    
              if (session != null) {
                  String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
                  if (redirect != null) {
                      // redirect should ends with "InvNo=X" or "Item=X", replace "X"
                      // with the new location
                      int pos = redirect.lastIndexOf("InvNo=");
                      if (pos >= 0) {
                          // Request from Inventory.jsp
                          session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos + 6) + invNo);
                      } else {
                          pos = redirect.lastIndexOf("Item=");
                          if (pos >= 0) {
                              // Request from NewLabel.jsp, only count thermostats
                              int itemNo = 0;
                              for (int i = 0; i < invNo; i++) {
                                  StarsInventory inv = starsInvs.getStarsInventory(i);
                                  if (inv.getLMHardware() != null && inv.getLMHardware().getStarsThermostatSettings() != null) {
                                    itemNo++;
                                }
                              }
    
                              session.setAttribute(ServletUtils.ATT_REDIRECT, redirect.substring(0, pos + 5) + itemNo);
                          }
                      }
                  }
    
                  StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
                  if (StarsUtils.isOperator(user.getYukonUser())) {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware information updated successfully");
                } else {
                    session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Thermostat name updated successfully");
                }
              }
          }

    /* from UpdateLMHardwareConfigAction */
    public static void saveSwitchCommand(LiteStarsLMHardware liteHw, String commandType, LiteStarsEnergyCompany energyCompany) {
        SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
        cmd.setEnergyCompanyID(energyCompany.getLiteID());
        cmd.setAccountID(liteHw.getAccountID());
        cmd.setInventoryID(liteHw.getInventoryID());
        cmd.setCommandType(commandType);
    
        SwitchCommandQueue.getInstance().addCommand(cmd, true);
    }

    /* from UpdateLMHardwareConfigAction */    
    public static void updateLMConfiguration(
                                             StarsLMConfiguration starsHwConfig,
                                             LiteStarsLMHardware liteHw,
                                             LiteStarsEnergyCompany energyCompany)
            throws WebClientException {
        com.cannontech.stars.database.data.hardware.LMConfigurationBase config = new com.cannontech.stars.database.data.hardware.LMConfigurationBase();
        com.cannontech.stars.database.db.hardware.LMConfigurationBase configDB = config.getLMConfigurationBase();
    
        if (starsHwConfig.getColdLoadPickup() != null) {
            if (starsHwConfig.getColdLoadPickup().length() > 0) {
                configDB.setColdLoadPickup(starsHwConfig.getColdLoadPickup());
            } else {
                configDB.setColdLoadPickup(CtiUtilities.STRING_NONE);
            }
        }
        if (starsHwConfig.getTamperDetect() != null) {
            if (starsHwConfig.getTamperDetect().length() > 0) {
                configDB.setTamperDetect(starsHwConfig.getTamperDetect());
            } else {
                configDB.setTamperDetect(CtiUtilities.STRING_NONE);
            }
        }
    
        if (starsHwConfig.getExpressCom() != null) {
            com.cannontech.stars.database.db.hardware.LMConfigurationExpressCom xcom = new com.cannontech.stars.database.db.hardware.LMConfigurationExpressCom();
            xcom.setServiceProvider(new Integer(starsHwConfig.getExpressCom().getServiceProvider()));
            xcom.setGEO(new Integer(starsHwConfig.getExpressCom().getGEO()));
            xcom.setSubstation(new Integer(starsHwConfig.getExpressCom().getSubstation()));
            xcom.setFeeder(new Integer(starsHwConfig.getExpressCom().getFeeder()));
            xcom.setZip(new Integer(starsHwConfig.getExpressCom().getZip()));
            xcom.setUserAddress(new Integer(starsHwConfig.getExpressCom().getUserAddress()));
            xcom.setProgram(starsHwConfig.getExpressCom().getProgram());
            xcom.setSplinter(starsHwConfig.getExpressCom().getSplinter());
            config.setExpressCom(xcom);
        } else if (starsHwConfig.getVersaCom() != null) {
            com.cannontech.stars.database.db.hardware.LMConfigurationVersaCom vcom = new com.cannontech.stars.database.db.hardware.LMConfigurationVersaCom();
            vcom.setUtilityID(new Integer(starsHwConfig.getVersaCom().getUtility()));
            vcom.setSection(new Integer(starsHwConfig.getVersaCom().getSection()));
            vcom.setClassAddress(new Integer(starsHwConfig.getVersaCom().getClassAddress()));
            vcom.setDivisionAddress(new Integer(starsHwConfig.getVersaCom().getDivision()));
            config.setVersaCom(vcom);
        } else if (starsHwConfig.getSA205() != null) {
            com.cannontech.stars.database.db.hardware.LMConfigurationSA205 sa205 = new com.cannontech.stars.database.db.hardware.LMConfigurationSA205();
            sa205.setSlot1(new Integer(starsHwConfig.getSA205().getSlot1()));
            sa205.setSlot2(new Integer(starsHwConfig.getSA205().getSlot2()));
            sa205.setSlot3(new Integer(starsHwConfig.getSA205().getSlot3()));
            sa205.setSlot4(new Integer(starsHwConfig.getSA205().getSlot4()));
            sa205.setSlot5(new Integer(starsHwConfig.getSA205().getSlot5()));
            sa205.setSlot6(new Integer(starsHwConfig.getSA205().getSlot6()));
            config.setSA205(sa205);
        } else if (starsHwConfig.getSA305() != null) {
            com.cannontech.stars.database.db.hardware.LMConfigurationSA305 sa305 = new com.cannontech.stars.database.db.hardware.LMConfigurationSA305();
            sa305.setUtility(new Integer(starsHwConfig.getSA305().getUtility()));
            sa305.setGroupAddress(new Integer(starsHwConfig.getSA305().getGroup()));
            sa305.setDivision(new Integer(starsHwConfig.getSA305().getDivision()));
            sa305.setSubstation(new Integer(starsHwConfig.getSA305().getSubstation()));
            sa305.setRateFamily(new Integer(starsHwConfig.getSA305().getRateFamily()));
            sa305.setRateMember(new Integer(starsHwConfig.getSA305().getRateMember()));
            sa305.setRateHierarchy(new Integer(starsHwConfig.getSA305().getRateHierarchy()));
            config.setSA305(sa305);
        } else if (starsHwConfig.getSASimple() != null) {
            com.cannontech.stars.database.db.hardware.LMConfigurationSASimple simple = new com.cannontech.stars.database.db.hardware.LMConfigurationSASimple();
            simple.setOperationalAddress(starsHwConfig.getSASimple().getOperationalAddress());
            config.setSASimple(simple);
        }
    
        try {
            if (liteHw.getConfigurationID() == 0) {
                config = Transaction.createTransaction(Transaction.INSERT, config).execute();
    
                com.cannontech.stars.database.data.hardware.LMHardwareBase hw = new com.cannontech.stars.database.data.hardware.LMHardwareBase();
                com.cannontech.stars.database.db.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
                StarsLiteFactory.setLMHardwareBase(hw, liteHw);
                hwDB.setConfigurationID(config.getLMConfigurationBase().getConfigurationID());
    
                hwDB = Transaction.createTransaction(Transaction.UPDATE, hwDB).execute();
    
                liteHw.setConfigurationID(hwDB.getConfigurationID().intValue());
                LiteLMConfiguration liteCfg = new LiteLMConfiguration();
                StarsLiteFactory.setLiteLMConfiguration(liteCfg, config);
                liteHw.setLMConfiguration(liteCfg);
            } else {
                config.setConfigurationID(new Integer(liteHw.getConfigurationID()));
    
                // Check to see if the configuration is in both the parent and
                // the child table
                if (config.getExpressCom() != null && liteHw.getLMConfiguration().getExpressCom() == null) {
                    Transaction.createTransaction(Transaction.INSERT, config.getExpressCom()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();
                } else if (config.getVersaCom() != null && liteHw.getLMConfiguration().getVersaCom() == null) {
                    Transaction.createTransaction(Transaction.INSERT, config.getVersaCom()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();
                } else if (config.getSA205() != null && liteHw.getLMConfiguration().getSA205() == null) {
                    Transaction.createTransaction(Transaction.INSERT, config.getSA205()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();
                } else if (config.getSA305() != null && liteHw.getLMConfiguration().getSA305() == null) {
                    Transaction.createTransaction(Transaction.INSERT, config.getSA305()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();
                } else if (config.getSASimple() != null && liteHw.getLMConfiguration().getSASimple() == null) {
                    Transaction.createTransaction(Transaction.INSERT, config.getSASimple()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB).execute();
                } else {
                    config = Transaction.createTransaction(Transaction.UPDATE, config).execute();
                }
    
                StarsLiteFactory.setLiteLMConfiguration(liteHw.getLMConfiguration(), config);
            }
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            throw new WebClientException("Failed to update the hardware addressing tables");
        }
    }

    /* from LMHardwareConfigAction */
    public static StarsUpdateLMHardwareConfigResponse updateLMHardwareConfig(
                                                                             StarsUpdateLMHardwareConfig updateHwConfig,
                                                                             LiteStarsLMHardware liteHw,
                                                                             int userID,
                                                                             LiteStarsEnergyCompany energyCompany)
            throws WebClientException {
        LiteStarsCustAccountInformation liteAcctInfo = null;
        List<LiteStarsLMHardware> hwsToConfig = null;

        LiteYukonUser currentUser = DaoFactory.getYukonUserDao().getLiteYukonUser(userID);
    
        // save configuration first, so its available to compute groupID later
        // on
        if (updateHwConfig.getStarsLMConfiguration() != null) {
            updateLMConfiguration(updateHwConfig.getStarsLMConfiguration(), liteHw, energyCompany);
    
            hwsToConfig = new ArrayList<LiteStarsLMHardware>();
            hwsToConfig.add(liteHw);
        }
    
        CustomerAccount customerAccount = null;
        if (liteHw.getAccountID() > 0) {
            CustomerAccountDao customerAccountDao = YukonSpringHook.getBean("customerAccountDao",
                                                                            CustomerAccountDao.class);
            customerAccount = customerAccountDao.getById(liteHw.getAccountID());
    
            List<ProgramEnrollment> requests = new ArrayList<ProgramEnrollment>();
            for (int i = 0; i < updateHwConfig.getStarsLMHardwareConfigCount(); i++) {
                StarsLMHardwareConfig starsConfig = updateHwConfig.getStarsLMHardwareConfig(i);
    
                ProgramEnrollment enrollment = new ProgramEnrollment();
                enrollment.setInventoryId(liteHw.getInventoryID());
                enrollment.setAssignedProgramId(starsConfig.getProgramID());
                enrollment.setLmGroupId(starsConfig.getGroupID());
                enrollment.setRelay(starsConfig.getLoadNumber());
                requests.add(enrollment);
            }
    
    
            ProgramEnrollmentService programEnrollmentService = YukonSpringHook.getBean(
                                                                                        "starsProgramEnrollmentService",
                                                                                        ProgramEnrollmentService.class);
            hwsToConfig = programEnrollmentService.applyEnrollmentRequests(customerAccount, requests, liteHw, currentUser);
    
            if (!hwsToConfig.contains(liteHw)) {
                hwsToConfig.add(0, liteHw);
            }
            // refresh account info, after update program enrollment
            liteAcctInfo = energyCompany.getCustAccountInformation(liteHw.getAccountID(), true);
        }
    
        StarsInventories starsInvs = new StarsInventories();
        boolean disabled = false;
    
        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = Boolean.valueOf(trackHwAddr).booleanValue();
    
        for (int i = 0; i < hwsToConfig.size(); i++) {
            LiteStarsLMHardware lHw = hwsToConfig.get(i);
    
            if (!updateHwConfig.getSaveConfigOnly()) {
                boolean toConfig = true;
                if (liteAcctInfo != null && !useHardwareAddressing) {
                    toConfig = isToConfig(lHw, liteAcctInfo);
                }
                if (lHw.equals(liteHw)) {
                    disabled = !toConfig;
                }
    
                if (updateHwConfig.getSaveToBatch()) {
                    String commandType = (toConfig) ? SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE : SwitchCommandQueue.SWITCH_COMMAND_DISABLE;
                    saveSwitchCommand(lHw, commandType, energyCompany);
                } else {
                    if (toConfig) {
                        YukonSwitchCommandAction.sendConfigCommand(energyCompany, lHw, true, null);
                    } else {
                        YukonSwitchCommandAction.sendDisableCommand(energyCompany, lHw, null);
                    }
                }
            }
    
            if (liteAcctInfo != null) {
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(lHw, energyCompany);
                starsInvs.addStarsInventory(starsInv);
            }
        }
    
        // Log activity
        HardwareEventLogService hardwareEventLogService = 
            YukonSpringHook.getBean("hardwareEventLogService", HardwareEventLogService.class);
        
        String accountNumber = null;
        if (customerAccount != null) {
            accountNumber = customerAccount.getAccountNumber();
        }
        
        String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber();
        if (!disabled) {
            for (int i = 0; i < hwsToConfig.size(); i++) {
                LiteStarsLMHardware lHw = hwsToConfig.get(i);
                
                hardwareEventLogService.hardwareConfigUpdated(currentUser,
                                                              lHw.getManufacturerSerialNumber(),
                                                              accountNumber);
                
                if (!lHw.equals(liteHw)) {
                    logMsg += "," + lHw.getManufacturerSerialNumber();
                }
            }
        }
    
        String action = null;
        if (updateHwConfig.getSaveConfigOnly()) {
            action = ActivityLogActions.HARDWARE_SAVE_CONFIG_ONLY_ACTION;
        } else if (updateHwConfig.getSaveToBatch()) {
            action = ActivityLogActions.HARDWARE_SAVE_TO_BATCH_ACTION;
        } else if (disabled) {
            action = ActivityLogActions.HARDWARE_DISABLE_ACTION;
        } else {
            action = ActivityLogActions.HARDWARE_CONFIGURATION_ACTION;
        }
    
        int customerID = (liteAcctInfo != null) ? liteAcctInfo.getCustomer().getCustomerID() : 0;
        ActivityLogger.logEvent(userID, liteHw.getAccountID(),energyCompany.getLiteID(), customerID, action, logMsg);
    
        if (liteAcctInfo != null) {
            StarsUpdateLMHardwareConfigResponse resp = new StarsUpdateLMHardwareConfigResponse();
            resp.setStarsInventories(starsInvs);
            resp.setStarsLMPrograms(StarsLiteFactory.createStarsLMPrograms(liteAcctInfo, energyCompany));
            resp.setStarsAppliances(StarsLiteFactory.createStarsAppliances(liteAcctInfo.getAppliances(), energyCompany));
            return resp;
        }
    
        return null;
    }

}
