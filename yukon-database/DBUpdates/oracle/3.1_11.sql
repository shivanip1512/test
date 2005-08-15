/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

alter table DynamicPAOInfo drop constraint AK_DYNPAO_OWNKYUQ;

alter table DynamicPAOInfo
   add constraint AK_DYNPAO_OWNKYUQ unique  (PAObjectID, Owner, InfoKey);

alter table DynamicPAOInfo add UpdateTime date;
update DynamicPAOInfo set UpdateTime = '01-JAN-1990';
alter table DynamicPAOInfo modify UpdateTime not null;




insert into YukonRole values (-109,'Reporting','Application','Access to reports generation.');

insert into YukonRoleProperty values(-10900,-109,'Header Label','Reporting','The header label for reporting.');
insert into YukonRoleProperty values(-10901,-109,'Download Reports Enable','true','Access to download the report files..');
insert into YukonRoleProperty values(-10902,-109,'Download Reports Default Filename','report.txt','A default filename for the downloaded report.');
insert into YukonRoleProperty values(-10903,-109,'Admin Reports Group','true','Access to administrative group reports.');
insert into YukonRoleProperty values(-10904,-109,'AMR Reports Group','true','Access to AMR group reports.');
insert into YukonRoleProperty values(-10905,-109,'Statistical Reports Group','true','Access to statistical group reports.');
insert into YukonRoleProperty values(-10906,-109,'Load Managment Reports Group','false','Acces to Load Management group reports.');
insert into YukonRoleProperty values(-10907,-109,'Cap Control Reports Group','false','Access to Cap Control group reports.');
insert into YukonRoleProperty values(-10908,-109,'Database Reports Group','true','Access to Database group reports.');
insert into YukonRoleProperty values(-10909,-109,'Stars Reports Group','true','Access to Stars group reports.');
insert into YukonRoleProperty values(-10910,-109,'Other Reports Group','true','Access to Other group reports.');
insert into YukonRoleProperty values(-10913,-109,'Admin Reports Group Label','Administrator','Label (header) for administrative group reports.');
insert into YukonRoleProperty values(-10914,-109,'AMR Reports Group Label','Metering','Label (header) for AMR group reports.');
insert into YukonRoleProperty values(-10915,-109,'Statistical Reports Group Label','Statistical','Label (header) for statistical group reports.');
insert into YukonRoleProperty values(-10916,-109,'Load Managment Reports Group Label','Load Management','Label (header) for Load Management group reports.');
insert into YukonRoleProperty values(-10917,-109,'Cap Control Reports Group Label','Cap Control','Label (header) for Cap Control group reports.');
insert into YukonRoleProperty values(-10918,-109,'Database Reports Group Label','Database','Label (header) for Database group reports.');
insert into YukonRoleProperty values(-10919,-109,'Stars Reports Group Label','Stars','Label (header) for Stars group reports.');
insert into YukonRoleProperty values(-10920,-109,'Other Reports Group Label','Other','Label (header) for Other group reports.');

update yukongrouprole set rolepropertyid = -10900, roleid = -109 where rolepropertyid = -60000 and roleid = -600;
update yukongrouprole set rolepropertyid = -10901, roleid = -109 where rolepropertyid = -60001 and roleid = -600;
update yukongrouprole set rolepropertyid = -10902, roleid = -109 where rolepropertyid = -60002 and roleid = -600;
update yukongrouprole set rolepropertyid = -10903, roleid = -109 where rolepropertyid = -60003 and roleid = -600;
update yukongrouprole set rolepropertyid = -10904, roleid = -109 where rolepropertyid = -60004 and roleid = -600;
update yukongrouprole set rolepropertyid = -10905, roleid = -109 where rolepropertyid = -60005 and roleid = -600;
update yukongrouprole set rolepropertyid = -10906, roleid = -109 where rolepropertyid = -60006 and roleid = -600;
update yukongrouprole set rolepropertyid = -10907, roleid = -109 where rolepropertyid = -60007 and roleid = -600;
update yukongrouprole set rolepropertyid = -10908, roleid = -109 where rolepropertyid = -60008 and roleid = -600;
update yukongrouprole set rolepropertyid = -10909, roleid = -109 where rolepropertyid = -60009 and roleid = -600;
update yukongrouprole set rolepropertyid = -10910, roleid = -109 where rolepropertyid = -60010 and roleid = -600;
update yukongrouprole set rolepropertyid = -10913, roleid = -109 where rolepropertyid = -60013 and roleid = -600;
update yukongrouprole set rolepropertyid = -10914, roleid = -109 where rolepropertyid = -60014 and roleid = -600;
update yukongrouprole set rolepropertyid = -10915, roleid = -109 where rolepropertyid = -60015 and roleid = -600;
update yukongrouprole set rolepropertyid = -10916, roleid = -109 where rolepropertyid = -60016 and roleid = -600;
update yukongrouprole set rolepropertyid = -10917, roleid = -109 where rolepropertyid = -60017 and roleid = -600;
update yukongrouprole set rolepropertyid = -10918, roleid = -109 where rolepropertyid = -60018 and roleid = -600;
update yukongrouprole set rolepropertyid = -10919, roleid = -109 where rolepropertyid = -60019 and roleid = -600;
update yukongrouprole set rolepropertyid = -10920, roleid = -109 where rolepropertyid = -60020 and roleid = -600;

update yukonuserrole set rolepropertyid = -10900, roleid = -109 where rolepropertyid = -60000 and roleid = -600;
update yukonuserrole set rolepropertyid = -10901, roleid = -109 where rolepropertyid = -60001 and roleid = -600;
update yukonuserrole set rolepropertyid = -10902, roleid = -109 where rolepropertyid = -60002 and roleid = -600;
update yukonuserrole set rolepropertyid = -10903, roleid = -109 where rolepropertyid = -60003 and roleid = -600;
update yukonuserrole set rolepropertyid = -10904, roleid = -109 where rolepropertyid = -60004 and roleid = -600;
update yukonuserrole set rolepropertyid = -10905, roleid = -109 where rolepropertyid = -60005 and roleid = -600;
update yukonuserrole set rolepropertyid = -10906, roleid = -109 where rolepropertyid = -60006 and roleid = -600;
update yukonuserrole set rolepropertyid = -10907, roleid = -109 where rolepropertyid = -60007 and roleid = -600;
update yukonuserrole set rolepropertyid = -10908, roleid = -109 where rolepropertyid = -60008 and roleid = -600;
update yukonuserrole set rolepropertyid = -10909, roleid = -109 where rolepropertyid = -60009 and roleid = -600;
update yukonuserrole set rolepropertyid = -10910, roleid = -109 where rolepropertyid = -60010 and roleid = -600;
update yukonuserrole set rolepropertyid = -10913, roleid = -109 where rolepropertyid = -60013 and roleid = -600;
update yukonuserrole set rolepropertyid = -10914, roleid = -109 where rolepropertyid = -60014 and roleid = -600;
update yukonuserrole set rolepropertyid = -10915, roleid = -109 where rolepropertyid = -60015 and roleid = -600;
update yukonuserrole set rolepropertyid = -10916, roleid = -109 where rolepropertyid = -60016 and roleid = -600;
update yukonuserrole set rolepropertyid = -10917, roleid = -109 where rolepropertyid = -60017 and roleid = -600;
update yukonuserrole set rolepropertyid = -10918, roleid = -109 where rolepropertyid = -60018 and roleid = -600;
update yukonuserrole set rolepropertyid = -10919, roleid = -109 where rolepropertyid = -60019 and roleid = -600;
update yukonuserrole set rolepropertyid = -10920, roleid = -109 where rolepropertyid = -60020 and roleid = -600;

delete yukonroleproperty where roleid = -600;
delete yukonrole where roleid = -600;

insert into YukonRoleProperty values(-10202, -102, 'Trending Disclaimer',' ','The disclaimer that appears with trends.');
insert into yukonroleproperty values(-10203, -102, 'Scan Now Enabled', 'false', 'Controls access to retrieve meter data on demand.');
insert into yukonroleproperty values(-10204, -102, 'Scan Now Label', 'Get Data Now', 'The label for the scan data now option.');
insert into yukonroleproperty values(-10205, -102, 'Minimum Scan Frequency', '15', 'Minimum duration (in minutes) between get data now events.');
insert into yukonroleproperty values(-10206, -102, 'Maximum Daily Scans', '2', 'Maximum number of get data now scans available daily.');
insert into yukonroleproperty values(-10207, -102, 'Reset Peaks Enabled', 'false', 'Allow access to reset the peak time period.');
insert into yukonroleproperty values(-10208, -102, 'Header Label', 'Trending', 'The header label for trends.');
insert into yukonroleproperty values(-10209, -102, 'Header Secondary Label', 'Interval Data', 'A secondary header label for grouping trends.');
insert into yukonroleproperty values(-10210, -102, 'Trend Assignment', 'false', 'Allow assignment of trends to users.');
insert into yukonroleproperty values(-10211, -102, 'Trend Create', 'false', 'Allow creation of new trends.');
insert into yukonroleproperty values(-10212, -102, 'Trend Delete', 'false', 'Allow deletion of old trends.');
insert into yukonroleproperty values(-10213, -102, 'Trend Edit', 'false', 'Allow ditting of existing trends.');
insert into yukonroleproperty values(-10214, -102, 'Options Button Enabled', 'true', 'Display the Options link to additional trending configuration properties.');
insert into yukonroleproperty values(-10215, -102, 'Export/Print Button Enabled', 'true', 'Display the Export/Print options button (drop down menu).');
insert into yukonroleproperty values(-10216, -102, 'View Button Enabled', 'true', 'Display the View options button (drop down menu).');
insert into yukonroleproperty values(-10217, -102, 'Export/Print Button Label', 'Trend', 'The label for the trend print/export button (drop down menu).');
insert into yukonroleproperty values(-10218, -102, 'View Button Label', 'View', 'The label for the trend view options button (drop down menu).');
insert into yukonroleproperty values(-10219, -102, 'Trending Usage', 'false', 'Allow access to trending time of use.');
insert into yukonroleproperty values(-10220, -102, 'Default Start Date Offset', '0', 'Offset the start date by this number.');
insert into yukonroleproperty values(-10221, -102, 'Default Time Period', '(none)', 'Default the time period.');

insert into YukonGroupRole values(-152,-100,-102,-10202,'(none)');
insert into YukonGroupRole values(-153,-100,-102,-10203,'(none)');
insert into YukonGroupRole values(-154,-100,-102,-10204,'(none)');
insert into YukonGroupRole values(-155,-100,-102,-10205,'(none)');
insert into YukonGroupRole values(-156,-100,-102,-10206,'(none)');
insert into YukonGroupRole values(-157,-100,-102,-10207,'(none)');
insert into YukonGroupRole values(-158,-100,-102,-10208,'(none)');
insert into YukonGroupRole values(-159,-100,-102,-10209,'(none)');
insert into YukonGroupRole values(-160,-100,-102,-10210,'(none)');
insert into YukonGroupRole values(-161,-100,-102,-10211,'(none)');
insert into YukonGroupRole values(-162,-100,-102,-10212,'(none)');
insert into YukonGroupRole values(-163,-100,-102,-10213,'(none)');
insert into YukonGroupRole values(-164,-100,-102,-10214,'(none)');
insert into YukonGroupRole values(-165,-100,-102,-10215,'(none)');
insert into YukonGroupRole values(-166,-100,-102,-10216,'(none)');
insert into YukonGroupRole values(-167,-100,-102,-10217,'(none)');
insert into YukonGroupRole values(-168,-100,-102,-10218,'(none)');
insert into YukonGroupRole values(-169,-100,-102,-10219,'(none)');
insert into YukonGroupRole values(-149,-100,-102,-10220,'(none)');
insert into YukonGroupRole values(-148,-100,-102,-10221,'(none)');


insert into YukonGroupRole values(-1052,-2,-102,-10202,'(none)');
insert into YukonGroupRole values(-1053,-2,-102,-10203,'(none)');
insert into YukonGroupRole values(-1054,-2,-102,-10204,'(none)');
insert into YukonGroupRole values(-1055,-2,-102,-10205,'(none)');
insert into YukonGroupRole values(-1056,-2,-102,-10206,'(none)');
insert into YukonGroupRole values(-1057,-2,-102,-10207,'(none)');
insert into YukonGroupRole values(-1058,-2,-102,-10208,'(none)');
insert into YukonGroupRole values(-1059,-2,-102,-10209,'(none)');
insert into YukonGroupRole values(-1060,-2,-102,-10210,'(none)');
insert into YukonGroupRole values(-1061,-2,-102,-10211,'(none)');
insert into YukonGroupRole values(-1062,-2,-102,-10212,'(none)');
insert into YukonGroupRole values(-1063,-2,-102,-10213,'(none)');
insert into YukonGroupRole values(-1064,-2,-102,-10214,'(none)');
insert into YukonGroupRole values(-1065,-2,-102,-10215,'(none)');
insert into YukonGroupRole values(-1066,-2,-102,-10216,'(none)');
insert into YukonGroupRole values(-1067,-2,-102,-10217,'(none)');
insert into YukonGroupRole values(-1068,-2,-102,-10218,'(none)');
insert into YukonGroupRole values(-1069,-2,-102,-10219,'(none)');
insert into YukonGroupRole values(-1049,-2,-102,-10220,'(none)');
insert into YukonGroupRole values(-1048,-2,-102,-10221,'(none)');

insert into YukonUserRole values(-152,-1,-102,-10202,'(none)');
insert into YukonUserRole values(-153,-1,-102,-10203,'(none)');
insert into YukonUserRole values(-154,-1,-102,-10204,'(none)');
insert into YukonUserRole values(-155,-1,-102,-10205,'(none)');
insert into YukonUserRole values(-156,-1,-102,-10206,'(none)');
insert into YukonUserRole values(-157,-1,-102,-10207,'(none)');
insert into YukonUserRole values(-158,-1,-102,-10208,'(none)');
insert into YukonUserRole values(-159,-1,-102,-10209,'(none)');
insert into YukonUserRole values(-160,-1,-102,-10210,'(none)');
insert into YukonUserRole values(-161,-1,-102,-10211,'(none)');
insert into YukonUserRole values(-162,-1,-102,-10212,'(none)');
insert into YukonUserRole values(-163,-1,-102,-10213,'(none)');
insert into YukonUserRole values(-164,-1,-102,-10214,'(none)');
insert into YukonUserRole values(-165,-1,-102,-10215,'(none)');
insert into YukonUserRole values(-166,-1,-102,-10216,'(none)');
insert into YukonUserRole values(-167,-1,-102,-10217,'(none)');
insert into YukonUserRole values(-168,-1,-102,-10218,'(none)');
insert into YukonUserRole values(-169,-1,-102,-10219,'(none)');
insert into YukonUserRole values(-149,-1,-102,-10220,'(none)');
insert into YukonUserRole values(-148,-1,-102,-10221,'(none)');

delete yukonuserrole where roleid = -601;
delete yukongrouprole where roleid = -601;
delete yukonroleproperty where roleid = -601;
delete yukonrole where roleid = -601;

insert into YukonRole values(-6,'Billing Configuration','Yukon','Billing. Edit this role from the Yukon SetUp page.');

insert into YukonRoleProperty values(-1500,-6,'wiz_activate','false','<description>');
insert into YukonRoleProperty values(-1501,-6,'input_file','c:\yukon\client\bin\BillingIn.txt','<description>');
insert into YukonRoleProperty values(-1503,-6,'Default File Format','CTI-CSV','The Default file formats.  See table BillingFileFormats.format for other valid values.');
insert into YukonRoleProperty values(-1504,-6,'Demand Days Previous','30','Integer value for number of days for demand readings to query back from billing end date.');
insert into YukonRoleProperty values(-1505,-6,'Energy Days Previous','7','Integer value for number of days for energy readings to query back from billing end date.');
insert into YukonRoleProperty values(-1506,-6,'Append To File','false','Append to existing file.');
insert into YukonRoleProperty values(-1507,-6,'Remove Multiplier','false','Remove the multiplier value from the reading.');
insert into YukonRoleProperty values(-1508,-6,'Coop ID - CADP Only','(none)','CADP format requires a coop id number.');

update yukongrouprole set rolepropertyid = -1500, roleid= -6 where rolepropertyid = -10600 and roleid = -106;
update yukongrouprole set rolepropertyid = -1501, roleid= -6 where rolepropertyid = -10601 and roleid = -106;
delete yukongrouprole where rolepropertyid = -10602;
update yukongrouprole set rolepropertyid = -1503, roleid= -6 where rolepropertyid = -10603 and roleid = -106;
update yukongrouprole set rolepropertyid = -1504, roleid= -6 where rolepropertyid = -10604 and roleid = -106;
update yukongrouprole set rolepropertyid = -1505, roleid= -6 where rolepropertyid = -10605 and roleid = -106;
update yukongrouprole set rolepropertyid = -1506, roleid= -6 where rolepropertyid = -10606 and roleid = -106;
update yukongrouprole set rolepropertyid = -1507, roleid= -6 where rolepropertyid = -10607 and roleid = -106;
update yukongrouprole set rolepropertyid = -1508, roleid= -6 where rolepropertyid = -10608 and roleid = -106;

update yukonuserrole set rolepropertyid = -1500, roleid= -6 where rolepropertyid = -10600 and roleid = -106;
update yukonuserrole set rolepropertyid = -1501, roleid= -6 where rolepropertyid = -10601 and roleid = -106;
delete yukonuserrole where rolepropertyid = -10602;
update yukonuserrole set rolepropertyid = -1503, roleid= -6 where rolepropertyid = -10603 and roleid = -106;
update yukonuserrole set rolepropertyid = -1504, roleid= -6 where rolepropertyid = -10604 and roleid = -106;
update yukonuserrole set rolepropertyid = -1505, roleid= -6 where rolepropertyid = -10605 and roleid = -106;
update yukonuserrole set rolepropertyid = -1506, roleid= -6 where rolepropertyid = -10606 and roleid = -106;
update yukonuserrole set rolepropertyid = -1507, roleid= -6 where rolepropertyid = -10607 and roleid = -106;
update yukonuserrole set rolepropertyid = -1508, roleid= -6 where rolepropertyid = -10608 and roleid = -106;

delete yukonroleproperty where roleid = -106;

update yukonrole set category = 'Application' where roleid = -106;
insert into YukonRoleProperty values(-10600,-106,'Header Label','Billing','The header label for billing.');
insert into YukonGroupRole values(-180,-100,-106,-10600,'(none)');
insert into YukonGroupRole values(-1390,-2,-106,-10600,'(none)');
insert into YukonUserRole values(-190,-1,-106,-10600,'(none)');

insert into YukonRole values(-7,'Multispeak','Yukon','Multispeak web services interface.');

insert into YukonRoleProperty values(-1600,-7,'OMS WebServices URL','(none)','The OMS vendor Webservices endpoint URL. (ex. http://127.0.0.1:80/soap/)');
insert into YukonRoleProperty values(-1601,-7,'OMS unique key','meterNumber','The OMS and Yukon unique key field.  Valid values [meterNumber | deviceName]');
insert into YukonRoleProperty values(-1602,-7,'OMS username','','The OMS username.');
insert into YukonRoleProperty values(-1603,-7,'OMS password','','The OMS password.');
insert into YukonRoleProperty values(-1604,-7,'OMS OA_OD Service','OA_ODSoap','The OMS OA_OD service name.');
insert into YukonRoleProperty values(-1610,-7,'CIS WebServices URL','(none)','The CIS vendor Webservices endpoint URL. (ex. http://127.0.0.1:80/soap/)');
insert into YukonRoleProperty values(-1611,-7,'CIS unique key','meterNumber','The CIS and Yukon unique key field.  Valid values [meterNumber | deviceName]');
insert into YukonRoleProperty values(-1612,-7,'CIS username','','The CIS username.');
insert into YukonRoleProperty values(-1613,-7,'CIS password','','The CIS password.');
insert into YukonRoleProperty values(-1614,-7,'CIS CB_MR Service','CB_MRSoap','The CIS CB_MR service name.');

insert into YukonGroupRole values(-270,-1,-7,-1600,'(none)');
insert into YukonGroupRole values(-271,-1,-7,-1601,'(none)');
insert into YukonGroupRole values(-272,-1,-7,-1602,'(none)');
insert into YukonGroupRole values(-273,-1,-7,-1603,'(none)');
insert into YukonGroupRole values(-274,-1,-7,-1604,'(none)');
insert into YukonGroupRole values(-280,-1,-7,-1610,'(none)');
insert into YukonGroupRole values(-281,-1,-7,-1611,'(none)');
insert into YukonGroupRole values(-282,-1,-7,-1612,'(none)');
insert into YukonGroupRole values(-283,-1,-7,-1613,'(none)');
insert into YukonGroupRole values(-284,-1,-7,-1614,'(none)');





/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '5-AUG-2005', 'Manual version insert done', 11);