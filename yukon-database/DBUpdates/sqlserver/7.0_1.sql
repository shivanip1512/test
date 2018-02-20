/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17899 */
DROP TABLE MaintenanceTaskSettings;
DROP TABLE MaintenanceTask;
GO

CREATE TABLE MaintenanceTaskSettings (
   SettingType            VARCHAR(50)          NOT NULL,
   Value                  VARCHAR(50)          NOT NULL,
   CONSTRAINT PK_MaintenanceTaskSettings PRIMARY KEY (SettingType)
);
GO
/* End YUK-17899 */

/* Start YUK-17709 */
/* @start-block */
BEGIN

    /* Create a temp table with any items that include 'LAST_INTERVAL_VOLTAGE' and something else that's not 'SLOT_DISABLED' */
    SELECT 
        I.DeviceConfigCategoryId AS DeviceConfigCategoryIdTemp, 
        I.ItemValue AS ItemValueTemp, 
        ROW_NUMBER() OVER ( PARTITION BY I.DeviceConfigCategoryId ORDER BY CAST( SUBSTRING( I.ItemName, 12, 2 ) AS INT ) ) AS Ordering
    INTO #ConfigCategoryItemTemp
    FROM DeviceConfigCategoryItem I
    WHERE I.DeviceConfigCategoryId IN (
        SELECT C.DeviceConfigCategoryId
        FROM DeviceConfigCategory C
        JOIN DeviceConfigCategoryItem CI
            ON C.DeviceConfigCategoryId = CI.DeviceConfigCategoryId
        WHERE C.CategoryType = 'centron410DisplayItems'
        AND CI.ItemValue = 'LAST_INTERVAL_VOLTAGE'
    )
    AND I.ItemValue NOT IN( 'SLOT_DISABLED', 'LAST_INTERVAL_VOLTAGE' )
    AND ItemName LIKE 'displayItem%'
    ORDER BY CAST( SUBSTRING( I.ItemName, 12, 2 ) AS INT );
    
    /* Fill in the edge cases where the category items only includes 'LAST_INTERVAL_VOLTAGE' and 'SLOT_DISABLED' */
    /* Any such edge cases will end up only having 'SLOT_DISABLED' remaining */
    INSERT INTO #ConfigCategoryItemTemp
    SELECT 
        I.DeviceConfigCategoryId AS DeviceConfigCategoryIdTemp, 
        'DELETE_THIS' AS ItemValueTemp, 
        -1 AS Ordering
    FROM DeviceConfigCategoryItem I
    WHERE I.DeviceConfigCategoryId IN (
        SELECT C.DeviceConfigCategoryId
        FROM DeviceConfigCategory C
        JOIN DeviceConfigCategoryItem CI
            ON C.DeviceConfigCategoryId = CI.DeviceConfigCategoryId
        WHERE C.CategoryType = 'centron410DisplayItems'
        AND CI.ItemValue = 'LAST_INTERVAL_VOLTAGE'
    )
    AND I.DeviceConfigCategoryId NOT IN (SELECT DeviceConfigCategoryIdTemp FROM #ConfigCategoryItemTemp)
    AND I.ItemValue = 'LAST_INTERVAL_VOLTAGE'
    GROUP BY DeviceConfigCategoryId, ItemValue;

    /* Now that the temp table is complete, wipe clean all DeviceConfigs that include the 'LAST_INTERVAL_VOLTAGE' item */
    UPDATE DeviceConfigCategoryItem
    SET ItemValue = 'SLOT_DISABLED'
    WHERE DeviceConfigCategoryId IN (
        SELECT DeviceConfigCategoryIdTemp
        FROM #ConfigCategoryItemTemp
    );
    
    /* Remove the edge cases from the temp table */
    DELETE FROM #ConfigCategoryItemTemp
    WHERE Ordering = -1;
    
    /* Update the table to match the now sorted temp table, where ordering matches the number after 'displayItemXX' */
    UPDATE DeviceConfigCategoryItem
    SET ItemValue = (
        SELECT T.ItemValueTemp
        FROM #ConfigCategoryItemTemp T
        WHERE DeviceConfigCategoryId = T.DeviceConfigCategoryIdTemp
        AND ItemName LIKE 'displayItem%'
        AND CAST( SUBSTRING( ItemName, 12, 2 ) AS INT ) = Ordering
    )
    WHERE DeviceConfigCategoryId = (
        SELECT T.DeviceConfigCategoryIdTemp
        FROM #ConfigCategoryItemTemp T
        WHERE DeviceConfigCategoryId = T.DeviceConfigCategoryIdTemp
        AND ItemName LIKE 'displayItem%'
        AND CAST( SUBSTRING( ItemName, 12, 2 ) AS INT ) = Ordering
    );
    
    /* Clean up */
    DROP TABLE #ConfigCategoryItemTemp;

END;
/* @end-block */
/* End YUK-17709 */

/* Start YUK-17906 */
DELETE FROM DeviceConfigCategoryMap
WHERE DeviceConfigurationId IN (
    SELECT dc.DeviceConfigurationID
    FROM DeviceConfiguration dc
    WHERE NOT EXISTS (
        SELECT 1 FROM DeviceConfigDeviceTypes dcdt
        WHERE dcdt.DeviceConfigurationId = dc.DeviceConfigurationID
        AND dcdt.PaoType IN ('MCT-430A', 'MCT-430A3', 'MCT-430S4', 'MCT-430SL', 'MCT-470')
    )
)
AND DeviceConfigCategoryId IN (
    SELECT dcc.DeviceConfigCategoryId
    FROM DeviceConfigCategory dcc
    WHERE dcc.CategoryType = 'demand'
);
/* End YUK-17906 */

/* Start YUK-17652 */
/* @error ignore-begin */
/* @start-block */
DECLARE @count NUMERIC;
BEGIN
SELECT @count = (SELECT COUNT(*) FROM JOB WHERE BeanName = 'scheduledRphDuplicateDeletionExecutionJobDefinition' AND disabled='Y')
IF (@count = 1)
    BEGIN 
        INSERT INTO MaintenanceTaskSettings 
        VALUES ('DUPLICATE_POINT_DATA_PRUNING_ENABLED','false');
    END
END;
GO
/* @end-block */
/* @error ignore-end */
/* End YUK-17652 */
	
/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '26-JAN-2018', 'Latest Update', 1, GETDATE());*/