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
    EstimatedLoadFormulaId NUMBER NOT NULL,
    Name VARCHAR2(32) NOT NULL,
    FormulaType VARCHAR2(32) NOT NULL,
    CalculationType VARCHAR2(32) NOT NULL,
    FunctionIntercept FLOAT NOT NULL
    CONSTRAINT PK_EstimatedLoadFormula PRIMARY KEY (EstimatedLoadFormulaId)
);

CREATE TABLE EstimatedLoadFunction (
    EstimatedLoadFunctionId NUMBER NOT NULL,
    EstimatedLoadFormulaId NUMBER NOT NULL,
    Name VARCHAR2(32) NOT NULL,
    InputType VARCHAR2(32) NOT NULL,
    InputMin VARCHAR2(32) NOT NULL,
    InputMax VARCHAR2(32) NOT NULL,
    InputPointId NUMBER,
    Quadratic FLOAT NOT NULL,
    Linear FLOAT NOT NULL,
    CONSTRAINT PK_EstimatedLoadFunction PRIMARY KEY (EstimatedLoadFunctionId)
);
ALTER TABLE EstimatedLoadFunction
   ADD CONSTRAINT FK_EstLoadFunction_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadLookupTable (
    EstimatedLoadLookupTableId NUMBER NOT NULL,
    EstimatedLoadFormulaId NUMBER NOT NULL,
    Name VARCHAR2(32) NOT NULL,
    InputType VARCHAR2(32) NOT NULL,
    InputMin VARCHAR2(32)NOT NULL,
    InputMax VARCHAR2(32)NOT NULL,
    InputPointId NUMBER
    CONSTRAINT PK_EstimatedLoadLookupTable PRIMARY KEY (EstimatedLoadLookupTableId)
);
ALTER TABLE EstimatedLoadLookupTable
   ADD CONSTRAINT FK_EstLoadLookup_LoadFormula FOREIGN KEY (EstimatedLoadFormulaId)
      REFERENCES EstimatedLoadFormula (EstimatedLoadFormulaId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadTableEntry (
    EstimatedLoadTableEntryId NUMBER NOT NULL,
    EstimatedLoadLookupTableId NUMBER NOT NULL,
    EntryKey VARCHAR2(32) NOT NULL,
    EntryValue FLOAT NOT NULL
    CONSTRAINT PK_EstimatedLoadTableEntry PRIMARY KEY (EstimatedLoadTableEntryId)
);
ALTER TABLE EstimatedLoadTableEntry
   ADD CONSTRAINT FK_TableEntry_LookupTable FOREIGN KEY (EstimatedLoadLookupTableId)
      REFERENCES EstimatedLoadLookupTable (EstimatedLoadLookupTableId)
         ON DELETE CASCADE;

CREATE TABLE EstimatedLoadFormulaAssignment (
    FormulaAssignmentId NUMBER NOT NULL,
    FormulaId NUMBER NOT NULL,
    GearId NUMBER,
    ApplianceCategoryId NUMBER
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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, SYSDATE);*/