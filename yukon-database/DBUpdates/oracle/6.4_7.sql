/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14754 */
/* @start-block */
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM USER_TABLES WHERE TABLE_NAME = UPPER('temp_FdrTranslation_Increment');
    
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE('
            CREATE TABLE temp_FdrTranslation_Increment (PointId       NUMBER        NOT NULL, 
                                                        DirectionType VARCHAR2(30)  NOT NULL, 
                                                        Destination   VARCHAR2(20)  NOT NULL, 
                                                        Translation   VARCHAR2(200) NOT NULL, 
                                                        PreOffset     VARCHAR2(200) NOT NULL, 
                                                        PostOffset    VARCHAR2(200) NOT NULL)');
        
        EXECUTE IMMEDIATE('
            INSERT INTO temp_FdrTranslation_Increment (PointId, DirectionType, Destination, Translation, PreOffset, PostOffset)
                SELECT PointId, DirectionType, Destination, Translation,
                    SUBSTR(Translation, 1, INSTR(Translation, '';Offset:'') + 7),
                    SUBSTR(Translation, -1 * (LENGTH(Translation) - INSTR(Translation, '';Offset:'') - 7))
                FROM FDRTranslation 
                WHERE InterfaceType = ''DNPSLAVE'' 
                 AND (DirectionType = ''Receive for control'' OR DirectionType = ''Receive for Analog Output'')');
        
        EXECUTE IMMEDIATE('
            MERGE INTO FDRTranslation orig
            USING (SELECT pointId, DirectionType, Destination,
                      (PreOffset || CAST(CAST(SUBSTR(PostOffset, 1, INSTR(PostOffset, '';'') - 1) AS NUMBER) + 1 AS VARCHAR2(100)) 
                       || SUBSTR(PostOffset, INSTR(PostOffset, '';''), (LENGTH(PostOffset) - INSTR('';'', PostOffset)))) as NewTranslation
                   FROM temp_FdrTranslation_Increment) chopped
            ON     (orig.PointId       = chopped.PointId
                AND orig.DirectionType = chopped.DirectionType
                AND orig.InterfaceType = ''DNPSLAVE''
                AND orig.Destination   = chopped.Destination)
            WHEN MATCHED THEN UPDATE SET orig.Translation = chopped.newTranslation');
    END IF;
END;
/* @end-block */
/* End YUK-14754 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '1-NOV-2015', 'Latest Update', 7, SYSDATE);*/