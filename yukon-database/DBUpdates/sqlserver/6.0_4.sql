/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12728 */
ALTER TABLE FileExportHistory
ADD ArchiveFileExists CHAR(1);
GO
 
UPDATE FileExportHistory
SET ArchiveFileExists = 1;

ALTER TABLE FileExportHistory
ALTER COLUMN ArchiveFileExists CHAR(1) NOT NULL;
/* End YUK-12728 */

/* Start YUK-12742 */
INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
VALUES ((SELECT ISNULL(MAX(GlobalSettingId)+1,1) FROM GlobalSetting),  
        'AD_SSL_ENABLED', 0, null, null);

INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
VALUES ((SELECT ISNULL(MAX(GlobalSettingId)+1,1) FROM GlobalSetting),  
        'LDAP_SSL_ENABLED', 0, null, null);
/* End YUK-12742 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '12-NOV-2013', 'Latest Update', 4, GETDATE());