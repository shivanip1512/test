/* All the "dummy" entries */
/* LMProgram: insert an empty line for appliances without a program assigned */
insert into LMProgram values(0, 'Automatic', 'NNNN', 'NNNNNNNN', 0, 0, 0, 0, 0, 0);

/* Route */
INSERT INTO Route VALUES (0,0,'N')

/* EnergyCompany */
INSERT INTO EnergyCompany VALUES (-1,'Default Energy Company',0)

/* CustomerSelectionList */
INSERT INTO CustomerSelectionList VALUES (0,'N','(none)','(none)','(none)','N')

/* CustomerListEntry */
INSERT INTO CustomerListEntry VALUES (0,0,0,'(none)','(none)')

/* CustomerWebConfiguration */
INSERT INTO CustomerWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Summer','')
INSERT INTO CustomerWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Winter','')

/* CustomerAddress */
INSERT INTO CustomerAddress VALUES (-1,'(none)','(none)','(none)','MN','(none)')

/* ServiceCompany */
INSERT INTO ServiceCompany VALUES (0,'(none)',-1,'(none)','(none)',-1,'(none)')

/* Substation */
INSERT INTO Substation VALUES (0,'(none)',0)

/* CustomerBase */
INSERT INTO CustomerBase VALUES (0,-1,0,'(none)',0)

/* SiteInformation */
INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0)

/* AccountSite */
INSERT INTO AccountSite VALUES (0,0,'(none)',-1,'(none)')

/* CustomerAccount */
INSERT INTO CustomerAccount VALUES (0,0,'(none)',0,-1,'(none)')

/* InventoryBase */
INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0)

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (-1,0)



/* EnergyCompany */
insert into EnergyCompany values (1, 'Test Company', 3)

/* OperatorLogin */
insert into operatorlogin values(1, 'op1', 'op1', 'RDMT,LC,CURT', 0, '27-AUG-02', 'Normal')
insert into energycompanyoperatorloginlist values(1,1)
insert into operatorserialgroup values(1, 4)

/* Login with Yukon user, group, roles */
/* YukonGroup */
insert into YukonGroup values (-2,'Default Web Operator')
insert into YukonGroup values (-3,'Default Web User')

/* YukonGroupRole */
insert into YukonGroupRole values (-2,-101,'')
insert into YukonGroupRole values (-3,-100,'')

/* YukonUser */
insert into YukonUser values (-2,'op1','op1',0,'1970-01-01','Enabled')

/* YukonUserGroup */
insert into YukonUserGroup values (-2,-2)

/* CustomerSelectionList */
insert into customerselectionlist values (1,'A','','Not visible, list defines the event ids','LMCustomerEvent','N')
insert into customerselectionlist values (2,'A','','Not visible, defines possible event actions','LMCustomerAction','N')
insert into customerselectionlist values (3,'A','','Not visible, defines customer types','CustomerType','N')
insert into customerselectionlist values (4,'A','','Not visible, defines inventory device category','InventoryCategory','N')
insert into customerselectionlist values (5,'A','','Device voltage selection','DeviceVoltage','N')
insert into customerselectionlist values (6,'A','','Device type selection','DeviceType','N')
insert into customerselectionlist values (7,'A','','Appliance category','ApplianceCategory','N')
insert into CustomerSelectionList values (8,'A','','Call type selection', 'CallType','N')
insert into CustomerSelectionList values (9,'A','','Service type selection', 'ServiceType','N')
insert into CustomerSelectionList values (10,'N','','Service request status', 'ServiceStatus','N')
insert into CustomerSelectionList values (11,'N','','Search by selection', 'SearchBy','N')
insert into CustomerSelectionList values (12,'N','','hardware status selection', 'DeviceStatus','N')
insert into CustomerSelectionList values (13,'A','','Appliance manufacturer selection', 'Manufacturer','N')
insert into CustomerSelectionList values (14,'A','','Appliance location selection', 'ApplianceLocation','N')
insert into CustomerSelectionList values (15,'N','','Chance of control selection', 'ChanceOfControl','N')
insert into CustomerSelectionList values (16,'N','','Thermostat settings time of week selection', 'TimeOfWeek','N')
insert into CustomerSelectionList values (17,'N','','Question type selection', 'QuestionType','N')
insert into CustomerSelectionList values (18,'N','','Answer type selection', 'AnswerType','N')
insert into CustomerSelectionList values (19,'N','','Thermostat mode selection', 'ThermostatMode','N')
insert into CustomerSelectionList values (20,'N','','Thermostat fan state selection', 'ThermostatFanState','N')


/* CustomerListEntry */
insert into customerlistentry values (1,1,0,'Program','LMPROGRAMEVENT')
insert into customerlistentry values (2,1,0,'Hardware','LMHARDWAREEVENT')
insert into customerlistentry values (3,2,0,'Signup','ACT_SIGNUP')
insert into customerlistentry values (4,2,0,'Activation Pending','ACT_PENDING')
insert into customerlistentry values (5,2,0,'Activation Completed','ACT_COMPLETED')
insert into customerlistentry values (6,2,0,'Termination','ACT_TERMINATION')
insert into customerlistentry values (7,2,0,'Temp Opt Out','ACT_TEMPTERMINATION')
insert into customerlistentry values (8,2,0,'Future Activation','ACT_FUTUREACTIVATION')
insert into customerlistentry values (9,3,0,'Residential','CUSTTYPE_RES')
insert into customerlistentry values (10,3,0,'Commercial','CUSTTYPE_COMM')
insert into customerlistentry values (11,4,0,'OneWayReceiver','INVCAT_ONEWAYREC')
insert into customerlistentry values (12,5,0,'120/120','')
insert into customerlistentry values (13,6,0,'LCR-5000','')
insert into customerlistentry values (14,7,0,'Air Conditioner','APPLCAT_AC')
insert into customerlistentry values (15,7,0,'Water Heater','APPLCAT_WH')
insert into CustomerListEntry values (16,7,0,'Heating System', 'APPCAT_HS')
insert into CustomerListEntry values (17,7,0,'Pool Pump', 'APPCAT_PP')
insert into CustomerListEntry values (18,7,0,'Hot Tub', 'APPCAT_HT')
insert into CustomerListEntry values (19,8,0,'General', '')
insert into CustomerListEntry values (20,8,0,'Credit', '')
insert into CustomerListEntry values (21,9,0,'Service Call', '')
insert into CustomerListEntry values (22,9,0,'Install', '')
insert into CustomerListEntry values (23,10,0,'To be scheduled', 'SERVSTAT_TOBESCHED')
insert into CustomerListEntry values (24,10,0,'Scheduled', 'SERVSTAT_SCHEDULED')
insert into CustomerListEntry values (25,10,0,'Completed', 'SERVSTAT_COMPLETED')
insert into CustomerListEntry values (26,11,0,'Acct #', 'SEARCHBY_ACCTNO')
insert into CustomerListEntry values (27,11,0,'Phone #', 'SEARCHBY_PHONENO')
insert into CustomerListEntry values (28,11,0,'Last name', 'SEARCHBY_LASTNAME')
insert into CustomerListEntry values (29,11,0,'Serial #', 'SEARCHBY_SERIALNO')
INSERT INTO CustomerListEntry VALUES (31,2,0,'Install', 'ACT_INSTALL')
INSERT INTO CustomerListEntry VALUES (32,2,0,'Configure', 'ACT_CONFIG')
INSERT INTO CustomerListEntry VALUES (33,2,0,'Repair', 'ACT_REPAIR')
INSERT INTO CustomerListEntry VALUES (34,6,0,'LCR-4000', '')
INSERT INTO CustomerListEntry VALUES (35,6,0,'LCR-3000', '')
INSERT INTO CustomerListEntry VALUES (36,6,0,'LCR-2000', '')
INSERT INTO CustomerListEntry VALUES (37,6,0,'LCR-1000', '')
INSERT INTO CustomerListEntry VALUES (38,12,0,'Available', 'DEVSTAT_AVAIL')
INSERT INTO CustomerListEntry VALUES (39,12,0,'Temp Unavail', 'DEVSTAT_TEMPUNAVAIL')
INSERT INTO CustomerListEntry VALUES (40,12,0,'Unavailable', 'DEVSTAT_UNAVAIL')
INSERT INTO CustomerListEntry VALUES (41,13,0,'Century', '')
INSERT INTO CustomerListEntry VALUES (42,14,0,'Basement', '')
INSERT INTO CustomerListEntry VALUES (43,14,0,'North Side', '')
INSERT INTO CustomerListEntry VALUES (44,15,0,'Likely', '')
INSERT INTO CustomerListEntry VALUES (45,15,0,'Unlikely', '')
INSERT INTO CustomerListEntry VALUES (46,16,0,'Weekday', 'TIMEOFWEEK_WEEKDAY')
INSERT INTO CustomerListEntry VALUES (47,16,0,'Weekend', 'TIMEOFWEEK_WEEKEND')
INSERT INTO CustomerListEntry VALUES (48,16,0,'Saturday', 'TIMEOFWEEK_SATURDAY')
INSERT INTO CustomerListEntry VALUES (49,16,0,'Sunday', 'TIMEOFWEEK_SUNDAY')
INSERT INTO CustomerListEntry VALUES (50,17,0,'Signup','QUESTIONTYPE_SIGNUP')
INSERT INTO CustomerListEntry VALUES (51,17,0,'Exit','QUESTIONTYPE_EXIT')
INSERT INTO CustomerListEntry VALUES (52,18,0,'Selection','ANSWERTYPE_SELECTION')
INSERT INTO CustomerListEntry VALUES (53,18,0,'Free Form','ANSWERTYPE_FREEFORM')
INSERT INTO CustomerListEntry values (54,6,0,'Thermostat','DEVTYPE_THERMOSTAT')
INSERT INTO CustomerListEntry values (55,19,0,'Cool','THERMMODE_COOL')
INSERT INTO CustomerListEntry values (56,19,0,'Heat','THERMMODE_HEAT')
INSERT INTO CustomerListEntry values (57,19,0,'Off','THERMMODE_OFF')
INSERT INTO CustomerListEntry values (58,20,0,'Auto','FANSTATE_AUTO')
INSERT INTO CustomerListEntry values (59,20,0,'On','FANSTATE_ON')



/* CustomerWebConfiguration */
insert into customerwebconfiguration values (1,'','','','')
insert into CustomerWebConfiguration values(
2, 'AC.gif', '<b>CYCLE AC<br> Light, Medium</b> - When controlled, your air conditioning compressor will be interrupted for 10 minutes out of every half hour if you sign up for the light program and interrupted for 15 minutes out of every half hour if you sign up for the medium program.', 'Cycle AC', '')
insert into CustomerWebConfiguration values(
3, 'WaterHeater.gif', '<b>WATER HEATER<br>4Hr, 8Hr</b> - When controlled, power to your water heater''s heating elements is turned off for up to 4 hours or 8 hours depending on the program you choose. The hot water in the tank will still be available for you to use.<br><br>  <b>ETS</b> - Your Electric Thermal Storage water heater''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.', 'Water Heater', '')
insert into CustomerWebConfiguration values(
4, 'DualFuel.gif', '<b>DUAL FUEL <br> Limited 4hr, Unlimited</b> - When controlled, electric power to your home''s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. Control is limited to four hours consecutively when signed up for the limited program. While usually limited to a few hours, control could be for an extended period if signed up for the unlimited program.', 'Dual Fuel Space Heater', '')
insert into CustomerWebConfiguration values(
5, 'Electric.gif', '<b>ETS</b><br>Your Electric Thermal Storage heating system''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.', 'ETS', '')
insert into CustomerWebConfiguration values(
6, 'Pool.gif', '<b>POOL PUMP</b><br>When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.', 'Pool Pump', '')
insert into CustomerWebConfiguration values(
7, 'HotTub.gif', '<b>HOT TUB</b><br>When controlled, power to your hot tub''s water heating elements are interrupted. Interruptions usually last for four hours or less.', 'Hot Tub', '')
insert into CustomerWebConfiguration values(8, 'AC.gif', '', 'Medium', '')
insert into CustomerWebConfiguration values(9, 'AC.gif', '', 'Light', '')
insert into CustomerWebConfiguration values(10, 'WaterHeater.gif', '', '8 Hours', '')
insert into CustomerWebConfiguration values(11, 'WaterHeater.gif', '', '4 Hours', '')
insert into CustomerWebConfiguration values(12, 'WaterHeater.gif', '', 'ETS', '')
insert into CustomerWebConfiguration values(13, 'DualFuel.gif', '', 'Limited', '')
insert into CustomerWebConfiguration values(14, 'DualFuel.gif', '', 'Unlimited', '')
insert into CustomerWebConfiguration values(15, 'Electric.gif', '', '', '')
insert into CustomerWebConfiguration values(16, 'Pool.gif', '', '', '')
insert into CustomerWebConfiguration values(17, 'HotTub.gif', '', '', '')
insert into CustomerWebConfiguration values(
18, 'Setback.gif', '<b>SETBACK <br>2&deg, 4&deg</b> - When controlled, your air conditioning compressor will be set back 2 degrees or 4 degrees depending on the program you sign up for. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'SetBack', '')
insert into CustomerWebConfiguration values(19, 'Setback.gif', '', '2 Degrees', '')
insert into CustomerWebConfiguration values(20, 'Setback.gif', '', '4 Degrees', '')


/* service company contacts */
insert into CustomerContact values (106,'Kevin','Johnson','763-555-1000',' ',0,-1)
insert into CustomerContact values (107,'Rudy','Johnson','763-555-8000',' ',0,-1)

/* service company address */
insert into CustomerAddress values (106,'3121 Rhode Island Ave',' ','Golden Valley','MN','55427')
insert into CustomerAddress values (107,'3121 25th Ave N',' ','Golden Valley','MN','55427')

/* service company */
insert into ServiceCompany values (1,'Johnson Electric',106,'7635551000','7635551001',106,'Electric')
insert into ServiceCompany values (2,'AAA Electric',107,'7635558888','7635558889',107,'Electric')

/* select * from yukonpaobject where category='ROUTE'*/
/* substation */
/* update route id for each substation */
insert into Substation values (1,'North Sub',3)
insert into Substation values (2,'South Sub',3)

/* customer contact */
/* need logins defined here */
insert into CustomerContact values (101,'Robert','Livingston','763-595-7777',' ',0,-1)
insert into CustomerContact values (102,'Jim','Wetland','763-444-9081',' ',0,-1)
insert into CustomerContact values (103,'Daniel','Sly','763-444-9012',' ',0,-1)
insert into CustomerContact values (104,'Joan','Elleswood','763-595-1432',' ',0,-1)
insert into CustomerContact values (105,'Nancy','Johnson','763-595-9817',' ',0,-1)

/* customer address */
insert into CustomerAddress values (101,'3801 Golden Valley Rd','Suite 300 ','Golden Valley','MN','55427')
insert into CustomerAddress values (102,'4210 42nd Ave N',' ','New Hope','MN','55435')
insert into CustomerAddress values (103,'89102 Birchwood Lane','','Edina','MN','55490')
insert into CustomerAddress values (104,'9182 Lakewood Ave','','Lakeville','MN','55900')
insert into CustomerAddress values (105,'1010 10th Ave SE','','Edina','MN','54091')

/* customer table */
insert into CustomerBase values (1,101,9,'CST',0)
insert into CustomerBase values (2,102,9,'CST',0)
insert into CustomerBase values (3,103,9,'CST',0)
insert into CustomerBase values (4,104,9,'CST',0)
insert into CustomerBase values (5,105,9,'CST',0)

/* site information */
insert into SiteInformation values (1,'1','B109','15 KVA','120',1)
insert into SiteInformation values (2,'1','A198','15 KVA','120',2)
insert into SiteInformation values (3,'2','LX09','15 KVA','120',1)
insert into SiteInformation values (4,'2','NW10','15 KVA','120',1)
insert into SiteInformation values (5,'1','B110','15 KVA','120',1)

/* Account site */
insert into AccountSite values (1,1,'NE0095',101,'')
insert into AccountSite values (2,2,'SW0918',102,'')
insert into AccountSite values (3,3,'NE9102',103,'')
insert into AccountSite values (4,4,'E10023',104,'')
insert into AccountSite values (5,5,'N08710',105,'')

/* customer account */
insert into CustomerAccount values (1,1,'12345',1,101,'Fenced in yard, call')
insert into CustomerAccount values (2,2,'80000',2,102,'Dog, enough said')
insert into CustomerAccount values (3,3,'90000',3,103,'')
insert into CustomerAccount values (4,4,'60000',4,104,'Meter in locked shed')
insert into CustomerAccount values (5,5,'10000',5,105,'')

/* inventory base */
INSERT INTO InventoryBase VALUES (-1,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0)
insert into InventoryBase values (1,1,1,11,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',12,'',0)
insert into InventoryBase values (2,2,1,11,'18-May-02','02-Jun-02','1970-01-01 00:00:00.000','',12,'',0)
insert into InventoryBase values (3,3,2,11,'18-May-02','10-Jun-02','1970-01-01 00:00:00.000','',12,'',0)
insert into InventoryBase values (4,4,2,11,'18-May-02','10-Jun-02','1970-01-01 00:00:00.000','',12,'',0)
insert into InventoryBase values (5,5,1,11,'18-May-02','10-Jun-02','1970-01-01 00:00:00.000','',12,'',0)
insert into InventoryBase values (6,1,1,11,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',12,'Thermostat',0)

/* lmhardware base */
insert into LMHardwareBase values (1,'550000034',13)
insert into LMHardwareBase values (2,'550000100',13)
insert into LMHardwareBase values (3,'550000200',13)
insert into LMHardwareBase values (4,'550000300',13)
insert into LMHardwareBase values (5,'550000400',13)
insert into LMHardwareBase values (6,'600000328',54)

/* appliance category */
insert into ApplianceCategory values (1, 'Air Conditioner', 14, 2)
insert into ApplianceCategory values (2, 'Water Heater', 15, 3)
insert into ApplianceCategory values (3, 'Heating System', 16, 4)
insert into ApplianceCategory values (4, 'Electric Thermal Storage', 16, 5)
insert into ApplianceCategory values (5, 'Pool Pump', 17, 6)
insert into ApplianceCategory values (6, 'Hot Tub', 18, 7)
insert into ApplianceCategory values (7, 'SetBack', 14, 18)


/* appliance base */
/* select * from lmprogramdirect */
/* update the lmprogram id so we have one for all controlled appliances */
insert into ApplianceBase values (1,1,1,5,'2000',41,42,0,0,'')
insert into ApplianceBase values (2,2,1,5,'2000',41,42,0,0,'')
insert into ApplianceBase values (3,2,2,5,'2000',41,43,0,0,'')
insert into ApplianceBase values (4,3,1,5,'2000',41,42,0,0,'')
insert into ApplianceBase values (5,4,1,5,'2000',41,42,0,0,'')
insert into ApplianceBase values (6,5,1,5,'2000',41,42,0,0,'')
insert into ApplianceBase values (7,1,2,8,'2000',41,43,0,0,'')


/* lm hardware configuration */
/* select * from LMGroup
select * from yukonpaobject where paobjectid=4
*/

/* last column is group paoid and is specific to server !!! */
insert into LMHardwareConfiguration values (1,1,4)
insert into LMHardwareConfiguration values (2,2,4)
insert into LMHardwareConfiguration values (2,3,4)
insert into LMHardwareConfiguration values (3,4,4)
insert into LMHardwareConfiguration values (4,5,4)
insert into LMHardwareConfiguration values (5,6,4)
insert into LMHardwareConfiguration values (1,7,4)


/* LMProgramWebPublishing */
insert into LMProgramWebPublishing values(1, 5, 8, 45)
insert into LMProgramWebPublishing values(1, 6, 9, 45)
insert into LMProgramWebPublishing values(2, 7, 10, 45)
insert into LMProgramWebPublishing values(2, 8, 11, 45)
insert into LMProgramWebPublishing values(2, 9, 12, 45)
insert into LMProgramWebPublishing values(3, 10, 13, 45)
insert into LMProgramWebPublishing values(3, 11, 14, 45)
insert into LMProgramWebPublishing values(4, 12, 15, 45)
insert into LMProgramWebPublishing values(5, 13, 16, 45)
insert into LMProgramWebPublishing values(6, 14, 17, 45)
insert into LMProgramWebPublishing values(7, 15, 19, 45)
insert into LMProgramWebPublishing values(7, 16, 20, 45)


/* InterviewQuestion */
insert into InterviewQuestion values(1,51,'What is your reason for overriding the current event?','N',1,53,0)
insert into InterviewQuestion values(2,51,'Would you like to be contacted by the program manager for any reason concerning the pilot?','N',2,53,0)


/* All the mapping tables */
/* ECToAccountMapping */
INSERT INTO ECToAccountMapping VALUES (1, 1)
INSERT INTO ECToAccountMapping VALUES (1, 2)
INSERT INTO ECToAccountMapping VALUES (1, 3)
INSERT INTO ECToAccountMapping VALUES (1, 4)
INSERT INTO ECToAccountMapping VALUES (1, 5)

/* ECToInventoryMapping */
INSERT INTO ECToInventoryMapping VALUES (1,-1)
INSERT INTO ECToInventoryMapping VALUES (1, 1)
INSERT INTO ECToInventoryMapping VALUES (1, 2)
INSERT INTO ECToInventoryMapping VALUES (1, 3)
INSERT INTO ECToInventoryMapping VALUES (1, 4)
INSERT INTO ECToInventoryMapping VALUES (1, 5)

/* ECToGenericMapping */
insert into ectogenericmapping values (1, 1, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 2, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 3, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 4, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 5, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 6, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 7, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 8, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 9, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 10, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 11, 'CustomerSelectionList')
insert into ectogenericmapping values (1, 12, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 13, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 14, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 15, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 16, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 17, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 18, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 19, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 20, 'CustomerSelectionList')
insert into ECToGenericMapping values (1, 5, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 6, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 7, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 8, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 9, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 10, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 11, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 12, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 13, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 14, 'LMPrograms-Switch')
insert into ECToGenericMapping values(1, 1, 'Substation')
insert into ECToGenericMapping values(1, 2, 'Substation')
insert into ECToGenericMapping values(1, 5, 'LMPrograms-Thermostat')
insert into ECToGenericMapping values(1, 6, 'LMPrograms-Thermostat')
insert into ECToGenericMapping values(1, 15, 'LMPrograms-Thermostat')
insert into ECToGenericMapping values(1, 16, 'LMPrograms-Thermostat')
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


/* Thermostat tables */
/* LMThermostatSeason */
INSERT INTO LMThermostatSeason VALUES (1,-1,-1,'01-JUN-00',1)
INSERT INTO LMThermostatSeason VALUES (2,-1,-2,'15-OCT-00',2)

/* LMThermostatSeasonEntry */
INSERT INTO LMThermostatSeasonEntry VALUES (1,46,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,46,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,46,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,46,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,48,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,48,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,48,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,48,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,49,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,49,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,49,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (1,49,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,46,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,46,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,46,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,46,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,48,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,48,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,48,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,48,75600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,49,21600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,49,30600,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,49,61200,72)
INSERT INTO LMThermostatSeasonEntry VALUES (2,49,75600,72)

/* LMThermostatManualOption */
INSERT INTO LMThermostatManualOption VALUES (-1,72,'N',55,58)