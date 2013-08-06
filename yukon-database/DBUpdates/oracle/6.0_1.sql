/******************************************/ 
/****     Oracle DBupdates             ****/ 
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
ADD Description VARCHAR2(1024);

ALTER TABLE DeviceConfigCategory 
ADD Description VARCHAR2(1024);
/* End YUK-12415 */

/* Start YUK-12437 */
CREATE TABLE EstimatedLoadFormula (
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(100) NOT NULL,
    FormulaType VARCHAR(32) NOT NULL,
    CalculationType VARCHAR(32) NOT NULL,
    FunctionIntercept NUMERIC NOT NULL,
    PRIMARY KEY (EstimatedLoadFormulaId)
);

CREATE TABLE EstimatedLoadFunction (
    EstimatedLoadFunctionId NUMERIC NOT NULL,
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(100) NOT NULL,
    InputType VARCHAR(32) NOT NULL,
    InputMin VARCHAR(100) NOT NULL,
    InputMax VARCHAR(100) NOT NULL,
    InputPointId NUMERIC NOT NULL,
    Quadratic NUMERIC NOT NULL,
    Linear NUMERIC NOT NULL,
    PRIMARY KEY (EstimatedLoadFunctionId)
);
ALTER TABLE EstimatedLoadFunction
   ADD CONSTRAINT FK_EstLoadFunction_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadLookupTable (
    EstimatedLoadLookupTableId NUMERIC NOT NULL,
    EstimatedLoadFormulaId NUMERIC NOT NULL,
    Name VARCHAR(100) NOT NULL,
    InputType VARCHAR(32) NOT NULL,
    InputMin VARCHAR(100) NOT NULL,
    InputMax VARCHAR(100) NOT NULL,
    InputPointId NUMERIC NOT NULL,
    PRIMARY KEY (EstimatedLoadLookupTableId)
);
ALTER TABLE EstimatedLoadLookupTable
   ADD CONSTRAINT FK_EstLoadLookup_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadTableEntry (
    EstimatedLoadTableEntryId NUMERIC NOT NULL,
    EstimatedLoadLookupTableId NUMERIC NOT NULL,
    EntryKey VARCHAR(100) NOT NULL,
    EntryValue NUMERIC NOT NULL,
    PRIMARY KEY (EstimatedLoadTableEntryId)
);
ALTER TABLE EstimatedLoadTableEntry
   ADD CONSTRAINT FK_TableEntry_LookupTable FOREIGN KEY (EstimatedLoadLookupTableId)
      REFERENCES EstimatedLoadLookupTable (EstimatedLoadLookupTableId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadFormulaAssignment (
    FormulaAssignmentId NUMERIC NOT NULL,
    FormulaId NUMERIC NOT NULL,
    GearId NUMERIC,
    ApplianceCategoryId NUMERIC,
    PRIMARY KEY (FormulaAssignmentId)
);
/* End YUK-12437 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, SYSDATE);*/