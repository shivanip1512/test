/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create table MCTConfig (
ConfigID             numeric              not null,
ConfigName           varchar(30)          not null,
ConfigType           numeric              not null,
ConfigMode           varchar(30)          not null,
MCTWire1             numeric              not null,
Ke1                  float                not null,
MCTWire2             numeric              not null,
Ke2                  float                not null,
MCTWire3             numeric              not null,
Ke3                  float                not null
);
go
alter table MCTConfig
   add constraint PK_MCTCONFIG primary key  (ConfigID);
go


create table MCTConfigMapping (
MctID                numeric              not null,
ConfigID             numeric              not null
);
go
alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key  (MctID, ConfigID);
go
alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);
go
alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID);
go



create table YukonServices (
ServiceID            numeric              not null,
ServiceName          varchar(60)          not null,
ServiceClass         varchar(100)         not null,
ParamNames           varchar(300)         not null,
ParamValues          varchar(300)         not null
);
go

insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
alter table YukonServices
   add constraint PK_YUKSER primary key  (ServiceID);
go


insert into billingFileformats values (9, 'CTI2');

alter table GraphDataSeries add renderer smallint;
go
update GraphDataSeries set renderer = 0;	/*Line renderer */
go
alter table GraphDataSeries alter column renderer smallint not null;
go


alter table GraphDataSeries add moreData varchar(100);
go
update GraphDataSeries set moreData = '(none)';
go
alter table GraphDataSeries alter column moreData varchar(100) not null;
go


alter table systemlog add millis smallint;
go
update systemlog set millis = 0;
go
alter table systemlog alter column millis smallint not null;
go

alter table rawpointhistory add millis smallint;
go
update rawpointhistory set millis = 0;
go
alter table rawpointhistory alter column millis smallint not null;
go

alter table dynamicpointdispatch add millis smallint;
go
update dynamicpointdispatch set millis = 0;
go
alter table dynamicpointdispatch alter column millis smallint not null;
go



/* Default user for STARS customers who don't have a login */
insert into YukonUser values (-9999,'(none)','(none)',0,'01-JAN-2000','Disabled');

alter table YukonWebConfiguration alter column AlternateDisplayName varchar(100);

/* energy company role properties */
update YukonRoleProperty set Description='(Deprecated) Use this value as the admin email address only if the energy company''s primary contact doesn''t have an email address.' where RolePropertyId=-1100;
update YukonRoleProperty set DefaultValue='override@cannontech.com' where RolePropertyID=-1101;

/* web client role properties */
update YukonRoleProperty set DefaultValue='yukon/CannonStyle.css' where RolePropertyId=-10802 and DefaultValue='CannonStyle.css';
update YukonRoleProperty set DefaultValue='yukon/Bullet.gif' where RolePropertyId=-10803 and DefaultValue='Bullet.gif';
update YukonRoleProperty set DefaultValue='yukon/BulletExpand.gif' where RolePropertyId=-10804 and DefaultValue='BulletExpand.gif';
update YukonRoleProperty set DefaultValue='yukon/DemoHeader.gif' where RolePropertyId=-10805 and DefaultValue='DemoHeader.gif';
update YukonRoleProperty set DefaultValue='yukon/BottomConnector.gif' where RolePropertyId=-10807 and DefaultValue='BottomConnector.gif';
update YukonRoleProperty set DefaultValue='yukon/MidConnector.gif' where RolePropertyId=-10808 and DefaultValue='MidConnector.gif';

/* operator administrator role properties */
insert into YukonRoleProperty values(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');

/* operator consumer info role properties */
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-20152;
/* @error ignore */
insert into YukonRoleProperty values(-20157,-201,'Opt Out Rules','(none)','Defines the rules for opting out.');

/* residential customer role properties */
insert into YukonRoleProperty values(-40055,-400,'Opt Out Rules','(none)','Defines the rules for opting out.');
update YukonRoleProperty set KeyName='Description Enrollment', DefaultValue='Select the check boxes and corresponding radio button of the programs you would like to be enrolled in.', Description='Description on the program enrollment page' where RolePropertyId=-40172;
update YukonRoleProperty set DefaultValue='yukon/Mom.jpg' where RolePropertyId=-40180 and DefaultValue='Mom.jpg';
update YukonRoleProperty set DefaultValue='yukon/Family.jpg' where RolePropertyId=-40181 and DefaultValue='Family.jpg';

/* residential customers group */
insert into yukongrouprole values (555,-300,-400,-40055,'(none)');
update YukonGroupRole set Value='yukon/DemoHeaderCES.gif' where Value='DemoHeaderCES.gif';

/* web client operators group */
insert into yukongrouprole values (757,-301,-201,-20157,'(none)');
update YukonGroupRole set Value='(none)' where RolePropertyID=-20152 and Value='false';



update yukonrole set category = 'Yukon' where roleid = -104;
update yukonrole set roledescription = 'Calculation HIstorical. Edit this role from the Yukon SetUp page.' where roleid = -104;
update yukonrole set category = 'Yukon' where roleid = -105;
update yukonrole set roledescription = 'Web Graph. Edit this role from the Yukon SetUp page.' where roleid = -105;
update yukonrole set category = 'Yukon' where roleid = -106;
update yukonrole set roledescription = 'Billing. Edit this role from the Yukon SetUp page.' where roleid = -106;
update yukonrole set category = 'Operator' where roleid = -2;
update yukonrole set roledescription = 'Default Yukon role. Edit this role from the Yukon SetUp page.' where roleid = -1;
go


delete from YukonGroupRole where roleid=-104;
delete from YukonGroupRole where roleid=-105;
delete from YukonGroupRole where roleid=-106;


delete from yukonroleproperty where roleid=-303;
delete from yukonrole where roleid=-303;


delete from YukonUserRole where userid=-1;

insert into YukonRoleProperty values(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');
go

/* Database Editor */
insert into YukonUserRole values(100,-1,-100,-10000,'(none)');
insert into YukonUserRole values(101,-1,-100,-10001,'(none)');
insert into YukonUserRole values(102,-1,-100,-10002,'(none)');
insert into YukonUserRole values(103,-1,-100,-10003,'(none)');
insert into YukonUserRole values(104,-1,-100,-10004,'(none)');
insert into YukonUserRole values(105,-1,-100,-10005,'(none)');
insert into YukonUserRole values(106,-1,-100,-10006,'(none)');
insert into YukonUserRole values(107,-1,-100,-10007,'(none)');

/* TDC */
insert into YukonUserRole values(120,-1,-101,-10100,'(none)');
insert into YukonUserRole values(121,-1,-101,-10101,'(none)');
insert into YukonUserRole values(122,-1,-101,-10102,'(none)');
insert into YukonUserRole values(123,-1,-101,-10103,'(none)');
insert into YukonUserRole values(124,-1,-101,-10104,'(none)');
insert into YukonUserRole values(125,-1,-101,-10105,'(none)');
insert into YukonUserRole values(126,-1,-101,-10106,'(none)');
insert into YukonUserRole values(127,-1,-101,-10107,'(none)');
insert into YukonUserRole values(128,-1,-101,-10108,'(none)');
insert into YukonUserRole values(129,-1,-101,-10109,'(none)');
insert into YukonUserRole values(130,-1,-101,-10111,'(none)');

/* Trending */
insert into YukonUserRole values(150,-1,-102,-10200,'(none)');
insert into YukonUserRole values(151,-1,-102,-10201,'(none)');

/* Commander */
insert into YukonUserRole values(170,-1,-103,-10300,'(none)');

/* Esubstation Editor */
insert into YukonUserRole values(250,-1,-107,-10700,'(none)');

insert into YukonUserRole values(300,-1,-206,-20600,'(none)');
insert into YukonUserRole values(301,-1,-206,-20601,'(none)');
insert into YukonUserRole values(302,-1,-206,-20602,'(none)');

insert into YukonUserRole values(350,-1,-206,-20600,'(none)');
insert into YukonUserRole values(351,-1,-206,-20601,'true');
insert into YukonUserRole values(352,-1,-206,-20602,'false');

/* Web Client Customers Web Client role */
insert into YukonUserRole values (400, -1, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonUserRole values (401, -1, -108, -10801, '(none)');
insert into YukonUserRole values (402, -1, -108, -10802, '(none)');
insert into YukonUserRole values (403, -1, -108, -10803, '(none)');
insert into YukonUserRole values (404, -1, -108, -10804, '(none)');
insert into YukonUserRole values (405, -1, -108, -10805, '(none)');
insert into YukonUserRole values (406, -1, -108, -10806, '(none)');
insert into YukonUserRole values (407, -1, -108, -10807, '(none)');
insert into YukonUserRole values (408, -1, -108, -10808, '(none)');

/* Web Client Customers Direct Load Control role */
insert into YukonUserRole values (410, -1, -300, -30000, '(none)');
insert into YukonUserRole values (411, -1, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into YukonUserRole values (420, -1, -301, -30100, '(none)');
insert into YukonUserRole values (421, -1, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into YukonUserRole values (430, -1, -302, -30200, '(none)');
insert into YukonUserRole values (431, -1, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into YukonUserRole values (440, -1, -304, -30400, '(none)');
insert into YukonUserRole values (441, -1, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into YukonUserRole values (450, -1, -305, -30500, 'true');

insert into YukonUserRole values (520,-1,-400,-40000,'(none)');
insert into YukonUserRole values (521,-1,-400,-40001,'(none)');
insert into YukonUserRole values (522,-1,-400,-40002,'(none)');
insert into YukonUserRole values (523,-1,-400,-40003,'(none)');
insert into YukonUserRole values (524,-1,-400,-40004,'(none)');
insert into YukonUserRole values (525,-1,-400,-40005,'(none)');
insert into YukonUserRole values (526,-1,-400,-40006,'(none)');
insert into YukonUserRole values (527,-1,-400,-40007,'(none)');
insert into YukonUserRole values (528,-1,-400,-40008,'(none)');
insert into YukonUserRole values (529,-1,-400,-40009,'(none)');
insert into YukonUserRole values (530,-1,-400,-40010,'(none)');
insert into YukonUserRole values (550,-1,-400,-40050,'(none)');
insert into YukonUserRole values (551,-1,-400,-40051,'(none)');
insert into YukonUserRole values (552,-1,-400,-40052,'(none)');
insert into YukonUserRole values (554,-1,-400,-40054,'(none)');
insert into YukonUserRole values (555,-1,-400,-40055,'(none)');
insert into YukonUserRole values (600,-1,-400,-40100,'(none)');
insert into YukonUserRole values (601,-1,-400,-40101,'(none)');
insert into YukonUserRole values (602,-1,-400,-40102,'(none)');
insert into YukonUserRole values (610,-1,-400,-40110,'(none)');
insert into YukonUserRole values (611,-1,-400,-40111,'(none)');
insert into YukonUserRole values (612,-1,-400,-40112,'(none)');
insert into YukonUserRole values (613,-1,-400,-40113,'(none)');
insert into YukonUserRole values (614,-1,-400,-40114,'(none)');
insert into YukonUserRole values (615,-1,-400,-40115,'(none)');
insert into YukonUserRole values (616,-1,-400,-40116,'(none)');
insert into YukonUserRole values (617,-1,-400,-40117,'(none)');
insert into YukonUserRole values (630,-1,-400,-40130,'(none)');
insert into YukonUserRole values (631,-1,-400,-40131,'(none)');
insert into YukonUserRole values (632,-1,-400,-40132,'(none)');
insert into YukonUserRole values (633,-1,-400,-40133,'(none)');
insert into YukonUserRole values (634,-1,-400,-40134,'(none)');
insert into YukonUserRole values (650,-1,-400,-40150,'(none)');
insert into YukonUserRole values (651,-1,-400,-40151,'(none)');
insert into YukonUserRole values (652,-1,-400,-40152,'(none)');
insert into YukonUserRole values (653,-1,-400,-40153,'(none)');
insert into YukonUserRole values (654,-1,-400,-40154,'(none)');
insert into YukonUserRole values (655,-1,-400,-40155,'(none)');
insert into YukonUserRole values (656,-1,-400,-40156,'(none)');
insert into YukonUserRole values (657,-1,-400,-40157,'(none)');
insert into YukonUserRole values (658,-1,-400,-40158,'(none)');
insert into YukonUserRole values (670,-1,-400,-40170,'(none)');
insert into YukonUserRole values (671,-1,-400,-40171,'(none)');
insert into YukonUserRole values (672,-1,-400,-40172,'(none)');
insert into YukonUserRole values (673,-1,-400,-40173,'(none)');
insert into YukonUserRole values (680,-1,-400,-40180,'(none)');
insert into YukonUserRole values (681,-1,-400,-40181,'(none)');

insert into YukonUserRole values (720,-1,-201,-20100,'(none)');
insert into YukonUserRole values (721,-1,-201,-20101,'(none)');
insert into YukonUserRole values (722,-1,-201,-20102,'(none)');
insert into YukonUserRole values (723,-1,-201,-20103,'(none)');
insert into YukonUserRole values (724,-1,-201,-20104,'(none)');
insert into YukonUserRole values (725,-1,-201,-20105,'(none)');
insert into YukonUserRole values (726,-1,-201,-20106,'(none)');
insert into YukonUserRole values (727,-1,-201,-20107,'(none)');
insert into YukonUserRole values (728,-1,-201,-20108,'(none)');
insert into YukonUserRole values (729,-1,-201,-20109,'(none)');
insert into YukonUserRole values (730,-1,-201,-20110,'(none)');
insert into YukonUserRole values (731,-1,-201,-20111,'(none)');
insert into YukonUserRole values (732,-1,-201,-20112,'(none)');
insert into YukonUserRole values (733,-1,-201,-20113,'(none)');
insert into YukonUserRole values (734,-1,-201,-20114,'(none)');
insert into YukonUserRole values (735,-1,-201,-20115,'(none)');
insert into YukonUserRole values (736,-1,-201,-20116,'(none)');
insert into YukonUserRole values (737,-1,-201,-20117,'(none)');

insert into YukonUserRole values (750,-1,-201,-20150,'(none)');
insert into YukonUserRole values (751,-1,-201,-20151,'(none)');
insert into YukonUserRole values (752,-1,-201,-20152,'(none)');
insert into YukonUserRole values (753,-1,-201,-20153,'(none)');
insert into YukonUserRole values (754,-1,-201,-20154,'(none)');
insert into YukonUserRole values (755,-1,-201,-20155,'(none)');
insert into YukonUserRole values (756,-1,-201,-20156,'(none)');
insert into YukonUserRole values (757,-1,-201,-20157,'(none)');
insert into YukonUserRole values (765,-1,-210,-21000,'(none)');
insert into YukonUserRole values (766,-1,-210,-21001,'(none)');
insert into YukonUserRole values (770,-1,-202,-20200,'(none)');
insert into YukonUserRole values (775,-1,-203,-20300,'(none)');
insert into YukonUserRole values (776,-1,-203,-20301,'(none)');
insert into YukonUserRole values (780,-1,-204,-20400,'(none)');
insert into YukonUserRole values (785,-1,-205,-20500,'(none)');
insert into YukonUserRole values (790,-1,-207,-20700,'(none)');
insert into YukonUserRole values (795,-1,-209,-20904,'(none)');
insert into YukonUserRole values (800,-1,-201,-20800,'(none)');
insert into YukonUserRole values (801,-1,-201,-20801,'(none)');
insert into YukonUserRole values (810,-1,-201,-20810,'(none)');
insert into YukonUserRole values (813,-1,-201,-20813,'(none)');
insert into YukonUserRole values (814,-1,-201,-20814,'(none)');
insert into YukonUserRole values (815,-1,-201,-20815,'(none)');
insert into YukonUserRole values (816,-1,-201,-20816,'(none)');
insert into YukonUserRole values (819,-1,-201,-20819,'(none)');
insert into YukonUserRole values (820,-1,-201,-20820,'(none)');
insert into YukonUserRole values (830,-1,-201,-20830,'(none)');
insert into YukonUserRole values (831,-1,-201,-20831,'(none)');
insert into YukonUserRole values (832,-1,-201,-20832,'(none)');
insert into YukonUserRole values (833,-1,-201,-20833,'(none)');
insert into YukonUserRole values (834,-1,-201,-20834,'(none)');
insert into YukonUserRole values (850,-1,-201,-20850,'(none)');
insert into YukonUserRole values (851,-1,-201,-20851,'(none)');
insert into YukonUserRole values (852,-1,-201,-20852,'(none)');
insert into YukonUserRole values (853,-1,-201,-20853,'(none)');
insert into YukonUserRole values (854,-1,-201,-20854,'(none)');
insert into YukonUserRole values (855,-1,-201,-20855,'(none)');
insert into YukonUserRole values (856,-1,-201,-20856,'(none)');
insert into YukonUserRole values (870,-1,-201,-20870,'(none)');




insert into YukonRole values(-3,'Logging','Yukon','Settings for how Yukon logs output. Edit this role from the Yukon SetUp page.');
go

/* Yukon Logging Role Properties */
insert into YukonRoleProperty values(-1200,-3,'dbeditor_log_level','INFO','Logging level for DBEditor functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1201,-3,'database_log_level','INFO','Logging level for the Database. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1202,-3,'tdc_log_level','INFO','Logging level for TDC functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1203,-3,'commander_log_level','INFO','Logging level for Yukon Commander functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1204,-3,'billing_log_level','INFO','Logging level for Billing functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1205,-3,'calchist_log_level','INFO','Logging level for Calc Historical functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1206,-3,'cap_control_log_level','INFO','Logging level for Cap Control functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1207,-3,'esub_log_level','INFO','Logging level for Esubstation functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1208,-3,'export_log_level','INFO','Logging level for Export functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1209,-3,'load_control_log_level','INFO','Logging level for Load Control functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1210,-3,'macs_log_level','INFO','Logging level for MACS functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1211,-3,'notification_log_level','INFO','Logging level for Notification functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1212,-3,'reporting_log_level','INFO','Logging level for Reporting functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1213,-3,'trending_log_level','INFO','Logging level for Trending functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1214,-3,'stars_log_level','INFO','Logging level for STARS functionality. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
insert into YukonRoleProperty values(-1215,-3,'general_log_level','INFO','Logging level for all functionality that is not otherwise defined. Possible values are OFF, FATAL, ERROR, WARN, INFO, DEBUG, or ALL');
go

/* Logging default Yukon group */
insert into YukonGroupRole values(50,-1,-3,-1200,'(none)');
insert into YukonGroupRole values(51,-1,-3,-1201,'(none)');
insert into YukonGroupRole values(52,-1,-3,-1202,'(none)');
insert into YukonGroupRole values(53,-1,-3,-1203,'(none)');
insert into YukonGroupRole values(54,-1,-3,-1204,'(none)');
insert into YukonGroupRole values(55,-1,-3,-1205,'(none)');
insert into YukonGroupRole values(56,-1,-3,-1206,'(none)');
insert into YukonGroupRole values(57,-1,-3,-1207,'(none)');
insert into YukonGroupRole values(58,-1,-3,-1208,'(none)');
insert into YukonGroupRole values(59,-1,-3,-1209,'(none)');
insert into YukonGroupRole values(60,-1,-3,-1210,'(none)');
insert into YukonGroupRole values(61,-1,-3,-1211,'(none)');
insert into YukonGroupRole values(62,-1,-3,-1212,'(none)');
insert into YukonGroupRole values(63,-1,-3,-1213,'(none)');
insert into YukonGroupRole values(64,-1,-3,-1214,'(none)');
insert into YukonGroupRole values(65,-1,-3,-1215,'(none)');
go


/* Delete all the deprecated LOG_LEVEL */
delete from yukongrouprole where rolepropertyid = -10006;
delete from yukongrouprole where rolepropertyid = -10110;
delete from yukongrouprole where rolepropertyid = -10201;
delete from yukongrouprole where rolepropertyid = -10403;
delete from yukongrouprole where rolepropertyid = -10502;
delete from yukongrouprole where rolepropertyid = -10602;
delete from yukongrouprole where rolepropertyid = -10801;
go

delete from yukonuserrole where rolepropertyid = -10006;
delete from yukonuserrole where rolepropertyid = -10110;
delete from yukonuserrole where rolepropertyid = -10201;
delete from yukonuserrole where rolepropertyid = -10403;
delete from yukonuserrole where rolepropertyid = -10502;
delete from yukonuserrole where rolepropertyid = -10602;
delete from yukonuserrole where rolepropertyid = -10801;
go

delete from yukonroleproperty where rolepropertyid = -10006;
delete from yukonroleproperty where rolepropertyid = -10110;
delete from yukonroleproperty where rolepropertyid = -10201;
delete from yukonroleproperty where rolepropertyid = -10403;
delete from yukonroleproperty where rolepropertyid = -10502;
delete from yukonroleproperty where rolepropertyid = -10602;
delete from yukonroleproperty where rolepropertyid = -10801;
go

/* Update the roles that only have 1 property, the deprecated LOG_LEVEL */
update YukonRoleProperty set keyname='msg_priority', defaultvalue='14', description='Tells commander what the outbound priority of messages are (low)1 - 14(high)' where rolepropertyid=-10300;
update YukonRoleProperty set keyname='default', defaultvalue='false', description='The default esub editor property' where rolepropertyid=-10700;
go


insert into YukonRoleProperty values(-1010,-1,'smtp_host','127.0.0.1','Name or IP address of the mail server');
insert into YukonRoleProperty values(-1011,-1,'mail_from_address','yukon@cannontech.com','Name of the FROM email address the mail server will use');
insert into YukonRoleProperty values(-1012,-1,'print_insert_sql','(none)','File name of where to print all SQL insert statements');
insert into YukonRoleProperty values(-1013,-1,'stars_soap_server','(none)','Where the soap server is running, the default value is the local host');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
insert into YukonRoleProperty values(-1216,-3,'log_to_file','false','Tells all logging that it needs to go to a file');
go


insert into YukonGroupRole values(11,-1,-1,-1010,'(none)');
insert into YukonGroupRole values(12,-1,-1,-1011,'(none)');
insert into YukonGroupRole values(13,-1,-1,-1012,'(none)');
insert into YukonGroupRole values(14,-1,-1,-1013,'(none)');
insert into YukonGroupRole values(15,-1,-1,-1014,'CannonLogo.gif');
insert into YukonGroupRole values(66,-1,-3,-1216,'(none)');


/*Add the CICustomer user-control role*/
insert into yukonrole values (-306, 'User Control', 'CICustomer', 'Customer access to user control operations.');

/*Add the CICustomer user-control properties */
insert into yukonroleproperty values(-30600, -306, 'User Control Label', 'User-Control', 'The customer specific name for user control');
insert into yukonroleproperty values(-30601, -306, 'Auto Control', 'true', 'Controls access to auto control.');
insert into yukonroleproperty values(-30602, -306, 'Time Based Control', 'true', 'Controls access to time based control');
insert into yukonroleproperty values(-30603, -306, 'Switch Command Control', 'true', 'Controls acces to switch commands');

/*Add the user-control properties to the Web Client Customers group */
/* REPLACE the 985-989 ids with the proper grouproleid values */
insert into yukongrouprole values ( 985, -302, -306, -30600, '(none)');
insert into yukongrouprole values ( 986, -302, -306, -30601, 'true');
insert into yukongrouprole values ( 987, -302, -306, -30602, 'true');
insert into yukongrouprole values ( 988, -302, -306, -30603, 'true');


/* Start CapControl Changes */
alter table capbank add RecloseDelay numeric;
go
update capbank set RecloseDelay = 0;
go
alter table capbank alter column RecloseDelay numeric not null;
go

alter table dynamicccsubstationbus add WaiveControlFlag char(1);
go
update dynamicccsubstationbus set WaiveControlFlag = 'N';
go
alter table dynamicccsubstationbus alter column WaiveControlFlag char(1) not null;
go

alter table dynamicccfeeder add WaiveControlFlag char(1);
go
update dynamicccfeeder set WaiveControlFlag = 'N';
go
alter table dynamicccfeeder alter column WaiveControlFlag char(1) not null;
go

alter table capcontrolsubstationbus add ControlDelayTime numeric;
go
update capcontrolsubstationbus set ControlDelayTime = 0;
go
alter table capcontrolsubstationbus alter column ControlDelayTime numeric not null;
go

alter table capcontrolsubstationbus add ControlSendRetries numeric;
go
update capcontrolsubstationbus set ControlSendRetries = 0;
go
alter table capcontrolsubstationbus alter column ControlSendRetries numeric not null;
go


drop table SoeLog;
drop table TagLog;
go

create table TagLog (
LogID                numeric              not null,
InstanceID           numeric              not null,
PointID              numeric              not null,
TagID                numeric              not null,
UserName             varchar(60)          not null,
Action               varchar(20)          not null,
Description          varchar(120)         not null,
TagTime              datetime             not null,
RefStr               varchar(60)          not null,
ForStr               varchar(60)          not null
);
go
alter table TagLog
   add constraint PK_TAGLOG primary key  (LogID);
go
alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);
go
alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);
go


/** Just an assurance that all points have a pointalarming entry **/
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
select pointid,
'',
'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
'N', 1, 0  from point where pointid not in (select pointid from pointalarming);


insert into tags values(-1, 'Out Of Service', 1, 'Y', 1, 0);
insert into tags values(-2, 'Info', 1, 'N', 6, 0);


update columntype set name='State' where name='Tags';
update templatecolumns set title='State' where title='Tags';


update YukonRoleProperty set keyname='cap_control_interface' where rolepropertyid=-10105;
go

create table ActivityLog (
ActivityLogID        numeric              not null,
TimeStamp            datetime             not null,
UserID               numeric              null,
AccountID            numeric              null,
EnergyCompanyID      numeric              null,
CustomerID           numeric              null,
PaoID                numeric              null,
Action               varchar(80)          not null,
Description          varchar(120)         not null
);
go
alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key  (ActivityLogID);
go

insert into display values(15, 'Priority 11 Alarms', 'Alarms and Events', 'Priority 11 Alarm Viewer', 'This display will recieve all priority 11 alarm events as they happen in the system.');
insert into display values(16, 'Priority 12 Alarms', 'Alarms and Events', 'Priority 12 Alarm Viewer', 'This display will recieve all priority 12 alarm events as they happen in the system.');
insert into display values(17, 'Priority 13 Alarms', 'Alarms and Events', 'Priority 13 Alarm Viewer', 'This display will recieve all priority 13 alarm events as they happen in the system.');
insert into display values(18, 'Priority 14 Alarms', 'Alarms and Events', 'Priority 14 Alarm Viewer', 'This display will recieve all priority 14 alarm events as they happen in the system.');
insert into display values(19, 'Priority 15 Alarms', 'Alarms and Events', 'Priority 15 Alarm Viewer', 'This display will recieve all priority 15 alarm events as they happen in the system.');
insert into display values(20, 'Priority 16 Alarms', 'Alarms and Events', 'Priority 16 Alarm Viewer', 'This display will recieve all priority 16 alarm events as they happen in the system.');
insert into display values(21, 'Priority 17 Alarms', 'Alarms and Events', 'Priority 17 Alarm Viewer', 'This display will recieve all priority 17 alarm events as they happen in the system.');
insert into display values(22, 'Priority 18 Alarms', 'Alarms and Events', 'Priority 18 Alarm Viewer', 'This display will recieve all priority 18 alarm events as they happen in the system.');
insert into display values(23, 'Priority 19 Alarms', 'Alarms and Events', 'Priority 19 Alarm Viewer', 'This display will recieve all priority 19 alarm events as they happen in the system.');
insert into display values(24, 'Priority 20 Alarms', 'Alarms and Events', 'Priority 20 Alarm Viewer', 'This display will recieve all priority 20 alarm events as they happen in the system.');
insert into display values(25, 'Priority 21 Alarms', 'Alarms and Events', 'Priority 21 Alarm Viewer', 'This display will recieve all priority 21 alarm events as they happen in the system.');
insert into display values(26, 'Priority 22 Alarms', 'Alarms and Events', 'Priority 22 Alarm Viewer', 'This display will recieve all priority 22 alarm events as they happen in the system.');
insert into display values(27, 'Priority 23 Alarms', 'Alarms and Events', 'Priority 23 Alarm Viewer', 'This display will recieve all priority 23 alarm events as they happen in the system.');
insert into display values(28, 'Priority 24 Alarms', 'Alarms and Events', 'Priority 24 Alarm Viewer', 'This display will recieve all priority 24 alarm events as they happen in the system.');
insert into display values(29, 'Priority 25 Alarms', 'Alarms and Events', 'Priority 25 Alarm Viewer', 'This display will recieve all priority 25 alarm events as they happen in the system.');
insert into display values(30, 'Priority 26 Alarms', 'Alarms and Events', 'Priority 26 Alarm Viewer', 'This display will recieve all priority 26 alarm events as they happen in the system.');
insert into display values(31, 'Priority 27 Alarms', 'Alarms and Events', 'Priority 27 Alarm Viewer', 'This display will recieve all priority 27 alarm events as they happen in the system.');
insert into display values(32, 'Priority 28 Alarms', 'Alarms and Events', 'Priority 28 Alarm Viewer', 'This display will recieve all priority 28 alarm events as they happen in the system.');
insert into display values(33, 'Priority 29 Alarms', 'Alarms and Events', 'Priority 29 Alarm Viewer', 'This display will recieve all priority 29 alarm events as they happen in the system.');
insert into display values(34, 'Priority 30 Alarms', 'Alarms and Events', 'Priority 30 Alarm Viewer', 'This display will recieve all priority 30 alarm events as they happen in the system.');
insert into display values(35, 'Priority 31 Alarms', 'Alarms and Events', 'Priority 31 Alarm Viewer', 'This display will recieve all priority 31 alarm events as they happen in the system.');
go

insert into displaycolumns values(15, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(15, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(15, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(15, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(15, 'User Name', 8, 5, 50 );

insert into displaycolumns values(16, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(16, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(16, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(16, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(16, 'User Name', 8, 5, 50 );

insert into displaycolumns values(17, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(17, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(17, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(17, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(17, 'User Name', 8, 5, 50 );

insert into displaycolumns values(18, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(18, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(18, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(18, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(18, 'User Name', 8, 5, 50 );

insert into displaycolumns values(19, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(19, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(19, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(19, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(19, 'User Name', 8, 5, 50 );

insert into displaycolumns values(20, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(20, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(20, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(20, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(20, 'User Name', 8, 5, 50 );

insert into displaycolumns values(21, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(21, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(21, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(21, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(21, 'User Name', 8, 5, 50 );

insert into displaycolumns values(22, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(22, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(22, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(22, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(22, 'User Name', 8, 5, 50 );

insert into displaycolumns values(23, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(23, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(23, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(23, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(23, 'User Name', 8, 5, 50 );

insert into displaycolumns values(24, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(24, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(24, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(24, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(24, 'User Name', 8, 5, 50 );

insert into displaycolumns values(25, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(25, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(25, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(25, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(25, 'User Name', 8, 5, 50 );

insert into displaycolumns values(26, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(26, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(26, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(26, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(26, 'User Name', 8, 5, 50 );

insert into displaycolumns values(27, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(27, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(27, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(27, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(27, 'User Name', 8, 5, 50 );

insert into displaycolumns values(28, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(28, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(28, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(28, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(28, 'User Name', 8, 5, 50 );

insert into displaycolumns values(29, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(29, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(29, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(29, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(29, 'User Name', 8, 5, 50 );

insert into displaycolumns values(30, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(30, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(30, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(30, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(30, 'User Name', 8, 5, 50 );

insert into displaycolumns values(31, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(31, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(31, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(31, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(31, 'User Name', 8, 5, 50 );

insert into displaycolumns values(32, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(32, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(32, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(32, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(32, 'User Name', 8, 5, 50 );

insert into displaycolumns values(33, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(33, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(33, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(33, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(33, 'User Name', 8, 5, 50 );

insert into displaycolumns values(34, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(34, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(34, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(34, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(34, 'User Name', 8, 5, 50 );

insert into displaycolumns values(35, 'Time Stamp', 11, 1, 90 );
insert into displaycolumns values(35, 'Device Name', 5, 2, 90 );
insert into displaycolumns values(35, 'Point Name', 2, 3, 90 );
insert into displaycolumns values(35, 'Text Message', 12, 4, 200 );
insert into displaycolumns values(35, 'User Name', 8, 5, 50 );

update graphdataseries set moredata = '1072936800000' where moredata = '(none)' and type = 17;


delete from YukonUserGroup where userid = -1 and groupid = -100;

update YukonGroup set groupname = 'Yukon Grp' where groupid = -1;
update YukonGroup set groupname = 'Operators Grp' where groupid= -100;
update YukonGroup set groupname = 'Esub Users Grp' where groupid= -200;
update YukonGroup set groupname = 'Esub Operators Grp' where groupid= -201;
update YukonGroup set groupname = 'Web Client Operators Grp' where groupid= -301;
update YukonGroup set groupname = 'Residential Customers Grp' where groupid= -300;
update YukonGroup set groupname = 'Web Client Customers Grp' where groupid= -302;



update YukonRoleProperty set defaultvalue = 'false' where rolepropertyid = -10000;
go

update YukonGroupRole set grouproleid=grouproleid * -1 where grouproleid > 0;
go

update YukonUserRole set userroleid=userroleid * -1 where userroleid > 0;
go

insert into YukonGroup values(-2,'System Administrator Grp','A set of roles that allow administrative access to the system.');
insert into YukonUserGroup values(-2,-2);
insert into YukonUserGroup values(-2,-1);
go


/* START the Admin role Group */
insert into YukonGroupRole values(-1000,-2,-100,-10000,'(none)');
insert into YukonGroupRole values(-1001,-2,-100,-10001,'(none)');
insert into YukonGroupRole values(-1002,-2,-100,-10002,'(none)');
insert into YukonGroupRole values(-1003,-2,-100,-10003,'(none)');
insert into YukonGroupRole values(-1004,-2,-100,-10004,'(none)');
insert into YukonGroupRole values(-1005,-2,-100,-10005,'(none)');
insert into YukonGroupRole values(-1007,-2,-100,-10007,'(none)');

insert into YukonGroupRole values(-1020,-2,-101,-10100,'(none)');
insert into YukonGroupRole values(-1021,-2,-101,-10101,'(none)');
insert into YukonGroupRole values(-1022,-2,-101,-10102,'(none)');
insert into YukonGroupRole values(-1023,-2,-101,-10103,'(none)');
insert into YukonGroupRole values(-1024,-2,-101,-10104,'(none)');
insert into YukonGroupRole values(-1025,-2,-101,-10105,'(none)');
insert into YukonGroupRole values(-1026,-2,-101,-10106,'(none)');
insert into YukonGroupRole values(-1027,-2,-101,-10107,'(none)');
insert into YukonGroupRole values(-1028,-2,-101,-10108,'(none)');
insert into YukonGroupRole values(-1029,-2,-101,-10109,'(none)');
insert into YukonGroupRole values(-1030,-2,-101,-10111,'(none)');

insert into YukonGroupRole values(-1050,-2,-102,-10200,'(none)');

insert into YukonGroupRole values(-1070,-2,-103,-10300,'(none)');

insert into YukonGroupRole values(-1080,-2,-107,-10700,'(none)');
insert into YukonGroupRole values(-1081,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1082,-2,-206,-20601,'(none)');
insert into YukonGroupRole values(-1083,-2,-206,-20602,'(none)');
insert into YukonGroupRole values(-1084,-2,-206,-20600,'(none)');
insert into YukonGroupRole values(-1085,-2,-206,-20601,'true');
insert into YukonGroupRole values(-1086,-2,-206,-20602,'false');

insert into YukonGroupRole values (-1090,-2, -108, -10800, '/user/CILC/user_trending.jsp');
insert into YukonGroupRole values (-1091,-2, -108, -10802, '(none)');
insert into YukonGroupRole values (-1092,-2, -108, -10803, '(none)');
insert into YukonGroupRole values (-1093,-2, -108, -10804, '(none)');
insert into YukonGroupRole values (-1094,-2, -108, -10805, '(none)');
insert into YukonGroupRole values (-1095,-2, -108, -10806, '(none)');
insert into YukonGroupRole values (-1096,-2, -108, -10807, '(none)');
insert into YukonGroupRole values (-1097,-2, -108, -10808, '(none)');

insert into YukonGroupRole values (-1100,-2, -300, -30000, '(none)');
insert into YukonGroupRole values (-1101,-2, -300, -30001, 'true');

insert into YukonGroupRole values (-1110,-2, -301, -30100, '(none)');
insert into YukonGroupRole values (-1111,-2, -301, -30101, '(none)');

insert into YukonGroupRole values (-1120,-2, -302, -30200, '(none)');
insert into YukonGroupRole values (-1121,-2, -302, -30200, '(none)');

insert into YukonGroupRole values (-1130,-2, -304, -30400, '(none)');
insert into YukonGroupRole values (-1131,-2, -304, -30401, 'true');

insert into YukonGroupRole values (-1140,-2, -305, -30500, 'true');
insert into YukonGroupRole values (-1141,-2,-400,-40000,'(none)');
insert into YukonGroupRole values (-1142,-2,-400,-40001,'(none)');
insert into YukonGroupRole values (-1143,-2,-400,-40002,'(none)');
insert into YukonGroupRole values (-1144,-2,-400,-40003,'(none)');
insert into YukonGroupRole values (-1145,-2,-400,-40004,'(none)');
insert into YukonGroupRole values (-1146,-2,-400,-40005,'(none)');
insert into YukonGroupRole values (-1147,-2,-400,-40006,'(none)');
insert into YukonGroupRole values (-1148,-2,-400,-40007,'(none)');
insert into YukonGroupRole values (-1149,-2,-400,-40008,'(none)');
insert into YukonGroupRole values (-1150,-2,-400,-40009,'(none)');
insert into YukonGroupRole values (-1151,-2,-400,-40010,'(none)');
insert into YukonGroupRole values (-1152,-2,-400,-40050,'(none)');
insert into YukonGroupRole values (-1153,-2,-400,-40051,'(none)');
insert into YukonGroupRole values (-1154,-2,-400,-40052,'(none)');
insert into YukonGroupRole values (-1155,-2,-400,-40054,'(none)');
insert into YukonGroupRole values (-1156,-2,-400,-40055,'(none)');

insert into YukonGroupRole values (-1157,-2,-400,-40100,'(none)');
insert into YukonGroupRole values (-1158,-2,-400,-40101,'(none)');
insert into YukonGroupRole values (-1159,-2,-400,-40110,'(none)');
insert into YukonGroupRole values (-1160,-2,-400,-40111,'(none)');
insert into YukonGroupRole values (-1161,-2,-400,-40112,'(none)');
insert into YukonGroupRole values (-1162,-2,-400,-40113,'(none)');
insert into YukonGroupRole values (-1163,-2,-400,-40114,'(none)');
insert into YukonGroupRole values (-1164,-2,-400,-40115,'(none)');
insert into YukonGroupRole values (-1165,-2,-400,-40116,'(none)');
insert into YukonGroupRole values (-1166,-2,-400,-40117,'(none)');
insert into YukonGroupRole values (-1167,-2,-400,-40130,'(none)');
insert into YukonGroupRole values (-1168,-2,-400,-40131,'(none)');
insert into YukonGroupRole values (-1169,-2,-400,-40132,'(none)');
insert into YukonGroupRole values (-1170,-2,-400,-40133,'(none)');
insert into YukonGroupRole values (-1171,-2,-400,-40134,'(none)');
insert into YukonGroupRole values (-1172,-2,-400,-40150,'(none)');
insert into YukonGroupRole values (-1173,-2,-400,-40151,'(none)');
insert into YukonGroupRole values (-1174,-2,-400,-40152,'(none)');
insert into YukonGroupRole values (-1175,-2,-400,-40153,'(none)');
insert into YukonGroupRole values (-1176,-2,-400,-40154,'(none)');
insert into YukonGroupRole values (-1177,-2,-400,-40155,'(none)');
insert into YukonGroupRole values (-1178,-2,-400,-40156,'(none)');
insert into YukonGroupRole values (-1179,-2,-400,-40157,'(none)');
insert into YukonGroupRole values (-1180,-2,-400,-40158,'(none)');
insert into YukonGroupRole values (-1181,-2,-400,-40170,'(none)');
insert into YukonGroupRole values (-1182,-2,-400,-40171,'(none)');
insert into YukonGroupRole values (-1183,-2,-400,-40172,'(none)');
insert into YukonGroupRole values (-1184,-2,-400,-40173,'(none)');
insert into YukonGroupRole values (-1185,-2,-400,-40180,'(none)');
insert into YukonGroupRole values (-1186,-2,-400,-40181,'(none)');

insert into YukonGroupRole values (-1187,-2,-201,-20100,'(none)');
insert into YukonGroupRole values (-1188,-2,-201,-20101,'(none)');
insert into YukonGroupRole values (-1189,-2,-201,-20102,'(none)');
insert into YukonGroupRole values (-1190,-2,-201,-20103,'(none)');
insert into YukonGroupRole values (-1191,-2,-201,-20104,'(none)');
insert into YukonGroupRole values (-1192,-2,-201,-20105,'(none)');
insert into YukonGroupRole values (-1193,-2,-201,-20106,'(none)');
insert into YukonGroupRole values (-1194,-2,-201,-20107,'(none)');
insert into YukonGroupRole values (-1195,-2,-201,-20108,'(none)');
insert into YukonGroupRole values (-1196,-2,-201,-20109,'(none)');
insert into YukonGroupRole values (-1197,-2,-201,-20110,'(none)');
insert into YukonGroupRole values (-1198,-2,-201,-20111,'(none)');
insert into YukonGroupRole values (-1199,-2,-201,-20112,'(none)');
insert into YukonGroupRole values (-1200,-2,-201,-20113,'(none)');
insert into YukonGroupRole values (-1201,-2,-201,-20114,'(none)');
insert into YukonGroupRole values (-1202,-2,-201,-20115,'(none)');
insert into YukonGroupRole values (-1203,-2,-201,-20116,'(none)');
insert into YukonGroupRole values (-1204,-2,-201,-20117,'(none)');

insert into YukonGroupRole values (-1250,-2,-201,-20150,'(none)');
insert into YukonGroupRole values (-1251,-2,-201,-20151,'(none)');
insert into YukonGroupRole values (-1252,-2,-201,-20152,'(none)');
insert into YukonGroupRole values (-1253,-2,-201,-20153,'(none)');
insert into YukonGroupRole values (-1254,-2,-201,-20154,'(none)');
insert into YukonGroupRole values (-1255,-2,-201,-20155,'(none)');
insert into YukonGroupRole values (-1256,-2,-201,-20156,'(none)');
insert into YukonGroupRole values (-1257,-2,-201,-20157,'(none)');

insert into YukonGroupRole values (-1265,-2,-210,-21000,'(none)');
insert into YukonGroupRole values (-1266,-2,-210,-21001,'(none)');

insert into YukonGroupRole values (-1270,-2,-202,-20200,'(none)');
insert into YukonGroupRole values (-1275,-2,-203,-20300,'(none)');
insert into YukonGroupRole values (-1276,-2,-203,-20301,'(none)');
insert into YukonGroupRole values (-1280,-2,-204,-20400,'(none)');
insert into YukonGroupRole values (-1285,-2,-205,-20500,'(none)');
insert into YukonGroupRole values (-1290,-2,-207,-20700,'(none)');
insert into YukonGroupRole values (-1291,-2,-209,-20900,'(none)');
insert into YukonGroupRole values (-1292,-2,-209,-20901,'(none)');
insert into YukonGroupRole values (-1293,-2,-209,-20902,'(none)');
insert into YukonGroupRole values (-1294,-2,-209,-20903,'(none)');
insert into YukonGroupRole values (-1295,-2,-209,-20904,'(none)');

insert into YukonGroupRole values (-1300,-2,-201,-20800,'(none)');
insert into YukonGroupRole values (-1301,-2,-201,-20801,'(none)');
insert into YukonGroupRole values (-1310,-2,-201,-20810,'(none)');
insert into YukonGroupRole values (-1313,-2,-201,-20813,'(none)');
insert into YukonGroupRole values (-1314,-2,-201,-20814,'(none)');
insert into YukonGroupRole values (-1315,-2,-201,-20815,'(none)');
insert into YukonGroupRole values (-1316,-2,-201,-20816,'(none)');
insert into YukonGroupRole values (-1319,-2,-201,-20819,'(none)');
insert into YukonGroupRole values (-1320,-2,-201,-20820,'(none)');
insert into YukonGroupRole values (-1330,-2,-201,-20830,'(none)');
insert into YukonGroupRole values (-1331,-2,-201,-20831,'(none)');
insert into YukonGroupRole values (-1332,-2,-201,-20832,'(none)');
insert into YukonGroupRole values (-1333,-2,-201,-20833,'(none)');
insert into YukonGroupRole values (-1334,-2,-201,-20834,'(none)');
insert into YukonGroupRole values (-1350,-2,-201,-20850,'(none)');
insert into YukonGroupRole values (-1351,-2,-201,-20851,'(none)');
insert into YukonGroupRole values (-1352,-2,-201,-20852,'(none)');
insert into YukonGroupRole values (-1353,-2,-201,-20853,'(none)');
insert into YukonGroupRole values (-1354,-2,-201,-20854,'(none)');
insert into YukonGroupRole values (-1355,-2,-201,-20855,'(none)');
insert into YukonGroupRole values (-1356,-2,-201,-20856,'(none)');
insert into YukonGroupRole values (-1370,-2,-201,-20870,'(none)');
/* END the Admin role Group */


INSERT into point  values (-100, 'System', 'Threshold' , 0, 'Default', 0, 'N', 'N', 'S', 10 ,'None', 0);

insert into YukonRoleProperty values(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
insert into YukonRoleProperty values(-10009,-100,'allow_user_roles','false','Allows the editor panel individual user roles to be shown');
insert into YukonGroupRole values(-108,-100,-100,-10008,'(none)');
insert into YukonGroupRole values(-109,-100,-100,-10009,'(none)');
insert into YukonGroupRole values(-1008,-2,-100,-10008,'(none)');
insert into YukonGroupRole values(-1009,-2,-100,-10009,'(none)');
insert into YukonUserRole values(-108,-1,-100,-10008,'(none)');
insert into YukonUserRole values(-109,-1,-100,-10009,'true');


insert into YukonRoleProperty values(-20118,-201,'Create Trend','false','Controls whether to allow new trends to assigned to the customer');
insert into YukonUserRole values (-738,-1,-201,-20118,'true');
insert into YukonGroupRole values (-738,-301,-201,-20118,'true');
insert into YukonGroupRole values (-1205,-2,-201,-20118,'true');


alter table CALCCOMPONENT
   add constraint FK_ClcCmp_Pt foreign key (COMPONENTPOINTID)
      references POINT (POINTID);
go

alter table PAOExclusion add FuncParams varchar(200);
go
update PAOExclusion set FuncParams = '(none)';
go
alter table PAOExclusion alter column FuncParams varchar(200) not null;
go

delete from fdrinterfaceoption where interfaceid = 14;
delete from fdrinterface where InterfaceId = 14;
insert into fdrinterface values (16,'LODESTAR_STD','Receive','f');
insert into fdrinterface values (17,'LODESTAR_ENH','Receive','f');

delete from fdrinterfaceoption where interfaceid = 14;
insert into fdrinterfaceoption values (16,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (16,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (16,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (16,'Filename',4,'Text','(none)');
insert into fdrinterfaceoption values (17,'Customer',1,'Text','(none)');
insert into fdrinterfaceoption values (17,'Channel',2,'Text','(none)');
insert into fdrinterfaceoption values (17,'DrivePath',3,'Text','(none)');
insert into fdrinterfaceoption values (17,'Filename',4,'Text','(none)');

/* Stars tables initialization script */
insert into LMProgram values(0, 'Automatic', 'NNNN', 'NNNNNNNN', 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO Route VALUES (0,0,'N');
INSERT INTO Customer VALUES (-1,0,0,'(none)');

INSERT INTO YukonUser VALUES (-100,'DefaultCTI','$cti_default',0,'01-JAN-00','Enabled');
INSERT INTO EnergyCompany VALUES (-1,'Default Energy Company',0,-100);
INSERT INTO EnergyCompanyOperatorLoginList VALUES (-1,-100);

insert into YukonUserRole values (-1000, -100, -108, -10800, '/operator/Operations.jsp');
insert into YukonUserRole values (-1002, -100, -108, -10802, '(none)');
insert into YukonUserRole values (-1003, -100, -108, -10803, '(none)');
insert into YukonUserRole values (-1004, -100, -108, -10804, '(none)');
insert into YukonUserRole values (-1005, -100, -108, -10805, '(none)');
insert into YukonUserRole values (-1006, -100, -108, -10806, '(none)');
insert into YukonUserRole values (-1010, -100, -200, -20000, '(none)');
insert into YukonUserRole values (-1011, -100, -200, -20001, 'true');
insert into YukonUserRole values (-1012, -100, -200, -20002, '(none)');
insert into YukonUserRole values (-1013, -100, -200, -20003, '(none)');

INSERT INTO YukonWebConfiguration VALUES (-1,'Summer.gif','Default Summer Settings','Cooling','Cool');
INSERT INTO YukonWebConfiguration VALUES (-2,'Winter.gif','Default Winter Settings','Heating','Heat');

insert into YukonSelectionList values (1001,'A','(none)','Not visible, list defines the event ids','LMCustomerEvent','N');
insert into YukonSelectionList values (1002,'A','(none)','Not visible, defines possible event actions','LMCustomerAction','N');
insert into YukonSelectionList values (1003,'A','(none)','Not visible, defines inventory device category','InventoryCategory','N');
insert into YukonSelectionList values (1004,'A','(none)','Device voltage selection','DeviceVoltage','Y');
insert into YukonSelectionList values (1005,'A','(none)','Device type selection','DeviceType','Y');
insert into YukonSelectionList values (1006,'N','(none)','Hardware status selection','DeviceStatus','N');
insert into YukonSelectionList values (1007,'A','(none)','Appliance category','ApplianceCategory','N');
insert into YukonSelectionList values (1008,'A','(none)','Call type selection','CallType','Y');
insert into YukonSelectionList values (1009,'A','(none)','Service type selection','ServiceType','Y');
insert into YukonSelectionList values (1010,'N','(none)','Service request status','ServiceStatus','N');
insert into YukonSelectionList values (1011,'N','(none)','Search by selection','SearchBy','N');
insert into YukonSelectionList values (1012,'A','(none)','Appliance manufacturer selection','Manufacturer','Y');
insert into YukonSelectionList values (1013,'A','(none)','Appliance location selection','ApplianceLocation','Y');
insert into YukonSelectionList values (1014,'N','(none)','Chance of control selection','ChanceOfControl','Y');
insert into YukonSelectionList values (1015,'N','(none)','Thermostat settings time of week selection','TimeOfWeek','N');
insert into YukonSelectionList values (1016,'N','(none)','Question type selection','QuestionType','N');
insert into YukonSelectionList values (1017,'N','(none)','Answer type selection','AnswerType','N');
insert into YukonSelectionList values (1018,'N','(none)','Thermostat mode selection','ThermostatMode','N');
insert into YukonSelectionList values (1019,'N','(none)','Thermostat fan state selection','ThermostatFanState','N');
insert into YukonSelectionList values (1020,'O','(none)','Customer FAQ groups','CustomerFAQGroup','N');
insert into YukonSelectionList values (1021,'N','(none)','Residence type selection','ResidenceType','Y');
insert into YukonSelectionList values (1022,'N','(none)','Construction material selection','ConstructionMaterial','Y');
insert into YukonSelectionList values (1023,'N','(none)','Decade built selection','DecadeBuilt','Y');
insert into YukonSelectionList values (1024,'N','(none)','Square feet selection','SquareFeet','Y');
insert into YukonSelectionList values (1025,'N','(none)','Insulation depth selection','InsulationDepth','Y');
insert into YukonSelectionList values (1026,'N','(none)','General condition selection','GeneralCondition','Y');
insert into YukonSelectionList values (1027,'N','(none)','Main cooling system selection','CoolingSystem','Y');
insert into YukonSelectionList values (1028,'N','(none)','Main heating system selection','HeatingSystem','Y');
insert into YukonSelectionList values (1029,'N','(none)','Number of occupants selection','NumberOfOccupants','Y');
insert into YukonSelectionList values (1030,'N','(none)','Ownership type selection','OwnershipType','Y');
insert into YukonSelectionList values (1031,'N','(none)','Main fuel type selection','FuelType','Y');
insert into YukonSelectionList values (1032,'N','(none)','AC Tonnage selection','ACTonnage','Y');
insert into YukonSelectionList values (1033,'N','(none)','AC type selection','ACType','Y');
insert into YukonSelectionList values (1034,'N','(none)','Water heater number of gallons selection','WHNumberOfGallons','Y');
insert into YukonSelectionList values (1035,'N','(none)','Water heater energy source selection','WHEnergySource','Y');
insert into YukonSelectionList values (1036,'N','(none)','Dual fuel switch over type selection','DFSwitchOverType','Y');
insert into YukonSelectionList values (1037,'N','(none)','Dual fuel secondary source selection','DFSecondarySource','Y');
insert into YukonSelectionList values (1038,'N','(none)','Grain dryer type selection','GrainDryerType','Y');
insert into YukonSelectionList values (1039,'N','(none)','Grain dryer bin size selection','GDBinSize','Y');
insert into YukonSelectionList values (1040,'N','(none)','Grain dryer blower energy source selection','GDEnergySource','Y');
insert into YukonSelectionList values (1041,'N','(none)','Grain dryer blower horse power selection','GDHorsePower','Y');
insert into YukonSelectionList values (1042,'N','(none)','Grain dryer blower heat source selection','GDHeatSource','Y');
insert into YukonSelectionList values (1043,'N','(none)','Storage heat type selection','StorageHeatType','Y');
insert into YukonSelectionList values (1044,'N','(none)','Heat pump type selection','HeatPumpType','Y');
insert into YukonSelectionList values (1045,'N','(none)','Heat pump standby source selection','HPStandbySource','Y');
insert into YukonSelectionList values (1046,'N','(none)','Irrigation type selection','IrrigationType','Y');
insert into YukonSelectionList values (1047,'N','(none)','Irrigation soil type selection','IRRSoilType','Y');
insert into YukonSelectionList values (1048,'N','(none)','Device location selection','DeviceLocation','N');
insert into YukonSelectionList values (1049,'O','(none)','Opt out period selection','OptOutPeriod','Y');
insert into YukonSelectionList values (1050,'N','(none)','Gateway end device data description','GatewayEndDeviceDataDesc','N');
insert into YukonSelectionList values (1051,'N','(none)','Hardware Inventory search by selection','InvSearchBy','N');
insert into YukonSelectionList values (1052,'N','(none)','Hardware Inventory sort by selection','InvSortBy','N');
insert into YukonSelectionList values (1053,'N','(none)','Hardware Inventory filter by selection','InvFilterBy','N');
insert into YukonSelectionList values (1054,'N','(none)','Service order search by selection','SOSearchBy','N');
insert into YukonSelectionList values (1055,'N','(none)','Service order sort by selection','SOSortBy','N');
insert into YukonSelectionList values (1056,'N','(none)','Service order filter by selection','SOFilterBy','N');
insert into YukonSelectionList values (1057,'N','(none)','Generator transfer switch type selection','GENTransferSwitchType','Y');
insert into YukonSelectionList values (1058,'N','(none)','Generator transfer switch manufacturer selection','GENTransferSwitchMfg','Y');
insert into YukonSelectionList values (1059,'N','(none)','Irrigation horse power selection','IRRHorsePower','Y');
insert into YukonSelectionList values (1060,'N','(none)','Irrigation energy source selection','IRREnergySource','Y');
insert into YukonSelectionList values (1061,'N','(none)','Irrigation meter location selection','IRRMeterLocation','Y');
insert into YukonSelectionList values (1062,'N','(none)','Irrigation meter voltage selection','IRRMeterVoltage','Y');
insert into YukonSelectionList values (1063,'N','(none)','Water heater location selection','WHLocation','Y');
insert into YukonSelectionList values (1064,'N','(none)','Heat pump size selection','HeatPumpSize','Y');
insert into YukonSelectionList values (2000,'N','(none)','Customer Selection Base','(none)','N');

insert into YukonListEntry values (1001,1001,0,'Program',1001);
insert into YukonListEntry values (1002,1001,0,'Hardware',1002);
insert into YukonListEntry values (1003,1001,0,'ThermostatManual',1003);
insert into YukonListEntry values (1011,1002,0,'Signup',1101);
insert into YukonListEntry values (1012,1002,0,'Activation Pending',1102);
insert into YukonListEntry values (1013,1002,0,'Activation Completed',1103);
insert into YukonListEntry values (1014,1002,0,'Termination',1104);
insert into YukonListEntry values (1015,1002,0,'Temp Opt Out',1105);
insert into YukonListEntry values (1016,1002,0,'Future Activation',1106);
insert into YukonListEntry values (1017,1002,0,'Install',1107);
insert into YukonListEntry values (1018,1002,0,'Configure',1108);
insert into YukonListEntry values (1019,1002,0,'Programming',1109);
insert into YukonListEntry values (1020,1002,0,'Manual Option',1110);
insert into YukonListEntry values (1021,1002,0,'Uninstall',1111);
insert into YukonListEntry values (1031,1003,0,'OneWayReceiver',1201);
insert into YukonListEntry values (1032,1003,0,'TwoWayReceiver',1202);
insert into YukonListEntry values (1033,1003,0,'MCT',1203);
insert into YukonListEntry values (1041,1004,0,' ',0);
insert into YukonListEntry values (1042,1004,0,'120/120',0);
insert into YukonListEntry values (1051,1005,0,'LCR-5000',1302);
insert into YukonListEntry values (1052,1005,0,'LCR-4000',1302);
insert into YukonListEntry values (1053,1005,0,'LCR-3000',1302);
insert into YukonListEntry values (1054,1005,0,'LCR-2000',1302);
insert into YukonListEntry values (1055,1005,0,'LCR-1000',1302);
insert into YukonListEntry values (1056,1005,-1,'ExpressStat',1301);
insert into YukonListEntry values (1057,1005,-1,'EnergyPro',3100);
insert into YukonListEntry values (1058,1005,-1,'MCT',1303);
insert into YukonListEntry values (1059,1005,-1,'Commercial ExpressStat',1304);
insert into YukonListEntry values (1071,1006,0,'Available',1701);
insert into YukonListEntry values (1072,1006,0,'Temp Unavail',1702);
insert into YukonListEntry values (1073,1006,0,'Unavailable',1703);
insert into YukonListEntry values (1081,1007,0,'(Default)',1400);
insert into YukonListEntry values (1082,1007,0,'Air Conditioner',1401);
insert into YukonListEntry values (1083,1007,0,'Water Heater',1402);
insert into YukonListEntry values (1084,1007,0,'Storage Heat',1403);
insert into YukonListEntry values (1085,1007,0,'Heat Pump',1404);
insert into YukonListEntry values (1086,1007,0,'Dual Fuel',1405);
insert into YukonListEntry values (1087,1007,0,'Generator',1406);
insert into YukonListEntry values (1088,1007,0,'Grain Dryer',1407);
insert into YukonListEntry values (1089,1007,0,'Irrigation',1408);
insert into YukonListEntry values (1101,1008,0,'General',0);
insert into YukonListEntry values (1102,1008,0,'Credit',0);
insert into YukonListEntry values (1111,1009,0,'Service Call',0);
insert into YukonListEntry values (1112,1009,0,'Install',0);
insert into YukonListEntry values (1121,1010,0,'Pending',1501);
insert into YukonListEntry values (1122,1010,0,'Scheduled',1502);
insert into YukonListEntry values (1123,1010,0,'Completed',1503);
insert into YukonListEntry values (1124,1010,0,'Cancelled',1504);
insert into YukonListEntry values (1131,1011,0,'Acct #',1601);
insert into YukonListEntry values (1132,1011,0,'Phone #',1602);
insert into YukonListEntry values (1133,1011,0,'Last name',1603);
insert into YukonListEntry values (1134,1011,0,'Serial #',1604);
insert into YukonListEntry values (1135,1011,0,'Map #',1605);
insert into YukonListEntry values (1141,1012,0,'(Unknown)',1801);
insert into YukonListEntry values (1142,1012,0,'Century',0);
insert into YukonListEntry values (1143,1012,0,'Universal',0);
insert into YukonListEntry values (1151,1013,0,'(Unknown)',1901);
insert into YukonListEntry values (1152,1013,0,'Basement',0);
insert into YukonListEntry values (1153,1013,0,'North Side',0);
insert into YukonListEntry values (1161,1014,0,'Likely',0);
insert into YukonListEntry values (1162,1014,0,'Unlikely',0);
insert into YukonListEntry values (1171,1015,0,'Weekday',2101);
insert into YukonListEntry values (1172,1015,0,'Weekend',2102);
insert into YukonListEntry values (1173,1015,0,'Saturday',2103);
insert into YukonListEntry values (1174,1015,0,'Sunday',2104);
insert into YukonListEntry values (1175,1015,0,'Monday',2105);
insert into YukonListEntry values (1176,1015,0,'Tuesday',2106);
insert into YukonListEntry values (1177,1015,0,'Wednesday',2107);
insert into YukonListEntry values (1178,1015,0,'Thursday',2108);
insert into YukonListEntry values (1179,1015,0,'Friday',2109);
insert into YukonListEntry values (1191,1016,0,'Signup',2201);
insert into YukonListEntry values (1192,1016,0,'Exit',2202);
insert into YukonListEntry values (1201,1017,0,'Selection',2301);
insert into YukonListEntry values (1202,1017,0,'Free Form',2302);
insert into YukonListEntry values (1211,1018,0,'(Default)',2401);
insert into YukonListEntry values (1212,1018,0,'Cool',2402);
insert into YukonListEntry values (1213,1018,0,'Heat',2403);
insert into YukonListEntry values (1214,1018,0,'Off',2404);
insert into YukonListEntry values (1215,1018,0,'Auto',2405);
insert into YukonListEntry values (1216,1018,0,'Emergency Heat',2406);
insert into YukonListEntry values (1221,1019,0,'(Default)',2501);
insert into YukonListEntry values (1222,1019,0,'Auto',2502);
insert into YukonListEntry values (1223,1019,0,'On',2503);
insert into YukonListEntry values (1231,1020,1,'PROGRAMS',0);
insert into YukonListEntry values (1232,1020,2,'THERMOSTAT CONTROL',0);
insert into YukonListEntry values (1233,1020,3,'SAVINGS',0);
insert into YukonListEntry values (1241,1049,1,'Tomorrow',2601);
insert into YukonListEntry values (1242,1049,2,'Today',2602);
insert into YukonListEntry values (1243,1049,99,'Repeat Last',2699);

insert into YukonListEntry values (1251,1050,0,'Last Updated Time',3201);
insert into YukonListEntry values (1252,1050,0,'Setpoint',3202);
insert into YukonListEntry values (1253,1050,0,'Fan',3203);
insert into YukonListEntry values (1254,1050,0,'System',3204);
insert into YukonListEntry values (1255,1050,0,'Room,Unit',3205);
insert into YukonListEntry values (1256,1050,0,'Outdoor',3234);
insert into YukonListEntry values (1257,1050,0,'Filter Remaining,Filter Restart',3236);
insert into YukonListEntry values (1258,1050,0,'Lower CoolSetpoint Limit,Upper HeatSetpoint Limit',3237);
insert into YukonListEntry values (1259,1050,0,'Information String',3299);
insert into YukonListEntry values (1260,1050,0,'Cool Runtime,Heat Runtime',3238);
insert into YukonListEntry values (1261,1050,0,'Battery',3239);

insert into YukonListEntry values (1301,1051,0,'Serial #',2701);
insert into YukonListEntry values (1302,1051,0,'Acct #',2702);
insert into YukonListEntry values (1303,1051,0,'Phone #',2703);
insert into YukonListEntry values (1304,1051,0,'Last name',2704);
insert into YukonListEntry values (1305,1051,0,'Order #',2705);
insert into YukonListEntry values (1311,1052,0,'Serial #',2801);
insert into YukonListEntry values (1312,1052,0,'Install date',2802);
insert into YukonListEntry values (1321,1053,0,'Device type',2901);
insert into YukonListEntry values (1322,1053,0,'Service company',2902);
insert into YukonListEntry values (1323,1053,0,'Location',2903);
insert into YukonListEntry values (1324,1053,0,'Configuration',2904);
insert into YukonListEntry values (1325,1053,0,'Device status',2905);
insert into YukonListEntry values (1331,1054,0,'Order #',3301);
insert into YukonListEntry values (1332,1054,0,'Acct #',3302);
insert into YukonListEntry values (1333,1054,0,'Phone #',3303);
insert into YukonListEntry values (1334,1054,0,'Last Name',3304);
insert into YukonListEntry values (1335,1054,0,'Serial #',3305);
insert into YukonListEntry values (1341,1055,0,'Order #',3401);
insert into YukonListEntry values (1342,1055,0,'Date/Time',3402);
insert into YukonListEntry values (1352,1056,0,'Service Type',3502);
insert into YukonListEntry values (1353,1056,0,'Service Company',3503);

insert into YukonListEntry values (1400,1032,0,' ',0);
insert into YukonListEntry values (1401,1032,0,'2',0);
insert into YukonListEntry values (1402,1032,0,'2.5',0);
insert into YukonListEntry values (1403,1032,0,'3',0);
insert into YukonListEntry values (1404,1032,0,'3.5',0);
insert into YukonListEntry values (1405,1032,0,'4',0);
insert into YukonListEntry values (1406,1032,0,'4.5',0);
insert into YukonListEntry values (1407,1032,0,'5',0);
insert into YukonListEntry values (1410,1033,0,' ',0);
insert into YukonListEntry values (1411,1033,0,'Central Air',0);
insert into YukonListEntry values (1420,1034,0,' ',0);
insert into YukonListEntry values (1421,1034,0,'30',0);
insert into YukonListEntry values (1422,1034,0,'40',0);
insert into YukonListEntry values (1423,1034,0,'52',0);
insert into YukonListEntry values (1424,1034,0,'80',0);
insert into YukonListEntry values (1425,1034,0,'120',0);
insert into YukonListEntry values (1430,1035,0,' ',0);
insert into YukonListEntry values (1431,1035,0,'Propane',0);
insert into YukonListEntry values (1432,1035,0,'Electric',0);
insert into YukonListEntry values (1433,1035,0,'Natural Gas',0);
insert into YukonListEntry values (1434,1035,0,'Oil',0);
insert into YukonListEntry values (1440,1036,0,' ',0);
insert into YukonListEntry values (1441,1036,0,'Automatic',0);
insert into YukonListEntry values (1442,1036,0,'Manual',0);
insert into YukonListEntry values (1450,1037,0,' ',0);
insert into YukonListEntry values (1451,1037,0,'Propane',0);
insert into YukonListEntry values (1452,1037,0,'Electric',0);
insert into YukonListEntry values (1453,1037,0,'Natural Gas',0);
insert into YukonListEntry values (1454,1037,0,'Oil',0);
insert into YukonListEntry values (1460,1038,0,' ',0);
insert into YukonListEntry values (1461,1038,0,'Batch Dry',0);
insert into YukonListEntry values (1462,1038,0,'Aerate',0);
insert into YukonListEntry values (1463,1038,0,'Low Temp Air',0);
insert into YukonListEntry values (1470,1039,0,' ',0);
insert into YukonListEntry values (1471,1039,0,'0-2500 Bushels',0);
insert into YukonListEntry values (1472,1039,0,'2500-5000 Bushels',0);
insert into YukonListEntry values (1473,1039,0,'5000-7500 Bushels',0);
insert into YukonListEntry values (1474,1039,0,'7500-10000 Bushels',0);
insert into YukonListEntry values (1475,1039,0,'10000-12500 Bushels',0);
insert into YukonListEntry values (1476,1039,0,'12500-15000 Bushels',0);
insert into YukonListEntry values (1477,1039,0,'15000-17500 Bushels',0);
insert into YukonListEntry values (1478,1039,0,'17500-20000 Bushels',0);
insert into YukonListEntry values (1479,1039,0,'20000-25000 Bushels',0);
insert into YukonListEntry values (1480,1039,0,'25000-30000 Bushels',0);
insert into YukonListEntry values (1481,1039,0,'30000+ Bushels',0);
insert into YukonListEntry values (1490,1040,0,' ',0);
insert into YukonListEntry values (1491,1040,0,'Electric',0);
insert into YukonListEntry values (1500,1041,0,' ',0);
insert into YukonListEntry values (1501,1041,0,'5',0);
insert into YukonListEntry values (1502,1041,0,'10',0);
insert into YukonListEntry values (1503,1041,0,'15',0);
insert into YukonListEntry values (1504,1041,0,'20',0);
insert into YukonListEntry values (1505,1041,0,'25',0);
insert into YukonListEntry values (1506,1041,0,'30',0);
insert into YukonListEntry values (1507,1041,0,'35',0);
insert into YukonListEntry values (1508,1041,0,'40',0);
insert into YukonListEntry values (1509,1041,0,'45',0);
insert into YukonListEntry values (1510,1041,0,'50',0);
insert into YukonListEntry values (1511,1041,0,'60',0);
insert into YukonListEntry values (1512,1041,0,'70',0);
insert into YukonListEntry values (1513,1041,0,'80',0);
insert into YukonListEntry values (1514,1041,0,'100',0);
insert into YukonListEntry values (1515,1041,0,'150',0);
insert into YukonListEntry values (1520,1042,0,' ',0);
insert into YukonListEntry values (1521,1042,0,'Propane',0);
insert into YukonListEntry values (1522,1042,0,'Electric',0);
insert into YukonListEntry values (1523,1042,0,'Natural Gas',0);
insert into YukonListEntry values (1530,1043,0,' ',0);
insert into YukonListEntry values (1531,1043,0,'Ceramic Brick',0);
insert into YukonListEntry values (1532,1043,0,'Concrete Slab',0);
insert into YukonListEntry values (1533,1043,0,'Water',0);
insert into YukonListEntry values (1534,1043,0,'Phase-Change Comp',0);
insert into YukonListEntry values (1535,1043,0,'Pond',0);
insert into YukonListEntry values (1536,1043,0,'Rock Box',0);
insert into YukonListEntry values (1540,1044,0,' ',0);
insert into YukonListEntry values (1541,1044,0,'Air Source',0);
insert into YukonListEntry values (1542,1044,0,'Water Heater Closed',0);
insert into YukonListEntry values (1543,1044,0,'Water Heater Open',0);
insert into YukonListEntry values (1544,1044,0,'Water Heater Direct',0);
insert into YukonListEntry values (1545,1044,0,'Dual-Fuel (Gas)',0);
insert into YukonListEntry values (1550,1045,0,' ',0);
insert into YukonListEntry values (1551,1045,0,'Propane',0);
insert into YukonListEntry values (1552,1045,0,'Electric',0);
insert into YukonListEntry values (1553,1045,0,'Natural Gas',0);
insert into YukonListEntry values (1554,1045,0,'Oil',0);
insert into YukonListEntry values (1560,1046,0,' ',0);
insert into YukonListEntry values (1561,1046,0,'Pivot',0);
insert into YukonListEntry values (1562,1046,0,'Gated Pipe',0);
insert into YukonListEntry values (1563,1046,0,'Pivot/Power Only',0);
insert into YukonListEntry values (1564,1046,0,'Reuse Pit Only',0);
insert into YukonListEntry values (1565,1046,0,'Ditch W/Syphon Tube',0);
insert into YukonListEntry values (1566,1046,0,'Flood',0);
insert into YukonListEntry values (1570,1047,0,' ',0);
insert into YukonListEntry values (1571,1047,0,'Heavy Loam',0);
insert into YukonListEntry values (1572,1047,0,'Loam',0);
insert into YukonListEntry values (1573,1047,0,'Medium',0);
insert into YukonListEntry values (1574,1047,0,'Sandy',0);
insert into YukonListEntry values (1580,1059,0,' ',0);
insert into YukonListEntry values (1581,1059,0,'5',0);
insert into YukonListEntry values (1582,1059,0,'10',0);
insert into YukonListEntry values (1583,1059,0,'15',0);
insert into YukonListEntry values (1584,1059,0,'20',0);
insert into YukonListEntry values (1585,1059,0,'25',0);
insert into YukonListEntry values (1586,1059,0,'30',0);
insert into YukonListEntry values (1587,1059,0,'35',0);
insert into YukonListEntry values (1588,1059,0,'40',0);
insert into YukonListEntry values (1589,1059,0,'45',0);
insert into YukonListEntry values (1590,1059,0,'50',0);
insert into YukonListEntry values (1591,1059,0,'60',0);
insert into YukonListEntry values (1592,1059,0,'70',0);
insert into YukonListEntry values (1593,1059,0,'80',0);
insert into YukonListEntry values (1594,1059,0,'100',0);
insert into YukonListEntry values (1595,1059,0,'150',0);
insert into YukonListEntry values (1596,1059,0,'200',0);
insert into YukonListEntry values (1597,1059,0,'250',0);
insert into YukonListEntry values (1598,1059,0,'300',0);
insert into YukonListEntry values (1600,1060,0,' ',0);
insert into YukonListEntry values (1601,1060,0,'Propane',0);
insert into YukonListEntry values (1602,1060,0,'Electric',0);
insert into YukonListEntry values (1603,1060,0,'Natural Gas',0);
insert into YukonListEntry values (1604,1060,0,'Oil',0);
insert into YukonListEntry values (1610,1061,0,' ',0);
insert into YukonListEntry values (1611,1061,0,'At Road',0);
insert into YukonListEntry values (1612,1061,0,'At Pump',0);
insert into YukonListEntry values (1613,1061,0,'At Pivot',0);
insert into YukonListEntry values (1620,1062,0,' ',0);
insert into YukonListEntry values (1621,1062,0,'120 (PT)',0);
insert into YukonListEntry values (1622,1062,0,'240',0);
insert into YukonListEntry values (1623,1062,0,'277/480',0);
insert into YukonListEntry values (1624,1062,0,'480',0);
insert into YukonListEntry values (1630,1063,0,' ',0);
insert into YukonListEntry values (1631,1063,0,'Basement',0);
insert into YukonListEntry values (1632,1063,0,'Crawl Space',0);
insert into YukonListEntry values (1633,1063,0,'Main Floor Closet',0);
insert into YukonListEntry values (1634,1063,0,'Second Floor Closet',0);
insert into YukonListEntry values (1635,1063,0,'Under Counter',0);
insert into YukonListEntry values (1636,1063,0,'Attic',0);
insert into YukonListEntry values (1640,1064,0,' ',0);
insert into YukonListEntry values (1641,1064,0,'2',0);
insert into YukonListEntry values (1642,1064,0,'2.5',0);
insert into YukonListEntry values (1643,1064,0,'3',0);
insert into YukonListEntry values (1644,1064,0,'3.5',0);
insert into YukonListEntry values (1645,1064,0,'4',0);
insert into YukonListEntry values (1646,1064,0,'4.5',0);
insert into YukonListEntry values (1647,1064,0,'5',0);

insert into YukonListEntry values (1700,1021,0,' ',0);
insert into YukonListEntry values (1701,1021,0,'One Story Unfinished Basement',0);
insert into YukonListEntry values (1702,1021,0,'One Story Finished Basement',0);
insert into YukonListEntry values (1703,1021,0,'Two Story',0);
insert into YukonListEntry values (1704,1021,0,'Manufactured Home',0);
insert into YukonListEntry values (1705,1021,0,'Apartment',0);
insert into YukonListEntry values (1706,1021,0,'Duplex',0);
insert into YukonListEntry values (1707,1021,0,'Townhome',0);
insert into YukonListEntry values (1710,1022,0,' ',0);
insert into YukonListEntry values (1711,1022,0,'Frame',0);
insert into YukonListEntry values (1712,1022,0,'Brick',0);
insert into YukonListEntry values (1720,1023,0,' ',0);
insert into YukonListEntry values (1721,1023,0,'pre-1900',0);
insert into YukonListEntry values (1722,1023,0,'1910',0);
insert into YukonListEntry values (1723,1023,0,'1920',0);
insert into YukonListEntry values (1724,1023,0,'1930',0);
insert into YukonListEntry values (1725,1023,0,'1940',0);
insert into YukonListEntry values (1726,1023,0,'1950',0);
insert into YukonListEntry values (1727,1023,0,'1960',0);
insert into YukonListEntry values (1728,1023,0,'1970',0);
insert into YukonListEntry values (1729,1023,0,'1980',0);
insert into YukonListEntry values (1730,1023,0,'1990',0);
insert into YukonListEntry values (1731,1023,0,'2000',0);
insert into YukonListEntry values (1740,1024,0,' ',0);
insert into YukonListEntry values (1741,1024,0,'Less Than 1000',0);
insert into YukonListEntry values (1742,1024,0,'1000-1499',0);
insert into YukonListEntry values (1743,1024,0,'1500-1999',0);
insert into YukonListEntry values (1744,1024,0,'2000-2499',0);
insert into YukonListEntry values (1745,1024,0,'2500-2999',0);
insert into YukonListEntry values (1746,1024,0,'3000-3499',0);
insert into YukonListEntry values (1747,1024,0,'3500-3999',0);
insert into YukonListEntry values (1748,1024,0,'4000+',0);
insert into YukonListEntry values (1751,1025,0,'Unknown',0);
insert into YukonListEntry values (1752,1025,0,'Poor (0-3)"',0);
insert into YukonListEntry values (1753,1025,0,'Fair (3-5)"',0);
insert into YukonListEntry values (1754,1025,0,'Average (6-8)"',0);
insert into YukonListEntry values (1755,1025,0,'Good (9-11)"',0);
insert into YukonListEntry values (1756,1025,0,'Excellant (12+)"',0);
insert into YukonListEntry values (1760,1026,0,' ',0);
insert into YukonListEntry values (1761,1026,0,'Poor',0);
insert into YukonListEntry values (1762,1026,0,'Fair',0);
insert into YukonListEntry values (1763,1026,0,'Good',0);
insert into YukonListEntry values (1764,1026,0,'Excellent',0);
insert into YukonListEntry values (1770,1027,0,' ',0);
insert into YukonListEntry values (1771,1027,0,'None',0);
insert into YukonListEntry values (1772,1027,0,'Central Air',0);
insert into YukonListEntry values (1773,1027,0,'GSHP',0);
insert into YukonListEntry values (1774,1027,0,'ASHP',0);
insert into YukonListEntry values (1775,1027,0,'Window Unit',0);
insert into YukonListEntry values (1776,1027,0,'Gas ASHP',0);
insert into YukonListEntry values (1777,1027,0,'Attic Fan',0);
insert into YukonListEntry values (1780,1028,0,' ',0);
insert into YukonListEntry values (1781,1028,0,'Electric Forced Air',0);
insert into YukonListEntry values (1782,1028,0,'GSHP',0);
insert into YukonListEntry values (1783,1028,0,'ASHP',0);
insert into YukonListEntry values (1784,1028,0,'Electric Baseboard',0);
insert into YukonListEntry values (1785,1028,0,'Ceiling Fan',0);
insert into YukonListEntry values (1786,1028,0,'ETS',0);
insert into YukonListEntry values (1787,1028,0,'Gas Forced Air',0);
insert into YukonListEntry values (1788,1028,0,'Gas Wall Unit',0);
insert into YukonListEntry values (1790,1029,0,' ',0);
insert into YukonListEntry values (1791,1029,0,'1 - 2',0);
insert into YukonListEntry values (1792,1029,0,'3 - 4',0);
insert into YukonListEntry values (1793,1029,0,'5 - 6',0);
insert into YukonListEntry values (1794,1029,0,'7 - 8',0);
insert into YukonListEntry values (1795,1029,0,'9+',0);
insert into YukonListEntry values (1800,1030,0,' ',0);
insert into YukonListEntry values (1801,1030,0,'Own',0);
insert into YukonListEntry values (1802,1030,0,'Rent',0);
insert into YukonListEntry values (1810,1031,0,' ',0);
insert into YukonListEntry values (1811,1031,0,'Propane',0);
insert into YukonListEntry values (1812,1031,0,'Electric',0);
insert into YukonListEntry values (1813,1031,0,'Natural Gas',0);
insert into YukonListEntry values (1814,1031,0,'Oil',0);

insert into YukonListEntry values (2000,0,0,'Customer List Entry Base',0);


/* CICustomer Commercial Metering Role Properties */
insert into yukonroleproperty values(-30402, -304, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events');
insert into yukonroleproperty values(-30403, -304, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily');

/* Web Client Customers Commercial Metering role */
insert into YukonUserRole values (-442, -1, -304, -30402, '(none)');
insert into YukonUserRole values (-443, -1, -304, -30403, '(none)');

/* Web Client Customers Commercial Metering role */
insert into YukonGroupRole values (-416, -302, -304, -30402, '(none)');
insert into YukonGroupRole values (-417, -302, -304, -30403, '(none)');

/* START the Admin role Group */
insert into YukonGroupRole values (-1132,-2, -304, -30402, '(none)');
insert into YukonGroupRole values (-1133,-2, -304, -30403, '(none)');

alter table DeviceLoadProfile add VoltageDmdInterval numeric;
go
update DeviceLoadProfile set VoltageDmdInterval = 60;
go
alter table DeviceLoadProfile alter column VoltageDmdInterval numeric not null;
go

alter table DeviceLoadProfile add VoltageDmdRate numeric;
go
update DeviceLoadProfile set VoltageDmdRate = 300;
go
alter table DeviceLoadProfile alter column VoltageDmdRate numeric not null;
go

update yukonpaobject set type = 'MCT-410iLE' where type = 'MCT-410 kWh Only';


alter table CTIDatabase add Build numeric;
go
update CTIDatabase set Build = 0;
go
alter table CTIDatabase alter column Build numeric not null;
go




/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '22-APR-2004', 'Many changes to a major version jump', 12 );
go