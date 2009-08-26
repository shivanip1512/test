/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-7455 */
UPDATE YukonRoleProperty 
SET KeyName='Opt Out Period', Description='The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)'
WHERE RolePropertyId IN (-20157, -40055);

DELETE FROM YukonListEntry 
WHERE ListId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod'
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList'));

DELETE FROM ECToGenericMapping 
WHERE ItemId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'OptOutPeriod' 
                 AND ListId IN (SELECT ItemId 
                                FROM ECToGenericMapping 
                                WHERE MappingCategory LIKE 'YukonSelectionList')); 

DELETE FROM YukonSelectionList 
WHERE ListName = 'OptOutPeriod'; 
/* End YUK-7455 */

/* Start YUK-7618 */
UPDATE YukonRoleProperty 
SET Description = 'Defines a Yukon Pao (Device) Name field alias. Valid values(0-5): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location, 5=Grid Location, 6=Service Location [Position]]' 
WHERE RolePropertyId = -1600;
/* End YUK-7618 */

/* Start YUK-7461 */
INSERT INTO YukonRoleProperty VALUES (-21308,-213,'Add/Remove Points','false','Controls access to Add/Remove Points collection action.');
/* End YUK-7461 */

/* Start YUK-7594 */
ALTER TABLE CCStrategyTimeOfDay ADD WkndPercentClose NUMERIC;
GO
UPDATE CCStrategyTimeOfDay SET WkndPercentClose = 0;
GO
ALTER TABLE CCStrategyTimeOfDay ALTER COLUMN WkndPercentClose NUMERIC NOT NULL;
GO
/* End YUK-7594 */

/* Start YUK-7498 */
ALTER TABLE PAOScheduleAssignment ADD DisableOvUv VARCHAR(1);
GO
UPDATE PAOScheduleAssignment SET DisableOvUv = 'N';
GO
ALTER TABLE PAOScheduleAssignment ALTER COLUMN DisableOvUv VARCHAR(1) NOT NULL; 
GO
/* End YUK-7498 */

/* Start YUK-6623 */
UPDATE Command 
SET Command = 'putconfig xcom temp service enable', Label = 'Temp Out-Of-Service Cancel' 
WHERE CommandId = -69;
/* End YUK-6623 */

/* Start YUK-7587 */
ALTER TABLE MCTBroadCastMapping
   DROP CONSTRAINT FK_MCTB_MAPMCT;
GO
ALTER TABLE MCTBroadCastMapping
   ADD CONSTRAINT FK_MCTBCM_Device_MCTId FOREIGN KEY (MCTId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
GO
/* End YUK-7587 */

/* Start YUK-7404 */
ALTER TABLE DeviceSeries5RTU
    DROP Constraint FK_DvS5r_Dv2w;
GO
ALTER TABLE DeviceSeries5RTU
   ADD CONSTRAINT FK_DeviceSer5RTU_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId);
GO

DROP TABLE Device2WayFlags;
/* End YUK-7404 */

/* Start YUK-7384 */
INSERT INTO YukonRoleProperty VALUES(-20603,-206,'Esub Home URL','/esub/sublist.html','The url of the starting page for esubstation. Usually the sublist page.');
/* End YUK-7384 */

/* Start YUK-7662 */
DELETE FROM YukonGroupRole WHERE GroupRoleId IN (-1084, -1085, -1086);
DELETE FROM YukonUserRole WHERE UserRoleID IN (-350, -351, -352);
/* End YUK-7662 */

/* Start YUK-7637 */
ALTER TABLE LMHardwareControlGroup ADD ProgramId INT;

UPDATE LMHardwareControlGroup
SET ProgramId = (SELECT LMPWP.ProgramID
                 FROM LMProgramDirectGroup LMPDG, LMProgramWebPublishing LMPWP
                 WHERE LMPDG.DeviceID =LMPWP.DeviceID
                 AND LMPDG.LMGroupDeviceID = LMHardwareControlGroup.LMGroupID);
                 
UPDATE LMHardwareControlGroup
SET ProgramId = 0
WHERE ProgramId IS NULL;

ALTER TABLE LMHardwareControlGroup ALTER COLUMN ProgramId INT NOT NULL;
/* End YUK-7637 */

/* Start YUK-7175 */
DELETE RawPointHistory WHERE PointId NOT IN (SELECT DISTINCT PointId FROM Point);
/* End YUK-7175 */

/* Start YUK-7731 */
ALTER TABLE DeviceConfigurationItem DROP CONSTRAINT FK_DEVICECO_REF_DEVICEC2;

ALTER TABLE DeviceConfigurationItem ALTER COLUMN FieldName VARCHAR(60) NOT NULL;
ALTER TABLE DeviceConfigurationItem ALTER COLUMN Value VARCHAR(60) NOT NULL;

ALTER TABLE DeviceConfigurationItem
   ADD CONSTRAINT FK_DevConfItem_DevConf FOREIGN KEY (DeviceConfigurationId)
      REFERENCES DeviceConfiguration (DeviceConfigurationId)
      ON DELETE CASCADE;
/* End YUK-7731 */

/* Start YUK-7735 */
CREATE TABLE TamperFlagMonitor (
   TamperFlagMonitorId  NUMERIC              NOT NULL,
   TamperFlagMonitorName VARCHAR(255)         NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   EvaluatorStatus      VARCHAR(255)         NOT NULL,
   constraint PK_TAMPERFLAGMONITOR PRIMARY KEY (TamperFlagMonitorId)
);
GO

CREATE UNIQUE INDEX INDX_TampFlagMonName_UNQ ON TamperFlagMonitor (
    TamperFlagMonitorName ASC
);
GO
/* End YUK-7735 */

/* Start YUK-7719 */
ALTER TABLE DeviceTNPPSettings
ALTER COLUMN PagerID VARCHAR(10);
/* End YUK-7719 */

/* Start YUK-7176 */
CREATE TABLE YukonRolePropertyTemp (
   RolePropertyId       NUMERIC              NOT NULL,
   RoleId               NUMERIC              NOT NULL,
   KeyName              VARCHAR(100)         NOT NULL,
   DefaultValue         VARCHAR(1000)        NOT NULL,
   Description          VARCHAR(1000)        NOT NULL
);
GO

/* Yukon Role */
INSERT INTO YukonRolePropertyTemp VALUES(-1000,-1,'dispatch_machine','127.0.0.1','Name or IP address of the Yukon Dispatch Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1001,-1,'dispatch_port','1510','TCP/IP port of the Yukon Dispatch Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1002,-1,'porter_machine','127.0.0.1','Name or IP address of the Yukon Port Control Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1003,-1,'porter_port','1540','TCP/IP port of the Yukon Port Control Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1004,-1,'macs_machine','127.0.0.1','Name or IP address of the Yukon Metering and Control Scheduler Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1005,-1,'macs_port','1900','TCP/IP port of the Yukon Metering and Control Scheduler Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1006,-1,'cap_control_machine','127.0.0.1','Name or IP address of the Yukon Capacitor Control Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1007,-1,'cap_control_port','1910','TCP/IP port of the Yukon Capacitor Control Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1008,-1,'loadcontrol_machine','127.0.0.1','Name or IP Address of the Yukon Load Management Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1009,-1,'loadcontrol_port','1920','TCP/IP port of the Yukon Load Management Service');
INSERT INTO YukonRolePropertyTemp VALUES(-1010,-1,'smtp_host','127.0.0.1','Name or IP address of the mail server');
INSERT INTO YukonRolePropertyTemp VALUES(-1011,-1,'mail_from_address','yukon@cannontech.com','Name of the FROM email address the mail server will use');
INSERT INTO YukonRolePropertyTemp VALUES(-1013,-1,'stars_preload_data','true','Controls whether the STARS application should preload data into the cache.');
INSERT INTO YukonRolePropertyTemp VALUES(-1014,-1,'web_logo','CannonLogo.gif','The logo that is used for the yukon web applications');
INSERT INTO YukonRolePropertyTemp VALUES(-1015,-1,'voice_host','127.0.0.1','Name or IP address of the voice server');
INSERT INTO YukonRolePropertyTemp VALUES(-1016,-1,'notification_host','127.0.0.1','Name or IP address of the Yukon Notification service');
INSERT INTO YukonRolePropertyTemp VALUES(-1017,-1,'notification_port','1515','TCP/IP port of the Yukon Notification service');
INSERT INTO YukonRolePropertyTemp VALUES(-1019,-1,'batched_switch_command_timer','auto','Specifies whether the STARS application should automatically process batched switch commands');
INSERT INTO YukonRolePropertyTemp VALUES(-1020,-1,'stars_activation','false','Specifies whether STARS functionality should be allowed in this web deployment.');
INSERT INTO YukonRolePropertyTemp VALUES(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 

/* Energy Company Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-1100,-2,'admin_email_address','info@cannontech.com','Sender address of emails sent on behalf of energy company, e.g. control odds and opt out notification emails.');
INSERT INTO YukonRolePropertyTemp VALUES(-1101,-2,'optout_notification_recipients','(none)','Recipients of the opt out notification email');
INSERT INTO YukonRolePropertyTemp VALUES(-1102,-2,'default_time_zone','CST','Default time zone of the energy company');
INSERT INTO YukonRolePropertyTemp VALUES(-1105,-2,'customer_group_ids','-300','Group IDs of all the residential customer logins');
INSERT INTO YukonRolePropertyTemp VALUES(-1106,-2,'operator_group_ids','-301','Group IDs of all the web client operator logins');
INSERT INTO YukonRolePropertyTemp VALUES(-1107,-2,'track_hardware_addressing','false','Controls whether to track the hardware addressing information.');
INSERT INTO YukonRolePropertyTemp VALUES(-1108,-2,'single_energy_company','true','Indicates whether this is a single energy company system.');
INSERT INTO YukonRolePropertyTemp VALUES(-1109,-2,'z_optional_product_dev','00000000','This feature is for development purposes only');
INSERT INTO YukonRolePropertyTemp VALUES(-1110,-2,'Default Temperature Unit','F','Default temperature unit for an energy company, F(ahrenheit) or C(elsius)');
INSERT INTO YukonRolePropertyTemp VALUES(-1111,-2,'z_meter_mct_base_desig','yukon','Allow meters to be used general STARS entries versus Yukon MCTs');
INSERT INTO YukonRolePropertyTemp VALUES(-1112,-2,'applicable_point_type_key',' ','The name of the set of CICustomerPointData TYPES that should be set for customers.');
INSERT INTO YukonRolePropertyTemp VALUES(-1114,-2,'Inherit Parent App Cats','true','If part of a member structure, should appliance categories be inherited from the parent.');

INSERT INTO YukonRolePropertyTemp VALUES(-1300,-4,'server_address','127.0.0.1','Authentication server machine address');
INSERT INTO YukonRolePropertyTemp VALUES(-1301,-4,'auth_port','1812','Authentication port.');
INSERT INTO YukonRolePropertyTemp VALUES(-1302,-4,'acct_port','1813','Accounting port.');
INSERT INTO YukonRolePropertyTemp VALUES(-1303,-4,'secret_key','cti','Client machine secret key value, defined by the server.');
INSERT INTO YukonRolePropertyTemp VALUES(-1304,-4,'auth_method','(none)','Authentication method. Possible values are (none) | PAP, [chap, others to follow soon]');
INSERT INTO YukonRolePropertyTemp VALUES(-1305,-4,'authentication_mode','Yukon','Authentication mode to use.  Valid values are:   Yukon | Radius');
INSERT INTO YukonRolePropertyTemp VALUES(-1306,-4,'auth_timeout','30','Number of seconds before the authentication process times out');
INSERT INTO YukonRolePropertyTemp VALUES(-1307,-4,'Default Authentication Type', 'PLAIN', 'Set the default authentication type to use {PLAIN,HASH_SHA,RADIUS,AD,LDAP,NONE}');
INSERT INTO YukonRolePropertyTemp VALUES(-1308,-4,'LDAP DN','dc=example,dc=com','LDAP Distinguished Name');
INSERT INTO YukonRolePropertyTemp VALUES(-1309,-4,'LDAP User Suffix','ou=users','LDAP User Suffix');
INSERT INTO YukonRolePropertyTemp VALUES(-1310,-4,'LDAP User Prefix','uid=','LDAP User Prefix');
INSERT INTO YukonRolePropertyTemp VALUES(-1311,-4,'LDAP Server Address','127.0.0.1','LDAP Server Address');
INSERT INTO YukonRolePropertyTemp VALUES(-1312,-4,'LDAP Server Port','389','LDAP Server Port');
INSERT INTO YukonRolePropertyTemp VALUES(-1313,-4,'LDAP Server Timeout','30','LDAP Server Timeout (in seconds)');
INSERT INTO YukonRolePropertyTemp VALUES(-1314,-4,'Active Directory Server Address','127.0.0.1','Active Directory Server Address');
INSERT INTO YukonRolePropertyTemp VALUES(-1315,-4,'Active Directory Server Port','389','Active Directory Server Port');
INSERT INTO YukonRolePropertyTemp VALUES(-1316,-4,'Active Directory Server Timeout','30','Active Directory Server Timeout (in seconds)');
INSERT INTO YukonRolePropertyTemp VALUES(-1317,-4,'Active Directory NT Domain Name','(none)','Active Directory NT DOMAIN NAME');
INSERT INTO YukonRolePropertyTemp VALUES(-1318,-4,'Enable Password Recovery','true','Controls access to password recovery (Forgot your password?) feature.');

INSERT INTO YukonRolePropertyTemp VALUES(-1402,-5,'call_response_timeout','240','The time-out in seconds given to each outbound call response');
INSERT INTO YukonRolePropertyTemp VALUES(-1403,-5,'Call Prefix','(none)','Any number or numbers that must be dialed before a call can be placed.');

/* Billing Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-1500,-6,'wiz_activate','false','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-1501,-6,'input_file','c:yukonclientbinBillingIn.txt','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-1503,-6,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
INSERT INTO YukonRolePropertyTemp VALUES(-1504,-6,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
INSERT INTO YukonRolePropertyTemp VALUES(-1505,-6,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
INSERT INTO YukonRolePropertyTemp VALUES(-1506,-6,'Append To File','false','Append to existing file.');
INSERT INTO YukonRolePropertyTemp VALUES(-1507,-6,'Remove Multiplier','false','Remove the multiplier value from the reading.');
INSERT INTO YukonRolePropertyTemp VALUES(-1508,-6,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');
INSERT INTO YukonRolePropertyTemp VALUES(-1509,-6,'Rounding Mode','HALF_EVEN','Rounding Mode used when formatting value data in billing formats. Available placeholders: HALF_EVEN, CEILING, FLOOR, UP, DOWN, HALF_DOWN, HALF_UP'); 

/* Database Editor Role */
INSERT INTO YukonRolePropertyTemp VALUES(-10000,-100,'point_id_edit','false','Controls whether point ids can be edited');
INSERT INTO YukonRolePropertyTemp VALUES(-10001,-100,'dbeditor_core','true','Controls whether the Core menu item in the View menu is displayed');
INSERT INTO YukonRolePropertyTemp VALUES(-10002,-100,'dbeditor_lm','true','Controls whether the Loadmanagement menu item in the View menu is displayed');
INSERT INTO YukonRolePropertyTemp VALUES(-10004,-100,'dbeditor_system','true','Controls whether the System menu item in the View menu is displayed');
INSERT INTO YukonRolePropertyTemp VALUES(-10005,-100,'utility_id_range','1-254','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10007,-100,'dbeditor_trans_exclusion','false','Allows the editor panel for the mutual exclusion of transmissions to be shown');
INSERT INTO YukonRolePropertyTemp VALUES(-10008,-100,'permit_login_edit','true','Closes off all access to logins and login groups for non-administrators in the dbeditor');
INSERT INTO YukonRolePropertyTemp VALUES(-10009,-100,'allow_user_roles','false','Allows the editor panel individual user roles to be shown');
INSERT INTO YukonRolePropertyTemp VALUES(-10010,-100,'z_optional_product_dev','00000000','This feature is for development purposes only');
INSERT INTO YukonRolePropertyTemp VALUES(-10011,-100,'allow_member_programs','false','Allows member management of LM Direct Programs through the DBEditor');

/* MultiSpeak */
INSERT INTO YukonRolePropertyTemp VALUES(-1600,-7,'PAOName Alias','0','Defines a Yukon Pao (Device) Name field alias. Valid VALUES(0-5): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location, 5=Grid Location, 6=Service Location [Position]]'); 
INSERT INTO YukonRolePropertyTemp VALUES(-1601,-7,'Primary CIS Vendor','0','Defines the primary CIS vendor for CB interfaces.');
INSERT INTO YukonRolePropertyTemp VALUES(-1602,-7,'Msp BillingCycle DeviceGroup','/Meters/Billing/','Defines the Device Group parent group name for the MultiSpeak billingCycle element. Valid values are ''/Meters/Billing/'', ''/Meters/Collection'', ''/Meters/Alternate''');
INSERT INTO YukonRolePropertyTemp VALUES(-1603,-7,'Msp LM Interface Mapping Setup','false','Controls access to setup the MultiSpeak LM interface mappings.');

/* Configuration */
INSERT INTO YukonRolePropertyTemp VALUES(-1700,-8,'Device Display Template','DEVICE_NAME','Defines the format for displaying devices. Available placeholders: DEVICE_NAME, METER_NUMBER, ID, ADDRESS');
INSERT INTO YukonRolePropertyTemp VALUES(-1701,-8,'Alert Timeout Hours', '168', 'The number of hours that an alert should be held (zero = forever, decimal numbers are okay)'); 
INSERT INTO YukonRolePropertyTemp VALUES(-1702,-8,'Customer Info Importer File Location', ' ', 'File location of the automated consumer information import process.');
INSERT INTO YukonRolePropertyTemp VALUES(-1703,-8,'System Default TimeZone', ' ', 'System Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)'); 
INSERT INTO YukonRolePropertyTemp VALUES(-1704,-8,'Opt Outs Count', 'true', 'Determines whether new opt outs count against the opt out limits.'); 

/* TDC Role */
INSERT INTO YukonRolePropertyTemp VALUES(-10100,-101,'loadcontrol_edit','00000000','(No settings yet)');
INSERT INTO YukonRolePropertyTemp VALUES(-10101,-101,'macs_edit','00000CCC','The following settings are valid: CREATE_SCHEDULE(0x0000000C), ENABLE_SCHEDULE(0x000000C0), ABLE_TO_START_SCHEDULE(0x00000C00)');
INSERT INTO YukonRolePropertyTemp VALUES(-10102,-101,'tdc_express','ludicrous_speed','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10103,-101,'tdc_max_rows','500','The number of rows shown before creating a new page of data');
INSERT INTO YukonRolePropertyTemp VALUES(-10104,-101,'tdc_rights','00000000','The following settings are valid: HIDE_MACS(0x00001000), HIDE_CAPCONTROL(0x00002000), HIDE_LOADCONTROL(0x00004000), HIDE_ALL_DISPLAYS(0x0000F000), CONTROL_YUKON_SERVICES(0x00010000), HIDE_ALARM_COLORS(0x80000000)');
INSERT INTO YukonRolePropertyTemp VALUES(-10107,-101,'tdc_alarm_count','3','Total number alarms that are displayed in the quick access list');
INSERT INTO YukonRolePropertyTemp VALUES(-10108,-101,'decimal_places','2','How many decimal places to show for real values');
INSERT INTO YukonRolePropertyTemp VALUES(-10111,-101,'lc_reduction_col','true','Tells TDC to show the LoadControl reduction column or not');


/* Trending Role */
INSERT INTO YukonRolePropertyTemp VALUES(-10200,-102,'graph_edit_graphdefinition','true','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10202, -102, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
INSERT INTO YukonRolePropertyTemp VALUES(-10203, -102, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
INSERT INTO YukonRolePropertyTemp VALUES(-10205, -102, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
INSERT INTO YukonRolePropertyTemp VALUES(-10206, -102, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');

/* Commander Role Properties */ 
INSERT INTO YukonRolePropertyTemp VALUES(-10300,-103,'msg_priority','14','Tells commander what the outbound priority of messages are (low)1 - 14(high)');
INSERT INTO YukonRolePropertyTemp VALUES(-10301,-103,'Versacom Serial','true','Show a Versacom Serial Number SortBy display');
INSERT INTO YukonRolePropertyTemp VALUES(-10302,-103,'Expresscom Serial','true','Show an Expresscom Serial Number SortBy display');
INSERT INTO YukonRolePropertyTemp VALUES(-10303,-103,'DCU SA205 Serial','false','Show a DCU SA205 Serial Number SortBy display');
INSERT INTO YukonRolePropertyTemp VALUES(-10304,-103,'DCU SA305 Serial','false','Show a DCU SA305 Serial Number SortBy display');
INSERT INTO YukonRolePropertyTemp VALUES(-10305,-103,'Commands Group Name','Default Commands','The commands group name for the displayed commands.');
INSERT INTO YukonRolePropertyTemp VALUES(-10306,-103,'Read device', 'true', 'Allow the ability to read values from a device');
INSERT INTO YukonRolePropertyTemp VALUES(-10307,-103,'Write to device', 'true', 'Allow the ability to write values to a device');
INSERT INTO YukonRolePropertyTemp VALUES(-10309,-103,'Control disconnect', 'true', 'Allow the ability to control a disconnect to a device');
INSERT INTO YukonRolePropertyTemp VALUES(-10310,-103,'Read LM device', 'true', 'Allow the ability to read values from an LM device');
INSERT INTO YukonRolePropertyTemp VALUES(-10311,-103,'Write to LM device', 'true', 'Allow the ability to write values to an LM device');
INSERT INTO YukonRolePropertyTemp VALUES(-10312,-103,'Control LM device', 'true', 'Allow the ability to control an LM device');
INSERT INTO YukonRolePropertyTemp VALUES(-10313,-103,'Read Cap Control device', 'true', 'Allow the ability to read values from a Cap Control device');
INSERT INTO YukonRolePropertyTemp VALUES(-10314,-103,'Write to Cap Control device', 'true', 'Allow the ability to write values to a Cap Control device');
INSERT INTO YukonRolePropertyTemp VALUES(-10315,-103,'Control Cap Control device', 'true', 'Allow the ability to control a Cap Control device');
INSERT INTO YukonRolePropertyTemp VALUES(-10316,-103,'Execute Unknown Command', 'true', 'Allow the ability to execute commands which do not fall under another role property.');
INSERT INTO YukonRolePropertyTemp VALUES(-10317,-103,'Execute Manual Command', 'true', 'Allow the ability to execute manual commands');
INSERT INTO YukonRolePropertyTemp VALUES(-10318,-103,'Enable Web Commander', 'true', 'Controls access to web commander applications');
INSERT INTO YukonRolePropertyTemp VALUES(-10319,-103,'Enable Client Commander', 'true', 'Controls access to client commander application'); 

/* Calc Historical Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-10400,-104,'interval','900','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10401,-104,'baseline_calctime','4','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10402,-104,'daysprevioustocollect','30','<description>');

/* Web Graph Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-10500,-105,'home_directory','c:yukonclientwebgraphs','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10501,-105,'run_interval','900','<description>');
INSERT INTO YukonRolePropertyTemp VALUES(-10600,-106,'Dynamic Billing File Setup','true','Controls access to create, edit, and delete dynamic billing files.'); 

/* Esubstation Editor Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-10700,-107,'default','false','The default esub editor property');

/* Web Client Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-10800,-108,'home_url','/operator/Operations.jsp','The url to take the user immediately after logging into the Yukon web application');
INSERT INTO YukonRolePropertyTemp VALUES(-10802,-108,'style_sheet','yukon/CannonStyle.css','The web client cascading style sheet.');
INSERT INTO YukonRolePropertyTemp VALUES(-10803,-108,'nav_bullet_selected','yukon/Bullet.gif','The bullet used when an item in the nav is selected.');
INSERT INTO YukonRolePropertyTemp VALUES(-10804,-108,'nav_bullet_expand','yukon/BulletExpand.gif','The bullet used when an item in the nav can be expanded to show submenu.');
INSERT INTO YukonRolePropertyTemp VALUES(-10805,-108,'header_logo','yukon/DefaultHeader.gif','The main header logo');
INSERT INTO YukonRolePropertyTemp VALUES(-10806,-108,'log_in_url','/login.jsp','The url where the user login from. It is used as the url to send the users to when they log off.');
INSERT INTO YukonRolePropertyTemp VALUES(-10807,-108,'nav_connector_bottom','yukon/BottomConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of the last hardware under each category');
INSERT INTO YukonRolePropertyTemp VALUES(-10808,-108,'nav_connector_middle','yukon/MidConnector.gif','The connector icon in the nav used for showing the hardware tree structure, in front of every hardware except the last one under each category');
INSERT INTO YukonRolePropertyTemp VALUES(-10810,-108, 'pop_up_appear_style','onmouseover', 'Style of the popups appearance when the user selects element in capcontrol.');
INSERT INTO YukonRolePropertyTemp VALUES(-10811,-108, 'inbound_voice_home_url', '/voice/inboundOptOut.jsp', 'Home URL for inbound voice logins');
INSERT INTO YukonRolePropertyTemp VALUES(-10812, -108,'Java Web Start Launcher Enabled', 'true', 'Allow access to the Java Web Start Launcher for client applications.');
INSERT INTO YukonRolePropertyTemp VALUES(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');
INSERT INTO YukonRolePropertyTemp VALUES(-10815, -108,'Data Updater Delay (milliseconds)', '4000', 'The number of milliseconds between requests for the latest point values on pages that support the data updater.');
INSERT INTO YukonRolePropertyTemp VALUES(-10816, -108,'Standard Page Style Sheet',' ','A comma separated list of URLs for CSS files that will be included on every Standard Page');
INSERT INTO YukonRolePropertyTemp VALUES(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
INSERT INTO YukonRolePropertyTemp VALUES(-10818, -108, 'View Alarms Alerts','false','Ability to receive point alarms as alerts');
INSERT INTO YukonRolePropertyTemp VALUES(-10819, -108, 'Default TimeZone',' ','Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)');

/* Reporting Analysis role properties */
INSERT INTO YukonRolePropertyTemp VALUES(-10903,-109,'Admin Reports Group','true','Access to administrative group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10904,-109,'AMR Reports Group','true','Access to AMR group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10905,-109,'Statistical Reports Group','true','Access to statistical group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10906,-109,'Load Management Reports Group','false','Access to Load Management group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10907,-109,'Cap Control Reports Group','false','Access to Cap Control group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10908,-109,'Database Reports Group','true','Access to Database group reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-10909,-109,'Stars Reports Group','true','Access to Stars group reports.');
/* YUK-6642 INSERT INTO YukonRolePropertyTemp VALUES(-10911,-109,'Settlement Reports Group','false','Access to Settlement group reports.'); */ 
INSERT INTO YukonRolePropertyTemp VALUES(-10923,-109,'C&I Curtailment Reports Group','false','Access to C&I Curtailment group reports');

/* Operator Consumer Info Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20101,-201,'Account General','true','Controls whether to show the general account information');
INSERT INTO YukonRolePropertyTemp VALUES(-20102,-201,'Account Residence','false','Controls whether to show the customer residence information');
INSERT INTO YukonRolePropertyTemp VALUES(-20103,-201,'Account Call Tracking','false','Controls whether to enable the call tracking feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20104,-201,'Metering Interval Data','false','Controls whether to show the metering interval data');
INSERT INTO YukonRolePropertyTemp VALUES(-20106,-201,'Programs Control History','true','Controls whether to show the program control history');
INSERT INTO YukonRolePropertyTemp VALUES(-20107,-201,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20108,-201,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20109,-201,'Appliances','true','Controls whether to show the appliance information');
INSERT INTO YukonRolePropertyTemp VALUES(-20110,-201,'Appliances Create','true','Controls whether to enable the appliance creation feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20111,-201,'Hardware','true','Controls whether to show the hardware information');
INSERT INTO YukonRolePropertyTemp VALUES(-20112,-201,'Hardware Create','true','Controls whether to enable the hardware creation feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20113,-201,'Hardware Thermostat','true','Controls whether to enable the thermostat programming feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20114,-201,'Work Orders','false','Controls whether to enable the service request feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20115,-201,'Admin Change Login Username','true','Controls access to change a customer login username'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20116,-201,'Admin FAQ','false','Controls whether to show customer FAQs');
INSERT INTO YukonRolePropertyTemp VALUES(-20117,-201,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
INSERT INTO YukonRolePropertyTemp VALUES(-20118,-201,'Create Trend','false','Controls whether to allow new trends to assigned to the customer');
INSERT INTO YukonRolePropertyTemp VALUES(-20119,-201,'Admin Change Login Password','true','Controls access to change a customer login password'); 

/* Operator Consumer Info Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20151,-201,'New Account Wizard','true','Controls whether to enable the new account wizard');
INSERT INTO YukonRolePropertyTemp VALUES(-20152,-201,'Import Customer Account','(none)','Controls whether to enable the customer account importing feature');
INSERT INTO YukonRolePropertyTemp VALUES(-20153,-201,'Inventory Checking','true','Controls when to perform inventory checking while creating or updating hardware information');
INSERT INTO YukonRolePropertyTemp VALUES(-20154,-201,'Automatic Configuration','false','Controls whether to automatically send out config command when creating hardware or changing program enrollment');
INSERT INTO YukonRolePropertyTemp VALUES(-20155,-201,'Order Number Auto Generation','false','Controls whether the order number is automatically generated or entered by user');
INSERT INTO YukonRolePropertyTemp VALUES(-20156,-201,'Call Number Auto Generation','false','Controls whether the call number is automatically generated or entered by user');
INSERT INTO YukonRolePropertyTemp VALUES(-20157,-201,'Opt Out Period','(none)','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRolePropertyTemp VALUES(-20158,-201,'Disable Switch Sending','false','Disables the ability to send configs and connects/disconnects to switches.');
INSERT INTO YukonRolePropertyTemp VALUES(-20159,-201,'Switches to Meter','(none)','Allow switches to be assigned under meters for an account.');
INSERT INTO YukonRolePropertyTemp VALUES(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
INSERT INTO YukonRolePropertyTemp VALUES(-20161,-201,'Account Number Length','(none)','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.');
INSERT INTO YukonRolePropertyTemp VALUES(-20162,-201,'Rotation Digit Length','(none)','Specifies the number of rotation digit characters to ignore during the customer account import process.');
INSERT INTO YukonRolePropertyTemp VALUES(-20163,-201,'Allow Account Editing','true','Can be used to disable the ability to edit and delete customer account information.');
INSERT INTO YukonRolePropertyTemp VALUES(-20164,-201,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.');

/* Operator Administrator Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20000,-200,'Config Energy Company','false','Controls whether to allow configuring the energy company');
INSERT INTO YukonRolePropertyTemp VALUES(-20001,-200,'Create Energy Company','false','Controls whether to allow creating a new energy company');
INSERT INTO YukonRolePropertyTemp VALUES(-20002,-200,'Delete Energy Company','false','Controls whether to allow deleting the energy company');
INSERT INTO YukonRolePropertyTemp VALUES(-20003,-200,'Manage Members','false','Controls whether to allow managing the energy company''s members');
INSERT INTO YukonRolePropertyTemp VALUES(-20004,-200,'View Batch Commands','false','Controls whether to allow monitoring of all batched switch commands');
INSERT INTO YukonRolePropertyTemp VALUES(-20005,-200,'View Opt Out Events','false','Controls whether to allow monitoring of all scheduled opt out events');
INSERT INTO YukonRolePropertyTemp VALUES(-20006,-200,'Member Login Cntrl','false','Ignored if not a member company -- Controls whether operator logins are shown on the EC administration page.');
INSERT INTO YukonRolePropertyTemp VALUES(-20007,-200,'Member Route Select','false','Ignored if not a member company -- Controls whether routes are visible through the EC administration page.');
INSERT INTO YukonRolePropertyTemp VALUES(-20008,-200,'Allow Designation Codes','false','Toggles on or off the regional (usually zip) code option for service companies.');
INSERT INTO YukonRolePropertyTemp VALUES(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
INSERT INTO YukonRolePropertyTemp VALUES(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20011,-200,'MultiSpeak Setup','false','Controls access to configure the MultiSpeak Interfaces.');
INSERT INTO YukonRolePropertyTemp VALUES(-20012,-200,'LM User Assignment','false','Controls visibility of LM objects for 3-tier and direct control, based off assignment of users.');
INSERT INTO YukonRolePropertyTemp VALUES(-20013,-200,'Edit Device Config','false','Controls the ability to edit and create device configurations');
INSERT INTO YukonRolePropertyTemp VALUES(-20014,-200,'View Device Config','true','Controls the ability to view existing device configurations');
INSERT INTO YukonRolePropertyTemp VALUES(-20015,-200,'Manage Indexes','true','Controls access to manually build Lucene indexes.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20016,-200,'View Logs','true','Controls access to view or download log files.');

/* Operator Metering Role Properties*/
INSERT INTO YukonRolePropertyTemp VALUES(-20203,-202,'Enable Bulk Importer','true','Allows access to the Bulk Importer');
INSERT INTO YukonRolePropertyTemp VALUES(-20206,-202,'Profile Collection','true','Controls access to submit a (past) profile collection request');
INSERT INTO YukonRolePropertyTemp VALUES(-20207,-202,'Move In/Move Out Auto Archiving','true','Enables automatic archiving of move in/move out transactions');
INSERT INTO YukonRolePropertyTemp VALUES(-20208,-202,'Move In/Move Out','true','Controls access to process a move in/move out');
INSERT INTO YukonRolePropertyTemp VALUES(-20209,-202,'Profile Collection Scanning','true','Controls access to start/stop scanning of profile data'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20210,-202,'High Bill Complaint','true','Controls access to process a high bill complaint'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20211,-202,'CIS Info Widget Enabled','true','Controls access to view the CIS Information widget.');
INSERT INTO YukonRolePropertyTemp VALUES(-20212,-202,'CIS Info Type','NONE','Defines the type of CIS Information widget to display. Available placeholders: NONE, MULTISPEAK, CAYENTA');

/* Operator Esubstation Drawings Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20600,-206,'View Drawings','true','Controls viewing of Esubstations drawings');
INSERT INTO YukonRolePropertyTemp VALUES(-20601,-206,'Edit Limits','false','Controls editing of point limits');
INSERT INTO YukonRolePropertyTemp VALUES(-20602,-206,'Control','false','Controls control from Esubstation drawings');
INSERT INTO YukonRolePropertyTemp VALUES(-20603,-206,'Esub Home URL','/esub/sublist.html','The url of the starting page for esubstation. Usually the sublist page.');

/* Odds For Control Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20700,-207,'Odds For Control Label','Odds for Control','The operator specific name for odds for control');

/* Operator Consumer Info Role Properties Part II */
INSERT INTO YukonRolePropertyTemp VALUES(-20800,-201,'Link FAQ','(none)','The customized FAQ link');
INSERT INTO YukonRolePropertyTemp VALUES(-20801,-201,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');

INSERT INTO YukonRolePropertyTemp VALUES(-20810,-201,'Text Control','control','Term for control');
INSERT INTO YukonRolePropertyTemp VALUES(-20813,-201,'Text Opt Out Noun','opt out','Noun form of the term for opt out');
INSERT INTO YukonRolePropertyTemp VALUES(-20814,-201,'Text Opt Out Verb','opt out of','Verbical form of the term for opt out');
INSERT INTO YukonRolePropertyTemp VALUES(-20815,-201,'Text Opt Out Past','opted out','Past form of the term for opt out');
INSERT INTO YukonRolePropertyTemp VALUES(-20816,-201,'Text Reenable','reenable','Term for reenable');
INSERT INTO YukonRolePropertyTemp VALUES(-20819,-201,'Text Odds for Control','odds for control','Text for odds for control');
INSERT INTO YukonRolePropertyTemp VALUES(-20820,-201,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');

INSERT INTO YukonRolePropertyTemp VALUES(-20830,-201,'Label Programs Control History','Control History','Text of the programs control history link');
INSERT INTO YukonRolePropertyTemp VALUES(-20831,-201,'Label Programs Enrollment','Enrollment','Text of the programs enrollment link');
INSERT INTO YukonRolePropertyTemp VALUES(-20832,-201,'Label Programs Opt Out','Opt Out','Text of the programs opt out link');
INSERT INTO YukonRolePropertyTemp VALUES(-20833,-201,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
INSERT INTO YukonRolePropertyTemp VALUES(-20834,-201,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
INSERT INTO YukonRolePropertyTemp VALUES(-20835,-201,'Label Account General','General','Text of the account general link');
INSERT INTO YukonRolePropertyTemp VALUES(-20836,-201,'Label Account Contacts','Contacts','Text of the account contacts link');
INSERT INTO YukonRolePropertyTemp VALUES(-20837,-201,'Label Account Residence','Residence','Text of the account residence link');
INSERT INTO YukonRolePropertyTemp VALUES(-20838,-201,'Label Call Tracking','Call Tracking','Text of the call tracking link');
INSERT INTO YukonRolePropertyTemp VALUES(-20839,-201,'Label Create Call','Create Call','Text of the create call link');
INSERT INTO YukonRolePropertyTemp VALUES(-20840,-201,'Label Service Request','Service Request','Text of the service request link');
INSERT INTO YukonRolePropertyTemp VALUES(-20841,-201,'Label Service History','Service History','Text of the service history link');
INSERT INTO YukonRolePropertyTemp VALUES(-20842,-201,'Label Change Login','Change Login','Text of the change login link');
INSERT INTO YukonRolePropertyTemp VALUES(-20843,-201,'Label FAQ','FAQ','Text of the FAQ link');
INSERT INTO YukonRolePropertyTemp VALUES(-20844,-201,'Label Interval Data','Interval Data','Text of the interval data link');
INSERT INTO YukonRolePropertyTemp VALUES(-20845,-201,'Label Thermostat Saved Schedules','Saved Schedules','Text of the thermostat saved schedules link');
INSERT INTO YukonRolePropertyTemp VALUES(-20846,-201,'Label Alt Tracking #','Alt Tracking #','Text of the alternate tracking number label on a customer account');

INSERT INTO YukonRolePropertyTemp VALUES(-20850,-201,'Title Programs Control History','PROGRAMS - CONTROL HISTORY','Title of the programs control history page');
INSERT INTO YukonRolePropertyTemp VALUES(-20851,-201,'Title Program Control History','PROGRAM - CONTROL HISTORY','Title of the control history page of a particular program');
INSERT INTO YukonRolePropertyTemp VALUES(-20852,-201,'Title Program Control Summary','PROGRAM - CONTROL SUMMARY','Title of the control summary page of a particular program');
INSERT INTO YukonRolePropertyTemp VALUES(-20853,-201,'Title Programs Enrollment','PROGRAMS - ENROLLMENT','Title of the programs enrollment page');
INSERT INTO YukonRolePropertyTemp VALUES(-20854,-201,'Title Programs Opt Out','PROGRAMS - OPT OUT','Title of the programs opt out page');
INSERT INTO YukonRolePropertyTemp VALUES(-20855,-201,'Title Thermostat Schedule','THERMOSTAT - SCHEDULE','Title of the thermostat schedule page');
INSERT INTO YukonRolePropertyTemp VALUES(-20856,-201,'Title Thermostat Manual','THERMOSTAT - MANUAL','Title of the thermostat manual page');
INSERT INTO YukonRolePropertyTemp VALUES(-20857,-201,'Title Call Tracking','ACCOUNT - CALL TRACKING','Title of the call tracking page');
INSERT INTO YukonRolePropertyTemp VALUES(-20858,-201,'Title Create Call','ACCOUNT - CREATE NEW CALL','Title of the create call page');
INSERT INTO YukonRolePropertyTemp VALUES(-20859,-201,'Title Service Request','WORK ORDERS - SERVICE REQUEST','Title of the service request page');
INSERT INTO YukonRolePropertyTemp VALUES(-20860,-201,'Title Service History','WORK ORDERS - SERVICE HISTORY','Title of the service history page');
INSERT INTO YukonRolePropertyTemp VALUES(-20861,-201,'Title Change Login','ADMINISTRATION - CHANGE LOGIN','Title of the change login page');
INSERT INTO YukonRolePropertyTemp VALUES(-20862,-201,'Title Create Trend','METERING - CREATE NEW TREND','Title of the create trend page');
INSERT INTO YukonRolePropertyTemp VALUES(-20863,-201,'Title Thermostat Saved Schedules','THERMOSTAT - SAVED SCHEDULES','Title of the thermostat saved schedules page');
INSERT INTO YukonRolePropertyTemp VALUES(-20864,-201,'Title Hardware Overriding','HARDWARE - OVERRIDING','Title of the hardware overriding page');
INSERT INTO YukonRolePropertyTemp VALUES(-20870,-201,'Description Opt Out','If you would like to temporarily opt out of all programs, select the time frame below, then click Submit.','Description on the programs opt out page');

INSERT INTO YukonRolePropertyTemp VALUES(-20880,-201,'Heading Account','Account','Heading of the account links');
INSERT INTO YukonRolePropertyTemp VALUES(-20881,-201,'Heading Metering','Metering','Heading of the metering links');
INSERT INTO YukonRolePropertyTemp VALUES(-20882,-201,'Heading Programs','Programs','Heading of the program links');
INSERT INTO YukonRolePropertyTemp VALUES(-20883,-201,'Heading Appliances','Appliances','Heading of the appliance links');
INSERT INTO YukonRolePropertyTemp VALUES(-20884,-201,'Heading Hardware','Hardware','Heading of the hardware links');
INSERT INTO YukonRolePropertyTemp VALUES(-20885,-201,'Heading Work Orders','Work Orders','Heading of the work order links');
INSERT INTO YukonRolePropertyTemp VALUES(-20886,-201,'Heading Administration','Administration','Heading of the administration links');
INSERT INTO YukonRolePropertyTemp VALUES(-20887,-201,'Sub-Heading Switches','Switches','Sub-heading of the switch links');
INSERT INTO YukonRolePropertyTemp VALUES(-20888,-201,'Sub-Heading Thermostats','Thermostats','Sub-heading of the thermostat links');
INSERT INTO YukonRolePropertyTemp VALUES(-20889,-201,'Sub-Heading Meters','Meters','Sub-heading of the meter links');
INSERT INTO YukonRolePropertyTemp VALUES(-20890,-201,'Address State Label','State','Labelling for the address field which is usually state in the US or province in Canada');
INSERT INTO YukonRolePropertyTemp VALUES(-20891,-201,'Address County Label','County','Labelling for the address field which is usually county in the US or postal code in Canada');
INSERT INTO YukonRolePropertyTemp VALUES(-20892,-201,'Address PostalCode Label','Zip','Labelling for the address field which is usually zip code in the US or postal code in Canada');
INSERT INTO YukonRolePropertyTemp VALUES(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');
INSERT INTO YukonRolePropertyTemp VALUES(-20894,-201,'Opt Out Today Only','false','Prevents operator side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRolePropertyTemp VALUES(-20895,-201,'Opt Out Admin Status','true','Determines whether an operator can see current opt out status on the Opt Out Admin page.');
INSERT INTO YukonRolePropertyTemp VALUES(-20896,-201,'Opt Out Admin Change Enabled','true','Determines whether an operator can enable or disable Opt Outs for the rest of the day.');
INSERT INTO YukonRolePropertyTemp VALUES(-20897,-201,'Opt Out Admin Cancel Current','true','Determines whether an operator can cancel (reenable) ALL currently Opted Out devices.');
INSERT INTO YukonRolePropertyTemp VALUES(-20898,-201,'Opt Out Admin Change Counts','true','Determines whether an operator can change from Opt Outs count against limits today to Opt Outs do not count.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-20899,-201,'Thermostat Schedule 5-2','false','Allows a user to select Weekday/Weekend in addition to Weekday/Saturday/Sunday for thermostat schedule editing.');

/* Operator Hardware Inventory Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-20900,-209,'Show All Inventory','true','Controls whether to allow showing all inventory');
INSERT INTO YukonRolePropertyTemp VALUES(-20901,-209,'Add SN Range','true','Controls whether to allow adding hardware by serial number range');
INSERT INTO YukonRolePropertyTemp VALUES(-20902,-209,'Update SN Range','true','Controls whether to allow updating hardware by serial number range');
INSERT INTO YukonRolePropertyTemp VALUES(-20903,-209,'Config SN Range','true','Controls whether to allow configuring hardware by serial number range');
INSERT INTO YukonRolePropertyTemp VALUES(-20904,-209,'Delete SN Range','true','Controls whether to allow deleting hardware by serial number range');
INSERT INTO YukonRolePropertyTemp VALUES(-20905,-209,'Create Hardware','true','Controls whether to allow creating new hardware');
INSERT INTO YukonRolePropertyTemp VALUES(-20906,-209,'Expresscom Restore First','false','Controls whether an opt out command should also contain a restore');
INSERT INTO YukonRolePropertyTemp VALUES(-20907,-209,'Allow Designation Codes','false','Toggles on or off the ability utilize service company zip codes.');
INSERT INTO YukonRolePropertyTemp VALUES(-20908,-209,'Multiple Warehouses','false','Allows for inventory to be assigned to multiple user-created warehouses instead of a single generic warehouse.');
INSERT INTO YukonRolePropertyTemp VALUES(-20909,-209,'Purchasing Access','false','Activates the purchasing section of the inventory module.'); 

/* operator work order management role properties */
INSERT INTO YukonRolePropertyTemp VALUES(-21000,-210,'Show All Work Orders','true','Controls whether to allow showing all work orders');
INSERT INTO YukonRolePropertyTemp VALUES(-21001,-210,'Create Work Order','true','Controls whether to allow creating new work orders');
INSERT INTO YukonRolePropertyTemp VALUES(-21002,-210,'Work Order Report','true','Controls whether to allow reporting on work orders');
INSERT INTO YukonRolePropertyTemp VALUES(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

INSERT INTO YukonRolePropertyTemp VALUES(-21100,-211,'CI Curtailment Label','CI Curtailment','The operator specific name for C&I Curtailment'); 

/* Scheduler Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-21200,-212,'Enable/Disable Schedule','true','Right to enable or disable a schedule'); 

/* Device Actions Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-21300,-213,'Bulk Import Operation','true','Controls access to bulk import operations'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21301,-213,'Bulk Update Operation','true','Controls access to bulk update operations'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21302,-213,'Device Group Edit','true','Controls editing of Device Groups (Add/Remove Group, update name, etc.)'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21303,-213,'Device Group Modify','true','Controls modifying contents of a Device Group (Add to/Remove from group, etc.)'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21304,-213,'Group Commander','true','Controls access to group command actions'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21305,-213,'Mass Change','true','Controls access to mass change collection actions. Includes all Mass Change actions.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21306,-213,'Locate Route','true','Controls access to locate route action'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21307,-213,'Mass Delete','true','Controls access to mass delete devices action'); 
INSERT INTO YukonRolePropertyTemp VALUES(-21308,-213,'Add/Remove Points','false','Controls access to Add/Remove Points collection action.');

/* Residential Customer Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-40001,-400,'Account General','true','Controls whether to show the general account information');
INSERT INTO YukonRolePropertyTemp VALUES(-40003,-400,'Programs Control History','true','Controls whether to show the program control history');
INSERT INTO YukonRolePropertyTemp VALUES(-40004,-400,'Programs Enrollment','true','Controls whether to enable the program enrollment feature');
INSERT INTO YukonRolePropertyTemp VALUES(-40005,-400,'Programs Opt Out','true','Controls whether to enable the program opt out/reenable feature');
INSERT INTO YukonRolePropertyTemp VALUES(-40006,-400,'Hardware Thermostat','true','Controls whether to enable the thermostat programming feature');
INSERT INTO YukonRolePropertyTemp VALUES(-40007,-400,'Questions Utility','true','Controls whether to show the contact information of the energy company');
INSERT INTO YukonRolePropertyTemp VALUES(-40008,-400,'Questions FAQ','true','Controls whether to show customer FAQs');
INSERT INTO YukonRolePropertyTemp VALUES(-40009,-400,'Change Login Username','true','Controls access for customers to change their own login username'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40010,-400,'Thermostats All','false','Controls whether to allow programming multiple thermostats at one time');
INSERT INTO YukonRolePropertyTemp VALUES(-40011,-400,'Change Login Password','true','Controls access for customers to change their own login password'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40051,-400,'Hide Opt Out Box','false','Controls whether to show the opt out box on the programs opt out page');
INSERT INTO YukonRolePropertyTemp VALUES(-40052,-400,'Automatic Configuration','false','Controls whether to automatically send out config command when changing program enrollment');
INSERT INTO YukonRolePropertyTemp VALUES(-40055,-400,'Opt Out Period','(none)','The duration, in days, for the customer Opt Out period. (Use commas to separate multiple values: Ex. 1,3,4,5)');
INSERT INTO YukonRolePropertyTemp VALUES(-40056,-400,'Opt Out Limits',' ','Contains information on Opt Out limits.');
INSERT INTO YukonRolePropertyTemp VALUES(-40100,-400,'Link FAQ','(none)','The customized FAQ link');
INSERT INTO YukonRolePropertyTemp VALUES(-40102,-400,'Link Thermostat Instructions','(none)','The customized thermostat instructions link');
INSERT INTO YukonRolePropertyTemp VALUES(-40117,-400,'Text Recommended Settings','Recommended Settings','Text of the Recommended Settings button on the thermostat schedule page');

INSERT INTO YukonRolePropertyTemp VALUES(-40133,-400,'Label Thermostat Schedule','Schedule','Text of the thermostat schedule link');
INSERT INTO YukonRolePropertyTemp VALUES(-40134,-400,'Label Thermostat Manual','Manual','Text of the thermostat manual link');
INSERT INTO YukonRolePropertyTemp VALUES(-40172,-400,'Description Enrollment','Select the check boxes and corresponding radio button of the programs you would like to be enrolled in.','Description on the program enrollment page');
INSERT INTO YukonRolePropertyTemp VALUES(-40197,-400,'Contacts Access','false','Turns residential side contact access on or off.');
INSERT INTO YukonRolePropertyTemp VALUES(-40198,-400,'Opt Out Today Only','false','Prevents residential side opt outs from being available for scheduling beyond the current day.');
INSERT INTO YukonRolePropertyTemp VALUES(-40199,-400,'Sign Out Enabled','true','Allows end-users to see a sign-out link when accessing their account pages.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40201,-400,'Opt Out Device Selection','false','Displays a second web page that allows for specific device selection when performing an opt out.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40202,-400,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-40203,-400,'Enrollment per Device','false','Displays a second web page that allows for enrollment by individual device per program.');
INSERT INTO YukonRolePropertyTemp VALUES(-40204,-400,'Thermostat Schedule 5-2','false','Allows a user to select Weekday/Weekend in addition to Weekday/Saturday/Sunday for thermostat schedule editing.');

/* Capacitor Control role properties */
INSERT INTO YukonRolePropertyTemp VALUES(-70000,-700,'Access','false','Sets accessibility to the CapControl module.');
INSERT INTO YukonRolePropertyTemp VALUES(-70002,-700,'Hide Reports','false','Sets the visibility of reports.');
INSERT INTO YukonRolePropertyTemp VALUES(-70003,-700,'Hide Graphs','false','Sets the visibility of graphs.');
INSERT INTO YukonRolePropertyTemp VALUES(-70004,-700,'Hide One-Lines','false','Sets the visibility of one-line displays.');
INSERT INTO YukonRolePropertyTemp VALUES(-70005,-700,'cap_control_interface','amfm','Optional interface to the AMFM mapping system');
INSERT INTO YukonRolePropertyTemp VALUES(-70006,-700,'cbc_creation_name','CBC <PAOName>','What text will be added onto CBC names when they are created');
INSERT INTO YukonRolePropertyTemp VALUES(-70007,-700,'pfactor_decimal_places','1','How many decimal places to show for real values for PowerFactor');
INSERT INTO YukonRolePropertyTemp VALUES(-70008,-700,'Allow OV/UV','false','Allows users to toggle OV/UV usage for capbanks, substations, subs, and feeders.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70010,-700,'Database Editing','false','Allows the user to view/modify the database set up for all CapControl items');
INSERT INTO YukonRolePropertyTemp VALUES(-70011,-700,'Show flip command', 'false', 'Show flip command for Cap Banks with 7010 type controller');
INSERT INTO YukonRolePropertyTemp VALUES(-70012,-700,'Show Cap Bank Add Info','false','Show Cap Bank Addititional Info tab');
INSERT INTO YukonRolePropertyTemp VALUES(-70013,-700,'Definition Available','Switched:Open,Switched:OpenQuestionable,Switched:OpenPending,StandAlone:Open,StandAlone:OpenQuestionable,StandAlone:OpenPending','Capbank sized in these states will be added to the available sum.');
INSERT INTO YukonRolePropertyTemp VALUES(-70014,-700,'Definition Unavailable','Switched:Close,Switched:CloseQuestionable,Switched:CloseFail,Switched:ClosePending,Switched:OpenFail,StandAlone:Close,StandAlone:CloseQuestionable,StandAlone:CloseFail,StandAlone:ClosePending,StandAlone:OpenFail,Fixed:Open,Disabled:Open','Capbank sized in these states will be added to the unavailable sum.');
INSERT INTO YukonRolePropertyTemp VALUES(-70015,-700,'Definition Tripped','Switched:Open,Switched:OpenFail,Switched:OpenPending,Switched:OpenQuestionable,StandAlone:Open,StandAlone:OpenFail,StandAlone:OpenPending,StandAlone:OpenQuestionable','Capbank sized in these states will be added to the tripped sum.');
INSERT INTO YukonRolePropertyTemp VALUES(-70016,-700,'Definition Closed','Switched:Close,Switched:CloseFail,Switched:CloseQuestionable,Switched:ClosePending,StandAlone:Close,StandAlone:CloseFail,StandAlone:CloseQuestionable,StandAlone:ClosePending,Fixed:Close,Fixed:CloseFail,Fixed:CloseQuestionable,Fixed:ClosePending,Disabled:Close,Disabled:CloseFail,Disabled:CloseQuestionable,Disabled:ClosePending','Capbank sized in these states will be added to the closed sum.');
INSERT INTO YukonRolePropertyTemp VALUES(-70017,-700,'Add Comments', 'false', 'Allows the user to Add comments to Cap Bank objects.');
INSERT INTO YukonRolePropertyTemp VALUES(-70018,-700,'Modify Comments', 'false', 'Allows the user to Modify comments on Cap Bank objects.');
INSERT INTO YukonRolePropertyTemp VALUES(-70019,-700,'System Wide Controls', 'false', 'Allow system wide controls');
INSERT INTO YukonRolePropertyTemp VALUES(-70020,-700,'Force Default Comment', 'false', 'If the user does not provide a comment, a default comment will be stored.'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70021,-700,'Allow Area Control','true','Enables or disables field and local Area controls for the given user'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70022,-700,'Allow Substation Control','true','Enables or disables field and local Substation controls for the given user'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70023,-700,'Allow SubBus Control','true','Enables or disables field and local Substation Bus controls for the given user'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70024,-700,'Allow Feeder Control','true','Enables or disables field and local Feeder controls for the given user'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70025,-700,'Allow Capbank/CBC Control','true','Enables or disables field and local Capbank/CBC controls for the given user'); 
INSERT INTO YukonRolePropertyTemp VALUES(-70026,-700,'Warn on control send.','true','If true the user will be asked if they are sure they want to send that command.');

/* Notification / IVR Role properties */
INSERT INTO YukonRolePropertyTemp VALUES(-80001,-800,'Number of Channels','1','The number of outgoing channels assigned to the specified voice application.');
INSERT INTO YukonRolePropertyTemp VALUES(-80004,-800,'IVR URL Dialer Template','http://127.0.0.1:9998/VoiceXML.start?tokenid=yukon-{MESSAGE_TYPE}&numbertodial={PHONE_NUMBER}','The URL used to initiate a call, see documentation for allowed variables'); 
INSERT INTO YukonRolePropertyTemp VALUES(-80005,-800,'IVR URL Dialer Success Matcher','success','A Java Regular Expression that will be matched against the output of the URL to determine if the call was successful'); 

/* Notification / Configuration role properties */
INSERT INTO YukonRolePropertyTemp VALUES(-80100,-801,'Template Root','Server/web/webapps/ROOT/WebConfig/custom/notif_templates/','Either a URL base where the notification templates will be stored (file: or http:) or a directory relative to YUKON_BASE.');

/* Loadcontrol Role Properties */
INSERT INTO YukonRolePropertyTemp VALUES(-90000,-900,'Direct Loadcontrol Label','Direct Control','The operator specific name for direct loadcontrol');
INSERT INTO YukonRolePropertyTemp VALUES(-90001,-900,'Individual Switch','true','Controls access to operator individual switch control');
INSERT INTO YukonRolePropertyTemp VALUES(-90002,-900,'3 Tier Direct Control','false','Allows access to the 3-tier load management web interface');
INSERT INTO YukonRolePropertyTemp VALUES(-90003,-900,'Direct Loadcontrol','true','Allows access to the Direct load management web interface');
INSERT INTO YukonRolePropertyTemp VALUES(-90004,-900,'Constraint Check','true','Allow load management program constraints to be CHECKED before starting');
INSERT INTO YukonRolePropertyTemp VALUES(-90005,-900,'Constraint Observe','true','Allow load management program constraints to be OBSERVED before starting');
INSERT INTO YukonRolePropertyTemp VALUES(-90006,-900,'Constraint Override','true','Allow load management program constraints to be OVERRIDDEN before starting');
INSERT INTO YukonRolePropertyTemp VALUES(-90007,-900,'Constraint Default','Check','The default program constraint selection prior to starting a program');
INSERT INTO YukonRolePropertyTemp VALUES(-90008,-900,'Allow Gear Change for Stop','false','Activates the ability to change gears as part of manually stopping a load program'); 

/* Capacitor Control role properties cont...*/
INSERT INTO YukonRolePropertyTemp VALUES(-100000, -1000, 'Target', 'true', 'display Target settings');
INSERT INTO YukonRolePropertyTemp VALUES(-100001, -1000, 'kVAR', 'true', 'display kVAR');
INSERT INTO YukonRolePropertyTemp VALUES(-100002, -1000, 'Estimated kVAR', 'true', 'display estimated kVAR');
INSERT INTO YukonRolePropertyTemp VALUES(-100003, -1000, 'Power Factor', 'true', 'display Power Factor');
INSERT INTO YukonRolePropertyTemp VALUES(-100004, -1000, 'Estimated Power Factor', 'true', 'display estimated Power Factor');
INSERT INTO YukonRolePropertyTemp VALUES(-100005, -1000, 'Watts', 'true', 'display Watts');
INSERT INTO YukonRolePropertyTemp VALUES(-100006, -1000, 'Volts', 'true', 'display Volts');

INSERT INTO YukonRolePropertyTemp VALUES(-100100, -1001, 'kVAR', 'true', 'display kVAR');
INSERT INTO YukonRolePropertyTemp VALUES(-100101, -1001, 'Power Factor', 'true', 'display Power Factor');
INSERT INTO YukonRolePropertyTemp VALUES(-100102, -1001, 'Watts', 'false', 'display Watts');
INSERT INTO YukonRolePropertyTemp VALUES(-100103, -1001, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
INSERT INTO YukonRolePropertyTemp VALUES(-100104, -1001, 'Volts', 'false', 'display Volts');
INSERT INTO YukonRolePropertyTemp VALUES(-100105, -1001, 'Target', 'true', 'is target stat displayed');

INSERT INTO YukonRolePropertyTemp VALUES(-100107, -1001, 'Watt/Volt', 'true', 'display Watts/Volts');
INSERT INTO YukonRolePropertyTemp VALUES(-100108, -1001, 'Three Phase', 'false', 'display 3-phase data for feeder'); 

INSERT INTO YukonRolePropertyTemp VALUES(-100201, -1002, 'Bank Size', 'true', 'display Bank Size');
INSERT INTO YukonRolePropertyTemp VALUES(-100202, -1002, 'CBC Name', 'true', 'display CBC Name');

INSERT INTO YukonRolePropertyTemp VALUES(-100011, -1000, 'Daily/Max Operation Count', 'true', 'is Daily/Max Operation stat displayed');
INSERT INTO YukonRolePropertyTemp VALUES(-100012, -1000, 'Substation Last Update Timestamp', 'true', 'is last update timestamp shown for substations');
INSERT INTO YukonRolePropertyTemp VALUES(-100013, -1000, 'Three Phase', 'false', 'display 3-phase data for sub bus');

INSERT INTO YukonRolePropertyTemp VALUES(-100106,-1001, 'Feeder Last Update Timestamp', 'true', 'is last update timestamp shown for feeders');
INSERT INTO YukonRolePropertyTemp VALUES(-100203,-1002, 'CapBank Last Update Timestamp', 'true', 'is last update timestamp shown for capbanks');
INSERT INTO YukonRolePropertyTemp VALUES(-100205,-1002, 'Capbank Fixed/Static Text', 'Fixed', 'The text to display for fixed/static capbanks');
INSERT INTO YukonRolePropertyTemp VALUES(-100206,-1002, 'Daily/Max/Total Operation Count', 'true', 'is Daily/Max/Total Operation Count displayed.');

DELETE FROM YukonUserRole
WHERE RolePropertyId NOT IN (SELECT RolePropertyId
                             FROM YukonRolePropertyTemp);
DELETE FROM YukonGroupRole
WHERE RolePropertyId NOT IN (SELECT RolePropertyId
                             FROM YukonRolePropertyTemp);
GO
DELETE FROM YukonRoleProperty
WHERE RolePropertyId NOT IN (SELECT RolePropertyId
                             FROM YukonRolePropertyTemp);

DROP TABLE YukonRolePropertyTemp;
/* End YUK-7176 */

/* Start YUK-7681 */
CREATE TABLE CommandRequestExec (
   CommandRequestExecId NUMERIC              NOT NULL,
   StartTime            datetime             NOT NULL,
   StopTime             datetime             NULL,
   RequestCount         NUMERIC              NULL,
   CommandRequestExecType VARCHAR(255)         NOT NULL,
   UserName             VARCHAR(64)          NULL,
   CommandRequestType   VARCHAR(100)         NOT NULL,
   CONSTRAINT PK_CommandRequestExec PRIMARY KEY (CommandRequestExecId)
);
GO

CREATE TABLE CommandRequestExecResult (
   CommandRequestExecResultId NUMERIC              NOT NULL,
   CommandRequestExecId NUMERIC              NULL,
   Command              VARCHAR(255)         NOT NULL,
   ErrorCode            NUMERIC              NULL,
   CompleteTime         datetime             NULL,
   DeviceId             NUMERIC              NULL,
   RouteId              NUMERIC              NULL,
   CONSTRAINT PK_CommandRequestExecResult PRIMARY KEY (CommandRequestExecResultId)
);
GO

CREATE TABLE OutageMonitor (
   OutageMonitorId      NUMERIC              NOT NULL,
   OutageMonitorName    VARCHAR(255)         NOT NULL,
   GroupName            VARCHAR(255)         NOT NULL,
   TimePeriod           NUMERIC              NOT NULL,
   NumberOfOutages      NUMERIC              NOT NULL,
   EvaluatorStatus      VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_OutageMonitor PRIMARY KEY (OutageMonitorId)
);
GO

CREATE UNIQUE INDEX INDX_OutMonName_UNQ ON OutageMonitor (
   OutageMonitorName ASC
);
GO

CREATE TABLE ScheduledGrpCommandRequest (
   CommandRequestExecId NUMERIC              NOT NULL,
   JobId                INT                  NOT NULL,
   CONSTRAINT PK_ScheduledGrpCommandRequest PRIMARY KEY (CommandRequestExecId)
);
GO

ALTER TABLE CommandRequestExecResult
   ADD CONSTRAINT FK_ComReqExecResult_ComReqExec foreign key (CommandRequestExecId)
      REFERENCES CommandRequestExec (CommandRequestExecId)
         ON DELETE CASCADE;
GO

ALTER TABLE CommandRequestExecResult
   ADD CONSTRAINT FK_ComReqExecResult_Device foreign key (DeviceId)
      REFERENCES DEVICE (DeviceId)
         ON DELETE SET NULL;
GO

ALTER TABLE CommandRequestExecResult
   ADD CONSTRAINT FK_ComReqExecResult_Route foreign key (RouteId)
      REFERENCES Route (RouteId)
         ON DELETE SET NULL;
GO

ALTER TABLE ScheduledGrpCommandRequest
   ADD CONSTRAINT FK_SchGrpComReq_ComReqExec foreign key (CommandRequestExecId)
      REFERENCES CommandRequestExec (CommandRequestExecId)
         ON DELETE CASCADE;
GO

ALTER TABLE ScheduledGrpCommandRequest
   ADD CONSTRAINT FK_SchGrpComReq_Job foreign key (JobId)
      REFERENCES Job (JobId);
GO
/* End YUK-7681 */

/* Start YUK-7762 */
INSERT INTO YukonRoleProperty VALUES(-20213,-202,'Outage Processing','true','Controls access to Outage Processing');
INSERT INTO YukonRoleProperty VALUES(-20214,-202,'Tamper Flag Processing','true','Controls access to Tamper Flag Processing');
/* End YUK-7762 */

/* Start YUK-7718 */
INSERT INTO Command VALUES(-171, 'putvalue ovuv analog 1 0', 'Disable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES(-172, 'putvalue ovuv analog 1 1', 'Enable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES(-173, 'putvalue analog ?''Enter point offset''?''Enter value''', 'Write Value', 'Twoway CBCs');

UPDATE Command SET Category = 'Oneway CBCs' WHERE CommandId IN (-33, -32 );

UPDATE DeviceTypeCommand SET CommandId = -171 WHERE DeviceCommandId = -484;
UPDATE DeviceTypeCommand SET CommandId = -172 WHERE DeviceCommandId = -485;
UPDATE DeviceTypeCommand SET CommandId = -171 WHERE DeviceCommandId = -488;
UPDATE DeviceTypeCommand SET CommandId = -172 WHERE DeviceCommandId = -489;
UPDATE DeviceTypeCommand SET CommandId = -171 WHERE DeviceCommandId = -492;
UPDATE DeviceTypeCommand SET CommandId = -172 WHERE DeviceCommandId = -493;
UPDATE DeviceTypeCommand SET CommandId = -171 WHERE DeviceCommandId = -496;
UPDATE DeviceTypeCommand SET CommandId = -172 WHERE DeviceCommandId = -497;

INSERT INTO DeviceTypeCommand VALUES (-802, -173, 'CBC 7020', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-803, -173, 'CBC 7022', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-804, -173, 'CBC 7023', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-805, -173, 'CBC 7024', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-807, -30, 'CBC DNP', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-808, -31, 'CBC DNP', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-809, -173, 'CBC DNP', 3, 'Y', -1);
/* End YUK-7718 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
