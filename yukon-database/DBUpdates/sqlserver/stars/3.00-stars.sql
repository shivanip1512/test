/***************************************************/
/**** SQLServer 2000 DBupdates for STARS        ****/
/***************************************************/


alter table InventoryBase alter column Notes Varchar(500);
go
alter table AccountSite alter column PropertyNotes Varchar(300);
go
alter table ApplianceBase alter column Notes Varchar(500);
go
alter table CustomerResidence alter column Notes Varchar(300);
go

alter table ApplianceHeatPump add PumpSizeID numeric;
go
alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID)
go

alter table CustomerAccount drop constraint FK_YkUs_CstAcc;
go
alter table CustomerAccount drop column LoginID;
go

alter table LMProgramWebPublishing add ProgramOrder numeric;
go
update LMProgramWebPublishing set ProgramOrder = 0;
go

update YukonUserRole set Value=(select GroupID from YukonGroup where GroupName=YukonUserRole.Value or GroupName=YukonUserRole.Value + ' Grp') where RolePropertyID in (-1105,-1106) and Value <> '(none)';
go

update YukonWebConfiguration set LogoLocation='yukon/Icons/' + LogoLocation where LogoLocation in (
	'AC.gif', 'DualFuel.gif', 'Electric.gif', 'Generation.gif', 'GrainDryer.gif', 'HeatPump.gif', 'HotTub.gif',
	'Irrigation.gif', 'Load.gif', 'Pool.gif', 'SetBack.gif', 'StorageHeat.gif', 'WaterHeater.gif');
go



/* Stars tables initialization script */
INSERT INTO ServiceCompany VALUES (0,'(none)',0,'(none)','(none)',0,'(none)');
INSERT INTO Substation VALUES (0,'(none)',0);
INSERT INTO SiteInformation VALUES (0,'(none)','(none)','(none)','(none)',0);
INSERT INTO AccountSite VALUES (0,0,'(none)',0,'(none)');
INSERT INTO CustomerAccount VALUES (0,0,'(none)',-1,0,'(none)');

INSERT INTO InventoryBase VALUES (0,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'(none)',0,'(none)');
INSERT INTO InventoryBase VALUES (-1,0,0,0,'01-JAN-70','01-JAN-70','01-JAN-70','(none)',0,'Default Thermostat',0,'(none)');
INSERT INTO LMHardwareBase VALUES (-1,'0',0);
INSERT INTO ECToInventoryMapping VALUES (-1,-1);

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

insert into CustomerFAQ values(1,1231,'How long does it take for my program to become effective after adding or changing a program?','Immediately! You can take advantage of energy savings the moment you decide to. Just make your selection on the "Programs - Add/Change" page, click the submit button, and select Yes at the prompt.');
insert into CustomerFAQ values(2,1231,'How do I find out more about my program or other programs?','Go to the "Programs - Add/Change" page and click the Program Details button. You will find all of the information you need here regarding the program, amount of control, and savings.');
insert into CustomerFAQ values(3,1231,'Can I sign up for more than one program?','Certainly! The more programs you enroll in, the more energy savings you will receive.');
insert into CustomerFAQ values(4,1232,'Can I control my thermostat even if I do not know my current settings?','Yes. You may select the temperature change (up or down) in degrees without knowing the current temperature or simply set a new specific temperature. If pre-cooling, you may also select a new specific temperature or select the number of degress to decrease in temperature.');
insert into CustomerFAQ values(5,1232,'What does the Fan setting do?','The fan setting controls the operation of the fan. <br>Auto - the fan runs only as necessary to maintain the current temperature settings. <br>On - the fan runs continuously. <br>Off - the fan does not run.');
insert into CustomerFAQ values(6,1232,'Does the utility company have access to my thermostat?','The utility only has access to your thermostat for control based on the programs you have signed up for. When not being controlled, you have complete control of your thermostat.');
insert into CustomerFAQ values(7,1233,'How much credit do I receive if I opt out while controlling?','You will receive credit for the portion of time you were controlled.');

INSERT INTO LMThermostatSeason VALUES (-1,-1,-1,'01-JUN-00',1);
INSERT INTO LMThermostatSeason VALUES (-2,-1,-2,'15-OCT-00',2);

INSERT INTO LMThermostatSeasonEntry VALUES (-24,-1,1171,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-23,-1,1171,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-22,-1,1171,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-21,-1,1171,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-20,-1,1173,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-19,-1,1173,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-18,-1,1173,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-17,-1,1173,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-16,-1,1174,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-15,-1,1174,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-14,-1,1174,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-13,-1,1174,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-12,-2,1171,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-11,-2,1171,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-10,-2,1171,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-9,-2,1171,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-8,-2,1173,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-7,-2,1173,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-6,-2,1173,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-5,-2,1173,75600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-4,-2,1174,21600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-3,-2,1174,30600,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-2,-2,1174,61200,72);
INSERT INTO LMThermostatSeasonEntry VALUES (-1,-2,1174,75600,72);

INSERT INTO LMCustomerEventBase VALUES (-1,1003,1020,'01-JAN-70','','');
INSERT INTO LMThermostatManualEvent VALUES (-1,-1,72,'N',1211,1221);
INSERT INTO ECToLMCustomerEventMapping VALUES (-1,-1);
