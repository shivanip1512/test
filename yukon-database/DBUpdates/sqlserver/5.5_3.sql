/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11015 */
CREATE TABLE UserGroup  (
   UserGroupId              NUMERIC                        NOT NULL,
   UserGroupName            VARCHAR(1000)                  NOT NULL,
   UserGroupDescription     VARCHAR(200)                   NOT NULL,
   GroupIdStr               VARCHAR(150)                   NULL,
   CONSTRAINT PK_UserGroup PRIMARY KEY (UserGroupId)
);

INSERT INTO UserGroup VALUES(-1, 'Admin User Group', 'A user group with basic admin rights for configuring Yukon.', '-2,-1');

/* Setting up the UserGroupToYukonGroupMapping table, which will link UserGroups to LoginGroups */
CREATE TABLE UserGroupToYukonGroupMapping  (
   UserGroupId              NUMERIC                          NOT NULL,
   GroupId                  NUMERIC                          NOT NULL,
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
    ADD UserGroupId NUMERIC;
GO

ALTER TABLE YukonUser
    ADD CONSTRAINT FK_YukonUser_UserGroup FOREIGN KEY (UserGroupId)
        REFERENCES UserGroup (UserGroupId);

/* @start-block */
DECLARE @newUserGroupId NUMERIC;
DECLARE @newUserGroupNeeded NUMERIC;
DECLARE @userId NUMERIC;
DECLARE @userGroupIdStr VARCHAR(150);
DECLARE @userGroupName VARCHAR(1000);

DECLARE userId_curs CURSOR FOR 
    SELECT DISTINCT YUG.UserId 
    FROM YukonUserGroup YUG;

OPEN userId_curs;
FETCH userId_curs INTO @userId;

WHILE (@@fetch_status = 0)
    BEGIN
        /* Getting the groups associated with the userId */
        SET @userGroupIdStr = NULL;
        SELECT @userGroupIdStr = ISNULL(@userGroupIdStr + ' AND ','') + ISNULL(CAST(GroupID as VARCHAR),'')
        FROM YukonUserGroup YUG
        WHERE YUG.UserId = @userId;
        
        /* Getting the groups associated with the userId */
        SET @userGroupName = NULL;
        SELECT @userGroupName = ISNULL(@userGroupName + ' AND ','') + ISNULL(CAST(GroupName as VARCHAR),'')
        FROM YukonUserGroup YUG
            JOIN YukonGroup YG ON YUG.GroupId = YG.GroupId
        WHERE YUG.UserId = @userId;
        
        /* Check to see if we have a user group matching that configuration, if not lets create one. */
        BEGIN
            SELECT @newUserGroupNeeded = COUNT(*)
                                      FROM UserGroup UG
                                      WHERE UG.GroupIdStr = @userGroupIdStr;

            IF 0 = @newUserGroupNeeded 
            BEGIN
                SELECT @newUserGroupId = MAX(UserGroupId)+1 FROM UserGroup;
                INSERT INTO UserGroup VALUES (@newUserGroupId, @userGroupName, 'Generated User Group', @userGroupIdStr);
                
                INSERT INTO UserGroupToYukonGroupMapping 
                SELECT @newUserGroupId, YUG.GroupId
                FROM YukonUserGroup YUG
                WHERE YUG.UserId = @userId;
            END;
        END;

        /* Now that we have a group assign the user to that group. */
        UPDATE YukonUser SET UserGroupId = (SELECT UG.UserGroupId FROM UserGroup UG WHERE UG.GroupIdStr = @userGroupIdStr) WHERE UserId = @userId;
        FETCH NEXT FROM userId_curs INTO @userId;
    END;
CLOSE userId_curs;
DEALLOCATE userId_curs;
/* @end-block */

/* Clean up old tables and detact the foreign keys */
ALTER TABLE UserGroup
    DROP COLUMN GroupIdStr;

sp_rename 'YukonUserGroup', 'YukonUserGroup_Old';

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
GO

SP_RENAME 'ECToOperatorGroupMapping', 'ECToOperatorGroupMap_Delete';
ALTER TABLE ECToOperatorGroupMap_Delete
   DROP CONSTRAINT PK_ECToOpGroupMap;

/* Create new ECToOperatorGroupMapping table with data. */
CREATE TABLE ECToOperatorGroupMapping ( 
   EnergyCompanyId NUMERIC NOT NULL, 
   UserGroupId NUMERIC NOT NULL, 
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
GO

SP_RENAME 'ECToResidentialGroupMapping', 'ECToResidentialGroupMap_delete';
ALTER TABLE ECToResidentialGroupMap_delete
   DROP CONSTRAINT PK_ECToResGroupMap;

/* Create new ECToResidentialGroupMapping table with data. */
CREATE TABLE ECToResidentialGroupMapping ( 
   EnergyCompanyId NUMERIC NOT NULL, 
   UserGroupId NUMERIC NOT NULL, 
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
