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
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_TCPIP_H__
#define __PORT_TCPIP_H__
#pragma warning( disable : 4786)


#include "port_serial.h"
#include "port_dialable.h"
#include "tbl_port_tcpip.h"


class IM_EX_PRTDB CtiPortTCPIPDirect : public CtiPortSerial
{
protected:

   CtiTablePortTCPIP    _tcpIpInfo;

private:

   SOCKET               _socket;

   bool                 _open;
   bool                 _connected;
   CtiTime              _lastConnect;

   CtiPortDialable      *_dialable;

   bool isSocketBroken() const;

public:

   typedef CtiPortSerial Inherited;

   CtiPortTCPIPDirect();
   CtiPortTCPIPDirect(CtiPortDialable *dial);

   virtual ~CtiPortTCPIPDirect();

   INT           getIPPort()    const;
   const std::string &getIPAddress() const;

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void DecodeDialableDatabaseReader(Cti::RowReader &rdr);

   virtual bool isViable();
   virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
   virtual INT reset(INT trace);
   virtual INT setup(INT trace);
   virtual INT close(INT trace);
   virtual INT connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
   virtual INT disconnect(CtiDeviceSPtr Device, INT trace);
   virtual BOOL connected();

   virtual INT inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);

   virtual INT inClear() const;
   virtual INT outClear() const;
   virtual BOOL shouldDisconnect() const;
   virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

   virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
   virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
   virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

   INT shutdownClose(PCHAR Label = NULL, ULONG Line = 0);
   INT receiveData(PBYTE Message, LONG Length, ULONG TimeOut, PLONG ReceiveLength);
   INT sendData(PBYTE Message, ULONG Length, PULONG Written);
};

typedef boost::shared_ptr< CtiPortTCPIPDirect > CtiPortTCPIPDirectSPtr;

#endif // #ifndef __PORT_TCPIP_H__
