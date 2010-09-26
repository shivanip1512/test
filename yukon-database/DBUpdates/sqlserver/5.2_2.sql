/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9057 */
ALTER TABLE SurveyResult
ALTER COLUMN AccountId NUMERIC NULL;
/* End YUK-9057 */ 

/* Start YUK-9022 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20165,-201,'Account Search','true','Enables you to use account searching.');
INSERT INTO YukonRoleProperty VALUES(-20911,-209,'Inventory Search','true','Enables you to use inventory searching.');
/* @error ignore-end */
/* End YUK-9022 */

/* Start YUK-9022 */
DROP INDEX StateGroup.Indx_STATEGRP_Nme;
ALTER TABLE StateGroup ALTER COLUMN Name VARCHAR(60) NOT NULL;
GO
CREATE UNIQUE INDEX Indx_StateGroup_Name_UNQ ON StateGroup (
    NAME ASC
);

/* Add 'RFN Disconnect Status' State and State Groups */
INSERT INTO StateGroup VALUES(-12, 'RFN Disconnect Status', 'Status');
GO

INSERT INTO State VALUES(-12, 0, 'Unknown', 3, 6, 0);
INSERT INTO State VALUES(-12, 1, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-12, 2, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-12, 3, 'Armed', 4, 6, 0);

/* Renaming CRFAddress table to RFNAddress */
DROP INDEX CRFAddress.Indx_CRFAdd_SerNum_Man_Mod_UNQ;
ALTER TABLE CRFAddress
DROP CONSTRAINT FK_CRFAdd_Device;
ALTER TABLE CRFAddress
DROP CONSTRAINT PK_CRFAdd;
GO

EXEC SP_RENAME 'CRFAddress', 'RFNAddress';
GO

ALTER TABLE RFNAddress
ADD CONSTRAINT PK_RFNAdd PRIMARY KEY (DeviceId);
ALTER TABLE RFNAddress
   ADD CONSTRAINT FK_RFNAdd_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
CREATE UNIQUE INDEX Indx_RFNAdd_SerNum_Man_Mod_UNQ ON RFNAddress (
   SerialNumber ASC,
   Manufacturer ASC,
   Model ASC
);
GO

UPDATE YukonPAObject SET Type = 'RFN-AL' WHERE Type = 'CRF-AL';
UPDATE YukonPAObject SET Type = 'RFN-AX' WHERE Type = 'CRF-AX';
/* End YUK-9022 */

/* Start YUK-8976 */
UPDATE YukonRoleProperty
SET DefaultValue = '120'
WHERE RolePropertyId = -10820;
/* End YUK-8976 */

/* Start YUK-9105 */
INSERT INTO YukonRoleProperty 
VALUES(-1605,-7,'PAOName Extension',' ','The extension name of the field appended to PaoName Alias, only applicable when PaoName Alias Uses Extension is true.');

-- Insert a new entry for the PaoName Alias Extension IF the legacy option of Service Location with Position (value= 6) is found.
INSERT INTO YukonGroupRole (GroupRoleId, GroupID, RoleID, RolePropertyID, Value)
SELECT (SELECT MAX(GroupRoleId) 
         FROM YukonGroupRole)+1, -1, -7, -1605, 'positionNumber' 
FROM YukonGroupRole 
WHERE RolePropertyId = -1600 
AND Value = '6';

-- Changing the PaoName Alias role property value from int to enum values. 
-- Note: Only one of these updates should affect 1 row. The rest should affect 0 rows.
UPDATE YukonGroupRole 
SET Value = 'METER_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '0';

UPDATE YukonGroupRole 
SET Value = 'ACCOUNT_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '1';

UPDATE YukonGroupRole 
SET Value = 'SERVICE_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '2';

UPDATE YukonGroupRole 
SET Value = 'CUSTOMER_ID' 
WHERE RolePropertyId = -1600 
AND Value = '3';

UPDATE YukonGroupRole 
SET Value = 'EA_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '4';

UPDATE YukonGroupRole 
SET Value = 'GRID_LOCATION' 
WHERE RolePropertyId = -1600 
AND Value = '5';

UPDATE YukonGroupRole 
SET Value = 'SERVICE_LOCATION' 
WHERE RolePropertyId = -1600
AND Value = '6';

UPDATE YukonGroupRole 
SET Value = 'POLE_NUMBER' 
WHERE RolePropertyId = -1600 
AND Value = '7';
/* End YUK-9105 */

/* Start YUK-9094 */
UPDATE ContactNotification
SET NotificationCategoryId = 5
WHERE NotificationCategoryId = 0
AND ContactNotifId != 0;
/* End YUK-9094 */

/* Start YUK-9086 */
CREATE TABLE StatusPointMonitor (
   StatusPointMonitorId   NUMERIC              NOT NULL,
   StatusPointMonitorName VARCHAR(255)         NOT NULL,
   GroupName              VARCHAR(255)         NOT NULL,
   Attribute              VARCHAR(255)         NOT NULL,
   StateGroupId           NUMERIC              NOT NULL,
   EvaluatorStatus        VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_StatPointMon PRIMARY KEY (StatusPointMonitorId)
);
GO

CREATE UNIQUE INDEX Indx_StatPointMon_MonName_UNQ ON StatusPointMonitor (
    StatusPointMonitorName ASC
);
GO

CREATE TABLE StatusPointMonitorProcessor (
   StatusPointMonitorProcessorId NUMERIC              NOT NULL,
   StatusPointMonitorId          NUMERIC              NULL,
   PrevState                     VARCHAR(255)         NOT NULL,
   NextState                     VARCHAR(255)         NOT NULL,
   ActionType                    VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_StatPointMonProcId PRIMARY KEY (StatusPointMonitorProcessorId)
);
GO

ALTER TABLE StatusPointMonitor
    ADD CONSTRAINT FK_StatPointMon_StateGroup FOREIGN KEY (StateGroupId)
        REFERENCES StateGroup (StateGroupId);

ALTER TABLE StatusPointMonitorProcessor
    ADD CONSTRAINT FK_StatPointMonProc_StatPointM FOREIGN KEY (StatusPointMonitorId)
        REFERENCES StatusPointMonitor (StatusPointMonitorId);
GO

INSERT INTO YukonRoleProperty VALUES(-20217,-202,'Status Point Monitor','false','Controls access to the Status Point Monitor');
/* End YUK-9086 */

/* Start YUK-9064 */
INSERT INTO YukonRoleProperty VALUES(-1115,-2,'Auto Create Login For Additional Contacts','true','Automatically create a default login for each additional contact created on a STARS account.');
/* End YUK-9064 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
