

#pragma once

#include "row_reader.h"
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "utility.h"
#include "database_connection.h"


#define MAX_COMMAND_LENGTH  50
#define MAX_OUTMESS_LENGTH  160
#define MAX_INMESS_LENGTH   160

class IM_EX_CTIYUKONDB CtiTableCommErrorHistory: public CtiMemDBObject
{
protected:

   LONG           _commErrorID;
   LONG           _paoID;
   CtiTime         _dateTime;
   LONG           _soeTag;
   LONG           _errorType;
   LONG           _errorNumber;
   string      _command;
   string      _outMessage;
   string      _inMessage;

public:

   //CtiTableCommErrorHistory();

   CtiTableCommErrorHistory(LONG             paoid    = 0,
                            const CtiTime&    datetime = CtiTime(),
                            LONG             soe      = 0,
                            LONG             type     = 0,
                            LONG             number   = 0,
                            const string& cmd      = string("none"),
                            const string& out      = string("none"),
                            const string& in       = string("none"),
                            LONG             ceid     = CommErrorHistoryIdGen());

   CtiTableCommErrorHistory(const CtiTableCommErrorHistory& aRef);

   virtual ~CtiTableCommErrorHistory();

   CtiTableCommErrorHistory& operator=(const CtiTableCommErrorHistory &aRef);
   bool operator<(const CtiTableCommErrorHistory& aRef) const;

   LONG getCommErrorID() const;
   CtiTableCommErrorHistory& setCommErrorID( const LONG ce );

   LONG getPAOID() const;
   CtiTableCommErrorHistory& setPAOID( const LONG pao );

   const CtiTime& getDateTime() const;
   CtiTableCommErrorHistory& setDateTime( const CtiTime& dt );

   LONG getSoeTag() const;
   CtiTableCommErrorHistory& setSoeTag( const LONG st );

   LONG getErrorType() const;
   CtiTableCommErrorHistory& setErrorType( const LONG et );

   LONG getErrorNumber() const;
   CtiTableCommErrorHistory& setErrorNumber( const LONG en );

   const string& getCommand() const;
   CtiTableCommErrorHistory& setCommand( const string &str );

   const string& getOutMessage() const;
   CtiTableCommErrorHistory& setOutMessage( const string &str );

   const string& getInMessage() const;
   CtiTableCommErrorHistory& setInMessage( const string &str );

   static string getTableName();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual bool Insert();
   virtual bool Insert(Cti::Database::DatabaseConnection &conn);
   static  bool Prune(CtiDate &earliestDate);

};

