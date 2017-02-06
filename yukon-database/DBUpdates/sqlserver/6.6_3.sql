/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-16225 */
ALTER TABLE HoneywellWifiThermostat
   ADD CONSTRAINT AK_HONEYWELLWIFITHERMOSTAT_MAC UNIQUE (MacAddress);
GO
/* End YUK-16225 */

/* Start YUK-16246 */
UPDATE DynamicPAOInfo
SET Value = REPLACE(Value, 'Schedule ', 'SCHEDULE_')
WHERE Value LIKE 'Schedule %'
AND InfoKey IN (
    'rfn monday schedule',
    'rfn tuesday schedule',
    'rfn wednesday schedule',
    'rfn thursday schedule',
    'rfn friday schedule',
    'rfn saturday schedule',
    'rfn sunday schedule',
    'rfn holiday schedule'
);
/* End YUK-16246 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.6', '19-JAN-2017', 'Latest Update', 3, GETDATE());*/
