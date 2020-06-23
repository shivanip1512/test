package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;

public class EcobeeLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] ecobeeFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_ECOBEE.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_ECOBEE.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_ECOBEE.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_ECOBEE.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_ECOBEE.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
    };

    @Test
    public void Test_LmEcobee_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEcobee_Create" })
    public void Test_LmEcobee_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEcobee_Get" })
    public void Test_LmEcobee_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEcobee_Update" })
    public void Test_LmEcobee_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEcobee_Update" })
    public void Test_LmEcobee_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(ecobeeFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_ECOBEE;
    }
}
