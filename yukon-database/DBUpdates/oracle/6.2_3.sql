/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13632 */
UPDATE YukonPaobject SET Type = 'RFN-1200' WHERE Type = 'RF-DA';
/* End YUK-13632 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* __YUKON_VERSION__ */