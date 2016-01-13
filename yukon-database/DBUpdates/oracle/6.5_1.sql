/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14940 */
UPDATE DeviceConfigCategory
SET CategoryType= 'lcdConfiguration'
WHERE CategoryType = 'meterParameters';
/* End YUK-14940 */

/* Start YUK-14987 */
INSERT INTO StateGroup VALUES(-20, 'Ignored Control', 'Status');

INSERT INTO State VALUES(-20, 0, 'Manual', 0, 6, 0);
INSERT INTO State VALUES(-20, 1, 'SCADA Override', 1, 6, 0);
INSERT INTO State VALUES(-20, 2, 'Fault Current', 2, 6, 0);
INSERT INTO State VALUES(-20, 3, 'Emergency Voltage', 3, 6, 0);
INSERT INTO State VALUES(-20, 4, 'Time ONOFF', 4, 6, 0);
INSERT INTO State VALUES(-20, 5, 'OVUV Control', 5, 6, 0);
INSERT INTO State VALUES(-20, 6, 'VAR', 7, 6, 0);
INSERT INTO State VALUES(-20, 7, 'Va', 8, 6, 0);
INSERT INTO State VALUES(-20, 8, 'Vb', 1, 6, 0);
INSERT INTO State VALUES(-20, 9, 'Vc', 2, 6, 0);
INSERT INTO State VALUES(-20, 10, 'Ia', 3, 6, 0);
INSERT INTO State VALUES(-20, 11, 'Ib', 4, 6, 0);
INSERT INTO State VALUES(-20, 12, 'Ic', 5, 6, 0);
INSERT INTO State VALUES(-20, 13, 'Temp', 7, 6, 0);
INSERT INTO State VALUES(-20, 14, 'Remote', 8, 6, 0);
INSERT INTO State VALUES(-20, 15, 'Time', 1, 6, 0);
INSERT INTO State VALUES(-20, 17, 'Bad Active Relay', 2, 6, 0);
INSERT INTO State VALUES(-20, 18, 'NC Lockout', 3, 6, 0);
INSERT INTO State VALUES(-20, 20, 'Auto Mode', 4, 6, 0);
INSERT INTO State VALUES(-20, 21, 'Reclose Block', 5, 6, 0);
/* End YUK-14987 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JAN-2015', 'Latest Update', 1, SYSDATE);*/