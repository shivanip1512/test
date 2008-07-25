package com.cannontech.web.navigation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author jdayton
 *
 * Used to store navigation state between pages
 */
public class CtiNavObject implements Serializable{
	private String currentPage;
	private String previousPage;

	private String moduleExitPage = null;
	private String moduleRedirectPage;
	private String moduleLabel;
	private String preservedAddress;
	
	/**
	 * This field is for use only in STARS.
	 * It is used to aid in member management for Energy Company Administration.
	 */
	private boolean memberECAdmin;
    private boolean internalLogin = false;
    
    private Stack<String> history;
	
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
	
	public String getPreviousPageForSearch() {
	    String returnURL = getPreviousPage();
    	if(returnURL.contains("tabledata")){
            Iterator<String> history = getHistory().iterator();
            while(history.hasNext()){
                String next = history.next();
                if(!next.contains("tabledata")){
                    returnURL = next;
                    break;
                }
            }
        }
    	return returnURL;
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
	
	/**
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
	/**
	 * @return
	 */
	public String getModuleExitPage() {
		return moduleExitPage;
	}

	/**
	 * @param string
	 */
	public void setModuleExitPage(String string) {
		moduleExitPage = string;
	}

	/**
	 * @return
	 */
	public String getModuleRedirectPage() {
		return moduleRedirectPage;
	}

	/**
	 * @param string
	 */
	public void setModuleRedirectPage(String string) {
		moduleRedirectPage = string;
	}

	/**
	 * @return
	 */
	public String getModuleLabel() {
		return moduleLabel;
	}

	/**
	 * @param string
	 */
	public void setModuleLabel(String string) {
		moduleLabel = string;
	}

    public Stack<String> getHistory() { 
        if (history == null)
            history = new Stack<String>();
        return history;
    }
    
    public void clearHistory() {
        getHistory().clear();
    }
    
    public boolean isInternalLogin() {
        return internalLogin;
    }

    public void setInternalLogin(boolean internalLogin) {
        this.internalLogin = internalLogin;
    }

    public String getPreservedAddress() {
        return preservedAddress;
    }

    public void setPreservedAddress(String preservedAddress) {
        this.preservedAddress = preservedAddress;
    }

}