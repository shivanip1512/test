/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-13917 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10803;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10803;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10804;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10804;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10808;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10808;
 
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10807;
 
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10807;
/* End YUK-13917 */

/* Start YUK-13905 */
SELECT * INTO #DemandProfileCategories
FROM DeviceConfigCategory
WHERE CategoryType = 'demandProfile'


/* @start-block */
DECLARE @DeviceConfigCategoryId INT
DECLARE @NextDeviceConfigCategoryId INT
DECLARE @Name VARCHAR(100)
DECLARE @Description VARCHAR(1024)
 
WHILE EXISTS (SELECT * FROM #DemandProfileCategories)
BEGIN
    SET @DeviceConfigCategoryId = 
       (SELECT TOP 1 DeviceConfigCategoryId
        FROM #DemandProfileCategories
        ORDER BY DeviceConfigCategoryId ASC);
 
    /* Create new row (demand) */
    SELECT
        @NextDeviceConfigCategoryId = (SELECT MAX(DeviceConfigCategoryID) + 1 FROM DeviceConfigCategory),
        @Name = Name, 
        @Description = Description 
    FROM DeviceConfigCategory
    WHERE DeviceConfigCategoryId = @DeviceConfigCategoryId;
 
    INSERT INTO DeviceConfigCategory VALUES (
        @NextDeviceConfigCategoryId,
        'demand',
        LEFT(@Name, 84) + ' (Demand Split)',
        LEFT(@Description, 984) + ' (Demand Split)'
    );
 
    UPDATE DeviceConfigCategoryItem 
    SET DeviceConfigCategoryId = @NextDeviceConfigCategoryId
    WHERE DeviceConfigCategoryId = @DeviceConfigCategoryId
    AND ItemName = 'demandInterval';
 
    INSERT INTO DeviceConfigCategoryMap
        SELECT DeviceConfigurationId, @NextDeviceConfigCategoryId
        FROM DeviceConfigCategoryMap
        WHERE DeviceConfigCategoryId = @DeviceConfigCategoryId;
 
    /* Fix current row (profile) */
    UPDATE DeviceConfigCategory SET
        CategoryType = 'profile',
        Name = LEFT(Name, 84) + ' (Profile Split)',
        Description = LEFT(Description, 984) + ' (Profile Split)'
    WHERE DeviceConfigCategoryId = @DeviceConfigCategoryId;
 
    DELETE #DemandProfileCategories
    WHERE DeviceConfigCategoryId = @DeviceConfigCategoryId;
END
/* @end-block */
 
DROP TABLE #DemandProfileCategories;
/* End YUK-13905 */


/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.3', '14-DEC-2014', 'Latest Update', 2, GETDATE());*/