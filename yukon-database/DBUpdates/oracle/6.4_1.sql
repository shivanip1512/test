/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14171 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20903;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20903;
/* End YUK-14171 */

/* Start YUK-14167 */
CREATE TABLE RegulatorEvents  (
   RegulatorEventId     NUMBER                          NOT NULL,
   EventType            VARCHAR2(64)                    NOT NULL,
   RegulatorId          NUMBER                          NOT NULL,
   TimeStamp            DATE                            NOT NULL,
   UserName             VARCHAR2(64)                    NOT NULL,
   SetPointValue        FLOAT,
   TapPosition          NUMBER,
   Phase                CHAR(1),
   CONSTRAINT PK_RegulatorEvents PRIMARY KEY (RegulatorEventId)
);

ALTER TABLE RegulatorEvents
   ADD CONSTRAINT FK_RegulatorEvents_Regulator FOREIGN KEY (RegulatorId)
      REFERENCES Regulator (RegulatorId)
      ON DELETE CASCADE;
/* End YUK-14167 */


/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAR-2015', 'Latest Update', 1, SYSDATE);*/