package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.loadgroup.request.MockAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockControlPriority;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.loadgroup.request.MockLoads;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupExpressComAPITest {

    MockLoadGroupExpresscom loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = (MockLoadGroupExpresscom)LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM);
    }

    /**
     * This test case validates creation of Expresscomm load group with default values provided in payload json file
     */
    @Test
    public void loadGroupExpresscom_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("groupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GroupId should not be Null", groupId != null);
        Log.endTestCase("loadGroupExpresscom_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of Expresscomm load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_02_Get");
        String groupId = context.getAttribute("groupId").toString();

        Log.info("GroupId of LmGroupExpresscomm created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        MockLoadGroupExpresscom loadGroupExpresscomResponse = response.as(MockLoadGroupExpresscom.class);
        context.setAttribute("Expresscomm_GrpName", loadGroupExpresscomResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupExpresscomResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupExpresscomResponse.getType());
        assertTrue("routeId Should be : " + loadGroup.getRouteId(), loadGroup.getRouteId().intValue() == loadGroupExpresscomResponse.getRouteId().intValue());

        Log.endTestCase("loadGroupExpresscom_02_Get");

    }

    /**
     * This test case validates updation of Expresscomm load group and validates response with updated values
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_03_Update");

        String groupId = context.getAttribute("groupId").toString();
        String name = "Auto_ExpresscommGroup_Update";
        context.setAttribute("expresscom_UpdateGrpName", name);

        loadGroup.setName(name);
        loadGroup.setKWCapacity(785.0);
        Log.info("Updated payload is :" + loadGroup);

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedLoadGroupExpresscomResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupExpresscom updatedLoadGroupExpresscomResponse = getupdatedLoadGroupExpresscomResponse.as(MockLoadGroupExpresscom.class);
        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(updatedLoadGroupExpresscomResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedLoadGroupExpresscomResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedLoadGroupExpresscomResponse.getKWCapacity()));
        Log.endTestCase("loadGroupExpresscom_04_Update");

    }

    @Test(enabled = false)
    public void loadGroupExpresscom_04_Copy(ITestContext context) {

    }

    /**
     * This test case validates deletion of Expresscomm load group
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";

        Log.startTestCase("loadGroupExpresscom_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("expresscom_UpdateGrpName").toString()).build();
        Log.info("Delete payload is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject, context.getAttribute("groupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> response2 = ApiCallHelper.get("getloadgroup", context.getAttribute("groupId").toString());
        assertTrue("Status code should be 400", response2.statusCode() == 400);

        MockApiError error = response2.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        Log.endTestCase("loadGroupExpresscom_05_Delete");

    }

    /**
     * This test case validates negative scenarios of Emetcon load group with different input data provided in
     * DataProviderClass
     */
    @Test(dataProvider = "ExpresscomAddressData")
    public void loadGroupExpresscom_06_PhysicalAddressValidation(String spid, String geoId, String subId, String zip,
            String user, String expectedErrorMsg, Integer expectedStatusCode) {

        Log.startTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");

        List<MockAddressUsage> addressUsage = new ArrayList<>();
        addressUsage.add(MockAddressUsage.GEO);
        addressUsage.add(MockAddressUsage.SUBSTATION);
        addressUsage.add(MockAddressUsage.USER);
        addressUsage.add(MockAddressUsage.PROGRAM);
        addressUsage.add(MockAddressUsage.SPLINTER);
        addressUsage.add(MockAddressUsage.LOAD);
        addressUsage.add(MockAddressUsage.ZIP);
        addressUsage.add(MockAddressUsage.FEEDER);
        
        List<MockLoads> relayUsage = new ArrayList<>();
        relayUsage.add(MockLoads.Load_1);

        MockLoadGroupExpresscom loadGroup = MockLoadGroupExpresscom.builder()
                                       .name("Test_ExpressCom_LoadGroup")
                                       .type(MockPaoType.LM_GROUP_EXPRESSCOMM)
                                       .routeId(12815)
                                       .disableControl(false)
                                       .disableGroup(false)
                                       .feeder("1000000000000000")
                                       .serviceProvider(100)
                                       .geo(223)
                                       .zip(3334)
                                       .kWCapacity(0.0)
                                       .addressUsage(addressUsage)
                                       .relayUsage(relayUsage)
                                       .serialNumber("1245")
                                       .program(12)
                                       .protocolPriority(MockControlPriority.DEFAULT)
                                       .build();
        
        loadGroup.setSplinter(Integer.valueOf(spid));
        loadGroup.setGeo(Integer.valueOf(geoId));
        loadGroup.setZip(Integer.valueOf(zip));
        loadGroup.setUser(Integer.valueOf(user));
        loadGroup.setSubstation(Integer.valueOf(subId));

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        Integer statusCode = response.statusCode();
        assertTrue("Status code should be " + expectedStatusCode, expectedStatusCode.equals(statusCode));
       
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedErrorMsg.equals(error.getFieldErrors().get(0).getCode()));

        Log.endTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided in test data sheet -
     * col1 : serviceProviderId
     * col2 : geoId
     * col3 : substationId
     * col4 : feeder
     * col5 : zip
     * col6 : user
     * col7 : Expected field errors code in response
     * col8 : Expected response code
     */
    @DataProvider(name = "ExpresscomAddressData")
    public Object[][] getExpresscomAddressData(ITestContext context) {

        return new Object[][] { { "65535", "22", "36", "16777214", "36", "Must be between 1 and 99.", 422 },
                { "0", "22", "36", "16777214", "36", "Must be between 1 and 99.", 422 },
                { "22", "65535", "36", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "0", "36", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "0", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65535", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65534", "0", "36", "Must be between 1 and 16,777,214.", 422 },
                { "22", "22", "65534", "16777215", "36", "Must be between 1 and 16,777,214.", 422 },
                { "22", "22", "65534", "16777214", "0", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65534", "16777214", "65535", "Must be between 1 and 65,534.", 422 } };
    }

}
