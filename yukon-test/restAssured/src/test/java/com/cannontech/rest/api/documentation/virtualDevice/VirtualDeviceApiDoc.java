package com.cannontech.rest.api.documentation.virtualDevice;

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
import com.cannontech.rest.api.virtualDevice.helper.VirtualDeviceHelper;
import com.cannontech.rest.api.virtualDevice.request.MockVirtualDeviceModel;

public class VirtualDeviceApiDoc extends DocumentationBase {

    private String virtualDeviceId = null;
    public final static String idStr = "id";
    public final static String idDescStr = "Virtual Device Id";
    
    private MockVirtualDeviceModel getMockObject() {
        return VirtualDeviceHelper.buildDevice();
    }
    
    private static List<FieldDescriptor> getFieldDescriptors() {
        FieldDescriptor[] virtualDeviceFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("name")
                    .type(JsonFieldType.STRING)
                    .description("Virtual Device Name"),
                fieldWithPath("enable")
                    .type(JsonFieldType.BOOLEAN)
                    .optional()
                    .description("Enable flag for virtual device"),
                fieldWithPath("type")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("The PAO Type of a virtual device. Will always be VIRTUAL_SYSTEM")
        };
        return new ArrayList<>(Arrays.asList(virtualDeviceFieldDescriptor));
    }
    
    @Test
    public void Test_VirtualDevice_01_Create() {
        virtualDeviceId = createDoc();
    }
    
    @Test(dependsOnMethods = { "Test_VirtualDevice_01_Create" })
    public void Test_VirtualDevice_01_Get() {
        getDoc();
    }
    
    @Test(dependsOnMethods = { "Test_VirtualDevice_01_Get" })
    public void Test_VirtualDevice_01_Update() {
        virtualDeviceId = updatePartialDoc();
    }
    
    @Test(dependsOnMethods = { "Test_VirtualDevice_01_Update" })
    public void Test_VirtualDevice_01_Delete() {
        virtualDeviceId = deleteDoc();
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("getVirtualDevice") + virtualDeviceId;
        return new DocumentationFields.Get(responseFields, url);
    }

    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("createVirtualDevice");
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("updateVirtualDevice") + virtualDeviceId;
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
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
