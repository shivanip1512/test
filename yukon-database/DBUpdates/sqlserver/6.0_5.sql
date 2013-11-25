/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12765 */
INSERT INTO GlobalSetting (GlobalSettingId, Name, Value, Comments, LastChangedDate)
VALUES ((SELECT ISNULL(MAX(GlobalSettingId)+1,1) FROM GlobalSetting),  
        'CLIENT_APPLICATION_REMEMBER_ME', 'USERNAME_AND_PASSWORD', null, null);
/* End YUK-12765 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '9-DEC-2013', 'Latest Update', 5, GETDATE());*/