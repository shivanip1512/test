/* This sql is for a functional based index for the location address 1 field in the address table.
 * 
 * Functional bases indexes are only possible for an Oracle DBMS(Database Management System).
 * For this reason we must add this index seperately instead of adding this index through
 * the creation scripts, which are created by power designer.
 */

DROP INDEX INDX_Add_LocAdd_FB;
CREATE INDEX INDX_Add_LocAdd_FB ON Address(
    UPPER(LocationAddress1)
);