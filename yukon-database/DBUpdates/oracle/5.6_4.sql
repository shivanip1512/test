/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11880 */
ALTER TABLE ECToAcctThermostatSchedule
    DROP CONSTRAINT FK_ECToAccThermSch_AccThermSch;

ALTER TABLE ECToAcctThermostatSchedule
    ADD CONSTRAINT FK_ECToAccThermSch_AccThermSch FOREIGN KEY (AcctThermostatScheduleId)
        REFERENCES AcctThermostatSchedule (AcctThermostatScheduleId)
            ON DELETE CASCADE;
/* End YUK-11880 */

/* Start YUK-12012 */
ALTER TABLE AcctThermostatSchedule
ADD Archived CHAR(1);

UPDATE AcctThermostatSchedule
SET Archived = 'N';

ALTER TABLE AcctThermostatSchedule
MODIFY Archived CHAR(1) NOT NULL;
/* End YUK-12012 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 4, SYSDATE); */