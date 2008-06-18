/* 4.0_0rc2 changes.  These are changes to 4.0 that have been made since 4.0_0rc1*/
/* This script must be run manually using the SQL tool and not the DBToolsFrame tool. */

/* A database that was newly created in verion 3.5 AND already upgraded to 4.0rc1 CANNOT be upgraded to rc2 using 
 * this script.  Manual processing will be needed for upgrading a brand new 3.5 database, that has already been upgraded
 * to r.0rc1, to 4.0rc2. 
 */ 

/* The following block is only included to be run on a database that is being upgrade from version 4.0fc5 or less.
 * If the database has already been upgraded to rc1 , then do not run the following block.
 * Please ask Stacey (x7720) if you have questions on whether this block should be run or not.  
 */
/* START SPECIAL BLOCK */
/* Start YUK-5961 */
ALTER TABLE DeviceGroupMember DROP CONSTRAINT FK_DevGrpMember_DeviceGroup;
go 

ALTER TABLE DeviceGroup DROP CONSTRAINT FK_DeviceGroup_DeviceGroup;
go

CREATE TABLE TempDeviceGroupIdMap ( 
   deviceGroupId NUMERIC(18,0), 
   newDeviceGroupId NUMERIC(18,0) 
); 
go 

INSERT INTO TempDeviceGroupIdMap (deviceGroupId, newDeviceGroupId) 
(SELECT deviceGroupid, deviceGroupid 
 FROM DeviceGroup 
 WHERE deviceGroupId > 11 and deviceGroupId < 101); 
go 

UPDATE tempDeviceGroupIdMap 
SET newDeviceGroupId = deviceGroupId + (SELECT CASE 
                                        WHEN (MAX(deviceGroupId) is NULL) 
                                            THEN 101 
                                        ELSE MAX(deviceGroupId) + 1 
                                        END 
                                        FROM DeviceGroup 
                                        WHERE deviceGroupId > 100);
go 

UPDATE DeviceGroup 
SET parentDeviceGroupId = (SELECT newDeviceGroupId 
                           FROM TempDeviceGroupIdMap DGMAP
                           WHERE deviceGroup.parentDeviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap
                        WHERE parentDeviceGroupId > 11);
go

UPDATE DeviceGroup 
SET deviceGroupId = (SELECT newDeviceGroupId 
                     FROM TempDeviceGroupIdMap DGMAP 
                     WHERE deviceGroup.deviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap);
go

UPDATE DeviceGroupMember 
SET deviceGroupId = (SELECT newDeviceGroupid 
                     FROM TempDeviceGroupIdMap DGMAP 
                     WHERE DeviceGroupMember.deviceGroupId = DGMAP.deviceGroupId) 
WHERE deviceGroupId IN (SELECT DISTINCT deviceGroupId 
                        FROM TempDeviceGroupIdMap);
go 

DROP TABLE TempDeviceGroupIdMap; 
go 

ALTER TABLE DEVICEGROUP
    ADD CONSTRAINT FK_DeviceGroup_DeviceGroup FOREIGN KEY (parentDeviceGroupId)
        REFERENCES DeviceGroup (deviceGroupId);
go

ALTER TABLE DEVICEGROUPMEMBER 
   ADD CONSTRAINT FK_DevGrpMember_DeviceGroup foreign key (deviceGroupId) 
      REFERENCES DEVICEGROUP (deviceGroupId); 
go

DELETE FROM SequenceNumber 
WHERE SequenceName = 'DeviceGroup'; 

/* @start-block */
DECLARE @dGSequenceNumber INT; 
SET @dGSequenceNumber = CAST((SELECT CASE  
                                  WHEN (MAX(DeviceGroupId) IS NULL)
                                      THEN 100 
                                  ELSE 
                                      MAX(deviceGroupId) 
                                  END 
                              FROM DeviceGroup 
                              WHERE deviceGroupId > 100) as INT);


INSERT INTO SequenceNumber 
VALUES (@dGSequenceNumber, 'DeviceGroup');
/* @end-block */
go
/* End YUK-5961 */

/* END SPECIAL block */



/* Start YUK-4744 */
update DynamicBillingFormat set footer = ' ' WHERE formatId = 31;
update DynamicBillingFormat set header = ' ', footer = ' ' where formatId = 21; 

update DynamicBillingField set fieldFormat = ' ' where id = 2;
update DynamicBillingField set fieldFormat = ' ' where id = 10;
/* End YUK-4744 */


/* Start YUK-5963 */
/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'CCurtProgram' AND COLUMN_NAME = 'lastIdentifier')
    ALTER TABLE CCurtProgram ADD lastIdentifier NUMERIC;
go
/* @end-block */

UPDATE CCurtProgram 
SET lastIdentifier = 0
WHERE lastIdentifier IS NULL;
go

ALTER TABLE CCurtProgram ALTER COLUMN lastIdentifier NUMERIC NOT NULL;
go

/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'CCurtProgram' AND COLUMN_NAME = 'identifierPrefix')
ALTER TABLE CCurtProgram ADD identifierPrefix VARCHAR(32);
go
/* @end-block */

UPDATE CCurtProgram 
SET identifierPrefix = 'PROG-'
WHERE identifierPrefix IS NULL;

ALTER TABLE CCurtProgram ALTER COLUMN identifierPrefix VARCHAR(32) NOT NULL;
go
/* End YUK-5963 */

/* Start YUK-5960 */ 
ALTER TABLE State DROP CONSTRAINT SYS_C0013342; 

ALTER TABLE Point DROP CONSTRAINT Ref_STATGRP_PT;
go

UPDATE StateGroup 
SET stateGroupId = -9 
WHERE stateGroupId = 2;
go

UPDATE Point 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 

UPDATE State 
SET stateGroupId = -9 
WHERE stateGroupId = 2; 
go

ALTER TABLE Point 
    ADD CONSTRAINT Ref_STATGRP_PT FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 

ALTER TABLE State 
    ADD CONSTRAINT SYS_C0013342 FOREIGN KEY (stateGroupId) 
        REFERENCES StateGroup (stateGroupId); 
go
/* End YUK-5960 */

/* Start YUK-6004 */
/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'dynamicCCCapBank' AND COLUMN_NAME = 'twoWayCBCLastControl')
ALTER TABLE DynamicCCCapBank ADD twoWayCBCLastControl NUMERIC;
go
/* @end-block */

UPDATE DynamicCCCapBank 
SET twoWayCBCLastControl = 0
WHERE twoWayCBCLastControl IS NULL;

ALTER TABLE DynamicCCCapBank ALTER COLUMN twoWayCBCLastControl NUMERIC NOT NULL;
go
/* End YUK-6004 */

/* Start YUK-6006 */
/* @start-block */
IF NOT EXISTS(SELECT * 
              FROM INFORMATION_SCHEMA.COLUMNS
              WHERE TABLE_NAME = 'dynamicCCFeeder' AND COLUMN_NAME = 'retryIndex')
ALTER TABLE DynamicCCFeeder ADD retryIndex NUMERIC;
go
/* @end-block */

UPDATE DynamicCCFeeder 
SET retryIndex = 0
WHERE retryIndex IS NULL;

ALTER TABLE DynamicCCFeeder ALTER COLUMN retryIndex NUMERIC NOT NULL;
go
/* End YUK-6006 */

/* Start YUK-5999*/
INSERT INTO YukonRoleProperty VALUES(-100013, -1000, 'Three Phase', 'false', 'display 3-phase data for sub bus');
/* End YUK-5999 */
