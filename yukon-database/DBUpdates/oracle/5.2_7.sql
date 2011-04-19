/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9705 */
ALTER TABLE OptOutEvent DROP CONSTRAINT FK_OptOutEvent_CustAcct;

ALTER TABLE OptOutEvent
    MODIFY CustomerAccountId NUMBER NULL;

ALTER TABLE OptOutEvent 
    ADD CONSTRAINT FK_OptOutEvent_CustAcct FOREIGN KEY(CustomerAccountId) 
        REFERENCES CustomerAccount (AccountId) 
            ON DELETE SET NULL;
/* End YUK-9705 */

/* Start YUK-9647 */
ALTER TABLE DynamicPAOStatistics RENAME TO DynamicPAOStatisticsOld;

ALTER TABLE DynamicPAOStatisticsOld
    DROP CONSTRAINT PK_DynamicPAOStatistics;

/* @error ignore-begin */
DROP INDEX PK_DynamicPAOStatistics;
/* @error ignore-end */
    
UPDATE DynamicPAOStatisticsOld
SET StatisticType = 'Monthly'
WHERE StatisticType = 'LastMonth';

CREATE TABLE DynamicPAOStatistics ( 
   DynamicPAOStatisticsId          NUMBER                 NOT NULL, 
   PAObjectId                      NUMBER                 NOT NULL, 
   StatisticType                   VARCHAR2(16)           NOT NULL, 
   StartDateTime                   DATE                  NOT NULL, 
   Requests                        NUMBER                 NOT NULL, 
   Attempts                        NUMBER                 NOT NULL, 
   Completions                     NUMBER                 NOT NULL, 
   CommErrors                      NUMBER                 NOT NULL, 
   ProtocolErrors                  NUMBER                 NOT NULL, 
   SystemErrors                    NUMBER                 NOT NULL, 
   CONSTRAINT PK_DynPAOStat PRIMARY KEY (DynamicPAOStatisticsId) 
);

ALTER TABLE DynamicPaoStatistics 
    ADD CONSTRAINT FK_DynPAOStat_PAO FOREIGN KEY (PAObjectId) 
        REFERENCES YukonPAObject (PAObjectId) 
            ON DELETE CASCADE; 

CREATE UNIQUE INDEX Indx_DynPAOStat_PId_ST_SD_UNQ ON DynamicPAOStatistics (
   PAOBjectId ASC,
   StatisticType ASC,
   StartDateTime ASC
);

INSERT INTO DynamicPAOStatistics 
SELECT ROW_NUMBER() OVER (ORDER BY StartDateTime, PAObjectId, StatisticType), 
        PAObjectId, StatisticType, StartDateTime, Requests, Attempts, Completions, 
        CommErrors, ProtocolErrors, SystemErrors 
FROM DynamicPAOStatisticsOld 
WHERE StatisticType IN ('Lifetime', 'Monthly', 'Daily');

/* @start-block */ 
DECLARE 
    maxId number; 
BEGIN 
    SELECT MAX(DynamicPaoStatisticsId) INTO maxId
    FROM DynamicPaoStatistics; 

    INSERT INTO DynamicPAOStatistics 
    SELECT maxId + ROW_NUMBER() OVER (ORDER BY PAObjectId, DateOffset), PAObjectId, 
            'Daily', TO_DATE('1970-01-01', 'yyyy-mm-dd') + DateOffset, Requests, Attempts, 
            Completions, CommErrors, ProtocolErrors, SystemErrors 
    FROM DynamicPAOStatisticsHistory; 
END;
/
/* @end-block */ 

DROP TABLE DynamicPaoStatisticsOld CASCADE CONSTRAINTS; 
DROP TABLE DynamicPaoStatisticsHistory CASCADE CONSTRAINTS;
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
