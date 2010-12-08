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

/* Start YUK-9293 */
ALTER TABLE CapControlComment
MODIFY UserId NUMBER;

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONPA2; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_PAO FOREIGN KEY (PaoId)
        REFERENCES YukonPAObject (PAObjectId)
            ON DELETE CASCADE;

ALTER TABLE CapControlComment 
DROP CONSTRAINT FK_CAPCONTR_REFERENCE_YUKONUSE; 

ALTER TABLE CapControlComment
    ADD CONSTRAINT FK_CapContCom_YukonUser FOREIGN KEY (UserId)
        REFERENCES YukonUser (UserId)
            ON DELETE SET NULL;
/* End YUK-9293 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
