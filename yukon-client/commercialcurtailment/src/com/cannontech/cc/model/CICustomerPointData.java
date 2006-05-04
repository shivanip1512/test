package com.cannontech.cc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Yep. This class is pretty ugly. Maybe when the EJB3 Persistence stuff
 * is better documented I'll be able to figure out how to have a foreign key
 * (customerId in the customer table) refer to part of a composite primary key 
 * (custoemrId in this table) and have a propper ManyToOne pointing back. For
 * now this class will just have to be dealt with at the id level (although
 * there are some helper methods defined to ease the pain).
 */
@Entity
@Table(name = "CICustomerPointData")
public class CICustomerPointData {
    private CICustomerPointDataPk id = new CICustomerPointDataPk();
    private Integer pointId;
    private String optionalLabel = "";
    
    @Id
    public CICustomerPointDataPk getId() {
        return id;
    }
    
    public void setId(CICustomerPointDataPk id) {
        this.id = id;
    }
    
    @Column(nullable=false)
    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }


    public CICustomerPointData() {
        super();
    }

    @Column(length=32, nullable=false)
    public String getOptionalLabel() {
        return optionalLabel;
    }

    public void setOptionalLabel(String optionalLabel) {
        this.optionalLabel = optionalLabel;
    }

    @Transient
    public Integer getCustomerId() {
        return id.getCustomerId();
    }

    @Transient
    public String getType() {
        return id.getType();
    }

    public void setCustomerId(Integer customerId) {
        id.setCustomerId(customerId);
    }

    public void setType(String type) {
        id.setType(type);
    }
    
    /**
     * Helper method to set the customer id. There can
     * be no get for this, unfortunately.
     * @param customer
     */
    public void setCustomer(CICustomerStub customer) {
        id.setCustomerId(customer.getId());
    }
    

}
