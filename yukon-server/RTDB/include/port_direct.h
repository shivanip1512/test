
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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2002/12/12 17:06:42 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "port_base.h"
#include "port_dialout.h"
#include "port_dialin.h"
#include "tbl_port_serial.h"

class IM_EX_PRTDB CtiPortDirect : public CtiPort
{
protected:

   HANDLE                     _portHandle;
   CtiTablePortLocalSerial    _localSerial;

   CtiPortDialout             *_dialout;
   CtiPortDialin              *_dialin;

private:

    DCB _dcb;
    COMMTIMEOUTS _cto;

    int initPrivateStores();
    int getPortInQueueCount();
    int getPortOutQueueCount();
    int getPortCommError();


public:

   typedef CtiPort  Inherited;

   CtiPortDirect();
   CtiPortDirect(CtiPortDialout *dial);
   CtiPortDirect(CtiPortDialin *dialin);
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
   virtual void DecodeDialinDatabaseReader(RWDBReader &rdr);

   virtual INT openPort();
   virtual INT reset(INT trace);
   virtual INT setup(INT trace);
   virtual INT close(INT trace);
   virtual INT  connectToDevice(CtiDevice *Device, INT trace);
   virtual INT  disconnect(CtiDevice *Device, INT trace);
   virtual BOOL connected();
   virtual BOOL shouldDisconnect() const;
   virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

   virtual INT setPortReadTimeOut(USHORT timeout);
   virtual INT setPortWriteTimeOut(USHORT timeout);
   virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
   virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
   virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

   virtual INT setLine(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT );     // Set/reset the port's linesettings.
   virtual INT byteTime(ULONG bytes) const;
   virtual INT ctsTest() const;
   virtual INT dcdTest() const;
   virtual INT lowerRTS();
   virtual INT raiseRTS();
   virtual INT lowerDTR();
   virtual INT raiseDTR();

   virtual INT inClear();
   virtual INT outClear();

   virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);

   INT readIDLCHeader(CtiXfer& Xfer, unsigned long *bytesRead, bool suppressEcho);

   int enableXONXOFF();
   int disableXONXOFF();
   int enableRTSCTS();
   int disableRTSCTS();

};
#endif // #ifndef __PORT_DIRECT_H__
