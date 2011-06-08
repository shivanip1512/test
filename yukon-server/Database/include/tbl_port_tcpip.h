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
#include "row_reader.h"
#include <limits.h>
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
   std::string _ipAddress;
   INT _ipPort;
   std::string _encodingKey;
   std::string _encodingType;

private:

public:

   CtiTablePortTCPIP();

   CtiTablePortTCPIP(const CtiTablePortTCPIP& aRef);

   virtual ~CtiTablePortTCPIP();

   CtiTablePortTCPIP& operator=(const CtiTablePortTCPIP& aRef);

   INT getIPPort() const;
   void setIPPort(const INT i);

   const std::string &getIPAddress() const;
   void setIPAddress(const std::string &str);

   const std::string & getEncodingKey() const;
   void setEncodingKey(const std::string& encodingKey);

   const std::string & getEncodingType() const;
   void setEncodingType(const std::string& encodingType);

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   static std::string getTableName();

};
#endif // #ifndef __TBL_PORT_TCPIP_H__
