package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;

public class PointLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] pointGroupFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_POINT.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_POINT.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_POINT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_POINT.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_POINT.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control"),
            fieldWithPath("LM_GROUP_POINT.deviceUsage.id").type(JsonFieldType.NUMBER)
                    .description("Control device id"),
            fieldWithPath("LM_GROUP_POINT.pointUsage.id").type(JsonFieldType.NUMBER)
                    .description("Point id of available control device"),
            fieldWithPath("LM_GROUP_POINT.startControlRawState.rawState").type(JsonFieldType.NUMBER)
                    .description("Control start state id of available control Point ")
    };

    @Test
    public void Test_LmPointGroup_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Create" })
    public void Test_LmPointGroup_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Get" })
    public void Test_LmPointGroup_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Update" })
    public void Test_LmPointGroup_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmPointGroup_Copy" })
    public void Test_LmPointGroup_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_POINT;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(pointGroupFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        String paoType = getMockPaoType().name();
        responseFields.add(0,
                fieldWithPath(paoType + ".id").type(JsonFieldType.NUMBER).description(LoadGroupHelper.CONTEXT_GROUP_ID_DESC));
        responseFields.add(7, fieldWithPath(paoType + ".deviceUsage.name").type(JsonFieldType.STRING).optional()
                .description("Control device usage name."));
        responseFields.add(9, fieldWithPath(paoType + ".pointUsage.name").type(JsonFieldType.STRING).optional()
                .description("Point name of available control device."));
        responseFields.add(11, fieldWithPath(paoType + ".startControlRawState.stateText").type(JsonFieldType.STRING)
                .optional().description("Control start state name of available control Point."));

        String url = ApiCallHelper.getProperty("getloadgroup") + getLoadGroupId();
        return new DocumentationFields.Get(responseFields, url);
    }
}
