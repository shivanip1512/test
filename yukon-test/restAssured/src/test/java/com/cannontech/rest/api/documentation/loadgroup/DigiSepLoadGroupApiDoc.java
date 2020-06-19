package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;

public class DigiSepLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] digiSepFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_DIGI_SEP.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_DIGI_SEP.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_DIGI_SEP.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_DIGI_SEP.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_DIGI_SEP.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_DIGI_SEP.utilityEnrollmentGroup").type(JsonFieldType.NUMBER)
                    .description("Utility Enrollment Group"),
            fieldWithPath("LM_GROUP_DIGI_SEP.deviceClassSet").type(JsonFieldType.ARRAY).description("Device Class Set"),
            fieldWithPath("LM_GROUP_DIGI_SEP.rampInMinutes").type(JsonFieldType.NUMBER).description("RampIn value in minutes"),
            fieldWithPath("LM_GROUP_DIGI_SEP.rampOutMinutes").type(JsonFieldType.NUMBER).description("RampOut value in minutes")
    };

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
        paoId = updateDoc(RequestMethod.POST);
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Update" })
    public void Test_LmDigiSep_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmDigiSep_Copy" })
    public void Test_LmDigiSep_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
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