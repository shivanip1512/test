package com.cannontech.rest.api.documentation.point;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;

import io.restassured.response.Response;

public class AnalogPointApiDoc extends PointApiDocBase {

    private String pointId = null;
    private String copyPointId = null;
    private static FieldDescriptor[] buildAnalogDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("pointAnalog.deadband").type(JsonFieldType.NUMBER).description("Point Analog deadband").optional(),
                fieldWithPath("pointAnalog.multiplier").type(JsonFieldType.NUMBER).description("Point Analog multiplier").optional(),
                fieldWithPath("pointAnalog.dataOffset").type(JsonFieldType.NUMBER).description("Point Analog dataOffset").optional(),
                
                fieldWithPath("pointAnalogControl.controlType").type(JsonFieldType.STRING).description("Analog control type possible values are :NONE,NORMAL").optional(),
                fieldWithPath("pointAnalogControl.controlOffset").type(JsonFieldType.NUMBER).description("Control offset").optional(),
                fieldWithPath("pointAnalogControl.controlInhibited").type(JsonFieldType.BOOLEAN).description("Control Inhibited").optional(),
        };
    }

    private static FieldDescriptor[] getAllPointsFieldDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("paoidentifier.paoId").type(JsonFieldType.NUMBER).description("Pao Id").optional(),
                fieldWithPath("paoidentifier.paoType").type(JsonFieldType.STRING).description("Pao Type").optional(),
                fieldWithPath("points[].pointId").type(JsonFieldType.NUMBER).description("Point Id").optional(),
                fieldWithPath("points[].name").type(JsonFieldType.STRING).description("Name").optional(),
                fieldWithPath("points[].pointIdentifier.offset").type(JsonFieldType.NUMBER).description("Point Offset").optional(),
                fieldWithPath("points[].pointIdentifier.pointType").type(JsonFieldType.STRING).description("Point Type").optional(),
                fieldWithPath("points[].stateGroupId").type(JsonFieldType.NUMBER).description("State Group Id").optional(),
                fieldWithPath("points[].attributes").type(JsonFieldType.ARRAY).description("Built In Attributes possible values are :RFN_BLINK_COUNT,BLINK_COUNT,COMM_STATUS,CONTROL_STATUS,NEUTRAL_CURRENT,USAGE,CURRENT.").optional(),
        };
    }

    private static List<FieldDescriptor> buildAnalogPointDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(pointBaseFields()));
        list.addAll(Arrays.asList(buildPointUnitAndLimitDescriptor()));
        list.addAll(Arrays.asList(buildAnalogDescriptor()));
        return list;
    }

    @Test
    public void Test_AnalogPoint_01_Create() {
        pointId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_AnalogPoint_01_Create" })
    public void Test_AnalogPoint_02_Update() {
        pointId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_AnalogPoint_01_Create" })
    public void Test_AnalogPoint_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_AnalogPoint_02_Update" })
    public void Test_AnalogPoint_04_Copy() {
        copyPointId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_AnalogPoint_04_Copy" })
    public void Test_AnalogPoint_05_Delete() {
        deleteDoc();

        // Delete copied point.
        PointHelper.deletePoint(copyPointId);
    }

    /**
     * Test case is to  retrieve all points associated with pao Id.
     */
    @Test
    public void Test_AllPointsForPaoId_Get() {
        Integer paoId = Integer.valueOf(ApiCallHelper.getProperty("paoId"));
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(getAllPointsFieldDescriptor())); 
        Response response = given(documentationSpec)
                                .filter(document("{ClassName}/{methodName}",responseFields(list)))
                                .accept("application/json")
                                .contentType("application/json")
                                .header("Authorization","Bearer " + ApiCallHelper.authToken)
                                .when()
                                .get(ApiCallHelper.getProperty("getPoints") + paoId + "/points")
                                .then()
                                .extract()
                                .response();
    
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Override
    protected MockPointType getMockPointType() {
        return MockPointType.Analog;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildAnalogPointDescriptor();
    }

    @Override
    protected String getPointId() {
        return pointId;
    }

    @Override
    protected List<FieldDescriptor> getCopyPointFieldDescriptors() {
        return Arrays.asList(pointCopyFieldsDescriptor());
    }

}
