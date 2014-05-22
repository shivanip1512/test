/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13277 */
ALTER TABLE InventoryToAcctThermostatSch
   DROP CONSTRAINT FK_InvToAcctThermSch_InvBase;

ALTER TABLE InventoryToAcctThermostatSch
   ADD CONSTRAINT FK_InvToAcctThermSch_InvBase FOREIGN KEY (InventoryId)
      REFERENCES InventoryBase (InventoryID)
      ON DELETE CASCADE;
/* End YUK-13277 */

/* Start YUK-13230 */
/* @error ignore-begin */
DROP INDEX Indx_Add_LocAdd;
DROP INDEX Indx_Add_LocAdd_FB;
/* @error ignore-end */

ALTER TABLE Address
    MODIFY LocationAddress1 VARCHAR2(100);

ALTER TABLE Address
    MODIFY LocationAddress2 VARCHAR2(100);

CREATE INDEX Indx_Add_LocAdd ON Address (
    LocationAddress1 ASC
);

CREATE INDEX Indx_Add_LocAdd_FB ON Address (
   UPPER(LocationAddress1) ASC
);
/* End YUK-13230 */

/* Start YUK-13191 */
/* @start-block */
DECLARE
    v_count NUMBER;
BEGIN  
    SELECT COUNT(*) INTO v_count FROM (
       SELECT T1.EnergyCompanyID, T1.RolePropertyId, COUNT(T1.Value) as ValueCount FROM (
          SELECT DISTINCT EC.EnergyCompanyID, YGR.RolePropertyId, YGR.Value 
          FROM YukonGroupRole YGR
          JOIN UserGroupToYukonGroupMapping UGYG ON UGYG.GroupId = YGR.GroupID
          JOIN YukonUser YU ON YU.UserGroupId = UGYG.UserGroupId
          JOIN EnergyCompany EC ON EC.UserID = YU.UserID
          WHERE YGR.RoleId = -201 
            AND YGR.RolePropertyId in (-20153, -20155, -20156, -20160, -20893)
            AND YGR.Value != ' '
            AND YGR.Value != '(none)'
            AND YGR.Value IS NOT NULL) T1
       GROUP BY EnergyCompanyID, RolePropertyID) T2
       WHERE ValueCount > 1 ;
       
    IF 0 < v_count
    THEN
        RAISE_APPLICATION_ERROR(-20001, 'There was a problem converting some role properties into energy company settings for one or more energy companies. Multiple role groups contained different values for a role property being converted.  One value must be chosen to become the value for the energy company-wide setting. Please see YUK-13191 for instructions on how to resolve the problem.');
    END IF;
END;
/
/* @end-block */

CREATE TABLE RolePropToSetting_Temp (
    RolePropertyId NUMERIC      NOT NULL,
    RoleName       VARCHAR(100) NOT NULL,
    CONSTRAINT PK_RolePropToSetting_Temp PRIMARY KEY (RolePropertyId)
);

INSERT INTO RolePropToSetting_Temp VALUES (-20153, 'INVENTORY_CHECKING');
INSERT INTO RolePropToSetting_Temp VALUES (-20155, 'WORK_ORDER_NUMBER_AUTO_GEN');
INSERT INTO RolePropToSetting_Temp VALUES (-20156, 'CALL_TRACKING_NUMBER_AUTO_GEN');
INSERT INTO RolePropToSetting_Temp VALUES (-20160, 'AUTO_CREATE_LOGIN_FOR_ACCOUNT');
INSERT INTO RolePropToSetting_Temp VALUES (-20893, 'INVENTORY_CHECKING_CREATE');

/* @start-block */
DECLARE
    v_energyCompanySettingId NUMBER; 
BEGIN
    SELECT NVL(MAX(EnergyCompanySettingId)+1,0) INTO v_energyCompanySettingId
    FROM EnergyCompanySetting ECS; 
 
    INSERT INTO EnergyCompanySetting (EnergyCompanySettingId, EnergyCompanyId, Name, Value, Enabled)
        SELECT 
            v_energyCompanySettingId + ROW_NUMBER() OVER (ORDER BY ygr.RoleId, ygr.RolePropertyId), 
            EC.EnergyCompanyId,
            RPTS.RoleName, 
            YGR.Value,
            'Y' as Enabled
        FROM EnergyCompany EC 
        JOIN YukonUser YU ON YU.UserID = EC.UserID 
        JOIN UserGroupToYukonGroupMapping UGYG ON UGYG.UserGroupId = YU.UserGroupId
        JOIN YukonGroupRole YGR          ON YGR.GroupID = UGYG.GroupID
        JOIN RolePropToSetting_Temp RPTS ON RPTS.RolePropertyId = YGR.RolePropertyId
        WHERE ygr.RoleID = -201
          AND ygr.RolePropertyID IN (-20153, -20155, -20156, -20160, -20893)
        AND ygr.Value != ' ' 
        AND ygr.Value != '(none)'
        AND ygr.Value IS NOT NULL
        ORDER BY EnergyCompanyId, RoleName;
END;
/
/* @end-block */

DROP TABLE RolePropToSetting_Temp;

DELETE FROM YukonGroupRole 
WHERE RoleId = -201 
  AND RolePropertyId in (-20153, -20155, -20156, -20159, -20160, -20893);

DELETE FROM YukonRoleProperty 
WHERE RoleId = -201 
  AND RolePropertyId in (-20153, -20155, -20156, -20159, -20160, -20893);
/* End YUK-13191 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAY-2014', 'Latest Update', 1, SYSDATE);*/