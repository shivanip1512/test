/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8710 */
/* @error ignore-begin */
INSERT INTO DeviceTypeCommand VALUES (-810, -52, 'Repeater 850', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-811, -3, 'Repeater 850', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-812, -53, 'Repeater 850', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-813, -54, 'Repeater 850', 4, 'Y', -1);
/* @error ignore-end */
/* End YUK-8710 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.1', 'Matt K', '20-MAY-2010', 'Latest Update', 7);
