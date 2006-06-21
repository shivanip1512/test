/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/

insert into YukonRoleProperty values(-20161,-201,'Account Number Length','(none)','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.');
insert into YukonRoleProperty values(-20162,-201,'Rotation Digit Length','(none)','Specifies the number of rotation digit characters to ignore during the customer account import process.');

/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
insert into CTIDatabase values('3.1', 'DBAdmin', '21-JUNE-2006', 'Manual version insert done', 24);
