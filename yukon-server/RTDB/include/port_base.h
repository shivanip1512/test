
#pragma warning( disable : 4786)
#ifndef __PORT_BASE_H__
#define __PORT_BASE_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_base
*
* Class:  CtiPort
* Date:   3/29/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/port_base.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2002/07/18 16:56:28 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
using namespace std;

#include <rw/thr/thrfunc.h>

#include "dev_base.h"
#include "dlldefs.h"
#include "logger.h"
#include "tbl_port_base.h"
#include "tbl_port_settings.h"
#include "tbl_port_timing.h"
#include "xfer.h"

#ifdef CTIOLDSTATS
  #include "tbl_port_statistics.h"
#endif

typedef shared_ptr< CtiPort > CtiPortSPtr;
typedef void (*CTI_PORTTHREAD_FUNC_PTR)(void*);

class CtiTraceMsg;

class IM_EX_PRTDB CtiPort : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

    CtiTblPAO _tblPAO;
    CtiTablePortBase _tblPortBase;
    CtiTablePortSettings _tblPortSettings;
    CtiTablePortTimings _tblPortTimings;

    #ifdef CTIOLDSTATS
    CtiTablePortStatistics* _portStatistics[ StatTypeInvalid ];
    #endif

    RWThreadFunction  _portThread;
    HCTIQUEUE         _portQueue;
    LONG              _connectedDevice;       // this is NON-ZERO if we are currently connected/communicating.
    ULONG             _connectedDeviceUID;    // A unique reproducable indicator for this device/connection etc.
    RWCString         _portNameWas;
    INT               _lastBaudRate;
    BOOL              _tapPort;               // This port has a TAP terminal connected to it!


    mutable CtiLogger _portLog;

private:

    size_t                      _traceListOffset;
    CTI_PORTTHREAD_FUNC_PTR     _portFunc;

public:


    typedef CtiMemDBObject Inherited;


    CtiPort();
    CtiPort(const CtiPort& aRef);

    virtual ~CtiPort();

    CtiPort& operator=(const CtiPort& aRef);
    bool operator<(const CtiPort& rhs) const;

    LONG                    getConnectedDevice() const;
    CtiPort&                setConnectedDevice(const LONG d);
    INT                     getLastBaudRate() const;
    CtiPort&                setLastBaudRate(const INT r);

    RWCString&              getPortNameWas();
    CtiPort&                setPortNameWas(const RWCString str);

    BOOL                    isTAP() const;
    CtiPort&                setTAP(BOOL b = TRUE);

#ifdef CTIOLDSTATS
    CtiTablePortStatistics  getPortStatistics(INT i) const;
    CtiTablePortStatistics& getPortStatistics(INT i);
    CtiPort&                setPortStatistics(const CtiTablePortStatistics& ps, INT i);
#endif

    virtual INT init() = 0;

    virtual INT connectToDevice(CtiDevice *Device, INT trace);

    virtual BOOL connected();
    virtual BOOL connectedTo(LONG devID);
    virtual BOOL connectedTo(ULONG crc);
    virtual INT disconnect(CtiDevice *Device, INT trace);
    virtual BOOL shouldDisconnect() const;
    virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);
    virtual INT reset(INT trace);
    virtual INT setup(INT trace);
    virtual INT close(INT trace);


    virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList) = 0;
    virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList) = 0;
    virtual INT outInMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT traceIn(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev = NULL, INT status = NORMAL) const;
    virtual INT traceOut(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev = NULL, INT status = NORMAL) const;
    virtual INT traceXfer(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev = NULL, INT status = NORMAL) const;

    INT traceBytes(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const;
    INT logBytes(BYTE *Message, ULONG Length) const;
    INT generateTraces(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const;
    void fileTraces(RWTPtrSlist< CtiMessage > &traceList) const;




    /* virtuals to make the world all fat and happy */
    virtual INT       ctsTest() const;
    virtual INT       dcdTest() const;

    virtual INT       baudRate(INT rate = 0);
    virtual INT       lowerRTS() const;
    virtual INT       raiseRTS() const;
    virtual INT       lowerDTR() const;
    virtual INT       raiseDTR() const;

    virtual INT       inClear() const;
    virtual INT       outClear() const;

    virtual INT       byteTime(ULONG bytes) const;


    virtual bool      needsReinit() const;
    virtual HANDLE    getHandle() const;
    virtual INT       getIPPort() const;
    virtual RWCString getIPAddress() const;
    virtual RWCString getPhysicalPort() const;
    virtual RWCString getModemInit() const;
    virtual ULONG     getDelay(int Offset) const;
    virtual CtiPort&  setDelay(int Offset, int D);


    void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    #ifdef CTIOLDSTATS
    virtual void DecodeStatisticsDatabaseReader(RWDBReader &rdr);
    #endif

    virtual void Dump() const;

    HCTIQUEUE&  getPortQueueHandle();
    INT         writeQueue(ULONG Request, LONG  DataSize, PVOID Data, ULONG Priority, HANDLE hQuit = NULL);

    INT queueInit(HANDLE hQuit);                 // Sets up the PortQueue
    INT queueDeInit();               // Blasts the PortQueue
    INT verifyPortIsRunnable(HANDLE hQuit = NULL);

    RWThreadFunction& getPortThread();

    void haltLog();



    // I hate this crap
    RWCString getSharedPortType() const;
    INT getSharedSocketNumber() const;
    INT getType() const;
    LONG getPortID() const;
    INT isDialup() const;
    RWCString getName() const;
    bool isInhibited() const;
    INT getBaudRate() const;
    bool isTCPIPPort() const;
    INT getProtocol() const;

    CtiPort &setBaudRate(INT baudRate);

    ULONG getConnectedDeviceUID() const;
    CtiPort& setConnectedDeviceUID(const ULONG &i);
    INT verifyPortStatus();

    CTI_PORTTHREAD_FUNC_PTR  setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn);
};

typedef CtiPort CtiPortBase;

inline INT CtiPort::getType() const   { return _tblPAO.getType();}
inline LONG CtiPort::getPortID() const { return _tblPAO.getID();}
inline bool CtiPort::isInhibited() const { return _tblPAO.isInhibited();}
inline RWCString CtiPort::getName() const { return _tblPAO.getName();}

inline RWCString CtiPort::getSharedPortType() const { return _tblPortBase.getSharedPortType();}
inline INT CtiPort::getSharedSocketNumber() const   { return _tblPortBase.getSharedSocketNumber();}
inline INT CtiPort::getProtocol() const { return _tblPortBase.getProtocol();}

inline INT CtiPort::isDialup() const { return ((getType() == PortTypeLocalDialup || getType() == PortTypeTServerDialup)); }
inline bool CtiPort::isTCPIPPort() const { return ((getType() == TSERVER_SERIAL_PORT) || (getType() == TSERVER_DIALUP_PORT)); }

inline INT CtiPort::getBaudRate() const { return _tblPortSettings.getBaudRate();}
inline bool CtiPort::operator<(const CtiPort& rhs) const { return getPortID() < rhs.getPortID(); }


#endif // #ifndef __PORT_BASE_H__
