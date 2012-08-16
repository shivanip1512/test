/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11015 */
CREATE TABLE UserGroup  (
   UserGroupId              NUMBER                          NOT NULL,
   UserGroupName            VARCHAR2(1000)                  NOT NULL,
   UserGroupDescription     VARCHAR2(200)                   NOT NULL,
   GroupIdStr               VARCHAR2(150)                   NULL,
   CONSTRAINT PK_UserGroup PRIMARY KEY (UserGroupId)
);

INSERT INTO UserGroup VALUES(-1, 'Admin User Group', 'A user group with basic admin rights for configuring Yukon.', null);

/* Setting up the UserGroupToYukonGroupMapping table, which will link UserGroups to LoginGroups */
CREATE TABLE UserGroupToYukonGroupMapping  (
   UserGroupId              NUMBER                          NOT NULL,
   GroupId                  NUMBER                          NOT NULL,
   CONSTRAINT PK_UserGroupToYukonGroupMap PRIMARY KEY (UserGroupId, GroupId)
);

INSERT INTO UserGroupToYukonGroupMapping VALUES(-1, -1);
INSERT INTO UserGroupToYukonGroupMapping VALUES(-1, -2);

ALTER TABLE UserGroupToYukonGroupMapping
    ADD CONSTRAINT FK_UserGrpYukGrpMap_YukGrp FOREIGN KEY (GroupId)
        REFERENCES YukonGroup (GroupId)
            ON DELETE CASCADE;

ALTER TABLE UserGroupToYukonGroupMapping
    ADD CONSTRAINT FK_UserGrpYukGrpMap_UserGroup FOREIGN KEY (UserGroupId)
        REFERENCES UserGroup (UserGroupId)
            ON DELETE CASCADE;

/* Adding the UserGroupId column to YukonUser to link users to the new yukon user groups */         
ALTER TABLE YukonUser
    ADD UserGroupId NUMBER;

ALTER TABLE YukonUser
    ADD CONSTRAINT FK_YukonUser_UserGroup FOREIGN KEY (UserGroupId)
        REFERENCES UserGroup (UserGroupId);

/* @start-block */
DECLARE
    v_NewUserGroupId      NUMBER;
    v_NewUserGroupNeeded  NUMBER;
    v_UserId              NUMBER;
    v_UserGroupIdStr      VARCHAR2(150);
    v_UserGroupName       VARCHAR2(1000);
    
    CURSOR userId_curs IS SELECT DISTINCT YUG.UserId FROM YukonUserGroup YUG;
BEGIN
    OPEN userId_curs;
    LOOP
        FETCH userId_curs INTO v_userId;
        EXIT WHEN userId_curs%NOTFOUND;
        
            /* Getting the groups associated with the userId */
            SELECT (SELECT SUBSTR (SYS_CONNECT_BY_PATH (YUG.GroupId, ' AND '), 2)
                    FROM (SELECT GroupId, ROW_NUMBER () OVER (ORDER BY GroupId) rn, COUNT (*) OVER () cnt
                          FROM YukonUserGroup
                          WHERE UserId = v_UserId
                          ORDER BY GroupId) YUG
                    WHERE YUG.rn = YUG.cnt
                    START WITH YUG.rn = 1
                    CONNECT BY YUG.rn = PRIOR YUG.rn + 1) GroupIds INTO v_UserGroupIdStr
            FROM YukonUser YU
            WHERE YU.UserId = v_UserId;

            /* Getting the groups associated with the userId */
            SELECT (SELECT SUBSTR (SYS_CONNECT_BY_PATH (YUG_YG.GroupName, ' AND '), 2)
                    FROM (SELECT GroupName, ROW_NUMBER () OVER (ORDER BY GroupName) rn, COUNT (*) OVER () cnt
                          FROM YukonUserGroup YUG
                            JOIN YukonGroup YG ON YG.GroupId = YUG.GroupId
                          WHERE YUG.UserId = v_UserId
                          ORDER BY GroupName) YUG_YG
                    WHERE YUG_YG.rn = YUG_YG.cnt
                    START WITH YUG_YG.rn = 1
                    CONNECT BY YUG_YG.rn = PRIOR YUG_YG.rn + 1) GroupName INTO v_UserGroupName
            FROM YukonUser YU
            WHERE YU.UserId = v_UserId;
            
            /* Check to see if we have a user group matching that configuration, if not lets create one. */
            BEGIN
                SELECT COUNT(*) INTO v_NewUserGroupNeeded
                FROM UserGroup UG
                WHERE UG.GroupIdStr = v_UserGroupIdStr;

                IF 0 = v_NewUserGroupNeeded THEN
                    SELECT MAX(UserGroupId)+1 INTO v_NewUserGroupId FROM UserGroup;
                    INSERT INTO UserGroup VALUES (v_NewUserGroupId, v_UserGroupName, 'Generated User Group', v_UserGroupIdStr);
                    COMMIT;
                    
                    INSERT INTO UserGroupToYukonGroupMapping 
                    SELECT v_NewUserGroupId, YUG.GroupId
                    FROM YukonUserGroup YUG
                    WHERE YUG.UserId = v_UserId;
                    COMMIT;
                END IF;
            END;

            /* Now that we have a group assign the user to that group. */
            UPDATE YukonUser SET UserGroupId = (SELECT UG.UserGroupId FROM UserGroup UG WHERE UG.GroupIdStr = v_UserGroupIdStr) WHERE UserId = v_UserId;
            COMMIT;
        END LOOP;
    CLOSE userId_curs;
END;
/
/* @end-block */

/* Clean up old tables and detact the foreign keys */
ALTER TABLE UserGroup
    DROP COLUMN GroupIdStr;

ALTER TABLE YukonUserGroup RENAME TO YukonUserGroup_Old;

ALTER TABLE YukonUserGroup_Old
    DROP CONSTRAINT FK_YkUsGr_YkGr;

ALTER TABLE YukonUserGroup_Old
    DROP CONSTRAINT FK_YUKONUSE_REF_YKUSG_YUKONUSE;
/* End YUK-11015 */
    
/* Start YUK-11298 */
/* Converting ECToOperatorGroupMapping to use user groups instead of role groups. */
ALTER TABLE ECToOperatorGroupMapping
   DROP CONSTRAINT FK_ECToOpGroupMap_EC;
ALTER TABLE ECToOperatorGroupMapping
   DROP CONSTRAINT FK_ECToOpGroupMap_YukonGroup;

ALTER TABLE ECToOperatorGroupMapping RENAME TO ECToOperatorGroupMap_Delete;
ALTER TABLE ECToOperatorGroupMap_Delete
   DROP CONSTRAINT PK_ECToOpGroupMap;
DROP INDEX PK_ECToOpGroupMap;

/* Create new ECToOperatorGroupMapping table with data. */
CREATE TABLE ECToOperatorGroupMapping ( 
   EnergyCompanyId NUMBER NOT NULL, 
   UserGroupId NUMBER NOT NULL, 
   CONSTRAINT PK_ECToOpGroupMap PRIMARY KEY (EnergyCompanyId, UserGroupId) 
); 

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_UserGroup FOREIGN KEY (UserGroupId) 
        REFERENCES UserGroup (UserGroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToOperatorGroupMapping 
    ADD CONSTRAINT FK_ECToOpGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE; 

INSERT INTO ECToOperatorGroupMapping
SELECT DISTINCT ECOGM.EnergyCompanyId, UGYGM.UserGroupId
FROM ECToOperatorGroupMap_Delete ECOGM
  JOIN UserGroupToYukonGroupMapping UGYGM ON ECOGM.GroupId = UGYGM.GroupId;

DROP TABLE ECToOperatorGroupMap_Delete;

/* Converting ECToResidentialGroupMapping to use user groups instead of role groups. */
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_EC;
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_YukonGroup;

ALTER TABLE ECToResidentialGroupMapping RENAME TO ECToResidentialGroupMap_delete;
ALTER TABLE ECToResidentialGroupMap_delete
   DROP CONSTRAINT PK_ECToResGroupMap;
DROP INDEX PK_ECToResGroupMap;

/* Create new ECToResidentialGroupMapping table with data. */
CREATE TABLE ECToResidentialGroupMapping ( 
   EnergyCompanyId NUMBER NOT NULL, 
   UserGroupId NUMBER NOT NULL, 
   CONSTRAINT PK_ECToResGroupMap PRIMARY KEY (EnergyCompanyId, UserGroupId) 
); 

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_UserGroup FOREIGN KEY (UserGroupId) 
        REFERENCES UserGroup (UserGroupId) 
            ON DELETE CASCADE; 

ALTER TABLE ECToResidentialGroupMapping 
    ADD CONSTRAINT FK_ECToResGroupMap_EC FOREIGN KEY (EnergyCompanyId) 
        REFERENCES EnergyCompany (EnergyCompanyId) 
            ON DELETE CASCADE; 

INSERT INTO ECToResidentialGroupMapping
SELECT DISTINCT ECRGM.EnergyCompanyId, UGYGM.UserGroupId
FROM ECToResidentialGroupMap_delete ECRGM
  JOIN UserGroupToYukonGroupMapping UGYGM ON ECRGM.GroupId = UGYGM.GroupId;

DROP TABLE ECToResidentialGroupMap_delete;
/* End YUK-11298 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 