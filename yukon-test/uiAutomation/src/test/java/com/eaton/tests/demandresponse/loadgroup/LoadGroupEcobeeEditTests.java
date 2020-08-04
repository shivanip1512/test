package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupEditPage;

public class LoadGroupEcobeeEditTests extends SeleniumTestSetup {

	private DriverExtensions driverExt;
	private Integer id;
	private String name;
	private LoadGroupEditPage editPage;
	private LoadGroupDetailPage detailsPage;

	@BeforeClass(alwaysRun = true)
	public void beforeClass() {
		driverExt = getDriverExt();
		Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();
		JSONObject response = pair.getValue1();
		name = response.getString("name");
		id = response.getInt("id");
	}

	@BeforeMethod
	public void beforeTest() {
		navigate(Urls.DemandResponse.LOAD_GROUP_EDIT + id + Urls.EDIT);
		editPage = new LoadGroupEditPage(driverExt, id);
	}

	@Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_pageTitleCorrect() {

		final String EXPECTED_TITLE = "Edit Load Group: " + name;

		String actualPageTitle = editPage.getPageTitle();

		assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_requiredFieldsOnlySuccess() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited Ecobee Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";

		editPage.getName().setInputValue(name);

		editPage.getSaveBtn().click();

		waitForPageToLoad("Load Group: " + name, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);

		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_name_requiredValidation() {
		final String EXPECTED_MSG = "Name is required.";

		editPage.getName().setInputValue(" ");
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_name_alreadyExists() {
		Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();
		JSONObject response = pair.getValue1();

		String name = response.getString("name");

		final String EXPECTED_MSG = "Name must be unique.";

		editPage.getName().setInputValue(name);
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_name_invalidChars() {
		final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";

		editPage.getName().setInputValue("/eco,|group ");
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getName().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_kWCapacity_minValidation() {
		final String EXPECTED_MSG = "Must be between 0 and 99,999.999.";

		editPage.getkWCapacity().setInputValue("-1");
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getkWCapacity().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_kWCapacity_maxValidation() {
		final String EXPECTED_MSG = "Must be between 0 and 99,999.999.";

		editPage.getkWCapacity().setInputValue("100000.00");
		editPage.getSaveBtn().click();

		String actualMsg = editPage.getkWCapacity().getValidationError();
		assertThat(actualMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_cancelNavigatesCorrectly() {
		final String EXPECTED_MSG = "Load Group: " + name;
		editPage.getCancelBtn().click();

		waitForPageToLoad("Load Group: " + name, Optional.empty());

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getPageTitle();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_updateAllFieldsSuccess() {
		String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
		String name = "AT Edited Ecobee Ldgrp " + timeStamp;
		final String EXPECTED_MSG = name + " saved successfully.";

		editPage.getName().setInputValue(name);
		editPage.getkWCapacity().setInputValue("2345");
		editPage.getDisableGroup().setValue(true);
		editPage.getDisableControl().setValue(true);
		editPage.getSaveBtn().click();

		LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
		String userMsg = detailsPage.getUserMessage();

		assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_GeneralSectionTitleCorrect() {

		Section general = editPage.getPageSection("General");
		assertThat(general.getSection()).isNotNull();
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_OptionalAttributeSectionTitleCorrect() {

		Section optAttr = editPage.getPageSection("Optional Attributes");
		assertThat(optAttr.getSection()).isNotNull();
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_GeneralSectionLabelsCorrect() {
		String sectionName = "General";
		List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));

		List<String> actualLabels = editPage.getPageSection(sectionName).getSectionLabels();

		assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	}

	@Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpEcobeeEdit_OptionalAttrSectionLabelsCorrect() {
		String sectionName = "Optional Attributes";
		List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));

		List<String> actualLabels = editPage.getPageSection(sectionName).getSectionLabels();

		assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	}

}