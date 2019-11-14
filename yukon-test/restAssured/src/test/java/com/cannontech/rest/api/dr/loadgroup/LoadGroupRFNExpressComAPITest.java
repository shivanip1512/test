package com.cannontech.rest.api.dr.loadgroup;

import static org.testng.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.utilities.Log;
import io.restassured.response.ExtractableResponse;
import com.cannontech.rest.api.utilities.ValidationHelper;

public class LoadGroupRFNExpressComAPITest {
    MockLoadGroupExpresscom loadGroup = null;

    /**
     * This test case validates creation of RFNExpresscomm load group with default values
     */

    @Test
    public void loadGroupRFNExpresscom_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupRFNExpresscom_01_Create");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(groupId != null, "Group Id should not be Null");
        Log.endTestCase("loadGroupRFNExpresscom_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of RFN Expresscomm load group and validates response
     */

    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupRFNExpresscom_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue(response.statusCode() == 200, "Status code should be 200");

        MockLoadGroupExpresscom loadGroupRFNExpresscomResponse = response.as(MockLoadGroupExpresscom.class);
        context.setAttribute("rfnExpresscomm_GrpName", loadGroupRFNExpresscomResponse.getName());

        assertTrue(loadGroup.getName().equals(loadGroupRFNExpresscomResponse.getName()),
                "Name Should be : " + loadGroup.getName());
        assertTrue(loadGroup.getType() == loadGroupRFNExpresscomResponse.getType(), "Type Should be : " + loadGroup.getType());

        Log.endTestCase("loadGroupRFNExpresscom_02_Get");
    }

    /**
     * This test case validates update functionality of RFN Expresscom load group
     */

    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupRFNExpresscom_03_Update");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
       
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        String name = "RFNExpresscommGroup_Update";
        context.setAttribute("rfnExpresscom_UpdateGrpName", name);

        loadGroup.setName(name);
        loadGroup.setKWCapacity(785.0);
        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue(response.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getUpdatedLoadGroupRFNExpresscomResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        MockLoadGroupExpresscom updatedLoadGroupRFNExpresscomResponse = getUpdatedLoadGroupRFNExpresscomResponse
                .as(MockLoadGroupExpresscom.class);

        assertTrue(loadGroup.getName().equals(updatedLoadGroupRFNExpresscomResponse.getName()), "Name Should be : " +
                loadGroup.getName());
        assertTrue(loadGroup.getType() == updatedLoadGroupRFNExpresscomResponse.getType(), "Type Should be : " +
                loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(updatedLoadGroupRFNExpresscomResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());

        Log.endTestCase("loadGroupRFNExpresscom_03_Update");
    }

    /**
     * This test case validates copy of RFNExpresscom load group
     */

    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupRFNExpresscom_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM)).build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy, context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        String copyGroupId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyGroupId != null, "Group Id should not be Null");
        context.setAttribute("rfnExpresscom_CopyGrpId", copyGroupId);
        context.setAttribute("rfnExpresscom_CopyGrpName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupRFNExpresscom_04_Copy");
    }

    /**
     * This test case validates deletion of RFN Expresscomm load group
     */

    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_02_Get" })
    public void loadGroupRFNExpresscom_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";
        String grpToDelete = "rfnExpresscom_UpdateGrpName";
        Log.startTestCase("loadGroupRFNExpresscom_05_Delete");
        
        // Delete Created Load group
        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute(grpToDelete).toString()).build();
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup",lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getDeletedResponse.statusCode() == 400, "Status code should be 400");

        assertTrue(ValidationHelper.validateErrorMessage(getDeletedResponse, expectedMessage), "Expected message should be:  " +
                expectedMessage);
        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("rfnExpresscom_CopyGrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject,
                context.getAttribute("rfnExpresscom_CopyGrpId").toString());
        assertTrue(deleteCopyResponse.statusCode() == 200, "Status code should be 200");
        Log.endTestCase("loadGroupRFNExpresscom_05_Delete");
    }

    /**
     * This test case validates less than Minimum value of Splinter, Geo, substation, zip and user of RFN Expresscomm load group
     */

    @Test
    public void loadGroupRFNExpresscom_06_PhysicalAddressLessThanMinVal() {
        Log.startTestCase("loadGroupRFNExpresscom_06_PhysicalAddressLessThanMinVal");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        loadGroup.setSplinter(Integer.valueOf(0));
        loadGroup.setGeo(Integer.valueOf(0));
        loadGroup.setZip(Integer.valueOf(0));
        loadGroup.setUser(Integer.valueOf(0));
        loadGroup.setSubstation(Integer.valueOf(0));

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(ValidationHelper.validateFieldError(response, "splinter",
                "Must be between 1 and 99."), "Expected Error not found: Must be between 1 and 99.");
        assertTrue(ValidationHelper.validateFieldError(response, "geo",
                "Must be between 1 and 65,534."), "Expected Error not found: Must be between 1 and 65,534.");
        assertTrue(ValidationHelper.validateFieldError(response, "substation",
                "Must be between 1 and 65,534."), "Expected Error not found: Must be between 1 and 65,534.");
        assertTrue(ValidationHelper.validateFieldError(response, "zip",
                "Must be between 1 and 16,777,214."), "Expected Error not found: Must be between 1 and 16,777,214.");
        assertTrue(ValidationHelper.validateFieldError(response, "user",
                "Must be between 1 and 65,534"), "Expected Error not found: Must be between 1 and 65,534.");
        Log.endTestCase("loadGroupRFNExpresscom_06_PhysicalAddressLessThanMinVal");

    }

    /**
     * This test case validates Greater than Maximum value of Splinter, Geo, substation, zip, user of RFN Expresscomm load group
     */
    @Test
    public void loadGroupRFNExpresscom_07_PhysicalAddressGreaterThanMaxValue() {
        Log.startTestCase("loadGroupRFNExpresscom_07_PhysicalAddressGreaterThanMaxValue");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        loadGroup.setSplinter(Integer.valueOf(100));
        loadGroup.setGeo(Integer.valueOf(65535));
        loadGroup.setZip(Integer.valueOf(16777215));
        loadGroup.setUser(Integer.valueOf(65535));
        loadGroup.setSubstation(Integer.valueOf(65535));

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(ValidationHelper.validateFieldError(response, "splinter",
                "Must be between 1 and 99."), "Expected Error not found: Must be between 1 and 99.");
        assertTrue(ValidationHelper.validateFieldError(response, "geo",
                "Must be between 1 and 65,534."), "Expected Error not found: Must be between 1 and 65,534.");
        assertTrue(ValidationHelper.validateFieldError(response, "substation",
                "Must be between 1 and 65,534."), "Expected Error not found: Must be between 1 and 65,534.");
        assertTrue(ValidationHelper.validateFieldError(response, "zip",
                "Must be between 1 and 16,777,214."), "Expected Error not found: Must be between 1 and 16,777,214.");
        assertTrue(ValidationHelper.validateFieldError(response, "user",
                "Must be between 1 and 65,534"), "Expected Error not found: Must be between 1 and 65,534.");
        Log.endTestCase("loadGroupRFNExpresscom_07_PhysicalAddressGreaterThanMaxValue");
    }

    /**
     * This test case validates validation error if we are creating RFN Expresscomm load group without adding mandatory address
     * usage
     */
    @Test
    public void loadGroupRFNExpresscom_08_WithoutRequiredAddressUsage() {
        Log.startTestCase("loadGroupRFNExpresscom_08_WithoutRequiredAddressUsage");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        loadGroup.setAddressUsage(null);
        List<MockAddressUsage> rfnAddressUsage = new ArrayList<>();
        rfnAddressUsage.add(MockAddressUsage.SERIAL);
        loadGroup.setAddressUsage(rfnAddressUsage);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(ValidationHelper.validateFieldError(response, "addressUsage",
                        "Load Address should atleast have LOAD, SPLINTER or PROGRAM"),
                "Load Address should atleast have LOAD, SPLINTER or PROGRAM");
        Log.endTestCase("loadGroupRFNExpresscom_08_WithoutRequiredAddressUsage");
    }

    /**
     * This test case validates validation error when we are creating RFN Expresscomm load group without
     * adding Serial number
     */
    @Test
    public void loadGroupRFNExpresscom_09_WithoutSerialNumber() {
        Log.startTestCase("loadGroupRFNExpresscom_09_WithoutSerialNumber");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        loadGroup.setAddressUsage(null);
        List<MockAddressUsage> rfnAddressUsage = new ArrayList<>();
        rfnAddressUsage.add(MockAddressUsage.SPLINTER);
        rfnAddressUsage.add(MockAddressUsage.SERIAL);
        loadGroup.setAddressUsage(rfnAddressUsage);

        loadGroup.setAddressUsage(rfnAddressUsage);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(ValidationHelper.validateFieldError(response, "serialNumber",
                "Serial Number is required."), "Serial Number is required.");
        Log.endTestCase("loadGroupRFNExpresscom_09_WithoutSerialNumber");
    }
    /**
     * This test case validates Serial Not Allowed With Other UsageTypes 
     * validation error. When serial Address is selected then other usage types are not allowed
     */
    @Test
    public void loadGroupRFNExpresscom_10_SerialNotAllowedWithOtherUsageTypes() {
        Log.startTestCase("loadGroupRFNExpresscom_10_SerialNotAllowedWithOtherUsageTypes");
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM);
        loadGroup.setAddressUsage(null);
        List<MockAddressUsage> rfnAddressUsage = new ArrayList<>();
        rfnAddressUsage.add(MockAddressUsage.GEO);
        rfnAddressUsage.add(MockAddressUsage.SPLINTER);
        rfnAddressUsage.add(MockAddressUsage.SERIAL);
        loadGroup.setAddressUsage(rfnAddressUsage);

        loadGroup.setAddressUsage(rfnAddressUsage);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(ValidationHelper.validateFieldError(response, "addressUsage",
                "Address Usage, SERIAL not allowed with other usage types."),
                "Address Usage, SERIAL not allowed with other usage types.");
        Log.endTestCase("loadGroupRFNExpresscom_10_SerialNotAllowedWithOtherUsageTypes");
    }
}
