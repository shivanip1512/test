/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11861 */
/* @error ignore-begin */
CREATE INDEX Indx_ADAS_AnalysisId_SlotId ON ArchiveDataAnalysisSlot (
    AnalysisId ASC, 
    SlotId ASC
);
 
CREATE INDEX Indx_ADASV_SlotId_DeviceId ON ArchiveDataAnalysisSlotValue (
    SlotId ASC, 
    DeviceId ASC
);
/* @error ignore-end */
/* End YUK-11861 */

/* Start YUK-11904 */
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-3100', 1326);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-4600', 1327);
INSERT INTO YukonListEntry VALUES ((SELECT MAX(EntryId)+1 FROM YukonListEntry), 1005, 0, 'LCR-4700', 1328);
/* End YUK-11904 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.6', '08-MAR-2013', 'Latest Update', 2, SYSDATE);*/