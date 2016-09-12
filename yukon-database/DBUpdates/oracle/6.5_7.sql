/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15711*/
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1045'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1051'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1057'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1063'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1087';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1088';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1089'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1090';

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1092'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1093'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1094'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1095'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1097';  
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1098'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1099';  
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1100';  

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1102'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1103'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1104'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1105'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1112'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1113'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1114'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1115'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1117'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1118'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1119'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1120'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1122'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1123'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1124'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1125'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1127'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1128'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1129'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1130'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1132'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1133';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1134'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1135'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1164'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1170'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1176'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1182'; 

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1206';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1207';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1208';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1209';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1210';

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1212';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1213';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1214';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1215';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1216';

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1218';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1219';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1220';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1221';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1222';

DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1224'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1225'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1226';
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1227'; 
DELETE FROM DeviceTypeCommand Where DeviceCommandID = '-1228';  

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1046;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1047;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1048;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1049;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1052;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1053;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1054;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1058;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1059;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1060;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1061;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1064;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1065;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1066;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1067;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1165;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1166;
UPDATE DeviceTypeCommand SET DisplayOrder = '4'  WHERE DeviceCommandID = -1167;
UPDATE DeviceTypeCommand SET DisplayOrder = '5'  WHERE DeviceCommandID = -1168;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1171;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1172;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1173;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1174;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1177;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1178;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1179;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1180;

UPDATE DeviceTypeCommand SET DisplayOrder = '2' WHERE DeviceCommandID = -1183;
UPDATE DeviceTypeCommand SET DisplayOrder = '3' WHERE DeviceCommandID = -1184;
UPDATE DeviceTypeCommand SET DisplayOrder = '4' WHERE DeviceCommandID = -1185;
UPDATE DeviceTypeCommand SET DisplayOrder = '5' WHERE DeviceCommandID = -1186;
/* End YUK-15711*/

/* Start YUK-15712*/
INSERT INTO Command VALUES (-214, 'putconfig install all', 'Send configuration', 'ALL RFNs');

INSERT INTO DeviceTypeCommand VALUES (-1231, -214, 'RFN-410cL', 6, 'Y', -1);    
INSERT INTO DeviceTypeCommand VALUES (-1232, -214, 'RFN-410fD', 7, 'Y', -1);//      
INSERT INTO DeviceTypeCommand VALUES (-1233, -214, 'RFN-410fL', 7, 'Y', -1);//
INSERT INTO DeviceTypeCommand VALUES (-1234, -214, 'RFN-410fX', 7, 'Y', -1);//
INSERT INTO DeviceTypeCommand VALUES (-1235, -214, 'RFN-420cD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1236, -214, 'RFN-420cL', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1237, -214, 'RFN-420fD', 6, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1238, -214, 'RFN-420fL', 7, 'Y', -1);//
INSERT INTO DeviceTypeCommand VALUES (-1239, -214, 'RFN-420fRD', 5, 'Y', -1 ;   
INSERT INTO DeviceTypeCommand VALUES (-1240, -214, 'RFN-420fRX', 6, 'Y', -1);//
INSERT INTO DeviceTypeCommand VALUES (-1241, -214, 'RFN-420fX', 6, 'Y', -1);//
INSERT INTO DeviceTypeCommand VALUES (-1242, -214, 'RFN-430A3D', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1243, -214, 'RFN-430A3K', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1244, -214, 'RFN-430A3R', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1245, -214, 'RFN-430A3T', 2, 'Y', -1);
INSERT INTO DeviceTypeCommand VALUES (-1246, -214, 'RFN-430KV', 6, 'Y', -1);//
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
/* End YUK-15712*/

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.5', '18-AUG-2016', 'Latest Update', 7, SYSDATE);*/