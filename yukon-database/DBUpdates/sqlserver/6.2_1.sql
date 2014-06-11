/******************************************/
/**** SQL Server DBupdates             ****/
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
ALTER TABLE Address
    ALTER COLUMN LocationAddress1 VARCHAR(100) NOT NULL;

ALTER TABLE Address
    ALTER COLUMN LocationAddress2 VARCHAR(100) NOT NULL;
/* End YUK-13230 */

/* Start YUK-13191 */
/* @start-block */
DECLARE
    @count NUMERIC;
BEGIN  
    SELECT @count = COUNT(*) FROM (
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

    IF 0 < @count
    BEGIN
        RAISERROR('There was a problem converting some role properties into energy company settings for one or more energy companies. Multiple role groups contained different values for a role property being converted.  One value must be chosen to become the value for the energy company-wide setting. Please see YUK-13191 for instructions on how to resolve the problem.', 16, 1);
    END
END;
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
    @energyCompanySettingId NUMERIC; 
BEGIN
    SELECT @energyCompanySettingId = ISNULL(MAX(EnergyCompanySettingId)+1,0)
    FROM EnergyCompanySetting ecs; 
 
    INSERT INTO EnergyCompanySetting (EnergyCompanySettingId, EnergyCompanyId, Name, Value, Enabled)
        SELECT 
            @energyCompanySettingId + ROW_NUMBER() OVER (ORDER BY ygr.RoleId, ygr.RolePropertyId), 
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
/* @end-block */

DROP TABLE RolePropToSetting_Temp;

DELETE FROM YukonGroupRole 
WHERE RoleId = -201 
  AND RolePropertyId in (-20153, -20155, -20156, -20159, -20160, -20893);

DELETE FROM YukonRoleProperty 
WHERE RoleId = -201 
  AND RolePropertyId in (-20153, -20155, -20156, -20159, -20160, -20893);
/* End YUK-13191 */

/* Start YUK-13365 */
ALTER TABLE EcobeeQueryStatistics
DROP CONSTRAINT PK_EcobeeQueryStatistics;

ALTER TABLE EcobeeQueryStatistics
DROP CONSTRAINT FK_EcobeeQueryStats_EnergyCo;

ALTER TABLE EcobeeQueryStatistics
DROP COLUMN EnergyCompanyId;

ALTER TABLE EcobeeQueryStatistics
ADD CONSTRAINT PK_EcobeeQueryStatistics PRIMARY KEY (MonthIndex, YearIndex, QueryType);

DROP INDEX EcobeeReconciliationReport.Indx_EcobeeReconReport_EC_UNQ;

ALTER TABLE EcobeeReconciliationReport
DROP CONSTRAINT FK_EcobeeReconReport_EnergyCo;

ALTER TABLE EcobeeReconciliationReport
DROP COLUMN EnergyCompanyId;

ALTER TABLE EcobeeReconReportError
ALTER COLUMN SerialNumber VARCHAR(30) NULL;
/* End YUK-13365 */

/* Start YUK-13377 */
INSERT INTO YukonRoleProperty VALUES (-90046,-900,'Enable ecobee','false','Controls access to actions and information related to ecobee devices.');
/* End YUK-13377 */

/* Start YUK-13359 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -20001;

DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -20001;
/* End YUK-13359 */

/* Start YUK-13354 */
ALTER TABLE CommandRequestUnsupported
ADD Type VARCHAR(50) NULL;
GO

UPDATE CommandRequestUnsupported
SET Type = 'UNSUPPORTED';

ALTER TABLE CommandRequestUnsupported
ALTER COLUMN Type VARCHAR(50) NOT NULL;

CREATE INDEX Indx_CommandReqUnsupport_Type ON CommandRequestUnsupported (
    Type ASC
);
GO

ALTER TABLE State
ALTER COLUMN Text VARCHAR(50) NOT NULL;

INSERT INTO State VALUES(-12, 4, 'Disconnected Demand Threshold Active', 1, 6, 0);
INSERT INTO State VALUES(-12, 5, 'Connected Demand Threshold Active ', 7, 6, 0);
INSERT INTO State VALUES(-12, 6, 'Disconnected Cycling Active', 1, 6, 0);
INSERT INTO State VALUES(-12, 7, 'Connected Cycling Active ', 7, 6, 0);

INSERT INTO YukonRoleProperty VALUES (-21314,-213,'Connect/Disconnect','true','Controls access to Connect/Disconnect collection action.');
/* End YUK-13354 */

/* Start YUK-13323 */
INSERT INTO DeviceConfigCategoryItem
SELECT 
    ROW_NUMBER() OVER(ORDER BY C.DeviceConfigCategoryID) + (SELECT ISNULL(MAX(DeviceConfigCategoryItemID), 1) FROM DeviceConfigCategoryItem),
    C.DeviceConfigCategoryId,
    'disconnectMode',
    CASE
        WHEN (CAST(DisconnectDemandThreshold.ItemValue AS FLOAT) <> 0) THEN 'DEMAND_THRESHOLD'
        WHEN (ConnectMinutes.ItemValue <> '0') THEN 'CYCLING'
        WHEN (DisconnectMinutes.ItemValue <> '0') THEN 'CYCLING'
        ELSE 'ON_DEMAND'
    END
FROM DeviceConfigCategory C 
JOIN DeviceConfigCategoryItem ConnectMinutes ON C.DeviceConfigCategoryId = ConnectMinutes.DeviceConfigCategoryId
JOIN DeviceConfigCategoryItem DisconnectMinutes ON C.DeviceConfigCategoryId = DisconnectMinutes.DeviceConfigCategoryId
JOIN DeviceConfigCategoryItem DisconnectDemandThreshold ON C.DeviceConfigCategoryId = DisconnectDemandThreshold.DeviceConfigCategoryId
WHERE 
    C.CategoryType = 'mctDisconnectConfiguration'
    AND ConnectMinutes.ItemName = 'connectMinutes'
    AND DisconnectMinutes.ItemName = 'disconnectMinutes'
    AND DisconnectDemandThreshold.ItemName = 'disconnectDemandThreshold'
    AND NOT EXISTS (SELECT 1 FROM DeviceConfigCategoryItem DCCI 
                    WHERE C.DeviceConfigCategoryId = DCCI.DeviceConfigCategoryId 
                    AND DCCI.ItemName = 'disconnectMode');
/* End YUK-13323 */

/* Start YUK-13416 */
UPDATE EventLog 
SET EventType = 'system.maintenance.systemLogWeatherDataUpdate' 
WHERE EventType = 'system.mantenance.systemLogWeatherDataUpdate';
/* End YUK-13416 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '31-MAY-2014', 'Latest Update', 1, GETDATE());*/