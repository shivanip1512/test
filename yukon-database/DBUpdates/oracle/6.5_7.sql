/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15712 */
INSERT INTO Command VALUES (-214, 'putconfig install all', 'Send configuration', 'ALL RFNs');
/* End YUK-15712 */

/* Start YUK-15711 */
INSERT INTO DeviceTypeCommand VALUES (-1234, -214, 'RFN-410fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1232, -214, 'RFN-410fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1238, -214, 'RFN-420fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1241, -214, 'RFN-420fX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1237, -214, 'RFN-420fD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1240, -214, 'RFN-420fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1239, -214, 'RFN-420fRD', 7, 'Y', -1); 
INSERT INTO DeviceTypeCommand VALUES (-1231, -214, 'RFN-410cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1236, -214, 'RFN-420cL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1235, -214, 'RFN-420cD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1242, -214, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1245, -214, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1243, -214, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1244, -214, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1246, -214, 'RFN-430KV', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1247, -214, 'RFN-430SL0', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1248, -214, 'RFN-430SL1', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1249, -214, 'RFN-430SL2', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1250, -214, 'RFN-430SL3', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1251, -214, 'RFN-430SL4', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1255, -214, 'RFN-510fL', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1256, -214, 'RFN-520fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1257, -214, 'RFN-520fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1258, -214, 'RFN-520fAXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1259, -214, 'RFN-520fRXD', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1260, -214, 'RFN-530fAX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1261, -214, 'RFN-530fRX', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1262, -214, 'RFN-530S4x', 7, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1263, -214, 'RFN-530S4eAD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1264, -214, 'RFN-530S4eAT', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1265, -214, 'RFN-530S4eRD', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1266, -214, 'RFN-530S4eRT', 2, 'Y', -1);

DELETE FROM DeviceTypeCommand WHERE DeviceCommandID IN (-1045, -1051, -1057, -1063, -1075, -1081, -1087, -1088, -1089,
    -1090, -1092, -1093, -1094, -1095, -1097, -1098, -1099, -1100, -1102, -1103, -1104, -1105, -1112, -1113, -1114,
    -1115, -1117, -1118, -1119, -1120, -1122, -1123, -1124, -1125, -1127, -1128, -1129, -1130, -1132, -1133, -1134,
    -1135, -1164, -1170, -1176, -1182, -1206, -1207, -1208, -1209, -1210, -1212, -1213, -1214, -1215, -1216, -1218,
    -1219, -1220, -1221, -1222, -1224, -1225, -1226, -1227, -1228 );
/* End YUK-15711 */

/* Start YUK-15972 */
ALTER TABLE DynamicPAOInfo DROP CONSTRAINT FK_DynPAOInfo_YukPAO;

ALTER TABLE DynamicPAOInfo
   ADD CONSTRAINT FK_DynPAOInfo_YukPAO FOREIGN KEY (PAObjectID)
      REFERENCES YukonPAObject (PAObjectID)
         ON DELETE CASCADE;
/* End YUK-15972 */

/* Start YUK-16017 */
INSERT INTO YukonRoleProperty VALUES(-20222,-202,'Water Leak Report','true','Controls access to the Water Leak Report.');
/* End YUK-16017 */     

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.5', '08-DEC-2016', 'Latest Update', 7, SYSDATE);