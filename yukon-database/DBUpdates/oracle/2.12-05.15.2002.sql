/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.12', 'Ryan', '15-MAY-2002', 'Added some new UOM, modified DyanmicLMControlArea and added BillingsFormat table');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

alter table UnitMeasure MODIFY LongName VARCHAR2(40);

INSERT INTO UnitMeasure VALUES ( 41,'Volts', 1,'Volts from V2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 42,'Amps', 1,'Amps from A2H','(none)' );


create table BillingFileFormats  (
   FormatID             NUMBER                           not null,
   FormatType           VARCHAR2(30)                     not null
);


alter table DynamicLMControlArea add CurrentDailyStartTime Number;
update DynamicLMControlArea set CurrentDailyStartTime = -1;
alter table DynamicLMControlArea MODIFY CurrentDailyStartTime NOT NULL;

alter table DynamicLMControlArea add CurrentDailyStopTime Number;
update DynamicLMControlArea set CurrentDailyStopTime = -1;
alter table DynamicLMControlArea MODIFY CurrentDailyStopTime NOT NULL;



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
