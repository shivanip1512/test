/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-20819 */
ALTER TABLE DeviceMacAddress
ADD SecondaryMacAddress varchar(255) null;
GO

INSERT INTO DBUpdates VALUES ('YUK-20819', '7.3.2', GETDATE());
/* @end YUK-20819 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.3', '29-OCT-2019', 'Latest Update', 2, GETDATE());*/