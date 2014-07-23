package com.cannontech.billing.format.simple;

import com.cannontech.billing.SimpleBillingFormat;
import com.cannontech.billing.mainprograms.BillingFileDefaults;

public abstract class SimpleBillingFormatBase implements SimpleBillingFormat{

	public BillingFileDefaults billingFileDefaults = null;

	public SimpleBillingFormatBase() {
		super();
	}

	/**
	 * Returns the Billing File Defaults.
	 * @return BillingFileDefaults
	 */
	@Override
	public BillingFileDefaults getBillingFileDefaults() {
		return billingFileDefaults;
	}

	/**
	 * Set the billingFileDefaults field.
	 * @param newBillingFileDefaults com.cannontech.billing.mainprograms.BillingFileDefaults
	 */
	@Override
	public void setBillingFileDefaults(BillingFileDefaults newBillingDefaults) {
		billingFileDefaults = newBillingDefaults;
	}
}