/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12029 */
UPDATE DeviceGroup
SET ParentDeviceGroupId = 0
WHERE GroupName = 'Monitors' AND ParentDeviceGroupId = 1;

UPDATE DeviceDataMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE DeviceGroupComposedGroup
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE OutageMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE PorterResponseMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE StatusPointMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE TamperFlagMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');
 
UPDATE ValidationMonitor
SET GroupName = SUBSTR(GroupName, 8, LENGTH(GroupName)-7)
WHERE GroupName LIKE ('/Meters/Monitors%');

UPDATE JobProperty
SET value = SUBSTR(value, 8, LENGTH(value)-7)
WHERE name = 'deviceGroup'
  AND value LIKE ('/Meters/Monitors%');
/* End YUK-12029 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 3, SYSDATE);