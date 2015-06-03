/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-14327 */
INSERT INTO Command VALUES (-204, 'putconfig temp enable', 'Enable Temp Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-205, 'putconfig temp disable', 'Disable Temp Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-206, 'putconfig var enable', 'Enable Var Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-207, 'putconfig var disable', 'Disable Var Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-208, 'putconfig time enable', 'Enable Time Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-209, 'putconfig time disable', 'Disable Time Control', 'Twoway CBCs');
INSERT INTO Command VALUES (-210, 'putconfig ovuv enable', 'Enable OVUV', 'Twoway CBCs');
INSERT INTO Command VALUES (-211, 'putconfig ovuv disable', 'Disable OVUV', 'Twoway CBCs');

INSERT INTO DeviceTypeCommand VALUES (-1141, -204, 'CBC 8020', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1142, -205, 'CBC 8020', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1143, -206, 'CBC 8020', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1144, -207, 'CBC 8020', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1145, -208, 'CBC 8020', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1146, -209, 'CBC 8020', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1147, -210, 'CBC 8020', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1148, -211, 'CBC 8020', 8, 'Y', -1);

INSERT INTO DeviceTypeCommand VALUES (-1149, -204, 'CBC 8024', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1150, -205, 'CBC 8024', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1151, -206, 'CBC 8024', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1152, -207, 'CBC 8024', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1153, -208, 'CBC 8024', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1154, -209, 'CBC 8024', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1155, -210, 'CBC 8024', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1156, -211, 'CBC 8024', 8, 'Y', -1);
/* End YUK-14327 */

/* Start YUK-14350 */
DELETE FROM GlobalSetting WHERE Name IN ('AUTH_METHOD', 'AUTHENTICATION_MODE');
/* End YUK-14350 */

/* Start YUK-14334 */
ALTER TABLE RegulatorToZoneMapping
    DROP CONSTRAINT FK_ZoneReg_Reg;

ALTER TABLE RegulatorToZoneMapping
    ADD CONSTRAINT FK_RegulatorToZoneMap_Device FOREIGN KEY (RegulatorId)
        REFERENCES Regulator (RegulatorId)
           ON DELETE CASCADE;
/* End YUK-14334 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.4', '31-MAY-2015', 'Latest Update', 3, SYSDATE);*/