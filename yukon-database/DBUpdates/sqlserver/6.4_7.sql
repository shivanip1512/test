/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14754 */
/* @start-block */
IF (SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'temp_FdrTranslation_Increment') IS NULL
BEGIN
    CREATE TABLE temp_FdrTranslation_Increment
    (
        PointId       NUMERIC      NOT NULL, 
        DirectionType VARCHAR(30)  NOT NULL, 
        Destination   VARCHAR(20)  NOT NULL, 
        Translation   VARCHAR(200) NOT NULL,
        PreOffset     VARCHAR(200) NOT NULL,
        PostOffset    VARCHAR(200) NOT NULL
    );
    
    INSERT INTO temp_FdrTranslation_Increment (PointId, DirectionType, Destination, Translation, PreOffset, PostOffset)
        SELECT PointId, DirectionType, Destination, Translation,
            LEFT(Translation, CHARINDEX(';Offset:', Translation)),
            RIGHT(Translation, LEN(Translation) - CHARINDEX(';Offset:', Translation) - 7) 
        FROM FDRTranslation 
        WHERE InterfaceType = 'DNPSLAVE' 
         AND (DirectionType = 'Receive for control' OR DirectionType = 'Receive for Analog Output');
    
    UPDATE FDRTranslation 
    SET Translation =
        CONCAT(
            chopped.PreOffset, 
            'Offset:', 
            CAST(LEFT(chopped.PostOffset, CHARINDEX(';', chopped.PostOffset) - 1) AS NUMERIC) + 1, 
            ';', 
            RIGHT(chopped.PostOffset, LEN(chopped.PostOffset) - CHARINDEX(';', chopped.PostOffset)))
    FROM temp_FdrTranslation_Increment chopped 
    INNER JOIN FDRTranslation orig ON (orig.PointId       = chopped.PointId 
                                   AND orig.DirectionType = chopped.DirectionType
                                   AND orig.InterfaceType = 'DNPSLAVE'
                                   AND orig.Destination   = chopped.Destination); 
END;
/* @end-block */
/* End YUK-14754 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '1-NOV-2015', 'Latest Update', 7, GETDATE());*/