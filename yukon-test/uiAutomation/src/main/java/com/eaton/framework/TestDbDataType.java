package com.eaton.framework;

import java.util.Random;

public class TestDbDataType {
    
    private static String database = SeleniumTestSetup.getDatabase();

    public enum EnergyCompanyData {
        EC_ID("QA_Test", 124, 64),
        DELETE_EC_ID("AT Delete EC", 216, 822),
        EC_AUTOMATION_OPR_ID("automation", 32, 212),
        EC_DELETE_OPR_ID("ATDeleteUser", 35, 302);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        EnergyCompanyData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public enum MeterData {
        MCT_420CL_CEDIT_ID("AT Could Edit MCT-420cL", 390, 1293),
        RFN_420CL_CEDIT_ID("AT Could Edit RFN-420cL", 391, 1296),
        RFN_420FL_DELETE_ID("AT Delete RFN-420fL", 392, 584),
        RFN_420FL_EDIT_ID("AT Edit RFN-420fL", 399, 492),
        RFN_430SL4_EDIT_ID("AT Edit RFN-430SL4", 400, 585),
        RFN_430SL4_DELETE_ID("AT Delete RFN-430SL4", 393, 586),
        RFN_530S4X_DELETE_ID("AT Delete RFN-530S4x", 394, 588),
        RFN_530S4X_EDIT_ID("AT Edit RFN-530S4x", 401, 587),
        WRL_420CD_DETAIL_ID("AT Detail WRL-420cD", 482, 1325),
        WRL_420CD_DELETE_ID("AT Delete WRL-420cD", 395, 1263),
        WRL_420CD_EDIT_ID("AT Edit WRL-420cD", 402, 1262),
        WRL_420CL_DETAIL_ID("AT Detail WRL-420cL", 483, 1324),
        WRL_420CL_DELETE_ID("AT Delete WRL-420cL", 396, 1233),
        WRL_420CL_EDIT_ID("AT Edit WRL-420cL", 403, 1232),
        MCT_420CL_WILLEDIT_ID("AT Will Edit MCT-420cL", 404, 1294),
        RFN_420CL_WILLEDIT_ID("AT Will Edit RFN-420cL", 405, 1297),
        RFN_420CL_EDITALL_ID("AT Could Edit RFN-420cL", 391, 1296),
        MCT_420CL_WONTEDIT_ID("AT Wont Edit MCT-420cL", 406, 1292),
        RFN_420CL_WONTEDIT_ID("AT Wont Edit RFN-420cL", 407, 1295),
        MCT_310IL_ID("MCT-310IL (230153)", 408, 259);
        
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        MeterData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    }  
    
    public enum VoltVarData {
        AREA_ID("AT Area", 332, 672),
        AREA_EDIT_ID("AT Edit Area", 334, 449),
        AREA_DELETE_ID("AT Delete Area", 333, 579),
        
        CAPBANK_ID("AT Cap Bank", 347, 669),
        CAPBANK_EDIT_ID("AT Edit CapBank", 349, 459),
        CAPBANK_DELETE_ID("AT Delete CapBank", 348, 576),
        
        CBC_ID("AT CBC", 350, 670),
        CBC_EDIT_ID("AT Edit Cbc", 352, 563),
        CBC_DELETE_ID("AT Delete Cbc", 351, 577),
        
        FEEDER_ID("AT Feader", 346, 668),
        FEEDER_EDIT_ID("AT Edit Feeder", 345, 458),
        FEEDER_DELETE_ID("AT Delete Feeder", 344, 575),
        
        REGULATOR_ID("AT Regulator", 354, 671),
        REGULATOR_EDIT_ID("AT Regulator Edit", 355, 490),
        REGULATOR_DELETE_ID("AT Delete Regulator", 353, 578),
        
        SUBBUS_ID("AT Substation Bus", 343, 667),
        SUBBUS_EDIT_ID("AT Edit Bus", 339, 430),
        SUBBUS_DELETE_ID("AT Delete Bus", 342, 574),
        
        SUBSTATION_ID("AT Substation", 338, 666),
        SUBSTATION_EDIT_ID("AT Edit Substation", 340, 451),
        SUBSTATION_DELETE_ID("AT Delete Substation", 341, 573);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        VoltVarData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    } 
    
    public enum DemandResponseData {
        CONTROLAREA_ID("AT Control Area", 356, 662),
        CONTROLAREA_EDIT_ID("AT Edit Control Area", 384, 514),
        CONTROLAREA_DELETE_ID("AT Delete Control Area", 383, 589),
        
        LOADPROGRAM_ID("AT Load Program", 379, 665),
        LOADPROGRAM_EDIT_ID("AT Edit Direct Program", 375, 599),
        LOADPROGRAM_DELETE_ID("AT Delete Direct Program", 371, 605),
        LOADPROGRAM_COPY_ID("AT Copy Direct Program", 370, 604),
        
        LOADGROUP_RFNEXPRESSCOM_COPY_ID("AT Copy RFN Expresscom Ldgrp", 1120, 4978),
        
        SCENARIO_ID("AT Scenario", 387, 663),
        SCENARIO_EDIT_ID("AT Edit Scenario", 386, 590),
        SCENARIO_DELETE_ID("AT Delete Scenario", 385, 619);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        DemandResponseData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    } 
    
    public enum TrendPointData {
        CREATE_TREND_ANALOG_POINT_ID("Analog Point for Create Trend", 1601, 5231),
        EDIT_TREND_CALCANALOG_POINT_ID("Calc Analog Point for Edit Trends", 1602, 5232);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        TrendPointData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public enum AdminData {
        QA_ADMIN_USERGROUP_ID("QA Admin User Grp", 70, 42);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        AdminData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public enum CommChannelData {
        A_IPC410FL_ID("a_TCP_Port", 74, 23);
        
        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        CommChannelData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
    }
    
    public enum CommunicationRouteData {
        ACCU710A("a_CCU-710A", 79, 28),
        ACCU711("a_CCU-711", 83, 32),
        ACCU721("a_CCU-721", 87, 36),
        ALCUEASTRIVER("a_LCU-EASTRIVER", 91, 40),
        APAGINGTAPTERMINAL("a_PAGING TAP TERMINAL", 94, 43),
        AREPEATER800("a_REPEATER-800", 103, 52),
        AREPEATER801("a_REPEATER-801", 106, 55),
        AREPEATER900("a_REPEATER-900", 97, 46),
        AREPEATER902("a_REPEATER-902", 100, 49),
        AREPEATER921("a_REPEATER-921", 109, 58),
        ARTC("a_RTC", 113, 62),
        ARTULMI("a_RTU-LMI", 117, 66),
        ASNPPTERMINAL("a_SNPP-TERMINAL", 120, 69),
        ATCU5000("a_TCU-5000", 124, 73),
        ATCU5500("a_TCU-5500", 128, 77),
        AWCTPTERMINAL("a_WCTP-TERMINAL", 131, 80),
        AXML("a_XML", 135, 84);

        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        CommunicationRouteData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }
        
        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public static CommunicationRouteData getRandomRouteId() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum PointData {
        SCADA_OVERRIDE("SCADA Override", 1114, 4230),
        CVR_MODE("CVR Mode", 1117, 4231),
        CAPACITOR_BANK_STATE("Capacitor Bank State", 1079, 4249),
        REMOTE_CONTROL_MODE("Remote Control Mode", 1108, 4251),
        OVUV_CONTROL("OVUV Control", 1105, 4255),
        MANUAL_CONTROL_MODE("Manual Control Mode", 1084, 4265);

        private final String name;
        private final Integer oracleId;
        private final Integer sqlId;
        
        PointData(String name, Integer oracleId, Integer sqlId) {
            this.name = name;
            this.oracleId = oracleId;
            this.sqlId = sqlId;
        }

        public Integer getId() {
            Integer id = -999;
            
            if(TestDbDataType.database.equalsIgnoreCase("ORACLE")) {
                id = oracleId;
            }
            
            if(TestDbDataType.database.equalsIgnoreCase("SQL")) {
                id = sqlId;
            }
            
            return id;
        }
        
        public String getName() {
            return name;
        }

        public static PointData getRandomPointId() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}