/* Start YUK-4116, YUK-4936, YUK-5001 */
create table LMHardwareControlGroup  (
   ControlEntryID       INTEGER                         not null,
   InventoryID          INTEGER                         not null,
   LMGroupID            INTEGER                         not null,
   AccountID            INTEGER                         not null,
   GroupEnrollStart     DATE,
   GroupEnrollStop      DATE,
   OptOutStart          DATE,
   OptOutStop           DATE,
   Type                 INTEGER                         not null,
   Relay                INTEGER                         not null,
   UserIDFirstAction    INTEGER                         not null,
   UserIDSecondAction   INTEGER                         not null
);

alter table LMHardwareControlGroup
   add constraint PK_LMHARDWARECONTROLGROUP primary key (ControlEntryID);
/* End YUK-4116, YUK-4936, YUK-5001 */

