/******************************************/ 
/****     Oracle DBupdates             ****/ 
/******************************************/ 

/* Start YUK-10669 */
UPDATE YukonListEntry
SET EntryOrder = (SELECT SubQuery.Sort_Order
FROM (
    SELECT EntryId, Row_Number() OVER (ORDER BY EntryText) as SORT_ORDER
    FROM YukonListEntry
    WHERE ListID = 1
) SubQuery
WHERE SubQuery.EntryID = YukonListEntry.EntryID)
WHERE ListID = 1;
/* End YUK-10669 */

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 