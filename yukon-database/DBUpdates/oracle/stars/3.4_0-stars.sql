/* Start YUK-5001 */
/* @start-block */
declare v_count number;
begin
select count(*) into v_count from ApplianceCategory;
if v_count > 1 then
   insert into YukonGroupRole values (-20, -1, -1, -1020, 'true');
end if;
end;
/* @end-block */
/* End YUK-5001 */
