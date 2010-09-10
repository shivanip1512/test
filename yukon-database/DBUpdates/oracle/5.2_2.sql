/******************************************/ 
/**** Oracle DBupdates                 ****/ 
/******************************************/ 

/* Start YUK-9057*/
ALTER TABLE SurveyResult
MODIFY AccountId NUMBER NULL;
/* End YUK-9057*/

/**************************************************************/ 
/* VERSION INFO                                               */ 
/*   Automatically gets inserted from build script            */ 
/**************************************************************/ 
