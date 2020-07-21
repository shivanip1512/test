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

public class PulseAccumulatorPointApiDoc extends PointApiDocBase {

    private String pointId = null;
    private String copyPointId = null;
    
    private static FieldDescriptor[] buildPulseAccumulatorDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("accumulatorPoint.multiplier").type(JsonFieldType.NUMBER).description("Accumulator point Multiplier").optional(),
                fieldWithPath("accumulatorPoint.dataOffset").type(JsonFieldType.NUMBER).description("Accumulator  point data offset").optional(),
          };
    }

    private static List<FieldDescriptor> buildPulseAccumulatorPointDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(pointBaseFields()));
        list.addAll(Arrays.asList(buildPulseAccumulatorDescriptor()));
        list.addAll(Arrays.asList(buildPointUnitAndLimitDescriptor()));
        return list;
    }

    @Test
    public void Test_PulseAccumulatorPoint_01_Create() {
        pointId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_PulseAccumulatorPoint_01_Create" })
    public void Test_PulseAccumulatorPoint_02_Update() {
        pointId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_PulseAccumulatorPoint_01_Create" })
    public void Test_PulseAccumulatorPoint_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_PulseAccumulatorPoint_02_Update" })
    public void Test_PulseAccumulatorPoint_04_Copy() {
        copyPointId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_PulseAccumulatorPoint_04_Copy" })
    public void Test_PulseAccumulatorPoint_05_Delete() {
        deleteDoc();
        
        //Delete copied point.
        PointHelper.deletePoint(copyPointId);
    }

    @Override
    protected MockPointType getMockPointType() {
        return MockPointType.PulseAccumulator;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildPulseAccumulatorPointDescriptor();
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
