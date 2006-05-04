package com.cannontech.cc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.database.data.notification.NotifMap;

@Entity
@Table(name = "CCurtGroupCustomerNotif",
       uniqueConstraints=@UniqueConstraint(columnNames={"GroupId","CustomerId"}))
public class GroupCustomerNotif {
    private Integer id;
    private CICustomerStub customer;
    private Group group;
    private NotifMap notifMap = new NotifMap();
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtGroupCustomerNotifId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne()
    @JoinColumn(name="CustomerId")
    public CICustomerStub getCustomer() {
        return customer;
    }

    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }

    @ManyToOne()
    @JoinColumn(name="GroupId")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column(nullable=false)
    public String getAttribs() {
        return notifMap.getAttribs();
    }

    public void setAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
    @Transient
    public NotifMap getNotifMap() {
        return notifMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupCustomerNotif == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        GroupCustomerNotif rhs = (GroupCustomerNotif) obj;
        return new EqualsBuilder().append(group, rhs.group)
            .append(customer, rhs.customer).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(group).append(customer).toHashCode();
    }
    
    @Override
    public String toString() {
        return "GroupCustomerNotif [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }


}
