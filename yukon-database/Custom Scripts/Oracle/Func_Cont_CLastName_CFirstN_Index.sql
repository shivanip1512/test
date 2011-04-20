/* This sql is for a functional based index for the contact last name and contact first name
 * fields in the contact table.
 * 
 * Functional bases indexes are only possible for an Oracle DBMS(Database Management System).
 * For this reason we must add this index seperately instead of adding this index through
 * the creation scripts, which are created by power designer.
 */

DROP INDEX INDX_Cont_CLastName_CFirstN_FB;
CREATE INDEX INDX_Cont_CLastName_CFirstN_FB ON Contact(
    UPPER(ContLastName),
    UPPER(ContFirstName)
);