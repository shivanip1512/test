package com.cannontech.dr.nest.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Address;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestFileType;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public class NestSimulatorServiceImpl implements NestSimulatorService {

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AccountService accountService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    public static final String SIMULATED_FILE_PATH = CtiUtilities.getNestDirPath() + System.getProperty("file.separator") + "Simulator";
    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";

    @Override
    public String generateExistingFile(List<String> groupNames, int rows, int maxSerialNumbers, boolean isWinterProgram, LiteYukonUser user)
            throws Exception {

        List<CustomerAccount> accounts = customerAccountDao.getAll().stream()
                .filter(account -> account.getAccountNumber() != null && !account.getAccountNumber().equals("(none)"))
                .collect(Collectors.toList());
        List<AccountDto> accountsWithAddresses = new ArrayList<>();
        for (CustomerAccount account : accounts) {
            AccountDto dto = accountService.getAccountDto(account.getAccountNumber(), user);
            if (dto != null && !Strings.isEmpty(dto.getStreetAddress().getLocationAddress1())
                && !Strings.isEmpty(dto.getLastName())) {
                accountsWithAddresses.add(dto);
            }
            if (accountsWithAddresses.size() == rows) {
                break;
            }
        }
        
        List<NestExisting> existing = accountsWithAddresses.stream()
                .map(account -> getExistingRow(account, maxSerialNumbers, isWinterProgram, groupNames, user))
                .sorted((f1, f2) -> compareDates(f1.getContractApproved(), f2.getContractApproved()))
                .collect(Collectors.toList());
            
        addStaticNestInfo(existing, groupNames);

        return writeToAFile(existing);
    }
    
    private int compareDates(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format.parse(date2).compareTo(format.parse(date1));
        } catch (ParseException e) {
           return 0;
        }
    }

    private String writeToAFile(List<NestExisting> existing) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
        String fileName = "Nest_existing_users_generated_" + formatter.format(new Date()) + ".csv";
        File directory = new File(SIMULATED_FILE_PATH);
        if (!directory.exists()){
            directory.mkdir();
        }
        File file = new File(SIMULATED_FILE_PATH, fileName);
        ObjectWriter writer =
            new CsvMapper().writerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema().withHeader());
        try {
            writer.writeValues(file).writeAll(existing);
        } catch (IOException e) {
            throw e;
        }
        return fileName;
    }

    /**
     * Nest file has a static info that needs to be appended to the end of the file.
     */
    private void addStaticNestInfo(List<NestExisting> existing, List<String> groupNames) {
        existing.add(new NestExisting("***", "***", "***", "***", "***", "***", "***", "***", "***", "***", "***",
            "***", "***", "***", "***", "***", "***", "***", "***", "***"));
        if (groupNames.size() == 1) {
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Y",
                getGroupName(groupNames, 0), "Y", "CUSTOMER_UNENROLLED"));
        } else if (groupNames.size() == 2) {
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Y",
                getGroupName(groupNames, 0), "Y", "CUSTOMER_UNENROLLED"));
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "N",
                getGroupName(groupNames, 1), "N", "CUSTOMER_MOVED"));
        } else if (groupNames.size() == 3) {
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Y",
                getGroupName(groupNames, 0), "Y", "CUSTOMER_UNENROLLED"));
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "N",
                getGroupName(groupNames, 1), "N", "CUSTOMER_MOVED"));
            existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                getGroupName(groupNames, 2), "", "OTHER"));
        }
    }

    /**
     * Returns the group name or an empty string
     */
    private String getGroupName(List<String> groupNames, int index) {
        String groupName = groupNames.get(index);
        return groupName == null ? "" : groupName;
    }

    /**
     * Generates and returns Nest Reference number
     */
    public String getNestRef() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array);
        return Hex.encodeHexString(generatedString.getBytes()).substring(0, 8) + "-79d2-4a98-9fc9-f6112a39f9b2";
    }

    /**
     * Returns a random group name from the list
     */
    private String getRandomGroup(List<String> groupNames) {
        Random random = new Random();
        return groupNames.get(random.nextInt(groupNames.size()));
    }

    /**
     * Returns up to X(maxSerialNumbers) comma delimited serial numbers
     */
    public String getSerialNumbers(int maxSerialNumbers) {
        double randomMax = (int) (Math.random() * ((maxSerialNumbers - 1) + 1)) + 1;
        int i = 0;
        List<String> numbers = new ArrayList<>();
        while (i < randomMax) {
            byte[] array = new byte[16];
            new Random().nextBytes(array);
            numbers.add(Hex.encodeHexString(new String(array).getBytes()).substring(0, 16).toUpperCase());
            i++;
        }
        return String.join(",", numbers);
    }

    /**
     * Returns data for 1 row in the file
     */
    private NestExisting getExistingRow(AccountDto account, int maxSerialNumbers, boolean isWinterProgram,
            List<String> groupNames, LiteYukonUser user) {

        DateTime start = new DateTime();
        DateTime end = start.minusYears(1);

        long random = ThreadLocalRandom.current().nextLong(end.getMillis(), start.getMillis());
        DateTime date = new DateTime(random);

        String winterRewards = isWinterProgram ? getSerialNumbers(maxSerialNumbers) : "";
        String summerRewards = isWinterProgram ? "" : getSerialNumbers(maxSerialNumbers);
        String programs = isWinterProgram ? "WINTER_RHR" : "SUMMER_RHR";

        Address addr = account.getStreetAddress();

        NestExisting existing =
            new NestExisting(getNestRef(), String.valueOf(date.getYear()), String.valueOf(date.getMonthOfYear()),
                String.valueOf(date.getDayOfMonth()), account.getFirstName() + " " + account.getLastName(), "******",
                addr.getLocationAddress1(), addr.getCityName(), addr.getStateCode(), addr.getZipCode(),
                account.getAccountNumber(), date.toString(DATE_FORMAT), programs, winterRewards, summerRewards, "",
                getRandomGroup(groupNames), "", "", "");

        return existing;
    }
    
    @Override
    public void saveSettings(String fileName) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.NEST_FILE_NAME, fileName);
    }
    
    @Override
    public String getStringValue(YukonSimulatorSettingsKey key) {
        return yukonSimulatorSettingsDao.getStringValue(key);
    }
}
