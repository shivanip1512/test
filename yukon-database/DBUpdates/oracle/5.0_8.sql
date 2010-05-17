/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8691 */
/* @error ignore-begin */
INSERT INTO CapControlStrategy VALUES (0, '(none)', '(none)', 0, 'N', 0, 0, 0, 0, 0, 0, 'NYYYYYNN', '(none)', 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 100.0, 100.0, 'N', 0, 'N', '(none)');
/* @error ignore-end */
INSERT INTO CCHolidayStrategyAssignment
SELECT (YukonPAObject.PAObjectID), -1, 0
FROM YukonPAObject
WHERE Type = 'CCAREA'
AND PAObjectId NOT IN (SELECT PAObjectId
                        FROM CCHolidayStrategyAssignment);
/* End YUK-8691 */

/* Start YUK-8710 */
INSERT INTO DeviceTypeCommand VALUES (-810, -52, 'Repeater 850', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-811, -3, 'Repeater 850', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-812, -53, 'Repeater 850', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-813, -54, 'Repeater 850', 4, 'Y', -1);
/* End YUK-8710 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
