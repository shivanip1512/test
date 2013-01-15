#pragma once

#include <windows.h>
#include <list>
#include <iostream>
#include <boost/shared_ptr.hpp>
#include <rw/thr/thrfunc.h>

#include "dev_base.h"
#include "dlldefs.h"
#include "logger.h"
#include "tbl_port_base.h"
#include "tbl_pao_lite.h"
#include "tbl_paoexclusion.h"
#include "xfer.h"
#include "critical_section.h"
#include "counter.h"

#define DEFAULT_QUEUE_GRIPE_POINT 50

#include <boost/noncopyable.hpp>

class CtiPort;
typedef boost::shared_ptr< CtiPort > CtiPortSPtr;


typedef void (*CTI_PORTTHREAD_FUNC_PTR)(void*);
typedef CTI_PORTTHREAD_FUNC_PTR (*CTI_PORTTHREAD_FUNC_FACTORY_PTR)(int);
class CtiTraceMsg;

class IM_EX_PRTDB CtiPort : public CtiMemDBObject, boost::noncopyable
{
public:

    typedef std::vector< CtiTablePaoExclusion >  exclusions;
    typedef std::vector< unsigned long >         prohibitions;
    typedef CtiMemDBObject Inherited;


    CtiPort();

    virtual ~CtiPort();

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

    virtual INT inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList) = 0;
    virtual INT outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList) = 0;
    virtual INT outInMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, std::list< CtiMessage* > &traceList);
    virtual INT traceIn(CtiXfer& Xfer, std::list< CtiMessage* > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;
    virtual INT traceOut(CtiXfer& Xfer, std::list< CtiMessage* > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;
    virtual INT traceXfer(CtiXfer& Xfer, std::list< CtiMessage* > &traceList, CtiDeviceSPtr Dev, INT status = NORMAL) const;

    static INT traceBytes(const BYTE *Message, ULONG Length, CtiTraceMsg &trace, std::list< CtiMessage* > &traceList);
    INT logBytes(BYTE *Message, ULONG Length) const;
    void fileTraces(std::list< CtiMessage* > &traceList) const;

    CtiLogger& getPortLog() { return _portLog; }

    /* virtuals to make the world all fat and happy */
    virtual bool   isViable();

    virtual INT    ctsTest() const;
    virtual INT    dcdTest() const;
    virtual INT    dsrTest() const;

    virtual INT    lowerRTS();
    virtual INT    raiseRTS();
    virtual INT    lowerDTR();
    virtual INT    raiseDTR();

    virtual INT    inClear();
    virtual INT    outClear();

    virtual INT    byteTime(ULONG bytes) const;

    virtual HANDLE getHandle() const;
    virtual std::string getPhysicalPort() const;
    virtual std::string getModemInit() const;

    virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT) = 0;
    virtual INT reset(INT trace);
    virtual INT setup(INT trace);
    virtual INT close(INT trace);

    virtual INT writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten);
    virtual INT readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead);

    virtual INT setPortReadTimeOut(USHORT millitimeout);
    virtual INT setPortWriteTimeOut(USHORT millitimeout);
    virtual INT waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual void DecodeDialableDatabaseReader(Cti::RowReader &rdr);

    HCTIQUEUE&  getPortQueueHandle();
    INT writeQueue( OUTMESS *OutMessage, HANDLE hQuit = NULL );
    INT writeQueueWithPriority( OUTMESS *OutMessage, int priority, HANDLE hQuit = NULL );
    INT readQueue( PULONG DataSize, PPVOID Data, BOOL32 WaitFlag, PBYTE Priority, ULONG* pElementCount);
    INT searchQueue( void *ptr, BOOL (*myFunc)(void*, void*), bool useFirstElement = true );

    INT queueInit(HANDLE hQuit);     // Sets up the PortQueue
    INT queueDeInit();               // Blasts the PortQueue
    INT verifyPortIsRunnable(HANDLE hQuit = NULL);

    INT writeShareQueue(ULONG Request, LONG DataSize, PVOID Data, ULONG Priority, HANDLE hQuit);

    RWThreadFunction& getPortThread();

    void haltLog();

    std::string getSharedPortType() const;
    INT getSharedSocketNumber() const;
    INT getType() const;
    LONG getPortID() const;
    INT isDialup() const;
    std::string getName() const;
    bool isInhibited() const;
    bool isSimulated() const;
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
    std::pair< bool, INT > verifyPortStatus(CtiDeviceSPtr Device, INT trace = 0);
    std::pair< bool, INT > checkCommStatus(CtiDeviceSPtr Device, INT trace = 0);

    CTI_PORTTHREAD_FUNC_PTR  setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn);

    virtual int enableXONXOFF();
    virtual int disableXONXOFF();
    virtual int enableRTSCTS();
    virtual int disableRTSCTS();

    virtual INT setLine(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT );     // Set/reset the port's linesettings.
    virtual bool setPortForDevice(CtiDeviceSPtr  Device);

    virtual ULONG getDelay(int Offset) const { return 0L; }
    virtual CtiPort& setDelay(int Offset, int D) { return *this; }

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

    CtiTime getLastOMRead() const;
    void setLastOMRead(CtiTime &atime = CtiTime());
    CtiTime getLastOMComplete() const;
    void setLastOMComplete(CtiTime &atime = CtiTime());

    void setPortShareBlock(CtiTime &atime);
    bool isPortShareBlocked(void) const;

    ULONG getQueueSlot() const;
    CtiPort& setQueueSlot(const ULONG slot = 0);

    bool getDeviceQueued() const;
    bool setDeviceQueued(LONG id);
    bool resetDeviceQueued(LONG id);
    void addDeviceQueuedWork(long deviceID, unsigned workCount);
    void setPortCommunicating(bool state = true, DWORD ticks = 0);
    void addPortTiming(DWORD ticks);
    int getWorkCount(long requestID = 0);
    std::vector<long> getQueuedWorkDevices();
    DWORD getPortTiming();

    CtiPort& setDevicePreload(LONG id);
    CtiPort& resetDevicePreload(LONG id);
    std::set<LONG> getPreloads(void);

    INT incQueueSubmittal(int bumpcnt, CtiTime &rwt);    // Bumps the count of submitted deviceQ entries for this 5 minute window.
    INT incQueueProcessed(int bumpCnt, CtiTime & rwt);   // Bumps the count of processed deviceQ entries for this 5 minute window.
    INT setQueueOrphans(int num, CtiTime &rwt);          // Number of queue entries remaining on device following this pass.
    void getQueueMetrics(int index, int &submit, int &processed, int &orphan); // Return the metrics above.

    bool getSharingStatus( ) const;
    void setSharingStatus( bool b );
    bool getShareToggle( ) const;
    void setShareToggle( bool b );

protected:

    ULONG               _queueGripe;
    CtiCounter          _submittal;
    CtiCounter          _processed;
    CtiCounter          _orphaned;

    CtiTblPAOLite       _tblPAO;
    CtiTablePortBase    _tblPortBase;

    RWThreadFunction  _portThread;
    HCTIQUEUE         _portQueue;
    HCTIQUEUE         _portShareQueue;        // This queue is used for ALL portsharing OMs.
    LONG              _connectedDevice;       // this is NON-ZERO if we are currently connected/communicating.
    ULONG             _connectedDeviceUID;    // A unique reproducable indicator for this device/connection etc.
    BOOL              _tapPort;               // This port has a TAP terminal connected to it!


    mutable CtiLogger _portLog;
    mutable CtiMutex  _classMutex;

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

    CTI_PORTTHREAD_FUNC_PTR     _portFunc;

    mutable CtiMutex            _exclusionMux;          // Used when processing the exclusion logic
    CtiCriticalSection          _criticalSection;       // Used for out message counting
    bool                        _executing;             // Port is currently executing work...
    bool                        _communicating;         // Port is being used for communicating
    prohibitions                _executionProhibited;   // Port is currently prohibited from executing because of this list of portids.
    exclusions                  _excluded;
    CtiTime                      _lastReport;       // Last comm fail report happened here.
    bool                        _minMaxIdle;

    CtiTime                      _lastOMRead;
    CtiTime                      _lastOMComplete;

    CtiTime                      _lastWrite;        //  used to determine if we should block the port share...
                                                    //  initialized to CtiTime::now() on startup

    typedef std::map<long, unsigned> device_queue_counts;

    std::set< LONG >                 _devicesQueued;
    std::set< LONG >                 _devicesPreloaded;
    device_queue_counts         _queuedWork;

    ULONG                       _queueSlot;         // This is the queue entry which will be popped on the next readQueue call.
    DWORD                       _entryMsecTime;     // The time to get one entry off the queue in Msecs.

    mutable short               _simulated;
    bool                        _sharingStatus;     // This is set to true if we are portsharing on this port.
    bool                        _sharingToggle;     // true if the next OM should be from Yukon, false if the foreign system has priority
};

inline bool CtiPort::isQuestionable() const   { return _commFailCount >= portMaxCommFails();}

inline INT CtiPort::getType() const   { return _tblPAO.getType();}
inline LONG CtiPort::getPortID() const { return _tblPAO.getID();}
inline bool CtiPort::isInhibited() const { return _tblPAO.isInhibited();}
inline std::string CtiPort::getName() const { return _tblPAO.getName();}

inline std::string CtiPort::getSharedPortType() const { return _tblPortBase.getSharedPortType();}
inline INT CtiPort::getSharedSocketNumber() const   { return _tblPortBase.getSharedSocketNumber();}
inline INT CtiPort::getProtocolWrap() const { return _tblPortBase.getProtocol();}

inline INT CtiPort::isDialup() const { return ((getType() == PortTypeLocalDialup || getType() == PortTypeTServerDialup || getType() == PortTypePoolDialout)); }

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

inline bool CtiPort::getSharingStatus( ) const { return _sharingStatus; }
inline void CtiPort::setSharingStatus( bool b ) { _sharingStatus = b; }
inline void CtiPort::setShareToggle( bool b ) { _sharingToggle = b; }
inline bool CtiPort::getShareToggle(  ) const { return _sharingToggle; }

