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
DROP COLUMN EntryID;

ALTER TABLE DynamicPaoInfo 
DROP CONSTRAINT AK_DYNPAO_OWNKYUQ;

ALTER TABLE DynamicPaoInfo 
ADD CONSTRAINT PK_DynamicPaoInfo PRIMARY KEY (PAObjectID, Owner, InfoKey);
/* End YUK-13174 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAR-2014', 'Latest Update', 0, GETDATE());*/