package com.cannontech.cc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.cannontech.cc.dao.CustomerDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.exception.NoPointException;
import com.cannontech.database.cache.functions.SimplePointAccess;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.support.CustomerPointTypeHelper;
import com.cannontech.support.CustomerPointTypeLookup;

public class CustomerPointService {
    private CustomerDao customerDao;
    private CustomerPointTypeLookup pointTypeLookup;
    private CustomerPointTypeHelper pointTypeHelper;
    private SimplePointAccess pointAccess;

    public CustomerPointService() {
        super();
    }

    public Map<String, Double> getPointValueCache(CICustomerStub customer) {
        Map<String, Double> pointValueCache = new TreeMap<String, Double>();
        Set<String> pointTypeList = pointTypeLookup.getApplicablePoints(customer.getLite());
        for (String pointType : pointTypeList) {
            try {
                LitePoint point = pointTypeHelper.getPoint(customer, pointType);
                double pointValue = pointAccess.getPointValue(point);
                pointValueCache.put(pointType, pointValue);
            } catch (NoPointException e) {
                // then it doesn't belong in the cache!
            }
        }
        return pointValueCache;
    }
    
    public void savePointValues(CICustomerStub customer, Map<String, Double> pointValueCache) {
        for (Map.Entry<String, Double> entry : pointValueCache.entrySet()) {
            try {
                LitePoint point = pointTypeHelper.getPoint(customer, entry.getKey());
                pointAccess.setPointValue(point, entry.getValue());
            } catch (NoPointException e) {
                // if points are to be created on save, this would be
                // the place to do it
                // however, for the time being, points will be created
                // immediately when the create point button is pressed
            }
        }
    }
    
    public List<String> getSatisfiedPointGroups(CICustomerStub customer) {
        Set<String> satisfiedPointGroups = pointTypeHelper.getSatisfiedPointGroups(customer);
        return new ArrayList<String>(satisfiedPointGroups);
    }
    
    public List<String> getPointTypeList(CICustomerStub customer) {
        ArrayList<String> arrayList = new ArrayList<String>(pointTypeLookup.getApplicablePoints(customer.getLite()));
        Collections.sort(arrayList);
        return arrayList;
    }

    public void createPoint(CICustomerStub customer, String pointType) {
        pointTypeHelper.createPoint(customer, pointType);
    }
    
    public List<CICustomerStub> getCustomers(LiteEnergyCompany energyCompany) {
        return customerDao.getCustomersForEC(energyCompany.getEnergyCompanyID());
    }
    
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public CICustomerStub getCustomer(int customerId) {
        return customerDao.getForId(customerId);
    }

    public void setPointAccess(SimplePointAccess pointAccess) {
        this.pointAccess = pointAccess;
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

    public void setPointTypeLookup(CustomerPointTypeLookup pointTypeLookup) {
        this.pointTypeLookup = pointTypeLookup;
    }




}
