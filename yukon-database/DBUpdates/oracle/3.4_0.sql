/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* @error ignore-begin */
delete from yukonlistentry where entryid = 125;

alter table MspVendor add AppName varchar2(64);
update MspVendor set AppName = '(none)';
alter table MspVendor modify AppName varchar2(64) not null;

alter table MspVendor add OutUserName varchar2(64);
update MspVendor set OutUserName = '(none)';
alter table MspVendor modify OutUserName varchar2(64) not null;

alter table MspVendor add OutPassword varchar2(64);
update MspVendor set OutPassword = '(none)';
alter table MspVendor modify OutPassword varchar2(64) not null;

delete YukonGroupRole where Roleid = -7;
delete YukonUserRole where RoleID = -7;
delete yukonroleproperty where RoleID = -7;

insert into YukonRoleProperty values(-1600,-7,'PaoName Alias','0','Defines a Yukon Pao (Device) Name field alias. Valid values(0-4): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer]');
insert into YukonRoleProperty values(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');
insert into YukonGroupRole values(-270,-1,-7,-1600,'0');
insert into point values( -110, 'System', 'Multispeak' , 0, 'Default', 0, 'N', 'N', 'S', 110 ,'None', 0);

create table Substation (
   SubstationID         number              not null,
   SubstationName       varchar2(50)          null,
   LMRouteID              number              null
);

insert into Substation values (0,'(none)',0);
alter table Substation
   add constraint PK_SUBSTATION primary key  (SubstationID);

alter table Substation
   add constraint FK_Sub_Rt foreign key (LMRouteID)
      references Route (RouteID);

alter table Substation rename column RouteID to LMRouteID;

create table SubstationToRouteMapping (
   SubstationID		number              not null,
   RouteID              number              not null,
   Ordering             number              not null
);

alter table SubstationToRouteMapping
   add constraint PK_SUB_Rte_Map primary key  (SubstationID, RouteID);

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_SubID foreign key (SubstationID)
      references Substation (SubstationID);

alter table SubstationToRouteMapping
   add constraint FK_Sub_Rte_Map_RteID foreign key (RouteID)
      references Route (RouteID);

alter table ImportData add BillGrp varchar2(64);
update ImportData set BillGrp = '';
alter table ImportData modify BillGrp varchar2(64) not null;

alter table ImportData add SubstationName varchar2(50);
update ImportData set SubstationName = '';
alter table ImportData modify SubstationName varchar2(50) not null;

alter table ImportFail add BillGrp varchar2(64);
update ImportFail set BillGrp = '';
alter table ImportFail modify BillGrp varchar2(64) not null;

alter table ImportFail add SubstationName varchar2(50);
update ImportFail set SubstationName = '';
alter table ImportFail modify SubstationName varchar2(50) not null;

alter table ImportFail add FailType varchar2(64);
update ImportFail set FailType = '';
alter table ImportFail modify FailType varchar2(64) not null;

create table ImportPendingComm (
   DeviceID	       	   	number		    	 not null,
   Address             	varchar2(64)          not null,
   Name	               	varchar2(64)          not null,
   RouteName           	varchar2(64)          not null,
   MeterNumber	       	varchar2(64)	    	 not null,
   CollectionGrp       	varchar2(64)          not null,
   AltGrp	       		varchar2(64)          not null,
   TemplateName	       	varchar2(64)          not null,
   BillGrp             	varchar2(64)          not null, 
   SubstationName      	varchar2(64)          not null
)

alter table ImportPendingComm
   add constraint PK_IMPPENDINGCOMM primary key  (DeviceID);

alter table ImportPendingComm
   add constraint FK_ImpPC_PAO foreign key (DeviceID)
      references YukonPAObject (PAObjectID);

insert into YukonRoleProperty values(-20011,-200,'Multispeak Setup','false','Controls access to configure the Multispeak Interfaces.');

/* @error ignore-end */

insert into YukonRoleProperty values(-10922,-109,'C&I Curtailment Reports Group Label','Stars','Label (header) for C&I Curtailment group reports.');
insert into YukonRoleProperty values(-10923,-109,'C&I Curtailment Reports Group','false','Access to C&I Curtailment group reports');
insert into YukonRoleProperty values(-10812, -108,'Java Web Start Launcher Enabled', 'false', 'Allow access to the Java Web Start Launcher for client applications.');

create table TemplateDisplay (
   DisplayNum           number              not null,
   TemplateNum          number              not null
);

alter table TemplateDisplay
   add constraint PK_TEMPLATEDISPLAY primary key  (DisplayNum);

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_DISPLAY foreign key (DisplayNum)
      references DISPLAY (DISPLAYNUM);

alter table TemplateDisplay
   add constraint FK_TemplateDisplay_TEMPLATE foreign key (TemplateNum)
      references TEMPLATE (TEMPLATENUM);

update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

/* @error ignore-begin */
update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

insert into billingfileformats values(21, 'SIMPLE_TOU');
insert into billingfileformats values(22, 'EXTENDED_TOU');
insert into billingfileformats values (-23, 'Big Rivers Elec Coop');
insert into billingfileformats values(-24, 'INCODE (Extended TOU)');

alter table DeviceMeterGroup modify MeterNumber VARCHAR2(50);
/* @error ignore-end */

update yukonpaobject set type = 'MCT-430EL' where type = 'MCT-430A' or type = 'MCT430A'
update yukonpaobject set type = 'MCT-430LG' where type = 'MCT-430S' or type = 'MCT430S'

update devicetypecommand set devicetype = 'MCT-430EL' where devicetype = 'MCT-430A' or devicetype = 'MCT430A'
update devicetypecommand set devicetype = 'MCT-430LG' where devicetype = 'MCT-430S' or devicetype = 'MCT430S'

INSERT INTO DEVICETYPECOMMAND VALUES (-501, -34, 'CCU-721', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-502, -35, 'CCU-721', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-503, -36, 'CCU-721', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-504, -37, 'CCU-721', 4, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-505, -1, 'MCT-410FL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-506, -81, 'MCT-410FL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-507, -3, 'MCT-410FL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-508, -6, 'MCT-410FL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-509, -34, 'MCT-410FL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-510, -82, 'MCT-410FL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-511, -18, 'MCT-410FL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-512, -19, 'MCT-410FL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-513, -83, 'MCT-410FL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-514, -84, 'MCT-410FL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-515, -11, 'MCT-410FL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-516, -12, 'MCT-410FL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-517, -13, 'MCT-410FL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-518, -98, 'MCT-410FL', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-519, -99, 'MCT-410FL', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-520, -105, 'MCT-410FL', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-521, -106, 'MCT-410FL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-522, -107, 'MCT-410FL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-523, -108, 'MCT-410FL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-524, -109, 'MCT-410FL', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-525, -110, 'MCT-410FL', 21, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-526, -111, 'MCT-410FL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-527, -112, 'MCT-410FL', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-528, -113, 'MCT-410FL', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-529, -114, 'MCT-410FL', 25, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-530, -1, 'MCT-410GL', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-531, -81, 'MCT-410GL', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-532, -3, 'MCT-410GL', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-533, -6, 'MCT-410GL', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-534, -34, 'MCT-410GL', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-535, -82, 'MCT-410GL', 6, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-536, -18, 'MCT-410GL', 7, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-537, -19, 'MCT-410GL', 8, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-538, -83, 'MCT-410GL', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-539, -84, 'MCT-410GL', 10, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-540, -11, 'MCT-410GL', 11, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-541, -12, 'MCT-410GL', 12, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-542, -13, 'MCT-410GL', 13, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-543, -98, 'MCT-410GL', 14, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-544, -99, 'MCT-410GL', 15, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-545, -105, 'MCT-410GL', 16, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-546, -106, 'MCT-410GL', 17, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-547, -107, 'MCT-410GL', 18, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-548, -108, 'MCT-410GL', 19, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-549, -109, 'MCT-410GL', 20, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-550, -110, 'MCT-410GL', 21, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-551, -111, 'MCT-410GL', 22, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-552, -112, 'MCT-410GL', 23, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-553, -113, 'MCT-410GL', 24, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-554, -114, 'MCT-410GL', 25, 'Y', -1);

INSERT INTO DEVICETYPECOMMAND VALUES (-555, -3, 'MCT-430IN', 1, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-556, -20, 'MCT-430IN', 2, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-557, -21, 'MCT-430IN', 3, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-558, -22, 'MCT-430IN', 4, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-559, -115, 'MCT-430IN', 5, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-560, -116, 'MCT-430IN', 6, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-561, -117, 'MCT-430IN', 7, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-562, -118, 'MCT-430IN', 8, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-563, -119, 'MCT-430IN', 9, 'Y', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-564, -120, 'MCT-430IN', 10, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-565, -121, 'MCT-430IN', 11, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-566, -122, 'MCT-430IN', 12, 'N', -1);
INSERT INTO DEVICETYPECOMMAND VALUES (-567, -123, 'MCT-430IN', 13, 'Y', -1);

insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , '1LNSUBSTATE', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'Enable', 5, 6, 0 from stategroup where name = '1LNSUBSTATE';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'Disable', 9, 6, 0 from stategroup where name = '1LNSUBSTATE';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 2, 'Pending', 7, 6, 0 from stategroup where name = '1LNSUBSTATE';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 3, 'Alt - Enabled', 2, 6, 0 from stategroup where name = '1LNSUBSTATE';

insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1 , '1LNVERIFY', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'Verify All', 2, 6, 0 from stategroup where name = '1LNVERIFY';
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'Verify Stop', 6, 6, 0 from stategroup where name = '1LNVERIFY';

update point set pointoffset = 2 where pointname like 'UV op count%';
update point set pointoffset = 3 where pointname like 'OV op count%';

alter table RawPointHistory drop constraint FK_RawPt_Point;
alter table SystemLog drop constraint SYS_C0013408;

/* @error ignore-begin */
alter table cceventlog add kvarBefore float;
update cceventlog set kvarBefore = 0.0;
alter table cceventlog modify kvarBefore float not null;

alter table cceventlog add kvarAfter float;
update cceventlog set kvarAfter = 0.0;
alter table cceventlog modify kvarAfter float not null;

alter table cceventlog add kvarChange float;
update cceventlog set kvarChange = 0.0;
alter table cceventlog modify kvarChange float not null;

alter table cceventlog add additionalInfo varchar2(20);
update cceventlog set additionalInfo = '(none)';
alter table cceventlog modify additionalInfo varchar2(20) not null;

insert into YukonRoleProperty values(-10813, -108,'Show flip command', 'false', 'Show flip command for Cap Banks with 7010 type controller');
/* @error ignore-end */

insert into FDRInterface values (24, 'WABASH', 'Send', 'f' );
insert into FDRInterfaceOption values(24, 'SchedName', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(24, 'Path', 2, 'Text', 'c:\\yukon\\server\\export\\' );
insert into FDRInterfaceOption values(24, 'Filename', 3, 'Text', 'control.txt' );

insert into yukonroleproperty values (-10306, -103, 'Read device', 'true', 'Allow the ability to read values from a device')
insert into yukonroleproperty values (-10307, -103, 'Write to device', 'true', 'Allow the ability to write values to a device')
insert into yukonroleproperty values (-10308, -103, 'Read disconnect', 'true', 'Allow the ability to read disconnect from a device')
insert into yukonroleproperty values (-10309, -103, 'Write disconnect', 'true', 'Allow the ability to write a disconnect to a device')

insert into yukonroleproperty values (-10310, -103, 'Read LM device', 'true', 'Allow the ability to read values from an LM device')
insert into yukonroleproperty values (-10311, -103, 'Write to LM device', 'true', 'Allow the ability to write values to an LM device')
insert into yukonroleproperty values (-10312, -103, 'Control LM device', 'true', 'Allow the ability to control an LM device')

insert into yukonroleproperty values (-10313, -103, 'Read Cap Control device', 'true', 'Allow the ability to read values from a Cap Control device')
insert into yukonroleproperty values (-10314, -103, 'Write to Cap Control device', 'true', 'Allow the ability to write values to a Cap Control device')
insert into yukonroleproperty values (-10315, -103, 'Control Cap Control device', 'true', 'Allow the ability to control a Cap Control device')

insert into yukonroleproperty values (-10316, -103, 'Execute Unknown Command', 'true', 'Allow the ability to execute commands which do not fall under another role property.')
insert into yukonroleproperty values (-10317, -103, 'Execute Manual Command', 'true', 'Allow the ability to execute manual commands')

alter table YukonUser add AuthType varchar2(16);
update YukonUser set AuthType = 'PLAIN';
alter table YukonUser modify AuthType varchar2(16) not null;

insert into yukonroleproperty values (-1307, -4, 'Default Authentication Type', 'PLAIN', 'Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,NONE');

create table UserPaoPermission  (
   UserPaoPermissionID  NUMBER                          not null,
   UserID               NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null
);

alter table UserPaoPermission
   add constraint PK_USERPAOPERMISSION primary key (UserPaoPermissionID);

/**********************USERPAOOWNER MIGRATION*****************************/
/* @start-block */
declare
v_userid number;
v_paoid number;
v_counterid number := 1;
cursor c_userpaoowner is select userid, paoid from userpaoowner;

begin
     open c_userpaoowner;
     loop
          fetch c_userpaoowner into v_userid, v_paoid;
          insert into userpaopermission values (v_counterid, v_userid, v_paoid, 'allow LM visibility');
	  v_counterid := v_counterid + 1; 
	  exit when c_userpaoowner%notfound; 
     end loop;

     close c_userpaoowner;

end;
/* @end-block */
/*commit;*/
/*************************************************************************/

alter table UserPaoPermission
   add constraint AK_USRPAOPERM unique (UserID, PaoID, Permission);

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YKUSR_YUKONUSE foreign key (UserID)
      references YukonUser (UserID);

alter table UserPaoPermission
   add constraint FK_USERPAOP_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

drop table UserPaoOwner;
   
create table GroupPaoPermission  (
   GroupPaoPermissionID NUMBER                          not null,
   GroupID              NUMBER                          not null,
   PaoID                NUMBER                          not null,
   Permission           VARCHAR2(50)                    not null
);

alter table GroupPaoPermission
   add constraint PK_GROUPPAOPERMISSION primary key (GroupPaoPermissionID);

alter table GroupPaoPermission
   add constraint AK_GRPPAOPERM unique (GroupID, PaoID, Permission);

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YKGRP_YUKONGRO foreign key (GroupID)
      references YukonGroup (GroupID);

alter table GroupPaoPermission
   add constraint FK_GROUPPAO_REF_YUKPA_YUKONPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);

/* @error ignore-begin */
alter table dynamiccccapbank add twowaycbcstate number;
update dynamiccccapbank set twowaycbcstate = -1;
alter table dynamiccccapbank modify twowaycbcstate number not null;

alter table dynamiccccapbank add twowaycbcstatetime date;
update dynamiccccapbank set twowaycbcstatetime = '01-JAN-1990';
alter table dynamiccccapbank modify twowaycbcstatetime date not null;
/* @error ignore-end */

create table DEVICEREADJOBLOG  (
   DeviceReadJobLogID   NUMBER                          not null,
   ScheduleID           NUMBER                          not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null
);

alter table DEVICEREADJOBLOG
   add constraint PK_DEVICEREADJOBLOG primary key (DeviceReadJobLogID);

alter table DEVICEREADJOBLOG
   add constraint FK_DEVICERE_FK_DRJOBL_MACSCHED foreign key (ScheduleID)
      references MACSchedule (ScheduleID);
      
create table DEVICEREADREQUESTLOG  (
   DeviceReadRequestLogID NUMBER                        not null,
   RequestID            NUMBER                          not null,
   Command              VARCHAR2(128)                   not null,
   StartTime            DATE                            not null,
   StopTime             DATE                            not null,
   DeviceReadJobLogID   NUMBER                          not null
);

alter table DEVICEREADREQUESTLOG
   add constraint PK_DEVICEREADREQUESTLOG primary key (DeviceReadRequestLogID);

alter table DEVICEREADREQUESTLOG
   add constraint FK_DEVICERE_FK_DRREQL_DEVICERE foreign key (DeviceReadJobLogID)
      references DEVICEREADJOBLOG (DeviceReadJobLogID);
      
create table DEVICEREADLOG  (
   DeviceReadLogID      NUMBER                          not null,
   DeviceID             NUMBER                          not null,
   Timestamp            DATE                            not null,
   StatusCode           SMALLINT                        not null,
   DeviceReadRequestLogID NUMBER                        not null
);

alter table DEVICEREADLOG
   add constraint PK_DEVICEREADLOG primary key (DeviceReadLogID);

alter table DEVICEREADLOG
   add constraint FK_DEVICERE_FK_DRLOGR_DEVICERE foreign key (DeviceReadRequestLogID)
      references DEVICEREADREQUESTLOG (DeviceReadRequestLogID);

/* @error ignore-begin */
create table SequenceNumber  (
   LastValue            number                          not null,
   SequenceName         VARCHAR2(20)                    not null
);

alter table SequenceNumber
   add constraint PK_SEQUENCENUMBER primary key (SequenceName);
/* @error ignore-end */

insert into SequenceNumber values (1,'DeviceReadLog');
insert into SequenceNumber values (1,'DeviceReadRequestLog');
insert into SequenceNumber values (1,'DeviceReadJobLog');

insert into stategroup (StateGroupId, Name, GroupType) select max(stategroupid) + 1, 'TwoStateActive', 'Status' from stategroup;
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 0, 'Active', 0, 6, 0 from stategroup where name = 'TwoStateActive';      
insert into state ( stateGroupId, rawState, text, foregroundcolor, backgroundcolor, imageId) select stategroupid, 1, 'Inactive', 2, 6, 0 from stategroup where name = 'TwoStateActive';      

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */
