package com.cannontech.billing.record;


/**
 * This format overrides how the accountnumber in MVRS format is looked up.
 * This accountNumber is taken from the leftmost 6 digits, which in essence truncates
 * any rotation digits on the account number.
 * @author stacey
 *
 */
public class MVRS_KetchikanRecord extends MVRSRecord
{
	public String getAccountNumber (String acctNumFromFile) {
		return acctNumFromFile.substring(0, 6);  //the leftmost 6digits of the accountnumber field
	}
}
