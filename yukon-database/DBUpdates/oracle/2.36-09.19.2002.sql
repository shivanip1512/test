/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.36', 'Ryan', '9-SEP-2002', 'Changed loadprofile defaults, add column to DynamicPointDispatch, added a row to BillingFileFormats');


/* Default all DeviceLoadProfile intervals to 5 minutes(300 seconds), not 0 */
update deviceloadprofile set lastintervaldemandrate=300 where lastintervaldemandrate=0;
update deviceloadprofile set loadprofiledemandrate=300 where loadprofiledemandrate=0;
go

/* Add a column to the DynamicCCSubstationBus table */
alter table DynamicPointDispatch add LastAlarmLogID NUMBER;
update DynamicPointDispatch set LastAlarmLogID = 0;
alter TABLE DynamicPointDispatch MODIFY LastAlarmLogID NOT NULL;
/

/* Add a billing file format */
insert into BillingFileFormats values(7,'NCDC');
go