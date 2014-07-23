package com.cannontech.billing;

import java.io.IOException;
import java.io.OutputStream;

import com.cannontech.billing.mainprograms.BillingFileDefaults;

public interface SimpleBillingFormat {
	public void setBillingFileDefaults(BillingFileDefaults billingFileDefaults);
	public BillingFileDefaults getBillingFileDefaults();
	public boolean writeToFile(OutputStream out) throws IOException;
	public int getReadingCount();
}
