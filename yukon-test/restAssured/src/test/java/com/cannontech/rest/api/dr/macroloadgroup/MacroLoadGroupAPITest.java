package com.cannontech.rest.api.dr.macroloadgroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiFieldError;
import com.cannontech.rest.api.common.model.MockLMPaoDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockMacroLoadGroup;

import io.restassured.response.ExtractableResponse;

/*
 * List of allowed Load Groups in any Macro Load Group DigiSEP, RFNExpresscom, Emetcon, Versacom, MeterDisconnect
 * List of not allowed Load Groups in any Macro Load Group Honeywell, Nest,Ecobee,Itron,Macro Load Group
 */

@Listeners(com.cannontech.test.base.TestBase.class)
public class MacroLoadGroupAPITest {
    private static final Logger log = LogManager.getLogger(MacroLoadGroupAPITest.class);
    private ArrayList<MockLMPaoDto> allLoadGroups = new ArrayList<>();

    @Test
    public void macroLoadGroup_01_createWithDigiSEPLoadGroup(ITestContext context) {
        String mlgName = "MLG001_DigiSEP";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.createMacroLoadGroup(mlgName,
                List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_DIGISEP", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_DIGISEP", mlgName);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " created successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_01_createWithDigiSEPLoadGroup" })
    public void macroLoadGroup_02_getWithDigiSEPLoadGroup(ITestContext context) {
        LoadGroupHelper
                .getMacroLoadGroup(context.getAttribute("MACRO_GROUP_ID_DIGISEP").toString());
        log.info("Macro Load Group get request successful");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_01_createWithDigiSEPLoadGroup" })
    public void macroLoadGroup_03_updateWithDigiSEPLoadGroup(ITestContext context) {
        String mlgNameUpdated = "MLG001_DigiSEP_Updated";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.updateMacroLoadGroup(
                context.getAttribute("MACRO_GROUP_ID_DIGISEP").toString(),
                mlgNameUpdated, List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_DIGISEP", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_DIGISEP", mlgNameUpdated);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " updated successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_01_createWithDigiSEPLoadGroup" })
    public void macroLoadGroup_04_copyWithDigiSEPLoadGroup(ITestContext context) {
        String mlgNameCopy = "MLG001_DigiSEP_Copy";
        Integer copiedMacroLoadGroupID = LoadGroupHelper.copyMacroLoadGroup(mlgNameCopy,
                context.getAttribute("MACRO_GROUP_ID_DIGISEP").toString());
        context.setAttribute("COPY_MACRO_LOAD_GROUP_ID_DIGISEP", copiedMacroLoadGroupID);
        context.setAttribute("COPY_MACRO_LOAD_GROUP_NAME_DIGISEP", mlgNameCopy);
        log.info("Macro Load Group '" + mlgNameCopy + "' with load group id " + copiedMacroLoadGroupID
                + " copied successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_01_createWithDigiSEPLoadGroup" })
    public void macroLoadGroup_05_deleteWithDigiSEPLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("MACRO_LOAD_GROUP_NAME_DIGISEP").toString(),
                context.getAttribute("MACRO_GROUP_ID_DIGISEP").toString());
        log.info("Macro Load Group " + context.getAttribute("MACRO_LOAD_GROUP_NAME_DIGISEP").toString()
                + " deleted successfully.");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_04_copyWithDigiSEPLoadGroup" })
    public void macroLoadGroup_06_deleteWithCopiedDigiSEPLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_DIGISEP").toString(),
                context.getAttribute("COPY_MACRO_LOAD_GROUP_ID_DIGISEP").toString());
        log.info("Deleted Macro Load Group " + context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_DIGISEP").toString()
                + " deleted successfully.");
    }

    @Test
    public void macroLoadGroup_07_createWithEmetconLoadGroup(ITestContext context) {
        String mlgName = "MLG001_Emetcon";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.createMacroLoadGroup(mlgName,
                List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_EMETCON", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_EMETCON", mlgName);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " created successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_07_createWithEmetconLoadGroup" })
    public void macroLoadGroup_08_getWithEmetconLoadGroup(ITestContext context) {
        LoadGroupHelper
                .getMacroLoadGroup(context.getAttribute("MACRO_GROUP_ID_EMETCON").toString());
        log.info("Macro Load Group get request successful");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_07_createWithEmetconLoadGroup" })
    public void macroLoadGroup_09_updateWithEmetconLoadGroup(ITestContext context) {
        String mlgNameUpdated = "MLG001_Emetcon_Updated";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.updateMacroLoadGroup(
                context.getAttribute("MACRO_GROUP_ID_EMETCON").toString(),
                mlgNameUpdated, List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_EMETCON", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_EMETCON", mlgNameUpdated);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " updated successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_07_createWithEmetconLoadGroup" })
    public void macroLoadGroup_10_copyWithEmetconLoadGroup(ITestContext context) {
        String mlgNameCopy = "MLG001_Emetcon_Copy";
        Integer copiedMacroLoadGroupID = LoadGroupHelper.copyMacroLoadGroup(mlgNameCopy,
                context.getAttribute("MACRO_GROUP_ID_EMETCON").toString());
        context.setAttribute("COPY_MACRO_LOAD_GROUP_ID_EMETCON", copiedMacroLoadGroupID);
        context.setAttribute("COPY_MACRO_LOAD_GROUP_NAME_EMETCON", mlgNameCopy);
        log.info("Macro Load Group '" + mlgNameCopy + "' with load group id " + copiedMacroLoadGroupID
                + " copied successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_07_createWithEmetconLoadGroup" })
    public void macroLoadGroup_11_deleteWithEmetconLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("MACRO_LOAD_GROUP_NAME_EMETCON").toString(),
                context.getAttribute("MACRO_GROUP_ID_EMETCON").toString());
        log.info("Macro Load Group " + context.getAttribute("MACRO_LOAD_GROUP_NAME_EMETCON").toString()
                + " deleted successfully.");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_10_copyWithEmetconLoadGroup" })
    public void macroLoadGroup_12_deleteWithCopiedEmetconLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_EMETCON").toString(),
                context.getAttribute("COPY_MACRO_LOAD_GROUP_ID_EMETCON").toString());
        log.info("Deleted Macro Load Group " + context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_EMETCON").toString()
                + " deleted successfully.");
    }

    @Test
    public void macroLoadGroup_13_createWithVersacomLoadGroup(ITestContext context) {
        String mlgName = "MLG001_Versacom";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_VERSACOM);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.createMacroLoadGroup(mlgName,
                List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_VERSACOM", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_VERSACOM", mlgName);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " created successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_13_createWithVersacomLoadGroup" })
    public void macroLoadGroup_14_getWithVersacomLoadGroup(ITestContext context) {
        LoadGroupHelper
                .getMacroLoadGroup(context.getAttribute("MACRO_GROUP_ID_VERSACOM").toString());
        log.info("Macro Load Group get request successful");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_13_createWithVersacomLoadGroup" })
    public void macroLoadGroup_15_updateWithVersacomLoadGroup(ITestContext context) {
        String mlgNameUpdated = "MLG001_Versacom_Updated";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_VERSACOM);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.updateMacroLoadGroup(
                context.getAttribute("MACRO_GROUP_ID_VERSACOM").toString(),
                mlgNameUpdated, List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_VERSACOM", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_VERSACOM", mlgNameUpdated);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " updated successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_13_createWithVersacomLoadGroup" })
    public void macroLoadGroup_16_copyWithVersacomLoadGroup(ITestContext context) {
        String mlgNameCopy = "MLG001_Versacom_Copy";
        Integer copiedMacroLoadGroupID = LoadGroupHelper.copyMacroLoadGroup(mlgNameCopy,
                context.getAttribute("MACRO_GROUP_ID_VERSACOM").toString());
        context.setAttribute("COPY_MACRO_LOAD_GROUP_ID_Versacom", copiedMacroLoadGroupID);
        context.setAttribute("COPY_MACRO_LOAD_GROUP_NAME_VERSACOM_Versacom", mlgNameCopy);
        log.info("Macro Load Group '" + mlgNameCopy + "' with load group id " + copiedMacroLoadGroupID
                + " copied successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_13_createWithVersacomLoadGroup" })
    public void macroLoadGroup_17_deleteWithVersacomLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("MACRO_LOAD_GROUP_NAME_VERSACOM").toString(),
                context.getAttribute("MACRO_GROUP_ID_VERSACOM").toString());
        log.info("Macro Load Group " + context.getAttribute("MACRO_LOAD_GROUP_NAME_VERSACOM").toString()
                + " deleted successfully.");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_16_copyWithVersacomLoadGroup" })
    public void macroLoadGroup_18_deleteWithCopiedVersacomLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_VERSACOM_Versacom").toString(),
                context.getAttribute("COPY_MACRO_LOAD_GROUP_ID_Versacom").toString());
        log.info("Deleted Macro Load Group " + context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_VERSACOM_Versacom").toString()
                + " deleted successfully.");
    }

    @Test
    public void macroLoadGroup_19_createWithMeterDisconnectLoadGroup(ITestContext context) {
        String mlgName = "MLG001_MeterDisconnect";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.createMacroLoadGroup(mlgName,
                List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_METERDISCONNECT", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_MeterDisconnect", mlgName);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " created successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_19_createWithMeterDisconnectLoadGroup" })
    public void macroLoadGroup_20_getWithMeterDisconnectLoadGroup(ITestContext context) {
        LoadGroupHelper
                .getMacroLoadGroup(context.getAttribute("MACRO_GROUP_ID_METERDISCONNECT").toString());
        log.info("Macro Load Group get request successful");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_19_createWithMeterDisconnectLoadGroup" })
    public void macroLoadGroup_21_updateWithMeterDisconnectLoadGroup(ITestContext context) {
        String mlgNameUpdated = "MLG001_MeterDisconnect_Updated";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        allLoadGroups.add(loadGroupToBeAssigned);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.updateMacroLoadGroup(
                context.getAttribute("MACRO_GROUP_ID_METERDISCONNECT").toString(),
                mlgNameUpdated, List.of(loadGroupToBeAssigned));
        context.setAttribute("MACRO_GROUP_ID_METERDISCONNECT", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_MeterDisconnect", mlgNameUpdated);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " updated successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_19_createWithMeterDisconnectLoadGroup" })
    public void macroLoadGroup_22_copyWithMeterDisconnectLoadGroup(ITestContext context) {
        String mlgNameCopy = "MLG001_MeterDisconnect_Copy";
        Integer copiedMacroLoadGroupID = LoadGroupHelper.copyMacroLoadGroup(mlgNameCopy,
                context.getAttribute("MACRO_GROUP_ID_METERDISCONNECT").toString());
        context.setAttribute("COPY_MACRO_LOAD_GROUP_ID_MeterDisconnect", copiedMacroLoadGroupID);
        context.setAttribute("COPY_MACRO_LOAD_GROUP_NAME_MeterDisconnect_MeterDisconnect", mlgNameCopy);
        log.info("Macro Load Group '" + mlgNameCopy + "' with load group id " + copiedMacroLoadGroupID
                + " copied successfully");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_19_createWithMeterDisconnectLoadGroup" })
    public void macroLoadGroup_23_deleteWithMeterDisconnectLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(context.getAttribute("MACRO_LOAD_GROUP_NAME_MeterDisconnect").toString(),
                context.getAttribute("MACRO_GROUP_ID_METERDISCONNECT").toString());
        log.info("Macro Load Group " + context.getAttribute("MACRO_LOAD_GROUP_NAME_MeterDisconnect").toString()
                + " deleted successfully.");
    }

    @Test(dependsOnMethods = { "macroLoadGroup_22_copyWithMeterDisconnectLoadGroup" })
    public void macroLoadGroup_24_deleteWithCopiedMeterDisconnectLoadGroup(ITestContext context) {
        LoadGroupHelper.deleteMacroLoadGroup(
                context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_MeterDisconnect_MeterDisconnect").toString(),
                context.getAttribute("COPY_MACRO_LOAD_GROUP_ID_MeterDisconnect").toString());
        log.info("Deleted Macro Load Group "
                + context.getAttribute("COPY_MACRO_LOAD_GROUP_NAME_MeterDisconnect_MeterDisconnect").toString()
                + " deleted successfully.");

    }

    @Test(dependsOnMethods = { "macroLoadGroup_01_createWithDigiSEPLoadGroup" })
    public void macroLoadGroup_25_createWithMultipleAllowedLoadGroups(ITestContext context) {
        String mlgName = "MLG_WithMultipleLoadGroups";
        // Add MockLMPaoDto objects to global array to use later in MLG creation with multiple LoadGroups
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.createMacroLoadGroup(mlgName, allLoadGroups);
        context.setAttribute("MACRO_GROUP_ID_All_GROUP", macroLoadGroup.getId());
        context.setAttribute("MACRO_LOAD_GROUP_NAME_All_GROUP", mlgName);
        log.info("Macro Load Group '" + macroLoadGroup.getName() + "' with load group id " + macroLoadGroup.getId()
                + " created successfully");
    }

    /**
     * Negative scenario: validation Macro Load Group can't be created with another Macro Load Group
     */
    @Test(dependsOnMethods = { "macroLoadGroup_25_createWithMultipleAllowedLoadGroups" })
    public void macroLoadGroup_26_cannotBeCreateWithAnotherMLG(ITestContext context) {
        String mlgName = "MLG_MacroLoadGroup";
        String expectedErrorMsg = "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group";
        // build Macro Load Group which needs to be assigned in Macro load Group
        Integer loadGrpId = (Integer) context.getAttribute("MACRO_GROUP_ID_All_GROUP");
        String loadGrpName = (String) context.getAttribute("MACRO_LOAD_GROUP_NAME_All_GROUP");
        MockLMPaoDto loadGroupToBeAssigned = MockLMPaoDto.builder().id(loadGrpId).name(loadGrpName)
                .type(MockPaoType.MACRO_GROUP).build();
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 400, "Status code should be 400");
        Assert.assertTrue(response.path("message").toString().contains(expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
        log.info(response.path("message").toString());
    }

    @Test
    public void macroLoadGroup_27_nameLengthValidation(ITestContext context) {
        // Test data
        String mlgName = "MLG_name_more_then_60_chars_is_not_allowed_MLG_name_more_then_60_chars_is_not_allowed";
        String expectedErrorMsg = "Exceeds maximum length of 60.";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        // create MockLMPaoDto object from created load group object
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 422, "Status code should be 422");
        Assert.assertTrue(response.path("message").toString().equals("Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(validateFieldError(response, expectedErrorMsg), "Expected Error not found:" + expectedErrorMsg);
    }

    @Test
    public void macroLoadGroup_28_notAllowedCharsValidationInName(ITestContext context) {
        // Test data
        String mlgName = "MLG_dummy";
        char notAllowedChars[] = { '/', '\\', ',', '\'', '"', '|' };
        String expectedErrorMsg = "cannot be blank or include any of the following characters: / \\ , ' \" |";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        for (char ch : notAllowedChars) {
            String invaidGrpName = mlgName + ch;
            MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(invaidGrpName,
                    List.of(loadGroupToBeAssigned));
            // get post response
            ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
            // validations
            Assert.assertTrue(response.statusCode() == 422, "Status code should be 422");
            Assert.assertTrue(validateFieldError(response, expectedErrorMsg), "Expected Error not found:" + expectedErrorMsg);
        }

    }

    /**
     * Negative scenario: validation Macro Load Group can't be created with Honeywell Load Group
     */
    @Test
    public void macroLoadGroup_29_cannotBeCreateWithHoneywell(ITestContext context) {
        // Test data
        String mlgName = "MLG_Honeywell";
        String expectedErrorMsg = "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 400, "Status code should be 400");
        Assert.assertTrue(response.path("message").toString().contains(expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
        log.info(response.path("message").toString());
    }

    /**
     * Negative scenario: validation Macro Load Group can't be created with ECOBEE Load Group
     */
    @Test
    public void macroLoadGroup_30_cannotBeCreateWithEcobee(ITestContext context) {
        // Test data
        String mlgName = "MLG_ECOBEE";
        String expectedErrorMsg = "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 400, "Status code should be 400");
        Assert.assertTrue(response.path("message").toString().contains(expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
        log.info(response.path("message").toString());
    }

    /**
     * Negative scenario: validation Macro Load Group can't be created with Itron Load Group
     */
    @Test
    public void macroLoadGroup_31_cannotBeCreateWithItron(ITestContext context) {
        // Test data
        String mlgName = "MLG_Itron";
        String expectedErrorMsg = "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ITRON);
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 400, "Status code should be 400");
        Assert.assertTrue(response.path("message").toString().contains(expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
        log.info(response.path("message").toString());
    }

    /**
     * Negative Scenario: validation Macro Load Group can't be created with Nest Load Group
     */
    @Test
    public void macroLoadGroup_32_cannotBeCreateWithNest(ITestContext context) {
        // Test data
        String mlgName = "MLG_Nest";
        String expectedErrorMsg = "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group";
        // create Load Group which needs to be assigned in Macro load Group
        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_NEST);
        MockLMPaoDto loadGroupToBeAssigned = LoadGroupHelper.getMockLMPaoDtoObject(loadGroup);
        MockMacroLoadGroup macroLoadGroup = LoadGroupHelper.buildMacroLoadGroup(mlgName, List.of(loadGroupToBeAssigned));
        // get post response
        ExtractableResponse<?> response = ApiCallHelper.post("saveMacroLoadGroup", macroLoadGroup);
        // validations
        Assert.assertTrue(response.statusCode() == 400, "Status code should be 400");
        Assert.assertTrue(response.path("message").toString().contains(expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
        log.info(response.path("message").toString());
    }

    /**
     * This function validates validateErrMessage and returns boolean in Macro Load Group Error Response
     */
    public boolean validateFieldError(ExtractableResponse<?> response, String validateErrMessage) {
        MockApiError mockApiError = response.as(MockApiError.class);
        List<MockApiFieldError> mockApiFieldError = mockApiError.getFieldErrors();
        for (MockApiFieldError err : mockApiFieldError) {
            if (err.getCode().contains(validateErrMessage) && err.getField().equals("name")) {
                log.info(err.getCode().toString());
                return true;
            }
        }
        return false;
    }
}
