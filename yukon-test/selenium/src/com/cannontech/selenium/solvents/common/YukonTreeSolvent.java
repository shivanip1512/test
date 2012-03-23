/**
 * 
 */
package com.cannontech.selenium.solvents.common;

import org.apache.log4j.Logger;

import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.metering.DeviceGroupPopupSolvent;

/**
 * TODO: Not completed at all, find common grounds between tree structures in 
 * CapControl and rest of the areas (metering page).
 * @author anuradha.uduwage
 *
 */
public class YukonTreeSolvent extends AbstractSolvent {

	private static final Logger log = Logger.getLogger(AbstractSolvent.class.getName());
	private static int treeLoadWaitTime = 10000;
	/**
	 * @param params
	 */
	public YukonTreeSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
	}

	/**
	 * Expand the tree node based on the node value.
	 * @param nodeName Text value of the node.
	 * @return
	 */
	public YukonTreeSolvent expandByName(String nodeName) {
		
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
	public YukonTreeSolvent collapseByName(String nodeName) {
		
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
	public YukonTreeSolvent selectTreeNode(String nodeName) {
		String nodeLocator = "//ul//a[@class='dynatree-title' and contains(., '" + nodeName + "')]";
		selenium.waitForElement(nodeLocator);
		if(!selenium.isElementPresent(nodeLocator))
			throw new SolventSeleniumException(" Unable to locate Node witn name " + nodeName + " expand the node before selecting it.");
		try {
			selenium.clickWithoutWait(nodeLocator);
		} catch (Exception e) {
			log.error(e.getStackTrace());
		}
		return this;
	}
	
	/**
	 * Expand the child node based on its parent, but child node should be visible 
	 * (available on DOM) prior to selection. 
	 * @param parentNode name of the parent node.
	 * @param childNode name of the child node.
	 * @return
	 */
	public YukonTreeSolvent expandChildByParent(String parentNode, String childNode) {
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
