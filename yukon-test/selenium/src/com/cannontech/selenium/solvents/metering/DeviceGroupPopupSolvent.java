/**
 * 
 */
package com.cannontech.selenium.solvents.metering;

import org.apache.log4j.Logger;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;

/**
 * This class handle the Tree structure and any other functionalities in Select Device Group popup window.
 * <br> 
 * @author anuradha.uduwage
 *
 */
public class DeviceGroupPopupSolvent extends MeteringSolvent {
	
	private static final Logger log = Logger.getLogger(AbstractSolvent.class.getName());
	private static int treeLoadWaitTime = 10000;

	/**
	 * Method click on the cross (X) to close the popup window.
	 * @return
	 */
	public DeviceGroupPopupSolvent clickCloseIcon() {
		String closeIconLocator = "//div[@class='x-tool x-tool-close']";
		selenium.waitForElement(closeIconLocator);
		if(!selenium.isElementPresent(closeIconLocator))
			throw new SolventSeleniumException("Close (X) icon on pop window is not available");
		selenium.click(closeIconLocator);
		return this;
	}
	
	/**
	 * Method click the cancel button at the bottom of the popup window.
	 * @return
	 */
	public DeviceGroupPopupSolvent clickCancelButton() {
		String cancelBtnLocator = "//span[@class='ui-button-text' and contains (., 'Cancel')]";
		selenium.waitForElement(cancelBtnLocator);
		if(!selenium.isElementPresent(cancelBtnLocator))
			throw new SolventSeleniumException("Cancel Button is not available in Device Group Popup window");
		selenium.click(cancelBtnLocator);
		return this;
	}
	
	/**
	 * Method click the Select Group button at the bottom of the popup window.
	 * @return
	 */
	public DeviceGroupPopupSolvent clickSelectGroupButton() {
		String grpBtnLocator = "//span[@class='ui-button-text' and contains (., 'Select Group')]";
		selenium.waitForElement(grpBtnLocator);
		if(!selenium.isElementPresent(grpBtnLocator))
			throw new SolventSeleniumException("Select Group button is not available in Device Group Popup window");
		selenium.click(grpBtnLocator);
		return this;
	}
	
	/**
	 * Method click the Add Device to Selected groups at the bottom of the add to groups popup window.
	 * @return
	 */
	public DeviceGroupPopupSolvent clickAddDeviceToSelectedGroupButton() {
		String buttonLocator = "//span[@class='ui-button-text' and contains (., 'Add Device To Selected Groups')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Add Device to Selected Groups button is not available");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Method click the Select Device Group button at the bottom of Select devices by device group popup window.
	 * @return
	 */
	public DeviceGroupPopupSolvent clickSelectDeviceGroupButton() {
		String buttonLocator = "//span[@class='ui-button-text' and contains (., 'Select Device Group')]";
		selenium.waitForElement(buttonLocator);
		if(!selenium.isElementPresent(buttonLocator))
			throw new SolventSeleniumException("Select Device Group button is not available");
		selenium.click(buttonLocator);
		return this;
	}
	
	/**
	 * Expand the tree node based on the node value.
	 * @param nodeName Text value of the node.
	 * @return
	 */
	public DeviceGroupPopupSolvent expandByName(String nodeName) {
		
		String expandLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName 
								+ "')]/preceding-sibling::span[@class='dynatree-expander']";
		String endExpandLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName 
								+ "')]/preceding-sibling::span[@class='dynatree-icon']";
		
		if(selenium.isElementPresent(expandLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(expandLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(expandLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(expandLocator);
		}
		else if(selenium.isElementPresent(endExpandLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(endExpandLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(endExpandLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(endExpandLocator);			
		}
		return this;
	}
	
	/**
	 * Collapse the tree node based on the node value.
	 * @param nodeName Text value of the node.
	 * @return
	 */
	public DeviceGroupPopupSolvent collapseByName(String nodeName) {
		
		String collapseLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName 
								+ "')]/preceding-sibling::span[@class='dynatree-icon']";
		String endCollapseLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName 
								+ "')]/preceding-sibling::span[@class='dynatree-expander']";		
		if(selenium.isElementPresent(collapseLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(collapseLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(collapseLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(collapseLocator);
		}
		else if(selenium.isElementPresent(endCollapseLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(endCollapseLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(endCollapseLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(endCollapseLocator);			
		}		
		return this;
	}
	
	/**
	 * Select a expanded node by click on it.
	 * @param nodeName name of the node.
	 * @return
	 */
	public DeviceGroupPopupSolvent selectTreeNode(String nodeName) {
		String nodeLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName + "')]";
		selenium.waitForElement(nodeLocator, treeLoadWaitTime);
		if(!selenium.isElementPresentAndVisible(nodeLocator, treeLoadWaitTime))
			throw new SolventSeleniumException(" Unable to locate Node With name " + nodeName + " expand the node before selecting it.");
		
		try {
			selenium.click(nodeLocator);
		} catch (Exception e) {
			log.error(e.getStackTrace());
		}
		
		//selenium.click(nodeLocator);
		return this;
	}
	
	/**
	 * Expand the child node based on its parent, but child node should be visible 
	 * (available on DOM) prior to selection. 
	 * @param parentNode name of the parent node.
	 * @param childNode name of the child node.
	 * @return
	 */
	public DeviceGroupPopupSolvent expandChildByParent(String parentNode, String childNode) {
		String expandLocator = this.constructNodeXpath(parentNode) + this.constructChildNodeXpath(childNode) 
								+ "/preceding-sibling::span[@class='dynatree-expander']";
		String endExpandLocator = this.constructNodeXpath(parentNode) + this.constructChildNodeXpath(childNode) 
								+ "/preceding-sibling::span[@class='dynatree-icon']";

		if(selenium.isElementPresent(expandLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(expandLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(expandLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(expandLocator);
		}
		else if(selenium.isElementPresent(endExpandLocator, treeLoadWaitTime)) {
			selenium.waitForElementToBecomeVisible(endExpandLocator, treeLoadWaitTime);
			if(!selenium.isElementPresentAndVisible(endExpandLocator, treeLoadWaitTime))
				throw new SolventSeleniumException("This Node have may not have any children to expand.");
			selenium.click(endExpandLocator);			
		}
		
		return this;
	}
	
	
	public boolean checkTamperFlagWithNameExists(String tamperFlagName) {
		String errorMsg = "//div[@class='errorRed' and contains(text(), 'Tamper Flag Monitor with name" + "\"" + tamperFlagName + "\"" + "already exists.')]";
		return selenium.isElementPresent(errorMsg);
	}
	
	/**
	 * Construct Xpath for the root node based on given node name.
	 * @param node name of the node.
	 * @return
	 */
	private String constructNodeXpath(String node) {
		String rootNode = "//ul//a[@class='dynatree-title' and contains(., '" + node + "')]";
		return rootNode;
	}
	
	/**
	 * Construct Xpath for the child node, but method should be use in conjunction with 
	 * {@link DeviceGroupPopupSolvent#constructNodeXpath(String)} to construct full length of xpath.
	 * @param childNode name of the child node.
	 * @return
	 */
	private String constructChildNodeXpath(String childNode) {
		String rootChild = "/following::a[@class='dynatree-title' and contains(., '" + childNode + "')]";
		return rootChild;
	}
		
}
