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

public class VersacomLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] versacomFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_VERSACOM.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_VERSACOM.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_VERSACOM.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_VERSACOM.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_VERSACOM.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_VERSACOM.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_VERSACOM.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_VERSACOM.utilityAddress").type(JsonFieldType.NUMBER).description("Value of Utility Address"),
            fieldWithPath("LM_GROUP_VERSACOM.sectionAddress").type(JsonFieldType.NUMBER).description("Value of Section Address"),
            fieldWithPath("LM_GROUP_VERSACOM.classAddress").type(JsonFieldType.STRING).description("Value of Class Address"),
            fieldWithPath("LM_GROUP_VERSACOM.divisionAddress").type(JsonFieldType.STRING)
                    .description("Value of Division Address"),
            fieldWithPath("LM_GROUP_VERSACOM.addressUsage").type(JsonFieldType.ARRAY)
                    .description("Address Uasge. Select UTILITY, SECTION, CLASS, DIVISION"),
            fieldWithPath("LM_GROUP_VERSACOM.relayUsage").type(JsonFieldType.ARRAY)
                    .description("Relay Usage. RELAY_1, RELAY_2, RELAY_3, RELAY_4")
    };

    @Test
    public void Test_LmVersacom_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmVersacom_Create" })
    public void Test_LmVersacom_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmVersacom_Get" })
    public void Test_LmVersacom_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmVersacom_Update" })
    public void Test_LmVersacom_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmVersacom_Copy" })
    public void Test_LmVersacom_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_VERSACOM;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(versacomFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
