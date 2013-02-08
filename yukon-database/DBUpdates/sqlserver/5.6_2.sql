/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11861 */
/* @error ignore-begin */
CREATE INDEX Indx_ADAS_AnalysisId_SlotId ON ArchiveDataAnalysisSlot (
    AnalysisId ASC, 
    SlotId ASC
);
GO
 
CREATE INDEX Indx_ADASV_SlotId_DeviceId ON ArchiveDataAnalysisSlotValue (
    SlotId ASC, 
    DeviceId ASC
);
GO
/* @error ignore-end */
/* End YUK-11861 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.6', '08-MAR-2013', 'Latest Update', 2, GETDATE());