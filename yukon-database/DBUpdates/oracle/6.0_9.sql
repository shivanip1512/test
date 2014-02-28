/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13066 */
DELETE FROM DeviceCollectionById 
WHERE DeviceId NOT IN (SELECT DISTINCT DeviceId FROM Device);

ALTER TABLE DeviceCollectionById
   ADD CONSTRAINT FK_DeviceCollectionById_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
/* End YUK-13066 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.0', '14-MAR-2014', 'Latest Update', 9, SYSDATE);*/