/*-----------------------------------------------------------------------------
    Filename:
         dbaccess.cpp

    Programmer:
         Aaron Lauinger

    Description:
         Provides access to rw database and connection objects.

    Initial Date:  1/11/99
    07/07/1999    CGP   Carved this module down to a more CGP managable unit.

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <set>
#include "ctidbgmem.h"      // defines CTIDBG_new for memory tracking!
#include "types.h"
#include "dlldefs.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "logger.h"
#include "cparms.h"

using namespace std;

DLLEXPORT CtiSemaphore  gDBAccessSema(gMaxDBConnectionCount, gMaxDBConnectionCount);

typedef set< long > ignoreCol;
static ignoreCol dbIgnores;     // Vendor Error numbers that should be ignored for Oracle, or SQLServer.

// Bookkeeping information about a database connection
struct DBInfo
{
    string dll;
    string name;
    string user;
    string password;
    RWDBDatabase*  db;
    bool reconnect;
    void (*error_handler) (const RWDBStatus&);
};

// Each RWDBDatabase requires its own error handler
void dbErrorHandler(const RWDBStatus& aStatus, DBInfo* dbInfo);
void dbErrorHandler1( const RWDBStatus& aStatus);
void dbErrorHandler2( const RWDBStatus& aStatus);

// This lock serializes access to the database,connection
static RWRecursiveLock<RWMutexLock> DbMutex;

// Keep information about a max of 2 databases
static const int MAXDBINFO = 2;
static DBInfo* db_info[MAXDBINFO] = { NULL, NULL};


DLLEXPORT void testAnarchyOnDB()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    for(int i = 0; i < MAXDBINFO; i++)
    {
        if(db_info[i] != NULL)
        {
            if(db_info[i]->db != NULL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ANARCHY **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                delete db_info[i]->db;
                db_info[i]->db = (RWDBDatabase*)1;                 // Thats a nice pointer!
                db_info[i]->reconnect = true;
            }
        }
    }
}

DLLEXPORT void cleanupDB()
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    for(int i = 0; i < MAXDBINFO; i++)
    {
        if(db_info[i] != NULL)
        {
            // delete db_info[i]->db;
            // db_info[i]->db = 0;
            delete db_info[i];
            db_info[i] = NULL;
        }
    }
}

/**
 For a given dbID, set the necessary information to make a connection
 to a database.  After a call to setDatabaseParams with a given dbID
 a call to getDatabase or getConnection will cause a connection to be made.
**/
DLLEXPORT
void setDatabaseParams(unsigned dbID,
                       const string& dbDll, const string& dbName,
                       const string& dbUser, const string& dbPassword )
{
    assert( dbID == 0 || dbID == 1 );

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    DBInfo* info;
    if( (info = db_info[dbID]) == NULL )
    {
        db_info[dbID] = info = CTIDBG_new DBInfo;
        info->db = NULL;
    }

    info->dll = dbDll;
    info->name = dbName;
    info->user = dbUser;
    info->password = dbPassword;

    if(info->db != NULL)
    {
        RWDBConnection conn = info->db->connection();
        conn.close();

        delete info->db;
    }

    info->db = NULL;
    info->reconnect = true;

    switch(dbID)
    {
    case 0:
        info->error_handler = dbErrorHandler1;
        break;
    case 1:
        info->error_handler = dbErrorHandler2;
        break;
    default: //oh crap
        assert( 0 );
    }
}

DLLEXPORT
RWDBDatabase getDatabase()
{
    return getDatabase(0);
}

/**
 Returns a RWDBDatabase for the given dbID
 setDatabaseParams must have been called before the first call
 to getDatabase for a given dbID.
**/
DLLEXPORT
RWDBDatabase getDatabase(unsigned dbID)
{
    assert( dbID == 0 || dbID == 1 );

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);

    DBInfo* info = db_info[dbID];

    assert( info != NULL );

    RWDBDatabase*& db = info->db;
    bool& reconnect = info->reconnect;

    if( reconnect == true )
    {
        if(db != NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Invalidating all pending connections to " << info->name << " as " << info->user << " with " << info->dll << endl;
            }
            try
            {
                RWDBConnection conn = db->connection();
                conn.close();
                delete db;
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Invalidating database.  Nonfatal. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        db = NULL;
    }

    if(db == NULL)
    {
        db = CTIDBG_new RWDBDatabase;
    }

    if( reconnect == true )
    {
        try
        {
            RWDBManager::setErrorHandler(info->error_handler);
            *db = RWDBManager::database( info->dll.c_str(),info->name.c_str(), info->user.c_str(), info->password.c_str(), "" );

            if( db->isValid() )
            {
                db->defaultConnections( gMaxDBConnectionCount );
                reconnect = false;

                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() <<    " Connected to "    << info->name <<
                " as "              << info->user <<
                " with "            << info->dll << endl;
            }
            else
            {
                Sleep(10000);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Failed to connect to database" << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Creating new database connection.  Nonfatal if non-repetitive. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return *db;
}

DLLEXPORT
RWDBConnection getConnection()
{
    return getConnection(0);
}

DLLEXPORT
RWDBConnection getConnection(unsigned dbID)
{
    RWDBConnection conn;

    RWRecursiveLock<RWMutexLock>::LockGuard guard( DbMutex);
    conn = getDatabase(dbID).connection();
    return conn;
}

DLLEXPORT
RWDBReader ExecuteQuery(RWDBConnection& conn, const string& query)
{
    RWDBResult results = conn.executeSql(query.c_str());

    if(!results.isValid())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** RWDB Error #" << results.status().errorCode() << " in query:" << endl;
            dout << query << endl << endl;
        }
    }

    RWDBTable resTable = results.table();

    return resTable.reader();
}

/**
 Database error handler.
 Sets the reconnect flag for the database that caused the error.
 Reconnection will occur on the next call to getDatabase(..)
**/
void
dbErrorHandler (const RWDBStatus& aStatus, DBInfo* dbInfo)
{
    ignoreCol::iterator itr;

    if( dbIgnores.end() != (itr = dbIgnores.find(aStatus.vendorError1())) )
    {
        // These errors have been specified as ignores.
        return;
    }

    switch(aStatus.vendorError1())
    {
    case 547:           // This is a foreign key violation for sqlserver.
    case 2627:          // This is a primary key violation for sqlserver.
    case 5701:          // This is a changed database context for sqlserver.
        return;
        break;

    default:
        {
            CtiTime Now;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            // Print out the error.
            dout << Now << " Thread id:        " << rwThreadId() << endl
            << Now << " Error code:       " << (int) aStatus.errorCode() << endl
            << Now << " Error message     " << aStatus.message() << endl
            << Now << " Is terminal:      " << (aStatus.isTerminal() ? "Yes" : "No") << endl
            << Now << " Vendor error 1:   " << aStatus.vendorError1() << endl
            << Now << " Vendor error 2:   " << aStatus.vendorError2() << endl
            << Now << " Vendor message 1: " << aStatus.vendorMessage1() << endl
            << Now << " Vendor message 2: " << aStatus.vendorMessage2() << endl << endl;
        }
    }

    // Force a reconnect
    dbInfo->reconnect = true;
}

void
dbErrorHandler1 (const RWDBStatus& aStatus)
{
    dbErrorHandler(aStatus, db_info[0]);
}

void
dbErrorHandler2 (const RWDBStatus& aStatus)
{
    dbErrorHandler(aStatus, db_info[1]);
}

/*
 *  rowsAffected can be used to detect an Update of a non-existent row... It will be zero if no update occurred.
 */
DLLEXPORT
RWDBStatus::ErrorCode ExecuteUpdater(RWDBConnection& conn, RWDBUpdater &updater, const char *file, int line, long *rowsAffected)
{
    RWDBResult myResult = updater.execute( conn );
    RWDBStatus stat = myResult.status();
    RWDBStatus::ErrorCode ec = stat.errorCode();

    if(rowsAffected)                                        // Ha.  The calls to table and rowCount ALTER stat!!!!
    {                                                       // This may make the usage of the return VERY suspect!
        RWDBTable myTable = myResult.table();
        *rowsAffected = myResult.rowCount();
    }

    if(isDebugLudicrous())
    {
        string loggedSQLstring = updater.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << CtiTime() << " **** Checkpoint **** ";
            if(file != 0) dout << file << " (" << line << ")";
            dout << endl << loggedSQLstring << endl;
        }
    }

    if( ec != RWDBStatus::ok )
    {
        string loggedSQLstring = updater.asString();
        {
            CtiTime Now;
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " Error Code = " << stat.errorCode() << ". ";
            if(file != 0) dout << file << " (" << line << ")";
            dout << endl << endl << loggedSQLstring << endl << endl;


            if(stat.vendorError1() != 0)
            {
                dout << Now << " Thread id:        " << rwThreadId() << endl
                << Now << " Error code:       " << (int) stat.errorCode() << endl
                << Now << " Error message     " << stat.message() << endl
                << Now << " Is terminal:      " << (stat.isTerminal() ? "Yes" : "No") << endl
                << Now << " Vendor error 1:   " << stat.vendorError1() << endl
                << Now << " Vendor error 2:   " << stat.vendorError2() << endl
                << Now << " Vendor message 1: " << stat.vendorMessage1() << endl
                << Now << " Vendor message 2: " << stat.vendorMessage2() << endl << endl;
                if(rowsAffected) dout << Now << " Rows Updated      " << *rowsAffected << endl;
            }
        }
    }


    return ec;
}

DLLEXPORT
RWDBStatus ExecuteInserter(RWDBConnection& conn, RWDBInserter &inserter, const char *file, int line)
{
    RWDBStatus stat = inserter.execute( conn ).status();

    if(isDebugLudicrous())
    {
        string loggedSQLstring = inserter.asString();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << CtiTime() << " **** Checkpoint **** ";
            if(file != 0) dout << file << " (" << line << ")";
            dout << endl << loggedSQLstring << endl;
        }
    }

    if( stat.errorCode() != RWDBStatus::ok )
    {
        string loggedSQLstring = inserter.asString();
        {
            CtiTime Now;
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << Now << " Error Code = " << stat.errorCode() << ". ";

            if(file != 0) dout << file << " (" << line << ")";
            dout << endl << endl << loggedSQLstring << endl << endl;

            if(stat.vendorError1() != 0)
            {
                dout << Now << " Thread id:        " << rwThreadId() << endl
                << Now << " Error code:       " << (int) stat.errorCode() << endl
                << Now << " Error message     " << stat.message() << endl
                << Now << " Is terminal:      " << (stat.isTerminal() ? "Yes" : "No") << endl
                << Now << " Vendor error 1:   " << stat.vendorError1() << endl
                << Now << " Vendor error 2:   " << stat.vendorError2() << endl
                << Now << " Vendor message 1: " << stat.vendorMessage1() << endl
                << Now << " Vendor message 2: " << stat.vendorMessage2() << endl << endl;
            }
        }
    }

    return stat;
}


DLLEXPORT int addDBIgnore(long ignoreError)
{
    dbIgnores.insert(ignoreError);
    return (int)(dbIgnores.size());
}
DLLEXPORT void resetDBIgnore()
{
    dbIgnores.clear();
    return;
}


/*
    This function searches the input string for the pre-SQL92 style left outer join operator.
    It comes in two flavors:

        1. Microsoft SQL:   a.x *= b.x
        2. Oracle:          a.x = b.x (+)

    And replaces them with proper SQL92 standard syntax.

        1 & 2.              A a LEFT OUTER JOIN B b ON a.x = b.x

    REQUIRES:

        1. SQL keywords are ALL CAPS ( SELECT, FROM, ... )      (RogueWave does this)
        2. all tables have aliases in the FROM clause           (RogueWave does this)
        3. if there are multiple left outer joins, the table
            on the left must be the same for all of them.

                eg:  a.x *= b.x AND a.x *= c.x

        4. if there are multiple joins on the same pair of
           tables, the WHERE conditions for the same tables
           must be sequentially grouped.

                eg:  a.x = b.x (+) AND a.y = b.y (+) AND a.x = c.x (+)      (ab conditions together -> then ac)
                NOT: a.x = b.x (+) AND a.x = c.x (+) AND a.y = b.y (+)      (ab then ac then ab again)
*/
DLLEXPORT
string makeLeftOuterJoinSQL92Compliant(const string &sql)
{
    bool isOracleSyntax = false;        // defaults to Microsoft SQL syntax

    // Look for Pre-SQL92 syntax.  If none is found we return our original string.
    if (sql.find("*=") == string::npos)
    {
        if (sql.find("(+)") == string::npos)
        {
            return sql;
        }
        else
        {
            isOracleSyntax = true;
        }
    }

    typedef map<string, string>     TableMapping;
    typedef vector<string>          WhereSequence;

    string::size_type   index;
    string              select_part;
    string              rest;

    // Since the SELECT part has no significance on the join syntax, we split the SQL
    // statement just after the FROM keyword.  Squirrel away the SELECT .... FROM part
    // for later use.
    if ((index = sql.find(" FROM ")) != string::npos)
    {
        select_part = sql.substr(0, index + 5);     // "SELECT ..... FROM"
        rest = sql.substr(index + 5);               // the rest of the statement
    }

    TableMapping    tables;

    // Isolate the list of table names and aliases by splitting the SQL statement
    // right before the WHERE token.
    if ((index = rest.find(" WHERE ")) != string::npos)
    {
        // tables_part is a list of tables and their aliases seperated by commas like so:
        //      "A a, B b, C c, D d"
        string tables_part = rest.substr(0, index + 1);
        rest = rest.substr(index + 1);                      // "WHERE ....."

        // Replace the commas with spaces to ensure proper alias name parsing for the mapping
        replace(tables_part.begin(), tables_part.end(), ',', ' ');

        // Tokenize the table list and insert into the mapping.
        string          table_name, table_alias;
        istringstream   table_stream(tables_part);

        while (table_stream >> table_name >> table_alias)
        {
            // tables[table_alias] = table_name
            tables.insert(make_pair(table_alias, table_name));
        }
    }

    string          where_part;

    // See if rest of statement contains a ORDER BY or GROUP BY part.  If so,
    // split it at that point.
    if ((index = rest.find(" BY ")) != string::npos)
    {
        where_part = rest.substr(0, index - 5);         // "WHERE ....." up to a ORDER/GROUP BY
        rest = rest.substr(index - 5);                  // "ORDER BY ....."  or "GROUP BY ...."
    }
    else
    {
        where_part = rest;
        rest = rest.erase();
    }

    // Tokenize the WHERE part...
    WhereSequence   where_tokens;
    string          token;
    istringstream   where_stream(where_part);

    while (where_stream >> token)
    {
        where_tokens.push_back(token);
    }

    // Finally ready to process the SQL...

    string  join;

    for (int loj_count = count(where_tokens.begin(), where_tokens.end(), isOracleSyntax ? "(+)" : "*=");
         loj_count > 0;
         --loj_count)
    {
        string lhs_alias, rhs_alias, on;

        // Locate the first occurrence of the old style join syntax.
        WhereSequence::iterator loj = find(where_tokens.begin(),
                                           where_tokens.end(),
                                           isOracleSyntax ? "(+)" : "*=");

        if (isOracleSyntax)
        {
            // where_tokens: ...... 'a.x'  '='  'b.x'  '(+)'  .......
            lhs_alias = *(loj - 3);     // a.x
            rhs_alias = *(loj - 1);     // b.x

            // Erase ON condition from sequence -- if we are at the end of the WHERE sequence
            // remove the ON condition and the previous keyword (AND or something or WHERE if
            // it is the only condition) else remove the ON condition and the logical condition AFTER it.
            loj = (loj + 1 == where_tokens.end()) ? loj - 4 : loj - 3;
            where_tokens.erase(loj, loj + 5);
        }
        else    // is Microsoft SQL syntax...
        {
            // where_tokens: ...... 'a.x'  '*='  'b.x'  .......
            lhs_alias = *(loj - 1);     // a.x
            rhs_alias = *(loj + 1);     // b.x

            // see comment in if() above...
            loj = (loj + 2 == where_tokens.end()) ? loj - 2 : loj - 1;
            where_tokens.erase(loj, loj + 4);
        }

        // assemble ON condition
        on = lhs_alias + " = " + rhs_alias;     // a.x = b.x

        // get aliases
        lhs_alias = lhs_alias.substr(0, lhs_alias.find('.'));       // a
        rhs_alias = rhs_alias.substr(0, rhs_alias.find('.'));       // b

        // Look up tables in the table mapping.
        TableMapping::iterator  lhs_table = tables.find(lhs_alias);
        TableMapping::iterator  rhs_table = tables.find(rhs_alias);

        // If the rhs_table iterator is end(), that means that we have consecutive
        // joins on the same two tables.  We need to form a compound ON condition with the
        // previous one.  Just append to the previous pass, with logic AND.  (Requirement #4 above)
        // Erase tables from the mapping as they are referenced.
        if (rhs_table == tables.end())
        {
            join += " AND " + on;
        }
        else
        {
            // build proper SQL92 left outer join statement
            if (lhs_table != tables.end())
            {
                join += lhs_table->second + " " + lhs_table->first;
                tables.erase(lhs_table->first);
            }
            join += " LEFT OUTER JOIN " + rhs_table->second + " " + rhs_table->first + " ON " + on;
            tables.erase(rhs_table->first);
        }
    }

    // Prepend the normally joined tables (whatever is left in the mapping)
    for (TableMapping::iterator tbl_iter = tables.begin();
         tbl_iter != tables.end();
         ++tbl_iter)
    {
        join = tbl_iter->second + " " + tbl_iter->first + ", " + join;
    }

    // Append the remaining WHERE conditions.
    for (WhereSequence::iterator where_iter = where_tokens.begin();
         where_iter != where_tokens.end();
         ++where_iter)
    {
        join += " " + *where_iter;
    }

    // Build our result.
    select_part += " " + join;
    if ( ! rest.empty())
    {
        select_part += " " + rest;
    }

    if(isDebugLudicrous())
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "\nTranslating SQL from:\n"
             << sql
             << "\nto:\n"
             << select_part
             << endl << endl;
    }

    // Return the complete SQL statement.
    return (select_part);
}


DLLEXPORT
void addIDClause(RWDBSelector &selector, RWDBColumn &id_column, const set<long> &id_range)
{
    if( id_range.empty() )
    {
        return;
    }

    if( id_range.size() == 1 )
    {
        //  special single id case

        selector.where(selector.where() && id_column == *(id_range.begin()));

        return;
    }

    ostringstream in_list;

    in_list << "(";

    copy(id_range.begin(), id_range.end(), csv_output_iterator<long, ostringstream>(in_list));

    in_list << ")";

    selector.where(selector.where() && id_column.in(RWDBExpr(in_list.str().c_str(), false)));
}

