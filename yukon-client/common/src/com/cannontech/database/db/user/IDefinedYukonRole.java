package com.cannontech.database.db.user;

/**
 * @author rneuharth
 *
 * Represents a defined role that a Group or User has.
 * 
 */
public interface IDefinedYukonRole
{
	Integer getRoleID();
	
	String getValue();
	void setValue( String value_ );
	
	Integer getRolePropertyID();
}
