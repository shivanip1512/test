/******************************************/ 
/**** SQLServer 2000 DBupdates         ****/ 
/******************************************/ 

/* Start YUK-8288 */
CREATE UNIQUE INDEX Indx_LMPWP_DevId_UNQ ON LMProgramWebPublishing (
DeviceId ASC
);
/* End YUK-8288 */

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

ALTER TABLE Point
    ADD CONSTRAINT FK_Point_ExtraPAOPointAsgmt FOREIGN KEY (PointId)
        REFERENCES ExtraPaoPointAssignment (PointId)
            ON DELETE CASCADE;
GO

ALTER TABLE YukonPAObject
    ADD CONSTRAINT FK_YukonPAO_ExtraPAOPointAsgmt FOREIGN KEY (PAObjectId)
        REFERENCES ExtraPaoPointAssignment (PAObjectId)
            ON DELETE CASCADE;
GO 
/* End YUK-8315 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
