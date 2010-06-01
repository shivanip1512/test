/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-8718 */
CREATE TABLE CRFAddress  (
   DeviceId             NUMBER                          NOT NULL,
   SerialNumber         VARCHAR2(30)                    NOT NULL,
   Manufacturer         VARCHAR2(80)                    NOT NULL,
   Model                VARCHAR2(80)                    NOT NULL,
   CONSTRAINT PK_CRFAdd PRIMARY KEY (DeviceId)
);

ALTER TABLE CRFAddress
    ADD CONSTRAINT FK_CRFAdd_Device FOREIGN KEY (DeviceId)
        REFERENCES Device (DeviceId)
            ON DELETE CASCADE;

CREATE UNIQUE INDEX Indx_CRFAdd_SerNum_Man_Mod_UNQ ON CRFAddress(
SerialNumber ASC,
Manufacturer ASC,
Model ASC
);
/* End YUK-8718 */

/* Start YUK-8708 */
DELETE FROM YukonUserRole
WHERE RolePropertyId = -10001;
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -10001;
DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -10001;
/* End YUK-8708 */

/* Start YUK-8727 */
ALTER TABLE PointAlarming
    DROP CONSTRAINT FK_CntNt_PtAl;
    
ALTER TABLE PointAlarming
    DROP COLUMN RecipientId;
/* End YUK-8727 */ 

/* Start YUK-8741 */
INSERT INTO StateGroup VALUES( -11, 'Comm Status State', 'Status' );
INSERT INTO State VALUES( -11,-1, 'Any', 2, 6 , 0);
INSERT INTO State VALUES( -11, 0, 'Connected', 0, 6 , 0);
INSERT INTO State VALUES( -11, 1, 'Disconnected', 1, 6 , 0);
/* End YUK-8741 */
    
/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
