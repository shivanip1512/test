
#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------
    Filename:
         port_direct.cpp

    Programmer:
         Corey G. Plender

    Description:
        Defines functions related to retrieving port data from a rdbms and
        mapping it into C style structs.

    Initial Date:  1/11/99, 07/07/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;

#include "port_direct.h"
#include "dllbase.h"

CtiTablePortLocalSerial CtiPortDirect::getLocalSerial() const
{
    return _localSerial;
}

CtiTablePortLocalSerial& CtiPortDirect::getLocalSerial()
{
    return _localSerial;
}

CtiPortDirect& CtiPortDirect::setLocalSerial(const CtiTablePortLocalSerial& aRef)
{
    _localSerial = aRef;
    return *this;
}

INT CtiPortDirect::openPort()
{
    INT      status = NORMAL;
    ULONG    Result, i;

    if( isViable() )
    {
        close(FALSE);
    }

    if(isInhibited())
    {
        status = PORTINHIBITED;
    }
    else
    {
        if(CTIOpen ((char*)(_localSerial.getPhysicalPort().data()),
                    &getHandle(),
                    &Result,
                    0L,
                    FILE_NORMAL,
                    FILE_OPEN,
                    OPEN_ACCESS_READWRITE | OPEN_SHARE_DENYREADWRITE | OPEN_FLAGS_WRITE_THROUGH,
                    0L) || Result != 1)
        {
            close(false);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << getName() << " *** ERROR *** acquiring port handle on " << _localSerial.getPhysicalPort() << endl;
            }

            return(BADPORT);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << getName() << " acquiring port handle" << endl;
        }


        /* load _dcb and set the default DCB/COMMTIMEOUTS info for the port */
        initPrivateStores();

        /* set the baud rate bits parity etc! on the port */
        if((i = setLine()) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the Read Timeout for the port */
        if((i = setPortReadTimeOut(1000)) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the write timeout for the port */
        if((i = setPortWriteTimeOut(1000)) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* See if we need to use 7E1 */
        if(isTAP())
        {
            setLine(0, 7, EVENPARITY, ONESTOPBIT);
            enableXONXOFF();
        }

        /* Lower RTS */
        lowerRTS();

        /* Raise DTR */
        raiseDTR();

        #if 1
        if((status = reset(true)) != NORMAL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error resetting port on " << getName() << endl;
        }

        /* set the modem parameters */
        if((status = setup(true)) != NORMAL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Error setting port on " << getName() << endl;
        }

        #else
        if(_dialout && isViable())
        {
            if((status = reset(true)) != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error resetting port for dialup on " << getName() << endl;
            }

            /* set the modem parameters */
            if((status = setup(true)) != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error setting port for dialup modem on " << getName() << endl;
            }
        }
        else if(_dialin)
        {
            enableRTSCTS();
            _dialin->getModem().setPort(this);
            _dialin->getModem().waitForOK(500, "OK" );
            _dialin->getModem().reset();
            _dialin->getModem().sendString("AT&F&K0&M0&N6\r");
            _dialin->getModem().setAutoAnswerRingCount(3);
        }
        #endif
    }

    return status;
}

HANDLE CtiPortDirect::getHandle() const
{
    return _portHandle;
}

HANDLE& CtiPortDirect::getHandle()
{
    return _portHandle;
}

HANDLE* CtiPortDirect::getHandlePtr()
{
    return &_portHandle;
}

CtiPortDirect& CtiPortDirect::setHandle(const HANDLE& hdl)
{
    _portHandle = hdl;
    return *this;
}

INT CtiPortDirect::setLine(INT rate, INT bits, INT parity, INT stopbits)
{
    INT r = getTablePortSettings().getBaudRate();

    if(rate != 0)
    {
        r = rate;
    }

    _dcb.BaudRate  = r;
    _dcb.ByteSize  = bits;
    _dcb.Parity    = parity;
    _dcb.StopBits  = stopbits;

    return (SetCommState(_portHandle, &_dcb) ? NORMAL : SYSTEM);
}

INT CtiPortDirect::byteTime(ULONG bytes) const
{
    DOUBLE msbits = (10000 * bytes);
    DOUBLE mytime = ( msbits / (DOUBLE)getTablePortSettings().getBaudRate() );

    return(int) (mytime / 1000.0) + 2;
}


INT CtiPortDirect::ctsTest() const
{
    return(isCTS());
}

INT CtiPortDirect::dcdTest() const
{
    return(isDCD());
}

INT CtiPortDirect::lowerRTS()
{
    #if 1
    return (EscapeCommFunction(_portHandle, CLRRTS) ? NORMAL : SYSTEM);
    #else
    _dcb.fRtsControl =  RTS_CONTROL_DISABLE;
    return (SetCommState( _portHandle, &_dcb ) ? NORMAL : SYSTEM);
    #endif
}

INT CtiPortDirect::raiseRTS()
{
    #if 1
    return (EscapeCommFunction(_portHandle, SETRTS) ? NORMAL : SYSTEM);
    #else
    _dcb.fRtsControl =  RTS_CONTROL_ENABLE;
    return (SetCommState( _portHandle, &_dcb ) ? NORMAL : SYSTEM);
    #endif
}

INT CtiPortDirect::lowerDTR()
{
    #if 1
    return (EscapeCommFunction(_portHandle, CLRDTR) ? NORMAL : SYSTEM);
    #else
    _dcb.fDtrControl =  DTR_CONTROL_DISABLE;
    return (SetCommState( _portHandle, &_dcb ) ? NORMAL : SYSTEM);
    #endif
}

INT CtiPortDirect::raiseDTR()
{
    #if 1
    return (EscapeCommFunction(_portHandle, SETDTR) ? NORMAL : SYSTEM);
    #else
    _dcb.fDtrControl =  DTR_CONTROL_ENABLE;
    return (SetCommState( _portHandle, &_dcb ) ? NORMAL : SYSTEM);
    #endif
}

INT CtiPortDirect::inClear()
{
    return (PurgeComm(_portHandle, PURGE_RXCLEAR) ? NORMAL : SYSTEM );
}

INT CtiPortDirect::outClear()
{
    return (PurgeComm(_portHandle, PURGE_TXCLEAR) ? NORMAL : SYSTEM );
}

INT CtiPortDirect::inMess(CtiXfer& Xfer, CtiDeviceBase *Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT      status      = NORMAL;

    // 091201 CGP CCH  // BYTE     SomeMessage[300];
    ULONG    DCDCount    = 0;
    ULONG    SomeRead    = 0;
    ULONG    byteCount   = 0;


    BYTE     *Message    = Xfer.getInBuffer();      // Local alias for ease of use!

    Xfer.setInCountActual( (ULONG)0 );     // Mark it as zero to prevent any "lies"

    if(Xfer.getNonBlockingReads())         // We need to get all that are out there.
    {
        int bytesavail = 0;
        int lpcnt = 0;
        ULONG expected = Xfer.getInCountExpected();

        while( Xfer.getInTimeout() * 4 >= lpcnt++ )  // Must do this at least once.
        {
            Sleep(250);

            bytesavail = 0;
            bytesavail = getPortInQueueCount();

            if(0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "   There are " << bytesavail << " on the port..  I wanted " << expected << "  I waited " << lpcnt << " 1/20 seconds " << endl;
            }

            if( (expected > 0 && bytesavail >= expected) ||  (expected == 0 && bytesavail > 0) )
            {
                /*
                 *   If we specified a byte count, we will wait one timeout amount of time before returning (and
                 *   return whatever is available). If not we will wait for any bytes to become available and
                 *   return them.
                 */
                break; // the while loop
            }
        }

        Xfer.setInCountExpected( bytesavail );
    }

    if(Xfer.getInCountExpected() == 0)  // Don't ask me for it then!
    {
        /* If getInCountExpected() is 0 just return */
        return(NORMAL);
    }

    if(Xfer.isMessageStart())           // Are we the initial request?
    {
        if(getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY))
        {
            if(getDebugLevel() & DEBUGLEVEL_PORTCOMM)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "  Flushing inbound buffer " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            CTISleep ((ULONG) getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));

            if(getDebugLevel() & DEBUGLEVEL_PORTCOMM)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "  Done flushing inbound buffer " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            inClear();
        }

        if(getTablePortSettings().getCDWait() != 0)
        {
            status = NODCD;

            /* Check if we have DCD */
            while(!dcdTest() && DCDCount < getTablePortSettings().getCDWait())
            {
                /* We do not have DCD... Wait 1/20 second and try again */
                CTISleep (50L);
                DCDCount += 50;
            }

            if(DCDCount < getTablePortSettings().getCDWait())
            {
                status = NORMAL;
            }
        }
    }

    /* Make sure that any errors on the port are cleared */
    getPortCommError();

    if(status == NORMAL)
    {
<<<<<<< port_direct.cpp
        USHORT Told, T_new, Tmot;
=======
        USHORT Told, Tnew, Tmot;
>>>>>>> 1.12

        /* set the read timeout */
        Told = (USHORT)(Xfer.getInTimeout() + (USHORT)getDelay(EXTRA_DELAY));
<<<<<<< port_direct.cpp
        T_new = (USHORT)(byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );
=======
        Tnew = (USHORT)(byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );
>>>>>>> 1.12

<<<<<<< port_direct.cpp
        Tmot = (Told > T_new) ? Told : T_new;
=======
        Tmot = (Told > Tnew) ? Told : Tnew;
>>>>>>> 1.12

        setPortReadTimeOut( Tmot * 1000 );

        //  if the beginning of an IDLC message
        if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Xfer.isMessageStart())
        {
            status = readIDLCHeader(Xfer, &byteCount, true);
        }
        else
        {
            if((status = CTIRead(getHandle(), Message, Xfer.getInCountExpected(), &byteCount)) != NORMAL)
            {
                if(status == ERROR_INVALID_HANDLE)
                {
                    close(false);
                }

                status = PORTREAD;
            }
        }
    }

    if(status == NORMAL)
    {
        if(byteCount != Xfer.getInCountExpected())
        {
            INT oldcount = byteCount;

            if((status = CTIRead(getHandle(), Message + byteCount, Xfer.getInCountExpected() - byteCount, &byteCount)) != NORMAL)
            {
                if(status == ERROR_INVALID_HANDLE)
                {
                    close(false);
                }

                status = PORTREAD;
            }

            if(byteCount != Xfer.getInCountExpected())
            {
                byteCount += oldcount;
                status = READTIMEOUT;
            }
        }
    }

    Xfer.setInCountActual((ULONG)byteCount);      // This is the number of bytes filled into the buffer!

    /* Do the extra delay if the message is a completing type */
    if( Xfer.isMessageComplete() )
    {
        if(Dev->getPostDelay()) CTISleep((ULONG)Dev->getPostDelay());
    }

    if(Xfer.verifyCRC() && CheckCCITT16CRC(Dev->getType(), Xfer.getInBuffer(), *Xfer.getInCountActual()))    // CRC check failed.
    {
        status = BADCRC;
    }


    return status;
}


INT CtiPortDirect::readIDLCHeader(CtiXfer& Xfer, unsigned long *byteCount, bool suppressEcho)
{
    int status = NORMAL;

    int   matchCount = 0;
    bool  matching   = true;

    BYTE *Message    = Xfer.getInBuffer();      // Local alias for ease of use!

    //  only suppress if Mr. Cparm says we can
    suppressEcho = suppressEcho & gIDLCEchoSuppression;

    //  wait for the framing byte (0x7e) (or its wacky counterpart, 0xfc - eventually remove support for 0xfc...?)
    do
    {
        if((status = CTIRead(getHandle(), Message,  1, byteCount)) || *byteCount != 1)
        {
            if(status != NORMAL)
            {
                status = READERR;
            }
            else if(*byteCount != 1)
            {
                status = READTIMEOUT;
            }

            break;
        }

        /* Make sure that any errors on the port are cleared */
        getPortCommError();

    } while(Message[0] != 0x7e && Message[0] != 0xfc);

    //  if we successfully got the framing byte
    if(status == NORMAL)
    {
        if( Message[0] == 0xfc)
        {
            Message[0] = 0x7e;
        }

        matchCount++;

        if( Message[matchCount - 1] != (Xfer.getOutBuffer())[matchCount - 1] )
        {
            matching = false;
        }

        if( suppressEcho )
        {
            //  FIX/IMPROVE:  maybe read a chunk of 5 instead of just one-by-one... ?
            //                make sure to check against all IDLC message types before assuming

            //  check byte-by-byte against output to make sure it's not an echo
            while( matching && matchCount < Xfer.getOutCount() && status == NORMAL )
            {
                if((status = CTIRead(getHandle(), Message + matchCount, 1, byteCount)) == NORMAL && *byteCount == 1)
                {
                    matchCount += *byteCount;
                }

                if( Message[matchCount - 1] != (Xfer.getOutBuffer())[matchCount - 1] )
                {
                    matching = false;
                }
            }

            if( matchCount == Xfer.getOutCount() && matching )
            {
                //  if it's an echo
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " Discarding IDLC echo message" << endl;
                }

                //  start all over, but don't check for an echo - it's already been caught
                status = readIDLCHeader(Xfer, byteCount, false);
            }
        }


        //  if the message doesn't match OR we don't care about suppressing echoes, just blithely read on
        if( !suppressEcho || !matching )
        {
            if( status == NORMAL )
            {
                if((status = CTIRead(getHandle(), Message + matchCount, Xfer.getInCountExpected() - matchCount, byteCount)) != NORMAL)
                {
                    if(status == ERROR_INVALID_HANDLE)
                    {
                        close(false);
                        status = PORTREAD;
                    }
                    else if(status != NORMAL)
                    {
                        status = READERR;
                    }
                }
                else
                {
                    //  add on the bytes we've already read
                    *byteCount += matchCount;
                }
            }
        }
    }

    return status;
}



INT CtiPortDirect::outMess(CtiXfer& Xfer, CtiDevice *Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT      status = NORMAL;
    INT      i = 0;

    ULONG    Written;
    ULONG    ByteCount;
    ULONG    StartWrite;
    ULONG    ReturnWrite;

    if(getHandle() == NULL)
    {
        status = BADPORT;        // Invalid Handle really
    }
    else if(Xfer.getOutCount() > 0)
    {
        if(Xfer.getOutCount() > 4096)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " *** ERROR *** to attempt an OutMess of " << Xfer.getOutCount() << " bytes" << endl;
            }

            Xfer.setOutCount(100);     // Only allow 100 or so...
        }

        if(Xfer.addCRC())
        {
            BYTEUSHORT  CRC;
            CRC.sh = CCITT16CRC(Dev->getType(), Xfer.getOutBuffer(), Xfer.getOutCount(), TRUE); // CRC func appends the CRC data
            Xfer.setOutCount( Xfer.getOutCount() + 2 );
        }

        #if 0  // 20020708 CGP.  This code seems custom.
        /* Wait for DCD to dissapear */
        if(getTablePortSettings().getCDWait() != 0)
        {
            i = 0;
            while(dcdTest() && (i < getTablePortSettings().getCDWait()) )
            {
                CTISleep (50L);
                i+= 50;
            }
        }
        #endif

        /* Check if we need to key ... Pre Key Delay */
        if(getDelay(PRE_RTS_DELAY))
        {
            CTISleep (getDelay(PRE_RTS_DELAY));
        }

        /* Clear the Buffers */
        outClear();
        inClear();

        /* Key the radio */
        raiseRTS();

        ULONG rtstoout = getDelay(RTS_TO_DATA_OUT_DELAY);
        if(rtstoout)
        {
            CTISleep(rtstoout);
        }

        // We should determine if CTS is high.
        if( getDebugLevel() & DEBUGLEVEL_PORTCOMM && !isCTS() )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " CTS has not come high." << endl;
                dout << "  CTS has not come high.  Check your communications channel timings" << endl;
            }
#if 0
            INT cnt = rtstoout / 10;

            // We will wait one more RTS to DATA out cycle looking for this.
            while( !isCTS() && cnt-- > 0 )
            {
                CTISleep(10);
            }

            if(cnt <= 0)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  CTS never came high.  Check your communications channel timings" << endl;
            }
#endif
        }

        /* Remember when we started writing */
        MilliTime (&StartWrite);

        if(CTIWrite (getHandle(), Xfer.getOutBuffer(), Xfer.getOutCount(), &Written) || Written != Xfer.getOutCount())
        {
            close(false);
            status = PORTWRITE;
        }

        if(status == NORMAL)
        {
            /* if software queue is not empty wait for it to be */
            for (int cnt=0; cnt < 5; cnt++)
            {
                if ((ByteCount = getPortOutQueueCount()) != 0)
                {
                    CTISleep ((10000L * ByteCount) / getTablePortSettings().getBaudRate());
                }
                else
                {
                    break;
                }
            }

            /* Now outwait the hardware queue if neccessary */
            if(Xfer.getOutCount() > 2)
            {
                MilliTime (&ReturnWrite);
                if(ReturnWrite < (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / getTablePortSettings().getBaudRate())))
                {
                    CTISleep (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / getTablePortSettings().getBaudRate()) - ReturnWrite);
                }
            }

            if(getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))    /* Time to do the RTS thing */
            {
                CTISleep (getDelay(DATA_OUT_TO_RTS_DOWN_DELAY));

                if(!isDialup())
                {
                    lowerRTS();
                }
            }

            if(Dev->getAddress() == RTUGLOBAL || Dev->getAddress() == CCUGLOBAL)
            {
                CTISleep (Dev->getPostDelay());
            }
        }

        /* Check if we need to do a trace */
        traceOut(Xfer, traceList, Dev, status);
    }

    return status;
}

RWCString CtiPortDirect::getPhysicalPort() const
{
    return _localSerial.getPhysicalPort();
}


INT CtiPortDirect::close(INT trace)
{
    INT status = NORMAL;

    try
    {
        if(getHandle() != NULL)
        {
            if(_dialout)
            {
                _dialout->disconnect(NULL, trace);
            }
            else if(_dialin)
            {
                _dialin->disconnect(NULL, trace);
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " releasing port handle" << endl;
            }
            status = CTIClose(getHandle());
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _lastBaudRate = 0;


    return status;
}

CtiPortDirect::CtiPortDirect() :
_dialin(0),
_dialout(0),
_portHandle(0)
{
}

CtiPortDirect::CtiPortDirect(CtiPortDialout *dial) :
_dialin(0),
_dialout(dial),
_portHandle(0)
{
    if(_dialout != 0)
    {
        _dialout->setSuperPort(this);
    }
}

CtiPortDirect::CtiPortDirect(CtiPortDialin *dial) :
_dialin(dial),
_dialout(0),
_portHandle(0)
{
    if(_dialin != 0)
    {
        _dialin->setSuperPort(this);
    }
}

CtiPortDirect::CtiPortDirect(const CtiPortDirect& aRef) :
_dialin(0),
_dialout(0),
_portHandle(0)
{
    *this = aRef;
}

CtiPortDirect::~CtiPortDirect()
{
    close(false);
    if(_dialout)
    {
        delete _dialout;
        _dialout = 0;
    }
    if(_dialin)
    {
        delete _dialin;
        _dialin = 0;
    }
}

CtiPortDirect& CtiPortDirect::operator=(const CtiPortDirect& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _portHandle    = aRef.getHandle();
        _localSerial   = aRef.getLocalSerial();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

void CtiPortDirect::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTablePortLocalSerial::getSQL(db, keyTable, selector);
}

void CtiPortDirect::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
    _localSerial.DecodeDatabaseReader(rdr);       // get the base class handled
}

void CtiPortDirect::DecodeDialoutDatabaseReader(RWDBReader &rdr)
{
    if(_dialout)
    {
        _dialout->DecodeDatabaseReader(rdr);
    }
}

void CtiPortDirect::DecodeDialinDatabaseReader(RWDBReader &rdr)
{
    if(_dialin)
    {
        _dialin->DecodeDatabaseReader(rdr);
    }
}

INT CtiPortDirect::setPortReadTimeOut(USHORT millitimeout)
{
    _cto.ReadIntervalTimeout = 0;
    _cto.ReadTotalTimeoutMultiplier = 0;
    _cto.ReadTotalTimeoutConstant = (millitimeout);

    return (SetCommTimeouts(_portHandle, &_cto) ? NORMAL : SYSTEM);
}

INT CtiPortDirect::setPortWriteTimeOut(USHORT millitimeout)
{
    _cto.WriteTotalTimeoutMultiplier = 0;
    _cto.WriteTotalTimeoutConstant = (millitimeout);

    return (SetCommTimeouts(_portHandle, &_cto) ? NORMAL : SYSTEM);
}



INT CtiPortDirect::waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    INT status = BADPORT;

    if(_dialout)
    {
        status = _dialout->waitForResponse(ResponseSize,Response,Timeout,ExpectedResponse);
    }
    else if(_dialin)
    {
        status = _dialin->waitForResponse(ResponseSize,Response,Timeout,ExpectedResponse);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiPortDirect::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    return CTIWrite(getHandle(),pBuf,BufLen,pBytesWritten);
}

INT CtiPortDirect::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    return CTIRead(getHandle(),pBuf,BufLen,pBytesRead);
}

bool CtiPortDirect::isViable() const
{
    return (getHandle() != NULL);
}


INT CtiPortDirect::reset(INT trace)
{
    setLastBaudRate(0);
    setConnectedDevice(0);
    setConnectedDeviceUID(-1);

    if(_dialout)
    {
        _dialout->reset(trace);
    }
    else if(_dialin)
    {
        _dialin->reset(trace);
    }

    return NORMAL;
}

INT CtiPortDirect::setup(INT trace)
{
    setConnectedDevice(0);
    setConnectedDeviceUID(-1);

    if(_dialout)
    {
        _dialout->setup(trace);
    }
    else if(_dialin)
    {
        _dialin->setup(trace);
    }

    return NORMAL;
}

INT  CtiPortDirect::connectToDevice(CtiDevice *Device, INT trace)
{
    INT status = NORMAL;

    if(_dialout)
    {
        status = _dialout->connectToDevice(Device,trace);
    }
    else if(_dialin)
    {
        status = _dialin->connectToDevice(Device,trace);
    }
    else
    {
        status = Inherited::connectToDevice(Device,trace);
    }
    return NORMAL;
}

INT  CtiPortDirect::disconnect(CtiDevice *Device, INT trace)
{
    Inherited::disconnect(Device,trace);
    if(_dialout || _dialin)
    {
        close(trace);                           // Release the port handle
    }

    return NORMAL;
}

BOOL CtiPortDirect::connected()
{
    if(_dialout && getTablePortSettings().getCDWait() != 0 && getConnectedDevice() > 0)
    {
        if(!dcdTest())    // No DCD and we think we are connected!  This is BAD.
        {
            disconnect(NULL, FALSE);
        }
    }

    return Inherited::connected();
}

BOOL CtiPortDirect::shouldDisconnect() const
{
    BOOL bRet = Inherited::shouldDisconnect();

    if(_dialout)
    {
        bRet = _dialout->shouldDisconnect();
    }
    else if(_dialin)
    {
        bRet = _dialin->shouldDisconnect();
    }

    return bRet;
}

CtiPort& CtiPortDirect::setShouldDisconnect(BOOL b)
{
    if(_dialout)
    {
        _dialout->setShouldDisconnect(b);
    }
    else if(_dialin)
    {
        _dialin->setShouldDisconnect(b);
    }

    return *this;
}


int CtiPortDirect::initPrivateStores()
{
    GetCommState( _portHandle, &_dcb );

    _dcb.fOutxCtsFlow = FALSE;    // CTS is not monitored for flow control
    _dcb.fOutxDsrFlow = FALSE;    // DSR is not monitored for flow control
    _dcb.fOutX        = FALSE;
    _dcb.fInX         = FALSE;
    _dcb.fErrorChar   = FALSE;
    _dcb.fNull        = FALSE;
    _dcb.fRtsControl  = RTS_CONTROL_DISABLE;

    SetCommState( _portHandle, &_dcb );

    GetCommTimeouts( _portHandle, &_cto );

    return NORMAL;
}

int CtiPortDirect::enableXONXOFF()
{
    _dcb.fOutX  = TRUE;
    _dcb.fInX   = TRUE;

    return (SetCommState(_portHandle, &_dcb) ? NORMAL : SYSTEM);
}
int CtiPortDirect::disableXONXOFF()
{
    _dcb.fOutX  = FALSE;
    _dcb.fInX   = FALSE;

    return (SetCommState(_portHandle, &_dcb) ? NORMAL : SYSTEM);
}

int CtiPortDirect::enableRTSCTS()
{
    _dcb.fOutxCtsFlow = 1;
    _dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;

    return (SetCommState(_portHandle, &_dcb) ? NORMAL : SYSTEM);
}
int CtiPortDirect::disableRTSCTS()
{
    _dcb.fOutxCtsFlow = 0;
    _dcb.fRtsControl = RTS_CONTROL_DISABLE;

    return (SetCommState(_portHandle, &_dcb) ? NORMAL : SYSTEM);
}

int CtiPortDirect::getPortInQueueCount()
{
    DWORD   Err;
    COMSTAT Stat;

    if(ClearCommError(_portHandle, &Err, &Stat))
    {
       return Stat.cbInQue;
    }

    return 0;
}

int CtiPortDirect::getPortOutQueueCount()
{
    DWORD   Err;
    COMSTAT Stat;

    if(ClearCommError(_portHandle, &Err, &Stat))
    {
       return Stat.cbOutQue;
    }

    return 0;
}

int CtiPortDirect::getPortCommError()
{
    DWORD   Errors;
    return ( ClearCommError(_portHandle, &Errors, NULL) ? Errors : SYSTEM );
}
