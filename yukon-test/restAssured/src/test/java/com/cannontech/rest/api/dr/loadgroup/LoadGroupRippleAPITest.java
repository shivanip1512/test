package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupRipple;
import com.cannontech.rest.api.utilities.Log;
import io.restassured.response.ExtractableResponse;

public class LoadGroupRippleAPITest {

    @Test
    public void loadGroupRipple_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupRipple_01_Create");
        MockLoadGroupRipple loadGroup = (MockLoadGroupRipple) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_RIPPLE);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID) != null, "Group Id should not be Null");
        loadGroup.setId(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        context.setAttribute("expectedloadGroup", loadGroup);
        Log.endTestCase("loadGroupRipple_01_Create");
    }

    @Test(dependsOnMethods = "loadGroupRipple_01_Create")
    public void loadGroupRipple_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupRipple_02_Get");
        Log.info("Group Id of LmGroupRipple created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID));

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupRipple loadGroupResponse = getResponse.as(MockLoadGroupRipple.class);
        context.setAttribute("Ripple_GrpName", loadGroupResponse.getName());

        MockLoadGroupRipple loadGroup = (MockLoadGroupRipple) context.getAttribute("expectedloadGroup");

        assertTrue(loadGroup.getName().equals(loadGroupResponse.getName()), "Name Should be : " + loadGroup.getName());
        assertTrue(loadGroup.getType() == loadGroupResponse.getType(), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()), "kWCapacity Should be : " + loadGroup.getKWCapacity());
        assertTrue(loadGroup.isDisableGroup() == (loadGroupResponse.isDisableGroup()), "Group Should be disabled : ");
        assertTrue(loadGroup.isDisableControl() == (loadGroupResponse.isDisableControl()), "Control Should be disabled : ");

        Log.endTestCase("loadGroupRipple_02_Get");
    }

    @Test(dependsOnMethods = "loadGroupRipple_02_Get")
    public void loadGroupRipple_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupRipple_03_Update");

        MockLoadGroupRipple loadGroup = (MockLoadGroupRipple) context.getAttribute("expectedloadGroup");
        String name = "LM_Group_Ripple_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Ripple_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                                                                loadGroup,
                                                                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupRipple updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupRipple.class);
        assertTrue(name.equals(updatedLoadGroupResponse.getName()), "Name Should be : " + name);
        assertTrue(loadGroup.getType().equals(updatedLoadGroupResponse.getType()), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()), "kWCapacity Should be : " + loadGroup.getKWCapacity());
        Log.endTestCase("loadGroupRipple_03_Update");
    }

    @Test(dependsOnMethods = "loadGroupRipple_03_Update")
    public void loadGroupRipple_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupRipple_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_RIPPLE)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                                                                 loadGroupCopy,
                                                                 context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null, "Group Id should not be Null");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupRipple loadGroupResponse = getResponse.as(MockLoadGroupRipple.class);
        context.setAttribute("Copied_Ripple_GrpName", loadGroupResponse.getName());
        context.setAttribute("Copied_Ripple_GrpId", loadGroupResponse.getId());
        Log.endTestCase("loadGroupRipple_04_Copy");
    }

    @Test(dependsOnMethods = "loadGroupRipple_04_Copy")
    public void loadGroupRipple_05_Delete(ITestContext context) {
        Log.startTestCase("loadGroupRipple_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Ripple_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                                                                     lmDeleteObject,
                                                                     context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", deleteResponse.statusCode() == 200);

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Copied_Ripple_GrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                                                                         lmDeleteObject,
                                                                         context.getAttribute("Copied_Ripple_GrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);
        Log.startTestCase("loadGroupRipple_05_Delete");
    }
}