package com.cannontech.cc.model;

import java.util.List;

public class GroupBean {
    private Integer id;
    private String name;
    private List<Customer> assignedCustomers;
    private List<Customer> availableCustomers; //aka unassigned customers
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Customer> getAssignedCustomers() {
        return assignedCustomers;
    }

    public void setAssignedCustomers(List<Customer> assignedCustomers) {
        this.assignedCustomers = assignedCustomers;
    }

    public List<Customer> getAvailableCustomers() {
        return availableCustomers;
    }

    public void setAvailableCustomers(List<Customer> availableCustomers) {
        this.availableCustomers = availableCustomers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static final class Customer {
        private int id;
        private String companyName;
        private boolean emails;
        private boolean voice;
        private boolean sms;
        
        public int getId() {
            return id;
        }
        
        public void setId(int customerId) {
            id = customerId;
        }
        
        public boolean isEmails() {
            return emails;
        }
        
        public void setEmails(boolean emails) {
            this.emails = emails;
        }
        
        public boolean isVoice() {
            return voice;
        }
        
        public void setVoice(boolean voice) {
            this.voice = voice;
        }
        
        public boolean isSms() {
            return sms;
        }
        
        public void setSms(boolean sms) {
            this.sms = sms;
        }
        
        public String getCompanyName() {
            return companyName;
        }
        
        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }
    }
}
