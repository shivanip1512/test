package com.cannontech.web.stars.dr.operator.importAccounts.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.service.StarsControllableDeviceDTOConverter;
import com.cannontech.stars.service.UpdatableAccountConverter;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsClientRequestException;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.ImportManagerUtil;
import com.cannontech.stars.ws.dto.StarsControllableDeviceDTO;
import com.cannontech.stars.ws.helper.StarsControllableDeviceHelper;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;

public class AccountImportService {
    
    private PrintWriter importLog;
    
    private AccountEventLogService accountEventLogService;
    private HardwareEventLogService hardwareEventLogService;
    
    private StarsCustAccountInformationDao starsCustAccountInformationDao; 
    private StarsSearchDao starsSearchDao;
    private YukonUserDao yukonUserDao;
    private Executor executor;
    private AccountService accountService;
    private UpdatableAccountConverter updatableAccountConverter;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private StarsControllableDeviceDTOConverter starsControllableDeviceDTOConverter;
    private StarsControllableDeviceHelper starsControllableDeviceHelper;
    private EnrollmentHelperService enrollmentHelperService;
    private static final Logger log = YukonLogManager.getLogger(AccountImportService.class);
    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
    
    public void startAccountImport(final AccountImportResult result, final YukonUserContext userContext) {
        executor.execute(new Runnable() {
            public void run() {
                processAccountImport(result, userContext);
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    private void processAccountImport(AccountImportResult result, YukonUserContext userContext) {
        String email = result.getEmail();
        boolean preScan = result.isPrescan();
        final File custFile = result.getCustomerFile();
        final File hwFile = result.getHardwareFile();
        
        File logFile = null;
        String errorMsg = null;
        
        List<String[]> custFieldsList = null;
        List<String[]> hwFieldsList = null;
        List<String[]> appFieldsList = null;
        int lineNo = 0;
        
        int[] custColIdx = new int[ result.CUST_COLUMNS.length ];
        int[] hwColIdx = new int [ result.HW_COLUMNS.length ];
        int numCustCol = result.CUST_COLUMNS.length;
        int numHwCol = result.HW_COLUMNS.length;
        
        for (int i = 0; i < result.CUST_COLUMNS.length; i++)
            custColIdx[i] = -1;
        for (int i = 0; i < result.HW_COLUMNS.length; i++)
            hwColIdx[i] = -1;
        
        // Pre-scan the import file(s). If customer and hardware information is in the same file,
        // no optimization will be preformed.
        Map<String, String[]> custFieldsMap = new HashMap<String, String[]>();  // Map from account # (String) to customer fields (String[])
        Map<String, String> hwFieldsMap = new HashMap<String, String>();    // Map from serial # (String) to account # (String)

        try {
            final String fs = System.getProperty( "file.separator" );
            LiteStarsEnergyCompany energyCompany = result.getEnergyCompany();
            File baseDir = getBaseDir(energyCompany);
            
            //  Gets the archive directory found inside the default directory
            File archiveDir = new File(baseDir, fs + ServerUtils.ARCHIVE_DIR);
            if (!archiveDir.exists()) {
                archiveDir.mkdirs();
            }

            // Gets the upload directory found inside the default directory
            File importFailsDir = new File(archiveDir, fs + ServerUtils.FAILED_IMPORT_DIR);
            if (!importFailsDir.exists()) {
                importFailsDir.mkdirs();
            }
            
            Date now = new Date();
            String logFileName = null; 
            String custFileName = null;
            String hwFileName = null;
            if (custFile != null && custFile.length() > 4){
                custFileName = custFile.getName();
                logFileName = custFileName.substring(0, custFileName.length()-4)+"-"+StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".log";
                if(preScan){
                    logFileName = "Prescan-"+logFileName;
                }
            }
            
            if (hwFile != null && hwFile.length() > 4){
                hwFileName = hwFile.getName();
                logFileName = hwFileName.substring(0, hwFileName.length()-4)+"-"+StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".log";
                if(preScan){
                    logFileName = "Prescan-"+logFileName;
                }
            }
            
            logFile = new File(importFailsDir, logFileName);
            
            importLog = new PrintWriter(new FileWriter(logFile), true);
            importLog.println("Start time: " + StarsUtils.formatDate( now, energyCompany.getDefaultTimeZone() ));
            importLog.println();
            
            int errors = 0;

            // Creates a list of all the appliance categories names
            Iterable<LiteApplianceCategory> allApplianceCategories = energyCompany.getAllApplianceCategories();
            List<String> applianceNameList = new ArrayList<String>(); 
            for (LiteApplianceCategory liteApplianceCategory : allApplianceCategories) {
                applianceNameList.add(liteApplianceCategory.getDescription());
            }
            
            if (custFile != null) {
                custFieldsList = new ArrayList<String[]>();
                List<String> addedUsernames = new ArrayList<String>();
                List<String> removedUsernames = new ArrayList<String>();
                result.setCustLines(new TreeMap<Integer, String[]>());
                boolean hwInfoContained = false;
                FileInputStream fiStream = new FileInputStream(custFile);
                InputStreamReader isReader = new InputStreamReader(fiStream);
                BufferedReader reader = new BufferedReader(isReader);
                String line = null;
                lineNo = 0;
                Integer lineNoKey;
                
                // Sets up the archive file
                String archiveFileName = custFileName.substring(0,custFileName.length()-4)+"-"+StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".csv";
                File archiveFile = new File(archiveDir, archiveFileName);
                PrintWriter archive = new PrintWriter(new FileWriter(archiveFile), true);
                
                while ((line = reader.readLine()) != null) {
                    // This line adds the latest line to the archive file
                    archive.println(line);
                    lineNo++;
                    lineNoKey = lineNo;
                    
                    if (line.trim().length() == 0 || line.charAt(0) == '#')
                        continue;

                    result.getCustLines().put(lineNo, new String[]{line, null});
                    
                    result.setPosition("customer file line #" + lineNo);
                    
                    if (lineNo == 1) {
                        if (line.startsWith( result.COL_NAME_LABEL ) || line.startsWith( result.CUST_COLUMNS[0] )) {
                            String[] labels;
                            if(line.startsWith( result.COL_NAME_LABEL ))
                                labels = StarsUtils.splitString( line.substring(result.COL_NAME_LABEL.length()), "," );
                            else
                                labels = StarsUtils.splitString( line, "," );
                            numCustCol = labels.length;
                            
                            for (int i = 0; i < labels.length; i++) {
                                for (int j = 0; j < result.CUST_COLUMNS.length; j++) {
                                    if (labels[i].equalsIgnoreCase( result.CUST_COLUMNS[j] )) {
                                        custColIdx[j] = i;
                                        break;
                                    }
                                }
                                
                                if (hwFile == null) {
                                    // There could be only one file which also contains hardware information
                                    for (int j = 0; j < result.HW_COLUMNS.length; j++) {
                                        if (labels[i].equalsIgnoreCase( result.HW_COLUMNS[j] )) {
                                            hwColIdx[j] = i;
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            if (custColIdx[result.COL_ACCOUNT_NO] == -1) {
                                String errorStr = "The required column \'\" + CUST_COLUMNS[COL_ACCOUNT_NO] + \"\' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            
                            hwInfoContained = (hwColIdx[result.COL_DEVICE_TYPE] != -1) ||
                                              (hwColIdx[result.COL_SERIAL_NO] != -1);

                            if (hwInfoContained && hwColIdx[result.COL_DEVICE_TYPE] == -1) {
                                String errorStr = "The required column \'\" + HW_COLUMNS[COL_DEVICE_TYPE] + \"\' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            
                            if (hwInfoContained && hwColIdx[result.COL_SERIAL_NO] == -1) {
                                String errorStr = "The required column \'\" + HW_COLUMNS[COL_SERIAL_NO] + \"\' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            
                            continue;
                        }
                        
                        for (int i = 0; i < result.CUST_COLUMNS.length; i++) {
                            custColIdx[i] = i;
                        }    
                    }
                    
                    String[] columns = StarsUtils.splitString( line, "," );
                    if (columns.length > numCustCol) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    String[] custFields = prepareFields( ImportManagerUtil.NUM_ACCOUNT_FIELDS );
                    custFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
                    setCustomerFields( custFields, columns, custColIdx, result );

                    if (custFields[ImportManagerUtil.IDX_ACCOUNT_NO].trim().length() == 0) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    String action = custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION];
                    if (!(StringUtils.isBlank(action) || action.equalsIgnoreCase("INSERT") || action.equalsIgnoreCase("UPDATE") || action.equalsIgnoreCase("REMOVE"))) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: " + action + " is not a valid action]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    result.setInsertSpecified(custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT"));
                    /*
                     * Some customers use rotation digits on the end of account numbers.
                     * EXAMPLE: if a customer changed at account 123456, the whole account number
                     * plus rotation digits might change like this: 12345610 to 12345620, so we want
                     * to only consider the accountnumber itself.  The number of digits to consider
                     * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                     */
                    LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
                    
                    // Nothing is done to existing Accounts when CUST_ACTION is set to INSERT.
                    if ((liteAcctInfo != null) && result.isInsertSpecified()) {
                        
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: INSERT account action, but account already exists.]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    if (!preScan) {
                        try {
                            liteAcctInfo = importAccount( custFields, energyCompany, result, userContext);
                        } catch (Exception ex) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: " + ex.getMessage() + "]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                    else {
                        if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                            // The current record is a "REMOVE"
                            String[] prevFields = custFieldsMap.get( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
                            if (prevFields != null) {
                                if (prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                                    // If a "REMOVE" record already exists in the list, do nothing but report a warning
                                    importLog.println( "WARNING at " + result.getPosition() + ": account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " already removed earlier, record ignored" );
                                    continue;
                                }
                                
                                // Found a "INSERT" or "UPDATE" record in the list, remove the old record,
                                // and add the new "REMOVE" record if necessary 
                                if (!hwInfoContained) custFieldsList.remove( prevFields );
                                if (hwInfoContained || !prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT"))
                                    custFieldsList.add( custFields );
                                custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );

                                if (liteAcctInfo != null) {
                                    LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
                                    if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID && !removedUsernames.contains( login.getUsername() ))
                                        removedUsernames.add( login.getUsername() );
                                }
                                else if (prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0) {
                                    addedUsernames.remove( prevFields[ImportManagerUtil.IDX_USERNAME] );
                                }
                            }
                            else {
                                if (liteAcctInfo != null) {
                                    // Found the account in the database, add the "REMOVE" record to the list
                                    custFieldsList.add( custFields );
                                    custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
                                    
                                    LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
                                    if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID && !removedUsernames.contains( login.getUsername() ))
                                        removedUsernames.add( login.getUsername() );
                                }
                                else {
                                    // Account not found in the database, do nothing but report a warning
                                    importLog.println( "WARNING at " + result.getPosition() + ": account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " doesn't exist, record ignored" );
                                    continue;
                                }
                            }
                        }
                        else {
                            // The current record is a "INSERT" or "UPDATE"
                            boolean checkUsername = false;
                            
                            String[] prevFields = custFieldsMap.get( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
                            if (prevFields != null) {
                                if (prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                                    // If a "REMOVE" record already exists in the list, add the new record with action set to "INSERT"
                                    custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "INSERT";
                                    custFieldsList.add( custFields );
                                    custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
                                    
                                    checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
                                }
                                else {
                                    // If a "INSERT" or "UPDATE" record already exists in the list, replace the old record
                                    // with the new record except the action field, also keep the old user login fields if provided
                                    if (prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0) {
                                        custFields[ImportManagerUtil.IDX_USERNAME] = prevFields[ImportManagerUtil.IDX_USERNAME];
                                        custFields[ImportManagerUtil.IDX_PASSWORD] = prevFields[ImportManagerUtil.IDX_PASSWORD];
                                    }
                                    
                                    if (!hwInfoContained) {
                                        custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = prevFields[ImportManagerUtil.IDX_ACCOUNT_ACTION];
                                        custFieldsList.remove( prevFields );
                                    }
                                    else
                                        custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "UPDATE";
                                    
                                    custFieldsList.add( custFields );
                                    custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
                                    
                                    checkUsername = prevFields[ImportManagerUtil.IDX_USERNAME].trim().length() == 0
                                            && custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
                                }
                            }
                            else {
                                // Add the new record to the list with the action field set accordingly
                                if (liteAcctInfo != null) {
                                    custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "UPDATE";
                                    
                                    LiteYukonUser login = DaoFactory.getContactDao().getYukonUser( liteAcctInfo.getCustomer().getPrimaryContactID() );
                                    if (login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID)
                                        custFields[ImportManagerUtil.IDX_USERNAME] = login.getUsername();
                                    else
                                        checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
                                }
                                else {
                                    custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = "INSERT";
                                    checkUsername = custFields[ImportManagerUtil.IDX_USERNAME].trim().length() > 0;
                                }
                                
                                custFieldsList.add( custFields );
                                custFieldsMap.put( custFields[ImportManagerUtil.IDX_ACCOUNT_NO], custFields );
                            }
                            
                            // Check if the username already exists
                            if (checkUsername) {
                                if (custFields[ImportManagerUtil.IDX_PASSWORD].length() == 0) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Password cannot be empty]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }   

                                String username = custFields[ImportManagerUtil.IDX_USERNAME];
                                if (addedUsernames.contains( username )) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Username would have already been added by the import program]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }

                                if (removedUsernames.contains( username ))
                                    removedUsernames.remove( username );
                                else if (yukonUserDao.getLiteYukonUser( username ) != null) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Username already exists]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }
                                addedUsernames.add( username );
                            }
                        }
                    }
                    
                    if (hwInfoContained) {
                        if (preScan) {
                            if (hwFieldsList == null) hwFieldsList = new ArrayList<String[]>();
                            if (hwColIdx[result.COL_APP_TYPE] != -1 && appFieldsList == null)
                                appFieldsList = new ArrayList<String[]>();
                        }
                        
                        String[] hwFields = prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
                        hwFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
                        setHardwareFields( hwFields, columns, hwColIdx, result );
                        
                        if (hwFields[ImportManagerUtil.IDX_SERIAL_NO].trim().length() == 0) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Serial number cannot be empty]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                        
                        if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")
                            || hwFields[ImportManagerUtil.IDX_SERIAL_NO].trim().length() == 0)
                        {
                            // If customer action is "REMOVE", or serial # field is empty, this record is for customer action only
                            if (preScan) {
                                hwFieldsList.add( null );
                                if (appFieldsList != null) appFieldsList.add( null );
                            }
                            continue;
                        }
                        
                        if (hwFields[ImportManagerUtil.IDX_DEVICE_TYPE].trim().length() == 0) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }

                        result.setInsertSpecified(hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("INSERT"));
                        
                        YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
                        YukonListEntry deviceType = DaoFactory.getYukonListDao().getYukonListEntry( devTypeList, hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] );
                        if (deviceType == null) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] + "\"]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                        String[] appFields = null;
                        if (hwColIdx[result.COL_APP_TYPE] != -1) {
                            appFields = prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
                            setApplianceFields( appFields, columns, hwColIdx, result );
                            
                            if ((appFields[ImportManagerUtil.IDX_APP_TYPE].trim().length() > 0) &&
                                (!applianceNameList.contains(appFields[ImportManagerUtil.IDX_APP_TYPE]))) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: Appliance Type was supplied, but doesn't exist]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }                           
                        }
                        
                        if (!preScan) {
                            try {
                                LiteInventoryBase liteInv = null;
                                
                                // IMPORT HARDWARE
                                liteInv = importHardware( hwFields, liteAcctInfo, energyCompany, 
                                                          result, userContext);

                                if (hwFields[ImportManagerUtil.IDX_PROGRAM_NAME].trim().length() > 0
                                        && !hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE"))
                                {
                                    programSignUp(hwFields, appFields, liteAcctInfo, liteInv, energyCompany, result);
                                }
                            } catch (WebClientException e) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            } catch (SQLException sqle) {
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: " + sqle.getMessage() + "]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                importLog.println(sqle.getStackTrace());
                                continue;
                            }
                        }
                        else {
                            String acctNo = hwFieldsMap.get( hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
                            if (acctNo == null) {
                                LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsSearchDao.searchLMHardwareBySerialNumber(hwFields[ImportManagerUtil.IDX_SERIAL_NO], energyCompany);
                                if (liteHw != null && liteHw.getAccountID() > 0)
                                    acctNo = starsCustAccountInformationDao.getById(liteHw.getAccountID(), energyCompany.getEnergyCompanyId()).getCustomerAccount().getAccountNumber();
                            }
                            
                            if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                // Remove a hardware from an account, if hardware doesn't exist in the account, report a warning
                                if (acctNo == null || !acctNo.equalsIgnoreCase( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] )) {
                                    importLog.println( "WARNING at " + result.getPosition() + ": serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account" );
                                    if (preScan) {
                                        hwFieldsList.add( null );
                                        if (appFieldsList != null) appFieldsList.add( null );
                                    }
                                    continue;
                                }
                                
                                if (preScan) {
                                    hwFieldsList.add( hwFields );
                                    hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], "" );
                                }
                            }
                            else {
                                // Insert/update a hardware in an account, if hardware already exists in another account, report an error 
                                if (acctNo != null && !acctNo.equals("") && !acctNo.equalsIgnoreCase( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] )) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " already exists in account #" + acctNo + "]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }

                                if (acctNo != null && acctNo.equals( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] ))
                                    hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "UPDATE";
                                else
                                    hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "INSERT";
                                
                                if (preScan) {
                                    hwFieldsList.add( hwFields );
                                    hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
                                }
                            }
                            
                            if (appFieldsList != null) appFieldsList.add( appFields );
                        }
                    }
                    
                    if (result.isCanceled()) {
                        throw new Exception();
                    }
                }
                
                errors += result.custFileErrors;
                // Closing the buffered reader closes all the i/o behind.
                // In this case the inputStream is closed b/c the reader is being closed.
                reader.close();
                archive.close();
                
                if(!preScan){
                    result.setNumAcctTotal(custFieldsList.size());
                    
                    boolean isDeleted = custFile.delete();
                    if (isDeleted == false) {
                        importLog.println("* "+custFile.getName()+" was not deleted *");
                    }
                }
            }
            
            if (hwFile != null) {
                hwFieldsList = new ArrayList<String[]>();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(hwFile)));
                String line = null;
                result.setHwLines(new TreeMap<Integer, String[]>());
                lineNo = 0;
                Integer lineNoKey;
                
                // Sets up the archive file
                String archiveFileName = hwFileName+StarsUtils.starsDateFormat.format(now) + "_" + StarsUtils.starsTimeFormat.format(now) + ".csv";
                File archiveFile = new File(archiveDir, archiveFileName);
                PrintWriter archive = new PrintWriter(new FileWriter(archiveFile), true);

                while ((line = reader.readLine()) != null) {
                    // This line adds the latest line to the archive file
                    archive.println(line);
                    
                    lineNo++;
                    lineNoKey = lineNo;
                    
                    if (line.trim().length() == 0 || line.charAt(0) == '#')
                        continue;

                    result.getHwLines().put(lineNo, new String[]{line, null});
                    
                    result.setPosition("hardware file line #" + lineNo);
                    
                    if (lineNo == 1) {
                        if (line.startsWith( result.COL_NAME_LABEL ) || line.startsWith( result.CUST_COLUMNS[0] )) {
                            String[] labels;
                            if(line.startsWith( result.COL_NAME_LABEL ))
                                labels = StarsUtils.splitString( line.substring(result.COL_NAME_LABEL.length()), "," );
                            else
                                labels = StarsUtils.splitString( line, "," );
                            numHwCol = labels.length;
                            
                            for (int i = 0; i < labels.length; i++) {
                                for (int j = 0; j < result.HW_COLUMNS.length; j++) {
                                    if (labels[i].equalsIgnoreCase( result.HW_COLUMNS[j] )) {
                                        hwColIdx[j] = i;
                                        break;
                                    }
                                }
                            }
                            
                            if (hwColIdx[result.COL_ACCOUNT_NO] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_ACCOUNT_NO] + "' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            if (hwColIdx[result.COL_SERIAL_NO] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_SERIAL_NO] + "' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            if (hwColIdx[result.COL_DEVICE_TYPE] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_DEVICE_TYPE] + "' is missing";
                                automationCheck(errorStr, result.isAutomatedImport());
                            }
                            
                            continue;
                        }
                        
                        for (int i = 0; i < result.HW_COLUMNS.length; i++) {
                            hwColIdx[i] = i;
                        }
                    }
                    
                    String[] columns = StarsUtils.splitString( line, "," );
                    if (columns.length > numHwCol) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    String[] hwFields = prepareFields( ImportManagerUtil.NUM_INV_FIELDS );
                    hwFields[ImportManagerUtil.IDX_LINE_NUM] = String.valueOf(lineNo);
                    setHardwareFields( hwFields, columns, hwColIdx, result );
                    
                    if (hwFields[ImportManagerUtil.IDX_ACCOUNT_ID].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    if (hwFields[ImportManagerUtil.IDX_SERIAL_NO].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Serial # cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    if (hwFields[ImportManagerUtil.IDX_DEVICE_TYPE].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
                    YukonListEntry deviceType = DaoFactory.getYukonListDao().getYukonListEntry( devTypeList, hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] );
                    if (deviceType == null) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportManagerUtil.IDX_DEVICE_TYPE] + "\"]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    String[] custFields = null;
                    if (preScan)
                        custFields = custFieldsMap.get( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
                    LiteStarsCustAccountInformation liteAcctInfo = null;
                    
                    if (custFields != null) {
                        if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equals("REMOVE")) {
                            if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                importLog.println( "WARNING at " + result.getPosition() + ": account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " is removed by the import program, record ignored" );
                                continue;
                            }
                            
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Cannot import hardware, account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " will be removed by the import program]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                    else 
                    {
                        /*
                         * Some customers use rotation digits on the end of account numbers.
                         * EXAMPLE: if a customer changed at account 123456, the whole account number
                         * plus rotation digits might change like this: 12345610 to 12345620, so we want
                         * to only consider the accountnumber itself.  The number of digits to consider
                         * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                         */
                        liteAcctInfo = energyCompany.searchAccountByAccountNo( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
                        if (liteAcctInfo == null) {
                            if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                importLog.println( "WARNING at " + result.getPosition() + ": account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " doesn't exist, record ignored" );
                                continue;
                            }
                            
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Cannot import hardware, account #" + hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] + " doesn't exist]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                    
                    String[] appFields = null;
                    if (hwColIdx[result.COL_APP_TYPE] != -1) {
                        appFields = prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
                        setApplianceFields( appFields, columns, hwColIdx, result );
                        if (preScan && appFieldsList == null)
                            appFieldsList = new ArrayList<String[]>();
                        
                        if ((appFields[ImportManagerUtil.IDX_APP_TYPE].trim().length() > 0) &&
                            (!applianceNameList.contains(appFields[ImportManagerUtil.IDX_APP_TYPE]))) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Appliance Type was supplied, but doesn't exist]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                    
                    if (!preScan) {
                        LiteInventoryBase liteInv;
                        try {
                            liteInv = importHardware( hwFields, liteAcctInfo, energyCompany, result, userContext );                            

                            if (hwFields[ImportManagerUtil.IDX_PROGRAM_NAME].trim().length() > 0
                                    && !hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE"))
                            {
                                programSignUp( hwFields, appFields, liteAcctInfo, liteInv, energyCompany, result );
                            }
                        } catch (WebClientException e) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        } catch (SQLException sqle) {
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: " + sqle.getMessage() + "]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            importLog.println(sqle.getStackTrace());
                            continue;
                        }
                    }
                    else {
                        String acctNo = hwFieldsMap.get( hwFields[ImportManagerUtil.IDX_SERIAL_NO] );
                        if (acctNo == null) {
                            LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsSearchDao.searchLMHardwareBySerialNumber(hwFields[ImportManagerUtil.IDX_SERIAL_NO], energyCompany);
                            if (liteHw != null && liteHw.getAccountID() > 0)
                                acctNo = starsCustAccountInformationDao.getById(liteHw.getAccountID(), energyCompany.getEnergyCompanyId()).getCustomerAccount().getAccountNumber();
                        }
                        
                        if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                            if (acctNo == null || !acctNo.equalsIgnoreCase( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] )) {
                                importLog.println( "WARNING at " + result.getPosition() + ": serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account, record ignored" );
                                continue;
                            }
                            
                            if (preScan) {
                                hwFieldsList.add( hwFields );
                                hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], "" );
                            }
                        }
                        else {
                            if (acctNo != null && !acctNo.equals("") && !acctNo.equalsIgnoreCase( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] )) {
                                result.hwFileErrors++;
                                String[] value = result.getHwLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " already exists in another account]";
                                result.getHwLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }

                            if (acctNo != null && acctNo.equals( hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] ))
                                hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "UPDATE";
                            else
                                hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION] = "INSERT";
                            
                            if (preScan) {
                                hwFieldsList.add( hwFields );
                                hwFieldsMap.put( hwFields[ImportManagerUtil.IDX_SERIAL_NO], hwFields[ImportManagerUtil.IDX_ACCOUNT_ID] );
                            }
                        }
                        
                        if (appFieldsList != null) appFieldsList.add( appFields );
                    }
                    
                    if (result.isCanceled()) {
                        throw new Exception();
                    }
                }
                
                errors += result.hwFileErrors;
                
                // Closing the buffered reader closes all the i/o behind.
                // In this case the inputStream is closed b/c the reader is being closed.
                reader.close();
                archive.close();
                
                if(!preScan){
                    result.setNumHwTotal(hwFieldsList.size());
                    boolean isDeleted = hwFile.delete();
                    if (isDeleted == false) {
                        importLog.println("* "+hwFile.getName()+" was not deleted *");
                    }
                }
            }
            
            result.setComplete(true);
            
        } catch (Exception e) {
            result.setComplete(true);
            if (!result.isCanceled()) {
                CTILogger.error( e.getMessage(), e );
                String[] value = new String[3];
                
                if(importLog == null) {
                    try {
                        importLog = new PrintWriter(new FileWriter(logFile), true);
                        importLog.println("Error Occured");
                    } catch (IOException e1) {
                        CTILogger.error(e1);
                    }
                }

                // Could not open the file passing error writing.
                if (!result.getErrors().isEmpty()) {
                    importLog.println("Error Occured");
                    if (result.getCustLines() != null){
                        value = result.getCustLines().get(lineNo);
                        result.getCustLines().put(lineNo, value);
                    } else {
                        if(result.getHwLines() != null) {
                            value = result.getHwLines().get(lineNo);
                            result.getHwLines().put(lineNo, value);
                        }
                    }
                    
                    value[1] = "[line: "+lineNo+" error: "+e.getMessage()+"]";
                    addToLog(lineNo, value, importLog);
                }
            }

            ActivityLogger.logEvent( result.getCurrentUser().getUserID(), ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, errorMsg );
        }
        
        if (importLog != null) {
            importLog.println();
            importLog.println("Stop time: " + StarsUtils.formatDate( new Date(), result.getEnergyCompany().getDefaultTimeZone() ));
            importLog.println();
            if (errorMsg != null) {
                importLog.println( errorMsg );
            }
            importLog.close();

            try {
                if (!StringUtils.isBlank(email) && ((preScan && result.hasErrors()) || !preScan)) {
                    sendImportLog( logFile, email, result.getEnergyCompany() );
                }
            } catch (Exception e) {
                if (errorMsg != null)
                    log.error(errorMsg);
                
                    log.error("Failed to send the import log by email");
            }
        }
    }
    
    private LiteInventoryBase importHardware(String[] hwFields, LiteStarsCustAccountInformation liteAcctInfo, 
                                             LiteStarsEnergyCompany energyCompany, AccountImportResult result, 
                                             YukonUserContext userContext) throws WebClientException {
        LiteInventoryBase liteInv = null;

        for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
            int invID = liteAcctInfo.getInventories().get(i);
            LiteInventoryBase lInv = starsInventoryBaseDao.getByInventoryId(invID);
            if (lInv instanceof LiteStarsLMHardware && ((LiteStarsLMHardware)lInv).getManufacturerSerialNumber().equals(hwFields[ImportManagerUtil.IDX_SERIAL_NO])) {
                liteInv = lInv;
                break;
            }
        }
        
        try {
            
            if (hwFields[ImportManagerUtil.IDX_HARDWARE_ACTION].equalsIgnoreCase( "REMOVE" )) {
                hardwareEventLogService.hardwareRemovalAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                       hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
                
                if (liteInv == null)
                    throw new WebClientException("Cannot remove hardware, serial #" + hwFields[ImportManagerUtil.IDX_SERIAL_NO] + " not found in the customer account");
                
                // REMOVE HARDWARE
                StarsControllableDeviceDTO dto = starsControllableDeviceDTOConverter
                    .getDtoForHardware(liteAcctInfo.getCustomerAccount()
                            .getAccountNumber(), liteInv, energyCompany);
                
                if (!StringUtils.isBlank(hwFields[ImportManagerUtil.IDX_REMOVE_DATE])) {
                    Date removeDate = ServletUtil.parseDateStringLiberally(hwFields[ImportManagerUtil.IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone());
                    if (removeDate == null) {
                        removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportManagerUtil.IDX_REMOVE_DATE]);
                    }
                    dto.setFieldRemoveDate(removeDate);
                }
                
                starsControllableDeviceHelper.removeDeviceFromAccount(dto, result.getCurrentUser());
                
                result.getHardwareRemoved().add(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
            }
            else if (liteInv == null) {
                hardwareEventLogService.hardwareCreationAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                        hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
                
                // ADD HARDWARE
                StarsControllableDeviceDTO dto = starsControllableDeviceDTOConverter.createNewDto(liteAcctInfo.getCustomerAccount()
                                .getAccountNumber(), hwFields, energyCompany);
                liteInv = starsControllableDeviceHelper.addDeviceToAccount(dto, result.getCurrentUser());
                
                result.getHardwareAdded().add(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
            } else if (!result.isInsertSpecified()) {
                hardwareEventLogService.hardwareUpdateAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                      hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
                
                // UPDATE HARDWARE
                StarsControllableDeviceDTO dto = starsControllableDeviceDTOConverter
                        .getDtoForHardware(liteAcctInfo.getCustomerAccount()
                                .getAccountNumber(), liteInv, energyCompany);
                starsControllableDeviceDTOConverter.updateDtoWithHwFields(dto, hwFields, energyCompany);
                liteInv = starsControllableDeviceHelper.updateDeviceOnAccount(dto, result.getCurrentUser());
                
                result.getHardwareUpdated().add(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
            }
        } catch (StarsClientRequestException e) {
            automationCheck(e.getMessage(), result.isAutomatedImport());
        } catch (ParseException e) {
            automationCheck(e.getMessage(), result.isAutomatedImport());
        }
        
        result.setNumHwImported(result.getNumHwImported() + 1);
        return liteInv;
    }
    
    @SuppressWarnings("deprecation")
    private LiteStarsCustAccountInformation importAccount(String[] custFields, 
                                                          LiteStarsEnergyCompany energyCompany, 
                                                          AccountImportResult result,
                                                          YukonUserContext userContext) throws Exception {
        LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
        
        try {
            
            if (custFields[ImportManagerUtil.IDX_ACCOUNT_ACTION].equalsIgnoreCase( "REMOVE" )) {
                accountEventLogService.accountDeletionAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                      custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
                // deletion 
                if (liteAcctInfo == null) {
                    String errorStr = "Cannot delete customer account: account #" + custFields[ImportManagerUtil.IDX_ACCOUNT_NO] + " doesn't exist";
                    automationCheck(errorStr, result.isAutomatedImport());
                } else {
                    
                    // DELETE ACCOUNT
                    accountService.deleteAccount(liteAcctInfo.getCustomerAccount().getAccountNumber(), 
                                                 userContext.getYukonUser());
                    
                    result.getAccountsRemoved().add(custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
                    result.setNumAcctImported(result.getNumAcctImported() + 1);
                    
                    return null;
                }
            }
            
            if (liteAcctInfo == null) {
                accountEventLogService.accountCreationAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                      custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
                
                // Validates the IVR fields and throws a web client exception if they don't
                ServletUtils.formatPin(custFields[ImportManagerUtil.IDX_IVR_USERNAME]);
                ServletUtils.formatPin(custFields[ImportManagerUtil.IDX_IVR_PIN]);
                
                // ADD ACCOUNT
                UpdatableAccount updatableAccount = updatableAccountConverter.createNewUpdatableAccount(custFields, energyCompany);
                accountService.addAccount(updatableAccount, userContext.getYukonUser());
                liteAcctInfo = energyCompany.searchAccountByAccountNo( custFields[ImportManagerUtil.IDX_ACCOUNT_NO] );
                
                result.getAccountsAdded().add(custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
            } else if (!result.isInsertSpecified()) {
                accountEventLogService.accountUpdateAttemptedThroughAccountImporter(userContext.getYukonUser(),
                                                                                    custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
    
                // Validates the IVR fields and throws a web client exception if they don't
                ServletUtils.formatPin(custFields[ImportManagerUtil.IDX_IVR_USERNAME]);
                ServletUtils.formatPin(custFields[ImportManagerUtil.IDX_IVR_PIN]);
                
                // UPDATE ACCOUNT
                UpdatableAccount updatableAccount = updatableAccountConverter.getUpdatedUpdatableAccount(liteAcctInfo, custFields, energyCompany);
                accountService.updateAccount(updatableAccount, userContext.getYukonUser());
                
                result.getAccountsUpdated().add(custFields[ImportManagerUtil.IDX_ACCOUNT_NO]);
            }
    
        } catch (Exception e) {
            automationCheck(e.getMessage(), result.isAutomatedImport());
        }
        
        result.setNumAcctImported(result.getNumAcctImported() + 1);
        return liteAcctInfo;
    }
    
    private static void setCustomerFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        if (colIdx[result.COL_ACCOUNT_NO] >= 0 && colIdx[result.COL_ACCOUNT_NO] < columns.length)
            fields[ImportManagerUtil.IDX_ACCOUNT_NO] = columns[ colIdx[result.COL_ACCOUNT_NO] ];
        if (colIdx[result.COL_CUST_ACTION] >= 0 && colIdx[result.COL_CUST_ACTION] < columns.length)
            fields[ImportManagerUtil.IDX_ACCOUNT_ACTION] = columns[ colIdx[result.COL_CUST_ACTION] ];
        if (colIdx[result.COL_LAST_NAME] >= 0 && colIdx[result.COL_LAST_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_LAST_NAME] = columns[ colIdx[result.COL_LAST_NAME] ];
        if (colIdx[result.COL_FIRST_NAME] >= 0 && colIdx[result.COL_FIRST_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_FIRST_NAME] = columns[ colIdx[result.COL_FIRST_NAME] ];
        if (colIdx[result.COL_LAST_NAME] >= 0 && colIdx[result.COL_LAST_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_LAST_NAME] = columns[ colIdx[result.COL_LAST_NAME] ];
        if (colIdx[result.COL_HOME_PHONE] >= 0 && colIdx[result.COL_HOME_PHONE] < columns.length)
            fields[ImportManagerUtil.IDX_HOME_PHONE] = columns[ colIdx[result.COL_HOME_PHONE] ];
        if (colIdx[result.COL_WORK_PHONE] >= 0 && colIdx[result.COL_WORK_PHONE] < columns.length)
            fields[ImportManagerUtil.IDX_WORK_PHONE] = columns[ colIdx[result.COL_WORK_PHONE] ];
        if (colIdx[result.COL_EMAIL] >= 0 && colIdx[result.COL_EMAIL] < columns.length)
            fields[ImportManagerUtil.IDX_EMAIL] = columns[ colIdx[result.COL_EMAIL] ];
        if (colIdx[result.COL_STREET_ADDR1] >= 0 && colIdx[result.COL_STREET_ADDR1] < columns.length)
            fields[ImportManagerUtil.IDX_STREET_ADDR1] = columns[ colIdx[result.COL_STREET_ADDR1] ];
        if (colIdx[result.COL_STREET_ADDR2] >= 0 && colIdx[result.COL_STREET_ADDR2] < columns.length)
            fields[ImportManagerUtil.IDX_STREET_ADDR2] = columns[ colIdx[result.COL_STREET_ADDR2] ];
        if (colIdx[result.COL_CITY] >= 0 && colIdx[result.COL_CITY] < columns.length)
            fields[ImportManagerUtil.IDX_CITY] = columns[ colIdx[result.COL_CITY] ];
        if (colIdx[result.COL_STATE] >= 0 && colIdx[result.COL_STATE] < columns.length)
            fields[ImportManagerUtil.IDX_STATE] = columns[ colIdx[result.COL_STATE] ];
        if (colIdx[result.COL_COUNTY] >= 0 && colIdx[result.COL_COUNTY] < columns.length)
            fields[ImportManagerUtil.IDX_COUNTY] = columns[ colIdx[result.COL_COUNTY] ];
        if (colIdx[result.COL_ZIP_CODE] >= 0 && colIdx[result.COL_ZIP_CODE] < columns.length)
            fields[ImportManagerUtil.IDX_ZIP_CODE] = columns[ colIdx[result.COL_ZIP_CODE] ];
        if (colIdx[result.COL_MAP_NO] >= 0 && colIdx[result.COL_MAP_NO] < columns.length)
            fields[ImportManagerUtil.IDX_MAP_NO] = columns[ colIdx[result.COL_MAP_NO] ];
        if (colIdx[result.COL_SUBSTATION] >= 0 && colIdx[result.COL_SUBSTATION] < columns.length && columns[colIdx[result.COL_SUBSTATION]].trim().length() > 0)
            fields[ImportManagerUtil.IDX_SUBSTATION] = columns[ colIdx[result.COL_SUBSTATION] ];
        if (colIdx[result.COL_FEEDER] >= 0 && colIdx[result.COL_FEEDER] < columns.length)
            fields[ImportManagerUtil.IDX_FEEDER] = columns[ colIdx[result.COL_FEEDER] ];
        if (colIdx[result.COL_POLE] >= 0 && colIdx[result.COL_POLE] < columns.length)
            fields[ImportManagerUtil.IDX_POLE] = columns[ colIdx[result.COL_POLE] ];
        if (colIdx[result.COL_TRFM_SIZE] >= 0 && colIdx[result.COL_TRFM_SIZE] < columns.length)
            fields[ImportManagerUtil.IDX_TRFM_SIZE] = columns[ colIdx[result.COL_TRFM_SIZE] ];
        if (colIdx[result.COL_SERV_VOLT] >= 0 && colIdx[result.COL_SERV_VOLT] < columns.length)
            fields[ImportManagerUtil.IDX_SERV_VOLT] = columns[ colIdx[result.COL_SERV_VOLT] ];
        if (colIdx[result.COL_USERNAME] >= 0 && colIdx[result.COL_USERNAME] < columns.length)
            fields[ImportManagerUtil.IDX_USERNAME] = columns[ colIdx[result.COL_USERNAME] ];
        if (colIdx[result.COL_PASSWORD] >= 0 && colIdx[result.COL_PASSWORD] < columns.length)
            fields[ImportManagerUtil.IDX_PASSWORD] = columns[ colIdx[result.COL_PASSWORD] ];
        if (colIdx[result.COL_LOGIN_GROUP] >= 0 && colIdx[result.COL_LOGIN_GROUP] < columns.length)
            fields[ImportManagerUtil.IDX_LOGIN_GROUP] = columns[ colIdx[result.COL_LOGIN_GROUP] ];
        if (colIdx[result.COL_COMPANY_NAME] >= 0 && colIdx[result.COL_COMPANY_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_COMPANY_NAME] = columns[ colIdx[result.COL_COMPANY_NAME] ];
        if (colIdx[result.COL_IVR_PIN] >= 0 && colIdx[result.COL_IVR_PIN] < columns.length)
            fields[ImportManagerUtil.IDX_IVR_PIN] = columns[ colIdx[result.COL_IVR_PIN] ];
        if (colIdx[result.COL_IVR_USERNAME] >= 0 && colIdx[result.COL_IVR_USERNAME] < columns.length)
            fields[ImportManagerUtil.IDX_IVR_USERNAME] = columns[ colIdx[result.COL_IVR_USERNAME] ];
    }
    
    private static void setHardwareFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        if (colIdx[result.COL_HW_ACCOUNT_NO] >= 0 && colIdx[result.COL_HW_ACCOUNT_NO] < columns.length)
            fields[ImportManagerUtil.IDX_ACCOUNT_ID] = columns[ colIdx[result.COL_HW_ACCOUNT_NO] ];    // Use account ID field to store account #
        if (colIdx[result.COL_HW_ACTION] >= 0 && colIdx[result.COL_HW_ACTION] < columns.length)
            fields[ImportManagerUtil.IDX_HARDWARE_ACTION] = columns[ colIdx[result.COL_HW_ACTION] ];
        if (colIdx[result.COL_SERIAL_NO] >= 0 && colIdx[result.COL_SERIAL_NO] < columns.length)
            fields[ImportManagerUtil.IDX_SERIAL_NO] = columns[ colIdx[result.COL_SERIAL_NO] ];
        if (colIdx[result.COL_DEVICE_TYPE] >= 0 && colIdx[result.COL_DEVICE_TYPE] < columns.length)
            fields[ImportManagerUtil.IDX_DEVICE_TYPE] = columns[ colIdx[result.COL_DEVICE_TYPE] ];
        if (colIdx[result.COL_INSTALL_DATE] >= 0 && colIdx[result.COL_INSTALL_DATE] < columns.length)
            fields[ImportManagerUtil.IDX_INSTALL_DATE] = columns[ colIdx[result.COL_INSTALL_DATE] ];
        if (colIdx[result.COL_REMOVE_DATE] >= 0 && colIdx[result.COL_REMOVE_DATE] < columns.length)
            fields[ImportManagerUtil.IDX_REMOVE_DATE] = columns[ colIdx[result.COL_REMOVE_DATE] ];
        if (colIdx[result.COL_SERVICE_COMPANY] >= 0 && colIdx[result.COL_SERVICE_COMPANY] < columns.length && !columns[colIdx[result.COL_SERVICE_COMPANY]].trim().equals(""))
            fields[ImportManagerUtil.IDX_SERVICE_COMPANY] = columns[ colIdx[result.COL_SERVICE_COMPANY] ];
        if (colIdx[result.COL_PROGRAM_NAME] >= 0 && colIdx[result.COL_PROGRAM_NAME] < columns.length)
            fields[ImportManagerUtil.IDX_PROGRAM_NAME] = columns[ colIdx[result.COL_PROGRAM_NAME] ];
        if (colIdx[result.COL_ADDR_GROUP] >= 0 && colIdx[result.COL_ADDR_GROUP] < columns.length)
            fields[ImportManagerUtil.IDX_ADDR_GROUP] = columns[ colIdx[result.COL_ADDR_GROUP] ];
        if (colIdx[result.COL_OPTION_PARAMS] >= 0 && colIdx[result.COL_OPTION_PARAMS] < columns.length)
            fields[ImportManagerUtil.IDX_OPTION_PARAMS] = columns[ colIdx[result.COL_OPTION_PARAMS] ];
        if (colIdx[result.COL_DEVICE_LABEL] >= 0 && colIdx[result.COL_DEVICE_LABEL] < columns.length)
            fields[ImportManagerUtil.IDX_DEVICE_LABEL] = columns[ colIdx[result.COL_DEVICE_LABEL] ];
    }
    
    private static void setApplianceFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        if (colIdx[result.COL_APP_TYPE] >= 0 && colIdx[result.COL_APP_TYPE] < columns.length)
            fields[ImportManagerUtil.IDX_APP_TYPE] = columns[ colIdx[result.COL_APP_TYPE] ];
        if (colIdx[result.COL_APP_KW] >= 0 && colIdx[result.COL_APP_KW] < columns.length)
            fields[ImportManagerUtil.IDX_APP_KW] = columns[ colIdx[result.COL_APP_KW] ];
        if (colIdx[result.COL_APP_RELAY_NUMBER] >= 0 && colIdx[result.COL_APP_RELAY_NUMBER] < columns.length)
            fields[ImportManagerUtil.IDX_RELAY_NUM] = columns[ colIdx[result.COL_APP_RELAY_NUMBER] ];
    }
    
    @SuppressWarnings("deprecation")
    private static File getBaseDir(LiteStarsEnergyCompany energyCompany){
        final String fs = System.getProperty( "file.separator" );
        String ecName = energyCompany.getName();
        if(ecName.indexOf('<') > -1) {
            ecName = "EnergyCompany" + energyCompany.getEnergyCompanyId();
        }
        
        // Check to see if it exist in role property if not use default.
        String baseDirRoleValue = DaoFactory.getRoleDao().getGlobalPropertyValue(ConfigurationRole.CUSTOMER_INFO_IMPORTER_FILE_LOCATION);

        // Gets base Directory
        File baseDir = null;
        if (baseDirRoleValue.trim().length() > 0) {
            baseDir = new File(baseDirRoleValue);
        } else {
            baseDir = new File(ServerUtils.getStarsTempDir() + fs + ecName);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
        }
        
        return baseDir;
    }
    
    private static void addToLog(Integer lineNoKey, String[] value, PrintWriter importLog) {
        importLog.println("Error found -Line "+lineNoKey+"= "+value[0]+"- Error="+value[1]);
    }
    
    private void automationCheck(String errorStr, boolean isAutomatedImport) throws WebClientException{
        if(isAutomatedImport){
            importLog.println(errorStr);
        } else {
            throw new WebClientException(errorStr);
        }
    }
    
    private static String[] prepareFields(int numFields) {
        String[] fields = new String[ numFields ];
        for (int i = 0; i < numFields; i++)
            fields[i] = "";
        
        return fields;
    }
    
    private void programSignUp(String[] hwFields, String[] appFields, LiteStarsCustAccountInformation liteAcctInfo, 
                               LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany, AccountImportResult result) throws Exception {
       if(appFields == null) {
           appFields = prepareFields( ImportManagerUtil.NUM_APP_FIELDS );
       }
       
       try {
           EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
           EnrollmentEnum enrollType = EnrollmentEnum.ENROLL;
           enrollmentHelper.setAccountNumber(liteAcctInfo.getCustomerAccount().getAccountNumber());
           enrollmentHelper.setSerialNumber(hwFields[ImportManagerUtil.IDX_SERIAL_NO]);
           if(result.UNENROLL_CASE.equalsIgnoreCase( hwFields[ImportManagerUtil.IDX_PROGRAM_NAME] )) {
               enrollType = EnrollmentEnum.UNENROLL;
               // leave programName blank, to unenroll from all programs for the Inventory
           } else {
               enrollmentHelper.setProgramName(hwFields[ImportManagerUtil.IDX_PROGRAM_NAME]);    
           }
           enrollmentHelper.setLoadGroupName(hwFields[ImportManagerUtil.IDX_ADDR_GROUP]);
           enrollmentHelper.setRelay(appFields[ImportManagerUtil.IDX_RELAY_NUM]);        
           enrollmentHelper.setApplianceCategoryName(appFields[ImportManagerUtil.IDX_APP_TYPE]);
           if (!StringUtils.isBlank(appFields[ImportManagerUtil.IDX_APP_KW])) {
               try {
                   Double appKw = Double.parseDouble(appFields[ImportManagerUtil.IDX_APP_KW]);
                   if (appKw < 0) throw new IllegalArgumentException("Appliance KW should be a valid numeric value");
                   enrollmentHelper.setApplianceKW(appKw.floatValue());                
               } catch(NumberFormatException e) {
                   throw new IllegalArgumentException("Appliance KW should be a valid numeric value");
               }
           }
           // call enrollment service now
           enrollmentHelperService.doEnrollment(enrollmentHelper, enrollType, result.getCurrentUser());
       } catch(NotFoundException e) {
           automationCheck(e.getMessage(), result.isAutomatedImport());
       } catch(IllegalArgumentException e) {
           automationCheck(e.getMessage(), result.isAutomatedImport());
       } catch(DuplicateEnrollmentException e) {
           automationCheck(e.getMessage(), result.isAutomatedImport());
       }
       
    }

    private void sendImportLog(File importLog, String email, LiteStarsEnergyCompany energyCompany) throws Exception {
        EmailMessage emailMsg = new EmailMessage();
        emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
        emailMsg.setTo( email );
        emailMsg.setSubject( "Import Log" );
        emailMsg.setBody( "The log file containing information of the import process is attached." + LINE_SEPARATOR + LINE_SEPARATOR );
        emailMsg.addAttachment( importLog, null );
        
        emailMsg.send();
    }

    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setStarsSearchDao(StarsSearchDao starsSearchDao) {
        this.starsSearchDao = starsSearchDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Resource(name="longRunningExecutor")
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @Autowired
    public void setUpdatableAccountConverter(UpdatableAccountConverter updatableAccountConverter) {
        this.updatableAccountConverter = updatableAccountConverter;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
    
    @Autowired
    public void setStarsControllableDeviceDTOConverter(StarsControllableDeviceDTOConverter starsControllableDeviceDTOConverter) {
        this.starsControllableDeviceDTOConverter = starsControllableDeviceDTOConverter;
    }
    
    @Autowired
    public void setStarsControllableDeviceHelper(StarsControllableDeviceHelper starsControllableDeviceHelper) {
        this.starsControllableDeviceHelper = starsControllableDeviceHelper;
    }
    
    @Autowired
    public void setEnrollmentHelperService(EnrollmentHelperService enrollmentHelperService) {
        this.enrollmentHelperService = enrollmentHelperService;
    }
}