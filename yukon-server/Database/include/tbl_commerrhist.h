
#pragma warning( disable : 4786)
#ifndef __TBL_COMMERRHIST_H__
#define __TBL_COMMERRHIST_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_commerrhist
*
* Class:  CtiTableCommErrorHistory
* Date:   9/21/2001
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_commerrhist.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/09/24 14:36:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "utility.h"

#define MAX_COMMAND_LENGTH  50
#define MAX_OUTMESS_LENGTH  160
#define MAX_INMESS_LENGTH   160

class IM_EX_CTIYUKONDB CtiTableCommErrorHistory: public CtiMemDBObject
{
protected:

   LONG           _commErrorID;
   LONG           _paoID;
   RWTime         _dateTime;
   LONG           _soeTag;
   LONG           _errorType;
   LONG           _errorNumber;
   RWCString      _command;
   RWCString      _outMessage;
   RWCString      _inMessage;

private:

public:

   //CtiTableCommErrorHistory();

   CtiTableCommErrorHistory(LONG             paoid    = 0,
                            const RWTime&    datetime = RWTime(),
                            LONG             soe      = 0,
                            LONG             type     = 0,
                            LONG             number   = 0,
                            const RWCString& cmd      = RWCString("none"),
                            const RWCString& out      = RWCString("none"),
                            const RWCString& in       = RWCString("none"),
                            LONG             ceid     = CommErrorHistoryIdGen());

   CtiTableCommErrorHistory(const CtiTableCommErrorHistory& aRef);

   virtual ~CtiTableCommErrorHistory();

   CtiTableCommErrorHistory& operator=(const CtiTableCommErrorHistory &aRef);
   bool operator<(const CtiTableCommErrorHistory& aRef) const;

   LONG getCommErrorID() const;
   CtiTableCommErrorHistory& setCommErrorID( const LONG ce );

   LONG getPAOID() const;
   CtiTableCommErrorHistory& setPAOID( const LONG pao );

   const RWTime& getDateTime() const;
   CtiTableCommErrorHistory& setDateTime( const RWTime& dt );

   LONG getSoeTag() const;
   CtiTableCommErrorHistory& setSoeTag( const LONG st );

   LONG getErrorType() const;
   CtiTableCommErrorHistory& setErrorType( const LONG et );

   LONG getErrorNumber() const;
   CtiTableCommErrorHistory& setErrorNumber( const LONG en );

   const RWCString& getCommand() const;
   CtiTableCommErrorHistory& setCommand( const RWCString &str );

   const RWCString& getOutMessage() const;
   CtiTableCommErrorHistory& setOutMessage( const RWCString &str );

   const RWCString& getInMessage() const;
   CtiTableCommErrorHistory& setInMessage( const RWCString &str );

   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Insert(RWDBConnection &conn);
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
   static  RWDBStatus Prune(RWDate &earliestDate);

};
#endif // #ifndef __TBL_COMMERRHIST_H__
