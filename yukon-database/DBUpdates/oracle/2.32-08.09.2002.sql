/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.32', 'Ryan', '16-AUG-2002', 'Added the ImageName to the StateImage table.');



alter table StateImage ADD ImageName VARCHAR2(80);
UPDATE StateImage SET ImageName='(none)';
alter TABLE StateImage MODIFY ImageName NOT NULL;

