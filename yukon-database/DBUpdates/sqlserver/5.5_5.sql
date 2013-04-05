/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/
 
/* Start YUK-11861 */
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
/* End YUK-11861 */
 
/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('5.5', 'Garrett D', '5-FEB-2013', 'Latest Update', 5 );