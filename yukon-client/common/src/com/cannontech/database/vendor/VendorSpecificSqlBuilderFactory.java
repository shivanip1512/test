package com.cannontech.database.vendor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class VendorSpecificSqlBuilderFactory {
    private DatabaseVendor currentVendor;
    private DatabaseVendorResolver databaseConnectionVendorResolver;

    @PostConstruct
    public void init() throws Exception {
        currentVendor = databaseConnectionVendorResolver.getDatabaseVendor();
    }
    
    public VendorSpecificSqlBuilder create() {
        return new VendorSpecificSqlBuilder() {
            private SqlStatementBuilder usefulSqlSource = null;
            
            private SqlStatementBuilder getUsefulSqlSource() {
                if (usefulSqlSource == null) {
                    throw new IllegalStateException("SQL for a valid DB was not provided");
                }
                
                return usefulSqlSource;
            }
            
            @Override
            public List<Object> getArgumentList() {
                return getUsefulSqlSource().getArgumentList();
            }
            
            @Override
            public Object[] getArguments() {
                return getUsefulSqlSource().getArguments();
            }
            
            @Override
            public String getSql() {
                return getUsefulSqlSource().getSql();
            }
            
            /* (non-Javadoc)
             * @see com.cannontech.database.vendor.VendorSpecificSqlBuilder#buildFor(com.cannontech.database.vendor.DatabaseVendor)
             */
            @Override
            public SqlBuilder buildFor(DatabaseVendor ... vendors) {
                boolean isUsefulForMyDb = Arrays.asList(vendors).contains(currentVendor);
                
                if (isUsefulForMyDb) {
                    if (usefulSqlSource != null) {
                        throw new IllegalStateException("Two buildFor clauses matched the current database");
                    }
                    usefulSqlSource = new SqlStatementBuilder();
                    return usefulSqlSource;
                } else {
                    return new NullSqlBuilder();
                }
            }
            
            /* (non-Javadoc)
             * @see com.cannontech.database.vendor.VendorSpecificSqlBuilder#buildOther()
             */
            @Override
            public SqlBuilder buildOther() {
                if (usefulSqlSource != null) {
                    // we already found our match
                    return new NullSqlBuilder();
                } else {
                    usefulSqlSource = new SqlStatementBuilder();
                    return usefulSqlSource;
                }
            }

        };
    }
    
    
    private static class NullSqlBuilder implements SqlBuilder {

        @Override
        public SqlBuilder append(Object... args) {
            return this;
        }

        @Override
        public SqlBuilder appendArgument(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder appendArgumentList(Iterable<?> list) {
            return this;
        }

        @Override
        public SqlBuilder appendFragment(SqlFragmentSource fragment) {
            return this;
        }

        @Override
        public SqlBuilder appendList(Object[] array) {
            return this;
        }

        @Override
        public SqlBuilder appendList(Collection<?> array) {
            return this;
        }

        @Override
        public SqlBuilder eq(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder neq(Object argument) {
            return this;
        }
        
        @Override
        public SqlBuilder gt(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder gte(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder in(Iterable<?> list) {
            return this;
        }
        
        @Override
        public SqlBuilder notIn(Iterable<?> list) {
            return null;
        }
        
        @Override
        public SqlBuilder notIn(SqlFragmentSource sqlFragmentSource) {
            return null;
        }

        @Override
        public SqlBuilder lt(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder lte(Object argument) {
            return this;
        }

        @Override
        public SqlBuilder contains(String argument) {
            return this;
        }

        @Override
        public SqlBuilder endsWith(String argument) {
            return this;
        }

        @Override
        public SqlBuilder in(SqlFragmentSource sqlFragmentSource) {
            return this;
        }

        @Override
        public SqlBuilder startsWith(String argument) {
            return this;
        }
        
    }
    
    @Autowired
    public void setDatabaseConnectionVendorResolver(
            DatabaseVendorResolver databaseConnectionVendorResolver) {
        this.databaseConnectionVendorResolver = databaseConnectionVendorResolver;
    }
    
}
