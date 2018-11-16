package com.cannontech.dr.nest.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Address;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.simulator.NestFileType;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSimulatorService;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public class NestSimulatorServiceImpl implements NestSimulatorService {

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AccountService accountService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private NestCommunicationService nestCommunicationService;
    private HttpHost host;
    private GlobalSettingDao settingDao;
    private Proxy proxy;

    public static final String SIMULATED_FILE_PATH = CtiUtilities.getNestDirPath() + System.getProperty("file.separator") + "Simulator";
    private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";
    public String fileGenerated = "Generated";
    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class); 

    public NestSimulatorServiceImpl(GlobalSettingDao settingDao) {
        this.settingDao = settingDao;
        proxy = YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpProxy)
                .orElse(null);
        host = YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpHost)
                .orElse(null);
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

        File file = createFile(SIMULATED_FILE_PATH, fileGenerated);
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
    
    @Override
    public List<NestExisting> downloadExisting() {
        log.debug("Downloading Nest existing file");
        InputStream inputStream = getFileInputStream(NestFileType.EXISTING);
        List<NestExisting> existing = parseExistingCsvFile(inputStream);
        log.debug("Download of the Nest file complete");
        return existing; 
    }
        
    private InputStream getFileInputStream(NestFileType type) {
        InputStream inputStream = null;
        String nestUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
        String stringUrl = nestUrl + "/v1/users/current/latest.csv";
        log.debug("Nest Url:"+stringUrl);
        // curl https://enterprise-api.nest.com/api/v1/users/pending/latest.csv -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA=="
        try {
            URLConnection connection =
                    nestCommunicationService.useProxy(stringUrl) ? new URL(stringUrl).openConnection(proxy) : new URL(stringUrl).openConnection();
            connection.setRequestProperty("Authorization", nestCommunicationService.encodeAuthorization());
            inputStream = connection.getInputStream();
        } catch (NestException | IOException e) {
            log.error("Error connecting to "+stringUrl, e);
            throw new NestException("Error connecting to ", e);
        }
        return inputStream;
    }
    
    
    private List<NestExisting> parseExistingCsvFile(InputStream inputStream) {
        List<NestExisting> existing = new ArrayList<>();
        if (inputStream != null) {
            try {
                MappingIterator<NestExisting> it =
                    new CsvMapper().readerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema()).readValues(inputStream);
                existing.addAll(it.readAll());
            } catch (IOException e) {
                throw new NestException("Unable to parse exising file ", e);
            }
        }
        log.debug(existing);
        return existing;
    }
    
    /**
     * Example of error response from Nest if group doesn't exist.
     * {"num_group_changes":0,"num_dissolved":0,"errors":[{"row_number":2,"header":["REF","YEAR","MONTH","DAY","NAME","EMAIL","SERVICE_ADDRESS","SERVICE_CITY","SERVICE_STATE","SERVICE_POSTAL_CODE","ACCOUNT_NUMBER","CONTRACT_APPROVED","PROGRAMS","WINTER_RHR","SUMMER_RHR","ASSIGN_GROUP","GROUP","DISSOLVE","DISSOLVE_REASON","DISSOLVE_NOTES"],"fields":["a5a1305e-79d2-4a98-9fc9-f6112a39f9b2","2018","8","21","Marina Feldman","******","ATRIA Corporate Center1","Plymouth","MN","55441","1","2018-08-20 11:32:06","SUMMER_RHR","","09AA01AC35170N2N","Y","A","","",""],"errors":["invalid value for GROUP: 'A'"]}]}
     * @return 
     */
    @Override
    public void uploadExisting(List<NestExisting> existing) {
        log.debug("Uploading file {}", existing);
        File uploadFile = null;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String requestUrl =
                settingDao.getString(GlobalSettingType.NEST_SERVER_URL) + NestFileType.EXISTING.getUrl();
            HttpPost httppost = new HttpPost(requestUrl);
            httppost.addHeader("content-type", "text/csv");
            httppost.addHeader("Accept", "application/json");
            httppost.setHeader("Authorization", nestCommunicationService.encodeAuthorization());
            if (host != null && nestCommunicationService.useProxy(requestUrl)) {
                RequestConfig requestConfig = RequestConfig.custom().setProxy(host).build();
                httppost.setConfig(requestConfig);
            }

            uploadFile = createFile(CtiUtilities.getNestDirPath(), "upload");
            ObjectWriter writer =
                new CsvMapper().writerFor(NestExisting.class).with(NestFileType.EXISTING.getSchema().withHeader());
            writer.writeValues(uploadFile).writeAll(existing).close();
            httppost.setEntity(new FileEntity(uploadFile));

            log.debug(EntityUtils.toString(httppost.getEntity()));
            log.debug("executing request {} {}", httppost.getRequestLine(), httppost.getConfig());
            HttpResponse response = httpclient.execute(httppost);
            org.apache.http.HttpEntity resEntity = response.getEntity();
            log.debug(response.getStatusLine());
            if (resEntity != null) {
                String str = EntityUtils.toString(resEntity);
                log.debug(str);
            }
        } catch (IOException e) {
            throw new NestException("Error uploading existing file to Nest", e);
        } finally {
            if (uploadFile != null) {
                uploadFile.delete();
            }
        }
    }
    
    /**
     * Returns the row of the account found
     * 
     * @throws NestException if account is not found
     */
    
    private NestExisting getRowToModifyAndRemoveAllOtherAccounts(List<NestExisting> existing, String accountNumber) {
        return existing.stream()
                .filter(row -> Strings.isNotBlank(row.getAccountNumber()) && row.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new NestException("Account " + accountNumber + " is not found in the Nest file"));
    }
    
    @Override
    public Optional<String> updateGroup(String accountNumber, String newGroup) {
        log.info("Changing group for accountNumber {} to new group {}", accountNumber, newGroup);
        List<NestExisting> existing = downloadExisting();
        NestExisting row = getRowToModifyAndRemoveAllOtherAccounts(existing, accountNumber);
        log.debug("Existing group {}", row.getGroup());
        row.setAssignGroup("Y");
        row.setGroup(newGroup);
        uploadExisting(existing);
        //modify simulator to return error after we get Nest error description
        return Optional.empty();
    }
    
    @Override
    public Optional<String> dissolveAccountWithNest(String accountNumber) {
        log.info("Dissolving account with Nest {}", accountNumber);
        List<NestExisting> existing = downloadExisting();
        NestExisting row = getRowToModifyAndRemoveAllOtherAccounts(existing, accountNumber);
        row.setDissolve("Y");
        //modify simulator to return error after we get Nest error description
        return Optional.empty();
    }
    
    public static final SimpleDateFormat FILE_NAME_DATE_FORMATTER = new SimpleDateFormat("YYYYMMddHHmm");

    private File createFile(String path, String name) {
        String fileName = FILE_NAME_DATE_FORMATTER.format(new Date()) + "_" + name + ".csv";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new NestException("Failed to create file:" + fileName, e);
            }
        }
        return file;
    }
}
