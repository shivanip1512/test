/*Please be kind and keep things cleanly organized.  Add new entries to the end and use /**********************/
/*to split your new additions from those already there.  Thanks!*/

/* Sorry about the late change here Jon. I moved all the auto-add stuff up to 100 and added a few more groups */
insert into DeviceGroup values (8,'Flags',1,'Y','STATIC');
insert into DeviceGroup values (9,'Inventory',8,'Y','STATIC');
insert into DeviceGroup values (10,'DisconnectedStatus',8,'Y','STATIC');
insert into DeviceGroup values (11,'UsageMonitoring',8,'Y','STATIC');

update 
	devicemetergroup
set 
	CollectionGroup = replace(replace(replace(CollectionGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','')
	, BillingGroup = replace(replace(replace(BillingGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','')
	, TestCollectionGroup = replace(replace(replace(TestCollectionGroup,'@_INV_',''),'@_UM_',''),'@_DISC_','')


insert into DeviceGroup select distinct 100, CollectionGroup, 3, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 100, TestCollectionGroup, 4, 'N', 'STATIC' from DeviceMeterGroup;
insert into DeviceGroup select distinct 100, BillingGroup, 2, 'N', 'STATIC' from DeviceMeterGroup;
declare @cnt numeric set @cnt = 100 update DeviceGroup set @cnt = DeviceGroupID = @cnt + 1 where DeviceGroupID >= 100;
/* end */