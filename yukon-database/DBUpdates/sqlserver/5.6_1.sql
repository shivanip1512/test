/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-11672 */
CREATE TABLE ProgramToSeasonalProgram  (
   AssignedProgramId      NUMERIC                 NOT NULL,
   SeasonalProgramId      NUMERIC                 NOT NULL,
   CONSTRAINT PK_ProgramToSeasonalProgram PRIMARY KEY (AssignedProgramId)
);

ALTER TABLE ProgramToSeasonalProgram
    ADD CONSTRAINT FK_ProgSeaProg_LMProgWebPub_AP FOREIGN KEY (AssignedProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId)
            ON DELETE CASCADE;

ALTER TABLE ProgramToSeasonalProgram
    ADD CONSTRAINT FK_ProgSeaProg_LMProgWebPub_SP FOREIGN KEY (SeasonalProgramId)
        REFERENCES LMProgramWebPublishing (ProgramId);
/* End YUK-11672 */

/* Start YUK-11744 */
INSERT INTO YukonRoleProperty VALUES(-1126, -2, 'Alternate Program Enrollment', 'false', 'Enables the use of alternate program enrollments.');
/* End YUK-11744 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
--INSERT INTO CTIDatabase VALUES ('5.6', '23-JAN-2013', 'Latest Update', 1, GETDATE());
