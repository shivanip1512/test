package com.cannontech.message.dispatch.message;

/**
* This type was created by Cannon Technologies Inc.
*/
public class DBChangeMsg extends com.cannontech.message.util.Message 
{
	//Refers to the primary key of the type of DB that was changed.
	private int id;
	
	//which are of the database is affected
	private int database = CHANGE_INVALID_ID;

	//add, delete or update
	private int typeOfChange = CHANGE_INVALID_ID;

	//PAOCategory if PAObject, everythingelse does not matter
	private String category = null;

	//PAOType if PAOBject, PointType if Point, (other types if type is present)
	private String objectType = null; 

	//invalid change
	public static final int CHANGE_INVALID_ID = -1;		


	//Possible values for the database field
	public static final int CHANGE_PAO_DB = 0;
	public static final int CHANGE_POINT_DB = 1;
	public static final int CHANGE_STATE_GROUP_DB = 2;
	public static final int CHANGE_NOTIFICATION_GROUP_DB = 3;
//	public static final int CHANGE_NOTIFICATION_RECIPIENT_DB = 4;
	public static final int CHANGE_ALARM_CATEGORY_DB = 5;
	public static final int CHANGE_CONTACT_DB = 6;
	public static final int CHANGE_GRAPH_DB = 7;
	public static final int CHANGE_HOLIDAY_SCHEDULE_DB = 8;
	public static final int CHANGE_ENERGY_COMPANY_DB = 9;
	public static final int CHANGE_YUKON_USER_DB = 10;
	public static final int CHANGE_CUSTOMER_DB = 11;
	public static final int CHANGE_CUSTOMER_ACCOUNT_DB = 12;
	public static final int CHANGE_YUKON_IMAGE = 13;
	public static final int CHANGE_BASELINE_DB = 14;
	public static final int CHANGE_CONFIG_DB = 15;
	public static final int CHANGE_TAG_DB = 16;
	public static final int CHANGE_CI_CUSTOMER_DB = 17;
	public static final int CHANGE_LMCONSTRAINT_DB = 18;
	public static final int CHANGE_SEASON_SCHEDULE_DB = 20;
	public static final int CHANGE_TDC_DB = 21;
	public static final int CHANGE_DEVICETYPE_COMMAND_DB = 22;
	public static final int CHANGE_COMMAND_DB = 23;
	public static final int CHANGE_TOU_SCHEDULE_DB = 24;
	


	
	public static final String CAT_POINT = "Point";
	public static final String CAT_STATEGROUP ="StateGroup";
	public static final String CAT_NOTIFCATIONGROUP = "NotificationGroup";
//	public static final String CAT_NOTIFICATIONRECIPIENT = "NotificationRecipient";
	public static final String CAT_ALARMCATEGORY = "AlarmCategory";
	public static final String CAT_CUSTOMERCONTACT = "CustomerContact";
	public static final String CAT_GRAPH = "Graph";
	public static final String CAT_HOLIDAY_SCHEDULE = "HolidaySchedule";
	public static final String CAT_ENERGY_COMPANY = "EnergyCompany";
	public static final String CAT_YUKON_USER = "YukonUser";
	public static final String CAT_CUSTOMER = "Customer";
	public static final String CAT_CI_CUSTOMER = "CICustomer";
	public static final String CAT_YUKON_USER_GROUP = "YukonUserGroup";
	public static final String CAT_BASELINE = "BaseLine";
	public static final String CAT_CONFIG = "Config";
	public static final String CAT_TAG = "Tag";
	public static final String CAT_LMCONSTRAINT = "Load Constraint";
	public static final String CAT_LMSCENARIO = "Load Scenario";
	public static final String CAT_SEASON_SCHEDULE = "Season Schedule";
	public static final String CAT_DEVICETYPE_COMMAND = "DeviceType Command";
	public static final String CAT_COMMAND = "Command";
	public static final String CAT_TOU_SCHEDULE = "TOU Schedule";
	
	// Categories used by stars
	public static final String CAT_CUSTOMER_ACCOUNT = "CustomerAccount";
	public static final String CAT_LM_PROGRAM = "LMProgram";
	public static final String CAT_LM_HARDWARE = "LMHardware";
	public static final String CAT_APPLIANCE = "Appliance";
	public static final String CAT_CUSTOMER_LOGIN = "CustomerLogin";

	
	//THE FOLLOWING EXIST ONLY IN THE JAVA VERSION OF THIS MESSAGE!
	//HEED THIS WARNING AND DO NOT MIX W/C++ - MAYBE TEMPORARY
	public static final int CHANGE_DSM2_PROGRAM_DB = 50;

	//Possible values for the typeOfChange field	
	public static final int CHANGE_TYPE_ADD = 0;
	public static final int CHANGE_TYPE_DELETE = 1;
	public static final int CHANGE_TYPE_UPDATE = 2;
	
	public static final int RELOAD_ALL = 0;
/**
 * DBChangeMsg constructor comment.
 */
protected DBChangeMsg()
{
	super();
}
/**
 * DBChangeMsg constructor comment.
 */
public DBChangeMsg( int id_, int database_, String category_, String objectType_, int typeOfChange_ )
{
	super();

	setId(id_);
	setDatabase(database_);
	setCategory(category_);
	setObjectType(objectType_);
	setTypeOfChange(typeOfChange_);
	setPriority(15);

}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:08:45 AM)
 * @return java.lang.String
 */
public java.lang.String getCategory() {
	return category;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDatabase() {
	return database;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @return int
 */
public int getId() {
	return id;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @return java.lang.String
 */
public java.lang.String getObjectType() {
	return objectType;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @return int
 */
public int getTypeOfChange() {
	return typeOfChange;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:08:45 AM)
 * @param newCategory java.lang.String
 */
public void setCategory(java.lang.String newCategory) {
	category = newCategory;
}
/**
 * This method was created in VisualAge.
 * @param newValue int
 */
public void setDatabase(int newValue) {
	this.database = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @param newId int
 */
public void setId(int newId) {
	id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @param newObjectType java.lang.String
 */
public void setObjectType(java.lang.String newObjectType) {
	objectType = newObjectType;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2001 11:00:38 AM)
 * @param newTypeOfChange int
 */
public void setTypeOfChange(int newTypeOfChange) {
	typeOfChange = newTypeOfChange;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 4:47:17 PM)
 * @return java.lang.String
 */
public String toString() 
{
	String retString = getClass().getName() + ":  \n";
	retString += "Database:  " + getDatabase() + "\n";
	retString += "Type:  " + getTypeOfChange() + "\n";
	retString += "ID:  " + getId() + "\n";
	retString += "Category:  " + getCategory() + "\n";
	retString += "ObjectType:  " + getObjectType() + "\n";

	return retString;
}
}
