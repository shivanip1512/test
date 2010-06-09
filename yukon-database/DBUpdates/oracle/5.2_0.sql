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

/* Start YUK-8733 */
ALTER TABLE YukonSelectionList
ADD EnergyCompanyId NUMBER;

UPDATE YukonSelectionList
SET EnergyCompanyId = (SELECT ECTGM.EnergyCompanyId
                       FROM ECToGenericMapping ECTGM
                       WHERE ECTGM.MappingCategory = 'YukonSelectionList'
                       AND ECTGM.ItemId = YukonSelectionList.ListId);

UPDATE YukonSelectionList
SET EnergyCompanyId = -1
WHERE EnergyCompanyId IS NULL;

ALTER TABLE YukonSelectionList
MODIFY EnergyCompanyId NUMBER NOT NULL;

ALTER TABLE YukonSelectionList
    ADD CONSTRAINT FK_YukonSelList_EnergyComp FOREIGN KEY (EnergyCompanyId)
        REFERENCES EnergyCompany (EnergyCompanyId);

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'YukonSelectionList';

DELETE FROM YukonSelectionList
WHERE ListId = 2000;

CREATE UNIQUE INDEX Indx_YSL_ListName_ECId_UNQ ON YukonSelectionList(
    ListName ASC,
    EnergyCompanyId ASC
);
/* End YUK-8733 */

/* Start YUK-8740 */
/* @error ignore-begin */
ALTER TABLE GraphCustomerList
    DROP CONSTRAINT FK_GRAPHCUS_REFGRPHCS_CICUSTOM;

ALTER TABLE GraphCustomerList
    ADD CONSTRAINT FK_GrphCstLst_Cst FOREIGN KEY (CustomerId)
        REFERENCES Customer (CustomerId);
/* @error ignore-end */
/* End YUK-8740 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
