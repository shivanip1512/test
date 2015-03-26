/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14171 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId =  -20903;
  
DELETE FROM YukonRoleProperty
WHERE RolePropertyId =  -20903;
/* End YUK-14171 */


/* Start YUK-14167 */
CREATE TABLE RegulatorEvents (
   RegulatorEventId     NUMERIC              NOT NULL,
   EventType            VARCHAR(64)          NOT NULL,
   RegulatorId          NUMERIC              NOT NULL,
   TimeStamp            DATETIME             NOT NULL,
   UserName             VARCHAR(64)          NOT NULL,
   SetPointValue        FLOAT                NULL,
   TapPosition          NUMERIC              NULL,
   Phase                CHAR(1)              NULL,
   CONSTRAINT PK_RegulatorEvents PRIMARY KEY (RegulatorEventId)
);

ALTER TABLE RegulatorEvents
   ADD CONSTRAINT FK_RegulatorEvents_Regulator FOREIGN KEY (RegulatorId)
      REFERENCES Regulator (RegulatorId)
         ON DELETE CASCADE;
GO
/* End YUK-14167 */


/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAR-2015', 'Latest Update', 1, GETDATE());*/