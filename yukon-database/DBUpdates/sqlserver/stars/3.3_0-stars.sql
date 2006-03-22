create index Indx_CstAcc_CstId on CustomerAccount (
   CustomerID ASC
)
go

alter table AccountSite add CustomerStatus varchar(1);
go
update AccountSite set CustomerStatus=' ';
go
alter table AccountSite alter column CustomerStatus varchar(1) not null;
go

alter table AccountSite add CustAtHome varchar(1);
go
update accountsite set CustAtHome='N';
go
alter table AccountSite alter column CustAtHome varchar(1) not null;
go

alter table WorkOrderBase add AdditionalOrderNumber varchar(24);
go


alter table InventoryBase add CurrentStateID numeric;
go
update InventoryBase set CurrentStateID = 0;
go
alter table InventoryBase alter column CurrentStateID numeric not null;
go

alter table WorkOrderBase alter column Description varchar(500);
go
create table MeterHardwareBase 
(
   InventoryID			numeric			not null,
   MeterNumber			varchar(30)		not null,
   MeterTypeID			numeric 		not null
);
go

alter table MeterHardwareBase
   add constraint PK_METERHARDWAREBASE primary key  (InventoryID);
go

alter table MeterHardwareBase
   add constraint FK_METERHARD_YUKONLSTNTRY foreign key (MeterTypeID)
      references YukonListEntry (EntryID);

alter table MeterHardwareBase
   add constraint FK_METERHARD_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);
go

create table ApplianceDualStageAirCond  (
   ApplianceID          	numeric            	not null,
   StageOneTonnageID      	numeric,
   StageTwoTonnageID      	numeric,
   TypeID			numeric
);
go

alter table ApplianceDualStageAirCond
   add constraint PK_APPLIANCEDUALSTAGEAIRCOND primary key (ApplianceID);
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STNENTRY foreign key (StageOneTonnageID)
      references YukonListEntry (EntryID);
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STGTWONTRY foreign key (StageTwoTonnageID)
      references YukonListEntry (EntryID);
go

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);
go

create table ApplianceChiller  (
   ApplianceID          	numeric            	not null,
   TonnageID      		numeric,
   TypeID			numeric
);
go

alter table ApplianceChiller
   add constraint PK_APPLIANCECHILLER primary key (ApplianceID);
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TONNTRY foreign key (TonnageID)
      references YukonListEntry (EntryID);
go

alter table ApplianceChiller
   add constraint FK_APPLCHILL_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);
go

create table EventBase  (
   EventID              	numeric			not null,
   UserID					numeric			not null,
   SystemCategoryID     	numeric          not null,
   ActionID             	numeric          not null,
   EventTimestamp       	datetime			not null
);
go

alter table EventBase
   add constraint PK_EVENTBASE primary key (EventID);
go

alter table EventBase
   add constraint FK_EVNTBSE_SYSCATNTRY foreign key (SystemCategoryID)
      references YukonListEntry (EntryID);
go

alter table EventBase
   add constraint FK_EVNTBSE_YUKUSR foreign key (UserID)
      references YukonUser (UserID);
go

alter table EventBase
   add constraint FK_EVNTBSE_ACTNTRY foreign key (ActionID)
      references YukonListEntry (EntryID);
go

create table EventInventory  (
   EventID              numeric                          not null,
   InventoryID          numeric                          not null
);
go

alter table EventInventory
   add constraint PK_EVENTINVENTORY primary key (EventID);
go

alter table EventInventory
   add constraint FK_EVENTINV_EVNTBSE foreign key (EventID)
      references EventBase (EventID);
go

alter table EventInventory
   add constraint FK_EVENTINV_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);
go

create table EventWorkOrder  (
   EventID              numeric                          not null,
   OrderID          	numeric                          not null
);
go

alter table EventWorkOrder
   add constraint PK_EVENTWRKORDR primary key (EventID);
go

alter table EventWorkOrder
   add constraint FK_EVENTWO_EVNTBSE foreign key (EventID)
      references EventBase (EventID);
go

alter table EventWorkOrder
   add constraint FK_EVENTWO_WOBASE foreign key (OrderID)
      references WorkOrderBase (OrderID);
go

create table EventAccount  (
   EventID              numeric                          not null,
   AccountID          	numeric                          not null
);
go

alter table EventAccount
   add constraint PK_EVENTACCOUNT primary key (EventID);
go

alter table EventAccount
   add constraint FK_EVENTACCT_EVNTBSE foreign key (EventID)
      references EventBase (EventID);
go

alter table EventAccount
   add constraint FK_EVENTACCT_CUSTACCT foreign key (AccountID)
      references CustomerAccount (AccountID);
go

create table PurchasePlan 
(
  PurchaseID		numeric		not null,
  EnergyCompanyID	numeric		not null,
  PlanName		varchar(60)	not null,
  PODesignation		varchar(40)	not null,
  AccountingCode	varchar(30)	not null,
  TimePeriod		datetime        not null

);
go

alter table PurchasePlan
   add constraint PK_PURCHASEPLAN primary key (PurchaseID);
go

alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);
go

create table DeliverySchedule 
(
  ScheduleID		numeric		not null,
  PurchasePlanID	numeric		not null,
  ScheduleName		varchar(60)	not null,
  ModelID		numeric 	not null,
  StyleNumber		varchar(60)	not null,
  OrderNumber		varchar(60)	not null,
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

create table ScheduleTimePeriod 
(
  TimePeriodID 		numeric		not null,
  ScheduleID		numeric 	not null,
  TimePeriodName	varchar(60)	not null,
  Quantity		numeric	        not null,
  PredictedShipDate	datetime	not null
);
go

alter table ScheduleTimePeriod
   add constraint PK_SCHEDTIMEPERIOD primary key (TimePeriodID);
go

alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);
go

create table Warehouse (
   WarehouseID			numeric		not null,
   WarehouseName		varchar(60)	not null,
   AddressID			numeric 	not null,
   Notes			varchar(300)	null,
   EnergyCompanyID		numeric		not null
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
go

create table Shipment (
  ShipmentID		numeric		not null,
  ShipmentNumber	varchar(60)	not null,
  WarehouseID		numeric		not null, 	
  SerialNumberStart	varchar(30)	not null,
  SerialNumberEnd	varchar(30)	not null,
  ShipDate		datetime	not null,
  ActualPricePerUnit	float		not null,
  SalesTotal		float		not null,
  SalesTax		float		not null,		
  OtherCharges		float		not null,
  ShippingCharges	float		not null,
  AmountPaid		float		not null,
  OrderedDate		datetime	not null,
  ReceivedDate		datetime	not null
);
go

alter table Shipment
   add constraint PK_SHIPMENT primary key (ShipmentID);
go

alter table Shipment
   add constraint FK_SHPMNT_WRHSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);
go

create table ScheduleShipmentMapping (
   ScheduleID	    	numeric         not null,
   ShipmentID          	numeric         not null
);
go

alter table ScheduleShipmentMapping
   add constraint PK_SCHEDULESHIPMENTMAPPING primary key  (ScheduleID, ShipmentID);
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);
go

create table Invoice (
  InvoiceID			numeric				not null,	
  PurchasePlanID	numeric				not null,
  InvoiceDesignation	varchar(60)	not null,
  DateSubmitted		datetime				not null,
  Authorized		varchar(1)			not null,
  AuthorizedBy		varchar(30)    	not null,
  HasPaid			varchar(1)			not null,
  DatePaid			datetime				not null,
  TotalQuantity		numeric				not null
);
go

alter table Invoice
   add constraint PK_INVOICE primary key (InvoiceID);
go

alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);
go

create table InvoiceShipmentMapping (
   InvoiceID	    	numeric         not null,
   ShipmentID          	numeric         not null
);
go

alter table InvoiceShipmentMapping
   add constraint PK_INVOICESHIPMENTMAPPING primary key  (InvoiceID, ShipmentID);
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID);
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);
go

create table ServiceCompanyDesignationCode (
   DesignationCodeID		numeric		not null,
   DesignationCodeValue		varchar(60)	not null,
   ServiceCompanyID		numeric		not null
);
go

alter table ServiceCompanyDesignationCode
   add constraint PK_SERVICECOMPANYDESIGNATIONCO primary key (DesignationCodeID);
go

alter table ServiceCompanyDesignationCode
   add constraint FK_SRVCODSGNTNCODES_SRVCO foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID);
go




create table InventoryToWarehouseMapping (
   WarehouseID			numeric		not null,
   InventoryID			numeric		not null
);
go

alter table InventoryToWarehouseMapping
   add constraint PK_INVENTORYTOWAREHOUSEMAPPING primary key (WarehouseID, InventoryID);
go

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_INVENBASE foreign key (InventoryID)
      references InventoryBase (InventoryID);
go

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_WAREHOUSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);  
go

create table LMHardwareToMeterMapping
 (
   LMHardwareInventoryID	numeric		not null,
   MeterInventoryID		numeric		not null
);
go

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID);  
go

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID);  
go

create table EventWorkOrderBaseTemp (
   EventID               NUmeric   not null,
   UserID   NUMeric   not null,
   SystemCategoryID      NUMeric                  not null,
   ActionID              NUMeric                  not null,
   EventTimestamp        DATEtime   not null,
   OrderID   Numeric   not null
);

insert into eventbase values (-1, -9999, 0, 0, '01-JAN-1970');

/*Insert all event workorder data into a temp table*/
INSERT INTO eventworkorderbaseTemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + ((orderid*3) -2), -2, 10103, 1121, datereported, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');
go
INSERT INTO eventworkorderbasetemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + ((orderid*3) -1), -2, 10103, 1122, datescheduled, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');
go
INSERT INTO eventworkorderbasetemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + (orderid*3), -2, 10103, 1123, datecompleted, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');
go
/* Move temp table data into eventbase and eventworkorder*/
INSERT INTO eventbase
   (eventid, userid, systemcategoryid, actionid, eventtimestamp)
SELECT eventid, userid, systemcategoryid, actionid, eventtimestamp
FROM eventworkorderbasetemp;
go
INSERT INTO eventworkorder
   (eventid, orderid)
SELECT eventid, orderid
FROM eventworkorderbasetemp;
go

drop table eventworkorderbasetemp;
go

insert into ECToGenericMapping values (-1, 1067, 'YukonSelectionList');
insert into ECToGenericMapping values (-1, 1068, 'YukonSelectionList');
Insert into ECToGenericMapping values(-1, 1071, 'YukonSelectionList');
go
