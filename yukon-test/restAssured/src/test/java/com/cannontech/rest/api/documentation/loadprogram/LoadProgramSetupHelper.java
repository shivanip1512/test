package com.cannontech.rest.api.documentation.loadprogram;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.JsonFileReader;

public class LoadProgramSetupHelper {


    @SuppressWarnings("unchecked")
    public static JSONObject buildJSONRequest(JSONObject constraintJson,JSONObject loadGroupJson, String inputFilePath) {
        JSONObject jsonObject = JsonFileReader.readJsonFileAsJSONObject(inputFilePath);
            JSONObject jsonArrayObject = new JSONObject();
            jsonArrayObject.put("groupId", loadGroupJson.get("assignedLoadGroupId"));
            jsonArrayObject.put("groupName", loadGroupJson.get("loadGroupName"));
            jsonArrayObject.put("type", loadGroupJson.get("loadGroupType"));
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(jsonArrayObject);
            JSONObject constraint =(JSONObject) jsonObject.get("constraint");
            constraint.put("constraintId",constraintJson.get("constraintId"));
            jsonObject.put("constraint", constraint);
            jsonObject.put("assignedGroups", jsonArray);
            return jsonObject;
    }
    
    @SuppressWarnings("unchecked")
    public static void delete(Integer id, String name,String url) {
        JSONObject programObj = new JSONObject();
        programObj.put("name", name);
        
        given().accept("application/json")
               .contentType("application/json")
               .header( "Authorization", "Bearer " + ApiCallHelper.authToken)
               .body(programObj)
               .when()
               .delete(ApiCallHelper.getProperty(url) + id)
               .then()
               .extract();
    }
    
    public static FieldDescriptor[] getloadProgramDefaultFieldsFieldDescriptors() {
        
        FieldDescriptor[] loadProgramDefaultFields = new FieldDescriptor[] {
            fieldWithPath("programId").type(JsonFieldType.NUMBER).optional().description("Load Program Id"), 
            fieldWithPath("name").type(JsonFieldType.STRING).description("Load Program name"), 
            fieldWithPath("type").type(JsonFieldType.STRING).description("Load Program type"),
            fieldWithPath("operationalState").type(JsonFieldType.STRING).description("Operational State of load program"),
            fieldWithPath("constraint.constraintId").type(JsonFieldType.NUMBER).description("Constraint Id"),
            fieldWithPath("triggerOffset").type(JsonFieldType.NUMBER).description("Trigger offset. Min Value: 0.0, Max Value: 99999.9999"),
            fieldWithPath("restoreOffset").type(JsonFieldType.NUMBER).description("Restore offset. Min Value: -9999.9999 , Max Value: 99999.9999"),
            
            fieldWithPath("gears[].gearName").type(JsonFieldType.STRING).description("Name of gear"),
            fieldWithPath("gears[].gearNumber").type(JsonFieldType.NUMBER).description("Gear number"),
            fieldWithPath("gears[].controlMethod").type(JsonFieldType.STRING).description("Control method used in gear/type of gear"),};
        
        return loadProgramDefaultFields;
    }
    public static FieldDescriptor[] getloadProgramControlFieldsFieldDescriptors() {
        FieldDescriptor[] loadProgramControlFields = new FieldDescriptor[] {
            fieldWithPath("controlWindow.controlWindowOne.availableStartTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Start Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowOne.availableStopTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Stop Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowTwo.availableStartTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Start Time In Minutes"),
            fieldWithPath("controlWindow.controlWindowTwo.availableStopTimeInMinutes").type(JsonFieldType.NUMBER).description("Available Stop Time In Minutes"),

            fieldWithPath("assignedGroups[].groupId").type(JsonFieldType.NUMBER).description("Assigned Load Group Id"),
            fieldWithPath("assignedGroups[].groupName").type(JsonFieldType.STRING).description("Assigned Load Group Name"),
            fieldWithPath("assignedGroups[].type").type(JsonFieldType.STRING).description("Assigned  Load Group Type"),
            
            fieldWithPath("notification.notifyOnAdjust").type(JsonFieldType.BOOLEAN).description("Notify onaAdjust"),
            fieldWithPath("notification.enableOnSchedule").type(JsonFieldType.BOOLEAN).description("Enable on schedule"),
            fieldWithPath("notification.assignedNotificationGroups[]").type(JsonFieldType.ARRAY).description("assigned notification groups"),
            fieldWithPath("notification.assignedNotificationGroups[].notificationGrpID").type(JsonFieldType.NUMBER).description("Notification Id"),
            fieldWithPath("notification.assignedNotificationGroups[].notificationGrpName").type(JsonFieldType.STRING).description("Notification Group Name") };
        return loadProgramControlFields;
        }
        
    public static List<FieldDescriptor> mergeFieldDescriptors(FieldDescriptor[] GearFieldDescriptor) {
        List<FieldDescriptor> fieldDescriptorList = new ArrayList<FieldDescriptor>();
        fieldDescriptorList.addAll(0, Arrays.asList(getloadProgramDefaultFieldsFieldDescriptors()));
        fieldDescriptorList.addAll(1, Arrays.asList(GearFieldDescriptor));
        fieldDescriptorList.addAll(2, Arrays.asList(getloadProgramControlFieldsFieldDescriptors()));
        return fieldDescriptorList;
    }

}
