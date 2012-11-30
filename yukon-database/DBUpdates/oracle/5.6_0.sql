/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11288 */
ALTER TABLE CTIDatabase RENAME COLUMN DateApplied TO BuildDate;

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;

ALTER TABLE CTIDatabase
ADD InstallDate DATE;
/* End YUK-11288 */

/* Start YUK-11156 */
ALTER TABLE DynamicCalcHistorical
    DROP CONSTRAINT FK_DynClc_ClcB;

ALTER TABLE DynamicCalcHistorical
    ADD CONSTRAINT FK_DynamicCalcHist_CalcBase FOREIGN KEY (PointId)
        REFERENCES CalcBase (PointId)
            ON DELETE CASCADE;

ALTER TABLE DynamicAccumulator
    DROP CONSTRAINT SYS_C0015129;

ALTER TABLE DynamicAccumulator
    ADD CONSTRAINT FK_DynamicAccumulator_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;

ALTER TABLE DynamicPointDispatch
   DROP CONSTRAINT SYS_C0013331;

ALTER TABLE DynamicPointDispatch
    ADD CONSTRAINT FK_DynamicPointDispatch_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;

ALTER TABLE DynamicPointAlarming
    DROP CONSTRAINT FK_DynPtAl_Pt;

ALTER TABLE DynamicPointAlarming
    ADD CONSTRAINT FK_DynamicPointAlarming_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;

ALTER TABLE DynamicTags
    DROP CONSTRAINT FK_DynTgs_Pt;

ALTER TABLE DynamicTags
    ADD CONSTRAINT FK_DynamicTags_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
/* End YUK-11156 */

/* Start YUK-11081 */
DROP TABLE LMThermostatSeasonEntry;
DROP TABLE LMThermostatSeason;
DROP TABLE LMThermostatSchedule;
/* End YUK-11081 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.6', '05-DEC-2012', 'Latest Update', 0, SYSDATE);*/ 