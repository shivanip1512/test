/* Start YUK-4116, YUK-4936, YUK-5001 */
create table LMHardwareControlGroup  (
   ControlEntryID       INTEGER                         not null,
   InventoryID          INTEGER                         not null,
   LMGroupID            INTEGER                         not null,
   AccountID            INTEGER                         not null,
   GroupEnrollStart     DATE,
   GroupEnrollStop      DATE,
   OptOutStart          DATE,
   OptOutStop           DATE,
   Type                 INTEGER                         not null,
   Relay                INTEGER                         not null,
   UserIDFirstAction    INTEGER                         not null,
   UserIDSecondAction   INTEGER                         not null
);

alter table LMHardwareControlGroup
   add constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID);
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
/* End YUK-5017, YUK-5001*/

/* Start YUK-5312 */
/* @error ignore-begin */
ALTER TABLE METERHARDWAREBASE
MODIFY(METERTYPEID  NOT NULL);

ALTER TABLE WAREHOUSE
MODIFY(NOTES  NULL);

ALTER TABLE WORKORDERBASE
MODIFY(ADDITIONALORDERNUMBER  NULL);
/* @error ignore-end */
/* End YUK-5312 */
