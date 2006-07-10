/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

create   index Indx_CntNotif_CntId on ContactNotification (
ContactID
)
go

create   index Indx_Cstmr_PcId on Customer (
PrimaryContactID
)
go


insert into YukonRoleProperty values(-20008,-200,'Allow Designation Codes','false','Toggles on or off the regional (usually zip) code option for service companies.');
insert into YukonRoleProperty values(-20907,-209,'Allow Designation Codes','false','Toggles on or off the ability utilize service company zip codes.');
insert into YukonRoleProperty values(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
insert into YukonRoleProperty values(-20908,-209,'Multiple Warehouses','false','Allows for inventory to be assigned to multiple user-created warehouses instead of a single generic warehouse.');
insert into YukonRoleProperty values(-20159,-201,'Switches to Meter','(none)','Allow switches to be assigned under meters for an account.');
insert into YukonRoleProperty values(-1111,-2,'z_meter_mct_base_desig','yukon','Allow meters to be used general STARS entries versus Yukon MCTs');
go

insert into YukonListEntry values (1326,1053,0,'Member',2906);
insert into YukonListEntry values (1327,1053,0,'Warehouse',2907);
insert into YukonListEntry values (1328,1053,0,'Min Serial Number',2908);
insert into YukonListEntry values (1329,1053,0,'Max Serial Number',2909);
insert into YukonListEntry values (1330,1053,0,'Postal Code',2910);
go

update YukonListEntry set EntryText = 'Appliance Type' where YukonDefinitionID = 2903;
go

update YukonSelectionList set UserUpdateAvailable = 'Y' where ListID = 1006;
go

insert into YukonListEntry values (1074,1006,0,'Ordered',1704);
insert into YukonListEntry values (1075,1006,0,'Shipped',1705);
insert into YukonListEntry values (1076,1006,0,'Received',1706);
insert into YukonListEntry values (1077,1006,0,'Issued',1707);
insert into YukonListEntry values (1078,1006,0,'Installed',1708);
insert into YukonListEntry values (1079,1006,0,'Removed',1709);
go

update yukonlistentry set yukondefinitionid = 1550 where entryid in 
	(select distinct entryid From yukonlistentry yle, yukonselectionlist ysl
	where listname = 'ServiceType'
	and yle.listid = ysl.listid
	and entrytext like 'Service%');

update yukonlistentry set yukondefinitionid = 1551 where entryid in 
	(select distinct entryid From yukonlistentry yle, yukonselectionlist ysl
	where listname = 'ServiceType'
	and yle.listid = ysl.listid
	and entrytext like 'Install%');

insert into YukonListEntry values (1113,1009,0,'Activation',1552);
insert into YukonListEntry values (1114,1009,0,'Deactivation',1553);
insert into YukonListEntry values (1115,1009,0,'Removal',1554);
insert into YukonListEntry values (1116,1009,0,'Repair',1555);
insert into YukonListEntry values (1117,1009,0,'Other',1556);
insert into YukonListEntry values (1118,1009,0,'Maintenance',1557);
go

insert into YukonListEntry values (1125,1010,0,'Assigned',1505);
insert into YukonListEntry values (1126,1010,0,'Released',1506);
insert into YukonListEntry values (1127,1010,0,'Processed',1507);
insert into YukonListEntry values (1128,1010,0,'Hold',1508);
go

insert into YukonListEntry values( 10, 1, 0, 'Call Back Phone', 2);
go

insert into yukonselectionlist values(1067, 'A', '(none)', 'System category types for Event Logging in STARS', 'EventSystemCategory', 'N');
insert into yukonselectionlist values(1068, 'A', '(none)', 'Action types for Customer Account events in STARS', 'EventAccountActions', 'N');
go

insert into yukonlistentry values (10101, 1067, 0, 'CustomerAccount', 0);
insert into yukonlistentry values (10102, 1067, 0, 'Inventory', 0);
insert into yukonlistentry values (10103, 1067, 0, 'WorkOrder', 0);
insert into yukonlistentry values (10201, 1068, 0, 'Created', 0);
insert into yukonlistentry values (10202, 1068, 0, 'Updated', 0);
go

insert into yukonlistentry values(1351, 1056, 0, 'Service Status', 3501);
insert into YukonListEntry values (1354,1056,0,'Zip Code',3504);
go

insert into YukonListEntry values (1343,1055,0,'Service Company',3403);
insert into YukonListEntry values (1344,1055,0,'Service Type',3404);
insert into YukonListEntry values (1345,1055,0,'Service Status',3405);
insert into YukonListEntry values (1346,1055,0,'Customer Type',3406);
go

insert into YukonListEntry values (20000,0,0,'Customer List Entry Base 2',0);
go

insert into YukonRoleProperty values(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.');
go

insert into YukonSelectionList values (1071,'A','(none)','Commercial Customer Types','CICustomerType','N');
insert into YukonListEntry values (1930,1071,0, 'Commercial', 0);
insert into YukonListEntry values (1931,1071,0, 'Industrial', 0);
insert into YukonListEntry values (1932,1071,0, 'Manufacturing', 0);
insert into YukonListEntry values (1933,1071,0, 'Municipal', 0);
go

alter table ciCustomerBase add CICustType numeric;
go
update CICustomerBase set CICustType = 1930;
go
alter table CICustomerBase alter column CICustType numeric not null;
go

alter table CICustomerBase add constraint FK_CUSTTYPE_ENTRYID foreign key (CICustType) 
    references YukonListEntry (EntryID);
go

insert into YukonRoleProperty values(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
insert into YukonListEntry values (10431,1053,0,'Consumption Type',2911);
insert into YukonListEntry values (1090, 1007, 0, 'Chiller', 1409);
insert into YukonListEntry values (1091, 1007, 0, 'Dual Stage', 1410);
go

insert into YukonListEntry values (1355,1056,0,'Customer Type',3505);
go

insert into billingfileformats values( -19, ' NISC-Turtle No Limit kWh ');
go

insert into YukonListEntry values (1034,1003,0,'Non Yukon Meter',1204);
go

insert into YukonRoleProperty values(-80002,-800,'Intro Text','An important message from your energy provider','The text that is read after the phone is answered, but before the pin has been entered');
go


create table SequenceNumber  (
   LastValue            numeric                          not null,
   SequenceName         VARCHAR(20)                    not null
);
go

alter table SequenceNumber
   add constraint PK_SEQUENCENUMBER primary key (SequenceName);
go

/* @error ignore-begin */
insert into YukonRoleProperty values(-20890,-201,'Address State Label','State','Labelling for the address field which is usually state in the US or province in Canada');
go
insert into YukonRoleProperty values(-20891,-201,'Address County Label','County','Labelling for the address field which is usually county in the US or postal code in Canada');
go
insert into YukonRoleProperty values(-20892,-201,'Address PostalCode Label','Zip','Labelling for the address field which is usually zip code in the US or postal code in Canada');
go
insert into yukongrouprole values (-890,-301,-201,-20890,'(none)'); 
go
insert into yukongrouprole values (-891,-301,-201,-20891,'(none)');
go
insert into yukongrouprole values (-892,-301,-201,-20892,'(none)');
go
insert into yukongrouprole values (-893,-301,-201,-20893,'(none)');
go

insert into yukongrouprole values (-2190,-303,-201,-20890,'(none)');
go
insert into yukongrouprole values (-2191,-303,-201,-20891,'(none)');
go
insert into yukongrouprole values (-2192,-303,-201,-20892,'(none)'); 
go
insert into yukongrouprole values (-2193,-303,-201,-20893,'(none)');
go
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
insert into CTIDatabase values('3.3', 'Ryan', '06-Jan-2006', 'Manual version insert done', 0 );
