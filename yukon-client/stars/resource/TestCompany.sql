insert into YukonWebConfiguration values(1,'','','XYZ Utility','');
insert into YukonWebConfiguration values(
2, 'AC.gif', '<b>CYCLE AC<br> Light, Medium</b> - When controlled, your air conditioning compressor will be interrupted for 10 minutes out of every half hour if you sign up for the light program and interrupted for 15 minutes out of every half hour if you sign up for the medium program.', 'Cycle AC', '');
insert into YukonWebConfiguration values(
3, 'WaterHeater.gif', '<b>WATER HEATER<br>4 Hour, 8 Hour</b> - When controlled, power to your water heater''s heating elements is turned off for up to 4 hours or 8 hours depending on the program you choose. The hot water in the tank will still be available for you to use.<br><br>  <b>ETS</b> - Your Electric Thermal Storage water heater''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.', 'Water Heater', '');
insert into YukonWebConfiguration values(
4, 'DualFuel.gif', '<b>DUAL FUEL <br> Limited 4 Hour, Unlimited</b> - When controlled, electric power to your home''s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. Control is limited to four hours consecutively when signed up for the limited program. While usually limited to a few hours, control could be for an extended period if signed up for the unlimited program.', 'Dual Fuel Space Heater', '');
insert into YukonWebConfiguration values(
5, 'Electric.gif', '<b>ETS</b><br>Your Electric Thermal Storage heating system''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.', 'ETS', '');
insert into YukonWebConfiguration values(
6, 'Pool.gif', '<b>POOL PUMP</b><br>When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.', 'Pool Pump', '');
insert into YukonWebConfiguration values(
7, 'HotTub.gif', '<b>HOT TUB</b><br>When controlled, power to your hot tub''s water heating elements are interrupted. Interruptions usually last for four hours or less.', 'Hot Tub', '');
insert into YukonWebConfiguration values(8, 'AC.gif,ACSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'When controlled, your air conditioning compressor will be interrupted for 15 minutes out of every half hour. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'Medium', '');
insert into YukonWebConfiguration values(9, 'AC.gif,ACSm.gif,$$Sm.gif,ThirdSm.gif,Tree2Sm.gif', 'When controlled, your air conditioning compressor will be interrupted for ten minutes out of every half hour. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'Light', '');
insert into YukonWebConfiguration values(10, 'WaterHeater.gif,WaterSm.gif,$$Sm.gif,ThirdSm.gif,Tree2Sm.gif', 'When controlled, power to your water heater''s heating elements is turned off for up to eight hours. The hot water in the tank will still be available for you use.', '8 Hour', '');
insert into YukonWebConfiguration values(11, 'WaterHeater.gif,WaterSm.gif,$$Sm.gif,SixthSm.gif,Tree2Sm.gif', 'When controlled, power to your water heater''s heating elements is turned off for up to four hours. The hot water in the tank will still be available for you use.', '4 Hour', '');
insert into YukonWebConfiguration values(12, 'WaterHeater.gif,WaterSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'Your Electric Thermal Storage water heater''s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The hot water stored in the tank will supply your hot water needs.', 'ETS', '');
insert into YukonWebConfiguration values(13, 'DualFuel.gif,DualFuelSm.gif,$$Sm.gif,SixthSm.gif,Tree2Sm.gif', 'When controlled, electric power to your home’s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. Control is limited to four hours consecutively. ', 'Limited 4 Hour', '');
insert into YukonWebConfiguration values(14, 'DualFuel.gif,DualFuelSm.gif,$$$Sm.gif,ThirdSm.gif,Tree3Sm.gif', 'When controlled, electric power to your home’s heating system will be switched off, and your non-electric heat source will provide for your home''s heating needs. While usually limited to a few hours, control could be for an extended period. ', 'Unlimited', '');
insert into YukonWebConfiguration values(15, 'Electric.gif,ElectricSm.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'Your Electric Thermal Storage heating system’s heating elements are interrupted on a daily 12-hour on, 12-hour off cycle. The heat stored will supply your home needs.', 'ETS', '');
insert into YukonWebConfiguration values(16, 'Pool.gif,PoolPumpSm.gif,$Sm.gif,SixthSm.gif,Tree1Sm.gif', 'When controlled, power to your pool pump is interrupted. Interruptions usually last for 4 hours or less.', '', '');
insert into YukonWebConfiguration values(17, 'HotTub.gif,HotTubSm.gif,$Sm.gif,SixthSm.gif,Tree1Sm.gif', '', '', '');
insert into YukonWebConfiguration values(
18, 'Setback.gif', '<b>SETBACK <br>2&deg, 4&deg</b> - When controlled, your air conditioning compressor will be set back 2 degrees or 4 degrees depending on the program you sign up for. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', 'SetBack', '');
insert into YukonWebConfiguration values(19, 'Setback.gif,$Sm.gif,QuarterSm.gif,Tree1Sm.gif', 'When controlled, your air conditioning compressor will be set back two degrees. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', '2&deg', '');
insert into YukonWebConfiguration values(20, 'Setback.gif,$$$Sm.gif,HalfSm.gif,Tree3Sm.gif', 'When controlled, your air conditioning compressor will be set back four degrees. Your furnace fan will keep running. You may notice a slight increase in your indoor air temperature.', '4&deg', '');
insert into YukonWebConfiguration values(21,'Irrigation.gif','<b>IRRIGATION</b><br>Irrigation control.','Irrigation','');
insert into YukonWebConfiguration values(22,'Irrigation.gif,IrrigationSm.gif','Irrigation control.','Irrigation','');

/* energy company address */
insert into Address values (1,'9000 6th Street','','Minneapolis','MN','55441','(none)');

/* energy company contact */
insert into Contact values (1,'(none)','(none)',-1,1);
insert into ContactNotification values (1,1,2,'N','1-800-555-5432');
insert into ContactNotification values (2,1,4,'N','1-800-555-5431');
insert into ContactNotification values (3,1,1,'N','info@xyzutility.com');

/*
Create energy company default user in DBEditor!!!
As far as what I concern, an EnergyCompany role must be assigned to this "user". The role hasn't been checked into cvs yet, it'll be defined in com.cannontech.roles.yukon.EnergyCompanyRole.

A default route id must also be attached to this user. This is what I figured out how it should be done:
First, create a load group w/ serial# set to 0, and route set to whatever you want to use.
Then, create a macro group to include this group (you can also add other groups for a CI cusotmer, but I don't think we need to here)
Last, add the mapping b/w the user and the macro group in the OperatorSerialGroup table.
*/
insert into OperatorSerialGroup values(&loginId, &lmGroupId);

/* EnergyCompany */
insert into EnergyCompany values (1,'Test Company',&contactId,&loginId);

/* service company contacts */
insert into Contact values (2,'Kevin','Johnson',-1,0);
insert into Contact values (3,'Rudy','Johnson',-1,0);

/* service company address */
insert into Address values (2,'3121 Rhode Island Ave',' ','Golden Valley','MN','55427','Hennepin');
insert into Address values (3,'3121 25th Ave N',' ','Golden Valley','MN','55427','Hennepin');

/* service company */
insert into ServiceCompany values (1,'Johnson Electric',&addressId,'7635551000','7635551001',&contactId,'Electric');
insert into ServiceCompany values (2,'AAA Electric',&addressId,'7635558888','7635558889',&contactId,'Electric');

/* substation */
insert into Substation values (1,'North Sub',0);
insert into Substation values (2,'South Sub',0);

/* appliance category */
insert into ApplianceCategory values (1, 'Air Conditioner', 1019, 2);
insert into ApplianceCategory values (2, 'Water Heater', 1020, 3);
insert into ApplianceCategory values (3, 'Dual Fuel', 1023, 4);
insert into ApplianceCategory values (4, 'ETS', 1021, 5);
insert into ApplianceCategory values (5, 'Pool Pump', 1022, 6);
insert into ApplianceCategory values (6, 'Hot Tub', 1020, 7);
insert into ApplianceCategory values (7, 'Irrigation', 1069, 21);

/* Create LM Programs in DBEditor!!! */

/* LMProgramWebPublishing */
insert into LMProgramWebPublishing values(1,6,8,0);
insert into LMProgramWebPublishing values(1,7,9,0);
insert into LMProgramWebPublishing values(2,8,10,0);
insert into LMProgramWebPublishing values(2,9,11,0);
insert into LMProgramWebPublishing values(2,10,12,0);
insert into LMProgramWebPublishing values(7,11,22,1045);

/* ECToGenericMapping */
insert into ECToGenericMapping values (1, 1, 'Substation');
insert into ECToGenericMapping values (1, 2, 'Substation');
INSERT INTO ECToGenericMapping VALUES (1, 1, 'ServiceCompany');
INSERT INTO ECToGenericMapping VALUES (1, 2, 'ServiceCompany');
INSERT INTO ECToGenericMapping VALUES (1, 1, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 2, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 3, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 4, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 5, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 6, 'ApplianceCategory');
INSERT INTO ECToGenericMapping VALUES (1, 7, 'ApplianceCategory');

/* Create YukonUser in DBEditor!!! */
insert into EnergyCompanyOperatorLoginList values (1, &loginId);

/* customer contact */
insert into Contact values (101,'Robert','Livingston',-1,0);
insert into ContactNotification values (101,101,5,'N','763-595-7777');	/* home hone */

/* customer address */
insert into Address values (101,'3801 Golden Valley Rd','Suite 300','Golden Valley','MN','55427','Hennepin');

/* customer table */
insert into Customer values (101,101,1,'CST');	/* residential customer */

/* site information */
insert into SiteInformation values (1,'1','B109','15 KVA','120',1);

/* Account site */
insert into AccountSite values (1,1,'NE0095',101,'');

/* customer account */
insert into CustomerAccount values (1,1,'12345',101,101,'Fenced in yard, call',102);	/* login id set here! */

/* inventory base */
insert into InventoryBase values (1,1,1,1011,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',1012,'',0);
insert into InventoryBase values (2,1,1,1011,'18-May-02','01-Jun-02','1970-01-01 00:00:00.000','',1012,'Thermostat',0);

/* lmhardware base */
insert into LMHardwareBase values (1,'550000034',1013);	/* LCR-5000 */
insert into LMHardwareBase values (2,'000000000',1018);	/* thermostat */
