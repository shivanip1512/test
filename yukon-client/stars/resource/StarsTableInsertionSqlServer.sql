/* All the "dummy" entries */
/* LMProgram: insert an empty line for appliances without a program assigned */
insert into LMProgram values(0, 'Automatic', 'NNNN', 'NNNNNNNN', 0, 0, 0, 0, 0, 0, 0, 0);

/* Route */
INSERT INTO Route VALUES (0,0,'N');

/* EnergyCompany */
INSERT INTO YukonUser VALUES (-2,'DefaultCompany','DefaultCompany',0,'01-JAN-90','Enabled');
INSERT INTO EnergyCompany VALUES (-1,'Default Energy Company',0,-2);
INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-2);

/* YukonWebConfiguration */
INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');

/* ServiceCompany */
INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');

/* Substation */
INSERT INTO Substation VALUES (0,'(none)',0);

/* CustomerBase */
INSERT INTO Customer VALUES (-1,0,0,'(none)');

/* SiteInformation */
INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);

/* AccountSite */
INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)');

/* CustomerAccount */
INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)',-1);

/* InventoryBase */
INSERT INTO InventoryBase VALUES (-1,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'Default Thermostat',0);

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (-1,-1);

/* YukonSelectionList */
insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection', 'DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection', 'CallType','N');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection', 'ServiceType','N');
insert into YukonSelectionList values (1010,'N','(none)','Service request status', 'ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection', 'SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection', 'Manufacturer','Y');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection', 'ApplianceLocation','N');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection', 'ChanceOfControl','Y');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection', 'TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection', 'QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection', 'AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection', 'ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection', 'ThermostatFanState','N');
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups', 'CustomerFAQGroup','N');
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection', 'ResidenceType','N');
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection', 'ConstructionMaterial','N');
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection', 'DecadeBuilt','N');
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection', 'SquareFeet','N');
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection', 'InsulationDepth','N');
insert into YukonSelectionList values (1026,'N','(none)','General condition selection', 'GeneralCondition','N');
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection', 'CoolingSystem','N');
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection', 'HeatingSystem','N');
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection', 'NumberOfOccupants','N');
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection', 'OwnershipType','N');
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection', 'FuelType','N');
insert into YukonSelectionList values (1032,'N','(none)','Tonnage selection', 'Tonnage','N');
insert into YukonSelectionList values (1033,'N','(none)','AC type selection', 'ACType','N');
insert into YukonSelectionList values (1034,'N','(none)','Number of gallons selection', 'NumberOfGallons','N');
insert into YukonSelectionList values (1035,'N','(none)','Energy source selection', 'EnergySource','N');
insert into YukonSelectionList values (1036,'N','(none)','Switch over type selection', 'SwitchOverType','N');
insert into YukonSelectionList values (1037,'N','(none)','Transfer switch type selection', 'TransferSwitchType','N');
insert into YukonSelectionList values (1038,'N','(none)','Transfer switch manufacturer selection', 'TransferSwitchMfg','N');
insert into YukonSelectionList values (1039,'N','(none)','Dryer type selection', 'DryerType','N');
insert into YukonSelectionList values (1040,'N','(none)','Bin size selection', 'BinSize','N');
insert into YukonSelectionList values (1041,'N','(none)','Horse power selection', 'HorsePower','N');
insert into YukonSelectionList values (1042,'N','(none)','Heat source selection', 'HeatSource','N');
insert into YukonSelectionList values (1043,'N','(none)','Storage type selection', 'StorageType','N');
insert into YukonSelectionList values (1044,'N','(none)','Pump type selection', 'PumpType','N');
insert into YukonSelectionList values (1045,'N','(none)','Standby source selection', 'StandbySource','N');
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection', 'IrrigationType','N');
insert into YukonSelectionList values (1047,'N','(none)','Soil type selection', 'SoilType','N');
insert into YukonSelectionList values (1048,'N','(none)','Device location selection', 'DeviceLocation','N');
insert into YukonSelectionList values (1049,'O','(none)','Opt out period selection','OptOutPeriod','Y');
insert into YukonSelectionList values (1050,'N','(none)','Gateway end device data description','GatewayEndDeviceDataDesc','N');
insert into YukonSelectionList values (1051,'N','(none)','Hardware Inventory search by selection', 'InvSearchBy','N');
insert into YukonSelectionList values (1052,'N','(none)','Hardware Inventory sort by selection', 'InvSortBy','N');
insert into YukonSelectionList values (1053,'N','(none)','Hardware Inventory filter by selection', 'InvFilterBy','N');
insert into YukonSelectionList values (2000,'N','(none)','Customer Selection Base','(none)','N');

/* YukonListEntry */
insert into YukonListEntry values (1001,1001,0,'Program',1001);
insert into YukonListEntry values (1002,1001,0,'Hardware',1002);
insert into YukonListEntry values (1003,1002,0,'Signup',1101);
insert into YukonListEntry values (1004,1002,0,'Activation Pending',1102);
insert into YukonListEntry values (1005,1002,0,'Activation Completed',1103);
insert into YukonListEntry values (1006,1002,0,'Termination',1104);
insert into YukonListEntry values (1007,1002,0,'Temp Opt Out',1105);
insert into YukonListEntry values (1008,1002,0,'Future Activation',1106);
insert into YukonListEntry values (1009,1002,0,'Install',1107);
insert into YukonListEntry values (1010,1002,0,'Configure',1108);
insert into YukonListEntry values (1011,1003,0,'OneWayReceiver',1201);
insert into YukonListEntry values (1012,1004,0,'120/120',0);
insert into YukonListEntry values (1013,1005,0,'LCR-5000',1302);
insert into YukonListEntry values (1014,1005,0,'LCR-4000',1302);
insert into YukonListEntry values (1015,1005,0,'LCR-3000',1302);
insert into YukonListEntry values (1016,1005,0,'LCR-2000',1302);
insert into YukonListEntry values (1017,1005,0,'LCR-1000',1302);
insert into YukonListEntry values (1018,1005,0,'Thermostat',1301);
insert into YukonListEntry values (1019,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1020,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1021,1007,0,'Storage Heat',1403);
insert into YukonListEntry values (1022,1007,0,'Heat Pump',1404);
insert into YukonListEntry values (1023,1007,0,'Dual Fuel',1405);
insert into YukonListEntry values (1024,1008,0,'General',0);
insert into YukonListEntry values (1025,1008,0,'Credit',0);
insert into YukonListEntry values (1026,1009,0,'Service Call',0);
insert into YukonListEntry values (1027,1009,0,'Install',0);
insert into YukonListEntry values (1028,1010,0,'Unscheduled',1501);
insert into YukonListEntry values (1029,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1030,1010,0,'Completed',1503);
insert into YukonListEntry values (1031,1011,0,'Acct #',1601);
insert into YukonListEntry values (1032,1011,0,'Phone #',1602);
insert into YukonListEntry values (1033,1011,0,'Last name',1603);
insert into YukonListEntry values (1034,1011,0,'Serial #',1604);
insert into YukonListEntry values (1035,1006,0,'Available',1701);
insert into YukonListEntry values (1036,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1037,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1038,1012,0,'(Unknown)',1801);
insert into YukonListEntry values (1039,1012,0,'Century',0);
insert into YukonListEntry values (1040,1012,0,'Universal',0);
insert into YukonListEntry values (1041,1013,0,'(Unknown)',1901);
insert into YukonListEntry values (1042,1013,0,'Basement',0);
insert into YukonListEntry values (1043,1013,0,'North Side',0);
insert into YukonListEntry values (1044,1014,0,'Likely',0);
insert into YukonListEntry values (1045,1014,0,'Unlikely',0);
insert into YukonListEntry values (1046,1015,0,'Weekday',2101);
insert into YukonListEntry values (1047,1015,0,'Weekend',2102);
insert into YukonListEntry values (1048,1015,0,'Saturday',2103);
insert into YukonListEntry values (1049,1015,0,'Sunday',2104);
insert into YukonListEntry values (1050,1016,0,'Signup',2201);
insert into YukonListEntry values (1051,1016,0,'Exit',2202);
insert into YukonListEntry values (1052,1017,0,'Selection',2301);
insert into YukonListEntry values (1053,1017,0,'Free Form',2302);
insert into YukonListEntry values (1054,1018,0,'(Default)',2401);
insert into YukonListEntry values (1055,1018,0,'Cool',2402);
insert into YukonListEntry values (1056,1018,0,'Heat',2403);
insert into YukonListEntry values (1057,1018,0,'Off',2404);
insert into YukonListEntry values (1058,1019,0,'(Default)',2501);
insert into YukonListEntry values (1059,1019,0,'Auto',2502);
insert into YukonListEntry values (1060,1019,0,'On',2503);
insert into YukonListEntry values (1061,1020,1,'PROGRAMS',0);
insert into YukonListEntry values (1062,1020,2,'THERMOSTAT CONTROL',0);
insert into YukonListEntry values (1063,1020,3,'SAVINGS',0);
insert into YukonListEntry values (1064,1001,0,'ThermostatManual',1003);
insert into YukonListEntry values (1065,1002,0,'Programming',1109);
insert into YukonListEntry values (1066,1002,0,'Manual Option',1110);
insert into YukonListEntry values (1067,1007,0,'Generator',1406);
insert into YukonListEntry values (1068,1007,0,'Grain Dryer',1407);
insert into YukonListEntry values (1069,1007,0,'Irrigation',1408);
insert into YukonListEntry values (1070,1021,0,'Home',0);
insert into YukonListEntry values (1071,1021,0,'Duplex',0);
insert into YukonListEntry values (1072,1021,0,'Apartment',0);
insert into YukonListEntry values (1073,1022,0,'Wood',0);
insert into YukonListEntry values (1074,1022,0,'Brick',0);
insert into YukonListEntry values (1075,1023,0,'1920',0);
insert into YukonListEntry values (1076,1023,0,'1930',0);
insert into YukonListEntry values (1077,1023,0,'1940',0);
insert into YukonListEntry values (1078,1023,0,'1950',0);
insert into YukonListEntry values (1079,1023,0,'1960',0);
insert into YukonListEntry values (1080,1023,0,'1970',0);
insert into YukonListEntry values (1081,1023,0,'1980',0);
insert into YukonListEntry values (1082,1023,0,'1990',0);
insert into YukonListEntry values (1083,1023,0,'2000',0);
insert into YukonListEntry values (1084,1024,0,'0 - 1000',0);
insert into YukonListEntry values (1085,1024,0,'1000 - 1500',0);
insert into YukonListEntry values (1086,1024,0,'1500 - 2000',0);
insert into YukonListEntry values (1087,1024,0,'2000 - 2500',0);
insert into YukonListEntry values (1088,1024,0,'2500 - 3000',0);
insert into YukonListEntry values (1089,1024,0,'3000 - 3500',0);
insert into YukonListEntry values (1090,1024,0,'3500 - 4000',0);
insert into YukonListEntry values (1091,1024,0,'4000+',0);
insert into YukonListEntry values (1092,1025,0,'1 - 2',0);
insert into YukonListEntry values (1093,1025,0,'3 - 6',0);
insert into YukonListEntry values (1094,1025,0,'6+',0);
insert into YukonListEntry values (1095,1026,0,'Poor',0);
insert into YukonListEntry values (1096,1026,0,'Fair',0);
insert into YukonListEntry values (1097,1026,0,'Good',0);
insert into YukonListEntry values (1098,1026,0,'Excellent',0);
insert into YukonListEntry values (1099,1029,0,'1 - 2',0);
insert into YukonListEntry values (1100,1029,0,'3 - 4',0);
insert into YukonListEntry values (1101,1029,0,'5 - 6',0);
insert into YukonListEntry values (1102,1029,0,'7 - 8',0);
insert into YukonListEntry values (1103,1029,0,'9+',0);
insert into YukonListEntry values (1104,1030,0,'Own',0);
insert into YukonListEntry values (1105,1030,0,'Rent',0);
insert into YukonListEntry values (1106,1049,1,'Tomorrow',2601);
insert into YukonListEntry values (1107,1049,2,'Today',2602);
insert into YukonListEntry values (1108,1049,99,'Repeat Last',2699);
insert into YukonListEntry values (1109,1003,0,'TwoWayRec',1202);
insert into YukonListEntry values (1110,1005,0,'EnergyPro',3100);
insert into YukonListEntry values (1111,1018,0,'Auto',2405);
insert into YukonListEntry values (1112,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1113,1015,0,'Monday',2105);
insert into YukonListEntry values (1114,1015,0,'Tuesday',2106);
insert into YukonListEntry values (1115,1015,0,'Wednesday',2107);
insert into YukonListEntry values (1116,1015,0,'Thursday',2108);
insert into YukonListEntry values (1117,1015,0,'Friday',2109);
insert into YukonListEntry values (1118,1002,0,'Uninstall',1111);
insert into YukonListEntry values (1201,1050,0,'Last Updated Time',3201);
insert into YukonListEntry values (1202,1050,0,'Setpoint',3202);
insert into YukonListEntry values (1203,1050,0,'Fan',3203);
insert into YukonListEntry values (1204,1050,0,'System',3204);
insert into YukonListEntry values (1205,1050,0,'Room,Unit',3205);
insert into YukonListEntry values (1206,1050,0,'Outdoor',3234);
insert into YukonListEntry values (1207,1050,0,'Filter Remaining,Filter Restart',3236);
insert into YukonListEntry values (1208,1050,0,'Lower CoolSetpoint Limit,Upper HeatSetpoint Limit',3237);
insert into YukonListEntry values (1209,1050,0,'Information String',3299);
insert into YukonListEntry values (1210,1050,0,'Cool Runtime,Heat Runtime',3238);
insert into YukonListEntry values (1211,1050,0,'Battery',3239);
insert into YukonListEntry values (1301,1051,0,'Serial #',2701);
insert into YukonListEntry values (1302,1051,0,'Acct #',2702);
insert into YukonListEntry values (1303,1051,0,'Phone #',2703);
insert into YukonListEntry values (1304,1051,0,'Last name',2704);
insert into YukonListEntry values (1305,1051,0,'Order #',2705);
insert into YukonListEntry values (1311,1052,0,'Serial #',2801);
insert into YukonListEntry values (1312,1052,0,'Install date',2802);
insert into YukonListEntry values (1321,1053,0,'Device type',2901);
insert into YukonListEntry values (1322,1053,0,'Service company',2902);
insert into YukonListEntry values (1323,1053,0,'Location',2903);
insert into YukonListEntry values (1324,1053,0,'Configuration',2904);
insert into YukonListEntry values (2000,0,0,'Customer List Entry Base',0);

/* ECToGenericMapping */
insert into ectogenericmapping values (-1, 1001, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1002, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1003, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1004, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1005, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1006, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1007, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1008, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1009, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1010, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1011, 'YukonSelectionList');
insert into ectogenericmapping values (-1, 1012, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1013, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1014, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1015, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1016, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1017, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1018, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1019, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1020, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1021, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1022, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1023, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1024, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1025, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1026, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1027, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1028, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1029, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1030, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1031, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1032, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1033, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1034, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1035, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1036, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1037, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1038, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1039, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1040, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1041, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1042, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1043, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1044, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1045, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1046, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1047, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1048, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1049, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1050, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1051, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1052, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1053, 'YukonSelectionList');

/* CustomerFAQ */
insert into CustomerFAQ values(1,1061,'How long does it take for my program to become effective after adding or changing a program?','Immediately! You can take advantage of energy savings the moment you decide to. Just make your selection on the "Programs - Add/Change" page, click the submit button, and select Yes at the prompt.');
insert into CustomerFAQ values(2,1061,'How do I find out more about my program or other programs?','Go to the "Programs - Add/Change" page and click the Program Details button. You will find all of the information you need here regarding the program, amount of control, and savings.');
insert into CustomerFAQ values(3,1061,'Can I sign up for more than one program?','Certainly! The more programs you enroll in, the more energy savings you will receive.');
insert into CustomerFAQ values(4,1062,'Can I control my thermostat even if I do not know my current settings?','Yes. You may select the temperature change (up or down) in degrees without knowing the current temperature or simply set a new specific temperature. If pre-cooling, you may also select a new specific temperature or select the number of degress to decrease in temperature.');
insert into CustomerFAQ values(5,1062,'What does the Fan setting do?','The fan setting controls the operation of the fan. <br>Auto - the fan runs only as necessary to maintain the current temperature settings. <br>On - the fan runs continuously. <br>Off - the fan does not run.');
insert into CustomerFAQ values(6,1062,'Does the utility company have access to my thermostat?','The utility only has access to your thermostat for control based on the programs you have signed up for. When not being controlled, you have complete control of your thermostat.');
insert into CustomerFAQ values(7,1063,'How much credit do I receive if I opt out while contolling?','You will receive credit for the protion of time you were controlled.');

/* Thermostat tables */
/* LMThermostatSeason */
INSERT INTO LMThermostatSeason VALUES (-1,-1,-1,'01-JUN-00',1);
INSERT INTO LMThermostatSeason VALUES (-2,-1,-2,'15-OCT-00',2);

/* LMThermostatSeasonEntry */
INSERT INTO LMThermostatSeasonEntry VALUES (-24,-1,1046,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-23,-1,1046,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-22,-1,1046,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-21,-1,1046,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-20,-1,1048,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-19,-1,1048,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-18,-1,1048,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-17,-1,1048,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-16,-1,1049,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-15,-1,1049,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-14,-1,1049,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-13,-1,1049,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-12,-2,1046,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-11,-2,1046,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-10,-2,1046,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-9,-2,1046,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-8,-2,1048,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-7,-2,1048,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-6,-2,1048,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-5,-2,1048,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-4,-2,1049,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-3,-2,1049,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-2,-2,1049,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-1,-2,1049,75600,72);

/* LMThermostatManualEvent */
INSERT INTO LMCustomerEventBase VALUES (-1,1064,1066,'01-JAN-70','','');
INSERT INTO LMThermostatManualEvent VALUES (-1,-1,72,'N',1054,1058);
INSERT INTO ECToLMCustomerEventMapping VALUES (-1,-1);
