/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.01', 'Ryan', '14-DEC-01', 'Added CommandTimeOut to PointStatus and some other minor changes.');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
alter table lmcontrolhistory modify controltype varchar2(20);


/* before running the next 2 update lines, it may be wise to see if this "needs" */
/* to be done in the database.  One way to check is to look at the data in these */
/* 2 columns and see if the values are multiples of 60 or not.  If they are you */
/* more than likely don't have to run them. But...if the values are 5 or 15 for */
/* example, then these two lines need to be run.  Values were originally inserted */
/* as minutes but should have been done in seconds. */

update deviceloadprofile set lastintervaldemandrate=lastintervaldemandrate*60
update deviceloadprofile set loadprofiledemandrate=loadprofiledemandrate*60


alter table CICustomerBase add CurtailAmount FLOAT;
update CICustomerBase set CurtailAmount = 0.0;
alter table CICustomerBase MODIFY CurtailAmount NOT NULL;

update fdrinterface set hasdestination='t' where interfaceid=6;


/******************************************************************************/
/* Be sure the RenCol procedure is created!!                                  */
/* The rencol() procedure can only be created from                            */
/* the sys or system account. The rencol() procedure                          */
/* is found in the RenameColProcedure.sql file.                               */
/******************************************************************************/
exec rencol('<UserName>', 'CICUstomerBase', 'CustPDL', 'CustFPL');


alter table PointStatus add CommandTimeOut NUMBER;
update PointStatus set CommandTimeOut = -1;
alter table PointStatus MODIFY CommandTimeOut NOT NULL;
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
