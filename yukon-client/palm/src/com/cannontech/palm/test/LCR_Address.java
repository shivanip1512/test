package com.cannontech.palm.test;

/**
 * Insert the type's description here.
 * Creation date: (5/30/2001 3:40:58 PM)
 * @author: 
 */
public class LCR_Address 
{
	private String Address_Name 	= null;
	private int Address_Section 	= 0;
	private int Address_Class 		= 0;
	private int Address_Division 	= 0;
/**
 * LCR_Address constructor comment.
 */
public LCR_Address()
{
	super();
}
/**
 * this one can be gotten rid of....
 */
public LCR_Address(String newAddressName)
{
	super();
	Address_Name = newAddressName;
}
/**
 * LCR_Address constructor comment.
 */
public LCR_Address(String typeAddy, int secAddy, int classAddy, int divAddy)
{
	super();
	
	String Address_Name 	= typeAddy;
	int Address_Section 	= secAddy;
	int Address_Class 		= classAddy;
	int Address_Division 	= divAddy;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public int getAddressClass() 
{
	return Address_Class;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public int getAddressDivision() 
{
	return Address_Division;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public String getAddressName() 
{
	return Address_Name;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public int getAddressSection() 
{
	return Address_Section;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public void setAddressClass(int newAddress) 
{
	Address_Class = newAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public void setAddressDivision(int newAddress) 
{
	Address_Division = newAddress;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 3:14:25 PM)
 * @return java.lang.String
 */
public void setAddressSection(int newAddress) 
{
	Address_Section = newAddress;
}
}
