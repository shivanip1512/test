create table Display (
                DisplayNum      INTEGER NOT NULL,
                Name            VARCHAR2(40) NOT NULL,
                Type            VARCHAR2(20) NOT NULL,
                Title           VARCHAR2(30),
                Description     VARCHAR2(200),
                PRIMARY KEY (DisplayNum) );

create or replace view Display2WayData_View (PointID, PointName, 
	PointType, PointState, DeviceName, DeviceType, 
	DeviceCurrentState, DeviceID,
	PointValue, PointQuality, PointTimeStamp, UofM ) 
	as
	select point.pointid, point.pointname, point.pointtype, 
	point.serviceflag, device.name, device.type, 
	device.currentstate, device.deviceid,
	'**DYNAMIC**', '**DYNAMIC**',	'**DYNAMIC**', 
	(select unit from pointunit where pointunit.pointid=point.pointid)
	from point, device
	where 
	point.deviceid=device.deviceid;

create table ColumnType (
                TypeNum	        INTEGER,
                Name	        VARCHAR2(20) NOT NULL,
                PRIMARY KEY (TypeNum) );

create table DisplayColumns (
          DisplayNum    INTEGER  REFERENCES Display(DisplayNum),
          Title         VARCHAR2(50),
	  TypeNum	INTEGER NOT NULL REFERENCES ColumnType(TypeNum),
          Ordering      INTEGER NOT NULL,
	  Width		INTEGER NOT NULL );

create table Display2WayData (
                DisplayNum      INTEGER REFERENCES Display(DisplayNum),
                PointID         INTEGER NOT NULL,
                Ordering        INTEGER NOT NULL );

create table Template (
                TemplateNum     INTEGER NOT NULL,
                Name            VARCHAR2(40) NOT NULL,
                Description     VARCHAR2(200),
                PRIMARY KEY (TemplateNum) );

create table TemplateColumns (
          TemplateNum   INTEGER  REFERENCES Template(TemplateNum),
          Title         VARCHAR2(50),
	  TypeNum	INTEGER NOT NULL REFERENCES ColumnType(TypeNum),
          Ordering      INTEGER NOT NULL,
	  Width		INTEGER NOT NULL );

insert into columntype values (1, 'PointID');
insert into columntype values (2, 'PointName');
insert into columntype values (3, 'PointType');
insert into columntype values (4, 'PointState');
insert into columntype values (5, 'DeviceName');
insert into columntype values (6, 'DeviceType');
insert into columntype values (7, 'DeviceCurrentState');
insert into columntype values (8, 'DeviceID');
insert into columntype values (9, 'PointValue');
insert into columntype values (10, 'PointQuality');
insert into columntype values (11, 'PointTimeStamp');
insert into columntype values (12, 'UofM');

insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon Template');

insert into templatecolumns values( 1, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 1, 'Point Name', 2, 2, 85 );
insert into templatecolumns values( 1, 'Value', 9, 3, 85 );
insert into templatecolumns values( 1, 'Quality', 10, 4, 80 );
insert into templatecolumns values( 1, 'Time', 11, 5, 135 );
insert into templatecolumns values( 2, 'Device Name', 5, 1, 85 );
insert into templatecolumns values( 2, 'Value', 9, 2, 85 );
insert into templatecolumns values( 2, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 2, 'Time', 11, 4, 135 );
insert into templatecolumns values( 3, 'Point Name', 2, 1, 85 );
insert into templatecolumns values( 3, 'Value', 9, 2, 85 );
insert into templatecolumns values( 3, 'Quality', 10, 3, 80 );
insert into templatecolumns values( 3, 'Time', 11, 4, 135 );

insert into display values(-1, 'Scheduler Client', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');
insert into display values(-2, 'Cap Control Client', 'Cap Control Client', 'Capacitor Bank Controller', 'com.cannontech.cbc.gui.StrategyReceiver');
insert into display values(-3, 'Load Control Client', 'Load Control Client', 'Load Control', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');

insert into display values(99, 'Basic User Created', 'Custom Displays', 'A Predefined Generic Display', 'This display is is used to show what a user created display looks like.');
insert into displaycolumns values(99, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(99, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(99, 'Value', 9, 4, 160 );

insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');
insert into displaycolumns values(1, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(1, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(1, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(1, 'Description', 9, 4, 180 );
insert into displaycolumns values(1, 'Action', 10, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');
insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Description', 9, 4, 180 );
insert into displaycolumns values(2, 'Action', 10, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );

insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.');
insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Description', 9, 4, 200 );
insert into displaycolumns values(3, 'Action', 10, 5, 200 );
insert into displaycolumns values(3, 'User Name', 8, 6, 35 );

insert into stategroup values (-5, 'Event Priority');
insert into state values (-5, 1, 'Priority 1', 1, 6);
insert into state values (-5, 2, 'Priority 2', 4, 6);
insert into state values (-5, 3, 'Priority 3', 0, 6);
insert into state values (-5, 4, 'Priority 4', 7, 6);
insert into state values (-5, 5, 'Priority 5', 8, 6);
insert into state values (-5, 6, 'Priority 6', 5, 6);
insert into state values (-5, 7, 'Events', 2, 6);

insert into display values(4, 'All Alarms', 'Alarms and Events', 'Global Alarm Viewer', 'This display will recieve all alarm events as they happen in the system.');
insert into displaycolumns values(4, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(4, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(4, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(4, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(4, 'User Name', 8, 5, 50 );
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'Priority 1 Alarm Viewer', 'This display will recieve all priority 1 alarm events as they happen in the system.');
insert into displaycolumns values(5, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(5, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(5, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(5, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(5, 'User Name', 8, 5, 50 );
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'Priority 2 Alarm Viewer', 'This display will recieve all priority 2 alarm events as they happen in the system.');
insert into displaycolumns values(6, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(6, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(6, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(6, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(6, 'User Name', 8, 5, 50 );
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'Priority 3 Alarm Viewer', 'This display will recieve all priority 3 alarm events as they happen in the system.');
insert into displaycolumns values(7, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(7, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(7, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(7, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(7, 'User Name', 8, 5, 50 );
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'Priority 4 Alarm Viewer', 'This display will recieve all priority 4 alarm events as they happen in the system.');
insert into displaycolumns values(8, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(8, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(8, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(8, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(8, 'User Name', 8, 5, 50 );
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'Priority 5 Alarm Viewer', 'This display will recieve all priority 5 alarm events as they happen in the system.');
insert into displaycolumns values(9, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(9, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(9, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(9, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(9, 'User Name', 8, 5, 50 );
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'Priority 6 Alarm Viewer', 'This display will recieve all priority 6 alarm events as they happen in the system.');
insert into displaycolumns values(10, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(10, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(10, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(10, 'Text Message', 9, 4, 200 );
insert into displaycolumns values(10, 'User Name', 8, 5, 50 );