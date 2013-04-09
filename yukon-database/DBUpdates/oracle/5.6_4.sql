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

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/* INSERT INTO CTIDatabase VALUES ('5.6', '03-APR-2013', 'Latest Update', 4, SYSDATE); */