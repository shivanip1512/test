/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

/* Start Yuk-5900 */
/* @error ignore-begin */
INSERT INTO YukonRoleProperty VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.');
/* @error ignore-end */
/* End Yuk-5900 */

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/

insert into CTIDatabase values('3.5', 'Matt K', '16-May-2008', 'Latest Update', 5 );