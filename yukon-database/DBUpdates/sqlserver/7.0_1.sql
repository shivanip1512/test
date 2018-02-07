/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17899 */
DROP TABLE MaintenanceTaskSettings;
DROP TABLE MaintenanceTask;

CREATE TABLE MaintenanceTaskSettings (
   SettingType            VARCHAR(50)          NOT NULL,
   Value                  VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_MaintenanceTaskSettings PRIMARY KEY (SettingType)
);
GO

/* End YUK-17899 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '26-JAN-2018', 'Latest Update', 1, GETDATE());*/