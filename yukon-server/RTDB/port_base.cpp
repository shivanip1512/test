

#include <iostream>
#include <iomanip>
using namespace std;

#include "port_base.h"
#include "portsup.h"
#include "dsm2err.h"
#include "color.h"
#include "porter.h"
#include "logger.h"
#include "msg_trace.h"

#include "numstr.h"

#define SCREEN_WIDTH    80

void CtiPort::Dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Port \"" << _tblPAO.getID( ) << "\" Definition" << endl;;
    dout << " Description                       = " << getName() << endl;
    dout << " Protocol wrap                     = " << _tblPortBase.getProtocol() << endl;

    return;
}


INT CtiPort::traceIn(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev, INT ErrorCode) const
{
    INT status = NORMAL;

    RWCString msg;

    if(Xfer.doTrace(ErrorCode) &&  !(*Xfer.getInCountActual() == 0 && !ErrorCode) )
    {
        if(!isTAP())
        {
            {
                CtiTraceMsg trace;

                //  set bright yellow for the time message
                trace.setBrightYellow();
                trace.setTrace( RWTime().asString() );
                traceList.insert(trace.replicateMessage());

                //  set bright cyan for the info message
                trace.setBrightCyan();
                msg = "  P: " + CtiNumStr(getPortID()).spad(3) + " / " + getName();
                trace.setTrace(msg);
                traceList.insert(trace.replicateMessage());

                if(Dev != NULL)
                {
                    trace.setBrightCyan();
                    msg = "  D: " + CtiNumStr(Dev->getID()).spad(3) + " / " + Dev->getName();
                    trace.setTrace(msg);
                    traceList.insert(trace.replicateMessage());
                }

                if(ErrorCode)
                {
                    trace.setBrightRed();
                    msg = " IN: " + CtiNumStr(ErrorCode).spad(3) + "\n";
                }
                else
                {
                    trace.setBrightWhite();
                    msg = " IN:\n";
                }
                trace.setTrace(msg);
                traceList.insert(trace.replicateMessage());


                //  then print the formatted hex trace
                trace.setBrightMagenta();
                traceBytes(Xfer.getInBuffer(), *Xfer.getInCountActual(), trace, traceList);

                if(ErrorCode)
                {
                    char ebuff[120];
                    GetErrorString(ErrorCode, ebuff);
                    trace.setBrightRed();
                    trace.setTrace( RWCString(ebuff) );
                    traceList.insert(trace.replicateMessage());

                    trace.setNormal();
                    trace.setTrace( RWCString("\n") );
                    traceList.insert(trace.replicateMessage());
                }
            }
        }
    }

    return status;
}

INT CtiPort::traceXfer(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev, INT ErrorCode) const
{
    INT status = NORMAL;

    if(!isTAP())
    {
        if(Xfer.traceError() && ErrorCode != NORMAL)      // if Error is set, it happened on the InMessage, and we didn't print the outmessage before
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

INT CtiPort::traceOut(CtiXfer& Xfer, RWTPtrSlist< CtiMessage > &traceList, CtiDevice* Dev, INT ErrorCode) const
{
    INT status = NORMAL;
    RWCString msg;

    if(!isTAP())
    {
        if(Xfer.doTrace(ErrorCode))
        {
            {
                CtiTraceMsg trace;

                //  set bright yellow for the time message
                trace.setBrightYellow();
                trace.setTrace( RWTime().asString() );
                traceList.insert(trace.replicateMessage());

                //  set bright cyan for the info message
                trace.setBrightCyan();
                msg = "  P: " + CtiNumStr(getPortID()).spad(3) + " / " + getName();
                trace.setTrace(msg);
                traceList.insert(trace.replicateMessage());

                if(Dev != NULL)
                {
                    trace.setBrightCyan();
                    msg = "  D: " + CtiNumStr(Dev->getID()).spad(3) + " / " + Dev->getName();
                    trace.setTrace(msg);
                    traceList.insert(trace.replicateMessage());
                }

                if(ErrorCode)
                {
                    trace.setBrightRed();
                    msg = " OUT: " + CtiNumStr((short)ErrorCode).spad(3) + "\n";
                }
                else
                {
                    trace.setBrightWhite();
                    msg = " OUT:\n";
                }
                trace.setTrace(msg);
                traceList.insert(trace.replicateMessage());

                //  then print the formatted hex trace
                trace.setBrightGreen();
                traceBytes(Xfer.getOutBuffer(), Xfer.getOutCount(), trace, traceList);
            }
        }
    }

    return status;
}

INT CtiPort::traceBytes(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const
{
    return generateTraces( Message, Length, trace, traceList);
}

INT CtiPort::logBytes(BYTE *Message, ULONG Length) const
{
    INT status = NORMAL;
    ULONG i;
    ULONG width = 1;
    ULONG offset = 0;

    RWTPtrSlist< CtiMessage > traceList;
    CtiTraceMsg trace;

    generateTraces( Message, Length, trace, traceList);

    _portLog << endl;

    while( traceList.entries() )
    {
        CtiTraceMsg *pTrace = (CtiTraceMsg*)traceList.get();
        _portLog << pTrace->getTrace();
        delete pTrace;
    }

    _portLog << endl;

    return status;
}

INT CtiPort::writeQueue(ULONG Request, LONG DataSize, PVOID Data, ULONG Priority, HANDLE hQuit)
{
#define DEFAULT_QUEUE_GRIPE_POINT 10
    int status = NORMAL;
    ULONG QueEntries;
    static ULONG QueueGripe = DEFAULT_QUEUE_GRIPE_POINT;

#ifdef DEBUG
    OUTMESS *OutMessage = (OUTMESS*)Data;
    if(OutMessage->DeviceID == 0 && OutMessage->TargetID == 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  A thread just dumped an untraceable OUTMESS request into a port queue" << endl;
            dout << "  You will undoubtedly have difficulties soon" << endl;
        }
    }
#endif

    if(verifyPortIsRunnable( hQuit ) == NORMAL)
    {
        if(_portQueue != NULL)
        {
            status = WriteQueue(_portQueue, Request, DataSize, Data, Priority, &QueEntries);

            if(QueEntries > QueueGripe)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getName() << " has just received a new port queue entry.  There are " << QueEntries << " pending." << endl;
                }

                ULONG gripemore = QueueGripe * 2;

                QueueGripe = QueueGripe + ( gripemore < 1000 ? gripemore : 1000);
            }
            else if(QueEntries < DEFAULT_QUEUE_GRIPE_POINT)
            {
                QueueGripe = DEFAULT_QUEUE_GRIPE_POINT;
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

ULONG CtiPort::getDelay(int Offset) const
{
    return _tblPortTimings.getDelay(Offset);
}

CtiPort& CtiPort::setDelay(int Offset, int D)
{
    _tblPortTimings.setDelay(Offset, D);
    return *this;
}

INT CtiPort::queueInit(HANDLE hQuit)
{
    INT status = NORMAL;

    LockGuard gd(monitor());

    if(_portQueue == NULL)
    {
        /* create the queue for this port */
        if( (status = CreateQueue (&_portQueue, QUE_PRIORITY, hQuit)) != NORMAL )
        {
            CloseQueue( _portQueue );
            _portQueue = NULL;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "Error Creating Queue for Port:  " << setw(2) << getPortID() << " / " << getName() << endl;
            }
        }
    }

    return status;
}

INT CtiPort::queueDeInit()
{
    INT status = NORMAL;

    RWMutexLock::LockGuard( getMux() );

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
                        dout << RWTime() << " No port thread function defined" << endl;
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
            dout << RWTime() << " **** RW EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << e.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << endl;
        }
    }


    return status;
}

void CtiPort::DecodeDatabaseReader(RWDBReader &rdr)
{
    LockGuard gd(monitor());

    _tblPAO.DecodeDatabaseReader(rdr);
    _tblPortBase.DecodeDatabaseReader(rdr);
    _tblPortSettings.DecodeDatabaseReader(rdr);       // get the base class handled
    _tblPortTimings.DecodeDatabaseReader(rdr);       // get the base class handled

    setValid();

    if(gLogPorts && !_portLog.isRunning())
    {
        {
            RWCString of(getName() + "_");

            _portLog.setToStdOut(false);  // Not to std out.
            _portLog.setOutputPath(gLogDirectory.data());
            _portLog.setOutputFile( of.data() );
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

INT CtiPort::outInMess(CtiXfer& Xfer, CtiDevice *Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT   status = NORMAL;

    LockGuard gd(monitor());

    *(Xfer.getInCountActual()) = 0L;    // Make sure that any error on the outMess does not affect a state machine!

    if( NORMAL == (status = outMess(Xfer, Dev, traceList)) )
    {
        status = inMess(Xfer, Dev, traceList);
    }

    return status;
}



CtiPort::CtiPort() :
_portFunc(0),
_portQueue(NULL),
_connectedDevice(0L),
_connectedDeviceUID(-1),
_lastBaudRate(0),
_tapPort(FALSE)
{
}

CtiPort::CtiPort(const CtiPort& aRef) :
_portFunc(0)
{
    *this = aRef;
}


CtiPort& CtiPort::operator=(const CtiPort& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _traceListOffset = aRef._traceListOffset;
        setConnectedDeviceUID( aRef.getConnectedDeviceUID() );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " FINISH THIS " << endl;
        }
    }
    return *this;
}

LONG CtiPort::getConnectedDevice() const          { return _connectedDevice;}
CtiPort& CtiPort::setConnectedDevice(const LONG d)
{
    _connectedDevice = d;
    return *this;
}

INT CtiPort::getLastBaudRate() const             { return _lastBaudRate;}
CtiPort& CtiPort::setLastBaudRate(const INT r)
{
    _lastBaudRate = r;
    return *this;
}

CtiPort &CtiPort::setBaudRate(INT baudRate)
{
    if(baudRate)
        _tblPortSettings.setBaudRate(baudRate);

    return *this;
}



RWCString& CtiPort::getPortNameWas()                    { return _portNameWas;}
CtiPort& CtiPort::setPortNameWas(const RWCString str)
{
    _portNameWas = str;
    return *this;
}

BOOL CtiPort::isTAP() const                       { return _tapPort;}
CtiPort& CtiPort::setTAP(BOOL b)
{
    _tapPort = b;
    return *this;
}

INT CtiPort::connectToDevice(CtiDevice *Device, INT trace)
{
    INT status     = NORMAL;
    ULONG DeviceCRC = Device->getUniqueIdentifier();

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
INT CtiPort::disconnect(CtiDevice *Device, INT trace)
{
    if(Device != NULL)
    {
        Device->setLogOnNeeded(TRUE);
    }

    setConnectedDevice(-1L);

    return NORMAL;
}

CtiPort& CtiPort::setShouldDisconnect(BOOL b)               { return *this;}
BOOL CtiPort::shouldDisconnect() const                      { return FALSE;}
INT CtiPort::reset(INT trace)                               { return NORMAL;}
INT CtiPort::setup(INT trace)                               { return NORMAL;}
INT CtiPort::close(INT trace)                               { return NORMAL;}


/* virtuals to make the world all fat and happy */
INT       CtiPort::ctsTest() const            { return TRUE;}
INT       CtiPort::dcdTest() const            { return TRUE;}

INT       CtiPort::baudRate(INT rate)         { return NORMAL;}
INT       CtiPort::lowerRTS() const           { return NORMAL;}
INT       CtiPort::raiseRTS() const           { return NORMAL;}
INT       CtiPort::lowerDTR() const           { return NORMAL;}
INT       CtiPort::raiseDTR() const           { return NORMAL;}

INT       CtiPort::inClear() const            { return NORMAL;}
INT       CtiPort::outClear() const           { return NORMAL;}

INT       CtiPort::byteTime(ULONG bytes) const      { return 0;}


bool      CtiPort::needsReinit() const              { return getHandle() == (HANDLE)NULL;}
HANDLE    CtiPort::getHandle() const                { return NULL;}
INT       CtiPort::getIPPort() const                { return -1;}
RWCString CtiPort::getIPAddress() const             { return RWCString("");}
RWCString CtiPort::getPhysicalPort() const          { return RWCString("");}
RWCString CtiPort::getModemInit() const             { return RWCString("");}

void CtiPort::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    _tblPAO.getSQL(db, keyTable, selector);
    CtiTablePortBase::getSQL(db, keyTable, selector);
    CtiTablePortSettings::getSQL(db, keyTable, selector);
    CtiTablePortTimings::getSQL(db, keyTable, selector);
}

HCTIQUEUE&  CtiPort::getPortQueueHandle()
{
    return _portQueue;
}

RWThreadFunction& CtiPort::getPortThread()
{
    return _portThread;
}


INT CtiPort::generateTraces(BYTE *Message, ULONG Length, CtiTraceMsg &trace, RWTPtrSlist< CtiMessage > &traceList) const
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
            trace.setTrace( RWCString( buffer ) );
            traceList.insert(trace.replicateMessage());

            buffer[0] = '\0';
            offset = 0;
            width = 1;
        }

        /* print byte in hex with leading zero */
        if(width + 3 > SCREEN_WIDTH)
        {
            sprintf(&buffer[offset], "%02x", (BYTE)Message[i]);
            offset += 2;
            width += 2;
        }
        else
        {
            sprintf(&buffer[offset], "%02x ", (BYTE)Message[i]);
            offset += 3;
            width += 3;
        }
    }

    trace.setTrace( RWCString( buffer ) + RWCString("\n") );
    traceList.insert(trace.replicateMessage());

    return status;
}


void CtiPort::fileTraces(RWTPtrSlist< CtiMessage > &traceList) const
{
    if(gLogPorts)
    {
        CtiLockGuard<CtiLogger> portlog_guard(_portLog);
        for(size_t i = 0; i < traceList.entries(); i++)
        {
            RWCString trace = ((CtiTraceMsg*)traceList.at(i))->getTrace();

            _portLog << trace;
        }
        _portLog << endl;
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


INT CtiPort::verifyPortStatus()
{
    INT         status   = NORMAL;

    if(needsReinit() && !isDialup())
    {
        if( NORMAL != (status = init()) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error initializing Virtual Port " << getPortID() << ": \"" << getName() << "\"" << endl;
            }
        }
    }

    return status;
}

CTI_PORTTHREAD_FUNC_PTR CtiPort::setPortThreadFunc(CTI_PORTTHREAD_FUNC_PTR aFn)
{
    CTI_PORTTHREAD_FUNC_PTR oldFn = _portFunc;
    _portFunc = aFn;
    return oldFn;
}


