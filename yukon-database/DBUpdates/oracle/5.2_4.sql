/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9288 */
DELETE FROM YukonServices 
WHERE ServiceId = 13; 

INSERT INTO YukonServices 
VALUES (13, 'Eka', 'classpath:com/cannontech/services/rfn/rfnMeteringContext.xml', '(none)', '(none)', 'ServiceManager');
/* End YUK-9288 */

/* Start YUK-9291 */ 
UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'LTC'; 

UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'GO_REGULATOR'; 

UPDATE YukonPAObject 
SET paoClass = 'CAPCONTROL' 
WHERE Type = 'PO_REGULATOR'; 
/* End YUK-9291 */

/* Start YUK-9284 */
ALTER TABLE CCEventLog
ADD RegulatorId NUMBER;

UPDATE CCEventLog
SET RegulatorId = 0; 

ALTER TABLE CCEventLog
MODIFY RegulatorId NUMBER NOT NULL;
/* End YUK-9284 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
