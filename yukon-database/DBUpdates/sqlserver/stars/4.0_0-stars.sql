/* Start YUK-4116, YUK-4936, YUK-5001 */
create table LMHardwareControlGroup (
   ControlEntryID       int                  not null,
   InventoryID          int                  not null,
   LMGroupID            int                  not null,
   AccountID            int                  not null,
   GroupEnrollStart     datetime             null,
   GroupEnrollStop      datetime             null,
   OptOutStart          datetime             null,
   OptOutStop           datetime             null,
   Type                 int                  not null,
   Relay                int                  not null,
   UserIDFirstAction    int                  not null,
   UserIDSecondAction   int                  not null
);
go

alter table LMHardwareControlGroup
   add constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID);
go
/* End YUK-4116, YUK-4936, YUK-5001 */

