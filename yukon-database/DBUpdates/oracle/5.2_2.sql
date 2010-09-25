/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9057 */
ALTER TABLE SurveyResult
MODIFY AccountId NUMBER NULL;
/* End YUK-9057 */

/* Start YUK-9022 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-20165,-201,'Account Search','true','Enables you to use account searching.');
INSERT INTO YukonRoleProperty VALUES(-20911,-209,'Inventory Search','true','Enables you to use inventory searching.');
/* @error ignore-end */
/* End YUK-9022 */

/* Start YUK-9022 */
DROP INDEX Indx_STATEGRP_Nme;
ALTER TABLE StateGroup MODIFY Name VARCHAR2(60);
CREATE UNIQUE INDEX Indx_StateGroup_Name_UNQ ON StateGroup (
    NAME ASC
);

/* Add 'RFN Disconnect Status' State and State Groups */
INSERT INTO StateGroup VALUES(-12, 'RFN Disconnect Status', 'Status');

INSERT INTO State VALUES(-12, 0, 'Unknown', 3, 6, 0);
INSERT INTO State VALUES(-12, 1, 'Connected', 0, 6, 0);
INSERT INTO State VALUES(-12, 2, 'Disconnected', 1, 6, 0);
INSERT INTO State VALUES(-12, 3, 'Armed', 4, 6, 0);

/* Renaming CRFAddress table to RFNAddress */
DROP INDEX Indx_CRFAdd_SerNum_Man_Mod_UNQ;
ALTER TABLE CRFAddress
DROP CONSTRAINT FK_CRFAdd_Device;
ALTER TABLE CRFAddress
DROP CONSTRAINT PK_CRFAdd;

ALTER TABLE CRFAddress RENAME TO RFNAddress;

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

/* Update PAObjects */
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

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
