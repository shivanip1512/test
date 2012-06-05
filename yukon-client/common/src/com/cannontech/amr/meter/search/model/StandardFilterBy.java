package com.cannontech.amr.meter.search.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class StandardFilterBy implements FilterBy {

    private String name = null;
    private String filterValue = null;
    private List<MeterSearchField> fieldList = new ArrayList<MeterSearchField>();

    public StandardFilterBy(String name, MeterSearchField... fieldList) {
        this.name = name;
        this.fieldList = Arrays.asList(fieldList);
    }
    
    @Override
    public String getFormatKey() {
    	return "yukon.web.standardFilterBy." + getName().replace(" ", "");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilterValue() {
        return filterValue;
    }
    
    @Override
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }
    
    @Override
    public SqlFragmentSource getSqlWhereClause() {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	
    	if (fieldList.size() == 1) {
    	    sql.append("UPPER("+fieldList.get(0).getSearchQueryField() + ") LIKE UPPER (");
    		sql.appendArgument(filterValue + "%");
    		sql.append(")");
        
    	} else {
        	
    		sql.append("(");
    		
    		boolean first = true;
            for (MeterSearchField field : fieldList) {

                if (!first) {
                    sql.append(" OR ");
                } else {
                    first = false;
                }

                sql.append("UPPER("+field.getSearchQueryField() + ") LIKE UPPER(");
                sql.appendArgument(filterValue + "%");
                sql.append(")");
            }
            
    		sql.append(")");
        }

    	return sql;
    }

    @Override
    public String toSearchString() {

        StringBuffer sb = new StringBuffer();

        if (fieldList.size() == 1) {
            return fieldList.get(0).getMeterSearchString() + " starts with '" + filterValue + "'";
        }

        if (fieldList.size() > 1) {
            sb.append(" (");
        }
        boolean first = true;
        for (MeterSearchField field : fieldList) {

            if (!first) {
                sb.append(" OR ");
            } else {
                first = false;
            }

            sb.append(" " + field.getMeterSearchString() + " starts with '" + filterValue + "' ");
        }
        if (fieldList.size() > 1) {
            sb.append(") ");
        }

        return sb.toString();

    }
}
