/***************************************************/
/**** SQLServer 2000 DBupdates for STARS        ****/
/***************************************************/


alter table InventoryBase alter column Notes Varchar(500);
alter table AccountSite alter column PropertyNotes Varchar(300);
alter table ApplianceBase alter column Notes Varchar(300);
