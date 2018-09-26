/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-18870 */
CREATE TABLE LMNestLoadShapingGear  (
    GearId               NUMBER                          NOT NULL,
    PreparationOption    VARCHAR2(20)                    NOT NULL,
    PeakOption           VARCHAR2(20)                    NOT NULL,
    PostPeakOption       VARCHAR2(20)                    NOT NULL,
    CONSTRAINT PK_LMNestLoadShapingGear PRIMARY KEY (GearId)
);

ALTER TABLE LMNestLoadShapingGear
    ADD CONSTRAINT FK_NLSGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearID)
            ON DELETE CASCADE;

INSERT INTO DBUpdates VALUES ('YUK-18870', '7.2.0', SYSDATE);
/* @start YUK-18870 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.1', '07-SEP-2018', 'Latest Update', 2, SYSDATE);*/