/* Start YUK-5001 */
/* @start-block */
if 1 < (select count(*) from ApplianceCategory)
begin
   insert into YukonGroupRole values (-20, -1, -1, -1020, 'true');
end;
/* @end-block */
go
/* End YUK-5001 */
