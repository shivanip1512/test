/******************************************************************************/
/* VERSION INFO                                                               */
/******************************************************************************/
insert into CTIDatabase values('2.31', 'Ryan', '9-AUG-2002', 'Added DeviceDNP table.');



create table DeviceDNP (
DeviceID             numeric              not null,
MasterAddress        numeric              not null,
SlaveAddress         numeric              not null,
PostCommWait         numeric              not NULL,
constraint PK_DEVICEDNP primary key  (DeviceID)
)
go
alter table DeviceDNP
   add constraint FK_Dev_DevDNP foreign key (DeviceID)
      references DEVICE (DEVICEID)
go