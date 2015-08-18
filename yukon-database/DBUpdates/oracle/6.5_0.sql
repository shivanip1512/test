/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14427 */
DELETE FROM ExtraPaoPointAssignment 
WHERE Attribute = 'KEEP_ALIVE_TIMER';
/* End YUK-14427 */

/* Start YUK-14460 */
ALTER TABLE PaoLocation
ADD LastChangedDate DATE;

UPDATE PaoLocation
SET LastChangedDate = SYSDATE;

ALTER TABLE PaoLocation
MODIFY LastChangedDate NOT NULL;

ALTER TABLE PaoLocation
ADD Origin VARCHAR2(64);

UPDATE PaoLocation
SET Origin = 'MANUAL';

ALTER TABLE PaoLocation
MODIFY Origin NOT NULL;
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

UPDATE InventoryConfigTask
SET SendOutOfService = 'N';

ALTER TABLE InventoryConfigTask
MODIFY SendOutOfService CHAR(1) NOT NULL;
/* End YUK-14537 */

/* Start YUK-14474 */
UPDATE GlobalSetting 
SET Value = 'eaton_logo.png'
WHERE Name = 'WEB_LOGO_URL'
AND Value = 'CannonLogo.gif';
/* End YUK-14474 */

/* Start YUK-14433 */
/* @error warn-once */
/* @start-block */
DECLARE
    errorFlagCount INT;
BEGIN
    SELECT COUNT(*) INTO errorFlagCount
    FROM YukonServices YS
    WHERE YS.ServiceID = 3;
    
    IF 0 < errorFlagCount
    THEN 
        RAISE_APPLICATION_ERROR(-20001, 'Yukon Calc Historical Service is enabled. This service must be uninstalled and Yukon Calc-Logic Service used instead.');
    END IF;
END;
/
/* @end-block */
DELETE FROM YukonServices WHERE ServiceId = 3 OR ServiceId = -3;
/* End YUK-14433 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JUL-2015', 'Latest Update', 0, SYSDATE);*/