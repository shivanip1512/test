/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.08', 'Ryan', '14-MAR-2002', 'Added a LMGroupPoint, remove RecordControlHistory flag from LMGroup and added the DSM2IMPORT interface');



/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

create table LMGroupPoint  (
   DEVICEID             NUMBER                           not null,
   DeviceIDUsage        NUMBER                           not null,
   PointIDUsage         NUMBER                           not null,
   StartControlRawState NUMBER                           not null,
   constraint PK_LMGROUPPOINT primary key (DEVICEID),
   constraint FK_LMGrpPt_LMGrp foreign key (DEVICEID)
         references LMGroup (DeviceID),
   constraint FK_LMGrpPt_Dev foreign key (DeviceIDUsage)
         references DEVICE (DEVICEID),
   constraint FK_LMGrpPt_Pt foreign key (PointIDUsage)
         references POINT (POINTID)
);


alter table LMGroup drop column RecordControlHistoryFlag;
alter table LMProgramDirectGear drop column MethodCommand;

insert into fdrinterface values (10,'DSM2IMPORT','Receive,Receive for control','f');
insert into fdrinterfaceoption values(10,'Point',1,'Text','(none)');



create or replace view LMGroupMacroExpander_View as
select distinct PAObjectID, Category, PAOClass, PAOName, Type, Description, DisableFlag, 
ALARMINHIBIT, CONTROLINHIBIT, KWCapacity, dg.DeviceID, 
LMGroupDeviceID, GroupOrder, OwnerID, ChildID, ChildOrder
from YukonPAObject y, DEVICE d, LMGroup g,
LMProgramDirectGroup dg, GenericMacro m
where y.PAObjectID = d.DEVICEID 
and d.DEVICEID = g.DeviceID
and dg.lmgroupdeviceid = m.ownerid (+);

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
