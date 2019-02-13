SET MODE MSSQLServer;

/*==============================================================*/
/* Table: ScheduledDataImportHistory                            */
/*==============================================================*/
create table ScheduledDataImportHistory (
   EntryId              numeric              not null,
   FileName             varchar(100)         not null,
   FileImportType       varchar(50)          not null,
   ImportDate           datetime             not null,
   ArchiveFileName      varchar(100)         not null,
   ArchiveFilePath      varchar(300)         not null,
   ArchiveFileExists    char(1)              not null,
   FailedFileName       varchar(100)         null,
   FailedFilePath       varchar(300)         null,
   SuccessCount         numeric              not null,
   FailureCount         numeric              not null,
   JobGroupId           int                  not null,
   constraint PK_ScheduledDataImportHistory primary key (EntryId)
);