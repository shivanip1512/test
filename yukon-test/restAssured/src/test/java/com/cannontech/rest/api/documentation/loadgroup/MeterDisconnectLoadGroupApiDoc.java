package com.cannontech.rest.api.documentation.loadgroup;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;

public class MeterDisconnectLoadGroupApiDoc extends LoadGroupApiDocBase {

    private String paoId = null;
    private String copyPaoId = null;
    private FieldDescriptor[] meterDisconnectFieldDescriptor = new FieldDescriptor[] {
            fieldWithPath("LM_GROUP_METER_DISCONNECT.name").type(JsonFieldType.STRING).description("Load Group Name"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.type").type(JsonFieldType.STRING).description("Load Group Type"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.kWCapacity").type(JsonFieldType.NUMBER).description("KW Capacity"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.disableGroup").type(JsonFieldType.BOOLEAN)
                    .description("Flag to disable Group"),
            fieldWithPath("LM_GROUP_METER_DISCONNECT.disableControl").type(JsonFieldType.BOOLEAN)
                    .description("Flag to disable Control")
    };

    @Test
    public void Test_LmGroupMeterDisconnect_Create() {
        paoId = createDoc();
    }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Create" })
    public void Test_LmGroupMeterDisconnect_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Get" })
    public void Test_LmGroupMeterDisconnect_Update() {
        paoId = updateDoc();
    }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Update" })
    public void Test_LmGroupMeterDisconnect_Copy() {
        copyPaoId = copyDoc();
    }

    @Test(dependsOnMethods = { "Test_LmGroupMeterDisconnect_Copy" })
    public void Test_LmGroupMeterDisconnect_Delete() {
        deleteDoc();

        // clean up the copied object as well
        LoadGroupHelper.deleteLoadGroup(LoadGroupHelper.getCopiedLoadGroupName(getMockPaoType()), copyPaoId);
    }

    @Override
    protected MockPaoType getMockPaoType() {
        return MockPaoType.LM_GROUP_METER_DISCONNECT;
    }

    @Override
    protected List<FieldDescriptor> getFieldDescriptors() {
        return new ArrayList<>(Arrays.asList(meterDisconnectFieldDescriptor));
    }

    @Override
    protected String getLoadGroupId() {
        return paoId;
    }
}
