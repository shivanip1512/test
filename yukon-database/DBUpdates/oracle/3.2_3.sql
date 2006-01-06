/******************************************/
/**** Oracle 9.2 DBupdates             ****/
/******************************************/


/* @error ignore */
create table CommandGroup  (
   CommandGroupID       NUMBER                          not null,
   CommandGroupName     VARCHAR2(60)                    not null
);

/* @error ignore */
insert into CommandGroup values (-1, 'Default Commands');

/* @error ignore */
alter table CommandGroup
   add constraint PK_COMMANDGROUP primary key (CommandGroupID);

/* @error ignore */
alter table DeviceTypeCommand
   add constraint FK_DevCmd_Grp foreign key (CommandGroupID)
      references CommandGroup (CommandGroupID);


/******************************************************************************/
/* Run the Stars Update if needed here */
/* Note: DBUpdate application will ignore this if STARS is not present */
/* @include StarsUpdate */
/******************************************************************************/


/**************************************************************/
/* VERSION INFO                                               */
/*   Automatically gets inserted from build script            */
/**************************************************************/
/* __YUKON_VERSION__ */