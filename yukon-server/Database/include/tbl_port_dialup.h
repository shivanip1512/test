/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_dialup
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_dialup.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_DIALUP_H__
#define __TBL_PORT_DIALUP_H__

#include <limits.h>

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePortDialup : public CtiMemDBObject
{

protected:

   // PortDialupModem
   LONG           _portID;
   RWCString      _modemType;
   RWCString      _modemInitString;           // in struct [MODEMINITLENGTH];
   RWCString      _prefixString;              // Pre Dial        - FIX FIX FIX
   RWCString      _suffixString;              // Post Dial       - FIX FIX FIX

private:

public:

   CtiTablePortDialup();

   CtiTablePortDialup(const CtiTablePortDialup& aRef);

   virtual ~CtiTablePortDialup();

   CtiTablePortDialup& operator=(const CtiTablePortDialup& aRef);

   RWCString                     getModemType() const;
   RWCString&                    getModemType();
   CtiTablePortDialup&           setModemType(const RWCString& str);

   RWCString                     getModemInitString() const;
   RWCString&                    getModemInitString();
   CtiTablePortDialup&           setModemInitString(const RWCString& str);

   RWCString                     getPrefixString() const;
   RWCString&                    getPrefixString();
   CtiTablePortDialup&           setPrefixString(const RWCString& str);

   RWCString                     getSuffixString() const;
   RWCString&                    getSuffixString();
   CtiTablePortDialup&           setSuffixString(const RWCString& str);

   static RWCString              getTableName();
   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   LONG                          getPortID() const;
   CtiTablePortDialup&           setPortID( const LONG ptid );

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

};
#endif // #ifndef __TBL_PORT_DIALUP_H__
