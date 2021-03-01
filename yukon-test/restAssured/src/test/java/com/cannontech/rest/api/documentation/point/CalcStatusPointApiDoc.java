package com.cannontech.rest.api.documentation.point;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;

public class CalcStatusPointApiDoc extends PointApiDocBase {

    private String pointId = null;
    private String copyPointId = null;

    private static List<FieldDescriptor> buildCalcStatusPointDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(pointBaseFields()));
        list.addAll(Arrays.asList(CalcPointsFields()));
        list.add(23,fieldWithPath("calculationBase.updateType").type(JsonFieldType.STRING)
                .description("Calc analog update type possible values are : ON_FIRST_CHANGE,ON_ALL_CHANGE,ON_TIMER,ON_TIMER_AND_CHANGE,CONSTANT,HISTORICAL").optional());
        list.add(24,fieldWithPath("calculationBase.periodicRate").type(JsonFieldType.NUMBER).description("Calc Base Periodic rate").optional());
        list.addAll(Arrays.asList(buildStatusDescriptor()));
        return list;
    }

    @Test
    public void Test_CalcStatusPoint_01_Create() {
        pointId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_CalcStatusPoint_01_Create" })
    public void Test_CalcStatusPoint_02_Update() {
        pointId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_CalcStatusPoint_01_Create" })
    public void Test_CalcStatusPoint_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_CalcStatusPoint_02_Update" })
    public void Test_CalcStatusPoint_04_Copy() {
        copyPointId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_CalcStatusPoint_04_Copy" })
    public void Test_CalcStatusPoint_05_Delete() {
        deleteDoc();

        // Delete copied point.
        PointHelper.deletePoint(copyPointId);
    }

    @Override
    protected MockPointType getMockPointType() {
        return MockPointType.CalcStatus;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildCalcStatusPointDescriptor();
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
