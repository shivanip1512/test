package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.attributes.AddAttributeAssignmentsModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;
import com.github.javafaker.Faker;

public class AddAttributeAssignmentTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;
    private Faker faker;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();        

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
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_FieldLabels_Correct() {
        SoftAssertions softly = new SoftAssertions();
        setRefreshPage(true);
        
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        
        List<String> labels = modal.getFieldLabels();
        
        softly.assertThat(labels.get(0)).isEqualTo("Attribute Name:");
        softly.assertThat(labels.get(1)).isEqualTo("Device Type(s):");
        softly.assertThat(labels.get(2)).isEqualTo("Point Type:");
        softly.assertThat(labels.get(3)).isEqualTo("Point Offset:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_Add_OpensCorrectModal() {
        setRefreshPage(true);
        String expectedModalTitle = "Add Attribute Assignment";
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        String actualModalTitle = modal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_PointOffset_RequiredValidation() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Point Offset must be between 0 and 99,999,999.";
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        modal.clickOk();
        String errorMsg = modal.getpointOffSet().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_PointOffset_MaxValueValidation() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Point Offset must be between 0 and 99,999,999.";
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        modal.getpointOffSet().setInputValue("100000000");
        modal.clickOk();
        String errorMsg = modal.getpointOffSet().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_PointOffset_MinValueValidation() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Point Offset must be between 0 and 99,999,999.";
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        modal.getpointOffSet().setInputValue("-1");
        modal.clickOk();
        String errorMsg = modal.getpointOffSet().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_PointOffset_GreaterThanZeroValidation() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "Must be a whole number greater than 0.";
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        modal.getpointOffSet().setInputValue("0");
        modal.getPointType().selectItemByValue("Status");
        modal.clickOk();
        String errorMsg = modal.getpointOffSet().getValidationError();
        
        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_PointType_ExpectedValuesCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();

        List<String> Styles = modal.getPointType().getOptionValues();

        softly.assertThat(Styles.size()).isEqualTo(9);
        softly.assertThat(Styles.get(0)).isEqualTo("Status");
        softly.assertThat(Styles.get(1)).isEqualTo("Analog");
        softly.assertThat(Styles.get(2)).isEqualTo("Pulse Accumulator");
        softly.assertThat(Styles.get(3)).isEqualTo("Demand Accumulator");
        softly.assertThat(Styles.get(4)).isEqualTo("Calc Analog");
        softly.assertThat(Styles.get(5)).isEqualTo("Status Output");
        softly.assertThat(Styles.get(6)).isEqualTo("Analog Output");
        softly.assertThat(Styles.get(7)).isEqualTo("System");
        softly.assertThat(Styles.get(8)).isEqualTo("Calc Status");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_SelectAnalogPointType_ExpectedValuesCorrect() {
        setRefreshPage(true);
        SoftAssertions softly = new SoftAssertions();
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        
        String point = TestDbDataType.DemandResponseData.LOADGROUP_RFNEXPRESSCOM_COPY_ID.getId().toString();
        
        SelectPointModal pointModal = modal.clickSearchAndWait();
        
        pointModal.selectPoint("Annual History", Optional.of(point));

        softly.assertThat(modal.getPointType().getSelectedValue()).isEqualTo("Analog");
        softly.assertThat(modal.getpointOffSet().getInputValue()).isEqualTo("2503");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_CalcAnalogPointType_Success() {
        setRefreshPage(true);
        
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());        

        JSONObject response = pair.getValue1();
        String name = response.getString("name");
        Integer id = response.getInt("customAttributeId");
        
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        
        modal.getAttributeName().selectItemByValue(id.toString());
        modal.getDeviceType().selectItemByText("Virtual System");
        modal.getPointType().selectItemByValue("CalcAnalog");
        modal.getpointOffSet().setInputValue("0");
        modal.clickOkAndWaitForModalToClose();
        
        assertThat(page.getAttrAsgmtErrorMsg()).isEqualTo("Attribute: " + name + " has been successfully assigned to the following device types: VIRTUAL SYSTEM.");
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void addAttributeAssignment_AllPointTypes_Success() {
        SoftAssertions softly = new SoftAssertions();
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());        

        JSONObject response = pair.getValue1();
        String name = response.getString("name");
        Integer id = response.getInt("customAttributeId");
        Integer pointOffset = faker.number().numberBetween(1, 99999999);
        AddAttributeAssignmentsModal modal = page.showAddAttrAsgmtAndWait();
        
        modal.getAttributeName().selectItemByValue(id.toString());
        modal.getPointType().selectItemByValue("CalcAnalog");
        modal.getpointOffSet().setInputValue(pointOffset.toString());
        modal.clickOkAndWaitForModalToClose();
        
        String errMsg = page.getAttrAsgmtErrorMsg();
        
        softly.assertThat(errMsg).containsOnlyOnce("Attribute: " + name + " has been successfully assigned to the following device types: ");
        softly.assertThat(errMsg).containsOnlyOnce("LCR-6200 RFN");
        softly.assertThat(errMsg).containsOnlyOnce("LCR-6600 RFN");
        softly.assertThat(errMsg).containsOnlyOnce("LCR-6700 RFN");
        softly.assertThat(errMsg).containsOnlyOnce("MCT Broadcast");
        softly.assertThat(errMsg).containsOnlyOnce("MCT-310IL");
        softly.assertThat(errMsg).containsOnlyOnce("MCT-420cL");
        softly.assertThat(errMsg).containsOnlyOnce("MCT-430A");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-410cL");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-420cD");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-420cL");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-420fL");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-430SL4");
        softly.assertThat(errMsg).containsOnlyOnce("RFN-530S4x");
        softly.assertThat(errMsg).containsOnlyOnce("VIRTUAL SYSTEM");
        softly.assertThat(errMsg).containsOnlyOnce("WRL-420cD");
        softly.assertThat(errMsg).containsOnlyOnce("WRL-420cL");
        softly.assertAll();
    }
}
