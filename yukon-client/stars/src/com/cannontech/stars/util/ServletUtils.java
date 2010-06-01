package com.cannontech.stars.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.hardware.LMHardwareConfiguration;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
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
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerContact;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardwareConfigResponse;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.user.UserUtils;
import com.cannontech.util.PhoneNumber;
import com.cannontech.util.ServletUtil;

public class ServletUtils {

    // Attribute names to store objects in session or StarsYukonUser object
    public static final String TRANSIENT_ATT_LEADING = "$$";

    public static final String ATT_ERROR_MESSAGE = ServletUtil.ATT_ERROR_MESSAGE;
    public static final String ATT_CONFIRM_MESSAGE = ServletUtil.ATT_CONFIRM_MESSAGE;
    public static final String ATT_PROMPT_MESSAGE = "PROMPT_MESSAGE";
    public static final String ATT_REDIRECT = ServletUtil.ATT_REDIRECT;
    public static final String ATT_REDIRECT2 = ServletUtil.ATT_REDIRECT2;
    public static final String ATT_REFERRER = ServletUtil.ATT_REFERRER;
    public static final String ATT_REFERRER2 = ServletUtil.ATT_REFERRER2;

    public static final int ACTION_CHANGEDEVICE = 1;
    public static final int ACTION_CHANGESTATE = 2;
    public static final int ACTION_TOSERVICECOMPANY = 3;
    public static final int ACTION_TOWAREHOUSE = 4;
    public static final int ACTION_CHANGE_WO_SERVICE_STATUS = 5;
    public static final int ACTION_CHANGE_WO_SERVICE_TYPE = 6;
    
    /**
     * When used in session, the attribute with this name should be passed a
     * CtiNavObject
     */
    public static final String NAVIGATE = ServletUtil.NAVIGATE;

    /**
     * When used in session, the attribute with this name should be passed an
     * ArrayList of FilterWrappers
     */
    public static final String FILTER_INVEN_LIST = ServletUtil.FILTER_INVEN_LIST;

    public static final String ATT_YUKON_USER = ServletUtil.ATT_YUKON_USER;
    public static final String ATT_STARS_YUKON_USER = "STARS_YUKON_USER";
    public static final String ATT_ENERGY_COMPANY_SETTINGS = "ENERGY_COMPANY_SETTINGS";
    public static final String ATT_CUSTOMER_SELECTION_LISTS = "CUSTOMER_SELECTION_LISTS";
    public static final String ATT_DEFAULT_THERMOSTAT_SETTINGS = "DEFAULT_THERMOSTAT_SETTINGS";
    public static final String ATT_CUSTOMER_ACCOUNT_INFO = "CUSTOMER_ACCOUNT_INFORMATION";
    public static final String TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO = ServletUtils.TRANSIENT_ATT_LEADING
                                                                     + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO;

    public static final String ATT_LM_PROGRAM_HISTORY = "LM_PROGRAM_HISTORY";
    public static final String ATT_CHANGED_THERMOSTAT_SETTINGS = "CHANGED_THERMOSTAT_SETTINGS";
    public static final String ATT_APPLY_TO_WEEKEND = "APPLY_TO_WEEKEND";
    public static final String ATT_APPLY_TO_WEEKDAYS = "APPLY_TO_WEEKDAYS";
    public static final String ATT_ACCOUNT_SEARCH_RESULTS = "ACCOUNT_SEARCH_RESULTS";
    public static final String ATT_NEW_CUSTOMER_ACCOUNT = "NEW_CUSTOMER_ACCOUNT";
    public static final String ATT_LAST_ACCOUNT_SEARCH_OPTION = "LAST_ACCOUNT_SEARCH_OPTION";
    public static final String ATT_LAST_ACCOUNT_SEARCH_VALUE = "LAST_ACCOUNT_SEARCH_VALUE";
    public static final String ATT_LAST_INVENTORY_SEARCH_OPTION = "LAST_INVENTORY_SEARCH_OPTION";
    public static final String ATT_LAST_INVENTORY_SEARCH_VALUE = "LAST_INVENTORY_SEARCH_VALUE";
    public static final String ATT_LAST_SERVICE_SEARCH_OPTION = "LAST_SERVICE_SEARCH_OPTION";
    public static final String ATT_LAST_SERVICE_SEARCH_VALUE = "LAST_SERVICE_SEARCH_VALUE";

    public static final String ATT_MULTI_ACTIONS = "MULTI_ACTIONS";
    public static final String ATT_NEW_ACCOUNT_WIZARD = "NEW_ACCOUNT_WIZARD";
    public static final String ATT_LAST_SUBMITTED_REQUEST = "LAST_SUBMITTED_REQUEST";

    public static final String ATT_OMIT_GATEWAY_TIMEOUT = "OMIT_GATEWAY_TIMEOUT";
    public static final String ATT_THERMOSTAT_INVENTORY_IDS = "THERMOSTAT_INVENTORY_IDS";

    public static final String ATT_CONTEXT_SWITCHED = "CONTEXT_SWITCHED";

    public static final String NEED_MORE_INFORMATION = "NeedMoreInformation";
    public static final String CONFIRM_ON_MESSAGE_PAGE = "ConfirmOnMessagePage";
    public static final String ATT_MSG_PAGE_REDIRECT = "MSG_PAGE_REDIRECT";
    public static final String ATT_MSG_PAGE_REFERRER = "MSG_PAGE_REFERRER";

    public static final String IN_SERVICE = "In Service";
    public static final String OUT_OF_SERVICE = "Out of Service";

    public static final int MAX_NUM_IMAGES = 3;
    public static final int GATEWAY_TIMEOUT_HOURS = 24;

    public static final String UTIL_COMPANY_ADDRESS = "<<COMPANY_ADDRESS>>";
    public static final String UTIL_PHONE_NUMBER = "<<PHONE_NUMBER>>";
    public static final String UTIL_FAX_NUMBER = "<<FAX_NUMBER>>";
    public static final String UTIL_EMAIL = "<<EMAIL>>";

    public static final String INHERITED_FAQ = "INHERITED_FAQ";

    private static final AuthenticationService authenticationService = (AuthenticationService) YukonSpringHook.getBean("authenticationService");
    private static final java.text.SimpleDateFormat[] timeFormat = {
            new java.text.SimpleDateFormat("hh:mm a"),
            new java.text.SimpleDateFormat("hh:mma"),
            new java.text.SimpleDateFormat("HH:mm"), };

    // this static initializer sets all the simpledateformat to lenient
    static {
        for (int i = 0; i < timeFormat.length; i++)
            timeFormat[i].setLenient(true);
    }

    public static void sendDisableCommand(LiteStarsEnergyCompany energyCompany,
                                          LiteStarsLMHardware liteHw,
                                          Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

        Integer hwEventEntryID = new Integer(
                                             energyCompany.getYukonListEntry(
                                                                             YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                                 .getEntryID());
        Integer termEntryID = new Integer(
                                          energyCompany.getYukonListEntry(
                                                                          YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
                                              .getEntryID());
        Integer unavailStatusEntryID = new Integer(
                                                   energyCompany.getYukonListEntry(
                                                                                   YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
                                                       .getEntryID());

        LiteYukonUser user = energyCompany.getUser();

        String cmd = null;
        String map1Cmdlorm = null;
        String map1Cmdhorm = null;
        boolean is305 = false;

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
            cmd = "putconfig vcom service out serial "
                  + liteHw.getManufacturerSerialNumber();
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
            cmd = "putconfig xcom service out serial "
                  + liteHw.getManufacturerSerialNumber();
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
            // To disable a SA205 switch, reconfig all slots to the unused
            // address
            cmd = "putconfig sa205 serial "
                  + liteHw.getManufacturerSerialNumber() + " assign" + " 1="
                  + InventoryUtils.SA205_UNUSED_ADDR + ",2="
                  + InventoryUtils.SA205_UNUSED_ADDR + ",3="
                  + InventoryUtils.SA205_UNUSED_ADDR + ",4="
                  + InventoryUtils.SA205_UNUSED_ADDR + ",5="
                  + InventoryUtils.SA205_UNUSED_ADDR + ",6="
                  + InventoryUtils.SA205_UNUSED_ADDR;
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
            /*
             * To disable a SA305 switch, we need to zero out relay map 1 and
             * tell the
             * switch to use map 1 instead of map 0
             */
            is305 = true;

            // sets map 1 to zero values
            map1Cmdlorm = "putconfig sa305 serial "
                          + liteHw.getManufacturerSerialNumber() + " utility "
                          + liteHw.getLMConfiguration().getSA305().getUtility()
                          + " lorm1=0";
            map1Cmdhorm = "putconfig sa305 serial "
                          + liteHw.getManufacturerSerialNumber() + " utility "
                          + liteHw.getLMConfiguration().getSA305().getUtility()
                          + " horm1=0";
            // tell the switch to use relay map1 instead of relay map0
            cmd = "putconfig sa305 serial "
                  + liteHw.getManufacturerSerialNumber() + " utility "
                  + liteHw.getLMConfiguration().getSA305().getUtility()
                  + " use relay map 1";
        }

        if (cmd == null)
            return;

        int rtID = 0;
        if (routeID != null)
            rtID = routeID.intValue();
        else
            rtID = liteHw.getRouteID();
        if (rtID == 0)
            rtID = energyCompany.getDefaultRouteID();

        if (is305) {
            ServerUtils.sendSerialCommand(map1Cmdlorm, rtID, user);
            ServerUtils.sendSerialCommand(map1Cmdhorm, rtID, user);
        }

        ServerUtils.sendSerialCommand(cmd, rtID, user);

        // Add "Termination" to hardware events
        try {
            com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
            com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();

            eventDB.setInventoryID(new Integer(liteHw.getInventoryID()));
            eventBase.setEventTypeID(hwEventEntryID);
            eventBase.setActionID(termEntryID);
            eventBase.setEventDateTime(new Date());
            event.setEnergyCompanyID(energyCompany.getEnergyCompanyID());

            event = Transaction.createTransaction(Transaction.INSERT, event)
                .execute();

            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(unavailStatusEntryID);
            invDB = Transaction.createTransaction(Transaction.UPDATE, invDB)
                .execute();

            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public static void sendEnableCommand(LiteStarsEnergyCompany energyCompany,
                                         LiteStarsLMHardware liteHw,
                                         Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

        LiteYukonUser user = energyCompany.getUser();

        String cmd = null;
        String map1Cmdlorm = null;
        String map1Cmdhorm = null;
        boolean is305 = false;

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
            cmd = "putconfig vcom service in serial "
                  + liteHw.getManufacturerSerialNumber();
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
            cmd = "putconfig xcom service in serial "
                  + liteHw.getManufacturerSerialNumber();
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
            // To enable a SA205 switch, just reconfig it using the saved
            // configuration
            cmd = getConfigCommands(liteHw, energyCompany, true, null)[0];
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
            /*
             * To enable a SA305 switch, we need to tell the
             * switch to use map 1 instead of map 0 and then reset
             * values in relay map 1 to their defaults just to be neat and tidy
             */
            is305 = true;

            // tell the switch to use relay map0 instead of relay map1
            cmd = "putconfig sa305 serial "
                  + liteHw.getManufacturerSerialNumber() + " utility "
                  + liteHw.getLMConfiguration().getSA305().getUtility()
                  + " use relay map 0";

            // puts relay 1 back to its default values
            map1Cmdlorm = "putconfig sa305 serial "
                          + liteHw.getManufacturerSerialNumber() + " utility "
                          + liteHw.getLMConfiguration().getSA305().getUtility()
                          + " lorm1=40";
            map1Cmdhorm = "putconfig sa305 serial "
                          + liteHw.getManufacturerSerialNumber() + " utility "
                          + liteHw.getLMConfiguration().getSA305().getUtility()
                          + " horm1=65";

        }

        if (cmd == null)
            return;

        int rtID = 0;
        if (routeID != null)
            rtID = routeID.intValue();
        else
            rtID = liteHw.getRouteID();
        if (rtID == 0)
            rtID = energyCompany.getDefaultRouteID();

        ServerUtils.sendSerialCommand(cmd, rtID, user);
        if (is305) {
            ServerUtils.sendSerialCommand(map1Cmdlorm, rtID, user);
            ServerUtils.sendSerialCommand(map1Cmdhorm, rtID, user);
        }

        // Add "Activation Completed" to hardware events
        Integer hwEventEntryID = new Integer(
                                             energyCompany.getYukonListEntry(
                                                                             YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                                 .getEntryID());
        Integer actCompEntryID = new Integer(
                                             energyCompany.getYukonListEntry(
                                                                             YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED)
                                                 .getEntryID());
        Integer availStatusEntryID = new Integer(
                                                 energyCompany.getYukonListEntry(
                                                                                 YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL)
                                                     .getEntryID());

        try {
            com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
            com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();

            eventDB.setInventoryID(new Integer(liteHw.getInventoryID()));
            eventBase.setEventTypeID(hwEventEntryID);
            eventBase.setActionID(actCompEntryID);
            eventBase.setEventDateTime(new Date());
            event.setEnergyCompanyID(energyCompany.getEnergyCompanyID());

            event = Transaction.createTransaction(Transaction.INSERT, event)
                .execute();

            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(availStatusEntryID);
            invDB = Transaction.createTransaction(Transaction.UPDATE, invDB)
                .execute();

            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    // builds up the command from the information stored in the LMConfiguration
    // object/tables
    private static String[] getConfigCommands(
                                              LiteStarsLMHardware liteHw,
                                              LiteStarsEnergyCompany energyCompany,
                                              boolean useHardwareAddressing,
                                              Integer groupID)
            throws WebClientException {
        List<String> commands = new ArrayList<String>();
        String[] coldLoads = new String[4];
        String[] tamperDetects = new String[2];

        if (useHardwareAddressing) {
            if (liteHw.getLMConfiguration() == null)
                throw new WebClientException(
                                             "There is no configuration saved for serial #"
                                                     + liteHw.getManufacturerSerialNumber()
                                                     + ".");

            String freezer = liteHw.getLMConfiguration().getColdLoadPickup();
            if (freezer.compareTo(CtiUtilities.STRING_NONE) != 0
                && freezer.length() > 0) {
                coldLoads = StarsUtils.splitString(freezer, ",");
            }

            String foolDetect = liteHw.getLMConfiguration().getTamperDetect();
            if (foolDetect.compareTo(CtiUtilities.STRING_NONE) != 0
                && foolDetect.length() > 0) {
                tamperDetects = StarsUtils.splitString(foolDetect, ",");
            }

            if (liteHw.getLMConfiguration().getExpressCom() != null) {
                String program = null;
                String splinter = null;
                String load = null;
                String[] programs = liteHw.getLMConfiguration()
                    .getExpressCom()
                    .getProgram()
                    .split(",");
                String[] splinters = liteHw.getLMConfiguration()
                    .getExpressCom()
                    .getSplinter()
                    .split(",");

                for (int loadNo = 1; loadNo <= 8; loadNo++) {
                    int prog = 0;
                    if (programs.length >= loadNo
                        && programs[loadNo - 1].length() > 0)
                        prog = Integer.parseInt(programs[loadNo - 1]);
                    int splt = 0;
                    if (splinters.length >= loadNo
                        && splinters[loadNo - 1].length() > 0)
                        splt = Integer.parseInt(splinters[loadNo - 1]);

                    if (prog > 0 || splt > 0) {
                        if (program == null)
                            program = String.valueOf(prog);
                        else
                            program += "," + String.valueOf(prog);
                        if (splinter == null)
                            splinter = String.valueOf(splt);
                        else
                            splinter += "," + String.valueOf(splt);
                        if (load == null)
                            load = String.valueOf(loadNo);
                        else
                            load += "," + String.valueOf(loadNo);
                    }
                }

                String cmd = "putconfig xcom serial "
                             + liteHw.getManufacturerSerialNumber()
                             + " assign"
                             + " S "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getServiceProvider()
                             + " G "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getGEO()
                             + " B "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getSubstation()
                             + " F "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getFeeder()
                             + " Z "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getZip()
                             + " U "
                             + liteHw.getLMConfiguration()
                                 .getExpressCom()
                                 .getUserAddress();
                if (load != null)
                    cmd += " P " + program + " R " + splinter + " Load " + load;
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
                        String clCmd = "putconfig xcom serial "
                                       + liteHw.getManufacturerSerialNumber()
                                       + " coldload r" + (j + 1) + "="
                                       + coldLoads[j];
                        commands.add(clCmd);
                    }
                }
            } else if (liteHw.getLMConfiguration().getVersaCom() != null) {
                String cmd = "putconfig vcom serial "
                             + liteHw.getManufacturerSerialNumber()
                             + " assign"
                             + " U "
                             + liteHw.getLMConfiguration()
                                 .getVersaCom()
                                 .getUtilityID()
                             + " S "
                             + liteHw.getLMConfiguration()
                                 .getVersaCom()
                                 .getSection()
                             + " C 0x"
                             + Integer.toHexString(liteHw.getLMConfiguration()
                                 .getVersaCom()
                                 .getClassAddress())
                             + " D 0x"
                             + Integer.toHexString(liteHw.getLMConfiguration()
                                 .getVersaCom()
                                 .getDivisionAddress());
                commands.add(cmd);
            } else if (liteHw.getLMConfiguration().getSA205() != null) {
                String cmd = "putconfig sa205 serial "
                             + liteHw.getManufacturerSerialNumber()
                             + " assign"
                             + " 1="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot1()
                             + ",2="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot2()
                             + ",3="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot3()
                             + ",4="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot4()
                             + ",5="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot5()
                             + ",6="
                             + liteHw.getLMConfiguration()
                                 .getSA205()
                                 .getSlot6();
                commands.add(cmd);

                // cold load pickup needs to be in a separate command for each
                // individual value
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        String clCmd = "putconfig sa205 serial "
                                       + liteHw.getManufacturerSerialNumber()
                                       + " coldload f" + (j + 1) + "="
                                       + coldLoads[j];
                        commands.add(clCmd);
                    }
                }

                // tamper detect also needs to be in a separate command for each
                // value
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        String tdCmd = "putconfig sa205 serial "
                                       + liteHw.getManufacturerSerialNumber()
                                       + " tamper f" + (j + 1) + "="
                                       + tamperDetects[j];
                        commands.add(tdCmd);
                    }
                }
            }

            else if (liteHw.getLMConfiguration().getSA305() != null) {
                String cmd = "putconfig sa305 serial "
                             + liteHw.getManufacturerSerialNumber()
                             + " utility "
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getUtility()
                             + " assign"
                             + " g="
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getGroup()
                             + " d="
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getDivision()
                             + " s="
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getSubstation()
                             + " f="
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getRateFamily()
                             + " m="
                             + liteHw.getLMConfiguration()
                                 .getSA305()
                                 .getRateMember();
                commands.add(cmd);

                // cold load pickup needs to be in a separate command for each
                // individual value
                for (int j = 0; j < coldLoads.length; j++) {
                    if (coldLoads[j].length() > 0) {
                        String clCmd = "putconfig sa305 serial "
                                       + liteHw.getManufacturerSerialNumber()
                                       + " utility "
                                       + liteHw.getLMConfiguration()
                                           .getSA305()
                                           .getUtility() + " coldload f"
                                       + (j + 1) + "=" + coldLoads[j];
                        commands.add(clCmd);
                    }
                }

                // tamper detect also needs to be in a separate command for each
                // value
                for (int j = 0; j < tamperDetects.length; j++) {
                    if (tamperDetects[j].length() > 0) {
                        String tdCmd = "putconfig sa305 serial "
                                       + liteHw.getManufacturerSerialNumber()
                                       + " utility "
                                       + liteHw.getLMConfiguration()
                                           .getSA305()
                                           .getUtility() + " tamper f"
                                       + (j + 1) + "=" + tamperDetects[j];
                        commands.add(tdCmd);
                    }
                }
            } else {
                throw new WebClientException(
                                             "Unsupported configuration type for serial #"
                                                     + liteHw.getManufacturerSerialNumber()
                                                     + ".");
            }
        } else if (groupID != null) {
            try {
                String groupName = DaoFactory.getPaoDao()
                    .getYukonPAOName(groupID.intValue());
                String cmd = "putconfig serial "
                             + liteHw.getManufacturerSerialNumber()
                             + " template '" + groupName + "'";
                commands.add(cmd);
            } catch (NotFoundException e) {
                CTILogger.error(e.getMessage(), e);
            }
        } else {
            if (liteHw.getAccountID() > 0) {
                LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation(
                                                                                                       liteHw.getAccountID(),
                                                                                                       true);
                for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
                    LiteStarsAppliance liteApp = liteAcctInfo.getAppliances()
                        .get(i);
                    if (liteApp.getInventoryID() == liteHw.getInventoryID()
                        && liteApp.getAddressingGroupID() > 0) {
                        try {
                            String groupName = DaoFactory.getPaoDao()
                                .getYukonPAOName(liteApp.getAddressingGroupID());
                            String cmd = "putconfig serial "
                                         + liteHw.getManufacturerSerialNumber()
                                         + " template '" + groupName + "'";
                            commands.add(cmd);
                        } catch (NotFoundException e) {
                            CTILogger.error(e.getMessage(), e);
                        }
                    } else if (liteApp.getInventoryID() == liteHw.getInventoryID()
                               && liteApp.getAddressingGroupID() == 0)
                        throw new WebClientException(
                                                     "Unable to config since no Addressing Group is assigned.  If no groups are available in the Assigned Group column, please verify that your programs are valid Yukon LM Programs with assigned load groups.");
                }
            }
        }

        String[] cfgCmds = new String[commands.size()];
        commands.toArray(cfgCmds);
        return cfgCmds;
    }

    public static void fileWriteConfigCommand(
                                              LiteStarsEnergyCompany energyCompany,
                                              LiteStarsLMHardware liteHw,
                                              boolean forceInService,
                                              String options)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

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
                    if (fields[i].startsWith("GroupID:"))
                        optGroupID = Integer.valueOf(fields[i].substring("GroupID:".length()));
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
            optGroupID = LMHardwareConfiguration.getLMHardwareConfigurationFromInvenID(
                                                                                       liteHw.getInventoryID())
                .getAddressingGroupID();
            if (optGroupID > 0)
                loadGroupName = DaoFactory.getPaoDao()
                    .getYukonPAOName(optGroupID);
        }

        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs
                              + energyCompany.getName());
        if (!ecDir.exists())
            ecDir.mkdirs();

        File commFile;

        String cmd = null;

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "1," + liteHw.getManufacturerSerialNumber() + ","
                  + loadGroupName;
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "1," + liteHw.getManufacturerSerialNumber() + ","
                  + loadGroupName;
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Config";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
            commFile = new File(ecDir, ServerUtils.SA305_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Config";
        } else {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            cmd = liteHw.getManufacturerSerialNumber();
        }

        if (loadGroupName == null) {
            commFile = new File(ecDir, ServerUtils.PROBLEM_FILE);
            if (optGroupID == null)
                optGroupID = -1;
            cmd = liteHw.getManufacturerSerialNumber()
                  + ": Unable to find a load group in Yukon with the specified groupID of "
                  + optGroupID;
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

    public static void fileWriteReceiverConfigLine(File commFile, String cmd) {
        try {
            if (!commFile.exists())
                commFile.createNewFile();
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
            if (fw != null)
                fw.close();
        }
    }

    public static void sendConfigCommand(LiteStarsEnergyCompany energyCompany,
                                         LiteStarsLMHardware liteHw,
                                         boolean forceInService, String options)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

        LiteYukonUser user = energyCompany.getUser();

        Integer invID = new Integer(liteHw.getInventoryID());
        Integer hwEventEntryID = new Integer(
                                             energyCompany.getYukonListEntry(
                                                                             YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE)
                                                 .getEntryID());
        Integer configEntryID = new Integer(
                                            energyCompany.getYukonListEntry(
                                                                            YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG)
                                                .getEntryID());
        Integer availStatusEntryID = new Integer(
                                                 energyCompany.getYukonListEntry(
                                                                                 YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL)
                                                     .getEntryID());
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
                    if (fields[i].startsWith("GroupID:"))
                        optGroupID = Integer.valueOf(fields[i].substring("GroupID:".length()));
                    else if (fields[i].startsWith("RouteID:"))
                        optRouteID = Integer.valueOf(fields[i].substring("RouteID:".length()));
                } catch (NumberFormatException e) {
                    CTILogger.error(e.getMessage(), e);
                }
            }
        }

        int routeID = 0;
        if (optRouteID != null)
            routeID = optRouteID.intValue();
        else
            routeID = liteHw.getRouteID();
        if (routeID == 0)
            routeID = energyCompany.getDefaultRouteID();

        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = (trackHwAddr != null)
                                        && Boolean.valueOf(trackHwAddr)
                                            .booleanValue();

        final String[] cfgCmds = getConfigCommands(liteHw, energyCompany,
                                                   useHardwareAddressing,
                                                   optGroupID);
        if (cfgCmds.length == 0)
            throw new WebClientException(
                                         "No hardware configuration set up for serial #"
                                                 + liteHw.getManufacturerSerialNumber()
                                                 + ".");

        if ((liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL || forceInService)
            && InventoryUtils.supportServiceInOut(liteHw.getLmHardwareTypeID())) {
            // Send an in service command first
            sendEnableCommand(energyCompany, liteHw, optRouteID);
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(
                                                                      "rolePropertyDao",
                                                                      RolePropertyDao.class);
            if ((StarsUtils.isOperator(user) && rolePropertyDao.checkProperty(
                                                                              YukonRoleProperty.OPERATOR_AUTOMATIC_CONFIGURATION,
                                                                              user))
                || (StarsUtils.isResidentialCustomer(user) && rolePropertyDao.checkProperty(
                                                                                            YukonRoleProperty.RESIDENTIAL_AUTOMATIC_CONFIGURATION,
                                                                                            user))) {
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
                            for (int i = 0; i < cfgCmds.length; i++)
                                ServerUtils.sendSerialCommand(cfgCmds[i],
                                                              routeID2,
                                                              adminUser);
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
            for (int i = 0; i < cfgCmds.length; i++)
                ServerUtils.sendSerialCommand(cfgCmds[i], routeID, user);
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
            event.setEnergyCompanyID(energyCompany.getEnergyCompanyID());

            event = Transaction.createTransaction(Transaction.INSERT, event)
                .execute();

            com.cannontech.database.db.stars.hardware.InventoryBase invDB = new com.cannontech.database.db.stars.hardware.InventoryBase();
            StarsLiteFactory.setInventoryBase(invDB, liteHw);
            invDB.setCurrentStateID(availStatusEntryID);
            invDB = Transaction.createTransaction(Transaction.UPDATE, invDB)
                .execute();

            liteHw.setCurrentStateID(invDB.getCurrentStateID());
            liteHw.updateDeviceStatus();
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
        }
    }

    public static void fileWriteDisableCommand(
                                               LiteStarsEnergyCompany energyCompany,
                                               LiteStarsLMHardware liteHw,
                                               Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs
                              + energyCompany.getName());
        if (!ecDir.exists())
            ecDir.mkdirs();

        File commFile;

        String cmd = null;

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",OUT";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",OUT";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Deactivate";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
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

    public static void fileWriteEnableCommand(
                                              LiteStarsEnergyCompany energyCompany,
                                              LiteStarsLMHardware liteHw,
                                              Integer routeID)
            throws WebClientException {
        if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
            throw new WebClientException(
                                         "The manufacturer serial # of the hardware cannot be empty");

        final String fs = System.getProperty("file.separator");
        File ecDir = new File(ServerUtils.getFileWriteSwitchConfigDir() + fs
                              + energyCompany.getName());
        if (!ecDir.exists())
            ecDir.mkdirs();

        File commFile;

        String cmd = null;

        int hwConfigType = InventoryUtils.getHardwareConfigType(liteHw.getLmHardwareTypeID());
        /*
         * This could all be consolidated, but I want to keep it separate
         * because it is
         * likely that there will be more functionality added to this per device
         * type.
         */
        if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_VERSACOM) {
            commFile = new File(ecDir, ServerUtils.VERSACOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",IN";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
            commFile = new File(ecDir, ServerUtils.EXPRESSCOM_FILE);
            cmd = "2," + liteHw.getManufacturerSerialNumber() + ",IN";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA205) {
            commFile = new File(ecDir, ServerUtils.SA205_FILE);
            cmd = liteHw.getManufacturerSerialNumber() + ", Activate";
        } else if (hwConfigType == InventoryUtils.HW_CONFIG_TYPE_SA305) {
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

    public static void populateInventoryFields(StarsCustAccountInformation starsAcctInfo,
                                     StarsInventory starsInv) {
        StarsInventories inventories = starsAcctInfo.getStarsInventories();

        for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
            StarsInventory inv = inventories.getStarsInventory(i);
            if (inv.getInventoryID() == starsInv.getInventoryID()) {
                inventories.setStarsInventory(i, starsInv);
                break;
            }
        }
    }

    public static String getDurationFromHours(int hour) {
        String durationStr = null;

        if (hour >= 24) {
            int numDays = (int) (hour / 24.0 + 0.5);
            durationStr = String.valueOf(numDays) + " Day";
            if (numDays > 1)
                durationStr += "s";
        } else {
            durationStr = String.valueOf(hour) + " Hour";
            if (hour > 1)
                durationStr += "s";
        }

        return durationStr;
    }

    public static String formatDate(Date date, java.text.SimpleDateFormat format) {
        return formatDate(date, format, "");
    }

    public static String formatDate(Date date,
                                    java.text.SimpleDateFormat format,
                                    String emptyStr) {
        if (date == null)
            return emptyStr;
        if (date.getTime() < StarsUtils.VERY_EARLY_TIME)
            return emptyStr;
        return format.format(date);
    }

    /**
     * Strips a phone number down to a simple string of digits
     */
    public static String formatPhoneNumberForSearch(String phoneNo)
            throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals(""))
            return "";

        // get rid of US country code (long distance)
        if (phoneNo.startsWith("1"))
            phoneNo = phoneNo.replaceFirst("1", "");

        return formatPhoneNumberForStorage(phoneNo);
    }

    public static String formatPhoneNumberForStorage(String phoneNo)
            throws WebClientException {
        phoneNo = phoneNo.trim();
        if (phoneNo.equals(""))
            return "";

        char[] checkDigits = phoneNo.toCharArray();
        for (char j : checkDigits) {
            if (!Character.isDigit(j) && j != '-' && j != 'x' && j != '('
                && j != ')' && j != ' ')
                throw new WebClientException(
                                             "Invalid phone number format '"
                                                     + phoneNo
                                                     + "'.  The phone number contains non-digits.");
        }

        return PhoneNumber.extractDigits(phoneNo);
    }

    public static String formatPin(String pin) throws WebClientException {
        pin = pin.trim();
        if (pin.equals(""))
            return "";

        if (pin.length() > 19) {
            throw new WebClientException(
                                         "Invalid IVR information format '"
                                                 + pin
                                                 + "'. This IVR field should have fewer than 20 digits.");
        }

        try {
            Long.parseLong(pin);
        } catch (NumberFormatException e) {
            throw new WebClientException(
                                         "Invalid IVR information format '"
                                                 + pin
                                                 + "'. This field is required to be numeric.'");
        }

        return pin;
    }
    
    public static String getOneLineAddress(StarsCustomerAddress starsAddr) {
		if (starsAddr == null) return "(none)";
    	
		StringBuffer sBuf = new StringBuffer();
		if (starsAddr.getStreetAddr1().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr1() ).append(", ");
		if (starsAddr.getStreetAddr2().trim().length() > 0)
			sBuf.append( starsAddr.getStreetAddr2() ).append(", ");
		if (starsAddr.getCity().trim().length() > 0)
			sBuf.append( starsAddr.getCity() ).append(", ");
		if (starsAddr.getState().trim().length() > 0)
			sBuf.append( starsAddr.getState() ).append(" ");
		if (starsAddr.getZip().trim().length() > 0)
			sBuf.append( starsAddr.getZip() );
    	
		return sBuf.toString();
	}
    

    public static TimeZone getTimeZone(String timeZoneStr) {
        if (timeZoneStr.equalsIgnoreCase("AST"))
            return TimeZone.getTimeZone("US/Alaska");
        else if (timeZoneStr.equalsIgnoreCase("PST"))
            return TimeZone.getTimeZone("US/Pacific");
        else if (timeZoneStr.equalsIgnoreCase("MST"))
            return TimeZone.getTimeZone("US/Mountain");
        else if (timeZoneStr.equalsIgnoreCase("CST"))
            return TimeZone.getTimeZone("US/Central");
        else if (timeZoneStr.equalsIgnoreCase("EST"))
            return TimeZone.getTimeZone("US/Eastern");
        else
            return null;
    }

    public static void removeTransientAttributes(HttpSession session) {
        @SuppressWarnings("unchecked")
        Enumeration<String> attributeEnum = session.getAttributeNames();
        List<String> attToBeRemoved = new ArrayList<String>();

        while (attributeEnum.hasMoreElements()) {
            String attName = attributeEnum.nextElement();
            if (attName.startsWith(ServletUtils.TRANSIENT_ATT_LEADING))
                attToBeRemoved.add(attName);
        }

        for (int i = 0; i < attToBeRemoved.size(); i++)
            session.removeAttribute(attToBeRemoved.get(i));
    }

    // Return image names: large icon, small icon, saving icon, control icon,
    // environment icon
    public static String[] getImageNames(String imageStr) {
        String[] names = imageStr.split(",");
        String[] imgNames = new String[MAX_NUM_IMAGES];
        for (int i = 0; i < MAX_NUM_IMAGES; i++) {
            if (i < names.length)
                imgNames[i] = names[i].trim();
            else
                imgNames[i] = "";
        }

        return imgNames;
    }
    
    // Return program display names: display name, short name (used in enrollment page)
    public static String[] getProgramDisplayNames(StarsEnrLMProgram starsProg) {
        String[] names = StarsUtils.splitString( starsProg.getStarsWebConfig().getAlternateDisplayName(), "," );
        String[] dispNames = new String[2];
        for (int i = 0; i < 2; i++) {
            if (i < names.length)
                dispNames[i] = names[i].trim();
            else
                dispNames[i] = "";
        }
        
        // If not provided, default display name to program name, and short name to display name
        if (dispNames[0].length() == 0) {
            if (starsProg.getYukonName() != null)
                dispNames[0] = starsProg.getYukonName();
            else
                dispNames[0] = "(none)";
        }
        if (dispNames[1].length() == 0)
            dispNames[1] = dispNames[0];
        return dispNames;
    }

    public static String getInventoryLabel(StarsInventory starsInv) {
        String label = starsInv.getDeviceLabel();
        if (label.equals("")) {
            if (starsInv.getLMHardware() != null)
                label = starsInv.getLMHardware().getManufacturerSerialNumber();
            else if (starsInv.getMCT() != null)
                label = starsInv.getMCT().getDeviceName();
            else if (starsInv.getMeterNumber() != null)
                label = starsInv.getMeterNumber();
        }

        return label;
    }

    public static StarsEnrLMProgram getEnrollmentProgram(
                                                         StarsEnrollmentPrograms categories,
                                                         int programID) {
        for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
            StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
            for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
                if (category.getStarsEnrLMProgram(j).getProgramID() == programID)
                    return category.getStarsEnrLMProgram(j);
            }
        }

        return null;
    }
    
    public static void saveRequest(HttpServletRequest req, HttpSession session, String[] params) {
        Properties savedReq = new Properties();
        for (int i = 0; i < params.length; i++) {
            if (req.getParameter(params[i]) != null)
                savedReq.setProperty( params[i], req.getParameter(params[i]) );
        }
        
        session.setAttribute( ATT_LAST_SUBMITTED_REQUEST, savedReq );
    }

    public static String hideUnsetNumber(int num, int num_unset) {
        return (num == num_unset) ? "" : String.valueOf(num);
    }

    public static void newEnergyCompanySaveRequest(HttpServletRequest req, HttpSession session,
                                   String[] params) {
        Properties savedReq = new Properties();
        for (int i = 0; i < params.length; i++) {
            if (req.getParameter(params[i]) != null)
                savedReq.setProperty(params[i], req.getParameter(params[i]));
        }

        session.setAttribute(ATT_LAST_SUBMITTED_REQUEST, savedReq);
    }

    public static ContactNotification getContactNotification(
                                                             StarsCustomerContact contact,
                                                             int notifCatID) {
        for (int i = 0; i < contact.getContactNotificationCount(); i++) {
            if (contact.getContactNotification(i) != null
                && contact.getContactNotification(i).getNotifCatID() == notifCatID)
                return contact.getContactNotification(i);
        }

        return null;
    }

    public static ContactNotification createContactNotification(String value,
                                                                int notifCatID) {
        if (value != null && value.trim().length() > 0) {
            ContactNotification contNotif = new ContactNotification();
            contNotif.setNotifCatID(notifCatID);
            contNotif.setNotification(value);
            return contNotif;
        }

        return null;
    }

    public static boolean isCustomerFAQInherited(
                                                 LiteStarsEnergyCompany energyCompany) {
        if (energyCompany == null)
            return false;
        String faqLink = ServletUtils.getCustomerFAQLink(energyCompany);
        boolean isInherited = ServletUtils.INHERITED_FAQ.equals(faqLink);
        return isInherited;
    }

    /**
     * Get the FAQ link (usually a URL to the customer's website rather than
     * using the default FAQ page).
     * It will search in the first operator group, then in the first customer
     * group.
     * If FAQ link is not set in either of them, a null value will be returned.
     */
    public static String getCustomerFAQLink(LiteStarsEnergyCompany energyCompany) {
        String faqLink = null;

        LiteYukonGroup[] operGroups = energyCompany.getWebClientOperatorGroups();
        if (operGroups.length > 0)
            faqLink = DaoFactory.getRoleDao()
                .getRolePropValueGroup(operGroups[0],
                                       ConsumerInfoRole.WEB_LINK_FAQ, null);

        if (StarsUtils.forceNotNone(faqLink).length() == 0) {
            LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
            if (custGroups.length > 0)
                faqLink = DaoFactory.getRoleDao()
                    .getRolePropValueGroup(
                                           custGroups[0],
                                           ResidentialCustomerRole.WEB_LINK_FAQ,
                                           null);
        }

        if (StarsUtils.forceNotNone(faqLink).length() == 0)
            faqLink = null;

        return faqLink;
    }

    public static StarsYukonUser getStarsYukonUser(final HttpSession session) {
        return (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
    }

    public static StarsEnergyCompanySettings removeEnergyCompanySettings(
                                                                         final HttpSession session) {
        StarsEnergyCompanySettings settings = (StarsEnergyCompanySettings) session.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
        session.removeAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
        return settings;
    }

    public static String removeErrorMessage(final HttpSession session) {
        String errorMsg = (String) session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE);
        session.removeAttribute(ServletUtils.ATT_ERROR_MESSAGE);
        return errorMsg;
    }

    public static String removeConfirmMessage(final HttpSession session) {
        String confirmMsg = (String) session.getAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
        session.removeAttribute(ServletUtils.ATT_CONFIRM_MESSAGE);
        return confirmMsg;
    }

    public static StarsCustAccountInformation removeAccountInformation(
                                                                       final HttpSession session) {
        StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) session.getAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        session.removeAttribute(ServletUtils.TRANSIENT_ATT_CUSTOMER_ACCOUNT_INFO);
        return accountInfo;
    }

    /**
     * @return the current StarsYukonUser Object found in the request.
     * @throws NotLoggedInException if no session exists
     */
    public static StarsYukonUser getStarsYukonUser(final ServletRequest request)
            throws NotLoggedInException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new NotLoggedInException();
        }
        StarsYukonUser starsYukonUser = getStarsYukonUser(session);
        if (starsYukonUser == null) {
            throw new NotLoggedInException();
        }
        return starsYukonUser;
    }

    public static void deleteLogin(int userID) throws TransactionException {
        LiteContact liteContact = DaoFactory.getYukonUserDao()
            .getLiteContact(userID);
        if (liteContact != null) {
            liteContact.setLoginID(com.cannontech.user.UserUtils.USER_DEFAULT_ID);
            com.cannontech.database.data.customer.Contact contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent(liteContact);
            Transaction.createTransaction(Transaction.UPDATE,
                                          contact.getContact()).execute();
            ServerUtils.handleDBChange(liteContact,
                                       DBChangeMsg.CHANGE_TYPE_UPDATE);
        }

        com.cannontech.database.data.user.YukonUser yukonUser = new com.cannontech.database.data.user.YukonUser();
        LiteYukonUser liteUser = DaoFactory.getYukonUserDao()
            .getLiteYukonUser(userID);
        yukonUser.setUserID(new Integer(userID));
        Transaction.createTransaction(Transaction.DELETE, yukonUser).execute();

        StarsDatabaseCache.getInstance().deleteStarsYukonUser(userID);
        ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_DELETE);
    }

    public static LiteYukonUser createLogin(
                                            StarsUpdateLogin login,
                                            LiteContact liteContact,
                                            LiteStarsEnergyCompany energyCompany,
                                            boolean authTypeChange)
            throws TransactionException {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(
                                                                  "rolePropertyDao",
                                                                  RolePropertyDao.class);
        LiteYukonUser user = energyCompany.getUser();
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(
                                                                        YukonRoleProperty.DEFAULT_AUTH_TYPE,
                                                                        AuthType.class,
                                                                        user);

        com.cannontech.database.data.user.YukonUser dataUser = new com.cannontech.database.data.user.YukonUser();
        com.cannontech.database.db.user.YukonUser dbUser = dataUser.getYukonUser();

        if (login.hasGroupID()) {
            com.cannontech.database.db.user.YukonGroup dbGroup = new com.cannontech.database.db.user.YukonGroup();
            dbGroup.setGroupID(new Integer(login.getGroupID()));
            dataUser.getYukonGroups().addElement(dbGroup);
        }

        dbUser.setUsername(login.getUsername());
        if (authTypeChange)
            dbUser.setAuthType(AuthType.NONE);
        else
            dbUser.setAuthType(defaultAuthType);

        if (login.getStatus() != null)
            dbUser.setLoginStatus(StarsMsgUtils.getUserStatus(login.getStatus()));
        else
            dbUser.setLoginStatus(LoginStatusEnum.ENABLED);

        dataUser = Transaction.createTransaction(Transaction.INSERT, dataUser)
            .execute();
        LiteYukonUser liteUser = new LiteYukonUser(dbUser.getUserID()
            .intValue(), dbUser.getUsername(), dbUser.getLoginStatus());
        liteUser.setAuthType(dbUser.getAuthType());

        ServerUtils.handleDBChange(liteUser, DBChangeMsg.CHANGE_TYPE_ADD);

        if (authenticationService.supportsPasswordSet(defaultAuthType)
            && !authTypeChange) {
            authenticationService.setPassword(liteUser, login.getPassword());
        }

        if (liteContact != null) {
            liteContact.setLoginID(liteUser.getUserID());
            com.cannontech.database.data.customer.Contact contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent(liteContact);
            Transaction.createTransaction(Transaction.UPDATE,
                                          contact.getContact()).execute();
            ServerUtils.handleDBChange(liteContact,
                                       DBChangeMsg.CHANGE_TYPE_UPDATE);
        }

        return liteUser;
    }

    public static void saveSwitchCommand(LiteStarsLMHardware liteHw,
                                         String commandType,
                                         LiteStarsEnergyCompany energyCompany) {
        SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
        cmd.setEnergyCompanyID(energyCompany.getLiteID());
        cmd.setAccountID(liteHw.getAccountID());
        cmd.setInventoryID(liteHw.getInventoryID());
        cmd.setCommandType(commandType);

        SwitchCommandQueue.getInstance().addCommand(cmd, true);
    }

    public static void removeRouteResponse(int origInvID, StarsInventory starsInv,
                                     StarsCustAccountInformation starsAcctInfo,
                                     HttpSession session) {
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
                if (starsApps.getStarsAppliance(i).getInventoryID() == origInvID)
                    starsApps.getStarsAppliance(i)
                        .setInventoryID(starsInv.getInventoryID());
            }
        }

        String deviceLabel = ServletUtils.getInventoryLabel(starsInv);
        int invNo = 0;
        for (; invNo < starsInvs.getStarsInventoryCount(); invNo++) {
            String label = ServletUtils.getInventoryLabel(starsInvs.getStarsInventory(invNo));
            if (label.compareTo(deviceLabel) > 0)
                break;
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
                    session.setAttribute(ServletUtils.ATT_REDIRECT,
                                         redirect.substring(0, pos + 6) + invNo);
                } else {
                    pos = redirect.lastIndexOf("Item=");
                    if (pos >= 0) {
                        // Request from NewLabel.jsp, only count thermostats
                        int itemNo = 0;
                        for (int i = 0; i < invNo; i++) {
                            StarsInventory inv = starsInvs.getStarsInventory(i);
                            if (inv.getLMHardware() != null
                                && inv.getLMHardware()
                                    .getStarsThermostatSettings() != null)
                                itemNo++;
                        }

                        session.setAttribute(ServletUtils.ATT_REDIRECT,
                                             redirect.substring(0, pos + 5)
                                                     + itemNo);
                    }
                }
            }

            StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
            if (StarsUtils.isOperator(user.getYukonUser()))
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE,
                                     "Hardware information updated successfully");
            else
                session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE,
                                     "Thermostat name updated successfully");
        }
    }

    public static void updateLMConfiguration(
                                             StarsLMConfiguration starsHwConfig,
                                             LiteStarsLMHardware liteHw,
                                             LiteStarsEnergyCompany energyCompany)
            throws WebClientException {
        com.cannontech.database.data.stars.hardware.LMConfigurationBase config = new com.cannontech.database.data.stars.hardware.LMConfigurationBase();
        com.cannontech.database.db.stars.hardware.LMConfigurationBase configDB = config.getLMConfigurationBase();

        if (starsHwConfig.getColdLoadPickup() != null) {
            if (starsHwConfig.getColdLoadPickup().length() > 0)
                configDB.setColdLoadPickup(starsHwConfig.getColdLoadPickup());
            else
                configDB.setColdLoadPickup(CtiUtilities.STRING_NONE);
        }
        if (starsHwConfig.getTamperDetect() != null) {
            if (starsHwConfig.getTamperDetect().length() > 0)
                configDB.setTamperDetect(starsHwConfig.getTamperDetect());
            else
                configDB.setTamperDetect(CtiUtilities.STRING_NONE);
        }

        if (starsHwConfig.getExpressCom() != null) {
            com.cannontech.database.db.stars.hardware.LMConfigurationExpressCom xcom = new com.cannontech.database.db.stars.hardware.LMConfigurationExpressCom();
            xcom.setServiceProvider(new Integer(starsHwConfig.getExpressCom()
                .getServiceProvider()));
            xcom.setGEO(new Integer(starsHwConfig.getExpressCom().getGEO()));
            xcom.setSubstation(new Integer(starsHwConfig.getExpressCom()
                .getSubstation()));
            xcom.setFeeder(new Integer(starsHwConfig.getExpressCom()
                .getFeeder()));
            xcom.setZip(new Integer(starsHwConfig.getExpressCom().getZip()));
            xcom.setUserAddress(new Integer(starsHwConfig.getExpressCom()
                .getUserAddress()));
            xcom.setProgram(starsHwConfig.getExpressCom().getProgram());
            xcom.setSplinter(starsHwConfig.getExpressCom().getSplinter());
            config.setExpressCom(xcom);
        } else if (starsHwConfig.getVersaCom() != null) {
            com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom vcom = new com.cannontech.database.db.stars.hardware.LMConfigurationVersaCom();
            vcom.setUtilityID(new Integer(starsHwConfig.getVersaCom()
                .getUtility()));
            vcom.setSection(new Integer(starsHwConfig.getVersaCom()
                .getSection()));
            vcom.setClassAddress(new Integer(starsHwConfig.getVersaCom()
                .getClassAddress()));
            vcom.setDivisionAddress(new Integer(starsHwConfig.getVersaCom()
                .getDivision()));
            config.setVersaCom(vcom);
        } else if (starsHwConfig.getSA205() != null) {
            com.cannontech.database.db.stars.hardware.LMConfigurationSA205 sa205 = new com.cannontech.database.db.stars.hardware.LMConfigurationSA205();
            sa205.setSlot1(new Integer(starsHwConfig.getSA205().getSlot1()));
            sa205.setSlot2(new Integer(starsHwConfig.getSA205().getSlot2()));
            sa205.setSlot3(new Integer(starsHwConfig.getSA205().getSlot3()));
            sa205.setSlot4(new Integer(starsHwConfig.getSA205().getSlot4()));
            sa205.setSlot5(new Integer(starsHwConfig.getSA205().getSlot5()));
            sa205.setSlot6(new Integer(starsHwConfig.getSA205().getSlot6()));
            config.setSA205(sa205);
        } else if (starsHwConfig.getSA305() != null) {
            com.cannontech.database.db.stars.hardware.LMConfigurationSA305 sa305 = new com.cannontech.database.db.stars.hardware.LMConfigurationSA305();
            sa305.setUtility(new Integer(starsHwConfig.getSA305().getUtility()));
            sa305.setGroupAddress(new Integer(starsHwConfig.getSA305()
                .getGroup()));
            sa305.setDivision(new Integer(starsHwConfig.getSA305()
                .getDivision()));
            sa305.setSubstation(new Integer(starsHwConfig.getSA305()
                .getSubstation()));
            sa305.setRateFamily(new Integer(starsHwConfig.getSA305()
                .getRateFamily()));
            sa305.setRateMember(new Integer(starsHwConfig.getSA305()
                .getRateMember()));
            sa305.setRateHierarchy(new Integer(starsHwConfig.getSA305()
                .getRateHierarchy()));
            config.setSA305(sa305);
        } else if (starsHwConfig.getSASimple() != null) {
            com.cannontech.database.db.stars.hardware.LMConfigurationSASimple simple = new com.cannontech.database.db.stars.hardware.LMConfigurationSASimple();
            simple.setOperationalAddress(starsHwConfig.getSASimple()
                .getOperationalAddress());
            config.setSASimple(simple);
        }

        try {
            if (liteHw.getConfigurationID() == 0) {
                config = Transaction.createTransaction(Transaction.INSERT,
                                                       config).execute();

                com.cannontech.database.data.stars.hardware.LMHardwareBase hw = new com.cannontech.database.data.stars.hardware.LMHardwareBase();
                com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
                StarsLiteFactory.setLMHardwareBase(hw, liteHw);
                hwDB.setConfigurationID(config.getLMConfigurationBase()
                    .getConfigurationID());

                hwDB = Transaction.createTransaction(Transaction.UPDATE, hwDB)
                    .execute();

                liteHw.setConfigurationID(hwDB.getConfigurationID().intValue());
                LiteLMConfiguration liteCfg = new LiteLMConfiguration();
                StarsLiteFactory.setLiteLMConfiguration(liteCfg, config);
                liteHw.setLMConfiguration(liteCfg);
            } else {
                config.setConfigurationID(new Integer(
                                                      liteHw.getConfigurationID()));

                // Check to see if the configuration is in both the parent and
                // the child table
                if (config.getExpressCom() != null
                    && liteHw.getLMConfiguration().getExpressCom() == null) {
                    Transaction.createTransaction(Transaction.INSERT,
                                                  config.getExpressCom())
                        .execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB)
                        .execute();
                } else if (config.getVersaCom() != null
                           && liteHw.getLMConfiguration().getVersaCom() == null) {
                    Transaction.createTransaction(Transaction.INSERT,
                                                  config.getVersaCom())
                        .execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB)
                        .execute();
                } else if (config.getSA205() != null
                           && liteHw.getLMConfiguration().getSA205() == null) {
                    Transaction.createTransaction(Transaction.INSERT,
                                                  config.getSA205()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB)
                        .execute();
                } else if (config.getSA305() != null
                           && liteHw.getLMConfiguration().getSA305() == null) {
                    Transaction.createTransaction(Transaction.INSERT,
                                                  config.getSA305()).execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB)
                        .execute();
                } else if (config.getSASimple() != null
                           && liteHw.getLMConfiguration().getSASimple() == null) {
                    Transaction.createTransaction(Transaction.INSERT,
                                                  config.getSASimple())
                        .execute();
                    Transaction.createTransaction(Transaction.UPDATE, configDB)
                        .execute();
                } else {
                    config = Transaction.createTransaction(Transaction.UPDATE,
                                                           config).execute();
                }

                StarsLiteFactory.setLiteLMConfiguration(
                                                        liteHw.getLMConfiguration(),
                                                        config);
            }
        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);
            throw new WebClientException(
                                         "Failed to update the hardware addressing tables");
        }
    }

    public static StarsUpdateLMHardwareConfigResponse updateLMHardwareConfig(
                                                                             StarsUpdateLMHardwareConfig updateHwConfig,
                                                                             LiteStarsLMHardware liteHw,
                                                                             int userID,
                                                                             LiteStarsEnergyCompany energyCompany)
            throws WebClientException {
        LiteStarsCustAccountInformation liteAcctInfo = null;
        List<LiteStarsLMHardware> hwsToConfig = null;

        // save configuration first, so its available to compute groupID later
        // on
        if (updateHwConfig.getStarsLMConfiguration() != null) {
            updateLMConfiguration(updateHwConfig.getStarsLMConfiguration(),
                                  liteHw, energyCompany);

            hwsToConfig = new ArrayList<LiteStarsLMHardware>();
            hwsToConfig.add(liteHw);
        }

        if (liteHw.getAccountID() > 0) {
            CustomerAccountDao customerAccountDao = YukonSpringHook.getBean(
                                                                            "customerAccountDao",
                                                                            CustomerAccountDao.class);
            CustomerAccount customerAccount = customerAccountDao.getById(liteHw.getAccountID());

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

            LiteYukonUser currentUser = DaoFactory.getYukonUserDao()
                .getLiteYukonUser(userID);

            ProgramEnrollmentService programEnrollmentService = YukonSpringHook.getBean(
                                                                                        "starsProgramEnrollmentService",
                                                                                        ProgramEnrollmentService.class);
            hwsToConfig = programEnrollmentService.applyEnrollmentRequests(
                                                                           customerAccount,
                                                                           requests,
                                                                           liteHw,
                                                                           currentUser);

            if (!hwsToConfig.contains(liteHw))
                hwsToConfig.add(0, liteHw);
            // refresh account info, after update program enrollment
            liteAcctInfo = energyCompany.getCustAccountInformation(
                                                                   liteHw.getAccountID(),
                                                                   true);
        }

        StarsInventories starsInvs = new StarsInventories();
        boolean disabled = false;

        String trackHwAddr = energyCompany.getEnergyCompanySetting(EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING);
        boolean useHardwareAddressing = Boolean.valueOf(trackHwAddr)
            .booleanValue();

        for (int i = 0; i < hwsToConfig.size(); i++) {
            LiteStarsLMHardware lHw = hwsToConfig.get(i);

            if (!updateHwConfig.getSaveConfigOnly()) {
                boolean toConfig = true;
                if (liteAcctInfo != null && !useHardwareAddressing)
                    toConfig = isToConfig(lHw, liteAcctInfo);
                if (lHw.equals(liteHw))
                    disabled = !toConfig;

                if (updateHwConfig.getSaveToBatch()) {
                    String commandType = (toConfig) ? SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE
                            : SwitchCommandQueue.SWITCH_COMMAND_DISABLE;
                    saveSwitchCommand(lHw, commandType, energyCompany);
                } else {
                    if (toConfig)
                        ServletUtils.sendConfigCommand(energyCompany, lHw,
                                                       true, null);
                    else
                        ServletUtils.sendDisableCommand(energyCompany, lHw,
                                                        null);
                }
            }

            if (liteAcctInfo != null) {
                StarsInventory starsInv = StarsLiteFactory.createStarsInventory(
                                                                                lHw,
                                                                                energyCompany);
                starsInvs.addStarsInventory(starsInv);
            }
        }

        // Log activity
        String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber();
        if (!disabled) {
            for (int i = 0; i < hwsToConfig.size(); i++) {
                LiteStarsLMHardware lHw = hwsToConfig.get(i);
                if (!lHw.equals(liteHw))
                    logMsg += "," + lHw.getManufacturerSerialNumber();
            }
        }

        String action = null;
        if (updateHwConfig.getSaveConfigOnly())
            action = ActivityLogActions.HARDWARE_SAVE_CONFIG_ONLY_ACTION;
        else if (updateHwConfig.getSaveToBatch())
            action = ActivityLogActions.HARDWARE_SAVE_TO_BATCH_ACTION;
        else if (disabled)
            action = ActivityLogActions.HARDWARE_DISABLE_ACTION;
        else
            action = ActivityLogActions.HARDWARE_CONFIGURATION_ACTION;

        int customerID = (liteAcctInfo != null) ? liteAcctInfo.getCustomer()
            .getCustomerID() : 0;
        ActivityLogger.logEvent(userID, liteHw.getAccountID(),
                                energyCompany.getLiteID(), customerID, action,
                                logMsg);

        if (liteAcctInfo != null) {
            StarsUpdateLMHardwareConfigResponse resp = new StarsUpdateLMHardwareConfigResponse();
            resp.setStarsInventories(starsInvs);
            resp.setStarsLMPrograms(StarsLiteFactory.createStarsLMPrograms(
                                                                           liteAcctInfo,
                                                                           energyCompany));
            resp.setStarsAppliances(StarsLiteFactory.createStarsAppliances(
                                                                           liteAcctInfo.getAppliances(),
                                                                           energyCompany));
            return resp;
        }

        return null;
    }
    
    public static boolean isToConfig(LiteStarsLMHardware liteHw, LiteStarsCustAccountInformation liteAcctInfo) {
        for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            LiteStarsAppliance liteApp = liteAcctInfo.getAppliances().get(j);
            if (liteApp.getInventoryID() == liteHw.getInventoryID())
                return true;
        }
        
        return false;
    }
    
    public static void deleteCustomerAccount(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
    throws TransactionException, WebClientException
{
    ApplianceDao applianceDao = YukonSpringHook.getBean("applianceDao", ApplianceDao.class);
    
    
     /* Remove all the inventory from the account and move it to the warehouse.  This
      * also includes any unenrolling that is needed.
      */
    List<Integer> inventories = new ArrayList<Integer>(); 
    inventories.addAll(liteAcctInfo.getInventories());
    for (int inventoryId : inventories) {
        
        StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
        deleteHw.setInventoryID(inventoryId);
        deleteHw.setRemoveDate(new Date());
        
        removeInventory(deleteHw, liteAcctInfo, energyCompany);
    }
    
    applianceDao.deleteAppliancesByAccountId(liteAcctInfo.getAccountID());

    com.cannontech.database.data.stars.customer.CustomerAccount account =
            StarsLiteFactory.createCustomerAccount(liteAcctInfo, energyCompany);
    Transaction.createTransaction( Transaction.DELETE, account ).execute();
    
    // Delete contacts from database
    LiteContact primContact = DaoFactory.getContactDao().getContact( liteAcctInfo.getCustomer().getPrimaryContactID() );
    com.cannontech.database.data.customer.Contact contact =
            (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( primContact );
    Transaction.createTransaction( Transaction.DELETE, contact ).execute();
    ServerUtils.handleDBChange( primContact, DBChangeMsg.CHANGE_TYPE_DELETE );
    
    Vector<LiteContact> contacts = liteAcctInfo.getCustomer().getAdditionalContacts();
    for (int i = 0; i < contacts.size(); i++) {
        LiteContact liteContact = contacts.get(i);
        contact = (com.cannontech.database.data.customer.Contact) StarsLiteFactory.createDBPersistent( liteContact );
        Transaction.createTransaction( Transaction.DELETE, contact ).execute();
        ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
        int userId = liteContact.getLoginID();
        if (userId != UserUtils.USER_DEFAULT_ID &&
                userId != UserUtils.USER_ADMIN_ID &&
                userId != UserUtils.USER_YUKON_ID)
                ServletUtils.deleteLogin( userId );
    }
    
    // Delete login
    int userID = primContact.getLoginID();
    if (userID != UserUtils.USER_DEFAULT_ID &&
        userID != UserUtils.USER_ADMIN_ID &&
        userID != UserUtils.USER_YUKON_ID)
        ServletUtils.deleteLogin( userID );
    
    // Delete lite and stars objects
    energyCompany.deleteCustAccountInformation( liteAcctInfo );
    ServerUtils.handleDBChange( liteAcctInfo, DBChangeMsg.CHANGE_TYPE_DELETE );
}

    public static void removeInventory(StarsDeleteLMHardware deleteHw, 
                                       LiteStarsCustAccountInformation liteAcctInfo, 
                                       LiteStarsEnergyCompany energyCompany)
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
                if (removeDate == null) removeDate = new java.util.Date();
                
                // Add "Uninstall" to hardware events
                com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
                com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
                com.cannontech.database.db.stars.event.LMCustomerEventBase eventBaseDB = event.getLMCustomerEventBase();
                
                int hwEventEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE ).getEntryID();
                int uninstallActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_UNINSTALL ).getEntryID();
                
                eventBaseDB.setEventTypeID( new Integer(hwEventEntryID) );
                eventBaseDB.setActionID( new Integer(uninstallActID) );
                eventBaseDB.setEventDateTime( removeDate );
                if (liteAcctInfo != null)
                    eventBaseDB.setNotes( "Removed from account #" + liteAcctInfo.getCustomerAccount().getAccountNumber() );
                eventDB.setInventoryID( new Integer(liteInv.getInventoryID()) );
                event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
                
                event = Transaction.createTransaction( Transaction.INSERT, event ).execute();
                
                if (liteInv instanceof LiteStarsLMHardware)
                    applianceDao.deleteAppliancesByAccountIdAndInventoryId(liteAcctInfo.getAccountID(), liteInv.getInventoryID());
                
                // Removes any entries found in the inventoryBase Table
                com.cannontech.database.db.stars.hardware.InventoryBase invDB =
                        new com.cannontech.database.db.stars.hardware.InventoryBase();
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
        
}