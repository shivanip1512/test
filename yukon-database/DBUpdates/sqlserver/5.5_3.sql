/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11015 */
CREATE TABLE UserGroup  (
   UserGroupId              NUMERIC                        NOT NULL,
   Name                     VARCHAR(1000)                  NOT NULL,
   Description              VARCHAR(200)                   NOT NULL,
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
        SELECT @userGroupIdStr = ISNULL(@userGroupIdStr + ',','') + ISNULL(CAST(GroupID as VARCHAR),'')
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
GO
sp_rename 'YukonUserGroup', 'YukonUserGroup_Old';
/* @error ignore-begin */
ALTER TABLE YukonUserGroup_Old
    DROP CONSTRAINT FK_YkUsGr_YkGr;

ALTER TABLE YukonUserGroup_Old
    DROP CONSTRAINT FK_YUKONUSE_REF_YKUSG_YUKONUSE;
/* @error ignore-end */
/* End YUK-11015 */

/* Start YUK-11298 */
/* Converting ECToOperatorGroupMapping to use user groups instead of role groups. */
/* @error ignore-begin */
ALTER TABLE ECToOperatorGroupMapping
   DROP CONSTRAINT FK_ECToOpGroupMap_EC;
ALTER TABLE ECToOperatorGroupMapping
   DROP CONSTRAINT FK_ECToOpGroupMap_YukonGroup;
/* @error ignore-end */
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
/* @error ignore-begin */
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_EC;
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_YukonGroup;
GO

SP_RENAME 'ECToResidentialGroupMapping', 'ECToResidentialGroupMap_delete';
ALTER TABLE ECToResidentialGroupMap_delete
   DROP CONSTRAINT PK_ECToResGroupMap;
/* @error ignore-end */

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
    
/* Start YUK-11317 */
/* Converting GroupPAOPermission to use user groups instead of role groups. */
/* @error ignore-begin */
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT FK_GROUPPAO_REF_YKGRP_YUKONGRO;
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT FK_GROUPPAO_REF_YUKPA_YUKONPAO;
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT AK_GRPPAOPERM;
GO

SP_RENAME 'GroupPaoPermission', 'GroupPaoPermission_Delete';
ALTER TABLE GroupPaoPermission_Delete
   DROP CONSTRAINT PK_GROUPPAOPERMISSION;
/* @error ignore-end */

/* Create new GroupPaoPermission table with data. */
CREATE TABLE GroupPaoPermission  (
   GroupPaoPermissionId NUMERIC                          NOT NULL,
   UserGroupId          NUMERIC                          NOT NULL,
   PaoId                NUMERIC                          NOT NULL,
   Permission           VARCHAR(50)                     NOT NULL,
   Allow                VARCHAR(5)                      NOT NULL,
   CONSTRAINT PK_GroupPAOPermission PRIMARY KEY (GroupPaoPermissionId)
);

CREATE UNIQUE INDEX Indx_UserGrpId_PaoId_Perm_UNQ ON GroupPaoPermission (
    UserGroupId ASC,
    PaoId ASC,
    Permission ASC
);

ALTER TABLE GroupPaoPermission
    ADD CONSTRAINT FK_GroupPaoPerm_UserGroup FOREIGN KEY (UserGroupId)
        REFERENCES UserGroup (UserGroupId);

ALTER TABLE GroupPaoPermission
    ADD CONSTRAINT FK_GroupPaoPerm_PAO FOREIGN KEY (PaoId)
        REFERENCES YukonPAObject (PAObjectId);

/* Migrate the data from the old table */
/* @start-block */
DECLARE @NewGroupPaoPermissionId    NUMERIC;
DECLARE @UserGroupId                NUMERIC;
DECLARE @PaoId                      NUMERIC;
DECLARE @Permission                 VARCHAR(50);
DECLARE @Allow                      VARCHAR(5);
    
DECLARE groupPaoPerm_curs CURSOR FOR (SELECT DISTINCT UGYGM.UserGroupId, GPP_D.PaoId, GPP_D.Permission, GPP_D.Allow
                                      FROM GroupPaoPermission_Delete GPP_D
                                        JOIN UserGroupToYukonGroupMapping UGYGM ON GPP_D.GroupId = UGYGM.GroupId);

OPEN groupPaoPerm_curs;
FETCH groupPaoPerm_curs INTO @UserGroupId, @PaoId, @Permission, @Allow;
WHILE (@@fetch_status = 0)
    BEGIN
        SELECT @NewGroupPaoPermissionId = ISNULL(MAX(GroupPaoPermissionId)+1, 1)
        FROM GroupPaoPermission;

        INSERT INTO GroupPaoPermission
        VALUES (@NewGroupPaoPermissionId, @UserGroupId, @PaoId, @Permission, @Allow);

        FETCH NEXT FROM groupPaoPerm_curs INTO @UserGroupId, @PaoId, @Permission, @Allow;
    END;
CLOSE groupPaoPerm_curs;
DEALLOCATE groupPaoPerm_curs;
/* @end-block */

DROP TABLE GroupPaoPermission_Delete;
/* End YUK-11317 */

/* Start YUK-11311 */
CREATE TABLE LMBeatThePeakGear
(
    GearId       NUMERIC        NOT NULL,
    AlertLevel   VARCHAR(20)    NOT NULL,
    CONSTRAINT PK_LMBeatThePeakGear PRIMARY KEY (GearId)
);
ALTER TABLE LMBeatThePeakGear
    ADD CONSTRAINT FK_BTPGear_LMProgramDirectGear FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearId)
            ON DELETE CASCADE;
/* End YUK-11311 */

/* Start YUK-11192 */
UPDATE YukonListEntry 
SET EntryText = 'Meter'
WHERE EntryId = 1058;

UPDATE InventoryBase SET AlternateTrackingNumber = ' '
FROM InventoryBase IB
WHERE IB.AlternateTrackingNumber IS NULL;

UPDATE InventoryBase SET Notes = ' '
FROM InventoryBase IB
WHERE IB.Notes IS NULL;

UPDATE InventoryBase SET DeviceLabel = LMB.ManufacturerSerialNumber
FROM InventoryBase IB
JOIN LMHardwareBase LMB ON LMB.InventoryId = IB.InventoryId
WHERE LTRIM(RTRIM(IB.DeviceLabel)) = '' 
   OR IB.DeviceLabel IS NULL;

UPDATE InventoryBase SET DeviceLabel = mhb.MeterNumber
FROM InventoryBase IB
JOIN  MeterHardwareBase mhb ON mhb.InventoryId = IB.InventoryId
WHERE LTRIM(RTRIM(IB.DeviceLabel)) = '' 
   OR IB.DeviceLabel IS NULL;

UPDATE InventoryBase SET DeviceLabel = YPO.PAOName
FROM InventoryBase IB
JOIN  YukonPAObject YPO ON IB.DeviceID = YPO.PAObjectID 
WHERE IB.DeviceID > 0 AND LTRIM(RTRIM(IB.DeviceLabel)) = '' 
   OR IB.DeviceLabel IS NULL;
/* End YUK-11192 */

/* Start YUK-11330 */
ALTER TABLE LMGroupSEP 
ADD RampIn NUMERIC;
GO
UPDATE LMGroupSEP
SET RampIn = 30;
ALTER TABLE LMGroupSEP 
ALTER COLUMN RampIn NUMERIC NOT NULL;
ALTER TABLE LMGroupSEP 
ADD RampOut NUMERIC;
GO
UPDATE LMGroupSEP
SET RampOut = 30;
ALTER TABLE LMGroupSEP 
ALTER COLUMN RampOut NUMERIC NOT NULL;
/* End YUK-11330 */

/* Start YUK-11313 */
CREATE TABLE EncryptionKey (
    EncryptionKeyId NUMERIC      NOT NULL,
    Name            VARCHAR(128) NOT NULL,
    Value           VARCHAR(512) NOT NULL,
    CONSTRAINT PK_EncryptionKey PRIMARY KEY (EncryptionKeyId)
);
GO

CREATE TABLE YukonPAObjectEncryptionKey (
    PAObjectID      NUMERIC NOT NULL,
    EncryptionKeyId NUMERIC NOT NULL,
    CONSTRAINT PK_YukonPAObjectEncryptionKey PRIMARY KEY (PAObjectID)
);
GO
DELETE FROM StaticPaoInfo WHERE InfoKey = 'CPS_ONE_WAY_ENCRYPTION_KEY';
/* End YUK-11313 */

/* Start YUK-11293 */
CREATE TABLE ReportedAddressExpressCom (
   ChangeId             NUMERIC              NOT NULL,
   DeviceId             NUMERIC              NOT NULL,
   Timestamp            DATETIME             NOT NULL,
   SPID                 NUMERIC              NOT NULL,
   GEO                  NUMERIC              NOT NULL,
   Substation           NUMERIC              NOT NULL,
   Feeder               NUMERIC              NOT NULL,
   Zip                  NUMERIC              NOT NULL,
   UDA                  NUMERIC              NOT NULL,
   Required             NUMERIC              NOT NULL,
   CONSTRAINT PK_ReportedAddressExpressCom PRIMARY KEY (ChangeId)
);
GO

ALTER TABLE ReportedAddressExpressCom
   ADD CONSTRAINT FK_RepAddExpressCom_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId)
         ON DELETE CASCADE;
GO

CREATE TABLE ReportedAddressRelayExpressCom (
   ChangeId             NUMERIC              NOT NULL,
   RelayNumber          NUMERIC              NOT NULL,
   Program              NUMERIC              NOT NULL,
   Splinter             NUMERIC              NOT NULL,
   CONSTRAINT PK_ReportedAddressRelayExpCom PRIMARY KEY (ChangeId, RelayNumber)
);
GO

ALTER TABLE ReportedAddressRelayExpressCom
   ADD CONSTRAINT FK_RepAddRelayExp_RepAddExpCom FOREIGN KEY (ChangeId)
      REFERENCES ReportedAddressExpressCom (ChangeId)
         ON DELETE CASCADE;
GO
/* End YUK-11293 */

/* Start YUK-11039 */
UPDATE YukonUser
SET ForceReset = 'Y'
WHERE UserId IN (-1, -2, -100);
/* End YUK-11039 */

/* Start YUK-11338 */
UPDATE State 
SET Text = 'Temporarily Out of Service' 
WHERE Text = 'Temporarily Out of Serivice';
/* End YUK-11338 */

/* Start YUK-11329 */
UPDATE YukonRoleProperty
SET DefaultValue = 'ENCRYPTED',
Description = 'Set the default authentication type to use {AD,ENCRYPTED,LDAP,RADIUS,NONE}'
WHERE RolePropertyId = -1307 AND RoleId = -4;

UPDATE YukonGroupRole SET Value = 'ENCRYPTED'
WHERE RolePropertyId = -1307 AND Value IN ('HASH_SHA', 'PLAIN');
/* End YUK-11329 */

/* Start YUK-11336 */
DELETE FROM YukonGroupRole
WHERE RolePropertyId = -11055;

DELETE FROM YukonRoleProperty
WHERE RolePropertyId = -11055;
/* End YUK-11336 */

/* Start YUK-11339 */
ALTER TABLE DynamicLMGroup
ADD LastStopTimeSent DATETIME;
GO
UPDATE DynamicLMGroup
SET LastStopTimeSent ='01-JAN-1990';

ALTER TABLE DynamicLMGroup
ALTER COLUMN LastStopTimeSent DATETIME NOT NULL;
/* End YUK-11339 */

/* Start YUK-11371 */
/* Fix minimum password length & password policy quality values */
UPDATE YukonRoleProperty
SET DefaultValue = '1'
WHERE RolePropertyID IN (-11002, -11050)
  AND DefaultValue = '0';

UPDATE YukonGroupRole
SET Value = '1'
WHERE RolePropertyID IN (-11002, -11050)
  AND Value = '0';
/* End YUK-11371 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
INSERT INTO CTIDatabase VALUES ('5.5', 'Garrett D', '10-SEP-2012', 'Latest Update', 3 );