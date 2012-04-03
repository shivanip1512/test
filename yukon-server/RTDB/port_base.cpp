#include "precompiled.h"

#include <iostream>

#include "cparms.h"
#include "port_base.h"
#include "prot_emetcon.h"
#include "dsm2err.h"
#include "color.h"
#include "porter.h"
#include "logger.h"
#include "msg_trace.h"
#include "dllbase.h"

#include "numstr.h"

#define SCREEN_WIDTH    80

using namespace std;

INT CtiPort::traceIn(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr  Dev, INT ErrorCode) const
{
    INT status = NORMAL;

    string msg;

    try
    {
        if(Xfer.doTrace(ErrorCode) &&  !(Xfer.getInCountActual() == 0 && !ErrorCode) )
        {
            if(!isTAP() || (Dev && !Dev->isTAP()))
            {
                {
                    CtiTraceMsg trace;

                    //  set bright yellow for the time message
                    trace.setBrightYellow();
                    trace.setTrace( CtiTime().asString().c_str() );
                    trace.setEnd(false);
                    traceList.push_back(trace.replicateMessage());

                    //  set bright cyan for the info message
                    trace.setBrightCyan();
                    msg = "  P: " + CtiNumStr(getPortID()).spad(3) + string(" / ") + getName();
                    trace.setTrace(msg);
                    trace.setEnd(false);
                    traceList.push_back(trace.replicateMessage());

                    if(Dev)
                    {
                        trace.setBrightCyan();
                        msg = "  D: " + CtiNumStr(Dev->getID()).spad(3) + string(" / ") + Dev->getName();
                        trace.setTrace(msg);
                        trace.setEnd(false);
                        traceList.push_back(trace.replicateMessage());
                    }

                    if(ErrorCode)
                    {
                        if( ErrorCode == ErrPortSimulated )
                        {
                            trace.setBrightWhite();
                            msg = " IN: (simulated, no bytes returned)";
                        }
                        else
                        {
                            trace.setBrightRed();
                            msg = " IN: " + CtiNumStr(ErrorCode).spad(3);
                        }
                    }
                    else
                    {
                        trace.setBrightWhite();
                        msg = " IN:";
                    }
                    trace.setTrace(msg);
                    trace.setEnd(true);
                    traceList.push_back(trace.replicateMessage());


                    //  then print the formatted hex trace
                    if(Xfer.getInCountActual() > 0)
                    {
                        trace.setBrightMagenta();
                        traceBytes(Xfer.getInBuffer(), Xfer.getInCountActual(), trace, traceList);
                    }

                    if(ErrorCode && ErrorCode != ErrPortSimulated)
                    {
                        trace.setBrightRed();
                        trace.setTrace( FormatError(ErrorCode) );
                        trace.setEnd(true);
                        traceList.push_back(trace.replicateMessage());
                        trace.setNormal();
                    }
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}

INT CtiPort::traceXfer(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr  Dev, INT ErrorCode) const
{
    INT status = NORMAL;

    if(!isTAP() || (Dev && !Dev->isTAP()))
    {
        if(Xfer.traceError() && ErrorCode != NORMAL && ErrorCode != ErrPortSimulated)      // if Error is set, it happened on the InMessage, and we didn't print the outmessage before
        {
            status = traceOut(Xfer, traceList, Dev, ErrorCode);
        }

        if( NORMAL == status)
        {
            status = traceIn(Xfer, traceList, Dev, ErrorCode);
        }

    }

    return status;
}

INT CtiPort::traceOut(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr Dev, INT ErrorCode) const
{
    INT status = NORMAL;
    string msg;

    try
    {
        if(!isTAP() || (Dev && !Dev->isTAP()))
        {
            if(Xfer.doTrace(ErrorCode))
            {
                {
                    CtiTraceMsg trace;

                    //  set bright yellow for the time message
                    trace.setBrightYellow();
                    trace.setTrace( CtiTime().asString().c_str() );
                    trace.setEnd(false);
                    traceList.push_back(trace.replicateMessage());

                    //  set bright cyan for the info message
                    trace.setBrightCyan();
                    msg = "  P: " + CtiNumStr(getPortID()).spad(3) + string(" / ") + getName();
                    trace.setTrace(msg);
                    trace.setEnd(false);
                    traceList.push_back(trace.replicateMessage());

                    if(Dev)
                    {
                        trace.setBrightCyan();
                        msg = "  D: " + CtiNumStr(Dev->getID()).spad(3) + string(" / ") + Dev->getName();
                        trace.setTrace(msg);
                        trace.setEnd(false);
                        traceList.push_back(trace.replicateMessage());
                    }

                    if(ErrorCode)
                    {
                        trace.setBrightRed();
                        msg = " OUT: " + CtiNumStr((short)ErrorCode).spad(3);
                    }
                    else
                    {
                        trace.setBrightWhite();
                        msg = " OUT:";
                    }
                    trace.setTrace(msg);
                    trace.setEnd(true);
                    traceList.push_back(trace.replicateMessage());

                    //  then print the formatted hex trace
                    trace.setBrightGreen();
                    traceBytes(Xfer.getOutBuffer(), Xfer.getOutCount(), trace, traceList);
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}

INT CtiPort::logBytes(BYTE *Message, ULONG Length) const
{
    INT status = NORMAL;
    ULONG i;
    ULONG width = 1;
    ULONG offset = 0;

    list< CtiMessage* > traceList;
    CtiTraceMsg trace;

    traceBytes( Message, Length, trace, traceList);

    _portLog << endl;

    while( traceList.size() )
    {
        CtiTraceMsg *pTrace = (CtiTraceMsg*)traceList.front();traceList.pop_front();
        _portLog << pTrace->getTrace();
        delete pTrace;
    }

    _portLog << endl;

    return status;
}

INT CtiPort::writeQueue(ULONG Request, LONG DataSize, PVOID Data, ULONG Priority, HANDLE hQuit)
{
    int status = NORMAL;
    ULONG QueEntries;
    //static ULONG _queueGripe = DEFAULT_QUEUE_GRIPE_POINT;

#ifdef DEBUG
    OUTMESS *OutMessage = (OUTMESS*)Data;
    if(OutMessage->DeviceID == 0 && OutMessage->TargetID == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  A thread just dumped an untraceable OUTMESS request into a port queue" << endl;
            dout << "  You will undoubtedly have difficulties soon" << endl;
        }
    }
#endif

    OUTMESS *OutMessage = (OUTMESS*)Data;
    if(OutMessage && (DataSize == sizeof(CtiOutMessage)) &&
       OutMessage->HeadFrame[0] == 0x02 && OutMessage->HeadFrame[1] == 0xe0 &&
       OutMessage->TailFrame[0] == 0xea && OutMessage->TailFrame[1] == 0x03)
    {
        if(OutMessage->Sequence == Cti::Protocols::EmetconProtocol::Scan_LoadProfile)
        {
            if( isDebugLudicrous() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Cleaning Excess LP Entries for TargetID " << OutMessage->TargetID << " **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            // Remove any other Load Profile Queue Entries for this Queue.
            CleanQueue( _portQueue, (void*)OutMessage, findLPRequestEntries, cleanupOutMessages );
        }
    }

    if(verifyPortIsRunnable( hQuit ) == NORMAL)
    {
        if(OutMessage && OutMessage->MessageFlags & MessageFlag_PortSharing)        // This OM has been tagged as a sharing OM.
        {
            int blockTime = gConfigParms.getValueAsInt("PORT_SHARE_BLOCKING_DURATION", 0);

            if( !blockTime || ((_lastWrite + blockTime) < CtiTime::now()) )
            {
                status = writeShareQueue(Request, DataSize, Data, Priority, &QueEntries);
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - port \"" << getName() << "\" has blocked an incoming OM from its port share (_lastWrite = " << _lastWrite << ", blockTime = " << blockTime << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                status = QUEUE_WRITE;
            }
        }
        else if(_portQueue != NULL)
        {
            if(_postEvent != INVALID_HANDLE_VALUE)
            {
                SetEvent( _postEvent );                 // Just in case someone is sleeping at the wheel
            }

            _lastWrite = CtiTime::now();

            status = WriteQueue( _portQueue, Request, DataSize, Data, Priority, &QueEntries);

            if(QueEntries >= _queueGripe)
            {
                ULONG gripemore = _queueGripe * 2;
                _queueGripe = _queueGripe + ( gripemore < 1000 ? gripemore : 1000);
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getName() << " has just received a new port queue entry.  There are " << QueEntries << " pending." << endl;
                }
            }
            else if(QueEntries < DEFAULT_QUEUE_GRIPE_POINT)
            {
                _queueGripe = DEFAULT_QUEUE_GRIPE_POINT;
            }
        }
        else
        {
            status = QUEUE_WRITE;
        }
    }
    else
    {
        status = QUEUE_WRITE;
    }

    return status;
}

INT CtiPort::queueInit(HANDLE hQuit)
{
    INT status = NORMAL;

    if(_portQueue == NULL)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        /* create the queue for this port */
        if( (status = CreateQueue (&_portQueue, hQuit)) != NORMAL )
        {
            CloseQueue( _portQueue );
            _portQueue = NULL;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "Error Creating Queue for Port:  " << setw(2) << getPortID() << " / " << getName() << endl;
            }
        }
    }

    return status;
}

INT CtiPort::queueDeInit()
{
    INT status = NORMAL;

    // RWMutexLock::LockGuard( getMux() );      // 072503 CGP - What the !@$$@#??
    CtiLockGuard<CtiMutex> guard(_classMutex);

    /* create the queue for this port */
    CloseQueue( _portQueue );
    _portQueue = NULL;

    status = !NORMAL;

    return status;
}

INT CtiPort::verifyPortIsRunnable( HANDLE hQuit )
{
    INT status = NORMAL;

    try
    {
        if( _tblPAO.isInhibited() )
        {
            status = PORTINHIBITED;
        }
        else if( (status = queueInit(hQuit)) == NORMAL )
        {
            if(!_portThread.isValid() || _portThread.getCompletionState() != RW_THR_PENDING)
            {
                if(_portFunc != 0)
                {
                    _portThread = rwMakeThreadFunction( _portFunc, (void*)getPortID() );
                    _portThread.start();
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " No port thread function defined" << endl;
                    }
                    status = !NORMAL;
                }
            }
        }
    }
    catch( RWxmsg &e)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** RW EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << e.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << endl;
        }
    }


    return status;
}

void CtiPort::DecodeDialableDatabaseReader(Cti::RowReader &rdr)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** DecodeDialableDatabaseReader not defined for " << getName() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void CtiPort::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    _tblPAO.DecodeDatabaseReader(rdr);
    _tblPortBase.DecodeDatabaseReader(rdr);

    setValid();

    if(gLogPorts && !_portLog.isRunning())
    {
        {
            string of(getName() + "_");

            string comlogdir(gLogDirectory + "\\Comm");
            // Create a subdirectory called Comm beneath Log.
            CreateDirectoryEx( gLogDirectory.c_str(), comlogdir.c_str(), NULL);

            _portLog.setToStdOut(false);  // Not to std out.
            _portLog.setOwnerInfo(CompileInfo);
            _portLog.setOutputPath(comlogdir.c_str());
            _portLog.setOutputFile( of.c_str() );
            _portLog.setWriteInterval(10000);                   // 7/23/01 CGP.

            _portLog.start();
        }
    }
}

CtiPort::~CtiPort()
{
    if(_portQueue != NULL)
    {
        CloseQueue( _portQueue );
        _portQueue = NULL;
    }

    if(_portShareQueue != NULL)
    {
        CloseQueue( _portShareQueue );
        _portShareQueue = NULL;
    }

    if(_postEvent != INVALID_HANDLE_VALUE)
    {
        CloseHandle( _postEvent );
        _postEvent = INVALID_HANDLE_VALUE;
    }

    haltLog();
}

void CtiPort::haltLog()
{
    if( gLogPorts )
    {
        _portLog.interrupt(CtiThread::SHUTDOWN);
        _portLog.join();
    }
}

INT CtiPort::outInMess(CtiXfer& Xfer, CtiDeviceSPtr Dev, list< CtiMessage* > &traceList)
{
    INT   status = NORMAL;

    Xfer.setInCountActual((ULONG)0L);    // Make sure that any error on the outMess does not affect a state machine!

    if( NORMAL == (status = outMess(Xfer, Dev, traceList)) )
    {
        status = inMess(Xfer, Dev, traceList);
    }

    return status;
}



CtiPort::CtiPort() :
_poolAssignedGUID(0),
_quitEvent(INVALID_HANDLE_VALUE),
_postEvent(INVALID_HANDLE_VALUE),
_portFunc(0),
_portQueue(NULL),
_portShareQueue(NULL),
_connectedDevice(0L),
_connectedDeviceUID(-1),
_tapPort(FALSE),
_minMaxIdle(false),
_commFailCount(0),
_attemptCount(0),
_attemptCommFailCount(0),
_attemptOtherFailCount(0),
_attemptSuccessCount(0),
_queueSlot(0),
_queueGripe(DEFAULT_QUEUE_GRIPE_POINT),
_simulated(0),
_sharingStatus(false),
_sharingToggle(false),
_communicating(false),
_executing(false),
_entryMsecTime(0)
{
    _postEvent = CreateEvent( NULL, TRUE, FALSE, NULL);
}

LONG CtiPort::getConnectedDevice() const
{
    return _connectedDevice;
}
CtiPort& CtiPort::setConnectedDevice(const LONG d)
{
    _connectedDevice = d;
    return *this;
}

CtiPort &CtiPort::setBaudRate(INT baudRate)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Port " << getName() << " is not a serial port.  It cannot \"setBaudRate()\"" << endl;
    }

    return *this;
}



BOOL CtiPort::isTAP() const
{
    return _tapPort;
}
CtiPort& CtiPort::setTAP(BOOL b)
{
    _tapPort = b;
    return *this;
}

bool CtiPort::isSimulated() const
{
    //  if we haven't checked before
    if( !_simulated )
    {
        set<long>::iterator itr;
        _simulated = -1;  //  default to NOT simulated

        if( !gSimulatedPorts.empty() )
        {
            itr = gSimulatedPorts.find(getPortID());
        }
        else
        {
            itr = gSimulatedPorts.end();
        }

        if( (gSimulatePorts > 0) && (itr != gSimulatedPorts.end()) )  //  must be included
        {
            _simulated = 1;
        }
        else if( (gSimulatePorts < 0) && (itr == gSimulatedPorts.end()) )  //  must be excluded
        {
            _simulated = 1;
        }
    }

    return _simulated > 0;
}

INT CtiPort::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    INT status     = NORMAL;
    ULONG DeviceCRC = Device->getUniqueIdentifier();

    LastDeviceId = 0L;

    /*
     *  This next block Makes sure we hang up if we are connected to a different device's UID
     */

    if(connected() && !connectedTo(DeviceCRC))      // This port connected to a device, and is not connected to this device.
    {
        disconnect(Device, trace);
    }

    /*
     *  At this point we either connected to the device we need (Device), or are not connected to any device.
     */

    {
        /* Make sure the remotes match */
        setConnectedDevice( Device->getID() );
        setConnectedDeviceUID( DeviceCRC );
    }

    return status;
}

BOOL CtiPort::connected()
{
    BOOL status = FALSE;

    if(getConnectedDevice() > 0)
    {
        status = TRUE;
    }

    return status;
}

BOOL CtiPort::connectedTo(LONG devID)
{
    return(devID == getConnectedDevice());
}
BOOL CtiPort::connectedTo(ULONG crc)
{
    return(crc == getConnectedDeviceUID());
}
INT CtiPort::disconnect(CtiDeviceSPtr Device, INT trace)
{
    if(Device)
    {
        Device->setLogOnNeeded(TRUE);
    }

    setConnectedDevice(0L);
    setConnectedDeviceUID(-1);

    return NORMAL;
}

CtiPort& CtiPort::setShouldDisconnect(BOOL b)
{
    return *this;
}
BOOL CtiPort::shouldDisconnect() const
{
    return FALSE;
}
INT CtiPort::reset(INT trace)
{
    return NORMAL;
}
INT CtiPort::setup(INT trace)
{
    return NORMAL;
}
INT CtiPort::close(INT trace)
{
    return NORMAL;
}


/* virtuals to make the world all fat and happy */
INT       CtiPort::ctsTest() const
{
    return TRUE;
}
INT       CtiPort::dcdTest() const
{
    return TRUE;
}
INT       CtiPort::dsrTest() const
{
    return TRUE;
}

INT       CtiPort::lowerRTS()
{
    return NORMAL;
}
INT       CtiPort::raiseRTS()
{
    return NORMAL;
}
INT       CtiPort::lowerDTR()
{
    return NORMAL;
}
INT       CtiPort::raiseDTR()
{
    return NORMAL;
}

INT       CtiPort::inClear()
{
    return NORMAL;
}
INT       CtiPort::outClear()
{
    return NORMAL;
}

INT       CtiPort::byteTime(ULONG bytes) const
{
    return 0;
}


HANDLE    CtiPort::getHandle() const
{
    return NULL;
}
string CtiPort::getPhysicalPort() const
{
    return string("");
}
string CtiPort::getModemInit() const
{
    return string("");
}

HCTIQUEUE&  CtiPort::getPortQueueHandle()
{
    return _portQueue;
}

RWThreadFunction& CtiPort::getPortThread()
{
    return _portThread;
}


INT CtiPort::traceBytes(const BYTE *Message, ULONG Length, CtiTraceMsg &trace, list< CtiMessage* > &traceList)
{
    INT status = NORMAL;
    ULONG i;
    ULONG width = 1;
    ULONG offset = 0;
    char buffer[SCREEN_WIDTH + 1];

    buffer[0] = '\0';

    /* loop through all of the characters */
    for(i = 0; i < Length; i++)
    {
        if(width + 2 > SCREEN_WIDTH)
        {
            /* yes so goto new line */
            trace.setTrace( string( buffer ) );
            trace.setEnd(true);
            traceList.push_back(trace.replicateMessage());

            buffer[0] = '\0';
            offset = 0;
            width = 1;
        }

        /* print byte in hex with leading zero */
        if(width + 3 > SCREEN_WIDTH)
        {
            ::sprintf(&buffer[offset], "%02x", (BYTE)Message[i]);
            offset += 2;
            width += 2;
        }
        else
        {
            ::sprintf(&buffer[offset], "%02x ", (BYTE)Message[i]);
            offset += 3;
            width += 3;
        }
    }

    trace.setTrace( string( buffer ));
    traceList.push_back(trace.replicateMessage());

    return status;
}


void CtiPort::fileTraces(list< CtiMessage* > &traceList) const
{
    if(gLogPorts)
    {
        CtiLockGuard<CtiLogger> portlog_guard(_portLog);
        std::list< CtiMessage* >::iterator itr = traceList.begin();
        while( itr != traceList.end() )
        {
            CtiTraceMsg* pTrace = (CtiTraceMsg*)*itr;
            _portLog << pTrace->getTrace();
            if(pTrace->isEnd())
            {
                _portLog << endl;
            }
            ++itr;
        }
    }
}

ULONG CtiPort::getConnectedDeviceUID() const
{
    return _connectedDeviceUID;
}
CtiPort& CtiPort::setConnectedDeviceUID(const ULONG &i)
{
    _connectedDeviceUID = i;
    return *this;
}


pair< bool, INT > CtiPort::verifyPortStatus(CtiDeviceSPtr Device, INT trace)
{
    pair< bool, INT > rpair = make_pair( false, NORMAL );
    static const bool release_idle_ports = gConfigParms.isTrue("PORTER_RELEASE_IDLE_PORTS");

    //  no need to attempt this if we're simulating the port
    if( !isSimulated() )
    {
        if( !isDialup() && !release_idle_ports) // We don't always want to re-open these types of port.
        {
            rpair = checkCommStatus(Device, trace);
        }
    }

    return rpair;
}

pair< bool, INT > CtiPort::checkCommStatus(CtiDeviceSPtr Device, INT trace)
{
    INT status = NORMAL;
    pair< bool, INT > rpair = make_pair( false, NORMAL );

    if(!isViable())
    {
        /* set up the port */
        if( (status = openPort()) != NORMAL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error initializing Virtual Port " << getPortID() << ": \"" << getName() << "\"" << endl;
            }
        }

        rpair = make_pair( true, status );

        if(Device)
        {
            Device->setLogOnNeeded(TRUE); // Make sure this guy forgets about it.  He must reconnect himself.
        }
    }

    setPortForDevice(Device);

    return rpair;
}


CTI_PORTTHREAD_FUNC_PTR CtiPort::setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn)
{
    CTI_PORTTHREAD_FUNC_PTR oldFn = _portFunc;
    _portFunc = aFn;
    return oldFn;
}

INT CtiPort::setPortReadTimeOut(USHORT millitimeout)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

INT CtiPort::waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

INT CtiPort::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

INT CtiPort::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

bool CtiPort::isViable()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return false;
}


bool CtiPort::setPortForDevice(CtiDeviceSPtr  Device)
{
    bool bret = false;

    if(Device)
    {
        if(Device->getType() == TYPE_TAPTERM)
        {
            if(isDebugLudicrous())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port is about to communicate with a TAP device. " << Device->getName() << endl;
            }

            setLine(getBaudRate(), 7, EVENPARITY, ONESTOPBIT);
            enableXONXOFF();
        }
        else if(Device->getType() == TYPE_RTM ||
                Device->getType() == TYPE_RTC)
        {
            if(isDebugLudicrous())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port is about to communicate with RTM/RTC \"" << Device->getName() << "\"." << endl;
            }

            setLine(1200, 8, ODDPARITY, ONESTOPBIT);
            disableXONXOFF();
        }
        else
        {
            if(isDebugLudicrous())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port is about to communicate with a NON - TAP device. " << Device->getName() << endl;
            }

            if(Device->getBaudRate() && Device->getBaudRate() != getBaudRate())
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Device: " << Device->getName() << " linesettings (" << Device->getBaudRate() << ":" << Device->getBits() << "," << Device->getParity() << "," << Device->getStopBits() << ") are overriding port " << getName() << " settings!" << endl;
                }
                setLine(Device->getBaudRate(), Device->getBits(), Device->getParity(), Device->getStopBits());
            }
            else
            {
                setLine(getBaudRate(), getBits(), getParity(), getStopBits());
            }
            disableXONXOFF();
        }
    }
    else
    {
        // Ok, some default from the port!
        setLine( getBaudRate(), getBits(), getParity(), getStopBits() );
        disableXONXOFF();
    }

    return bret;
}

bool CtiPort::hasExclusions() const
{
    bool bstatus = false;
    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            bstatus = _excluded.size() != 0;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: " << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return bstatus;
}

CtiPort::exclusions CtiPort::getExclusions() const
{
    return _excluded;
}
void CtiPort::addExclusion(CtiTablePaoExclusion &paox)
{
    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 30000);

        if(ex_guard.isAcquired())
        {
            _excluded.push_back(paox);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: addExclusion()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

void CtiPort::clearExclusions()
{
    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            _excluded.clear();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: clearExclusions()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

/*
 *  Check if the passed portid is in the exclusion list?
 */
bool CtiPort::isPortExcluded(long portid) const
{
    bool bstatus = false;

    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            if(hasExclusions())
            {
                exclusions::const_iterator itr;

                for(itr = _excluded.begin(); itr != _excluded.end(); itr++)
                {
                    const CtiTablePaoExclusion &paox = *itr;
                    if(paox.getExcludedPaoId() == portid)
                    {
                        bstatus = true;
                        break;
                    }
                }
            }
        }
        else
        {
            bstatus = true;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: isPortExcluded()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return bstatus;
}

bool CtiPort::isExecuting() const
{
    return _executing;
}

void CtiPort::setExecuting(bool executing)
{
    _executing = executing;
    return;
}

bool CtiPort::isExecutionProhibited() const
{
    return(_executionProhibited.size() != 0);
}

size_t CtiPort::setExecutionProhibited(unsigned long pid)
{
    size_t cnt = 0;
    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            _executionProhibited.push_back( pid );
            cnt = _executionProhibited.size();
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: setExecutionProhibited()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return cnt;
}

bool CtiPort::removeInfiniteExclusion(unsigned long pid)
{
    bool removed = false;

    try
    {
        CtiLockGuard<CtiMutex> ex_guard(_exclusionMux, 5000);

        if(ex_guard.isAcquired())
        {
            CtiPort::prohibitions::iterator itr;

            for(itr = _executionProhibited.begin(); itr != _executionProhibited.end(); )
            {
                if(*itr == pid)
                {
                    itr = _executionProhibited.erase(itr);
                    removed = true;
                }
                else
                {
                    itr++;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  " << getName() << " unable to acquire exclusion mutex: removeInfiniteProhibit()" << endl;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return removed;
}


CtiPort& CtiPort::setProtocolWrap(INT prot)
{
    _tblPortBase.setProtocol(prot);
    return *this;
}


ULONG CtiPort::queueCount() const
{
    ULONG QueEntries = 0;

    if(_portQueue != NULL)
    {
        QueryQueue( _portQueue, &QueEntries );
    }

    return QueEntries;
}

INT CtiPort::applyPortQueue(void *ptr, void (*myFunc)(void*, void*))
{
    INT qEnt = 0;

    if(_portQueue)
    {
        qEnt = ApplyQueue(_portQueue, ptr, myFunc);
    }

    return qEnt;
}


INT CtiPort::searchPortQueue(void *ptr, BOOL (*myFunc)(void*, void*))
{
    INT qEnt = 0;

    if(_portQueue)
    {
        qEnt = SearchQueue(_portQueue, ptr, myFunc);
    }

    return qEnt;
}

INT CtiPort::searchPortQueueForConnectedDeviceUID(BOOL (*myFunc)(void*, void*))
{
    INT qEnt = 0;

    if(connected() && _portQueue)
    {
        qEnt = SearchQueue(_portQueue, (void*)getConnectedDeviceUID(), myFunc);
    }

    return qEnt;
}


INT CtiPort::readQueue( PULONG DataSize, PPVOID Data, BOOL32 WaitFlag, PBYTE Priority, ULONG* pElementCount)
{
    bool readPortQueue = true;
    static CtiTime lastQueueReportTime;
    INT status = QUEUE_READ;

    ULONG Element = getQueueSlot();

    if( _portShareQueue && getSharingStatus() && getShareToggle() )
    {
        if(!Element || gConfigParms.isTrue("PORTER_FORCE_PORT_SHARE"))
        {
            /*
             *  If we "detect" a queue slot element request, we will not pull from the shareQueue (starvation issue maybe on the shareQ)
             *  if _portShareQueue is NULL, we will not pull from the shareQueue
             *  if the SharingStatus is true, we MAY proceed
             *  if the toggle is set to true, we MAY proceed!
             */
            // We are sharing the port and the share toggle is set to select a shared queue entry.
            setShareToggle(false);    // Indicates that the next queue read should look for an UN-flagged (MSGFLG_PORT_SHARING) OM (One from Yukon that is)
            
            // Always read the _first_ element of the port share queue!
            status = ReadFrontElement(_portShareQueue, DataSize, Data, WaitFlag, Priority);

            if(!status) 
            {
                readPortQueue = false; // We pulled one from the share queue.
            }
            else if(status != ERROR_QUE_EMPTY)
            {
                if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE") & 0x10)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getName() << " was unable to read from the port share queue. Error: " << status << endl;
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " bypassed the portShareQueue due to a targeted portqueue read." << endl;
        }
    }

    if(readPortQueue && _portQueue)
    {
        setShareToggle(true);    // Indicates that the next queue read should look for a flagged (MSGFLG_PORT_SHARING) OM (One NOT from Yukon that is)
        status = ReadElementById(_portQueue, DataSize, Data, Element, WaitFlag, Priority, pElementCount);

        if(pElementCount && *pElementCount > 5000 && CtiTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << getName() << " has " << *pElementCount << " pending OUTMESS requests " << endl;
            }
            lastQueueReportTime = CtiTime() + 300;
        }
    }

    if(status == NORMAL)
    {
        setLastOMRead();
    }

    setQueueSlot(0);   // Set this back to zero every time.

    return status;
}

INT CtiPort::searchQueue( void *ptr, BOOL (*myFunc)(void*, void*), bool useFirstElement )
{
    return SearchQueue( _portQueue, ptr, myFunc, useFirstElement );
}


#ifndef  COMM_FAIL_REPORT_TIME
    #define COMM_FAIL_REPORT_TIME 300
#endif

INT CtiPort::portMaxCommFails() const
{
    return gDefaultPortCommFailCount;
}

bool CtiPort::adjustCommCounts( INT CommResult )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    bool bAdjust = false;
    bool bStateChange = false;
    bool success = (CommResult == NORMAL);
    bool isCommClassError = (GetErrorType(CommResult) == ERRTYPECOMM);      // is this a comm class error (else device attributable)

    bool isCommFail;

    CtiTime now;
    INT lastCommCount = _commFailCount;

    ++_attemptCount;

    if(success)
    {
        _commFailCount = 0;             // reset the consecutive fails.
        ++_attemptSuccessCount;
    }
    else
    {
        if(isCommClassError)
        {
            ++_attemptCommFailCount;        // This is a comm error.
            ++_commFailCount;               // These are the only errors which count towards port failures.
        }
        else
        {
            ++_attemptOtherFailCount;       // This is not a comm error must be protocol or device related.
        }
    }

    bool badtogood = ( success && lastCommCount >= portMaxCommFails() );
    bool goodtobad = ( !success && (lastCommCount < portMaxCommFails()) && (_commFailCount >= portMaxCommFails()) );

    if( goodtobad )
    {
        bStateChange = true;
    }
    else if( badtogood )
    {
        bStateChange = true;
    }

    if( bStateChange || now > _lastReport )
    {
        bAdjust = true;
        _lastReport = nextScheduledTimeAlignedOnRate(now, gConfigParms.getValueAsULong("COMM_FAIL_REPORT_TIME", 300)); // ((now - (now.seconds() % COMM_FAIL_REPORT_TIME)) + COMM_FAIL_REPORT_TIME);
    }

    return(bAdjust);
}

// Return all queue entries to the processing parent.
INT CtiPort::requeueToParent(OUTMESS *&OutMessage)
{
    INT status = NORMAL;

    if(_parentPort) // Do we have this ability??
    {
        CtiOutMessage *NewOutMessage = 0;
        setPoolAssignedGUID(0L);        // Keep us from grabbing this one!

        if(OutMessage)
        {
            // Deal with the failed OM which was passed in...
            NewOutMessage = CTIDBG_new CtiOutMessage(*OutMessage);

            NewOutMessage->Retry = 2;
            _parentPort->writeQueue( NewOutMessage->Request.GrpMsgID, sizeof(*NewOutMessage), (char *)NewOutMessage, NewOutMessage->Priority );
        }

        BYTE           ReadPriority;
        ULONG          QueEntries;
        ULONG          ReadLength;

        setQueueSlot(0);

        while(queueCount())
        {
            // Move the OM from the pool queue to the child queue.
            if( readQueue( &ReadLength, (PPVOID) &NewOutMessage, DCWW_WAIT, &ReadPriority, &QueEntries ) == NORMAL )
            {
                _parentPort->writeQueue( NewOutMessage->Request.GrpMsgID, sizeof(*NewOutMessage), (char *) NewOutMessage, NewOutMessage->Priority );
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Port " << getName() << " Moving OutMessage back to parent port " << _parentPort->getName() << endl;
                }
            }
        }

        status = RETRY_SUBMITTED;
    }

    return status;
}

bool CtiPort::waitForPost(HANDLE quitEvent, LONG timeout) const
{
    bool status = false;

    if(_postEvent != INVALID_HANDLE_VALUE)
    {
        HANDLE hWaitObjects[2];
        DWORD cnt = (quitEvent != INVALID_HANDLE_VALUE ? 2 : 1);

        ResetEvent( _postEvent );

        hWaitObjects[0] = _postEvent;
        hWaitObjects[1] = quitEvent;

        DWORD dwWaitResult = WaitForMultipleObjects( cnt, hWaitObjects, FALSE, timeout );

        switch(dwWaitResult)
        {
        case WAIT_OBJECT_0:         // This is a post... Wake up!
            {
                status = true;
                break;
            }
        case WAIT_OBJECT_0 + 1:     // This is QUIT by the way.
        case WAIT_TIMEOUT:
            {
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << dwWaitResult << endl;
                }
                break;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

void CtiPort::postEvent()
{
    setLastOMComplete();

    if(_postEvent != INVALID_HANDLE_VALUE)
    {
        SetEvent(_postEvent);
    }

    if(_parentPort)
    {
        _parentPort->postEvent();
    }
}

CtiTime CtiPort::getLastOMRead() const
{
    return _lastOMRead;
}
void CtiPort::setLastOMRead(CtiTime &atime)
{
    _lastOMRead = atime;
}
CtiTime CtiPort::getLastOMComplete() const
{
    return _lastOMComplete;
}
void CtiPort::setLastOMComplete(CtiTime &atime)
{
    _lastOMComplete = atime;
}

bool CtiPort::getDeviceQueued() const
{
    return !_devicesQueued.empty();
}

bool CtiPort::setDeviceQueued(LONG id)
{
    return _devicesQueued.insert( id ).second;
}

bool CtiPort::resetDeviceQueued(LONG id)
{
    return _devicesQueued.erase( id ) > 0;
}

void CtiPort::addDeviceQueuedWork(long deviceID, unsigned workCount)
{
    device_queue_counts::iterator iter;
    try
    {
        _criticalSection.acquire();
        if( (iter = _queuedWork.find(deviceID)) != _queuedWork.end() )
        {
            if(workCount > 0)
            {
                iter->second = workCount;
            }
        }
        else
        {
            if( workCount > 0 )
            {
                _queuedWork.insert(make_pair(deviceID, workCount));
            }
            else if(workCount == -1) // -1 means I exist, but 0 count. This device is registering.
            {
                _queuedWork.insert(make_pair(deviceID, 0));
            }
        }
        _criticalSection.release();
    }
    catch( ... )
    {
        _criticalSection.release();
    }
}

vector<long> CtiPort::getQueuedWorkDevices()
{
    CtiLockGuard<CtiCriticalSection> guard(_criticalSection);

    vector<long> retVal;

    std::transform(
        _queuedWork.begin(),
        _queuedWork.end(),
        back_inserter(retVal),
        boost::bind(&device_queue_counts::value_type::first, _1));

    return retVal;
}

void CtiPort::setPortCommunicating(bool state, DWORD ticks)
{
    try
    {
        _criticalSection.acquire();
        _communicating = state;
        _criticalSection.release();

        if( ticks > 0 )
        {
            addPortTiming(ticks);
        }
    }
    catch( ... )
    {
        _criticalSection.release();
    }
}

void CtiPort::addPortTiming(DWORD ticks)
{
    _entryMsecTime = (_entryMsecTime*4 + ticks)/5;
}

DWORD CtiPort::getPortTiming()
{
    return _entryMsecTime;
}

int CtiPort::getWorkCount(long requestID)
{
    ULONG workCount = 0;
    map< LONG, int >::iterator iter;
    try
    {
        if( requestID == 0 )
        {
            if( _communicating )
            {
                workCount++;
            }
            workCount += queueCount();

            _criticalSection.acquire();
            for each( device_queue_counts::value_type queue_count in _queuedWork )
            {
                workCount += queue_count.second;
            }
            _criticalSection.release();
        }
        else
        {
            if(_portQueue != NULL)
            {
                GetRequestCount(_portQueue, requestID, workCount);
            }
        }
    }
    catch( ... )
    {
        _criticalSection.release();
        workCount = 0;
    }

    return workCount;
}

CtiPort& CtiPort::setDevicePreload(LONG id)
{
    _devicesPreloaded.insert( id );
    return *this;
}

CtiPort& CtiPort::resetDevicePreload(LONG id)
{
    _devicesPreloaded.erase( id );
    return *this;
}

set<LONG> CtiPort::getPreloads(void)
{
    return _devicesPreloaded;
}


INT CtiPort::incQueueSubmittal(int bumpCnt, CtiTime &rwt)    // Bumps the count of submitted deviceQ entries for this 5 minute window.
{
    int index = (rwt.hour()*60 + rwt.minute()) / 5;
    _submittal.inc(index,bumpCnt);
    return _submittal.get(index);
}
INT CtiPort::incQueueProcessed(int bumpCnt, CtiTime & rwt)   // Bumps the count of processed deviceQ entries for this 5 minute window.
{
    int index = (rwt.hour()*60 + rwt.minute()) / 5;
    _processed.inc(index,bumpCnt);
    return _processed.get(index);
}
INT CtiPort::setQueueOrphans(int num, CtiTime &rwt)          // Number of queue entries remaining on device following this pass.
{
    int index = (rwt.hour()*60 + rwt.minute()) / 5;
    _orphaned.set(index,num);
    return _orphaned.get(index);
}
void CtiPort::getQueueMetrics(int index, int &submit, int &processed, int &orphan) // Return the metrics above.
{
    submit = _submittal.get(index);
    processed = _processed.get(index);
    orphan = _orphaned.get(index);
}

INT CtiPort::writeShareQueue(ULONG Request, LONG DataSize, PVOID Data, ULONG Priority, HANDLE hQuit)
{
    INT status = !NORMAL;
    ULONG QueEntries;

    if(!_portShareQueue)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        /* create the queue for this port */
        if( (status = CreateQueue (&_portShareQueue, hQuit)) != NORMAL )
        {
            CloseQueue( _portShareQueue );
            _portShareQueue = NULL;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "Error Creating Shared Queue for Port:  " << setw(2) << getPortID() << " / " << getName() << endl;
            }
        }
    }

    if(_portShareQueue)
    {
        setSharingStatus(true);   // Indicates a sharing condition.
        status = WriteQueue( _portShareQueue, Request, DataSize, Data, Priority, &QueEntries);

        if(gConfigParms.getValueAsULong("PORT_SHARE_QUEUE") & 0x00000010)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " has " << QueEntries << " elements on the port share queue " << endl;
        }
    }

    return status;
}
