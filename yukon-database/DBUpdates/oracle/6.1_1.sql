/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-12914 */
ALTER TABLE RfnBroadcastEvent
RENAME COLUMN EventSendTime TO EventSentTime;
/* End YUK-12914 */

/* Start YUK-13066 */
/* @error ignore-begin */
DELETE FROM DeviceCollectionById 
WHERE DeviceId NOT IN (SELECT DISTINCT DeviceId FROM Device);

ALTER TABLE DeviceCollectionById
   ADD CONSTRAINT FK_DeviceCollectionById_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
/* @error ignore-end */
/* End YUK-13066 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.1', '28-FEB-2014', 'Latest Update', 1, SYSDATE);