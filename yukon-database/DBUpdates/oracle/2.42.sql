/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/
create table Tags  (
   TagID                NUMBER                           not null,
   TagName              VARCHAR2(60)                     not null,
   TagLevel             NUMBER                           not null,
   Inhibit              CHAR(1)                          not null,
   ColorID              NUMBER                           not null,
   ImageID              NUMBER                           not null
);
alter table Tags
   add constraint PK_TAGS primary key (TagID);


create table DynamicTags  (
   InstanceID           NUMBER                           not null,
   PointID              NUMBER                           not null,
   TagID                NUMBER                           not null,
   UserName             VARCHAR2(60)                     not null,
   Action          		VARCHAR2(20)                     not null,
   Description          VARCHAR2(120)                    not null,
   TagTime              DATE                             not null,
   RefStr               VARCHAR2(60),
   ForStr               VARCHAR2(60)
);
alter table DynamicTags
   add constraint PK_DYNAMICTAGS primary key (InstanceID);
alter table DynamicTags
   add constraint FK_DynTgs_Pt foreign key (PointID)
      references POINT (POINTID);
alter table DynamicTags
   add constraint FK_DYN_REF__TAG foreign key (TagID)
      references Tags (TagID);


create table TagLog  (
   LogID                NUMBER                           not null,
   PointID              NUMBER                           not null,
   TagID                NUMBER                           not null,
   UserName             VARCHAR2(60)                     not null,
   Action          		VARCHAR2(20)                     not null,
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



create table DynamicPointAlarming  (
   PointID              NUMBER                           not null,
   AlarmCondition       NUMBER                           not null,
   CategoryID           NUMBER                           not null,
   AlarmTime            DATE                             not null,
   Action               VARCHAR2(60)                     not null,
   Description          VARCHAR2(120)                    not null,
   Tags                 NUMBER                           not null,
   LogID                NUMBER                           not null,
   SOE_TAG              NUMBER                           not null,
   Type                 NUMBER                           not null,
   UserName             VARCHAR2(30)                     not null
);
alter table DynamicPointAlarming
   add constraint PK_DYNAMICPOINTALARMING primary key (PointID, AlarmCondition);
alter table DynamicPointAlarming
   add constraint FKf_DynPtAl_SysL foreign key (LogID)
      references SYSTEMLOG (LOGID);
alter table DynamicPointAlarming
   add constraint FK_DynPtAl_Pt foreign key (PointID)
      references POINT (POINTID);



create table SOELog  (
   LogID                NUMBER                           not null,
   PointID              NUMBER                           not null,
   SOEDateTime          DATE                             not null,
   Millis               NUMBER                           not null,
   Description          VARCHAR2(60)                     not null,
   AdditionalInfo       VARCHAR2(120)                    not null
);
alter table SOELog
   add constraint SYS_SOELog_PK primary key (LogID);
create index Indx_SOELG_PtId on SOELog (
   PointID ASC
);
create index Indx_SOELG_Date on SOELog (
   SOEDateTime ASC
);

/* @error ignore */
create unique index Indx_SOELog_PK on SOELog (
   LogID ASC
);
alter table SOELog
   add constraint FK_Soe_Pt foreign key (PointID)
      references POINT (POINTID);



alter table LMGroupMCT drop constraint FK_LMGrMC_Dev;

alter table LMGroupMCT
   add constraint FK_LMGrMC_Grp foreign key (DeviceID)
      references LMGroup (DeviceID);


update displaycolumns set typenum=7, width=110
where displaynum=1 and typenum=10;

update displaycolumns set typenum=7, width=110, title='Additional Info'
where displaynum >=4 and displaynum <=20 and typenum=8;

update state set foregroundcolor=10 where stategroupid=-5 and rawstate=9;


/* CICustomer Administrator Role */
insert into YukonRole values(-305,'Administrator','CICustomer','Administrator privilages.');
insert into yukonroleproperty values(-30500, -305, 'Contact Information Editable', 'false', 'Contact information is editable by the customer');


/* Energy Company Role Properties */
update YukonRoleProperty set DefaultValue='Residential Customers', Description='Group name of all the residential customer logins' where RolePropertyID=-1105;
insert into YukonRoleProperty values(-1106,-2,'operator_group_name','WebClient Operators','Group name of all the web client operator logins');

/* Web Client Role Properties */
update YukonRoleProperty set KeyName = 'nav_bullet_expand', DefaultValue = 'BulletExpand.gif', Description = 'The bullet used when an item in the nav can be expanded to show submenu.' where RolePropertyID = -10804;
insert into YukonRoleProperty values(-10806,-108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

/* Operator Administrator Role Properties */
insert into YukonRoleProperty values(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
insert into YukonRoleProperty values(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
insert into YukonRoleProperty values(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');

/* Operator ConsumerInfo Role Properties */
delete from YukonRoleProperty where RolePropertyID <= -20130 and RolePropertyID >= -20162;

insert into YukonRoleProperty values(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-20150,-201,'Super Operator','false','Used for some testing functions (not recommended)');
insert into YukonRoleProperty values(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
insert into YukonRoleProperty values(-20152,-201,'Import Customer Account','false','Controls whether to enable the customer account importing feature');
insert into YukonRoleProperty values(-20153,-201,'Inventory Checking Time','EARLY','Controls when to perform inventory checking while creating or updating hardware information. Possible values are EARLY, LATE, and NONE');
insert into YukonRoleProperty values(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
insert into YukonRoleProperty values(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20800,-201,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
insert into YukonRoleProperty values(-20810,-201,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-20816,-201,'Text Reenable','reenable','Term for reenable');
insert into YukonRoleProperty values(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-20855,-201,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-20856,-201,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');

/* Operator Inventory Management Role */
insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');

/* Operator Inventory Management Role Properties */
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');

/* Operator Workorder Management Role */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/* Operator Workorder Management Role Properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

/* Residential Customer Role Properties */
delete from YukonRoleProperty where RolePropertyID <= -40030 and RolePropertyID >= -40068;

insert into YukonRoleProperty values(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-40050,-400,'Notification on General Page','false','Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)');
insert into YukonRoleProperty values(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
insert into YukonRoleProperty values(-40054,-400,'Disable Program Signup','false','Controls whether to prevent the customers from enrolling in or out of the programs');
insert into YukonRoleProperty values(-40100,-400,'Link FAQ','(none)','The customized FAQ link');
insert into YukonRoleProperty values(-40101,-400,'Link Utility Email','(none)','The customized utility email');
insert into YukonRoleProperty values(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
insert into YukonRoleProperty values(-40110,-400,'Text Control','control','Term for control');
insert into YukonRoleProperty values(-40111,-400,'Text Controlled','controlled','Past form of the term for control');
insert into YukonRoleProperty values(-40112,-400,'Text Controlling','controlling','Present form of the term for control');
insert into YukonRoleProperty values(-40113,-400,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
insert into YukonRoleProperty values(-40114,-400,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
insert into YukonRoleProperty values(-40115,-400,'Text Opt Out Past','opted out','Past form of the term for opt out');
insert into YukonRoleProperty values(-40116,-400,'Text Odds for Control','odds for control','Text for odds for control');
insert into YukonRoleProperty values(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');
insert into YukonRoleProperty values(-40130,-400,'Label Programs Control History','Control History','Text of the programs control history link');
insert into YukonRoleProperty values(-40131,-400,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
insert into YukonRoleProperty values(-40132,-400,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
insert into YukonRoleProperty values(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
insert into YukonRoleProperty values(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
insert into YukonRoleProperty values(-40150,-400,'Title General','WELCOME TO ENERGY COMPANY SERVICES!','Title of the general page');
insert into YukonRoleProperty values(-40151,-400,'Title Programs Control History','PROGRAMS - CONTROL SUMMARY','Title of the programs control summary page');
insert into YukonRoleProperty values(-40152,-400,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
insert into YukonRoleProperty values(-40153,-400,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
insert into YukonRoleProperty values(-40154,-400,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
insert into YukonRoleProperty values(-40155,-400,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
insert into YukonRoleProperty values(-40156,-400,'Title Utility','QUESTIONS - UTILITY','Title of the utility page');
insert into YukonRoleProperty values(-40157,-400,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
insert into YukonRoleProperty values(-40158,-400,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
insert into YukonRoleProperty values(-40170,-400,'Description General','Thank you for participating in our Consumer Energy Services programs. By participating, you have helped to optimize our delivery of energy, stabilize rates, and reduce energy costs. Best of all, you are saving energy dollars!<br><br>This site is designed to help manage your programs on-line from anywhere with access to a Web browser.','Description on the general page');
insert into YukonRoleProperty values(-40171,-400,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame from the drop-down box below, then select Submit.','Description on the programs opt out page');
insert into YukonRoleProperty values(-40172,-400,'Description Program','(none)','Description on the programs details page. If not provided, the descriptions of the published programs will be used.');
insert into YukonRoleProperty values(-40173,-400,'Description Utility','<<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields in the default value will be replaced by real information when showing on the web.');
insert into YukonRoleProperty values(-40180,-400,'Image Corner','Mom.jpg','Image at the upper-left corner');
insert into YukonRoleProperty values(-40181,-400,'Image General','Family.jpg','Image on the general page');

/* Web Client Groups */
insert into yukongroup values (-301,'Web Client Operators','The default group of web client operators');
insert into yukongroup values (-300,'Residential Customers','The default group of residential customers');

insert into yukongrouprole values (500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (501,-300,-108,-10801,'(none)');
insert into yukongrouprole values (502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (505,-300,-108,-10805,'DemoHeaderCES.gif');
insert into yukongrouprole values (506,-300,-108,-10806,'(none)');
insert into yukongrouprole values (507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (508,-300,-108,-10808,'(none)');
insert into yukongrouprole values (520,-300,-400,-40000,'true');
insert into yukongrouprole values (521,-300,-400,-40001,'true');
insert into yukongrouprole values (522,-300,-400,-40002,'false');
insert into yukongrouprole values (523,-300,-400,-40003,'true');
insert into yukongrouprole values (524,-300,-400,-40004,'true');
insert into yukongrouprole values (525,-300,-400,-40005,'true');
insert into yukongrouprole values (526,-300,-400,-40006,'true');
insert into yukongrouprole values (527,-300,-400,-40007,'true');
insert into yukongrouprole values (528,-300,-400,-40008,'true');
insert into yukongrouprole values (529,-300,-400,-40009,'true');
insert into yukongrouprole values (530,-300,-400,-40010,'true');
insert into yukongrouprole values (550,-300,-400,-40050,'false');
insert into yukongrouprole values (551,-300,-400,-40051,'false');
insert into yukongrouprole values (552,-300,-400,-40052,'false');
insert into yukongrouprole values (554,-300,-400,-40054,'false');
insert into yukongrouprole values (600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (601,-300,-400,-40101,'(none)');
insert into yukongrouprole values (602,-300,-400,-40102,'(none)');
insert into yukongrouprole values (610,-300,-400,-40110,'(none)');
insert into yukongrouprole values (611,-300,-400,-40111,'(none)');
insert into yukongrouprole values (612,-300,-400,-40112,'(none)');
insert into yukongrouprole values (613,-300,-400,-40113,'(none)');
insert into yukongrouprole values (614,-300,-400,-40114,'(none)');
insert into yukongrouprole values (615,-300,-400,-40115,'(none)');
insert into yukongrouprole values (616,-300,-400,-40116,'(none)');
insert into yukongrouprole values (617,-300,-400,-40117,'(none)');
insert into yukongrouprole values (630,-300,-400,-40130,'(none)');
insert into yukongrouprole values (631,-300,-400,-40131,'(none)');
insert into yukongrouprole values (632,-300,-400,-40132,'(none)');
insert into yukongrouprole values (633,-300,-400,-40133,'(none)');
insert into yukongrouprole values (634,-300,-400,-40134,'(none)');
insert into yukongrouprole values (650,-300,-400,-40150,'(none)');
insert into yukongrouprole values (651,-300,-400,-40151,'(none)');
insert into yukongrouprole values (652,-300,-400,-40152,'(none)');
insert into yukongrouprole values (653,-300,-400,-40153,'(none)');
insert into yukongrouprole values (654,-300,-400,-40154,'(none)');
insert into yukongrouprole values (655,-300,-400,-40155,'(none)');
insert into yukongrouprole values (656,-300,-400,-40156,'(none)');
insert into yukongrouprole values (657,-300,-400,-40157,'(none)');
insert into yukongrouprole values (658,-300,-400,-40158,'(none)');
insert into yukongrouprole values (670,-300,-400,-40170,'(none)');
insert into yukongrouprole values (671,-300,-400,-40171,'(none)');
insert into yukongrouprole values (672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (673,-300,-400,-40173,'(none)');
insert into yukongrouprole values (680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (681,-300,-400,-40181,'(none)');

insert into yukongrouprole values (700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (701,-301,-108,-10801,'(none)');
insert into yukongrouprole values (702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (706,-301,-108,-10806,'(none)');
insert into yukongrouprole values (707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (708,-301,-108,-10808,'(none)');
insert into yukongrouprole values (720,-301,-201,-20100,'true');
insert into yukongrouprole values (721,-301,-201,-20101,'true');
insert into yukongrouprole values (722,-301,-201,-20102,'true');
insert into yukongrouprole values (723,-301,-201,-20103,'true');
insert into yukongrouprole values (724,-301,-201,-20104,'false');
insert into yukongrouprole values (725,-301,-201,-20105,'false');
insert into yukongrouprole values (726,-301,-201,-20106,'true');
insert into yukongrouprole values (727,-301,-201,-20107,'true');
insert into yukongrouprole values (728,-301,-201,-20108,'true');
insert into yukongrouprole values (729,-301,-201,-20109,'true');
insert into yukongrouprole values (730,-301,-201,-20110,'true');
insert into yukongrouprole values (731,-301,-201,-20111,'true');
insert into yukongrouprole values (732,-301,-201,-20112,'true');
insert into yukongrouprole values (733,-301,-201,-20113,'true');
insert into yukongrouprole values (734,-301,-201,-20114,'true');
insert into yukongrouprole values (735,-301,-201,-20115,'true');
insert into yukongrouprole values (736,-301,-201,-20116,'true');
insert into yukongrouprole values (737,-301,-201,-20117,'true');
insert into yukongrouprole values (750,-301,-201,-20150,'true');
insert into yukongrouprole values (751,-301,-201,-20151,'true');
insert into yukongrouprole values (752,-301,-201,-20152,'false');
insert into yukongrouprole values (753,-301,-201,-20153,'(none)');
insert into yukongrouprole values (754,-301,-201,-20154,'false');
insert into yukongrouprole values (755,-301,-201,-20155,'true');
insert into yukongrouprole values (756,-301,-201,-20156,'true');
insert into yukongrouprole values (765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (766,-301,-210,-21001,'(none)');
insert into yukongrouprole values (770,-301,-202,-20200,'(none)');
insert into yukongrouprole values (775,-301,-203,-20300,'(none)');
insert into yukongrouprole values (776,-301,-203,-20301,'(none)');
insert into yukongrouprole values (780,-301,-204,-20400,'(none)');
insert into yukongrouprole values (785,-301,-205,-20500,'(none)');
insert into yukongrouprole values (790,-301,-207,-20700,'(none)');
insert into yukongrouprole values (791,-301,-209,-20900,'(none)');
insert into yukongrouprole values (792,-301,-209,-20901,'(none)');
insert into yukongrouprole values (793,-301,-209,-20902,'(none)');
insert into yukongrouprole values (794,-301,-209,-20903,'(none)');
insert into yukongrouprole values (795,-301,-209,-20904,'(none)');
insert into yukongrouprole values (800,-301,-201,-20800,'(none)');
insert into yukongrouprole values (801,-301,-201,-20801,'(none)');
insert into yukongrouprole values (810,-301,-201,-20810,'(none)');
insert into yukongrouprole values (813,-301,-201,-20813,'(none)');
insert into yukongrouprole values (814,-301,-201,-20814,'(none)');
insert into yukongrouprole values (815,-301,-201,-20815,'(none)');
insert into yukongrouprole values (816,-301,-201,-20816,'(none)');
insert into yukongrouprole values (819,-301,-201,-20819,'(none)');
insert into yukongrouprole values (820,-301,-201,-20820,'(none)');
insert into yukongrouprole values (830,-301,-201,-20830,'(none)');
insert into yukongrouprole values (831,-301,-201,-20831,'(none)');
insert into yukongrouprole values (832,-301,-201,-20832,'(none)');
insert into yukongrouprole values (833,-301,-201,-20833,'(none)');
insert into yukongrouprole values (834,-301,-201,-20834,'(none)');
insert into yukongrouprole values (850,-301,-201,-20850,'(none)');
insert into yukongrouprole values (851,-301,-201,-20851,'(none)');
insert into yukongrouprole values (852,-301,-201,-20852,'(none)');
insert into yukongrouprole values (853,-301,-201,-20853,'(none)');
insert into yukongrouprole values (854,-301,-201,-20854,'(none)');
insert into yukongrouprole values (855,-301,-201,-20855,'(none)');
insert into yukongrouprole values (856,-301,-201,-20856,'(none)');
insert into yukongrouprole values (870,-301,-201,-20870,'(none)');

/* Web Client Customers Group */
insert into yukongroup values (-302, 'Web Client Customers', 'The default group of web client customers');

/* Web Client Customers Web Client role */
insert into yukongrouprole values (400, -302, -108, -10800, '/user/CILC/user_trending.jsp');
insert into yukongrouprole values (401, -302, -108, -10801, '(none)');
insert into yukongrouprole values (402, -302, -108, -10802, '(none)');
insert into yukongrouprole values (403, -302, -108, -10803, '(none)');
insert into yukongrouprole values (404, -302, -108, -10804, '(none)');
insert into yukongrouprole values (405, -302, -108, -10805, '(none)');
insert into yukongrouprole values (406, -302, -108, -10806, '(none)');

/* Web Client Customers Direct Load Control role */
insert into yukongrouprole values (407, -302, -300, -30000, '(none)');
insert into yukongrouprole values (408, -302, -300, -30001, 'true');

/* Web Client Customers Curtailment role */
insert into yukongrouprole values (409, -302, -301, -30100, '(none)');
insert into yukongrouprole values (410, -302, -301, -30101, '(none)');

/* Web Client Customers Energy Buyback role */
insert into yukongrouprole values (411, -302, -302, -30200, '(none)');
insert into yukongrouprole values (412, -302, -302, -30200, '(none)');

/* Web Client Customers Commercial Metering role */
insert into yukongrouprole values (413, -302, -304, -30400, '(none)');
insert into yukongrouprole values (414, -302, -304, -30401, 'true');

/* Web Client Customers Administrator role */
insert into yukongrouprole values (415, -302, -305, -30500, 'true');


/* @error ignore */
alter table customerbaseline drop constraint fk_cicst_cstbslne;

drop table CustomerbaseLine;


create table BaseLine  (
   BaselineID           NUMBER                           not null,
   BaselineName         VARCHAR2(30)                     not null,
   DaysUsed             NUMBER                           not null,
   PercentWindow        NUMBER                           not null,
   CalcDays             NUMBER                           not null,
   ExcludedWeekDays     CHAR(7)                          not null,
   HolidaysUsed         NUMBER                           not null
);
insert into baseline values (1, 'Default Baseline', 30, 75, 5, 'YNNNNNY', 0);

alter table BaseLine
   add constraint PK_BASELINE primary key (BaselineID);

/* @error ignore */
create unique index Indx_BASELINE_PK on BaseLine (
   BaselineID ASC
);

create table CalcPointBaseline  (
   PointID              NUMBER                           not null,
   BaselineID           NUMBER                           not null
);
alter table CalcPointBaseline
   add constraint PK_CalcBsPt primary key (PointID);

create unique index Indx_CLCPTB_PK on CalcPointBaseline (
   BaselineID ASC
);
alter table CalcPointBaseline
   add constraint FK_CLCBS_BASL foreign key (BaselineID)
      references BaseLine (BaselineID);
alter table CalcPointBaseline
   add constraint FK_ClcPtBs_ClcBs foreign key (PointID)
      references CALCBASE (POINTID);

insert into calcpointbaseline select pointid, '1' from calccomponent WHERE functionname = 'Baseline';

insert into columntype values (15, 'QualityCount' );


/* @error ignore */
update billingfileformats set FormatType = 'CADP' where formatid = 1;

/* @error ignore */
update billingfileformats set FormatType = 'CADPXL2' where formatid = 2;


alter table FDRTRANSLATION drop constraint PK_FDRTrans;

alter table FDRTRANSLATION
   add constraint PK_FDRTrans primary key  (POINTID, InterfaceType, TRANSLATION);


insert into display values(50, 'SOE Log', 'Alarms and Events', 'SOE Log Viewer', 'This display shows all the SOE events in the SOE log table for a given day.');
insert into display values(51, 'TAG Log', 'Alarms and Events', 'TAG Log Viewer', 'This display shows all the TAG events in the TAG log table for a given day.');

insert into displaycolumns values(50, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(50, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(50, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(50, 'Description', 12, 4, 180 );
insert into displaycolumns values(50, 'Additional Info', 10, 5, 180 );

insert into displaycolumns values(51, 'Device Name', 5, 1, 70 );
insert into displaycolumns values(51, 'Point Name', 2, 2, 70 );
insert into displaycolumns values(51, 'Time Stamp', 11, 3, 60 );
insert into displaycolumns values(51, 'Description', 12, 4, 180 );
insert into displaycolumns values(51, 'Additional Info', 10, 5, 180 );
insert into displaycolumns values(51, 'User Name', 8, 6, 40 );
insert into displaycolumns values(51, 'Tag', 13, 7, 60 );

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/



/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.42', 'Ryan', '11-SEP-2003', 'Added some more roles, tag tables, new alarms, soe tables');