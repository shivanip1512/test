package com.cannontech.rest.api.documentation.attributeAssignment;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
import com.cannontech.rest.api.attributeAssignment.helper.AssignmentHelper;
import com.cannontech.rest.api.attributeAssignment.helper.AttributeAssignmentHelper;
import com.cannontech.rest.api.attributeAssignment.request.MockAssignmentModel;
import com.cannontech.rest.api.attributeAssignment.request.MockAttributeAssignmentModel;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.common.model.MockPointType;

public class AttributeAssignmentApiDoc extends DocumentationBase {
    
    private String attributeAssignmentId = null;
    private final Integer attributeId = 1;
    private final MockPaoType paoType = MockPaoType.RFN420FL;
    private final String offset = "100";
    private String offsetDescription = "Point Offset";
    private final MockPointType pointType = MockPointType.CalcAnalog;
    
    
    private MockAssignmentModel getMockAssignmentObject() {
        return AssignmentHelper.buildDevice();
    }
    
    private MockAttributeAssignmentModel getMockAttributeAssignmentObject() {
        return AttributeAssignmentHelper.buildDevice();
    }
    
    
    private static List<FieldDescriptor> getAssignmentFieldDescriptors() {
        FieldDescriptor[] assignmentFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("attributeId")
                    .type(JsonFieldType.NUMBER)
                    .description("Custom Attribute Id"),
                fieldWithPath("attributeAssignmentId")
                    .type(JsonFieldType.NUMBER)
                    .optional()
                    .description("Custom Attribute Assignment Id"),
                fieldWithPath("paoType")
                    .type(JsonFieldType.STRING)
                    .description("Pao Type for the Attribute Assignment"),
                fieldWithPath("offset")
                    .type(JsonFieldType.NUMBER)
                    .description("Point Offset"),
                fieldWithPath("pointType")
                    .type(JsonFieldType.STRING)
                    .description("The Point Type for the Attribute Assignment")
        };
        return new ArrayList<>(Arrays.asList(assignmentFieldDescriptor));
    }
    
    private static List<FieldDescriptor> getAttributeAssignmentFieldDescriptors() {
        FieldDescriptor[] attributeAssignmentFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("attributeId")
                    .type(JsonFieldType.NUMBER)
                    .description("Custom Attribute Id"),
                fieldWithPath("attributeAssignmentId")
                    .type(JsonFieldType.NUMBER)
                    .optional()
                    .description("Custom Attribute Assignment Id"),
                fieldWithPath("paoType")
                    .type(JsonFieldType.STRING)
                    .description("Pao Type for the Attribute Assignment"),
                fieldWithPath("offset")
                    .type(JsonFieldType.STRING)
                    .description("Point Offset"),
                fieldWithPath("pointType")
                    .type(JsonFieldType.STRING)
                    .description("The Point Type for the Attribute Assignment"),
                fieldWithPath("customAttribute")
                    .type(JsonFieldType.OBJECT)
                    .description("CustomAttribute Object")
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
        List<FieldDescriptor> responseFields = getAttributeAssignmentFieldDescriptors();
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL") + "/" + attributeAssignmentId;
        return new DocumentationFields.Get(responseFields, url);
//        return null;
    }


    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getAssignmentFieldDescriptors();
        List<FieldDescriptor> responseFields = getAttributeAssignmentFieldDescriptors();
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL");
        return new DocumentationFields.Create(requestFields, responseFields, offset, offsetDescription, getMockAssignmentObject(), url);
//        return null;
    }


    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getAssignmentFieldDescriptors();
        List<FieldDescriptor> responseFields = getAttributeAssignmentFieldDescriptors();
        String url = ApiCallHelper.getProperty("attributeAssignmentsBaseURL") + "/" + attributeAssignmentId;
        return new DocumentationFields.Update(requestFields, responseFields, offset, offsetDescription, getMockAssignmentObject(), url);
//        return null;
    }


    @Override
    protected Copy buildCopyFields() {
        return null;
    }


    @Override
    protected Delete buildDeleteFields() {
        return null;
    }

}
