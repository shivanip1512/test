/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.14', 'Ryan', '20-JUN-2002', 'Added PAOStatistics to YukonPAObject, dropped a PK from DyanmicPAOStatistics and Display2WayData');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

alter table dynamicpaostatistics drop constraint PK_DYNAMICPAOSTATISTICS;
alter table dynamicpaostatistics add constraint PK_DYNAMICPAOSTATISTICS primary key( paobjectid, statistictype);


alter table Display2WayData drop constraint PK_DISPLAY2WAYDATA;
alter table Display2WayData add constraint PK_DISPLAY2WAYDATA primary key( DisplayNum, Ordering );


alter table YukonPAObject add PAOStatistics VARCHAR2(10);
update YukonPAObject set PAOStatistics = '-----';
alter TABLE YukonPAObject MODIFY PAOStatistics NOT NULL;

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
