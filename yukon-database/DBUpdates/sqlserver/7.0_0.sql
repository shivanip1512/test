/******************************************/
/**** SQL Server DBupdates             ****/
/******************************************/

/* Start YUK-17045 */
IF( (SELECT Count(*) 
     FROM   yukonlistentry 
     WHERE  entrytext = 'LCR-6700(RFN)') = 0 ) 
  INSERT INTO yukonlistentry 
  VALUES      ((SELECT Max(entryid) + 1 
                FROM   yukonlistentry 
                WHERE  entryid < 10000), 
               1005, 
               0, 
               'LCR-6700(RFN)', 
               1337); 
/* End YUK-17045 */

/**************************************************************/
/* VERSION INFO                                               */
/* Inserted when update script is run                         */
/**************************************************************/
/*INSERT INTO CTIDatabase VALUES ('7.0', '01-AUG-2017', 'Latest Update', 0, GETDATE());*/