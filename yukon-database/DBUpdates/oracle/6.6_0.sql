/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15182 */
/* @error ignore-begin */
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
/* @error ignore-end */
/* End YUK-15182 */

/* Start YUK-15180 */
DELETE FROM UserPage WHERE Module = 'commercialcurtailment';

UPDATE YukonGroupRole SET Value = '/dr/cc/user/overview' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/user/overview.jsf';

UPDATE YukonGroupRole SET Value = '/dr/cc/home' 
WHERE RolePropertyId = -10800 
AND Value = '/cc/ciSetup.jsf';
/* End YUK-15180 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '31-MAR-2015', 'Latest Update', 0, SYSDATE);*/