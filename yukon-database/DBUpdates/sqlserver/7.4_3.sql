/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22622 */
DECLARE @StartGear AS NUMERIC,
        @ProgramId AS NUMERIC,
        @StartGearNumber AS NUMERIC;
        @GearCount AS NUMERIC;

DECLARE startGearAndProgramIdCursor CURSOR STATIC FOR (
    SELECT StartGear , ProgramID
    FROM LMControlScenarioProgram 
);
/* @start-block */
BEGIN
    OPEN startGearAndProgramIdCursor 
    FETCH NEXT FROM startGearAndProgramIdCursor INTO @StartGear , @ProgramId
    WHILE (@@FETCH_STATUS=0)
    BEGIN
        IF (@StartGear>12)
        BEGIN
            SET @StartGearNumber=(SELECT GearNumber FROM LMProgramDirectGear WHERE GearID=@StartGear)
            UPDATE LMControlScenarioProgram SET StartGear=@StartGearNumber WHERE StartGear=@StartGear AND ProgramID=@ProgramId
        END
        ELSE
        BEGIN
            SET @GearCount=(SELECT Count(GearID) from LMProgramDirectGear where DeviceID=@ProgramID GROUP BY DeviceID)
            IF (@StartGear>@GearCount)
                BEGIN
                    UPDATE LMControlScenarioProgram SET StartGear=1 WHERE StartGear=@StartGear AND ProgramID=@ProgramId
                END
         END
         FETCH NEXT FROM startGearAndProgramIdCursor INTO @StartGear , @ProgramId
         END
    CLOSE startGearAndProgramIdCursor;
    DEALLOCATE startGearAndProgramIdCursor;
END;
/* @end-block */
GO

INSERT INTO DBUpdates VALUES ('YUK-22622', '7.4.3', GETDATE());
/* @end YUK-22622 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.4', '2-SEPT-2020', 'Latest Update', 3, GETDATE());*/