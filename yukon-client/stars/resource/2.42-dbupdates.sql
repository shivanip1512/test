/* energy company role properties */
update YukonRoleProperty set DefaultValue='Residential Customers', Description='Group name of all the residential customer logins' where RolePropertyID=-1105;
update YukonRoleProperty set DefaultValue='Web Client Operators' where RolePropertyID=-1106;

/* web client role properties */
update YukonRoleProperty set KeyName='log_in_url', DefaultValue='/login.jsp', Description='The url where the user login from. It is used as the url to send the users to when they log off.' where RolePropertyID=-10806;
insert into YukonRoleProperty values(-10807,-108,'nav_connector_bottom','BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
insert into YukonRoleProperty values(-10808,-108,'nav_connector_middle','MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');

/* operator consumer info role properties */
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

/* operator inventory management role properties */
insert into YukonRoleProperty values(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardwares by serial number range');

/* operator work order management role */
insert into YukonRole values (-210,'Work Order','Operator','Operator Access to work order management');

/* operator work order management role properties */
insert into YukonRoleProperty values(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
insert into YukonRoleProperty values(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');

/* residential customer role properties */
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

/* residential customers group */
insert into yukongrouprole values (507,-300,-108,-10807,'(none)');
insert into yukongrouprole values (508,-300,-108,-10808,'(none)');
insert into yukongrouprole values (552,-300,-400,-40052,'false');
insert into yukongrouprole values (602,-300,-400,-40102,'(none)');

/* web client operators group */
insert into yukongrouprole values (707,-301,-108,-10807,'(none)');
insert into yukongrouprole values (708,-301,-108,-10808,'(none)');
insert into yukongrouprole values (753,-301,-201,-20153,'(none)');
insert into yukongrouprole values (754,-301,-201,-20154,'false');
insert into yukongrouprole values (755,-301,-201,-20155,'true');
insert into yukongrouprole values (756,-301,-201,-20156,'true');
insert into yukongrouprole values (765,-301,-210,-21000,'(none)');
insert into yukongrouprole values (766,-301,-210,-21001,'(none)');
insert into yukongrouprole values (795,-301,-209,-20904,'(none)');
insert into yukongrouprole values (801,-301,-201,-20801,'(none)');
