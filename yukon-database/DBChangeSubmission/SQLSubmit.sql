/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!
*/

/** elliot: to go into 3.4 and head **/
/** CBC Name property for oneline **/
insert into YukonRoleProperty values(-100202, -1002, 'CBC Name', 'true', 'display CBC Name');
go
/************/

/*** JULIE: to go into 3.4 and head ****/
create unique  index Indx_SchedNm on paoschedule (
schedulename
)
/*** END JULIE: to go into 3.4 and head ****/


/*** Jason: to go into 3.4 and head ****/

update yukonpaobject set type='MCT-430A' where type = 'MCT-430EL'
update yukonpaobject set type='MCT-430S4' where type = 'MCT-430LG'
update yukonpaobject set type='MCT-430SN' where type = 'MCT-430IN'


/***************************************/

/**  Thain: to go into head(3.5)  ready */

insert into FDRInterface values (25, 'TRISTATESUB', 'Receive', 'f' );
insert into FDRInterfaceOption values(25, 'Point', 1, 'Combo', 
'Nucla 115/69 Xfmr.,Happy Canyon 661Idarado,Cascade 115/69 (T2),Ames Generation,Dallas Creek MW,Dallas Creek MV' );

/** End Thain   */
