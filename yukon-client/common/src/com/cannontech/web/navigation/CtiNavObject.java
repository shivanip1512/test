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
	
	public CtiNavObject()
	{
		setCurrentPage("");
		setPreviousPage("");
	}
	
	public String getCurrentPage()
	{
		return currentPage;
	}
	
	public String getPreviousPage()
	{
		return previousPage;
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
}