/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9557 */
UPDATE CapControlStrategy 
SET ControlUnits = 'MULTI_VOLT_VAR' 
WHERE ControlUnits = 'Multi Volt Var'; 
/* End YUK-9557 */

/* Start YUK-9565 */
UPDATE State 
SET ForegroundColor = 3 
WHERE StateGroupId = -14 
  AND RawState = 1; 
UPDATE State 
SET ForegroundColor = 1 
WHERE StateGroupId = -14 
  AND RawState = 2;
/* End YUK-9565 */

/* Start YUK-9575 */
ALTER TABLE ServiceCompanyDesignationCode 
    DROP CONSTRAINT FK_SRVCODSGNTNCODES_SRVCO;

ALTER TABLE ServiceCompanyDesignationCode 
    ADD CONSTRAINT FK_ServCompDesCode_ServComp FOREIGN KEY (ServiceCompanyId) 
        REFERENCES ServiceCompany (CompanyId) 
            ON DELETE CASCADE;
/* End YUK-9575 */

/* Start YUK-9603 */
INSERT INTO YukonRoleProperty VALUES (-1120, -2, 'Allow Designation Codes', 'false', 'Toggles on or off the regional (usually zip) code option for service companies.') 

DELETE FROM YukonUserRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonGroupRole 
WHERE RolePropertyId = -20008; 
DELETE FROM YukonRoleProperty 
WHERE RolePropertyId = -20008;
/* End YUK-9603 */

/* Start YUK-9595 */
INSERT INTO Command VALUES (-187, 'getconfig ied dnp address', 'Read DNP address configuration', 'MCT-470');
INSERT INTO Command VALUES (-188, 'putconfig emetcon ied dnp address master ?''Master address'' outstation ?''Outstation address''', 'Write DNP address configuration', 'MCT-470');

INSERT INTO DeviceTypeCommand VALUES (-814, -187, 'MCT-470', 34, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-815, -188, 'MCT-470', 35, 'Y', -1);
/* End YUK-9595 */

/* Start YUK-9646 */
/* @error ignore-begin */
ALTER TABLE CCMonitorBankList
    DROP CONSTRAINT FK_CCMONBNKLIST_BNKID;
ALTER TABLE CCMonitorBankList
    DROP CONSTRAINT FK_CCMONBNKLST_PTID;

ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_CapBank FOREIGN KEY (BankId)
        REFERENCES CapBank (DeviceId)
            ON DELETE CASCADE;
ALTER TABLE CCMonitorBankList
    ADD CONSTRAINT FK_CCMonBankList_Point FOREIGN KEY (PointId)
        REFERENCES Point (PointId)
            ON DELETE CASCADE;
/* @error ignore-end */
/* End YUK-9646 */

/* Start YUK-9648 */
ALTER TABLE ZBDevice 
ADD MacAddress VARCHAR2(255); 

UPDATE ZBDevice
SET MacAddress = ' ';

ALTER TABLE ZBDevice 
MODIFY MacAddress VARCHAR2(255) NOT NULL; 

ALTER TABLE ZBDevice RENAME TO ZBEndPoint;

ALTER TABLE ZBEndPoint RENAME CONSTRAINT PK_ZBDevice to PK_ZBEndPoint;
ALTER INDEX PK_ZBDevice RENAME TO PK_ZBEndPoint;

ALTER TABLE ZBEndPoint RENAME CONSTRAINT FK_ZBDevice_Device to FK_ZBEndPoint_Device;
ALTER TABLE ZBGatewayToDeviceMapping RENAME CONSTRAINT FK_ZBGateDeviceMap_ZBDevice to FK_ZBGateDeviceMap_ZBEndPoint;
/* End YUK-9648 */

/* Start YUK-9669 */
UPDATE State 
SET Text = 'Decommissioned' 
WHERE StateGroupId = -13 
  AND RawState = 1;
/* End YUK-9669 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
