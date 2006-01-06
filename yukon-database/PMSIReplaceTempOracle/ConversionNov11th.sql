/*P205: C-CUST-STATUS*/
alter table AccountSite add CustomerStatus VARCHAR2(1);

/*P205: C-CUST-AT-HOME*/
alter table AccountSite add CustAtHome VARCHAR2(1);

/*
P205: C-CUST-RI
P205: C-CUST-METER-NO
P205: C-CUST-RATE
*/
create table MeterHardwareBase 
(
   InventoryID			NUMBER			not null,
   MeterNumber			VARCHAR2(30)		not null,
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

/*
P206: SERVICE-ORDER-ID
(This is the PTJID)
*/
alter table WorkOrderBase add AdditionalOrderNumber VARCHAR2(24);

/*
P206: DUAL-STAGE
*/
create table ApplianceDualStageAirCond  (
   ApplianceID          	NUMBER            	not null,
   StageOneTonnageID      	NUMBER,
   StageTwoTonnageID      	NUMBER,
   TypeID			NUMBER
);

alter table ApplianceDualStageAirCond
   add constraint PK_APPLIANCEDUALSTAGE primary key (ApplianceID);

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
   add constraint FK_DUALSTAGE_APPLNCBSE foreign key (ApplianceID)
      references ApplianceBase (ApplianceID);

/*
DATED EVENTS*/
/*****************************************************************************/
create table EventBase  (
   EventID              	NUMBER			not null,
   UserID			NUMBER			not null,
   SystemCategoryID     	NUMBER                  not null,
   ActionID             	NUMBER                  not null,
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

/*
P206: D-INSTALL
P206: D-REMOVE
P210: D-RECEIVED
P210: D-ISSUED
P210: D-STATUS-CHG
P210: D-LAST-CHG
*/
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

/*
P206: D-CUST-CALL
P206: D-WO-PRINT
P206: D-WO-RETURNED
P206: D-LAST-CHG
*/
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

/*
P205: D-LAST-CHG
*/
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
/*****************************************************************************/
