package com.cannontech.cc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.database.data.notification.NotifMap;

public class GroupCustomerNotif {
    private Integer id;
    private CICustomerStub customer;
    private Group group;
    private NotifMap notifMap = new NotifMap();
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public CICustomerStub getCustomer() {
        return customer;
    }

    public void setCustomer(CICustomerStub customer) {
        this.customer = customer;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getAttribs() {
        return notifMap.getAttribs();
    }

    public void setAttribs(String attribs) {
        notifMap.setAttribs(attribs);
    }
    
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
        return new EqualsBuilder().append(group, rhs.group).append(customer, rhs.customer).isEquals();
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
