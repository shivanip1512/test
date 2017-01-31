/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-16225 */
ALTER TABLE HoneywellWifiThermostat
   ADD CONSTRAINT AK_HONEYWELLWIFITHERMOSTAT_MAC UNIQUE (MacAddress);
GO
/* End YUK-16225 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '19-JAN-2017', 'Latest Update', 3, GETDATE());*/
