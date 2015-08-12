/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14427 */
DELETE FROM ExtraPaoPointAssignment 
WHERE Attribute = 'KEEP_ALIVE_TIMER';
/* End YUK-14427 */

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATETIME;
GO

UPDATE PaoLocation
SET LastChangedDate = GETDATE();

ALTER TABLE PaoLocation
ALTER COLUMN LastChangedDate DATETIME NOT NULL;

ALTER TABLE PaoLocation
ADD Origin VARCHAR(64);
GO

UPDATE PaoLocation
SET Origin = 'MANUAL';

ALTER TABLE PaoLocation
ALTER COLUMN Origin VARCHAR(64) NOT NULL;
/* End YUK-14460 */

/* Start YUK-14377 */
DELETE FROM State 
WHERE StateGroupId = -17 
  AND RawState IN (16, 17, 18, 19, 20, 21);

UPDATE State
SET Text = 'Remote'
WHERE StateGroupId = -17
  AND RawState = 14;
/* End YUK-14377 */

/* Start YUK-14530 */
DELETE FROM YukonGroupRole 
WHERE RoleID IN (-107, -206);

DELETE FROM YukonRoleProperty 
WHERE RoleID IN (-107, -206);

DELETE FROM YukonRole 
WHERE RoleId IN (-107, -206);

DROP TABLE EsubDisplayIndex;
/* End YUK-14530 */

/* Start YUK-14537 */
ALTER TABLE InventoryConfigTask
ADD SendOutOfService CHAR(1);
GO

UPDATE InventoryConfigTask
SET SendOutOfService = 'N';

ALTER TABLE InventoryConfigTask
ALTER COLUMN SendOutOfService CHAR(1) NOT NULL;
GO
/* End YUK-14537 */

/* Start YUK-14474 */
UPDATE GlobalSetting 
SET Value = 'eaton_logo.png'
WHERE Name = 'WEB_LOGO_URL'
AND Value = 'CannonLogo.gif';
/* End YUK-14474 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, GETDATE());*/