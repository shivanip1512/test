IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'PKC_RawPointHistory')
BEGIN
    DROP INDEX Index_PointID ON RawPointHistory;
    DROP INDEX Indx_TimeStamp ON RawPointHistory;
    DROP INDEX Indx_RwPtHisPtIDTst ON RawPointHistory;
    DROP INDEX Indx_RwPtHisTstPtId ON RawPointHistory;
    
    ALTER TABLE RPHTag
       DROP CONSTRAINT FK_RPHTag_RPH;
    ALTER TABLE RawPointHistory
        DROP CONSTRAINT PK_RawPointHistory;
    
    /* Creating this will take a while!!!! Slightly longer than the below one */
    ALTER TABLE RawPointHistory ADD CONSTRAINT PKC_RawPointHistory PRIMARY KEY CLUSTERED 
    (
           ChangeId ASC
    ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, 
            ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);
    
    
    /* Creating this will take a while!!!! */
    CREATE NONCLUSTERED INDEX Indx_RawPointHistory_PtId_Ts ON RawPointHistory
    (
           PointId ASC,
           TimeStamp DESC
    )
    INCLUDE 
    (
        Quality,
        Value
    ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF,
            ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON);
    
    ALTER TABLE RPHTag
       ADD CONSTRAINT FK_RPHTag_RPH FOREIGN KEY (ChangeId)
          REFERENCES RawPointHistory (ChangeId)
             ON DELETE CASCADE;
END;