/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13699 */
DELETE FROM DeviceTypeCommand
WHERE DeviceCommandID IN (
    SELECT DeviceCommandId 
    FROM DeviceTypeCommand dtc
    JOIN Command c ON c.CommandID = dtc.CommandID
    WHERE c.Command = 'putstatus reset'
      AND dtc.DeviceType != 'CCU-711');
/* End YUK-13699 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.2', '09-OCT-2014', 'Latest Update', 5, GETDATE());