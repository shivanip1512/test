/*==============================================================*/
/* Database name:  YUKON                                        */
/* DBMS name:      ORACLE Version 8                             */
/* Created on:     4/16/2001 4:13:13 PM                         */
/*==============================================================*/


drop index Index_DISPLAYNAME
/


drop index Index_UserIDName
/


drop index Indx_CAPCNTST2
/


drop index Indx_CAPCNTSTR1
/


drop index Indx_COLUMNTYPE
/


drop index Indx_COMMPORT1
/


drop index Indx_COMMPORT2
/


drop index Indx_DEVICE1
/


drop index Indx_DEVICE2
/


drop index Indx_DISPLAY
/


drop index Indx_GPHDATSER
/


drop index Indx_GPHDEF
/


drop index Indx_LOGIC
/


drop index Indx_MCSCHED
/


drop index Indx_NOTIFGRP
/


drop index Indx_POINT
/


drop index Indx_PROGRAM1
/


drop index Indx_PROGRAM2
/


drop index Indx_RAWPTHIS
/


drop index Indx_ROUTE1
/


drop index Indx_ROUTE2
/


drop index Indx_STATEGRP1
/


drop index Indx_STATEGRP2
/


drop index Indx_SYSTEMLOG
/


drop index Indx_TEMPLATE
/


drop index Indx_UOM
/


drop index Indx_USERINFO
/


drop index SchedNameIndx
/


drop table LMCurtailCustomerActivity cascade constraints
/


drop table DYNAMICCAPCONTROLSTRATEGY cascade constraints
/


drop table LMProgramCurtailCustomerList cascade constraints
/


drop table CCSTRATEGYBANKLIST cascade constraints
/


drop table LMGROUPVERSACOMSERIAL cascade constraints
/


drop table LMGROUPVERSACOM cascade constraints
/


drop table LMGROUPEMETCON cascade constraints
/


drop table LASTPOINTSCAN cascade constraints
/


drop table CICustContact cascade constraints
/


drop table CustomerContact cascade constraints
/


drop table CAPCONTROLSTRATEGY cascade constraints
/


drop table CICustomerBase cascade constraints
/


drop table SERIALNUMBER cascade constraints
/


drop table GRAPHDATASERIES cascade constraints
/


drop table REPEATERROUTE cascade constraints
/


drop table LMProgramDirectGroup cascade constraints
/


drop table RAWPOINTHISTORY cascade constraints
/


drop table FDRTRANSLATION cascade constraints
/


drop table CAPBANK cascade constraints
/


drop table DYNAMICACCUMULATOR cascade constraints
/


drop table COMMUNICATIONROUTE cascade constraints
/


drop table LMCONTROLAREAPROGRAM cascade constraints
/


drop table LMCONTROLAREATRIGGER cascade constraints
/


drop table SYSTEMLOG cascade constraints
/


drop table DISPLAY2WAYDATA cascade constraints
/


drop table CALCCOMPONENT cascade constraints
/


drop table CALCBASE cascade constraints
/


drop table DEVICECBC cascade constraints
/


drop table POINTUNIT cascade constraints
/


drop table PointAlarming cascade constraints
/


drop table POINTSTATUS cascade constraints
/


drop table POINTLIMITS cascade constraints
/


drop table POINTDISPATCH cascade constraints
/


drop table POINTCONTROL cascade constraints
/


drop table POINTANALOG cascade constraints
/


drop table POINTACCUMULATOR cascade constraints
/


drop table POINT cascade constraints
/


drop table DEVICEROUTES cascade constraints
/


drop table MACROROUTE cascade constraints
/


drop table CARRIERROUTE cascade constraints
/


drop table STATE cascade constraints
/


drop table DynamicLMProgram cascade constraints
/


drop table DynamicLMGroup cascade constraints
/


drop table DynamicLMProgramDirect cascade constraints
/


drop table CustomerLogin cascade constraints
/


drop table CustomerAddress cascade constraints
/


drop table LMCurtailProgramActivity cascade constraints
/


drop table LMProgramCurtailment cascade constraints
/


drop table LMGroup cascade constraints
/


drop table DynamicLMControlArea cascade constraints
/


drop table LMProgramDirectGear cascade constraints
/


drop table LMProgramDirect cascade constraints
/


drop table LMProgramControlWindow cascade constraints
/


drop table LMPROGRAM cascade constraints
/


drop table LMCONTROLAREA cascade constraints
/


drop table MACSimpleSchedule cascade constraints
/


drop table MACSchedule cascade constraints
/


drop table AlarmState cascade constraints
/


drop table UserGraph cascade constraints
/


drop table NotificationDestination cascade constraints
/


drop table NotificationGroup cascade constraints
/


drop table VERSACOMROUTE cascade constraints
/


drop table USERWEB cascade constraints
/


drop table USERPROGRAM cascade constraints
/


drop table USERINFO cascade constraints
/


drop table UNITMEASURE cascade constraints
/


drop table TEMPLATECOLUMNS cascade constraints
/


drop table TEMPLATE cascade constraints
/


drop table GroupRecipient cascade constraints
/


drop table STOPABSOLUTETIME cascade constraints
/


drop table STATEGROUP cascade constraints
/


drop table STARTDOWTIME cascade constraints
/


drop table STARTDOMTIME cascade constraints
/


drop table STARTDELTATIME cascade constraints
/


drop table STARTDATETIME cascade constraints
/


drop table STARTABSOLUTETIME cascade constraints
/


drop table ROUTE cascade constraints
/


drop table PROGRAM cascade constraints
/


drop table PORTTIMING cascade constraints
/


drop table PORTTERMINALSERVER cascade constraints
/


drop table PORTSTATISTICS cascade constraints
/


drop table STOPDURATIONTIME cascade constraints
/


drop table PORTSETTINGS cascade constraints
/


drop table PORTRADIOSETTINGS cascade constraints
/


drop table PORTLOCALSERIAL cascade constraints
/


drop table PORTDIALUPMODEM cascade constraints
/


drop table MCSCHEDULE cascade constraints
/


drop table LOGIC cascade constraints
/


drop table GRAPHDEFINITION cascade constraints
/


drop table DYNAMICDEVICESCANDATA cascade constraints
/


drop table DISPLAYCOLUMNS cascade constraints
/


drop table DISPLAY cascade constraints
/


drop table DEVICETAPPAGINGSETTINGS cascade constraints
/


drop table DEVICESTATISTICS cascade constraints
/


drop table DEVICESCANRATE cascade constraints
/


drop table DEVICEMETERGROUP cascade constraints
/


drop table DEVICEMCTIEDPORT cascade constraints
/


drop table DEVICELOADPROFILE cascade constraints
/


drop table DEVICEIED cascade constraints
/


drop table DEVICEIDLCREMOTE cascade constraints
/


drop table DEVICEDIRECTCOMMSETTINGS cascade constraints
/


drop table DEVICEDIALUPSETTINGS cascade constraints
/


drop table DEVICECARRIERSETTINGS cascade constraints
/


drop table DEVICE2WAYFLAGS cascade constraints
/


drop table DEVICE cascade constraints
/


drop table COMMPORT cascade constraints
/


drop table COLUMNTYPE cascade constraints
/


drop view DISPLAY2WAYDATA_VIEW
/


drop view LMCurtailCustomerActivity_View
/


drop view POINTHISTORY
/


/*==============================================================*/
/* Table : COLUMNTYPE                                           */
/*==============================================================*/

create table COLUMNTYPE (
   TYPENUM              NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
         constraint SYS_C0013413 check ("NAME" IS NOT NULL),
   constraint SYS_C0013414 primary key (TYPENUM)
)
/


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

/*==============================================================*/
/* Table : COMMPORT                                             */
/*==============================================================*/

create table COMMPORT (
   PORTID               NUMBER                           not null,
   DESCRIPTION          VARCHAR2(20)                     not null
         constraint SYS_C0013104 check ("DESCRIPTION" IS NOT NULL),
   PORTTYPE             VARCHAR2(30)                     not null
         constraint SYS_C0013105 check ("PORTTYPE" IS NOT NULL),
   CURRENTSTATE         VARCHAR2(8)                      not null
         constraint SYS_C0013106 check ("CURRENTSTATE" IS NOT NULL),
   DISABLEFLAG          VARCHAR2(1)                      not null
         constraint SYS_C0013107 check ("DISABLEFLAG" IS NOT NULL),
   ALARMINHIBIT         VARCHAR2(1)                      not null
         constraint SYS_C0013108 check ("ALARMINHIBIT" IS NOT NULL),
   COMMONPROTOCOL       VARCHAR2(8)                      not null
         constraint SYS_C0013109 check ("COMMONPROTOCOL" IS NOT NULL),
   PERFORMTHRESHOLD     NUMBER                           not null
         constraint SYS_C0013110 check ("PERFORMTHRESHOLD" IS NOT NULL),
   PERFORMANCEALARM     VARCHAR2(1)                      not null
         constraint SYS_C0013111 check ("PERFORMANCEALARM" IS NOT NULL),
   constraint SYS_C0013112 primary key (PORTID)
)
/


/*==============================================================*/
/* Table : DEVICE                                               */
/*==============================================================*/

create table DEVICE (
   DEVICEID             NUMBER                           not null,
   NAME                 VARCHAR2(60)                     not null
         constraint SYS_C0013114 check ("NAME" IS NOT NULL),
   TYPE                 VARCHAR2(18)                     not null
         constraint SYS_C0013115 check ("TYPE" IS NOT NULL),
   CURRENTSTATE         VARCHAR2(8)                      not null
         constraint SYS_C0013116 check ("CURRENTSTATE" IS NOT NULL),
   DISABLEFLAG          VARCHAR2(1)                      not null
         constraint SYS_C0013117 check ("DISABLEFLAG" IS NOT NULL),
   ALARMINHIBIT         VARCHAR2(1)                      not null
         constraint SYS_C0013118 check ("ALARMINHIBIT" IS NOT NULL),
   CONTROLINHIBIT       VARCHAR2(1)                      not null
         constraint SYS_C0013119 check ("CONTROLINHIBIT" IS NOT NULL),
   CLASS                VARCHAR2(12)                     not null
         constraint SYS_C0013120 check ("CLASS" IS NOT NULL),
   constraint SYS_C0013121 primary key (DEVICEID)
)
/


INSERT into device values (0, 'System Device', 'System', 'Normal', 'N', 'N', 'N', 'System');

/*==============================================================*/
/* Table : DEVICE2WAYFLAGS                                      */
/*==============================================================*/

create table DEVICE2WAYFLAGS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013199 check ("DEVICEID" IS NOT NULL),
   MONTHLYSTATS         VARCHAR2(1)                      not null
         constraint SYS_C0013200 check ("MONTHLYSTATS" IS NOT NULL),
   TWENTYFOURHOURSTATS  VARCHAR2(1)                      not null
         constraint SYS_C0013201 check ("TWENTYFOURHOURSTATS" IS NOT NULL),
   HOURLYSTATS          VARCHAR2(1)                      not null
         constraint SYS_C0013202 check ("HOURLYSTATS" IS NOT NULL),
   FAILUREALARM         VARCHAR2(1)                      not null
         constraint SYS_C0013203 check ("FAILUREALARM" IS NOT NULL),
   PERFORMANCETHRESHOLD NUMBER                           not null
         constraint SYS_C0013204 check ("PERFORMANCETHRESHOLD" IS NOT NULL),
   PERFORMANCEALARM     VARCHAR2(1)                      not null
         constraint SYS_C0013205 check ("PERFORMANCEALARM" IS NOT NULL),
   PERFORMANCETWENTYFOURALARM VARCHAR2(1)                      not null
         constraint SYS_C0013206 check ("PERFORMANCETWENTYFOURALARM" IS NOT NULL),
   CONFIGURATIONNAME    VARCHAR2(14)                     not null
         constraint SYS_C0013207 check ("CONFIGURATIONNAME" IS NOT NULL),
   constraint SYS_C0013208 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICECARRIERSETTINGS                                */
/*==============================================================*/

create table DEVICECARRIERSETTINGS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013214 check ("DEVICEID" IS NOT NULL),
   ADDRESS              NUMBER                           not null
         constraint SYS_C0013215 check ("ADDRESS" IS NOT NULL),
   constraint SYS_C0013216 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICEDIALUPSETTINGS                                 */
/*==============================================================*/

create table DEVICEDIALUPSETTINGS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013188 check ("DEVICEID" IS NOT NULL),
   PHONENUMBER          VARCHAR2(40)                     not null
         constraint SYS_C0013189 check ("PHONENUMBER" IS NOT NULL),
   MINCONNECTTIME       NUMBER                           not null
         constraint SYS_C0013190 check ("MINCONNECTTIME" IS NOT NULL),
   MAXCONNECTTIME       NUMBER                           not null
         constraint SYS_C0013191 check ("MAXCONNECTTIME" IS NOT NULL),
   LINESETTINGS         VARCHAR2(4)                      not null
         constraint SYS_C0013192 check ("LINESETTINGS" IS NOT NULL),
   constraint SYS_C0013193 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICEDIRECTCOMMSETTINGS                             */
/*==============================================================*/

create table DEVICEDIRECTCOMMSETTINGS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013184 check ("DEVICEID" IS NOT NULL),
   PORTID               NUMBER                           not null
         constraint SYS_C0013185 check ("PORTID" IS NOT NULL),
   constraint SYS_C0013186 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013187 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : DEVICEIDLCREMOTE                                     */
/*==============================================================*/

create table DEVICEIDLCREMOTE (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013238 check ("DEVICEID" IS NOT NULL),
   ADDRESS              NUMBER                           not null
         constraint SYS_C0013239 check ("ADDRESS" IS NOT NULL),
   POSTCOMMWAIT         NUMBER                           not null
         constraint SYS_C0013240 check ("POSTCOMMWAIT" IS NOT NULL),
   constraint SYS_C0013241 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICEIED                                            */
/*==============================================================*/

create table DEVICEIED (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013242 check ("DEVICEID" IS NOT NULL),
   PASSWORD             VARCHAR2(20)                     not null
         constraint SYS_C0013243 check ("PASSWORD" IS NOT NULL),
   SLAVEADDRESS         VARCHAR2(20)                     not null
         constraint SYS_C0013244 check ("SLAVEADDRESS" IS NOT NULL),
   constraint SYS_C0013245 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICELOADPROFILE                                    */
/*==============================================================*/

create table DEVICELOADPROFILE (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013230 check ("DEVICEID" IS NOT NULL),
   LASTINTERVALDEMANDRATE NUMBER                           not null
         constraint SYS_C0013231 check ("LASTINTERVALDEMANDRATE" IS NOT NULL),
   LOADPROFILEDEMANDRATE NUMBER                           not null
         constraint SYS_C0013232 check ("LOADPROFILEDEMANDRATE" IS NOT NULL),
   LOADPROFILECOLLECTION VARCHAR2(4)                      not null
         constraint SYS_C0013233 check ("LOADPROFILECOLLECTION" IS NOT NULL),
   constraint SYS_C0013234 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICEMCTIEDPORT                                     */
/*==============================================================*/

create table DEVICEMCTIEDPORT (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013246 check ("DEVICEID" IS NOT NULL),
   CONNECTEDIED         VARCHAR2(20)                     not null
         constraint SYS_C0013247 check ("CONNECTEDIED" IS NOT NULL),
   IEDSCANRATE          NUMBER                           not null
         constraint SYS_C0013248 check ("IEDSCANRATE" IS NOT NULL),
   DEFAULTDATACLASS     NUMBER                           not null
         constraint SYS_C0013249 check ("DEFAULTDATACLASS" IS NOT NULL),
   DEFAULTDATAOFFSET    NUMBER                           not null
         constraint SYS_C0013250 check ("DEFAULTDATAOFFSET" IS NOT NULL),
   PASSWORD             VARCHAR2(6)                      not null
         constraint SYS_C0013251 check ("PASSWORD" IS NOT NULL),
   REALTIMESCAN         VARCHAR2(1)                      not null
         constraint SYS_C0013252 check ("REALTIMESCAN" IS NOT NULL),
   constraint SYS_C0013253 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICEMETERGROUP                                     */
/*==============================================================*/

create table DEVICEMETERGROUP (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013209 check ("DEVICEID" IS NOT NULL),
   CYCLEGROUP           VARCHAR2(20)                     not null
         constraint SYS_C0013210 check ("CYCLEGROUP" IS NOT NULL),
   AREACODEGROUP        VARCHAR2(20)                     not null
         constraint SYS_C0013211 check ("AREACODEGROUP" IS NOT NULL),
   METERNUMBER          VARCHAR2(15)                     not null
         constraint SYS_C0013212 check ("METERNUMBER" IS NOT NULL),
   constraint SYS_C0013213 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICESCANRATE                                       */
/*==============================================================*/

create table DEVICESCANRATE (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013194 check ("DEVICEID" IS NOT NULL),
   SCANTYPE             VARCHAR2(20)                     not null
         constraint SYS_C0013195 check ("SCANTYPE" IS NOT NULL),
   INTERVALRATE         NUMBER                           not null
         constraint SYS_C0013196 check ("INTERVALRATE" IS NOT NULL),
   SCANGROUP            NUMBER                           not null
         constraint SYS_C0013197 check ("SCANGROUP" IS NOT NULL),
   constraint SYS_C0013198 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICESTATISTICS                                     */
/*==============================================================*/

create table DEVICESTATISTICS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013221 check ("DEVICEID" IS NOT NULL),
   STATISTICTYPE        VARCHAR2(16)                     not null
         constraint SYS_C0013222 check ("STATISTICTYPE" IS NOT NULL),
   ATTEMPTS             NUMBER                           not null
         constraint SYS_C0013223 check ("ATTEMPTS" IS NOT NULL),
   COMLINEERRORS        NUMBER                           not null
         constraint SYS_C0013224 check ("COMLINEERRORS" IS NOT NULL),
   SYSTEMERRORS         NUMBER                           not null
         constraint SYS_C0013225 check ("SYSTEMERRORS" IS NOT NULL),
   DLCERRORS            NUMBER                           not null
         constraint SYS_C0013226 check ("DLCERRORS" IS NOT NULL),
   STARTDATETIME        DATE                             not null
         constraint SYS_C0013227 check ("STARTDATETIME" IS NOT NULL),
   STOPDATETIME         DATE                             not null
         constraint SYS_C0013228 check ("STOPDATETIME" IS NOT NULL),
   constraint SYS_C0013229 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DEVICETAPPAGINGSETTINGS                              */
/*==============================================================*/

create table DEVICETAPPAGINGSETTINGS (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013235 check ("DEVICEID" IS NOT NULL),
   PAGERNUMBER          VARCHAR2(20)                     not null
         constraint SYS_C0013236 check ("PAGERNUMBER" IS NOT NULL),
   constraint SYS_C0013237 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DISPLAY                                              */
/*==============================================================*/

create table DISPLAY (
   DISPLAYNUM           NUMBER                           not null
         constraint SYS_C0013409 check ("DISPLAYNUM" IS NOT NULL),
   NAME                 VARCHAR2(40)                     not null
         constraint SYS_C0013410 check ("NAME" IS NOT NULL),
   TYPE                 VARCHAR2(20)                     not null
         constraint SYS_C0013411 check ("TYPE" IS NOT NULL),
   TITLE                VARCHAR2(30),
   DESCRIPTION          VARCHAR2(200),
   constraint SYS_C0013412 primary key (DISPLAYNUM)
)
/


insert into display values(-1, 'Scheduler Client', 'Scheduler Client', 'Metering And Control Scheduler', 'com.cannontech.macs.gui.Scheduler');
insert into display values(-2, 'All Areas', 'Cap Control Client', 'Capacitor Bank Controller', 'com.cannontech.cbc.gui.StrategyReceiver');
insert into display values(-3, 'Load Control Client', 'Load Control Client', 'Load Control', 'com.cannontech.loadcontrol.gui.LoadControlMainPanel');

insert into display values(99, 'Basic User Created', 'Custom Displays', 'A Predefined Generic Display', 'This display is is used to show what a user created display looks like.');
insert into display values(1, 'Event Viewer', 'Alarms and Events', 'Current Event Viewer', 'This display will recieve current events as they happen in the system.');
insert into display values(2, 'Historical Viewer', 'Alarms and Events', 'Historical Event Viewer', 'This display will allow the user to select a range of dates and show the events that occured.');
insert into display values(3, 'Raw Point Viewer', 'Alarms and Events', 'Current Raw Point Viewer', 'This display will recieve current raw point updates as they happen in the system.');
insert into display values(4, 'All Alarms', 'Alarms and Events', 'Global Alarm Viewer', 'This display will recieve all alarm events as they happen in the system.');
insert into display values(5, 'Priority 1 Alarms', 'Alarms and Events', 'Priority 1 Alarm Viewer', 'This display will recieve all priority 1 alarm events as they happen in the system.');
insert into display values(6, 'Priority 2 Alarms', 'Alarms and Events', 'Priority 2 Alarm Viewer', 'This display will recieve all priority 2 alarm events as they happen in the system.');
insert into display values(7, 'Priority 3 Alarms', 'Alarms and Events', 'Priority 3 Alarm Viewer', 'This display will recieve all priority 3 alarm events as they happen in the system.');
insert into display values(8, 'Priority 4 Alarms', 'Alarms and Events', 'Priority 4 Alarm Viewer', 'This display will recieve all priority 4 alarm events as they happen in the system.');
insert into display values(9, 'Priority 5 Alarms', 'Alarms and Events', 'Priority 5 Alarm Viewer', 'This display will recieve all priority 5 alarm events as they happen in the system.');
insert into display values(10, 'Priority 6 Alarms', 'Alarms and Events', 'Priority 6 Alarm Viewer', 'This display will recieve all priority 6 alarm events as they happen in the system.');
insert into display values(11, 'Priority 7 Alarms', 'Alarms and Events', 'Priority 7 Alarm Viewer', 'This display will recieve all priority 7 alarm events as they happen in the system.');
insert into display values(12, 'Priority 8 Alarms', 'Alarms and Events', 'Priority 8 Alarm Viewer', 'This display will recieve all priority 8 alarm events as they happen in the system.');
insert into display values(13, 'Priority 9 Alarms', 'Alarms and Events', 'Priority 9 Alarm Viewer', 'This display will recieve all priority 9 alarm events as they happen in the system.');
insert into display values(14, 'Priority 10 Alarms', 'Alarms and Events', 'Priority 10 Alarm Viewer', 'This display will recieve all priority 10 alarm events as they happen in the system.');

/*==============================================================*/
/* Table : DISPLAYCOLUMNS                                       */
/*==============================================================*/

create table DISPLAYCOLUMNS (
   DISPLAYNUM           NUMBER                           not null,
   TITLE                VARCHAR2(50),
   TYPENUM              NUMBER                           not null
         constraint SYS_C0013415 check ("TYPENUM" IS NOT NULL),
   ORDERING             NUMBER                           not null
         constraint SYS_C0013416 check ("ORDERING" IS NOT NULL),
   WIDTH                NUMBER                           not null
         constraint SYS_C0013417 check ("WIDTH" IS NOT NULL),
   constraint SYS_C0013418 foreign key (DISPLAYNUM)
         references DISPLAY (DISPLAYNUM),
   constraint SYS_C0013419 foreign key (TYPENUM)
         references COLUMNTYPE (TYPENUM)
)
/


insert into displaycolumns values(99, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(99, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(99, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(99, 'Value', 9, 4, 160 );

insert into displaycolumns values(1, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(1, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(1, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(1, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(1, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(1, 'User Name', 8, 6, 35 );

insert into displaycolumns values(2, 'Time Stamp', 11, 1, 60 );
insert into displaycolumns values(2, 'Device Name', 5, 2, 70 );
insert into displaycolumns values(2, 'Point Name', 2, 3, 70 );
insert into displaycolumns values(2, 'Text Message', 12, 4, 180 );
insert into displaycolumns values(2, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(2, 'User Name', 8, 6, 35 );

insert into displaycolumns values(3, 'Time Stamp', 11, 1, 70 );
insert into displaycolumns values(3, 'Device Name', 5, 2, 60 );
insert into displaycolumns values(3, 'Point Name', 2, 3, 60 );
insert into displaycolumns values(3, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(3, 'Additional Info', 10, 5, 200 );
insert into displaycolumns values(3, 'User Name', 8, 6, 35 );

insert into displaycolumns values(4, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(4, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(4, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(4, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(4, 'User Name', 8, 5, 50 );

insert into displaycolumns values(5, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(5, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(5, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(5, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(5, 'User Name', 8, 5, 50 );
insert into displaycolumns values(6, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(6, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(6, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(6, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(6, 'User Name', 8, 5, 50 );
insert into displaycolumns values(7, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(7, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(7, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(7, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(7, 'User Name', 8, 5, 50 );
insert into displaycolumns values(8, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(8, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(8, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(8, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(8, 'User Name', 8, 5, 50 );
insert into displaycolumns values(9, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(9, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(9, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(9, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(9, 'User Name', 8, 5, 50 );
insert into displaycolumns values(10, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(10, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(10, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(10, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(10, 'User Name', 8, 5, 50 );
insert into displaycolumns values(11, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(11, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(11, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(11, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(11, 'User Name', 8, 5, 50 );
insert into displaycolumns values(12, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(12, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(12, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(12, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(12, 'User Name', 8, 5, 50 );
insert into displaycolumns values(13, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(13, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(13, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(13, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(13, 'User Name', 8, 5, 50 );
insert into displaycolumns values(14, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(14, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(14, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(14, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(14, 'User Name', 8, 5, 50 );

/*==============================================================*/
/* Table : DYNAMICDEVICESCANDATA                                */
/*==============================================================*/

create table DYNAMICDEVICESCANDATA (
   DEVICEID             NUMBER,
   LASTFREEZETIME       DATE                             not null
         constraint SYS_C0015130 check ("LASTFREEZETIME" IS NOT NULL),
   PREVFREEZETIME       DATE                             not null
         constraint SYS_C0015131 check ("PREVFREEZETIME" IS NOT NULL),
   LASTLPTIME           DATE                             not null
         constraint SYS_C0015132 check ("LASTLPTIME" IS NOT NULL),
   LASTFREEZENUMBER     NUMBER                           not null
         constraint SYS_C0015133 check ("LASTFREEZENUMBER" IS NOT NULL),
   PREVFREEZENUMBER     NUMBER                           not null
         constraint SYS_C0015134 check ("PREVFREEZENUMBER" IS NOT NULL),
   NEXTSCAN0            DATE                             not null
         constraint SYS_C0015135 check ("NEXTSCAN0" IS NOT NULL),
   NEXTSCAN1            DATE                             not null
         constraint SYS_C0015136 check ("NEXTSCAN1" IS NOT NULL),
   NEXTSCAN2            DATE                             not null
         constraint SYS_C0015137 check ("NEXTSCAN2" IS NOT NULL),
   NEXTSCAN3            DATE                             not null
         constraint SYS_C0015138 check ("NEXTSCAN3" IS NOT NULL),
   constraint SYS_C0015139 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : GRAPHDEFINITION                                      */
/*==============================================================*/

create table GRAPHDEFINITION (
   GRAPHDEFINITIONID    NUMBER                           not null,
   NAME                 VARCHAR2(40)                     not null
         constraint SYS_C0015108 check ("NAME" IS NOT NULL),
   AutoScaleTimeAxis    CHAR(1)                          not null,
   AutoScaleLeftAxis    CHAR(1)                          not null,
   AutoScaleRightAxis   CHAR(1)                          not null,
   STARTDATE            DATE                             not null,
   STOPDATE             DATE                             not null,
   LeftMin              FLOAT                            not null,
   LeftMax              FLOAT                            not null,
   RightMin             FLOAT                            not null,
   RightMax             FLOAT                            not null,
   Type                 CHAR(1)                          not null,
   constraint SYS_C0015109 primary key (GRAPHDEFINITIONID)
)
/


/*==============================================================*/
/* Table : LOGIC                                                */
/*==============================================================*/

create table LOGIC (
   LOGICID              NUMBER                           not null,
   LOGICNAME            VARCHAR2(20)                     not null
         constraint SYS_C0013441 check ("LOGICNAME" IS NOT NULL),
   PERIODICRATE         NUMBER                           not null
         constraint SYS_C0013442 check ("PERIODICRATE" IS NOT NULL),
   STATEFLAG            VARCHAR2(10)                     not null
         constraint SYS_C0013443 check ("STATEFLAG" IS NOT NULL),
   SCRIPTNAME           VARCHAR2(20)                     not null
         constraint SYS_C0013444 check ("SCRIPTNAME" IS NOT NULL),
   constraint SYS_C0013445 primary key (LOGICID)
)
/


/*==============================================================*/
/* Table : MCSCHEDULE                                           */
/*==============================================================*/

create table MCSCHEDULE (
   SCHEDULEID           NUMBER                           not null,
   SCHEDULENAME         VARCHAR2(20)                     not null
         constraint SYS_C0013376 check ("SCHEDULENAME" IS NOT NULL),
   COMMANDFILE          VARCHAR2(48)                     not null
         constraint SYS_C0013377 check ("COMMANDFILE" IS NOT NULL),
   CURRENTSTATE         VARCHAR2(12)                     not null
         constraint SYS_C0013378 check ("CURRENTSTATE" IS NOT NULL),
   STARTTYPE            VARCHAR2(20)                     not null
         constraint SYS_C0013379 check ("STARTTYPE" IS NOT NULL),
   STOPTYPE             VARCHAR2(20)                     not null
         constraint SYS_C0013380 check ("STOPTYPE" IS NOT NULL),
   LASTRUNTIME          DATE                             not null
         constraint SYS_C0013381 check ("LASTRUNTIME" IS NOT NULL),
   LASTRUNSTATUS        VARCHAR2(12)                     not null
         constraint SYS_C0013382 check ("LASTRUNSTATUS" IS NOT NULL),
   constraint SYS_C0013383 primary key (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : PORTDIALUPMODEM                                      */
/*==============================================================*/

create table PORTDIALUPMODEM (
   PORTID               NUMBER                           not null
         constraint SYS_C0013170 check ("PORTID" IS NOT NULL),
   MODEMTYPE            VARCHAR2(30)                     not null
         constraint SYS_C0013171 check ("MODEMTYPE" IS NOT NULL),
   INITIALIZATIONSTRING VARCHAR2(50)                     not null
         constraint SYS_C0013172 check ("INITIALIZATIONSTRING" IS NOT NULL),
   PREFIXNUMBER         VARCHAR2(10)                     not null
         constraint SYS_C0013173 check ("PREFIXNUMBER" IS NOT NULL),
   SUFFIXNUMBER         VARCHAR2(10)                     not null
         constraint SYS_C0013174 check ("SUFFIXNUMBER" IS NOT NULL),
   constraint SYS_C0013175 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PORTLOCALSERIAL                                      */
/*==============================================================*/

create table PORTLOCALSERIAL (
   PORTID               NUMBER                           not null
         constraint SYS_C0013145 check ("PORTID" IS NOT NULL),
   PHYSICALPORT         VARCHAR2(8)                      not null
         constraint SYS_C0013146 check ("PHYSICALPORT" IS NOT NULL),
   constraint SYS_C0013147 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PORTRADIOSETTINGS                                    */
/*==============================================================*/

create table PORTRADIOSETTINGS (
   PORTID               NUMBER                           not null
         constraint SYS_C0013164 check ("PORTID" IS NOT NULL),
   RTSTOTXWAITSAMED     NUMBER                           not null
         constraint SYS_C0013165 check ("RTSTOTXWAITSAMED" IS NOT NULL),
   RTSTOTXWAITDIFFD     NUMBER                           not null
         constraint SYS_C0013166 check ("RTSTOTXWAITDIFFD" IS NOT NULL),
   RADIOMASTERTAIL      NUMBER                           not null
         constraint SYS_C0013167 check ("RADIOMASTERTAIL" IS NOT NULL),
   REVERSERTS           NUMBER                           not null
         constraint SYS_C0013168 check ("REVERSERTS" IS NOT NULL),
   constraint SYS_C0013169 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PORTSETTINGS                                         */
/*==============================================================*/

create table PORTSETTINGS (
   PORTID               NUMBER                           not null
         constraint SYS_C0013152 check ("PORTID" IS NOT NULL),
   BAUDRATE             NUMBER                           not null
         constraint SYS_C0013153 check ("BAUDRATE" IS NOT NULL),
   CDWAIT               NUMBER                           not null
         constraint SYS_C0013154 check ("CDWAIT" IS NOT NULL),
   LINESETTINGS         VARCHAR2(4)                      not null
         constraint SYS_C0013155 check ("LINESETTINGS" IS NOT NULL),
   constraint SYS_C0013156 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : STOPDURATIONTIME                                     */
/*==============================================================*/

create table STOPDURATIONTIME (
   SCHEDULEID           NUMBER,
   DURATIONTIME         VARCHAR2(8)                      not null
         constraint SYS_C0013400 check ("DURATIONTIME" IS NOT NULL),
   constraint SYS_C0013401 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : PORTSTATISTICS                                       */
/*==============================================================*/

create table PORTSTATISTICS (
   PORTID               NUMBER                           not null
         constraint SYS_C0013176 check ("PORTID" IS NOT NULL),
   STATISTICTYPE        NUMBER                           not null
         constraint SYS_C0013177 check ("STATISTICTYPE" IS NOT NULL),
   ATTEMPTS             NUMBER                           not null
         constraint SYS_C0013178 check ("ATTEMPTS" IS NOT NULL),
   DATAERRORS           NUMBER                           not null
         constraint SYS_C0013179 check ("DATAERRORS" IS NOT NULL),
   SYSTEMERRORS         NUMBER                           not null
         constraint SYS_C0013180 check ("SYSTEMERRORS" IS NOT NULL),
   STARTDATETIME        DATE                             not null
         constraint SYS_C0013181 check ("STARTDATETIME" IS NOT NULL),
   STOPDATETIME         DATE                             not null
         constraint SYS_C0013182 check ("STOPDATETIME" IS NOT NULL),
   constraint SYS_C0013183 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PORTTERMINALSERVER                                   */
/*==============================================================*/

create table PORTTERMINALSERVER (
   PORTID               NUMBER                           not null
         constraint SYS_C0013148 check ("PORTID" IS NOT NULL),
   IPADDRESS            VARCHAR2(16)                     not null
         constraint SYS_C0013149 check ("IPADDRESS" IS NOT NULL),
   SOCKETPORTNUMBER     NUMBER                           not null
         constraint SYS_C0013150 check ("SOCKETPORTNUMBER" IS NOT NULL),
   constraint SYS_C0013151 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PORTTIMING                                           */
/*==============================================================*/

create table PORTTIMING (
   PORTID               NUMBER                           not null
         constraint SYS_C0013157 check ("PORTID" IS NOT NULL),
   PRETXWAIT            NUMBER                           not null
         constraint SYS_C0013158 check ("PRETXWAIT" IS NOT NULL),
   RTSTOTXWAIT          NUMBER                           not null
         constraint SYS_C0013159 check ("RTSTOTXWAIT" IS NOT NULL),
   POSTTXWAIT           NUMBER                           not null
         constraint SYS_C0013160 check ("POSTTXWAIT" IS NOT NULL),
   RECEIVEDATAWAIT      NUMBER                           not null
         constraint SYS_C0013161 check ("RECEIVEDATAWAIT" IS NOT NULL),
   EXTRATIMEOUT         NUMBER                           not null
         constraint SYS_C0013162 check ("EXTRATIMEOUT" IS NOT NULL),
   constraint SYS_C0013163 foreign key (PORTID)
         references COMMPORT (PORTID)
)
/


/*==============================================================*/
/* Table : PROGRAM                                              */
/*==============================================================*/

create table PROGRAM (
   PROGRAMID            NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
         constraint SYS_C0015116 check ("NAME" IS NOT NULL),
   STATE                VARCHAR2(20)                     not null
         constraint SYS_C0015117 check ("STATE" IS NOT NULL),
   STARTTIMESTAMP       DATE,
   STOPTIMESTAMP        DATE,
   TOTALCONTROLTIME     NUMBER                           not null
         constraint SYS_C0015118 check ("TOTALCONTROLTIME" IS NOT NULL),
   constraint SYS_C0015119 primary key (PROGRAMID)
)
/


/*==============================================================*/
/* Table : ROUTE                                                */
/*==============================================================*/

create table ROUTE (
   ROUTEID              NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
         constraint SYS_C0013123 check ("NAME" IS NOT NULL),
   TYPE                 VARCHAR2(20)                     not null
         constraint SYS_C0013124 check ("TYPE" IS NOT NULL),
   constraint SYS_C0013125 primary key (ROUTEID)
)
/


/*==============================================================*/
/* Table : STARTABSOLUTETIME                                    */
/*==============================================================*/

create table STARTABSOLUTETIME (
   SCHEDULEID           NUMBER,
   STARTTIME            VARCHAR2(8)                      not null
         constraint SYS_C0013384 check ("STARTTIME" IS NOT NULL),
   constraint SYS_C0013385 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : STARTDATETIME                                        */
/*==============================================================*/

create table STARTDATETIME (
   SCHEDULEID           NUMBER,
   MONTH                VARCHAR2(9)                      not null
         constraint SYS_C0013386 check ("MONTH" IS NOT NULL),
   DAYOFMONTH           NUMBER                           not null
         constraint SYS_C0013387 check ("DAYOFMONTH" IS NOT NULL),
   STARTTIME            VARCHAR2(8)                      not null
         constraint SYS_C0013388 check ("STARTTIME" IS NOT NULL),
   constraint SYS_C0013389 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : STARTDELTATIME                                       */
/*==============================================================*/

create table STARTDELTATIME (
   SCHEDULEID           NUMBER,
   DELTATIME            VARCHAR2(8)                      not null
         constraint SYS_C0013390 check ("DELTATIME" IS NOT NULL),
   constraint SYS_C0013391 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : STARTDOMTIME                                         */
/*==============================================================*/

create table STARTDOMTIME (
   SCHEDULEID           NUMBER,
   DAYOFMONTH           NUMBER                           not null
         constraint SYS_C0013392 check ("DAYOFMONTH" IS NOT NULL),
   STARTTIME            VARCHAR2(8)                      not null
         constraint SYS_C0013393 check ("STARTTIME" IS NOT NULL),
   constraint SYS_C0013394 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : STARTDOWTIME                                         */
/*==============================================================*/

create table STARTDOWTIME (
   SCHEDULEID           NUMBER,
   DAYOFWEEK            VARCHAR2(9)                      not null
         constraint SYS_C0013395 check ("DAYOFWEEK" IS NOT NULL),
   STARTTIME            VARCHAR2(8)                      not null
         constraint SYS_C0013396 check ("STARTTIME" IS NOT NULL),
   constraint SYS_C0013397 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : STATEGROUP                                           */
/*==============================================================*/

create table STATEGROUP (
   STATEGROUPID         NUMBER                           not null,
   NAME                 VARCHAR2(20)                     not null
         constraint SYS_C0013127 check ("NAME" IS NOT NULL),
   constraint SYS_C0013128 primary key (STATEGROUPID)
)
/


INSERT INTO StateGroup VALUES ( -1, 'DefaultAnalog' );
INSERT INTO StateGroup VALUES ( -2, 'DefaultAccumulator' );
INSERT INTO StateGroup VALUES ( -3, 'DefaultCalculated' );
INSERT INTO StateGroup VALUES (-5, 'Event Priority');
INSERT INTO StateGroup VALUES ( 0, 'SystemState' );
INSERT INTO StateGroup VALUES ( 1, 'TwoStateStatus' );
INSERT INTO StateGroup VALUES ( 2, 'ThreeStateStatus' );
INSERT INTO StateGroup VALUES ( 3, 'CapBankStatus' );

/*==============================================================*/
/* Table : STOPABSOLUTETIME                                     */
/*==============================================================*/

create table STOPABSOLUTETIME (
   SCHEDULEID           NUMBER,
   STOPTIME             VARCHAR2(8)                      not null
         constraint SYS_C0013398 check ("STOPTIME" IS NOT NULL),
   constraint SYS_C0013399 foreign key (SCHEDULEID)
         references MCSCHEDULE (SCHEDULEID)
)
/


/*==============================================================*/
/* Table : GroupRecipient                                       */
/*==============================================================*/

create table GroupRecipient (
   LocationID           NUMBER                           not null,
   LocationName         VARCHAR2(30)                     not null,
   EmailAddress         VARCHAR2(60)                     not null,
   EmailSendType        NUMBER                           not null,
   PagerNumber          VARCHAR2(20)                     not null,
   DisableFlag          CHAR(1)                          not null,
   constraint PK_GROUPRECIPIENT primary key (LocationID)
)
/


insert into GroupRecipient values(0,'(none)','(none)',1,'(none)','N');

/*==============================================================*/
/* Table : TEMPLATE                                             */
/*==============================================================*/

create table TEMPLATE (
   TEMPLATENUM          NUMBER                           not null
         constraint SYS_C0013423 check ("TEMPLATENUM" IS NOT NULL),
   NAME                 VARCHAR2(40)                     not null
         constraint SYS_C0013424 check ("NAME" IS NOT NULL),
   DESCRIPTION          VARCHAR2(200),
   constraint SYS_C0013425 primary key (TEMPLATENUM)
)
/


insert into template values( 1, 'Standard', 'First Standard Cannon Template');
insert into template values( 2, 'Standard - No PtName', 'Second Standard Cannon 
Template');
insert into template values( 3, 'Standard - No DevName', 'Third Standard Cannon 
Template');

/*==============================================================*/
/* Table : TEMPLATECOLUMNS                                      */
/*==============================================================*/

create table TEMPLATECOLUMNS (
   TEMPLATENUM          NUMBER                           not null,
   TITLE                VARCHAR2(50),
   TYPENUM              NUMBER                           not null
         constraint SYS_C0013426 check ("TYPENUM" IS NOT NULL),
   ORDERING             NUMBER                           not null
         constraint SYS_C0013427 check ("ORDERING" IS NOT NULL),
   WIDTH                NUMBER                           not null
         constraint SYS_C0013428 check ("WIDTH" IS NOT NULL),
   constraint SYS_C0013429 foreign key (TEMPLATENUM)
         references TEMPLATE (TEMPLATENUM),
   constraint SYS_C0013430 foreign key (TYPENUM)
         references COLUMNTYPE (TYPENUM)
)
/


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

/*==============================================================*/
/* Table : UNITMEASURE                                          */
/*==============================================================*/

create table UNITMEASURE (
   NAME                 VARCHAR2(8)                      not null,
   CALCTYPE             NUMBER                           not null
         constraint SYS_C0013343 check ("CALCTYPE" IS NOT NULL),
   constraint SYS_C0013344 primary key (NAME)
)
/


INSERT INTO UnitMeasure VALUES ( 'KW', 0 );
INSERT INTO UnitMeasure VALUES ( 'KWH', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVA', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVAR', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVAH', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVARH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MW', 0 );
INSERT INTO UnitMeasure VALUES ( 'MWH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVA', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVAR', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVAH', 0 );
INSERT INTO UnitMeasure VALUES ( 'MVARH', 0 );
INSERT INTO UnitMeasure VALUES ( 'Volts', 0 );
INSERT INTO UnitMeasure VALUES ( 'VA', 0 );
INSERT INTO UnitMeasure VALUES ( 'Temp-F', 0 );
INSERT INTO UnitMeasure VALUES ( 'Counts', 0 );
INSERT INTO UnitMeasure VALUES ( 'Temp-C', 0 );
INSERT INTO UnitMeasure VALUES ( 'Amps', 0 );
INSERT INTO UnitMeasure VALUES ( 'Gallons', 0 );
INSERT INTO UnitMeasure VALUES ( 'Gal/PM', 0 );
INSERT INTO UnitMeasure VALUES ( 'Watts', 0 );
INSERT INTO UnitMeasure VALUES ( 'Vars', 0 );
INSERT INTO UnitMeasure VALUES ( 'VoltAmps', 0 );
INSERT INTO UnitMeasure VALUES ( 'Degrees', 0 );
INSERT INTO UnitMeasure VALUES ( 'PF', 0 );
INSERT INTO UnitMeasure VALUES ( 'Percent', 0 );
INSERT INTO UnitMeasure VALUES ( '%', 0 );
INSERT INTO UnitMeasure VALUES ( 'Hours', 0 );
INSERT INTO UnitMeasure VALUES ( 'Minutes', 0 );
INSERT INTO UnitMeasure VALUES ( 'Seconds', 0 );
INSERT INTO UnitMeasure VALUES ( 'Watr-CFT', 0 );
INSERT INTO UnitMeasure VALUES ( 'GAS-CFT', 0 );
INSERT INTO UnitMeasure VALUES ( 'Feet', 0 );
INSERT INTO UnitMeasure VALUES ( 'Level', 0 );
INSERT INTO UnitMeasure VALUES ( 'PSI', 0 );
INSERT INTO UnitMeasure VALUES ( 'Ops.', 0 );
INSERT INTO UnitMeasure VALUES ( 'KVolts', 0 );

/*==============================================================*/
/* Table : USERINFO                                             */
/*==============================================================*/

create table USERINFO (
   USERID               NUMBER                           not null,
   USERNAME             VARCHAR2(20)                     not null
         constraint SYS_C0015103 check ("USERNAME" IS NOT NULL),
   PASSWORD             VARCHAR2(20)                     not null
         constraint SYS_C0015104 check ("PASSWORD" IS NOT NULL),
   DATABASEALIAS        VARCHAR2(20)                     not null
         constraint SYS_C0015105 check ("DATABASEALIAS" IS NOT NULL),
   LOGINCOUNT           NUMBER                           not null,
   LASTLOGIN            NUMBER                           not null,
   constraint SYS_C0015106 primary key (USERID)
)
/


/*==============================================================*/
/* Table : USERPROGRAM                                          */
/*==============================================================*/

create table USERPROGRAM (
   USERID               NUMBER,
   PROGRAMID            NUMBER,
   ATTRIB               NUMBER,
   constraint SYS_C0015121 foreign key (USERID)
         references USERINFO (USERID),
   constraint SYS_C0015122 foreign key (PROGRAMID)
         references PROGRAM (PROGRAMID)
)
/


/*==============================================================*/
/* Table : USERWEB                                              */
/*==============================================================*/

create table USERWEB (
   USERID               NUMBER                           not null
         constraint SYS_C0015125 check ("USERID" IS NOT NULL),
   HOMEURL              VARCHAR2(200),
   LOGO                 VARCHAR2(40),
   constraint SYS_C0015126 foreign key (USERID)
         references USERINFO (USERID)
)
/


/*==============================================================*/
/* Table : VERSACOMROUTE                                        */
/*==============================================================*/

create table VERSACOMROUTE (
   ROUTEID              NUMBER,
   UTILITYID            NUMBER                           not null
         constraint SYS_C0013276 check ("UTILITYID" IS NOT NULL),
   SECTIONADDRESS       NUMBER                           not null
         constraint SYS_C0013277 check ("SECTIONADDRESS" IS NOT NULL),
   CLASSADDRESS         NUMBER                           not null
         constraint SYS_C0013278 check ("CLASSADDRESS" IS NOT NULL),
   DIVISIONADDRESS      NUMBER                           not null
         constraint SYS_C0013279 check ("DIVISIONADDRESS" IS NOT NULL),
   BUSNUMBER            NUMBER                           not null
         constraint SYS_C0013280 check ("BUSNUMBER" IS NOT NULL),
   AMPCARDSET           NUMBER                           not null
         constraint SYS_C0013281 check ("AMPCARDSET" IS NOT NULL),
   constraint FK_VERSACOM_ROUTE_VER_ROUTE foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : NotificationGroup                                    */
/*==============================================================*/

create table NotificationGroup (
   NotificationGroupID  NUMBER                           not null,
   GroupName            VARCHAR2(40)                     not null,
   EmailSubject         VARCHAR2(30)                     not null,
   EmailFromAddress     VARCHAR2(40)                     not null,
   EmailMessage         VARCHAR2(160)                    not null,
   NumericPagerMessage  VARCHAR2(14)                     not null,
   DisableFlag          CHAR(1)                          not null,
   constraint PK_NOTIFICATIONGROUP primary key (NotificationGroupID)
)
/


insert into notificationgroup values(1,'None','None','None','None','None','N');

/*==============================================================*/
/* Table : NotificationDestination                              */
/*==============================================================*/

create table NotificationDestination (
   DestinationOrder     NUMBER                           not null,
   NotificationGroupID  NUMBER                           not null,
   LocationID           NUMBER                           not null,
   constraint PK_NOTIFICATIONDESTINATION primary key (NotificationGroupID, DestinationOrder),
   constraint FK_NOTIFICAGROUP_NOTIFICATDEST foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID),
   constraint FK_NOTIFICA_REFERENCE_GROUPREC foreign key (LocationID)
         references GroupRecipient (LocationID)
)
/


/*==============================================================*/
/* Table : UserGraph                                            */
/*==============================================================*/

create table UserGraph (
   UserID               NUMBER                           not null,
   GraphDefinitionID    NUMBER                           not null,
   constraint FK_USERGRAP_USERGRAPH_USERINFO foreign key (UserID)
         references USERINFO (USERID),
   constraint FK_USERGRAP_USERGRAPH_GRAPHDEF foreign key (GraphDefinitionID)
         references GRAPHDEFINITION (GRAPHDEFINITIONID)
)
/


/*==============================================================*/
/* Table : AlarmState                                           */
/*==============================================================*/

create table AlarmState (
   AlarmStateID         NUMBER                           not null,
   AlarmStateName       VARCHAR2(40)                     not null,
   NotificationGroupID  NUMBER                           not null,
   constraint PK_ALARMSTATE primary key (AlarmStateID),
   constraint FK_ALARMSTA_ALARMSTAT_NOTIFICA foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID)
)
/


insert into alarmstate values(1,'None',1);
insert into alarmstate values(2,'Priority 1',1);
insert into alarmstate values(3,'Priority 2',1);
insert into alarmstate values(4,'Priority 3',1);
insert into alarmstate values(5,'Priority 4',1);
insert into alarmstate values(6,'Priority 5',1);
insert into alarmstate values(7,'Priority 6',1);
insert into alarmstate values(8,'Priority 7',1);
insert into alarmstate values(9,'Priority 8',1);
insert into alarmstate values(10,'Priority 9',1);
insert into alarmstate values(11,'Priority 10',1);

/*==============================================================*/
/* Table : MACSchedule                                          */
/*==============================================================*/

create table MACSchedule (
   ScheduleID           INTEGER                          not null,
   ScheduleName         VARCHAR2(50)                     not null,
   CategoryName         VARCHAR2(20)                     not null,
   ScheduleType         VARCHAR2(12)                     not null,
   HolidayScheduleID    INTEGER,
   CommandFile          VARCHAR2(180),
   CurrentState         VARCHAR2(12)                     not null,
   StartPolicy          VARCHAR2(20)                     not null,
   StopPolicy           VARCHAR2(20)                     not null,
   LastRunTime          DATE,
   LastRunStatus        VARCHAR2(12),
   StartDay             INTEGER,
   StartMonth           INTEGER,
   StartYear            INTEGER,
   StartTime            VARCHAR2(8),
   StopTime             VARCHAR2(8),
   ValidWeekDays        CHAR(8),
   Duration             INTEGER,
   ManualStartTime      DATE,
   ManualStopTime       DATE,
   constraint PK_MACSCHEDULE primary key (ScheduleID)
)
/


/*==============================================================*/
/* Table : MACSimpleSchedule                                    */
/*==============================================================*/

create table MACSimpleSchedule (
   ScheduleID           INTEGER                          not null,
   TargetSelect         VARCHAR2(40),
   StartCommand         VARCHAR2(120),
   StopCommand          VARCHAR2(120),
   RepeatInterval       INTEGER,
   constraint PK_MACSIMPLESCHEDULE primary key (ScheduleID),
   constraint FK_MACSIMPLE_MACSCHED_ID foreign key (ScheduleID)
         references MACSchedule (ScheduleID)
)
/


/*==============================================================*/
/* Table : LMCONTROLAREA                                        */
/*==============================================================*/

create table LMCONTROLAREA (
   DEVICEID             NUMBER                           not null,
   DEFOPERATIONALSTATE  VARCHAR2(20)                     not null,
   CONTROLINTERVAL      NUMBER                           not null,
   MINRESPONSETIME      NUMBER                           not null,
   DEFDAILYSTARTTIME    NUMBER                           not null,
   DEFDAILYSTOPTIME     NUMBER                           not null,
   REQUIREALLTRIGGERSACTIVEFLAG VARCHAR2(1)                      not null,
   constraint PK_LMCONTROLAREA primary key (DEVICEID),
   constraint FK_Device_LMCctrlArea foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMPROGRAM                                            */
/*==============================================================*/

create table LMPROGRAM (
   DEVICEID             NUMBER                           not null,
   CONTROLTYPE          VARCHAR2(20)                     not null,
   AVAILABLESEASONS     VARCHAR2(4)                      not null,
   AVAILABLEWEEKDAYS    VARCHAR2(8)                      not null,
   MAXHOURSDAILY        NUMBER                           not null,
   MAXHOURSMONTHLY      NUMBER                           not null,
   MAXHOURSSEASONAL     NUMBER                           not null,
   MAXHOURSANNUALLY     NUMBER                           not null,
   MINACTIVATETIME      NUMBER                           not null,
   MINRESTARTTIME       NUMBER                           not null,
   constraint PK_LMPROGRAM primary key (DEVICEID),
   constraint FK_Device_LMProgram foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMProgramControlWindow                               */
/*==============================================================*/

create table LMProgramControlWindow (
   DeviceID             NUMBER                           not null,
   WindowNumber         NUMBER                           not null,
   AvailableStartTime   NUMBER                           not null,
   AvailableStopTime    NUMBER                           not null,
   constraint FK_LMPrg_LMPrgCntWind foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMProgramDirect                                      */
/*==============================================================*/

create table LMProgramDirect (
   DeviceID             NUMBER                           not null,
   GroupSelectionMethod VARCHAR2(30)                     not null,
   constraint PK_LMPROGRAMDIRECT primary key (DeviceID),
   constraint FK_LMPrg_LMPrgDirect foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMProgramDirectGear                                  */
/*==============================================================*/

create table LMProgramDirectGear (
   DeviceID             NUMBER                           not null,
   GearName             VARCHAR2(30)                     not null,
   GearNumber           NUMBER                           not null,
   ControlMethod        VARCHAR2(30)                     not null,
   MethodRate           NUMBER                           not null,
   MethodPeriod         NUMBER                           not null,
   MethodRateCount      NUMBER                           not null,
   CycleRefreshRate     NUMBER                           not null,
   MethodStopType       VARCHAR2(20)                     not null,
   ChangeCondition      VARCHAR2(24)                     not null,
   ChangeDuration       NUMBER                           not null,
   ChangePriority       NUMBER                           not null,
   ChangeTriggerNumber  NUMBER                           not null,
   ChangeTriggerOffset  FLOAT                            not null,
   PercentReduction     NUMBER                           not null,
   constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
         references LMProgramDirect (DeviceID)
)
/


/*==============================================================*/
/* Table : DynamicLMControlArea                                 */
/*==============================================================*/

create table DynamicLMControlArea (
   DeviceID             NUMBER                           not null,
   NextCheckTime        DATE                             not null,
   NewPointDataReceivedFlag CHAR(1)                          not null,
   UpdatedFlag          CHAR(1)                          not null,
   ControlAreaState     NUMBER                           not null,
   CurrentPriority      NUMBER                           not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMCONTROLAREA primary key (DeviceID),
   constraint FK_LMCntlAr_DynLMCntAr foreign key (DeviceID)
         references LMCONTROLAREA (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMGroup                                              */
/*==============================================================*/

create table LMGroup (
   DeviceID             NUMBER                           not null,
   KWCapacity           FLOAT                            not null,
   RecordControlHistoryFlag VARCHAR2(1)                      not null,
   constraint PK_LMGROUP primary key (DeviceID),
   constraint FK_Device_LMGrpBase foreign key (DeviceID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMProgramCurtailment                                 */
/*==============================================================*/

create table LMProgramCurtailment (
   DeviceID             NUMBER                           not null,
   MinNotifyTime        NUMBER                           not null,
   Heading              VARCHAR2(40)                     not null,
   MessageHeader        VARCHAR2(160)                    not null,
   MessageFooter        VARCHAR2(160)                    not null,
   AckTimeLimit         NUMBER                           not null,
   CanceledMsg          VARCHAR2(80)                   default 'THIS CURTAILMENT HAS BEEN CANCELED, PLEASE DISREGARD.'  not null,
   StoppedEarlyMsg      VARCHAR2(80)                   default 'THIS CURTAILMENT HAS STOPPED EARLY, YOU MAY RESUME NORMAL OPERATIONS.'  not null,
   constraint PK_LMPROGRAMCURTAILMENT primary key (DeviceID),
   constraint FK_LMPrg_LMPrgCurt foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMCurtailProgramActivity                             */
/*==============================================================*/

create table LMCurtailProgramActivity (
   DeviceID             NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   ActionDateTime       DATE                             not null,
   NotificationDateTime DATE                             not null,
   CurtailmentStartTime DATE                             not null,
   CurtailmentStopTime  DATE                             not null,
   RunStatus            VARCHAR2(20)                     not null,
   AdditionalInfo       VARCHAR2(100)                    not null,
   constraint PK_LMCURTAILPROGRAMACTIVITY primary key (CurtailReferenceID),
   constraint FK_LMPrgCrt_LMCrlPAct foreign key (DeviceID)
         references LMProgramCurtailment (DeviceID)
)
/


/*==============================================================*/
/* Table : CustomerAddress                                      */
/*==============================================================*/

create table CustomerAddress (
   AddressID            NUMBER                           not null,
   LocationAddress1     VARCHAR2(40)                     not null,
   LocationAddress2     VARCHAR2(40)                     not null,
   CityName             VARCHAR2(32)                     not null,
   StateCode            CHAR(2)                          not null,
   ZipCode              VARCHAR2(12)                     not null,
   constraint PK_CUSTOMERADDRESS primary key (AddressID)
)
/


/*==============================================================*/
/* Table : CustomerLogin                                        */
/*==============================================================*/

create table CustomerLogin (
   LogInID              NUMBER                           not null,
   UserName             VARCHAR2(20)                     not null,
   Password             VARCHAR2(20)                     not null,
   LoginType            VARCHAR2(25)                     not null,
   LoginCount           NUMBER                           not null,
   LastLogin            DATE                             not null,
   Status               VARCHAR2(20)                     not null,
   constraint PK_CUSTOMERLOGIN primary key (LogInID)
)
/


insert into CustomerLogin(LogInID,UserName,Password,LoginType,LoginCount,LastLogin,Status)
values (-1,'(none)','(none)','(none)',0,'01-JAN-1990', 'Disabled');

/*==============================================================*/
/* Table : DynamicLMProgramDirect                               */
/*==============================================================*/

create table DynamicLMProgramDirect (
   DeviceID             NUMBER                           not null,
   CurrentGearNumber    NUMBER                           not null,
   LastGroupControlled  NUMBER                           not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMPROGRAMDIRECT primary key (DeviceID),
   constraint FK_DYNAMICL_LMPROGDIR_LMPROGRA foreign key (DeviceID)
         references LMProgramDirect (DeviceID)
)
/


/*==============================================================*/
/* Table : DynamicLMGroup                                       */
/*==============================================================*/

create table DynamicLMGroup (
   DeviceID             NUMBER                           not null,
   GroupControlState    NUMBER                           not null,
   CurrentHoursDaily    NUMBER                           not null,
   CurrentHoursMonthly  NUMBER                           not null,
   CurrentHoursSeasonal NUMBER                           not null,
   CurrentHoursAnnually NUMBER                           not null,
   LastControlSent      DATE                             not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMGROUP primary key (DeviceID),
   constraint FK_LMGrp_DynLmGrp foreign key (DeviceID)
         references LMGroup (DeviceID)
)
/


/*==============================================================*/
/* Table : DynamicLMProgram                                     */
/*==============================================================*/

create table DynamicLMProgram (
   DeviceID             NUMBER                           not null,
   ProgramState         NUMBER                           not null,
   ReductionTotal       FLOAT                            not null,
   StartedControlling   DATE                             not null,
   LastControlSent      DATE                             not null,
   ManualControlReceivedFlag CHAR(1)                          not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICLMPROGRAM primary key (DeviceID),
   constraint FK_LMProg_DynLMPrg foreign key (DeviceID)
         references LMPROGRAM (DEVICEID)
)
/


/*==============================================================*/
/* Table : STATE                                                */
/*==============================================================*/

create table STATE (
   STATEGROUPID         NUMBER                           not null
         constraint SYS_C0013337 check ("STATEGROUPID" IS NOT NULL),
   RAWSTATE             NUMBER                           not null
         constraint SYS_C0013338 check ("RAWSTATE" IS NOT NULL),
   TEXT                 VARCHAR2(20)                     not null
         constraint SYS_C0013339 check ("TEXT" IS NOT NULL),
   FOREGROUNDCOLOR      NUMBER                           not null
         constraint SYS_C0013340 check ("FOREGROUNDCOLOR" IS NOT NULL),
   BACKGROUNDCOLOR      NUMBER                           not null
         constraint SYS_C0013341 check ("BACKGROUNDCOLOR" IS NOT NULL),
   constraint PK_STATE primary key (STATEGROUPID, RAWSTATE),
   constraint SYS_C0013342 foreign key (STATEGROUPID)
         references STATEGROUP (STATEGROUPID)
)
/


INSERT INTO State VALUES ( -1, 0, 'AnalogText', 0, 6 );
INSERT INTO State VALUES ( -2, 0, 'AccumulatorText', 0, 6 );
INSERT INTO State VALUES ( -3, 0, 'CalculatedText', 0, 6 );
INSERT INTO State VALUES ( 0, 0, 'SystemText', 0, 6 );
INSERT INTO State VALUES ( 1, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 1, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 1, 1, 'Closed', 1, 6 );
INSERT INTO State VALUES ( 2, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 2, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 2, 1, 'Closed', 1, 6 );
INSERT INTO State VALUES ( 2, 2, 'Unknown', 2, 6 );
INSERT INTO State VALUES ( 3, -1, 'Any', 2, 6 );
INSERT INTO State VALUES ( 3, 0, 'Open', 0, 6 );
INSERT INTO State VALUES ( 3, 1, 'Close', 1, 6 );
INSERT INTO State VALUES ( 3, 2, 'OpenQuestionable', 2, 6 );
INSERT INTO State VALUES ( 3, 3, 'CloseQuestionable', 3, 6 );
INSERT INTO State VALUES ( 3, 4, 'OpenFail', 4, 6 );
INSERT INTO State VALUES ( 3, 5, 'CloseFail', 5, 6 );
INSERT INTO State VALUES ( 3, 6, 'OpenPending', 7, 6 );
INSERT INTO State VALUES ( 3, 7, 'ClosePending', 8, 6 );
INSERT INTO State VALUES(-5, 0, 'Events', 2, 6);
INSERT INTO State VALUES(-5, 1, 'Priority 1', 1, 6);
INSERT INTO State VALUES(-5, 2, 'Priority 2', 4, 6);
INSERT INTO State VALUES(-5, 3, 'Priority 3', 0, 6);
INSERT INTO State VALUES(-5, 4, 'Priority 4', 7, 6);
INSERT INTO State VALUES(-5, 5, 'Priority 5', 8, 6);
INSERT INTO State VALUES(-5, 6, 'Priority 6', 5, 6);
INSERT INTO State VALUES(-5, 7, 'Priority 7', 3, 6);
INSERT INTO State VALUES(-5, 8, 'Priority 8', 2, 6);
INSERT INTO State VALUES(-5, 9, 'Priority 9', 6, 6);
INSERT INTO State VALUES(-5, 10, 'Priority 10', 9, 6);

/*==============================================================*/
/* Table : CARRIERROUTE                                         */
/*==============================================================*/

create table CARRIERROUTE (
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013259 check ("ROUTEID" IS NOT NULL),
   BUSNUMBER            NUMBER                           not null
         constraint SYS_C0013260 check ("BUSNUMBER" IS NOT NULL),
   AMPUSETYPE           VARCHAR2(20)                     not null
         constraint SYS_C0013261 check ("AMPUSETYPE" IS NOT NULL),
   CCUFIXBITS           NUMBER                           not null
         constraint SYS_C0013262 check ("CCUFIXBITS" IS NOT NULL),
   CCUVARIABLEBITS      NUMBER                           not null
         constraint SYS_C0013263 check ("CCUVARIABLEBITS" IS NOT NULL),
   constraint SYS_C0013264 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : MACROROUTE                                           */
/*==============================================================*/

create table MACROROUTE (
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013271 check ("ROUTEID" IS NOT NULL),
   SINGLEROUTEID        NUMBER                           not null
         constraint SYS_C0013272 check ("SINGLEROUTEID" IS NOT NULL),
   ROUTEORDER           NUMBER                           not null
         constraint SYS_C0013273 check ("ROUTEORDER" IS NOT NULL),
   constraint SYS_C0013274 foreign key (ROUTEID)
         references ROUTE (ROUTEID),
   constraint SYS_C0013275 foreign key (SINGLEROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : DEVICEROUTES                                         */
/*==============================================================*/

create table DEVICEROUTES (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013217 check ("DEVICEID" IS NOT NULL),
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013218 check ("ROUTEID" IS NOT NULL),
   constraint SYS_C0013219 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013220 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : POINT                                                */
/*==============================================================*/

create table POINT (
   POINTID              NUMBER                           not null,
   POINTTYPE            VARCHAR2(20)                     not null
         constraint SYS_C0013130 check ("POINTTYPE" IS NOT NULL),
   POINTNAME            VARCHAR2(60)                     not null
         constraint SYS_C0013131 check ("POINTNAME" IS NOT NULL),
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013132 check ("DEVICEID" IS NOT NULL),
   LOGICALGROUP         VARCHAR2(14)                     not null
         constraint SYS_C0013133 check ("LOGICALGROUP" IS NOT NULL),
   STATEGROUPID         NUMBER                           not null
         constraint SYS_C0013134 check ("STATEGROUPID" IS NOT NULL),
   SERVICEFLAG          VARCHAR2(1)                      not null
         constraint SYS_C0013135 check ("SERVICEFLAG" IS NOT NULL),
   ALARMINHIBIT         VARCHAR2(1)                      not null
         constraint SYS_C0013136 check ("ALARMINHIBIT" IS NOT NULL),
   ALARMCLASS           NUMBER                           not null
         constraint SYS_C0013137 check ("ALARMCLASS" IS NOT NULL),
   PSEUDOFLAG           VARCHAR2(1)                      not null
         constraint SYS_C0013138 check ("PSEUDOFLAG" IS NOT NULL),
   POINTOFFSET          NUMBER                           not null
         constraint SYS_C0013139 check ("POINTOFFSET" IS NOT NULL),
   ARCHIVETYPE          VARCHAR2(12)                     not null
         constraint SYS_C0013140 check ("ARCHIVETYPE" IS NOT NULL),
   ARCHIVEINTERVAL      NUMBER                           not null
         constraint SYS_C0013141 check ("ARCHIVEINTERVAL" IS NOT NULL),
   constraint SYS_C0013142 primary key (POINTID),
   constraint SYS_C0013143 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013144 foreign key (STATEGROUPID)
         references STATEGROUP (STATEGROUPID)
)
/


INSERT into point  values (0,   'System', 'System Point', 0, 'Default', 0, 'N', 'N', 0, 'S', 0  ,'None', 0);
INSERT into point  values (-1,  'System', 'Porter', 0, 'Default', 0, 'N', 'N', 0, 'S', 1  ,'None', 0);
INSERT into point  values (-2,  'System', 'Scanner', 0, 'Default', 0, 'N', 'N', 0, 'S', 2  ,'None', 0);
INSERT into point  values (-3,  'System', 'Dispatch', 0, 'Default', 0, 'N', 'N', 0, 'S', 3  ,'None', 0);
INSERT into point  values (-4,  'System', 'Macs', 0, 'Default', 0, 'N', 'N', 0, 'S', 4  ,'None', 0);
INSERT into point  values (-5,  'System', 'Cap Control', 0, 'Default', 0, 'N', 'N', 0, 'S', 5  ,'None', 0);
INSERT into point  values (-10, 'System', 'Load Management' , 0, 'Default', 0, 'N', 'N', 0, 'S', 10 ,'None', 0);

/*==============================================================*/
/* Table : POINTACCUMULATOR                                     */
/*==============================================================*/

create table POINTACCUMULATOR (
   POINTID              NUMBER                           not null
         constraint SYS_C0013313 check ("POINTID" IS NOT NULL),
   MULTIPLIER           FLOAT                            not null
         constraint SYS_C0013314 check ("MULTIPLIER" IS NOT NULL),
   DATAOFFSET           FLOAT                            not null
         constraint SYS_C0013315 check ("DATAOFFSET" IS NOT NULL),
   READINGTYPE          VARCHAR2(14)                     not null
         constraint SYS_C0013316 check ("READINGTYPE" IS NOT NULL),
   constraint SYS_C0013317 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : POINTANALOG                                          */
/*==============================================================*/

create table POINTANALOG (
   POINTID              NUMBER                           not null
         constraint SYS_C0013295 check ("POINTID" IS NOT NULL),
   DEADBAND             FLOAT                            not null
         constraint SYS_C0013296 check ("DEADBAND" IS NOT NULL),
   TRANSDUCERTYPE       VARCHAR2(14)                     not null
         constraint SYS_C0013297 check ("TRANSDUCERTYPE" IS NOT NULL),
   MULTIPLIER           FLOAT                            not null
         constraint SYS_C0013298 check ("MULTIPLIER" IS NOT NULL),
   DATAOFFSET           FLOAT                            not null
         constraint SYS_C0013299 check ("DATAOFFSET" IS NOT NULL),
   constraint SYS_C0013300 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : POINTCONTROL                                         */
/*==============================================================*/

create table POINTCONTROL (
   POINTID              NUMBER                           not null
         constraint SYS_C0013308 check ("POINTID" IS NOT NULL),
   CONTROLOFFSET        NUMBER                           not null
         constraint SYS_C0013309 check ("CONTROLOFFSET" IS NOT NULL),
   CLOSETIME1           NUMBER                           not null
         constraint SYS_C0013310 check ("CLOSETIME1" IS NOT NULL),
   CLOSETIME2           NUMBER                           not null
         constraint SYS_C0013311 check ("CLOSETIME2" IS NOT NULL),
   constraint SYS_C0013312 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : POINTDISPATCH                                        */
/*==============================================================*/

create table POINTDISPATCH (
   POINTID              NUMBER                           not null
         constraint SYS_C0013324 check ("POINTID" IS NOT NULL),
   TIMESTAMP            DATE                             not null
         constraint SYS_C0013325 check ("TIMESTAMP" IS NOT NULL),
   QUALITY              NUMBER                           not null
         constraint SYS_C0013326 check ("QUALITY" IS NOT NULL),
   VALUE                FLOAT                            not null
         constraint SYS_C0013327 check ("VALUE" IS NOT NULL),
   TAGS                 NUMBER                           not null
         constraint SYS_C0013328 check ("TAGS" IS NOT NULL),
   NEXTARCHIVE          DATE                             not null
         constraint SYS_C0013329 check ("NEXTARCHIVE" IS NOT NULL),
   STALECOUNT           NUMBER                           not null
         constraint SYS_C0013330 check ("STALECOUNT" IS NOT NULL),
   constraint SYS_C0013331 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : POINTLIMITS                                          */
/*==============================================================*/

create table POINTLIMITS (
   POINTID              NUMBER                           not null
         constraint SYS_C0013283 check ("POINTID" IS NOT NULL),
   LIMITNUMBER          NUMBER                           not null
         constraint SYS_C0013284 check ("LIMITNUMBER" IS NOT NULL),
   HIGHLIMIT            FLOAT                            not null
         constraint SYS_C0013285 check ("HIGHLIMIT" IS NOT NULL),
   LOWLIMIT             FLOAT                            not null
         constraint SYS_C0013286 check ("LOWLIMIT" IS NOT NULL),
   DATAFILTERTYPE       VARCHAR2(14)                     not null
         constraint SYS_C0013287 check ("DATAFILTERTYPE" IS NOT NULL),
   NAME                 VARCHAR2(14)                     not null
         constraint SYS_C0013288 check ("NAME" IS NOT NULL),
   LIMITDURATION        NUMBER                           not null,
   constraint SYS_C0013289 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : POINTSTATUS                                          */
/*==============================================================*/

create table POINTSTATUS (
   POINTID              NUMBER                           not null
         constraint SYS_C0013301 check ("POINTID" IS NOT NULL),
   NORMALSTATE          NUMBER                           not null
         constraint SYS_C0013302 check ("NORMALSTATE" IS NOT NULL),
   ALARMSTATE           NUMBER                           not null
         constraint SYS_C0013303 check ("ALARMSTATE" IS NOT NULL),
   INITIALSTATE         NUMBER                           not null
         constraint SYS_C0013304 check ("INITIALSTATE" IS NOT NULL),
   CONTROLTYPE          VARCHAR2(12)                     not null
         constraint SYS_C0013305 check ("CONTROLTYPE" IS NOT NULL),
   CONTROLINHIBIT       VARCHAR2(1)                      not null
         constraint SYS_C0013306 check ("CONTROLINHIBIT" IS NOT NULL),
   constraint SYS_C0013307 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : PointAlarming                                        */
/*==============================================================*/

create table PointAlarming (
   PointID              NUMBER                           not null,
   AlarmStates          VARCHAR2(32)                     not null,
   ExcludeNotifyStates  VARCHAR2(32)                     not null,
   NotifyOnAcknowledge  CHAR(1)                          not null,
   NotificationGroupID  NUMBER                           not null,
   LocationID           NUMBER                           not null,
   constraint PK_POINTALARMING primary key (PointID),
   constraint FK_POINTALAARM_POINT_POINTID foreign key (PointID)
         references POINT (POINTID),
   constraint FK_POINTALARMING foreign key (NotificationGroupID)
         references NotificationGroup (NotificationGroupID),
   constraint FK_POINTALA_POINTALAR_GROUPREC foreign key (LocationID)
         references GroupRecipient (LocationID)
)
/


insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, locationid)
	select pointid,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from point;

/*==============================================================*/
/* Table : POINTUNIT                                            */
/*==============================================================*/

create table POINTUNIT (
   POINTID              NUMBER                           not null
         constraint SYS_C0013290 check ("POINTID" IS NOT NULL),
   UNIT                 VARCHAR2(10)                     not null
         constraint SYS_C0013291 check ("UNIT" IS NOT NULL),
   LOGFREQUENCY         NUMBER                           not null
         constraint SYS_C0013292 check ("LOGFREQUENCY" IS NOT NULL),
   DEFAULTVALUE         FLOAT                            not null
         constraint SYS_C0013293 check ("DEFAULTVALUE" IS NOT NULL),
   DECIMALPLACES        NUMBER                           not null,
   constraint SYS_C0013294 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : DEVICECBC                                            */
/*==============================================================*/

create table DEVICECBC (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013456 check ("DEVICEID" IS NOT NULL),
   SERIALNUMBER         NUMBER                           not null
         constraint SYS_C0013457 check ("SERIALNUMBER" IS NOT NULL),
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013458 check ("ROUTEID" IS NOT NULL),
   constraint SYS_C0013459 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013460 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : CALCBASE                                             */
/*==============================================================*/

create table CALCBASE (
   POINTID              NUMBER                           not null
         constraint SYS_C0013431 check ("POINTID" IS NOT NULL),
   UPDATETYPE           VARCHAR2(16)                     not null
         constraint SYS_C0013432 check ("UPDATETYPE" IS NOT NULL),
   PERIODICRATE         NUMBER                           not null
         constraint SYS_C0013433 check ("PERIODICRATE" IS NOT NULL),
   constraint SYS_C0013434 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : CALCCOMPONENT                                        */
/*==============================================================*/

create table CALCCOMPONENT (
   POINTID              NUMBER                           not null
         constraint SYS_C0013435 check ("POINTID" IS NOT NULL),
   COMPONENTORDER       NUMBER                           not null
         constraint SYS_C0013436 check ("COMPONENTORDER" IS NOT NULL),
   COMPONENTTYPE        VARCHAR2(10)                     not null
         constraint SYS_C0013437 check ("COMPONENTTYPE" IS NOT NULL),
   COMPONENTPOINTID     NUMBER                           not null
         constraint SYS_C0013438 check ("COMPONENTPOINTID" IS NOT NULL),
   OPERATION            VARCHAR2(10),
   CONSTANT             FLOAT                            not null
         constraint SYS_C0013439 check ("CONSTANT" IS NOT NULL),
   FUNCTIONNAME         VARCHAR2(20),
   constraint SYS_C0013440 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : DISPLAY2WAYDATA                                      */
/*==============================================================*/

create table DISPLAY2WAYDATA (
   DISPLAYNUM           NUMBER                           not null,
   ORDERING             NUMBER                           not null
         constraint SYS_C0013421 check ("ORDERING" IS NOT NULL),
   POINTID              NUMBER,
   constraint SYS_C0013422 foreign key (DISPLAYNUM)
         references DISPLAY (DISPLAYNUM),
   constraint FK_DISPLAY2W_REF_POINT foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : SYSTEMLOG                                            */
/*==============================================================*/

create table SYSTEMLOG (
   LOGID                NUMBER                           not null,
   POINTID              NUMBER                           not null
         constraint SYS_C0013402 check ("POINTID" IS NOT NULL),
   DATETIME             DATE                             not null
         constraint SYS_C0013403 check ("DATETIME" IS NOT NULL),
   SOE_TAG              NUMBER                           not null
         constraint SYS_C0013404 check ("SOE_TAG" IS NOT NULL),
   TYPE                 NUMBER                           not null
         constraint SYS_C0013405 check ("TYPE" IS NOT NULL),
   PRIORITY             NUMBER                           not null
         constraint SYS_C0013406 check ("PRIORITY" IS NOT NULL),
   ACTION               VARCHAR2(60),
   DESCRIPTION          VARCHAR2(120),
   USERNAME             VARCHAR2(12),
   constraint SYS_C0013407 primary key (LOGID),
   constraint SYS_C0013408 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/

create table LMCONTROLAREATRIGGER (
   DEVICEID             NUMBER                           not null,
   TRIGGERNUMBER        NUMBER                           not null,
   TRIGGERTYPE          VARCHAR2(20)                     not null,
   POINTID              NUMBER                           not null,
   NORMALSTATE          NUMBER                           not null,
   THRESHOLD            FLOAT                            not null,
   PROJECTIONTYPE       VARCHAR2(14)                     not null,
   PROJECTIONPOINTS     NUMBER                           not null,
   PROJECTAHEADDURATION NUMBER                           not null,
   THRESHOLDKICKPERCENT NUMBER                           not null,
   MINRESTOREOFFSET     FLOAT                            not null,
   PEAKPOINTID          NUMBER                           not null,
   constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
         references LMCONTROLAREA (DEVICEID),
   constraint FK_Point_LMCntlArTrig foreign key (POINTID)
         references POINT (POINTID),
   constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : LMCONTROLAREAPROGRAM                                 */
/*==============================================================*/

create table LMCONTROLAREAPROGRAM (
   DEVICEID             NUMBER                           not null,
   LMPROGRAMDEVICEID    NUMBER                           not null,
   USERORDER            NUMBER                           not null,
   STOPORDER            NUMBER                           not null,
   DEFAULTPRIORITY      NUMBER                           not null,
   constraint FK_LMCntlArea_LMCntlArProg foreign key (DEVICEID)
         references LMCONTROLAREA (DEVICEID),
   constraint FK_LMPrg_LMCntlArProg foreign key (LMPROGRAMDEVICEID)
         references LMPROGRAM (DEVICEID)
)
/


/*==============================================================*/
/* Table : COMMUNICATIONROUTE                                   */
/*==============================================================*/

create table COMMUNICATIONROUTE (
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013254 check ("ROUTEID" IS NOT NULL),
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013255 check ("DEVICEID" IS NOT NULL),
   DEFAULTROUTE         VARCHAR2(1)                      not null
         constraint SYS_C0013256 check ("DEFAULTROUTE" IS NOT NULL),
   constraint SYS_C0013257 foreign key (ROUTEID)
         references ROUTE (ROUTEID),
   constraint SYS_C0013258 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : DYNAMICACCUMULATOR                                   */
/*==============================================================*/

create table DYNAMICACCUMULATOR (
   POINTID              NUMBER,
   PREVIOUSPULSES       NUMBER                           not null
         constraint SYS_C0015127 check ("PREVIOUSPULSES" IS NOT NULL),
   PRESENTPULSES        NUMBER                           not null
         constraint SYS_C0015128 check ("PRESENTPULSES" IS NOT NULL),
   constraint SYS_C0015129 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : CAPBANK                                              */
/*==============================================================*/

create table CAPBANK (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013446 check ("DEVICEID" IS NOT NULL),
   BANKADDRESS          VARCHAR2(40)                     not null
         constraint SYS_C0013447 check ("BANKADDRESS" IS NOT NULL),
   OPERATIONALSTATE     VARCHAR2(8)                      not null
         constraint SYS_C0013448 check ("OPERATIONALSTATE" IS NOT NULL),
   CONTROLPOINTID       NUMBER                           not null
         constraint SYS_C0013449 check ("CONTROLPOINTID" IS NOT NULL),
   BANKSIZE             FLOAT                            not null
         constraint SYS_C0013450 check ("BANKSIZE" IS NOT NULL),
   CONTROLDEVICEID      NUMBER                           not null
         constraint SYS_C0013451 check ("CONTROLDEVICEID" IS NOT NULL),
   constraint PK_CAPBANK primary key (BANKADDRESS),
   constraint SYS_C0013453 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013454 foreign key (CONTROLPOINTID)
         references POINT (POINTID),
   constraint SYS_C0013455 foreign key (CONTROLDEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : FDRTRANSLATION                                       */
/*==============================================================*/

create table FDRTRANSLATION (
   POINTID              NUMBER                           not null
         constraint SYS_C0015061 check ("POINTID" IS NOT NULL),
   DIRECTIONTYPE        VARCHAR2(20)                     not null
         constraint SYS_C0015062 check ("DIRECTIONTYPE" IS NOT NULL),
   INTERFACETYPE        VARCHAR2(20)                     not null
         constraint SYS_C0015063 check ("INTERFACETYPE" IS NOT NULL),
   DESTINATION          VARCHAR2(20)                     not null
         constraint SYS_C0015064 check ("DESTINATION" IS NOT NULL),
   TRANSLATION          VARCHAR2(100)                    not null
         constraint SYS_C0015065 check ("TRANSLATION" IS NOT NULL),
   constraint SYS_C0015066 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : RAWPOINTHISTORY                                      */
/*==============================================================*/

create table RAWPOINTHISTORY (
   CHANGEID             NUMBER                           not null,
   POINTID              NUMBER                           not null
         constraint SYS_C0013318 check ("POINTID" IS NOT NULL),
   TIMESTAMP            DATE                             not null
         constraint SYS_C0013319 check ("TIMESTAMP" IS NOT NULL),
   QUALITY              NUMBER                           not null
         constraint SYS_C0013320 check ("QUALITY" IS NOT NULL),
   VALUE                FLOAT                            not null
         constraint SYS_C0013321 check ("VALUE" IS NOT NULL),
   BOOKMARK             VARCHAR2(16),
   constraint SYS_C0013322 primary key (CHANGEID),
   constraint SYS_C0013323 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : LMProgramDirectGroup                                 */
/*==============================================================*/

create table LMProgramDirectGroup (
   DeviceID             NUMBER                           not null,
   LMGroupDeviceID      NUMBER                           not null,
   GroupOrder           NUMBER                           not null,
   constraint FK_LMPrgD_LMPrgDGrp foreign key (DeviceID)
         references LMProgramDirect (DeviceID),
   constraint FK_LMGrp_LMPrgDGrp foreign key (LMGroupDeviceID)
         references LMGroup (DeviceID)
)
/


/*==============================================================*/
/* Table : REPEATERROUTE                                        */
/*==============================================================*/

create table REPEATERROUTE (
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013265 check ("ROUTEID" IS NOT NULL),
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013266 check ("DEVICEID" IS NOT NULL),
   VARIABLEBITS         NUMBER                           not null
         constraint SYS_C0013267 check ("VARIABLEBITS" IS NOT NULL),
   REPEATERORDER        NUMBER                           not null
         constraint SYS_C0013268 check ("REPEATERORDER" IS NOT NULL),
   constraint SYS_C0013269 foreign key (ROUTEID)
         references ROUTE (ROUTEID),
   constraint SYS_C0013270 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : GRAPHDATASERIES                                      */
/*==============================================================*/

create table GRAPHDATASERIES (
   GRAPHDATASERIESID    NUMBER                           not null,
   GRAPHDEFINITIONID    NUMBER                           not null
         constraint SYS_C0015111 check ("GRAPHDEFINITIONID" IS NOT NULL),
   POINTID              NUMBER                           not null
         constraint SYS_C0015112 check ("POINTID" IS NOT NULL),
   Label                VARCHAR2(40)                     not null,
   Axis                 CHAR(1)                          not null,
   Color                NUMBER                           not null,
   Type                 VARCHAR2(12)                     not null,
   constraint SYS_C0015113 primary key (GRAPHDATASERIESID),
   constraint SYS_C0015114 foreign key (GRAPHDEFINITIONID)
         references GRAPHDEFINITION (GRAPHDEFINITIONID),
   constraint SYS_C0015115 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : SERIALNUMBER                                         */
/*==============================================================*/

create table SERIALNUMBER (
   OWNER                NUMBER                           not null
         constraint SYS_C0015123 check ("OWNER" IS NOT NULL),
   SERIALNUMBER         VARCHAR2(15),
   NAME                 VARCHAR2(20),
   constraint SYS_C0015124 foreign key (OWNER)
         references USERINFO (USERID)
)
/


/*==============================================================*/
/* Table : CICustomerBase                                       */
/*==============================================================*/

create table CICustomerBase (
   DeviceID             NUMBER                           not null,
   AddressID            NUMBER                           not null,
   MainPhoneNumber      VARCHAR2(18)                     not null,
   MainFaxNumber        VARCHAR2(18)                     not null,
   CustPDL              FLOAT                            not null,
   PrimeContactID       NUMBER                           not null,
   CustTimeZone         VARCHAR2(6)                      not null,
   CurtailmentAgreement VARCHAR2(100)                    not null,
   constraint PK_CICUSTOMERBASE primary key (DeviceID),
   constraint FK_Dev_CICustBase foreign key (DeviceID)
         references DEVICE (DEVICEID),
   constraint FK_CICstBas_CstAddrs foreign key (AddressID)
         references CustomerAddress (AddressID)
)
/


/*==============================================================*/
/* Table : CAPCONTROLSTRATEGY                                   */
/*==============================================================*/

create table CAPCONTROLSTRATEGY (
   CAPSTRATEGYID        NUMBER                           not null,
   STRATEGYNAME         VARCHAR2(30)                     not null
         constraint SYS_C0013461 check ("STRATEGYNAME" IS NOT NULL),
   DISTRICTNAME         VARCHAR2(30)                     not null
         constraint SYS_C0013462 check ("DISTRICTNAME" IS NOT NULL),
   ACTUALVARPOINTID     NUMBER                           not null
         constraint SYS_C0013463 check ("ACTUALVARPOINTID" IS NOT NULL),
   MAXDAILYOPERATION    NUMBER                           not null
         constraint SYS_C0013464 check ("MAXDAILYOPERATION" IS NOT NULL),
   PEAKSETPOINT         FLOAT                            not null
         constraint SYS_C0013465 check ("PEAKSETPOINT" IS NOT NULL),
   OFFPEAKSETPOINT      FLOAT                            not null
         constraint SYS_C0013466 check ("OFFPEAKSETPOINT" IS NOT NULL),
   PEAKSTARTTIME        DATE                             not null
         constraint SYS_C0013467 check ("PEAKSTARTTIME" IS NOT NULL),
   PEAKSTOPTIME         DATE                             not null
         constraint SYS_C0013468 check ("PEAKSTOPTIME" IS NOT NULL),
   CALCULATEDVARLOADPOINTID NUMBER                           not null
         constraint SYS_C0013469 check ("CALCULATEDVARLOADPOINTID" IS NOT NULL),
   BANDWIDTH            FLOAT                            not null
         constraint SYS_C0013470 check ("BANDWIDTH" IS NOT NULL),
   CONTROLINTERVAL      NUMBER                           not null
         constraint SYS_C0013471 check ("CONTROLINTERVAL" IS NOT NULL),
   MINRESPONSETIME      NUMBER                           not null
         constraint SYS_C0013472 check ("MINRESPONSETIME" IS NOT NULL),
   MINCONFIRMPERCENT    NUMBER                           not null
         constraint SYS_C0013473 check ("MINCONFIRMPERCENT" IS NOT NULL),
   FAILUREPERCENT       NUMBER                           not null
         constraint SYS_C0013474 check ("FAILUREPERCENT" IS NOT NULL),
   STATUS               VARCHAR2(12)                     not null
         constraint SYS_C0013475 check ("STATUS" IS NOT NULL),
   DAYSOFWEEK           CHAR(8)                          not null
         constraint SYS_C0014656 check ("DAYSOFWEEK" IS NOT NULL),
   constraint SYS_C0013476 primary key (CAPSTRATEGYID),
   constraint SYS_C0013478 foreign key (ACTUALVARPOINTID)
         references POINT (POINTID),
   constraint SYS_C0013479 foreign key (CALCULATEDVARLOADPOINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : CustomerContact                                      */
/*==============================================================*/

create table CustomerContact (
   ContactID            NUMBER                           not null,
   ContFirstName        VARCHAR2(20)                     not null,
   ContLastName         VARCHAR2(32)                     not null,
   ContPhone1           VARCHAR2(14)                     not null,
   ContPhone2           VARCHAR2(14)                     not null,
   LocationID           NUMBER                           not null,
   LogInID              NUMBER                           not null,
   constraint PK_CUSTOMERCONTACT primary key (ContactID),
   constraint FK_CstCont_GrpRecip foreign key (LocationID)
         references GroupRecipient (LocationID),
   constraint FK_RefCstLg_CustCont foreign key (LogInID)
         references CustomerLogin (LogInID)
)
/


insert into CustomerContact(contactID, contFirstName, contLastName, contPhone1, contPhone2, locationID,loginID)
values (-1,'(none)','(none)','(none)','(none)',0,-1)

/*==============================================================*/
/* Table : CICustContact                                        */
/*==============================================================*/

create table CICustContact (
   DeviceID             NUMBER                           not null,
   ContactID            NUMBER                           not null,
   constraint PK_CICUSTCONTACT primary key (ContactID, DeviceID),
   constraint FK_CstCont_CICstCont foreign key (ContactID)
         references CustomerContact (ContactID),
   constraint FK_CICstBase_CICstCont foreign key (DeviceID)
         references CICustomerBase (DeviceID)
)
/


/*==============================================================*/
/* Table : LASTPOINTSCAN                                        */
/*==============================================================*/

create table LASTPOINTSCAN (
   POINTID              NUMBER,
   VALUE                FLOAT                            not null
         constraint SYS_C0013345 check ("VALUE" IS NOT NULL),
   TIME                 DATE                             not null
         constraint SYS_C0013346 check ("TIME" IS NOT NULL),
   QUALITY              NUMBER                           not null
         constraint SYS_C0013347 check ("QUALITY" IS NOT NULL),
   ALARMSTATE           NUMBER                           not null
         constraint SYS_C0013348 check ("ALARMSTATE" IS NOT NULL),
   constraint SYS_C0013349 foreign key (POINTID)
         references POINT (POINTID)
)
/


/*==============================================================*/
/* Table : LMGROUPEMETCON                                       */
/*==============================================================*/

create table LMGROUPEMETCON (
   DEVICEID             NUMBER                           not null,
   GOLDADDRESS          NUMBER                           not null
         constraint SYS_C0013351 check ("GOLDADDRESS" IS NOT NULL),
   SILVERADDRESS        NUMBER                           not null
         constraint SYS_C0013352 check ("SILVERADDRESS" IS NOT NULL),
   ADDRESSUSAGE         CHAR(1)                          not null
         constraint SYS_C0013353 check ("ADDRESSUSAGE" IS NOT NULL),
   RELAYUSAGE           CHAR(1)                          not null
         constraint SYS_C0013354 check ("RELAYUSAGE" IS NOT NULL),
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013355 check ("ROUTEID" IS NOT NULL),
   constraint SYS_C0013356 foreign key (DEVICEID)
         references LMGroup (DeviceID),
   constraint SYS_C0013357 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : LMGROUPVERSACOM                                      */
/*==============================================================*/

create table LMGROUPVERSACOM (
   DEVICEID             NUMBER                           not null,
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013359 check ("ROUTEID" IS NOT NULL),
   UTILITYADDRESS       NUMBER                           not null
         constraint SYS_C0013360 check ("UTILITYADDRESS" IS NOT NULL),
   SECTIONADDRESS       NUMBER                           not null
         constraint SYS_C0013361 check ("SECTIONADDRESS" IS NOT NULL),
   CLASSADDRESS         NUMBER                           not null
         constraint SYS_C0013362 check ("CLASSADDRESS" IS NOT NULL),
   DIVISIONADDRESS      NUMBER                           not null
         constraint SYS_C0013363 check ("DIVISIONADDRESS" IS NOT NULL),
   ADDRESSUSAGE         CHAR(4)                          not null
         constraint SYS_C0013364 check ("ADDRESSUSAGE" IS NOT NULL),
   RELAYUSAGE           CHAR(3)                          not null
         constraint SYS_C0013365 check ("RELAYUSAGE" IS NOT NULL),
   constraint FK_LMGrp_LMGrpVers foreign key (DEVICEID)
         references LMGroup (DeviceID),
   constraint SYS_C0013367 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : LMGROUPVERSACOMSERIAL                                */
/*==============================================================*/

create table LMGROUPVERSACOMSERIAL (
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013368 check ("DEVICEID" IS NOT NULL),
   DEVICEIDOFGROUP      NUMBER                           not null
         constraint SYS_C0013369 check ("DEVICEIDOFGROUP" IS NOT NULL),
   ROUTEID              NUMBER                           not null
         constraint SYS_C0013370 check ("ROUTEID" IS NOT NULL),
   SERIALNUMBER         NUMBER                           not null
         constraint SYS_C0013371 check ("SERIALNUMBER" IS NOT NULL),
   RELAYUSAGE           CHAR(3)                          not null
         constraint SYS_C0013372 check ("RELAYUSAGE" IS NOT NULL),
   constraint SYS_C0013373 foreign key (DEVICEID)
         references DEVICE (DEVICEID),
   constraint SYS_C0013374 foreign key (DEVICEIDOFGROUP)
         references DEVICE (DEVICEID),
   constraint SYS_C0013375 foreign key (ROUTEID)
         references ROUTE (ROUTEID)
)
/


/*==============================================================*/
/* Table : CCSTRATEGYBANKLIST                                   */
/*==============================================================*/

create table CCSTRATEGYBANKLIST (
   CAPSTRATEGYID        NUMBER                           not null
         constraint SYS_C0013480 check ("CAPSTRATEGYID" IS NOT NULL),
   DEVICEID             NUMBER                           not null
         constraint SYS_C0013481 check ("DEVICEID" IS NOT NULL),
   CONTROLORDER         NUMBER                           not null
         constraint SYS_C0013482 check ("CONTROLORDER" IS NOT NULL),
   constraint SYS_C0013483 foreign key (CAPSTRATEGYID)
         references CAPCONTROLSTRATEGY (CAPSTRATEGYID),
   constraint SYS_C0013484 foreign key (DEVICEID)
         references DEVICE (DEVICEID)
)
/


/*==============================================================*/
/* Table : LMProgramCurtailCustomerList                         */
/*==============================================================*/

create table LMProgramCurtailCustomerList (
   DeviceID             NUMBER                           not null,
   LMCustomerDeviceID   NUMBER                           not null,
   CustomerOrder        NUMBER                           not null,
   RequireAck           CHAR(1)                          not null,
   constraint PK_LMPROGRAMCURTAILCUSTOMERLIS primary key (LMCustomerDeviceID, DeviceID),
   constraint FK_CICstBase_LMProgCList foreign key (LMCustomerDeviceID)
         references CICustomerBase (DeviceID),
   constraint FK_LMPrgCrt_LMPrCstLst foreign key (DeviceID)
         references LMProgramCurtailment (DeviceID)
         on delete cascade
)
/


/*==============================================================*/
/* Table : DYNAMICCAPCONTROLSTRATEGY                            */
/*==============================================================*/

create table DYNAMICCAPCONTROLSTRATEGY (
   CapStrategyID        NUMBER                           not null,
   NewPointDataReceived CHAR(1)                          not null,
   StrategyUpdated      CHAR(1)                          not null,
   ActualVarPointValue  FLOAT                            not null,
   NextCheckTime        DATE                             not null,
   CalcVarPointValue    FLOAT                            not null,
   Operations           NUMBER                           not null,
   LastOperation        DATE                             not null,
   LastCapBankControlled NUMBER                           not null,
   PeakOrOffPeak        CHAR(1)                          not null,
   RecentlyControlled   CHAR(1)                          not null,
   CalcValueBeforeControl FLOAT                            not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICCAPCONTROLSTRATEGY primary key (CapStrategyID),
   constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (CapStrategyID)
         references CAPCONTROLSTRATEGY (CAPSTRATEGYID)
)
/


/*==============================================================*/
/* Table : LMCurtailCustomerActivity                            */
/*==============================================================*/

create table LMCurtailCustomerActivity (
   CustomerID           NUMBER                           not null,
   CurtailReferenceID   NUMBER                           not null,
   AcknowledgeStatus    VARCHAR2(30)                     not null,
   AckDateTime          DATE                             not null,
   IPAddressOfAckUser   VARCHAR2(15)                     not null,
   UserIDName           VARCHAR2(40)                     not null,
   NameOfAckPerson      VARCHAR2(40)                     not null,
   CurtailmentNotes     VARCHAR2(120)                    not null,
   CurrentPDL           FLOAT                            not null,
   AckLateFlag          CHAR(1)                          not null,
   constraint FK_CICBas_LMCrtCstAct foreign key (CustomerID)
         references CICustomerBase (DeviceID),
   constraint FK_LMCURTAI_REFLMCUST_LMCURTAI foreign key (CurtailReferenceID)
         references LMCurtailProgramActivity (CurtailReferenceID)
)
/


/*==============================================================*/
/* Index: Index_DISPLAYNAME                                     */
/*==============================================================*/
create unique index Index_DISPLAYNAME on DISPLAY (
   NAME ASC
)
/


/*==============================================================*/
/* Index: Index_UserIDName                                      */
/*==============================================================*/
create unique index Index_UserIDName on CustomerLogin (
   UserName ASC
)
/


/*==============================================================*/
/* Index: Indx_CAPCNTST2                                        */
/*==============================================================*/
create unique index Indx_CAPCNTST2 on CAPCONTROLSTRATEGY (
   STRATEGYNAME DESC
)
/


/*==============================================================*/
/* Index: Indx_CAPCNTSTR1                                       */
/*==============================================================*/
create unique index Indx_CAPCNTSTR1 on CAPCONTROLSTRATEGY (
   CAPSTRATEGYID DESC
)
/


/*==============================================================*/
/* Index: Indx_COLUMNTYPE                                       */
/*==============================================================*/
create unique index Indx_COLUMNTYPE on COLUMNTYPE (
   TYPENUM DESC
)
/


/*==============================================================*/
/* Index: Indx_COMMPORT1                                        */
/*==============================================================*/
create unique index Indx_COMMPORT1 on COMMPORT (
   PORTID DESC
)
/


/*==============================================================*/
/* Index: Indx_COMMPORT2                                        */
/*==============================================================*/
create unique index Indx_COMMPORT2 on COMMPORT (
   DESCRIPTION DESC
)
/


/*==============================================================*/
/* Index: Indx_DEVICE1                                          */
/*==============================================================*/
create unique index Indx_DEVICE1 on DEVICE (
   DEVICEID DESC
)
/


/*==============================================================*/
/* Index: Indx_DEVICE2                                          */
/*==============================================================*/
create unique index Indx_DEVICE2 on DEVICE (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_DISPLAY                                          */
/*==============================================================*/
create unique index Indx_DISPLAY on DISPLAY (
   DISPLAYNUM DESC
)
/


/*==============================================================*/
/* Index: Indx_GPHDATSER                                        */
/*==============================================================*/
create unique index Indx_GPHDATSER on GRAPHDATASERIES (
   GRAPHDATASERIESID DESC
)
/


/*==============================================================*/
/* Index: Indx_GPHDEF                                           */
/*==============================================================*/
create unique index Indx_GPHDEF on GRAPHDEFINITION (
   GRAPHDEFINITIONID DESC
)
/


/*==============================================================*/
/* Index: Indx_LOGIC                                            */
/*==============================================================*/
create unique index Indx_LOGIC on LOGIC (
   LOGICID DESC
)
/


/*==============================================================*/
/* Index: Indx_MCSCHED                                          */
/*==============================================================*/
create unique index Indx_MCSCHED on MCSCHEDULE (
   SCHEDULEID DESC
)
/


/*==============================================================*/
/* Index: Indx_NOTIFGRP                                         */
/*==============================================================*/
create unique index Indx_NOTIFGRP on NotificationGroup (
   GroupName ASC
)
/


/*==============================================================*/
/* Index: Indx_POINT                                            */
/*==============================================================*/
create unique index Indx_POINT on POINT (
   POINTID DESC
)
/


/*==============================================================*/
/* Index: Indx_PROGRAM1                                         */
/*==============================================================*/
create unique index Indx_PROGRAM1 on PROGRAM (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_PROGRAM2                                         */
/*==============================================================*/
create unique index Indx_PROGRAM2 on PROGRAM (
   PROGRAMID DESC
)
/


/*==============================================================*/
/* Index: Indx_RAWPTHIS                                         */
/*==============================================================*/
create unique index Indx_RAWPTHIS on RAWPOINTHISTORY (
   CHANGEID DESC
)
/


/*==============================================================*/
/* Index: Indx_ROUTE1                                           */
/*==============================================================*/
create unique index Indx_ROUTE1 on ROUTE (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_ROUTE2                                           */
/*==============================================================*/
create unique index Indx_ROUTE2 on ROUTE (
   ROUTEID DESC
)
/


/*==============================================================*/
/* Index: Indx_STATEGRP1                                        */
/*==============================================================*/
create unique index Indx_STATEGRP1 on STATEGROUP (
   STATEGROUPID DESC
)
/


/*==============================================================*/
/* Index: Indx_STATEGRP2                                        */
/*==============================================================*/
create unique index Indx_STATEGRP2 on STATEGROUP (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_SYSTEMLOG                                        */
/*==============================================================*/
create unique index Indx_SYSTEMLOG on SYSTEMLOG (
   LOGID DESC
)
/


/*==============================================================*/
/* Index: Indx_TEMPLATE                                         */
/*==============================================================*/
create unique index Indx_TEMPLATE on TEMPLATE (
   TEMPLATENUM DESC
)
/


/*==============================================================*/
/* Index: Indx_UOM                                              */
/*==============================================================*/
create unique index Indx_UOM on UNITMEASURE (
   NAME DESC
)
/


/*==============================================================*/
/* Index: Indx_USERINFO                                         */
/*==============================================================*/
create unique index Indx_USERINFO on USERINFO (
   USERID DESC
)
/


/*==============================================================*/
/* Index: SchedNameIndx                                         */
/*==============================================================*/
create unique index SchedNameIndx on MACSchedule (
   ScheduleName ASC
)
/


/*==============================================================*/
/* View: DISPLAY2WAYDATA_VIEW                                   */
/*==============================================================*/
create or replace view DISPLAY2WAYDATA_VIEW (POINTID, POINTNAME , POINTTYPE , POINTSTATE , DEVICENAME , DEVICETYPE , DEVICECURRENTSTATE , DEVICEID , POINTVALUE, POINTQUALITY, POINTTIMESTAMP, UofM) as
select point.POINTID, point.POINTNAME, point.POINTTYPE, point.SERVICEFLAG, device.NAME, device.TYPE, device.CURRENTSTATE, device.DEVICEID, '**DYNAMIC**', '**DYNAMIC**', '**DYNAMIC**', (select unit from pointunit where pointunit.pointid=point.pointid)
from POINT, DEVICE
where point.DEVICEID = device.DEVICEID
/


/*==============================================================*/
/* View: LMCurtailCustomerActivity_View                         */
/*==============================================================*/
create or replace view LMCurtailCustomerActivity_View as
select cust.CustomerID, prog.CurtailmentStartTime, prog.CurtailReferenceID, prog.CurtailmentStopTime, cust.AcknowledgeStatus, cust.AckDateTime, cust.NameOfAckPerson, cust.AckLateFlag
from LMCurtailProgramActivity prog, LMCurtailCustomerActivity cust
order by cust.AckDateTime ASC
/


/*==============================================================*/
/* View: POINTHISTORY                                           */
/*==============================================================*/
create or replace view POINTHISTORY (POINTID , POINTTIMESTAMP , QUALITY , VALUE) as
select RawPointHistory.POINTID, RawPointHistory.TIMESTAMP, RawPointHistory.QUALITY, (RawPointHistory.Value*PointAnalog.Multiplier)
from RAWPOINTHISTORY, POINTANALOG
where RawPointHistory.POINTID = PointAnalog.POINTID
/


