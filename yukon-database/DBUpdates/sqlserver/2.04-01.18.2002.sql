/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.04', 'Ryan', '18-JAN-2002', 'Added the table CustomerBaseLinePoint and deleted EExchange from the display table.');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
create table CustomerBaseLinePoint (
CustomerID           numeric              not null,
PointID              numeric              not null,
constraint PK_CUSTOMERBASELINEPOINT primary key  (CustomerID, PointID)
)
go
alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_CICust foreign key (CustomerID)
      references CICustomerBase (DeviceID)
alter table CustomerBaseLinePoint
   add constraint FK_CstBseLn_ClcBse foreign key (PointID)
      references CALCBASE (POINTID)
go
/******************************************************************************/
/* END DATABASEEDITOR UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START TDC UPDATES                                                 */
/******************************************************************************/

delete from display where displaynum = -4;

/******************************************************************************/
/* END TDC UPDATES                                                 */
/******************************************************************************/




/******************************************************************************/
/* START GRAPH UPDATES                                                 */
/******************************************************************************/

/******************************************************************************/
/* END GRAPH UPDATES                                                 */
/******************************************************************************/
