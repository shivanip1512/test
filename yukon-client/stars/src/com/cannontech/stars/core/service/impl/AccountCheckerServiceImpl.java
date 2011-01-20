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
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.util.StarsUtils;
import com.google.common.collect.Lists;

public class AccountCheckerServiceImpl implements AccountCheckerService {
    private final Logger logger = YukonLogManager.getLogger(AccountCheckerServiceImpl.class);
    private CustomerAccountDao customerAccountDao;
    private InventoryBaseDao inventoryBaseDao;
    private ContactDao contactDao;
    private ApplianceDao applianceDao;
    private ProgramDao programDao;
    private GraphDao graphDao;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
    @Override
    public void checkInventory(final LiteYukonUser user, final Integer... inventoryIds)
            throws NotAuthorizedException {
        
        if (isOperator(user)) return;
        
        List<Integer> actualInventoryIds = getInventoryIdsByUser(user);
        doCheck(user, actualInventoryIds, inventoryIds, "Inventory");
    }
    
    @Override
    public void checkInventory(final LiteYukonUser user, final List<Integer> inventoryIds) 
            throws NotAuthorizedException {
        checkInventory(user, inventoryIds.toArray(new Integer[inventoryIds.size()]));
        
    }
    
    @Override
    public void checkThermostatSchedule(final LiteYukonUser user, final Integer... scheduleIds)
            throws NotAuthorizedException {
    	
        if (isOperator(user)) return;
        
        if (isEmptyIds(scheduleIds)) return;
        
        final List<Integer> actualSchedulIds = getScheduleIdsByUser(user);
        final List<Integer> badScheduleIds = new ArrayList<Integer>(0);
        
        for (final Integer scheduleId : scheduleIds) {
            boolean containsId = actualSchedulIds.contains(scheduleId);
            if (!containsId) badScheduleIds.add(scheduleId);
        }
        
        if (badScheduleIds.size() == 0) return;
        
        List<Integer> inventoryIds = Lists.newArrayList();
        for (int badScheduleId : badScheduleIds) {
        	
        	inventoryIds.addAll(accountThermostatScheduleDao.getThermostatIdsUsingSchedule(badScheduleId));
        }

        checkInventory(user, inventoryIds.toArray(new Integer[inventoryIds.size()]));
    }
    
    @Override
    public void checkContact(final LiteYukonUser user, final Integer... contactIds) 
            throws NotAuthorizedException {
        
        if (isOperator(user)) return;
        
        List<Integer> actualContactIds = getContactIdsByUser(user);
        doCheck(user, actualContactIds, contactIds, "Contact");
    }
    
    @Override
    public void checkProgram(final LiteYukonUser user, final Integer... programIds)
            throws NotAuthorizedException {
        
        if (isOperator(user)) return;
        
        List<Integer> actualProgramIds = getProgramIdsByUser(user);
        doCheck(user, actualProgramIds, programIds, "Program");
    }
    
	@Override
	public void checkGraph(LiteYukonUser user, Integer... graphDefinitionIds)
			throws NotAuthorizedException {
	    
	    if (isOperator(user)) return;
	    
		List<Integer> actualDefinitionIds = getGraphDefinitionIdsByUser(user);
		doCheck(user, actualDefinitionIds, graphDefinitionIds, "Graph");
		
	}

    private void doCheck(LiteYukonUser user, List<Integer> actualIdList, Integer[] ids, String type) {
        boolean isEmptyIds = isEmptyIds(ids);
        if (isEmptyIds) return;
        
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
        final List<Integer> inventoryIdList = inventoryBaseDao.getInventoryIdsByAccountId(customerAccountId);
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
    
    private List<Integer> getProgramIdsByUser(LiteYukonUser user) {
        int customerAccountId = getCustomerAccountId(user);
        
        final List<Appliance> appliances = applianceDao.getAssignedAppliancesByAccountId(customerAccountId);
        final List<Program> programs = programDao.getByAppliances(appliances);
        
        final List<Integer> programsIdList = new ArrayList<Integer>(programs.size());
        
        for (final Program program : programs) {
            Integer programId = program.getProgramId();
            programsIdList.add(programId);
        }
        
        return programsIdList;
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
    
    private List<Integer> getScheduleIdsByUser(LiteYukonUser user) {
        
    	int accountId = getCustomerAccountId(user);
        
        List<AccountThermostatSchedule> allSchedulesForAccount = accountThermostatScheduleDao.getAllSchedulesForAccountByType(accountId, null);
        
        List<Integer> scheduleIdList = Lists.newArrayListWithCapacity(allSchedulesForAccount.size());
        for (AccountThermostatSchedule schedule : allSchedulesForAccount) {
        	int scheduleId = schedule.getAccountThermostatScheduleId();
        	scheduleIdList.add(scheduleId);
        }
        
        return scheduleIdList;
    }
    
    private int getCustomerAccountId(LiteYukonUser user) {
    	CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(user);
    	return customerAccount.getAccountId();
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
    
    private boolean isOperator(LiteYukonUser user) {
        boolean isOperator = StarsUtils.isOperator(user);
        return isOperator;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
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

    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
}
