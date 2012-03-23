package com.cannontech.selenium.test.sysadminstars;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.stars.HardwarePageSolvent;
import com.cannontech.selenium.solvents.stars.StarsCommonSolvent;
/**
 * This Class adds, then edits the properties of the appliance category
 * @author Kevin Krile	
 */
public class TestECSubstationSelenium extends SolventSeleniumTestCase{
	/**
	 * This method logs in as starsop11/starsop11
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("starsop11", "starsop11");
	}
	/**
	 * This test logs in as starsop11 and creates 2 Substations
	 * This test depend on data in input file {@link TestECSubstationSelenium.xml}
	 */
	@Test
	public void createSubstation(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"QA_Substation2", "QA_Substation3"};

		common.clickLinkByName("System Administration");
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("Substations");
			Assert.assertEquals("System Administration: Substation To Route Mapping", common.getPageTitle());
			common.enterTextByAttribute("name","name", name[i]);
			common.clickButtonByAttribute("id", "addButton");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and links a substation to a route
	 */
	@Test
	public void linkSubstationToRoute(){
		init();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"QA_Substation2", "QA_Substation3"};

		common.clickLinkByName("System Administration");
		for(int i=0;i<name.length;i++){
			common.clickLinkByName("Substations");
			Assert.assertEquals("System Administration: Substation To Route Mapping", common.getPageTitle());
			common.selectDropDownMenu("routeMapping/edit", "selection_list",name[i]);
			common.selectDropDownMenu("routeMapping/viewRoute", "avroutes", "a_CCU-710A");
			common.clickButtonByAttribute("onclick", "javascript:SubstationToRouteMappings_addRoute()");
			common.selectDropDownMenu("routeMapping/viewRoute", "avroutes", "a_CCU-721");
			common.clickButtonByAttribute("onclick", "javascript:SubstationToRouteMappings_addRoute()");
			common.clickButtonByExactName("Apply");
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and adds the substation and route for QA_Test10
	 */
	@Test
	public void addSubstationAndRoute(){
		init();
		HardwarePageSolvent hardware= new HardwarePageSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"QA_Substation2", "QA_Substation3"};

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Substations And Routes");
		common.selectDropDownMenuByAttributeName("routeId", "a_CCU-721");
		hardware.clickAddByTableHeader("Assigned Routes");
		Assert.assertEquals("Route Added Successfully", common.getYukonText("Route Added Successfully"));
		//adds both substations
		for(int i=0;i<name.length;i++){
			//selecting the substation
			common.selectDropDownMenu("edit", "substationId", name[i]);
			//adding the substation
			hardware.clickAddByTableHeader("Assigned Substations");
			waitFiveSeconds();
			Assert.assertEquals("Substation Added Successfully", common.getYukonText("Substation Added Successfully"));
			common.clickLinkByName("Substations And Routes");
			Assert.assertEquals(true, common.isTextPresent(name[i]));
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and removes the substation and route from QA_Test10
	 */
	@Test
	public void removeSubstationAndRouteCompany(){
		init();
		StarsCommonSolvent stars = new StarsCommonSolvent();
		CommonSolvent common = new CommonSolvent();
		String[] name = {"QA_Substation2", "QA_Substation3"};

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isLinkPresent("QA_Test10"));
		common.clickLinkByName("QA_Test10");
		common.clickLinkByName("Substations And Routes");
		//Assert.assertEquals(false, common.isOptionPresent("routeId", route[0]));
		stars.removeRouteOrSubstation("a_CCU-721");
		Assert.assertEquals("Route Removed Successfully", common.getYukonText("Route Removed Successfully"));
		for(int i=0;i<name.length;i++){
			//removing the substation
			stars.removeRouteOrSubstation(name[i]);
			waitFiveSeconds();
			Assert.assertEquals(true, common.isTextPresent("Substation Removed Successfully"));
		}
		common.end();
	}
	/**
	 * This test logs in as starsop11 and deletes the substation
	 */
	@Test
	public void deleteSubstation(){
		init();
		CommonSolvent common = new CommonSolvent();

		common.clickLinkByName("System Administration");
		common.clickLinkByName("Substations");
		Assert.assertEquals("System Administration: Substation To Route Mapping", common.getPageTitle());
		common.selectDropDownMenuByAttributeName("selection_list","QA_Substation3");
		common.clickButtonByAttribute("value","Delete");
		common.end();
	}
}
