create table DYNAMICCAPCONTROLSTRATEGY (
   CapStrategyID        NUMBER                           not null,
   NewPointDataReceived CHAR(1)                          not null,
   StrategyUpdated      CHAR(1)                          not null,
   ActualVarPointValue  FLOAT                            not null,
   NextCheckTime        DATE                             not null,
   CalcVarPointValue    FLOAT                            not null,
   Operations           NUMBER                           not null,
   LastOperation        DATE                             not null,
   LastCapBankControlled NUMBER                           not null,
   PeakOrOffPeak        CHAR(1)                          not null,
   RecentlyControlled   CHAR(1)                          not null,
   CalcValueBeforeControl FLOAT                            not null,
   TimeStamp            DATE                             not null,
   constraint PK_DYNAMICCAPCONTROLSTRATEGY primary key (CapStrategyID),
   constraint FK_DYNAMICC_REFERENCE_CAPCONTR foreign key (CapStrategyID)
         references CAPCONTROLSTRATEGY (CAPSTRATEGYID)
);