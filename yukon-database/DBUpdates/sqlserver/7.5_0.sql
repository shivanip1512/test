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
DECLARE @RoleGroupID AS NUMERIC,
        @CreateEditParm AS VARCHAR(20),
        @DeleteParm AS VARCHAR(20),
        @AdminPerm AS VARCHAR(20),
        @ViewPerm AS VARCHAR(20)
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
    UPDATE YukonRoleProperty
    SET KeyName = 'Manage Infrastructure', Description = 'Controls access to manage infrastructure devices. i.e. RF Gateways.', DefaultValue = 'NO_ACCESS'
    WHERE RolePropertyID = -21403;

    OPEN setting_cursor;
    FETCH NEXT FROM setting_cursor INTO @RoleGroupID, @CreateEditParm, @DeleteParm, @AdminPerm, @ViewPerm
    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF @ViewPerm = 'false'
        BEGIN
            UPDATE YukonGroupRole SET Value = 'NO_ACCESS' WHERE GroupID = @RoleGroupID AND RolePropertyID = -21403;
        END
        IF @ViewPerm = 'true'
        BEGIN
            UPDATE YukonGroupRole SET Value = 'VIEW' WHERE GroupID = @RoleGroupID AND RolePropertyID = -21403;
        END
        IF @CreateEditParm = 'true'
        BEGIN
            UPDATE YukonGroupRole SET Value = 'CREATE' WHERE GroupID = @RoleGroupID AND RolePropertyID = -21403;
        END
        IF @DeleteParm = 'true'
        BEGIN
            UPDATE YukonGroupRole SET Value = 'OWNER' WHERE GroupID = @RoleGroupID AND RolePropertyID = -21403;
        END
        IF @AdminPerm = 'true'
        BEGIN
            UPDATE YukonGroupRole SET Value = 'OWNER' WHERE GroupID = @RoleGroupID AND RolePropertyID = -21403;
        END
        FETCH NEXT FROM setting_cursor INTO @RoleGroupID, @CreateEditParm, @DeleteParm, @AdminPerm, @ViewPerm
    END

    CLOSE setting_cursor;
    DEALLOCATE setting_cursor;

    DELETE FROM YukonGroupRole WHERE RolePropertyID = -21400 OR RolePropertyID = -21401 OR RolePropertyID = -21402;
    DELETE FROM YukonRoleProperty WHERE RolePropertyID = -21400 OR RolePropertyID = -21401 OR RolePropertyID = -21402;
END;
/* @end YUK-20774 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '15-NOV-2019', 'Latest Update', 0, GETDATE());*/