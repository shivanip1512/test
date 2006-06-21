/******************************************/
/**** SQLServer 2000 DBupdates         ****/
/******************************************/

delete from YukonServices where servicename='Notification_Server';
go

/* @error ignore */
insert into YukonRoleProperty values(-20160,-201,'Create Login With Account','false','Require that a login is created with every new customer account.');
go

/* @error ignore */
insert into YukonRoleProperty values(-20161,-201,'Account Number Length','(none)','Specifies the number of account number characters to consider for comparison purposes during the customer account import process.');
go

/* @error ignore */
insert into YukonRoleProperty values(-20162,-201,'Rotation Digit Length','(none)','Specifies the number of rotation digit characters to ignore during the customer account import process.');
go

Update graphdataseries set type = 65 where type = 64;
go

create table POINTTRIGGER  (
   PointID              numeric                          not null,
   TriggerID            numeric                          not null,
   TriggerDeadband      float                           not null,
   VerificationID       numeric                          not null,
   VerificationDeadband float                           not null,
   CommandTimeout       numeric                          not null,
   Parameters           varchar(40)                    not null
);
go

alter table POINTTRIGGER
   add constraint PK_POINTTRIGGER primary key (PointID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGER_PT foreign key (PointID)
      references POINT (POINTID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERTRIGGERPT_PT foreign key (TriggerID)
      references POINT (POINTID);
go

alter table POINTTRIGGER
   add constraint FK_PTTRIGGERVERIF_PT foreign key (VerificationID)
      references POINT (POINTID);
go

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
