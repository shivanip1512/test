/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-16039 */
INSERT INTO StateGroup VALUES(-28, 'ThermostatRelayState', 'Status');
INSERT INTO State VALUES(-28, 0, 'Heat', 0, 6, 0);
INSERT INTO State VALUES(-28, 1, 'Cool', 1, 6, 0);
INSERT INTO State VALUES(-28, 2, 'Off', 2, 6, 0);
/* End YUK-16039 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '05-DEC-2016', 'Latest Update', 1, SYSDATE);*/
