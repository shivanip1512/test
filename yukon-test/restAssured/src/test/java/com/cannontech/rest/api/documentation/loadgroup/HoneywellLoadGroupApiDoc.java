package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;

public class HoneywellLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] honeywellFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_HONEYWELL.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_HONEYWELL.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_HONEYWELL.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_HONEYWELL.disableGroup").type(JsonFieldType.BOOLEAN).description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_HONEYWELL.disableControl").type(JsonFieldType.BOOLEAN).description("Flag to disable Control")
    };

    @Test
    public void Test_LmHoneywell_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmHoneywell_Create" })
    public void Test_LmHoneywell_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmHoneywell_Get" })
    public void Test_LmHoneywell_Update() {
        paoId = updateDoc(RequestMethod.POST);
    }

    @Test(dependsOnMethods = { "Test_LmHoneywell_Update" })
    public void Test_LmHoneywell_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmHoneywell_Update" })
    public void Test_LmHoneywell_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_HONEYWELL;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(honeywellFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
