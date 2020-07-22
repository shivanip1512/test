package com.cannontech.rest.api.documentation.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;

public class StatusPointApiDoc extends PointApiDocBase {

    private String pointId = null;
    private String copyPointId = null;

    private static List<FieldDescriptor> buildStatusPointDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(pointBaseFields()));
        list.addAll(Arrays.asList(buildStatusDescriptor()));
        return list;
    }

    @Test
    public void Test_StatusPoint_01_Create() {
        pointId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_StatusPoint_01_Create" })
    public void Test_StatusPoint_02_Update() {
        pointId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_StatusPoint_01_Create" })
    public void Test_StatusPoint_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_StatusPoint_02_Update" })
    public void Test_StatusPoint_04_Copy() {
        copyPointId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_StatusPoint_04_Copy" })
    public void Test_StatusPoint_05_Delete() {
        deleteDoc();

        // Delete copied point.
        PointHelper.deletePoint(copyPointId);
    }

    @Override
    protected MockPointType getMockPointType() {
        return MockPointType.Status;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildStatusPointDescriptor();
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
