/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.14', 'Ryan', '20-JUN-2002', 'Added PAOStatistics to YukonPAObject, dropped a PK from DyanmicPAOStatistics and Display2WayData');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

alter table dynamicpaostatistics drop constraint PK_DYNAMICPAOSTATISTICS;
go
alter table dynamicpaostatistics add constraint PK_DYNAMICPAOSTATISTICS primary key( paobjectid, statistictype);
go


alter table Display2WayData drop constraint PK_DISPLAY2WAYDATA;
go
alter table Display2WayData add constraint PK_DISPLAY2WAYDATA primary key( DisplayNum, Ordering );
go


alter TABLE YukonPAObject add PAOStatistics VARCHAR(10) not null DEFAULT '-----';

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
