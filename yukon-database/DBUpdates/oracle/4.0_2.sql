/******************************************/
/**** Oracle DBupdates   		       ****/
/******************************************/

/* Start YUK-6622 */
UPDATE Command
SET command = 'putconfig xcom data ''?''Text Message'''' port ?''Port (0 is default)'' deletable msgpriority 7 timeout 30 hour clear'
WHERE commandId = -134;
/* End YUK-6622 */

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('4.0', 'Matt K', '31-OCT-2008', 'Latest Update', 2);