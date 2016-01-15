/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14940 */
UPDATE DeviceConfigCategory
SET CategoryType= 'lcdConfiguration'
WHERE CategoryType = 'meterParameters';
/* End YUK-14940 */

/* Start YUK-14987 */
/* @error ignore-begin */
INSERT INTO StateGroup VALUES(-20, 'Ignored Control', 'Status');

INSERT INTO State VALUES(-20, 0, 'Manual', 1, 6, 0);
INSERT INTO State VALUES(-20, 1, 'SCADA Override', 2, 6, 0);
INSERT INTO State VALUES(-20, 2, 'Fault Current', 3, 6, 0);
INSERT INTO State VALUES(-20, 3, 'Emergency Voltage', 4, 6, 0);
INSERT INTO State VALUES(-20, 4, 'Time ONOFF', 5, 6, 0);
INSERT INTO State VALUES(-20, 5, 'OVUV Control', 7, 6, 0);
INSERT INTO State VALUES(-20, 6, 'VAR', 8, 6, 0);
INSERT INTO State VALUES(-20, 7, 'Va', 9, 6, 0);
INSERT INTO State VALUES(-20, 8, 'Vb', 10, 6, 0);
INSERT INTO State VALUES(-20, 9, 'Vc', 1, 6, 0);
INSERT INTO State VALUES(-20, 10, 'Ia', 2, 6, 0);
INSERT INTO State VALUES(-20, 11, 'Ib', 3, 6, 0);
INSERT INTO State VALUES(-20, 12, 'Ic', 4, 6, 0);
INSERT INTO State VALUES(-20, 13, 'Temp', 5, 6, 0);
INSERT INTO State VALUES(-20, 15, 'Time', 7, 6, 0);
INSERT INTO State VALUES(-20, 17, 'Bad Active Relay', 8, 6, 0);
INSERT INTO State VALUES(-20, 18, 'NC Lockout', 9, 6, 0);
INSERT INTO State VALUES(-20, 20, 'Auto Mode', 10, 6, 0);
INSERT INTO State VALUES(-20, 21, 'Reclose Block', 1, 6, 0);
/* @error ignore-end */
/* End YUK-14987 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JAN-2016', 'Latest Update', 1, GETDATE());*/