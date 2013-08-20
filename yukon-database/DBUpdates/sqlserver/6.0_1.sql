/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-12368 */
UPDATE CalcBase SET UpdateType = 'On First Change' WHERE UpdateType = 'ON_FIRST_CHANGE';
UPDATE CalcBase SET UpdateType = 'On All Change' WHERE UpdateType = 'ON_ALL_CHANGE';
UPDATE CalcBase SET UpdateType = 'On Timer' WHERE UpdateType = 'ON_TIMER';
UPDATE CalcBase SET UpdateType = 'On Timer+Change' WHERE UpdateType = 'ON_TIMER_CHANGE';
UPDATE CalcBase SET UpdateType = 'Constant' WHERE UpdateType = 'CONSTANT';
UPDATE CalcBase SET UpdateType = 'Historical' WHERE UpdateType = 'HISTORICAL';
/* End YUK-12368 */

/* Start YUK-12415 */
ALTER TABLE DeviceConfiguration 
ADD Description VARCHAR(1024);
GO
ALTER TABLE DeviceConfigCategory 
ADD Description VARCHAR(1024);
GO
/* End YUK-12415 */

/* Start YUK-12437 */
CREATE TABLE EstimatedLoadFormula (
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(32) NOT NULL,
    FormulaType VARCHAR(32) NOT NULL,
    CalculationType VARCHAR(32) NOT NULL,
    FunctionIntercept FLOAT NOT NULL
    CONSTRAINT PK_EstimatedLoadFormula PRIMARY KEY (EstimatedLoadFormulaId)
);

CREATE TABLE EstimatedLoadFunction (
    EstimatedLoadFunctionId NUMERIC NOT NULL,
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(32) NOT NULL,
    InputType VARCHAR(32) NOT NULL,
    InputMin VARCHAR(32) NOT NULL,
    InputMax VARCHAR(32) NOT NULL,
    InputPointId NUMERIC,
    Quadratic FLOAT NOT NULL,
    Linear FLOAT NOT NULL,
    CONSTRAINT PK_EstimatedLoadFunction PRIMARY KEY (EstimatedLoadFunctionId)
);
ALTER TABLE EstimatedLoadFunction
   ADD CONSTRAINT FK_EstLoadFunction_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadLookupTable (
    EstimatedLoadLookupTableId NUMERIC NOT NULL,
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(32) NOT NULL,
    InputType VARCHAR(32) NOT NULL,
    InputMin VARCHAR(32)NOT NULL,
    InputMax VARCHAR(32)NOT NULL,
    InputPointId NUMERIC
    CONSTRAINT PK_EstimatedLoadLookupTable PRIMARY KEY (EstimatedLoadLookupTableId)
);
ALTER TABLE EstimatedLoadLookupTable
   ADD CONSTRAINT FK_EstLoadLookup_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadTableEntry (
    EstimatedLoadTableEntryId NUMERIC NOT NULL,
    EstimatedLoadLookupTableId NUMERIC NOT NULL,
    EntryKey VARCHAR(32) NOT NULL,
    EntryValue FLOAT NOT NULL
    CONSTRAINT PK_EstimatedLoadTableEntry PRIMARY KEY (EstimatedLoadTableEntryId)
);
ALTER TABLE EstimatedLoadTableEntry
   ADD CONSTRAINT FK_EstLoadTableEntry_LookupTab FOREIGN KEY (EstimatedLoadLookupTableId)
      REFERENCES EstimatedLoadLookupTable (EstimatedLoadLookupTableId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadFormulaAssignment (
    FormulaAssignmentId NUMERIC NOT NULL,
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    GearId NUMERIC,
    ApplianceCategoryId NUMERIC
    CONSTRAINT PK_EstimatedLoadFormulaAssignm PRIMARY KEY (FormulaAssignmentId)
);

ALTER TABLE EstimatedLoadFormulaAssignment 
   ADD CONSTRAINT FK_FormulaAssign_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

ALTER TABLE EstimatedLoadFormulaAssignment 
   ADD CONSTRAINT FK_FormulaAssign_LmProgramGear FOREIGN KEY (GearId)
      REFERENCES LMProgramDirectGear (GearId)
         ON DELETE CASCADE;

ALTER TABLE EstimatedLoadFormulaAssignment 
   ADD CONSTRAINT FK_FormulaAssign_ApplianceCat FOREIGN KEY (ApplianceCategoryId)
      REFERENCES ApplianceCategory (ApplianceCategoryId)
         ON DELETE CASCADE;
/* End YUK-12437 */

/* Start YUK-12405 */
ALTER TABLE DeviceConfiguration 
    ADD CONSTRAINT AK_DeviceConfig_Name UNIQUE (Name);
GO
/* End YUK-12405 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, GETDATE());*/