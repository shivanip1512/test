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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.5', '10-FEB-2020', 'Latest Update', 0, SYSDATE);*/
