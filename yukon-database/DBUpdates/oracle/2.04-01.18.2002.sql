/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.04', 'Ryan', '01-JAN-2002', 'Added the table CustomerBaseLinePoint and deleted EExchange from the display table.');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/
create table CustomerBaseLinePoint  (
   CustomerID           NUMBER                           not null,
   PointID              NUMBER                           not null,
   constraint PK_CUSTOMERBASELINEPOINT primary key (CustomerID, PointID),
   constraint FK_CstBseLn_CICust foreign key (CustomerID)
         references CICustomerBase (DeviceID),
   constraint FK_CstBseLn_ClcBse foreign key (PointID)
         references CALCBASE (POINTID)
);
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
