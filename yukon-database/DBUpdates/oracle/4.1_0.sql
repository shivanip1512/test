/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* @error ignore-begin */
set define off;
/* @error ignore-end */

/* Start YUK-5103 */
/* @error ignore-begin */
insert into pointalarming(pointid, alarmstates, excludenotifystates, notifyonacknowledge, notificationgroupid, recipientid)
	select -110,
	'',
	'NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN',
	'N',
	1, 0  from dual;
/* @error ignore-end */ 
/* End YUK-5103*/

/* Start YUK-5123 */
ALTER TABLE DEVICEROUTES DROP CONSTRAINT PK_DEVICEROUTES;
commit;
/* @error ignore-begin */
drop index PK_DEVICEROUTES;
commit;
/* @error ignore-end */
ALTER TABLE DEVICEROUTES
 ADD CONSTRAINT PK_DEVICEROUTES
 PRIMARY KEY
 (DEVICEID);
commit;
/* End YUK-5123 */

/* Start YUK-5119 */
insert into YukonRoleProperty values(-10817, -108,'Theme Name',' ','The name of the theme to be applied to this group');
/* End YUK-5119 */

/* Start YUK-5204 */
alter table Job add Locale varchar2(10);
update Job set Locale = 'en_US';
alter table Job modify Locale varchar2(10) not null;

alter table Job add TimeZone varchar2(40);
update Job set TimeZone = ' ';
alter table Job modify TimeZone varchar2(40) not null;
/* End YUK-5204 */

/* Start YUK-5350 */
/* @error ignore-begin */
insert into YukonRoleProperty values(-1021,-1,'importer_communications_enabled','true','Specifies whether communications will be allowed by the bulk importer.'); 
/* @error ignore-end */
/* End YUK-5350 */

/* Start YUK-5337 */
INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Scanning Meters',12,'Y','STATIC' FROM DeviceGroup WHERE DeviceGroupId<100; 

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Load Profile',0,'Y','SCANNING_LOAD_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Voltage Profile',0,'Y','SCANNING_VOLTAGE_PROFILE' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Integrity',0,'Y','SCANNING_INTEGRITY' FROM DeviceGroup WHERE DeviceGroupId<100;

INSERT INTO DeviceGroup
(DeviceGroupId,GroupName,ParentDeviceGroupId,SystemGroup,Type)
SELECT MAX(DeviceGroupID)+1,'Accumulator',0,'Y','SCANNING_ACCUMULATOR' FROM DeviceGroup WHERE DeviceGroupId<100;

UPDATE DeviceGroup
SET ParentDeviceGroupId = (SELECT DeviceGroupId FROM DeviceGroup WHERE GroupName='Scanning Meters')
WHERE Type IN ('SCANNING_LOAD_PROFILE','SCANNING_VOLTAGE_PROFILE','SCANNING_INTEGRITY','SCANNING_ACCUMULATOR');

ALTER TABLE DEVICEGROUP
RENAME COLUMN SYSTEMGROUP TO Permission;

ALTER TABLE DEVICEGROUP
MODIFY(Permission NVARCHAR2(50));

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type = 'STATIC'
AND GroupName IN ('Scanning Meters');

UPDATE DeviceGroup
SET Permission = 'NOEDIT_MOD'
WHERE Permission = 'Y'
AND Type = 'STATIC';

UPDATE DeviceGroup
SET Permission = 'NOEDIT_NOMOD'
WHERE Permission = 'Y'
AND Type != 'STATIC';

UPDATE DeviceGroup
SET Permission = 'EDIT_MOD'
WHERE Permission = 'N';

update devicegroup
set Permission = 'NOEDIT_NOMOD'
where
    GroupName = 'System'
    and ParentDeviceGroupID = 0
    and Type = 'STATIC';
/* End YUK-5337 */

/* Start YUK-5454 */
ALTER TABLE DYNAMICBILLINGFIELD ADD PadChar char(1);
UPDATE DYNAMICBILLINGFIELD SET PadChar = ' ';
ALTER TABLE DYNAMICBILLINGFIELD modify PadChar char(1) not null;

ALTER TABLE DYNAMICBILLINGFIELD ADD PadSide varchar(5);
UPDATE DYNAMICBILLINGFIELD SET PadSide = 'none';
ALTER TABLE DYNAMICBILLINGFIELD modify PadSide varchar(5) not null;

ALTER TABLE DYNAMICBILLINGFIELD ADD ReadingType varchar(12);
UPDATE DYNAMICBILLINGFIELD SET ReadingType = 'ELECTRIC';
ALTER TABLE DYNAMICBILLINGFIELD modify ReadingType varchar(12) not null;
/* End YUK-5454 */

/* Start YUK-5519 */
ALTER TABLE CAPBANK
MODIFY(MAPLOCATIONID VARCHAR2(64));

ALTER TABLE CapControlFeeder
MODIFY(MAPLOCATIONID VARCHAR2(64));

ALTER TABLE CapControlSubstationBus
MODIFY(MAPLOCATIONID VARCHAR2(64));
/* End YUK-5519 */

/* Start YUK-5579 */
INSERT INTO BillingFileFormats VALUES(-32, 'NISC TOU (kVarH) Rates Only', 1);
/* End YUK-5579 */

/* Start YUK-5663 */ 
ALTER TABLE JOBSCHEDULEDREPEATING MODIFY CronString VARCHAR2(100) not null; 

ALTER TABLE JOBPROPERTY MODIFY name VARCHAR2(100) not null; 

ALTER TABLE JOBPROPERTY MODIFY value VARCHAR2(1000) not null; 

ALTER TABLE JOBSTATUS MODIFY Message VARCHAR2(1000) not null; 
/* End Oracle YUK-5663 */ 

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
/*==============================================================*/
/* View: AreaSubBusFeeder_View                                  */
/*==============================================================*/
create or replace view AreaSubBusFeeder_View as
SELECT YPA.PAOName AS Region, YPS.PAOName AS Substation, YP.PAOName AS Subbus, YPF.PAOName AS Feeder, 
       STRAT.StrategyName, STRAT.ControlMethod, SA.SeasonName, 
       cast(DOS.SeasonStartMonth AS VARCHAR(2)) + '/' + cast(DOS.SeasonStartDay AS VARCHAR(2)) + '/' + 
       cast(to_char(CURRENT_DATE, 'YYYY') AS VARCHAR(4)) AS SeasonStartDate, 
       cast(DOS.SeasonEndMonth AS VARCHAR(2)) + '/'  + cast(DOS.SeasonEndDay AS VARCHAR(2)) + '/' + 
       cast(to_char(CURRENT_DATE, 'YYYY') AS VARCHAR(4)) AS SeasonEndDate
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
AND TO_DATE((cast(SeasonStartMonth AS VARCHAR(2)) + '-' + 
     cast(SeasonStartDay AS VARCHAR(2)) + '-' + 
     cast(to_char(CURRENT_DATE, 'YYYY') AS VARCHAR(4))),'MON-DD-YYYY') <= CURRENT_DATE
AND TO_DATE((cast(SeasonEndMonth AS VARCHAR(2)) + '-' + 
     cast(SeasonEndDay AS VARCHAR(2)) + '-' + 
     cast(to_char(CURRENT_DATE, 'YYYY') AS VARCHAR(4))),'MON-DD-YYYY') > CURRENT_DATE;

/*==============================================================*/
/* View: CBCConfiguration2_View                                 */
/*==============================================================*/
create or replace view CBCConfiguration2_View as
SELECT YP.PAOName AS CBCName, D.* 
FROM DynamicCCTwoWayCBC D, YukonPAObject YP
WHERE YP.PAObjectId = D.DeviceId;

/*==============================================================*/
/* View: CBCConfiguration_View                                  */
/*==============================================================*/
create or replace view CBCConfiguration_View as
SELECT YP.PAOName AS CBCName, YP.PAObjectId AS CBCId, P.PointName AS PointName, P.PointId AS PointId, 
       PD.Value AS PointValue, PD.Timestamp, UOM.UOMName AS UnitOfMeasure
FROM Point P
JOIN YukonPAObject YP ON YP.PAObjectId = P.PAObjectId AND YP.Type like 'CBC 702%'
LEFT OUTER JOIN DynamicPointDispatch PD ON PD.PointId = P.PointId
LEFT OUTER JOIN PointUnit PU ON PU.PointId = P.PointId
LEFT OUTER JOIN UnitMeasure UOM ON UOM.UOMId = PU.UOMId;

/*==============================================================*/
/* View: CCCBCCVMSState_View                                    */
/*==============================================================*/
create or replace view CCCBCCVMSState_View as
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
LEFT OUTER JOIN State S1 ON S1.StateGroupId = 3 AND DCB.TwoWayCBCState = S1.RawState;

/*==============================================================*/
/* View: CCCBCInventory_View                                    */
/*==============================================================*/
create or replace view CCCBCInventory_View(CBCNAME, IPADDRESS, SLAVEADDRESS, CONTROLLERTYPE, OPCENTER, REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, CAPBANKNAME, BANKSIZE, OPERATIONMETHOD, LAT, LON, DRIVEDIRECTION, CAPBANKADDRESS, TA, CAPBANKCONFIG, COMMMEDIUM, COMMSTRENGTH, EXTERNALANTENNA, OPERATIONSCOUNTERRESETDATE, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
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
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;

/*==============================================================*/
/* View: CCCapInventory_View                                    */
/*==============================================================*/
create or replace view CCCapInventory_View as
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
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId;

/*==============================================================*/
/* View: CCInventory_View                                       */
/*==============================================================*/
create or replace view CCInventory_View(REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, AREAID, SUBID, SUBBUSID, FDRID, CBCNAME, CBCID, CAPBANKNAME, BANKID, CAPBANKSIZE, DISPLAYORDER, CONTROLSTATUS, CONTROLSTATUSNAME, SWMFGR, SWTYPE, OPERATIONMETHOD, CONTROLLERTYPE, IPADDRESS, SLAVEADDRESS, LAT, LON, DRIVEDIRECTION, OPCENTER, TA, CLOSESEQUENCE, OPENSEQUENCE, LASTOPERATIONTIME, LASTINSPECTIONDATE, LASTMAINTENANCEDATE, MAINTENANCEREQPEND, CAPDISABLED, POTENTIALTRANSFORMER, OTHERCOMMENTS, OPTEAMCOMMENTS, POLENUMBER, OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) as
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
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;

/*==============================================================*/
/* View: CCOperations_View                                      */
/*==============================================================*/
create or replace view CCOperations_View as
SELECT YP3.PAOName AS CBCName, YP.PAOName AS CapBankName, EL.DateTime AS OpTime, 
       EL.Text AS Operation, EL2.DateTime AS ConfTime, EL2.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, 
       YP2.PAObjectId AS SubBusId, YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, 
       YP4.PAOName AS Region, YP4.PAObjectId AS AreaId, CB.BankSize, CB.ControllerType, 
       EL.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       EL2.KvarAfter, EL2.KvarChange, EL2.KvarBefore
FROM (SELECT OP.LogId AS OId, MIN(aaa.confid) AS CId 
      FROM (SELECT LogId, PointId 
            FROM CCEventLog 
            WHERE Text LIKE '%Close sent%' OR Text LIKE '%Open sent%') OP
      LEFT OUTER JOIN (SELECT EL.LogId AS OpId, MIN(el2.LogID) AS ConfId 
                       FROM CCEventLog EL 
                       INNER JOIN CCEventLog EL2 ON EL2.PointId = EL.PointId AND EL.LogId < EL2.LogId 
                       LEFT OUTER JOIN (SELECT A.LogId AS AId, MIN(b.LogID) AS NextAId 
                                        FROM CCEventLog A 
                                        INNER JOIN CCEventLog B ON A.PointId = B.PointId AND A.LogId < B.LogId 
                                        WHERE (A.Text LIKE '%Close sent,%' OR A.Text LIKE '%Open sent,%') 
                                        AND (B.Text LIKE '%Close sent,%' OR B.Text LIKE '%Open sent,%')
                                        GROUP BY A.LogId) EL3 ON EL3.AId = EL.LogId 
                       WHERE (EL.Text LIKE '%Close sent,%' OR EL.Text LIKE '%Open sent,%') 
                       AND (EL2.Text LIKE 'Var: %') AND (EL2.LogId < EL3.NextAId) 
                       OR (EL.Text LIKE '%Close sent,%' OR EL.Text LIKE '%Open sent,%') 
                       AND (EL2.Text LIKE 'Var: %') AND (EL3.NextAId IS NULL)
                       GROUP BY EL.LogId) AAA ON OP.LogId = AAA.OpId
      GROUP BY OP.LogId) OpConf 
      INNER JOIN CCEventLog EL ON EL.LogId = OpConf.OId 
      LEFT OUTER JOIN CCEventLog EL2 ON EL2.LogId = OpConf.CId 
      INNER JOIN Point ON Point.PointId = EL.PointId 
      INNER JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId 
      INNER JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId 
      INNER JOIN YukonPAObject YP1 ON YP1.PAObjectId = EL.FeederId 
      INNER JOIN YukonPAObject YP2 ON YP2.PAObjectId = EL.SubId 
      INNER JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId 
      LEFT OUTER JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId 
      LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
      INNER JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId 
      LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
      LEFT OUTER JOIN (SELECT EntryId, PAObjectId, Owner, InfoKey, Value, UpdateTime
                       FROM DynamicPAOInfo 
                       WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId 
      LEFT OUTER JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = EL.SubId  
      LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId 
      LEFT OUTER JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId 
      LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;

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
create or replace view TempMovedCapBanks_View as
SELECT YPF.PAOName TempFeederName, YPF.PAObjectId TempFeederId, YPC.PAOName CapBankName, 
       YPC.PAObjectId CapBankId, FB.ControlOrder, FB.CloseOrder, FB.TripOrder, 
       YPOF.PAOName OriginalFeederName, YPOF.PAObjectId OriginalFeederId 
FROM CCFeederBankList FB, YukonPAObject YPF, YukonPAObject YPC, YukonPAObject YPOF, DynamicCCCapBank DC 
WHERE FB.DeviceId = DC.CapBankId 
AND YPC.PAObjectId = DC.CapBankId 
AND FB.FeederId = YPF.PAObjectId 
AND YPOF.PAObjectId = DC.OriginalFeederId 
AND DC.OriginalFeederId <> 0;
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
ALTER TABLE LMControlHistory ADD ProtocolPriority number(9,0);
UPDATE LMControlHistory SET ProtocolPriority = 0;
UPDATE LMControlhistory SET ProtocolPriority = 3 
WHERE PAObjectId IN (SELECT LMGroupId 
                     FROM LMGroupExpressCom);
ALTER TABLE LMControlHistory MODIFY ProtocolPriority number(9,0) NOT NULL;

/* Adding ProtocolPriority column to DynamicLMControlHistory */
ALTER TABLE DynamicLMControlHistory ADD ProtocolPriority number(9,0);
UPDATE DynamicLMControlHistory SET ProtocolPriority = 0;
UPDATE DynamicLMControlHistory SET ProtocolPriority = 3 
WHERE PAObjectId IN (SELECT LMGroupID 
                     FROM LMGroupExpressCom);
ALTER TABLE DynamicLMControlHistory MODIFY ProtocolPriority number(9,0) NOT NULL;

/* Adding ProtocolPriority column to LMGroupExpressCom */
ALTER TABLE LMGroupExpressCom ADD ProtocolPriority number(9,0);
UPDATE LMGroupExpressCom SET ProtocolPriority = 3;
ALTER TABLE LMGroupExpressCom MODIFY ProtocolPriority number(9,0) NOT NULL;

/* Adding ProtocolPriority column to the ExpressComAddress_View */
create or replace view ExpressComAddress_View as
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
      (Z.AddressType = 'ZIP' OR Z.AddressId = 0));
/* End YUK-5759 */

/* Start YUK-5419 */
UPDATE YukonRole SET RoleDescription = 'Administrator privileges.' 
WHERE RoleDescription = 'Administrator privilages.' 
/* End YUK-5419 */

/* Start YUK-5630 */
/* @error ignore-begin */
DROP VIEW CCCap_Inventory_View;
/* @error ignore-end */
/* End YUK-5630 */

/* Start YUK-5395 */
/* @error ignore-begin */
ALTER TABLE PointPropertyValue rename column FloatValue to FltValue;
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
INSERT INTO DeviceGroup (DeviceGroupId, GroupName, ParentDeviceGroupId, Permission, Type) VALUES (20,'Temporary',12,'HIDDEN','STATIC');
/* End Yuk-5872 */

/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

/* __YUKON_VERSION__ */
