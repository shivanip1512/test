/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.32', 'Ryan', '16-AUG-2002', 'Added the ImageName to the StateImage table.');



alter table StateImage ADD ImageName VARCHAR(80) not null DEFAULT '(none)';
go

