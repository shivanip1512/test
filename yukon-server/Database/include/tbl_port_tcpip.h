/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_tcpip
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_tcpip.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_TCPIP_H__
#define __TBL_PORT_TCPIP_H__

#define TSDEFAULTPORT 1000

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

class IM_EX_CTIYUKONDB CtiTablePortTCPIP : public CtiMemDBObject
{

protected:

   // PortTerminalServer
   LONG           _portID;
   RWCString      _ipAddress;                 // in struct [15];
   INT            _ipPort;

private:

public:

   CtiTablePortTCPIP();

   CtiTablePortTCPIP(const CtiTablePortTCPIP& aRef);

   virtual ~CtiTablePortTCPIP();

   CtiTablePortTCPIP& operator=(const CtiTablePortTCPIP& aRef);

   INT getIPPort() const;
   INT& getIPPort();

   CtiTablePortTCPIP& setIPPort(const INT i);

   RWCString getIPAddress() const;
   RWCString& getIPAddress();

   CtiTablePortTCPIP&   setIPAddress(const RWCString &str);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();
};
#endif // #ifndef __TBL_PORT_TCPIP_H__
