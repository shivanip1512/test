package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupDigiSepAPITest {
    MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
    }

    @Test
    public void loadGroupDigiSep_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupDigiSep_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", groupId != null);
        Log.endTestCase("loadGroupDigiSep_01_Create");
    }

    @Test
    public void loadGroupDigiSep_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupNest_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("GroupId of LmGroupDigiSep created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupBase digiSepLoadGroup = getResponse.as(MockLoadGroupBase.class);
        context.setAttribute("DigiSep_GrpName", digiSepLoadGroup.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(digiSepLoadGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == digiSepLoadGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(digiSepLoadGroup.getKWCapacity()));

        Boolean disableGroup = digiSepLoadGroup.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);

        Boolean disableControl = digiSepLoadGroup.isDisableControl();
        assertTrue("Control Should be enabled : ", disableControl);
        Log.endTestCase("loadGroupNest_02_Get");
    }

    @Test
    public void loadGroupDigiSep_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupDigiSep_03_Update");

        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        String name = "DigiSepUpdate";
        context.setAttribute("DigiSep_GrpName", name);
        loadGroup.setKWCapacity(543.908);
        loadGroup.setName(name);

        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupBase updatedDigiSepLoadGroup = getupdatedResponse.as(MockLoadGroupBase.class);
        assertTrue("Name Should be : " + name, name.equals(updatedDigiSepLoadGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedDigiSepLoadGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedDigiSepLoadGroup.getKWCapacity()));

        Log.endTestCase("loadGroupDigiSep_03_Update");
    }
}
