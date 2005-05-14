/*
 * Created on Apr 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.web.navigation;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CtiNavObject 
{
	private String currentPage;
	private String previousPage;
	
	/*
	 * This field is for use only in STARS.
	 * It is used to aid in member management for Energy Company Administration.
	 */
	private boolean memberECAdmin;
	
	public CtiNavObject()
	{
		setCurrentPage("");
		setPreviousPage("");
		setMemberECAdmin(false);
	}
	
	public String getCurrentPage()
	{
		return currentPage;
	}
	
	public String getPreviousPage()
	{
		return previousPage;
	}
	
	public boolean isMemberECAdmin()
	{
		return memberECAdmin;
	}
	
	public void setCurrentPage(String page)
	{
		currentPage = page;
	}
	
	public void setPreviousPage(String page)
	{
		previousPage = page;
	}
	
	/*
	 * This is the useful one.  Takes a new page string and sets
	 * it as the current page, first moving the old current page to 
	 * the previous field.
	 */
	public void setNavigation(String page)
	{
		previousPage = currentPage;
		currentPage = page;
	}
	
	public void setMemberECAdmin(boolean isManaging)
	{
		memberECAdmin = isManaging;
	}
}