/* This sql is for a functional based index for the alt tracking number field in the customer table.
 * 
 * Functional bases indexes are only possible for an Oracle DBMS(Database Management System).
 * For this reason we must add this index seperately instead of adding this index through
 * the creation scripts, which are created by power designer.
 */

DROP INDEX INDX_Cust_AltTrackNum_FB;
CREATE INDEX INDX_Cust_AltTrackNum_FB ON Customer(
    UPPER(AltTrackNum)
);