package com.cannontech.roles;



/**
 * Base role and property ids for each category of roles, keep this in sync with the database scripts or there will be trouble, shurley.
 * 
 * Extend this interface for each category of roles.
 * Use the role id base for each category as the initial value when enumerating the categories role ids.
 * Use the property id base for each category as the inital value when enumerating the categories role property ids.
 * @author Aaron Lauinger
 */
interface RoleDefs {
	/* 
	* The base roleids for each category
	*/	 
	static final int YUKON_ROLEID_BASE = -1;
	static final int APPLICATION_ROLEID_BASE = -100;
	static final int OPERATOR_ROLEID_BASE = -200;
	static final int CONSUMER_ROLEID_BASE = -400;	
	static final int CBC_ROLEID_BASE = -700;
	static final int NOTIFICATIONS_ROLEID_BASE = -800;
	static final int LM_ROLEID_BASE = -900;
    static final int CBC_ONELINE_ROLEID_BASE = -1000;
	
	/*
	 * The base rolepropertyids for each category
	*/ 
	static final int YUKON_PROPERTYID_BASE = -1000;
	static final int APPLICATION_PROPERTYID_BASE = -10000;
	static final int OPERATOR_PROPERTYID_BASE = -20000;
	static final int CONSUMER_PROPERTYID_BASE = -40000;
	
	static final int CBC_PROPERTYID_BASE = -70000;
	static final int NOTIFICATIONS_PROPERTYID_BASE = -80000;
	static final int LM_PROPERTYID_BASE = -90000;
    static final int CBC_ONELINE_PROPERTYID_BASE = -100000;

}