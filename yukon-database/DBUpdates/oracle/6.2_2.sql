/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13478 */
ALTER TABLE YukonServices
ADD ServiceType VARCHAR2(60);

UPDATE YukonServices
SET ServiceType = 
    CASE
       WHEN ServiceId <= 5 THEN 'CLASS_NAME_TYPE'
       WHEN ServiceId > 5 THEN 'CONTEXT_FILE_TYPE'
    END;

ALTER TABLE YukonServices
MODIFY ServiceType NOT NULL;
/* End YUK-13478 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.2', '27-JUN-2014', 'Latest Update', 2, SYSDATE);*/