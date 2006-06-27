package com.cannontech.cc.service;

import java.util.List;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CustomerEventService {
    private CustomerStubDao customerStubDao;
    private CustomerDao customerDao;
    private BaseEventDao baseEventDao;

    public CustomerEventService() {
        super();
    }
    
    public CICustomerStub getCustomer(LiteYukonUser user) {
        LiteCICustomer liteCICustomer = customerDao.getCustomerForUser(user);
        CICustomerStub customerStub = customerStubDao.getForLite(liteCICustomer);
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

    public CustomerStubDao getCustomerDao() {
        return customerStubDao;
    }

    public void setCustomerDao(CustomerStubDao customerDao) {
        this.customerStubDao = customerDao;
    }

    public BaseEventDao getBaseEventDao() {
        return baseEventDao;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

}
