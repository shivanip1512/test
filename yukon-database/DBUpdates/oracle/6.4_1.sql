/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14171 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20903;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20903;
/* End YUK-14171 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAR-2015', 'Latest Update', 1, SYSDATE);*/