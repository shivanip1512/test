/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table dynamiclmprogramdirect drop column dailyops;
alter table dynamiclmgroup add dailyops smallint;
update dynamiclmgroup set dailyops = 0;
alter table dynamiclmgroup modify dailyops smallint not null;

alter table state modify text varchar2(32) not null;

insert into stategroup values( -6, '410 Disconnect', 'Status');
insert into state values( -6, 0, 'Confirmed Disconnected', 1, 6, 0);
insert into state values( -6, 1, 'Connected', 0, 6, 0);
insert into state values( -6, 2, 'Unconfirmed Disconnected', 3, 6, 0);
insert into state values( -6, 3, 'Connect Armed', 5, 6, 0);





delete from devicetypecommand where devicecommandid in (-8, -74, -75, -77, -132, -133, -134, -135, -136, -137, -138,  -139, -140, -141, -142, -143, -144, -145);
delete from command where commandID in (-46, -47, -49, -70, -71, -72, -73, -74, -75, -76, -77, -78, -79, -80);
insert into command values(-70, 'putconfig cycle r1 50', 'Install Cycle Count', 'LCRSerial');
insert into command values(-71, 'putconfig template ''?LoadGroup''', 'Install Versacom Addressing', 'VersacomSerial');
insert into command values(-72, 'putconfig cold_load r1 10', 'Install Versacom Cold Load (relay, minutes)', 'VersacomSerial');
insert into command values(-73, 'putconfig raw 0x3a ff', 'Emetcon Cold Load (ON -ff / OFF -00', 'VersacomSerial');
insert into command values(-74, 'putconfig raw 35 0', 'Set LCR 3000 to Emetcon Mode', 'VersacomSerial');
insert into command values(-75, 'putconfig raw 36 1', 'Set LCR 3000 to Versacom Mode', 'VersacomSerial');
insert into command values(-85, 'control shed 5m relay 1', 'Shed 5-min Relay 1', 'All LCRs');
insert into command values(-86, 'control shed 5m relay 2', 'Shed 5-min Relay 2', 'All LCRs');
insert into command values(-87, 'control shed 5m relay 3', 'Shed 5-min Relay 3', 'All LCRs');
insert into command values(-88, 'control restore relay 1', 'Restore Relay 1', 'All LCRs');
insert into command values(-89, 'control restore relay 2', 'Restore Relay 2', 'All LCRs');
insert into command values(-90, 'control restore relay 3', 'Restore Relay 3', 'All LCRs');
insert into command values(-91, 'control cycle 50 period 30 relay 1', 'Cycle 50 Period-30 Relay 1', 'All LCRs');
insert into command values(-92, 'control cycle terminate relay 1', 'Terminate Cycle Relay 1', 'All LCRs');
insert into command values(-93, 'control cycle terminate relay 2', 'Terminate Cycle Relay 2', 'All LCRs');
insert into command values(-94, 'control cycle terminate relay 3', 'Terminate Cycle Relay 3', 'All LCRs');
insert into command values(-95, 'putconfig service out', 'Set to Out-of-Service', 'All LCRs');
insert into command values(-96, 'putconfig service in', 'Set to In-Service', 'All LCRs');
insert into command values(-97, 'putconfig led yyy', 'Configure LEDS (report, test, load)', 'All LCRs');

INSERT INTO DEVICETYPECOMMAND VALUES (-74, -95, 'Versacom Group', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-75, -96, 'Versacom Group', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-77, -97, 'Versacom Group', 8, 'Y');

INSERT INTO DEVICETYPECOMMAND VALUES (-328, -70, 'LCRSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-329, -85, 'LCRSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-330, -86, 'LCRSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-331, -87, 'LCRSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-332, -88, 'LCRSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-333, -89, 'LCRSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-334, -90, 'LCRSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-335, -91, 'LCRSerial', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-336, -92, 'LCRSerial', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-337, -93, 'LCRSerial', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-338, -94, 'LCRSerial', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-339, -95, 'LCRSerial', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-340, -96, 'LCRSerial', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-341, -97, 'LCRSerial', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-342, -48, 'LCRSerial', 15, 'Y');

INSERT INTO DEVICETYPECOMMAND VALUES (-343, -71, 'VersacomSerial', 1, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-344, -72, 'VersacomSerial', 2, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-345, -73, 'VersacomSerial', 3, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-346, -74, 'VersacomSerial', 4, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-347, -75, 'VersacomSerial', 5, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-348, -85, 'VersacomSerial', 6, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-349, -86, 'VersacomSerial', 7, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-350, -87, 'VersacomSerial', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-351, -88, 'VersacomSerial', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-352, -89, 'VersacomSerial', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-353, -90, 'VersacomSerial', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-354, -91, 'VersacomSerial', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-355, -92, 'VersacomSerial', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-356, -93, 'VersacomSerial', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-357, -94, 'VersacomSerial', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-358, -95, 'VersacomSerial', 16, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-359, -96, 'VersacomSerial', 17, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-360, -97, 'VersacomSerial', 18, 'Y');

INSERT INTO DEVICETYPECOMMAND VALUES (-132, -85, 'ExpresscomSerial', 8, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-133, -86, 'ExpresscomSerial', 9, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-134, -87, 'ExpresscomSerial', 10, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-135, -88, 'ExpresscomSerial', 11, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-136, -89, 'ExpresscomSerial', 12, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-137, -90, 'ExpresscomSerial', 13, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-138, -91, 'ExpresscomSerial', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-139, -92, 'ExpresscomSerial', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-140, -93, 'ExpresscomSerial', 16, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-141, -94, 'ExpresscomSerial', 17, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-142, -95, 'ExpresscomSerial', 18, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-143, -96, 'ExpresscomSerial', 19, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-144, -97, 'ExpresscomSerial', 20, 'Y');

update command set command = 'putconfig xcom service out temp offhours 24' where commandid = -68;
update command set command = 'putconfig xcom service in' where commandid = -69;

update point set stategroupid = -6
where paobjectid in (select paobjectid from yukonpaobject where type = 'MCT-410IL')
and pointoffset = 1 and pointtype = 'Status';


alter table lmprogramdirect add restoreoffset float;
update lmprogramdirect set restoreoffset = 0.0;
alter table lmprogramdirect modify restoreoffset float not null;


insert into YukonRoleProperty values(-10602,-106,'Header Label','Billing','The header label for billing.');
insert into YukonRoleProperty values(-10603,-106,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-10604,-106,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-10605,-106,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-10606,-106,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-10607,-106,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-10608,-106,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');
update yukonroleproperty set description = 'The NCDC format takes in an input file.' where rolepropertyid = -10601;

delete from yukongrouprole where roleid = -500;
delete from yukonuserrole where roleid = -500;
delete yukonroleproperty where rolepropertyid in (-50000, -50001, -50002, -50003, -50004, -50005, -50006, -50007);
delete yukonrole where roleid = -500;

insert into YukonGroupRole values(-232,-1,-106,-10602,'(none)');
insert into YukonGroupRole values(-233,-1,-106,-10603,'(none)');
insert into YukonGroupRole values(-234,-1,-106,-10604,'(none)');
insert into YukonGroupRole values(-235,-1,-106,-10605,'(none)');
insert into YukonGroupRole values(-236,-1,-106,-10606,'(none)');
insert into YukonGroupRole values(-237,-1,-106,-10607,'(none)');









/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '10-MAY-2005', 'Manual version insert done', 6);