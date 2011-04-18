package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a basic pattern for appending statements, arguments, 
 * and fragments together to form a larger another object that implements 
 * SqlFragmentSource. What makes this class interesting is that it defers
 * evaluating an appended fragment until the next append call is made.
 * 
 * This means you could create something like:
 * 
 * DeferedSqlFragmentHelper sql = new DeferedSqlFragmentHelper();
 * sql.appendStatement("select * from foo where");
 * whereCollection = SqlFragmentCollection.newOrCollection();
 * sql.appendFragment(whereCollection);
 * whereCollection.add(someOtherThing); // this is safe
 * sql.appendStatement("and bar = 5");
 * 
 * This is primarily of use when building a higher level "builder"
 * like the SqlStatementBuilder.
 */
public class DeferedSqlFragmentHelper implements SqlFragmentSource {
    private StringBuilder stringBuilder = new StringBuilder(100);
    private List<Object> arguments = new ArrayList<Object>(8);
    private SqlFragmentSource inProgress = null;
    
    private void collapseTemp() {
        if (inProgress != null) {
            stringBuilder.append(inProgress.getSql());
            arguments.addAll(inProgress.getArgumentList());
            inProgress = null;
        }
    }
    
    public void appendStatement(String statement) {
        collapseTemp();
        stringBuilder.append(statement);
    }
    
    public void appendArgument(Object argument) {
        collapseTemp();
        arguments.add(argument);
    }
    
    public void appendFragment(SqlFragmentSource fragment) {
        collapseTemp();
        inProgress = fragment;
    }
    
    @Override
    public String getSql() {
        collapseTemp();
        return stringBuilder.toString();
    }
    @Override
    public List<Object> getArgumentList() {
        collapseTemp();
        return arguments;
    }
    @Override
    public Object[] getArguments() {
        collapseTemp();
        return arguments.toArray();
    }

}
