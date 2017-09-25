/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17248 */
CREATE INDEX Indx_YukonUser_UserGroupId ON YukonUser (
UserGroupId ASC
);
GO
/* End YUK-17248 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '01-AUG-2017', 'Latest Update', 2, GETDATE());*/