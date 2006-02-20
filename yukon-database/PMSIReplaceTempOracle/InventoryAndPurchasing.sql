/*PurchasePlan*/
create table PurchasePlan 
(
  PurchaseID		number		not null,
  EnergyCompanyID	number		not null,
  PODesignation		varchar2(40)	null,
  AccountingCode	varchar2(30)	not null,
  TimePeriod		datetime	not null

);
go
alter table PurchasePlan
   add constraint PK_PURCHASEPLAN primary key (PurchaseID)
go
alter table PurchasePlan
   add constraint FK_PRCHSPL_REF_EC foreign key (EnergyCompanyID)
      references EnergyCompany (EnergyCompanyID)
go

/*DeliverySchedule*/
create table DeliverySchedule 
(
  ScheduleID		number		not null,
  PurchasePlanID	number		not null,
  ScheduleName		varchar2(60)	not null,
  ModelID		number		not null
);
go
alter table DeliverySchedule
   add constraint PK_DELIVERYSCHED primary key (ScheduleID)
go
alter table DeliverySchedule
   add constraint FK_DS_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchasePlanID)
go
alter table DeliverySchedule
   add constraint FK_DS_REF_YKNLSTNTRY foreign key (ModelID)
      references YukonListEntry (EntryID)
go

/*ScheduleTimePeriodMapping*/
create table ScheduleTimePeriod 
(
  ScheduleID		number		not null,
  TimePeriodName	varchar2(60)	not null,
  Quantity		number		not null,
  PredictedShipDate	datetime	not null
);
go
alter table ScheduleTimePeriod
   add constraint PK_SCHEDTIMEMAP primary key (ScheduleID, TimePeriodName)
go
alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID)
go

/*Shipment*/
create table Shipment (
  ShipmentID		number		not null,
  ShipTo		varchar2(60)	null, 	
  SerialNumberStart	varchar2(30)	not null,
  SerialNumberEnd	varchar2(30)	not null,
  ShipDate		datetime	null,
  SalesTotal		
  SalesTax
  OtherCharges
  ShippingCharges
  AmountPaid				not null
);
alter table Shipment
   add constraint PK_SHIPMENT primary key (ShipmentID)
go

/*ScheduleShipmentMapping*/
create table ScheduleShipmentMapping (
   ScheduleID	    	number         not null,
   ShipmentID          	number         not null
)
go
alter table ScheduleShipmentMapping
   add constraint PK_SCHEDSHIPMAP primary key  (ScheduleID, ShipmentID)
go
alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID)
go

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID)
go

/*Invoice*/
create table Invoice (
  InvoiceID		number		not null,	
  PurchasePlanID	number		not null,
  InvoiceDesignation	varchar2(60)	not null,
  DateSubmitted		datetime	null,
  Authorized		varchar2(1)	not null,
  HasPaid		varchar2(1)	not null,
  TotalQuantity		number		null
);
alter table Invoice
   add constraint PK_INVOICE primary key (InvoiceID)
go
alter table Invoice
   add constraint FK_INVC_REF_PP foreign key (PurchasePlanID)
      references PurchasePlan (PurchasePlanID)
go

/*InvoiceShipmentMapping*/
create table InvoiceShipmentMapping (
   InvoiceID	    	number         not null,
   ShipmentID          	number         not null
)
go
alter table InvoiceShipmentMapping
   add constraint PK_INVCSHIPMAP primary key  (InvoiceID, ShipmentID)
go
alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_INVC foreign key (InvoiceID)
      references Invoice (InvoiceID)
go

alter table InvoiceShipmentMapping
   add constraint FK_INVCSHPMNTMAP_SHPMNT foreign key (ShipmentID)
      references Shipment (ShipmentID)
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
      references EnergyCompanyID (EnergyCompanyID);
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
/*insert into YukonListEntry values (1330,1053,0,'Postal Code',2910);*/

/*New Device States*/
alter table InventoryBase add CurrentStateID number;
update InventoryBase set CurrentStateID = 0;
alter table InventoryBase modify CurrentStateID number not null;

update YukonSelectionList set UserUpdateAvailable = 'Y' where ListID = 1006
insert into YukonListEntry values (1074,1006,0,'Ordered',1704);
insert into YukonListEntry values (1075,1006,0,'Shipped',1705);
insert into YukonListEntry values (1076,1006,0,'Received',1706);
insert into YukonListEntry values (1077,1006,0,'Issued',1707);
insert into YukonListEntry values (1078,1006,0,'Installed',1708);
insert into YukonListEntry values (1079,1006,0,'Removed',1709);
/* Substates such as Activated, Deactivated, Scrapped, etc. can be done through customizing the list*/
/* IMPORTANT: The above added list entries are not automatically added to the database for any list other than the default list.
              Therefore, one must manually Config Energy Company to add the rest of the list entries */