/* All the "dummy" entries */
/* LMProgram: insert an empty line for appliances without a program assigned */
insert into LMProgram values(0, 'Automatic', 'NNNN', 'NNNNNNNN', 0, 0, 0, 0, 0, 0, 0, 0);

/* Route */
INSERT INTO Route VALUES (0,0,'N');

/* EnergyCompany */
INSERT INTO YukonUser VALUES (-100,'DefaultCTI','$cti_default',0,'01-JAN-00','Enabled');
INSERT INTO EnergyCompany VALUES (-1,'Default Energy Company',0,-100);
INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);
insert into YukonUserRole values (-1000, -100, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-1002, -100, -108, -10802, '(none)');
insert into YukonUserRole values (-1003, -100, -108, -10803, '(none)');
insert into YukonUserRole values (-1004, -100, -108, -10804, '(none)');
insert into YukonUserRole values (-1005, -100, -108, -10805, '(none)');
insert into YukonUserRole values (-1006, -100, -108, -10806, '(none)');
insert into YukonUserRole values (-1010, -100, -200, -20000, '(none)');
insert into YukonUserRole values (-1011, -100, -200, -20001, 'true');
insert into YukonUserRole values (-1012, -100, -200, -20002, '(none)');

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
INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)');
INSERT INTO InventoryBase VALUES (-1,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'Default Thermostat',0,'(none)');
INSERT INTO LMHardwareBase VALUES (-1,'0',0);

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (-1,-1);

/* YukonSelectionList */
insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection','DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection','CallType','Y');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection','ServiceType','Y');
insert into YukonSelectionList values (1010,'N','(none)','Service request status','ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection','SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection','Manufacturer','Y');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection','ApplianceLocation','Y');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection','ChanceOfControl','Y');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection','TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection','QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection','AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection','ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection','ThermostatFanState','N');
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups','CustomerFAQGroup','N');
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection','ResidenceType','Y');
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection','ConstructionMaterial','Y');
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection','DecadeBuilt','Y');
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection','SquareFeet','Y');
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection','InsulationDepth','Y');
insert into YukonSelectionList values (1026,'N','(none)','General condition selection','GeneralCondition','Y');
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection','CoolingSystem','Y');
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection','HeatingSystem','Y');
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection','NumberOfOccupants','Y');
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection','OwnershipType','Y');
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection','FuelType','Y');
insert into YukonSelectionList values (1032,'N','(none)','AC Tonnage selection','ACTonnage','Y');
insert into YukonSelectionList values (1033,'N','(none)','AC type selection','ACType','Y');
insert into YukonSelectionList values (1034,'N','(none)','Water heater number of gallons selection','WHNumberOfGallons','Y');
insert into YukonSelectionList values (1035,'N','(none)','Water heater energy source selection','WHEnergySource','Y');
insert into YukonSelectionList values (1036,'N','(none)','Dual fuel switch over type selection','DFSwitchOverType','Y');
insert into YukonSelectionList values (1037,'N','(none)','Dual fuel secondary source selection','DFSecondarySource','Y');
insert into YukonSelectionList values (1038,'N','(none)','Grain dryer type selection','GrainDryerType','Y');
insert into YukonSelectionList values (1039,'N','(none)','Grain dryer bin size selection','GDBinSize','Y');
insert into YukonSelectionList values (1040,'N','(none)','Grain dryer blower energy source selection','GDEnergySource','Y');
insert into YukonSelectionList values (1041,'N','(none)','Grain dryer blower horse power selection','GDHorsePower','Y');
insert into YukonSelectionList values (1042,'N','(none)','Grain dryer blower heat source selection','GDHeatSource','Y');
insert into YukonSelectionList values (1043,'N','(none)','Storage heat type selection','StorageHeatType','Y');
insert into YukonSelectionList values (1044,'N','(none)','Heat pump type selection','HeatPumpType','Y');
insert into YukonSelectionList values (1045,'N','(none)','Heat pump standby source selection','HPStandbySource','Y');
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection','IrrigationType','Y');
insert into YukonSelectionList values (1047,'N','(none)','Irrigation soil type selection','IRRSoilType','Y');
insert into YukonSelectionList values (1048,'N','(none)','Device location selection','DeviceLocation','N');
insert into YukonSelectionList values (1049,'O','(none)','Opt out period selection','OptOutPeriod','Y');
insert into YukonSelectionList values (1050,'N','(none)','Gateway end device data description','GatewayEndDeviceDataDesc','N');
insert into YukonSelectionList values (1051,'N','(none)','Hardware Inventory search by selection','InvSearchBy','N');
insert into YukonSelectionList values (1052,'N','(none)','Hardware Inventory sort by selection','InvSortBy','N');
insert into YukonSelectionList values (1053,'N','(none)','Hardware Inventory filter by selection','InvFilterBy','N');
insert into YukonSelectionList values (1054,'N','(none)','Service order search by selection','SOSearchBy','N');
insert into YukonSelectionList values (1055,'N','(none)','Service order sort by selection','SOSortBy','N');
insert into YukonSelectionList values (1056,'N','(none)','Service order filter by selection','SOFilterBy','N');
insert into YukonSelectionList values (1057,'N','(none)','Generator transfer switch type selection','GENTransferSwitchType','Y');
insert into YukonSelectionList values (1058,'N','(none)','Generator transfer switch manufacturer selection','GENTransferSwitchMfg','Y');
insert into YukonSelectionList values (1059,'N','(none)','Irrigation horse power selection','IRRHorsePower','Y');
insert into YukonSelectionList values (1060,'N','(none)','Irrigation energy source selection','IRREnergySource','Y');
insert into YukonSelectionList values (1061,'N','(none)','Irrigation meter location selection','IRRMeterLocation','Y');
insert into YukonSelectionList values (1062,'N','(none)','Irrigation meter voltage selection','IRRMeterVoltage','Y');
insert into YukonSelectionList values (1063,'N','(none)','Water heater location selection','WHLocation','Y');
insert into YukonSelectionList values (1064,'N','(none)','Heat pump size selection','HeatPumpSize','Y');
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
insert into YukonListEntry values (1028,1010,0,'Pending',1501);
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
insert into YukonListEntry values (1106,1049,1,'Tomorrow',2601);
insert into YukonListEntry values (1107,1049,2,'Today',2602);
insert into YukonListEntry values (1108,1049,99,'Repeat Last',2699);
insert into YukonListEntry values (1109,1003,0,'TwoWayReceiver',1202);
insert into YukonListEntry values (1110,1005,0,'EnergyPro',3100);
insert into YukonListEntry values (1111,1018,0,'Auto',2405);
insert into YukonListEntry values (1112,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1113,1015,0,'Monday',2105);
insert into YukonListEntry values (1114,1015,0,'Tuesday',2106);
insert into YukonListEntry values (1115,1015,0,'Wednesday',2107);
insert into YukonListEntry values (1116,1015,0,'Thursday',2108);
insert into YukonListEntry values (1117,1015,0,'Friday',2109);
insert into YukonListEntry values (1118,1002,0,'Uninstall',1111);
insert into YukonListEntry values (1119,1010,0,'Cancelled',1504);
insert into YukonListEntry values (1120,1003,0,'MCT',1203);
insert into YukonListEntry values (1121,1007,0,'(Default)',1400);
insert into YukonListEntry values (1122,1005,0,'MCT',1303);

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
insert into YukonListEntry values (1325,1053,0,'Device status',2905);
insert into YukonListEntry values (1331,1054,0,'Order #',3301);
insert into YukonListEntry values (1332,1054,0,'Acct #',3302);
insert into YukonListEntry values (1333,1054,0,'Phone #',3303);
insert into YukonListEntry values (1334,1054,0,'Last Name',3304);
insert into YukonListEntry values (1335,1054,0,'Serial #',3305);
insert into YukonListEntry values (1341,1055,0,'Order #',3401);
insert into YukonListEntry values (1342,1055,0,'Date/Time',3402);
//insert into YukonListEntry values (1351,1056,0,'Status',3501);
insert into YukonListEntry values (1352,1056,0,'Service Type',3502);
insert into YukonListEntry values (1353,1056,0,'Service Company',3503);

insert into YukonListEntry values (1400,1032,0,'',0);
insert into YukonListEntry values (1401,1032,0,'2',0);
insert into YukonListEntry values (1402,1032,0,'2.5',0);
insert into YukonListEntry values (1403,1032,0,'3',0);
insert into YukonListEntry values (1404,1032,0,'3.5',0);
insert into YukonListEntry values (1405,1032,0,'4',0);
insert into YukonListEntry values (1406,1032,0,'4.5',0);
insert into YukonListEntry values (1407,1032,0,'5',0);
insert into YukonListEntry values (1410,1033,0,'',0);
insert into YukonListEntry values (1411,1033,0,'Central Air',0);
insert into YukonListEntry values (1420,1034,0,'',0);
insert into YukonListEntry values (1421,1034,0,'30',0);
insert into YukonListEntry values (1422,1034,0,'40',0);
insert into YukonListEntry values (1423,1034,0,'52',0);
insert into YukonListEntry values (1424,1034,0,'80',0);
insert into YukonListEntry values (1425,1034,0,'120',0);
insert into YukonListEntry values (1430,1035,0,'',0);
insert into YukonListEntry values (1431,1035,0,'Propane',0);
insert into YukonListEntry values (1432,1035,0,'Electric',0);
insert into YukonListEntry values (1433,1035,0,'Natural Gas',0);
insert into YukonListEntry values (1434,1035,0,'Oil',0);
insert into YukonListEntry values (1440,1036,0,'',0);
insert into YukonListEntry values (1441,1036,0,'Automatic',0);
insert into YukonListEntry values (1442,1036,0,'Manual',0);
insert into YukonListEntry values (1450,1037,0,'',0);
insert into YukonListEntry values (1451,1037,0,'Propane',0);
insert into YukonListEntry values (1452,1037,0,'Electric',0);
insert into YukonListEntry values (1453,1037,0,'Natural Gas',0);
insert into YukonListEntry values (1454,1037,0,'Oil',0);
insert into YukonListEntry values (1460,1038,0,'',0);
insert into YukonListEntry values (1461,1038,0,'Batch Dry',0);
insert into YukonListEntry values (1462,1038,0,'Aerate',0);
insert into YukonListEntry values (1463,1038,0,'Low Temp Air',0);
insert into YukonListEntry values (1470,1039,0,'',0);
insert into YukonListEntry values (1471,1039,0,'0-2500 Bushels',0);
insert into YukonListEntry values (1472,1039,0,'2500-5000 Bushels',0);
insert into YukonListEntry values (1473,1039,0,'5000-7500 Bushels',0);
insert into YukonListEntry values (1474,1039,0,'7500-10000 Bushels',0);
insert into YukonListEntry values (1475,1039,0,'10000-12500 Bushels',0);
insert into YukonListEntry values (1476,1039,0,'12500-15000 Bushels',0);
insert into YukonListEntry values (1477,1039,0,'15000-17500 Bushels',0);
insert into YukonListEntry values (1478,1039,0,'17500-20000 Bushels',0);
insert into YukonListEntry values (1479,1039,0,'20000-25000 Bushels',0);
insert into YukonListEntry values (1480,1039,0,'25000-30000 Bushels',0);
insert into YukonListEntry values (1481,1039,0,'30000+ Bushels',0);
insert into YukonListEntry values (1490,1040,0,'',0);
insert into YukonListEntry values (1491,1040,0,'Electric',0);
insert into YukonListEntry values (1500,1041,0,'',0);
insert into YukonListEntry values (1501,1041,0,'5',0);
insert into YukonListEntry values (1502,1041,0,'10',0);
insert into YukonListEntry values (1503,1041,0,'15',0);
insert into YukonListEntry values (1504,1041,0,'20',0);
insert into YukonListEntry values (1505,1041,0,'25',0);
insert into YukonListEntry values (1506,1041,0,'30',0);
insert into YukonListEntry values (1507,1041,0,'35',0);
insert into YukonListEntry values (1508,1041,0,'40',0);
insert into YukonListEntry values (1509,1041,0,'45',0);
insert into YukonListEntry values (1510,1041,0,'50',0);
insert into YukonListEntry values (1511,1041,0,'60',0);
insert into YukonListEntry values (1512,1041,0,'70',0);
insert into YukonListEntry values (1513,1041,0,'80',0);
insert into YukonListEntry values (1514,1041,0,'100',0);
insert into YukonListEntry values (1515,1041,0,'150',0);
insert into YukonListEntry values (1520,1042,0,'',0);
insert into YukonListEntry values (1521,1042,0,'Propane',0);
insert into YukonListEntry values (1522,1042,0,'Electric',0);
insert into YukonListEntry values (1523,1042,0,'Natural Gas',0);
insert into YukonListEntry values (1530,1043,0,'',0);
insert into YukonListEntry values (1531,1043,0,'Ceramic Brick',0);
insert into YukonListEntry values (1532,1043,0,'Concrete Slab',0);
insert into YukonListEntry values (1533,1043,0,'Water',0);
insert into YukonListEntry values (1534,1043,0,'Phase-Change Comp',0);
insert into YukonListEntry values (1535,1043,0,'Pond',0);
insert into YukonListEntry values (1536,1043,0,'Rock Box',0);
insert into YukonListEntry values (1540,1044,0,'',0);
insert into YukonListEntry values (1541,1044,0,'Air Source',0);
insert into YukonListEntry values (1542,1044,0,'Water Heater Closed',0);
insert into YukonListEntry values (1543,1044,0,'Water Heater Open',0);
insert into YukonListEntry values (1544,1044,0,'Water Heater Direct',0);
insert into YukonListEntry values (1545,1044,0,'Dual-Fuel (Gas)',0);
insert into YukonListEntry values (1550,1045,0,'',0);
insert into YukonListEntry values (1551,1045,0,'Propane',0);
insert into YukonListEntry values (1552,1045,0,'Electric',0);
insert into YukonListEntry values (1553,1045,0,'Natural Gas',0);
insert into YukonListEntry values (1554,1045,0,'Oil',0);
insert into YukonListEntry values (1560,1046,0,'',0);
insert into YukonListEntry values (1561,1046,0,'Pivot',0);
insert into YukonListEntry values (1562,1046,0,'Gated Pipe',0);
insert into YukonListEntry values (1563,1046,0,'Pivot/Power Only',0);
insert into YukonListEntry values (1564,1046,0,'Reuse Pit Only',0);
insert into YukonListEntry values (1565,1046,0,'Ditch W/Syphon Tube',0);
insert into YukonListEntry values (1566,1046,0,'Flood',0);
insert into YukonListEntry values (1570,1047,0,'',0);
insert into YukonListEntry values (1571,1047,0,'Heavy Loam',0);
insert into YukonListEntry values (1572,1047,0,'Loam',0);
insert into YukonListEntry values (1573,1047,0,'Medium',0);
insert into YukonListEntry values (1574,1047,0,'Sandy',0);
insert into YukonListEntry values (1580,1059,0,'',0);
insert into YukonListEntry values (1581,1059,0,'5',0);
insert into YukonListEntry values (1582,1059,0,'10',0);
insert into YukonListEntry values (1583,1059,0,'15',0);
insert into YukonListEntry values (1584,1059,0,'20',0);
insert into YukonListEntry values (1585,1059,0,'25',0);
insert into YukonListEntry values (1586,1059,0,'30',0);
insert into YukonListEntry values (1587,1059,0,'35',0);
insert into YukonListEntry values (1588,1059,0,'40',0);
insert into YukonListEntry values (1589,1059,0,'45',0);
insert into YukonListEntry values (1590,1059,0,'50',0);
insert into YukonListEntry values (1591,1059,0,'60',0);
insert into YukonListEntry values (1592,1059,0,'70',0);
insert into YukonListEntry values (1593,1059,0,'80',0);
insert into YukonListEntry values (1594,1059,0,'100',0);
insert into YukonListEntry values (1595,1059,0,'150',0);
insert into YukonListEntry values (1596,1059,0,'200',0);
insert into YukonListEntry values (1597,1059,0,'250',0);
insert into YukonListEntry values (1598,1059,0,'300',0);
insert into YukonListEntry values (1600,1060,0,'',0);
insert into YukonListEntry values (1601,1060,0,'Propane',0);
insert into YukonListEntry values (1602,1060,0,'Electric',0);
insert into YukonListEntry values (1603,1060,0,'Natural Gas',0);
insert into YukonListEntry values (1604,1060,0,'Oil',0);
insert into YukonListEntry values (1610,1061,0,'',0);
insert into YukonListEntry values (1611,1061,0,'At Road',0);
insert into YukonListEntry values (1612,1061,0,'At Pump',0);
insert into YukonListEntry values (1613,1061,0,'At Pivot',0);
insert into YukonListEntry values (1620,1062,0,'',0);
insert into YukonListEntry values (1621,1062,0,'120 (PT)',0);
insert into YukonListEntry values (1622,1062,0,'240',0);
insert into YukonListEntry values (1623,1062,0,'277/480',0);
insert into YukonListEntry values (1624,1062,0,'480',0);
insert into YukonListEntry values (1630,1063,0,'',0);
insert into YukonListEntry values (1631,1063,0,'Basement',0);
insert into YukonListEntry values (1632,1063,0,'Crawl Space',0);
insert into YukonListEntry values (1633,1063,0,'Main Floor Closet',0);
insert into YukonListEntry values (1634,1063,0,'Second Floor Closet',0);
insert into YukonListEntry values (1635,1063,0,'Under Counter',0);
insert into YukonListEntry values (1636,1063,0,'Attic',0);
insert into YukonListEntry values (1640,1064,0,'',0);
insert into YukonListEntry values (1641,1064,0,'2',0);
insert into YukonListEntry values (1642,1064,0,'2.5',0);
insert into YukonListEntry values (1643,1064,0,'3',0);
insert into YukonListEntry values (1644,1064,0,'3.5',0);
insert into YukonListEntry values (1645,1064,0,'4',0);
insert into YukonListEntry values (1646,1064,0,'4.5',0);
insert into YukonListEntry values (1647,1064,0,'5',0);

insert into YukonListEntry values (1700,1021,0,'',0);
insert into YukonListEntry values (1701,1021,0,'One Story Unfinished Basement',0);
insert into YukonListEntry values (1702,1021,0,'One Story Finished Basement',0);
insert into YukonListEntry values (1703,1021,0,'Two Story',0);
insert into YukonListEntry values (1704,1021,0,'Manufactured Home',0);
insert into YukonListEntry values (1705,1021,0,'Apartment',0);
insert into YukonListEntry values (1706,1021,0,'Duplex',0);
insert into YukonListEntry values (1707,1021,0,'Townhome',0);
insert into YukonListEntry values (1711,1022,0,'Frame',0);
insert into YukonListEntry values (1712,1022,0,'Brick',0);
insert into YukonListEntry values (1720,1023,0,'',0);
insert into YukonListEntry values (1721,1023,0,'pre-1900',0);
insert into YukonListEntry values (1722,1023,0,'1910',0);
insert into YukonListEntry values (1723,1023,0,'1920',0);
insert into YukonListEntry values (1724,1023,0,'1930',0);
insert into YukonListEntry values (1725,1023,0,'1940',0);
insert into YukonListEntry values (1726,1023,0,'1950',0);
insert into YukonListEntry values (1727,1023,0,'1960',0);
insert into YukonListEntry values (1728,1023,0,'1970',0);
insert into YukonListEntry values (1729,1023,0,'1980',0);
insert into YukonListEntry values (1730,1023,0,'1990',0);
insert into YukonListEntry values (1731,1023,0,'2000',0);
insert into YukonListEntry values (1740,1024,0,'',0);
insert into YukonListEntry values (1741,1024,0,'Less Than 1000',0);
insert into YukonListEntry values (1742,1024,0,'1000-1499',0);
insert into YukonListEntry values (1743,1024,0,'1500-1999',0);
insert into YukonListEntry values (1744,1024,0,'2000-2499',0);
insert into YukonListEntry values (1745,1024,0,'2500-2999',0);
insert into YukonListEntry values (1746,1024,0,'3000-3499',0);
insert into YukonListEntry values (1747,1024,0,'3500-3999',0);
insert into YukonListEntry values (1748,1024,0,'4000+',0);
insert into YukonListEntry values (1751,1025,0,'Unknown',0);
insert into YukonListEntry values (1752,1025,0,'Poor (0-3)"',0);
insert into YukonListEntry values (1753,1025,0,'Fair (3-5)"',0);
insert into YukonListEntry values (1754,1025,0,'Average (6-8)"',0);
insert into YukonListEntry values (1755,1025,0,'Good (9-11)"',0);
insert into YukonListEntry values (1756,1025,0,'Excellant (12+)"',0);
insert into YukonListEntry values (1760,1026,0,'',0);
insert into YukonListEntry values (1761,1026,0,'Poor',0);
insert into YukonListEntry values (1762,1026,0,'Fair',0);
insert into YukonListEntry values (1763,1026,0,'Good',0);
insert into YukonListEntry values (1764,1026,0,'Excellent',0);
insert into YukonListEntry values (1770,1027,0,'',0);
insert into YukonListEntry values (1771,1027,0,'None',0);
insert into YukonListEntry values (1772,1027,0,'Central Air',0);
insert into YukonListEntry values (1773,1027,0,'GSHP',0);
insert into YukonListEntry values (1774,1027,0,'ASHP',0);
insert into YukonListEntry values (1775,1027,0,'Window Unit',0);
insert into YukonListEntry values (1776,1027,0,'Gas ASHP',0);
insert into YukonListEntry values (1777,1027,0,'Attic Fan',0);
insert into YukonListEntry values (1780,1028,0,'',0);
insert into YukonListEntry values (1781,1028,0,'Electric Forced Air',0);
insert into YukonListEntry values (1782,1028,0,'GSHP',0);
insert into YukonListEntry values (1783,1028,0,'ASHP',0);
insert into YukonListEntry values (1784,1028,0,'Electric Baseboard',0);
insert into YukonListEntry values (1785,1028,0,'Ceiling Fan',0);
insert into YukonListEntry values (1786,1028,0,'ETS',0);
insert into YukonListEntry values (1787,1028,0,'Gas Forced Air',0);
insert into YukonListEntry values (1788,1028,0,'Gas Wall Unit',0);
insert into YukonListEntry values (1790,1029,0,'',0);
insert into YukonListEntry values (1791,1029,0,'1 - 2',0);
insert into YukonListEntry values (1792,1029,0,'3 - 4',0);
insert into YukonListEntry values (1793,1029,0,'5 - 6',0);
insert into YukonListEntry values (1794,1029,0,'7 - 8',0);
insert into YukonListEntry values (1795,1029,0,'9+',0);
insert into YukonListEntry values (1800,1030,0,'',0);
insert into YukonListEntry values (1801,1030,0,'Own',0);
insert into YukonListEntry values (1802,1030,0,'Rent',0);
insert into YukonListEntry values (1810,1031,0,'',0);
insert into YukonListEntry values (1811,1031,0,'Propane',0);
insert into YukonListEntry values (1812,1031,0,'Electric',0);
insert into YukonListEntry values (1813,1031,0,'Natural Gas',0);
insert into YukonListEntry values (1814,1031,0,'Oil',0);

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
insert into ECToGenericMapping values (-1, 1054, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1055, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1056, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1057, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1058, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1059, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1060, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1061, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1062, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1063, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1064, 'YukonSelectionList');

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
z`