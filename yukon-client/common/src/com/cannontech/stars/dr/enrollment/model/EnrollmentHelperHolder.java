package com.cannontech.stars.dr.enrollment.model;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;


public class EnrollmentHelperHolder {
    
	private EnrollmentHelper enrollmentHelper;
	private CustomerAccount customerAccount;
	private LMHardwareBase lmHardwareBase;
	private LiteStarsEnergyCompany liteStarsEnergyCompany;
	
	public EnrollmentHelperHolder(EnrollmentHelper enrollmentHelper,
			CustomerAccount customerAccount, LMHardwareBase lmHardwareBase, LiteStarsEnergyCompany liteStarsEnergyCompany) {
		super();
		this.enrollmentHelper = enrollmentHelper;
		this.customerAccount = customerAccount;
		this.lmHardwareBase = lmHardwareBase;
		this.liteStarsEnergyCompany = liteStarsEnergyCompany;
	}
    
	public EnrollmentHelper getEnrollmentHelper() {
		return enrollmentHelper;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}
	
	public LMHardwareBase getLmHardwareBase() {
		return lmHardwareBase;
	}
	
	public LiteStarsEnergyCompany getLiteStarsEnergyCompany() {
		return liteStarsEnergyCompany;
	}
	
}