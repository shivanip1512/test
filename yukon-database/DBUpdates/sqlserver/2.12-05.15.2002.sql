/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.12', 'Ryan', '15-MAY-2002', 'Added some new UOM, modified DyanmicLMControlArea and added BillingsFormat table');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

alter table UnitMeasure ALTER COLUMN LongName VARCHAR(40);

INSERT INTO UnitMeasure VALUES ( 41,'Volts', 1,'Volts from V2H','(none)' );
INSERT INTO UnitMeasure VALUES ( 42,'Amps', 1,'Amps from A2H','(none)' );


create table BillingFileFormats (
FormatID             numeric              not null,
FormatType           varchar(30)          not null
);


alter table DynamicLMControlArea add CurrentDailyStartTime numeric not null DEFAULT -1;
go
alter table DynamicLMControlArea add CurrentDailyStopTime numeric not null DEFAULT -1;
go


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
