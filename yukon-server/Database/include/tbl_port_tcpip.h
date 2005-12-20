/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_tcpip
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_tcpip.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_TCPIP_H__
#define __TBL_PORT_TCPIP_H__

#define TSDEFAULTPORT 1000

#include <limits.h>
#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
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
   string      _ipAddress;                 // in struct [15];
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

   string getIPAddress() const;
   string& getIPAddress();

   CtiTablePortTCPIP&   setIPAddress(const string &str);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static string getTableName();
};
#endif // #ifndef __TBL_PORT_TCPIP_H__
