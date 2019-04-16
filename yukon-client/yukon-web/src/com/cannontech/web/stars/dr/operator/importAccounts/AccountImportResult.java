package com.cannontech.web.stars.dr.operator.importAccounts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.Completable;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AccountImportResult implements Completable {
    
 // Generic import file column names
    public final String COL_NAME_LABEL = "COLUMN_NAMES:";
    
    public final String[] CUST_COLUMNS = {
        "ACCOUNT_NO",
        "CUST_ACTION",
        "LAST_NAME",
        "FIRST_NAME",
        "HOME_PHONE",
        "WORK_PHONE",
        "EMAIL",
        "STREET_ADDR1",
        "STREET_ADDR2",
        "CITY",
        "STATE",
        "COUNTY",
        "ZIP_CODE",
        "MAP_NO",
        "SUBSTATION",
        "FEEDER",
        "POLE",
        "TRFM_SIZE",
        "SERV_VOLT",
        "USERNAME",
        "PASSWORD",
        "USER_GROUP",
        "COMPANY_NAME",
        "IVR_PIN",
        "IVR_USERNAME",
        "CUST_ALT_TRACK_NO"
    };
    
    public final String[] HW_COLUMNS = {
        "ACCOUNT_NO",
        "HW_ACTION",
        "DEVICE_TYPE",
        "SERIAL_NO",
        "INSTALL_DATE",
        "REMOVE_DATE",
        "SERVICE_COMPANY",
        "PROGRAM_NAME",
        "ADDR_GROUP",
        "APP_TYPE",
        "APP_KW",
        "APP_RELAY_NUM",
        "MAC_ADDRESS",
        "DEVICE_VENDOR_USER_ID",
        "OPTION_PARAMS",
        "DEVICE_LABEL",
        "LATITUDE",
        "LONGITUDE"
    };
    
    // Column indices of the generic customer info file
    public final int COL_ACCOUNT_NO = 0;
    public final int COL_CUST_ACTION = 1;
    public final int COL_LAST_NAME = 2;
    public final int COL_FIRST_NAME = 3;
    public final int COL_HOME_PHONE = 4;
    public final int COL_WORK_PHONE = 5;
    public final int COL_EMAIL = 6;
    public final int COL_STREET_ADDR1 = 7;
    public final int COL_STREET_ADDR2 = 8;
    public final int COL_CITY = 9;
    public final int COL_STATE = 10;
    public final int COL_COUNTY = 11;
    public final int COL_ZIP_CODE = 12;
    public final int COL_MAP_NO = 13;
    public final int COL_SUBSTATION = 14;
    public final int COL_FEEDER = 15;
    public final int COL_POLE = 16;
    public final int COL_TRFM_SIZE = 17;
    public final int COL_SERV_VOLT = 18;
    public final int COL_USERNAME = 19;
    public final int COL_PASSWORD = 20;
    public final int COL_LOGIN_GROUP = 21;
    public final int COL_COMPANY_NAME = 22;
    public final int COL_IVR_PIN = 23;
    public final int COL_IVR_USERNAME = 24;
    public final int COL_CUST_ALT_TRACK_NO = 25;
    
    // Column indices of the generic hardware info file
    public final int COL_HW_ACCOUNT_NO = 0;
    public final int COL_HW_ACTION = 1;
    public final int COL_DEVICE_TYPE = 2;
    public final int COL_SERIAL_NO = 3;
    public final int COL_INSTALL_DATE = 4;
    public final int COL_REMOVE_DATE = 5;
    public final int COL_SERVICE_COMPANY = 6;
    public final int COL_PROGRAM_NAME = 7;
    public final int COL_ADDR_GROUP = 8;
    public final int COL_APP_TYPE = 9;
    public final int COL_APP_KW = 10;
    public final int COL_APP_RELAY_NUMBER = 11;
    public final int COL_MAC_ADDRESS = 12;
    public final int COL_DEVICE_VENDOR_USER_ID = 13;
    public final int COL_OPTION_PARAMS = 14;
    public final int COL_DEVICE_LABEL = 15;
    public final int COL_LATITUDE = 16;
    public final int COL_LONGITUDE = 17;
    
    
    public final String UNENROLL_CASE = "UNENROLL";
    
    private Instant startTime;
    private Instant stopTime;
    private int totalCount;
    private YukonEnergyCompany energyCompany = null;
    private String email = null;
    private boolean insertSpecified = false;
    private LiteYukonUser currentUser;
    private boolean automatedImport = false;
    private String position;
    private int numAcctTotal = 0;
    private int numAcctImported = 0;
    private int numHwTotal = 0;
    private int numHwImported = 0;
    private Set<String> accountsAdded = Sets.newHashSet();
    private Set<String> accountsUpdated = Sets.newHashSet();
    private Set<String> accountsRemoved = Sets.newHashSet();
    private Set<String> hardwareAdded = Sets.newHashSet();
    private Set<String> hardwareUpdated = Sets.newHashSet();
    private Set<String> hardwareRemoved = Sets.newHashSet();
    private Map<Integer, String[]> hwLines;
    private Map<Integer, String[]> custLines;
    private String resultId;
    private boolean prescan;
    private BulkFileUpload accountFileUpload;
    private BulkFileUpload hardwareFileUpload;
    private boolean complete = false;
    private boolean canceled = false;
    public int custFileErrors = 0;
    public int hwFileErrors = 0;
    private File custArchiveDir;
    private File hwArchiveDir;
    private File outputLogDir;
    private boolean isScheduled = false;

    public Instant getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    
    public Duration getDuration() {
        if (startTime != null && stopTime != null) {
            Duration duration = new Duration(startTime, stopTime);
            if (duration.isShorterThan(Duration.standardSeconds(1))) {
                return Duration.standardSeconds(1);
            }
            return duration;
        }

        return null;
    }
    
    public String getFileNames() {
        
        String accountFile = accountFileUpload.getName();
        String hardwareFile = hardwareFileUpload.getName();
        String names = "";
        
        if (StringUtils.isNotBlank(accountFile)) {
            names += accountFile;
        }
        if (StringUtils.isNotBlank(accountFile) && StringUtils.isNotBlank(hardwareFile)) {
            names += ", ";
        }
        if (StringUtils.isNotBlank(hardwareFile)) {
            names += hardwareFile;
        }
        
        return names;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public Instant getStopTime() {
        return stopTime;
    }
    
    public void setStopTime(Instant stopTime) {
        this.stopTime = stopTime;
    }

    public YukonEnergyCompany getEnergyCompany() {
        return energyCompany;
    }

    public void setEnergyCompany(YukonEnergyCompany energyCompany) {
        this.energyCompany = energyCompany;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isInsertSpecified() {
        return insertSpecified;
    }

    public void setInsertSpecified(boolean insertSpecified) {
        this.insertSpecified = insertSpecified;
    }

    public LiteYukonUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(LiteYukonUser currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isAutomatedImport() {
        return automatedImport;
    }

    public void setAutomatedImport(boolean automatedImport) {
        this.automatedImport = automatedImport;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getNumAcctTotal() {
        return numAcctTotal;
    }

    public void setNumAcctTotal(int numAcctTotal) {
        this.numAcctTotal = numAcctTotal;
    }

    public int getNumAcctImported() {
        return numAcctImported;
    }

    public void setNumAcctImported(int numAcctImported) {
        this.numAcctImported = numAcctImported;
    }

    public int getNumHwTotal() {
        return numHwTotal;
    }

    public void setNumHwTotal(int numHwTotal) {
        this.numHwTotal = numHwTotal;
    }

    public int getNumHwImported() {
        return numHwImported;
    }

    public void setNumHwImported(int numHwImported) {
        this.numHwImported = numHwImported;
    }

    public Map<Integer, String[]> getHwLines() {
        return hwLines;
    }

    public void setHwLines(Map<Integer, String[]> hwLines) {
        this.hwLines = hwLines;
    }

    public Map<Integer, String[]> getCustLines() {
        return custLines;
    }

    public void setCustLines(Map<Integer, String[]> custLines) {
        this.custLines = custLines;
    }
    
    public Collection<String[]> getErrorList() {
        Collection<String[]> errorList = new ArrayList<String[]>();
        if (getCustLines() != null) {
            errorList.addAll(cleanList(getCustLines().values()));
        }
        if (getHwLines() != null) {
            errorList.addAll(cleanList(getHwLines().values()));
        }
        return errorList;
    }
    
    private Collection<String[]> cleanList(final Collection<String[]> c) {
        List<String[]> list = new ArrayList<String[]>();
        for (java.util.Iterator<String[]> i = c.iterator(); i.hasNext();) {
            String[] value = i.next();
            if (value.length > 1 && value[1] != null) {
                list.add(value);
            }
        }
        return list;
    }

    public List<AccountImportError> getErrors() {
        List<AccountImportError> errors = Lists.newArrayList();
        
        Pattern pattern = Pattern.compile("^\\[line:\\s+(\\d+)\\s+error:\\s+(.+)\\]$");
        Object[] array = getErrorList().toArray();
        for (int x = 0; x < getErrorList().size(); x++) {
            if (array[x] instanceof String[]) {
                String[] value = (String[]) array[x];
                if (value.length > 1 && value[1] != null) {
                    Matcher m = pattern.matcher(value[1]);
                    if (m.matches()) {
                        AccountImportError error = new AccountImportError();
                        error.setLineNumber(Integer.parseInt(m.group(1)));
                        error.setErrorMessage(m.group(2));
                        error.setImportAccount(StringEscapeUtils.escapeHtml4(value[0]));
                        errors.add(error);
                    }
                }
            }
        }
        
        return errors;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    public int getCompleted() {
        return (hwLines == null ? 0 : hwLines.size() - 1) + (custLines == null ? 0 : custLines.size() - 1);
    }
    
    public boolean isCanceled() {
        return canceled;
    }
    
    public void cancel() {
        complete = true;
        canceled = true;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }
    
    public String getResultId() {
        return resultId;
    }

    public boolean hasErrors() {
        return !getErrorList().isEmpty();
    }

    public Set<String> getAccountsAdded() {
        return accountsAdded;
    }
    
    public Set<String> getAccountsUpdated() {
        return accountsUpdated;
    }
    
    public Set<String> getAccountsRemoved() {
        return accountsRemoved;
    }
    
    public Set<String> getHardwareAdded() {
        return hardwareAdded;
    }
    
    public Set<String> getHardwareUpdated() {
        return hardwareUpdated;
    }
    
    public Set<String> getHardwareRemoved() {
        return hardwareRemoved;
    }

    public void setPrescan(boolean prescan) {
        this.prescan = prescan;
    }
    
    public boolean isPrescan() {
        return prescan;
    }

    public File getCustomerFile() {
        return (accountFileUpload == null || accountFileUpload.hasErrors()) ? null : accountFileUpload.getFile();
    }
    
    public File getHardwareFile() {
        return (hardwareFileUpload == null || hardwareFileUpload.hasErrors()) ? null : hardwareFileUpload.getFile();
    }

    public void setAccountFileUpload(BulkFileUpload accountFileUpload) {
        this.accountFileUpload = accountFileUpload;
    }
    
    public void setHardwareFileUpload(BulkFileUpload hardwareFileUpload) {
        this.hardwareFileUpload = hardwareFileUpload;
    }

    public BulkFileUpload getAccountFileUpload() {
        return accountFileUpload;
    }
    
    public BulkFileUpload getHardwareFileUpload() {
        return hardwareFileUpload;
    }

    public String getPrescanStatus() {
        String status = "";
        if(complete) {
            if(hasErrors()) {
                status += "finishedFailed";
            } else {
                status +="finishedPassed";
            }
        } else {
            status = "prescanning";
        }
        return status;
    }
    
    public String getPrescanStatusColor() {
        String status = "";
        if (complete) {
            if (canceled || hasErrors()) {
                return "error";
            }
            return "success";
        }
        
        return status;
    }
    
    public int getImportErrors() {
        return custFileErrors + hwFileErrors;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public File getOutputLogDir() {
        return outputLogDir;
    }

    public void setOutputLogDir(File outputLogDir) {
        this.outputLogDir = outputLogDir;
    }

    public File getCustArchiveDir() {
        return custArchiveDir;
    }

    public void setCustArchiveDir(File custArchiveDir) {
        this.custArchiveDir = custArchiveDir;
    }

    public File getHwArchiveDir() {
        return hwArchiveDir;
    }

    public void setHwArchiveDir(File hwArchiveDir) {
        this.hwArchiveDir = hwArchiveDir;
    }

    public int getHwSuccessCount() {
        return hwLines.size() - hwFileErrors - 1;
    }

    public int getCustSuccessCount() {
        return custLines.size() - custFileErrors - 1;
    }

}