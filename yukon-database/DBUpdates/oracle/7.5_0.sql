/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId;

ALTER TABLE DynamicRfnDeviceData
MODIFY GatewayId NUMBER NOT NULL;

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
);

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMBER NULL;

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
MODIFY DescendantCount NUMBER NOT NULL;

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew DATE NULL;

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
MODIFY LastTransferTimeNew DATE NOT NULL;

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;

ALTER TABLE DynamicRfnDeviceData
RENAME COLUMN LastTransferTimeNew
TO LastTransferTime;

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.5.0', SYSDATE);
/* @end YUK-21642 */

/* @start YUK-20982 */
ALTER TABLE MSPInterface ADD UseVendorAuth CHAR(1);
UPDATE MSPInterface SET UseVendorAuth = '1' WHERE UseVendorAuth IS NULL;
ALTER TABLE MSPInterface MODIFY UseVendorAuth CHAR(1) NOT NULL;

ALTER TABLE MSPInterface
ADD (InUserName VARCHAR2(64),
    InPassword VARCHAR2(64),
    OutUserName VARCHAR2(64),
    OutPassword VARCHAR2(64),
    ValidateCertificate CHAR(1));

INSERT INTO DBUpdates VALUES ('YUK-20982', '7.5.0', SYSDATE);
/* @end YUK-20982 */

/* @start YUK-21621 */
INSERT INTO StateGroup VALUES(-29, 'Meter Programming', 'Status');
INSERT INTO State VALUES(-29, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-29, 1, 'Failure', 1, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-21621', '7.5.0', SYSDATE);
/* @end YUK-21621 */

/* @start YUK-20774 */
/* @start-block */
DECLARE 
    v_RoleGroupID NUMBER;
    v_CreateEditParm VARCHAR(20);
    v_DeleteParm VARCHAR(20);
    v_AdminPerm VARCHAR(20);
    v_ViewPerm VARCHAR(20);
    v_RoleGroupIDText VARCHAR(20);
    v_NewPermissionLevel VARCHAR(20);

    CURSOR setting_cursor IS (
        SELECT DISTINCT YGR.GroupID, YGR_21400.Value AS CreateEditPerm, YGR_21401.Value AS DeletePerm, YGR_21402.Value AS AdminPerm, YGR_21403.Value AS ViewPerm FROM YukonGroupRole YGR
        LEFT JOIN YukonGroupRole YGR_21400 ON YGR.GroupID = YGR_21400.GroupID AND YGR_21400.RolePropertyID = -21400
        LEFT JOIN YukonGroupRole YGR_21401 ON YGR.GroupID = YGR_21401.GroupID AND YGR_21401.RolePropertyID = -21401
        LEFT JOIN YukonGroupRole YGR_21402 ON YGR.GroupID = YGR_21402.GroupID AND YGR_21402.RolePropertyID = -21402
        LEFT JOIN YukonGroupRole YGR_21403 ON YGR.GroupID = YGR_21403.GroupID AND YGR_21403.RolePropertyID = -21403
        WHERE YGR.RolePropertyID <= -21400 AND YGR.RolePropertyID >= -21403
    );

BEGIN
    OPEN setting_cursor;
    FETCH setting_cursor INTO v_RoleGroupID, v_CreateEditParm, v_DeleteParm, v_AdminPerm, v_ViewPerm;

    WHILE (setting_cursor%found)
    LOOP
        v_NewPermissionLevel := 'NO_ACCESS';
        IF v_ViewPerm = 'true' THEN
            v_NewPermissionLevel := 'VIEW';
        END IF;
        IF v_CreateEditParm = 'true' THEN
            v_NewPermissionLevel := 'CREATE';
        END IF;
        IF v_DeleteParm = 'true' THEN
            v_NewPermissionLevel := 'OWNER';
        END IF;
        IF v_AdminPerm = 'true' THEN
            v_NewPermissionLevel := 'OWNER';
        END IF;

        UPDATE YukonGroupRole SET Value = v_NewPermissionLevel WHERE GroupID = v_RoleGroupID AND RolePropertyID = -21400;
        FETCH setting_cursor INTO v_RoleGroupID, v_CreateEditParm, v_DeleteParm, v_AdminPerm, v_ViewPerm;
    END LOOP;

    CLOSE setting_cursor;
END;
/
/* @end-block */

UPDATE YukonRoleProperty
    SET KeyName = 'Manage Infrastructure', Description = 'Controls access to manage infrastructure devices. i.e. RF Gateways.', DefaultValue = 'NO_ACCESS'
    WHERE RolePropertyID = -21400;

DELETE FROM YukonGroupRole WHERE RolePropertyID = -21401 OR RolePropertyID = -21402 OR RolePropertyID = -21403;
DELETE FROM YukonRoleProperty WHERE RolePropertyID = -21401 OR RolePropertyID = -21402 OR RolePropertyID = -21403;

INSERT INTO DBUpdates VALUES ('YUK-20774', '7.5.0', SYSDATE);
/* @end YUK-20774 */

/* @start YUK-21777 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset = 2
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-21777', '7.5.0', SYSDATE);
/* @end YUK-21777 */

/* @start YUK-21967 */
DELETE FROM Job WHERE BeanName = 'deviceConfigVerificationJobDefinition';

INSERT INTO DBUpdates VALUES ('YUK-21967', '7.5.0', SYSDATE);
/* @end YUK-21967 */

/* @start YUK-21857 */
UPDATE POINT
SET ArchiveType = 'On Change'
WHERE PointID IN 
    (SELECT p.POINTID FROM POINT p
    JOIN YukonPAObject ypo on p.PAObjectID = ypo.PAObjectID
    WHERE (ypo.Type LIKE ('RFN-%') OR ypo.Type LIKE ('WRL-%'))
    AND ((p.PointOffset = 394 AND p.PointType = 'Analog') 
        OR (p.PointOffset = 2000 AND p.PointType = 'Status')));

UPDATE POINT
SET ArchiveInterval = 0
WHERE PointID IN 
    (SELECT p.POINTID FROM POINT p
    JOIN YukonPAObject ypo on p.PAObjectID = ypo.PAObjectID
    WHERE (ypo.Type LIKE ('RFN-%') OR ypo.Type LIKE ('WRL-%'))
    AND ((p.PointOffset = 394 AND p.PointType = 'Analog') 
        OR (p.PointOffset = 2000 AND p.PointType = 'Status')));

INSERT INTO DBUpdates VALUES ('YUK-21857', '7.5.0', SYSDATE);
/* @end YUK-21857 */

/* @start YUK-22094 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (19, 20, 21)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-22094', '7.5.0', SYSDATE);
/* @end YUK-22094 */

/* @start YUK-20252 */
INSERT INTO DeviceConfigState (PaObjectId, CurrentState, LastAction, LastActionStatus, LastActionStart, LastActionEnd, CommandRequestExecId)

(
SELECT
    PaObjectId, 
    'IN_SYNC' AS CurrentState, 
    'VERIFY' AS LastAction, 
    'SUCCESS' AS LastActionStatus, 
    cre.StartTime AS LastActionStart, 
    crer.CompleteTime AS LastActionEnd, 
    cre.CommandRequestExecId AS CommandRequestExecId
FROM YukonPAObject y 
    /*  Limits to only devices that have been assigned to a config, which is only meters and DNP/CBC devices  */
    JOIN DeviceConfigurationDeviceMap dcdm 
        ON y.paobjectid=dcdm.deviceid
    /*  Select commands sent to the devices, if any  */
    LEFT JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_SEND'
        GROUP BY DeviceId) latest_send
            ON latest_send.DeviceId=y.PAObjectID
    LEFT JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_READ'
        GROUP BY DeviceId) latest_read
            ON latest_read.DeviceId=y.PAObjectID
    LEFT JOIN (
        SELECT deviceId, MAX(completeTime) AS maxCompleteTime FROM
            CommandRequestExecResult crer
                JOIN CommandRequestExec cre 
                    ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        WHERE 
            ErrorCode<>0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY'
        GROUP BY DeviceId) failed_verify
            ON failed_verify.DeviceId=y.PAObjectID
    JOIN (
        SELECT deviceId, MAX(completeTime) AS maxCompleteTime FROM
            CommandRequestExecResult crer
                JOIN CommandRequestExec cre 
                    ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY'
        GROUP BY DeviceId) successful_verify
            ON successful_verify.DeviceId=y.PAObjectID
    JOIN CommandRequestExecResult crer
        ON crer.CompleteTime=successful_verify.maxCompleteTime
            AND crer.DeviceId=successful_verify.DeviceId
    JOIN CommandRequestExec cre
        ON cre.CommandRequestExecId=crer.CommandRequestExecId
    /*  Only include metering types - do not include RTUs, CBCs, regulators, etc  */
WHERE (y.type LIKE 'MCT%' OR y.type LIKE 'RF%' OR y.type LIKE 'WRL%')
    AND (latest_send.maxCompleteTime IS NULL OR successful_verify.maxCompleteTime > latest_send.maxCompleteTime)
    AND (latest_read.maxCompleteTime IS NULL OR successful_verify.maxCompleteTime > latest_read.maxCompleteTime)
    AND (failed_verify.maxCompleteTime IS NULL OR successful_verify.maxCompleteTime > failed_verify.maxCompleteTime)

UNION

SELECT
    PaObjectId, 
    'OUT_OF_SYNC' AS CurrentState, 
    'VERIFY' AS LastAction, 
    'FAILURE' AS LastActionStatus, 
    cre.StartTime AS LastActionStart, 
    crer.CompleteTime AS LastActionEnd, 
    cre.CommandRequestExecId AS CommandRequestExecId
FROM YukonPAObject y 
    /*  Limits to only devices that have been assigned to a config, which is only meters and DNP/CBC devices  */
    JOIN DeviceConfigurationDeviceMap dcdm 
        ON y.paobjectid=dcdm.deviceid
    /*  Select commands sent to the devices, if any  */
    LEFT JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_SEND'
        GROUP BY DeviceId) latest_send
            ON latest_send.DeviceId=y.PAObjectID
    LEFT JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_READ'
        GROUP BY DeviceId) latest_read
            ON latest_read.DeviceId=y.PAObjectID
    JOIN (
        SELECT deviceId, MAX(completeTime) AS maxCompleteTime FROM
            CommandRequestExecResult crer
                JOIN CommandRequestExec cre 
                    ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        WHERE 
            ErrorCode<>0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY'
        GROUP BY DeviceId) failed_verify
            ON failed_verify.DeviceId=y.PAObjectID
    LEFT JOIN (
        SELECT deviceId, MAX(completeTime) AS maxCompleteTime FROM
            CommandRequestExecResult crer
                JOIN CommandRequestExec cre 
                    ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY'
        GROUP BY DeviceId) successful_verify
            ON successful_verify.DeviceId=y.PAObjectID
    JOIN CommandRequestExecResult crer
        ON crer.CompleteTime=failed_verify.maxCompleteTime
            AND crer.DeviceId=failed_verify.DeviceId
    JOIN CommandRequestExec cre
        ON cre.CommandRequestExecId=crer.CommandRequestExecId
    /*  Only include metering types - do not include RTUs, CBCs, regulators, etc  */
WHERE (y.type LIKE 'MCT%' OR y.type LIKE 'RF%' OR y.type LIKE 'WRL%')
    AND (latest_send.maxCompleteTime IS NULL OR failed_verify.maxCompleteTime > latest_send.maxCompleteTime)
    AND (latest_read.maxCompleteTime IS NULL OR failed_verify.maxCompleteTime > latest_read.maxCompleteTime)
    AND (successful_verify.maxCompleteTime IS NULL OR failed_verify.maxCompleteTime > successful_verify.maxCompleteTime)

UNION

SELECT
    PaObjectId, 
    'UNCONFIRMED' AS CurrentState, 
    CASE WHEN CommandRequestExecType='GROUP_DEVICE_CONFIG_SEND' THEN 'SEND' 
         WHEN CommandRequestExecType='GROUP_DEVICE_CONFIG_READ' THEN 'READ' END AS LastAction, 
    'SUCCESS' AS LastActionStatus, 
    cre.StartTime AS LastActionStart, 
    crer.CompleteTime AS LastActionEnd, 
    cre.CommandRequestExecId AS CommandRequestExecId
FROM YukonPAObject y 
    /*  Limits to only devices that have been assigned to a config, which is only meters and DNP/CBC devices  */
    JOIN DeviceConfigurationDeviceMap dcdm 
        ON y.paobjectid=dcdm.deviceid
    /*  Select commands sent to the devices, if any  */
    JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            ErrorCode=0
            AND CommandRequestExecType in ('GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ')
        GROUP BY DeviceId) latest_send_or_read
            ON latest_send_or_read.DeviceId=y.PAObjectID
    LEFT JOIN (
        SELECT deviceId, MAX(completeTime) AS maxCompleteTime FROM
            CommandRequestExecResult crer
                JOIN CommandRequestExec cre 
                    ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        WHERE 
            CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY'
        GROUP BY DeviceId) latest_verify
            ON latest_verify.DeviceId=y.PAObjectID
    JOIN CommandRequestExecResult crer
        ON crer.CompleteTime=latest_send_or_read.maxCompleteTime
            AND crer.DeviceId=latest_send_or_read.DeviceId
    JOIN CommandRequestExec cre
        ON cre.CommandRequestExecId=crer.CommandRequestExecId
    /*  Only include metering types - do not include RTUs, CBCs, regulators, etc  */
WHERE (y.type LIKE 'MCT%' OR y.type LIKE 'RF%' OR y.type LIKE 'WRL%')
    AND (latest_verify.maxCompleteTime IS NULL OR latest_send_or_read.maxCompleteTime > latest_verify.maxCompleteTime)

UNION

SELECT
    PaObjectId, 
    'UNASSIGNED' AS CurrentState, 
    CASE WHEN CommandRequestExecType='GROUP_DEVICE_CONFIG_SEND' THEN 'SEND' 
         WHEN CommandRequestExecType='GROUP_DEVICE_CONFIG_READ' THEN 'READ' 
         WHEN CommandRequestExecType='GROUP_DEVICE_CONFIG_VERIFY' THEN 'VERIFY' END AS LastAction, 
    CASE WHEN crer.ErrorCode=0 THEN 'SUCCESS'
         WHEN crer.ErrorCode<>0 THEN 'FAILURE' END AS LastActionStatus, 
    cre.StartTime AS LastActionStart, 
    crer.CompleteTime AS LastActionEnd, 
    cre.CommandRequestExecId AS CommandRequestExecId
FROM YukonPAObject y 
    /*  Limits to only devices that have no config  */
    LEFT JOIN DeviceConfigurationDeviceMap dcdm 
        ON y.paobjectid=dcdm.deviceid
    /*  Select commands sent to the devices, if any  */
    JOIN (
        SELECT DeviceId, MAX(CompleteTime) AS maxCompleteTime FROM
            CommandRequestExec cre 
                JOIN CommandRequestExecResult crer
                    ON cre.CommandRequestExecId=crer.CommandRequestExecId
        WHERE 
            CommandRequestExecType in ('GROUP_DEVICE_CONFIG_SEND', 'GROUP_DEVICE_CONFIG_READ', 'GROUP_DEVICE_CONFIG_VERIFY')
        GROUP BY DeviceId) latest_action
            ON latest_action.DeviceId=y.PAObjectID
    JOIN CommandRequestExecResult crer
        ON crer.CompleteTime=latest_action.maxCompleteTime
            AND crer.DeviceId=latest_action.DeviceId
    JOIN CommandRequestExec cre
        ON cre.CommandRequestExecId=crer.CommandRequestExecId
    /*  Only include metering types - do not include RTUs, CBCs, regulators, etc  */
WHERE (y.type LIKE 'MCT%' OR y.type LIKE 'RF%' OR y.type LIKE 'WRL%')
    AND dcdm.DeviceConfigurationId IS NULL

UNION

SELECT PaObjectId, 'UNREAD', 'ASSIGN', 'SUCCESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL
FROM YukonPAObject y 
    /*  Limits to only devices that have been assigned to a config, which is only meters and DNP/CBC devices  */
    JOIN DeviceConfigurationDeviceMap dcdm ON y.paobjectid=dcdm.deviceid
    /*  Select commands sent to the devices, if any  */
    LEFT JOIN CommandRequestExecResult crer ON y.PAObjectID=crer.DeviceId
    LEFT JOIN CommandRequestExec cre ON crer.CommandRequestExecId=cre.CommandRequestExecId 
        AND cre.CommandRequestExecType IN ('GROUP_DEVICE_CONFIG_VERIFY','GROUP_DEVICE_CONFIG_SEND','GROUP_DEVICE_CONFIG_READ')
    /*  Only include metering types - do not include RTUs, CBCs, regulators, etc  */
WHERE (y.type LIKE 'MCT%' OR y.type LIKE 'RF%' OR y.type LIKE 'WRL%')
GROUP BY PaObjectId
HAVING COUNT(cre.commandrequestexectype) = 0
);

INSERT INTO DBUpdates VALUES ('YUK-20252', '7.5.0', SYSDATE);
/* @end YUK-20252 */

/* @start YUK-22110 */
DELETE FROM PointStatusControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointControl WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointStatus WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM GraphDataSeries WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM CalcComponent WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Display2WayData WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointUnit WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM PointAlarming WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

DELETE FROM Point WHERE POINTID IN (
    SELECT PointId FROM Point P JOIN YukonPAObject YP ON YP.PAObjectID = P.PAObjectID
    WHERE POINTTYPE = 'Status' AND PointOffset IN (1)
    AND YP.Type IN ('LCR-6600S', 'LCR-6601S'));

INSERT INTO DBUpdates VALUES ('YUK-22110', '7.5.0', SYSDATE);
/* @end YUK-22110 */

/* @start YUK-22234 */
/* @start-block */
DECLARE
    v_MaxDeviceGroupId NUMBER;
    v_MaxComposedId NUMBER;
    v_MaxComposedGroupId NUMBER;
BEGIN
    SELECT MAX(DeviceGroupId) INTO v_MaxDeviceGroupId FROM DeviceGroup WHERE DeviceGroupId < 100;
    INSERT INTO DeviceGroup VALUES (v_MaxDeviceGroupId + 1, 'Service', 0, 'NOEDIT_NOMOD', 'STATIC', SYSDATE, 'SERVICE');
    INSERT INTO DeviceGroup VALUES (v_MaxDeviceGroupId + 2, 'Active RF Electric Meters', v_MaxDeviceGroupId + 1, 'NOEDIT_MOD', 'COMPOSED', SYSDATE, 'SERVICE_ACTIVE_RF_ELECTRIC_METERS');
    INSERT INTO DeviceGroup VALUES (v_MaxDeviceGroupId + 3, 'Active RFW Meters', v_MaxDeviceGroupId + 1, 'NOEDIT_MOD', 'COMPOSED', SYSDATE, 'SERVICE_ACTIVE_RFW_METERS');

    SELECT MAX(DeviceGroupComposedId) INTO v_MaxComposedId FROM DeviceGroupComposed;
    SELECT MAX(DeviceGroupComposedGroupId) INTO v_MaxComposedGroupId FROM DeviceGroupComposedGroup;
    IF v_MaxComposedId IS NULL THEN
        v_MaxComposedId := 0;
    END IF;
    IF v_MaxComposedGroupId IS NULL THEN
        v_MaxComposedGroupId := 0;
    END IF;

    INSERT INTO DeviceGroupComposed VALUES (v_MaxComposedId + 1, v_MaxDeviceGroupId + 2, 'UNION');
    INSERT INTO DeviceGroupComposed VALUES (v_MaxComposedId + 2, v_MaxDeviceGroupId + 3, 'UNION');

    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 1, v_MaxComposedId + 1, '/System/Meters/All Meters/All RFN Meters/All RF Electric Meters', 'N');
    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 2, v_MaxComposedId + 1, '/Meters/Billing', 'N');
    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 3, v_MaxComposedId + 1, '/System/Meters/Disabled', 'Y');
    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 4, v_MaxComposedId + 2, '/System/Meters/All Meters/All RFN Meters/All RFW Meters', 'N');
    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 5, v_MaxComposedId + 2, '/Meters/Billing', 'N');
    INSERT INTO DeviceGroupComposedGroup VALUES (v_MaxComposedGroupId + 6, v_MaxComposedId + 2, '/System/Meters/Disabled', 'Y');
END;
/* @end-block */

INSERT INTO DBUpdates VALUES ('YUK-22234', '7.5.0', SYSDATE);
/* @end YUK-22234 */

/* @start YUK-21829 */
CREATE TABLE CustomAttribute (
   AttributeId          NUMERIC              NOT NULL,
   AttributeName        VARCHAR(60)          NOT NULL,
   CONSTRAINT PK_CustomAttribute PRIMARY KEY (AttributeId)
);

ALTER TABLE CustomAttribute
   ADD CONSTRAINT AK_AttributeName UNIQUE (AttributeName);

INSERT INTO DBUpdates VALUES ('YUK-21829', '7.5.0', SYSDATE);
/* @end YUK-21829 */

/* @start YUK-22443 */
UPDATE DeviceGroup SET Permission = 'NOEDIT_NOMOD' 
    WHERE SystemGroupEnum IN  ('SERVICE_ACTIVE_RFW_METERS', 'SERVICE_ACTIVE_RF_ELECTRIC_METERS');

INSERT INTO DBUpdates VALUES ('YUK-22443', '7.5.0', SYSDATE);
/* @end YUK-22443 */

/* @start YUK-22371 */
/* @start-block */
DECLARE
    v_DefaultValue VARCHAR(5) := 'VIEW';
    v_ExistingValue VARCHAR(5);
BEGIN
    SELECT DefaultValue INTO v_ExistingValue FROM YukonRoleProperty WHERE RolePropertyID = -10200;
    IF v_ExistingValue = 'true' THEN
        v_DefaultValue := 'OWNER';
    END IF;

    UPDATE YukonRoleProperty
        SET KeyName = 'Manage Trends', Description = 'Controls access to view, create, edit, or delete Trends.', DefaultValue = v_DefaultValue
        WHERE RolePropertyID = -10200;
END;
/* @end-block */
INSERT INTO DBUpdates VALUES ('22371', '7.5.0', SYSDATE);
/* @end YUK-22371 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '10-FEB-2020', 'Latest Update', 0, SYSDATE);*/
