create table Program (
        ProgramID       INTEGER         PRIMARY KEY,      
        Name            VARCHAR2(20)     NOT NULL UNIQUE,
        State           VARCHAR2(20)     NOT NULL,
        StartTimeStamp  DATE,
        StopTimeStamp   DATE,
        TotalControlTime INTEGER        NOT NULL )
/
        
        
