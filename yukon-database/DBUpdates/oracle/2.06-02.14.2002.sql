/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.06', 'Ryan', '14-FEB-2002', 'Removed a column from the LMProgramDirect table and put it in the LMProgramDirectGear table');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
alter table LMProgramDirect drop column GroupSelectionMethod;
alter table LMProgramDirectGear add GroupSelectionMethod Varchar2(30);
update LMProgramDirectGear set GroupSelectionMethod = 'LastControlled';
alter table LMProgramDirectGear MODIFY GroupSelectionMethod NOT NULL;


update yukonpaobject set category = 'LOADMANAGEMENT' where paoclass='LOADMANAGEMENT';
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
