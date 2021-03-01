package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeAsgmtTypes;
import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.modals.attributes.EditAttributeAssignmentsModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;
import com.github.javafaker.Faker;

public class EditAttributeAssignmentTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;
    private Faker faker;
    JSONObject attrAsgmtResponse;
    private String attrName;
    private Integer attrId;
    private String attrAsgmtPointType;
    private String attrAsgmtDeviceType;
    private Integer attrAsgmtPointOffset;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();
        
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());     
        
        Pair<JSONObject, JSONObject> attribute = map.get("Attribute");
        Pair<JSONObject, JSONObject> attrAsgmt = map.get("AttributeAsgmt");

        JSONObject attrResponse = attribute.getValue1();
        attrName = attrResponse.getString("name");
        attrId = attrResponse.getInt("customAttributeId");
        
        attrAsgmtResponse = attrAsgmt.getValue1();
        
        attrAsgmtPointType = attrAsgmtResponse.getString("pointType");
        attrAsgmtDeviceType= attrAsgmtResponse.getString("paoType");
        attrAsgmtPointOffset= attrAsgmtResponse.getInt("offset");
        
        navigate(Urls.Admin.ATTRIBUTES_LIST);
        page = new AttributesListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(page);
        }
        setRefreshPage(false);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeAssignment_Modal_TitleCorrect() {
        setRefreshPage(true);
        String expectedModalTitle = "Edit Attribute Assignment";
        EditAttributeAssignmentsModal modal = page.showEditAttrAsgmtAndWait(attrName);
        String actualModalTitle = modal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeAssignment_Cancel_ClosesModal() {
        setRefreshPage(true);

        EditAttributeAssignmentsModal modal = page.showEditAttrAsgmtAndWait(attrName);
        
        modal.clickCancelAndWait();

        assertThat(modal.isModalAvailable()).isFalse();
    }
    
    @Test(groups = {TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeAssignment_Field_ValuesCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        
        EditAttributeAssignmentsModal modal = page.showEditAttrAsgmtAndWait(attrName);
        
        AttributeAsgmtTypes.PointTypesUI pt = AttributeAsgmtTypes.PointTypesUI.valueOf(attrAsgmtPointType.toUpperCase());
        AttributeAsgmtTypes.PaoTypesUI dt = AttributeAsgmtTypes.PaoTypesUI.valueOf(attrAsgmtDeviceType.toUpperCase());
        
        softly.assertThat(modal.getAttributeName().getSelectedValue()).isEqualTo(attrName);
        softly.assertThat(modal.getDeviceType().getSelectedValue()).isEqualTo(dt.getPaoType());
        softly.assertThat(modal.getPointType().getSelectedValue()).isEqualTo(pt.getPointType());
        softly.assertThat(modal.getpointOffSet().getInputValue()).isEqualTo(attrAsgmtPointOffset.toString());
        softly.assertAll();
    }
    
    @Test(groups = {TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeAssignment_AllFields_Success() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> attrPair = AttributeService.createAttribute(Optional.empty());
        JSONObject res = attrPair.getValue1();
        Integer attrId = res.getInt("customAttributeId");
        String name = res.getString("name");
        
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithSpecificAssignment(AttributeAsgmtTypes.PaoTypes.MCTBROADCAST, AttributeAsgmtTypes.PointTypes.STATUS, 45, Optional.empty() );
        refreshPage(page);
        
        Pair<JSONObject, JSONObject> attribute = map.get("Attribute");

        JSONObject attrResponse = attribute.getValue1();
        String attrName = attrResponse.getString("name");
        Integer pointOffset = faker.number().numberBetween(1, 99999999);
        
        EditAttributeAssignmentsModal modal = page.showEditAttrAsgmtAndWait(attrName);
        
        modal.getAttributeName().selectItemByValue(attrId.toString());
        modal.getDeviceType().selectItemByValue("VIRTUAL_SYSTEM");
        modal.getPointType().selectItemByValue("AnalogOutput");
        modal.getpointOffSet().setInputValue(pointOffset.toString());
        modal.clickOkAndWaitForModalToClose();
        
        assertThat(page.getAttrAsgmtErrorMsg()).isEqualTo("Attribute: " + name + " has been successfully assigned to the following device types: VIRTUAL SYSTEM.");
    }
    
    @Test(groups = {TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void editAttributeAssignment_AttrNameDeviceType_Validation() {
        setRefreshPage(true);
        Map<String, Pair<JSONObject, JSONObject>> map = AttributeService.createAttributeWithAssignment(Optional.empty());   
        refreshPage(page);
        
        Pair<JSONObject, JSONObject> pair = map.get("Attribute");
        
        JSONObject attr = pair.getValue1();
        String name = attr.getString("name");

        AttributeAsgmtTypes.PaoTypesUI dt = AttributeAsgmtTypes.PaoTypesUI.valueOf(attrAsgmtDeviceType.toUpperCase());
        String dtName = dt.name();
        String dtValue = dt.getPaoType();
        
        EditAttributeAssignmentsModal modal = page.showEditAttrAsgmtAndWait(name);
        
        modal.getAttributeName().selectItemByValue(attrId.toString());
        modal.getDeviceType().selectItemByValue(dtName);
        modal.getPointType().selectItemByValue(attrAsgmtPointType);
        modal.getpointOffSet().setInputValue(attrAsgmtPointOffset.toString());
        modal.clickOkAndWaitForModalToClose();
        
        assertThat(page.getAttrAsgmtErrorMsg()).isEqualToIgnoringCase("There was an error assigning Attribute: " + attrName + " to the following device types: " + dtValue + ". Please see logs for more detail.");
    }
}
