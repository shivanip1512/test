/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-21642 */
DROP INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN GatewayId NUMERIC NOT NULL;
GO

CREATE INDEX INDX_DynRfnDevData_GatewayId ON DynamicRfnDeviceData (
GatewayId ASC
)
GO

ALTER TABLE DynamicRfnDeviceData
ADD DescendantCount NUMERIC NULL;
GO

UPDATE DynamicRfnDeviceData
SET DescendantCount = -1;

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN DescendantCount NUMERIC NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
ADD LastTransferTimeNew datetime NULL;
GO

UPDATE DynamicRfnDeviceData
SET LastTransferTimeNew = LastTransferTime;
GO

ALTER TABLE DynamicRfnDeviceData
ALTER COLUMN LastTransferTimeNew datetime NOT NULL;
GO

ALTER TABLE DynamicRfnDeviceData
DROP COLUMN LastTransferTime;
GO

EXEC sp_rename 'DynamicRfnDeviceData.LastTransferTimeNew', 'LastTransferTime', 'COLUMN';
GO

INSERT INTO DBUpdates VALUES ('YUK-21642', '7.5.0', GETDATE());
/* @end YUK-21642 */

/* @start YUK-20982 */
ALTER TABLE MSPInterface ADD UseVendorAuth CHAR(1);
GO
UPDATE MSPInterface SET UseVendorAuth = '1' WHERE UseVendorAuth IS NULL;
GO
ALTER TABLE MSPInterface ALTER COLUMN UseVendorAuth CHAR(1) NOT NULL;
GO

ALTER TABLE MSPInterface
ADD InUserName VARCHAR(64),
    InPassword VARCHAR(64),
    OutUserName VARCHAR(64),
    OutPassword VARCHAR(64),
    ValidateCertificate CHAR(1);
GO

INSERT INTO DBUpdates VALUES ('YUK-20982', '7.5.0', GETDATE());
/* @end YUK-20982 */

/* @start YUK-21621 */
INSERT INTO StateGroup VALUES(-29, 'Meter Programming', 'Status');
INSERT INTO State VALUES(-29, 0, 'Success', 0, 6, 0);
INSERT INTO State VALUES(-29, 1, 'Failure', 1, 6, 0);

INSERT INTO DBUpdates VALUES ('YUK-21621', '7.5.0', GETDATE());
/* @end YUK-21621 */

/* @start YUK-20774 */
/* @start-block */
DECLARE @RoleGroupID AS NUMERIC,
        @CreateEditParm AS VARCHAR(20),
        @DeleteParm AS VARCHAR(20),
        @AdminPerm AS VARCHAR(20),
        @ViewPerm AS VARCHAR(20),
        @NewPermissionLevel AS VARCHAR(20)
;

DECLARE setting_cursor CURSOR STATIC FOR (
    SELECT DISTINCT YGR.GroupID, YGR_21400.Value AS CreateEditPerm, YGR_21401.Value AS DeletePerm, YGR_21402.Value AS AdminPerm, YGR_21403.Value AS ViewPerm FROM YukonGroupRole YGR
    LEFT JOIN YukonGroupRole YGR_21400 ON YGR.GroupID = YGR_21400.GroupID AND YGR_21400.RolePropertyID = -21400
    LEFT JOIN YukonGroupRole YGR_21401 ON YGR.GroupID = YGR_21401.GroupID AND YGR_21401.RolePropertyID = -21401
    LEFT JOIN YukonGroupRole YGR_21402 ON YGR.GroupID = YGR_21402.GroupID AND YGR_21402.RolePropertyID = -21402
    LEFT JOIN YukonGroupRole YGR_21403 ON YGR.GroupID = YGR_21403.GroupID AND YGR_21403.RolePropertyID = -21403
    WHERE YGR.RolePropertyID <= -21400 AND YGR.RolePropertyID >= -21403
);

BEGIN
    OPEN setting_cursor;
    FETCH NEXT FROM setting_cursor INTO @RoleGroupID, @CreateEditParm, @DeleteParm, @AdminPerm, @ViewPerm
    WHILE @@FETCH_STATUS = 0
    BEGIN
        SET @NewPermissionLevel = 'NO_ACCESS';
        IF @ViewPerm = 'true'
        BEGIN
            SET @NewPermissionLevel = 'VIEW';
        END
        IF @CreateEditParm = 'true'
        BEGIN
            SET @NewPermissionLevel = 'CREATE';
        END
        IF @DeleteParm = 'true'
        BEGIN
            SET @NewPermissionLevel = 'OWNER';
        END
        IF @AdminPerm = 'true'
        BEGIN
            SET @NewPermissionLevel = 'OWNER';
        END

        UPDATE YukonGroupRole SET Value = @NewPermissionLevel WHERE GroupID = @RoleGroupID AND RolePropertyID = -21400;
        FETCH NEXT FROM setting_cursor INTO @RoleGroupID, @CreateEditParm, @DeleteParm, @AdminPerm, @ViewPerm
    END

    CLOSE setting_cursor;
    DEALLOCATE setting_cursor;
END;
GO
/* @end-block */

UPDATE YukonRoleProperty
SET KeyName = 'Manage Infrastructure', Description = 'Controls access to manage infrastructure devices. i.e. RF Gateways.', DefaultValue = 'NO_ACCESS'
WHERE RolePropertyID = -21400;

DELETE FROM YukonGroupRole WHERE RolePropertyID = -21401 OR RolePropertyID = -21402 OR RolePropertyID = -21403;
DELETE FROM YukonRoleProperty WHERE RolePropertyID = -21401 OR RolePropertyID = -21402 OR RolePropertyID = -21403;

INSERT INTO DBUpdates VALUES ('YUK-20774', '7.5.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-21777', '7.5.0', GETDATE());
/* @end YUK-21777 */

/* @start YUK-21967 */
DELETE FROM Job WHERE BeanName = 'deviceConfigVerificationJobDefinition';

INSERT INTO DBUpdates VALUES ('YUK-21967', '7.5.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-21857', '7.5.0', GETDATE());
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

INSERT INTO DBUpdates VALUES ('YUK-22094', '7.5.0', GETDATE());
/* @end YUK-22094 */

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

INSERT INTO DBUpdates VALUES ('YUK-22110', '7.5.0', GETDATE());
/* @end YUK-22110 */

/* @start YUK-22234 */
/* @start-block */
BEGIN
    DECLARE @v_MaxDeviceGroupId NUMERIC = (SELECT MAX(DeviceGroupId) FROM DeviceGroup WHERE DeviceGroupId < 100)
    INSERT INTO DeviceGroup VALUES (@v_MaxDeviceGroupId + 1, 'Service', 0, 'NOEDIT_NOMOD', 'STATIC', GETDATE(), 'SERVICE')
    INSERT INTO DeviceGroup VALUES (@v_MaxDeviceGroupId + 2, 'Active RF Electric Meters', @v_MaxDeviceGroupId + 1, 'NOEDIT_MOD', 'COMPOSED', GETDATE(), 'SERVICE_ACTIVE_RF_ELECTRIC_METERS')
    INSERT INTO DeviceGroup VALUES (@v_MaxDeviceGroupId + 3, 'Active RFW Meters', @v_MaxDeviceGroupId + 1, 'NOEDIT_MOD', 'COMPOSED', GETDATE(), 'SERVICE_ACTIVE_RFW_METERS')

    DECLARE @v_MaxComposedId NUMERIC = (SELECT MAX(DeviceGroupComposedId) FROM DeviceGroupComposed)
    DECLARE @v_MaxComposedGroupId NUMERIC = (SELECT MAX(DeviceGroupComposedGroupId) FROM DeviceGroupComposedGroup)
    IF @v_MaxComposedId IS NULL
    BEGIN
        SET @v_MaxComposedId = 0
    END 
    IF @v_MaxComposedGroupId IS NULL
    BEGIN
        SET @v_MaxComposedGroupId = 0
    END

    INSERT INTO DeviceGroupComposed VALUES (@v_MaxComposedId + 1, @v_MaxDeviceGroupId + 2, 'UNION')
    INSERT INTO DeviceGroupComposed VALUES (@v_MaxComposedId + 2, @v_MaxDeviceGroupId + 3, 'UNION')

    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 1, @v_MaxComposedId + 1, '/System/Meters/All Meters/All RFN Meters/All RF Electric Meters', 'N')
    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 2, @v_MaxComposedId + 1, '/Meters/Billing', 'N')
    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 3, @v_MaxComposedId + 1, '/System/Meters/Disabled', 'Y')
    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 4, @v_MaxComposedId + 2, '/System/Meters/All Meters/All RFN Meters/All RFW Meters', 'N')
    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 5, @v_MaxComposedId + 2, '/Meters/Billing', 'N')
    INSERT INTO DeviceGroupComposedGroup VALUES (@v_MaxComposedGroupId + 6, @v_MaxComposedId + 2, '/System/Meters/Disabled', 'Y')
END;
GO
/* @end-block */

INSERT INTO DBUpdates VALUES ('YUK-22234', '7.5.0', GETDATE());
/* @end YUK-22234 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '15-NOV-2019', 'Latest Update', 0, GETDATE());*/