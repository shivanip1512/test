package com.cannontech.rest.api.dr.helper;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockLMPaoDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.documentation.macroloadgroup.MacroLoadGroupSetupApiControllerTest;
import com.cannontech.rest.api.loadgroup.request.MockAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockControlPriority;
import com.cannontech.rest.api.loadgroup.request.MockEmetconAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockEmetconRelayUsage;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupDigiSep;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupDisconnect;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEcobee;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEmetcon;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupHoneywell;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupItron;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupNest;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupPoint;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupVersacom;
import com.cannontech.rest.api.loadgroup.request.MockLoads;
import com.cannontech.rest.api.loadgroup.request.MockMacroLoadGroup;
import com.cannontech.rest.api.loadgroup.request.MockRelays;
import com.cannontech.rest.api.loadgroup.request.MockSepDeviceClass;
import com.cannontech.rest.api.loadgroup.request.MockVersacomAddressUsage;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupHelper {
    private static final Logger log = LogManager.getLogger(LoadGroupHelper.class);
    public final static String CONTEXT_GROUP_ID = "groupId";

    public final static MockLoadGroupBase buildLoadGroup(MockPaoType paoType) {

        MockLoadGroupBase loadGroup = null;

        switch (paoType) {
        case LM_GROUP_METER_DISCONNECT:
            loadGroup = MockLoadGroupDisconnect.builder()
                    .type(paoType)
                    .kWCapacity(163.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .name(getLoadGroupName(paoType))
                    .build();
            break;
        case LM_GROUP_HONEYWELL:
            loadGroup = MockLoadGroupHoneywell.builder()
                    .type(paoType)
                    .kWCapacity(163.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .name(getLoadGroupName(paoType))
                    .build();
            break;
        case LM_GROUP_ECOBEE:
            loadGroup = MockLoadGroupEcobee.builder()
                    .type(paoType)
                    .kWCapacity(163.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .name(getLoadGroupName(paoType))
                    .build();

            break;
        case LM_GROUP_NEST:
            loadGroup = MockLoadGroupNest.builder()
                    .name(getLoadGroupName(paoType))
                    .type(paoType)
                    .kWCapacity(163.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .build();
            break;
        case LM_GROUP_EXPRESSCOMM:
            List<MockAddressUsage> addressUsage = new ArrayList<>();
            addressUsage.add(MockAddressUsage.LOAD);
            addressUsage.add(MockAddressUsage.ZIP);
            addressUsage.add(MockAddressUsage.FEEDER);
            addressUsage.add(MockAddressUsage.GEO);
            addressUsage.add(MockAddressUsage.SUBSTATION);
            addressUsage.add(MockAddressUsage.USER);
            addressUsage.add(MockAddressUsage.PROGRAM);
            addressUsage.add(MockAddressUsage.SPLINTER);

            List<MockLoads> relayUsage = new ArrayList<>();
            relayUsage.add(MockLoads.Load_1);
            relayUsage.add(MockLoads.Load_2);

            loadGroup = MockLoadGroupExpresscom.builder()
                    .name(getLoadGroupName(paoType))
                    .type(MockPaoType.LM_GROUP_EXPRESSCOMM)
                    .routeId(1)
                    .disableControl(false)
                    .disableGroup(false)
                    .kWCapacity(123.0)
                    .addressUsage(addressUsage)
                    .relayUsage(relayUsage)
                    .feeder("1000000000000000")
                    .serviceProvider(100)
                    .geo(223)
                    .user(6)
                    .substation(3)
                    .zip(3334)
                    .splinter(23)
                    .program(12)
                    .protocolPriority(MockControlPriority.DEFAULT)
                    .build();

        case LM_GROUP_RFN_EXPRESSCOMM:
            List<MockAddressUsage> rfnAddressUsage = new ArrayList<>();
            rfnAddressUsage.add(MockAddressUsage.LOAD);
            rfnAddressUsage.add(MockAddressUsage.ZIP);
            rfnAddressUsage.add(MockAddressUsage.FEEDER);
            rfnAddressUsage.add(MockAddressUsage.GEO);
            rfnAddressUsage.add(MockAddressUsage.SUBSTATION);
            rfnAddressUsage.add(MockAddressUsage.USER);
            rfnAddressUsage.add(MockAddressUsage.PROGRAM);
            rfnAddressUsage.add(MockAddressUsage.SPLINTER);

            List<MockLoads> rfnRelayUsage = new ArrayList<>();
            rfnRelayUsage.add(MockLoads.Load_1);
            rfnRelayUsage.add(MockLoads.Load_2);

            loadGroup = MockLoadGroupExpresscom.builder()
                    .name(getLoadGroupName(paoType))
                    .type(MockPaoType.LM_GROUP_RFN_EXPRESSCOMM)
                    .disableControl(false)
                    .disableGroup(false)
                    .kWCapacity(153.0)
                    .addressUsage(rfnAddressUsage)
                    .relayUsage(rfnRelayUsage)
                    .feeder("1000000000000000")
                    .serviceProvider(100)
                    .geo(223)
                    .user(6)
                    .substation(3)
                    .zip(334)
                    .splinter(43)
                    .program(12)
                    .protocolPriority(MockControlPriority.DEFAULT)
                    .build();
            break;
        case LM_GROUP_VERSACOM:

            List<MockVersacomAddressUsage> versacomAddressUsage = new ArrayList<>();
            versacomAddressUsage.add(MockVersacomAddressUsage.UTILITY);
            versacomAddressUsage.add(MockVersacomAddressUsage.SECTION);
            versacomAddressUsage.add(MockVersacomAddressUsage.CLASS);
            versacomAddressUsage.add(MockVersacomAddressUsage.DIVISION);

            List<MockRelays> relays = new ArrayList<>();
            relays.add(MockRelays.RELAY_1);
            relays.add(MockRelays.RELAY_2);
            relays.add(MockRelays.RELAY_3);
            relays.add(MockRelays.RELAY_4);
            loadGroup = MockLoadGroupVersacom.builder()
                    .name(getLoadGroupName(paoType))
                    .type(MockPaoType.LM_GROUP_VERSACOM)
                    .routeId(1)
                    .disableControl(false)
                    .disableGroup(false)
                    .kWCapacity(123.0)
                    .addressUsage(versacomAddressUsage)
                    .relayUsage(relays)
                    .utilityAddress(25)
                    .sectionAddress(27)
                    .classAddress("0000000000000001")
                    .divisionAddress("0000000000000001")
                    .build();

            break;
        case LM_GROUP_DIGI_SEP:

            List<MockSepDeviceClass> deviceClassSet = new ArrayList<>();
            deviceClassSet.add(MockSepDeviceClass.BASEBOARD_HEAT);
            loadGroup = MockLoadGroupDigiSep.builder()
                    .name(getLoadGroupName(paoType))
                    .type(paoType)
                    .disableControl(true)
                    .disableGroup(false)
                    .kWCapacity(23.0)
                    .rampInMinutes(30)
                    .rampOutMinutes(30)
                    .utilityEnrollmentGroup(12)
                    .deviceClassSet(deviceClassSet)
                    .build();
            break;
        case LM_GROUP_ITRON:
            loadGroup = MockLoadGroupItron.builder()
                    .name(getLoadGroupName(paoType))
                    .type(paoType)
                    .kWCapacity(163.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .virtualRelayId(8)
                    .build();
            break;
        case LM_GROUP_EMETCON:
            loadGroup = MockLoadGroupEmetcon.builder()
                    .name(getLoadGroupName(paoType))
                    .type(paoType)
                    .addressUsage(MockEmetconAddressUsage.GOLD)
                    .disableControl(false)
                    .disableGroup(false)
                    .goldAddress(4)
                    .silverAddress(4)
                    .kWCapacity(4.0)
                    .relayUsage(MockEmetconRelayUsage.RELAY_A)
                    .routeId(1)
                    .build();
            break;
        case LM_GROUP_POINT:
            loadGroup = MockLoadGroupPoint.builder()
                    .name(getLoadGroupName(paoType))
                    .type(paoType)
                    .kWCapacity(4.0)
                    .disableControl(false)
                    .disableGroup(false)
                    .deviceIdUsage(2)
                    .pointIdUsage(1234)
                    .startControlRawState(-1)
                    .build();
            break;
        }

        return loadGroup;
    }

    public static String getLoadGroupName(MockPaoType paoType) {
        return ApiUtils.buildFriendlyName(paoType, "LM_GROUP_", "Test");
    }

    public static String getCopiedLoadGroupName(MockPaoType paoType) {
        return ApiUtils.buildFriendlyName(paoType, "LM_GROUP_", "TestCopy");
    }

    public static void deleteLoadGroup(String name, String groupId) {
        MockLMDto lmDeleteObject = MockLMDto.builder().name(name).build();
        Log.info("Delete load group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    /**
     * This method creates load group request and returns loadgroup created
     * 
     * @param MockPaoType - type of load group to be created.
     * @returns load group created
     */
    public static MockLoadGroupBase createLoadGroup(MockPaoType paoType) {
        Random rand = new Random();
        int randomInt = rand.nextInt(10000);
        String name = paoType + "_" + randomInt;

        MockLoadGroupBase loadGroup = buildLoadGroup(paoType);
        loadGroup.setName(name);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        loadGroup.setId(createResponse.path("groupId"));

        return loadGroup;
    }

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
