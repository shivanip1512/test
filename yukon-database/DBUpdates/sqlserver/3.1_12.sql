/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* @error ignore */
alter table YukonImage alter column ImageValue image null;
go

/* @error ignore */
insert into YukonServices values( -6, 'Price_Server', 'com.cannontech.jmx.services.DynamicPriceServer', '(none)', '(none)' );
go

insert into command values(-98, 'putconfig emetcon disconnect', 'Upload Disconnect Address', 'MCT-410IL');
insert into command values(-99, 'getconfig disconnect', 'Read Disconnect Address/Status', 'MCT-410IL');

INSERT INTO DEVICETYPECOMMAND VALUES (-379, -98, 'MCT-410IL', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-380, -99, 'MCT-410IL', 15, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-381, -98, 'MCT-410CL', 14, 'Y');
INSERT INTO DEVICETYPECOMMAND VALUES (-382, -99, 'MCT-410CL', 15, 'Y');

/* @error ignore-begin */
delete from yukongrouprole where rolepropertyid < -1599 and rolepropertyid > -1615;
delete from yukonroleproperty where rolepropertyid < -1599 and rolepropertyid > -1615;
insert into YukonRoleProperty values(-1600,-7,'Vendor 01 Config','cannon, (none), (none), meterNumber, http://127.0.0.1:8080/soap/,OD_OA=OD_OASoap,OA_OD=OA_ODSoap,MR_EA=MR_EASoap,EA_MR=EA_MRSoap,MR_CB=MR_CBSoap,CB_MR=CB_MRSoap,CD_CB=CD_CBSoap,CB_CD=CB_CDSoap','Vendor 01 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1601,-7,'Vendor 02 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 02 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1602,-7,'Vendor 03 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 03 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1603,-7,'Vendor 04 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 04 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1604,-7,'Vendor 05 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 05 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1605,-7,'Vendor 06 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 06 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1606,-7,'Vendor 07 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 07 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1607,-7,'Vendor 08 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 08 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1608,-7,'Vendor 09 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 09 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');
insert into YukonRoleProperty values(-1609,-7,'Vendor 10 Config','(none), (none), (none), meterNumber, http://127.0.0.1:80/soap/','Vendor 10 Webservice setup parameters, format: <companyName>,<username>,<password>,<deviceName|meterNumber>,<webserviceURL>,<service=endpoint0>...<service=endpointX>');

insert into YukonGroupRole values(-270,-1,-7,-1600,'(none)');
insert into YukonGroupRole values(-271,-1,-7,-1601,'(none)');
insert into YukonGroupRole values(-272,-1,-7,-1602,'(none)');
insert into YukonGroupRole values(-273,-1,-7,-1603,'(none)');
insert into YukonGroupRole values(-274,-1,-7,-1604,'(none)');
insert into YukonGroupRole values(-275,-1,-7,-1605,'(none)');
insert into YukonGroupRole values(-276,-1,-7,-1606,'(none)');
insert into YukonGroupRole values(-277,-1,-7,-1607,'(none)');
insert into YukonGroupRole values(-278,-1,-7,-1608,'(none)');
insert into YukonGroupRole values(-279,-1,-7,-1609,'(none)');
/* @error ignore-end */



/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'Ryan', '12-SEP-2005', 'Manual version insert done', 12);