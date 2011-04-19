/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9705 */
ALTER TABLE OptOutEvent DROP CONSTRAINT FK_OptOutEvent_CustAcct;
GO

ALTER TABLE OptOutEvent
    ALTER COLUMN CustomerAccountId NUMERIC NULL;
GO

ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_CustAcct FOREIGN KEY(CustomerAccountId) 
        REFERENCES CustomerAccount (AccountId) 
            ON DELETE SET NULL;
/* End YUK-9705 */

/* Start YUK-9647 */
EXEC SP_RENAME 'DynamicPAOStatistics', 'DynamicPAOStatisticsOld';

ALTER TABLE DynamicPAOStatisticsOld
    DROP CONSTRAINT PK_DynamicPAOStatistics;

UPDATE DynamicPAOStatisticsOld
SET StatisticType='Monthly'
WHERE StatisticType='LastMonth';

CREATE TABLE DynamicPAOStatistics ( 
   DynamicPAOStatisticsId   NUMERIC          NOT NULL, 
   PAObjectId               NUMERIC          NOT NULL, 
   StatisticType            VARCHAR(16)      NOT NULL, 
   StartDateTime            DATETIME         NOT NULL, 
   Requests                 NUMERIC          NOT NULL, 
   Attempts                 NUMERIC          NOT NULL, 
   Completions              NUMERIC          NOT NULL, 
   CommErrors               NUMERIC          NOT NULL, 
   ProtocolErrors           NUMERIC          NOT NULL, 
   SystemErrors             NUMERIC          NOT NULL, 
   CONSTRAINT PK_DynPAOStat PRIMARY KEY (DynamicPAOStatisticsId) 
);
GO

ALTER TABLE DynamicPaoStatistics 
    ADD CONSTRAINT FK_DynPAOStat_PAO FOREIGN KEY (PAObjectId) 
        REFERENCES YukonPAObject (PAObjectId) 
            ON DELETE CASCADE;

CREATE UNIQUE INDEX Indx_DynPAOStat_PId_ST_SD_UNQ ON DynamicPAOStatistics (
   PAOBjectId ASC,
   StatisticType ASC,
   StartDateTime ASC
);

INSERT INTO DynamicPaoStatistics 
SELECT ROW_NUMBER() OVER (ORDER BY StartDateTime, PAObjectId, StatisticType), 
        PAObjectId, statistictype, startdatetime, requests, attempts, completions, 
        commerrors, protocolerrors, systemerrors 
FROM DynamicPAOStatisticsOld 
WHERE StatisticType IN ('Lifetime', 'Monthly', 'Daily');

/* @start-block */ 
DECLARE 
    @maxId NUMERIC; 
BEGIN 
    SELECT @maxId = MAX(DynamicPaoStatisticsId)
    FROM DynamicPaoStatistics; 

    INSERT INTO DynamicPAOStatistics 
    SELECT @maxId + ROW_NUMBER() OVER (ORDER BY PAObjectId, DateOffset), PAObjectId, 
            'Daily', DATEADD(Day, DateOffset, '1970-01-01') Requests, Attempts, 
            Completions, CommErrors, ProtocolErrors, SystemErrors 
    FROM DynamicPAOStatisticsHistory; 
END; 
/* @end-block */ 

DROP TABLE DynamicPAOStatisticsOld; 
DROP TABLE DynamicPAOStatisticsHistory;
/* End YUK-9647 */

/* Start YUK-9723 */
DELETE FROM DeviceTapPagingSettings 
WHERE DeviceId IN (SELECT PAObjectId 
                    FROM YukonPAObject 
                    WHERE type='TNPP TERMINAL'); 
/* End YUK-9723 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
