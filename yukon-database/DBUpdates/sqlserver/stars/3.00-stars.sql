/***************************************************/
/**** SQLServer 2000 DBupdates for STARS        ****/
/***************************************************/


alter table InventoryBase alter column Notes Varchar(500);
go
alter table AccountSite alter column PropertyNotes Varchar(300);
go
alter table ApplianceBase alter column Notes Varchar(500);
go
alter table CustomerResidence alter column Notes Varchar(300);
go

alter table ApplianceHeatPump add PumpSizeID numeric;
go
alter table ApplianceHeatPump
   add constraint FK_AppHtPm_YkLst3 foreign key (PumpSizeID)
      references YukonListEntry (EntryID)
go

alter table CustomerAccount drop constraint FK_YkUs_CstAcc;
go
alter table CustomerAccount drop column LoginID;
go

alter table LMProgramWebPublishing add ProgramOrder numeric;
go
update LMProgramWebPublishing set ProgramOrder = 0;
go

update YukonUserRole set Value=(select GroupID from YukonGroup where GroupName=YukonUserRole.Value or GroupName=YukonUserRole.Value + ' Grp') where RolePropertyID in (-1105,-1106) and Value <> '(none)';
go
