package com.cannontech.rest.api.documentation.customAttribute;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.customAttribute.helper.CustomAttributeHelper;
import com.cannontech.rest.api.customAttribute.request.MockCustomAttribute;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAttributeApiDoc extends DocumentationBase {

    private String customAttributeId = null;
    public final static String idStr = "customAttributeId";
    public final static String idDescStr = "Custom Attribute Id";

    private MockCustomAttribute getMockObject() {
        return CustomAttributeHelper.buildAttribute();
    }

    private static List<FieldDescriptor> getFieldDescriptors() {
        FieldDescriptor[] customAttributeFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("name").type(JsonFieldType.STRING).description("Custom Attribute Name") };
        return new ArrayList<>(Arrays.asList(customAttributeFieldDescriptor));
    }

    @Test
    public void Test_CustomAttribute_01_Create() {
        customAttributeId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_CustomAttribute_01_Create" })
    public void Test_CustomAttribute_01_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_CustomAttribute_01_Get" })
    public void Test_CustomAttribute_01_Update() {
        customAttributeId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_CustomAttribute_01_Update" })
    public void Test_CustomAttribute_01_Delete() {
        deleteDoc();
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("customAttributeBaseURL") + "/" + customAttributeId;
        return new DocumentationFields.Get(responseFields, url);
    }

    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("customAttributeBaseURL");
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("customAttributeBaseURL") + "/" + customAttributeId;
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Copy buildCopyFields() {
        // Not Used
        return null;
    }

    @Override
    protected Delete buildDeleteFields() {
        List<FieldDescriptor> responseFields = Arrays
                .asList(fieldWithPath("id").type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("customAttributeBaseURL") + "/" + customAttributeId;
        return new DocumentationFields.DeleteWithBody(null, responseFields, null, url);
    }
}
