/*PurchasePlan*/
create table PurchasePlan 
(
  PurchaseID		number		not null,
  EnergyCompanyID	number		not null,
  PlanName		varchar2(60)	not null,
  PODesignation		varchar2(40)	not null,
  AccountingCode	varchar2(30)	not null,
  TimePeriod		date		not null

);
go
alter table PurchasePlan
   add constraint PK_PURCHASEPLAN primary key (PurchaseID);
go
alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go

/*DeliverySchedule*/
create table DeliverySchedule 
(
  ScheduleID		number		not null,
  PurchasePlanID	number		not null,
  ScheduleName		varchar2(60)	not null,
  ModelID		number		not null,
  StyleNumber		varchar2(60)	not null,
  OrderNumber		varchar2(60)	not null,
  QuotedPricePerUnit	float		not null
);
go
alter table DeliverySchedule
   add constraint PK_DELIVERYSCHED primary key (ScheduleID);
go
alter table DeliverySchedule
   add constraint FK_DS_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);
go
alter table DeliverySchedule
   add constraint FK_DS_REF_YKNLSTNTRY foreign key (ModelID)
      references YukonListEntry (EntryID);
go

/*ScheduleTimePeriod*/
create table ScheduleTimePeriod 
(
  TimePeriodID 		number		not null,
  ScheduleID		number		not null,
  TimePeriodName	varchar2(60)	not null,
  Quantity		number		not null,
  PredictedShipDate	date	not null
);
go
alter table ScheduleTimePeriod
   add constraint PK_SCHEDTIMEMAP primary key (TimePeriodID);
go
alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);
go

/*Shipment*/
create table Shipment (
  ShipmentID		number		not null,
  ShipmentNumber	varchar2(60)	not null,
  WarehouseID		number		not null, 	
  SerialNumberStart	varchar2(30)	not null,
  SerialNumberEnd	varchar2(30)	not null,
  ShipDate		date		not null,
  ActualPricePerUnit	float		not null,
  SalesTotal		float		not null,
  SalesTax		float		not null,		
  OtherCharges		float		not null,
  ShippingCharges	float		not null,
  AmountPaid		float		not null,
  OrderedDate		date		not null,
  ReceivedDate		date		not null
);
alter table Shipment
   add constraint PK_SHIPMENT primary key (ShipmentID);
go

alter table Shipment
   add constraint FK_SHPMNT_WRHSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);

/*ScheduleShipmentMapping*/
create table ScheduleShipmentMapping (
   ScheduleID	    	number         not null,
   ShipmentID          	number         not null
);
go
alter table ScheduleShipmentMapping
   add constraint PK_SCHEDSHIPMAP primary key  (ScheduleID, ShipmentID);
go
alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);
go

/*Invoice*/
create table Invoice (
  InvoiceID			number				not null,	
  PurchasePlanID	number				not null,
  InvoiceDesignation	varchar2(60)	not null,
  DateSubmitted		date				not null,
  Authorized		varchar2(1)			not null,
  AuthorizedBy		varchar2(30)    	not null,
  HasPaid			varchar2(1)			not null,
  DatePaid			date				not null,
  TotalQuantity		number				not null
);
alter table Invoice
   add constraint PK_INVOICE primary key (InvoiceID);
go
alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);
go

/*InvoiceShipmentMapping*/
create table InvoiceShipmentMapping (
   InvoiceID	    	number         not null,
   ShipmentID          	number         not null
);
go
alter table InvoiceShipmentMapping
   add constraint PK_INVCSHIPMAP primary key  (InvoiceID, ShipmentID);
go
alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID);
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);
go

/*Zip Code*/
create table ServiceCompanyDesignationCode (
   DesignationCodeID		number		not null,
   DesignationCodeValue		varchar2(60)	not null,
   ServiceCompanyID		number		not null
);
go
alter table ServiceCompanyDesignationCode
   add constraint PK_SRVCODSGNTNCODES primary key (DesignationCodeID);
go
alter table ServiceCompanyDesignationCode
   add constraint FK_SRVCODSGNTNCODES_SRVCO foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID);
go
insert into YukonRoleProperty values(-20008,-200,'Allow Designation Codes','false','Toggles on or off the regional (usually zip) code option for service companies.');
insert into YukonRoleProperty values(-20907,-209,'Allow Designation Codes','false','Toggles on or off the ability utilize service company zip codes.');

/*Warehouse*/
create table Warehouse (
   WarehouseID			number		not null,
   WarehouseName		varchar2(60)	not null,
   AddressID			number		not null,
   Notes			varchar2(300)	null,
   EnergyCompanyID		number		not null
);
go
alter table Warehouse
   add constraint PK_WAREHOUSE primary key (WarehouseID);
go
alter table Warehouse
   add constraint FK_WAREHOUSE_ADDRESS foreign key (AddressID)
      references Address (AddressID);
go
alter table Warehouse
   add constraint FK_WAREHOUSE_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go
insert into Warehouse values (0, '(none)', 0, '(none)', -1);


create table InventoryToWarehouseMapping (
   WarehouseID			number		not null,
   InventoryID			number		not null
);
go
alter table InventoryToWarehouseMapping
   add constraint PK_INVTOWAREMAP primary key (WarehouseID, InventoryID);
go
alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_INVENBASE foreign key (InventoryID)
      references InventoryBase (InventoryID);
go
alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_WAREHOUSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);  
go

insert into YukonRoleProperty values(-20009,-200,'Multiple Warehouses','false','Allows for multiple user-created warehouses instead of a single generic warehouse.');
insert into YukonRoleProperty values(-20908,-209,'Multiple Warehouses','false','Allows for inventory to be assigned to multiple user-created warehouses instead of a single generic warehouse.');


/*This is for toggling on and off switch to meter assignment*/
insert into YukonRoleProperty values(-20159,-201,'Switches to Meter','(none)','Allow switches to be assigned under meters for an account.');
/*This allows whether a user can create a dumb meter vs. a Yukon MCT*/
insert into YukonRoleProperty values(-1111,-2,'z_meter_mct_base_desig','yukon','Allow meters to be used as general switch holders versus Yukon MCTs');

create table LMHardwareToMeterMapping
 (
   LMHardwareInventoryID	number		not null,
   MeterInventoryID		number		not null
);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID);  
go

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID);  
go

/*New filter types*/
insert into YukonListEntry values (1326,1053,0,'Member',2906);
insert into YukonListEntry values (1327,1053,0,'Warehouse',2907);
insert into YukonListEntry values (1328,1053,0,'Min Serial Number',2908);
insert into YukonListEntry values (1329,1053,0,'Max Serial Number',2909);
insert into YukonListEntry values (1330,1053,0,'Postal Code',2910);
/*Changes Location filter type to Appliance Type*/
update YukonListEntry set EntryText = 'Appliance Type' where YukonDefinitionID = 2903;

/*New Device States*/
alter table InventoryBase add CurrentStateID number;
update InventoryBase set CurrentStateID = 0;
alter table InventoryBase modify CurrentStateID number not null;

update YukonSelectionList set UserUpdateAvailable = 'Y' where ListID = 1006;
insert into YukonListEntry values (1074,1006,0,'Ordered',1704);
insert into YukonListEntry values (1075,1006,0,'Shipped',1705);
insert into YukonListEntry values (1076,1006,0,'Received',1706);
insert into YukonListEntry values (1077,1006,0,'Issued',1707);
insert into YukonListEntry values (1078,1006,0,'Installed',1708);
insert into YukonListEntry values (1079,1006,0,'Removed',1709);
/* Substates such as Activated, Deactivated, Scrapped, etc. can be done through customizing the list*/
/* IMPORTANT: The above added list entries are not automatically added to the database for any list other than the default list.
              Therefore, one must manually Config Energy Company to add the rest of the list entries */
              
/*Hopefully we get all updated because Hopefully they didn't change the word Service (Call)...hopefully.*/
update yukonlistentry set yukondefinitionid = 1550 where entryid in 
	(select distinct entryid From yukonlistentry yle, yukonselectionlist ysl
	where listname = 'ServiceType'
	and yle.listid = ysl.listid
	and entrytext like 'Service%');

/*Hopefully we get all updated because Hopefully they didn't change the word install...hopefully.*/
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

insert into YukonListEntry values (1125,1010,0,'Assigned',1505);
insert into YukonListEntry values (1126,1010,0,'Released',1506);
insert into YukonListEntry values (1127,1010,0,'Processed',1507);
insert into YukonListEntry values (1128,1010,0,'Hold',1508);

/* IMPORTANT: The above added list entries are not automatically added to the database for any list other than the default list.
              Therefore, one must manually Config Energy Company to add the rest of the list entries */

insert into YukonListEntry values( 10, 1, 0, 'Call Back Phone', 2);

insert into yukonselectionlist values(1067, 'A', '(none)', 'System category types for Event Logging in STARS', 'EventSystemCategory', 'N');
insert into yukonselectionlist values(1068, 'A', '(none)', 'Action types for Customer Account events in STARS', 'EventAccountActions', 'N');
/*We think these won't be necessary...we can use the service status and device status lists
insert into yukonselectionlist values(1069, 'A', '(none)', 'Action types for Inventory events in STARS', 'EventInventoryActions', 'N');
insert into yukonselectionlist values(1070, 'A', '(none)', 'Action types for Work Order events in STARS', 'EventWorkOrderActions', 'N');
*/

insert into yukonlistentry values (10101, 1067, 0, 'CustomerAccount', 0);
insert into yukonlistentry values (10102, 1067, 0, 'Inventory', 0);
insert into yukonlistentry values (10103, 1067, 0, 'WorkOrder', 0);
insert into yukonlistentry values (10201, 1068, 0, 'Created', 0);
insert into yukonlistentry values (10202, 1068, 0, 'Updated', 0);

/*
insert into yukonlistentry values (10301, 1069, 0, 'Ordered', 0);
insert into yukonlistentry values (10302, 1069, 0, 'Shipped', 0);
insert into yukonlistentry values (10303, 1069, 0, 'Received', 0);
insert into yukonlistentry values (10304, 1069, 0, 'Issued', 0);
insert into yukonlistentry values (10305, 1069, 0, 'Installed', 0);
insert into yukonlistentry values (10306, 1069, 0, 'Installed Activated', 0);
insert into yukonlistentry values (10307, 1069, 0, 'Installed Deactivated', 0);
insert into yukonlistentry values (10308, 1069, 0, 'Removed', 0);
insert into yukonlistentry values (10309, 1069, 0, 'Retired', 0);
insert into yukonlistentry values (10310, 1069, 0, 'Scrapped', 0);
insert into yukonlistentry values (10311, 1069, 0, 'Returned', 0);
insert into yukonlistentry values (10312, 1069, 0, 'Lost', 0);
*/

insert into yukonlistentry values(1351, 1056, 0, 'Service Status', 3501);
insert into YukonListEntry values (1354,1056,0,'Zip Code',3504);

insert into YukonListEntry values (1343,1055,0,'Service Company',3403);
insert into YukonListEntry values (1344,1055,0,'Service Type',3404);
insert into YukonListEntry values (1345,1055,0,'Service Status',3405);
insert into YukonListEntry values (1346,1055,0,'Customer Type',3406);

insert into YukonListEntry values (20000,0,0,'Customer List Entry Base 2',0);

insert into YukonRoleProperty values(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.');

alter table WorkOrderBase modify Description varchar(500);

/* Commerical Customer Types */
insert into YukonSelectionList values (1071,'A','(none)','Commercial Customer Types','CICustomerType','Y');
insert into YukonListEntry values (1930,1071,0, 'Commercial', 0);
insert into YukonListEntry values (1931,1071,0, 'Industrial', 0);
insert into YukonListEntry values (1932,1071,0, 'Manufacturing', 0);
insert into YukonListEntry values (1933,1071,0, 'Municipal', 0);


alter table ciCustomerBase add CICustType NUMBER;
update CICustomerBase set CICustType = 0;
alter table CICustomerBase modify CICustType number not null;
go
alter table CICustomerBase add constraint FK_CUSTTYPE_ENTRYID foreign key (CICustType) 
    references YukonListEntry (EntryID);
go