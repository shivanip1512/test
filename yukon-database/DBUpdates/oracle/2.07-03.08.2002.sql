/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.07', 'Stacey', '15-MAY-2002', 'Added 2 new FDR Interfaces and remove LMPrograms and LMControlAreas from the device table');




/******************************************************************************/
/* START DATABASEEDITOR UPDATES                                               */
/******************************************************************************/

insert into FDRInterface values ( 8, 'RDEX', 'Send,Send for control,Receive,Receive for control', 't' );
insert into FDRInterfaceOption values(8, 'Translation', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(8, 'Destination/Source', 2, 'Text', '(none)' );

insert into FDRInterface values (9,'SYSTEM','Link Status','f');
insert into FDRInterfaceOption values(9,'Client',1,'Text','(none)');


alter table LMControlArea drop constraint FK_Device_LMCctrlArea;
alter table LMProgram drop constraint FK_Device_LMProgram;
alter table LMProgramEnergyExchange drop constraint FK_PrgEnExc_DevID;

delete from device where deviceid in ( select deviceid from lmprogram);
delete from device where deviceid in ( select deviceid from lmcontrolarea);

alter table LMProgramEnergyExchange ADD CONSTRAINT FK_LmPrg_LmPrEEx
   foreign key (DEVICEID) references LMPROGRAM (DEVICEID);
alter table LMControlArea ADD CONSTRAINT FK_LmCntAr_YukPAO
   foreign key (DEVICEID) references YukonPAObject (PAObjectID);
alter table LMProgram ADD CONSTRAINT FK_LmProg_YukPAO
   foreign key (DEVICEID) references YukonPAObject (PAObjectID);


update yukonpaobject set paoclass = 'LOADMANAGEMENT' where paoclass = 'LOAD MANAGE';
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
