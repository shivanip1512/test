/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* @start YUK-22622 */
/* @start-block */
DECLARE @StartGear AS NUMERIC,
        @ProgramId AS NUMERIC,
        @StartGearNumber AS NUMERIC,
        @GearCount AS NUMERIC;

DECLARE startGearAndProgramIdCursor CURSOR STATIC FOR (
    SELECT StartGear, ProgramID
    FROM LMControlScenarioProgram 
);

BEGIN
    OPEN startGearAndProgramIdCursor
    FETCH NEXT FROM startGearAndProgramIdCursor INTO @StartGear, @ProgramId
    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        IF (@StartGear > 12)
        BEGIN
            SET @StartGearNumber = (SELECT GearNumber FROM LMProgramDirectGear WHERE GearID = @StartGear)
            UPDATE LMControlScenarioProgram SET StartGear = @StartGearNumber WHERE StartGear = @StartGear AND ProgramID = @ProgramId
        END
        ELSE
        BEGIN
            SET @GearCount = (SELECT COUNT(GearID) FROM LMProgramDirectGear WHERE DeviceID = @ProgramId GROUP BY DeviceID)
            IF (@StartGear > @GearCount)
                BEGIN
                    UPDATE LMControlScenarioProgram SET StartGear = 1 WHERE StartGear = @StartGear AND ProgramID = @ProgramId
                END
         END
         FETCH NEXT FROM startGearAndProgramIdCursor INTO @StartGear, @ProgramId
         END
    CLOSE startGearAndProgramIdCursor;
    DEALLOCATE startGearAndProgramIdCursor;
END;

GO
/* @end-block */
INSERT INTO DBUpdates VALUES ('YUK-22622', '7.4.3', GETDATE());
/* @end YUK-22622 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.4', '2-OCT-2020', 'Latest Update', 3, GETDATE());