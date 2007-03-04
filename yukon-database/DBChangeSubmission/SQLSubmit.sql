/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/********** JULIE: added 2way cbc state information to dynamic cap table ******/
alter table dynamiccccapbank add twowaycbcstate numeric;
go
update dynamiccccapbank set twowaycbcstate = -1;
go
alter table dynamiccccapbank alter column twowaycbcstate numeric not null;
/********** END JULIE ********************************************************/