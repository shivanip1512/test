/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.15', 'Ryan', '19-JUL-2002', 'Added MCTBroadCast, added column to the LMProgramDirectGear, added columns to CapControlSubStationBus and CapControlFeeder');
go
sp_rename 'CapControlSubstationBus.Bandwidth', 'UpperBandwidth', 'COLUMN';
go
sp_rename 'CapControlFeeder.Bandwidth', 'UpperBandwidth', 'COLUMN';
go

alter table CapControlSubstationBus ALTER COLUMN UpperBandwidth FLOAT;
alter table CapControlFeeder ALTER COLUMN UpperBandwidth FLOAT;
go
alter table CapControlSubstationBus ADD LowerBandwidth FLOAT not null DEFAULT 0.0;
alter table CapControlFeeder ADD LowerBandwidth FLOAT not null DEFAULT 0.0;
go
UPDATE CapControlSubstationBus SET UpperBandwidth=LowerBandwidth;
go
UPDATE CapControlFeeder SET UpperBandwidth=LowerBandwidth;
go
alter table CapControlSubstationBus ADD ControlUnits VARCHAR(20) not null DEFAULT 'KVAR';
go

/* Add some rows to the BillingFileFormats table */
insert into BillingFileFormats values(-1,'INVALID');
insert into BillingFileFormats values(0,'SEDC');
insert into BillingFileFormats values(1,'CADP');
insert into BillingFileFormats values(2,'CADPXL2');
insert into BillingFileFormats values(3,'WLT-40');
insert into BillingFileFormats values(4,'CTI-CSV');
insert into BillingFileFormats values(5,'OPU');
insert into BillingFileFormats values(6,'DAFFRON');
insert into BillingFileFormats values(7,'NCDC');
go

/* Change the LMProgramDirectGear table a little */
alter table LMProgramDirectGear drop constraint PK_LMPROGRAMDIRECTGEAR;
alter table LMProgramDirectGear add GearID numeric not null DEFAULT 0;
go
/* populate the new Primary Key(GearID) with unique values (WOW!!) */
declare @cnt numeric set @cnt = 0 update LMProgramDirectGear SET @cnt = GearID = @cnt + 1;
go
alter table LMProgramDirectGear add constraint PK_LMPROGRAMDIRECTGEAR primary key(GearID);
go
create unique index Indx_LMPrgDiGear_N_DevID on LMProgramDirectGear (DeviceID,GearNumber);
go


/* Create the new MCTBroadCastMapping table and its constraints */
create table MCTBroadCastMapping (
MCTBroadCastID       numeric              not null,
MctID                numeric              not null,
Ordering             numeric              not null,
constraint PK_MCTBROADCASTMAPPING primary key  (MCTBroadCastID, MctID)
);
go
alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
      references DEVICE (DEVICEID);
go
alter table MCTBroadCastMapping
   add constraint FK_MCTB_MAPMCT foreign key (MctID)
      references DEVICE (DEVICEID);
go


/* Add some new UnitMeasures */
INSERT INTO UnitMeasure VALUES ( 43,'Tap', 0,'LTC Tap Position','(none)' );
INSERT INTO UnitMeasure VALUES ( 44,'Miles', 0,'Miles','(none)' );
INSERT INTO UnitMeasure VALUES ( 45,'ms', 0,'Milliseconds','(none)' );
go
