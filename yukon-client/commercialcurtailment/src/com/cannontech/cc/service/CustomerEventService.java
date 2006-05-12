package com.cannontech.cc.service;

import java.util.List;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CustomerDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CustomerEventService {
    private CustomerDao customerDao;
    private BaseEventDao baseEventDao;

    public CustomerEventService() {
        super();
    }
    
    public CICustomerStub getCustomer(LiteYukonUser user) {
        LiteCICustomer liteCICustomer = CustomerFuncs.getCustomerForUser(user);
        CICustomerStub customerStub = customerDao.getForLite(liteCICustomer);
        return customerStub;
    }
    
    public List<BaseEvent> getCurrentEvents(LiteYukonUser user) {
        CICustomerStub customer = getCustomer(user);
        List<BaseEvent> allEvents = baseEventDao.getCurrentEvents(customer);
        return allEvents;
    }
    
    public List<BaseEvent> getPendingEvents(LiteYukonUser user) {
        CICustomerStub customer = getCustomer(user);
        List<BaseEvent> allEvents = baseEventDao.getPendingEvents(customer);
        return allEvents;
    }
    
    public List<BaseEvent> getRecentEvents(LiteYukonUser user) {
        CICustomerStub customer = getCustomer(user);
        List<BaseEvent> allEvents = baseEventDao.getRecentEvents(customer);
        return allEvents;
    }
    
    // dependency injection getters/setters

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public BaseEventDao getBaseEventDao() {
        return baseEventDao;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }

}
