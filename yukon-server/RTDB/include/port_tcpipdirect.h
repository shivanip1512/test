#pragma once

#include "port_serial.h"
#include "port_dialable.h"
#include "tbl_port_tcpip.h"


class IM_EX_PRTDB CtiPortTCPIPDirect : public CtiPortSerial
{
   CtiTablePortTCPIP    _tcpIpInfo;

   SOCKET               _socket;

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
   virtual YukonError_t openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
   virtual YukonError_t reset(INT trace);
   virtual YukonError_t setup(INT trace);
   virtual INT close(INT trace);
   virtual YukonError_t connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
   virtual INT disconnect(CtiDeviceSPtr Device, INT trace);
   virtual BOOL connected();

   virtual YukonError_t inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);
   virtual YukonError_t outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);

   virtual YukonError_t inClear() const;
   virtual INT outClear() const;
   virtual BOOL shouldDisconnect() const;
   virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

   virtual YukonError_t waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
   virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
   virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

   INT shutdownClose(PCHAR Label = NULL, ULONG Line = 0);
   YukonError_t receiveData(PBYTE Message, LONG Length, ULONG TimeOut, PLONG ReceiveLength);
   INT sendData(PBYTE Message, ULONG Length, PULONG Written);
};

typedef boost::shared_ptr< CtiPortTCPIPDirect > CtiPortTCPIPDirectSPtr;
