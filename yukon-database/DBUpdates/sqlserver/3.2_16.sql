/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);

/* Start Yuk-5900 */
INSERT INTO YukonRoleProperty VALUES(-40200,-400,'Create Login For Account','false','Allows a new login to be automatically created for each contact on a customer account.');
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
/* __YUKON_VERSION__ */
