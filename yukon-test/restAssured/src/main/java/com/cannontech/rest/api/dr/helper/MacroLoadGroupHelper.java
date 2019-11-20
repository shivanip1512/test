package com.cannontech.rest.api.dr.helper;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockLMPaoDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.documentation.macroloadgroup.MacroLoadGroupSetupApiControllerTest;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockMacroLoadGroup;

import io.restassured.response.ExtractableResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MacroLoadGroupHelper {

    public static MockMacroLoadGroup createMacroLoadGroup(String groupName, List<MockLMPaoDto> assignedLoadGroups) {
        MockMacroLoadGroup macroLoadGroup = buildMacroLoadGroup(groupName, assignedLoadGroups);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        Integer macroGroupId = createResponse.path(MacroLoadGroupSetupApiControllerTest.CONTEXT_MACRO_GROUP_ID);
        log.info("macroGroupId:" + macroGroupId);
        log.info("Name:" + createResponse.path("name"));
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Macro Group Id(paoId) should not be Null", macroGroupId != null);
        macroLoadGroup.setId(macroGroupId);
        return macroLoadGroup;
    }

    public static MockMacroLoadGroup updateMacroLoadGroup(String loadGroupIDtoUpdate, String updatedName,
            List<MockLMPaoDto> assignedLoadGroupsUpdated) {
        MockMacroLoadGroup macroLoadGroup = buildMacroLoadGroup(updatedName, assignedLoadGroupsUpdated);
        ExtractableResponse<?> updateResponse = ApiCallHelper.post("updateMacroLoadGroup", macroLoadGroup, loadGroupIDtoUpdate);
        Integer macroGroupId = updateResponse.path(MacroLoadGroupSetupApiControllerTest.CONTEXT_MACRO_GROUP_ID);
        assertTrue("Status code should be 200", updateResponse.statusCode() == 200);
        assertTrue("Macro Group Id(paoId) should not be Null", macroGroupId != null);
        assertTrue("Updated Macro Load group Id(paoId) should be same ", Integer.parseInt(loadGroupIDtoUpdate) == macroGroupId);
        macroLoadGroup.setId(macroGroupId);
        return macroLoadGroup;
    }

    public static Integer getMacroLoadGroup(String macroLoadGroupId) {
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getMacroloadgroup", macroLoadGroupId);
        Integer macroGroupId = getResponse.path("id");
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        assertTrue("Macro Load Group Id(paoId) should not be Null", macroGroupId != null);
        assertTrue("Macro Load Group name should not be Null", getResponse.path("name") != null);
        return macroGroupId;
    }

    public static Integer copyMacroLoadGroup(String newMacroLoadGroupName, String macroLoadGroupIdToBeCopied) {
        MockLoadGroupCopy macroloadGroupCopy = MockLoadGroupCopy.builder().name(newMacroLoadGroupName).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyMacroLoadGroup", macroloadGroupCopy,
                macroLoadGroupIdToBeCopied);
        Integer copiedMacroLoadGroupId = copyResponse.path(MacroLoadGroupSetupApiControllerTest.CONTEXT_MACRO_GROUP_ID);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Macro Load Group Id(paoId) should not be Null", copiedMacroLoadGroupId != null);
        assertTrue("Copied Macro Load group Id(paoId) should be differect ",
                copiedMacroLoadGroupId != Integer.parseInt(macroLoadGroupIdToBeCopied));
        return copiedMacroLoadGroupId;
    }

    public static boolean deleteMacroLoadGroup(String macroLoadGroupName, String macroLoadGroupId) {
        MockLMDto macroloadGroupDelete = MockLMDto.builder().name(macroLoadGroupName).build();
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteMacroLoadGroup", macroloadGroupDelete,
                macroLoadGroupId);
        assertTrue("Status code should be 200", deleteResponse.statusCode() == 200);
        return true;
    }

    public static MockMacroLoadGroup buildMacroLoadGroup(String macroLoadGroupName, List<MockLMPaoDto> assignedLoadGroups) {
        MockMacroLoadGroup macroLoadGroup = MockMacroLoadGroup.builder()
                .name(macroLoadGroupName)
                .type(MockPaoType.MACRO_GROUP)
                .assignedLoadGroups(assignedLoadGroups)
                .build();
        return macroLoadGroup;
    }

    public static MockLMPaoDto getMockLMPaoDtoObject(MockLoadGroupBase loadGroup) {

        return MockLMPaoDto.builder().id(loadGroup.getId()).name(loadGroup.getName()).type(loadGroup.getType()).build();

    }

}
