package com.cannontech.database;

/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 11:50:36 AM)
 * @author: 
 */
public final class SqlUtils {
/**
 * SqlUtils constructor comment.
 */
private SqlUtils() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 11:51:10 AM)
 * @return java.lang.String
 */
public static String createSqlString( String preText, String[] constraintColumnNames, 
	String[] tableNames,String postText)
{
	StringBuffer sql = new StringBuffer(preText);
					 for( int i = 0; i < constraintColumnNames.length; i++ )
					 	sql.append( constraintColumnNames[i] + "," );
					 sql.deleteCharAt( sql.length()-1); //delete the extra , on the end
					 
					 sql.append(" from ");

					 for( int i = 0; i < tableNames.length; i++ )
					 	sql.append( tableNames[i] + "," );
					 sql.deleteCharAt( sql.length()-1); //delete the extra , on the end


					 if( postText != null )
					 	sql.append(postText);

	return sql.toString();
}
}
