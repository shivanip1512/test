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
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2004/05/10 21:35:51 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_BASE_H__
#define __PORT_BASE_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include <list>
#include <iostream>
#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
using namespace std;

#include <rw/thr/thrfunc.h>

#include "dev_base.h"
#include "dlldefs.h"
#include "logger.h"
#include "tbl_port_base.h"
#include "tbl_paoexclusion.h"
#include "xfer.h"

#ifdef CTIOLDSTATS
  #include "tbl_port_statistics.h"
#endif

typedef shared_ptr< CtiPort > CtiPortSPtr;
typedef void (*CTI_PORTTHREAD_FUNC_PTR)(void*);
typedef CTI_PORTTHREAD_FUNC_PTR (*CTI_PORTTHREAD_FUNC_FACTORY_PTR)(int);
class CtiTraceMsg;

class IM_EX_PRTDB CtiPort : public CtiMemDBObject, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

    typedef vector< CtiTablePaoExclusion >  exclusions;
    typedef vector< unsigned long >         prohibitions;
    typedef CtiMemDBObject Inherited;


    CtiPort();
    CtiPort(const CtiPort& aRef);

    virtual ~CtiPort();

    CtiPort& operator=(const CtiPort& aRef);
    bool operator<(const CtiPort& rhs) const;

    LONG getConnectedDevice() const;
    CtiPort& setConnectedDevice(const LONG d);

    BOOL isTAP() const;
    CtiPort& setTAP(BOOL b = TRUE);

    virtual INT connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
    virtual BOOL connected();
    virtual BOOL connectedTo(LONG devID);
    virtual BOOL connectedTo(ULONG crc);
    virtual INT disconnect(CtiDeviceSPtr Device, INT trace);
    virtual BOOL shouldDisconnect() const;
    virtual CtiPort& setShouldDisconnect(BOOL b = TRUE);

    virtual INT inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, RWTPtrSlist< CtiMessage > &traceList) = 0;
    virtual INT outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, RWTPtrSlist< CtiMessage > &traceList) = 0;
    virtual INT outInMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT traceIn(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;
    virtual INT traceOut(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;
    virtual INT traceXfer(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;

    INT traceBytes(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const;
    INT logBytes(BYTE *Message, ULONG Length) const;
    INT generateTraces(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const;
    void fileTraces(RWTPtrSlist< CtiMessage > &traceList) const;


    CtiLogger& getPortLog() { return _portLog; }


    /* virtuals to make the world all fat and happy */
    virtual bool      isViable() const;

    virtual INT       ctsTest() const;
    virtual INT       dcdTest() const;
    virtual INT       dsrTest() const;

    virtual INT       lowerRTS();
    virtual INT       raiseRTS();
    virtual INT       lowerDTR();
    virtual INT       raiseDTR();

    virtual INT       inClear();
    virtual INT       outClear();

    virtual INT       byteTime(ULONG bytes) const;

    virtual HANDLE    getHandle() const;
    virtual INT       getIPPort() const;
    virtual RWCString getIPAddress() const;
    virtual RWCString getPhysicalPort() const;
    virtual RWCString getModemInit() const;

    virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT) = 0;
    virtual INT reset(INT trace);
    virtual INT setup(INT trace);
    virtual INT close(INT trace);

    virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
    virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

    virtual INT setPortReadTimeOut(USHORT millitimeout);
    virtual INT setPortWriteTimeOut(USHORT millitimeout);
    virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);


    void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual void DecodeDialableDatabaseReader(RWDBReader &rdr);
    virtual void Dump() const;

    HCTIQUEUE&  getPortQueueHandle();
    INT writeQueue(ULONG Request, LONG  DataSize, PVOID Data, ULONG Priority, HANDLE hQuit = NULL);
    INT readQueue( PREQUESTDATA RequestData, PULONG  DataSize, PPVOID Data, BOOL32 WaitFlag, PBYTE Priority, ULONG *pElementCount );
    INT searchQueue( void *ptr, BOOL (*myFunc)(void*, void*) );

    INT queueInit(HANDLE hQuit);                 // Sets up the PortQueue
    INT queueDeInit();               // Blasts the PortQueue
    INT verifyPortIsRunnable(HANDLE hQuit = NULL);

    RWThreadFunction& getPortThread();

    void haltLog();

    RWCString getSharedPortType() const;
    INT getSharedSocketNumber() const;
    INT getType() const;
    LONG getPortID() const;
    INT isDialup() const;
    bool isDialin() const;
    RWCString getName() const;
    bool isInhibited() const;
    virtual INT getBaudRate() const;
    virtual INT getBits() const;
    virtual INT getStopBits() const;
    virtual INT getParity() const;

    virtual ULONG getCDWait() const             { return 0L;}

    bool isTCPIPPort() const;
    INT getProtocolWrap() const;
    CtiPort& setProtocolWrap(INT prot);


    virtual CtiPort &setBaudRate(INT baudRate);

    ULONG getConnectedDeviceUID() const;
    CtiPort& setConnectedDeviceUID(const ULONG &i);
    pair< bool, INT > verifyPortStatus(CtiDeviceSPtr Device, INT trace = 0);
    pair< bool, INT > checkCommStatus(CtiDeviceSPtr Device, INT trace = 0);

    CTI_PORTTHREAD_FUNC_PTR  setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn);

    virtual int enableXONXOFF();
    virtual int disableXONXOFF();
    virtual int enableRTSCTS();
    virtual int disableRTSCTS();

    virtual INT setLine(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT );     // Set/reset the port's linesettings.
    virtual bool setPortForDevice(CtiDeviceSPtr  Device);

    virtual ULONG getDelay(int Offset) const { return 0L; }
    virtual CtiPort& setDelay(int Offset, int D) { return *this; }

    CtiMutex& getExclusionMux();
    bool hasExclusions() const;
    exclusions getExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    bool isPortExcluded(long portid) const;
    bool isExecuting() const;
    void setExecuting(bool set);
    bool isExecutionProhibited() const;
    size_t setExecutionProhibited(unsigned long pid);
    bool removeInfiniteExclusion(unsigned long pid);

    virtual size_t addPort(CtiPortSPtr port);
    void setParentPort(CtiPortSPtr port);
    CtiPortSPtr& getParentPort();

    ULONG queueCount() const;

    INT applyPortQueue(void *ptr, void (*myFunc)(void*, void*));
    INT searchPortQueue(void *ptr, BOOL (*myFunc)(void*, void*));
    INT searchPortQueueForConnectedDeviceUID(BOOL (*myFunc)(void*, void*));


    virtual INT portMaxCommFails() const;
    bool adjustCommCounts( INT CommResult );
    bool isQuestionable() const;
    INT requeueToParent(OUTMESS *&OutMessage);            // Return all queue entries to the processing parent.
    bool isMinMaxIdle() const;
    void setMinMaxIdle(bool mmi);
    bool waitForPost(HANDLE quitEvent = INVALID_HANDLE_VALUE, LONG timeout = -1L) const;
    void postEvent();
    LONG getPoolAssignedGUID() const;
    void setPoolAssignedGUID(LONG guid);
    HANDLE setQuitEventHandle(HANDLE quit);
    HANDLE getQuitEventHandle();

    virtual bool isExecutionProhibitedByInternalLogic() const;

    RWTime getLastOMRead() const;
    void setLastOMRead(RWTime &atime = RWTime());
    RWTime getLastOMComplete() const;
    void setLastOMComplete(RWTime &atime = RWTime());

    ULONG getQueueSlot() const;
    CtiPort& setQueueSlot(const ULONG slot = 0);

    bool shouldProcessQueuedDevices() const;

    bool getDeviceQueued() const;
    CtiPort& setDeviceQueued(LONG id);
    CtiPort& resetDeviceQueued(LONG id);



protected:

    CtiTblPAO           _tblPAO;
    CtiTablePortBase    _tblPortBase;

    RWThreadFunction  _portThread;
    HCTIQUEUE         _portQueue;
    LONG              _connectedDevice;       // this is NON-ZERO if we are currently connected/communicating.
    ULONG             _connectedDeviceUID;    // A unique reproducable indicator for this device/connection etc.
    BOOL              _tapPort;               // This port has a TAP terminal connected to it!


    mutable CtiLogger _portLog;

    // Port pooling constructs.
    CtiPortSPtr _parentPort;                // Record parent port if any to this child

    LONG _poolAssignedGUID;                 // This is the "device" executing on this port!

    INT _commFailCount;                     // Consecutive failures to this port.
    INT _attemptCount;                      // Cumulative. Attempts to communicate with the port
    INT _attemptCommFailCount;              // Cumulative. Failed ERRTYPE_COMM class error.
    INT _attemptOtherFailCount;             // Cumulative. Failed !ERRTYPE_COMM class error.
    INT _attemptSuccessCount;               // Cumulative. Comms successful.

    HANDLE _postEvent;                      // This is an event handle which I will be able to wait on.
    HANDLE _quitEvent;                      // This is an event handle assigned to us to check for quit occasionally or sooner.


private:

    size_t                      _traceListOffset;
    CTI_PORTTHREAD_FUNC_PTR     _portFunc;

    mutable CtiMutex            _exclusionMux;          // Used when processing the exclusion logic
    bool                        _executing;             // Port is currently executing work...
    prohibitions                _executionProhibited;   // Port is currently prohibited from executing because of this list of portids.
    exclusions                  _excluded;
    RWTime                      _lastReport;    // Last comm fail report happened here.
    bool                        _minMaxIdle;

    RWTime                      _lastOMRead;
    RWTime                      _lastOMComplete;

    list< LONG >                _devicesQueued;

    ULONG                       _queueSlot;         // This is the queue entry which will be popped on the next readQueue call.
};

inline CtiMutex& CtiPort::getExclusionMux() { return _exclusionMux; }

inline bool CtiPort::isQuestionable() const   { return _commFailCount >= portMaxCommFails();}

inline INT CtiPort::getType() const   { return _tblPAO.getType();}
inline LONG CtiPort::getPortID() const { return _tblPAO.getID();}
inline bool CtiPort::isInhibited() const { return _tblPAO.isInhibited();}
inline RWCString CtiPort::getName() const { return _tblPAO.getName();}

inline RWCString CtiPort::getSharedPortType() const { return _tblPortBase.getSharedPortType();}
inline INT CtiPort::getSharedSocketNumber() const   { return _tblPortBase.getSharedSocketNumber();}
inline INT CtiPort::getProtocolWrap() const { return _tblPortBase.getProtocol();}

inline INT CtiPort::isDialup() const { return ((getType() == PortTypeLocalDialup || getType() == PortTypeTServerDialup || getType() == PortTypePoolDialout)); }
inline bool CtiPort::isDialin() const { return ((getType() == PortTypeLocalDialBack || getType() == PortTypeTServerDialBack)); }
inline bool CtiPort::isTCPIPPort() const { return ((getType() == PortTypeTServerDirect) || (getType() == PortTypeTServerDialup)); }

inline bool CtiPort::operator<(const CtiPort& rhs) const { return getPortID() < rhs.getPortID(); }
inline INT CtiPort::setPortWriteTimeOut(USHORT timeout) { return NORMAL; }
inline INT CtiPort::setLine(INT rate, INT bits, INT parity, INT stopbits ) { return NORMAL; }
inline int CtiPort::enableXONXOFF()    { return NORMAL; }
inline int CtiPort::disableXONXOFF()   { return NORMAL; }
inline int CtiPort::enableRTSCTS()     { return NORMAL; }
inline int CtiPort::disableRTSCTS()    { return NORMAL; }

inline INT CtiPort::getBaudRate() const { return 0;}
inline INT CtiPort::getBits() const { return 8; }
inline INT CtiPort::getStopBits() const { return 1; }
inline INT CtiPort::getParity() const { return NOPARITY; }

inline size_t CtiPort::addPort(CtiPortSPtr port) { return (size_t)0; }
inline void CtiPort::setParentPort(CtiPortSPtr port) { _parentPort = port; return; }
inline CtiPortSPtr& CtiPort::getParentPort() { return _parentPort; }

inline bool CtiPort::isMinMaxIdle() const { return _minMaxIdle; }
inline void CtiPort::setMinMaxIdle(bool mmi) { _minMaxIdle = mmi; return;}

inline LONG CtiPort::getPoolAssignedGUID() const { return _poolAssignedGUID; }
inline void CtiPort::setPoolAssignedGUID(LONG guid) { _poolAssignedGUID = guid; }

inline HANDLE CtiPort::setQuitEventHandle(HANDLE quit) { HANDLE oldQuit = _quitEvent; _quitEvent = quit; return oldQuit; }
inline HANDLE CtiPort::getQuitEventHandle() { return _quitEvent; }

inline bool CtiPort::isExecutionProhibitedByInternalLogic() const { return false;}
inline ULONG CtiPort::getQueueSlot() const { return _queueSlot; }
inline CtiPort& CtiPort::setQueueSlot(const ULONG slot) { _queueSlot = slot; return *this; }


#endif // #ifndef __PORT_BASE_H__
