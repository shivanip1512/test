package com.eaton.framework;

/*This class will have all the API End points used across the application in a Modular way. 
 * if you are adding any new end points ensure you are putting it in the correct Module
 * 
 * 
 * */
public final class APIs {

    public static final class DemandResponse {
        // Load Group end points
        public static final String UPDATE_LOAD_GROUP = "/api/dr/setup/loadGroup/update/";
        public static final String GET_LOAD_GROUP = "/api/dr/setup/loadGroup/";
        public static final String CREATE_LOAD_GROUP = "/api/dr/setup/loadGroup/create";
        public static final String COPY_LOAD_GROUP = "/api/dr/setup/loadGroup/copy/";
        public static final String DELETE_LOAD_GROUP = "/api/dr/setup/loadGroup/delete/";

        // Macro load end points
        public static final String CREATE_MACRO_LOAD_GROUP = "/api/dr/setup/macroLoadGroup/create";
        public static final String GET_MACRO_LOAD_GROUP = "/api/dr/setup/macroLoadGroup/";
        public static final String UPDATE_MACRO_LOAD_GROUP = "/api/dr/setup/macroLoadGroup/update/";
        public static final String COPY_MACRO_LOAD_GROUP = "/api/dr/setup/macroLoadGroup/copy/";
        public static final String DELETE_MACRO_LOAD_GROUP = "/api/dr/setup/macroLoadGroup/delete/";

        // Program Constraint end points
        public static final String CREATE_PROGRAM_CONSTRAINT = "/api/dr/setup/constraint/create";
        public static final String GET_PROGRAM_CONSTRAINT = "/api/dr/setup/constraint/";
        public static final String UPDATE_PROGRAM_CONSTRAINT = "/api/dr/setup/constraint/update/";
        public static final String DELETE_PROGRAM_CONSTRAINT = "/api/dr/setup/constraint/delete/";

        // Load Program end points
        public static final String CREATE_LOAD_PROGRAM = "/api/dr/setup/loadProgram/create";
        public static final String DELETE_LOAD_PROGRAM = "/api/dr/setup/loadProgram/delete/";
        public static final String GET_LOAD_PROGRAM = "/api/dr/setup/loadProgram/";
        public static final String UPDATE_LOAD_PROGRAM = "/api/dr/setup/loadProgram/update/";
        public static final String COPY_LOAD_PROGRAM = "/api/dr/setup/loadProgram/copy/";

        // Control Area end points
        public static final String CREATE_CONTROL_AREA = "/api/dr/setup/controlArea/create";
        public static final String GET_CONTROL_AREA = "/api/dr/setup/controlArea/";
        public static final String DELETE_CONTROL_AREA = "/api/dr/setup/controlArea/delete/";
        public static final String UPDATE_CONTROL_AREA = "/api/dr/setup/controlArea/update/";

        // Control Scenario Url
        public static final String CREATE_CONTROL_SCENARIO = "/api/dr/setup/controlScenario/create";
        public static final String GET_CONTROL_SCENARIO = "/api/dr/setup/controlScenario/";
        public static final String DELETE_CONTROL_SCENARIO = "/api/dr/setup/controlScenario/delete/";
        public static final String UPDATE_CONTROL_SCENARIO = "/api/dr/setup/controlScenario/update/";
    }

    public static final class CommChannel {
        // Comm Channel
        public static final String CREATE_COMM_CHANNEL = "/api/devices/commChannels";
        public static final String GET_COMM_CHANNEL = "/api/devices/commChannels/";
        public static final String UPDATE_COMM_CHANNEL = "/api/devices/commChannels/";
        public static final String DELETE_COMM_CHANNEL = "/api/devices/commChannels/";
    }

    public static final class Trend {
        // Trend URL
        public static final String CREATE_TREND = "/api/trends";
        public static final String DELETE_TREND = "/api/trends/";
        public static final String UPDATE_TREND = "/api/trends/";
        public static final String GET_TREND = "/api/trends/";
    }
    
    public static final class VirtualDevice {
        // Virtual Device URL
        public static final String CREATE_VIRTUALDEVICE = "/api/device/virtualDevices";
        public static final String DELETE_VIRTUALDEVICE = "/api/device/virtualDevices/";
        public static final String UPDATE_VIRTUALDEVICE = "/api/device/virtualDevices/";
        public static final String GET_VIRTUALDEVICE = "/api/device/virtualDevices/";
        public static final String GET_VIRTUALDEVICES = "/api/device/virtualDevices";
    }
    
    public static final class Point {
    	//Point URL
    	public static final String CREATE_POINT = "/api/points";
        public static final String DELETE_POINT = "/api/points/";
        public static final String UPDATE_POINT = "/api/points/";
        public static final String GET_POINT = "/api/points/";
    }
    
    public static final class Attributes {
        public static final String CREATE_ATTRIBUTE = "/api/attributes";
        public static final String DELETE_ATTRIBUTE = "/api/attributes/";
        public static final String UPDATE_ATTRIBUTE = "/api/attributes/";
        public static final String GET_ATTRIBUTE = "/api/attributes/";
        public static final String GET_ATTRIBUTES = "/api/attributes";
    }
    
    public static final class AttributeAssignment {
        public static final String CREATE_ATTRIBUTE_ASGMT = "/api/attributeAssignments";
        public static final String DELETE_ATTRIBUTE_ASGMT = "/api/attributeAssignments/";
        public static final String UPDATE_ATTRIBUTE_ASGMT = "/api/attributeAssignments/";
        public static final String GET_ATTRIBUTE_ASGMT = "/api/attributeAssignments";
        public static final String GET_ATTRIBUTE_ASGMTS = "/api/attributeAssignments/";
    }
}
