package com.cannontech.selenium.test.asetup;

import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.stars.EnergyCompanySolvent;
import com.cannontech.selenium.test.common.CommonHelper;

/**
 * This class creates the Energy Company QA_Test, then edits the Company
 * @author kevin.krile
 */
public class TestECSetupSelenium extends SolventSeleniumTestCase{
	
    private static final String rolePropertyEditorFormAction = "roleEditor/update";
    private static final String createECFormAciton = "energyCompany/create";
    
	/**
	 * This method logs in as DefaultCTI/$cti_default
	 */
	public void init(){
		start();
		LoginLogoutSolvent loginlogoutsolvent = new LoginLogoutSolvent();
		loginlogoutsolvent.cannonLogin("DefaultCTI", "$cti_default");
	}
	/**
	 * edits the Yukon Grp yukon role property
	 * logs into setup.jsp instead of login.jsp
	 */
	@Test
	public void activateStars(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("User/Group");
		common.clickLinkByName("Yukon Grp");
		common.clickLinkByName("Yukon");
		common.enterInputText(rolePropertyEditorFormAction, "values[SMTP_HOST]", "10.106.33.60");
		String s = common.getBaseURL().split("QA")[1].substring(0,3);
		String machineIpAddress = "10.106.33." + s;
		common.enterInputText(rolePropertyEditorFormAction, "values[CAP_CONTROL_MACHINE]", machineIpAddress);
		common.enterInputText(rolePropertyEditorFormAction, "values[DISPATCH_MACHINE]", machineIpAddress);
		common.enterInputText(rolePropertyEditorFormAction, "values[LOADCONTROL_MACHINE]", machineIpAddress);
		common.enterInputText(rolePropertyEditorFormAction, "values[MACS_MACHINE]", machineIpAddress);
		common.enterInputText(rolePropertyEditorFormAction, "values[NOTIFICATION_HOST]", machineIpAddress);
		common.enterInputText(rolePropertyEditorFormAction, "values[PORTER_MACHINE]", machineIpAddress);
        common.clickFormButton(rolePropertyEditorFormAction, "save");

		common.end();
	}
	
	/**
	 * Creates an Energy Company called QA_Test
	 */
	@Test
	public void createEnergyCompany(){
		init();
		CommonSolvent common = new CommonSolvent();
		
		PopupMenuSolvent primaryPopup = PickerFactory.createPopupMenuSolvent("primaryOperatorGroup", PickerType.SingleSelect);
		PopupMenuSolvent adtlPopup = PickerFactory.createPopupMenuSolvent("additionalOperatorGroup", PickerType.MultiSelect);
		PopupMenuSolvent resPopup = PickerFactory.createPopupMenuSolvent("residentialGroup", PickerType.MultiSelect);
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
		common.clickFormButton("energyCompany/new", "create");

		Assert.assertEquals("System Administration: Create Energy Company", common.getPageTitle());
		common.enterInputText(createECFormAciton, "name","QA_Test");
		common.enterInputText(createECFormAciton, "email", "QA_TestAdmin@gmail.com");
		common.selectDropDownMenu(createECFormAciton, "defaultRouteId", "a_CCU-711");
		
		common.enterInputText(createECFormAciton, "adminUsername", "starsop");
		common.enterInputText(createECFormAciton, "adminPassword1", "starsop");
		common.enterInputText(createECFormAciton, "adminPassword2", "starsop");
		
		primaryPopup.openPickerPopup();
		primaryPopup.clickMenuItem("STARS Operators Grp");
		
		adtlPopup.openPickerPopup();
		adtlPopup.clickMenuItem("Esub Operators Grp");
		adtlPopup.clickButton("OK");
		
		resPopup.openPickerPopup();
		resPopup.clickMenuItem("STARS Residential Customers Grp");
		resPopup.clickMenuItem("Residential Customers Grp");
		resPopup.clickButton("OK");

        common.clickFormButton(createECFormAciton, "save");
		
		Assert.assertEquals("System Administration: General Info (QA_Test)", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("Energy Company QA_Test Created Successfully"));
		common.end();
	}
	
	/**
	 * Edits Energy Company QA_Test
	 */
	@Test
	public void editEnergyCompany(){
	    final String editECFormAction = "update";
	    
		init();
		EnergyCompanySolvent energy = new EnergyCompanySolvent();
		CommonSolvent common = new CommonSolvent();
		
		common.clickLinkByName("System Administration");
		common.clickLinkByName("Energy Company");
		Assert.assertEquals(true, common.isTextPresent("QA_Test"));
		common.clickLinkByName("QA_Test");
		Assert.assertEquals("System Administration: General Info (QA_Test)", common.getPageTitle());
		Assert.assertEquals(true, common.isTextPresent("No Member Energy Companies"));
		Assert.assertEquals(true, common.isTextPresent("QA_TestAdmin@gmail.com"));
		Assert.assertEquals(true, common.isTextPresent("a_CCU-711"));
		
		common.assertEqualsLinkTextNotPresent("Warehouses");
		common.assertEqualsLinkTextNotPresent("Service Companies");
		common.assertEqualsLinkTextNotPresent("Schedules");
		
		common.clickFormButton(editECFormAction, "edit");
		Assert.assertEquals("System Administration: Edit General Info (QA_Test)", common.getPageTitle());
		common.enterInputText(editECFormAction, "address.locationAddress1", "123 Addr 1");
		common.enterInputText(editECFormAction, "address.locationAddress2", "456 Addr 2");
		common.enterInputText(editECFormAction, "address.cityName", "QA_City");
		common.enterInputText(editECFormAction, "address.stateCode", "MN");
		common.enterInputText(editECFormAction, "address.zipCode", "99123");
		common.enterInputText(editECFormAction, "address.county", "QA_Cnty");
		common.enterInputText(editECFormAction, "phone", "800-555-1212");
		common.enterInputText(editECFormAction, "fax", "800-555-2323");
		common.clickFormButton(editECFormAction, "save");

		Assert.assertEquals(true, common.isTextPresent("General Info Updated Successfully"));
		Assert.assertEquals("System Administration: General Info (QA_Test)", common.getPageTitle());
		energy.removeLoginGroup("Esub Operators Grp");
		Assert.assertEquals(true, common.isTextPresent("Operator Groups Updated Successfully"));
		energy.removeLoginGroup("Residential Customers Grp");
		Assert.assertEquals(true, common.isTextPresent("Residential Customer Groups Updated Successfully"));
		common.end();
	}
	/**
	 * Re-building the indices
	 */
	@Test
	public void buildIndexes(){
		init();
		CommonSolvent common = new CommonSolvent();
		common.clickLinkByName("Manage Indexes");
		CommonHelper helper = new CommonHelper();
		helper.buildAllIndexes();
		common.end();
	}
}