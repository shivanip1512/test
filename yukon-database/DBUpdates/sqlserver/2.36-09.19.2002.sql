/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.36', 'Ryan', '9-SEP-2002', 'Changed loadprofile defaults, add column to DynamicPointDispatch, added a row to BillingFileFormats');


/* Default all DeviceLoadProfile intervals to 5 minutes(300 seconds), not 0 */
update deviceloadprofile set lastintervaldemandrate=300 where lastintervaldemandrate=0;
update deviceloadprofile set loadprofiledemandrate=300 where loadprofiledemandrate=0;
go

/* Add a column to the DynamicPointDispatch table */
alter TABLE DynamicPointDispatch add LastAlarmLogID NUMERIC not null DEFAULT 0;



/* Add a column to the DynamicCCSubstationBus table */
alter TABLE DynamicCCSubstationBus add EstimatedPFValue FLOAT not null DEFAULT 0.0;
go

/* Add a column to the DynamicCCFeeder table */
alter TABLE DynamicCCFeeder add EstimatedPFValue FLOAT not null DEFAULT 0.0;
go