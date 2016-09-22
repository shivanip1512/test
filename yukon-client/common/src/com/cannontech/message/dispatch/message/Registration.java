package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */
public class Registration extends com.cannontech.message.util.Message 
{
	private String appName;
	private int appIsUnique;
	private int appExpirationDelay;
   
    private long regID = System.currentTimeMillis();
   
/**
 * Registration constructor comment.
 */
public Registration() {
	super();
} 

public long getRegID()
{
   return regID;
}

/**
 * This method was created in VisualAge.
 * @return int
 */
public int getAppExpirationDelay() {
	return appExpirationDelay;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getAppIsUnique() {
	return appIsUnique;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getAppName() {
	return appName;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setAppExpirationDelay(int newValue) {
	this.appExpirationDelay = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setAppIsUnique(int newValue) {
	this.appIsUnique = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setAppName(String newValue) {
	this.appName = newValue;
}

@Override
public String toString() {
    return "Registration [appName=" + appName + ", appIsUnique=" + appIsUnique + 
            ", appExpirationDelay=" + appExpirationDelay + ", regID=" + regID + "]";
}
}
