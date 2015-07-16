package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.common.exception.PointDataException;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.support.CustomerPointTypeHelper;
import com.cannontech.support.CustomerPointTypeLookup;
import com.cannontech.support.NoPointException;

public class CustomerPointService {
    private CustomerStubDao customerStubDao;
    private CustomerPointTypeLookup pointTypeLookup;
    private CustomerPointTypeHelper pointTypeHelper;
    private SimplePointAccessDao pointAccess;

    public CustomerPointService() {
        super();
    }

    public Map<CICustomerPointType, BigDecimal> getPointValueCache(CICustomerStub customer) {
        Map<CICustomerPointType, BigDecimal> pointValueCache = new TreeMap<CICustomerPointType, BigDecimal>();
        Set<CICustomerPointType> pointTypeList = pointTypeLookup.getApplicablePoints(customer.getLite());
        for (CICustomerPointType pointType : pointTypeList) {
            try {
                LitePoint point = pointTypeHelper.getPoint(customer, pointType);
                double pointValue = pointAccess.getPointValue(point);
                BigDecimal dbvalue = new BigDecimal(pointValue, new MathContext(10));
                pointValueCache.put(pointType, dbvalue);
            } catch (NoPointException e) {
                // then it doesn't belong in the cache!
            } catch (PointDataException e) {
                // I'm not sure this is the best idea, bascially the calling code
                // expects there to be something in this map for every type that
                // exists on the customer, so we might as well put something here.
                pointValueCache.put(pointType, BigDecimal.ZERO);
            }
        }
        return pointValueCache;
    }
    
    public void savePointValues(CICustomerStub customer, Map<CICustomerPointType, BigDecimal> pointValueCache) {
        for (Map.Entry<CICustomerPointType, BigDecimal> entry : pointValueCache.entrySet()) {
            try {
                LitePoint point = pointTypeHelper.getPoint(customer, entry.getKey());
                BigDecimal value = entry.getValue();
                pointAccess.setPointValue(point, value.doubleValue());
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
    
    public List<CICustomerPointType> getPointTypeList(CICustomerStub customer) {
        ArrayList<CICustomerPointType> arrayList = new ArrayList<CICustomerPointType>(pointTypeLookup.getApplicablePoints(customer.getLite()));
        Collections.sort(arrayList);
        return arrayList;
    }

    public void createPoint(CICustomerStub customer, CICustomerPointType pointType) {
        pointTypeHelper.createPoint(customer, pointType);
    }
    
    public List<CICustomerStub> getCustomers(EnergyCompany energyCompany) {
        List<CICustomerStub> customersForEC = customerStubDao.getCustomersForEC(energyCompany.getId());
        Collections.sort(customersForEC);
        return customersForEC;
    }
    
    public void setCustomerStubDao(CustomerStubDao customerStubDao) {
        this.customerStubDao = customerStubDao;
    }

    public CICustomerStub getCustomer(int customerId) {
        return customerStubDao.getForId(customerId);
    }

    public void setPointAccess(SimplePointAccessDao pointAccess) {
        this.pointAccess = pointAccess;
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

    public void setPointTypeLookup(CustomerPointTypeLookup pointTypeLookup) {
        this.pointTypeLookup = pointTypeLookup;
    }
}
