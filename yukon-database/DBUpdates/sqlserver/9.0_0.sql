/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22834 */
/* @error warn-once */
/* @start-block */
BEGIN
    DECLARE @DuplicateZones NUMERIC = (SELECT COUNT(*) FROM (SELECT ZoneName, COUNT(*) AS temp_count FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1) AS temp_table)

IF @DuplicateZones > 0
    BEGIN
        DECLARE @NewLine CHAR(2) = CHAR(13) + CHAR(10);
        DECLARE @ErrorText VARCHAR(1024) = 'IVVC Zone Names are now required to be unique' + @NewLine
            + 'Setup has detected that IVVC Zones with duplicate names are present in the system.' + @NewLine
            + 'In order to proceed with the update this must be manually resolved' + @NewLine
            + 'More information can be found in YUK-22834.' + @NewLine
            + 'To locate Zones that have duplicated names you can use the query below:' + @NewLine
            + 'SELECT ZoneName, COUNT(*) AS NumberOfOccurences FROM Zone GROUP BY ZoneName HAVING COUNT(*) > 1';
        RAISERROR(@ErrorText, 16, 1);
    END;
END;
GO
/* @end-block */
ALTER TABLE Zone ADD CONSTRAINT Ak_ZoneName UNIQUE (ZoneName);

INSERT INTO DBUpdates VALUES ('YUK-22834', '9.0.0', GETDATE());
/* @end YUK-22834 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('9.0', '09-SEP-2020', 'Latest Update', 0, GETDATE()); */