
#pragma warning( disable : 4786)
#ifndef __PORT_TCPIP_H__
#define __PORT_TCPIP_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_tcpip
*
* Class:  CtiPortTCPIPDirect
* Date:   3/31/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_tcpip.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/22 19:47:18 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_base.h"
#include "tbl_port_tcpip.h"
#include "tcpsup.h"


class IM_EX_PRTDB CtiPortTCPIPDirect : public CtiPortBase
{
protected:

   CtiTablePortTCPIP    _tcpIpInfo;

private:

   struct sockaddr_in   _server;
   SOCKET               _socket;

   bool                 _open;
   bool                 _connected;
   bool                 _failed;
   bool                 _busy;
   INT                  _baud;


public:

   typedef CtiPortBase  Inherited;

   CtiPortTCPIPDirect();

   CtiPortTCPIPDirect(const CtiPortTCPIPDirect& aRef);
   virtual ~CtiPortTCPIPDirect();

   CtiPortTCPIPDirect& operator=(const CtiPortTCPIPDirect& aRef);

   INT&                       getIPPort();
   RWCString&                 getIPAddress();

   virtual INT                getIPPort() const;
   virtual RWCString          getIPAddress() const;


   CtiTablePortTCPIP          getTcpIpInfo() const;
   CtiTablePortTCPIP&         getTcpIpInfo();
   CtiPortTCPIPDirect&        setTcpIpInfo(const CtiTablePortTCPIP& tcpipinfo);


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT init();
   virtual INT close(INT trace);
   virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);

   virtual INT inClear() const;
   virtual INT outClear() const;
   virtual void Dump() const;
   virtual bool needsReinit() const;

   INT shutdownClose(PCHAR Label = NULL, ULONG Line = 0);
   INT queryBytesAvailable();
   INT receiveData(PBYTE Message, LONG Length, ULONG TimeOut, PLONG ReceiveLength);
   INT sendData(PBYTE Message, ULONG Length, PULONG Written);


};
#endif // #ifndef __PORT_TCPIP_H__
