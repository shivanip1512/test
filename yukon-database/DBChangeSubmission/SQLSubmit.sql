/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/
/**  Thain: to go into head(3.5)  ready */

insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 
'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );

/** End Thain   */

insert into YukonRoleProperty values(-10814, -108,'Suppress Error Page Details', 'false', 'Disable stack traces for this user.');
select * from yukonroleproperty where rolepropertyid = -10814;

/** Elliot - added target settings for oneline feeder**/
insert into YukonRoleProperty values(-100105, -1001, 'Target', 'true', 'display target settings');
go
/***************************************/


/** Jason - update devicetypecommand with new 430 names (into head AND 3.4) **/
update devicetypecommand set devicetype='MCT-430A' where devicetype = 'MCT-430EL';
update devicetypecommand set devicetype='MCT-430S4' where devicetype = 'MCT-430LG';
update devicetypecommand set devicetype='MCT-430SN' where devicetype = 'MCT-430IN';
/***************************************/

/*Missing role property, originally added for Xcel.  Should have been in 3.3 scripts*/
insert into YukonRoleProperty values(-21003,-210,'Addtl Order Number Label','Addtl Order Number','Customizable label for the additional order number field.');

/*Missing role property, originally added for Xcel.  Should have been in 3.3_0 update scripts*/
insert into YukonRoleProperty values(-20893,-201,'Inventory Checking Create','true','Allow creation of inventory if not found during Inventory Checking');

/* Added entry for drop down box*/
/* NOTE THIS BELONGS IN HEAD, 3.4 and 3.2!!! */
insert into yukonlistentry values (138, 100, 0, 'Float From 16bit', 0);
