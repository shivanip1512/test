/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9319 */
ALTER TABLE YukonServices 
DROP COLUMN ParamNames; 

ALTER TABLE YukonServices 
DROP COLUMN ParamValues;
/* End YUK-9319 */

/* Start YUK-9390 */
DELETE FROM ECToGenericMapping 
WHERE MappingCategory = 'InterviewQuestion'; 

DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT AnswerType 
                  FROM InterviewQuestion)
AND EntryId != 0;
DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT QuestionType 
                  FROM InterviewQuestion)
AND EntryId != 0;
DELETE FROM YukonListEntry 
WHERE EntryId IN (SELECT ExpectedAnswer 
                  FROM InterviewQuestion)
AND EntryId != 0;

DROP TABLE InterviewQuestion CASCADE CONSTRAINTS;
/* End YUK-9390 */

/* Start YUK-9504 */
/* Migrated energy company substation information to new ECToSubstationMapping Table */ 
CREATE TABLE ECToSubstationMapping ( 
   EnergyCompanyId      NUMBER          NOT NULL, 
   SubstationId         NUMBER          NOT NULL, 
   CONSTRAINT PK_ECToSubMap PRIMARY KEY (EnergyCompanyId, SubstationId) 
); 

INSERT INTO ECToSubstationMapping 
SELECT EnergyCompanyId, ItemId 
FROM ECToGenericMapping 
WHERE MappingCategory = 'Substation'; 

DELETE FROM ECToGenericMapping 
WHERE MappingCategory = 'Substation'; 

ALTER TABLE ECToSubstationMapping 
    ADD CONSTRAINT FK_ECToSubMap_Sub FOREIGN KEY (SubstationId) 
        REFERENCES Substation (SubstationId) 
ON DELETE CASCADE; 

ALTER TABLE ECToSubstationMapping 
    ADD CONSTRAINT FK_ECToSubMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
ON DELETE CASCADE; 

/* Migrated energy company route information to new ECToRouteMapping Table */ 
CREATE TABLE ECToRouteMapping ( 
   EnergyCompanyId      NUMBER          NOT NULL, 
   RouteId              NUMBER          NOT NULL, 
   CONSTRAINT PK_ECToRouteMap PRIMARY KEY (EnergyCompanyId, RouteId) 
); 

INSERT INTO ECToRouteMapping 
SELECT EnergyCompanyId, ItemId 
FROM ECToGenericMapping 
WHERE MappingCategory = 'Route'; 

DELETE FROM ECToGenericMapping 
WHERE MappingCategory = 'Route'; 

ALTER TABLE ECToRouteMapping 
    ADD CONSTRAINT FK_ECToRouteMap_Route FOREIGN KEY (RouteId) 
        REFERENCES Route (RouteId) 
ON DELETE CASCADE; 

ALTER TABLE ECToRouteMapping 
    ADD CONSTRAINT FK_ECToRouteMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
ON DELETE CASCADE; 

/* Removed lmRouteId from the substation table */ 
ALTER TABLE Substation 
    DROP COLUMN LmRouteId;
/* End YUK-9504 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
