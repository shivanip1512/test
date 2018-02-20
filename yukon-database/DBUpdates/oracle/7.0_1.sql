/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17899 */
DROP TABLE MaintenanceTaskSettings;
DROP TABLE MaintenanceTask;

CREATE TABLE MaintenanceTaskSettings  (
   SettingType            VARCHAR2(50)                    NOT NULL,
   Value                  VARCHAR2(50)                    NOT NULL,
   CONSTRAINT PK_MaintenanceTaskSettings PRIMARY KEY (SettingType)
);
/* End YUK-17899 */

/* Start YUK-17709 */
/* @error ignore-begin */
/* This will almost definitely error, but it's a necessary safety check */
TRUNCATE TABLE t_ConfigCategoryItemTemp;
DROP TABLE t_ConfigCategoryItemTemp;
/* @error ignore-end */

/* Create a temp table */
CREATE GLOBAL TEMPORARY TABLE t_ConfigCategoryItemTemp
(
    DeviceConfigCategoryIdTemp NUMBER,
    ItemValueTemp VARCHAR2(60),
    Ordering NUMBER
) ON COMMIT PRESERVE ROWS;

/* @start-block */
BEGIN
    
    /* Fill the temp table with any items that include 'LAST_INTERVAL_VOLTAGE' and something else that's not 'SLOT_DISABLED' */
    INSERT INTO t_ConfigCategoryItemTemp
    SELECT 
        I.DeviceConfigCategoryId AS DeviceConfigCategoryIdTemp, 
        I.ItemValue AS ItemValueTemp, 
        ROW_NUMBER() OVER ( PARTITION BY I.DeviceConfigCategoryId ORDER BY CAST( SUBSTR( I.ItemName, 12, 2 ) AS NUMBER ) ) AS Ordering
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
    ORDER BY CAST( SUBSTR( I.ItemName, 12, 2 ) AS INT );
    
    /* Fill in the edge cases where the category items only includes 'LAST_INTERVAL_VOLTAGE' and 'SLOT_DISABLED' */
    /* Any such edge cases will end up only having 'SLOT_DISABLED' remaining */
    INSERT INTO t_ConfigCategoryItemTemp
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
    AND I.DeviceConfigCategoryId NOT IN (SELECT DeviceConfigCategoryIdTemp FROM t_ConfigCategoryItemTemp)
    AND I.ItemValue = 'LAST_INTERVAL_VOLTAGE'
    GROUP BY DeviceConfigCategoryId, ItemValue;
    
    /* Now that the temp table is complete, wipe clean all DeviceConfigs that include the 'LAST_INTERVAL_VOLTAGE' item */
    UPDATE DeviceConfigCategoryItem
    SET ItemValue = 'SLOT_DISABLED'
    WHERE DeviceConfigCategoryId IN (
        SELECT DeviceConfigCategoryIdTemp
        FROM t_ConfigCategoryItemTemp
    );
    
    /* Remove the edge case... cases */
    DELETE FROM t_ConfigCategoryItemTemp
    WHERE Ordering = -1;
    
    /* Update the table to match the now sorted temp table, where ordering matches the number after 'displayItemXX' */
    UPDATE DeviceConfigCategoryItem
    SET ItemValue = (
        SELECT T.ItemValueTemp
        FROM t_ConfigCategoryItemTemp T
        WHERE DeviceConfigCategoryId = T.DeviceConfigCategoryIdTemp
        AND ItemName LIKE 'displayItem%'
        AND CAST( SUBSTR( ItemName, 12, 2 ) AS INT ) = Ordering
    )
    WHERE DeviceConfigCategoryId = (
        SELECT T.DeviceConfigCategoryIdTemp
        FROM t_ConfigCategoryItemTemp T
        WHERE DeviceConfigCategoryId = T.DeviceConfigCategoryIdTemp
        AND ItemName LIKE 'displayItem%'
        AND CAST( SUBSTR( ItemName, 12, 2 ) AS INT ) = Ordering
    );

END;
/
/* @end-block */

/* Clean up */
TRUNCATE TABLE t_ConfigCategoryItemTemp;
DROP TABLE t_ConfigCategoryItemTemp;
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
INSERT INTO MaintenanceTaskSettings 
    SELECT 'DUPLICATE_POINT_DATA_PRUNING_ENABLED',
        (SELECT Disabled FROM JOB 
        WHERE BeanName = 'scheduledRphDuplicateDeletionExecutionJobDefinition')
    FROM DUAL 
    WHERE EXISTS (SELECT Disabled FROM JOB 
        WHERE BeanName = 'scheduledRphDuplicateDeletionExecutionJobDefinition');
/* @error ignore-end */
/* End YUK-17652 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '26-JAN-2018', 'Latest Update', 1, SYSDATE);*/
