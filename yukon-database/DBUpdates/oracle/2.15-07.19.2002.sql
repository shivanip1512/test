/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.15', 'Ryan', '19-JUL-2002', 'Added MCTBroadCast, added column to the LMProgramDirectGear, added columns to CapControlSubStationBus and CapControlFeeder');


/******************************************************************************
*  THIS NEEDS THE rencol() PROCEDURE INSTALLED BEFORE IT WILL WORK
*  You must install the rencol() procedure AND run it from 
*  the sys/CHANGE_ON_INSTALL account
*
*  NOTE!!! for Oracle 9.x you should log in as 
*  sys/CHANGE_ON_INSTALL as sysdba (ACL 7/29)
*
******************************************************************************/
exec rencol('<UserName>', 'CapControlSubstationBus', 'Bandwidth', 'UpperBandwidth');
exec rencol('<UserName>', 'CapControlFeeder', 'Bandwidth', 'UpperBandwidth');



/******************************************************************************
*  Don't forget to log back in as the regular YUKON user now
******************************************************************************/
alter table CapControlSubstationBus MODIFY UpperBandwidth FLOAT;
alter table CapControlFeeder MODIFY UpperBandwidth FLOAT;

alter table CapControlSubstationBus add LowerBandwidth FLOAT;
alter table CapControlSubstationBus MODIFY LowerBandwidth NOT NULL;
alter table CapControlFeeder ADD LowerBandwidth FLOAT;
alter table CapControlFeeder MODIFY LowerBandwidth NOT NULL;

UPDATE CapControlSubstationBus SET UpperBandwidth=LowerBandwidth;
UPDATE CapControlFeeder SET UpperBandwidth=LowerBandwidth;

alter table CapControlSubstationBus ADD ControlUnits VARCHAR2(20);
UPDATE CapControlSubstationBus SET ControlUnits='KVAR';
alter table CapControlSubstationBus MODIFY ControlUnits NOT NULL;



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



/* Change the LMProgramDirectGear table a little */
alter table LMProgramDirectGear add GearID NUMBER;
alter table LMProgramDirectGear MODIFY GearID NOT NULL;

/* populate the new Primary Key(GearID) with unique values (WOW!!) */
UPDATE LMProgramDirectGear SET GearID = ROWNUM;

ALTER TABLE LMProgramDirectGear DROP CONSTRAINT PK_LMPROGRAMDIRECTGEAR;
alter table LMProgramDirectGear add constraint PK_LMPROGRAMDIRECTGEAR primary key(GearID);
create unique index Indx_LMPrgDiGear_N_DevID on LMProgramDirectGear 
(
   DeviceID ASC,
   GearNumber ASC
);



/* Create the new MCTBroadCastMapping table and its constraints */
create table MCTBroadCastMapping  (
   MCTBroadCastID       NUMBER                           not null,
   MctID                NUMBER                           not null,
   Ordering             NUMBER                           not null,
   constraint PK_MCTBROADCASTMAPPING primary key (MCTBroadCastID, MctID),
   constraint FK_MCTB_MAPDEV foreign key (MCTBroadCastID)
         references DEVICE (DEVICEID),
   constraint FK_MCTB_MAPMCT foreign key (MctID)
         references DEVICE (DEVICEID)
);


/* Add some new UnitMeasures */
INSERT INTO UnitMeasure VALUES ( 43,'Tap', 0,'LTC Tap Position','(none)' );
INSERT INTO UnitMeasure VALUES ( 44,'Miles', 0,'Miles','(none)' );
INSERT INTO UnitMeasure VALUES ( 45,'ms', 0,'Milliseconds','(none)' );

