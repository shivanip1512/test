/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-19587 if YUK-19531 */
/* errors are ignored for an edge case where TotalCount has been dropped already */
/* @error ignore-begin */
ALTER TABLE ScheduledDataImportHistory 
DROP COLUMN TotalCount;
/* @error ignore-end */

INSERT INTO DBUpdates VALUES ('YUK-19587', '7.2.1', GETDATE());
/* @end YUK-19587 */

/* @start YUK-19601 */
CREATE TABLE LMItronCycleGear (
    GearId          numeric         NOT NULL,
    CycleOption     nvarchar(20)    NOT NULL,
    CONSTRAINT PK_LMItronCycleGear PRIMARY KEY (GearId)
);
GO

ALTER TABLE LMItronCycleGear
    ADD CONSTRAINT FK_LMItronCycleGear_LMPDirGear FOREIGN KEY (GearId)
    REFERENCES LMProgramDirectGear (GearID)
    ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-19601', '7.2.1', GETDATE());
/* @end YUK-19601 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '02-FEB-2019', 'Latest Update', 1, GETDATE());*/
