package com.cannontech.roles;

import com.cannontech.database.cache.functions.AuthFuncs;

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
	static final int CICUSTOMER_ROLEID_BASE = -300;
	static final int CONSUMER_ROLEID_BASE = -400;	
	static final int ANALYSIS_ROLEID_BASE = -600;
	static final int CBC_ROLEID_BASE = -700;
	static final int IVR_ROLEID_BASE = -800;
	
	
	/*
	 * The base rolepropertyids for each category
	*/ 
	static final int YUKON_PROPERTYID_BASE = -1000;
	static final int APPLICATION_PROPERTYID_BASE = -10000;
	static final int OPERATOR_PROPERTYID_BASE = -20000;
	static final int CICUSTOMER_PROPERTYID_BASE = -30000;
	static final int CONSUMER_PROPERTYID_BASE = -40000;
	
	static final int ANALYSIS_PROPERTYID_BASE = -60000;
	static final int CBC_PROPERTYID_BASE = -70000;
	static final int IVR_PROPERTYID_BASE = -80000;
}