/* This sql is for a functional based index for the account number field in 
 * the customer account table.
 * 
 * Functional bases indexes are only possible for an Oracle DBMS(Database Management System).
 * For this reason we must add this index seperately instead of adding this index through
 * the creation scripts, which are created by power designer.
 */

DROP INDEX INDX_CustAcct_AcctNum_FB;
CREATE INDEX INDX_CustAcct_AcctNum_FB ON CustomerAccount(
    UPPER(AccountNumber)
);
