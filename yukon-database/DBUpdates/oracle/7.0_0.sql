/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-17045 */
INSERT INTO yukonlistentry 
SELECT (SELECT Max(entryid) + 1 
        FROM   yukonlistentry 
        WHERE  entryid < 10000), 
       1005, 
       0, 
       'LCR-6700(RFN)', 
       1337 
FROM   dual 
WHERE  NOT EXISTS (SELECT * 
                   FROM   yukonlistentry 
                   WHERE  entrytext = 'LCR-6700(RFN)'); 
/* End YUK-17045 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, SYSDATE);*/
