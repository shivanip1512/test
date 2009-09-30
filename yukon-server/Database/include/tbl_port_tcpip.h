/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_tcpip
*
* Date:   3/29/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_port_tcpip.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/08/06 18:26:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_PORT_TCPIP_H__
#define __TBL_PORT_TCPIP_H__

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
   LONG _portID;
   string _ipAddress;
   INT _ipPort;
   string _encodingKey;
   string _encodingType;

private:

public:

   CtiTablePortTCPIP();

   CtiTablePortTCPIP(const CtiTablePortTCPIP& aRef);

   virtual ~CtiTablePortTCPIP();

   CtiTablePortTCPIP& operator=(const CtiTablePortTCPIP& aRef);

   INT getIPPort() const;
   void setIPPort(const INT i);

   const string &getIPAddress() const;
   void setIPAddress(const string &str);

   const string & getEncodingKey() const;
   void setEncodingKey(const string& encodingKey);

   const string & getEncodingType() const;
   void setEncodingType(const string& encodingType);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   static string getTableName();

};
#endif // #ifndef __TBL_PORT_TCPIP_H__
