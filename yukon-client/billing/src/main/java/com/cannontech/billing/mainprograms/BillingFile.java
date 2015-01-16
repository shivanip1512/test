package com.cannontech.billing.mainprograms;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.SimpleBillingFormat;
import com.cannontech.billing.SimpleBillingFormatFactory;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.impl.AuthenticationServiceImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class BillingFile extends java.util.Observable implements Runnable {

    private SimpleBillingFormat simpleBillingFormat;

    /**
     * Takes args in format of key=value for batch running.
     */
    public static void main(String[] args) {
        try {
            System.setProperty("cti.app.name", "BillingFile");
            CTILogger.info("BillingFile starting...");
            YukonSpringHook.setDefaultContext("com.cannontech.context.billing");

            char argDel = '=';
            BillingFile billingFile = new BillingFile();
            BillingFileDefaults billingFileDefaults = new BillingFileDefaults();
            billingFileDefaults.setEndDate(ServletUtil.getToday());

            String username = null;
            String password = null;
            for (int i = 0; i < args.length; i++) {
                // first loop through, verify the char '=' is our delimiter, else try ':'
                if (i == 0) {
                    if (args[i].toLowerCase().indexOf(argDel) < 0) {
                        argDel = ':';
                    }
                }

                String arg = args[i];
                String argLowerCase = arg.toLowerCase();

                // Check the delimiter of '=', if not found check ':'
                int startIndex = argLowerCase.indexOf('=');
                if (startIndex < 0) {
                    startIndex = argLowerCase.indexOf(':');
                }
                startIndex += 1;

                if (argLowerCase.startsWith("format") || argLowerCase.startsWith("type")) {
                    String subString = arg.substring(startIndex);
                    if (subString.length() > 2) {
                        // must be the string value.
                        billingFileDefaults.setFormatID(FileFormatTypes.getFormatID(subString));
                    } else {
                        billingFileDefaults.setFormatID(Integer.valueOf(subString).intValue());
                    }
                } else if (argLowerCase.startsWith("dem")) {
                    String subString = arg.substring(startIndex);
                    billingFileDefaults.setDemandDaysPrev(Integer.valueOf(subString).intValue());
                } else if (argLowerCase.startsWith("ene")) {
                    String subString = arg.substring(startIndex);
                    billingFileDefaults.setEnergyDaysPrev(Integer.valueOf(subString).intValue());
                } else if (argLowerCase.startsWith("coll") || argLowerCase.startsWith("group")) {
                    String group;
                    String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.COLLECTIONGROUP.getGroup(subString);
                    }
                    billingFileDefaults.setDeviceGroups(Collections.singletonList(group));
                } else if (argLowerCase.startsWith("test") || argLowerCase.startsWith("alt")) {
                    String group;
                    String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.TESTCOLLECTIONGROUP.getGroup(subString);
                    }
                    billingFileDefaults.setDeviceGroups(Collections.singletonList(group));
                } else if (argLowerCase.startsWith("bill")) {
                    String group;
                    String subString = args[i].substring(startIndex);
                    if (subString.startsWith("/")) {
                        group = subString;
                    } else {
                        group = FixedDeviceGroups.BILLINGGROUP.getGroup(subString);
                    }
                    billingFileDefaults.setDeviceGroups(Collections.singletonList(group));
                } else if (argLowerCase.startsWith("end")) {
                    String subString = arg.substring(startIndex);
                    com.cannontech.util.ServletUtil.parseDateStringLiberally(subString);
                    billingFileDefaults.setEndDate(ServletUtil.parseDateStringLiberally(subString));
                } else if (argLowerCase.startsWith("file") || argLowerCase.startsWith("dir")) {
                    String subString = arg.substring(startIndex);
                    if (subString.indexOf(':') > 0) {
                        billingFileDefaults.setOutputFileDir(subString);
                    } else if (subString.indexOf("\\") > -1 || subString.indexOf("//") > -1) {
                        // to use a UNC
                        // name
                        billingFileDefaults.setOutputFileDir(subString);
                    } else {
                        // try to help out and default the directory
                        billingFileDefaults.setOutputFileDir(CtiUtilities.getExportDirPath() + subString);
                    }
                } else if (argLowerCase.startsWith("mult")) {
                    String subString = arg.substring(startIndex);
                    if (subString.startsWith("t")) {
                        billingFileDefaults.setRemoveMultiplier(true);
                    } else {
                        billingFileDefaults.setRemoveMultiplier(false);
                    }
                } else if (argLowerCase.startsWith("app")) {
                    String subString = arg.substring(startIndex);
                    if (subString.startsWith("t")) {
                        billingFileDefaults.setAppendToFile(true);
                    } else {
                        billingFileDefaults.setAppendToFile(false);
                    }
                } else if (argLowerCase.startsWith("user")) {
                    String subString = arg.substring(startIndex);
                    username = subString;
                } else if (argLowerCase.startsWith("pass")) {
                    String subString = arg.substring(startIndex);
                    password = subString;
                } else if (argLowerCase.startsWith("token")) {
                    if (startIndex == 0) {
                        // not key=value pair, it's just key. the value used here is not important...today
                        // anyways!
                        billingFileDefaults.setToken("blah");
                    } else { // this option doesn't make a lot of sense today (since the value of token is
                             // never read), but it paves the path for tomorrow...maybe?!
                        String token = arg.substring(startIndex);
                        billingFileDefaults.setToken(token);
                    }
                }
            }

            LiteYukonUser liteYukonUser = billingFile.getLiteYukonUser(username, password);
            billingFileDefaults.setLiteYukonUser(liteYukonUser);
            billingFile.setBillingFormatter(billingFileDefaults);
            billingFile.run();
            billingFile.simpleBillingFormat.getBillingFileDefaults().writeDefaultsFile();
        } catch (Throwable exception) {
            CTILogger.error(exception);
        }

        YukonSpringHook.shutdownContext();
    }

    /**
     * Retrieve all billingData and writeTofile for a formatBase.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            writeToFile();
        } catch (java.io.IOException ioe) {
            setChanged();
            CTILogger.error(ioe);
            notify("Unsuccessful reading of file : " + simpleBillingFormat.getBillingFileDefaults().getOutputFileDir());
        }
    }

    /**
     * Writes the record string to the output file.
     */
    private void writeToFile() throws java.io.IOException {
        FileOutputStream out =
            new FileOutputStream(simpleBillingFormat.getBillingFileDefaults().getOutputFileDir(),
                simpleBillingFormat.getBillingFileDefaults().isAppendToFile());
        try {
            encodeOutput(out);
        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * Method encodeOutput.
     * Retrieve all billingData and write to outputStream for a formatBase.
     * 
     * @param out
     * @throws IOException
     */
    public void encodeOutput(java.io.OutputStream out) throws IOException {
        CTILogger.info("Valid entries are for meter data where: ");
        CTILogger.info("  DEMAND readings > " + simpleBillingFormat.getBillingFileDefaults().getDemandStartDate()
            + " AND <= " + simpleBillingFormat.getBillingFileDefaults().getEndDate());
        CTILogger.info("  ENERGY readings > " + simpleBillingFormat.getBillingFileDefaults().getEnergyStartDate()
            + " AND <= " + simpleBillingFormat.getBillingFileDefaults().getEndDate());

        try {
            boolean success = simpleBillingFormat.writeToFile(out);
            if (!success) {
                notify("Unsuccessful database query");
            } else {
                setChanged();
                if (out instanceof FileOutputStream) {
                    notify("Successfully created the file : "
                    + simpleBillingFormat.getBillingFileDefaults().getOutputFileDir() + "\n"
                    + simpleBillingFormat.getReadingCount() + " Valid Readings Reported.");
                } else {
                    CTILogger.info(simpleBillingFormat.getReadingCount() + " Valid Readings Reported.");
                }
            }

        } catch (IllegalArgumentException e) {
            CTILogger.error(e);
            notify(e.getMessage());
        } catch (IOException e) {
            CTILogger.error(e);
            notify(e.getMessage());
        }
    }

    /**
     * Notify observers and write to CTILogger the notifyString.
     * 
     * @param notifyString
     */
    private void notify(String notifyString) {
        this.notifyObservers(notifyString);
        CTILogger.info(notifyString);
    }

    /**
     * Sets the simpleBillingFormat object using the billingFileDefaults (including formatId)
     * Sets the billingDefaults will be populated for the format object being used.
     * 
     * @param formatID
     */
    public void setBillingFormatter(BillingFileDefaults billingFileDefaults) {
        int formatId = billingFileDefaults.getFormatID();
        simpleBillingFormat = SimpleBillingFormatFactory.createFileFormat(formatId);

        simpleBillingFormat.setBillingFileDefaults(billingFileDefaults);
    }

    public SimpleBillingFormat getSimpleBillingFormat() {
        return simpleBillingFormat;
    }

    private LiteYukonUser getLiteYukonUser(String username, String password) throws PasswordExpiredException {
        LiteYukonUser liteYukonUser = null;
        if (username != null && password != null) {
            try {
                AuthenticationService authenticationService =
                    YukonSpringHook.getBean("authenticationService", AuthenticationServiceImpl.class);
                liteYukonUser = authenticationService.login(username, password);

            } catch (BadAuthenticationException e) {
                CTILogger.info("Username/Password not supplied, using system authentication");
                CTILogger.info(e);
                liteYukonUser = YukonUserContext.system.getYukonUser();
            }
        }
        return liteYukonUser;
    }
}
