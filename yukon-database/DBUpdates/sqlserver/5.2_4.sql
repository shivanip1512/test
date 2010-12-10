/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9288 */
DELETE FROM YukonServices 
WHERE ServiceId = 13; 

INSERT INTO YukonServices 
VALUES (13, 'Eka', 'classpath:com/cannontech/services/rfn/rfnMeteringContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-9288 */

/* Start YUK-9291 */ 
UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'LTC'; 

UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'GO_REGULATOR'; 

UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'PO_REGULATOR'; 
/* End YUK-9291 */

/* Start YUK-9284 */
ALTER TABLE CCEventLog
ADD RegulatorId NUMERIC;

UPDATE CCEventLog
SET RegulatorId = 0; 

ALTER TABLE CCEventLog
ALTER COLUMN RegulatorId NUMERIC NOT NULL;
/* End YUK-9284 */

/* Start YUK-9293 */
ALTER TABLE CapControlComment
ALTER COLUMN UserId NUMERIC;

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONPA2; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_PAO FOREIGN KEY (PaoId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
GO

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONUSE; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
            ON DELETE SET NULL;
GO
/* End YUK-9293 */

/* Start YUK-9300 */
INSERT INTO yukonServices 
VALUES (14, 'Inventory Management', 'classpath:com/cannontech/services/dr/inventoryContext.xml', '(none)', '(none)', 'ServiceManager'); 

CREATE TABLE CommandSchedule (
   CommandScheduleId    NUMERIC              NOT NULL,
   StartTimeCronString  VARCHAR(64)          NOT NULL,
   RunPeriod            VARCHAR(32)          NOT NULL,
   DelayPeriod          VARCHAR(32)          NOT NULL,
   Enabled              CHAR(1)              NOT NULL,
   EnergyCompanyId      NUMERIC              NOT NULL,
   CONSTRAINT PK_CommandSchedule PRIMARY KEY (CommandScheduleId)
);
GO

CREATE TABLE InventoryConfigTask (
   InventoryConfigTaskId NUMERIC              NOT NULL,
   TaskName             VARCHAR(255)         NOT NULL,
   SendInService        CHAR(1)              NOT NULL,
   NumberOfItems        NUMERIC              NOT NULL,
   NumberOfItemsProcessed NUMERIC              NOT NULL,
   EnergyCompanyId      NUMERIC              NOT NULL,
   CONSTRAINT PK_InvConfTask PRIMARY KEY (InventoryConfigTaskId)
);
GO

CREATE UNIQUE INDEX Indx_InvConfTa_EC_TaskName_UNQ ON InventoryConfigTask (
    TaskName ASC,
    EnergyCompanyId ASC
);
GO

CREATE TABLE InventoryConfigTaskItem (
   InventoryConfigTaskId NUMERIC              NOT NULL,
   InventoryId          NUMERIC              NOT NULL,
   Status               VARCHAR(16)          NOT NULL,
   CONSTRAINT PK_InvConfTaskItem PRIMARY KEY (InventoryConfigTaskId, InventoryId)
);
GO

ALTER TABLE CommandSchedule
    ADD CONSTRAINT FK_CommSche_EnergyComp FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyID)
            ON DELETE CASCADE;

ALTER TABLE InventoryConfigTask
    ADD CONSTRAINT FK_InvConfTask_EC FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyID)
            ON DELETE CASCADE;

ALTER TABLE InventoryConfigTaskItem
    ADD CONSTRAINT FK_InvConfTaskItem_InvBase FOREIGN KEY (InventoryId)
        REFERENCES InventoryBase (InventoryID)
            ON DELETE CASCADE;

ALTER TABLE InventoryConfigTaskItem
    ADD CONSTRAINT FK_InvConfTaskItem_InvConfTask FOREIGN KEY (InventoryConfigTaskId)
        REFERENCES InventoryConfigTask (InventoryConfigTaskId)
            ON DELETE CASCADE;
/* End YUK-9300 */

/* Start YUK-9318 */
UPDATE YukonRoleProperty 
SET KeyName = 'Inventory Configuration', 
    Description = 'Controls access to Inventory Configuration Tool' 
WHERE RolePropertyId = -20910;
/* End YUK-9318 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '13-DEC-2010', 'Latest Update', 4);