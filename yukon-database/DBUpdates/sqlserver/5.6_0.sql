/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11288 */
sp_rename 'CTIDatabase.DateApplied', 'BuildDate', 'COLUMN';
GO

ALTER TABLE CTIDatabase
DROP COLUMN CTIEmployeeName;
GO

ALTER TABLE CTIDatabase
ADD InstallDate DATETIME;
GO
/* End YUK-11288 */

/* Start YUK-11156 */
ALTER TABLE DynamicCalcHistorical
    DROP CONSTRAINT FK_DynClc_ClcB;
GO

ALTER TABLE DynamicCalcHistorical
    ADD CONSTRAINT FK_DynamicCalcHist_CalcBase FOREIGN KEY (PointId)
        REFERENCES CalcBase (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE DynamicAccumulator
    DROP CONSTRAINT SYS_C0015129;
GO

ALTER TABLE DynamicAccumulator
    ADD CONSTRAINT FK_DynamicAccumulator_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE DynamicPointDispatch
   DROP CONSTRAINT SYS_C0013331;
GO

ALTER TABLE DynamicPointDispatch
    ADD CONSTRAINT FK_DynamicPointDispatch_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE DynamicPointAlarming
    DROP CONSTRAINT FK_DynPtAl_Pt;
GO

ALTER TABLE DynamicPointAlarming
    ADD CONSTRAINT FK_DynamicPointAlarming_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE DynamicTags
    DROP CONSTRAINT FK_DynTgs_Pt;
GO

ALTER TABLE DynamicTags
    ADD CONSTRAINT FK_DynamicTags_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
GO
/* End YUK-11156 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('5.6', '05-DEC-2012', 'Latest Update', 0, GETDATE());*/