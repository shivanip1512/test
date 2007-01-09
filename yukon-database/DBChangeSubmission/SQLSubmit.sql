/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/***********************************************************************************/
/**SN - Changing default companyname from 'cannon' to 'Cannon' 
Update statement needed for update scripts.
Modify the mspVendor default insert statements for creation scripts */
update mspVendor set CompanyName = 'Cannon' where vendorid = 1;
/**********************/