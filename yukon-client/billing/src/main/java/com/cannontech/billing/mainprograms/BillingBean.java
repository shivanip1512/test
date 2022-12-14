package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * 
 * @author:
 */
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.SimpleBillingFormat;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class BillingBean implements Observer {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private GlobalSettingDao globalSettingDao = YukonSpringHook.getBean(GlobalSettingDao.class);
    private BillingFile billingFile = null;
    private BillingFileDefaults billingFileDefaults = null;

    private int fileFormat = FileFormatTypes.INVALID;
    private int demandDaysPrev = -1;
    private int energyDaysPrev = -1;
    private List<String> billGroup = Collections.singletonList("/Meters");
    private String outputFile = null;
    private String inputFile = null;
    private Boolean removeMult = null;
    private Boolean appendToFile = null;
    private Date endDate = null;
    private String errorMsg = null;

    public void generateFile(OutputStream out) throws IOException {
        // Gather new billing defaults and write them to the properties file.
        // FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
        BillingFileDefaults defaults =
            new BillingFileDefaults(getFileFormat(), getDemandDaysPrev(), getEnergyDaysPrev(), getBillGroup(),
                getOutputFile(), getRemoveMult(), getInputFile(), getEndDate(), getAppendToFile());

        defaults.setLiteYukonUser(getLiteYukonUser());
        setBillingFormatter(defaults);

        if (getSimpleBillingFormat() != null) {
            Date timerStart = new Date();
            CTILogger.info("Started " + FileFormatTypes.getFormatType(getBillingDefaults().getFormatID())
                + " format at: " + timerStart);

            // start our DB thread
            getBillingFile().encodeOutput(out);
        } else {
            CTILogger.info(getBillingDefaults().getFormatID() + " unrecognized file format id");
        }
    }

    private BillingFile getBillingFile() {
        if (billingFile == null) {
            billingFile = new BillingFile();
        }
        return billingFile;
    }

    public BillingFileDefaults getBillingDefaults() {
        if (this.billingFileDefaults == null) {
            this.billingFileDefaults = new BillingFileDefaults();
        }
        return billingFileDefaults;
    }

    public int getFileFormat() {
        if (fileFormat == FileFormatTypes.INVALID) {
            String format = globalSettingDao.getString(GlobalSettingType.DEFAULT_BILLING_FORMAT);
            fileFormat = FileFormatTypes.getFormatID(format);
        }
        return fileFormat;
    }

    public void setFileFormat(int newFileFormat) {
        if (fileFormat != newFileFormat) {
            fileFormat = newFileFormat;
            getBillingDefaults().setFormatID(fileFormat);
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            endDate = getBillingDefaults().getEndDate();
        }
        CTILogger.info(" Getting End Date! " + endDate);
        return endDate;
    }

    public void setEndDate(Date newEndDate) {
        if (endDate == null || endDate.compareTo(newEndDate) != 0) {
            endDate = newEndDate;
            CTILogger.info("Changing End Date from: " + endDate + " to: " + newEndDate);
            getBillingDefaults().setEndDate(newEndDate);
        }
    }

    public void setEndDateStr(String newEndDateStr) {
        try {
            setEndDate(dateFormat.parse(newEndDateStr));
        } catch (ParseException e) {
            CTILogger.error(e);
        }
    }

    public int getDemandDaysPrev() {
        if (demandDaysPrev < 0) {
            demandDaysPrev = globalSettingDao.getInteger(GlobalSettingType.DEMAND_DAYS_PREVIOUS);
        }
        return demandDaysPrev;
    }

    /**
     * Returns the demandStartDate.
     * The minimum date (according to getDemandDaysPrev() and endDate) for valid demand readings.
     */
    public Date getDemandStartDate() {
        return getBillingDefaults().getDemandStartDate();
    }

    public void setDemandDaysPrev(int newDemandDaysPrev) {
        demandDaysPrev = newDemandDaysPrev;
        getBillingDefaults().setDemandDaysPrev(demandDaysPrev);
    }

    public int getEnergyDaysPrev() {
        if (energyDaysPrev < 0) {
            energyDaysPrev = globalSettingDao.getInteger(GlobalSettingType.ENERGY_DAYS_PREVIOUS);
        }
        return energyDaysPrev;
    }

    public void setEnergyDaysPrev(int newEnergyDaysPrev) {
        energyDaysPrev = newEnergyDaysPrev;
        getBillingDefaults().setEnergyDaysPrev(energyDaysPrev);
    }

    public boolean getAppendToFile() {
        if (appendToFile == null) {
            appendToFile = globalSettingDao.getBoolean(GlobalSettingType.APPEND_TO_FILE);
        }
        return appendToFile.booleanValue();
    }

    public void setAppendToFile(boolean isAppendToFile) {
        appendToFile = Boolean.valueOf(isAppendToFile);
    }

    public boolean getRemoveMult() {
        if (removeMult == null) {
            removeMult = globalSettingDao.getBoolean(GlobalSettingType.REMOVE_MULTIPLIER);
        }
        return removeMult.booleanValue();
    }

    public void setRemoveMult(boolean isRemoveMult) {
        removeMult = Boolean.valueOf(isRemoveMult);
    }

    public List<String> getBillGroup() {
        return billGroup;
    }

    public void setBillGroup(List<String> billGroup) {
        this.billGroup = billGroup;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String newOutputFile) {
        outputFile = newOutputFile;

        getBillingDefaults().setOutputFileDir(outputFile);
    }

    /**
     * Returns the SimpleBillingFormat object.
     * 
     * @return SimpleBillingFormat
     */
    public SimpleBillingFormat getSimpleBillingFormat() {
        return getBillingFile().getSimpleBillingFormat();
    }

    private void setBillingFormatter(BillingFileDefaults billingFileDefaults) {
        getBillingFile().setBillingFormatter(billingFileDefaults);
    }

    @Override
    public synchronized void update(Observable obs, Object data) {
        if (obs instanceof BillingFile) {
            CTILogger.info("Done with Billing File Format.");

            BillingFile src = (BillingFile) obs;
            src.deleteObserver(this);
        }
    }

    public LiteYukonUser getLiteYukonUser() {
        return getBillingDefaults().getLiteYukonUser();
    }

    public void setLiteYukonUser(LiteYukonUser liteYukonUser) {
        getBillingDefaults().setLiteYukonUser(liteYukonUser);
    }

    public String getInputFile() {
        if (inputFile == null) {
            inputFile = globalSettingDao.getString(GlobalSettingType.INPUT_FILE);
        }
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}