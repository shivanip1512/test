package com.cannontech.stars.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.util.StarsUtils;

public class AccountCheckerServiceImpl implements AccountCheckerService {
    private final Logger logger = YukonLogManager.getLogger(AccountCheckerServiceImpl.class);
    private CustomerAccountDao customerAccountDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private InventoryBaseDao inventoryBaseDao;
    private ContactDao contactDao;
    private ApplianceCategoryDao applianceCategoryDao;
    private ApplianceDao applianceDao;
    private ProgramDao programDao;
    private GraphDao graphDao;
    
    @Override
    public void checkInventory(final LiteYukonUser user, final Integer... inventoryIds)
            throws NotAuthorizedException {
        
        final List<Integer> actualInventoryIds = getInventoryIdsByUser(user);
        doCheck(user, actualInventoryIds, inventoryIds, "Inventory");
    }
    
    @Override
    public void checkThermostatSchedule(final LiteYukonUser user, final Integer... scheduleIds)
            throws NotAuthorizedException {
    	
    	if(!this.isEmptyIds(scheduleIds)) {
    		
	        // Check schedule authorization by looking at the inventory that each schedule is
	    	// associated with.
	    	List<Integer> inventoryIds = thermostatScheduleDao.getInventoryIdsForSchedules(scheduleIds);
	    	
	        this.checkInventory(user, inventoryIds.toArray(new Integer[]{}));
    	}
    }
    
    @Override
    public void checkContact(final LiteYukonUser user, final Integer... contactIds) 
            throws NotAuthorizedException {
        
        final List<Integer> actualContactIds = getContactIdsByUser(user);
        doCheck(user, actualContactIds, contactIds, "Contact");
    }
    
    @Override
    public void checkApplianceCategory(final LiteYukonUser user, final Integer... categoryIds) 
            throws NotAuthorizedException {
        
        final List<Integer> actualCategoryIds = getApplianceCategoryIdsByUser(user);
        doCheck(user, actualCategoryIds, categoryIds, "ApplianceCategory");
    }
    
    @Override
    public void checkProgram(final LiteYukonUser user, final Integer... programIds)
            throws NotAuthorizedException {
        final List<Integer> actualProgramIds = getProgramIdsByUser(user);
        doCheck(user, actualProgramIds, programIds, "Program");
    }
    
    @Override
    public void checkAppliance(final LiteYukonUser user, final Integer... applianceIds)
            throws NotAuthorizedException {
        final List<Integer> actualApplianceIds = getApplianceIdsByUser(user);
        doCheck(user, actualApplianceIds, applianceIds, "Appliance");
    }
    
    @Override
    public void checkCustomerAccount(final LiteYukonUser user, final Integer... customerAccountIds) 
            throws NotAuthorizedException {
        final List<Integer> actualCustomerAccountIds = getCustomerAccountIdsByUser(user);
        doCheck(user, actualCustomerAccountIds, customerAccountIds, "CustomerAccount");
    }
    

	@Override
	public void checkGraph(LiteYukonUser user, Integer... graphDefinitionIds)
			throws NotAuthorizedException {
		final List<Integer> actualDefinitionIds = getGraphDefinitionIdsByUser(user);
		doCheck(user, actualDefinitionIds, graphDefinitionIds, "Graph");
		
	}
    
    
    @Override
    public void haltOnCheckInventory(final LiteYukonUser user, final Integer... inventoryIds)
            throws HaltOnErrorException {
        try {
            checkInventory(user, inventoryIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }

    @Override
    public void haltOnCheckThermostatSchedule(final LiteYukonUser user, final Integer... scheduleIds)
            throws HaltOnErrorException {
        try {
            checkThermostatSchedule(user, scheduleIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    @Override
    public void haltOnCheckContact(final LiteYukonUser user, final Integer... contactIds)
            throws HaltOnErrorException {
        try {
            checkContact(user, contactIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    @Override
    public void haltOnCheckApplianceCategory(final LiteYukonUser user, final Integer... categoryIds)
            throws HaltOnErrorException {
        try {
            checkApplianceCategory(user, categoryIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    @Override
    public void haltOnCheckProgram(final LiteYukonUser user, final Integer... programIds)
            throws HaltOnErrorException {
        try {
            checkProgram(user, programIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    @Override
    public void haltOnCheckAppliance(final LiteYukonUser user, final Integer... applianceIds)
            throws HaltOnErrorException {
        try {
            checkAppliance(user, applianceIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    @Override
    public void haltOnCheckCustomerAccount(final LiteYukonUser user, final Integer... customerAccountIds)
            throws HaltOnErrorException {
        try {
            checkCustomerAccount(user, customerAccountIds);
        } catch (NotAuthorizedException e) {
            throw new HaltOnErrorException(e.getMessage());
        }
    }
    
    private void doCheck(LiteYukonUser user, List<Integer> actualIdList, Integer[] ids, String type) {
        boolean isEmptyIds = isEmptyIds(ids);
        if (isEmptyIds) return;
        
        boolean isOperator = StarsUtils.isOperator(user);
        if (isOperator) return; 
        
        for (final Integer id : ids) {
            boolean containsId = actualIdList.contains(id);
            if (containsId) continue;
            
            String errorMessage = generateErrorMessage(user, type, ids);
            logger.error(errorMessage);
            
            throw new NotAuthorizedException(errorMessage);
        }
    }
    
    private boolean isEmptyIds(Integer[] ids) {
        if (ids != null) {
            for (final Integer id : ids) {
                if (id != null) return false;
            }
        }
        return true;
    }
    
    private List<Integer> getInventoryIdsByUser(LiteYukonUser user) {
        int customerAccountId = getCustomerAccountId(user);

        final List<InventoryBase> list = inventoryBaseDao.getByAccountId(customerAccountId);
        final List<Integer> inventoryIdList = new ArrayList<Integer>(list.size());
        
        for (final InventoryBase inventory : list) {
            Integer inventoryId = inventory.getInventoryId();
            inventoryIdList.add(inventoryId);
        }
        
        return inventoryIdList;
    }
    
    private List<Integer> getContactIdsByUser(LiteYukonUser user) {
        int customerAccountId = getCustomerAccountId(user);
        
        final List<LiteContact> contacts = contactDao.getAdditionalContactsForAccount(customerAccountId);
        final LiteContact primaryContact = contactDao.getPrimaryContactForAccount(customerAccountId);
        contacts.add(primaryContact);
        final List<Integer> contactIdList = new ArrayList<Integer>(contacts.size());
        
        for (final LiteContact contact : contacts) {
            Integer contactId = contact.getContactID();
            contactIdList.add(contactId);
        }

        return contactIdList;
    }
    
    private List<Integer> getApplianceCategoryIdsByUser(LiteYukonUser user) {
        List<CustomerAccount> customerAccountList = customerAccountDao.getByUser(user);
        CustomerAccount customerAccount = customerAccountList.get(0);
        
        final List<ApplianceCategory> categories = 
            applianceCategoryDao.getApplianceCategories(customerAccount);
        final List<Integer> categoryIdList = new ArrayList<Integer>(categories.size());
        
        for (ApplianceCategory category : categories) {
            Integer applianceCategoryId = category.getApplianceCategoryId();
            categoryIdList.add(applianceCategoryId);
        }
        
        return categoryIdList;
    }
    
    private List<Integer> getProgramIdsByUser(LiteYukonUser user) {
        int customerAccountId = getCustomerAccountId(user);
        
        final List<Appliance> appliances = applianceDao.getByAccountId(customerAccountId);
        final List<Program> programs = programDao.getByAppliances(appliances);
        
        final List<Integer> programsIdList = new ArrayList<Integer>(programs.size());
        
        for (final Program program : programs) {
            Integer programId = program.getProgramId();
            programsIdList.add(programId);
        }
        
        return programsIdList;
    }
    
    private List<Integer> getApplianceIdsByUser(LiteYukonUser user) {
        int customerAccountId = getCustomerAccountId(user);
        
        final List<Appliance> appliances = applianceDao.getByAccountId(customerAccountId);
        final List<Integer> applianceIdList = new ArrayList<Integer>(appliances.size());
        
        for (final Appliance appliance : appliances) {
            Integer applianceId = appliance.getApplianceId();
            applianceIdList.add(applianceId);
        }
        
        return applianceIdList;
    }
    
    private List<Integer> getCustomerAccountIdsByUser(LiteYukonUser user) {
        final List<CustomerAccount> customerAccounts = customerAccountDao.getByUser(user);
        final List<Integer> customerAccountIds = new ArrayList<Integer>(customerAccounts.size());
        
        for (final CustomerAccount customerAccount : customerAccounts) {
            Integer customerAccountId = customerAccount.getAccountId();
            customerAccountIds.add(customerAccountId);
        }
        
        return customerAccountIds;
    }

    private List<Integer> getGraphDefinitionIdsByUser(LiteYukonUser user) {

		final List<LiteGraphDefinition> definitions = graphDao.getGraphDefinitionsForUser(user.getUserID());
		final List<Integer> definitionIds = new ArrayList<Integer>();
		for(final LiteGraphDefinition definition : definitions) {
			int graphDefinitionID = definition.getGraphDefinitionID();
			definitionIds.add(graphDefinitionID);
		}
    	
    	return definitionIds;
    }
    
    private int getCustomerAccountId(LiteYukonUser user) {
        List<Integer> customerAccountIdsList = getCustomerAccountIdsByUser(user);
        int customerAccountId = customerAccountIdsList.get(0);
        return customerAccountId;
    }
    
    private String generateErrorMessage(LiteYukonUser user, String type, Object[] ids) {
        final StringBuilder sb = new StringBuilder();
        sb.append("username: " + user.getUsername());
        sb.append(" userId: " + user.getUserID());
        sb.append(" is not authorized to access one of the following ");
        sb.append(type);
        sb.append(" ids: ");
        sb.append(Arrays.toString(ids));
        
        String errorMessage = sb.toString();
        return errorMessage;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setThermostatScheduleDao(
            ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setApplianceCategoryDao(
            ApplianceCategoryDao applianceCategoryDao) {
        this.applianceCategoryDao = applianceCategoryDao;
    }
    
    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setGraphDao(GraphDao graphDao) {
		this.graphDao = graphDao;
	}

}
