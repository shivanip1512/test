/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.01', 'Ryan', '14-DEC-01', 'Added CommandTimeOut to PointStatus and some other minor changes.');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
/* before running the next 2 update lines, it may be wise to see if this "needs" */
/* to be done in the database.  One way to check is to look at the data in these */
/* 2 columns and see if the values are multiples of 60 or not.  If they are you */
/* more than likely don't have to run them. But...if the values are 5 or 15 for */
/* example, then these two lines need to be run.  Values were originally inserted */
/* as minutes but should have been done in seconds. */

update deviceloadprofile set lastintervaldemandrate=lastintervaldemandrate*60;
update deviceloadprofile set loadprofiledemandrate=loadprofiledemandrate*60

alter table lmcontrolhistory alter column controltype varchar(20);


alter table CICustomerBase add CurtailAmount FLOAT not null DEFAULT 0.0;

update fdrinterface set hasdestination='t' where interfaceid=6;

alter table PointStatus add CommandTimeOut NUMERIC not null DEFAULT -1;

sp_rename 'CICUstomerBase.CustPDL', 'CustFPL', 'COLUMN';
/******************************************************************************/
/* END DATABASEEDITOR UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START TDC UPDATES                                                 */
/******************************************************************************/

/******************************************************************************/
/* END TDC UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START GRAPH UPDATES                                                 */
/******************************************************************************/

/******************************************************************************/
/* END GRAPH UPDATES                                                 */
/******************************************************************************/
