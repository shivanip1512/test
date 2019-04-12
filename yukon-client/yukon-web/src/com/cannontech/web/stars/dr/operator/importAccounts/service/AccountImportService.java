package com.cannontech.web.stars.dr.operator.importAccounts.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.DuplicateEnrollmentException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.scheduledFileImport.DataImportWarning;
import com.cannontech.common.scheduledFileImport.ScheduledImportType;
import com.cannontech.common.smartNotification.model.DataImportAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.service.SmartNotificationEventCreationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.service.LmDeviceDtoConverter;
import com.cannontech.stars.service.UpdatableAccountConverter;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.web.util.ImportFields;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailAttachmentMessage;
import com.cannontech.tools.email.EmailFileDataSource;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.dataImport.DataImportHelper;
import com.cannontech.web.scheduledDataImport.ScheduledDataImportException;
import com.cannontech.web.stars.dr.operator.AccountImportFields;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AccountImportService {
    
    @Autowired private AccountEventLogService accountLog;
    @Autowired private AccountService accountService;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private ContactDao contactDao;
    @Autowired private EmailService emailService;
    @Autowired private EnergyCompanyService ecService;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private HardwareEventLogService hardwareLog;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmDeviceDtoConverter dtoConverter;
    @Autowired private SelectionListService selectionListService;
    @Autowired private StarsControllableDeviceHelper deviceHelper;
    @Autowired private StarsCustAccountInformationDao scaiDao; 
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsSearchService starsSearchService;
    @Autowired private UpdatableAccountConverter accountConverter;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired @Qualifier("longRunning") private Executor executor;
    @Autowired private SmartNotificationEventCreationService smartNotificationEventCreationService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final Logger log = YukonLogManager.getLogger(AccountImportService.class);
    private PrintWriter importLog;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public void startAccountImport(final AccountImportResult result, final YukonUserContext context) {
        executor.execute(() -> {
            Instant startTime = Instant.now();
            result.setStartTime(startTime);
            processAccountImport(result, context, startTime.toDate());
        });
    }

    /**
     * create File directory based on baseDir and filename
     */
    private File setupFileDir(File baseDir, String filename) {
        final String fs = System.getProperty("file.separator");
        File fileDir = new File(baseDir, fs + filename);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;

    }

    /**
     * setup archive file directory for hardware and customer files.
     */

    private void setUpArchiveFile(Date startTime, File archiveDir, AccountImportResult result) {

        String archiveCustFilename = getFilename(startTime, result.getCustomerFile(), ".csv");
        if (archiveCustFilename != null) {
            File archiveCustFile = new File(archiveDir, archiveCustFilename);
            result.setCustArchiveDir(archiveCustFile);
        }

        String archiveHardFilename = getFilename(startTime, result.getHardwareFile(), ".csv");
        if (archiveHardFilename != null) {
            File archiveHardFile = new File(archiveDir, archiveHardFilename);
            result.setHwArchiveDir(archiveHardFile);
        }
    }

    /**
     * setup log directory for hardware and customer files.
     */

    private void setUpLogFile(Date startTime, File importFailsDir, AccountImportResult result) {

        boolean preScan = result.isPrescan();
        String logFileName = null;
        String archiveCustFilename = getFilename(startTime, result.getCustomerFile(), ".log");

        if (archiveCustFilename != null) {
            if (preScan) {
                logFileName = "Prescan-" + archiveCustFilename;
            } else {
                logFileName = archiveCustFilename;
            }
        }

        String archiveHardFilename = getFilename(startTime, result.getHardwareFile(), ".log");
        if (archiveHardFilename != null) {
            if (preScan) {
                logFileName = "Prescan-" + archiveHardFilename;
            } else {
                logFileName = archiveHardFilename;
            }
        }

        result.setOutputLogDir(new File(importFailsDir, logFileName));
    }

    /**
     * create file name (tempFile_YYYYMMDD_HHMM) based on start time and fileToProcess.
     */
    private String getFilename(Date startTime, File fileToProcess, String ext) {

        if (fileToProcess != null && fileToProcess.length() > 4) {
            String fileName = fileToProcess.getName();
            //Removed .tmp extension from tempFile
            String archiveFileName =
                fileName.substring(0, fileName.length() - 4) + "-" + StarsUtils.starsDateFormat.format(startTime) + "_"
                    + StarsUtils.starsTimeFormat.format(startTime) + ext;
            return archiveFileName;
        }
        return null;
    }

    /**
     * Create File Directory for archive, failedImport and log file
     */
    private void createFileDirectory(AccountImportResult result, LiteYukonUser user, Date startTime) throws Exception {

        YukonEnergyCompany energyCompany = result.getEnergyCompany();
        File baseDir = getBaseDir(energyCompany, user);

        // Gets the archive directory found inside the default directory
        File archiveDir = setupFileDir(baseDir, "archive");
        // Gets the upload directory found inside the default directory
        File importFailsDir = setupFileDir(archiveDir, "failedImport");

        setUpArchiveFile(startTime, archiveDir, result);
        setUpLogFile(startTime, importFailsDir, result);

    }

    @SuppressWarnings("deprecation")
    public void processAccountImport(AccountImportResult result, YukonUserContext context, Date startTime) {
        boolean preScan = result.isPrescan();
        final File custFile = result.getCustomerFile();
        final File hwFile = result.getHardwareFile();
        
        List<String[]> custFieldsList = null;
        List<String[]> hwFieldsList = null;
        List<String[]> appFieldsList = null;
        int lineNo = 0;
        
        int[] custColIdx = new int[result.CUST_COLUMNS.length];
        int[] hwColIdx = new int [result.HW_COLUMNS.length];
        int numCustCol = result.CUST_COLUMNS.length;
        int numHwCol = result.HW_COLUMNS.length;
        
        for (int i = 0; i < result.CUST_COLUMNS.length; i++) {
            custColIdx[i] = -1;
        }
        for (int i = 0; i < result.HW_COLUMNS.length; i++) {
            hwColIdx[i] = -1;
        }
        
        // Pre-scan the import file(s). If customer and hardware information is in the same file,
        // no optimization will be preformed.
        
        // Map from account # (String) to customer fields (String[])
        Map<String, String[]> custFieldsMap = Maps.newHashMap();
        
        // Map from serial # (String) to account # (String)
        Map<String, String> hwFieldsMap = Maps.newHashMap();


        try {
            YukonEnergyCompany energyCompany = result.getEnergyCompany();
            if (!result.isScheduled()) {
                createFileDirectory(result, context.getYukonUser(), startTime);
            }
            importLog = new PrintWriter(new FileWriter(result.getOutputLogDir()), true);
            
            importLog.println("Start time: " 
                    + StarsUtils.formatDate(startTime, ecService.getDefaultTimeZone(energyCompany.getEnergyCompanyId())));
            importLog.println();
            
            // Creates a list of all the appliance categories names
            Iterable<ApplianceCategory> allApplianceCategories = applianceCategoryDao.getApplianceCategoriesByEcId(result.getEnergyCompany().getEnergyCompanyId());
            List<String> applianceNameList = Lists.newArrayList(); 
            
            for (ApplianceCategory applianceCategory : allApplianceCategories) {
                applianceNameList.add(applianceCategory.getName());
            }
            
            boolean automatedImport = result.isAutomatedImport();
            if (custFile != null) {
                custFieldsList = Lists.newArrayList(); 
                
                List<String> addedUsernames = Lists.newArrayList(); 
                List<String> removedUsernames = Lists.newArrayList(); 
                
                result.setCustLines(new TreeMap<Integer, String[]>());
                boolean hwInfoContained = false;

                String line = null;
                lineNo = 0;
                Integer lineNoKey;
                
                // Sets up the archive file
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(custFile)));
                        PrintWriter archive = new PrintWriter(new FileWriter(result.getCustArchiveDir()), true);) {

                while ((line = reader.readLine()) != null) {
                    // This line adds the latest line to the archive file
                    archive.println(line);
                    lineNo++;
                    lineNoKey = lineNo;
                    
                    if (line.trim().length() == 0 || line.charAt(0) == '#') {
                        continue;
                    }

                    result.getCustLines().put(lineNo, new String[]{line, null});
                    
                    result.setPosition("customer file line #" + lineNo);
                    
                    if (lineNo == 1) {
                        if (line.startsWith(result.COL_NAME_LABEL) || line.startsWith(result.CUST_COLUMNS[0])) {
                            String[] labels;
                            if (line.startsWith(result.COL_NAME_LABEL)) {
                                labels = StarsUtils.splitString(line.substring(result.COL_NAME_LABEL.length()), ",");
                            } else {
                                labels = StarsUtils.splitString(line, ",");
                            }
                            for(int i = 0; i < labels.length; i++){
                                /*LOGIN_GROUP was renamed to USER_GROUP. LOGIN_GROUP is accepted as an import field to support someone
                                who already has a template file structure defined.*/
                                if(labels[i].equalsIgnoreCase("Login_Group")){
                                    labels[i] = AccountImportFields.USER_GROUP.name();
                                }
                            }
                            
                            numCustCol = labels.length;
                            
                            for (int i = 0; i < labels.length; i++) {
                                for (int j = 0; j < result.CUST_COLUMNS.length; j++) {
                                    if (labels[i].equalsIgnoreCase(result.CUST_COLUMNS[j])) {
                                        custColIdx[j] = i;
                                        break;
                                    }
                                }
                                
                                if (hwFile == null) {
                                    // There could be only one file which also contains hardware information
                                    for (int j = 0; j < result.HW_COLUMNS.length; j++) {
                                        if (labels[i].equalsIgnoreCase(result.HW_COLUMNS[j])) {
                                            hwColIdx[j] = i;
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            if (custColIdx[result.COL_ACCOUNT_NO] == -1) {
                                String errorStr = "The required column \'\" + CUST_COLUMNS[COL_ACCOUNT_NO] + \"\' is missing";
                                automationCheck(errorStr, automatedImport);
                            }
                            
                            hwInfoContained = (hwColIdx[result.COL_DEVICE_TYPE] != -1) || (hwColIdx[result.COL_SERIAL_NO] != -1);

                            if (hwInfoContained) {
                                if (hwColIdx[result.COL_DEVICE_TYPE] == -1) {
                                    String errorStr = "The required column \'\" + HW_COLUMNS[COL_DEVICE_TYPE] + \"\' is missing";
                                    automationCheck(errorStr, automatedImport);
                                }
                                
                                if (hwColIdx[result.COL_SERIAL_NO] == -1) {
                                    String errorStr = "The required column \'\" + HW_COLUMNS[COL_SERIAL_NO] + \"\' is missing";
                                    automationCheck(errorStr, automatedImport);
                                }
                            }
                            
                            continue;
                        }
                        
                        for (int i = 0; i < result.CUST_COLUMNS.length; i++) {
                            custColIdx[i] = i;
                        }
                    }
                    
                    String[] columns = StarsUtils.splitString(line, ",");
                    if (columns.length > numCustCol) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    String[] custFields = prepareFields(ImportFields.NUM_ACCOUNT_FIELDS);
                    custFields[ImportFields.IDX_LINE_NUM] = String.valueOf(lineNo);
                    setCustomerFields(custFields, columns, custColIdx, result);

                    if (custFields[ImportFields.IDX_ACCOUNT_NO].trim().length() == 0) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    String action = custFields[ImportFields.IDX_ACCOUNT_ACTION];
                    if (!(StringUtils.isBlank(action) || action.equalsIgnoreCase("INSERT") || action.equalsIgnoreCase("UPDATE") || action.equalsIgnoreCase("REMOVE"))) {
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: " + action + " is not a valid action]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    result.setInsertSpecified(custFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT"));
                    /*
                     * Some customers use rotation digits on the end of account numbers.
                     * EXAMPLE: if a customer changed at account 123456, the whole account number
                     * plus rotation digits might change like this: 12345610 to 12345620, so we want
                     * to only consider the account number itself.  The number of digits to consider
                     * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                     */
                    LiteAccountInfo liteAcctInfo = 
                            starsSearchService.searchAccountByAccountNo(energyCompany, custFields[ImportFields.IDX_ACCOUNT_NO]);
                    
                    // Nothing is done to existing Accounts when CUST_ACTION is set to INSERT.
                    if ((liteAcctInfo != null) && result.isInsertSpecified()) {
                        
                        result.custFileErrors++;
                        String[] value = result.getCustLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: INSERT account action, but account already exists.]";
                        result.getCustLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    if (preScan) {
                        if (custFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                            // The current record is a "REMOVE"
                            String[] prevFields = custFieldsMap.get(custFields[ImportFields.IDX_ACCOUNT_NO]);
                            if (prevFields != null) {
                                if (prevFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                                    // If a "REMOVE" record already exists in the list, do nothing but report a warning
                                    importLog.println("WARNING at " + result.getPosition() + ": account #" + custFields[ImportFields.IDX_ACCOUNT_NO] + " already removed earlier, record ignored");
                                    continue;
                                }
                                
                                // Found a "INSERT" or "UPDATE" record in the list, remove the old record,
                                // and add the new "REMOVE" record if necessary 
                                if (!hwInfoContained) {
                                    custFieldsList.remove(prevFields);
                                }
                                if (hwInfoContained || !prevFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("INSERT")) {
                                    custFieldsList.add(custFields);
                                }
                                custFieldsMap.put(custFields[ImportFields.IDX_ACCOUNT_NO], custFields);

                                if (liteAcctInfo != null) {
                                    LiteYukonUser login = contactDao.getYukonUser(liteAcctInfo.getCustomer().getPrimaryContactID());
                                    if (login != null && login.getUserID() != UserUtils.USER_NONE_ID && !removedUsernames.contains(login.getUsername())) {
                                        removedUsernames.add(login.getUsername());
                                    }
                                } else if (prevFields[ImportFields.IDX_USERNAME].trim().length() > 0) {
                                    addedUsernames.remove(prevFields[ImportFields.IDX_USERNAME]);
                                }
                            } else {
                                if (liteAcctInfo != null) {
                                    // Found the account in the database, add the "REMOVE" record to the list
                                    custFieldsList.add(custFields);
                                    custFieldsMap.put(custFields[ImportFields.IDX_ACCOUNT_NO], custFields);
                                    
                                    LiteYukonUser login = contactDao.getYukonUser(liteAcctInfo.getCustomer().getPrimaryContactID());
                                    if (login != null && login.getUserID() != UserUtils.USER_NONE_ID && !removedUsernames.contains(login.getUsername())) {
                                        removedUsernames.add(login.getUsername());
                                    }
                                } else {
                                    // Account not found in the database, do nothing but report a warning
                                    importLog.println("WARNING at " + result.getPosition() + ": account #" + custFields[ImportFields.IDX_ACCOUNT_NO] + " doesn't exist, record ignored");
                                    continue;
                                }
                            }
                        } else {
                            // The current record is a "INSERT" or "UPDATE"
                            boolean checkUsername = false;
                            
                            String[] prevFields = custFieldsMap.get(custFields[ImportFields.IDX_ACCOUNT_NO]);
                            if (prevFields != null) {
                                if (prevFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                                    // If a "REMOVE" record already exists in the list, add the new record with action set to "INSERT"
                                    custFields[ImportFields.IDX_ACCOUNT_ACTION] = "INSERT";
                                    custFieldsList.add(custFields);
                                    custFieldsMap.put(custFields[ImportFields.IDX_ACCOUNT_NO], custFields);
                                    
                                    checkUsername = custFields[ImportFields.IDX_USERNAME].trim().length() > 0;
                                } else {
                                    // If a "INSERT" or "UPDATE" record already exists in the list, replace the old record
                                    // with the new record except the action field, also keep the old user login fields if provided
                                    if (prevFields[ImportFields.IDX_USERNAME].trim().length() > 0) {
                                        custFields[ImportFields.IDX_USERNAME] = prevFields[ImportFields.IDX_USERNAME];
                                        custFields[ImportFields.IDX_PASSWORD] = prevFields[ImportFields.IDX_PASSWORD];
                                    }
                                    
                                    if (!hwInfoContained) {
                                        custFields[ImportFields.IDX_ACCOUNT_ACTION] = prevFields[ImportFields.IDX_ACCOUNT_ACTION];
                                        custFieldsList.remove(prevFields);
                                    } else {
                                        custFields[ImportFields.IDX_ACCOUNT_ACTION] = "UPDATE";
                                    }
                                    
                                    custFieldsList.add(custFields);
                                    custFieldsMap.put(custFields[ImportFields.IDX_ACCOUNT_NO], custFields);
                                    
                                    checkUsername = prevFields[ImportFields.IDX_USERNAME].trim().length() == 0
                                            && custFields[ImportFields.IDX_USERNAME].trim().length() > 0;
                                }
                            } else {
                                // Add the new record to the list with the action field set accordingly
                                if (liteAcctInfo != null) {
                                    custFields[ImportFields.IDX_ACCOUNT_ACTION] = "UPDATE";
                                    
                                    LiteYukonUser login = contactDao.getYukonUser(liteAcctInfo.getCustomer().getPrimaryContactID());
                                    if (login != null && login.getUserID() != UserUtils.USER_NONE_ID) {
                                        custFields[ImportFields.IDX_USERNAME] = login.getUsername();
                                    } else {
                                        checkUsername = custFields[ImportFields.IDX_USERNAME].trim().length() > 0;
                                    }
                                } else {
                                    custFields[ImportFields.IDX_ACCOUNT_ACTION] = "INSERT";
                                    checkUsername = custFields[ImportFields.IDX_USERNAME].trim().length() > 0;
                                }
                                
                                custFieldsList.add(custFields);
                                custFieldsMap.put(custFields[ImportFields.IDX_ACCOUNT_NO], custFields);
                            }
                            
                            // Check if the username already exists
                            if (checkUsername) {
                                if (custFields[ImportFields.IDX_PASSWORD].length() == 0) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Password cannot be empty]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }   

                                String username = custFields[ImportFields.IDX_USERNAME];
                                if (addedUsernames.contains(username)) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Username would have already been added by the import program]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }

                                if (removedUsernames.contains(username)) {
                                    removedUsernames.remove(username);
                                } else if (yukonUserDao.findUserByUsername(username) != null) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Username already exists]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }
                                addedUsernames.add(username);
                            }
                        }
                    }

                    String[] hwFields = prepareFields(ImportFields.NUM_INV_FIELDS);
                    String[] appFields = prepareFields(ImportFields.NUM_APP_FIELDS);

                    if (hwInfoContained) {
                        if (preScan) {
                            if (hwFieldsList == null) {
                                hwFieldsList = new ArrayList<String[]>();
                            }
                            if (hwColIdx[result.COL_APP_TYPE] != -1 && appFieldsList == null) {
                                appFieldsList = new ArrayList<String[]>();
                            }
                        }
                        hwFields[ImportFields.IDX_LINE_NUM] = String.valueOf(lineNo);
                        setHardwareFields(hwFields, columns, hwColIdx, result);
                        
                        if (hwFields[ImportFields.IDX_SERIAL_NO].trim().length() == 0) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Serial number cannot be empty]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                        
                        if (custFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")
                            || hwFields[ImportFields.IDX_SERIAL_NO].trim().length() == 0) {
                            // If customer action is "REMOVE", or serial # field is empty, this record is for customer action only
                            if (preScan) {
                                hwFieldsList.add(null);
                                if (appFieldsList != null) {
                                    appFieldsList.add(null);
                                }
                            }
                            continue;
                        }
                        
                        if (hwFields[ImportFields.IDX_DEVICE_TYPE].trim().length() == 0) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }

                        result.setInsertSpecified(hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("INSERT"));
                        
                        YukonSelectionList devTypeList = selectionListService.getSelectionList(energyCompany, 
                                                       YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
                        YukonListEntry deviceType = yukonListDao.getYukonListEntry(devTypeList, hwFields[ImportFields.IDX_DEVICE_TYPE]);
                        
                        if (deviceType == null) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }


                        if(isZigbeeDevice(deviceType, result, lineNoKey, hwFields,false)) {
                            continue;
                        }
                        
                        if (isNestDevice(deviceType, result, lineNoKey, hwFields, false)) {
                            continue;
                        }

                        if (HardwareType.valueOf(deviceType.getYukonDefID()).isHoneywell()) {

                            if (hwFields[ImportFields.IDX_MAC_ADDRESS].trim().length() == 0) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] =
                                    "[line: " + lineNo + " error: MAC Address cannot be empty for Honeywell Device]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            } else if (hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID].trim().length() == 0) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: Device Vendor UserId cannot be empty for Honeywell Device]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }
                        }

                        if (hwColIdx[result.COL_APP_TYPE] != -1) {
                            setApplianceFields(appFields, columns, hwColIdx, result);
                            
                            if ((appFields[ImportFields.IDX_APP_TYPE].trim().length() > 0) &&
                                (!applianceNameList.contains(appFields[ImportFields.IDX_APP_TYPE]))) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: Appliance Type was supplied, but doesn't exist]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }
                        }
                        
                        if (preScan) {
                            String acctNo = hwFieldsMap.get(hwFields[ImportFields.IDX_SERIAL_NO]);
                            if (acctNo == null) {
                                LiteLmHardwareBase liteHw = starsSearchDao.searchLmHardwareBySerialNumber(hwFields[ImportFields.IDX_SERIAL_NO], energyCompany);
                                if (liteHw != null && liteHw.getAccountID() > 0) {
                                    acctNo = scaiDao.getById(liteHw.getAccountID(), energyCompany.getEnergyCompanyId()).getCustomerAccount().getAccountNumber();
                                }
                            }
                            
                            if (hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                // Remove a hardware from an account, if hardware doesn't exist in the account, report a warning
                                if (acctNo == null || !acctNo.equalsIgnoreCase(custFields[ImportFields.IDX_ACCOUNT_NO])) {
                                    importLog.println("WARNING at " + result.getPosition() + ": serial #" + hwFields[ImportFields.IDX_SERIAL_NO] + " not found in the customer account");
                                    hwFieldsList.add(null);
                                    if (appFieldsList != null) {
                                        appFieldsList.add(null);
                                    }
                                    continue;
                                }

                                hwFieldsList.add(hwFields);
                                hwFieldsMap.put(hwFields[ImportFields.IDX_SERIAL_NO], "");

                            } else {
                                // Insert/update a hardware in an account, if hardware already exists in another account, report an error 
                                if (acctNo != null && !acctNo.equals("") && !acctNo.equalsIgnoreCase(custFields[ImportFields.IDX_ACCOUNT_NO])) {
                                    result.custFileErrors++;
                                    String[] value = result.getCustLines().get(lineNoKey);
                                    value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportFields.IDX_SERIAL_NO] + " already exists in account #" + acctNo + "]";
                                    result.getCustLines().put(lineNoKey, value);
                                    addToLog(lineNoKey, value, importLog);
                                    continue;
                                }

                                if (acctNo != null && acctNo.equals(custFields[ImportFields.IDX_ACCOUNT_NO])) {
                                    hwFields[ImportFields.IDX_HARDWARE_ACTION] = "UPDATE";
                                } else {
                                    hwFields[ImportFields.IDX_HARDWARE_ACTION] = "INSERT";
                                }

                                hwFieldsList.add(hwFields);
                                hwFieldsMap.put(hwFields[ImportFields.IDX_SERIAL_NO], custFields[ImportFields.IDX_ACCOUNT_NO]);
                            }
                            
                            if (appFieldsList != null) {
                                appFieldsList.add(appFields);
                            }
                        }
                    }

                    if (!preScan || result.isScheduled()) {
                        try {
                            liteAcctInfo = importAccount(custFields, result, context.getYukonUser());
                        } catch (Exception ex) {
                            result.custFileErrors++;
                            String[] value = result.getCustLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: " + ex.getMessage() + "]";
                            result.getCustLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }

                    if (hwInfoContained) {
                        if (!preScan || result.isScheduled()) {
                            try {
                                // IMPORT HARDWARE
                                LiteInventoryBase liteInv = importHardware(hwFields, liteAcctInfo, result, context.getYukonUser());

                                if (hwFields[ImportFields.IDX_PROGRAM_NAME].trim().length() > 0
                                    && !hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                    programSignUp(hwFields, appFields, liteAcctInfo, liteInv, result);
                                }
                            } catch (Exception e) {
                                result.custFileErrors++;
                                String[] value = result.getCustLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
                                result.getCustLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }
                        }
                    }
                    if (result.isCanceled()) {
                        throw new Exception();
                    }
                }
            }
                
                if (!preScan || result.isScheduled()) {
                    result.setNumAcctTotal(custFieldsList.size());
                    
                    boolean isDeleted = custFile.delete();
                    if (isDeleted == false) {
                        importLog.println("* "+custFile.getName()+" was not deleted *");
                    }
                }
            }
            
            if (hwFile != null) {
                hwFieldsList = Lists.newArrayList();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(hwFile)));
                     PrintWriter archive = new PrintWriter(new FileWriter(result.getHwArchiveDir()), true);) {

                String line = null;
                result.setHwLines(new TreeMap<Integer, String[]>());
                lineNo = 0;
                Integer lineNoKey;
                
                // Sets up the archive file
                while ((line = reader.readLine()) != null) {
                    // This line adds the latest line to the archive file
                    archive.println(line);
                    
                    lineNo++;
                    lineNoKey = lineNo;
                    
                    if (line.trim().length() == 0 || line.charAt(0) == '#') {
                        continue;
                    }

                    result.getHwLines().put(lineNo, new String[]{line, null});
                    
                    result.setPosition("hardware file line #" + lineNo);
                    
                    if (lineNo == 1) {
                        if (line.startsWith(result.COL_NAME_LABEL) || line.startsWith(result.CUST_COLUMNS[0])) {
                            String[] labels;
                            if (line.startsWith(result.COL_NAME_LABEL)) {
                                labels = StarsUtils.splitString(line.substring(result.COL_NAME_LABEL.length()), ",");
                            } else {
                                labels = StarsUtils.splitString(line, ",");
                            }
                            
                            numHwCol = labels.length;
                            
                            for (int i = 0; i < labels.length; i++) {
                                for (int j = 0; j < result.HW_COLUMNS.length; j++) {
                                    if (labels[i].equalsIgnoreCase(result.HW_COLUMNS[j])) {
                                        hwColIdx[j] = i;
                                        break;
                                    }
                                }
                            }
                            
                            if (hwColIdx[result.COL_ACCOUNT_NO] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_ACCOUNT_NO] + "' is missing";
                                automationCheck(errorStr, automatedImport);
                            }
                            if (hwColIdx[result.COL_SERIAL_NO] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_SERIAL_NO] + "' is missing";
                                automationCheck(errorStr, automatedImport);
                            }
                            if (hwColIdx[result.COL_DEVICE_TYPE] == -1) {
                                String errorStr = "The required column '" + result.HW_COLUMNS[result.COL_DEVICE_TYPE] + "' is missing";
                                automationCheck(errorStr, automatedImport);
                            }
                            
                            continue;
                        }
                        
                        for (int i = 0; i < result.HW_COLUMNS.length; i++) {
                            hwColIdx[i] = i;
                        }
                    }
                    
                    String[] columns = StarsUtils.splitString(line, ",");
                    if (columns.length > numHwCol) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Incorrect number of fields]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    String[] hwFields = prepareFields(ImportFields.NUM_INV_FIELDS);
                    hwFields[ImportFields.IDX_LINE_NUM] = String.valueOf(lineNo);
                    setHardwareFields(hwFields, columns, hwColIdx, result);
                    
                    if (hwFields[ImportFields.IDX_ACCOUNT_ID].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Account # cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    if (hwFields[ImportFields.IDX_SERIAL_NO].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Serial # cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    if (hwFields[ImportFields.IDX_DEVICE_TYPE].trim().length() == 0) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Device type cannot be empty]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }
                    
                    YukonSelectionList devTypeList = selectionListService.getSelectionList(energyCompany, 
                                                       YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE);
                    YukonListEntry deviceType = yukonListDao.getYukonListEntry(devTypeList, hwFields[ImportFields.IDX_DEVICE_TYPE]);
                    if (deviceType == null) {
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Invalid device type \"" + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    if(isZigbeeDevice(deviceType, result, lineNoKey, hwFields,true)) {
                        continue;
                    }
                    
                    if (isNestDevice(deviceType, result, lineNoKey, hwFields, true)) {
                        continue;
                    }

                    if (HardwareType.valueOf(deviceType.getYukonDefID()).isHoneywell()) {
                        if (hwFields[ImportFields.IDX_MAC_ADDRESS].trim().length() == 0) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: MAC Address cannot be empty for Honeywell Device]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        } else if (hwFields[ImportFields.IDX_DEVICE_VENDOR_USER_ID].trim().length() == 0) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Device Vendor UserId cannot be empty for Honeywell Device]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                        /*
                         * Some customers use rotation digits on the end of account numbers.
                         * EXAMPLE: if a customer changed at account 123456, the whole account number
                         * plus rotation digits might change like this: 12345610 to 12345620, so we want
                         * to only consider the accountnumber itself.  The number of digits to consider
                         * as valid comparable, non-rotation digits of the account number is expressed in a role property. 
                         */
                    LiteAccountInfo liteAcctInfo = starsSearchService.searchAccountByAccountNo(energyCompany, hwFields[ImportFields.IDX_ACCOUNT_ID]);
                    if (liteAcctInfo == null) {
                        if (hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                            importLog.println("WARNING at " + result.getPosition() + ": account #" + hwFields[ImportFields.IDX_ACCOUNT_ID] + " doesn't exist, record ignored");
                            continue;
                        }
                            
                        result.hwFileErrors++;
                        String[] value = result.getHwLines().get(lineNoKey);
                        value[1] = "[line: " + lineNo + " error: Cannot import hardware, account #" + hwFields[ImportFields.IDX_ACCOUNT_ID] + " doesn't exist]";
                        result.getHwLines().put(lineNoKey, value);
                        addToLog(lineNoKey, value, importLog);
                        continue;
                    }

                    String[] appFields = null;
                    if (hwColIdx[result.COL_APP_TYPE] != -1) {
                        appFields = prepareFields(ImportFields.NUM_APP_FIELDS);
                        setApplianceFields(appFields, columns, hwColIdx, result);
                        if (preScan && appFieldsList == null) {
                            appFieldsList = Lists.newArrayList();
                        }
                        
                        if ((appFields[ImportFields.IDX_APP_TYPE].trim().length() > 0) &&
                            (!applianceNameList.contains(appFields[ImportFields.IDX_APP_TYPE]))) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: Appliance Type was supplied, but doesn't exist]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    }
                   
                    if (preScan) {
                        String acctNo = hwFieldsMap.get(hwFields[ImportFields.IDX_SERIAL_NO]);
                        if (acctNo == null) {
                            LiteLmHardwareBase liteHw = starsSearchDao.searchLmHardwareBySerialNumber(hwFields[ImportFields.IDX_SERIAL_NO], energyCompany);
                            if (liteHw != null && liteHw.getAccountID() > 0) {
                                acctNo = scaiDao.getById(liteHw.getAccountID(), energyCompany.getEnergyCompanyId()).getCustomerAccount().getAccountNumber();
                            }
                        }
                        
                        if (hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                            if (acctNo == null || !acctNo.equalsIgnoreCase(hwFields[ImportFields.IDX_ACCOUNT_ID])) {
                                importLog.println("WARNING at " + result.getPosition() + ": serial #" + hwFields[ImportFields.IDX_SERIAL_NO] + " not found in the customer account, record ignored");
                                continue;
                            }

                            hwFieldsList.add(hwFields);
                            hwFieldsMap.put(hwFields[ImportFields.IDX_SERIAL_NO], "");
                        } else {
                            if (acctNo != null && !acctNo.equals("") && !acctNo.equalsIgnoreCase(hwFields[ImportFields.IDX_ACCOUNT_ID])) {
                                result.hwFileErrors++;
                                String[] value = result.getHwLines().get(lineNoKey);
                                value[1] = "[line: " + lineNo + " error: Cannot import hardware, serial #" + hwFields[ImportFields.IDX_SERIAL_NO] + " already exists in another account]";
                                result.getHwLines().put(lineNoKey, value);
                                addToLog(lineNoKey, value, importLog);
                                continue;
                            }

                            if (acctNo != null && acctNo.equals(hwFields[ImportFields.IDX_ACCOUNT_ID])) {
                                hwFields[ImportFields.IDX_HARDWARE_ACTION] = "UPDATE";
                            } else {
                                hwFields[ImportFields.IDX_HARDWARE_ACTION] = "INSERT";
                            }
                            
                            hwFieldsList.add(hwFields);
                            hwFieldsMap.put(hwFields[ImportFields.IDX_SERIAL_NO], hwFields[ImportFields.IDX_ACCOUNT_ID]);
                        }
                        
                        if (appFieldsList != null){
                            appFieldsList.add(appFields);
                        }
                    }
                    
                    if (!preScan || result.isScheduled()) {
                        LiteInventoryBase liteInv;
                        try {
                            liteInv = importHardware(hwFields, liteAcctInfo, result, context.getYukonUser());                           

                            if (hwFields[ImportFields.IDX_PROGRAM_NAME].trim().length() > 0
                                    && !hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                                programSignUp(hwFields, appFields, liteAcctInfo, liteInv, result);
                            }
                        } catch (Exception e) {
                            result.hwFileErrors++;
                            String[] value = result.getHwLines().get(lineNoKey);
                            value[1] = "[line: " + lineNo + " error: " + e.getMessage() + "]";
                            result.getHwLines().put(lineNoKey, value);
                            addToLog(lineNoKey, value, importLog);
                            continue;
                        }
                    } 

                    if (result.isCanceled()) {
                        throw new Exception();
                    }
                }
            }
                if (!preScan || result.isScheduled()) {
                    result.setNumHwTotal(hwFieldsList.size());
                    boolean isDeleted = hwFile.delete();
                    if (isDeleted == false) {
                        importLog.println("* " + hwFile.getName() + " was not deleted *");
                    }
                }
            }
            
            result.setComplete(true);

            if (!result.isScheduled()) {
                sendSmartNotifications(result, context);
            }

        } catch (Exception e) {
            result.setComplete(true);
            if (!result.isCanceled()) {
                CTILogger.error(e.getMessage(), e);
                String[] value = new String[3];
                
                if (importLog == null) {
                    try {
                        importLog = new PrintWriter(new FileWriter(result.getOutputLogDir()), true);
                        importLog.println("Error Occurred");
                    } catch (IOException e1) {
                        CTILogger.error(e1);
                    }
                }

                // Could not open the file passing error writing.
                if (!result.getErrors().isEmpty()) {
                    importLog.println("Error Occurred");
                    if (result.getCustLines() != null) {
                        value = result.getCustLines().get(lineNo);
                        result.getCustLines().put(lineNo, value);
                    } else {
                        if (result.getHwLines() != null) {
                            value = result.getHwLines().get(lineNo);
                            result.getHwLines().put(lineNo, value);
                        }
                    }
                    
                    value[1] = "[line: "+lineNo+" error: "+e.getMessage()+"]";
                    addToLog(lineNo, value, importLog);
                }
            }
            if (result.isScheduled()) {
                throw new ScheduledDataImportException(e);
            }

            ActivityLogger.logEvent(result.getCurrentUser().getUserID(), ActivityLogActions.IMPORT_CUSTOMER_ACCOUNT_ACTION, "");
        } finally {
            result.setStopTime(new Instant());
        }
        
        if (importLog != null) {
            importLog.println();
            
            importLog.println("Stop time: "
                    + StarsUtils.formatDate(new Date(), 
                          ecService.getDefaultTimeZone(result.getEnergyCompany().getEnergyCompanyId())));
            importLog.println();
            importLog.close();

            try {
                if (!result.isScheduled() && !StringUtils.isBlank(result.getEmail()) && ((preScan && result.hasErrors()) || !preScan)) {
                    sendImportLog(result.getOutputLogDir(), result.getEmail(), result.getEnergyCompany());
                }
            } catch (Exception e) {
                log.error("Failed to send the import log by email");
            }
        }

    }

    private void sendSmartNotifications(AccountImportResult result, YukonUserContext context) {

        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        String taskName;
        if (result.isPrescan()) {
            taskName = accessor.getMessage("yukon.web.modules.smartNotifications.MANUAL_PRESCAN.taskName");
        } else {
            taskName = accessor.getMessage("yukon.web.modules.smartNotifications.MANUAL_IMPORT.taskName");
        }
        // The first parameter passed to getDataImportWarning is -1 because this is manual import and does
        // not have a job id associated with it.

        List<String> errorFiles = new ArrayList<>();
        if (result.custFileErrors != 0) {
            errorFiles.add(result.getAccountFileUpload().getName());
        }
        if (result.hwFileErrors != 0) {
            errorFiles.add(result.getHardwareFileUpload().getName());
        }

        int successFileCount = 0;
        if (result.getHardwareFile() != null && result.getCustomerFile() != null) {
            successFileCount = 2 - errorFiles.size(); // Can upload both files simultaneously
        } else {
            successFileCount = 1 - errorFiles.size();
        }

        List<DataImportWarning> dataImportwarning = DataImportHelper.getDataImportWarning(-1, taskName,
            ScheduledImportType.ASSET_IMPORT.getImportType(), errorFiles, successFileCount);
        List<SmartNotificationEvent> smartNotificationEvent = dataImportwarning.stream()
                                                                               .map(importWarning -> DataImportAssembler.assemble(Instant.now(), importWarning))
                                                                               .collect(Collectors.toList());
        smartNotificationEventCreationService.send(SmartNotificationEventType.ASSET_IMPORT, smartNotificationEvent);
    }

    private LiteInventoryBase importHardware(String[] hwFields, 
                                             LiteAccountInfo liteAcctInfo, 
                                             AccountImportResult result, 
                                             LiteYukonUser user) throws Exception {
        
        YukonEnergyCompany energyCompany = result.getEnergyCompany();
        LiteInventoryBase liteInv = null;

        for (Integer inventoryId : liteAcctInfo.getInventories()) {
            LiteInventoryBase lInv = inventoryBaseDao.getByInventoryId(inventoryId);
            if (lInv instanceof LiteLmHardwareBase && ((LiteLmHardwareBase)lInv).getManufacturerSerialNumber().equals(hwFields[ImportFields.IDX_SERIAL_NO])) {
                liteInv = lInv;
                break;
            }
        }
        
        try {
            
            String accountNumber = liteAcctInfo.getCustomerAccount().getAccountNumber();
            
            if (hwFields[ImportFields.IDX_HARDWARE_ACTION].equalsIgnoreCase("REMOVE")) {
                hardwareLog.hardwareRemovalAttempted(user, accountNumber, hwFields[ImportFields.IDX_SERIAL_NO], EventSource.ACCOUNT_IMPORTER);
                
                if (liteInv == null) {
                    throw new Exception("Cannot remove hardware, serial #" + hwFields[ImportFields.IDX_SERIAL_NO] + " not found in the customer account");
                }
                
                // REMOVE HARDWARE
                LmDeviceDto dto = dtoConverter.getDtoForHardware(accountNumber, liteInv, energyCompany);
                
                if (!StringUtils.isBlank(hwFields[ImportFields.IDX_REMOVE_DATE])) {
                    TimeZone ecTimezone = ecService.getDefaultTimeZone(result.getEnergyCompany().getEnergyCompanyId());
                    Date removeDate = 
                            ServletUtil.parseDateStringLiberally(hwFields[ImportFields.IDX_REMOVE_DATE], ecTimezone);
                    if (removeDate == null) {
                        removeDate = StarsUtils.starsDateFormat.parse(hwFields[ImportFields.IDX_REMOVE_DATE]);
                    }
                    dto.setFieldRemoveDate(removeDate);
                }
                
                deviceHelper.removeDeviceFromAccount(dto, result.getCurrentUser());
                
                result.getHardwareRemoved().add(hwFields[ImportFields.IDX_SERIAL_NO]);
            } else if (liteInv == null) {
                hardwareLog.hardwareCreationAttempted(user, accountNumber, hwFields[ImportFields.IDX_SERIAL_NO], EventSource.ACCOUNT_IMPORTER);
                
                // ADD HARDWARE
                LmDeviceDto dto = dtoConverter.createNewDto(accountNumber, hwFields, energyCompany);
                liteInv = deviceHelper.addDeviceToAccount(dto, result.getCurrentUser());
                
                result.getHardwareAdded().add(hwFields[ImportFields.IDX_SERIAL_NO]);
            } else if (!result.isInsertSpecified()) {
                hardwareLog.hardwareUpdateAttempted(user, accountNumber, hwFields[ImportFields.IDX_SERIAL_NO], EventSource.ACCOUNT_IMPORTER);
                
                // UPDATE HARDWARE
                LmDeviceDto dto = dtoConverter.getDtoForHardware(accountNumber, liteInv, energyCompany);
                dtoConverter.updateDtoWithHwFields(dto, hwFields, energyCompany);
                liteInv = deviceHelper.updateDeviceOnAccount(dto, result.getCurrentUser(), false);
                
                result.getHardwareUpdated().add(hwFields[ImportFields.IDX_SERIAL_NO]);
            }
        } catch (Exception e) {
            automationCheck(e.getMessage(), result.isAutomatedImport());
        }
        
        result.setNumHwImported(result.getNumHwImported() + 1);
        return liteInv;
    }
    
    @SuppressWarnings("deprecation")
    private LiteAccountInfo importAccount(String[] custFields, AccountImportResult result, LiteYukonUser user) throws Exception {
        YukonEnergyCompany energyCompany = result.getEnergyCompany();
        LiteAccountInfo liteAcctInfo = 
                starsSearchService.searchAccountByAccountNo(energyCompany, custFields[ImportFields.IDX_ACCOUNT_NO]);
        try {
            
            if (custFields[ImportFields.IDX_ACCOUNT_ACTION].equalsIgnoreCase("REMOVE")) {
                accountLog.accountDeletionAttempted(user, custFields[ImportFields.IDX_ACCOUNT_NO], EventSource.ACCOUNT_IMPORTER);
                // deletion 
                if (liteAcctInfo == null) {
                    String errorStr = "Cannot delete customer account: account #" + custFields[ImportFields.IDX_ACCOUNT_NO] + " doesn't exist";
                    automationCheck(errorStr, result.isAutomatedImport());
                } else {
                    
                    // DELETE ACCOUNT
                    accountService.deleteAccount(liteAcctInfo.getCustomerAccount().getAccountNumber(), user, energyCompany);
                    
                    result.getAccountsRemoved().add(custFields[ImportFields.IDX_ACCOUNT_NO]);
                    result.setNumAcctImported(result.getNumAcctImported() + 1);
                    
                    return null;
                }
            }
            
            if (liteAcctInfo == null) {
                accountLog.accountCreationAttempted(user, custFields[ImportFields.IDX_ACCOUNT_NO], EventSource.ACCOUNT_IMPORTER);
                
                // Validates the IVR fields and throws a web client exception if they don't
                ServletUtils.formatPin(custFields[ImportFields.IDX_IVR_USERNAME]);
                ServletUtils.formatPin(custFields[ImportFields.IDX_IVR_PIN]);
                
                // ADD ACCOUNT
                UpdatableAccount updatableAccount = accountConverter.createNewUpdatableAccount(custFields, energyCompany);
                accountService.addAccount(updatableAccount, user, energyCompany);
                liteAcctInfo = starsSearchService.searchAccountByAccountNo(energyCompany, custFields[ImportFields.IDX_ACCOUNT_NO]);
                result.getAccountsAdded().add(custFields[ImportFields.IDX_ACCOUNT_NO]);
            } else if (!result.isInsertSpecified()) {
                accountLog.accountUpdateAttempted(user, custFields[ImportFields.IDX_ACCOUNT_NO], EventSource.ACCOUNT_IMPORTER);
    
                // Validates the IVR fields and throws a web client exception if they don't
                ServletUtils.formatPin(custFields[ImportFields.IDX_IVR_USERNAME]);
                ServletUtils.formatPin(custFields[ImportFields.IDX_IVR_PIN]);
                
                // UPDATE ACCOUNT
                UpdatableAccount updatableAccount = accountConverter.getUpdatedUpdatableAccount(liteAcctInfo, custFields, energyCompany);
                accountService.updateAccount(updatableAccount, user, energyCompany);
                
                result.getAccountsUpdated().add(custFields[ImportFields.IDX_ACCOUNT_NO]);
            }
    
        } catch (Exception e) {
            automationCheck(e.getMessage(), result.isAutomatedImport());
        }

        result.setNumAcctImported(result.getNumAcctImported() + 1);
        return liteAcctInfo;
    }

    
    /**
     * Private method to identify whether given YukonListEntry is a Zigbee device type or not
     * 
     * @param deviceType - The deviceType value from hardware import sheet
     * @param result - Account Import Result for the current import operation
     * @param lineNoKey - Key or index of line number in the import sheet
     * @param hwFields - The string array holding the hardware sheet details
     * @param isHwFile - Boolean value indicating its a hardware file or not.
     * @return - boolean true if the device type corresponds to zibgee device
     */
    private boolean isZigbeeDevice(YukonListEntry deviceType, AccountImportResult result, Integer lineNoKey,
            String[] hwFields, boolean isHwFile) {

        boolean isZigbee = false;
        try {
            if (HardwareType.valueOf(deviceType.getYukonDefID()).isZigbee()) {
                String[] value;
                if (!isHwFile) {
                    result.custFileErrors++;
                    value = result.getCustLines().get(lineNoKey);
                    value[1] =
                        "[line: " + lineNoKey.intValue() + " error: Cannot import Zigbee device type \""
                            + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                    result.getCustLines().put(lineNoKey, value);
                } else {
                    result.hwFileErrors++;
                    value = result.getHwLines().get(lineNoKey);
                    value[1] =
                        "[line: " + lineNoKey.intValue() + " error: Cannot import Zigbee device type \""
                            + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                    result.getHwLines().put(lineNoKey, value);
                }
                addToLog(lineNoKey, value, importLog);
                isZigbee = true;
            }
        } catch (IllegalArgumentException exception) {
            log.warn("Hardware Device type is not valid", exception);
        }
        return isZigbee;
    }
    
    /**
     * Method to identify whether given YukonListEntry is a Nest device type or not.
     * Nest devices cannot be imported. 
     */
    private boolean isNestDevice(YukonListEntry deviceType, AccountImportResult result, Integer lineNoKey,
            String[] hwFields, boolean isHwFile) {

        boolean isNest = false;
        try {
            if (HardwareType.valueOf(deviceType.getYukonDefID()).isNest()) {
                String[] value;
                if (!isHwFile) {
                    result.custFileErrors++;
                    value = result.getCustLines().get(lineNoKey);
                    value[1] =
                        "[line: " + lineNoKey.intValue() + " error: Cannot import Nest device type \""
                            + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                    result.getCustLines().put(lineNoKey, value);
                } else {
                    result.hwFileErrors++;
                    value = result.getHwLines().get(lineNoKey);
                    value[1] =
                        "[line: " + lineNoKey.intValue() + " error: Cannot import Nest device type \""
                            + hwFields[ImportFields.IDX_DEVICE_TYPE] + "\"]";
                    result.getHwLines().put(lineNoKey, value);
                }
                addToLog(lineNoKey, value, importLog);
                isNest = true;
            }
        } catch (IllegalArgumentException exception) {
            log.warn("Hardware Device type is not valid", exception);
        }
        return isNest;
    }

    private void setCustomerFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        
        if (colIdx[result.COL_ACCOUNT_NO] >= 0 && colIdx[result.COL_ACCOUNT_NO] < columns.length) {
            fields[ImportFields.IDX_ACCOUNT_NO] = columns[ colIdx[result.COL_ACCOUNT_NO] ];
        }
        if (colIdx[result.COL_CUST_ACTION] >= 0 && colIdx[result.COL_CUST_ACTION] < columns.length) {
            fields[ImportFields.IDX_ACCOUNT_ACTION] = columns[ colIdx[result.COL_CUST_ACTION] ];
        }
        if (colIdx[result.COL_LAST_NAME] >= 0 && colIdx[result.COL_LAST_NAME] < columns.length) {
            fields[ImportFields.IDX_LAST_NAME] = columns[ colIdx[result.COL_LAST_NAME] ];
        }
        if (colIdx[result.COL_FIRST_NAME] >= 0 && colIdx[result.COL_FIRST_NAME] < columns.length) {
            fields[ImportFields.IDX_FIRST_NAME] = columns[ colIdx[result.COL_FIRST_NAME] ];
        }
        if (colIdx[result.COL_LAST_NAME] >= 0 && colIdx[result.COL_LAST_NAME] < columns.length) {
            fields[ImportFields.IDX_LAST_NAME] = columns[ colIdx[result.COL_LAST_NAME] ];
        }
        if (colIdx[result.COL_HOME_PHONE] >= 0 && colIdx[result.COL_HOME_PHONE] < columns.length) {
            fields[ImportFields.IDX_HOME_PHONE] = columns[ colIdx[result.COL_HOME_PHONE] ];
        }
        if (colIdx[result.COL_WORK_PHONE] >= 0 && colIdx[result.COL_WORK_PHONE] < columns.length) {
            fields[ImportFields.IDX_WORK_PHONE] = columns[ colIdx[result.COL_WORK_PHONE] ];
        }
        if (colIdx[result.COL_EMAIL] >= 0 && colIdx[result.COL_EMAIL] < columns.length) {
            fields[ImportFields.IDX_EMAIL] = columns[ colIdx[result.COL_EMAIL] ];
        }
        if (colIdx[result.COL_STREET_ADDR1] >= 0 && colIdx[result.COL_STREET_ADDR1] < columns.length) {
            fields[ImportFields.IDX_STREET_ADDR1] = columns[ colIdx[result.COL_STREET_ADDR1] ];
        }
        if (colIdx[result.COL_STREET_ADDR2] >= 0 && colIdx[result.COL_STREET_ADDR2] < columns.length) {
            fields[ImportFields.IDX_STREET_ADDR2] = columns[ colIdx[result.COL_STREET_ADDR2] ];
        }
        if (colIdx[result.COL_CITY] >= 0 && colIdx[result.COL_CITY] < columns.length) {
            fields[ImportFields.IDX_CITY] = columns[ colIdx[result.COL_CITY] ];
        }
        if (colIdx[result.COL_STATE] >= 0 && colIdx[result.COL_STATE] < columns.length) {
            fields[ImportFields.IDX_STATE] = columns[ colIdx[result.COL_STATE] ];
        }
        if (colIdx[result.COL_COUNTY] >= 0 && colIdx[result.COL_COUNTY] < columns.length) {
            fields[ImportFields.IDX_COUNTY] = columns[ colIdx[result.COL_COUNTY] ];
        }
        if (colIdx[result.COL_ZIP_CODE] >= 0 && colIdx[result.COL_ZIP_CODE] < columns.length) {
            fields[ImportFields.IDX_ZIP_CODE] = columns[ colIdx[result.COL_ZIP_CODE] ];
        }
        if (colIdx[result.COL_MAP_NO] >= 0 && colIdx[result.COL_MAP_NO] < columns.length) {
            fields[ImportFields.IDX_MAP_NO] = columns[ colIdx[result.COL_MAP_NO] ];
        }
        if (colIdx[result.COL_SUBSTATION] >= 0 && colIdx[result.COL_SUBSTATION] < columns.length && columns[colIdx[result.COL_SUBSTATION]].trim().length() > 0) {
            fields[ImportFields.IDX_SUBSTATION] = columns[ colIdx[result.COL_SUBSTATION] ];
        }
        if (colIdx[result.COL_FEEDER] >= 0 && colIdx[result.COL_FEEDER] < columns.length) {
            fields[ImportFields.IDX_FEEDER] = columns[ colIdx[result.COL_FEEDER] ];
        }
        if (colIdx[result.COL_POLE] >= 0 && colIdx[result.COL_POLE] < columns.length) {
            fields[ImportFields.IDX_POLE] = columns[ colIdx[result.COL_POLE] ];
        }
        if (colIdx[result.COL_TRFM_SIZE] >= 0 && colIdx[result.COL_TRFM_SIZE] < columns.length) {
            fields[ImportFields.IDX_TRFM_SIZE] = columns[ colIdx[result.COL_TRFM_SIZE] ];
        }
        if (colIdx[result.COL_SERV_VOLT] >= 0 && colIdx[result.COL_SERV_VOLT] < columns.length) {
            fields[ImportFields.IDX_SERV_VOLT] = columns[ colIdx[result.COL_SERV_VOLT] ];
        }
        if (colIdx[result.COL_USERNAME] >= 0 && colIdx[result.COL_USERNAME] < columns.length) {
            fields[ImportFields.IDX_USERNAME] = columns[ colIdx[result.COL_USERNAME] ];
        }
        if (colIdx[result.COL_PASSWORD] >= 0 && colIdx[result.COL_PASSWORD] < columns.length) {
            fields[ImportFields.IDX_PASSWORD] = columns[ colIdx[result.COL_PASSWORD] ];
        }
        if (colIdx[result.COL_LOGIN_GROUP] >= 0 && colIdx[result.COL_LOGIN_GROUP] < columns.length) {
            fields[ImportFields.IDX_LOGIN_GROUP] = columns[ colIdx[result.COL_LOGIN_GROUP] ];
        }
        if (colIdx[result.COL_COMPANY_NAME] >= 0 && colIdx[result.COL_COMPANY_NAME] < columns.length) {
            fields[ImportFields.IDX_COMPANY_NAME] = columns[ colIdx[result.COL_COMPANY_NAME] ];
        }
        if (colIdx[result.COL_IVR_PIN] >= 0 && colIdx[result.COL_IVR_PIN] < columns.length) {
            fields[ImportFields.IDX_IVR_PIN] = columns[ colIdx[result.COL_IVR_PIN] ];
        }
        if (colIdx[result.COL_IVR_USERNAME] >= 0 && colIdx[result.COL_IVR_USERNAME] < columns.length) {
            fields[ImportFields.IDX_IVR_USERNAME] = columns[colIdx[result.COL_IVR_USERNAME]];
        }
        if (colIdx[result.COL_CUST_ALT_TRACK_NO] >= 0 && colIdx[result.COL_CUST_ALT_TRACK_NO] < columns.length) {
            fields[ImportFields.IDX_CUSTOMER_ALT_TRACK_NO] = columns[colIdx[result.COL_CUST_ALT_TRACK_NO]];
        }
    }
    
    private void setHardwareFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        if (colIdx[result.COL_HW_ACCOUNT_NO] >= 0 && colIdx[result.COL_HW_ACCOUNT_NO] < columns.length)
         {
            fields[ImportFields.IDX_ACCOUNT_ID] = columns[ colIdx[result.COL_HW_ACCOUNT_NO] ];    // Use account ID field to store account #
        }
        if (colIdx[result.COL_HW_ACTION] >= 0 && colIdx[result.COL_HW_ACTION] < columns.length) {
            fields[ImportFields.IDX_HARDWARE_ACTION] = columns[ colIdx[result.COL_HW_ACTION] ];
        }
        if (colIdx[result.COL_SERIAL_NO] >= 0 && colIdx[result.COL_SERIAL_NO] < columns.length) {
            fields[ImportFields.IDX_SERIAL_NO] = columns[ colIdx[result.COL_SERIAL_NO] ];
        }
        if (colIdx[result.COL_DEVICE_TYPE] >= 0 && colIdx[result.COL_DEVICE_TYPE] < columns.length) {
            fields[ImportFields.IDX_DEVICE_TYPE] = columns[ colIdx[result.COL_DEVICE_TYPE] ];
        }
        if (colIdx[result.COL_INSTALL_DATE] >= 0 && colIdx[result.COL_INSTALL_DATE] < columns.length) {
            fields[ImportFields.IDX_INSTALL_DATE] = columns[ colIdx[result.COL_INSTALL_DATE] ];
        }
        if (colIdx[result.COL_REMOVE_DATE] >= 0 && colIdx[result.COL_REMOVE_DATE] < columns.length) {
            fields[ImportFields.IDX_REMOVE_DATE] = columns[ colIdx[result.COL_REMOVE_DATE] ];
        }
        if (colIdx[result.COL_SERVICE_COMPANY] >= 0 && colIdx[result.COL_SERVICE_COMPANY] < columns.length && !columns[colIdx[result.COL_SERVICE_COMPANY]].trim().equals("")) {
            fields[ImportFields.IDX_SERVICE_COMPANY] = columns[ colIdx[result.COL_SERVICE_COMPANY] ];
        }
        if (colIdx[result.COL_PROGRAM_NAME] >= 0 && colIdx[result.COL_PROGRAM_NAME] < columns.length) {
            fields[ImportFields.IDX_PROGRAM_NAME] = columns[ colIdx[result.COL_PROGRAM_NAME] ];
        }
        if (colIdx[result.COL_ADDR_GROUP] >= 0 && colIdx[result.COL_ADDR_GROUP] < columns.length) {
            fields[ImportFields.IDX_ADDR_GROUP] = columns[ colIdx[result.COL_ADDR_GROUP] ];
        }
        if (colIdx[result.COL_MAC_ADDRESS] >= 0 && colIdx[result.COL_MAC_ADDRESS] < columns.length) {
            fields[ImportFields.IDX_MAC_ADDRESS] = columns[colIdx[result.COL_MAC_ADDRESS]];
        }
        if (colIdx[result.COL_DEVICE_VENDOR_USER_ID] >= 0 && colIdx[result.COL_DEVICE_VENDOR_USER_ID] < columns.length) {
            fields[ImportFields.IDX_DEVICE_VENDOR_USER_ID] = columns[colIdx[result.COL_DEVICE_VENDOR_USER_ID]];
        }
        if (colIdx[result.COL_OPTION_PARAMS] >= 0 && colIdx[result.COL_OPTION_PARAMS] < columns.length) {
            fields[ImportFields.IDX_OPTION_PARAMS] = columns[ colIdx[result.COL_OPTION_PARAMS] ];
        }
        if (colIdx[result.COL_DEVICE_LABEL] >= 0 && colIdx[result.COL_DEVICE_LABEL] < columns.length) {
            fields[ImportFields.IDX_DEVICE_LABEL] = columns[ colIdx[result.COL_DEVICE_LABEL] ];
        }
        if (colIdx[result.COL_LATITUDE] >= 0 && colIdx[result.COL_LATITUDE] < columns.length) {
            fields[ImportFields.IDX_LATITUDE] = columns[ colIdx[result.COL_LATITUDE] ];
        }
        if (colIdx[result.COL_LONGITUDE] >= 0 && colIdx[result.COL_LONGITUDE] < columns.length) {
            fields[ImportFields.IDX_LONGITUDE] = columns[ colIdx[result.COL_LONGITUDE] ];
        }
    }
    
    private void setApplianceFields(String[] fields, String[] columns, int[] colIdx, AccountImportResult result) {
        if (colIdx[result.COL_APP_TYPE] >= 0 && colIdx[result.COL_APP_TYPE] < columns.length) {
            fields[ImportFields.IDX_APP_TYPE] = columns[ colIdx[result.COL_APP_TYPE] ];
        }
        if (colIdx[result.COL_APP_KW] >= 0 && colIdx[result.COL_APP_KW] < columns.length) {
            fields[ImportFields.IDX_APP_KW] = columns[ colIdx[result.COL_APP_KW] ];
        }
        if (colIdx[result.COL_APP_RELAY_NUMBER] >= 0 && colIdx[result.COL_APP_RELAY_NUMBER] < columns.length) {
            fields[ImportFields.IDX_RELAY_NUM] = columns[ colIdx[result.COL_APP_RELAY_NUMBER] ];
        }
    }
    
    private File getBaseDir(YukonEnergyCompany energyCompany, LiteYukonUser user) {
        final String fs = System.getProperty("file.separator");
        String ecName = energyCompany.getName();
        if (ecName.indexOf('<') > -1) {
            ecName = "EnergyCompany" + energyCompany.getEnergyCompanyId();
        }
        
        // Check to see if it exist in role property if not use default.
        String baseDirRoleValue = globalSettingDao.getString(GlobalSettingType.CUSTOMER_INFO_IMPORTER_FILE_LOCATION);

        // Gets base Directory
        File baseDir = null;
        if (baseDirRoleValue.trim().length() > 0) {
            baseDir = new File(baseDirRoleValue);
        } else {
            baseDir = new File(StarsUtils.getStarsTempDir() + fs + ecName);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }
        }
        
        return baseDir;
    }
    
    private void addToLog(Integer lineNoKey, String[] value, PrintWriter importLog) {
        importLog.println("Error found -Line "+lineNoKey+"= "+value[0]+"- Error="+value[1]);
    }
    
    private void automationCheck(String errorStr, boolean isAutomatedImport) throws Exception {
        if (isAutomatedImport) {
            importLog.println(errorStr);
        } else {
            throw new Exception(errorStr);
        }
    }
    
    private String[] prepareFields(int numFields) {
        String[] fields = new String[ numFields ];
        for (int i = 0; i < numFields; i++) {
            fields[i] = "";
        }
        return fields;
    }
    
    private void programSignUp(String[] hwFields, 
                               String[] appFields, 
                               LiteAccountInfo liteAcctInfo, 
                               LiteInventoryBase liteInv, 
                               AccountImportResult result) throws Exception {
       if (appFields == null) {
           appFields = prepareFields(ImportFields.NUM_APP_FIELDS);
       }
       
       try {
           EnrollmentHelper enrollmentHelper = new EnrollmentHelper();
           EnrollmentEnum enrollType = EnrollmentEnum.ENROLL;
           enrollmentHelper.setAccountNumber(liteAcctInfo.getCustomerAccount().getAccountNumber());
           enrollmentHelper.setSerialNumber(hwFields[ImportFields.IDX_SERIAL_NO]);
           if (result.UNENROLL_CASE.equalsIgnoreCase(hwFields[ImportFields.IDX_PROGRAM_NAME])) {
               enrollType = EnrollmentEnum.UNENROLL;
               // leave programName blank, to unenroll from all programs for the Inventory
           } else {
               enrollmentHelper.setProgramName(hwFields[ImportFields.IDX_PROGRAM_NAME]);    
           }
           enrollmentHelper.setLoadGroupName(hwFields[ImportFields.IDX_ADDR_GROUP]);
           enrollmentHelper.setRelay(appFields[ImportFields.IDX_RELAY_NUM]);        
           enrollmentHelper.setApplianceCategoryName(appFields[ImportFields.IDX_APP_TYPE]);
           if (!StringUtils.isBlank(appFields[ImportFields.IDX_APP_KW])) {
               try {
                   Double appKw = Double.parseDouble(appFields[ImportFields.IDX_APP_KW]);
                   if (appKw < 0) {
                    throw new IllegalArgumentException("Appliance KW should be a valid numeric value");
                }
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
    
    private void sendImportLog(File importLog, String email, YukonEnergyCompany energyCompany) throws Exception {
        
        String body = "The log file containing information of the import process is attached." + LINE_SEPARATOR + LINE_SEPARATOR;
        String adminEmailAddress = ecSettingDao.getString(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, energyCompany.getEnergyCompanyId());
        if (adminEmailAddress == null || adminEmailAddress.trim().length() == 0) {
            adminEmailAddress = StarsUtils.ADMIN_EMAIL_ADDRESS;
        }
        EmailAttachmentMessage message = 
                new EmailAttachmentMessage(new InternetAddress(adminEmailAddress),
                                                  InternetAddress.parse(email), "Import Log", body);
        EmailFileDataSource dataSource = new EmailFileDataSource(importLog);
        message.addAttachment(dataSource);
        emailService.sendMessage(message);
    }
    
}