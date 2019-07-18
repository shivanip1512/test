/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-20294 */
UPDATE GlobalSetting 
SET Value = 'NONE'
WHERE Name IN ('LDAP_SSL_ENABLED', 'AD_SSL_ENABLED') 
AND Value = '0';

UPDATE GlobalSetting 
SET Value = 'TLS'
WHERE Name IN ('LDAP_SSL_ENABLED', 'AD_SSL_ENABLED') 
AND Value = '1';

INSERT INTO DBUpdates VALUES ('YUK-20294', '7.2.3', SYSDATE);
/* @end YUK-20294 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.2', '20-MAY-2019', 'Latest Update', 3, SYSDATE);*/
