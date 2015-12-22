/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14940 */
UPDATE DeviceConfigCategory
SET CategoryType= 'lcdConfiguration'
WHERE CategoryType = 'meterParameters';
/* End YUK-14940 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '31-JAN-2016', 'Latest Update', 1, GETDATE());*/