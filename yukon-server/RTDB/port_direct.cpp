
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
#include "portsup.h"

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

INT CtiPortDirect::init()
{
    INT      status = NORMAL;
    ULONG    Result, i;

    if( getHandle() != NULL )
    {
        close(FALSE);
    }

    if(isInhibited())
    {
        status = PORTINHIBITED;
    }
    else
    {
        if(CTIOpen ((char*)(_localSerial.getPhysicalPort().data()), &getHandle(), &Result, 0L, FILE_NORMAL, FILE_OPEN, OPEN_ACCESS_READWRITE | OPEN_SHARE_DENYREADWRITE | OPEN_FLAGS_WRITE_THROUGH, 0L) || Result != 1)
        {
            close(false);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " *** ERROR *** acquiring port handle on " << _localSerial.getPhysicalPort() << endl;
            }

            return(BADPORT);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getName() << " acquiring port handle" << endl;
        }

        /* set the baud rate on the port */
        if((i = baudRate()) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the line characteristics for this port */
        if((i = SetLineMode(_portHandle)) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the default DCB info for the port */
        if((i = SetDefaultDCB(_portHandle)) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the Read Timeout for the port */
        if((i = SetReadTimeOut(_portHandle, TIMEOUT)) != NORMAL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            return(i);
        }

        /* set the write timeout for the port */
        if((i = SetWriteTimeOut(_portHandle, TIMEOUT)) != NORMAL)
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
            SetLineModeTAPTerm(getHandle());
        }

        /* Lower RTS */
        lowerRTS();

        /* Raise DTR */
        raiseDTR();
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



INT CtiPortDirect::baudRate(INT rate) const     // Set/reset the port's baud rate to the DB value
{
    INT r = _tblPortSettings.getBaudRate();

    if(rate != 0)
    {
        r = rate;
    }

    return(SetBaudRate(_portHandle, r));
}

INT CtiPortDirect::byteTime(ULONG bytes) const
{
    DOUBLE msbits = (10000 * bytes);
    DOUBLE mytime = ( msbits / (DOUBLE)_tblPortSettings.getBaudRate() );

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

INT CtiPortDirect::lowerRTS() const
{
    return(LowerModemRTS(_portHandle));
}

INT CtiPortDirect::raiseRTS() const
{
    return(RaiseModemRTS(_portHandle));
}

INT CtiPortDirect::lowerDTR() const
{
    return(LowerModemDTR(_portHandle));
}

INT CtiPortDirect::raiseDTR() const
{
    return(RaiseModemDTR(_portHandle));
}

INT CtiPortDirect::inClear() const
{
    return(PortInputFlush(_portHandle));
}

INT CtiPortDirect::outClear() const
{
    return(PortOutputFlush(_portHandle));
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
            bytesavail = GetPortInputQueueCount(getHandle());

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

        if(_tblPortSettings.getCDWait() != 0)
        {
            status = NODCD;
            /* Check if we have DCD */
            while(!(dcdTest()) && DCDCount++ < _tblPortSettings.getCDWait())
            {
                /* We do not have DCD... Wait 1/20 second and try again */
                CTISleep (50L);
            }

            if(DCDCount < _tblPortSettings.getCDWait())
            {
                status = NORMAL;
            }
        }
    }

    /* Make sure that any errors on the port are cleared */
    GetPortCommError(getHandle());

    if(status == NORMAL)
    {
        USHORT Told, Tnew, Tmot;

        /* set the read timeout */
        Told = (USHORT)(Xfer.getInTimeout() + (USHORT)getDelay(EXTRA_DELAY));
        Tnew = (USHORT)(byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );

        Tmot = (Told > Tnew) ? Told : Tnew;

        SetReadTimeOut( getHandle(), Tmot );

        /* If neccesary wait for IDLC flag character */
        if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Xfer.isMessageStart())
        {
            do
            {
                if((status = CTIRead(getHandle(), Message,  1, &byteCount)) || byteCount != 1)
                {
                    if(status != NORMAL)
                    {
                        status = READERR;
                    }
                    else if(byteCount != 1)
                    {
                        status = READTIMEOUT;
                    }
                    break;
                }

                /* Make sure that any errors on the port are cleared */
                GetPortCommError(getHandle());

            }  while(Message[0] != 0x7e && Message[0] != 0xfc);

            if(status == NORMAL)
            {
                if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Message[0] == 0xfc)
                {
                    Message[0] = 0x7e;
                }

                if((status = CTIRead(getHandle(), Message + 1, Xfer.getInCountExpected() - 1, &byteCount)) != NORMAL)
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

                byteCount += 1;
            }
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

INT CtiPortDirect::outMess(CtiXfer& Xfer, CtiDevice *Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT      status = NORMAL;
    INT      i = 0;

    ULONG    Written;
    ULONG    MSecs;
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

        /* Wait for DCD to dissapear */
        if(_tblPortSettings.getCDWait() != 0)
        {
            i = 0;
            while(i++ < _tblPortSettings.getCDWait() && dcdTest() )
            {
                CTISleep (50L);
            }
        }

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
        /* get the present time */
        MilliTime (&MSecs);

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
                if ((ByteCount = GetPortOutputQueueCount(getHandle())) != 0)
                {
                    CTISleep ((10000L * ByteCount) / _tblPortSettings.getBaudRate());
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
                if(ReturnWrite < (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / _tblPortSettings.getBaudRate())))
                {
                    CTISleep (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / _tblPortSettings.getBaudRate()) - ReturnWrite);
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

    if(getHandle() != NULL)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << getName() << " releasing port handle" << endl;
        }
        status = CTIClose(getHandle());
    }
    _lastBaudRate = 0;

    return status;
}

CtiPortDirect::CtiPortDirect() :
_portHandle(NULL)
{}

CtiPortDirect::CtiPortDirect(const CtiPortDirect& aRef) :
_portHandle(NULL)
{
    *this = aRef;
}

CtiPortDirect::~CtiPortDirect()
{
    close(false);
}

CtiPortDirect& CtiPortDirect::operator=(const CtiPortDirect& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _portHandle    = aRef.getHandle();
        _localSerial   = aRef.getLocalSerial();
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


INT CtiPortDirect::checkCommStatus(INT trace)
{
    INT status = NORMAL;

    if(getLastBaudRate() == 0 || needsReinit())
    {
        /* set up the port */
        if( (status = init()) != NORMAL )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error initializing Virtual Port " << getPortID() <<" on " << getName() << endl;
            }

            return status;
        }
        else
        {
            setPortNameWas( getName() );
            setLastBaudRate( _tblPortSettings.getBaudRate() );
        }
    }

    return status;
}

/* Routine to check DCD on a Port */
INT CtiPortDirect::isDCD() const
{
    DWORD   eMask = 0;
    GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_RLSD_ON);     // Yes, that is DCD or receive-line-signal detect.
}


/* Routine to check DSR on a port */
INT CtiPortDirect::isDSR() const
{
    DWORD   eMask = 0;
    GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_DSR_ON);
}


/* Routine to check CTS on a port */
INT CtiPortDirect::isCTS() const
{
    DWORD   eMask = 0;
    GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_CTS_ON);
}


