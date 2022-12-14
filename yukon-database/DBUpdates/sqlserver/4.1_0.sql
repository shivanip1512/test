/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start YUK-5107 */
if exists (select 1
            from  sysobjects
           where  id = object_id('MeterReadLog')
            and   type = 'U')
   drop table MeterReadLog;
go

delete from SequenceNumber where SequenceName = 'MeterReadLog';
/* End YUK-5107 */


/* Start YUK-5103 */
/* @error ignore-begin */
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select -110,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0;
/* @error ignore-end */ 
/* End YUK-5103*/

/* Start YUK-5123 */
ALTER TABLE [DeviceRoutes]
DROP CONSTRAINT [PK_DEVICEROUTES];
GO

ALTER TABLE [DeviceRoutes]
ADD CONSTRAINT [PK_DEVICEROUTES] PRIMARY KEY CLUSTERED ([DEVICEID]);
GO
/* End YUK-5123 */

/* Start YUK-5119 */
insert into YukonRoleProperty values(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
/* End YUK-5119 */

/* Start YUK-5204 */
alter table Job add Locale varchar(10);
go
update Job set Locale = 'en_US';
go
alter table Job alter column Locale varchar(10) not null;
go

alter table Job add TimeZone varchar(40);
go
update Job set TimeZone = ' ';
go
alter table Job alter column TimeZone varchar(40) not null;
go
/* End YUK-5204 */

/* Start YUK-5350 */
/* @error ignore-begin */
insert into YukonRoleProperty values(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 
/* @error ignore-end */
/* End YUK-5350 */

/* Start YUK-5337 */ 
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Meters',12,'Y','STATIC' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go 

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Scanning',MAX(DeviceGroupID),'Y','STATIC' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go 

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Load Profile',0,'Y','METERS_SCANNING_LOAD_PROFILE' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Voltage Profile',0,'Y','METERS_SCANNING_VOLTAGE_PROFILE' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Integrity',0,'Y','METERS_SCANNING_INTEGRITY' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go

INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type) 
SELECT MAX(DeviceGroupID)+1,'Accumulator',0,'Y','METERS_SCANNING_ACCUMULATOR' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go 

UPDATE DeviceGroup 
SET ParentDeviceGroupId = (SELECT DeviceGroupId 
                           FROM DeviceGroup 
                           WHERE GroupName='Scanning') 
WHERE Type IN ('METERS_SCANNING_LOAD_PROFILE','METERS_SCANNING_VOLTAGE_PROFILE','METERS_SCANNING_INTEGRITY','METERS_SCANNING_ACCUMULATOR'); 
go 

ALTER TABLE DeviceGroup ALTER COLUMN SystemGroup nvarchar(50); 
go 
EXEC sp_rename 'DeviceGroup.SystemGroup', 'Permission', 'COLUMN'; 
go 

UPDATE DeviceGroup 
SET Permission = 'NOEDIT_NOMOD' 
WHERE Permission = 'Y' 
AND Type = 'STATIC' 
AND GroupName IN ('Meters') 
AND ParentDeviceGroupId = 12; 
go 

UPDATE DeviceGroup 
SET Permission = 'NOEDIT_NOMOD' 
WHERE Permission = 'Y' 
AND Type = 'STATIC' 
AND GroupName IN ('Scanning'); 
go 

UPDATE DeviceGroup 
SET Permission = 'NOEDIT_MOD' 
WHERE Permission = 'Y' 
AND Type = 'STATIC'; 
go 

UPDATE DeviceGroup 
SET Permission = 'NOEDIT_NOMOD' 
WHERE Permission = 'Y' 
AND Type != 'STATIC'; 
go 

UPDATE DeviceGroup 
SET Permission = 'EDIT_MOD' 
WHERE Permission = 'N'; 
go 

UPDATE devicegroup 
SET Permission = 'NOEDIT_NOMOD' 
WHERE GroupName = 'System' 
AND ParentDeviceGroupID = 0 
AND Type = 'STATIC'; 
go 
/* End YUK-5337 */

/* Start YUK-5454 */
ALTER TABLE DYNAMICBILLINGFIELD ADD PadChar char(1);
go
UPDATE DYNAMICBILLINGFIELD SET PadChar = ' ';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column PadChar char(1) not null;
go

ALTER TABLE DYNAMICBILLINGFIELD ADD PadSide varchar(5);
go
UPDATE DYNAMICBILLINGFIELD SET PadSide = 'none';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column PadSide varchar(5) not null;
go

ALTER TABLE DYNAMICBILLINGFIELD ADD ReadingType varchar(12);
go
UPDATE DYNAMICBILLINGFIELD SET ReadingType = 'ELECTRIC';
go
ALTER TABLE DYNAMICBILLINGFIELD alter column ReadingType varchar(12) not null;
go
/* End YUK-5454 */

/* Start YUK-5519 */
alter table CAPBANK alter column MAPLOCATIONID varchar(64) not null;
go
alter table CapControlFeeder alter column MAPLOCATIONID varchar(64) not null;
go
alter table CapControlSubstationBus alter column MAPLOCATIONID varchar(64) not null;
go
/* End YUK-5519 */

/* Start YUK-5579 */
INSERT INTO BillingFileFormats VALUES(-32, 'NISC TOU (kVarH) Rates Only', 1);
/* End YUK-5579 */

/* Start YUK-5663 */
ALTER TABLE JOBSCHEDULEDREPEATING ALTER COLUMN CronString VARCHAR(100) not null; 

ALTER TABLE JOBPROPERTY ALTER COLUMN name VARCHAR(100) not null; 

ALTER TABLE JOBPROPERTY ALTER COLUMN value VARCHAR(1000) not null; 

ALTER TABLE JOBSTATUS ALTER COLUMN Message VARCHAR(1000) not null;
/* End YUK-5663 */

/* Start YUK-5673 */
/* @error ignore-begin */
INSERT INTO Command VALUES(-141, 'putconfig emetcon freeze day ?''Day of month (0-31)''', 'Set meter to freeze on X day of month (use 0 for disable).', 'MCT-410IL'); 
INSERT INTO Command VALUES(-142, 'getconfig freeze', 'Read freeze config from meter and enable scheduled freeze procesing in Yukon.', 'MCT-410IL'); 

INSERT INTO DeviceTypeCommand VALUES (-714, -141, 'MCT-410CL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-715, -141, 'MCT-410FL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-716, -141, 'MCT-410GL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-717, -141, 'MCT-410IL', 34, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-718, -142, 'MCT-410CL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-719, -142, 'MCT-410FL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-720, -142, 'MCT-410GL', 35, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-721, -142, 'MCT-410IL', 35, 'N', -1); 
/* @error ignore-end */
/* End YUK-5673 */

/* Start YUK-5630 */
/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('AreaSubBusFeeder_View')
            and   type = 'V')
   drop view AreaSubBusFeeder_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CCInventory_View')
            and   type = 'V')
   drop view CCInventory_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CCCapInventory_View')
            and   type = 'V')
   drop view CCCapInventory_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CCCBCInventory_View')
            and   type = 'V')
   drop view CCCBCInventory_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CCCBCCVMSState_View')
            and   type = 'V')
   drop view CCCBCCVMSState_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CBCConfiguration_View')
            and   type = 'V')
   drop view CBCConfiguration_View
go
/* @end-block */

/* @start-block */
if exists (select 1
            from  sysobjects
           where  id = object_id('CBCConfiguration2_View')
            and   type = 'V')
   drop view CBCConfiguration2_View
go
/* @end-block */

/*==============================================================*/
/* View: AreaSubBusFeeder_View                                  */
/*==============================================================*/
CREATE VIEW AreaSubBusFeeder_View
AS 
SELECT YPA.PAOName AS Region, YPS.PAOName AS Substation, YP.PAOName AS Subbus, YPF.PAOName AS Feeder, 
       STRAT.StrategyName, STRAT.ControlMethod, SA.SeasonName, 
       cast(DOS.SeasonStartMonth AS VARCHAR(2)) + '/' + cast(DOS.SeasonStartDay AS VARCHAR(2)) + '/' + 
       cast(datepart(year,getdate()) AS VARCHAR(4)) AS SeasonStartDate, 
       cast(DOS.SeasonEndMonth AS VARCHAR(2)) + '/'  + cast(DOS.SeasonEndDay AS VARCHAR(2)) + '/' + 
       cast(datepart(year,getdate()) AS VARCHAR(4)) AS SeasonEndDate
FROM CCSeasonStrategyAssignment SA
JOIN YukonPAObject YPX ON (YPX.PAObjectId = SA.PAObjectId OR YPX.PAObjectId = SA.PAObjectId OR YPX.PAObjectId = SA.PAObjectId)
JOIN YukonPAObject YP ON YP.PAObjectId = SA.PAObjectId
JOIN CCFeederSubAssignment FS ON FS.SubstationBusId = YP.PAObjectId
JOIN YukonPAObject YPF ON YPF.PAObjectId = FS.FeederId
JOIN CCSubstationSubbusList SS ON SS.SubstationBusId = YP.PAObjectId
JOIN CCSubAreaAssignment SAA ON SAA.SubstationBusId = SS.SubstationId
JOIN YukonPAObject YPS ON SS.SubstationId = YPS.PAObjectId
JOIN YukonPAObject YPA ON YPA.PAObjectId = SAA.AreaId
JOIN CapControlStrategy STRAT ON STRAT.StrategyId = SA.StrategyId
JOIN DateOfSeason DOS ON SA.SeasonName = DOS.SeasonName 
AND SA.SeasonScheduleId = DOS.SeasonScheduleId
AND (cast(SeasonStartMonth AS VARCHAR(2)) + '/' + 
          cast(SeasonStartDay AS VARCHAR(2)) + '/' + 
          cast(datepart(year,getdate()) AS VARCHAR(4))) <= getdate()
AND (cast(SeasonEndMonth AS VARCHAR(2)) + '/' + 
          cast(SeasonEndDay AS VARCHAR(2)) + '/' + 
          cast(datepart(year,getdate()) AS VARCHAR(4))) > getdate()
go

/*==============================================================*/
/* View: CBCConfiguration2_View                                 */
/*==============================================================*/
create view CBCConfiguration2_View as
SELECT YP.PAOName AS CBCName, D.* 
FROM DynamicCCTwoWayCBC D, YukonPAObject YP
WHERE YP.PAObjectId = D.DeviceId
go

/*==============================================================*/
/* View: CBCConfiguration_View                                  */
/*==============================================================*/
create view CBCConfiguration_View as
SELECT YP.PAOName AS CBCName, YP.PAObjectId AS CBCId, P.PointName AS PointName, P.PointId AS PointId, 
       PD.Value AS PointValue, PD.Timestamp, UOM.UOMName AS UnitOfMeasure
FROM Point P
JOIN YukonPAObject YP ON YP.PAObjectId = P.PAObjectId AND YP.Type like 'CBC 702%'
LEFT OUTER JOIN DynamicPointDispatch PD ON PD.PointId = P.PointId
LEFT OUTER JOIN PointUnit PU ON PU.PointId = P.PointId
LEFT OUTER JOIN UnitMeasure UOM ON UOM.UOMId = PU.UOMId
go

/*==============================================================*/
/* View: CCCBCCVMSState_View                                    */
/*==============================================================*/
/* @start-block */
create view CCCBCCVMSState_View as
SELECT YP5.PAOName AS Region, YP4.PAOName AS Substation, CB.MapLocationId AS OpCenter, 
       YP3.PAOName AS SubName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       YP.PAOName AS CBCName, S.Text AS CapBankStatus, S1.Text AS CBCStatus, 
       CASE WHEN S.Text = S1.Text THEN 'No' ELSE 'Yes' END AS IsMisMatch, 
       DCB.LastStatusChangeTime AS CapBankChangeTime, DCB.TwoWayCBCStateTime AS CBCChangeTime
FROM (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
      FROM YukonPAObject
      WHERE  (Type LIKE 'CBC 702%')) YP 
LEFT OUTER JOIN CapBank CB ON CB.ControlDeviceId = YP.PAObjectId AND CB.ControlDeviceId > 0 
INNER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
            FROM YukonPAObject YukonPAObject_3
            WHERE (Type LIKE 'CAP BANK')) YP1 ON YP1.PAObjectId = cb.DeviceId 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_2
                 WHERE (Type LIKE 'CCFEEDER')) YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCSUBBUS')) YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SS ON SS.SubstationBusId = SF.SubStationBusId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCSUBSTATION')) YP4 ON YP4.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SS.SubStationId 
LEFT OUTER JOIN (SELECT PAObjectId, Category, PAOClass, PAOName, Type, Description, DisableFlag, PAOStatistics
                 FROM YukonPAObject YukonPAObject_1
                 WHERE (Type LIKE 'CCAREA')) YP5 ON YP5.PAObjectId = SA.AreaId 
INNER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = CB.DeviceId 
INNER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State S1 ON S1.StateGroupId = 3 AND DCB.TwoWayCBCState = S1.RawState
go
/* @end-block */

/*==============================================================*/
/* View: CCCBCInventory_View                                    */
/*==============================================================*/
/* @start-block */
create view CCCBCInventory_View (CBCNAME, IPADDRESS, SLAVEADDRESS, CONTROLLERTYPE, OPCENTER, REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, CAPBANKNAME, BANKSIZE, OPERATIONMETHOD, LAT, LON, DRIVEDIRECTION, CAPBANKADDRESS, TA, CAPBANKCONFIG, COMMMEDIUM, COMMSTRENGTH, EXTERNALANTENNA, OPERATIONSCOUNTERRESETDATE, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
SELECT YP.PAOName AS CBCName, DPI.Value AS IPAddress, DA.SlaveAddress, CB.ControllerType, 
       CB.MapLocationId AS OpCenter, YP5.PAOName AS Region, YP4.PAOName AS SubstationName, 
       YP3.PAOName AS SubBusName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       CB.BankSize, CB.OperationalState AS OperationMethod, CAPA.Latitude AS Lat, 
       CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       YP1.Description AS CapBankAddress, CAPA.MaintenanceAreaId AS TA, CAPA.CapBankConfig,
       CAPA.CommMedium, CAPA.CommStrength, CAPA.ExtAntenna AS ExternalAntenna, 
       CAPA.OpCountResetDate AS OperationsCounterResetDate, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, 
       DTWC.TotalOpCount AS OperationsCounterToday, DTWC.UvOpCount AS UvOperationsCounter, 
       DTWC.OvOpCount AS OvOperationsCounter, DTWC.OvUvCountResetDate AS UvOvCounterResetDate, 
       DTWC.LastOvUvDateTime
FROM (SELECT PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP 
LEFT OUTER JOIN CapBank CB ON CB.ControlDeviceId = YP.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP1 ON CB.DeviceId = YP1.PAObjectId 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId
go
/* @end-block */

/*==============================================================*/
/* View: CCCapInventory_View                                    */
/*==============================================================*/
/* @start-block */
create view CCCapInventory_View as
SELECT YP4.PAOName AS Region, CB.MapLocationId AS OpCenter, YP5.PAOName AS SubstationName, 
       YP3.PAOName AS SubbusName, YP2.PAOName AS FeederName, YP1.PAOName AS CapBankName, 
       CB.BankSize, CAPA.Latitude AS Lat, CAPA.Longitude AS LON, 
       CAPA.DriveDirections AS DriveDirection, CB.OperationalState AS OperationMethod, 
       CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType, YP.PAOName AS CBCName, 
       DPI.Value AS IPAddress, DA.SlaveAddress, CAPA.MaintenanceAreaId AS TA, 
       CAPA.CapBankConfig, CAPA.CommMedium, CAPA.CommStrength
FROM CapBank CB 
INNER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP ON CB.ControlDeviceId = YP.PAObjectId AND CB.ControlDeviceId > 0 
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubStationSubbusList SS on SS.SubstationBusId = YP3.PAObjectId
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SS.SubstationId = SA.SubstationBusId
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId
go
/* @end-block */

/*==============================================================*/
/* View: CCInventory_View                                       */
/*==============================================================*/
/* @start-block */
create view CCInventory_View (REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, AREAID, SUBID, SUBBUSID, FDRID, CBCNAME, CBCID, CAPBANKNAME, BANKID, CAPBANKSIZE, DISPLAYORDER, CONTROLSTATUS, CONTROLSTATUSNAME, SWMFGR, SWTYPE, OPERATIONMETHOD, CONTROLLERTYPE, IPADDRESS, SLAVEADDRESS, LAT, LON, DRIVEDIRECTION, OPCENTER, TA, CLOSESEQUENCE, OPENSEQUENCE, LASTOPERATIONTIME, LASTINSPECTIONDATE, LASTMAINTENANCEDATE, MAINTENANCEREQPEND, CAPDISABLED, POTENTIALTRANSFORMER, OTHERCOMMENTS, OPTEAMCOMMENTS, POLENUMBER, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
SELECT YP4.PAOName AS Region, YP5.PAOName AS SubstationName, YP3.PAOName AS SubBusName, 
       YP2.PAOName AS FeederName, YP4.PAObjectId AS AreaId, YP5.PAObjectId AS SubId, 
       YP3.PAObjectId AS SubBusId, YP2.PAObjectId AS FdrId, YP.PAOName AS CBCName, 
       YP.PAObjectId AS CBCId, YP1.PAOName AS CapBankName, YP1.PAObjectId AS BankId, 
       CB.BankSize AS CapBankSize, FB.ControlOrder AS DisplayOrder, DCB.ControlStatus, 
       S.Text AS ControlStatusName, CB.SwitchManufacture AS SWMfgr, CB.TypeOfSwitch AS SWType,
       CB.OperationalState AS OperationMethod, CB.ControllerType, DPI.Value AS IPAddress, 
       DA.SlaveAddress, CAPA.Latitude AS Lat, CAPA.Longitude AS Lon, CAPA.DriveDirections AS DriveDirection, 
       CB.MapLocationId AS OpCenter, CAPA.MaintenanceAreaId AS TA, FB.CloseOrder AS CloseSequence, 
       FB.TripOrder AS OpenSequence, DCB.LastStatusChangeTime AS LastOperationTime, 
       CAPA.LastInspVisit AS LastInspectionDate, CAPA.LastMaintVisit AS LastMaintenanceDate, 
       CAPA.MaintenanceReqPend, YP1.DisableFlag as CapDisabled, CAPA.PotentialTransformer, 
       CAPA.OtherComments, CAPA.OpTeamComments, CAPA.PoleNumber, 
       DTWC.TotalOpCount AS OpsCounterSinceLastReset, DTWC.TotalOpCount AS OperationsCounterToday, 
       DTWC.UvOpCount AS UvOperationsCounter, DTWC.OvOpCount AS OvOperationsCounter, 
       DTWC.OvUvCountResetDate AS UvOvCounterResetDate, DTWC.LastOvUvDateTime
FROM (SELECT  PAObjectId, PAOName 
      FROM YukonPAObject 
      WHERE (Type LIKE '%CBC%')) YP 
LEFT OUTER JOIN CapBank CB ON YP.PAObjectId = CB.ControlDeviceId 
LEFT OUTER JOIN YukonPAObject YP1 ON YP1.PAObjectId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCCapBank DCB ON DCB.CapBankId = YP1.PAObjectId 
LEFT OUTER JOIN State S ON S.StateGroupId = 3 AND DCB.ControlStatus = S.RawState 
LEFT OUTER JOIN State SL ON SL.StateGroupId = 3 AND DCB.TwoWayCBCState = SL.RawState
LEFT OUTER JOIN CCFeederBankList FB ON FB.DeviceId = CB.DeviceId 
LEFT OUTER JOIN YukonPAObject YP2 ON YP2.PAObjectId = FB.FeederId 
LEFT OUTER JOIN CCFeederSubAssignment SF ON FB.FeederId = SF.FeederId 
LEFT OUTER JOIN YukonPAObject YP3 ON YP3.PAObjectId = SF.SubStationBusId 
LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = YP3.PAObjectId 
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SSL.SubstationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SA.SubstationBusId = SSL.SubstationId 
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId 
LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = YP.PAObjectId 
LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId
go
/* @end-block */
/* End YUK-5630 */


/* Start YUK-5729 */
DELETE FROM YukonUserRole
WHERE RolePropertyId IN (-10204, -10207, -10208, -10209, -10210, -10211, 
						 -10212, -10213, -10214, -10215, -10216, -10217,
						 -10218, -10219, -10220, -10221);

DELETE FROM YukonGroupRole
WHERE RolePropertyId IN (-10204, -10207, -10208, -10209, -10210, -10211, 
						 -10212, -10213, -10214, -10215, -10216, -10217,
						 -10218, -10219, -10220, -10221);

DELETE FROM YukonRoleProperty
WHERE RolePropertyId IN (-10204, -10207, -10208, -10209, -10210, -10211, 
						 -10212, -10213, -10214, -10215, -10216, -10217,
						 -10218, -10219, -10220, -10221);
/* End YUK-5729 */

/* Start YUK-5403 */
/* @start-block */
if exists (SELECT 1
           FROM  SysObjects
           WHERE  Id = Object_Id('TempMovedCapBanks_View')
           AND   type = 'V')
   DROP VIEW TempMovedCapBanks_View
go
/* @end-block */

/* @start-block */
create view TempMovedCapBanks_View as
SELECT YPF.PAOName TempFeederName, YPF.PAObjectId TempFeederId, YPC.PAOName CapBankName, 
       YPC.PAObjectId CapBankId, FB.ControlOrder, FB.CloseOrder, FB.TripOrder, 
       YPOF.PAOName OriginalFeederName, YPOF.PAObjectId OriginalFeederId 
FROM CCFeederBankList FB, YukonPAObject YPF, YukonPAObject YPC, YukonPAObject YPOF, DynamicCCCapBank DC 
WHERE FB.DeviceId = DC.CapBankId 
AND YPC.PAObjectId = DC.CapBankId 
AND FB.FeederId = YPF.PAObjectId 
AND YPOF.PAObjectId = DC.OriginalFeederId 
AND DC.OriginalFeederId <> 0
go
/* @end-block */
/* End YUK-5403 */

/* Start YUK-5791 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-70020,-700, 'Force Default Comment', 'false', 'If the user does not provide a comment, a default comment will be stored.'); 
/* @error ignore-end */
/* End YUK-5791 */

/* Start YUK-5567 */
/* @error ignore-begin */
INSERT INTO FDRInterface VALUES(26, 'OPC', 'Receive', 'f'); 

INSERT INTO FDRInterfaceOption VALUES(26, 'Server Name', 1, 'Text', '(none)'); 
INSERT INTO FDRInterfaceOption VALUES(26, 'OPC Group', 2, 'Text', '(none)'); 
INSERT INTO FDRInterfaceOption VALUES(26, 'OPC Item', 3, 'Text', '(none)');
/* @error ignore-end */
/* End YUK-5567 */

/* Start YUK-5768 */
INSERT INTO DeviceTypeCommand VALUES (-722, -140, 'MCT-470', 33, 'Y', -1);
/* End YUK-5768 */

/* Start YUK-5759 */
/* Adding ProtocolPriority column to LMControlHistory */
ALTER TABLE LMControlHistory ADD ProtocolPriority numeric(9,0);
go
UPDATE LMControlHistory SET ProtocolPriority = 0;
go
UPDATE LMControlhistory SET ProtocolPriority = 3 
WHERE PAObjectId IN (SELECT LMGroupId 
                     FROM LMGroupExpressCom);
go
ALTER TABLE LMControlHistory ALTER COLUMN ProtocolPriority numeric(9,0) NOT NULL;
go

/* Adding ProtocolPriority column to DynamicLMControlHistory */ 
ALTER TABLE DynamicLMControlHistory ADD ProtocolPriority numeric(9,0);
go
UPDATE DynamicLMControlHistory SET ProtocolPriority = 0;
go
UPDATE DynamicLMControlHistory SET ProtocolPriority = 3 
WHERE PAObjectId IN (SELECT LMGroupID 
                     FROM LMGroupExpressCom);
go
ALTER TABLE DynamicLMControlHistory ALTER COLUMN ProtocolPriority numeric(9,0) NOT NULL;
go

/* Adding ProtocolPriority column to LMGroupExpressCom */
ALTER TABLE LMGroupExpressCom ADD ProtocolPriority numeric(9,0);
go
UPDATE LMGroupExpressCom SET ProtocolPriority = 3;
go
ALTER TABLE LMGroupExpressCom ALTER COLUMN ProtocolPriority numeric(9,0) NOT NULL;
go

/* Adding ProtocolPriority column to the ExpressComAddress_View */
/* @start-block */
if exists (SELECT 1
           FROM  SysObjects
           WHERE  Id = Object_Id('ExpressComAddress_View')
           AND   type = 'V')
   DROP VIEW ExpressComAddress_View
go
/* @end-block */

/* @start-block */
CREATE VIEW ExpressComAddress_View AS
SELECT X.LMGroupId, X.RouteId, X.SerialNumber, S.Address AS ServiceAddress, G.Address AS GeoAddress,
       B.Address AS SubstationAddress, F.Address AS FeederAddress, Z.Address AS ZipCodeAddress, 
       US.Address AS UDAddress, P.Address AS ProgramAddress, SP.Address AS SplinterAddress, 
       X.AddressUsage, X.RelayUsage, X.ProtocolPriority
FROM LMGroupExpressCom X, LMGroupExpressComAddress S, LMGroupExpressComAddress G, 
     LMGroupExpressComAddress B, LMGroupExpressComAddress F, LMGroupExpressComAddress P,
     LMGroupExpressComAddress SP, LMGroupExpressComAddress US, LMGroupExpressComAddress Z
WHERE (X.ServiceProviderId = S.AddressId AND 
      (S.AddressType = 'SERVICE' OR S.AddressId = 0)) AND 
      (X.FeederId = F.AddressId AND 
      (F.AddressType = 'FEEDER' OR F.AddressId = 0)) AND 
      (X.GeoId = G.AddressId AND 
      (G.AddressType = 'GEO' OR G.AddressId = 0 )) AND 
      (X.ProgramId = P.AddressId AND 
      (P.AddressType = 'PROGRAM' OR P.AddressId = 0)) AND 
      (X.SubstationId = B.AddressId AND 
      (B.AddressType = 'SUBSTATION' OR B.AddressId = 0)) AND 
      (X.SplinterId = SP.AddressId AND 
      (SP.AddressType = 'SPLINTER' OR SP.AddressId = 0)) AND 
      (X.UserId = US.AddressId AND 
      (US.AddressType = 'USER' OR US.AddressId = 0)) AND 
      (X.ZipId = Z.AddressId AND 
      (Z.AddressType = 'ZIP' OR Z.AddressId = 0))
go
/* @end-block */
/* End YUK-5759 */

/* Start YUK-5419 */
UPDATE YukonRole SET RoleDescription = 'Administrator privileges.' 
WHERE RoleDescription = 'Administrator privilages.';
/* End YUK-5419 */

/* Start YUK-5630 */
/* @start-block */
IF exists (SELECT 1
           FROM  SysObjects
           WHERE  Id = Object_Id('CCCAP_INVENTORY_VIEW')
           AND   type = 'V')
	DROP VIEW [CCCAP_INVENTORY_VIEW];
go
/* @end-block */
/* End YUK-5630 */

/* Start YUK-5395 */
/* @error ignore-begin */
sp_rename 'PointPropertyValue.FloatValue', FltValue;
go
/* @error ignore-end */
/* End YUK-5395 */

/* Start YUK-5875 */
DELETE FROM YukonGroupRole WHERE RolePropertyId = -70001;
DELETE FROM YukonRoleProperty WHERE RolePropertyId = -70001;

/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-70021,-700,'Allow Area Control','true','Enables or disables field and local Area controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70022,-700,'Allow Substation Control','true','Enables or disables field and local Substation controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70023,-700,'Allow SubBus Control','true','Enables or disables field and local Substation Bus controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70024,-700,'Allow Feeder Control','true','Enables or disables field and local Feeder controls for the given user'); 
INSERT INTO YukonRoleProperty VALUES(-70025,-700,'Allow Capbank/CBC Control','true','Enables or disables field and local Capbank/CBC controls for the given user'); 
/* @error ignore-end */
/* End YUK-5875 */

/* Start YUK-5823 */
UPDATE FDRInterface SET PossibleDirections = 'Receive,Send' WHERE InterfaceId = 25;
/* End YUK-5823 */

/* Start Yuk-5900 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.');
/* @error ignore-end */
/* End Yuk-5900 */

/* Start Yuk-5872 */ 
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type) 
SELECT MAX(DeviceGroupID)+1,'Temporary',12,'HIDDEN','STATIC' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
go 
/* End Yuk-5872 */ 

/* Start YUK-5923 */
INSERT INTO YukonRoleProperty VALUES(-10819, -108, 'Default TimeZone',' ','Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)'); 
INSERT INTO YukonRoleProperty VALUES(-1703, -8, 'System Default TimeZone', ' ', 'System Default TimeZone (e.g. America/Denver, America/Chicago, America/Los_Angeles, or America/New_York)'); 
/* End YUK-5923 */

/* Start YUK-5904 */ 
INSERT INTO DeviceGroup (DeviceGroupId,GroupName,ParentDeviceGroupId,Permission,Type) 
SELECT MAX(DeviceGroupID)+1,'Disabled', 
(SELECT DeviceGroupId 
 FROM DeviceGroup 
 WHERE GroupName = 'Meters' 
 AND ParentDeviceGroupId = 12), 
'NOEDIT_NOMOD','METERS_DISABLED' 
FROM DeviceGroup 
WHERE DeviceGroupId < 100; 
/* End YUK-5904 */

/* Start YUK-5269 */
UPDATE YukonRoleProperty 
SET description = 'Defines a Yukon Pao (Device) Name field alias. Valid values(0-4): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location]' 
WHERE rolePropertyId = -1600;
/* End YUK-5269 */

/* Start YUK-5960 */ 
ALTER TABLE State DROP CONSTRAINT SYS_C0013342; 

ALTER TABLE Point DROP CONSTRAINT Ref_STATGRP_PT;
go

UPDATE StateGroup 
SET stateGroupId = -9 
WHERE stateGroupId = 2;
go

UPDATE Point 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 

UPDATE State 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 
go

ALTER TABLE Point 
    ADD CONSTRAINT Ref_STATGRP_PT FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 

ALTER TABLE State 
    ADD CONSTRAINT SYS_C0013342 FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 
go
/* End YUK-5960 */

/* Start YUK-6004 */
/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'dynamicCCCapBank' AND COLUMN_NAME = 'twoWayCBCLastControl')
ALTER TABLE DynamicCCCapBank ADD twoWayCBCLastControl NUMERIC;
go
/* @end-block */

UPDATE DynamicCCCapBank 
SET twoWayCBCLastControl = 0
WHERE twoWayCBCLastControl IS NULL;

ALTER TABLE DynamicCCCapBank ALTER COLUMN twoWayCBCLastControl NUMERIC NOT NULL;
go
/* End YUK-6004 */

/* Start YUK-6006 */
/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'dynamicCCFeeder' AND COLUMN_NAME = 'retryIndex')
ALTER TABLE DynamicCCFeeder ADD retryIndex NUMERIC;
go
/* @end-block */

UPDATE DynamicCCFeeder 
SET retryIndex = 0
WHERE retryIndex IS NULL;

ALTER TABLE DynamicCCFeeder ALTER COLUMN retryIndex NUMERIC NOT NULL;
go
/* End YUK-6006 */

/* Start YUK-5999*/
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-100013, -1000, 'Three Phase', 'false', 'display 3-phase data for sub bus');
/* @error ignore-end */
/* End YUK-5999 */

/* Start YUK-6011 */
UPDATE yukongrouprole 
SET value = '/spring/stars/consumer/general' 
WHERE value='/user/ConsumerStat/stat/General.jsp';
/* End YUK-6011 */

/* Start YUK-6023 */
UPDATE YukonRoleProperty 
SET description = 'Defines the format for displaying devices. Available placeholders: {name},{meterNumber},{id},{address}' 
WHERE rolePropertyId = -1700;
/* End YUK-6023 */

/* Start YUK-6017 */
ALTER TABLE Job ADD themeName VARCHAR(60);
go
UPDATE Job 
SET themeName = ' ' 
WHERE themeName IS NULL;
go
ALTER TABLE Job ALTER COLUMN themeName VARCHAR(60) NOT NULL;
go
/* End YUK-6017 */

/* Start YUK-5932 */
UPDATE UnitMeasure 
SET uomName = 'Ops' 
WHERE uomId = 26;
/* End YUK-5932 */

/* Start YUK-6059 */
INSERT INTO YukonRoleProperty VALUES(-40201,-400,'Opt Out Device Selection','false','Displays a second web page that allows for specific device selection when performing an opt out.'); 
INSERT INTO YukonRoleProperty VALUES(-40202,-400,'Enroll Multiple Programs per Category','false','Enables you to enroll in multiple programs within an appliance category.'); 
INSERT INTO YukonRoleProperty VALUES(-40203,-400,'Enrollment per Device','false','Displays a second web page that allows for enrollment by individual device per program.');
/* End YUK-6059 */

/* Start YUK-5748 */
INSERT INTO Command 
VALUES(-143, 'putconfig xcom extended tier ?''tier'' rate ?''rate'' cmd ?''cmd'' display 3 timeout 600 delay 5432', 'Thermostat Extended Tier Message', 'ExpresscomSerial'); 

INSERT INTO DeviceTypeCommand 
VALUES (-723, -143, 'ExpresscomSerial', 24, 'Y', -1); 

INSERT INTO DeviceTypeCommand 
VALUES (-724, -143, 'Expresscom Group', 24, 'Y', -1); 
/* End YUK-5748 */

/* Start YUK-5757 */ 
INSERT INTO YukonListEntry 
VALUES (1065, 1005, -1, 'UtilityPRO', 1314); 

INSERT INTO YukonListEntry 
VALUES (1224, 1019, 0, 'Circulate', 2504);  
/* End YUK-5757 */

/* Start YUK-6180 */
INSERT INTO YukonRoleProperty 
VALUES (-70026,-700,'Warn on control send.','false','If true the user will be asked if they are sure they want to send that command.'); 
/* End YUK-6180 */

/* Start YUK-6220 */
/* Delete existing Enable TOU and Enable Device Group properties */
DELETE FROM YukonUserRole WHERE RolePropertyId IN (-20204, -20205);
DELETE FROM YukonGroupRole WHERE RolePropertyId IN (-20204, -20205);
DELETE FROM YukonRoleProperty WHERE rolePropertyId IN (-20204, -20205);

/* Update existing Role Properties */
UPDATE YukonRoleProperty SET keyName = 'Profile Collection', description = 'Controls access to submit a (past) profile collection request' WHERE rolePropertyId = -20206; 
UPDATE YukonRoleProperty SET keyName = 'Move In/Move Out Auto Archiving', description = 'Enables automatic archiving of move in/move out transactions' WHERE rolePropertyId = -20207; 
UPDATE YukonRoleProperty SET keyName = 'Move In/Move Out', description = 'Controls access to process a move in/move out' WHERE rolePropertyId = -20208; 

/* Adding new role properties for the Metering Role */ 
INSERT INTO YukonRoleProperty VALUES (-20209,-202,'Profile Collection Scanning','true','Controls access to start/stop scanning of profile data'); 
INSERT INTO YukonRoleProperty VALUES (-20210,-202,'High Bill Complaint','true','Controls access to process a high bill complaint'); 

/* Adding new role Device Actions */
INSERT INTO YukonRole VALUES (-213,'Device Actions','Operator','Operator access to device actions'); 

/* Adding new role properties for the Device Actions Role */
INSERT INTO YukonRoleProperty VALUES (-21300,-213,'Bulk Import Operation','true','Controls access to bulk import operations'); 
INSERT INTO YukonRoleProperty VALUES (-21301,-213,'Bulk Update Operation','true','Controls access to bulk update operations'); 
INSERT INTO YukonRoleProperty VALUES (-21302,-213,'Device Group Edit','true','Controls editing of Device Groups (Add/Remove Group, update name, etc.)'); 
INSERT INTO YukonRoleProperty VALUES (-21303,-213,'Device Group Modify','true','Controls modifying contents of a Device Group (Add to/Remove from group, etc.)'); 
INSERT INTO YukonRoleProperty VALUES (-21304,-213,'Group Commander','true','Controls access to group command actions'); 
INSERT INTO YukonRoleProperty VALUES (-21305,-213,'Mass Change','true','Controls access to mass change collection actions. Includes all Mass Change actions.');
INSERT INTO YukonRoleProperty VALUES (-21306,-213,'Locate Route','true','Controls access to locate route action'); 
INSERT INTO YukonRoleProperty VALUES (-21307,-213,'Mass Delete','true','Controls access to mass delete devices action'); 
/* End YUK-6220 */

/* Start YUK-5870 */
INSERT INTO Command VALUES (-144, 'putconfig emetcon channel 1 ied input 1', 'Configure 430 kW LP Collection', 'MCT-430A'); 

INSERT INTO DeviceTypeCommand VALUES (-725, -144, 'MCT-430A', 30, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-726, -144, 'MCT-430S4', 30, 'N', -1); 
INSERT INTO DeviceTypeCommand VALUES (-727, -144, 'MCT-430SL', 30, 'N', -1); 
/* End YUK-5870 */

/* Start YUK-6093 */
ALTER TABLE DynamicCCCapBank ALTER COLUMN beforeVar VARCHAR(48) NOT NULL; 
ALTER TABLE DynamicCCCapBank ALTER COLUMN afterVar VARCHAR(48) NOT NULL; 
ALTER TABLE DynamicCCCapBank ALTER COLUMN changeVar VARCHAR(48) NOT NULL; 
/* End YUK-6093 */

/* Start YUK-4777 */
if exists (SELECT *
           FROM syscolumns
           WHERE id=object_id('CCurtCurtailmentEvent')
           AND name='CCurtProgramTypeId')
	ALTER TABLE CCurtCurtailmentEvent DROP COLUMN CCurtProgramTypeId;
go
/* End YUK-4777 */

/* Start YUK-6197 */
DELETE FROM YukonUserRole
WHERE RolePropertyId IN (-10913, -10914, -10915, -10916, -10917, 
                         -10918, -10919, -10920, -10921, -10922);

DELETE FROM YukonGroupRole
WHERE RolePropertyId IN (-10913, -10914, -10915, -10916, -10917, 
                         -10918, -10919, -10920, -10921, -10922);

DELETE FROM YukonRoleProperty
WHERE RolePropertyId IN (-10913, -10914, -10915, -10916, -10917, 
                         -10918, -10919, -10920, -10921, -10922);
/* End YUK-6197 */

/* Start YUK-4968 */
DELETE CalcComponent 
WHERE ComponentPointId IN (SELECT DISTINCT pointId 
                           FROM Point P, YukonPAObject PAO 
                           WHERE PAO.paobjectId = P.paobjectId 
                           and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                           and pointOffset = 100 and pointType = 'Analog');

DELETE CalcPointBaseline 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE CustomerBaselinePoint 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog');
 
DELETE CalcBase 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog');

DELETE CICustomerPointData 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog');

DELETE Display2WayData 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog');
 
DELETE DynamicPointDispatch 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE DynamicPointAlarming 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE DynamicTags 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE FDRTranslation 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE GraphDataSeries 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE PointUnit 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE PointAlarming 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE TagLog 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE PointAnalog 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE RawPointHistory 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog'); 

DELETE Point 
WHERE pointId IN (SELECT DISTINCT pointId 
                  FROM Point P, YukonPAObject PAO 
                  WHERE PAO.paobjectId = P.paobjectId 
                  and (pao.type = 'MCT-470' or PAO.type like 'MCT-430%') 
                  and pointOffset = 100 and pointType = 'Analog');
/* End YUK-4968 */

/* Start YUK-6259 */
UPDATE YukonRoleProperty 
SET description = 'Defines a Yukon Pao (Device) Name field alias. Valid values(0-5): [0=Device Name, 1=Account Number, 2=Service Location, 3=Customer, 4=EA Location, 5=Grid Location]' 
WHERE rolePropertyId = -1600;
/* End YUK-6259 */

/* Start YUK-6139 */
if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperations_View') 
           and type = 'V') 
   drop view CCOperations_View 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsESentAndAll_view') 
           and type = 'V') 
   drop view CCOperationsESentAndAll_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsDSentAndValid_view') 
           and type = 'V') 
   drop view CCOperationsDSentAndValid_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsCOrphanedConf_view') 
           and type = 'V') 
   drop view CCOperationsCOrphanedConf_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsBConfirmed_view') 
           and type = 'V') 
   drop view CCOperationsBConfirmed_view 
go 

if exists (select 1 
           from sysobjects 
           where id = object_id('CCOperationsASent_View') 
           and type = 'V') 
   drop view CCOperationsASent_View 
go 

if exists (select 1
            from  sysobjects
           where  id = object_id('CCOperationLogCache')
            and   type = 'U')
   drop table CCOperationLogCache;
go

/* @start-block */
CREATE VIEW CCOperationsASent_View
as
SELECT logid, pointid, datetime, text, feederid, subid, additionalinfo
FROM CCEventLog
WHERE text LIKE '%Close sent,%' OR text LIKE '%Open sent,%'
go
/* @end-block */

/* @start-block */
CREATE VIEW CCOperationsBConfirmed_view
as
SELECT logid, pointid, datetime, text, kvarbefore, kvarafter, kvarchange
FROM CCEventLog
WHERE text LIKE 'Var: %'
go
/* @end-block */

/* @start-block */
CREATE VIEW CCOperationsCOrphanedConf_view
as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL3.NextAId IS NULL
GROUP BY EL.LogId
go
/* @end-block */

/* @start-block */
CREATE VIEW CCOperationsDSentAndValid_view
as
SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
FROM CCOperationsASent_View EL 
JOIN CCOperationsBConfirmed_view EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
LEFT JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
           FROM CCOperationsASent_View A 
           JOIN CCOperationsASent_View B ON A.PointId = B.PointId AND A.LogId < B.LogId 
           GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
WHERE EL2.LogId < EL3.NextAId OR EL3.NextAId IS NULL
GROUP BY EL.LogId
go
/* @end-block */

/* @start-block */
CREATE VIEW CCOperationsESentAndAll_view
as
SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
FROM CCOperationsASent_View OP
LEFT JOIN CCOperationsDSentAndValid_view AAA ON OP.LogId = AAA.OpId
GROUP BY OP.LogId
go
/* @end-block */

CREATE TABLE CCOperationLogCache 
(OperationLogId numeric(18,0) not null,
 ConfirmationLogId numeric(18,0) null,
 constraint PK_CCOperationLogCache primary key (OperationLogId));
go

INSERT INTO CCOperationLogCache
SELECT OpId, ConfId 
FROM CCOperationsDSentAndValid_view
WHERE OpId NOT IN (SELECT OperationLogId 
                   FROM CCOperationLogCache);
go

/* @start-block */
CREATE VIEW CCOperations_View
as 
SELECT YP3.PAObjectid AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectid AS CapBankId, 
       YP.PAOName AS CapBankName, EL.DateTime AS OpTime, EL.Text AS Operation, 
       EL2.DateTime AS ConfTime, EL2.Text AS ConfStatus, YP1.PAOName AS FeederName, 
       YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, 
       YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, CB.BankSize, CB.ControllerType, 
       EL.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       EL2.KvarAfter, EL2.KvarChange, EL2.KvarBefore 
FROM CCOperationsASent_View EL
JOIN CCOperationLogCache OpConf ON EL.LogId = OpConf.OperationLogId        
LEFT JOIN CCOperationsBConfirmed_view EL2 ON EL2.LogId = OpConf.ConfirmationLogId 
LEFT JOIN CCOperationsCOrphanedConf_view Orphs ON EL.logid = Orphs.opid       
JOIN Point ON Point.PointId = EL.PointId        
JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId        
JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId        
JOIN YukonPAObject YP1 ON YP1.PAObjectId = EL.FeederId        
JOIN YukonPAObject YP2 ON YP2.PAObjectId = EL.SubId        
JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId        
LEFT JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId        
LEFT JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId        
JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId        
LEFT JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId        
LEFT JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime                        
           FROM DynamicPAOInfo                         
           WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId        
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = EL.SubId         
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId 
go
/* @end-block */
/* End YUK-6139 */

/* Start YUK-6268 */
UPDATE YukonUserRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)';
go

UPDATE YukonGroupRole 
SET value = 'DEVICE_NAME' 
WHERE rolePropertyId = -1700 
AND value != '(none)';
go

UPDATE YukonRoleProperty 
SET defaultValue = 'DEVICE_NAME', description = 'Defines the format for displaying devices. Available placeholders: DEVICE_NAME, METER_NUMBER, ID, ADDRESS' 
WHERE rolePropertyId = -1700;
go
/* End YUK-6268 */

/* Start YUK-6393 */
EXEC sp_rename 'BaseLine.holidaysUsed', 'HolidayScheduleId', 'COLUMN'; 
go 

alter table BaseLine 
   add constraint FK_BASELINE_HOLIDAYS foreign key (HolidayScheduleId) 
      references HolidaySchedule (HolidayScheduleID) 
go 

alter table MACSchedule 
   add constraint FK_MACSCHED_HOLIDAYS foreign key (HolidayScheduleID) 
      references HolidaySchedule (HolidayScheduleID) 
go 
/* End YUK-6393 */

/* Start YUK-6395 */
CREATE UNIQUE INDEX Indx_YukonGroup_groupName_UNQ ON YukonGroup (
GroupName ASC
)
go
/* End YUK-6395 */

/* Start YUK-6351 */
UPDATE PointUnit
SET PointUnit.UOMID = 8
WHERE PointUnit.UOMID = 55;

DELETE FROM UnitMeasure
WHERE UOMID = 55;
/* End YUK-6351 */

/* Start YUK-6409 */
DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20105;

DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20105;

DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20105;
/* End YUK-6409 */

/* Start YUK-6289, YUK-6549 */
/* Device Actions role */ 
INSERT INTO YukonGroupRole VALUES (-2500, -2, -213, -21300, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2501, -2, -213, -21301, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2502, -2, -213, -21302, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2503, -2, -213, -21303, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2504, -2, -213, -21304, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2505, -2, -213, -21305, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2506, -2, -213, -21306, '(none)'); 
INSERT INTO YukonGroupRole VALUES (-2507, -2, -213, -21307, '(none)'); 
/* End YUK-6289, YUK-6549 */

/* Start YUK-6426 */
/* Versacom */ 
INSERT INTO Command VALUES(-145, 'putconfig vcom lcrmode ?''Enter e|v (for Emetcon or Versacom)''', 'Set LCR3100 Versacom mode', 'VersacomSerial'); 
INSERT INTO Command VALUES(-146, 'putconfig vcom silver ?''Enter a value 1-60''', 'Set LCR3100 Versacom Silver Addressing', 'VersacomSerial'); 
INSERT INTO Command VALUES(-147, 'putconfig vcom gold ?''Enter a value 1-4''', 'Set LCR3100 Versacom Gold Addressing', 'VersacomSerial'); 

/* ExpressCom */ 
INSERT INTO Command VALUES(-148, 'putconfig xcom lcrmode ?''Enter Ex|Em|V|G (For example: ExEmVG or ExV)''', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-149, 'putconfig xcom silver ?''Enter a value 1-60''', 'Set LCR3100 Expresscom Silver Addressing', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-150, 'putconfig xcom gold ?''Enter a value 1-4''', 'Set LCR3100 Expresscom Gold Addressing', 'ExpresscomSerial'); 

INSERT INTO Command VALUES(-151, 'putconfig xcom lcrmode Ex', 'Set LCR3100 Expresscom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-152, 'putconfig xcom lcrmode V', 'Set LCR3100 Versacom mode', 'ExpresscomSerial'); 
INSERT INTO Command VALUES(-153, 'putconfig xcom lcrmode Em', 'Set LCR3100 Emetcon mode', 'ExpresscomSerial'); 

INSERT INTO DEVICETYPECOMMAND VALUES (-728, -148, 'ExpresscomSerial', 25, 'N', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-729, -149, 'ExpresscomSerial', 26, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-730, -150, 'ExpresscomSerial', 27, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-731, -151, 'ExpresscomSerial', 28, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-732, -152, 'ExpresscomSerial', 29, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-733, -153, 'ExpresscomSerial', 30, 'Y', -1); 

INSERT INTO DEVICETYPECOMMAND VALUES (-734, -145, 'VersacomSerial', 24, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-735, -146, 'VersacomSerial', 25, 'Y', -1); 
INSERT INTO DEVICETYPECOMMAND VALUES (-736, -147, 'VersacomSerial', 26, 'Y', -1); 
/* End YUK-6426 */

/* Start YUK-6441 */
DELETE BillingFileFormats 
WHERE formatId < 0 
AND formatType IN (SELECT DISTINCT formatType 
                   FROM BillingFileFormats 
                   WHERE formatId >= 0); 

create unique index Indx_BillFile_FormType_UNQ on BillingFileFormats (
FormatType ASC
);
go
/* End YUK-6441 */

/* Start YUK-6450 */
INSERT INTO BillingFileFormats VALUES( 33, 'NISC Interval Readings', 1); 
/* End YUK-6450 */

/* Start YUK-6586 */
DELETE FROM YukonUserRole WHERE rolePropertyId = -40054; 
DELETE FROM YukonGroupRole WHERE rolePropertyId = -40054;
DELETE FROM YukonRoleProperty WHERE rolePropertyId = -40054; 
/* End YUK-6586 */

/* Start YUK-6241 */
UPDATE Display
SET description = 'This display will receive current events as they happen in the system.'
WHERE displayNum = 1;

UPDATE Display
SET description = 'This display will allow the user to select a range of dates and show the events that occurred.'
WHERE displayNum = 2;

UPDATE Display
SET description = 'This display will receive current raw point updates as they happen in the system.'
WHERE displayNum = 3;

UPDATE Display
SET description = 'This display will receive all alarm events as they happen in the system.'
WHERE displayNum = 4;

UPDATE Display
SET description = 'This display will receive all priority 1 alarm events as they happen in the system.'
WHERE displayNum = 5;

UPDATE Display
SET description = 'This display will receive all priority 2 alarm events as they happen in the system.'
WHERE displayNum = 6;

UPDATE Display
SET description = 'This display will receive all priority 3 alarm events as they happen in the system.'
WHERE displayNum = 7;

UPDATE Display
SET description = 'This display will receive all priority 4 alarm events as they happen in the system.'
WHERE displayNum = 8;

UPDATE Display
SET description = 'This display will receive all priority 5 alarm events as they happen in the system.'
WHERE displayNum = 9;

UPDATE Display
SET description = 'This display will receive all priority 6 alarm events as they happen in the system.'
WHERE displayNum = 10;

UPDATE Display
SET description = 'This display will receive all priority 7 alarm events as they happen in the system.'
WHERE displayNum = 11;

UPDATE Display
SET description = 'This display will receive all priority 8 alarm events as they happen in the system.'
WHERE displayNum = 12;

UPDATE Display
SET description = 'This display will receive all priority 9 alarm events as they happen in the system.'
WHERE displayNum = 13;

UPDATE Display
SET description = 'This display will receive all priority 10 alarm events as they happen in the system.'
WHERE displayNum = 14;

UPDATE Display
SET description = 'This display will receive all priority 11 alarm events as they happen in the system.'
WHERE displayNum = 15;

UPDATE Display
SET description = 'This display will receive all priority 12 alarm events as they happen in the system.'
WHERE displayNum = 16;

UPDATE Display
SET description = 'This display will receive all priority 13 alarm events as they happen in the system.'
WHERE displayNum = 17;

UPDATE Display
SET description = 'This display will receive all priority 14 alarm events as they happen in the system.'
WHERE displayNum = 18;

UPDATE Display
SET description = 'This display will receive all priority 15 alarm events as they happen in the system.'
WHERE displayNum = 19;

UPDATE Display
SET description = 'This display will receive all priority 16 alarm events as they happen in the system.'
WHERE displayNum = 20;

UPDATE Display
SET description = 'This display will receive all priority 17 alarm events as they happen in the system.'
WHERE displayNum = 21;

UPDATE Display
SET description = 'This display will receive all priority 18 alarm events as they happen in the system.'
WHERE displayNum = 22;

UPDATE Display
SET description = 'This display will receive all priority 19 alarm events as they happen in the system.'
WHERE displayNum = 23;

UPDATE Display
SET description = 'This display will receive all priority 20 alarm events as they happen in the system.'
WHERE displayNum = 24;

UPDATE Display
SET description = 'This display will receive all priority 21 alarm events as they happen in the system.'
WHERE displayNum = 25;

UPDATE Display
SET description = 'This display will receive all priority 22 alarm events as they happen in the system.'
WHERE displayNum = 26;

UPDATE Display
SET description = 'This display will receive all priority 23 alarm events as they happen in the system.'
WHERE displayNum = 27;

UPDATE Display
SET description = 'This display will receive all priority 24 alarm events as they happen in the system.'
WHERE displayNum = 28;

UPDATE Display
SET description = 'This display will receive all priority 25 alarm events as they happen in the system.'
WHERE displayNum = 29;

UPDATE Display
SET description = 'This display will receive all priority 26 alarm events as they happen in the system.'
WHERE displayNum = 30;

UPDATE Display
SET description = 'This display will receive all priority 27 alarm events as they happen in the system.'
WHERE displayNum = 31;

UPDATE Display
SET description = 'This display will receive all priority 28 alarm events as they happen in the system.'
WHERE displayNum = 32;

UPDATE Display
SET description = 'This display will receive all priority 29 alarm events as they happen in the system.'
WHERE displayNum = 33;

UPDATE Display
SET description = 'This display will receive all priority 30 alarm events as they happen in the system.'
WHERE displayNum = 34;

UPDATE Display
SET description = 'This display will receive all priority 31 alarm events as they happen in the system.'
WHERE displayNum = 35;

UPDATE Display
SET description = 'This display is used to show what a user created display looks like. You may edit this display to fit your own needs.'
WHERE displayNum = 99;
/* End YUK-6241 */

/* Start YUK-6240 */
UPDATE Command
SET label = 'Read freeze config from meter and enable scheduled freeze processing in Yukon.'
WHERE commandId = -142;
/* End YUK-6240 */

/* Start YUK-6628 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20010,-200,'Auto Process Batch Configs','false','Automatically process batch configs using the DailyTimerTask.');
/* @error ignore-end */
/* End YUK-6628 */

/* Start YUK-6622 */
UPDATE Command
SET command = 'putconfig xcom data ''?''Text Message'''' port ?''Port (0 is default)'' deletable msgpriority 7 timeout 30 hour clear'
WHERE commandId = -134;
/* End YUK-6622 */

/**************************************************************/
/* VERSION INFO                                               */
/**************************************************************/

insert into CTIDatabase values('4.1', 'Matt K', '31-OCT-2008', 'Latest Update', 0 );