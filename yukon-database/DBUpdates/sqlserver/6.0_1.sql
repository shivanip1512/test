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

ALTER TABLE EstimatedLoadFunction 
   ADD CONSTRAINT FK_EstLoadFunction_Point FOREIGN KEY (InputPointId)
      REFERENCES Point (PointId);

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

ALTER TABLE EstimatedLoadLookupTable 
   ADD CONSTRAINT FK_EstLoadLookup_Point FOREIGN KEY (InputPointId)
      REFERENCES Point (PointId);

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

/* Start YUK-12406 */
sp_rename 'DeviceConfigCategoryItem.DeviceConfigurationItemId', 'DeviceConfigCategoryItemId', 'COLUMN';
GO
/* End YUK-12406 */

/* Start YUK-12387 */
DROP TABLE CommErrorHistory;
GO
/* End YUK-12387 */

/* Start YUK-12464 */
ALTER TABLE ApplianceCategory
ADD AverageLoad VARCHAR(32);
GO

UPDATE ApplianceCategory
SET AverageLoad = '1.0';

ALTER TABLE ApplianceCategory
ALTER COLUMN AverageLoad VARCHAR(32) NOT NULL;
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

/* Start YUK-12469 */
CREATE TABLE UserPage (
    UserPageId  NUMERIC NOT NULL,
    UserId      NUMERIC NOT NULL,
    PagePath    VARCHAR(2048) NOT NULL,
    Module      VARCHAR(32) NOT NULL,
    PageName    VARCHAR(32) NOT NULL,
    Category    VARCHAR(32) NOT NULL,
    Timestamp   DATETIME NOT NULL,
    CONSTRAINT PK_UserPageTable PRIMARY KEY (UserPageId)
);
GO

ALTER TABLE UserPage
   ADD CONSTRAINT FK_UserPage_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserId)
         ON DELETE CASCADE;
GO

CREATE TABLE UserPageParam (
    UserPageParamId NUMERIC NOT NULL,
    UserPageId      NUMERIC NOT NULL,
    ParamNumber     NUMERIC NOT NULL,
    Parameter       VARCHAR(80) NOT NULL,
    CONSTRAINT PK_UserPageParamTable PRIMARY KEY (UserPageParamId)
);
GO

ALTER TABLE UserPageParam
   ADD CONSTRAINT FK_UserPageParam_UserPage FOREIGN KEY (UserPageId)
      REFERENCES UserPage (UserPageId)
         ON DELETE CASCADE;
GO

CREATE TABLE UserMonitor (
    UserMonitorId  NUMERIC NOT NULL,
    UserId         NUMERIC NOT NULL,
    MonitorName    VARCHAR(80) NOT NULL,
    MonitorType    VARCHAR(32) NOT NULL,
    MonitorId      VARCHAR(32) NOT NULL,
    CONSTRAINT PK_UserMonitorTable PRIMARY KEY (UserMonitorId)
);
GO

ALTER TABLE UserMonitor
   ADD CONSTRAINT FK_UserMonitor_YukonUser FOREIGN KEY (UserId)
      REFERENCES YukonUser (UserId)
         ON DELETE CASCADE;
/* End YUK-12469 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.0', '15-AUG-2013', 'Latest Update', 1, GETDATE());