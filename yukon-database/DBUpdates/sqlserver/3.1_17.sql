/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
update command set label = 'Read Meter Config' where commandid = -3;
update command set command = 'putconfig emetcon multiplier kyz 1 ?Multiplier(x.xxx)' where commandid = -8;
update command set command = 'getvalue ied kwht' where commandid = -21;
go


insert into command values(-115, 'getvalue ied current kwha', 'Read Current Rate A kWh/Peak kW', 'MCT-470');
insert into command values(-116, 'getvalue ied current kwhb', 'Read Current Rate B kWh/Peak kW', 'MCT-470');
insert into command values(-117, 'getvalue ied current kwhc', 'Read Current Rate C kWh/Peak kW', 'MCT-470');
insert into command values(-118, 'getvalue ied current kwhd', 'Read Current Rate D kWh/Peak kW', 'MCT-470');
insert into command values(-119, 'getvalue ied frozen kwha', 'Read Frozen Rate A kWh/Peak kW', 'MCT-470');
insert into command values(-120, 'getvalue ied frozen kwhb', 'Read Frozen Rate B kWh/Peak kW', 'MCT-470');
insert into command values(-121, 'getvalue ied frozen kwhc', 'Read Frozen Rate C kWh/Peak kW', 'MCT-470');
insert into command values(-122, 'getvalue ied frozen kwhd', 'Read Frozen Rate D kWh/Peak kW', 'MCT-470');
insert into command values(-123, 'getconfig options', 'Read Options', 'MCT-470');
go

INSERT INTO DEVICETYPECOMMAND VALUES (-408, -1, 'MCT-470', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-409, -2, 'MCT-470', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-410, -3, 'MCT-470', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-411, -20, 'MCT-470', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-412, -21, 'MCT-470', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-413, -22, 'MCT-470', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-414, -115, 'MCT-470', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-415, -116, 'MCT-470', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-416, -117, 'MCT-470', 9, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-417, -118, 'MCT-470', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-418, -119, 'MCT-470', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-419, -120, 'MCT-470', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-420, -121, 'MCT-470', 13, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-421, -122, 'MCT-470', 14, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-422, -123, 'MCT-470', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-423, -111, 'MCT-470', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-424, -7, 'MCT-470', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-425, -8, 'MCT-470', 18, 'Y', -1);
go





/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '14-DEC-2005', 'Manual version insert done', 17);