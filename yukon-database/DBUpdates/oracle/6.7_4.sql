/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17638 */
DELETE FROM Widget WHERE DashboardId = -2 AND WidgetType = 'GATEWAY_STREAMING_CAPACITY';
/* End YUK-17638 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.7', '26-JAN-2017', 'Latest Update', 4, SYSDATE);