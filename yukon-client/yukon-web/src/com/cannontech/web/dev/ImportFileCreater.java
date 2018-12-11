package com.cannontech.web.dev;

import java.io.IOException;
import java.io.Writer;

import com.google.common.base.Joiner;

public class ImportFileCreater {

    public static void writeHardwareFile(Writer writer, long numAccounts, int startAccountNum, int numHardware,
            long startSerialNum, String programName, String loadGroup, String appCat, String[] deviceTypes)
            throws IOException {

        writeCsvLine(writer, "ACCOUNT_NO", "HW_Action", "Device_Type", "Serial_No", "Install_Date", "Remove_Date",
            "Service_Company", "Program_Name", "Addr_Group", "App_Type", "App_kW", "App_Relay_Num", "MAC_Address",
            "DeviceVendorUserId", "Option_Params", "LATITUDE", "LONGITUDE");

        int serialNumberOffset = 0;
        for (int i = 0; i < numAccounts; i++) {
            int accountNum = startAccountNum + i;
            for (int h = 0; h < numHardware; h++) {
                long serialNumber = startSerialNum + h + serialNumberOffset;
                // Cycles through the provided device types
                String deviceType = deviceTypes[(int) (serialNumber % deviceTypes.length)];
                newHwLine(writer, accountNum, deviceType, serialNumber, programName, loadGroup, appCat);
            }
            serialNumberOffset += numHardware;
        }
        writer.flush();
    }

    public static void writeAccountFile(Writer writer, long numAccounts, int startAccountNum, String userGroup)
            throws IOException {
        RandomAccountInfoGenerator randomInfo = new RandomAccountInfoGenerator();

        writeCsvLine(writer, "ACCOUNT_NO", "Cust_Action", "FIRST_NAME", "LAST_NAME", "HOME_PHONE", "WORK_PHONE",
            "EMAIL", "STREET_ADDR1", "STREET_ADDR2", "CITY", "STATE", "ZIP_CODE", "Username", "Password", "User_Group", "Cust_Alt_Track_No");

        for (int i = 0; i < numAccounts; i++) {
            int accountNum = startAccountNum + i;
            newCustLine(writer, accountNum, userGroup, randomInfo);
        }
        writer.flush();
    }

    private static void newHwLine(Writer writer, int accNum, String deviceType, long serialNum, String programName,
            String loadGroup, String appCat) throws IOException {
        writeCsvLine(writer, Integer.toString(accNum), "INSERT", deviceType, Long.toString(serialNum), "6/21/2010",
            "", "", programName, loadGroup, appCat, "", "1", "", "", "", "", "");
    }

    private static void newCustLine(Writer writer, int accNum, String userGroup, RandomAccountInfoGenerator randomInfo)
            throws IOException {
        String[] cityStateZip = randomInfo.cityStateZip();
        writeCsvLine(writer, Integer.toString(accNum), "INSERT", randomInfo.firstName(), randomInfo.lastName(),
            randomInfo.phoneNum(), randomInfo.phoneNum(), randomInfo.email(), randomInfo.streetAddr(),
            randomInfo.streetAddr2(), cityStateZip[0], cityStateZip[1], cityStateZip[2], "", "", userGroup);
    }

    private static void writeCsvLine(Writer writer, String ... fields) throws IOException {
        writer.write(Joiner.on(',').join(fields) + "\n");
    }
}
