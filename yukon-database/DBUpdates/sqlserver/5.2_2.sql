/******************************************/ 
/**** SQL Server DBupdates             ****/ 
/******************************************/ 

/* Start YUK-9057*/
ALTER TABLE SurveyResult
ALTER COLUMN AccountId NUMERIC NULL;
/* End YUK-9057*/ 

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
