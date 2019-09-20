package com.cannontech.rest.api.helper;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.JsonFileReader;
import io.restassured.response.ExtractableResponse;

/**
 * 
 * This Class contains helper methods to create and delete any load group
 * this is basically useful to create and delete loadgroup data in our test cases
 */

public class LoadGroupHelper {

    private static Logger log = LogManager.getLogger(LoadGroupHelper.class);

    /**
     * This method creates load group and returns List<Object> with name and groupId
     * of load group created
     * 
     * @param loadGroup - type of load group to be created. Available loadgroup
     *        types are (Ecobee, DigiSep, Emetcon, Itron, Expresscom,
     *        RFNExpresscom, Honeywell, MeterDisconnect, Versacom)
     * @returns List<Object> - [name, groupId] of load group created
     */
    public static List<Object> createLoadGroup(String loadGroup) {
        Random rand = new Random();
        int str = rand.nextInt(10000);
        String name = "Auto" + loadGroup + str;
        String payloadFile = "";
        int groupId = 0;

        switch (loadGroup) {
        case "Ecobee":
            payloadFile = "loadgroup\\lmGroupEcobeeCreate.json";
            break;
        case "DigiSep":
            payloadFile = "loadgroup\\lmGroupDigiSepCreate.json";
            break;
        case "Emetcon":
            payloadFile = "loadgroup\\lmGroupEmetconCreate.json";
            break;
        case "Itron":
            payloadFile = "loadgroup\\lmGroupItronCreate.json";
            break;
        case "Expresscom":
            payloadFile = "loadgroup\\lmGroupExpresscomCreate.json";
            break;
        case "RFNExpresscom":
            payloadFile = "loadgroup\\lmGroupRFNExpresscomCreate.json";
            break;
        case "Honeywell":
            payloadFile = "loadgroup\\lmGroupHoneywellCreate.json";
            break;
        case "MeterDisconnect":
            payloadFile = "loadgroup\\lmGroupMeterDisconnectCreate.json";
            break;
        case "Versacom":
            payloadFile = "loadgroup\\lmGroupVersacomCreate.json";
            break;
        }

        JSONObject payload = JsonFileReader.updateLoadGroup(payloadFile, "name", name);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", payload);

        assertTrue("Error in creating Load group", createResponse.statusCode() == 200);
        log.info("LoadGroup " + loadGroup + " created successfully");

        return Arrays.asList(name, groupId);
    }

    /**
     * This method deletes load group
     * 
     * @param name - name of load group to be deleted
     * @param groupId - groupId of load group to be deleted
     */
    public static void deleteLoadGroup(String name, String groupId) {

        JSONObject payload = JsonFileReader.updateJsonFile("loadgroup\\delete.json", "name", name);
        log.info("Delete payload is : " + payload);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", payload, groupId);
        assertTrue("Error in deleting Load group", response.statusCode() == 200);
        log.info("Loadgroup " + name + " deleted successfully.");
    }
}
