/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12914 */
/* @error ignore-begin */
ALTER TABLE RfnBroadcastEvent
RENAME COLUMN EventSendTime TO EventSentTime;
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
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CalcPointBaseline WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CustomerBaseLinePoint WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCalcHistorical WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CALCBASE WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CALCCOMPONENT WHERE COMPONENTPOINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPBANK WHERE CONTROLPOINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE SwitchPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentVoltLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentWattLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CAPCONTROLSUBSTATIONBUS WHERE CurrentVarLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CCEventLog WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CCMonitorBankList WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CICUSTOMERPOINTDATA WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentVarLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentVoltLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM CapControlFeeder WHERE CurrentWattLoadPointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DISPLAY2WAYDATA WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DYNAMICACCUMULATOR WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DYNAMICPOINTDISPATCH WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCCMonitorBankHistory WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicCCMonitorPointResponse WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicPointAlarming WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM DynamicTags WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM EstimatedLoadFunction WHERE InputPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM EstimatedLoadLookupTable WHERE InputPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM ExtraPaoPointAssignment WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM FDRTranslation WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM GRAPHDATASERIES WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMControlAreaTrigger WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMControlAreaTrigger WHERE PeakPointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM LMGroupPoint WHERE PointIDUsage IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PAOExclusion WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTACCUMULATOR WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTANALOG WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTLIMITS WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTPROPERTYVALUE WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTSTATUS WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE TriggerID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE VerificationID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTTRIGGER WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM POINTUNIT WHERE POINTID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointAlarming WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointStatusControl WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointControl WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM PointToZoneMapping WHERE PointId IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM TagLog WHERE PointID IN ( 
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));

DELETE FROM Point WHERE PointId IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE PointName = 'Temp Out of Service Duration' AND YP.Type IN ('LCR-6200 RFN', 'LCR-6600 RFN'));
/* End YUK-13120 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAR-2014', 'Latest Update', 0, SYSDATE);*/