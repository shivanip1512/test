/*==============================================================*/
/* Set the null values in CalcComponent table to (none) */
/*==============================================================*/
update calccomponent set functionname = '(none)' where componenttype != 'Function';
update calccomponent set operation = '(none)' where componenttype='Function';

/*==============================================================*/
/*Create the new FDR Tables and add the pre-defined Interfaces */
/*==============================================================*/
create table FDRInterface  (
 InterfaceID          NUMBER         not null,
 InterfaceName        VARCHAR2(30)   not null,
 PossibleDirections   VARCHAR(40)    not null,
 constraint PK_FDRINTERFACE primary key (InterfaceID)
);
insert into FDRInterface values ( 1, 'INET', 'Send,Receive' );
insert into FDRInterface values ( 2, 'ACS', 'Send,Receive' );
insert into FDRInterface values ( 3, 'VALMET', 'Send,Receive' );
insert into FDRInterface values ( 4, 'CYGNET', 'Receive' );

create table FDRInterfaceOption  (
   InterfaceID          NUMBER                           not null,
   OptionLabel          VARCHAR2(20)                     not null,
   Ordering             NUMBER                           not null,
   OptionType           VARCHAR2(8)                      not null,
   OptionValues         VARCHAR2(150)                    not null,
   constraint PK_FDRINTERFACEOPTION primary key (InterfaceID, Ordering),
   constraint FK_FDRINTER_REFERENCE_FDRINTER foreign key (InterfaceID)
         references FDRInterface (InterfaceID)
);
insert into FDRInterfaceOption values(1, 'Device', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Point', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(1, 'Destination/Source', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Category', 1, 'Combo', 'PSEUDO,REAL' );
insert into FDRInterfaceOption values(2, 'Remote', 2, 'Text', '(none)' );
insert into FDRInterfaceOption values(2, 'Point', 3, 'Text', '(none)' );
insert into FDRInterfaceOption values(3, 'Point', 1, 'Text', '(none)' );
insert into FDRInterfaceOption values(4, 'PointID', 1, 'Text', '(none)' );



/*==============================================================*/
/* extend the SystemLog tables USERNAME column's size */
/*==============================================================*/
/* alter table SystemLog alter column UserName VARCHAR(30); */
alter table SystemLog modify UserName VARCHAR2(30);


/*==============================================================*/
/* extend the LMGroupVersacom tables RelayUsage column's size */
/*==============================================================*/
/* alter table LMGroupVersacom alter column RelayUsage CHAR(7); */
alter table LMGroupVersacom modify RelayUsage CHAR(7);

/*==============================================================*/
/* Update all Repeater-900 types and addresses */
/* DONT THINK THIS IS NEEDED!!!!!    */
/*==============================================================*/
/*update device set type='REPEATER 900' where type='Repeater';
update devicecarriersettings set address=address+4190000 where
deviceid in ( select deviceid from device where type='REPEATER 900' );*/


/*==============================================================*/
/* ADDED A NEW COLUMN TO LMProgramDirectGear, WE MUST DROP IT THEN CREATE IT */
/*==============================================================*/
drop table LMProgramDirectGear cascade constraints;
create table LMProgramDirectGear (
   DeviceID             NUMBER                           not null,
   GearName             VARCHAR2(30)                     not null,
   GearNumber           NUMBER                           not null,
   ControlMethod        VARCHAR2(30)                     not null,
   MethodRate           NUMBER                           not null,
   MethodPeriod         NUMBER                           not null,
   MethodRateCount      NUMBER                           not null,
   MethodCommand        VARCHAR2(40)                     not null,
   CycleRefreshRate     NUMBER                           not null,
   MethodStopType       VARCHAR2(20)                     not null,
   ChangeCondition      VARCHAR2(24)                     not null,
   ChangeDuration       NUMBER                           not null,
   ChangePriority       NUMBER                           not null,
   ChangeTriggerNumber  NUMBER                           not null,
   ChangeTriggerOffset  FLOAT                            not null,
   PercentReduction     NUMBER                           not null,
   constraint PK_LMPROGRAMDIRECTGEAR primary key (DeviceID, GearNumber),
   constraint FK_LMProgD_LMProgDGr foreign key (DeviceID)
         references LMProgramDirect (DeviceID)
);