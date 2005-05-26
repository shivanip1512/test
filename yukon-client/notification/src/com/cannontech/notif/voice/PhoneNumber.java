package com.cannontech.notif.voice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 */
public class PhoneNumber {
    private String _cleanPhoneNumber;
    Pattern _replacePattern = Pattern.compile("[^0-9]");

	/**
	 * Create a new PhoneNumber object with the given phone number.
     * The phone number will be stripped of any extraneous punctuation.
	 * @param phoneNumber the phone numer to use
	 * @throws PhoneNumberException
	 */
	public PhoneNumber(String rawPhoneNumber) {
		setPhoneNumber(rawPhoneNumber);
	}
	private String cleanPhoneNumber(String rawPhoneNumber) {
        Matcher matcher = _replacePattern.matcher(rawPhoneNumber);
        return matcher.replaceAll("");
    }
    
    /**
	 * @return Returns the phoneNumber.
	 */
	public String getPhoneNumber() {
		return _cleanPhoneNumber;
	}
	/**
	 * @param number The phoneNumber to set.
     * The phone number will be stripped of any extraneous punctuation.
	 * @throws PhoneNumberException
	 */
	public void setPhoneNumber(String number) {
        _cleanPhoneNumber = cleanPhoneNumber(number);
	}
    
    public String toString() {
        return _cleanPhoneNumber;
    }
    
    public boolean equals(Object obj) {
        return _cleanPhoneNumber.equals(obj);
    }
    
    public int hashCode() {
        return _cleanPhoneNumber.hashCode();
    }
}
