/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15052 */
INSERT INTO Device
SELECT PaobjectId, 'N', 'N'
FROM YukonPAObject
WHERE Type = 'WEATHER LOCATION'
  AND PAObjectID NOT IN (SELECT DeviceID FROM Device);
/* End YUK-15052 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.4', '05-FEB-2016', 'Latest Update', 9, SYSDATE);