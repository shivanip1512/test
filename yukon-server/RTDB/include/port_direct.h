#pragma once

#include "port_serial.h"
#include "port_dialable.h"
#include "tbl_port_serial.h"

class IM_EX_PRTDB CtiPortDirect : public CtiPortSerial
{
protected:

   HANDLE                     _portHandle;
   CtiTablePortLocalSerial    _localSerial;

   CtiPortDialable            *_dialable;

private:

    DCB _dcb;
    COMMTIMEOUTS _cto;

    int initPrivateStores();
    int getPortInQueueCount();
    int getPortOutQueueCount();
    int getPortCommError();

public:

   typedef CtiPortSerial Inherited;

   CtiPortDirect();
   CtiPortDirect(CtiPortDialable *dial);

   virtual ~CtiPortDirect();

   CtiTablePortLocalSerial          getLocalSerial() const;
   CtiTablePortLocalSerial&         getLocalSerial();

   CtiPortDirect& setLocalSerial(const CtiTablePortLocalSerial& aRef);

   virtual bool      isViable();
   virtual std::string getPhysicalPort() const;

   virtual HANDLE  getHandle() const;
   virtual HANDLE& getHandle();
   virtual HANDLE* getHandlePtr();
   CtiPortDirect& setHandle(const HANDLE& hdl);

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual void DecodeDialableDatabaseReader(Cti::RowReader &rdr);

   virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
   virtual INT reset(INT trace);
   virtual INT setup(INT trace);
   virtual INT close(INT trace);
   virtual INT  connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
   virtual INT  disconnect(CtiDeviceSPtr Device, INT trace);
   virtual BOOL connected();
   virtual BOOL shouldDisconnect() const;
   virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

   virtual INT setPortReadTimeOut(USHORT millitimeout);
   virtual INT setPortWriteTimeOut(USHORT millitimeout);
   virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
   virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
   virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

   virtual INT setLine(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT );     // Set/reset the port's linesettings.
   virtual INT byteTime(ULONG bytes) const;
   virtual INT ctsTest() const;
   virtual INT dcdTest() const;
   virtual INT dsrTest() const;
   virtual INT lowerRTS();
   virtual INT raiseRTS();
   virtual INT lowerDTR();
   virtual INT raiseDTR();

   virtual INT inClear();
   virtual INT outClear();

   virtual INT inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);
   virtual INT outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);

   INT readIDLCHeader(CtiXfer& Xfer, unsigned long *bytesRead, bool suppressEcho);

   int enableXONXOFF();
   int disableXONXOFF();
   int enableRTSCTS();
   int disableRTSCTS();

};
