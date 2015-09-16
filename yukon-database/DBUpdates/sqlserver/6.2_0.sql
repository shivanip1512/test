/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12914 */
/* @error ignore-begin */
sp_rename 'RfnBroadcastEvent.EventSendTime', 'EventSentTime', 'COLUMN';
/* @error ignore-end */
/* End YUK-12914 */

/* Start YUK-12988 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10911;

DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10911;
/* End YUK-12988 */

/* Start YUK-12523 */
/* @error ignore-begin */
DROP TABLE YukonUserGroup_old;
DROP TABLE YukonUserRole;
/* @error ignore-end */
/* End YUK-12523 */

/* Start YUK-13117 */
UPDATE DeviceConfigCategory
SET CategoryType = 'mctDisconnectConfiguration'
WHERE CategoryType = 'disconnectConfiguration';

UPDATE DeviceConfigCategoryItem 
SET ItemValue = 'ARM' 
WHERE ItemValue = 'true' AND ItemName = 'reconnectButton';

UPDATE DeviceConfigCategoryItem 
SET ItemValue = 'IMMEDIATE' 
WHERE ItemValue = 'false' AND ItemName = 'reconnectButton';

UPDATE DeviceConfigCategoryItem 
SET ItemName = 'reconnectParam' 
WHERE ItemName = 'reconnectButton';
/* End YUK-13117 */

/* Start YUK-13120 */
DELETE FROM CALCCOMPONENT WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CalcPointBaseline WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CustomerBaseLinePoint WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCalcHistorical WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CALCBASE WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CALCCOMPONENT WHERE COMPONENTPOINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPBANK WHERE CONTROLPOINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE SwitchPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentVoltLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentWattLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentVarLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CCEventLog WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CCMonitorBankList WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CICUSTOMERPOINTDATA WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentVarLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentVoltLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentWattLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DYNAMICACCUMULATOR WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCCMonitorBankHistory WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCCMonitorPointResponse WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicPointAlarming WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicTags WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM EstimatedLoadFunction WHERE InputPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM EstimatedLoadLookupTable WHERE InputPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM ExtraPaoPointAssignment WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM FDRTranslation WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM GRAPHDATASERIES WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMControlAreaTrigger WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMControlAreaTrigger WHERE PeakPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMGroupPoint WHERE PointIDUsage IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PAOExclusion WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTACCUMULATOR WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTANALOG WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTLIMITS WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTPROPERTYVALUE WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTSTATUS WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE TriggerID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE VerificationID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTUNIT WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointAlarming WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointStatusControl WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointControl WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointToZoneMapping WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM TagLog WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM Point WHERE PointId IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointType = 'Analog' AND PointOffset = 14 AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));
/* End YUK-13120 */

/* Start YUK-13141 */
ALTER TABLE ContactNotification
DROP CONSTRAINT FK_CntNot_YkLs;

DELETE FROM YukonListEntry 
WHERE ListId IN (SELECT ListId 
                 FROM YukonSelectionList 
                 WHERE ListName = 'ContactType');

DELETE FROM YukonSelectionList 
WHERE ListName = 'ContactType';
/* End YUK-13141 */

/* Start YUK-13065 */
UPDATE UnitMeasure 
SET UOMName = 'VoltsV2H' 
WHERE UOMID = 41;

UPDATE UnitMeasure 
SET UOMName = 'AmpsA2H' 
WHERE UOMID = 42;
/* End YUK-13065 */

/* Start YUK-13105 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -70004;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -70004;
/* End YUK-13105 */

/* Start YUK-13159 */
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER (ORDER BY DeviceConfigCategoryID) 
           + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
       DeviceConfigCategoryID,
       'enableTou',
       'false'
FROM DeviceConfigCategory 
WHERE CategoryType = 'tou';
/* End YUK-13159 */

/* Start YUK-13174 */
ALTER TABLE DynamicPaoInfo
DROP CONSTRAINT PK_DYNPAOINFO;

ALTER TABLE DynamicPaoInfo 
DROP CONSTRAINT AK_DYNPAO_OWNKYUQ;

ALTER TABLE DynamicPaoInfo 
DROP COLUMN EntryID;

ALTER TABLE DynamicPaoInfo 
ADD CONSTRAINT PK_DynamicPaoInfo PRIMARY KEY (PAObjectID, Owner, InfoKey);

DROP VIEW CCCBCInventory_View;
DROP VIEW CCCapInventory_View;
DROP VIEW CCInventory_View;
DROP VIEW CCOperations_View;
GO

CREATE VIEW CCCBCInventory_View (CBCNAME, IPADDRESS, SLAVEADDRESS, CONTROLLERTYPE, OPCENTER, REGION, SUBSTATIONNAME, 
                                 SUBBUSNAME, FEEDERNAME, CAPBANKNAME, BANKSIZE, OPERATIONMETHOD, LAT, LON,
                           DRIVEDIRECTION, CAPBANKADDRESS, TA, CAPBANKCONFIG, COMMMEDIUM, COMMSTRENGTH,
                           EXTERNALANTENNA, OPERATIONSCOUNTERRESETDATE, OPSCOUNTERSINCELASTRESET,
                           OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, OVOPERATIONSCOUNTER, 
                           UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) AS
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
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId 
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;
GO

CREATE VIEW CCCapInventory_View AS
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
LEFT OUTER JOIN CCSubStationSubbusList SS ON SS.SubstationBusId = YP3.PAObjectId
LEFT OUTER JOIN YukonPAObject YP5 ON YP5.PAObjectId = SS.SubStationId 
LEFT OUTER JOIN CCSubAreaAssignment SA ON SS.SubstationId = SA.SubstationBusId
LEFT OUTER JOIN YukonPAObject YP4 ON YP4.PAObjectId = SA.AreaId
LEFT OUTER JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime 
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId;
GO

CREATE VIEW CCInventory_View (REGION, SUBSTATIONNAME, SUBBUSNAME, FEEDERNAME, AREAID, SUBID, SUBBUSID, FDRID, CBCNAME,
                              CBCID, CAPBANKNAME, BANKID, CAPBANKSIZE, DISPLAYORDER, CONTROLSTATUS, CONTROLSTATUSNAME,
                        SWMFGR, SWTYPE, OPERATIONMETHOD, CONTROLLERTYPE, IPADDRESS, SLAVEADDRESS, LAT, LON,
                        DRIVEDIRECTION, OPCENTER, TA, CLOSESEQUENCE, OPENSEQUENCE, LASTOPERATIONTIME,
                        LASTINSPECTIONDATE, LASTMAINTENANCEDATE, MAINTENANCEREQPEND, CAPDISABLED,
                        POTENTIALTRANSFORMER, OTHERCOMMENTS, OPTEAMCOMMENTS, POLENUMBER,
                        OPSCOUNTERSINCELASTRESET, OPERATIONSCOUNTERTODAY, UVOPERATIONSCOUNTER, 
                        OVOPERATIONSCOUNTER, UVOVCOUNTERRESETDATE, LASTOVUVDATETIME) AS
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
       CAPA.MaintenanceReqPend, YP1.DisableFlag AS CapDisabled, CAPA.PotentialTransformer, 
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
LEFT OUTER JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime
                 FROM DynamicPAOInfo 
                 WHERE (InfoKey LIKE '%udp ip%')) DPI ON DPI.PAObjectId = YP.PAObjectId 
LEFT OUTER JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId 
LEFT OUTER JOIN CapBankAdditional CAPA ON CAPA.DeviceId = CB.DeviceId
LEFT OUTER JOIN DynamicCCTwoWayCBC DTWC ON CB.ControlDeviceId = DTWC.DeviceId;
GO

CREATE VIEW CCOperations_View AS
SELECT YP3.PAObjectId AS CBCId, YP3.PAOName AS CBCName, YP.PAObjectId AS CapBankId, YP.PAOName AS CapBankName, 
       CCOAS.PointId, CCOAS.LogId AS OpLogId, CCOAS.ActionId, CCOAS.DateTime AS OpTime, CCOAS.Text AS Operation, 
       CCOBC.LogId AS ConfLogId, CCOBC.ActionId AS ActionId2, CCOBC.DateTime AS ConfTime, CCOBC.Text AS ConfStatus, 
       YP1.PAOName AS FeederName, YP1.PAObjectId AS FeederId, YP2.PAOName AS SubBusName, YP2.PAObjectId AS SubBusId, 
       YP5.PAOName AS SubstationName, YP5.PAObjectId AS SubstationId, YP4.PAOName AS Region, YP4.PAObjectId AS AreaId,
       CB.BankSize, CB.ControllerType, CCOAS.AdditionalInfo AS IPAddress, CBC.SerialNumber AS SerialNum, DA.SlaveAddress, 
       CCOBC.KvarAfter, CCOBC.KvarChange, CCOBC.KvarBefore
FROM CCOperationsASent_View CCOAS
JOIN CCOperationsBConfirmed_view CCOBC ON CCOBC.ActionId = CCOAS.ActionId 
    AND CCOBC.PointId = CCOAS.PointId 
    AND CCOAS.ActionId >= 0
    AND CCOBC.ActionId >= 0
JOIN Point ON Point.PointId = CCOAS.PointId        
JOIN DynamicCCCapBank ON DynamicCCCapBank.CapBankId = Point.PAObjectId        
JOIN YukonPAObject YP ON YP.PAObjectId = DynamicCCCapBank.CapBankId        
JOIN YukonPAObject YP1 ON YP1.PAObjectId = CCOAS.FeederId        
JOIN YukonPAObject YP2 ON YP2.PAObjectId = CCOAS.SubId        
JOIN CapBank CB ON CB.DeviceId = DynamicCCCapBank.CapBankId        
LEFT JOIN DeviceDirectCommSettings DDCS ON DDCS.DeviceId = CB.ControlDeviceId        
LEFT JOIN DeviceAddress DA ON DA.DeviceId = CB.ControlDeviceId        
JOIN YukonPAObject YP3 ON YP3.PAObjectId = CB.ControlDeviceId        
LEFT JOIN DeviceCBC CBC ON CBC.DeviceId = CB.ControlDeviceId        
LEFT JOIN (SELECT PAObjectId, Owner, InfoKey, Value, UpdateTime                        
           FROM DynamicPAOInfo                         
           WHERE (InfoKey LIKE '%udp ip%')) P ON P.PAObjectId = CB.ControlDeviceId        
LEFT JOIN CCSubstationSubbusList SSL ON SSL.SubstationBusId = CCOAS.SubId         
LEFT JOIN YukonPAObject YP5 ON YP5.PAObjectId =  SSL.SubstationBusId        
LEFT JOIN CCSubAreaAssignment CSA ON CSA.SubstationBusId = SSL.SubstationId        
LEFT JOIN YukonPAObject YP4 ON YP4.PAObjectId = CSA.AreaId;
GO
/* End YUK-13174 */

/* Start YUK-12526 */
/* @error ignore-begin */
ALTER TABLE DeviceConfigCategory
   ADD CONSTRAINT AK_DeviceConfigCategory_Name UNIQUE (Name);
GO

sp_refreshview 'CCInventory_View';
GO
sp_refreshview 'CCOperations_View';
GO
sp_refreshview 'CCCapInventory_View';
GO
sp_refreshview 'CCCBCInventory_View';
GO

ALTER TABLE DeviceGroup 
ALTER COLUMN Permission NVARCHAR(50) NOT NULL;

ALTER TABLE JobStatus 
ALTER COLUMN Message VARCHAR(1000) NULL;

ALTER TABLE ThermostatEventHistory 
ALTER COLUMN ManualHeatTemp FLOAT;

ALTER TABLE DeviceTNPPSettings
ALTER COLUMN PagerID VARCHAR(10) NOT NULL;
/* @error ignore-end */
/* End YUK-12526 */

/* Start YUK-13182 */
ALTER TABLE CCEventLog
ADD EventSubtype INT NULL;
GO

UPDATE CCEventLog
SET EventSubtype = (
    CASE
        WHEN (LOWER(Text) LIKE 'manual confirm open sent, %' OR LOWER(Text) LIKE 'manual confirm close sent, %') THEN 4
        WHEN (LOWER(Text) LIKE 'manual flip sent, %') THEN 3
        WHEN (LOWER(Text) LIKE 'manual open sent, %' OR LOWER(Text) LIKE 'manual close sent, %') THEN 2
        WHEN (LOWER(Text) LIKE 'flip sent, %') THEN 1
        WHEN (LOWER(Text) LIKE 'open sent, %' OR LOWER(Text) LIKE 'close sent, %') THEN 0
    END)
WHERE EventType = 1;

CREATE INDEX Indx_CCEventLog_Type_Date_Sub
ON CCEventLog (EventType, DateTime, EventSubtype, PointID);
/* End YUK-13182 */

/* Start YUK-13225 */
/* @error ignore-begin */
ALTER TABLE EstimatedLoadFormula
    ADD CONSTRAINT AK_EstimatedLoadFormula_Name UNIQUE (Name);
/* @error ignore-end */
/* End YUK-13225 */

/* Start YUK-13217 */
ALTER TABLE ArchiveValuesExportFormat
ADD ExcludeAbnormal CHAR(1);
GO

UPDATE ArchiveValuesExportFormat
SET ExcludeAbnormal = '0';

ALTER TABLE ArchiveValuesExportFormat
ALTER COLUMN ExcludeAbnormal CHAR(1) NOT NULL;
/* End YUK-13217 */

/* Start YUK-13216 */
CREATE TABLE PaoLocation (
   PAObjectId           NUMERIC              NOT NULL,
   Latitude             DECIMAL(9, 6)        NOT NULL,
   Longitude            DECIMAL(9, 6)        NOT NULL,
   CONSTRAINT PK_PaoLocation PRIMARY KEY (PAObjectId)
);
GO
 
ALTER TABLE PaoLocation 
   ADD CONSTRAINT FK_PaoLocation_YukonPAObject FOREIGN KEY (PAObjectId)
      REFERENCES YukonPAObject (PAObjectId)
         ON DELETE CASCADE;
GO
/* End YUK-13216 */

/* Start YUK-13232 */
UPDATE DeviceConfigCategoryItem
SET ItemName = 'enableUnsolicitedMessagesClass1'
WHERE DeviceConfigCategoryItemId IN (
    SELECT I.DeviceConfigCategoryItemId
    FROM DeviceConfigCategory C 
    JOIN DeviceConfigCategoryItem I ON C.DeviceConfigCategoryId = I.DeviceConfigCategoryId
    WHERE C.CategoryType = 'dnp'
       AND ItemName = 'enableUnsolicitedMessages');
 
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER(ORDER BY I.DeviceConfigCategoryID) + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
    I.DeviceConfigCategoryId,
    'enableUnsolicitedMessagesClass2',
    I.ItemValue
FROM DeviceConfigCategory C 
JOIN DeviceConfigCategoryItem I ON C.DeviceConfigCategoryId = I.DeviceConfigCategoryId
WHERE C.CategoryType = 'dnp'
  AND I.ItemName = 'enableUnsolicitedMessagesClass1';
 
INSERT INTO DeviceConfigCategoryItem
SELECT ROW_NUMBER() OVER(ORDER BY I.DeviceConfigCategoryID) + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
    I.DeviceConfigCategoryId,
    'enableUnsolicitedMessagesClass3',
    I.ItemValue
FROM DeviceConfigCategory C JOIN DeviceConfigCategoryItem I ON C.DeviceConfigCategoryId = I.DeviceConfigCategoryId
WHERE C.CategoryType = 'dnp'
  AND I.ItemName = 'enableUnsolicitedMessagesClass1';
/* End YUK-13232 */

/* Start YUK-13259 */
UPDATE YukonRole 
SET RoleName = 'Demand Response', 
    Category = 'Demand Response', 
    RoleDescription = 'Operator access to Demand Response'
WHERE RoleId = -900;

DELETE FROM YukonGroupRole 
WHERE RoleId = -900 
  AND RolePropertyId IN (-90001, -90002, -90003, -90020, -90022, -90023, -90025, -90026, -90027, -90028, 
                         -90029, -90030, -90031, -90033, -90034, -90035, -90036, -90037, -90038);
DELETE FROM YukonRoleProperty 
WHERE RoleId = -900 
  AND RolePropertyId IN (-90001, -90002, -90003, -90020, -90022, -90023, -90025, -90026, -90027, -90028, 
                         -90029, -90030, -90031, -90033, -90034, -90035, -90036, -90037, -90038);
UPDATE YukonRoleProperty 
SET KeyName = 'Control Area Trigger Info', 
    Description = 'Controls access to view Control Area Trigger realted information.'
WHERE RoleId = -900 AND RolePropertyID = -90021;

UPDATE YukonRoleProperty 
SET KeyName = 'Priority', 
    Description = 'Controls access to view Control Area, Program, and Group Priority.'
WHERE RoleId = -900 AND RolePropertyID = -90024;

UPDATE YukonRoleProperty 
SET KeyName = 'Reduction', 
    Description = 'Controls access to view Program and Group Reduction.'
WHERE RoleId = -900 AND RolePropertyID = -90032;

INSERT INTO YukonRoleProperty 
VALUES (-90045, -900, 'Allow Load Group Control','true',
        'In addition to Allow DR Control, controls access to shed and restore at group level');
/* End YUK-13259 */

/* Start YUK-13218 */
UPDATE UserPage 
SET PagePath = '/tools/data-exporter/view'
WHERE PagePath =  '/amr/archivedValuesExporter/view';
/* End YUK-13218 */

/* Start YUK-13244 */
CREATE TABLE EcobeeQueryStatistics (
   EnergyCompanyId      NUMERIC              NOT NULL,
   MonthIndex           NUMERIC              NOT NULL,
   YearIndex            NUMERIC              NOT NULL,
   QueryType            VARCHAR(40)          NOT NULL,
   QueryCount           NUMERIC              NOT NULL,
   CONSTRAINT PK_EcobeeQueryStatistics PRIMARY KEY (EnergyCompanyId, MonthIndex, YearIndex, QueryType)
);
GO

CREATE TABLE EcobeeReconReportError (
   EcobeeReconReportErrorId NUMERIC          NOT NULL,
   EcobeeReconReportId  NUMERIC              NOT NULL,
   ErrorType            VARCHAR(40)          NOT NULL,
   SerialNumber         NUMERIC              NULL,
   CurrentLocation      VARCHAR(100)         NULL,
   CorrectLocation      VARCHAR(100)         NULL,
   CONSTRAINT PK_EcobeeReconReportError PRIMARY KEY (EcobeeReconReportErrorId)
);
GO

CREATE TABLE EcobeeReconciliationReport (
   EcobeeReconReportId  NUMERIC              NOT NULL,
   EnergyCompanyId      NUMERIC              NOT NULL,
   ReportDate           DATETIME             NOT NULL,
   CONSTRAINT PK_EcobeeReconReport PRIMARY KEY (EcobeeReconReportId)
);
GO

CREATE INDEX Indx_EcobeeReconReport_EC_UNQ ON EcobeeReconciliationReport (
   EnergyCompanyId ASC
);
GO

ALTER TABLE EcobeeQueryStatistics
   ADD CONSTRAINT FK_EcobeeQueryStats_EnergyCo FOREIGN KEY (EnergyCompanyId)
      REFERENCES EnergyCompany (EnergyCompanyID)
         ON DELETE CASCADE;
GO

ALTER TABLE EcobeeReconReportError
   ADD CONSTRAINT FK_EcobeeRecRepErr_EcobeeRecRp FOREIGN KEY (EcobeeReconReportId)
      REFERENCES EcobeeReconciliationReport (EcobeeReconReportId)
         ON DELETE CASCADE;
GO

ALTER TABLE EcobeeReconciliationReport
   ADD CONSTRAINT FK_EcobeeReconReport_EnergyCo FOREIGN KEY (EnergyCompanyId)
      REFERENCES EnergyCompany (EnergyCompanyID)
         ON DELETE CASCADE;
GO

INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry WHERE EntryId < 10000), 1005, 0, 'ecobee Smart Si', 1329);
INSERT INTO YukonServices VALUES (21, 'EcobeeMessageListener', 'classpath:com/cannontech/services/ecobeeMessageListener/ecobeeMessageListenerContext.xml', 'ServiceManager');
/* End YUK-13244 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.2', '01-MAY-2014', 'Latest Update', 0, GETDATE());