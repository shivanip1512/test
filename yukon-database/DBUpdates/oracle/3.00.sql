/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

create table MCTConfig  (
   ConfigID             NUMBER                           not null,
   ConfigName           VARCHAR2(30)                     not null,
   ConfigType           NUMBER                           not null,
   ConfigMode           VARCHAR2(30)                     not null,
   MCTWire1             NUMBER                           not null,
   Ke1                  FLOAT                            not null,
   MCTWire2             NUMBER                           not null,
   Ke2                  FLOAT                            not null,
   MCTWire3             NUMBER                           not null,
   Ke3                  FLOAT                            not null
);
alter table MCTConfig
   add constraint PK_MCTCONFIG primary key (ConfigID);


create table MCTConfigMapping  (
   MctID                NUMBER                           not null,
   ConfigID             NUMBER                           not null
);
alter table MCTConfigMapping
   add constraint PK_MCTCONFIGMAPPING primary key (MctID, ConfigID);
alter table MCTConfigMapping
   add constraint FK_McCfgM_McCfg foreign key (ConfigID)
      references MCTConfig (ConfigID);
alter table MCTConfigMapping
   add constraint FK_McCfgM_Dev foreign key (MctID)
      references DEVICE (DEVICEID);


create table YukonServices  (
   ServiceID            NUMBER                           not null,
   ServiceName          VARCHAR2(60)                     not null,
   ServiceClass         VARCHAR2(100)                    not null,
   ParamNames           VARCHAR2(300)                    not null,
   ParamValues          VARCHAR2(300)                    not null
);

insert into YukonServices values( 1, 'Notification_Server', 'com.cannontech.jmx.services.DynamicNotifcationServer', '(none)', '(none)' );
alter table YukonServices
   add constraint PK_YUKSER primary key (ServiceID);


insert into billingFileformats values (9, 'CTI2');


alter table GraphDataSeries add renderer smallint;
update GraphDataSeries set renderer = 0;	/*Line renderer*/
alter table GraphDataSeries modify renderer not null;

alter table GraphDataSeries add moreData varchar2(100);
update GraphDataSeries set moreData = '(none)';
alter table GraphDataSeries modify moreData not null;


alter table systemlog add millis smallint;
update systemlog set millis = 0;
alter table systemlog modify millis not null;

alter table rawpointhistory add millis smallint;
update rawpointhistory set millis = 0;
alter table rawpointhistory modify millis not null;



/* Default user for STARS customers who don't have a login */
insert into YukonUser values (-9999,'(none)','(none)',0,'01-JAN-2000','Disabled');



/* energy company role properties */
update YukonRoleProperty set Description='(Deprecated) Use this value as the admin email address only if the energy company''s primary contact doesn''t have an email address.' where RolePropertyId=-1100;
update YukonRoleProperty set DefaultValue='override@cannontech.com' where RolePropertyID=-1101;
update YukonRoleProperty set DefaultValue='Residential Customers', Description='Group name of all the residential customer logins' where RolePropertyID=-1105;
update YukonRoleProperty set DefaultValue='Web Client Operators' where RolePropertyID=-1106;

/* web client role properties */
update YukonRoleProperty set DefaultValue='yukon/CannonStyle.css' where RolePropertyId=-10802 and DefaultValue='CannonStyle.css';
update YukonRoleProperty set DefaultValue='yukon/Bullet.gif' where RolePropertyId=-10803 and DefaultValue='Bullet.gif';
update YukonRoleProperty set DefaultValue='yukon/BulletExpand.gif' where RolePropertyId=-10804 and DefaultValue='BulletExpand.gif';
update YukonRoleProperty set DefaultValue='yukon/DemoHeader.gif' where RolePropertyId=-10805 and DefaultValue='DemoHeader.gif';
update YukonRoleProperty set KeyName='log_in_url', DefaultValue='/login.jsp', Description='The url where the user login from. It is used as the url to send the users to when they log off.' where RolePropertyID=-10806;
/* @error ignore */
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
/* @error ignore */
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');
update YukonRoleProperty set DefaultValue='yukon/BottomConnector.gif' where RolePropertyId=-10807 and DefaultValue='BottomConnector.gif';
update YukonRoleProperty set DefaultValue='yukon/MidConnector.gif' where RolePropertyId=-10808 and DefaultValue='MidConnector.gif';

/* operator administrator role properties */
insert into YukonRoleProperty values(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');

/* operator consumer info role properties */
update YukonRoleProperty set KeyName='Super Operator', DefaultValue='false', Description='Used for some testing functions (not recommended)' where RolePropertyID=-20150;
update YukonRoleProperty set KeyName='New Account Wizard', DefaultValue='true', Description='Controls whether to enable the new account wizard' where RolePropertyID=-20151;
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-20152;
/* @error ignore */
insert into YukonRoleProperty values(-20153,-201,'Inventory Checking','true','Controls when to perform inventory checking while creating or updating hardware information');
/* @error ignore */
insert into YukonRoleProperty values(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
/* @error ignore */
insert into YukonRoleProperty values(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
/* @error ignore */
insert into YukonRoleProperty values(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
/* @error ignore */
insert into YukonRoleProperty values(-20157,-201,'Opt Out Rules','(none)','Defines the rules for opting out.');
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-20800;
/* @error ignore */
insert into YukonRoleProperty values(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
update YukonRoleProperty set DefaultValue='THERMOSTAT - SCHEDULE' where RolePropertyID=-20855;
update YukonRoleProperty set DefaultValue='THERMOSTAT - MANUAL' where RolePropertyID=-20856;

/* operator inventory management role properties */
/* @error ignore */
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');

/* operator work order management role */
/* @error ignore */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/* operator work order management role properties */
/* @error ignore */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
/* @error ignore */
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

/* residential customer role properties */
update YukonRoleProperty set KeyName='Notification on General Page', DefaultValue='false', Description='Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)' where RolePropertyID=-40050;
update YukonRoleProperty set KeyName='Hide Opt Out Box', DefaultValue='false', Description='Controls whether to show the opt out box on the programs opt out page' where RolePropertyID=-40051;
/* @error ignore */
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
update YukonRoleProperty set KeyName='Disable Program Signup', DefaultValue='false', Description='Controls whether to prevent the customers from enrolling in or out of the programs' where RolePropertyID=-40054;
/* @error ignore */
insert into YukonRoleProperty values(-40055,-400,'Opt Out Rules','(none)','Defines the rules for opting out.');
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-40100;
update YukonRoleProperty set DefaultValue='(none)', Description='(Deprecated) Use the "Description Utility" role property to handle the customized utility email.' where RolePropertyID=-40101;
/* @error ignore */
insert into YukonRoleProperty values(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
update YukonRoleProperty set DefaultValue='THERMOSTAT - SCHEDULE' where RolePropertyID=-40157;
update YukonRoleProperty set DefaultValue='THERMOSTAT - MANUAL' where RolePropertyID=-40158;
update YukonRoleProperty set Description='Description on the contact us page. The special fields will be replaced by real information when displayed on the web.' where RolePropertyID=-40173;
update YukonRoleProperty set DefaultValue='yukon/Mom.jpg' where RolePropertyId=-40180 and DefaultValue='Mom.jpg';
update YukonRoleProperty set DefaultValue='yukon/Family.jpg' where RolePropertyId=-40181 and DefaultValue='Family.jpg';

/* residential customers group */
/* @error ignore */
insert into yukongrouprole values (507,-300,-108,-10807,'(none)');
/* @error ignore */
insert into yukongrouprole values (508,-300,-108,-10808,'(none)');
/* @error ignore */
insert into yukongrouprole values (552,-300,-400,-40052,'false');
/* @error ignore */
insert into yukongrouprole values (555,-300,-400,-40055,'(none)');
/* @error ignore */
insert into yukongrouprole values (602,-300,-400,-40102,'(none)');
update YukonGroupRole set Value='yukon/DemoHeaderCES.gif' where Value='DemoHeaderCES.gif';

/* web client operators group */
/* @error ignore */
insert into yukongrouprole values (707,-301,-108,-10807,'(none)');
/* @error ignore */
insert into yukongrouprole values (708,-301,-108,-10808,'(none)');
/* @error ignore */
insert into yukongrouprole values (753,-301,-201,-20153,'(none)');
/* @error ignore */
insert into yukongrouprole values (754,-301,-201,-20154,'false');
/* @error ignore */
insert into yukongrouprole values (755,-301,-201,-20155,'true');
/* @error ignore */
insert into yukongrouprole values (756,-301,-201,-20156,'true');
/* @error ignore */
insert into yukongrouprole values (757,-301,-201,-20157,'(none)');
/* @error ignore */
insert into yukongrouprole values (765,-301,-210,-21000,'(none)');
/* @error ignore */
insert into yukongrouprole values (766,-301,-210,-21001,'(none)');
/* @error ignore */
insert into yukongrouprole values (791,-301,-209,-20900,'(none)');
/* @error ignore */
insert into yukongrouprole values (792,-301,-209,-20901,'(none)');
/* @error ignore */
insert into yukongrouprole values (793,-301,-209,-20902,'(none)');
/* @error ignore */
insert into yukongrouprole values (794,-301,-209,-20903,'(none)');
/* @error ignore */
insert into yukongrouprole values (795,-301,-209,-20904,'(none)');
/* @error ignore */
insert into yukongrouprole values (801,-301,-201,-20801,'(none)');
update YukonGroupRole set Value='(none)' where RolePropertyID=-20152 and Value='false';



update yukonrole set category = 'Yukon' where roleid = -104;
update yukonrole set roledescription = 'Calculation HIstorical. Edit this role from the Yukon SetUp page.' where roleid = -104;
update yukonrole set category = 'Yukon' where roleid = -105;
update yukonrole set roledescription = 'Web Graph. Edit this role from the Yukon SetUp page.' where roleid = -105;
update yukonrole set category = 'Yukon' where roleid = -106;
update yukonrole set roledescription = 'Billing. Edit this role from the Yukon SetUp page.' where roleid = -106;
update yukonrole set category = 'Operator' where roleid = -2;
update yukonrole set roledescription = 'Default Yukon role. Edit this role from the Yukon SetUp page.' where roleid = -1;



delete from YukonGroupRole where roleid=-104;
delete from YukonGroupRole where roleid=-105;
delete from YukonGroupRole where roleid=-106;


delete from yukonroleproperty where roleid=-303;
delete from yukonrole where roleid=-303;


delete from YukonUserRole where userid=-1;

update YukonRoleProperty set keyname='lc_reduction_col', defaultvalue='true', description='Tells TDC to show the LoadControl reduction column or not' where rolepropertyid=-10110;
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
insert into YukonUserRole values(130,-1,-101,-10110,'(none)');

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
insert into YukonUserRole values (738,-1,-201,-20118,'(none)');
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


/* Delete all the deprecated LOG_LEVEL */
delete from yukongrouprole where rolepropertyid = -10006;
delete from yukongrouprole where rolepropertyid = -10110;
delete from yukongrouprole where rolepropertyid = -10201;
delete from yukongrouprole where rolepropertyid = -10403;
delete from yukongrouprole where rolepropertyid = -10502;
delete from yukongrouprole where rolepropertyid = -10602;
delete from yukongrouprole where rolepropertyid = -10801;

delete from yukonuserrole where rolepropertyid = -10006;
delete from yukonuserrole where rolepropertyid = -10110;
delete from yukonuserrole where rolepropertyid = -10201;
delete from yukonuserrole where rolepropertyid = -10403;
delete from yukonuserrole where rolepropertyid = -10502;
delete from yukonuserrole where rolepropertyid = -10602;
delete from yukonuserrole where rolepropertyid = -10801;

delete from yukonroleproperty where rolepropertyid = -10006;
delete from yukonroleproperty where rolepropertyid = -10110;
delete from yukonroleproperty where rolepropertyid = -10201;
delete from yukonroleproperty where rolepropertyid = -10403;
delete from yukonroleproperty where rolepropertyid = -10502;
delete from yukonroleproperty where rolepropertyid = -10602;
delete from yukonroleproperty where rolepropertyid = -10801;


/* Update the roles that only have 1 property, the deprecated LOG_LEVEL */
update YukonRoleProperty set keyname='msg_priority', defaultvalue='14', description='Tells commander what the outbound priority of messages are (low)1 - 14(high)' where rolepropertyid=-10300;
update YukonRoleProperty set keyname='default', defaultvalue='false', description='The default esub editor property' where rolepropertyid=-10700;

insert into YukonRoleProperty values(-1010,-1,'smtp_host','127.0.0.1','Name or IP address of the mail server');
insert into YukonRoleProperty values(-1011,-1,'mail_from_address','yukon@cannontech.com','Name of the FROM email address the mail server will use');
insert into YukonRoleProperty values(-1012,-1,'print_insert_sql','(none)','File name of where to print all SQL insert statements');
insert into YukonRoleProperty values(-1013,-1,'stars_soap_server','(none)','Where the soap server is running, the default value is the local host');
insert into YukonRoleProperty values(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
insert into YukonRoleProperty values(-1216,-3,'log_to_file','false','Tells all logging that it needs to go to a file');

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
insert into yukongrouprole values ( 986, -302, -306, -30601, 'true)');
insert into yukongrouprole values ( 987, -302, -306, -30602, 'true');
insert into yukongrouprole values ( 988, -302, -306, -30603, 'true');



/* Start CapControl Changes */
alter table capbank add RecloseDelay number;
update capbank set RecloseDelay = 0;
alter table capbank modify column RecloseDelay not null;

alter table dynamicccsubstationbus add WaiveControlFlag char(1);
update dynamicccsubstationbus set WaiveControlFlag = 'N';
alter table dynamicccsubstationbus modify column WaiveControlFlag not null;

alter table dynamicccfeeder add WaiveControlFlag char(1);
update dynamicccfeeder set WaiveControlFlag = 'N';
alter table dynamicccfeeder modify column WaiveControlFlag not null;

alter table capcontrolsubstationbus add ControlDelayTime number;
update capcontrolsubstationbus set ControlDelayTime = 0;
alter table capcontrolsubstationbus modify column ControlDelayTime not null;

alter table capcontrolsubstationbus add ControlSendRetries number;
update capcontrolsubstationbus set ControlSendRetries = 0;
alter table capcontrolsubstationbus modify column ControlSendRetries not null;



drop table SoeLog;
drop table TagLog;
create table TagLog  (
   LogID                NUMBER                           not null,
   InstanceID           NUMBER                           not null,
   PointID              NUMBER                           not null,
   TagID                NUMBER                           not null,
   UserName             VARCHAR2(60)                     not null,
   Action               VARCHAR2(20)                     not null,
   Description          VARCHAR2(120)                    not null,
   TagTime              DATE                             not null,
   RefStr               VARCHAR2(60)                     not null,
   ForStr               VARCHAR2(60)                     not null
);
alter table TagLog
   add constraint PK_TAGLOG primary key (LogID);
alter table TagLog
   add constraint FK_TagLg_Pt foreign key (PointID)
      references POINT (POINTID);
alter table TagLog
   add constraint FK_TagLg_Tgs foreign key (TagID)
      references Tags (TagID);


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

create table ActivityLog  (
   ActivityLogID        NUMBER                           not null,
   TimeStamp            DATE                             not null,
   UserID               NUMBER,
   AccountID            NUMBER,
   EnergyCompanyID      NUMBER,
   CustomerID           NUMBER,
   PaoID                NUMBER,
   Action               VARCHAR2(80)                     not null,
   Description          VARCHAR2(120)                    not null
);
alter table ActivityLog
   add constraint PK_ACTIVITYLOG primary key (ActivityLogID);

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

update YukonGroupRole set grouproleid=grouproleid * -1 where grouproleid > 0;

update YukonUserRole set userroleid=userroleid * -1 where userroleid > 0;


update YukonUser set username = 'admin', password = 'admin' where userid = -1;
insert into YukonUser values(-2,'yukon','yukon',0,'01-JAN-00','Enabled');

insert into YukonGroup values(-2,'System Administrator Grp','A set of roles that allow administrative access to the system.');
insert into YukonUserGroup values(-2,-2);
insert into YukonUserGroup values(-2,-1);


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
insert into YukonGroupRole values (-1205,-2,-201,-20118,'(none)');

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


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('3.00', 'Ryan', '17-FEB-2004', 'Many changes to a major version jump');
