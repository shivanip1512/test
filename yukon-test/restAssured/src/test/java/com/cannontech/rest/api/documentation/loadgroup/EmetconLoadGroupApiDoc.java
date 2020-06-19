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

public class EmetconLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] emetconFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_EMETCON.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_EMETCON.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_EMETCON.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_EMETCON.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_EMETCON.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_EMETCON.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_EMETCON.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_EMETCON.addressUsage").type(JsonFieldType.STRING)
                    .description("Address Usage. Must have G (for gold address), S (For silver address)"),
            fieldWithPath("LM_GROUP_EMETCON.relayUsage").type(JsonFieldType.STRING)
                    .description("Relay Usgae. Must have A, B, C or S (for All)"),
            fieldWithPath("LM_GROUP_EMETCON.goldAddress").type(JsonFieldType.NUMBER).description("Gold address value."),
            fieldWithPath("LM_GROUP_EMETCON.silverAddress").type(JsonFieldType.NUMBER).description("Silver address value.")
    };

    @Test
    public void Test_LmEmetcon_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEmetcon_Create" })
    public void Test_LmEmetcon_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEmetcon_Get" })
    public void Test_LmEmetcon_Update() {
        paoId = updateDoc(RequestMethod.POST);
    }

    @Test(dependsOnMethods = { "Test_LmEmetcon_Update" })
    public void Test_LmEmetcon_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmEmetcon_Copy" })
    public void Test_LmEmetcon_Delete() {
        deleteDoc();

        // cleanup/delete the copied group as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(emetconFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_EMETCON;
    }
}
