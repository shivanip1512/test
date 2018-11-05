package com.cannontech.dr.nest.service.impl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Address;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestFileType;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.service.NestCommunicationService;
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
    @Autowired private NestCommunicationService nestCommunicationService;
    
    public static final String SIMULATED_FILE_PATH = CtiUtilities.getNestDirPath() + System.getProperty("file.separator") + "Simulator";
    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";
    public String fileGenerated = "Generated";

    @Override
    public NestUploadInfo upload(InputStream uploadedStream){
        
        NestUploadInfo info = null;
        
        String path = NestSimulatorServiceImpl.SIMULATED_FILE_PATH;
        String fileName = getFileName(YukonSimulatorSettingsKey.NEST_FILE_NAME);
        File file = new File(path, fileName);
        InputStream existingStream;
        try {
            existingStream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new NestException("Unable to read file " + path + fileName);
        }
        List<NestExisting> existing = nestCommunicationService.parseExistingCsvFile(existingStream);
        List<NestExisting> uploaded = nestCommunicationService.parseExistingCsvFile(uploadedStream);
        
        try {
            existingStream.close();
            FileDeleteStrategy.FORCE.delete(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        NestExisting uploadedRecord = uploaded.stream()
                .filter(row -> Strings.isNotBlank(row.getAccountNumber()))
                .findFirst().get();
        
        if(!Strings.isEmpty(uploadedRecord.getDissolve()) && uploadedRecord.getDissolve().equals("Y")) {
            existing.removeIf(row -> !Strings.isEmpty(row.getAccountNumber()) && row.getAccountNumber().equals(uploadedRecord.getAccountNumber()));
            info = new NestUploadInfo(0, 1, new ArrayList<>());
        }
       
       if(!Strings.isEmpty(uploadedRecord.getAssignGroup()) && uploadedRecord.getAssignGroup().equals("Y")) {
            int index = IntStream.range(0, existing.size())
                    .filter(i-> existing.get(i).getAccountNumber().equals(uploadedRecord.getAccountNumber()))
                    .findFirst()
                    .getAsInt();
            existing.remove(index);
            uploadedRecord.setAssignGroup(null);
            existing.add(index, uploadedRecord);
            info = new NestUploadInfo(0, 1, new ArrayList<>());
        }
        try {
            FileDeleteStrategy.FORCE.delete(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        writeToAFile(new File(path, fileName), existing);
        return info;
    }
    
    @Override
    public String generateExistingFile(List<String> groupNames, int rows, int maxSerialNumbers, boolean isWinterProgram, LiteYukonUser user){

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

        File file = NestCommunicationService.createFile(SIMULATED_FILE_PATH, fileGenerated);
        return writeToAFile(file, existing);
    }
    
    private int compareDates(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format.parse(date2).compareTo(format.parse(date1));
        } catch (ParseException e) {
           return 0;
        }
    }

    private String writeToAFile(File file, List<NestExisting> existing) {
        ObjectWriter writer =
            new CsvMapper().writerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema().withHeader());
        try {
            writer.writeValues(file).writeAll(existing).close();
        } catch (IOException e) {
            new NestException("Error writing to a file", e);
        }
        return file.getName();
    }

    /**
     * Nest file has a static info that needs to be appended to the end of the file.
     */
    private void addStaticNestInfo(List<NestExisting> existing, List<String> groupNames) {
        existing.add(new NestExisting("***", "***", "***", "***", "***", "***", "***", "***", "***", "***", "***",
            "***", "***", "***", "***", "***", "***", "***", "***", "***"));
        existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "Y",
            getGroupName(groupNames, 0), "Y", "CUSTOMER_UNENROLLED", ""));
        existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "N",
            getGroupName(groupNames, 1), "N", "CUSTOMER_MOVED", ""));
        existing.add(new NestExisting("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            getGroupName(groupNames, 2), "", "OTHER", ""));
    }

    /**
     * Returns the group name or an empty string
     */
    private String getGroupName(List<String> groupNames, int index) {
        try {
            return groupNames.get(index);
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
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
    public void saveFileName(String fileName) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.NEST_FILE_NAME, fileName);
    }

    @Override
    public String getFileName(YukonSimulatorSettingsKey key) {
        return yukonSimulatorSettingsDao.getStringValue(key);
    }
    
    @Override
    public void saveNestVersion(Integer versionNumber) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.NEST_VERSION, versionNumber);
    }
    
    
    @Override
    public Integer getNestVersion(YukonSimulatorSettingsKey key) {
        return yukonSimulatorSettingsDao.getIntegerValue(key);
    }
}
