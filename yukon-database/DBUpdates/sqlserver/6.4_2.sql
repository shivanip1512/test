/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-14220 */
DELETE FROM DynamicPointDispatch 
WHERE PointId IN (
    SELECT PointId 
    FROM Point p 
    JOIN YukonPaobject pao ON p.PAObjectID = pao.PAObjectID
    WHERE Type LIKE 'RFN-%')
AND (Timestamp > DATEADD(YEAR, 1, GETDATE())
OR Timestamp < '01-JAN-2000');
/* End YUK-14220 */

/* Start YUK-13002 */
INSERT INTO Command VALUES (-193, 'getconfig install all', 'Read configuration', 'ALL RFNs');
INSERT INTO Command VALUES (-194, 'getconfig install display', 'Read display metrics', 'ALL RFNs');
INSERT INTO Command VALUES (-195, 'getconfig install freezeday', 'Read demand freeze day and last freeze timestamp', 'ALL RFNs');
INSERT INTO Command VALUES (-196, 'getconfig tou', 'Read TOU status', 'ALL RFNs');
INSERT INTO Command VALUES (-197, 'getconfig holidays', 'Read holiday schedule', 'ALL RFNs');
INSERT INTO Command VALUES (-198, 'getvalue voltage profile ?''MM/DD/YYYY'' ?''HH:mm'' ?''MM/DD/YYYY'' ?''HH:mm''', 'Read voltage profile data, start date required, rest of fields optional', 'ALL RFNs');

INSERT INTO DeviceTypeCommand VALUES (-1020, -193, 'RFN-410fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1021, -194, 'RFN-410fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1022, -195, 'RFN-410fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1023, -196, 'RFN-410fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1024, -197, 'RFN-410fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1025, -198, 'RFN-410fL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1026, -193, 'RFN-410fX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1027, -194, 'RFN-410fX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1028, -195, 'RFN-410fX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1029, -196, 'RFN-410fX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1030, -197, 'RFN-410fX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1031, -198, 'RFN-410fX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1032, -193, 'RFN-410fD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1033, -194, 'RFN-410fD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1034, -195, 'RFN-410fD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1035, -196, 'RFN-410fD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1036, -197, 'RFN-410fD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1037, -198, 'RFN-410fD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1038, -193, 'RFN-420fL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1039, -194, 'RFN-420fL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1040, -195, 'RFN-420fL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1041, -196, 'RFN-420fL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1042, -197, 'RFN-420fL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1043, -198, 'RFN-420fL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1044, -193, 'RFN-420fX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1045, -194, 'RFN-420fX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1046, -195, 'RFN-420fX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1047, -196, 'RFN-420fX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1048, -197, 'RFN-420fX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1049, -198, 'RFN-420fX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1050, -193, 'RFN-420fD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1051, -194, 'RFN-420fD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1052, -195, 'RFN-420fD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1053, -196, 'RFN-420fD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1054, -197, 'RFN-420fD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1055, -198, 'RFN-420fD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1056, -193, 'RFN-420fRX', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1057, -194, 'RFN-420fRX', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1058, -195, 'RFN-420fRX', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1059, -196, 'RFN-420fRX', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1060, -197, 'RFN-420fRX', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1061, -198, 'RFN-420fRX', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1062, -193, 'RFN-420fRD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1063, -194, 'RFN-420fRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1064, -195, 'RFN-420fRD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1065, -196, 'RFN-420fRD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1066, -197, 'RFN-420fRD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1067, -198, 'RFN-420fRD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1068, -193, 'RFN-410cL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1069, -194, 'RFN-410cL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1070, -195, 'RFN-410cL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1071, -196, 'RFN-410cL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1072, -197, 'RFN-410cL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1073, -198, 'RFN-410cL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1074, -193, 'RFN-420cL', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1075, -194, 'RFN-420cL', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1076, -195, 'RFN-420cL', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1077, -196, 'RFN-420cL', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1078, -197, 'RFN-420cL', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1079, -198, 'RFN-420cL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1080, -193, 'RFN-420cD', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1081, -194, 'RFN-420cD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1082, -195, 'RFN-420cD', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1083, -196, 'RFN-420cD', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1084, -197, 'RFN-420cD', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1085, -198, 'RFN-420cD', 6, 'Y', -1);
 
INSERT INTO DeviceTypeCommand VALUES (-1086, -193, 'RFN-430A3D', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1087, -194, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1088, -195, 'RFN-430A3D', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1089, -196, 'RFN-430A3D', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1090, -197, 'RFN-430A3D', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1091, -193, 'RFN-430A3T', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1092, -194, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1093, -195, 'RFN-430A3T', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1094, -196, 'RFN-430A3T', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1095, -197, 'RFN-430A3T', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1096, -193, 'RFN-430A3K', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1097, -194, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1098, -195, 'RFN-430A3K', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1099, -196, 'RFN-430A3K', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1100, -197, 'RFN-430A3K', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1101, -193, 'RFN-430A3R', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1102, -194, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1103, -195, 'RFN-430A3R', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1104, -196, 'RFN-430A3R', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1105, -197, 'RFN-430A3R', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1106, -193, 'RFN-430KV', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1107, -194, 'RFN-430KV', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1108, -195, 'RFN-430KV', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1109, -196, 'RFN-430KV', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1110, -197, 'RFN-430KV', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1111, -193, 'RFN-430SL0', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1112, -194, 'RFN-430SL0', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1113, -195, 'RFN-430SL0', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1114, -196, 'RFN-430SL0', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1115, -197, 'RFN-430SL0', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1116, -193, 'RFN-430SL1', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1117, -194, 'RFN-430SL1', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1118, -195, 'RFN-430SL1', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1119, -196, 'RFN-430SL1', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1120, -197, 'RFN-430SL1', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1121, -193, 'RFN-430SL2', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1122, -194, 'RFN-430SL2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1123, -195, 'RFN-430SL2', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1124, -196, 'RFN-430SL2', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1125, -197, 'RFN-430SL2', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1126, -193, 'RFN-430SL3', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1127, -194, 'RFN-430SL3', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1128, -195, 'RFN-430SL3', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1129, -196, 'RFN-430SL3', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1130, -197, 'RFN-430SL3', 5, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1131, -193, 'RFN-430SL4', 1, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1132, -194, 'RFN-430SL4', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1133, -195, 'RFN-430SL4', 3, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1134, -196, 'RFN-430SL4', 4, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1135, -197, 'RFN-430SL4', 5, 'Y', -1);
/* End YUK-13002 */

/* Start YUK-14185 */
CREATE INDEX AK_RepAddExpressCom_DeviceId
ON ReportedAddressExpressCom (DeviceId);
/* End YUK-14185 */

/* Start YUK-14310 */
DELETE FROM UserPageParam WHERE UserPageId IN (SELECT UserPageId FROM UserPage WHERE PagePath LIKE '/adminSetup%');
DELETE FROM UserPage WHERE PagePath LIKE '/adminSetup%';
/* End YUK-14310 */

/* Start YUK-14250 */
UPDATE State SET Text = 'Va'  WHERE StateGroupId = -17 AND RawState = 7;
UPDATE State SET Text = 'Vb'  WHERE StateGroupId = -17 AND RawState = 8;
UPDATE State SET Text = 'Vc'  WHERE StateGroupId = -17 AND RawState = 9;
UPDATE State SET Text = 'Ia'  WHERE StateGroupId = -17 AND RawState = 10;
UPDATE State SET Text = 'Ib'  WHERE StateGroupId = -17 AND RawState = 11;
UPDATE State SET Text = 'Ic'  WHERE StateGroupId = -17 AND RawState = 12;
UPDATE State SET Text = 'N/A' WHERE StateGroupId = -17 AND RawState = 14;

INSERT INTO State VALUES
(-17, 17, 'Bad Active Relay', 3, 6, 0),
(-17, 18, 'NC Lockout', 4, 6, 0),
(-17, 19, 'Control Accepted', 5, 6, 0),
(-17, 20, 'Auto Mode', 7, 6, 0),
(-17, 21, 'Reclose Block', 8, 6, 0);
/* End YUK-14250 */

/* Start YUK-14094 */
INSERT INTO Command VALUES (-199, 'getconfig install', 'Read configuration', 'All Two Way LCR');
INSERT INTO Command VALUES (-200, 'getconfig address', 'Read addressing', 'All Two Way LCR');
INSERT INTO Command VALUES (-201, 'getvalue temperature', 'Read temperature', 'All Two Way LCR');
INSERT INTO Command VALUES (-202, 'getconfig time', 'Read Date/Time', 'All Two Way LCR');
INSERT INTO Command VALUES (-203, 'getvalue power', 'Read transmit power', 'All Two Way LCR');
 
INSERT INTO DeviceTypeCommand VALUES (-1136, -199, 'LCR-3102', 11, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1137, -200, 'LCR-3102', 12, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1138, -201, 'LCR-3102', 13, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1139, -202, 'LCR-3102', 14, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1140, -203, 'LCR-3102', 15, 'Y', -1);
/* End YUK-14094 */

/* Start YUK-14248 */
UPDATE DeviceTypeCommand SET DeviceType = 'MCT-410IL' WHERE DeviceCommandId IN (-379, -380, -388, -392, -395, -396);
UPDATE DeviceTypeCommand SET DeviceType = 'MCT-410CL' WHERE DeviceCommandId IN (-381, -382, -398, -402, -405, -406);
UPDATE DeviceTypeCommand SET DeviceType = 'MCT-410FL' WHERE DeviceCommandId IN (-518, -519, -520, -524, -527, -528);
UPDATE DeviceTypeCommand SET DeviceType = 'MCT-410GL' WHERE DeviceCommandId IN (-543, -544, -545, -549, -552, -553);
/* End YUK-14248 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.4', '08-MAY-2015', 'Latest Update', 2, GETDATE());