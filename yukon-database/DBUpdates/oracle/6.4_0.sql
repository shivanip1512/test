/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-13973 */
ALTER TABLE EnergyCompany 
ADD ParentEnergyCompanyId NUMBER NULL;

ALTER TABLE EnergyCompany 
    ADD CONSTRAINT FK_EnergyCompany_EnergyCompany FOREIGN KEY (ParentEnergyCompanyId)
       REFERENCES EnergyCompany (EnergyCompanyId);

UPDATE EnergyCompany ec
SET ec.ParentEnergyCompanyId = 
    (SELECT ecm.EnergyCompanyId
     FROM ECToGenericMapping ecm
     WHERE ec.EnergyCompanyId = ecm.ItemId
       AND ecm.MappingCategory = 'Member');

DELETE FROM ECToGenericMapping
WHERE MappingCategory = 'Member';
/* End YUK-13973 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-JAN-2015', 'Latest Update', 0, SYSDATE);*/