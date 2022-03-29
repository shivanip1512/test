/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* @start YUK-26160 */

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v4/MR_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v4/OD_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v4/CD_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'DR_Server', 'http://127.0.0.1:8080/multispeak/v4/DR_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'SCADA_Server', 'http://127.0.0.1:8080/multispeak/v4/SCADA_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v4/NOT_Server', '4.1', '1', NULL, NULL, NULL, NULL, NULL);

INSERT INTO DBUpdates VALUES ('YUK-26160', '9.3.0', SYSDATE);
/* @end YUK-26160 */

/***********************************************************************************/
/* VERSION INFO                                                                    */
/* Inserted when update script is run, stays commented out until the release build */
/***********************************************************************************/
/*INSERT INTO CTIDatabase VALUES ('9.3', '29-MAR-2022', 'Latest Update', 0, SYSDATE);*/