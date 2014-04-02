package com.cannontech.cc.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.CustomerFactory;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.spring.YukonSpringHook;

public class CICustomerStub implements Comparable<CICustomerStub>, Identifiable {
    private Integer id;
    private String companyName;
    private Map<CICustomerPointType, CICustomerPointData> pointData = new HashMap<CICustomerPointType, CICustomerPointData>();
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public LiteCICustomer getLite() {
        LiteCICustomer liteCICustomer = YukonSpringHook.getBean(CustomerDao.class).getLiteCICustomer(id);
        if (liteCICustomer == null) {
            throw new RuntimeException("Unable to get LiteCICustomer for stub (id=" + id + ")");
        }
        return liteCICustomer;
    }
    
    public Map<CICustomerPointType, CICustomerPointData> getPointData() {
        return pointData;
    }
    
    public void setPointData(Map<CICustomerPointType, CICustomerPointData> pointData) {
        this.pointData = pointData;
    }
    
    public CICustomerBase getDBPersistant() {
        try {
            return CustomerFactory.createCustomer(getLite());
        } catch (TransactionException e) {
            throw new RuntimeException("Unable to get CICustomerBase for stub (id=" + id + ")", e);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CICustomerStub == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CICustomerStub rhs = (CICustomerStub) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    @Override
    public String toString() {
        return "Customer [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

    public void addPoint(CICustomerPointData customerPoint) {
        customerPoint.setCustomerId(getId());
        pointData.put(customerPoint.getType(), customerPoint);
    }
    
    public int compareTo(CICustomerStub o) {
        return companyName.compareTo(o.companyName);
    }

    public void setPointData(Collection<CICustomerPointData> collection) {
        for (CICustomerPointData data : collection) {
            pointData.put(data.getType(),data);
        }
        
    }
}
