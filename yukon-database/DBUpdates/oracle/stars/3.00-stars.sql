/***************************************************/
/**** Oracle 9.2 DBupdates for STARS            ****/
/***************************************************/


alter table InventoryBase modify Notes Varchar(500);
alter table AccountSite modify PropertyNotes Varchar(300);
alter table ApplianceBase modify Notes Varchar(500);
alter table CustomerResidence modify Notes Varchar(300);

alter table ApplianceHeatPump add PumpSizeID number;
alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID);

alter table CustomerAccount drop constraint FK_YkUs_CstAcc;
alter table CustomerAccount drop column LoginID;
