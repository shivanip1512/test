/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-11015 */
CREATE TABLE UserGroup  (
   UserGroupId              NUMBER                          NOT NULL,
   Name                     VARCHAR2(1000)                  NOT NULL,
   Description              VARCHAR2(200)                   NOT NULL,
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
            SELECT (SELECT SUBSTR (SYS_CONNECT_BY_PATH (YUG.GroupId, ','), 2)
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
            SELECT (SELECT SUBSTR (SYS_CONNECT_BY_PATH (YUG_YG.GroupName, ' AND '), 6)
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

ALTER TABLE ECToOperatorGroupMapping RENAME TO ECToOperatorGroupMap_Delete;
ALTER TABLE ECToOperatorGroupMap_Delete
   DROP CONSTRAINT PK_ECToOpGroupMap;
DROP INDEX PK_ECToOpGroupMap;
/* @error ignore-end */

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
/* @error ignore-begin */
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_EC;
ALTER TABLE ECToResidentialGroupMapping
   DROP CONSTRAINT FK_ECToResGroupMap_YukonGroup;

ALTER TABLE ECToResidentialGroupMapping RENAME TO ECToResidentialGroupMap_delete;
ALTER TABLE ECToResidentialGroupMap_delete
   DROP CONSTRAINT PK_ECToResGroupMap;
DROP INDEX PK_ECToResGroupMap;
/* @error ignore-end */

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

/* Start YUK-11317 */
/* Converting GroupPAOPermission to use user groups instead of role groups. */
/* @error ignore-begin */
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT FK_GROUPPAO_REF_YKGRP_YUKONGRO;
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT FK_GROUPPAO_REF_YUKPA_YUKONPAO;
ALTER TABLE GroupPaoPermission
    DROP CONSTRAINT AK_GRPPAOPERM;

ALTER TABLE GroupPaoPermission RENAME TO GroupPaoPermission_Delete;
ALTER TABLE GroupPaoPermission_Delete
   DROP CONSTRAINT PK_GROUPPAOPERMISSION;
DROP INDEX PK_GROUPPAOPERMISSION;
/* @error ignore-end */

/* Create new GroupPaoPermission table with data. */
CREATE TABLE GroupPaoPermission  (
   GroupPaoPermissionId NUMBER                          NOT NULL,
   UserGroupId          NUMBER                          NOT NULL,
   PaoId                NUMBER                          NOT NULL,
   Permission           VARCHAR2(50)                    NOT NULL,
   Allow                VARCHAR2(5)                     NOT NULL,
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
DECLARE
    v_NewGroupPaoPermissionId    NUMBER;
    v_UserGroupId                NUMBER;
    v_PaoId                      NUMBER;
    v_Permission                 VARCHAR2(50);
    v_Allow                      VARCHAR2(5);
    
    CURSOR groupPaoPerm_curs IS (SELECT DISTINCT UGYGM.UserGroupId, GPP_D.PaoId, GPP_D.Permission, GPP_D.Allow
                                 FROM GroupPaoPermission_Delete GPP_D
                                   JOIN UserGroupToYukonGroupMapping UGYGM ON GPP_D.GroupId = UGYGM.GroupId);
BEGIN
    OPEN groupPaoPerm_curs;
    LOOP
        FETCH groupPaoPerm_curs INTO v_UserGroupId, v_PaoId, v_Permission, v_Allow;
        EXIT WHEN groupPaoPerm_curs%NOTFOUND;

            SELECT NVL(MAX(GroupPaoPermissionId)+1, 1) INTO v_NewGroupPaoPermissionId 
            FROM GroupPaoPermission;
        
            INSERT INTO GroupPaoPermission
            VALUES (v_NewGroupPaoPermissionId, v_UserGroupId, v_PaoId, v_Permission, v_Allow);
            
            COMMIT;
        END LOOP;
    CLOSE groupPaoPerm_curs;
END;
/
/* @end-block */

DROP TABLE GroupPaoPermission_Delete;
/* End YUK-11317 */

/* End YUK-11311 */
CREATE TABLE LMTierGear
(
    GearId    NUMBER    NOT NULL,
    Tier      NUMBER    NOT NULL,
    CONSTRAINT PK_GearId PRIMARY KEY (GearId)
);
ALTER TABLE LMTierGear
    ADD CONSTRAINT FK_GearId_DeviceId FOREIGN KEY (GearId)
        REFERENCES LMProgramDirectGear (GearId)
            ON DELETE CASCADE;
/* End YUK-11311 */
            
/* Start YUK-11192 */
UPDATE YukonListEntry SET EntryText = 'Meter'
WHERE EntryId = 1058;

UPDATE InventoryBase IB
SET IB.AlternateTrackingNumber = ' '
WHERE IB.AlternateTrackingNumber IS NULL;

UPDATE InventoryBase IB
SET IB.Notes = ' '
WHERE IB.Notes IS NULL;

UPDATE InventoryBase IB
SET DeviceLabel = (
      SELECT LMHB.ManufacturerSerialNumber 
      FROM LMHardwareBase LMHB
      WHERE LMHB.InventoryId = IB.InventoryId
)
WHERE (IB.DeviceLabel = '' OR IB.DeviceLabel IS NULL);

UPDATE InventoryBase IB
SET DeviceLabel = (
      SELECT MHB.MeterNumber 
      FROM MeterHardwareBase MHB
      WHERE MHB.InventoryId = IB.InventoryId
)
WHERE (IB.DeviceLabel = '' OR IB.DeviceLabel IS NULL);

UPDATE InventoryBase IB
SET DeviceLabel = (
      SELECT YPAO.PAOName
      FROM YukonPAObject YPAO
      WHERE YPAO.PAObjectID = IB.InventoryId
)
WHERE (IB.DeviceLabel = '' OR IB.DeviceLabel IS NULL);
/* End YUK-11192 */

/* Start YUK-11330 */
ALTER TABLE LMGroupSEP 
ADD RampIn NUMBER;
UPDATE LMGroupSEP
SET RampIn = 30;
ALTER TABLE LMGroupSEP 
MODIFY RampIn NUMBER NOT NULL;
ALTER TABLE LMGroupSEP 
ADD RampOut NUMBER;
UPDATE LMGroupSEP
SET RampOut = 30;
ALTER TABLE LMGroupSEP 
MODIFY RampOut NUMBER NOT NULL;
/* End YUK-11330 */

/* Start YUK-11313 */
CREATE TABLE EncryptionKey (
    EncryptionKeyId NUMBER        NOT NULL,
    Name            VARCHAR2(128) NOT NULL,
    Value           VARCHAR2(512) NOT NULL,
    CONSTRAINT PK_EncryptionKey PRIMARY KEY (EncryptionKeyId)
);

CREATE TABLE YukonPAObjectEncryptionKey (
    PAObjectID      NUMBER NOT NULL,
    EncryptionKeyId NUMBER NOT NULL,
    CONSTRAINT PK_YukonPAObjectEncryptionKey PRIMARY KEY (PAObjectID)
);
DELETE FROM StaticPaoInfo WHERE InfoKey = 'CPS_ONE_WAY_ENCRYPTION_KEY';
/* End YUK-11313 */

/* Start YUK-11293 */
CREATE TABLE ReportedAddressExpressCom (
   ChangeId             NUMBER              NOT NULL,
   DeviceId             NUMBER              NOT NULL,
   Timestamp            DATE                NOT NULL,
   SPID                 NUMBER              NOT NULL,
   GEO                  NUMBER              NOT NULL,
   Substation           NUMBER              NOT NULL,
   Feeder               NUMBER              NOT NULL,
   Zip                  NUMBER              NOT NULL,
   UDA                  NUMBER              NOT NULL,
   Required             NUMBER              NOT NULL,
   CONSTRAINT PK_ReportedAddressExpressCom PRIMARY KEY (ChangeId)
);

ALTER TABLE ReportedAddressExpressCom
   ADD CONSTRAINT FK_RepAddExpressCom_Device FOREIGN KEY (DeviceId)
      REFERENCES Device (DeviceId);

CREATE TABLE ReportedAddressRelayExpressCom (
   ChangeId             NUMBER              NOT NULL,
   RelayNumber          NUMBER              NOT NULL,
   Program              NUMBER              NOT NULL,
   Splinter             NUMBER              NOT NULL,
   CONSTRAINT PK_ReportedAddressRelayExpCom PRIMARY KEY (ChangeId, RelayNumber)
);

ALTER TABLE ReportedAddressRelayExpressCom
   ADD CONSTRAINT FK_RepAddRelayExp_RepAddExpCom FOREIGN KEY (ChangeId)
      REFERENCES ReportedAddressExpressCom (ChangeId);
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
/* End YUK-11329 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 