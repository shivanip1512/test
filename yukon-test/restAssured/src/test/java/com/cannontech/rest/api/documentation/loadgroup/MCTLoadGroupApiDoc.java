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

public class MCTLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] mctFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_MCT.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_MCT.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_MCT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_MCT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_MCT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_MCT.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_MCT.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_MCT.level").type(JsonFieldType.STRING).description("Value of Address Level"),
            fieldWithPath("LM_GROUP_MCT.address").type(JsonFieldType.NUMBER).description("Address Value"),
            fieldWithPath("LM_GROUP_MCT.mctDeviceId").type(JsonFieldType.NUMBER).description("Value of MCT Device Id").optional(),
            fieldWithPath("LM_GROUP_MCT.relayUsage").type(JsonFieldType.ARRAY)
                    .description("Relay Usage. RELAY_1, RELAY_2, RELAY_3, RELAY_4")
    };

    @Test
    public void Test_LmMCT_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmMCT_Create" })
    public void Test_LmMCT_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmMCT_Get" })
    public void Test_LmMCT_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmMCT_Update" })
    public void Test_LmMCT_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmMCT_Copy" })
    public void Test_LmMCT_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_MCT;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(mctFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
