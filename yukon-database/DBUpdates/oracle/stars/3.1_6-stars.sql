/* These two statements may fall if data exists, fix by copying data into a temp table and setting the origianl data to null, then copying back data into new column */
alter table ApplianceBase modify KWCapacity float not null; 
alter table ApplianceBase modify EfficiencyRating float not null;


