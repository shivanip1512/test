
#pragma warning( disable : 4786)
#ifndef __PORT_DIRECT_H__
#define __PORT_DIRECT_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_direct
*
* Class:  CtiPortDirect
* Date:   3/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_direct.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/09/19 15:57:59 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_base.h"
#include "port_dialout.h"
#include "tbl_port_serial.h"

class IM_EX_PRTDB CtiPortDirect : public CtiPort
{
protected:

   HANDLE                     _portHandle;
   CtiTablePortLocalSerial    _localSerial;

   CtiPortDialout             *_dialout;

private:

public:

   typedef CtiPort  Inherited;

   CtiPortDirect(CtiPortDialout *dial = 0);
   CtiPortDirect(const CtiPortDirect& aRef);

   virtual ~CtiPortDirect();

   CtiPortDirect& operator=(const CtiPortDirect& aRef);

   CtiTablePortLocalSerial          getLocalSerial() const;
   CtiTablePortLocalSerial&         getLocalSerial();

   CtiPortDirect& setLocalSerial(const CtiTablePortLocalSerial& aRef);

   virtual bool      isViable() const;
   virtual RWCString getPhysicalPort() const;

   virtual HANDLE  getHandle() const;
   virtual HANDLE& getHandle();
   virtual HANDLE* getHandlePtr();
   CtiPortDirect& setHandle(const HANDLE& hdl);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeDialoutDatabaseReader(RWDBReader &rdr);

   virtual INT init();
   virtual INT reset(INT trace);
   virtual INT setup(INT trace);
   virtual INT close(INT trace);
   virtual INT  connectToDevice(CtiDevice *Device, INT trace);
   virtual INT  disconnect(CtiDevice *Device, INT trace);
   virtual BOOL connected();
   virtual BOOL shouldDisconnect() const;
   virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

   virtual INT setPortReadTimeOut(USHORT timeout);
   virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
   virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
   virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

   virtual INT baudRate(INT rate = 0) const;     // Set/reset the port's baud rate to the DB value
   virtual INT byteTime(ULONG bytes) const;
   virtual INT ctsTest() const;
   virtual INT dcdTest() const;
   virtual INT lowerRTS() const;
   virtual INT raiseRTS() const;
   virtual INT lowerDTR() const;
   virtual INT raiseDTR() const;

   virtual INT inClear() const;
   virtual INT outClear() const;

   virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);

   INT readIDLCHeader(CtiXfer& Xfer, unsigned long *bytesRead, bool suppressEcho);
};
#endif // #ifndef __PORT_DIRECT_H__
