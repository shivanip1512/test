package com.cannontech.dr.nest.service.impl;

import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_ACCOUNT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_ADDRESS;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_GROUP_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.ENROLLED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_NEST;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_GROUP_ONLY_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MANUALLY_DELETE_GROUP_FROM_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MANUALLY_DELETE_THERMOSTAT_FROM_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_ENROLLED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_ACCOUNT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_ADDRESS;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_AREA_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_PROGRAM_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_FOUND_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.NOT_NEST_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CHANGE_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.ACCOUNT_NUMBER;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.PROGRAM;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.SERIAL_NUMBER;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.GROUP_FROM;
import static com.cannontech.dr.nest.model.NestSyncType.AUTO;
import static com.cannontech.dr.nest.model.NestSyncType.MANUAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.model.Address;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.service.impl.AccountServiceImpl;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.model.EnrollmentEnum;
import com.cannontech.stars.dr.enrollment.model.EnrollmentHelper;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.ws.LmDeviceDto;
import com.cannontech.stars.ws.StarsControllableDeviceHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class NestSyncServiceImpl implements NestSyncService{

    public static final int MINUTES_TO_WAIT_BEFORE_NEXT_SYNC = 15;
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private DemandResponseDao demandResponseDao;
    @Autowired private StarsControllableDeviceHelper starsControllableDeviceHelper;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired DbChangeManager dbChangeManager;
    
    @Autowired private IDatabaseCache dbCache;
    private boolean syncInProgress = false;
    public static final String EMPTY_ROW = "***";
    private static final Logger log = YukonLogManager.getLogger(NestSyncServiceImpl.class);
    @Autowired private NestDao nestDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private AccountService accountService;
    @Autowired private AddressDao addressDao;
    private EnergyCompany HARDCODED_EC;
    private LiteYukonUser HARDCODED_OPERATOR;
       
    /**
     * Collection of data from the Nest file that is deemed unacceptable by Yukon and should be ignored when
     * processing the data received from Nest.
     * 
     * Example: Thermostat "A" is in the Nest file. Yukon already has thermostat "A" as a thermostat of the non Nest type.
     * When processing the data we will skip "A", the thermostat will not be created or enrolled.
     * The Key is there for debugging purposes only, so we can print the values and know why "A" is blacklisted.
     * 
     * For all the blacklisted data user will see a discrepancy he will need to fix.
     */
    private class Blacklist {
        Map<String, NestSyncI18nKey> groups = new HashMap<>();
        Map<String, NestSyncI18nKey> thermostats = new HashMap<>();
        
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
        }
        
        boolean isEmpty() {
            return groups.isEmpty() && thermostats.isEmpty();
        }
    }

    @Override
    public void sync(boolean forceSync) {
        
        List<EnergyCompany> energyCompanies = Lists.newArrayList(energyCompanyDao.getAllEnergyCompanies());
        energyCompanies.removeIf(e -> e.getId() == -1);
        HARDCODED_EC = energyCompanies.get(0);
        List<Integer> ids = energyCompanyDao.getOperatorUserIds(HARDCODED_EC);
        HARDCODED_OPERATOR = yukonUserDao.getLiteYukonUser(ids.get(0));
        
        if(!runSync(forceSync)) {
            return;
        }
        
        List<YukonListEntry> yukonListEntry = yukonListDao.getYukonListEntry(
            YukonDefinition.DEV_TYPE_NEST_THERMOSTAT.getDefinitionId(), HARDCODED_EC);
        if(yukonListEntry.isEmpty()) {
            throw new NestException("Device type 'Nest thermostat' has not been created.");
        }
        
        syncInProgress = true;
        log.info("Nest sync started");
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        NestSync sync = new NestSync();
        nestDao.saveSyncInfo(sync);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        existing.removeIf(row -> Strings.isEmpty(row.getAccountNumber()) || row.getAccountNumber().equals(EMPTY_ROW));
        
        if (!existing.isEmpty()) {
           List<String> groupsInNest = parseGroupsFromTheNestFile(existing); 
           log.debug("Poccessing {} rows", existing.size());
           Blacklist ignore = new Blacklist();
           syncGroups(groupsInNest, sync.getId(), ignore);
           validateProgramAndAreaSetup(groupsInNest, sync.getId(), ignore);
           syncAccounts(existing, groupsInNest, sync.getId(), ignore);
        }
        sync.setStopTime(new Instant());
        nestDao.saveSyncInfo(sync);
        
        syncInProgress = false;
        log.info("Nest sync finished");
    }
    
    /**
     * Attempts to make sure the Nest account are in sync with Yukon by creating accounts, thermostats, groups.
     */
    private void syncAccounts(List<NestExisting> existing, List<String> groups, int syncId, Blacklist ignore) {
        log.debug("Syncing {} accounts", existing.size());
        Set<String> thermostatsInNest = existing.stream()
                .map(this::getThemostatsForAccountInNest)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Map<String, Thermostat> thermostatsInYukon = inventoryDao.getThermostatsBySerialNumbers(HARDCODED_EC, thermostatsInNest)
                .stream()
                .filter(thermostat -> thermostat.getType().isNest())
                .collect(Collectors.toMap(Thermostat::getSerialNumber, thermostat -> thermostat));
        
        nestDao.saveSyncDetails(validateThermostats(thermostatsInNest, thermostatsInYukon, syncId, ignore));
       
        Set<String> thermostatsToCreate = thermostatsInNest.stream()
                .filter(thermostat -> !thermostatsInYukon.containsKey(thermostat))
                .collect(Collectors.toSet());
        if(!thermostatsToCreate.isEmpty()) {
            log.debug("New thermostats to create {}", thermostatsToCreate);
        }
        
        Map<String, NestExisting> nestAccounts = existing.stream()
                .collect(Collectors.toMap(NestExisting::getAccountNumber, row -> row));
        Map<String, CustomerAccount> yukonAccounts = customerAccountDao.getCustomerAccountsByAccountNumbers(nestAccounts.keySet())
                .stream()
                .collect(Collectors.toMap(CustomerAccount::getAccountNumber, account -> account));
        
        syncUserAccounts(syncId, nestAccounts, yukonAccounts);
        syncAddresses(syncId, nestAccounts, yukonAccounts);
        syncThermostatsAndEnrollments(existing, groups, syncId, ignore, thermostatsToCreate, nestAccounts, thermostatsInYukon);        
    }

    /**
     * Creates user account in Yukon if account from Nest doesn't exist
     */
    private void syncUserAccounts(int syncId, Map<String, NestExisting> nestAccounts,
            Map<String, CustomerAccount> yukonAccounts) {
        nestDao.saveSyncDetails(nestAccounts.keySet().stream()
                .filter(account -> !yukonAccounts.containsKey(account))
                .map(accountNumber -> nestAccounts.get(accountNumber))
                .map(row -> createAccount(row, syncId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    /**
     * For all users in Nest checks if the address in Yukon is Empty if it is copies it from Nest
     */
    private void syncAddresses(int syncId, Map<String, NestExisting> nestAccounts,
            Map<String, CustomerAccount> yukonAccounts) {
        //add address in Yukon if the account in Nest has an address and Yukon doesn't
        nestDao.saveSyncDetails(yukonAccounts.values().stream().filter(account-> account.getAccountSiteId() == 0)
            .map(account -> createAddress(nestAccounts.get(account.getAccountNumber()), syncId))
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    }

    /**
     * Syncs thermostats and enrollment for all accounts in Nest
     */
    private void syncThermostatsAndEnrollments(List<NestExisting> existing, List<String> groups, int syncId,
            Blacklist ignore, Set<String> thermostatsToCreate, Map<String, NestExisting> nestAccounts, Map<String, Thermostat> thermostatsInYukon) {
        Map<String, LiteYukonPAObject> yukonGroups = getGroupPaos(groups);
        log.debug("Groups in Yukon {}", yukonGroups.keySet());
        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms =
            demandResponseDao.getGroupsToPrograms(Lists.newArrayList(yukonGroups.values()));
    
        if(!ignore.isEmpty()) {
            log.debug("Data to ignore when syncing {}", ignore);
        }
        Map<String, CustomerAccount> accounts = customerAccountDao.getCustomerAccountsByAccountNumbers(nestAccounts.keySet())
                .stream()
                .collect(Collectors.toMap(CustomerAccount::getAccountNumber, account -> account));
        existing.forEach(row -> {
            if (!ignore.groups.containsKey(row.getGroup())) {
                CustomerAccount account = accounts.get(row.getAccountNumber());
                LiteYukonPAObject group = yukonGroups.get(row.getGroup());
                //account or group might be null only if exception happened during creation of account or group, the exception is logged in WS log
                if(account != null && group != null) {
                    PaoIdentifier programIdent = groupsToPrograms.get(group.getPaoIdentifier()).iterator().next();
                    LiteYukonPAObject programPao = dbCache.getAllLMPrograms().stream()
                            .filter(program -> program.getPaoIdentifier().getPaoId() == programIdent.getPaoId())
                            .findFirst().get();
                    
                    syncThermostatAndEnrollment(syncId, ignore, thermostatsToCreate, row, account, group, programPao, thermostatsInYukon);
                }
            } else { 
              //this group is in the black list
              log.debug("Ignoring group {} reason {}", row.getGroup(), ignore.groups.get(row.getGroup()));
            }
        });
    }

    /**
     * Syncs thermostats and enrollment for user account
     */
    private void syncThermostatAndEnrollment(int syncId, Blacklist ignore, Set<String> thermostatsToCreate,
            NestExisting row, CustomerAccount account, LiteYukonPAObject group, LiteYukonPAObject programPao, Map<String, Thermostat> thermostatsInYukon) {
        List<NestSyncDetail> details = getThemostatsForAccountInNest(row).stream()
            .filter(thermostat -> {
                boolean isBlacklisted = ignore.thermostats.containsKey(thermostat);
                if(isBlacklisted) {
                    //this thermostat is in the black list
                    log.debug("Unable to enroll thermostat {} account {} reason {}.",
                        thermostat, row.getAccountNumber(),ignore.thermostats.get(thermostat));
                }
                return !isBlacklisted;
            })
            .map(thermostat -> {
                try {
                    List<NestSyncDetail> thermostatDetails = new ArrayList<>();
                    int invenoryId;
                    //create thermostat
                    if(thermostatsToCreate.contains(thermostat)) {
                        invenoryId = createThermostat(row.getAccountNumber(), thermostat, syncId, thermostatDetails);                    
                    } else {
                        //get inventory id for the existing thermostat
                        invenoryId = thermostatsInYukon.get(thermostat).getId();
                    }
                    //enroll thermostat
                    thermostatDetails.add(enrollThermostat(syncId, group.getPaoName(), programPao.getPaoName(), account, thermostat, invenoryId));   
                    return thermostatDetails;      
                } catch (Exception e) {
                    //continue processing the next row if enrollment failed.
                    log.error("Failed to create or enroll thermostat:" + thermostat + "data:" + row, e);
                    return null;
                }
          
            })
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        nestDao.saveSyncDetails(details);
    }

    /**
     * If address in Yukon is missing creates the address
     */
    private NestSyncDetail createAddress(NestExisting row, int syncId) {
        LiteAddress liteAddress = new LiteAddress();
        AccountServiceImpl.setAddressFieldsFromDTO(new LiteAddress(),
            new Address(row.getAddress(), "", row.getCity(), row.getState(), row.getZipCode(), ""));
        try {
            addressDao.add(liteAddress);
            NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_FOUND_ADDRESS, AUTO_CREATED_ADDRESS);
            detail.addValue(ACCOUNT_NUMBER, row.getAccountNumber());
            return detail;
        } catch (Exception e) {
            log.error("Unable to ");
            return null;
        }
    }
    
    /**
     * Creates account
     */
    private NestSyncDetail createAccount(NestExisting row, int syncId) {
        AccountDto account = new AccountDto();
        account.setAccountNumber(row.getAccountNumber());
        try {
            String[] name = row.getName().split(" ");
            account.setFirstName(name[0]);
            account.setLastName(name[name.length - 1]);
        } catch (Exception e) {
            log.error("Unable to split name {} into first and last name", row.getName());
        }
        account.setEmailAddress(row.getEmail());
        Address address = new Address(row.getAddress(), "", row.getCity(), row.getState(), row.getZipCode(), "");
        account.setStreetAddress(address);
        account.setBillingAddress(address);
        account.setIsCommercial(false);
        account.setIsCustAtHome(true);
        account.setAccountNotes("Account created by Nest sync");
        UpdatableAccount updatableAccount = new UpdatableAccount();
        updatableAccount.setAccountDto(account);
        updatableAccount.setAccountNumber(row.getAccountNumber());
        try {
            int accountId = accountService.addAccount(updatableAccount, HARDCODED_OPERATOR);
            log.debug("Created account for account number {} account id {}", row.getAccountNumber(), accountId);
        } catch (Exception e) {
           log.error("Unable to create a new account for account number " + row.getAccountNumber() + " row:" + row, e);
           return null;
        }
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_FOUND_ACCOUNT, AUTO_CREATED_ACCOUNT);
            detail.addValue(ACCOUNT_NUMBER, row.getAccountNumber());
        return detail;
    }
    
    /**
     * Enrolls thermostats in a program for account
     * @param thermostatInYukon 
     */
    private NestSyncDetail enrollThermostat(int syncId, String group, String program, CustomerAccount account,
            String serialNumber, int thermostatInventoryId) {
        String groupEnrolledIn = findGroupCurrentlyEnrolledIn(account.getAccountId(), thermostatInventoryId);

        // thermostat is not enrolled
        if (groupEnrolledIn == null) {
            log.debug(
                "Thermostat serial number {} account {} is not enrolled. Enrolling thermostat in program {} group {}.",
                serialNumber, account.getAccountNumber(), program, group);
            return enrollThermostat(syncId, group, program, account.getAccountNumber(), serialNumber);
        // thermostat is enrolled in a different group
        } else if (!groupEnrolledIn.equals(group)) {
            log.debug(
                "Thermostat serial number {} account {} is enrolled in group {}. Enrolling thermostat in program {} group {}.",
                serialNumber, account.getAccountNumber(), groupEnrolledIn, program, group);
            return changeGroup(syncId, group, program, account.getAccountNumber(), serialNumber, groupEnrolledIn);
        } else {
            log.debug("Thermostat serial number {} account {} is enrolled in group {}. Enrollment is in sync.",
                serialNumber, account.getAccountNumber(), groupEnrolledIn);
            return null;
        }
    }

    private NestSyncDetail enrollThermostat(int syncId, String group, String program, String accountNumber,
            String serialNumber) {
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper(accountNumber, group, program, serialNumber);
        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, HARDCODED_OPERATOR);
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_ENROLLED_THERMOSTAT, ENROLLED_THERMOSTAT);
            detail.addValue(ACCOUNT_NUMBER, accountNumber);
            detail.addValue(SERIAL_NUMBER, serialNumber);
            detail.addValue(PROGRAM, program);
            detail.addValue(GROUP, group);
        return detail;
    }
    
    private NestSyncDetail changeGroup(int syncId, String group, String program, String accountNumber,
            String serialNumber, String groupEnrolledIn) {
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper(accountNumber, group, program, serialNumber);
        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, HARDCODED_OPERATOR);
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, AUTO_CHANGE_GROUP, ENROLLED_THERMOSTAT);
            detail.addValue(ACCOUNT_NUMBER, accountNumber);
            detail.addValue(SERIAL_NUMBER, serialNumber);
            detail.addValue(PROGRAM, program);
            detail.addValue(GROUP, group);
            detail.addValue(GROUP_FROM, groupEnrolledIn);
        return detail;
    }
    
    /**
     * All nest thermostats for account must be enrolled in one group. This method returns that group or null
     * if the account is not enrolled.
     */
    private String findGroupCurrentlyEnrolledIn(int accountId, int inventoryId) {

        ProgramEnrollment enrollment = enrollmentDao.getActiveEnrollmentsByAccountId(accountId).stream()
                .filter(theromstat -> theromstat.getInventoryId()==inventoryId)
                .findFirst()
                .orElse(null);
        if(enrollment == null) {
            return null;
        }
        String groupName = dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST && enrollment.getLmGroupId() == group.getLiteID())
                .findFirst()
                .map(LiteYukonPAObject::getPaoName)
                .orElse(null);
        return groupName;
    }
    
    /**
     * Creates thermostat. Returns inventory id.
     */
    private int createThermostat(String accountNumber, String thermostat, int syncId, List<NestSyncDetail> details) {
        log.debug("Creating thermostat {} and adding it to account {}", thermostat, accountNumber);
        LmDeviceDto deviceInfo = new LmDeviceDto();
        deviceInfo.setAccountNumber(accountNumber);
        deviceInfo.setSerialNumber(thermostat);

        List<YukonListEntry> yukonListEntry =
            yukonListDao.getYukonListEntry(YukonDefinition.DEV_TYPE_NEST_THERMOSTAT.getDefinitionId(), HARDCODED_EC);
        String typeStr = yukonListEntry.iterator().next().getEntryText();
        deviceInfo.setDeviceType(typeStr);
        LiteInventoryBase inventory = starsControllableDeviceHelper.addDeviceToAccount(deviceInfo, HARDCODED_OPERATOR);
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_FOUND_THERMOSTAT, AUTO_CREATED_THERMOSTAT);
        detail.addValue(ACCOUNT_NUMBER, accountNumber);
        detail.addValue(SERIAL_NUMBER, thermostat);
        details.add(detail);
        return inventory.getInventoryID();
    }
        
    /**
     * Checks if Nest groups have areas and programs are setup correctly, if the setup is not correct creates a discrepancy.
     */
    private void validateProgramAndAreaSetup(List<String> groupsInNest, int syncId, Blacklist ignore) {
        List<LiteYukonPAObject> nestGroups = Lists.newArrayList(getGroupPaos(groupsInNest).values());

        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms = demandResponseDao.getGroupsToPrograms(nestGroups);
        Multimap<PaoIdentifier, PaoIdentifier> programsToAreas = demandResponseDao.getProgramsToAreas(groupsToPrograms.values().stream()
            .distinct()
            .collect(Collectors.toList()));
        List<NestSyncDetail> details = validateProgramAndAreaSetup(syncId, nestGroups, groupsToPrograms, programsToAreas, ignore);
        nestDao.saveSyncDetails(details);
    }
    
    private Map<String, LiteYukonPAObject> getGroupPaos(List<String> groupsInNest){
        return dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST && groupsInNest.contains(group.getPaoName()))
                .collect(Collectors.toMap(LiteYukonPAObject::getPaoName, group -> group));
    }
    
    
    ///////////////////Unit test
    /**
     * Returns set of thermostats to for an account in Nest
     */
    private Set<String> getThemostatsForAccountInNest(NestExisting row){
        Set<String> newThermostats = new HashSet<>();
        if(!Strings.isEmpty(row.getSummerRhr())){
            newThermostats.addAll(Arrays.asList(row.getSummerRhr().split(",")));
        }
        if(!Strings.isEmpty(row.getWinterRhr())){
            newThermostats.addAll(Arrays.asList(row.getWinterRhr().split(",")));
        }
        return newThermostats.stream()
                .map(String::trim)
                .collect(Collectors.toSet());
    }
    
    ///////////////////Unit test
    /**
     * Returns true if the program has an area
     */
    private boolean hasArea(Collection<PaoIdentifier> programs,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas) {
        for (PaoIdentifier programId : programs) {
            if (!programsToAreas.get(programId).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    ///////////////////Unit test
    /**
     * Checks if Nest groups have areas and programs are setup correctly
     */
    private List<NestSyncDetail> validateProgramAndAreaSetup(int syncId, List<LiteYukonPAObject> nestGroups,
            Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas, Blacklist ignore) {
        
        return nestGroups.stream()
                         .map(group -> buildSyncDetail(syncId, group, groupsToPrograms, programsToAreas, ignore))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toList());
    }
       
    ///////////////////Unit test
    /**
     * Builds a NestSyncDetail, or returns an empty Optional if the setup is correct.
     */
    private Optional<NestSyncDetail> buildSyncDetail(int syncId, LiteYukonPAObject group,
            Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms,
            Multimap<PaoIdentifier, PaoIdentifier> programsToAreas, Blacklist ignore) {

        Collection<PaoIdentifier> programs = groupsToPrograms.get(group.getPaoIdentifier());
        NestSyncDetail detail = null;
        if (programs.isEmpty()) {
            log.debug("Nest group {} doesn't have a program setup", group.getPaoName());
            detail = new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_PROGRAM_FOR_NEST_GROUP,
                SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP);
            detail.addValue(GROUP, group.getPaoName());
            ignore.groups.put(group.getPaoName(), NOT_FOUND_PROGRAM_FOR_NEST_GROUP);
        } else if (!hasArea(programs, programsToAreas)) {
            log.debug("Nest group {} doesn't have an area setup", group.getPaoName());
            detail = new NestSyncDetail(0, syncId, MANUAL, NOT_FOUND_AREA_FOR_NEST_GROUP,
                SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP);
            detail.addValue(GROUP, group.getPaoName());
            ignore.groups.put(group.getPaoName(), NOT_FOUND_AREA_FOR_NEST_GROUP);
        }
        return Optional.ofNullable(detail);
    }
    
    ///////////////////Unit test
    /**
     * Returns list of discrepancies if the thermostat in Nest file corresponds to a non Nest thermostat in Yukon
     */
    private List<NestSyncDetail> validateThermostats(Set<String> thermostatsInNest, Map<String, Thermostat> thermostatsInYukon,
            int syncId, Blacklist ignore) {
        return thermostatsInNest.stream()
                .filter(thermostatInNest -> {
                    if(thermostatsInYukon.get(thermostatInNest) != null && !thermostatsInYukon.get(thermostatInNest).getType().isNest()) {
                        ignore.thermostats.put(thermostatInNest, NOT_NEST_THERMOSTAT);
                        return true;
                    }
                    return false;
                })
                .map(thermostatInNest -> {
                    NestSyncDetail detail =
                        new NestSyncDetail(0, syncId, MANUAL, NOT_NEST_THERMOSTAT, MANUALLY_DELETE_THERMOSTAT_FROM_YUKON);
                    detail.addValue(SERIAL_NUMBER, thermostatInNest);
                    return detail;
                }).collect(Collectors.toList());
    }
    
    ///////////////////Unit test
    /**
     * If group in Nest file has the same name as group in Yukon that is not a Nest group, mark as
     * discrepancy.
     * 
     * @return list of sync details
     */
    private List<NestSyncDetail> filterNestGroups(List<String> groupsInNest, int syncId, Blacklist ignore) {
        List<String> nonNestGroupsWithNestGroupName = dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() != PaoType.LM_GROUP_NEST && groupsInNest.contains(group.getPaoName()))
                .map(group -> group.getPaoName())
                .collect(Collectors.toList());
        if(!nonNestGroupsWithNestGroupName.isEmpty()) {
            log.debug("Groups in Yukon that is not Nest that have the same name as Group in Nest {}", nonNestGroupsWithNestGroupName);
            return nonNestGroupsWithNestGroupName.stream().map(group -> {
                ignore.groups.put(group, FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME);
                NestSyncDetail detail = new NestSyncDetail(0, syncId, MANUAL, FOUND_NON_NEST_GROUP_IN_YUKON_WITH_THE_NEST_GROUP_NAME,
                        MODIFY_YUKON_GROUP_NOT_TO_CONFLICT_WITH_NEST_GROUP);
                detail.addValue(GROUP, group);
                return detail;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    /**
     * Returns true if sync can run.
     */
    private boolean runSync(boolean forceSync) {
        if(forceSync) {
            return true;
        }
        if(syncInProgress) {
            return false;
        }
        Instant lastSyncTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME);
        
        return TimeUtil.isXMinutesBeforeNow(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC, lastSyncTime);
    }
    
    private void syncGroups(List<String> groupsInNest, int syncId, Blacklist ignore) {
        log.debug("Syncing Nest {} groups",  groupsInNest);
        List<String> nestGroupsInYukon = dbCache.getAllLMGroups().stream()
                .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST)
                .map(group -> group.getPaoName())
                .collect(Collectors.toList());
        List<String> groupsOnlyInYukon = new ArrayList<>(nestGroupsInYukon);
        groupsOnlyInYukon.removeAll(groupsInNest);
        List<NestSyncDetail> discrepancies = groupsOnlyInYukon.stream().map(group -> {
            NestSyncDetail detail = new NestSyncDetail(0, syncId, MANUAL, FOUND_GROUP_ONLY_IN_YUKON, MANUALLY_DELETE_GROUP_FROM_YUKON);
            detail.addValue(GROUP, group);
            return detail;
        }).collect(Collectors.toList());
        if(!discrepancies.isEmpty()) {
            log.debug("Found Nest groups in Yukon that is not in Nest {}", groupsOnlyInYukon);
            nestDao.saveSyncDetails(discrepancies);  
        }
        
        nestDao.saveSyncDetails(filterNestGroups(groupsInNest, syncId, ignore));
        
        List<String> groupsToCreate = new ArrayList<>(groupsInNest);
        groupsToCreate.removeAll(nestGroupsInYukon);
        if(!groupsToCreate.isEmpty()) {
            nestDao.saveSyncDetails(createGroups(groupsToCreate, syncId));
        }
    }
    
    /**
     * Returns groups in the Nest file.
     */
    private List<String> parseGroupsFromTheNestFile(List<NestExisting> existing) {
        List<String> groupsInNest = existing.stream()
                .filter(row -> Strings.isNotEmpty(row.getGroup()) && !row.getGroup().equals(EMPTY_ROW))
                .map(row -> row.getGroup())
                .distinct()
                .collect(Collectors.toList());
        return groupsInNest;
    }
    
    /**
     * Creates Nest Group
     */
    private List<NestSyncDetail> createGroups(List<String> groups, int syncId) {
        log.debug("Creating Nest Groups in Yukon {}", groups);
        List<NestSyncDetail> details = new ArrayList<>();
        groups.forEach(group -> {
            YukonPAObject pao = LMFactory.createLoadManagement(PaoType.LM_GROUP_NEST);
            pao.setPAOName(group);
            dbPersistentDao.performDBChange(pao, TransactionType.INSERT);
            NestSyncDetail detail =
                new NestSyncDetail(0, syncId, AUTO, FOUND_GROUP_ONLY_IN_NEST, AUTO_CREATED_GROUP_IN_YUKON);
            detail.addValue(GROUP, group);
            details.add(detail);
        });
        return details;
    }

    @Override
    public NestSyncTimeInfo getSyncTimeInfo() {
        NestSyncTimeInfo info = new NestSyncTimeInfo();
        info.setSyncTime(persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME));
        info.setSyncInProgress(syncInProgress);
        if(info.getNextSyncTime() != null) {
            info.setNextSyncTime(info.getNextSyncTime().plus(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC));
        }
        return info;
    }
}
