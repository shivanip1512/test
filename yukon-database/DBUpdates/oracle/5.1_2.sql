/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8288 */
CREATE UNIQUE INDEX Indx_LMPWP_DevId_UNQ ON LMProgramWebPublishing (
DeviceId ASC
);
/* End YUK-8288 */

/* Start YUK-8160 */
ALTER TABLE CCurtEEParticipantSelection 
MODIFY ConnectionAudit VARCHAR2(2550); 
/* End YUK-8160 */

/* Start YUK-8315 */
CREATE TABLE ExtraPaoPointAssignment  (
   PAObjectId           NUMBER                          NOT NULL,
   PointId              NUMBER                          NOT NULL,
   Attribute            VARCHAR2(255)                   NOT NULL,
   CONSTRAINT PK_ExtraPAOPointAsgmt PRIMARY KEY (PAObjectId, Attribute)
);

ALTER TABLE ExtraPaoPointAssignment
    ADD CONSTRAINT FK_ExtraPAOPointAsgmt_Point FOREIGN KEY (PointId)
        REFERENCES POINT (PointId)
            ON DELETE CASCADE;

ALTER TABLE ExtraPaoPointAssignment
    ADD CONSTRAINT FK_ExtraPAOPointAsgmt_YukonPAO FOREIGN KEY (PAObjectId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;
/* End YUK-8315 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
