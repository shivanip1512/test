/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/
create table GatewayEndDevice (
SerialNumber         varchar(30)          not null,
HardwareType         numeric              not null,
DataType             numeric              not null,
DataValue            varchar(100)         null
);
go
alter table GatewayEndDevice
   add constraint PK_GATEWAYENDDEVICE primary key  (SerialNumber, HardwareType, DataType);
go

create table DeviceCustomerList (
CustomerID           numeric              not null,
DeviceID             numeric              not null
);
go
alter table DeviceCustomerList
   add constraint PK_DEVICECUSTOMERLIST primary key  (DeviceID, CustomerID);
go
alter table DeviceCustomerList
   add constraint FK_DvStLsCst foreign key (CustomerID)
      references Customer (CustomerID);
go
alter table DeviceCustomerList
   add constraint FK_DvStLsDev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go

insert into DeviceCustomerList
select OwnerID, ChildID from PAOOwner
where ownerid in (select PAObjectid from YukonPAObject where Category = 'CUSTOMER')
and childid in (select PAObjectid from YukonPAObject where PAOClass = 'METER');
go

delete from paoowner where ownerid in (select PAObjectid from YukonPAObject where Category = 'CUSTOMER');
go
delete from yukonpaobject where Category = 'CUSTOMER';
go


/* @error ignore */
alter table FDRTRANSLATION drop constraint PK_FDRTRANSLATION;
go
alter table FDRTRANSLATION add
  constraint PK_FDRTrans primary key  (POINTID, TRANSLATION);
go

/* @error ignore */
alter table pointlimits drop constraint PK_POINTLIMITS;
go
alter table pointlimits 
	add constraint PK__POINTID_LIMITNUM primary key (pointid, limitnumber);
go

alter table DynamicLMGroup add ControlStartTime datetime;
go
update DynamicLMGroup set ControlStartTime = '01-JAN-1990';
go
alter table DynamicLMGroup alter column ControlStartTime datetime not null;
go


alter table DynamicLMGroup add ControlCompleteTime datetime;
go
update DynamicLMGroup set ControlCompleteTime = '01-JAN-1990';
go
alter table DynamicLMGroup alter column ControlCompleteTime datetime not null;
go


alter table EnergyCompany add PrimaryContactID numeric;
go
update EnergyCompany set PrimaryContactID = 0;
go
alter table EnergyCompany alter column PrimaryContactID numeric not null;
go


alter table EnergyCompany
   add constraint FK_EnCm_Cnt foreign key (PrimaryContactID)
      references Contact (ContactID);
go

/* @error ignore */
alter table LMProgramControlWindow drop constraint PK_LMPROGRAMCONTROLWINDOW;
go
alter table LMProgramControlWindow 
    add constraint PK_LMPROGRAMCNTRLWINDOW primary key (DeviceID, WindowNumber);
go

/**** ADD TABLE SeasonSchedule ****/
create table SeasonSchedule (
ScheduleID           numeric              not null,
ScheduleName         varchar(40)          not null,
SpringMonth          numeric              not null,
SpringDay            numeric              not null,
SummerMonth          numeric              not null,
SummerDay            numeric              not null,
FallMonth            numeric              not null,
FallDay              numeric              not null,
WinterMonth          numeric              not null,
WinterDay            numeric              not null,
constraint PK_SEASONSCHEDULE primary key  (ScheduleID)
);
go

insert into SeasonSchedule values(0,'Default Season Schedule',3,15,5,1,8,15,11,1);

/* @error ignore */
alter table EnergyCompany drop constraint FK_EnCmpRt;
go
/* @error ignore */
alter table EnergyCompany drop constraint FK_YkWbC_EnC;
go
alter table EnergyCompany drop column RouteID;
go
alter table EnergyCompany drop column WebConfigID;
go

alter table EnergyCompany ADD UserID numeric;
go
update EnergyCompany set UserID = -1;
go
alter table EnergyCompany alter column UserID numeric not null;
go


alter table EnergyCompany
   add constraint FK_EngCmp_YkUs foreign key (UserID)
      references YukonUser (UserID);
go

/* @error ignore */
insert into HolidaySchedule values( 0, 'Empty Holiday Schedule' );
go

alter table LMProgram add HolidayScheduleID numeric;
go
update LMProgram set HolidayScheduleID = 0;
go
alter table LMProgram alter column HolidayScheduleID numeric not null;
go


alter table LMProgram add SeasonScheduleID numeric;
go
update LMProgram set SeasonScheduleID = 0;
go
alter table LMProgram alter column SeasonScheduleID numeric not null;
go


alter table LMPROGRAM
   add constraint FK_SesSch_LmPr foreign key (SeasonScheduleID)
      references SeasonSchedule (ScheduleID);
go
alter table LMPROGRAM
   add constraint FK_HlSc_LmPr foreign key (HolidayScheduleID)
      references HolidaySchedule (HolidayScheduleID);
go

create table PAOExclusion (
ExclusionID          numeric              not null,
PaoID                numeric              not null,
ExcludedPaoID        numeric              not null,
PointID              numeric              not null,
Value                numeric              not null,
FunctionID           numeric              not null,
FuncName             varchar(100)         not null,
FuncRequeue          numeric              not null,
constraint PK_PAOEXCLUSION primary key  (ExclusionID)
);
go
create unique  index Indx_PAOExclus on PAOExclusion (
PaoID,
ExcludedPaoID
);
go
alter table PAOExclusion
   add constraint FK_PAOEx_YkPAO foreign key (PaoID)
      references YukonPAObject (PAObjectID);
go
alter table PAOExclusion
   add constraint FK_PAO_REF__YUK foreign key (ExcludedPaoID)
      references YukonPAObject (PAObjectID);
go
alter table PAOExclusion
   add constraint FK_PAOEx_Pt foreign key (PointID)
      references POINT (POINTID);
go

create table LMGroupMCT (
DeviceID             numeric              not null,
MCTAddress           numeric              not null,
MCTLevel             char(1)              not null,
RelayUsage           char(7)              not null,
RouteID              numeric              not null,
MCTDeviceID          numeric              not null,
constraint PK_LMGrpMCTPK primary key  (DeviceID)
);
go
alter table LMGroupMCT
   add constraint FK_LMGrMC_Rt foreign key (RouteID)
      references Route (RouteID);
go
alter table LMGroupMCT
   add constraint FK_LMGrMC_Dev foreign key (DeviceID)
      references DEVICE (DEVICEID);
go
alter table LMGroupMCT
   add constraint FK_LMGrMC_YkP foreign key (MCTDeviceID)
      references YukonPAObject (PAObjectID);
go

alter table YukonRole drop column DefaultValue;
go

create table YukonRoleProperty (
RolePropertyID       numeric              not null,
RoleID               numeric              not null,
KeyName              varchar(100)         not null,
DefaultValue         varchar(1000)        not null,
Description          varchar(1000)        not null,
constraint PK_YUKONROLEPROPERTY primary key  (RolePropertyID)
);
go
alter table YukonRoleProperty
   add constraint FK_YkRlPrp_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go

drop table YukonUserRole;
go
create table YukonUserRole (
UserRoleID           numeric              not null,
UserID               numeric              not null,
RoleID               numeric              not null,
RolePropertyID       numeric              not null,
Value                varchar(1000)        not null,
constraint PK_YKONUSRROLE primary key  (UserRoleID)
);
go
alter table YukonUserRole
   add constraint FK_YkUsRlr_YkUsr foreign key (UserID)
      references YukonUser (UserID);
go
alter table YukonUserRole
   add constraint FK_YkUsRl_YkRol foreign key (RoleID)
      references YukonRole (RoleID);
go
alter table YukonUserRole
   add constraint FK_YkUsRl_RlPrp foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go

drop table YukonGroupRole;
go
create table YukonGroupRole (
GroupRoleID          numeric              not null,
GroupID              numeric              not null,
RoleID               numeric              not null,
RolePropertyID       numeric              not null,
Value                varchar(1000)        not null,
constraint PK_YUKONGRPROLE primary key  (GroupRoleID)
);
go
alter table YukonGroupRole
   add constraint FK_YkGrRl_YkGrp foreign key (GroupID)
      references YukonGroup (GroupID);
go
alter table YukonGroupRole
   add constraint FK_YkGrRl_YkRle foreign key (RoleID)
      references YukonRole (RoleID);
go
alter table YukonGroupRole
   add constraint FK_YkGrpR_YkRlPr foreign key (RolePropertyID)
      references YukonRoleProperty (RolePropertyID);
go

alter table YukonGroup add GroupDescription varchar(200);
go
update YukonGroup set GroupDescription = '(none)';
go
alter table YukonGroup alter column GroupDescription varchar(200) not null;
go


/**** Add some rows to FDR tables ****/
insert into fdrinterface values (12,'TEXTIMPORT','Receive,Receive for control','f');
insert into fdrinterface values (13,'TEXTEXPORT','Send','f');
insert into FDRInterface values (14, 'LODESTAR', 'Receive', 'f' );
insert into fdrinterfaceoption values (12,'Point ID',1,'Text','(none)');
insert into fdrinterfaceoption values (13,'Point ID',1,'Text','(none)');
insert into fdrinterfaceoption values(14,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values(14,'Channel',2,'Text','(none)');
go

insert into billingfileformats values( 12, 'SEDC 5.4');
go

/**** Role, Groups and YukonUser major changes (All Roles and Groups must be recreated) ****/
delete from YukonGroupRole;
go
delete from YukonUserRole;
go
delete from YukonUserGroup;
go
delete from YukonRole;
go
delete from YukonGroup;
go

insert into YukonGroup values(-1,'yukon','The default system user group that allows limited user interaction.');
insert into YukonGroup values(-100,'operators', 'The default group of yukon operators');
insert into yukongroup values(-200,'Esub Users', 'The default group of esubstation users');
insert into yukongroup values(-201,'Esub Operators', 'The default group of esubstation operators');
go

/* Default role for all users */
insert into YukonRole values(-1,'Yukon','Yukon','Default Yukon role');
insert into YukonRole values(-2,'Energy Company','Yukon','Energy company role');

/* Application specific roles */
insert into YukonRole values(-100,'Database Editor','Application','Access to the Yukon Database Editor application');
insert into YukonRole values(-101,'Tabular Display Console','Application','Access to the Yukon Tabular Display Console application');
insert into YukonRole values(-102,'Trending','Application','Access to the Yukon Trending application');
insert into YukonRole values(-103,'Commander','Application','Access to the Yukon Commander application');
insert into YukonRole values(-104,'Calc Historical','Application','Calc Historical');
insert into YukonRole values(-105,'Web Graph','Application','Web Graph');
insert into YukonRole values(-106,'Billing','Application','Billing');
insert into YukonRole values(-107,'Esubstation Editor','Application','Access to the Esubstation Drawing Editor application');
insert into YukonRole values(-108,'Web Client','Application','Access to the Yukon web application');

/* Web client operator roles */
insert into YukonRole values(-200,'Administrator','Operator','Access to Yukon administration');
insert into YukonRole values(-201,'Consumer Info','Operator','Operator access to consumer account information');
insert into YukonRole values(-202,'Commercial Metering','Operator','Operator access to commerical metering');
insert into YukonRole values(-203,'Direct Loadcontrol','Operator','Operator  access to direct loadcontrol');
insert into YukonRole values(-204,'Direct Curtailment','Operator','Operator access to direct curtailment');
insert into YukonRole values(-205,'Energy Buyback','Operator','Operator access to energy buyback');

/* Operator roles */
insert into YukonRole values(-206,'Esubstation Drawings','Operator','Operator access to esubstation drawings');
insert into YukonRole values(-207,'Odds For Control','Operator','Operator access to odds for control');

/* CI customer roles */
insert into YukonRole values(-300,'Direct Loadcontrol','CICustomer','Customer access to commercial/industrial customer direct loadcontrol');
insert into YukonRole values(-301,'Curtailment','CICustomer','Customer access to commercial/industrial customer direct curtailment');
insert into YukonRole values(-302,'Energy Buyback','CICustomer','Customer access to commercial/industrial customer energy buyback');
insert into YukonRole values(-303,'Esubstation Drawings','CICustomer','Customer access to esubstation drawings');
insert into YukonRole values(-304,'Commercial Metering','CICustomer','Customer access to commercial metering');

/* Consumer roles */
insert into YukonRole values(-400,'Residential Customer','Consumer','Access to residential customer information');
go


insert into YukonUserGroup values(-1,-1);
insert into YukonUserGroup values(-1,-100);
go


/* Yukon Role */
insert into YukonRoleProperty values(-1000,-1,'dispatch_machine','127.0.0.1','Name or IP address of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1001,-1,'dispatch_port','1510','TCP/IP port of the Yukon Dispatch Service');
insert into YukonRoleProperty values(-1002,-1,'porter_machine','127.0.0.1','Name or IP address of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1003,-1,'porter_port','1540','TCP/IP port of the Yukon Port Control Service');
insert into YukonRoleProperty values(-1004,-1,'macs_machine','127.0.0.1','Name or IP address of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1005,-1,'macs_port','1900','TCP/IP port of the Yukon Metering and Control Scheduler Service');
insert into YukonRoleProperty values(-1006,-1,'cap_control_machine','127.0.0.1','Name or IP address of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1007,-1,'cap_control_port','1910','TCP/IP port of the Yukon Capacitor Control Service');
insert into YukonRoleProperty values(-1008,-1,'loadcontrol_machine','127.0.0.1','Name or IP Address of the Yukon Load Management Service');
insert into YukonRoleProperty values(-1009,-1,'loadcontrol_port','1920','TCP/IP port of the Yukon Load Management Service');

/* Energy Company Role Properties */
insert into YukonRoleProperty values(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of the emails sent by the STARS server');
insert into YukonRoleProperty values(-1101,-2,'optout_notification_recipients','info@cannontech.com','Recipients of the opt out notification email');
insert into YukonRoleProperty values(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
insert into YukonRoleProperty values(-1103,-2,'switch_command_file','c:/yukon/switch_command/default_switch.txt','Location of the file to temporarily store the switch commands');
insert into YukonRoleProperty values(-1104,-2,'optout_command_file','c:/yukon/switch_command/default_optout.txt','Location of the file to temporarily store the opt out commands');
insert into YukonRoleProperty values(-1105,-2,'customer_group_name','Web Demo Residential Customers','Group name of all the customer logins');

/* Database Editor Role */
insert into YukonRoleProperty values(-10000,-100,'point_id_edit','true','Controls whether point ids can be edited');
insert into YukonRoleProperty values(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10003,-100,'dbeditor_cap_control','true','Controls whether the Cap Control menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
insert into YukonRoleProperty values(-10005,-100,'utility_id_range','1-254','<description>');
insert into YukonRoleProperty values(-10006,-100,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');

/* TDC Role */
insert into YukonRoleProperty values(-10100,-101,'loadcontrol_edit','00000000','<description>');
insert into YukonRoleProperty values(-10101,-101,'macs_edit','00000CCC','<description>');
insert into YukonRoleProperty values(-10102,-101,'tdc_express','ludicrous_speed','<description>');
insert into YukonRoleProperty values(-10103,-101,'tdc_max_rows','500','<description>');
insert into YukonRoleProperty values(-10104,-101,'tdc_rights','00000000','<description>');
insert into YukonRoleProperty values(-10105,-101,'CAP_CONTROL_INTERFACE','amfm','<description>');
insert into YukonRoleProperty values(-10106,-101,'cbc_creation_name','CBC %PAOName%','<description>');
insert into YukonRoleProperty values(-10107,-101,'tdc_alarm_count','3','<description>');
insert into YukonRoleProperty values(-10108,-101,'decimal_places','2','<description>');
insert into YukonRoleProperty values(-10109,-101,'pfactor_decimal_places','1','<description>');
insert into YukonRoleProperty values(-10110,-101,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Trending Role */
insert into YukonRoleProperty values(-10200,-102,'graph_edit_graphdefinition','true','<description>');
insert into YukonRoleProperty values(-10201,-102,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Commander Role Properties */ 
insert into YukonRoleProperty values(-10300,-103,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Calc Historical Role Properties */
insert into YukonRoleProperty values(-10400,-104,'interval','900','<description>');
insert into YukonRoleProperty values(-10401,-104,'baseline_calctime','4','<description>');
insert into YukonRoleProperty values(-10402,-104,'daysprevioustocollect','30','<description>');
insert into YukonRoleProperty values(-10403,-104,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Graph Role Properties */
insert into YukonRoleProperty values(-10500,-105,'home_directory','c:\yukon\client\webgraphs','<description>');
insert into YukonRoleProperty values(-10501,-105,'run_interval','900','<description>');
insert into YukonRoleProperty values(-10502,-105,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Billing Role Properties */
insert into YukonRoleProperty values(-10600,-106,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-10601,-106,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');
insert into YukonRoleProperty values(-10602,-106,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Esubstation Editor Role Properties */
insert into YukonRoleProperty values(-10700,-107,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');

/* Web Client Role Properties */
insert into YukonRoleProperty values(-10800,-108,'home_url','/default.jsp','The url to take the user immediately after logging into the Yukon web applicatoin');
insert into YukonRoleProperty values(-10801,-108,'client_log_level','INFO','Sets the logging level for the application.  Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into yukonroleproperty values (-10802, -108,'style_sheet','CannonStyle.css','The web client cascading style sheet.');
insert into yukonroleproperty values (-10803, -108,'nav_bullet_selected','Bullet.gif','The bullet used when an item in the nav is selected.');
insert into yukonroleproperty values (-10804,-108,'nav_bullet','Bullet2.gif','The bullet used when an item in the nav is NOT selected.');
insert into yukonroleproperty values (-10805,-108,'header_logo','DemoHeader.gif','The main header logo');

/* Operator Consumer Info Role Properties */
insert into YukonRoleProperty values(-20100,-201,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-20101,-201,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
insert into YukonRoleProperty values(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
insert into YukonRoleProperty values(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
insert into YukonRoleProperty values(-20105,-201,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
insert into YukonRoleProperty values(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
insert into YukonRoleProperty values(-20111,-201,'Hardwares','true','Controls whether to show the hardware information');
insert into YukonRoleProperty values(-20112,-201,'Hardwares Create','true','Controls whether to enable the hardware creation feature');
insert into YukonRoleProperty values(-20113,-201,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
insert into YukonRoleProperty values(-20115,-201,'Admin Change Login','true','Controls whether to enable the changing customer login feature');
insert into YukonRoleProperty values(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-20130,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20131,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20132,-201,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-20150,-201,'Web Link FAQ','FAQ.jsp','The customized FAQ link provided by the energy company');
insert into YukonRoleProperty values(-20151,-201,'Web Text Control','control','The energy company specific term for control');
insert into YukonRoleProperty values(-20152,-201,'Recommended Settings Button','Recommended Settings','The energy company specific term for Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20153,-201,'Programs Control History Title','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20154,-201,'Program Control History Title','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20155,-201,'Program Control Summary Title','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20156,-201,'Programs Enrollment Title','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20157,-201,'Programs Opt Out Title','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20158,-201,'Change Login Title','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-20159,-201,'Programs Control History Label','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20160,-201,'Programs Enrollment Label','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20161,-201,'Programs Opt Out Label','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20162,-201,'Opt Out Description','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');


/* Operator Administrator Role Properties */
/* Operator Commercial Metering Role Properties*/
insert into YukonRoleProperty values(-20200,-202,'Trending Disclaimer',' ','The disclaimer that appears with trends');

/* Operator Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-20300,-203,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
insert into YukonRoleProperty values(-20301,-203,'Individual Switch','true','Controls access to operator individual switch control');

/* Operator Direct Curtailment Role Properties */
insert into YukonRoleProperty values(-20400,-204,'Direct Curtailment Label','Notification','The operator specific name for direct curtailment');

/* Operator Energy Exchange Role Properties */
insert into YukonRoleProperty values(-20500,-205,'Energy Buyback Label','Energy Buyback','The operator specific name for Energy Buyback');

/* Operator Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-20601,-206,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-20602,-206,'Control','false','Controls control from Esubstation drawings');

/* Odds For Control Role Properties */
insert into YukonRoleProperty values(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Hardware Inventory Role Properties */

/* CICustomer Direct Loadcontrol Role Properties */
insert into YukonRoleProperty values(-30000,-300,'Direct Loadcontrol Label','Direct Control','The customer specific name for direct loadcontrol');
insert into YukonRoleProperty values(-30001,-300,'Individual Switch','false','Controls access to customer individual switch control');

/* CICustomer Curtailment Role Properties */
insert into YukonRoleProperty values(-30100,-301,'Direct Curtailment Label','Notification','The customer specific name for direct curtailment');
insert into YukonRoleProperty values(-30101,-301,'Direct Curtailment Provider','<curtailment provider>','This customers direct curtailment provider');

/* CICustomer Energy Buyback Role Properties */
insert into YukonRoleProperty values(-30200,-302,'Energy Buyback Label','Energy Buyback','The customer specific name for Energy Buyback');
insert into YukonRoleProperty values(-30201,-302,'Energy Buyback Phone Number','1-800-555-5555','The phone number to call if the customer has questions');

/* CICustomer Esubstation Drawings Role Properties */
insert into YukonRoleProperty values(-30300,-303,'View Drawings','true','Controls viewing of Esubstations drawings');
insert into YukonRoleProperty values(-30301,-303,'Edit Limits','false','Controls editing of point limits');
insert into YukonRoleProperty values(-30302,-303,'Control','false','Controls control from Esubstation drawings');

/* CICustomer Commercial Metering Role Properties */
insert into YukonRoleProperty values(-30400,-304,'Trending Disclaimer',' ','The disclaimer that appears with trends');
insert into yukonroleproperty values(-30401, -304, 'Trending Get Data Now Button', 'false', 'Controls access to retrieve meter data on demand');

/* Residential Customer Role Properties */
insert into YukonRoleProperty values(-40000,-400,'Not Implemented','false','Controls whether to show the features not implemented yet (not recommended)');
insert into YukonRoleProperty values(-40001,-400,'Account General','true','Controls whether to show the general account information');
insert into YukonRoleProperty values(-40002,-400,'Metering Usage','false','Controls whether to show the metering time of use');
insert into YukonRoleProperty values(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
insert into YukonRoleProperty values(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
insert into YukonRoleProperty values(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
insert into YukonRoleProperty values(-40006,-400,'Hardwares Thermostat','true','Controls whether to enable the thermostat programming feature');
insert into YukonRoleProperty values(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
insert into YukonRoleProperty values(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
insert into YukonRoleProperty values(-40009,-400,'Admin Change Login','true','Controls whether to allow customers to change their own login');
insert into YukonRoleProperty values(-40030,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40031,-400,'Hide Opt Out Box','true','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40032,-400,'Customized FAQ Link','false','Controls whether the FAQ link links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40033,-400,'Customized Utility Email Link','false','Controls whether the utility email links to a customized location provided by the energy company');
insert into YukonRoleProperty values(-40050,-400,'Web Link FAQ','FAQ.jsp','The customized FAQ link provided by the energy company');
insert into YukonRoleProperty values(-40051,-400,'Web Link Utility Email','FAQ.jsp','The customized utility email provided by the energy company');
insert into YukonRoleProperty values(-40052,-400,'Web Text Control','control','The energy company specific term for control');
insert into YukonRoleProperty values(-40053,-400,'Web Text Controlled','controlled','The energy company specific term for controlled');
insert into YukonRoleProperty values(-40054,-400,'Web Text Controlling','controlling','The energy company specific term for controlling');
insert into YukonRoleProperty values(-40055,-400,'Recommended Settings Button','Recommended Settings','The energy company specific term for Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40056,-400,'General Title','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40057,-400,'Programs Control History Title','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40058,-400,'Program Control History Title','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40059,-400,'Program Control Summary Title','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40060,-400,'Programs Enrollment Title','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40061,-400,'Programs Opt Out Title','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40062,-400,'Utility Title','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40063,-400,'Change Login Title','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
insert into YukonRoleProperty values(-40064,-400,'Programs Control History Label','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40065,-400,'Programs Enrollment Label','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40066,-400,'Programs Opt Out Label','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40067,-400,'General Description','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40068,-400,'Opt Out Description','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
go


/* Assign the default Yukon role to the default Yukon group */
insert into YukonGroupRole values(1,-1,-1,-1000,'(none)');
insert into YukonGroupRole values(2,-1,-1,-1001,'(none)');
insert into YukonGroupRole values(3,-1,-1,-1002,'(none)');
insert into YukonGroupRole values(4,-1,-1,-1003,'(none)');
insert into YukonGroupRole values(5,-1,-1,-1004,'(none)');
insert into YukonGroupRole values(6,-1,-1,-1005,'(none)');
insert into YukonGroupRole values(7,-1,-1,-1006,'(none)');
insert into YukonGroupRole values(8,-1,-1,-1007,'(none)');
insert into YukonGroupRole values(9,-1,-1,-1008,'(none)');
insert into YukonGroupRole values(10,-1,-1,-1009,'(none)');

/* Assign roles to the default operator group to allow them to use all the main rich Yukon applications */
/* Database Editor */
insert into YukonGroupRole values(100,-100,-100,-10000,'(none)');
insert into YukonGroupRole values(101,-100,-100,-10001,'(none)');
insert into YukonGroupRole values(102,-100,-100,-10002,'(none)');
insert into YukonGroupRole values(103,-100,-100,-10003,'(none)');
insert into YukonGroupRole values(104,-100,-100,-10004,'(none)');
insert into YukonGroupRole values(105,-100,-100,-10005,'(none)');
insert into YukonGroupRole values(106,-100,-100,-10006,'(none)');
insert into YukonGroupRole values(107,-100,-100,-10007,'(none)');

/* TDC */
insert into YukonGroupRole values(120,-100,-101,-10100,'(none)');
insert into YukonGroupRole values(121,-100,-101,-10101,'(none)');
insert into YukonGroupRole values(122,-100,-101,-10102,'(none)');
insert into YukonGroupRole values(123,-100,-101,-10103,'(none)');
insert into YukonGroupRole values(124,-100,-101,-10104,'(none)');
insert into YukonGroupRole values(125,-100,-101,-10105,'(none)');
insert into YukonGroupRole values(126,-100,-101,-10106,'(none)');
insert into YukonGroupRole values(127,-100,-101,-10107,'(none)');
insert into YukonGroupRole values(128,-100,-101,-10108,'(none)');
insert into YukonGroupRole values(129,-100,-101,-10109,'(none)');
insert into YukonGroupRole values(130,-100,-101,-10110,'(none)');

/* Trending */
insert into YukonGroupRole values(150,-100,-102,-10200,'(none)');
insert into YukonGroupRole values(151,-100,-102,-10201,'(none)');

/* Commander */
insert into YukonGroupRole values(170,-100,-103,-10300,'(none)');

/* Calc Historical */
insert into YukonGroupRole values(190,-100,-104,-10400,'(none)');
insert into YukonGroupRole values(191,-100,-104,-10401,'(none)');
insert into YukonGroupRole values(192,-100,-104,-10402,'(none)');
insert into YukonGroupRole values(193,-100,-104,-10403,'(none)');

/* Web Graph */
insert into YukonGroupRole values(210,-100,-105,-10500,'(none)');
insert into YukonGroupRole values(211,-100,-105,-10501,'(none)');
insert into YukonGroupRole values(212,-100,-105,-10502,'(none)');

/* Billing */
insert into YukonGroupRole values(230,-100,-106,-10600,'(none)');
insert into YukonGroupRole values(231,-100,-106,-10601,'(none)');
insert into YukonGroupRole values(232,-100,-106,-10602,'(none)');

/* Esubstation Editor */
insert into YukonGroupRole values(250,-100,-107,-10700,'(none)');

/* Assign roles to the default Esub Users */
insert into YukonGroupRole values(300,-200,-206,-20600,'(none)');
insert into YukonGroupRole values(301,-200,-206,-20601,'(none)');
insert into YukonGroupRole values(302,-200,-206,-20602,'(none)');

/* Assign roles to the default Esub Operators */
insert into YukonGroupRole values(350,-201,-206,-20600,'(none)');
insert into YukonGroupRole values(351,-201,-206,-20601,'true');
insert into YukonGroupRole values(352,-201,-206,-20602,'false');
go

/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.41', 'Ryan', '20-MAY-2003', 'Major changes to roles,groups. Added SeasonSchedule, modified LMProgram,DynamicLMGroup,EnergyCompany, PAOExclusion, LMGroupMCT');
go