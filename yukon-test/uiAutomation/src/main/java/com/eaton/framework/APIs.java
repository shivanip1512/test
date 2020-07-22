package com.eaton.framework;
/*This class will have all the API End points used across the application in a Modular way. 
 * if you are adding any new end points ensure you are putting it in the correct Module
 * 
 * 
 * */
public final class APIs {

    public static final class DemandResponse {
        // Load Group end points
        public static final String updateLoadGroup = "/api/dr/setup/loadGroup/update/";
        public static final String getLoadGroup = "/api/dr/setup/loadGroup/";
        public static final String createLoadGroup = "/api/dr/setup/loadGroup/create";
        public static final String copyLoadGroup = "/api/dr/setup/loadGroup/copy/";
        public static final String deleteLoadGroup = "/api/dr/setup/loadGroup/delete/";

        // Macro load end points
        public static final String createMacroLoadGroup = "/api/dr/setup/macroLoadGroup/create";
        public static final String getMacroloadgroup = "/api/dr/setup/macroLoadGroup/";
        public static final String updateMacroLoadGroup = "/api/dr/setup/macroLoadGroup/update/";
        public static final String copyMacroLoadGroup = "/api/dr/setup/macroLoadGroup/copy/";
        public static final String deleteMacroLoadGroup = "/api/dr/setup/macroLoadGroup/delete/";

        // Program Constraint end points
        public static final String createProgramConstraint = "/api/dr/setup/constraint/create";
        public static final String getProgramConstraint = "/api/dr/setup/constraint/";
        public static final String updateProgramConstraint = "/api/dr/setup/constraint/update/";
        public static final String deleteProgramConstraint = "/api/dr/setup/constraint/delete/";

        // Load Program end points
        public static final String createLoadProgram = "/api/dr/setup/loadProgram/create";
        public static final String deleteLoadProgram = "/api/dr/setup/loadProgram/delete/";
        public static final String getLoadProgram = "/api/dr/setup/loadProgram/";
        public static final String updateLoadProgram = "/api/dr/setup/loadProgram/update/";
        public static final String copyLoadProgram = "/api/dr/setup/loadProgram/copy/";

        // Control Area end points
        public static final String createControlArea = "/api/dr/setup/controlArea/create";
        public static final String getControlArea = "/api/dr/setup/controlArea/";
        public static final String deleteControlArea = "/api/dr/setup/controlArea/delete/";
        public static final String updateControlArea = "/api/dr/setup/controlArea/update/";

        // Control Scenario Url
        public static final String createControlScenario = "/api/dr/setup/controlScenario/create";
        public static final String getControlScenario = "/api/dr/setup/controlScenario/";
        public static final String deleteControlScenario = "/api/dr/setup/controlScenario/delete/";
        public static final String updateControlScenario = "/api/dr/setup/controlScenario/update/";
    }

    public static final class CommChannel {

        //Comm Channel
        public static final String createCommChannel = "/api/devices/commChannels";
        public static final String getCommChannel = "/api/devices/commChannels/";
        public static final String updateCommChannel = "/api/devices/commChannels/";
        public static final String deleteCommChannel = "/api/devices/commChannels/";
    }

}
