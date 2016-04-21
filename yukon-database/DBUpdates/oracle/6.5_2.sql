/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-15182 */
INSERT INTO State VALUES(-20, 14, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 15, 'N/A', 9, 6, 0);
INSERT INTO State VALUES(-20, 16, 'N/A', 9, 6, 0);
/* End YUK-15182 */

/* Start YUK-15216 */
UPDATE DeviceConfigCategoryItem 
SET ItemName = 'timeOffset', 
    ItemValue = CASE 
                  WHEN ItemValue='true' THEN 'LOCAL' 
                  ELSE 'UTC' 
                END
WHERE ItemName = 'localTime';
/* End YUK-15216 */

/* Start YUK-15201 */
CREATE TABLE StoredProcedureLog (
  EntryId           NUMBER          NOT NULL,
  ProcedureName     VARCHAR2(50)    NOT NULL,
  LogDate           DATE            NOT NULL,
  LogString         VARCHAR2(500)   NOT NULL,
  CONSTRAINT PK_StoredProcedureLog PRIMARY KEY (EntryId)
);
/* End YUK-15201 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
INSERT INTO CTIDatabase VALUES ('6.5', '06-APR-2016', 'Latest Update', 2, SYSDATE);