package com.cannontech.rest.api.documentation.attributeAssignment;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.attributeAssignment.helper.AssignmentHelper;
import com.cannontech.rest.api.attributeAssignment.request.MockAssignmentModel;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;

public class AttributeAssignmentApiDoc extends DocumentationBase {
    
    private String attributeAssignmentId = null;
    public final static String idStr = "attributeAssignmentId";
    public final static String idDescStr = "Attribute Assignment Id";

    
    
    private MockAssignmentModel getMockObject() {
        return AssignmentHelper.buildDevice();
    }
    
    private static List<FieldDescriptor> getFieldDescriptors() {
        FieldDescriptor[] attributeAssignmentFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("attributeId").type(JsonFieldType.NUMBER).description(idDescStr),
            fieldWithPath("paoType").type(JsonFieldType.STRING).description("Pao Type for the Attribute Assignment"),
            fieldWithPath("offset").type(JsonFieldType.NUMBER).description("Point Offset"),
            fieldWithPath("pointType").type(JsonFieldType.STRING).description("The Point Type for the Attribute Assignment")
        };
        return new ArrayList<>(Arrays.asList(attributeAssignmentFieldDescriptor));
    }
    
    @Test
    public void Test_AttributeAssignment_01_Create() {
        attributeAssignmentId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_AttributeAssignment_01_Create" })
    public void Test_AttributeAssignment_01_Get() {
        getDoc();
    }
    
    @Test(dependsOnMethods = { "Test_AttributeAssignment_01_Get" })
    public void Test_AttributeAssignment_01_Update() {
        attributeAssignmentId = updatePartialDoc();
    }
    
    @Test(dependsOnMethods = { "Test_AttributeAssignment_01_Update" })
    public void Test_AttributeAssignment_01_Delete() {
        deleteDoc();
    }


    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath("attributeAssignmentId").type(JsonFieldType.NUMBER).description("Custom Attribute Assignment Id"));
        responseFields.add(fieldWithPath("customAttribute.customAttributeId").type(JsonFieldType.NUMBER).description("Custom Attribute Id").optional());
        responseFields.add(fieldWithPath("customAttribute.name").type(JsonFieldType.STRING).description("Custom Attribute Name").optional());
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL") + "/" + attributeAssignmentId;
        return new DocumentationFields.Get(responseFields, url);
    }


    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath("attributeAssignmentId").type(JsonFieldType.NUMBER).description("Custom Attribute Assignment Id"));
        responseFields.add(fieldWithPath("customAttribute.customAttributeId").type(JsonFieldType.NUMBER).description("Custom Attribute Id").optional());
        responseFields.add(fieldWithPath("customAttribute.name").type(JsonFieldType.STRING).description("Custom Attribute Name").optional());
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL");
        return new DocumentationFields.Create(requestFields, responseFields, "attributeAssignmentId", idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath("attributeAssignmentId").type(JsonFieldType.NUMBER).description("Custom Attribute Assignment Id"));
        responseFields.add(fieldWithPath("customAttribute.customAttributeId").type(JsonFieldType.NUMBER).description("Custom Attribute Id").optional());
        responseFields.add(fieldWithPath("customAttribute.name").type(JsonFieldType.STRING).description("Custom Attribute Name").optional());
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL") + "/" + attributeAssignmentId;
        return new DocumentationFields.Update(requestFields, responseFields, "attributeAssignmentId", idDescStr, getMockObject(), url);
    }

    @Override
    protected Copy buildCopyFields() {
        return null;
    }

    @Override
    protected Delete buildDeleteFields() {
        List<FieldDescriptor> responseFields = Arrays
                .asList(fieldWithPath("id").type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL") + "/" + attributeAssignmentId;
        return new DocumentationFields.DeleteWithBody(null, responseFields, null, url);
    }
}