/* Start YUK-4116, YUK-4936, YUK-5001 */
create table LMHardwareControlGroup (
   ControlEntryID       int                  not null,
   InventoryID          int                  not null,
   LMGroupID            int                  not null,
   AccountID            int                  not null,
   GroupEnrollStart     datetime             null,
   GroupEnrollStop      datetime             null,
   OptOutStart          datetime             null,
   OptOutStop           datetime             null,
   Type                 int                  not null,
   Relay                int                  not null,
   UserIDFirstAction    int                  not null,
   UserIDSecondAction   int                  not null
);
go

alter table LMHardwareControlGroup
   add constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID);
go
/* End YUK-4116, YUK-4936, YUK-5001 */


/* Start YUK-5017, YUK-5001 */
/* @error ignore-begin */
alter table ScheduleTimePeriod
drop constraint FK_SCHDTMPRD_REF_DS;

alter table ScheduleShipmentMapping
drop constraint FK_SCHDSHPMNTMAP_DS;

ALTER TABLE deliveryschedule
drop CONSTRAINT pk_deliverysched;

alter table deliveryschedule
add constraint pk_deliveryschedule primary key (ScheduleID);

alter table ScheduleShipmentMapping
   add constraint FK_SCHDSHPMNTMAP_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);

alter table ScheduleTimePeriod
   add constraint FK_SCHDTMPRD_REF_DS foreign key (ScheduleID)
      references DeliverySchedule (ScheduleID);
/* @error ignore-end */
/* End YUK-5017, YUK-5001 */

/* Start YUK-5312 */
ALTER TABLE [MeterHardwareBase]
ALTER COLUMN [MeterTypeID] [numeric] (18, 0) NOT NULL;
GO

ALTER TABLE [Warehouse]
ALTER COLUMN [Notes] [varchar] (300) NULL;
GO

ALTER TABLE [WorkOrderBase]
ALTER COLUMN [AdditionalOrderNumber] [varchar] (24) NULL;
GO
/* End YUK-5312 */
