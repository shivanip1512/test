/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.11', 'Ryan', '12-APR-2002', 'Added the LMDirectOperatorList table');


/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
create table LMDirectOperatorList (
ProgramID            numeric              not null,
OperatorLoginID      numeric              not null,
constraint PK_LMDIRECTOPERATORLIST primary key  (ProgramID, OperatorLoginID)
)
go
alter table LMDirectOperatorList
   add constraint FK_LMDirOpLs_LMPrD foreign key (ProgramID)
      references LMProgramDirect (DeviceID)
go
alter table LMDirectOperatorList
   add constraint FK_OpLg_LMDOpLs foreign key (OperatorLoginID)
      references OperatorLogin (LoginID)
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
