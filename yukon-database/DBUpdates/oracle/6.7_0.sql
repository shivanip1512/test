/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-16106 */
UPDATE state SET foregroundcolor = 1 WHERE stategroupid = -28 AND rawstate = 0;
UPDATE state SET foregroundcolor = 4 WHERE stategroupid = -28 AND rawstate = 1;
UPDATE state SET foregroundcolor = 9 WHERE stategroupid = -28 AND rawstate = 2;
/* End YUK-16106 */

/* Start YUK-16173 */
ALTER TABLE MSPInterface
DROP CONSTRAINT PK_MSPINTERFACE;

ALTER TABLE MSPInterface
ADD Version NUMBER(10,1);

UPDATE MSPInterface 
SET Version = 3.0;

ALTER TABLE MSPInterface
MODIFY (Version NUMBER(10,1) NOT NULL);

ALTER TABLE MSPInterface
ADD CONSTRAINT PK_MSPINTERFACE PRIMARY KEY (VendorID,Interface,Version);

ALTER TABLE MSPInterface
MODIFY (Endpoint VARCHAR2(255));

UPDATE MSPInterface MI
SET endpoint = (
    SELECT MV.URL || MI.Endpoint
    FROM MSPVendor MV
    WHERE MI.VendorID = MV.VendorID);

ALTER TABLE MSPVendor 
DROP COLUMN URL;

INSERT INTO MSPInterface VALUES (1, 'MR_Server', 'http://127.0.0.1:8080/multispeak/v5/MR_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'OD_Server', 'http://127.0.0.1:8080/multispeak/v5/OD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'CD_Server', 'http://127.0.0.1:8080/multispeak/v5/CD_Server', '5.0');
INSERT INTO MSPInterface VALUES (1, 'NOT_Server', 'http://127.0.0.1:8080/multispeak/v5/NOT_Server', '5.0');
/* End YUK-16173 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('6.7', '04-JAN-2017', 'Latest Update', 0, SYSDATE);*/
