/******************************************/ 
/**** Oracle DBupdates                 ****/ 
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
ADD RegulatorId NUMBER;

UPDATE CCEventLog
SET RegulatorId = 0; 

ALTER TABLE CCEventLog
MODIFY RegulatorId NUMBER NOT NULL;
/* End YUK-9284 */

/* Start YUK-9293 */
ALTER TABLE CapControlComment
MODIFY UserId NUMBER;

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONPA2; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_PAO FOREIGN KEY (PaoId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONUSE; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
            ON DELETE SET NULL;
/* End YUK-9293 */

/* Start YUK-9300 */
INSERT INTO yukonServices 
VALUES (14, 'Inventory Management', 'classpath:com/cannontech/services/dr/inventoryContext.xml', '(none)', '(none)', 'ServiceManager'); 

CREATE TABLE CommandSchedule  (
   CommandScheduleId    NUMBER                          NOT NULL,
   StartTimeCronString  VARCHAR2(64)                    NOT NULL,
   RunPeriod            VARCHAR2(32)                    NOT NULL,
   DelayPeriod          VARCHAR2(32)                    NOT NULL,
   Enabled              CHAR(1)                         NOT NULL,
   EnergyCompanyId      NUMBER                          NOT NULL,
   CONSTRAINT PK_CommandSchedule PRIMARY KEY (CommandScheduleId)
);

CREATE TABLE InventoryConfigTask  (
   InventoryConfigTaskId NUMBER                          NOT NULL,
   TaskName             VARCHAR2(255)                   NOT NULL,
   SendInService        CHAR(1)                         NOT NULL,
   NumberOfItems        NUMBER                          NOT NULL,
   NumberOfItemsProcessed NUMBER                          NOT NULL,
   EnergyCompanyId      NUMBER                          NOT NULL,
   CONSTRAINT PK_InvConfTask PRIMARY KEY (InventoryConfigTaskId)
);

CREATE UNIQUE INDEX Indx_InvConfTa_EC_TaskName_UNQ ON InventoryConfigTask (
   TaskName ASC,
   EnergyCompanyId ASC
);

CREATE TABLE InventoryConfigTaskItem  (
   InventoryConfigTaskId NUMBER                          NOT NULL,
   InventoryId          NUMBER                          NOT NULL,
   Status               VARCHAR2(16)                    NOT NULL,
   CONSTRAINT PK_InvConfTaskItem PRIMARY KEY (InventoryConfigTaskId, InventoryId)
);

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
            
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '13-DEC-2010', 'Latest Update', 4);