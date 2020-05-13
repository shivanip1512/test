package com.cannontech.rest.api.documentation.loadgroup;

import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.JsonUtil;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class DigiSepLoadGroupSetupApiControllerTest extends LoadGroupApiDocBase {
    
    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] digiSepFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_DIGI_SEP.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_DIGI_SEP.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_DIGI_SEP.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_DIGI_SEP.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_DIGI_SEP.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_DIGI_SEP.utilityEnrollmentGroup").type(JsonFieldType.NUMBER).description("Utility Enrollment Group"),
            fieldWithPath("LM_GROUP_DIGI_SEP.deviceClassSet").type(JsonFieldType.ARRAY).description("Device Class Set"),
            fieldWithPath("LM_GROUP_DIGI_SEP.rampInMinutes").type(JsonFieldType.NUMBER).description("RampIn value in minutes"),
            fieldWithPath("LM_GROUP_DIGI_SEP.rampOutMinutes").type(JsonFieldType.NUMBER).description("RampOut value in minutes")};

    @Test
    public void Test_LmDigiSep_Create() {
        paoId = createDoc();
    }
    
    @Test(dependsOnMethods = { "Test_LmDigiSep_Create" })
    public void Test_LmDigiSep_Get() {
        getDoc();
    }
    
    @Test(dependsOnMethods = { "Test_LmDigiSep_Get" })
    public void Test_LmDigiSep_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Update" })
    public void Test_LmDigiSep_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Copy" })
    public void Test_LmDigiSep_Delete() {
        deleteDoc();
        
        // clean up the copied object as well
        MockLMDto lmDeleteCopyObject = MockLMDto.builder().name(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType())).build();
        Log.info("Delete Load Group is : " + JsonUtil.beautifyJson(lmDeleteCopyObject.toString()));
        ExtractableResponse<?> copyResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteCopyObject, copyPaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_DIGI_SEP;
    }
    
    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(digiSepFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}