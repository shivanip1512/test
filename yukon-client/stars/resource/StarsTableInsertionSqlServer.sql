/* All the "dummy" entries */
/* LMProgram: insert an empty line for appliances without a program assigned */
insert into LMProgram values(0, 'Automatic', 'NNNN', 'NNNNNNNN', 0, 0, 0, 0, 0, 0);

/* Route */
INSERT INTO Route VALUES (0,0,'N')

/* EnergyCompany */
INSERT INTO EnergyCompany VALUES (-1,'Default Energy Company',0,0)

/* YukonWebConfiguration */
INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Summer','')
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Winter','')

/* ServiceCompany */
INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)')

/* Substation */
INSERT INTO Substation VALUES (0,'(none)',0)

/* CustomerBase */
INSERT INTO Customer VALUES (0,0,0,'(none)')

/* SiteInformation */
INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0)

/* AccountSite */
INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)')

/* CustomerAccount */
INSERT INTO CustomerAccount VALUES (0,0,'(none)',0,0,'(none)',-1)

/* InventoryBase */
INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0)
INSERT INTO InventoryBase VALUES (-1,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'Default Thermostat',0)

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (-1,0)
INSERT INTO ECToInventoryMapping VALUES (-1,-1)

/* YukonSelectionList */
insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N')
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N')
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N')
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','N')
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','N')
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection', 'DeviceStatus','N')
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N')
insert into YukonSelectionList values (1008,'A','(none)','Call type selection', 'CallType','N')
insert into YukonSelectionList values (1009,'A','(none)','Service type selection', 'ServiceType','N')
insert into YukonSelectionList values (1010,'N','(none)','Service request status', 'ServiceStatus','N')
insert into YukonSelectionList values (1011,'N','(none)','Search by selection', 'SearchBy','N')
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection', 'Manufacturer','N')
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection', 'ApplianceLocation','N')
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection', 'ChanceOfControl','N')
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection', 'TimeOfWeek','N')
insert into YukonSelectionList values (1016,'N','(none)','Question type selection', 'QuestionType','N')
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection', 'AnswerType','N')
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection', 'ThermostatMode','N')
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection', 'ThermostatFanState','N')
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups', 'CustomerFAQGroup','N')
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection', 'ResidenceType','N')
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection', 'ConstructionMaterial','N')
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection', 'DecadeBuilt','N')
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection', 'SquareFeet','N')
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection', 'InsulationDepth','N')
insert into YukonSelectionList values (1026,'N','(none)','General condition selection', 'GeneralCondition','N')
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection', 'CoolingSystem','N')
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection', 'HeatingSystem','N')
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection', 'NumberOfOccupants','N')
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection', 'OwnershipType','N')
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection', 'FuelType','N')
insert into YukonSelectionList values (1032,'N','(none)','Tonnage selection', 'Tonnage','N')
insert into YukonSelectionList values (1033,'N','(none)','AC type selection', 'ACType','N')
insert into YukonSelectionList values (1034,'N','(none)','Number of gallons selection', 'NumberOfGallons','N')
insert into YukonSelectionList values (1035,'N','(none)','Energy source selection', 'EnergySource','N')
insert into YukonSelectionList values (1036,'N','(none)','Switch over type selection', 'SwitchOverType','N')
insert into YukonSelectionList values (1037,'N','(none)','Transfer switch type selection', 'TransferSwitchType','N')
insert into YukonSelectionList values (1038,'N','(none)','Transfer switch manufacturer selection', 'TransferSwitchMfg','N')
insert into YukonSelectionList values (1039,'N','(none)','Dryer type selection', 'DryerType','N')
insert into YukonSelectionList values (1040,'N','(none)','Bin size selection', 'BinSize','N')
insert into YukonSelectionList values (1041,'N','(none)','Horse power selection', 'HorsePower','N')
insert into YukonSelectionList values (1042,'N','(none)','Heat source selection', 'HeatSource','N')
insert into YukonSelectionList values (1043,'N','(none)','Storage type selection', 'StorageType','N')
insert into YukonSelectionList values (1044,'N','(none)','Pump type selection', 'PumpType','N')
insert into YukonSelectionList values (1045,'N','(none)','Standby source selection', 'StandbySource','N')
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection', 'IrrigationType','N')
insert into YukonSelectionList values (1047,'N','(none)','Soil type selection', 'SoilType','N')
insert into YukonSelectionList values (1048,'N','(none)','Device location selection', 'DeviceLocation','N')

/* YukonListEntry */
insert into YukonListEntry values (1001,1001,0,'Program',1001)
insert into YukonListEntry values (1002,1001,0,'Hardware',1002)
insert into YukonListEntry values (1003,1002,0,'Signup',1101)
insert into YukonListEntry values (1004,1002,0,'Activation Pending',1102)
insert into YukonListEntry values (1005,1002,0,'Activation Completed',1103)
insert into YukonListEntry values (1006,1002,0,'Termination',1104)
insert into YukonListEntry values (1007,1002,0,'Temp Opt Out',1105)
insert into YukonListEntry values (1008,1002,0,'Future Activation',1106)
insert into YukonListEntry values (1009,1002,0,'Install',1107)
insert into YukonListEntry values (1010,1002,0,'Configure',1108)
insert into YukonListEntry values (1011,1003,0,'OneWayReceiver',1201)
insert into YukonListEntry values (1012,1004,0,'120/120',0)
insert into YukonListEntry values (1013,1005,0,'LCR-5000',0)
insert into YukonListEntry values (1014,1005,0,'LCR-4000',0)
insert into YukonListEntry values (1015,1005,0,'LCR-3000',0)
insert into YukonListEntry values (1016,1005,0,'LCR-2000',0)
insert into YukonListEntry values (1017,1005,0,'LCR-1000',0)
insert into YukonListEntry values (1018,1005,0,'Thermostat',1301)
insert into YukonListEntry values (1019,1007,0,'Air Conditioner',1401)
insert into YukonListEntry values (1020,1007,0,'Water Heater',1402)
insert into YukonListEntry values (1021,1007,0,'Storage Heat',1403)
insert into YukonListEntry values (1022,1007,0,'Heat Pump',1404)
insert into YukonListEntry values (1023,1007,0,'Dual Fuel',1405)
insert into YukonListEntry values (1024,1008,0,'General',0)
insert into YukonListEntry values (1025,1008,0,'Credit',0)
insert into YukonListEntry values (1026,1009,0,'Service Call',0)
insert into YukonListEntry values (1027,1009,0,'Install',0)
insert into YukonListEntry values (1028,1010,0,'Unscheduled',1501)
insert into YukonListEntry values (1029,1010,0,'Scheduled',1502)
insert into YukonListEntry values (1030,1010,0,'Completed',1503)
insert into YukonListEntry values (1031,1011,0,'Acct #',1601)
insert into YukonListEntry values (1032,1011,0,'Phone #',1602)
insert into YukonListEntry values (1033,1011,0,'Last name',1603)
insert into YukonListEntry values (1034,1011,0,'Serial #',1604)
insert into YukonListEntry values (1035,1006,0,'Available',1701)
insert into YukonListEntry values (1036,1006,0,'Temp Unavail',1702)
insert into YukonListEntry values (1037,1006,0,'Unavailable',1703)
insert into YukonListEntry values (1038,1012,0,'(Unknown)',1801)
insert into YukonListEntry values (1039,1012,0,'Century',0)
insert into YukonListEntry values (1040,1012,0,'Universal',0)
insert into YukonListEntry values (1041,1013,0,'(Unknown)',1901)
insert into YukonListEntry values (1042,1013,0,'Basement',0)
insert into YukonListEntry values (1043,1013,0,'North Side',0)
insert into YukonListEntry values (1044,1014,0,'Likely',0)
insert into YukonListEntry values (1045,1014,0,'Unlikely',0)
insert into YukonListEntry values (1046,1015,0,'Weekday',2101)
insert into YukonListEntry values (1047,1015,0,'Weekend',2102)
insert into YukonListEntry values (1048,1015,0,'Saturday',2103)
insert into YukonListEntry values (1049,1015,0,'Sunday',2104)
insert into YukonListEntry values (1050,1016,0,'Signup',2201)
insert into YukonListEntry values (1051,1016,0,'Exit',2202)
insert into YukonListEntry values (1052,1017,0,'Selection',2301)
insert into YukonListEntry values (1053,1017,0,'Free Form',2302)
insert into YukonListEntry values (1054,1018,0,'(Default)',2401)
insert into YukonListEntry values (1055,1018,0,'Cool',2402)
insert into YukonListEntry values (1056,1018,0,'Heat',2403)
insert into YukonListEntry values (1057,1018,0,'Off',2404)
insert into YukonListEntry values (1058,1019,0,'(Default)',2501)
insert into YukonListEntry values (1059,1019,0,'Auto',2502)
insert into YukonListEntry values (1060,1019,0,'On',2503)
insert into YukonListEntry values (1061,1020,1,'PROGRAMS',0)
insert into YukonListEntry values (1062,1020,2,'THERMOSTAT CONTROL',0)
insert into YukonListEntry values (1063,1020,3,'SAVINGS',0)
insert into YukonListEntry values (1064,1001,0,'ThermostatManual',1003)
insert into YukonListEntry values (1065,1002,0,'Change Schedule',1109)
insert into YukonListEntry values (1066,1002,0,'Manual Option',1110)
insert into YukonListEntry values (1067,1007,0,'Generator',1406)
insert into YukonListEntry values (1068,1007,0,'Grain Dryer',1407)
insert into YukonListEntry values (1069,1007,0,'Irrigation',1408)
insert into YukonListEntry values (1070,1021,0,'Home',0)
insert into YukonListEntry values (1071,1021,0,'Duplex',0)
insert into YukonListEntry values (1072,1021,0,'Apartment',0)
insert into YukonListEntry values (1073,1022,0,'Wood',0)
insert into YukonListEntry values (1074,1022,0,'Brick',0)
insert into YukonListEntry values (1075,1023,0,'1920',0)
insert into YukonListEntry values (1076,1023,0,'1930',0)
insert into YukonListEntry values (1077,1023,0,'1940',0)
insert into YukonListEntry values (1078,1023,0,'1950',0)
insert into YukonListEntry values (1079,1023,0,'1960',0)
insert into YukonListEntry values (1080,1023,0,'1970',0)
insert into YukonListEntry values (1081,1023,0,'1980',0)
insert into YukonListEntry values (1082,1023,0,'1990',0)
insert into YukonListEntry values (1083,1023,0,'2000',0)
insert into YukonListEntry values (1084,1024,0,'&lt; 1000',0)
insert into YukonListEntry values (1085,1024,0,'1000 - 1500',0)
insert into YukonListEntry values (1086,1024,0,'1500 - 2000',0)
insert into YukonListEntry values (1087,1024,0,'2000 - 2500',0)
insert into YukonListEntry values (1088,1024,0,'2500 - 3000',0)
insert into YukonListEntry values (1089,1024,0,'3000 - 3500',0)
insert into YukonListEntry values (1090,1024,0,'3500 - 4000',0)
insert into YukonListEntry values (1091,1024,0,'4000+',0)
insert into YukonListEntry values (1092,1025,0,'1 - 2',0)
insert into YukonListEntry values (1093,1025,0,'3 - 6',0)
insert into YukonListEntry values (1094,1025,0,'6+',0)
insert into YukonListEntry values (1095,1026,0,'Poor',0)
insert into YukonListEntry values (1096,1026,0,'Fair',0)
insert into YukonListEntry values (1097,1026,0,'Good',0)
insert into YukonListEntry values (1098,1026,0,'Excellent',0)
insert into YukonListEntry values (1099,1029,0,'1 - 2',0)
insert into YukonListEntry values (1100,1029,0,'3 - 4',0)
insert into YukonListEntry values (1101,1029,0,'5 - 6',0)
insert into YukonListEntry values (1102,1029,0,'7 - 8',0)
insert into YukonListEntry values (1103,1029,0,'9+',0)
insert into YukonListEntry values (1104,1030,0,'Own',0)
insert into YukonListEntry values (1105,1030,0,'Rent',0)

/* ECToGenericMapping */
insert into ectogenericmapping values (-1, 1001, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1002, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1003, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1004, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1005, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1006, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1007, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1008, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1009, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1010, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1011, 'YukonSelectionList')
insert into ectogenericmapping values (-1, 1012, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1013, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1014, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1015, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1016, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1017, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1018, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1019, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1020, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1021, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1022, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1023, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1024, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1025, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1026, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1027, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1028, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1029, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1030, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1031, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1032, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1033, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1034, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1035, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1036, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1037, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1038, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1039, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1040, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1041, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1042, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1043, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1044, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1045, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1046, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1047, 'YukonSelectionList')
insert into ECToGenericMapping values (-1, 1048, 'YukonSelectionList')

/* Thermostat tables */
/* LMThermostatSeason */
INSERT INTO LMThermostatSeason VALUES (-1,-1,-1,'01-JUN-00',1)
INSERT INTO LMThermostatSeason VALUES (-2,-1,-2,'15-OCT-00',2)

/* LMThermostatSeasonEntry */
INSERT INTO LMThermostatSeasonEntry VALUES (1,-1,1046,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,-1,1046,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (3,-1,1046,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (4,-1,1046,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (5,-1,1048,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (6,-1,1048,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (7,-1,1048,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (8,-1,1048,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (9,-1,1049,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (10,-1,1049,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (11,-1,1049,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (12,-1,1049,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (13,-2,1046,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (14,-2,1046,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (15,-2,1046,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (16,-2,1046,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (17,-2,1048,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (18,-2,1048,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (19,-2,1048,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (20,-2,1048,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (21,-2,1049,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (22,-2,1049,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (23,-2,1049,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (24,-2,1049,75600,72)

/* LMThermostatManualEvent */
INSERT INTO LMCustomerEventBase VALUES (-1,1064,1066,'01-JAN-70','','')
INSERT INTO LMThermostatManualEvent VALUES (-1,-1,72,'N',1054,1058)
INSERT INTO ECToLMCustomerEventMapping VALUES (-1,-1)



/* YukonWebConfiguration */
insert into YukonWebConfiguration values (1,'','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','XYZ Utility','')
insert into YukonWebConfiguration values(
2, 'AC.gif', '<b>CYCLE AC<br> Light, Medium</b> - When controlled, your air conditioning compressor will be interrupted for 10 minutes out of every half hour if you sign up for the light program and interrupted for 15 minutes out of every half hour if you sign up for the medium program.', 'Cycle AC', '')
insert into YukonWebConfiguration values(
3, 'WaterHeater.gif', '<b>WATER HEATER<br>4 Hour, 8 Hour</b> - When controlled, power to your water heater''s heating elements is turned off for up to 4 hours or 8 hours depending on the program you choose. The hot water in the tank will still be available for you to use.<br><br>  <b>ETS</b> - Your Electric Thermal Storage water heater''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.', 'Water Heater', '')
insert into YukonWebConfiguration values(
4, 'DualFuel.gif', '<b>DUAL FUEL <br> Limited 4 Hour, Unlimited</b> - When controlled, electric power to your home''s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. Control is limited to four hours consecutively when signed up for the limited program. While usually limited to a few hours, control could be for an extended period if signed up for the unlimited program.', 'Dual Fuel Space Heater', '')
insert into YukonWebConfiguration values(
5, 'Electric.gif', '<b>ETS</b><br>Your Electric Thermal Storage heating system''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.', 'ETS', '')
insert into YukonWebConfiguration values(
6, 'Pool.gif', '<b>POOL PUMP</b><br>When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.', 'Pool Pump', '')
insert into YukonWebConfiguration values(
7, 'HotTub.gif', '<b>HOT TUB</b><br>When controlled, power to your hot tub''s water heating elements are interrupted. Interruptions usually last for four hours or less.', 'Hot Tub', '')
insert into YukonWebConfiguration values(8, 'AC.gif,ACSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'When controlled, your air conditioning compressor will be interrupted for 15 minutes out of every half hour. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'Medium', '')
insert into YukonWebConfiguration values(9, 'AC.gif,ACSm.gif,$$Sm.gif,ThirdSm.gif,Tree2Sm.gif', 'When controlled, your air conditioning compressor will be interrupted for ten minutes out of every half hour. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'Light', '')
insert into YukonWebConfiguration values(10, 'WaterHeater.gif,WaterSm.gif,$$Sm.gif,ThirdSm.gif,Tree2Sm.gif', 'When controlled, power to your water heater''s heating elements is turned off for up to eight hours. The hot water in the tank will still be available for you use.', '8 Hour', '')
insert into YukonWebConfiguration values(11, 'WaterHeater.gif,WaterSm.gif,$$Sm.gif,SixthSm.gif,Tree2Sm.gif', 'When controlled, power to your water heater''s heating elements is turned off for up to four hours. The hot water in the tank will still be available for you use.', '4 Hour', '')
insert into YukonWebConfiguration values(12, 'WaterHeater.gif,WaterSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'Your Electric Thermal Storage water heater''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.', 'ETS', '')
insert into YukonWebConfiguration values(13, 'DualFuel.gif,DualFuelSm.gif,$$Sm.gif,SixthSm.gif,Tree2Sm.gif', 'When controlled, electric power to your home’s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. Control is limited to four hours consecutively. ', 'Limited 4 Hour', '')
insert into YukonWebConfiguration values(14, 'DualFuel.gif,DualFuelSm.gif,$$$Sm.gif,ThirdSm.gif,Tree3Sm.gif', 'When controlled, electric power to your home’s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. While usually limited to a few hours, control could be for an extended period. ', 'Unlimited', '')
insert into YukonWebConfiguration values(15, 'Electric.gif,ElectricSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'Your Electric Thermal Storage heating system’s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.', 'ETS', '')
insert into YukonWebConfiguration values(16, 'Pool.gif,PoolPumpSm.gif,$Sm.gif,SixthSm.gif,Tree1Sm.gif', 'When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.', '', '')
insert into YukonWebConfiguration values(17, 'HotTub.gif,HotTubSm.gif,$Sm.gif,SixthSm.gif,Tree1Sm.gif', '', '', '')
insert into YukonWebConfiguration values(
18, 'Setback.gif', '<b>SETBACK <br>2&deg, 4&deg</b> - When controlled, your air conditioning compressor will be set back 2 degrees or 4 degrees depending on the program you sign up for. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'SetBack', '')
insert into YukonWebConfiguration values(19, 'Setback.gif,$Sm.gif,QuarterSm.gif,Tree1Sm.gif', 'When controlled, your air conditioning compressor will be set back two degrees. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', '2&deg', '')
insert into YukonWebConfiguration values(20, 'Setback.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'When controlled, your air conditioning compressor will be set back four degrees. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', '4&deg', '')
insert into YukonWebConfiguration values(21,'Irrigation.gif','<b>IRRIGATION</b><br>Irrigation control.','Irrigation','')
insert into YukonWebConfiguration values(22,'Irrigation.gif,IrrigationSm.gif','Irrigation control.','Irrigation','')



/* energy company address */
insert into Address values (100,'9000 6th Street','','Minneapolis','MN','55441','(none)')

/* energy company contact */
insert into Contact values (100,'(none)','(none)',-1,100)
insert into ContactNotification values (101,100,2,'N','1-800-555-5432')
insert into ContactNotification values (102,100,4,'N','1-800-555-5431')
insert into ContactNotification values (103,100,1,'N','info@xyzutility.com')

/* EnergyCompany */
insert into EnergyCompany values (1,'Test Company',3,1,100)

/* Create operator login */
insert into YukonUser values (101,'op1','op1',0,'1970-01-01','Enabled')
insert into YukonUserGroup values (101,-210)
insert into energycompanyoperatorloginlist values(1,101)

/* Create residential customer login */
insert into YukonUser values (102,'thermostat','test',0,'1970-01-01','Enabled')
insert into YukonUserGroup values (102,-211)

/* Create a "super" web operator for some testing functions */
insert into YukonUser values (100,'super','super',0,'1970-01-01','Enabled')
insert into YukonUserGroup values (100,-210)
insert into YukonRole values (-1000,'WEB_OPERATOR_SUPER','WebClient','(none)','(none)')
insert into YukonUserRole values (100,-1000,'(none)')
insert into YukonRole values (-1001,'NEW_ACCOUNT_WIZARD','WebClient','(none)','(none)')
insert into YukonUserRole values (100,-1001,'(none)')
insert into energycompanyoperatorloginlist values(1,100)


/* service company contacts */
insert into Contact values (106,'Kevin','Johnson',-1,0)
insert into Contact values (107,'Rudy','Johnson',-1,0)

/* service company address */
insert into Address values (106,'3121 Rhode Island Ave',' ','Golden Valley','MN','55427','Hennepin')
insert into Address values (107,'3121 25th Ave N',' ','Golden Valley','MN','55427','Hennepin')

/* service company */
insert into ServiceCompany values (1,'Johnson Electric',106,'7635551000','7635551001',106,'Electric')
insert into ServiceCompany values (2,'AAA Electric',107,'7635558888','7635558889',107,'Electric')

/* substation */
insert into Substation values (1,'North Sub',3)
insert into Substation values (2,'South Sub',3)

/* customer contact */
insert into Contact values (101,'Robert','Livingston',-1,0)
insert into ContactNotification values (101,101,5,'N','763-595-7777')	/* home hone */

/* customer address */
insert into Address values (101,'3801 Golden Valley Rd','Suite 300','Golden Valley','MN','55427','Hennepin')

/* customer table */
insert into Customer values (101,101,1,'CST')	/* residential customer */

/* site information */
insert into SiteInformation values (1,'1','B109','15 KVA','120',1)

/* Account site */
insert into AccountSite values (1,1,'NE0095',101,'')

/* customer account */
insert into CustomerAccount values (1,1,'12345',101,101,'Fenced in yard, call',102)	/* login id set here! */

/* inventory base */
insert into InventoryBase values (1,1,1,1011,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',1012,'',0)
insert into InventoryBase values (2,1,1,1011,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',1012,'Thermostat',0)

/* lmhardware base */
insert into LMHardwareBase values (1,'550000034',1013)	/* LCR-5000 */
insert into LMHardwareBase values (2,'600000066',1018)	/* thermostat */

/* appliance category */
insert into ApplianceCategory values (1, 'Air Conditioner', 1019, 2)
insert into ApplianceCategory values (2, 'Water Heater', 1020, 3)
insert into ApplianceCategory values (3, 'Dual Fuel', 1023, 4)
insert into ApplianceCategory values (4, 'ETS', 1021, 5)
insert into ApplianceCategory values (5, 'Pool Pump', 1022, 6)
insert into ApplianceCategory values (6, 'Hot Tub', 1020, 7)
insert into ApplianceCategory values (7, 'Irrigation', 1069, 21)

/* appliance base */
insert into ApplianceBase values (1,1,1,5,'2000',1039,1043,0,0,'')
insert into ApplianceBase values (2,1,2,8,'2000',1040,1042,0,0,'')

/* appliance air conditioner */
insert into ApplianceAirConditioner values (1,0,0)

/* appliance water heater */
insert into ApplianceWaterHeater values (2,0,0,0)

/* lm hardware configuration */
insert into LMHardwareConfiguration values (1,1,4)
insert into LMHardwareConfiguration values (1,2,4)


/* LMProgramWebPublishing */
insert into LMProgramWebPublishing values(1,5,8,0)
insert into LMProgramWebPublishing values(1,6,9,0)
insert into LMProgramWebPublishing values(2,7,10,0)
insert into LMProgramWebPublishing values(2,8,11,0)
insert into LMProgramWebPublishing values(2,9,12,0)
insert into LMProgramWebPublishing values(7,10,22,1045)		/* Chance of control unlikely */


/* InterviewQuestion */
insert into InterviewQuestion values(1,1051,'What is your reason for overriding the current event?','N',1,1053,0)	/* free form exit interview question */
insert into InterviewQuestion values(2,1051,'Would you like to be contacted by the program manager for any reason concerning the pilot?','N',2,1053,0)


/* CustomerFAQ */
insert into CustomerFAQ values(1,1061,'How long does it take for my program to become effective after adding or changing a program?','Immediately! You can take advantage of energy savings the moment you decide to. Just make your selection on the "Programs - Add/Change" page, click the submit button, and select Yes at the prompt.')
insert into CustomerFAQ values(2,1061,'How do I find out more about my program or other programs?','Go to the "Programs - Add/Change" page and click the Program Details button. You will find all of the information you need here regarding the program, amount of control, and savings.')
insert into CustomerFAQ values(3,1061,'Can I sign up for more than one program?','Certainly! The more programs you enroll in, the more energy savings you will receive.')
insert into CustomerFAQ values(4,1062,'Can I control my thermostat even if I do not know my current settings?','Yes. You may select the temperature change (up or down) in degrees without knowing the current temperature or simply set a new specific temperature. If pre-cooling, you may also select a new specific temperature or select the number of degress to decrease in temperature.')
insert into CustomerFAQ values(5,1062,'What does the Fan setting do?','The fan setting controls the operation of the fan. <br>Auto - the fan runs only as necessary to maintain the current temperature settings. <br>On - the fan runs continuously. <br>Off - the fan does not run.')
insert into CustomerFAQ values(6,1062,'Does the utility company have access to my thermostat?','The utility only has access to your thermostat for control based on the programs you have signed up for. When not being controlled, you have complete control of your thermostat.')
insert into CustomerFAQ values(7,1063,'How much credit do I receive if I opt out while contolling?','You will receive credit for the protion of time you were controlled.')



/* All the mapping tables */
/* ECToAccountMapping */
INSERT INTO ECToAccountMapping VALUES (1, 1)

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (1, 1)
INSERT INTO ECToInventoryMapping VALUES (1, 2)

/* ECToGenericMapping */
insert into ECToGenericMapping values (1, 5, 'LMPrograms')
insert into ECToGenericMapping values (1, 6, 'LMPrograms')
insert into ECToGenericMapping values (1, 7, 'LMPrograms')
insert into ECToGenericMapping values (1, 8, 'LMPrograms')
insert into ECToGenericMapping values (1, 9, 'LMPrograms')
insert into ECToGenericMapping values (1, 10, 'LMPrograms')
insert into ECToGenericMapping values (1, 1, 'Substation')
insert into ECToGenericMapping values (1, 2, 'Substation')
INSERT INTO ECToGenericMapping VALUES (1, 1, 'ServiceCompany')
INSERT INTO ECToGenericMapping VALUES (1, 2, 'ServiceCompany')
INSERT INTO ECToGenericMapping VALUES (1, 1, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 2, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 3, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 4, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 5, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 6, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 7, 'ApplianceCategory')
INSERT INTO ECToGenericMapping VALUES (1, 1, 'InterviewQuestion')
INSERT INTO ECToGenericMapping VALUES (1, 2, 'InterviewQuestion')
INSERT INTO ECToGenericMapping VALUES (1, 1, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 2, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 3, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 4, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 5, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 6, 'CustomerFAQ')
INSERT INTO ECToGenericMapping VALUES (1, 7, 'CustomerFAQ')


