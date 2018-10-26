package com.cannontech.database.vendor;

import java.util.Set;

import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;

public interface VendorSpecificSqlBuilder extends SqlFragmentSource {

    /**
     * Returns a special SqlBuilder that can be used to inject vendor-specific
     * SQL into this object. The vendors that this SQL should apply to should
     * be passed in to this method.
     * 
     * This method may be called multiple times, but must be called before
     * buildOther().
     * 
     * The returned SqlBuilder is not useful on its own. If the vendor 
     * does not match the current connected database, this method will
     * return a special builder that doesn't do anything at all.
     * 
     * @param vendors
     * @return an SqlBuilder
     */
    SqlBuilder buildFor(DatabaseVendor... vendors);
    
    SqlBuilder buildFor(Set<DatabaseVendor> vendors);
    

    /**
     * Returns a special SqlBuilder that can be used to inject vendor-specific
     * SQL into this object. The vendors that this SQL should apply to are
     * pre-defined in this method.
     * 
     * This method may be called multiple times, but must be called before
     * buildOther().
     * 
     * The returned SqlBuilder is not useful on its own. If the vendor 
     * does not match the current connected database, this method will
     * return a special builder that doesn't do anything at all.
     * 
     * @return an SqlBuilder
     */
    SqlBuilder buildForAllOracleDatabases();
    
    SqlBuilder buildForAllMsDatabases();
    
    
    /**
     * Returns a special SqlBuilder that can be used to inject SQL into this
     * object. This SQl should be applicable for all vendors that were not
     * mentioned in preceding buildFor() calls.
     * 
     * This method may only be called once and must be called after all
     * buildOther() calls.
     * @return an SqlBuilder
     */
    SqlBuilder buildOther();

}