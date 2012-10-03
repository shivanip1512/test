package com.cannontech.message.dispatch.message;

import com.cannontech.common.util.CtiUtilities;

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
    private DbChangeType dbChangeType = DbChangeType.NONE;

    //PAOCategory if PAObject, everythingelse does not matter
    private String category = null;

    //PAOType if PAOBject, PointType if Point, (other types if type is present)
    private String objectType = null; 

    //invalid change
    public static final int CHANGE_INVALID_ID = -1;     


    //Possible values for the database field
    public static final int USES_NEW_CATEGORY_ENUM = -1000; 
    public static final int CHANGE_PAO_DB = 0;
    public static final int CHANGE_POINT_DB = 1;
    public static final int CHANGE_STATE_GROUP_DB = 2;
    public static final int CHANGE_NOTIFICATION_GROUP_DB = 3;
//  public static final int CHANGE_NOTIFICATION_RECIPIENT_DB = 4;
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
    public static final int CHANGE_CBC_STRATEGY_DB = 25;
    public static final int CHANGE_PAO_SCHEDULE_DB = 26;
    public static final int CHANGE_SETTLEMENT_DB = 27;
    public static final int CHANGE_SERVICE_COMPANY_DB = 28;
    public static final int CHANGE_SERVICE_COMPANY_DESIGNATION_CODE_DB = 29;
    public static final int CHANGE_WORK_ORDER_DB = 30;
    public static final int CHANGE_CBC_ADDINFO_DB = 31;
    public static final int CHANGE_WEB_CONFIG_DB = 33;
    public static final int CHANGE_STARS_PUBLISHED_PROGRAM_DB = 34;
    public static final int CHANGE_IVVC_ZONE = 35;
    public static final int CHANGE_STATIC_PAO_INFO_DB = 36;
    public static final int CHANGE_USER_GROUP_DB = 37;
    public static final int CHANGE_YUKON_PAOBJECT_ENCRYPTION_KEY_DB = 38;
    public static final int CHANGE_ENCRYPTION_KEY_DB = 39;
    public static final int CHANGE_YUKON_SETTING_DB = 40;

    /*Possible values for the Categories field
     *NOTE: If you are using the CHANGE_PAO_DB database field you will
     *find the needed fields in PAOGRoups.java
     */
    public static final String CAT_POINT = "Point";
    public static final String CAT_STATEGROUP ="StateGroup";
    public static final String CAT_NOTIFCATIONGROUP = "NotificationGroup";
//  public static final String CAT_NOTIFICATIONRECIPIENT = "NotificationRecipient";
    public static final String CAT_ALARMCATEGORY = "AlarmCategory";
    public static final String CAT_CUSTOMERCONTACT = "CustomerContact";
    public static final String CAT_GRAPH = "Graph";
    public static final String CAT_HOLIDAY_SCHEDULE = "HolidaySchedule";
    public static final String CAT_ENERGY_COMPANY = "EnergyCompany";
    public static final String CAT_YUKON_USER = "YukonUser";
    public static final String CAT_USER_GROUP = "UserGroup";
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
    public static final String CAT_CBC_STRATEGY = "CBC Strategy";
    public static final String CAT_PAO_SCHEDULE = "PAO Schedule";
    public static final String CAT_SETTLEMENT = "Settlement";
    public static final String CAT_DEVICE_CONFIG = "Device Config";
    public static final String CAT_STATIC_PAO_INFO_DB = "Static Pao Info";
    public static final String CAT_YUKON_PAOBJECT_ENCRYPTION_KEY_DB = "Yukon Paobject Encryption Key";
    public static final String CAT_ENCRYPTION_KEY_DB = "Encryption Key";
    public static final String CAT_YUKON_SETTING_DB = "Yukon Setting";

    // Categories used by stars
    public static final String CAT_CUSTOMER_ACCOUNT = "CustomerAccount";
    public static final String CAT_LM_PROGRAM = "LMProgram";
    public static final String CAT_LM_HARDWARE = "LMHardware";
    public static final String CAT_APPLIANCE = "Appliance";
    public static final String CAT_CUSTOMER_LOGIN = "CustomerLogin";
    public static final String CAT_SERVICE_COMPANY = "ServiceCompany";
    public static final String CAT_SERVICE_COMPANY_DESIGNATION_CODE = "ServiceCompany";
    public static final String CAT_WORK_ORDER = "WorkOrder";
    public static final String CAT_CBC_ADDINFO = "CB Additional Info";
    public static final String CAT_WEB_CONFIG = "Web Configuration";
    public static final String CAT_STARS_PUBLISHED_PROGRAM = "Stars Published Program";
    
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
public DBChangeMsg( int id_, int database_, String category_, String objectType_, DbChangeType dbChangeType)
{
    super();

    setId(id_);
    setDatabase(database_);
    setCategory(category_);
    setObjectType(objectType_);
    setDbChangeType(dbChangeType);
    setPriority(15);
}

/**
 * Defaults the ObjectType to a (none) string
 * 
 */
public DBChangeMsg( int id_, int database_, String category_, DbChangeType dbChangeType)
{
    this( id_, database_, category_, CtiUtilities.STRING_NONE, dbChangeType);
}

public java.lang.String getCategory() {
    return category;
}

public int getDatabase() {
    return database;
}

public int getId() {
    return id;
}

public java.lang.String getObjectType() {
    return objectType;
}

public DbChangeType getDbChangeType() {
    return dbChangeType;
}

public void setCategory(java.lang.String newCategory) {
    category = newCategory;
}

public void setDatabase(int newValue) {
    this.database = newValue;
}

public void setId(int newId) {
    id = newId;
}

public void setObjectType(java.lang.String newObjectType) {
    objectType = newObjectType;
}

public void setDbChangeType(DbChangeType dbChangeType) {
    this.dbChangeType = dbChangeType;
}

public String toString() 
{
    StringBuilder retString = new StringBuilder(150);
    retString.append("DbChange: Database=" + getDatabase());
    retString.append("; Type=" + getDbChangeType());
    retString.append("; ID=" + getId());
    retString.append("; Category=" + getCategory());
    retString.append("; ObjectType=" + getObjectType()); 

    return retString.toString();
}
}
