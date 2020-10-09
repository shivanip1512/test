/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-22622 */
/* @start-block */
DECLARE 
    Start_Gear VARCHAR(10);
    Program_Id VARCHAR(10);
    Start_Gear_Number VARCHAR(10);
    Gear_Count VARCHAR(10);
    CURSOR startGearAndProgramIdCursor IS SELECT StartGear, ProgramID FROM LMControlScenarioProgram;

BEGIN
    OPEN startGearAndProgramIdCursor;
    IF startGearAndProgramIdCursor%Found THEN
        LOOP
            FETCH startGearAndProgramIdCursor INTO Start_Gear, Program_Id;
            IF Start_Gear > '12' THEN
                SELECT GearNumber INTO Start_Gear_Number FROM LMProgramDirectGear WHERE GearID = Start_Gear;
                UPDATE LMControlScenarioProgram 
                SET StartGear = Start_Gear_Number
                WHERE StartGear = Start_Gear AND ProgramID = Program_Id;
            ELSE
                SELECT Count(GearID) INTO Gear_Count FROM LMProgramDirectGear WHERE DeviceID = Program_Id GROUP BY DeviceID;
                IF(Start_Gear > Gear_Count) THEN
                    UPDATE LMControlScenarioProgram SET StartGear = 1 WHERE StartGear = Start_Gear AND ProgramID = Program_Id;
                END IF;
            END IF; 
            EXIT WHEN startGearAndProgramIdCursor%NOTFOUND; 
        END LOOP;
    END IF;
    CLOSE startGearAndProgramIdCursor;
END;
/* @end-block */
INSERT INTO DBUpdates VALUES ('YUK-22622', '7.4.3', SYSDATE);
/* @end YUK-22622 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('7.4', '2-OCT-2020', 'Latest Update', 3, SYSDATE);