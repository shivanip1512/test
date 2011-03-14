package com.cannontech.stars.web.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.InventoryUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.user.UserUtils;
import com.google.common.collect.Lists;

public class YukonSwitchCommandAction {

    /* from YukonSwitchCommandAction */
    public static void fileWriteConfigCommand(
                                              LiteStarsEnergyCompany energyCompany,
                                              LiteStarsLMHardware liteHw,
                                              boolean forceInService,
                                              String options)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }
    
        // Parameter options corresponds to the infoString field of the switch
        // command queue.
        // It takes the format of "GroupID:XX;RouteID:XX"
        Integer optGroupID = null;
        /**
         * These changes originated with the PMSI Replacement Project at Xcel
         * Some assumptions are made here since we are writing out MACS format
         * configs
         * -Don't need RouteID
         * -Don't need to worry about hardware addressing (although MACS format
         * does support it)
         * -Also don't need to use the getConfigCommands() method like
         * sendConfigCommand() does
         */
    
        if (options != null) {
            String[] fields = options.split(";");
            for (int i = 0; i < fields.length; i++) {
                try {
                    if (fields[i].startsWith("GroupID:")) {
                        optGroupID = Integer.valueOf(fields[i].substring("GroupID:".length()));
                    }
                } catch (NumberFormatException e) {
                    CTILogger.error(e.getMessage(), e);
                }
            }
        }
    
        String loadGroupName = null;
        if (optGroupID != null) {
            try {
                loadGroupName = DaoFactory.getPaoDao()
                    .getYukonPAOName(optGroupID);
            } catch (NotFoundException e) {
                CTILogger.error(e.getMessage(), e);
            }
        } else {
            optGroupID = LMHardwareConfiguration.getLMHardwareConfigurationFromInvenID(liteHw.getInventoryID()).getAddressingGroupID();
            if (optGroupID > 0) {
                loadGroupName = DaoFactory.getPaoDao().getYukonPAOName(optGroupID);
            }
        }
    
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs + energyCompany.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
    
        File commFile;
    
        String cmd = null;
    
        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == HardwareConfigType.VERSACOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "1," + liteHw.getManufacturerSerialNumber() + "," + loadGroupName;
        } else if (hwConfigType == HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "1," + liteHw.getManufacturerSerialNumber() + "," + loadGroupName;
        } else if (hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Config";
        } else if (hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA305_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Config";
        } else {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            cmd = liteHw.getManufacturerSerialNumber();
        }
    
        if (loadGroupName == null) {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            if (optGroupID == null) {
                optGroupID = -1;
            }
            cmd = liteHw.getManufacturerSerialNumber() + ": Unable to find a load group in Yukon with the specified groupID of " + optGroupID;
        }
    
        fileWriteReceiverConfigLine(commFile, cmd);
        /*
         * TODO Not sure if we want to leave this in since we don't know for
         * sure that Gill has
         * run the written out commands to the switch. Could be false
         * advertising...
         * // Add "Config" to hardware events
         * try {
         * com.cannontech.database.data.stars.event.LMHardwareEvent event = new
         * com.cannontech.database.data.stars.event.LMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMHardwareEvent eventDB =
         * event.getLMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase
         * = event.getLMCustomerEventBase();
         * eventDB.setInventoryID( invID );
         * eventBase.setEventTypeID( hwEventEntryID );
         * eventBase.setActionID( configEntryID );
         * eventBase.setEventDateTime( now );
         * event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
         * event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
         * Transaction.createTransaction( Transaction.INSERT, event ).execute();
         * LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent)
         * StarsLiteFactory.createLite( event );
         * liteHw.getInventoryHistory().add( liteEvent );
         * }
         * catch (TransactionException e) {
         * CTILogger.error( e.getMessage(), e );
         * }
         */
    }

    /* from YukonSwitchCommandAction */
    public static void fileWriteDisableCommand(
                                               LiteStarsEnergyCompany energyCompany,
                                               LiteStarsLMHardware liteHw,
                                               Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().trim().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }
    
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs + energyCompany.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
    
        File commFile;
    
        String cmd = null;
    
        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == HardwareConfigType.VERSACOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",OUT";
        } else if (hwConfigType == HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",OUT";
        } else if (hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Deactivate";
        } else if (hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA305_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Deactivate";
        } else {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            cmd = liteHw.getManufacturerSerialNumber();
        }
    
        fileWriteReceiverConfigLine(commFile, cmd);
    
        /*
         * TODO Not sure if we want to leave this in since we don't know for
         * sure that Gill has
         * run the written out commands to the switch. could be false
         * advertising
         * // Add "Termination" to hardware events
         * try {
         * com.cannontech.database.data.stars.event.LMHardwareEvent event = new
         * com.cannontech.database.data.stars.event.LMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMHardwareEvent eventDB =
         * event.getLMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase
         * = event.getLMCustomerEventBase();
         * eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
         * eventBase.setEventTypeID( hwEventEntryID );
         * eventBase.setActionID( termEntryID );
         * eventBase.setEventDateTime( new Date() );
         * event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
         * event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
         * Transaction.createTransaction( Transaction.INSERT, event ).execute();
         * LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent)
         * StarsLiteFactory.createLite( event );
         * liteHw.getInventoryHistory().add( liteEvent );
         * liteHw.updateDeviceStatus();
         * }
         * catch (TransactionException e) {
         * CTILogger.error( e.getMessage(), e );
         * }
         */
    }

    /* from YukonSwitchCommandAction */
    public static void fileWriteEnableCommand(
                                              LiteStarsEnergyCompany energyCompany,
                                              LiteStarsLMHardware liteHw,
                                              Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().trim().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }
    
        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs
                              + energyCompany.getName());
        if (!ecDir.exists()) {
            ecDir.mkdirs();
        }
    
        File commFile;
    
        String cmd = null;
    
        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == HardwareConfigType.VERSACOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",IN";
        } else if (hwConfigType == HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",IN";
        } else if (hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Activate";
        } else if (hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId()) {
            commFile = new File(ecDir, ServerUtils.SA305_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Activate";
        } else {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            cmd = liteHw.getManufacturerSerialNumber();
        }
    
        fileWriteReceiverConfigLine(commFile, cmd);
    
        /*
         * TODO Not sure if we want to leave this in since we don't know for
         * sure that Gill has
         * run the written out commands to the switch. could be false
         * advertising
         * // Add "Activation Completed" to hardware events
         * Integer hwEventEntryID = new Integer(
         * energyCompany.getYukonListEntry(
         * YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
         * Integer actCompEntryID = new Integer(
         * energyCompany.getYukonListEntry(
         * YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
         * java.util.Date now = new java.util.Date();
         * try {
         * com.cannontech.database.data.stars.event.LMHardwareEvent event = new
         * com.cannontech.database.data.stars.event.LMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMHardwareEvent eventDB =
         * event.getLMHardwareEvent();
         * com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase
         * = event.getLMCustomerEventBase();
         * eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
         * eventBase.setEventTypeID( hwEventEntryID );
         * eventBase.setActionID( actCompEntryID );
         * eventBase.setEventDateTime( new Date() );
         * event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
         * event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
         * Transaction.createTransaction( Transaction.INSERT, event ).execute();
         * LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent)
         * StarsLiteFactory.createLite( event );
         * liteHw.getInventoryHistory().add( liteEvent );
         * liteHw.updateDeviceStatus();
         * }
         * catch (TransactionException e) {
         * CTILogger.error( e.getMessage(), e );
         * }
         */
    }

    /* from YukonSwitchCommandAction */
    public static void fileWriteReceiverConfigLine(File commFile, String cmd) {
        try {
            if (!commFile.exists()) {
                commFile.createNewFile();
            }
        } catch (IOException e) {
            CTILogger.error("Failed to create the switch command file...");
            CTILogger.error(e.getMessage(), e);
            return;
        }
    
        PrintWriter fw = null;
        try {
            fw = new PrintWriter(new FileWriter(commFile, true));
            fw.println(cmd);
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    /* from YukonSwitchCommandAction
     * formerly named parseResponse()
     */
    public static void populateInventoryFields(StarsCustAccountInformation starsAcctInfo, StarsInventory starsInv) {
                  StarsInventories inventories = starsAcctInfo.getStarsInventories();
    
                  for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
                      StarsInventory inv = inventories.getStarsInventory(i);
                      if (inv.getInventoryID() == starsInv.getInventoryID()) {
                          inventories.setStarsInventory(i, starsInv);
                          break;
                      }
                  }
              }

    // builds up the command from the information stored in the LMConfiguration
    // object/tables
    /* from YukonSwitchCommandAction */
    public static String[] getConfigCommands(LiteStarsLMHardware liteHw,
                                              YukonEnergyCompany energyCompany,
                                              boolean useHardwareAddressing,
                                              Integer groupID)
            throws WebClientException {
        
        StarsCustAccountInformationDao starsCustAccountInformationDao = 
            YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
        
        List<String> commands = new ArrayList<String>();
        String[] coldLoads = new String[4];
        String[] tamperDetects = new String[2];
    
        if (useHardwareAddressing) {
            if (liteHw.getLMConfiguration() == null) {
                throw new WebClientException("There is no configuration saved for serial #" + liteHw.getManufacturerSerialNumber() + ".");
            }
    
            String freezer = liteHw.getLMConfiguration().getColdLoadPickup();
            if (freezer.compareTo(CtiUtilities.STRING_NONE) != 0 && freezer.length() > 0) {
                coldLoads = StarsUtils.splitString(freezer, ",");
            }
    
            String foolDetect = liteHw.getLMConfiguration().getTamperDetect();
            if (foolDetect.compareTo(CtiUtilities.STRING_NONE) != 0 && foolDetect.length() > 0) {
                tamperDetects = StarsUtils.splitString(foolDetect, ",");
            }
    
            if (liteHw.getLMConfiguration().getExpressCom() != null) {
                String program = null;
                String splinter = null;
                String load = null;
                String[] programs = liteHw.getLMConfiguration().getExpressCom().getProgram().split(",");
                String[] splinters = liteHw.getLMConfiguration().getExpressCom().getSplinter().split(",");
    
                for (int loadNo = 1; loadNo <= 8; loadNo++) {
                    int prog = 0;
                    if (programs.length >= loadNo && programs[loadNo - 1].length() > 0) {
                        prog = Integer.parseInt(programs[loadNo - 1]);
                    }
                    int splt = 0;
                    if (splinters.length >= loadNo && splinters[loadNo - 1].length() > 0) {
                        splt = Integer.parseInt(splinters[loadNo - 1]);
                    }
    
                    if (prog > 0 || splt > 0) {
                        if (program == null) {
                            program = String.valueOf(prog);
                        } else {
                            program += "," + String.valueOf(prog);
                        }
                        if (splinter == null) {
                            splinter = String.valueOf(splt);
                        } else {
                            splinter += "," + String.valueOf(splt);
                        }
                        if (load == null) {
                            load = String.valueOf(loadNo);
                        } else {
                            load += "," + String.valueOf(loadNo);
                        }
                    }
                }
    
                String cmd = "putconfig xcom serial " + liteHw.getManufacturerSerialNumber()
                + " assign"
                + " S "+ liteHw.getLMConfiguration().getExpressCom().getServiceProvider()
                + " G "+ liteHw.getLMConfiguration().getExpressCom().getGEO()
                + " B " + liteHw.getLMConfiguration().getExpressCom().getSubstation()
                + " F " + liteHw.getLMConfiguration().getExpressCom().getFeeder()
                + " Z " + liteHw.getLMConfiguration().getExpressCom().getZip()
                + " U " + liteHw.getLMConfiguration().getExpressCom().getUserAddress();
                if (load != null) {
                    cmd += " P " + program + " R " + splinter + " Load " + load;
                }
                commands.add(cmd);
    
                // cold load pickup needs to be in a separate command for each
                // individual value
                for (int j = 0; j < coldLoads.length; j++) {
                    /*
                     * defaulting to minutes since the fields on the page also
                     * seem to make that assumption.
                     * putconfig xcom coldload rx=Z....
                     * r=Z sets all cold load times out to Z
                     * r1=Z sets relay 1 to Z.
                     * r1 to r15 are valid for the 15 possible relays, and
                     * multiple may be in the same message. r= and rx= may not
                     * be used at the same time.
                     * putconfig xcom coldload r1=3 r2=33 r4=333. //This sets
                     * relay 1, 2, and 4.
                     * Z may be in minutes, hours or seconds. so the following
                     * four commands are identical. Minutes is the default.
                     * r1=1h, r1=60m, r1=3600s, r1=60
                     * Expresscom can send at most:
                     * 32767 seconds or 546 minutes or 9 hours.
                     */
                    if (coldLoads[j].length() > 0) {
                        String clCmd = "putconfig xcom serial " + liteHw.getManufacturerSerialNumber() + " coldload r" + (j + 1) + "=" + coldLoads[j];
                        commands.add(clCmd);
                    }
                }
            } else if (liteHw.getLMConfiguration().getVersaCom() != null) {
                String cmd = "putconfig vcom serial " + liteHw.getManufacturerSerialNumber() 
                             + " assign" + " U " + liteHw.getLMConfiguration().getVersaCom().getUtilityID()
                             + " S " + liteHw.getLMConfiguration().getVersaCom().getSection()
                             + " C 0x" + Integer.toHexString(liteHw.getLMConfiguration().getVersaCom().getClassAddress())
                             + " D 0x" + Integer.toHexString(liteHw.getLMConfiguration().getVersaCom().getDivisionAddress());
                commands.add(cmd);
            } else if (liteHw.getLMConfiguration().getSA205() != null) {
                String cmd = "putconfig sa205 serial " + liteHw.getManufacturerSerialNumber() 
                             + " assign" + " 1=" + liteHw.getLMConfiguration().getSA205().getSlot1()
                             + ",2=" + liteHw.getLMConfiguration().getSA205().getSlot2()
                             + ",3=" + liteHw.getLMConfiguration().getSA205().getSlot3()
                             + ",4=" + liteHw.getLMConfiguration().getSA205().getSlot4()
                             + ",5=" + liteHw.getLMConfiguration().getSA205().getSlot5()
                             + ",6=" + liteHw.getLMConfiguration().getSA205().getSlot6();
                commands.add(cmd);
    
                // cold load pickup needs to be in a separate command for each
                // individual value
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        String clCmd = "putconfig sa205 serial " + liteHw.getManufacturerSerialNumber() + " coldload f" + (j + 1) + "=" + coldLoads[j];
                        commands.add(clCmd);
                    }
                }
    
                // tamper detect also needs to be in a separate command for each
                // value
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        String tdCmd = "putconfig sa205 serial " + liteHw.getManufacturerSerialNumber() + " tamper f" + (j + 1) + "=" + tamperDetects[j];
                        commands.add(tdCmd);
                    }
                }
            }
    
            else if (liteHw.getLMConfiguration().getSA305() != null) {
                String cmd = "putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() + " utility " 
                             + liteHw.getLMConfiguration().getSA305().getUtility()
                             + " assign" + " g=" + liteHw.getLMConfiguration().getSA305().getGroup()
                             + " d=" + liteHw.getLMConfiguration().getSA305().getDivision()
                             + " s=" + liteHw.getLMConfiguration().getSA305().getSubstation()
                             + " f=" + liteHw.getLMConfiguration().getSA305().getRateFamily()
                             + " m=" + liteHw.getLMConfiguration().getSA305().getRateMember();
                commands.add(cmd);
    
                // cold load pickup needs to be in a separate command for each
                // individual value
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        String clCmd = "putconfig sa305 serial " + liteHw.getManufacturerSerialNumber()
                                       + " utility " + liteHw.getLMConfiguration().getSA305().getUtility()
                                       + " coldload f" + (j + 1) + "=" + coldLoads[j];
                        commands.add(clCmd);
                    }
                }
    
                // tamper detect also needs to be in a separate command for each
                // value
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        String tdCmd = "putconfig sa305 serial " + liteHw.getManufacturerSerialNumber()
                                       + " utility " + liteHw.getLMConfiguration().getSA305().getUtility()
                                       + " tamper f" + (j + 1) + "=" + tamperDetects[j];
                        commands.add(tdCmd);
                    }
                }
            } else {
                throw new WebClientException("Unsupported configuration type for serial #" + liteHw.getManufacturerSerialNumber() + ".");
            }
        } else if (groupID != null) {
            try {
                String groupName = DaoFactory.getPaoDao().getYukonPAOName(groupID.intValue());
                String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " template '" + groupName + "'";
                commands.add(cmd);
            } catch (NotFoundException e) {
                CTILogger.error(e.getMessage(), e);
            }
        } else {
            if (liteHw.getAccountID() > 0) {
                
                LiteStarsCustAccountInformation liteAcctInfo = 
                    starsCustAccountInformationDao.getByAccountId(liteHw.getAccountID());

                for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
                    LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(i);
                    if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getAddressingGroupID() > 0) {
                        try {
                            String groupName = DaoFactory.getPaoDao().getYukonPAOName(liteApp.getAddressingGroupID());
                            String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " template '" + groupName + "'";
                            commands.add(cmd);
                        } catch (NotFoundException e) {
                            CTILogger.error(e.getMessage(), e);
                        }
                    } else if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getAddressingGroupID() == 0)
                        throw new WebClientException("Unable to config since no Addressing Group is assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                }
            }
        }
    
        String[] cfgCmds = new String[commands.size()];
        commands.toArray(cfgCmds);
        return cfgCmds;
    }

    /* from YukonSwitchCommandAction */
    public static void sendConfigCommand(YukonEnergyCompany yukonEnergyCompany, LiteStarsLMHardware liteHw, 
                                         boolean forceInService, String options) throws WebClientException {

        DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
        EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao = YukonSpringHook.getBean("energyCompanyRolePropertyDao", EnergyCompanyRolePropertyDao.class);
        StarsDatabaseCache starsDatabaseCache = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);
        
        if (liteHw.getManufacturerSerialNumber().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }
    
        LiteYukonUser energyCompanyUser = yukonEnergyCompany.getEnergyCompanyUser();
    
        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        Integer invID = new Integer(liteHw.getInventoryID());
        Integer hwEventEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID());
        Integer configEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID());
        Integer availStatusEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID());
        java.util.Date now = new java.util.Date();
    
        // Parameter options corresponds to the infoString field of the switch
        // command queue.
        // It takes the format of "GroupID:XX;RouteID:XX"
        Integer optGroupID = null;
        Integer optRouteID = null;
        if (options != null) {
            String[] fields = options.split(";");
            for (int i = 0; i < fields.length; i++) {
                try {
                    if (fields[i].startsWith("GroupID:")) {
                        optGroupID = Integer.valueOf(fields[i].substring("GroupID:".length()));
                    } else if (fields[i].startsWith("RouteID:")) {
                        optRouteID = Integer.valueOf(fields[i].substring("RouteID:".length()));
                    }
                } catch (NumberFormatException e) {
                    CTILogger.error(e.getMessage(), e);
                }
            }
        }
    
        int routeID = 0;
        if (optRouteID != null) {
            routeID = optRouteID.intValue();
        } else {
            routeID = liteHw.getRouteID();
        }
        if (routeID == 0) {
            routeID = liteStarsEnergyCompany.getDefaultRouteId();
        }
    
        boolean trackHardwareAddressingEnabled = 
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING, yukonEnergyCompany);
        boolean automaticConfigurationEnabled =
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.AUTOMATIC_CONFIGURATION, yukonEnergyCompany);
        
        final String[] cfgCmds = getConfigCommands(liteHw, yukonEnergyCompany, trackHardwareAddressingEnabled, optGroupID);
        if (cfgCmds.length == 0) {
            throw new WebClientException("No hardware configuration set up for serial #" + liteHw.getManufacturerSerialNumber() + ".");
        }
    
        if ((liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL || forceInService)
            && InventoryUtils.supportServiceInOut(liteHw.getLmHardwareTypeID())) {
            // Send an in service command first
            sendEnableCommand(yukonEnergyCompany, liteHw, optRouteID);
            if (automaticConfigurationEnabled) {
                // Send the config command a while later
                final int routeID2 = routeID;
    
                TimerTask sendCfgTask = new TimerTask() {
                    public void run() {
                        /*
                         * With permissions now necessary to send commands,
                         * we'll use the admin user
                         * for automated STARS sends.
                         */
                        YukonUserDao yukonUserDao = DaoFactory.getYukonUserDao();
                        LiteYukonUser adminUser = yukonUserDao.getLiteYukonUser(UserUtils.USER_ADMIN_ID);
    
                        try {
                            for (int i = 0; i < cfgCmds.length; i++) {
                                ServerUtils.sendSerialCommand(cfgCmds[i], routeID2, adminUser);
                            }
                        } catch (WebClientException e) {}
                        CTILogger.info("*** Config command sent ***");
                    }
                };
    
                YukonSpringHook.getGlobalTimer().schedule(sendCfgTask,
                                                          300 * 1000);
                CTILogger.info("*** Send config command a while later ***");
            }
        } else {
            // Only send the config command
            for (int i = 0; i < cfgCmds.length; i++) {
                ServerUtils.sendSerialCommand(cfgCmds[i], routeID, energyCompanyUser);
            }
        }
    
        // Add "Config" to hardware events
        try {
            com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
            com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    
            eventDB.setInventoryID(invID);
            eventBase.setEventTypeID(hwEventEntryID);
            eventBase.setActionID(configEntryID);
            eventBase.setEventDateTime(now);
            event.setEnergyCompanyID(yukonEnergyCompany.getEnergyCompanyId());
    
            dbPersistentDao.performDBChange(event, TransactionType.INSERT);
    
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(availStatusEntryID);
            dbPersistentDao.performDBChange(invDB, TransactionType.UPDATE);
    
            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (PersistenceException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    /* from YukonSwitchCommandAction */
    public static void sendDisableCommand(YukonEnergyCompany yukonEnergyCompany, LiteStarsLMHardware liteHw, Integer routeId)
            throws WebClientException {
        DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
        StarsDatabaseCache starsDatabaseCache = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);

        if (liteHw.getManufacturerSerialNumber().trim().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }
        
        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        Integer hwEventEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID());
        Integer termEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID());
        Integer unavailStatusEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL).getEntryID());
    
        LiteYukonUser energyCompanyUser = yukonEnergyCompany.getEnergyCompanyUser();
        List<String> commands = getDisableCommands(liteHw);

        if (commands.isEmpty())
            return;

        if (routeId == null) {
            routeId = liteHw.getRouteID();
        }
        if (routeId == null || routeId == 0)
            routeId = liteStarsEnergyCompany.getDefaultRouteId();

        for (String command : commands) {
            ServerUtils.sendSerialCommand(command, routeId, energyCompanyUser);
        }

        // Add "Termination" to hardware events
        try {
            com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
            com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    
            eventDB.setInventoryID(new Integer(liteHw.getInventoryID()));
            eventBase.setEventTypeID(hwEventEntryID);
            eventBase.setActionID(termEntryID);
            eventBase.setEventDateTime(new Date());
            event.setEnergyCompanyID(yukonEnergyCompany.getEnergyCompanyId());
    
            dbPersistentDao.performDBChange(event, TransactionType.INSERT);
    
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(unavailStatusEntryID);
            dbPersistentDao.performDBChange(invDB, TransactionType.UPDATE);

            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (PersistenceException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public static List<String> getDisableCommands(LiteStarsLMHardware liteHw) {
        List<String> retVal = Lists.newArrayList();
        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        if (hwConfigType == HardwareConfigType.VERSACOM.getHardwareConfigTypeId()) {
            retVal.add("putconfig vcom service out serial " + liteHw.getManufacturerSerialNumber());
        } else if (hwConfigType == HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId()) {
            retVal.add("putconfig xcom service out serial " + liteHw.getManufacturerSerialNumber());
        } else if (hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId()) {
            // To disable a SA205 switch, reconfig all slots to the unused
            // address
            retVal.add("putconfig sa205 serial " + liteHw.getManufacturerSerialNumber() +
                       " assign" + " 1=" + InventoryUtils.SA205_UNUSED_ADDR + ",2=" +
                       InventoryUtils.SA205_UNUSED_ADDR + ",3=" + InventoryUtils.SA205_UNUSED_ADDR +
                       ",4=" + InventoryUtils.SA205_UNUSED_ADDR + ",5=" +
                       InventoryUtils.SA205_UNUSED_ADDR + ",6=" + InventoryUtils.SA205_UNUSED_ADDR);
        } else if (hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId()) {
            // To disable a SA305 switch, we need to zero out relay map 1 and tell the switch to
            // use map 1 instead of map 0

            // sets map 1 to zero values
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() + " lorm1=0");
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() + " horm1=0");
            // tell the switch to use relay map1 instead of relay map0
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() + " use relay map 1");
        }
        return retVal;
    }

    /* from YukonSwitchCommandAction */
    public static void sendEnableCommand(YukonEnergyCompany yukonEnergyCompany, LiteStarsLMHardware liteHw, Integer routeId)
            throws WebClientException {
        
        DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
        StarsDatabaseCache starsDatabaseCache = YukonSpringHook.getBean("starsDatabaseCache", StarsDatabaseCache.class);
        
        if (liteHw.getManufacturerSerialNumber().length() == 0) {
            throw new WebClientException("The manufacturer serial # of the hardware cannot be empty");
        }

        LiteYukonUser energyCompanyUser = yukonEnergyCompany.getEnergyCompanyUser();

        List<String> commands = getEnableCommands(liteHw, yukonEnergyCompany, false);
        if (commands.isEmpty()) {
            return;
        }

        if (routeId == null) {
            routeId = liteHw.getRouteID();
        }
        if (routeId == null || routeId == 0) {
            LiteStarsEnergyCompany energyCompany = 
                starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
            routeId = energyCompany.getDefaultRouteId();
        }

        for (String command : commands) {
            ServerUtils.sendSerialCommand(command, routeId, energyCompanyUser);
        }

        LiteStarsEnergyCompany liteStarsEnergyCompany = 
            starsDatabaseCache.getEnergyCompany(yukonEnergyCompany.getEnergyCompanyId());
        
        // Add "Activation Completed" to hardware events
        Integer hwEventEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID());
        Integer actCompEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID());
        Integer availStatusEntryID = new Integer(liteStarsEnergyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).getEntryID());
    
        try {
            com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
            com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    
            eventDB.setInventoryID(new Integer(liteHw.getInventoryID()));
            eventBase.setEventTypeID(hwEventEntryID);
            eventBase.setActionID(actCompEntryID);
            eventBase.setEventDateTime(new Date());
            event.setEnergyCompanyID(yukonEnergyCompany.getEnergyCompanyId());
    
            dbPersistentDao.performDBChange(event, TransactionType.INSERT);
    
            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(availStatusEntryID);
            dbPersistentDao.performDBChange(invDB, TransactionType.UPDATE);
    
            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (PersistenceException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public static List<String> getEnableCommands(LiteStarsLMHardware liteHw,
            YukonEnergyCompany energyCompany, boolean willAlsoConfig) throws WebClientException {
        List<String> retVal = Lists.newArrayList();

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        if (hwConfigType == HardwareConfigType.VERSACOM.getHardwareConfigTypeId()) {
            retVal.add("putconfig vcom service in serial " + liteHw.getManufacturerSerialNumber());
        } else if (hwConfigType == HardwareConfigType.EXPRESSCOM.getHardwareConfigTypeId()) {
            retVal.add("putconfig xcom service in serial " + liteHw.getManufacturerSerialNumber());
        } else if (hwConfigType == HardwareConfigType.SA205.getHardwareConfigTypeId()) {
            // To enable a SA205 switch, just reconfig it using the saved configuration
            if (!willAlsoConfig) {
                retVal.add(getConfigCommands(liteHw, energyCompany, true, null)[0]);
            }
        } else if (hwConfigType == HardwareConfigType.SA305.getHardwareConfigTypeId()) {
            // To enable a SA305 switch, we need to tell the switch to use map 1 instead of map 0
            // and then reset values in relay map 1 to their defaults just to be neat and tidy.

            // tell the switch to use relay map0 instead of relay map1
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() +
                       " use relay map 0");

            // puts relay 1 back to its default values
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() +
                       " lorm1=40");
            retVal.add("putconfig sa305 serial " + liteHw.getManufacturerSerialNumber() +
                       " utility " + liteHw.getLMConfiguration().getSA305().getUtility() +
                       " horm1=65");
        }

        return retVal;
    }
}
