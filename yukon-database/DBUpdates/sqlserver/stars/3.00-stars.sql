/***************************************************/
/**** SQLServer 2000 DBupdates for STARS        ****/
/***************************************************/


alter table InventoryBase alter column Notes Varchar(500);
alter table AccountSite alter column PropertyNotes Varchar(300);
alter table ApplianceBase alter column Notes Varchar(500);
alter table CustomerResidence alter column Notes Varchar(300);

alter table ApplianceHeatPump add PumpSizeID numeric;
alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID)
go

alter table CustomerAccount drop constraint FK_YkUs_CstAcc;
alter table CustomerAccount drop column LoginID;
