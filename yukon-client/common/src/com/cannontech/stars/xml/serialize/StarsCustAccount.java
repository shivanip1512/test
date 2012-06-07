package com.cannontech.stars.xml.serialize;

import java.util.Enumeration;
import java.util.Vector;

public abstract class StarsCustAccount {
    private int accountID;
    private boolean hasAccountID;
    private int customerID;
    private boolean hasCustomerID;
    private String accountNumber;
    private boolean isCommercial;
    private boolean hasIsCommercial;
    private String company;
    private int custType;
    private String accountNotes;
    private String propertyNumber;
    private String propertyNotes;
    private StreetAddress streetAddress;
    private StarsSiteInformation starsSiteInformation;
    private BillingAddress billingAddress;
    private PrimaryContact primaryContact;
    private Vector<AdditionalContact> additionalContactList;
    private String timeZone;
    private String customerNumber;
    private int rateScheduleID;
	private String altTrackNumber;
    private String custAtHome;
    private String custStatus;

    public StarsCustAccount() {
        additionalContactList = new Vector<AdditionalContact>();
    }

    public void addAdditionalContact(AdditionalContact vAdditionalContact) {
        additionalContactList.addElement(vAdditionalContact);
    }

    public void addAdditionalContact(int index, AdditionalContact vAdditionalContact) {
        additionalContactList.insertElementAt(vAdditionalContact, index);
    }

    public void deleteAccountID() {
        this.hasAccountID = false;
    } 

    public void deleteCustomerID() {
        this.hasCustomerID = false;
    } 

    public void deleteIsCommercial() {
        this.hasIsCommercial = false;
    }

    public Enumeration<AdditionalContact> enumerateAdditionalContact() {
        return additionalContactList.elements();
    } 

    public int getAccountID() {
        return this.accountID;
    }

    public String getAccountNotes() {
        return this.accountNotes;
    } 

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public AdditionalContact getAdditionalContact(int index) {
        //-- check bounds for index
        if ((index < 0) || (index > additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return additionalContactList.elementAt(index);
    } 

    public AdditionalContact[] getAdditionalContact() {
        int size = additionalContactList.size();
        AdditionalContact[] mArray = new AdditionalContact[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (AdditionalContact) additionalContactList.elementAt(index);
        }
        return mArray;
    } 

    public int getAdditionalContactCount() {
        return additionalContactList.size();
    }

    public BillingAddress getBillingAddress() {
        return this.billingAddress;
    }

    public String getCompany() {
        return this.company;
    }

    public int getCICustomerType() {
        return this.custType;
    }
    
    public int getCustomerID() {
        return this.customerID;
    }

    public boolean getIsCommercial() {
        return this.isCommercial;
    }

    public PrimaryContact getPrimaryContact() {
        return this.primaryContact;
    }

    public String getPropertyNotes() {
        return this.propertyNotes;
    }

    public String getPropertyNumber() {
        return this.propertyNumber;
    }

    public StarsSiteInformation getStarsSiteInformation() {
        return this.starsSiteInformation;
    }

    public StreetAddress getStreetAddress() {
        return this.streetAddress;
    }

    public String getTimeZone() {
        return this.timeZone;
    }
    
    public String getCustomerNumber() {
    	return this.customerNumber;
    }
     
    public int getRateScheduleID() {
    	return this.rateScheduleID;
    } 

	public String getAltTrackingNumber() {
		return this.altTrackNumber;
	}

	public boolean hasAccountID() {
        return this.hasAccountID;
    }

    public boolean hasCustomerID() {
        return this.hasCustomerID;
    }

    public boolean hasIsCommercial() {
        return this.hasIsCommercial;
    }

    public AdditionalContact removeAdditionalContact(int index) {
        AdditionalContact obj = additionalContactList.elementAt(index);
        additionalContactList.removeElementAt(index);
        return obj;
    } 

    public void removeAllAdditionalContact() {
        additionalContactList.removeAllElements();
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
        this.hasAccountID = true;
    }

    public void setAccountNotes(String accountNotes) {
        this.accountNotes = accountNotes;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAdditionalContact(int index, AdditionalContact vAdditionalContact) {
        //-- check bounds for index
        if ((index < 0) || (index > additionalContactList.size())) {
            throw new IndexOutOfBoundsException();
        }
        additionalContactList.setElementAt(vAdditionalContact, index);
    }

    public void setAdditionalContact(AdditionalContact[] additionalContactArray) {
        //-- copy array
        additionalContactList.removeAllElements();
        for (int i = 0; i < additionalContactArray.length; i++) {
            additionalContactList.addElement(additionalContactArray[i]);
        }
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCICustomerType(int custType) {
        this.custType = custType;
    }
    
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
        this.hasCustomerID = true;
    }

    public void setIsCommercial(boolean isCommercial) {
        this.isCommercial = isCommercial;
        this.hasIsCommercial = true;
    }

    public void setPrimaryContact(PrimaryContact primaryContact) {
        this.primaryContact = primaryContact;
    } 

    public void setPropertyNotes(String propertyNotes) {
        this.propertyNotes = propertyNotes;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public void setStarsSiteInformation(StarsSiteInformation starsSiteInformation) {
        this.starsSiteInformation = starsSiteInformation;
    } 

    public void setStreetAddress(StreetAddress streetAddress) {
        this.streetAddress = streetAddress;
    } 

    public void setTimeZone(String tZone) {
        this.timeZone = tZone;
    }
    
    public void setCustomerNumber(String custNum) {
		this.customerNumber = custNum;
	}

	public void setRateScheduleID(int rSched) {
		this.rateScheduleID = rSched;
	}
	
	public void setAltTrackingNumber(String altNum) {
		this.altTrackNumber = altNum;
	}
	
    public String getCustAtHome() {
        return custAtHome;
    }

    public void setCustAtHome(String _custAtHome) {
        this.custAtHome = _custAtHome;
    }

    public String getCustStatus() {
        return custStatus;
    }

    public void setCustStatus(String _custStatus) {
        this.custStatus = _custStatus;
    }
    
}
