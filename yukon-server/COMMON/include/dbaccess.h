#ifndef DBACCESS_H
#define DBACCESS_H


#include <rw/tvslist.h>
#include <rw/tvhdict.h>


#include <rw/thr/threadid.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/guard.h>

#include "dlldefs.h"
#include "dllbase.h"

#include "sema.h"
class SAConnection;

//various database connection options
IM_EX_CTIBASE
void setDatabaseParams(const string& dbDll, const string& dbName,
                       const string& dbUser, const string& dbPassword );

// returns a SAConnection if successful and connection is valid, returns NULL if not.
IM_EX_CTIBASE SAConnection*  getNewConnection();
IM_EX_CTIBASE void releaseDBConnection(SAConnection *connection);

IM_EX_CTIBASE std::string assignSQLPlaceholders(const std::string &sql);

#endif

