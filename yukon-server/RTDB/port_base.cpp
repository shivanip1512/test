#include "precompiled.h"

#include "cparms.h"
#include "port_base.h"
#include "prot_emetcon.h"
#include "error.h"
#include "color.h"
#include "porter.h"
#include "logger.h"
#include "msg_trace.h"
#include "dllbase.h"

#include "numstr.h"

#include "module_util.h"

#include <boost/range/adaptor/map.hpp>

#define SCREEN_WIDTH    80

#define DEBUG_INPUT_FROM_SCADA 0x00000010

#define DEFAULT_QUEUE_GRIPE_POINT 50

using namespace std;

BOOL APIENTRY DllMain(HANDLE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
    switch( ul_reason_for_call )
    {
        case DLL_PROCESS_ATTACH:
        {
            Cti::identifyLibrary(CompileInfo);
            break;
        }
        case DLL_THREAD_ATTACH:
        {
            break;
        }
        case DLL_THREAD_DETACH:
        {
            break;
        }
        case DLL_PROCESS_DETACH:
        {
            break;
        }
    }

    return TRUE;
}

YukonError_t CtiPort::traceIn(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr  Dev, YukonError_t ErrorCode) const
{
    YukonError_t status = ClientErrors::None;

    string msg;

    try
    {
        if(Xfer.doTrace(ErrorCode) &&  !(Xfer.getInCountActual() == 0 && !ErrorCode) )
        {
            if(!isTAP() || (Dev && !Dev->isTAP()))
            {
                {
                    CtiTraceMsg trace;
                    SYSTEMTIME stm;
                    GetLocalTime(&stm);

                    //  set bright yellow for the time message
                    trace.setBrightYellow();
                    trace.setTrace( Cti::formatSystemTime(stm) );
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
                        if( ErrorCode == ClientErrors::PortSimulated )
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

                    if(ErrorCode && ErrorCode != ClientErrors::PortSimulated)
                    {
                        trace.setBrightRed();
                        trace.setTrace( CtiError::GetErrorString(ErrorCode) );
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}

YukonError_t CtiPort::traceXfer(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr  Dev, YukonError_t ErrorCode) const
{
    YukonError_t status = ClientErrors::None;

    if(!isTAP() || (Dev && !Dev->isTAP()))
    {
        if(Xfer.traceError() && ErrorCode && ErrorCode != ClientErrors::PortSimulated)      // if Error is set, it happened on the InMessage, and we didn't print the outmessage before
        {
            status = traceOut(Xfer, traceList, Dev, ErrorCode);
        }

        if( ClientErrors::None == status)
        {
            status = traceIn(Xfer, traceList, Dev, ErrorCode);
        }

    }

    return status;
}

YukonError_t CtiPort::traceOut(CtiXfer& Xfer, list< CtiMessage* > &traceList, CtiDeviceSPtr Dev, YukonError_t ErrorCode) const
{
    YukonError_t status = ClientErrors::None;
    string msg;

    try
    {
        if(!isTAP() || (Dev && !Dev->isTAP()))
        {
            if(Xfer.doTrace(ErrorCode))
            {
                {
                    CtiTraceMsg trace;
                    SYSTEMTIME stm;
                    GetLocalTime(&stm);

                    //  set bright yellow for the time message
                    trace.setBrightYellow();
                    trace.setTrace( Cti::formatSystemTime(stm) );
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}

INT CtiPort::writeQueue(OUTMESS *OutMessage, HANDLE hQuit)
{
    return writeQueueWithPriority(OutMessage, OutMessage->Priority, hQuit);
}

INT CtiPort::writeQueueWithPriority(OUTMESS *OutMessage, int priority, HANDLE hQuit)
{
    int status = ClientErrors::None;
    ULONG QueEntries;

    if(OutMessage &&
       OutMessage->DeviceID == 0 && OutMessage->TargetID == 0)
    {
        CTILOG_DEBUG(dout, "A thread just dumped an untraceable OUTMESS request into a port queue."<<
                endl <<"You will undoubtedly have difficulties soon"
                );
    }

    if(OutMessage &&
       OutMessage->HeadFrame[0] == 0x02 && OutMessage->HeadFrame[1] == 0xe0 &&
       OutMessage->TailFrame[0] == 0xea && OutMessage->TailFrame[1] == 0x03)
    {
        if(OutMessage->Sequence == Cti::Protocols::EmetconProtocol::Scan_LoadProfile)
        {
            if( isDebugLudicrous() )
            {
                CTILOG_DEBUG(dout, "Cleaning Excess LP Entries for TargetID "<< OutMessage->TargetID);
            }

            // Remove any other Load Profile Queue Entries for this Queue.
            CleanQueue( _portQueue, (void*)OutMessage, findLPRequestEntries, cleanupOutMessages );
        }
    }

    if(verifyPortIsRunnable( hQuit ) == ClientErrors::None)
    {
        if(OutMessage && OutMessage->MessageFlags & MessageFlag_PortSharing)        // This OM has been tagged as a sharing OM.
        {
            int blockTime = gConfigParms.getValueAsInt("PORT_SHARE_BLOCKING_DURATION", 0);

            if( !blockTime || ((_lastWrite + blockTime) < CtiTime::now()) )
            {
                status = writeShareQueue(OutMessage->Request.GrpMsgID, sizeof(OUTMESS), OutMessage, priority, &QueEntries);
            }
            else
            {
                CTILOG_ERROR(dout, "port \""<< getName() <<"\" has blocked an incoming OM from its port share (_lastWrite = "<< _lastWrite <<", blockTime = "<< blockTime << ")");

                status = ClientErrors::QueueWrite;
            }
        }
        else if(_portQueue != NULL)
        {
            if(_postEvent != INVALID_HANDLE_VALUE)
            {
                SetEvent( _postEvent );                 // Just in case someone is sleeping at the wheel
            }

            _lastWrite = CtiTime::now();

            status = WriteQueue( _portQueue, OutMessage->Request.GrpMsgID, sizeof(OUTMESS), OutMessage, priority, &QueEntries);

            if(QueEntries >= _queueGripe)
            {
                ULONG gripemore = _queueGripe * 2;
                _queueGripe = _queueGripe + ( gripemore < 1000 ? gripemore : 1000);

                CTILOG_INFO(dout, getName() <<" has just received a new port queue entry.  There are "<< QueEntries <<" pending.");
            }
            else if(QueEntries < DEFAULT_QUEUE_GRIPE_POINT)
            {
                _queueGripe = DEFAULT_QUEUE_GRIPE_POINT;
            }
        }
        else
        {
            status = ClientErrors::QueueWrite;
        }
    }
    else
    {
        status = ClientErrors::QueueWrite;
    }

    return status;
}

INT CtiPort::queueInit(HANDLE hQuit)
{
    INT status = ClientErrors::None;

    if(_portQueue == NULL)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        /* create the queue for this port */
        if( status = CreateQueue (&_portQueue, hQuit) )
        {
            CloseQueue( _portQueue );
            _portQueue = NULL;

            CTILOG_ERROR(dout, "Could not create Queue for Port:  "<< setw(2) << getPortID() <<" / "<< getName());
        }
    }

    return status;
}

INT CtiPort::queueDeInit()
{
    INT status = ClientErrors::None;

    CtiLockGuard<CtiMutex> guard(_classMutex);

    /* create the queue for this port */
    CloseQueue( _portQueue );
    _portQueue = NULL;

    status = ClientErrors::Abnormal;

    return status;
}

INT CtiPort::verifyPortIsRunnable( HANDLE hQuit )
{
    try
    {
        if( _tblPAO.isInhibited() )
        {
            return ClientErrors::PortInhibited;
        }

        if( const int queueStatus = queueInit(hQuit) )
        {
            return queueStatus;
        }

        if( _portThread.get_id() == boost::thread::id() )
        {
            //  thread wasn't running
            if( ! _portFunc )
            {
                CTILOG_ERROR(dout, "No port thread function defined");

                return ClientErrors::Abnormal;
            }

            _portThread = boost::thread(_portFunc, (void*)getPortID());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}

void CtiPort::DecodeDialableDatabaseReader(Cti::RowReader &rdr)
{
    CTILOG_ERROR(dout, "DecodeDialableDatabaseReader not defined for " << getName());

    return;
}

void CtiPort::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    _tblPAO.DecodeDatabaseReader(rdr);
    _tblPortBase.DecodeDatabaseReader(rdr);

    setValid();
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
}

void CtiPort::startLog()
{
    if(gLogPorts && !_portLogManager.isStarted())
    {
        const string comlogdir = gLogDirectory + "\\Comm";

        // Create a subdirectory called Comm beneath Log.
        CreateDirectoryEx( gLogDirectory.c_str(), comlogdir.c_str(), NULL);

        _portLogManager.setOutputPath(comlogdir);
        _portLogManager.setOutputFile(getName());
        _portLogManager.reloadSettings();
        _portLogManager.start();
    }
}

void CtiPort::reloadLogger()
{
    if(_portLogManager.isStarted())
    {
        _portLogManager.reloadSettings();
    }
}

YukonError_t CtiPort::outInMess(CtiXfer& Xfer, CtiDeviceSPtr Dev, list< CtiMessage* > &traceList)
{
    YukonError_t status = ClientErrors::None;

    Xfer.setInCountActual((ULONG)0L);    // Make sure that any error on the outMess does not affect a state machine!

    if( ClientErrors::None == (status = outMess(Xfer, Dev, traceList)) )
    {
        status = inMess(Xfer, Dev, traceList);
    }

    return status;
}

namespace {

std::atomic<long> portCount = 0;

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
_entryMsecTime(0),
_portLogManager("port" + std::to_string(++portCount))
{
    _postEvent = CreateEvent( NULL, TRUE, FALSE, NULL);

    _portLogManager.setToStdOut     ( false );  // Not to std out - traces are logged in ANSI color to the console by traceBytes().
    _portLogManager.setOwnerInfo    ( CompileInfo );
    _portLogManager.setOutputFormat ( Cti::Logging::LogFormat_CommLog );

    _portLog = _portLogManager.getLogger();
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
    CTILOG_ERROR(dout, "Method undefined for port "<< getName() <<" of type" << getType());

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

YukonError_t CtiPort::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    YukonError_t status = ClientErrors::None;
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
        Device->setLogOnNeeded(true);
    }

    setConnectedDevice(0L);
    setConnectedDeviceUID(-1);

    return ClientErrors::None;
}

CtiPort& CtiPort::setShouldDisconnect(BOOL b)
{
    return *this;
}
BOOL CtiPort::shouldDisconnect() const
{
    return FALSE;
}
YukonError_t CtiPort::reset(INT trace)
{
    return ClientErrors::None;
}
YukonError_t CtiPort::setup(INT trace)
{
    return ClientErrors::None;
}
INT CtiPort::close(INT trace)
{
    return ClientErrors::None;
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
    return ClientErrors::None;
}
INT       CtiPort::raiseRTS()
{
    return ClientErrors::None;
}
INT       CtiPort::lowerDTR()
{
    return ClientErrors::None;
}
INT       CtiPort::raiseDTR()
{
    return ClientErrors::None;
}

YukonError_t CtiPort::inClear()
{
    return ClientErrors::None;
}
INT       CtiPort::outClear()
{
    return ClientErrors::None;
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

INT CtiPort::traceBytes(const BYTE *Message, ULONG Length, CtiTraceMsg &trace, list< CtiMessage* > &traceList)
{
    INT status = ClientErrors::None;
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
    if (gLogPorts && ! traceList.empty())
    {
        Cti::StreamBuffer output;

        const CtiMessage *lastMessage = traceList.back();

        for each( const CtiMessage *msg in traceList )
        {
            const CtiTraceMsg* trace = static_cast<const CtiTraceMsg*>(msg);
            output << trace->getTrace();
            if(trace->isEnd() && msg != lastMessage)
            {
                output << endl;
            }
        }

        CTILOG_INFO(_portLog, output);
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


pair< bool, YukonError_t > CtiPort::verifyPortStatus(CtiDeviceSPtr Device, INT trace)
{
    pair< bool, YukonError_t > rpair = make_pair( false, ClientErrors::None );
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

pair< bool, YukonError_t > CtiPort::checkCommStatus(CtiDeviceSPtr Device, INT trace)
{
    YukonError_t status = ClientErrors::None;
    pair< bool, YukonError_t > rpair = make_pair( false, ClientErrors::None );

    if(!isViable())
    {
        /* set up the port */
        if( status = openPort() )
        {
            CTILOG_ERROR(dout, "Could not initialize Virtual Port "<< getPortID() <<": \""<< getName() << "\"");
        }

        rpair = make_pair( true, status );

        if(Device)
        {
            Device->setLogOnNeeded(true); // Make sure this guy forgets about it.  He must reconnect himself.
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

YukonError_t CtiPort::setPortReadTimeOut(USHORT millitimeout)
{
    CTILOG_ERROR(dout, "function unimplemented");

    return ClientErrors::None;
}

YukonError_t CtiPort::waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    CTILOG_ERROR(dout, "function unimplemented");

    return ClientErrors::None;
}

INT CtiPort::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    CTILOG_ERROR(dout, "function unimplemented");

    return ClientErrors::None;
}

INT CtiPort::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    CTILOG_ERROR(dout, "function unimplemented");

    return ClientErrors::None;
}

bool CtiPort::isViable()
{
    CTILOG_ERROR(dout, "function unimplemented");

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
                CTILOG_DEBUG(dout, "Port is about to communicate with a TAP device. "<< Device->getName());
            }

            setLine(getBaudRate(), 7, EVENPARITY, ONESTOPBIT);
            enableXONXOFF();
        }
        else if(Device->getType() == TYPE_RTM ||
                Device->getType() == TYPE_RTC)
        {
            if(isDebugLudicrous())
            {
                CTILOG_DEBUG(dout, "Port is about to communicate with RTM/RTC \"" << Device->getName() << "\".");
            }

            setLine(1200, 8, ODDPARITY, ONESTOPBIT);
            disableXONXOFF();
        }
        else
        {
            if(isDebugLudicrous())
            {
                CTILOG_DEBUG(dout, "Port is about to communicate with a NON - TAP device. " << Device->getName());
            }

            if(Device->getBaudRate() && Device->getBaudRate() != getBaudRate())
            {
                CTILOG_WARN(dout, "Device: "<< Device->getName() <<" linesettings ("<< Device->getBaudRate() <<":"<< Device->getBits() <<","<< Device->getParity() <<","<< Device->getStopBits() <<") are overriding port "<< getName() <<" settings!");

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
            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
            CTILOG_ERROR(dout, getName() <<" - unable to acquire exclusion mutex");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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


int CtiPort::readQueue( PULONG DataSize, PPVOID Data, BOOL32 WaitFlag, PBYTE Priority, ULONG* pElementCount)
{
    bool readPortQueue = true;
    static CtiTime lastQueueReportTime;
    int status = ClientErrors::QueueRead;

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
                if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE") & DEBUG_INPUT_FROM_SCADA)
                {
                    CTILOG_DEBUG(dout, getName() <<" was unable to read from the port share queue. Error: "<< status);
                }
            }
        }
        else
        {
            CTILOG_INFO(dout, "bypassed the portShareQueue due to a targeted portqueue read.");
        }
    }

    if(readPortQueue && _portQueue)
    {
        setShareToggle(true);    // Indicates that the next queue read should look for a flagged (MSGFLG_PORT_SHARING) OM (One NOT from Yukon that is)
        status = ReadElementById(_portQueue, DataSize, Data, Element, WaitFlag, Priority, pElementCount);

        if(pElementCount && *pElementCount > 5000 && CtiTime() > lastQueueReportTime)  // Ok, we may have an issue here....
        {
            CTILOG_INFO(dout, "Port "<< getName() <<" has "<< *pElementCount <<" pending OUTMESS requests");

            lastQueueReportTime = CtiTime() + 300;
        }
    }

    if(status == ClientErrors::None)
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


INT CtiPort::portMaxCommFails() const
{
    return gDefaultPortCommFailCount;
}

// Return all queue entries to the processing parent.
YukonError_t CtiPort::requeueToParent(OUTMESS *&OutMessage)
{
    YukonError_t status = ClientErrors::None;

    if(_parentPort) // Do we have this ability??
    {
        CtiOutMessage *NewOutMessage = 0;
        setPoolAssignedGUID(0L);        // Keep us from grabbing this one!

        if(OutMessage)
        {
            // Deal with the failed OM which was passed in...
            NewOutMessage = CTIDBG_new CtiOutMessage(*OutMessage);

            NewOutMessage->Retry = 2;
            _parentPort->writeQueue( NewOutMessage );
        }

        BYTE           ReadPriority;
        ULONG          QueEntries;
        ULONG          ReadLength;

        setQueueSlot(0);

        while(queueCount())
        {
            // Move the OM from the pool queue to the child queue.
            if( readQueue( &ReadLength, (PPVOID) &NewOutMessage, DCWW_WAIT, &ReadPriority, &QueEntries ) == ClientErrors::None )
            {
                _parentPort->writeQueue( NewOutMessage );

                CTILOG_INFO(dout, "Port "<< getName() <<" Moving OutMessage back to parent port "<< _parentPort->getName());
            }
        }

        status = ClientErrors::RetrySubmitted;
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
                CTILOG_ERROR(dout, "WaitForMultipleObjects returned "<< dwWaitResult);

                break;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "_postEvent handle is invalid");
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

void CtiPort::addDeviceQueuedWork(long deviceID, int workCount)
{
    device_queue_counts::iterator iter;

    CtiLockGuard<CtiCriticalSection> guard(_criticalSection);
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
}

vector<long> CtiPort::getQueuedWorkDevices()
{
    CtiLockGuard<CtiCriticalSection> guard(_criticalSection);

    return boost::copy_range<vector<long>>(
        _queuedWork
            | boost::adaptors::map_keys);
}

void CtiPort::setPortCommunicating(bool state, DWORD ticks)
{
    {
        CtiLockGuard<CtiCriticalSection> guard(_criticalSection);
        _communicating = state;
    }

    if( ticks > 0 )
    {
        addPortTiming(ticks);
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

    if( requestID == 0 )
    {
        if( _communicating )
        {
            workCount++;
        }
        workCount += queueCount();

        {
            CtiLockGuard<CtiCriticalSection> guard(_criticalSection);
            for each( device_queue_counts::value_type queue_count in _queuedWork )
            {
                if (queue_count.second > 0)
                {
                    workCount += queue_count.second;
                }
            }
        }
    }
    else if(_portQueue != NULL)
    {
        GetRequestCount(_portQueue, requestID, workCount);
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


INT CtiPort::writeShareQueue(ULONG Request, LONG DataSize, PVOID Data, ULONG Priority, HANDLE hQuit)
{
    INT status = ClientErrors::Abnormal;
    ULONG QueEntries;

    if(!_portShareQueue)
    {
        CtiLockGuard<CtiMutex> guard(_classMutex);

        /* create the queue for this port */
        if( status = CreateQueue (&_portShareQueue, hQuit) )
        {
            CloseQueue( _portShareQueue );
            _portShareQueue = NULL;

            CTILOG_ERROR(dout, "Could not create Shared Queue for Port:  "<< setw(2) << getPortID() <<" / "<< getName());
        }
    }

    if(_portShareQueue)
    {
        setSharingStatus(true);   // Indicates a sharing condition.
        status = WriteQueue( _portShareQueue, Request, DataSize, Data, Priority, &QueEntries);

        if(gConfigParms.getValueAsULong("PORT_SHARE_QUEUE") & DEBUG_INPUT_FROM_SCADA)
        {
            CTILOG_DEBUG(dout, getName() <<" has "<< QueEntries <<" elements on the port share queue");;
        }
    }

    return status;
}
