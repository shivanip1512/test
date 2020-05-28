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

public class ExpresscomLoadGroupSetupApiControllerTest extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] expresscomFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_EXPRESSCOMM.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.disableControl").type(JsonFieldType.BOOLEAN)
                    .description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.routeId").type(JsonFieldType.NUMBER).description("Route Id"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.routeName").type(JsonFieldType.STRING).description("Route Name").optional(),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.serviceProvider").type(JsonFieldType.NUMBER).description("Service Provider Id"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.geo").type(JsonFieldType.NUMBER).description("Value of Geographical Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.substation").type(JsonFieldType.NUMBER)
                    .description("Value of Substation Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.feeder").type(JsonFieldType.STRING).description("Value of Fedder Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.zip").type(JsonFieldType.NUMBER).description("Value of Zip Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.user").type(JsonFieldType.NUMBER).description("Value of User Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.program").type(JsonFieldType.NUMBER).description("Value of Program Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.splinter").type(JsonFieldType.NUMBER).description("Value of Splinter Address"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.addressUsage").type(JsonFieldType.ARRAY)
                    .description("Address Usage. GEO, SUBSTATION, FEEDER, ZIP, USER, LOAD, PROGRAM, SPLINTER"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.relayUsage").type(JsonFieldType.ARRAY)
                    .description("Relay Usage. Use LOAD_1 to LOAD_8"),
            fieldWithPath("LM_GROUP_EXPRESSCOMM.protocolPriority").type(JsonFieldType.STRING)
                    .description("Relay Usage. Use DEFAULT,MEDIUM ,HIGH ,HIGHEST")
    };

    @Test
    public void Test_Expresscom_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_Expresscom_Create" })
    public void Test_LmExpresscom_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmExpresscom_Get" })
    public void Test_LmExpresscom_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmExpresscom_Update" })
    public void Test_LmExpresscom_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmExpresscom_Copy" })
    public void Test_LmExpresscom_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_EXPRESSCOMM;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(expresscomFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
