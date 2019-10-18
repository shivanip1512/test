package com.cannontech.rest.api.dr.helper;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.ApiUtils;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.loadgroup.request.MockAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockControlPriority;
import com.cannontech.rest.api.loadgroup.request.MockEmetconAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockEmetconRelayUsage;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupDigiSep;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupDisconnect;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEcobee;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEmetcon;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupHoneywell;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupItron;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupNest;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupVersacom;
import com.cannontech.rest.api.loadgroup.request.MockLoads;
import com.cannontech.rest.api.loadgroup.request.MockRelays;
import com.cannontech.rest.api.loadgroup.request.MockSepDeviceClass;
import com.cannontech.rest.api.loadgroup.request.MockVersacomAddressUsage;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupHelper {

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
                // loadGroup = LoadGroupExpresscom.builder().build()
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
     * @param MockPaoType - type of load group to be created.
     * @returns load group created
     */
    public static MockLoadGroupBase createLoadGroup(MockPaoType paoType) {
        Random rand = new Random();
        int randomInt = rand.nextInt(10000);
        String name = paoType + "" + randomInt;

        MockLoadGroupBase loadGroup = buildLoadGroup(paoType);
        loadGroup.setName(name);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        loadGroup.setId(createResponse.path("groupId"));
        
        return loadGroup;
    }
}
