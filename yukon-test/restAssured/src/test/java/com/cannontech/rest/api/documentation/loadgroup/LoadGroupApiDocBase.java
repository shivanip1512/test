package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;

public abstract class LoadGroupApiDocBase extends DocumentationBase {
    
    private MockLoadGroupBase getMockObject() {
        return LoadGroupHelper.buildLoadGroup(getMockPaoType());
    }
    
    private MockLoadGroupCopy getMockCopyObject() {
        if (LoadGroupHelper.isLoadGroupSupportRoute(getMockPaoType())) {
            String routeIdStr = ApiCallHelper.getProperty("loadGroupRouteId");
            Integer routeId = Integer.valueOf(routeIdStr);
            return MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType())).routeId(routeId).build();
        } else {
            return MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType())).build();
        }
    }
    /**
     * Return the request fieldDescriptors
     */
    protected abstract List<FieldDescriptor> getFieldDescriptors();
    
    protected abstract String getLoadGroupId();
    
    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = Arrays.asList(fieldWithPath(LoadGroupHelper.CONTEXT_GROUP_ID).type(JsonFieldType.NUMBER).description(LoadGroupHelper.CONTEXT_GROUP_ID_DESC));
        String url = ApiCallHelper.getProperty("saveloadgroup");
        return new DocumentationFields.Create(requestFields, responseFields, LoadGroupHelper.CONTEXT_GROUP_ID, LoadGroupHelper.CONTEXT_GROUP_ID_DESC, getMockObject(), url);
    }
    
    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = Arrays.asList(fieldWithPath(LoadGroupHelper.CONTEXT_GROUP_ID).type(JsonFieldType.NUMBER).description(LoadGroupHelper.CONTEXT_GROUP_ID_DESC));
        String url = ApiCallHelper.getProperty("updateloadgroup") + getLoadGroupId();
        return new DocumentationFields.Update(requestFields, responseFields, LoadGroupHelper.CONTEXT_GROUP_ID, LoadGroupHelper.CONTEXT_GROUP_ID_DESC, getMockObject(), url);
    }
    
    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        String typeId = getMockPaoType().name() + ".id";
        responseFields.add(0,fieldWithPath(typeId).type(JsonFieldType.NUMBER).description(LoadGroupHelper.CONTEXT_GROUP_ID_DESC));
        if (LoadGroupHelper.isLoadGroupSupportRoute(getMockPaoType())) {
            responseFields.add(7, fieldWithPath(getMockPaoType() + ".routeName").type(JsonFieldType.STRING).description("Route Name"));
        }
        String url = ApiCallHelper.getProperty("getloadgroup") + getLoadGroupId() ;
        return new DocumentationFields.Get(responseFields, url);
    }
    
    @Override
    protected Copy buildCopyFields() {
        List<FieldDescriptor> requestFields = new ArrayList(Arrays.asList(fieldWithPath("LOAD_GROUP_COPY.name").type(JsonFieldType.STRING).description("Load Group Copy Name")));
        if (LoadGroupHelper.isLoadGroupSupportRoute(getMockPaoType())) {
            requestFields.add(fieldWithPath("LOAD_GROUP_COPY.routeId").type(JsonFieldType.NUMBER).description("Route Id"));
        }
        List<FieldDescriptor> responseFields = Arrays.asList(fieldWithPath(LoadGroupHelper.CONTEXT_GROUP_ID).type(JsonFieldType.NUMBER).description("Load Group Copy Id"));
        String url = ApiCallHelper.getProperty("copyloadgroup") + getLoadGroupId();
        return new DocumentationFields.Copy(requestFields, responseFields, LoadGroupHelper.CONTEXT_GROUP_ID, LoadGroupHelper.CONTEXT_GROUP_ID_DESC, getMockCopyObject(), url);
    }
    
    @Override
    protected Delete buildDeleteFields() {
        MockLMDto lmDeleteObject = MockLMDto.builder().name(LoadGroupHelper.getLoadGroupName(getMockPaoType())).build();

        List<FieldDescriptor> requestFields = Arrays.asList(fieldWithPath("name").type(JsonFieldType.STRING).description("Load Group Name"));
        List<FieldDescriptor> responseFields = Arrays.asList(fieldWithPath(LoadGroupHelper.CONTEXT_GROUP_ID).type(JsonFieldType.NUMBER).description(LoadGroupHelper.CONTEXT_GROUP_ID_DESC));
        String url = ApiCallHelper.getProperty("deleteloadgroup") + getLoadGroupId();
        return new DocumentationFields.DeleteWithBody(requestFields, responseFields, lmDeleteObject, url);
    }
    
    protected abstract MockPaoType getMockPaoType();
}