package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.model.MockPaoType;

public class NestLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private FieldDescriptor[] nestFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_NEST.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_NEST.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_NEST.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_NEST.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_NEST.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
    };

    @Test(enabled = false)
    public void Test_LmNest_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmNest_Create" }, enabled = false)
    public void Test_LmNest_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmNest_Get" }, enabled = false)
    public void Test_LmNest_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmNest_Update" }, enabled = false)
    public void Test_LmNest_Delete() {
        deleteDoc();
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_NEST;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(nestFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
