/* @error ignore-remaining */
update YukonRoleProperty set DefaultValue='Residential Customers', Description='Group name of all the residential customer logins' where RolePropertyID=-1105;
update YukonRoleProperty set DefaultValue='Web Client Operators' where RolePropertyID=-1106;

update YukonRoleProperty set KeyName='log_in_url', DefaultValue='/login.jsp', Description='The url where the user login from. It is used as the url to send the users to when they log off.' where RolePropertyID=-10806;
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

update YukonRoleProperty set KeyName='Super Operator', DefaultValue='false', Description='Used for some testing functions (not recommended)' where RolePropertyID=-20150;
update YukonRoleProperty set KeyName='New Account Wizard', DefaultValue='true', Description='Controls whether to enable the new account wizard' where RolePropertyID=-20151;
insert into YukonRoleProperty values(-20153,-201,'Inventory Checking Time','EARLY','Controls when to perform inventory checking while creating or updating hardware information. Possible values are EARLY, LATE, and NONE');
insert into YukonRoleProperty values(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
insert into YukonRoleProperty values(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
insert into YukonRoleProperty values(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-20800;
insert into YukonRoleProperty values(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
update YukonRoleProperty set DefaultValue='THERMOSTAT - SCHEDULE' where RolePropertyID=-20855;
update YukonRoleProperty set DefaultValue='THERMOSTAT - MANUAL' where RolePropertyID=-20856;

insert into YukonRole values (-209,'Inventory','Operator','Operator Access to hardware inventory');
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');

insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

update YukonRoleProperty set KeyName='Notification on General Page', DefaultValue='false', Description='Controls whether to show the notification email box on the general page (useful only when the programs enrollment feature is not selected)' where RolePropertyID=-40050;
update YukonRoleProperty set KeyName='Hide Opt Out Box', DefaultValue='false', Description='Controls whether to show the opt out box on the programs opt out page' where RolePropertyID=-40051;
insert into YukonRoleProperty values(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
update YukonRoleProperty set KeyName='Disable Program Signup', DefaultValue='false', Description='Controls whether to prevent the customers from enrolling in or out of the programs' where RolePropertyID=-40054;
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-40100;
update YukonRoleProperty set DefaultValue='(none)' where RolePropertyID=-40101;
insert into YukonRoleProperty values(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
update YukonRoleProperty set DefaultValue='THERMOSTAT - SCHEDULE' where RolePropertyID=-40157;
update YukonRoleProperty set DefaultValue='THERMOSTAT - MANUAL' where RolePropertyID=-40158;
update YukonRoleProperty set Description='Description on the contact us page. The special fields will be replaced by real information when displayed on the web.' where RolePropertyID=-40173;

insert into yukongrouprole values (507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (508,-300,-108,-10808,'(none)');
insert into yukongrouprole values (552,-300,-400,-40052,'false');
insert into yukongrouprole values (602,-300,-400,-40102,'(none)');

insert into yukongrouprole values (707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (708,-301,-108,-10808,'(none)');
insert into yukongrouprole values (753,-301,-201,-20153,'(none)');
insert into yukongrouprole values (754,-301,-201,-20154,'false');
insert into yukongrouprole values (755,-301,-201,-20155,'true');
insert into yukongrouprole values (756,-301,-201,-20156,'true');
insert into yukongrouprole values (765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (766,-301,-210,-21001,'(none)');
insert into yukongrouprole values (791,-301,-209,-20900,'(none)');
insert into yukongrouprole values (792,-301,-209,-20901,'(none)');
insert into yukongrouprole values (793,-301,-209,-20902,'(none)');
insert into yukongrouprole values (794,-301,-209,-20903,'(none)');
insert into yukongrouprole values (795,-301,-209,-20904,'(none)');
insert into yukongrouprole values (801,-301,-201,-20801,'(none)');



update YukonRoleProperty set KeyName = 'nav_bullet_expand', DefaultValue = 'BulletExpand.gif', Description = 'The bullet used when an item in the nav can be expanded to show submenu.' where RolePropertyID = -10804;
delete from YukonRoleProperty where RolePropertyID <= -20130 and RolePropertyID >= -20132;
delete from YukonRoleProperty where RolePropertyID <= -20152 and RolePropertyID >= -20162;
 
insert into YukonRoleProperty values(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
insert into YukonRoleProperty values(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
insert into YukonRoleProperty values(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardwares by serial number range');
insert into YukonRoleProperty values(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardwares by serial number range');
insert into YukonRoleProperty values(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardwares by serial number range');
 
delete from YukonRoleProperty where RolePropertyID <= -40030 and RolePropertyID >= -40033;
 
delete from YukonRoleProperty where RolePropertyID = -40052;
delete from YukonRoleProperty where RolePropertyID = -40053;
 
delete from YukonRoleProperty where RolePropertyID <= -40055 and RolePropertyID >= -40068;
 
insert into YukonRoleProperty values(-20152,-201,'Import Customer Account','false','Controls whether to enable the customer account importing feature');
insert into YukonRoleProperty values(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
 
insert into yukongrouprole values (500,-300,-108,-10800,'/user/ConsumerStat/stat/General.jsp');
insert into yukongrouprole values (501,-300,-108,-10801,'(none)');
insert into yukongrouprole values (502,-300,-108,-10802,'(none)');
insert into yukongrouprole values (503,-300,-108,-10803,'(none)');
insert into yukongrouprole values (504,-300,-108,-10804,'(none)');
insert into yukongrouprole values (505,-300,-108,-10805,'DemoHeaderCES.gif');
insert into yukongrouprole values (506,-300,-108,-10806,'(none)');
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
insert into yukongrouprole values (554,-300,-400,-40054,'false');
insert into yukongrouprole values (600,-300,-400,-40100,'(none)');
insert into yukongrouprole values (601,-300,-400,-40101,'(none)');
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
insert into yukongrouprole values (680,-300,-400,-40180,'(none)');
insert into yukongrouprole values (681,-300,-400,-40181,'(none)');
 
insert into yukongrouprole values (700,-301,-108,-10800,'/operator/Operations.jsp');
insert into yukongrouprole values (701,-301,-108,-10801,'(none)');
insert into yukongrouprole values (702,-301,-108,-10802,'(none)');
insert into yukongrouprole values (703,-301,-108,-10803,'(none)');
insert into yukongrouprole values (704,-301,-108,-10804,'(none)');
insert into yukongrouprole values (705,-301,-108,-10805,'(none)');
insert into yukongrouprole values (706,-301,-108,-10806,'(none)');
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
insert into yukongrouprole values (800,-301,-201,-20800,'(none)');
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
 
 
insert into YukonRoleProperty values(-40172,-400,'Description Program','(none)','Description on the programs details page. If not provided, the descriptions of the published programs will be used.');
insert into YukonRoleProperty values(-40173,-400,'Description Utility','<<COMPANY_ADDRESS>><br><<PHONE_NUMBER>><<FAX_NUMBER>><<EMAIL>>','Description on the contact us page. The special fields in the default value will be replaced by real information when showing on the web.');
 
insert into yukongrouprole values (672,-300,-400,-40172,'(none)');
insert into yukongrouprole values (673,-300,-400,-40173,'(none)');
 
insert into columntype values (15, 'QualityCount' );

alter table FDRTRANSLATION drop constraint PK_FDRTrans;
 
alter table FDRTRANSLATION add constraint PK_FDRTrans primary key  (POINTID, InterfaceType, TRANSLATION);
 
 
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

update YukonUser set username = 'admin', password = 'admin' where userid = -1;
update YukonUser set username = 'yukon', password = 'yukon' where userid = -2;
insert into YukonUser values(-2,'yukon','yukon',0,'01-JAN-00','Enabled');

alter table tags drop column refstr;
alter table tags drop column forstr;