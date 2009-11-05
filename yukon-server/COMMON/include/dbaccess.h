/*-----------------------------------------------------------------------------
    Filename:
         dbaccess.h

    Programmer:
         Aaron Lauinger

    Description:
         Provides access to rw database and connection objects.

         // Example to set up 2 databases and get connections
         setDatabaseParams(0, "msq15d.dll", "cluster2","corey","corey");
         setDatabaseParams(1, "ora15d.dll", "betty","readmetertest","readmetertest");

         RWDBConnection conn1 = getConnection(0);
         RWDBConnection conn2 = getConnection(1);

    Initial Date:

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#ifndef DBACCESS_H
#define DBACCESS_H


#include <rw/tvslist.h>
#include <rw/tvhdict.h>

#include <rw/db/db.h>
#include <rw/db/dbmgr.h>

#include <rw/thr/threadid.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/guard.h>

#include "dlldefs.h"
#include "dllbase.h"

#include "sema.h"

extern IM_EX_CTIBASE CtiSemaphore  gDBAccessSema;
//various database connection options
IM_EX_CTIBASE
void setDatabaseParams(unsigned dbID,
                       const string& dbDll, const string& dbName,
                       const string& dbUser, const string& dbPassword );

IM_EX_CTIBASE RWDBDatabase getDatabase();
IM_EX_CTIBASE RWDBDatabase getDatabase(unsigned dbID);

IM_EX_CTIBASE RWDBConnection getConnection();
IM_EX_CTIBASE RWDBConnection getConnection(unsigned dbID);

IM_EX_CTIBASE RWDBReader ExecuteQuery(RWDBConnection& conn, const string& query);
IM_EX_CTIBASE RWDBStatus::ErrorCode ExecuteUpdater(RWDBConnection& conn, RWDBUpdater &updater, const char *file = 0, int line = 0, long *rowsAffected = 0);

IM_EX_CTIBASE RWDBStatus ExecuteInserter(RWDBConnection& conn, RWDBInserter &inserter, const char *file = 0, int line = 0);
IM_EX_CTIBASE int addDBIgnore(long ignoreError);
IM_EX_CTIBASE void resetDBIgnore();

IM_EX_CTIBASE string makeLeftOuterJoinSQL92Compliant(const string &sql);

IM_EX_CTIBASE void addIDClause(RWDBSelector &selector, RWDBColumn &id_column, const std::set<long> &paoids);

#endif

