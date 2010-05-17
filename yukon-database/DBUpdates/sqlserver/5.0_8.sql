/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8691 */
/* @error ignore-begin */
INSERT INTO CapControlStrategy VALUES (0, '(none)', '(none)', 0, 'N', 0, 0, 0, 0, 0, 0, 'NYYYYYNN', '(none)', 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 100.0, 100.0, 'N', 0, 'N', '(none)');
INSERT INTO CCHolidayStrategyAssignment
SELECT (YukonPAObject.PAObjectID), -1, 0
FROM YukonPAObject
WHERE Type = 'CCAREA'
AND PAObjectId NOT IN (SELECT PAObjectId
                        FROM CCHolidayStrategyAssignment);
/* @error ignore-end */
/* End YUK-8691 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
