/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13478 */
ALTER TABLE YukonServices
ADD ServiceType VARCHAR(60);
GO

UPDATE YukonServices
SET ServiceType = 
    CASE
       WHEN ServiceId <= 6 THEN 'CLASS_NAME_TYPE'
       WHEN ServiceId > 6 THEN 'CONTEXT_FILE_TYPE'
    END;

ALTER TABLE YukonServices
ALTER COLUMN ServiceType VARCHAR(60) NOT NULL;
/* End YUK-13478 */

/* Start YUK-13356 */
UPDATE YukonRoleProperty 
SET DefaultValue = '/dashboard' 
WHERE RolePropertyId = -10800;

UPDATE YukonGroupRole 
SET Value = '/dashboard' 
WHERE RolePropertyId = -10800 
  AND Value = '/operator/Operations.jsp';
/* End YUK-13356 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '27-JUN-2014', 'Latest Update', 2, GETDATE());*/