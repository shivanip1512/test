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
UPDATE StateGroup SET Name = 'Comm Status State Custom' where Name = 'Comm Status State' AND StateGroupId != -11;

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

/* Start YUK-8769 */
DELETE FROM YukonUserRole
WHERE RolePropertyId IN (-90000, -40172, -40134, -40133, -40117, -20892,
                         -20891, -20890, -20889, -20888, -20887, -20886,
                         -20885, -20884, -20883, -20882, -20881, -20880,
                         -20870, -20864, -20863, -20862, -20861, -20860,
                         -20859, -20858, -20857, -20856, -20855, -20854,
                         -20853, -20852, -20851, -20850, -20846, -20845,
                         -20844, -20843, -20842, -20841, -20840, -20839,
                         -20838, -20837, -20836, -20835, -20834, -20833,
                         -20832, -20831, -20830, -20820, -20819, -20816,
                         -20815, -20814, -20813, -20810);

DELETE FROM YukonGroupRole
WHERE RolePropertyId IN (-90000, -40172, -40134, -40133, -40117, -20892,
                         -20891, -20890, -20889, -20888, -20887, -20886,
                         -20885, -20884, -20883, -20882, -20881, -20880,
                         -20870, -20864, -20863, -20862, -20861, -20860,
                         -20859, -20858, -20857, -20856, -20855, -20854,
                         -20853, -20852, -20851, -20850, -20846, -20845,
                         -20844, -20843, -20842, -20841, -20840, -20839,
                         -20838, -20837, -20836, -20835, -20834, -20833,
                         -20832, -20831, -20830, -20820, -20819, -20816,
                         -20815, -20814, -20813, -20810);

DELETE FROM YukonRoleProperty
WHERE RolePropertyId IN (-90000, -40172, -40134, -40133, -40117, -20892,
                         -20891, -20890, -20889, -20888, -20887, -20886,
                         -20885, -20884, -20883, -20882, -20881, -20880,
                         -20870, -20864, -20863, -20862, -20861, -20860,
                         -20859, -20858, -20857, -20856, -20855, -20854,
                         -20853, -20852, -20851, -20850, -20846, -20845,
                         -20844, -20843, -20842, -20841, -20840, -20839,
                         -20838, -20837, -20836, -20835, -20834, -20833,
                         -20832, -20831, -20830, -20820, -20819, -20816,
                         -20815, -20814, -20813, -20810);
/* End YUK-8769 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.2', 'Matt K', '10-JUN-2010', 'Latest Update', 0);
