/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/***********************************************************************************/
/**SN - Changing default companyname from 'cannon' to 'Cannon' 
Update statement needed for update scripts.*/
update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
update mspVendor set AppName = 'Yukon' where vendorid = 1;

/*Modify the mspVendor default insert statement (as below) for creation scripts */
insert into MSPVendor values (1, 'Cannon', '(none)', '(none)', 'meterNumber', 0, 'http://127.0.0.1:8080/soap/', 'Yukon', '(none)', '(none)');
/**********************/