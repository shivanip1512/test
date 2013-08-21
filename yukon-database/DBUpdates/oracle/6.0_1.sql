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

/* Start YUK-12405 */
ALTER TABLE DeviceConfiguration 
    ADD CONSTRAINT AK_DeviceConfig_Name UNIQUE (Name);
/* End YUK-12405 */

/* Start YUK-12406 */
ALTER TABLE DeviceConfigCategoryItem
RENAME COLUMN DeviceConfigurationItemId TO DeviceConfigCategoryItemId;
/* End YUK-12406 */

/* Start YUK-12387 */
DROP TABLE CommErrorHistory;
/* End YUK-12387 */

/* Start YUK-12464 */
ALTER TABLE ApplianceCategory
ADD AverageLoad VARCHAR2(32);
/* End YUK-12464 */

/* Start YUK-12230 */
UPDATE Command SET Label = 'Read instant line data' WHERE CommandId = -189;
/* End YUK-12230 */

/* Start YUK-12231 */
INSERT INTO Command VALUES (-192, 'getstatus eventlog', 'Read event log', 'MCT-440-2131B');
INSERT INTO DeviceTypeCommand VALUES (-1017, -192, 'MCT-440-2131B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1018, -192, 'MCT-440-2132B', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1019, -192, 'MCT-440-2133B', 12, 'Y', -1);
/* End YUK-12231 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, SYSDATE);*/