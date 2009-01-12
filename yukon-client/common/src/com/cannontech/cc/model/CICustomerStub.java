package com.cannontech.cc.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.CustomerFactory;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.db.customer.CICustomerPointType;

@Entity
@Table(name = "CICustomerBase")
public class CICustomerStub implements Comparable<CICustomerStub> {
    private Integer id;
    //private Float customerDemandLevel;
    //private String curtailmentAgreement;
    //private Float curtailAmount;
    private String companyName;
    private Map<CICustomerPointType, CICustomerPointData> pointData = new HashMap<CICustomerPointType, CICustomerPointData>();
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CustomerID")
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
    
    @Transient
    public LiteCICustomer getLite() {
        LiteCICustomer liteCICustomer = DaoFactory.getCustomerDao().getLiteCICustomer(id);
        if (liteCICustomer == null) {
            throw new RuntimeException("Unable to get LiteCICustomer for stub (id=" + id + ")");
        }
        return liteCICustomer;
    }
    
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="CustomerId", referencedColumnName="CustomerId")
    @MapKey(name="id.type")
    @BatchSize(size=10)
    public Map<CICustomerPointType, CICustomerPointData> getPointData() {
        return pointData;
    }
    
    public void setPointData(Map<CICustomerPointType, CICustomerPointData> pointData) {
        this.pointData = pointData;
    }
    
    @Transient
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
        customerPoint.setCustomer(this);
        pointData.put(customerPoint.getType(), customerPoint);
    }

    public int compareTo(CICustomerStub o) {
        return companyName.compareTo(o.companyName);
    }


}
