/*Alter the table: LMProgramEnergyExchange */
alter table lmprogramenergyexchange drop column stoppedearlymessage;
alter table lmprogramenergyexchange drop column canceledmessage;
alter table lmprogramenergyexchange add canceledmsg VARCHAR2(80);
alter table lmprogramenergyexchange add stoppedearlymsg VARCHAR2(80);
update lmprogramenergyexchange set stoppedearlymsg = ' ';
update lmprogramenergyexchange set canceledmsg = ' ';
alter table lmprogramenergyexchange modify stoppedearlymsg NOT NULL;
alter table lmprogramenergyexchange modify canceledmsg NOT NULL;


/*Alter the table: FDRInterface */
alter table fdrinterface add hasDestination CHAR(1);
update fdrinterface set hasDestination = 'f';
update fdrinterface set hasDestination = 't' where interfaceid=1;
alter table fdrinterface modify hasDestination NOT NULL;
/* SQLServer */
/* alter table fdrinterface add hasDestination CHAR(1) not null DEFAULT 'f'; */
/* update fdrinterface set hasDestination='t' where interfaceid=1; */


/*==============================================================*/
/* Table : LMGroupRipple                                        */
/*==============================================================*/
create table LMGroupRipple  (
   DeviceID             NUMBER                           not null,
   RouteID              NUMBER                           not null,
   ShedTime             NUMBER                           not null,
   Control              CHAR(50)                         not null,
   Restore              CHAR(50)                         not null,
   constraint PK_LMGROUPRIPPLE primary key (DeviceID),
   constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
         references LMGroup (DeviceID),
   constraint FK_LmGrpRip_Rout foreign key (RouteID)
         references ROUTE (ROUTEID)
);


/*==============================================================*/
/* Table : LMCONTROLAREATRIGGER                                 */
/*==============================================================*/
drop table LMCONTROLAREATRIGGER cascade constraints;
create table LMCONTROLAREATRIGGER  (
   DEVICEID             NUMBER                           not null,
   TRIGGERNUMBER        NUMBER                           not null,
   TRIGGERTYPE          VARCHAR2(20)                     not null,
   POINTID              NUMBER                           not null,
   NORMALSTATE          NUMBER                           not null,
   THRESHOLD            FLOAT                            not null,
   PROJECTIONTYPE       VARCHAR2(14)                     not null,
   PROJECTIONPOINTS     NUMBER                           not null,
   PROJECTAHEADDURATION NUMBER                           not null,
   THRESHOLDKICKPERCENT NUMBER                           not null,
   MINRESTOREOFFSET     FLOAT                            not null,
   PEAKPOINTID          NUMBER                           not null,
   constraint PK_LMCONTROLAREATRIGGER primary key (DEVICEID, TRIGGERNUMBER),
   constraint FK_Point_LMCntlArTrig foreign key (POINTID)
         references POINT (POINTID),
   constraint FK_Point_LMCtrlArTrigPk foreign key (PEAKPOINTID)
         references POINT (POINTID),
   constraint FK_LMCntlArea_LMCntlArTrig foreign key (DEVICEID)
         references LMCONTROLAREA (DEVICEID)
);


/*==============================================================*/
/* Table : DynamicLMControlAreaTrigger                          */
/*==============================================================*/
create table DynamicLMControlAreaTrigger  (
   DeviceID             NUMBER                           not null,
   TriggerNumber        NUMBER                           not null,
   PointValue           FLOAT                            not null,
   LastPointValueTimeStamp DATE                             not null,
   PeakPointValue       FLOAT                            not null,
   LastPeakPointValueTimeStamp DATE                             not null,
   constraint PK_DYNAMICLMCONTROLAREATRIGGER primary key (DeviceID, TriggerNumber),
   constraint FK_LMCntArTr_DyLMCnArTr foreign key (DeviceID, TriggerNumber)
         references LMCONTROLAREATRIGGER (DEVICEID, TRIGGERNUMBER)
);
