package com.cannontech.dr.nest.service.impl;

import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_ACCOUNT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_ADDRESS;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_GROUP_IN_YUKON;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_CREATED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_DELETED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.AUTO_ENROLLED_THERMOSTAT;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.CHANGE_GROUP;
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
import static com.cannontech.dr.nest.model.NestSyncI18nKey.SETUP_PROGRAM_AND_AREA_CORRECTLY_FOR_NEST_GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.THERMOSTAT_IN_ACCOUNT_WHICH_IS_NOT_IN_NEST;
import static com.cannontech.dr.nest.model.NestSyncI18nKey.THERMOSTAT_IN_WRONG_ACCOUNT;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.ACCOUNT_NUMBER;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.GROUP;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.GROUP_FROM;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.PROGRAM;
import static com.cannontech.dr.nest.model.NestSyncI18nValue.SERIAL_NUMBER;
import static com.cannontech.dr.nest.model.NestSyncType.AUTO;
import static com.cannontech.dr.nest.model.NestSyncType.MANUAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.model.Address;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DemandResponseDao;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.model.v3.CustomerInfo;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
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
import com.cannontech.stars.dr.hardware.service.HardwareService;
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
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private AccountService accountService;
    @Autowired private AddressDao addressDao;
    @Autowired private HardwareService hardwareService;
    @Autowired private CustomerDao customerDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    private EnergyCompany energyCompany;
       
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
    protected static class Blacklist {
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
        
        initDefaultEnergyCompany();
        
        if (energyCompany == null || !runSync(forceSync)) {
            return;
        }
        
        List<YukonListEntry> yukonListEntry = yukonListDao.getYukonListEntry(
            YukonDefinition.DEV_TYPE_NEST_THERMOSTAT.getDefinitionId(), energyCompany);
        if(yukonListEntry.isEmpty()) {
            throw new NestException("Device type 'Nest thermostat' has not been created.");
        }
        
        syncInProgress = true;
        log.info("Nest sync started");
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        NestSync sync = new NestSync();
        nestDao.saveSyncInfo(sync);
        List<CustomerInfo> existing = nestCommunicationService.retrieveCustomers(EnrollmentState.ACCEPTED);
        
        if (!existing.isEmpty()) {
           List<String> groupsInNest = parseGroups(existing); 
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
     * Finds default Energy Company for Sync service to use
     */
    private void initDefaultEnergyCompany() {
        if (energyCompany == null) {
            try {
                energyCompany = energyCompanyDao.getDefaultEnergyCompanyForThirdPartyApiOrSystemUsage();
            } catch (EnergyCompanyNotFoundException e) {
                log.error("Unable to load EnergyCompany", e);
            }
        }
    }
    
    /**
     * Attempts to make sure the Nest account are in sync with Yukon by creating accounts, thermostats, groups.
     */
    private void syncAccounts(List<CustomerInfo> existing, List<String> groups, int syncId, Blacklist ignore) {
        log.debug("Syncing {} accounts", existing.size());
        Set<String> thermostatsInNest = existing.stream()
                .map(CustomerInfo::getDeviceIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Map<String, Thermostat> thermostatsInYukon = getThermostatsInYukon(thermostatsInNest);
        
        nestDao.saveSyncDetails(validateThermostats(thermostatsInNest, thermostatsInYukon, syncId, ignore));
        
        Map<String, CustomerInfo> nestAccounts = existing.stream()
                .collect(Collectors.toMap(CustomerInfo::getAccountNumber, row -> row));
        List<NestSyncDetail> details = deleteThermostatsAssociatedWithAccountNotInNest(syncId, nestAccounts.keySet());
        if(!details.isEmpty()) {
            nestDao.saveSyncDetails(details);
            //we deleted some thermostats refresh the list
            thermostatsInYukon = getThermostatsInYukon(thermostatsInNest);
        }
        
        Map<String, CustomerAccount> yukonAccounts = getCustomerAccounts(nestAccounts);
        syncUserAccounts(syncId, nestAccounts, yukonAccounts);
        //refresh all accounts as we might have created new accounts in the method above
        yukonAccounts = getCustomerAccounts(nestAccounts);
        updateAltTrackingNumberWithNestCustomerId(nestAccounts, yukonAccounts);
        syncAddresses(syncId, nestAccounts, yukonAccounts);
        syncThermostatsAndEnrollments(existing, groups, syncId, ignore, nestAccounts, thermostatsInYukon, yukonAccounts);        
    }

    private void updateAltTrackingNumberWithNestCustomerId(Map<String, CustomerInfo> nestAccounts,
            Map<String, CustomerAccount> yukonAccounts) {
        List<Integer> customerIds = yukonAccounts.values().stream()
            .map(CustomerAccount::getCustomerId)
            .collect(Collectors.toList());
        Map<Integer, LiteCustomer> customers = customerDao.getCustomersWithEmptyAltTrackNum(customerIds);
        for (String accountNumber : yukonAccounts.keySet()) {
            CustomerAccount account = yukonAccounts.get(accountNumber);
            LiteCustomer customer = customers.get(account.getCustomerId());
            //customer has empty alt tracking number
            if (customer != null) {
                CustomerInfo nestAccount = nestAccounts.get(accountNumber);
                // updating alt tracking number with Nest account number
                customer.setAltTrackingNumber(nestAccount.getCustomerId());
                log.debug("Updating AltTrackingNumber to {} for account {} customer id {}",
                    nestAccount.getCustomerId(), nestAccount.getCustomerId(), customer.getCustomerID());
                customerDao.updateCustomer(customer);
            }
        }
    }

    /**
     * Returns customer accounts in Yukon that match the accounts in Nest
     */
    private Map<String, CustomerAccount> getCustomerAccounts(Map<String, CustomerInfo> nestAccounts) {
        return customerAccountDao.getCustomerAccountsByAccountNumbers(nestAccounts.keySet(), energyCompany.getId())
                .stream()
                .collect(Collectors.toMap(CustomerAccount::getAccountNumber, account -> account));
    }

    /**
     * Returns the list of thermostats that are in Nest and Yukon
     */
    private Map<String, Thermostat> getThermostatsInYukon(Set<String> thermostatsInNest) {
        Map<String, Thermostat> thermostatsInYukon = inventoryDao.getThermostatsBySerialNumbers(energyCompany, thermostatsInNest)
                .stream()
                .filter(thermostat -> thermostat.getType().isNest())
                .collect(Collectors.toMap(Thermostat::getSerialNumber, thermostat -> thermostat));
        return thermostatsInYukon;
    }

    /**
     * If there is an account in Yukon that has Nest thermostats and this account is in not in Nest file, the
     * thermostats will be deleted.
     */
    private List<NestSyncDetail> deleteThermostatsAssociatedWithAccountNotInNest(int syncId, Set<String> accountsInNest) {
        return inventoryDao.getNestThermostatsNotInListedAccounts(energyCompany, accountsInNest).stream()
                .map(thermostat -> {
                    CustomerAccount accountForThermostat = customerAccountDao.getAccountByInventoryId(thermostat.getId());
                    String accountNumber = "(none)";
                    if(accountForThermostat.getAccountId() > 0) {
                        accountNumber = accountForThermostat.getAccountNumber();
                    }
                    log.debug("Attempting to delete thermostat {} from account {} as the account is not in nest file",
                            thermostat.getSerialNumber(), accountNumber);
                    return deleteThermostat(syncId, accountNumber, thermostat.getId(), thermostat.getSerialNumber(),
                            THERMOSTAT_IN_ACCOUNT_WHICH_IS_NOT_IN_NEST);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Creates user account in Yukon if account from Nest doesn't exist
     */
    private void syncUserAccounts(int syncId, Map<String, CustomerInfo> nestAccounts,
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
    private void syncAddresses(int syncId, Map<String, CustomerInfo> nestAccounts,
            Map<String, CustomerAccount> yukonAccounts) {
        Set<Integer> siteIds = yukonAccounts.values().stream()
                .map(CustomerAccount::getAccountSiteId)
                .collect(Collectors.toSet());
        Map<Integer, LiteAddress> emptyAddresses = addressDao.getEmptyAddresses(siteIds);
        
        //add address in Yukon if the account in Nest has an address and Yukon doesn't
        nestDao.saveSyncDetails(yukonAccounts.values().stream().filter(account -> emptyAddresses.containsKey(account.getAccountSiteId()))
        .map(account ->{
            CustomerInfo row = nestAccounts.get(account.getAccountNumber());
            LiteAddress emptyAddress = emptyAddresses.get(account.getAccountSiteId());
            //in database if a customer has no address
            //there is an entry with all fields empty or send to (none), we found that row and will update if with data from Nest
            return updateAddress(row , syncId, emptyAddress);
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
    }

    /**
     * Syncs thermostats and enrollment for all accounts in Nest
     */
    private void syncThermostatsAndEnrollments(List<CustomerInfo> existing, List<String> groups, int syncId,
            Blacklist ignore, Map<String, CustomerInfo> nestAccounts, Map<String, Thermostat> thermostatsInYukon,
            Map<String, CustomerAccount> accounts) {
        Map<String, LiteYukonPAObject> yukonGroups = getGroupPaos(groups);
        log.debug("Groups in Yukon {}", yukonGroups.keySet());
        Multimap<PaoIdentifier, PaoIdentifier> groupsToPrograms =
            demandResponseDao.getGroupsToPrograms(Lists.newArrayList(yukonGroups.values()));
    
        if(!ignore.isEmpty()) {
            log.debug("Data to ignore when syncing {}", ignore);
        }
        existing.forEach(row -> {
            if (!ignore.groups.containsKey(row.getGroupId())) {
                CustomerAccount account = accounts.get(row.getAccountNumber());
                LiteYukonPAObject group = yukonGroups.get(row.getGroupId());
                //account or group might be null only if exception happened during creation of account or group, the exception is logged in WS log
                if(account != null && group != null) {
                    PaoIdentifier programIdent = groupsToPrograms.get(group.getPaoIdentifier()).iterator().next();
                    LiteYukonPAObject programPao = dbCache.getAllLMPrograms().stream()
                            .filter(program -> program.getPaoIdentifier().getPaoId() == programIdent.getPaoId())
                            .findFirst().get();
                    
                    syncThermostatAndEnrollment(syncId, ignore, row, account, group, programPao, thermostatsInYukon);
                }
            } else { 
              //this group is in the black list
              log.debug("Ignoring group {} reason {}", row.getGroupId(), ignore.groups.get(row.getGroupId()));
            }
        });
    }

    /**
     * Syncs thermostats and enrollment for user account
     */
    private void syncThermostatAndEnrollment(int syncId, Blacklist ignore,
            CustomerInfo row, CustomerAccount account, LiteYukonPAObject group, LiteYukonPAObject programPao, Map<String, Thermostat> thermostatsInYukon) {
       
        List<NestSyncDetail> details = row.getDeviceIds().stream()
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
                List<NestSyncDetail> thermostatDetails = new ArrayList<>();
                Thermostat thermostatInYukon = thermostatsInYukon.get(thermostat);               
                try {
                    int inventoryId = thermostatInYukon != null? thermostatInYukon.getId() : 0;
                    if(thermostatInYukon == null) {
                        //add thermostat to the account
                        inventoryId = createThermostat(row.getAccountNumber(), thermostat, syncId, thermostatDetails);                    
                    } else {
                        CustomerAccount accountForThermostat = customerAccountDao.getAccountByInventoryId(thermostatInYukon.getId());
                        //this thermostat belongs to another account
                        if(accountForThermostat.getAccountId() != account.getAccountId()) {
                            //remove thermostat from the account
                            thermostatDetails.add(deleteThermostat(syncId, accountForThermostat.getAccountNumber(),
                                thermostatInYukon.getId(), thermostat, THERMOSTAT_IN_WRONG_ACCOUNT));
                            //add thermostat to the account
                            inventoryId = createThermostat(row.getAccountNumber(), thermostat, syncId, thermostatDetails);
                        }
                    }
                    // enroll thermostat
                    thermostatDetails.add(enrollThermostat(syncId, group.getPaoName(), programPao.getPaoName(), account,
                        thermostat, inventoryId));
                    return thermostatDetails;      
                } catch (Exception e) {
                    //continue processing the next row if enrollment failed.
                    log.error("Failed to create or enroll thermostat:" + thermostat + "data:" + row, e);
                    return thermostatDetails;
                }
          
            })
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        nestDao.saveSyncDetails(details);
    }

    /**
     * Unenrolls thermostat from the account
     */
    private NestSyncDetail deleteThermostat(int syncId, String accountNumber, int inventoryId,
            String serialNumber, NestSyncI18nKey reason) {
        try {
            hardwareService.deleteHardware(energyCompany.getUser(), true, inventoryId);
            NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, reason, AUTO_DELETED_THERMOSTAT);
            detail.addValue(ACCOUNT_NUMBER, accountNumber);
            detail.addValue(SERIAL_NUMBER, serialNumber);
            return detail;
        } catch (Exception e) {
            log.error("Unable to delete thermostat " + serialNumber + " from account " + accountNumber, e);
            return null;
        }
    }

    /**
     * If address in Yukon is missing creates the address, Missing = all fields set to empty
     */
    private NestSyncDetail updateAddress(CustomerInfo row, int syncId, LiteAddress address) {
        AccountServiceImpl.setAddressFieldsFromDTO(address,
            new Address(row.getServiceAddress().getStreetAddress(), "", row.getServiceAddress().getCity(),
                row.getServiceAddress().getState(), row.getServiceAddress().getPostalCode(), ""));
        try {
            log.debug("Adding address {} to account {}", address, row.getAccountNumber());
            addressDao.update(address);
            NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_FOUND_ADDRESS, AUTO_CREATED_ADDRESS);
            detail.addValue(ACCOUNT_NUMBER, row.getAccountNumber());
            return detail;
        } catch (Exception e) {
            log.error("Unable to create addess for account {} row {}", row.getAccountNumber(), row);
            return null;
        }
    }
    
    /**
     * Creates account
     */
    private NestSyncDetail createAccount(CustomerInfo row, int syncId) {
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
        Address address = new Address(row.getServiceAddress().getStreetAddress(), "", row.getServiceAddress().getCity(),
            row.getServiceAddress().getState(), row.getServiceAddress().getPostalCode(), "");
        account.setStreetAddress(address);
        account.setBillingAddress(address);
        account.setIsCommercial(false);
        account.setIsCustAtHome(true);
        UpdatableAccount updatableAccount = new UpdatableAccount();
        updatableAccount.setAccountDto(account);
        updatableAccount.setAccountNumber(row.getAccountNumber());
        try {
            int accountId = accountService.addAccount(updatableAccount, energyCompany.getUser());
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
        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, energyCompany.getUser());
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, NOT_ENROLLED_THERMOSTAT, AUTO_ENROLLED_THERMOSTAT);
            detail.addValue(ACCOUNT_NUMBER, accountNumber);
            detail.addValue(SERIAL_NUMBER, serialNumber);
            detail.addValue(PROGRAM, program);
            detail.addValue(GROUP, group);
        return detail;
    }
    
    private NestSyncDetail changeGroup(int syncId, String group, String program, String accountNumber,
            String serialNumber, String groupEnrolledIn) {
        EnrollmentHelper enrollmentHelper = new EnrollmentHelper(accountNumber, group, program, serialNumber);
        enrollmentHelperService.doEnrollment(enrollmentHelper, EnrollmentEnum.ENROLL, energyCompany.getUser());
        NestSyncDetail detail = new NestSyncDetail(0, syncId, AUTO, CHANGE_GROUP, AUTO_ENROLLED_THERMOSTAT);
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
            yukonListDao.getYukonListEntry(YukonDefinition.DEV_TYPE_NEST_THERMOSTAT.getDefinitionId(), energyCompany);
        String typeStr = yukonListEntry.iterator().next().getEntryText();
        deviceInfo.setDeviceType(typeStr);
        LiteInventoryBase inventory = starsControllableDeviceHelper.addDeviceToAccount(deviceInfo, energyCompany.getUser());
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
     * Returns true if the program has an area
     */
    protected boolean programHasArea(Collection<PaoIdentifier> programs,
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
    protected List<NestSyncDetail> validateProgramAndAreaSetup(int syncId, List<LiteYukonPAObject> nestGroups,
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
    protected Optional<NestSyncDetail> buildSyncDetail(int syncId, LiteYukonPAObject group,
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
        } else if (!programHasArea(programs, programsToAreas)) {
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
    protected List<NestSyncDetail> validateThermostats(Set<String> thermostatsInNest, Map<String, Thermostat> thermostatsInYukon,
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
    protected List<NestSyncDetail> filterNestGroups(List<String> groupsInNest, int syncId, Blacklist ignore) {
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
    private List<String> parseGroups(List<CustomerInfo> existing) {
        List<String> groupsInNest = existing.stream()
                .map(row -> row.getGroupId())
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
            paoCreationHelper.addDefaultPointsToPao(PaoIdentifier.of(pao.getPAObjectID(), pao.getPaoType()));
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
