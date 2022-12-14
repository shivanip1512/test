/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8160 */
ALTER TABLE CCurtEEParticipantSelection 
ALTER COLUMN ConnectionAudit VARCHAR(2550) NOT NULL; 
/* End YUK-8160 */

/* Start YUK-8315 */
CREATE TABLE ExtraPaoPointAssignment (
   PAObjectId           NUMERIC              NOT NULL,
   PointId              NUMERIC              NOT NULL,
   Attribute            VARCHAR(255)         NOT NULL,
   CONSTRAINT PK_ExtraPAOPointAsgmt PRIMARY KEY (PAObjectId, Attribute)
);
GO

ALTER TABLE ExtraPaoPointAssignment
    ADD CONSTRAINT FK_ExtraPAOPointAsgmt_Point FOREIGN KEY (PointId)
        REFERENCES POINT (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE ExtraPaoPointAssignment
    ADD CONSTRAINT FK_ExtraPAOPointAsgmt_YukonPAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
GO 
/* End YUK-8315 */

/* Start YUK-8342 */
ALTER TABLE CapControlSubstationBus ADD DisableBusPointId NUMERIC;
GO
UPDATE CapControlSubstationBus SET DisableBusPointId = 0;
GO
ALTER TABLE CapControlSubstationBus ALTER COLUMN DisableBusPointId NUMERIC NOT NULL; 
/* End YUK-8342 */

/* Start YUK-8336 */
ALTER TABLE OptOutEvent DROP CONSTRAINT FK_OptOutEvent_InvBase;
/* End YUK-8336 */

/* Start YUK-8345 */
UPDATE CCStrategyTargetSettings 
SET SettingName = 'Volt Weight' 
WHERE SettingName = 'KVOLT Weight'; 
/* End YUK-8345 */

/* Start YUK-8361 */
DROP TABLE CCSubstationBusToLTC;

CREATE TABLE CCSubstationBusToLTC (
   SubstationBusId      NUMERIC              NOT NULL,
   LtcId                NUMERIC              NOT NULL,
   CONSTRAINT PK_CCSubBusToLtc PRIMARY KEY (SubstationBusId)
);
GO

ALTER TABLE CCSubstationBusToLTC
    ADD CONSTRAINT FK_CCSubBusToLTC_CapContSubBus FOREIGN KEY (SubstationBusId)
        REFERENCES CapControlSubstationBus (SubstationBusId)
            ON DELETE CASCADE;
GO

ALTER TABLE CCSubstationBusToLTC
    ADD CONSTRAINT FK_CCSubBusToLTC_YukonPAO FOREIGN KEY (LtcId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
GO
/* End YUK-8361 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.1', 'Matt K', '03-FEB-2010', 'Latest Update', 2);
