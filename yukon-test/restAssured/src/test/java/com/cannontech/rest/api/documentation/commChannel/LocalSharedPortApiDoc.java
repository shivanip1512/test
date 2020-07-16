package com.cannontech.rest.api.documentation.commChannel;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.model.MockPaoType;

public class LocalSharedPortApiDoc extends CommChannelApiDocBase {
    
    private String portId = null;

    private static FieldDescriptor[] buildLocalPortDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("sharing.sharedPortType").type(JsonFieldType.STRING).description("Shared Port Type Possible values of Shared Port Type are : NONE,ACS,ILEX").optional(),
                fieldWithPath("sharing.sharedSocketNumber").type(JsonFieldType.NUMBER).description("Shared Socket Number").optional(),
                fieldWithPath("carrierDetectWaitInMilliseconds").type(JsonFieldType.NUMBER).description("Carrier Detect Wait In MiliSeconds").optional(),
                fieldWithPath("protocolWrap").type(JsonFieldType.STRING).description("Protocol Wrap Possible values of Protocol Wrap are None,IDLC").optional(),
                fieldWithPath("physicalPort").type(JsonFieldType.STRING).description("Physical Port").optional(),
        };
    }
    
    private static List<FieldDescriptor> buildLocalSharedPortDescriptor() {
        List<FieldDescriptor> list = new ArrayList<>(Arrays.asList(portBaseFields()));
        list.addAll(Arrays.asList(buildLocalPortDescriptor()));
        return list;
    }

    @Test
    public void Test_LocalSharedPort_01_Create() {
        portId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LocalSharedPort_01_Create" })
    public void Test_LocalSharedPort_02_Update() {
        portId = updatePartialDoc();
    }

    @Test(dependsOnMethods = { "Test_LocalSharedPort_01_Create" })
    public void Test_LocalSharedPort_03_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LocalSharedPort_01_Create" })
    public void Test_LocalSharedPort_04_Delete() {
        deleteDoc();
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return buildLocalSharedPortDescriptor();
    }
    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LOCAL_SHARED;
    }
    
    @Override
    protected String getPortId() {
        return portId;
    }
}