create index Indx_CstAcc_CstId on CustomerAccount (
   CustomerID ASC
);

alter table AccountSite add CustomerStatus VARCHAR2(1);
update AccountSite set CustomerStatus=' ';
alter table AccountSite modify CustomerStatus VARCHAR2(1) not null;

alter table AccountSite add CustAtHome VARCHAR2(1);
update accountsite set CustAtHome='N';
alter table AccountSite modify CustAtHome VARCHAR2(1) not null;

alter table WorkOrderBase add AdditionalOrderNUMBER VARCHAR2(24);

alter table InventoryBase add CurrentStateID NUMBER;
update InventoryBase set CurrentStateID = 0;
alter table InventoryBase modify CurrentStateID NUMBER not null;

alter table WorkOrderBase modify Description VARCHAR2(500);

create table MeterHardwareBase 
(
   InventoryID			NUMBER			not null,
   MeterNUMBER			VARCHAR2(30)		not null,
   MeterTypeID			NUMBER			not null
);

alter table MeterHardwareBase
   add constraint PK_METERHARDWAREBASE primary key  (InventoryID);

alter table MeterHardwareBase
   add constraint FK_METERHARD_YUKONLSTNTRY foreign key (MeterTypeID)
      references YukonListEntry (EntryID);

alter table MeterHardwareBase
   add constraint FK_METERHARD_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);

create table ApplianceDualStageAirCond  (
   ApplianceID          	NUMBER            	not null,
   StageOneTonnageID      	NUMBER,
   StageTwoTonnageID      	NUMBER,
   TypeID			NUMBER
);

alter table ApplianceDualStageAirCond
   add constraint PK_APPLIANCEDUALSTAGEAIRCOND primary key (ApplianceID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STNENTRY foreign key (StageOneTonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_STGTWONTRY foreign key (StageTwoTonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceDualStageAirCond
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);


create table ApplianceChiller  (
   ApplianceID          	NUMBER            	not null,
   TonnageID      		NUMBER,
   TypeID			NUMBER
);

alter table ApplianceChiller
   add constraint PK_APPLIANCECHILLER primary key (ApplianceID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TYPENTRY foreign key (TypeID)
      references YukonListEntry (EntryID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_TONNTRY foreign key (TonnageID)
      references YukonListEntry (EntryID);

alter table ApplianceChiller
   add constraint FK_APPLCHILL_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

create table EventBase  (
   EventID              	NUMBER			not null,
   UserID					NUMBER			not null,
   SystemCategoryID     	NUMBER          not null,
   ActionID             	NUMBER          not null,
   EventTimestamp       	DATE			not null
);

alter table EventBase
   add constraint PK_EVENTBASE primary key (EventID);

alter table EventBase
   add constraint FK_EVNTBSE_SYSCATNTRY foreign key (SystemCategoryID)
      references YukonListEntry (EntryID);

alter table EventBase
   add constraint FK_EVNTBSE_YUKUSR foreign key (UserID)
      references YukonUser (UserID);

alter table EventBase
   add constraint FK_EVNTBSE_ACTNTRY foreign key (ActionID)
      references YukonListEntry (EntryID);

create table EventInventory  (
   EventID              NUMBER                          not null,
   InventoryID          NUMBER                          not null
);

alter table EventInventory
   add constraint PK_EVENTINVENTORY primary key (EventID);

alter table EventInventory
   add constraint FK_EVENTINV_EVNTBSE foreign key (EventID)
      references EventBase (EventID);

alter table EventInventory
   add constraint FK_EVENTINV_INVENBSE foreign key (InventoryID)
      references InventoryBase (InventoryID);

create table EventWorkOrder  (
   EventID              NUMBER                          not null,
   OrderID          	NUMBER                          not null
);

alter table EventWorkOrder
   add constraint PK_EVENTWRKORDR primary key (EventID);

alter table EventWorkOrder
   add constraint FK_EVENTWO_EVNTBSE foreign key (EventID)
      references EventBase (EventID);

alter table EventWorkOrder
   add constraint FK_EVENTWO_WOBASE foreign key (OrderID)
      references WorkOrderBase (OrderID);

create table EventAccount  (
   EventID              NUMBER                          not null,
   AccountID          	NUMBER                          not null
);

alter table EventAccount
   add constraint PK_EVENTACCOUNT primary key (EventID);

alter table EventAccount
   add constraint FK_EVENTACCT_EVNTBSE foreign key (EventID)
      references EventBase (EventID);

alter table EventAccount
   add constraint FK_EVENTACCT_CUSTACCT foreign key (AccountID)
      references CustomerAccount (AccountID);

create table PurchasePlan 
(
  PurchaseID		NUMBER		not null,
  EnergyCompanyID	NUMBER		not null,
  PlanName		VARCHAR2(60)	not null,
  PODesignation		VARCHAR2(40)	not null,
  AccountingCode	VARCHAR2(30)	not null,
  TimePeriod		date		not null

);

alter table PurchasePlan
   add constraint PK_PURCHASEPLAN primary key (PurchaseID);

alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

create table DeliverySchedule 
(
  ScheduleID		NUMBER		not null,
  PurchasePlanID	NUMBER		not null,
  ScheduleName		VARCHAR2(60)	not null,
  ModelID		NUMBER		not null,
  StyleNUMBER		VARCHAR2(60)	not null,
  OrderNUMBER		VARCHAR2(60)	not null,
  QuotedPricePerUnit	float		not null
);

alter table DeliverySchedule
   add constraint PK_DELIVERYSCHED primary key (ScheduleID);

alter table DeliverySchedule
   add constraint FK_DS_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);

alter table DeliverySchedule
   add constraint FK_DS_REF_YKNLSTNTRY foreign key (ModelID)
      references YukonListEntry (EntryID);

create table ScheduleTimePeriod 
(
  TimePeriodID 		NUMBER		not null,
  ScheduleID		NUMBER		not null,
  TimePeriodName	VARCHAR2(60)	not null,
  Quantity		NUMBER		not null,
  PredictedShipDate	date	not null
);

alter table ScheduleTimePeriod
   add constraint PK_SCHEDTIMEPERIOD primary key (TimePeriodID);

alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);

create table Warehouse (
   WarehouseID			NUMBER		not null,
   WarehouseName		VARCHAR2(60)	not null,
   AddressID			NUMBER		not null,
   Notes			VARCHAR2(300)	null,
   EnergyCompanyID		NUMBER		not null
);

alter table Warehouse
   add constraint PK_WAREHOUSE primary key (WarehouseID);

alter table Warehouse
   add constraint FK_WAREHOUSE_ADDRESS foreign key (AddressID)
      references Address (AddressID);

alter table Warehouse
   add constraint FK_WAREHOUSE_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID);

insert into Warehouse values (0, '(none)', 0, '(none)', -1);

create table Shipment (
  ShipmentID		NUMBER		not null,
  ShipmentNUMBER	VARCHAR2(60)	not null,
  WarehouseID		NUMBER		not null, 	
  SerialNUMBERStart	VARCHAR2(30)	not null,
  SerialNUMBEREnd	VARCHAR2(30)	not null,
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

alter table Shipment
   add constraint FK_SHPMNT_WRHSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);

create table ScheduleShipmentMapping (
   ScheduleID	    	NUMBER         not null,
   ShipmentID          	NUMBER         not null
);

alter table ScheduleShipmentMapping
   add constraint PK_SCHEDULESHIPMENTMAPPING primary key  (ScheduleID, ShipmentID);

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);

create table Invoice (
  InvoiceID			NUMBER				not null,	
  PurchasePlanID	NUMBER				not null,
  InvoiceDesignation	VARCHAR2(60)	not null,
  DateSubmitted		date				not null,
  Authorized		VARCHAR2(1)			not null,
  AuthorizedBy		VARCHAR2(30)    	not null,
  HasPaid			VARCHAR2(1)			not null,
  DatePaid			date				not null,
  TotalQuantity		NUMBER				not null
);
alter table Invoice
   add constraint PK_INVOICE primary key (InvoiceID);

alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchaseID);

create table InvoiceShipmentMapping (
   InvoiceID	    	NUMBER         not null,
   ShipmentID          	NUMBER         not null
);

alter table InvoiceShipmentMapping
   add constraint PK_INVOICESHIPMENTMAPPING primary key  (InvoiceID, ShipmentID);

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID);

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID);

create table ServiceCompanyDesignationCode (
   DesignationCodeID		NUMBER		not null,
   DesignationCodeValue		VARCHAR2(60)	not null,
   ServiceCompanyID		NUMBER		not null
);

alter table ServiceCompanyDesignationCode
   add constraint PK_SERVICECOMPANYDESIGNATIONCO primary key (DesignationCodeID);

alter table ServiceCompanyDesignationCode
   add constraint FK_SRVCODSGNTNCODES_SRVCO foreign key (ServiceCompanyID)
      references ServiceCompany (CompanyID);



create table InventoryToWarehouseMapping (
   WarehouseID			NUMBER		not null,
   InventoryID			NUMBER		not null
);

alter table InventoryToWarehouseMapping
   add constraint PK_INVENTORYTOWAREHOUSEMAPPING primary key (WarehouseID, InventoryID);

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_INVENBASE foreign key (InventoryID)
      references InventoryBase (InventoryID);

alter table InventoryToWarehouseMapping
   add constraint FK_INVTOWAREMAP_WAREHOUSE foreign key (WarehouseID)
      references Warehouse (WarehouseID);  


create table LMHardwareToMeterMapping
 (
   LMHardwareInventoryID	NUMBER		not null,
   MeterInventoryID		NUMBER		not null
);

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_LMHARDBASE foreign key (LMHardwareInventoryID)
      references LMHardwareBase (InventoryID);  

alter table LMHardwareToMeterMapping
   add constraint FK_LMMETMAP_METERHARDBASE foreign key (MeterInventoryID)
      references MeterHardwareBase (InventoryID);  

create table EventWorkOrderBaseTemp (
   EventID               NUMBER   not null,
   UserID   NUMBER   not null,
   SystemCategoryID      NUMBER                  not null,
   ActionID              NUMBER                  not null,
   EventTimestamp        DATE   not null,
   OrderID   NUMBER   not null
);

/*Insert all event workorder data into a temp table*/
INSERT INTO eventworkorderbaseTemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + ((orderid*3) -2), -2, 10103, 1121, datereported, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');

INSERT INTO eventworkorderbasetemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + ((orderid*3) -1), -2, 10103, 1122, datescheduled, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');

INSERT INTO eventworkorderbasetemp
   (eventid, userid, systemcategoryid, actionid, eventtimestamp, orderid)
SELECT (select max(eventid)from eventbase) + (orderid*3), -2, 10103, 1123, datecompleted, orderid
FROM workorderbase
WHERE (datereported > '01-JAN-1970');

/* Move temp table data into eventbase and eventworkorder*/
INSERT INTO eventbase
   (eventid, userid, systemcategoryid, actionid, eventtimestamp)
SELECT eventid, userid, systemcategoryid, actionid, eventtimestamp
FROM eventworkorderbasetemp;

INSERT INTO eventworkorder
   (eventid, orderid)
SELECT eventid, orderid
FROM eventworkorderbasetemp;

drop table eventworkorderbasetemp;

Insert into ECToGenericMapping values(-1, 1071, 'YukonSelectionList');
