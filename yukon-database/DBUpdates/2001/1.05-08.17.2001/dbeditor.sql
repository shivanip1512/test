alter table display modify type VARCHAR2(40);

update display set name = 'Load Management Client', type = 'Load Management Client', title = 'Load Management' where displaynum=-3;
insert into display values(-4, 'Energy Exchange Client', 'Energy Exchange Client', 'Energy Exchange', 'com.cannontech.eexchange.gui.EnergyExchangeMainPanel');

/* Change some column names in the LMGroupRipple */
drop table LMGroupRipple cascade constraints;
create table LMGroupRipple  (
   DeviceID             NUMBER                           not null,
   RouteID              NUMBER                           not null,
   ShedTime             NUMBER                           not null,
   ControlValue         CHAR(50)                         not null,
   RestoreValue         CHAR(50)                         not null,
   constraint PK_LMGROUPRIPPLE primary key (DeviceID),
   constraint FK_LmGr_LmGrpRip foreign key (DeviceID)
         references LMGroup (DeviceID),
   constraint FK_LmGrpRip_Rout foreign key (RouteID)
         references ROUTE (ROUTEID) );


/* Added a new table */
create table GenericMacro  (
   OwnerID              NUMBER                           not null,
   ChildID              NUMBER                           not null,
   ChildOrder           NUMBER                           not null,
   MacroType            VARCHAR2(20)                     not null,
   constraint PK_GENERICMACRO primary key (OwnerID, ChildOrder, MacroType) );