#include "precompiled.h"

#include "cparms.h"
#include "dllbase.h"
#include "port_direct.h"

using std::string;
using std::endl;
using std::list;

YukonError_t CtiPortDirect::openPort(INT rate, INT bits, INT parity, INT stopbits)
{
    YukonError_t status = ClientErrors::None;
    ULONG    Result;

    if( isSimulated() )
    {
        return ClientErrors::None;
    }

    if( isViable() )
    {
        close(FALSE);
    }

    if(isInhibited())
    {
        return ClientErrors::PortInhibited;
    }

    try
    {
        if(CTIOpen ((char*)(_localSerial.getPhysicalPort().c_str()),
                    &getHandle(),
                    &Result,
                    0L,
                    FILE_NORMAL,
                    FILE_OPEN,
                    OPEN_ACCESS_READWRITE | OPEN_SHARE_DENYREADWRITE | OPEN_FLAGS_WRITE_THROUGH,
                    0L) || Result != 1)
        {
            close(false);

            CTILOG_ERROR(dout, "Could not acquire port handle "<< _localSerial.getPhysicalPort());

            Sleep(5000);

            return ClientErrors::BadPort;
        }

        CTILOG_INFO(dout, "Port "<< getName() <<" acquiring port handle");

        /* load _dcb and set the default DCB/COMMTIMEOUTS info for the port */
        initPrivateStores();

        YukonError_t i;

        /* set the baud rate bits parity etc! on the port */
        if( i = setLine() )
        {
            CTILOG_ERROR(dout, "Could not set baud rate/parity on port "<< getName());

            return(i);
        }

        /* set the Read Timeout for the port */
        if( i = setPortReadTimeOut(1000) )
        {
            CTILOG_ERROR(dout, "Could not set the read timeout on port "<< getName());

            return(i);
        }

        /* set the write timeout for the port */
        if( i = setPortWriteTimeOut(1000) )
        {
            CTILOG_ERROR(dout, "Could not set the write timeout on port "<< getName());

            return(i);
        }

        /* Lower RTS */
        lowerRTS();

        /* Raise DTR */
        raiseDTR();

        if( status = reset(true) )
        {
            CTILOG_ERROR(dout, "Could not reset port on "<< getName());
        }
        /* set the modem parameters */
        if( status = setup(true) )
        {
            CTILOG_ERROR(dout, "Could not set port on "<< getName());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

YukonError_t CtiPortDirect::setLine(INT rate, INT bits, INT parity, INT stopbits)
{
    YukonError_t retval = ClientErrors::None;

    if( !rate )
    {
        rate = getTablePortSettings().getBaudRate();
    }

    if( (_dcb.BaudRate != rate)   ||
        (_dcb.ByteSize != bits)   ||
        (_dcb.Parity   != parity) ||
        (_dcb.StopBits != stopbits) )
    {
        _dcb.BaudRate  = rate;
        _dcb.ByteSize  = bits;
        _dcb.Parity    = parity;
        _dcb.StopBits  = stopbits;

        if(_portHandle) retval = SetCommState(_portHandle, &_dcb) ? ClientErrors::None : ClientErrors::SystemRelated;
    }

    return retval;
}

INT CtiPortDirect::byteTime(ULONG bytes) const
{
    DOUBLE msbits = (10000 * bytes);
    DOUBLE mytime = ( msbits / (DOUBLE)getTablePortSettings().getBaudRate() );

    return(int) (mytime / 1000.0) + 2;
}


INT CtiPortDirect::ctsTest() const
{
    DWORD   eMask = 0;
    if(getHandle() > 0) GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_CTS_ON);
}

INT CtiPortDirect::dcdTest() const
{
    DWORD   eMask = 0;
    if(getHandle() > 0) GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_RLSD_ON);     // Yes, that is DCD or receive-line-signal detect.
}

/* Routine to check DSR on a port */
INT CtiPortDirect::dsrTest() const
{
    DWORD   eMask = 0;
    if(getHandle() > 0) GetCommModemStatus(getHandle(), &eMask);
    return(eMask & MS_DSR_ON);
}

INT CtiPortDirect::lowerRTS()
{
    _dcb.fRtsControl =  RTS_CONTROL_DISABLE;
    return(EscapeCommFunction(_portHandle, CLRRTS) ? ClientErrors::None : ClientErrors::SystemRelated);
}

INT CtiPortDirect::raiseRTS()
{
    _dcb.fRtsControl =  RTS_CONTROL_ENABLE;
    return(EscapeCommFunction(_portHandle, SETRTS) ? ClientErrors::None : ClientErrors::SystemRelated);
}

INT CtiPortDirect::lowerDTR()
{
    _dcb.fDtrControl =  DTR_CONTROL_DISABLE;
    return(EscapeCommFunction(_portHandle, CLRDTR) ? ClientErrors::None : ClientErrors::SystemRelated);
}

INT CtiPortDirect::raiseDTR()
{
    _dcb.fDtrControl =  DTR_CONTROL_ENABLE;
    return(EscapeCommFunction(_portHandle, SETDTR) ? ClientErrors::None : ClientErrors::SystemRelated);
}

YukonError_t CtiPortDirect::inClear()
{
    return(PurgeComm(_portHandle, PURGE_RXCLEAR) ? ClientErrors::None : ClientErrors::SystemRelated );
}

INT CtiPortDirect::outClear()
{
    return(PurgeComm(_portHandle, PURGE_TXCLEAR) ? ClientErrors::None : ClientErrors::SystemRelated );
}

YukonError_t CtiPortDirect::inMess(CtiXfer& Xfer, CtiDeviceSPtr Dev, list< CtiMessage* > &traceList)
{
    YukonError_t status = ClientErrors::None;

    // 091201 CGP CCH  // BYTE     SomeMessage[300];
    ULONG    DCDCount    = 0;
    ULONG    SomeRead    = 0;
    ULONG    byteCount   = 0;


    BYTE     *Message    = Xfer.getInBuffer();      // Local alias for ease of use!

    Xfer.setInCountActual( (ULONG)0 );     // Mark it as zero to prevent any "lies"

    if( (Xfer.getInCountExpected() > 0) && isSimulated() )
    {
        //  simulate the inbound delay as best we can
        CTISleep(byteTime(Xfer.getInCountExpected()) * 1000);

        status = ClientErrors::PortSimulated;
    }
    else
    {
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

            if(Xfer.getBufferSize() && (Xfer.getBufferSize()<bytesavail))//greater than 0 and less then bytes avail
            {
                Xfer.setInCountExpected(Xfer.getBufferSize());
            }
        }

        if(Xfer.getInCountExpected() == 0)  // Don't ask me for it then!
        {
            /* If getInCountExpected() is 0 just return */
            return ClientErrors::None;
        }

        if(Xfer.isMessageStart())           // Are we the initial request?
        {
            if(getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY))
            {
                if(getDebugLevel() & DEBUGLEVEL_PORTCOMM)
                {
                    CTILOG_DEBUG(dout, "Flushing inbound buffer");
                }
                CTISleep ((ULONG) getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));

                if(getDebugLevel() & DEBUGLEVEL_PORTCOMM)
                {
                    CTILOG_DEBUG(dout, "Done flushing inbound buffer");
                }
                inClear();
            }

            if(getTablePortSettings().getCDWait() != 0)
            {
                status = ClientErrors::NoDcd;

                /* Check if we have DCD */
                while(!dcdTest() && DCDCount < getTablePortSettings().getCDWait())
                {
                    /* We do not have DCD... Wait 1/20 second and try again */
                    CTISleep (50L);
                    DCDCount += 50;
                }

                if(DCDCount < getTablePortSettings().getCDWait())
                {
                    status = ClientErrors::None;
                }
            }
        }

        /* Make sure that any errors on the port are cleared */
        clearPortCommError();

        if(status == ClientErrors::None)
        {
            USHORT Told, Tnew, Tmot;

            /* set the read timeout */
            Told = (USHORT)(Xfer.getInTimeout() + (USHORT)getDelay(EXTRA_DELAY));
            Tnew = (USHORT)(byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );

            Tmot = (Told > Tnew) ? Told : Tnew;

            setPortReadTimeOut( Tmot * 1000 );

            //  if the beginning of an IDLC message
            if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Xfer.isMessageStart())
            {
                status = readIDLCHeader(Xfer, &byteCount, true);
            }
            else
            {
                if( status = CTIRead(getHandle(), Message, Xfer.getInCountExpected(), &byteCount) )
                {
                    if(status == ERROR_INVALID_HANDLE)
                    {
                        close(false);
                    }

                    status = ClientErrors::PortRead;
                }
            }
        }

        if(status == ClientErrors::None)
        {
            if(byteCount != Xfer.getInCountExpected())
            {
                INT oldcount = byteCount;

                if( status = CTIRead(getHandle(), Message + byteCount, Xfer.getInCountExpected() - byteCount, &byteCount) )
                {
                    if(status == ERROR_INVALID_HANDLE)
                    {
                        close(false);
                    }

                    status = ClientErrors::PortRead;
                }

                if(byteCount != Xfer.getInCountExpected())
                {
                    byteCount += oldcount;
                    status = ClientErrors::ReadTimeout;
                }
            }
        }

        Xfer.setInCountActual((ULONG)byteCount);      // This is the number of bytes filled into the buffer!

        /* Do the extra delay if the message is a completing type */
        if( Xfer.isMessageComplete() )
        {
            if(Dev->getPostDelay()) CTISleep((ULONG)Dev->getPostDelay());
        }

        if(Xfer.verifyCRC() && CheckCCITT16CRC(Dev->getType(), Xfer.getInBuffer(), Xfer.getInCountActual()))    // CRC check failed.
        {
            status = ClientErrors::BadCrc;
        }
    }


    return status;
}


YukonError_t CtiPortDirect::readIDLCHeader(CtiXfer& Xfer, unsigned long *byteCount, bool suppressEcho)
{
    YukonError_t status = ClientErrors::None;

    bool  matching;

    BYTE  *in_buf, *out_buf;
    ULONG  pos, bytes_read, in_count, in_expected, out_count;

    in_buf   = Xfer.getInBuffer();  // Local alias for ease of use!
    out_buf  = Xfer.getOutBuffer();
    in_expected = Xfer.getInCountExpected();
    out_count   = Xfer.getOutCount();

    pos = 0;

    //  wait for the framing byte (0x7e) (or its wacky counterpart, 0xfc - eventually remove support for 0xfc...?)
    do
    {
        if((status = CTIRead(getHandle(), in_buf, 1, &bytes_read)) == ClientErrors::None)
        {
            if( bytes_read == 1 )
            {
                if( in_buf[0] == 0xfc)
                {
                    in_buf[0] =  0x7e;
                }

                //  Make sure that any errors on the port are cleared
                clearPortCommError();
            }
            else
            {
                status = ClientErrors::ReadTimeout;
            }
        }
    } while(in_buf[0] != 0x7e && status == ClientErrors::None);

    //  if we successfully got the framing byte
    if(status == ClientErrors::None)
    {
        matching  = suppressEcho;
        matching &= (in_buf[pos] == out_buf[pos]);

        pos++;

        //  check byte-by-byte against output to make sure it's not an echo
        while( matching && pos < out_count && status == ClientErrors::None )
        {
            if((status = CTIRead(getHandle(), in_buf + pos, 1, &bytes_read)) == ClientErrors::None)
            {
                if( bytes_read == 1 )
                {
                    matching &= (in_buf[pos] == out_buf[pos]);

                    pos++;
                }
                else
                {
                    status = ClientErrors::ReadTimeout;
                }
            }
        }

        if( matching && pos == out_count )
        {
            //  if it's an echo
            CTILOG_WARN(dout, "Discarding IDLC echo message");

            //  start all over, but don't check for an echo - it's already been caught
            status = readIDLCHeader(Xfer, byteCount, false);
        }
        else if( status == ClientErrors::None )
        {
            if((status = CTIRead(getHandle(), in_buf + pos, in_expected - pos, &bytes_read)) == ClientErrors::None)
            {
                pos += bytes_read;
            }
        }
    }


    *byteCount = pos;

    if( status )
    {
        if( status == ERROR_INVALID_HANDLE )
        {
            close(false);
            status = ClientErrors::PortRead;
        }
        else if( status != ClientErrors::ReadTimeout )
        {
            status = ClientErrors::Read;
        }
    }

    return status;
}



YukonError_t CtiPortDirect::outMess(CtiXfer& Xfer, CtiDeviceSPtr Dev, list< CtiMessage* > &traceList)
{
    YukonError_t status = ClientErrors::None;
    INT      i = 0;

    ULONG    Written;
    ULONG    ByteCount;
    ULONG    StartWrite;
    ULONG    ReturnWrite;

    //if(!isViable())
    {
        checkCommStatus(Dev, TRUE);
    }

    //  if the handle is null and we're not simulating ports
    if( (getHandle() == NULL) && !isSimulated() )
    {
        status = ClientErrors::BadPort;        // Invalid Handle really
    }
    else if(Xfer.getOutCount() > 0)
    {
        if(Xfer.getOutCount() > 4096)
        {
            CTILOG_ERROR(dout, "attempting an OutMess of "<< Xfer.getOutCount() <<" bytes");

            Xfer.setOutCount(100);     // Only allow 100 or so...
        }

        if(Xfer.addCRC())
        {
            BYTEUSHORT  CRC;
            CRC.sh = CCITT16CRC(Dev->getType(), Xfer.getOutBuffer(), Xfer.getOutCount(), TRUE); // CRC func appends the CRC data
            Xfer.setOutCount( Xfer.getOutCount() + 2 );
        }

        if( isSimulated() )
        {
            //  simulate all delays

            if(getDelay(PRE_RTS_DELAY))                 CTISleep(getDelay(PRE_RTS_DELAY));
            if(getDelay(RTS_TO_DATA_OUT_DELAY))         CTISleep(getDelay(RTS_TO_DATA_OUT_DELAY));

            int portWriteTime = (10000L * Xfer.getOutCount()) / getTablePortSettings().getBaudRate();

            CTISleep(portWriteTime);

            if(getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))    CTISleep (getDelay(DATA_OUT_TO_RTS_DOWN_DELAY));
        }
        else
        {
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
            if( getDebugLevel() & DEBUGLEVEL_PORTCOMM && !ctsTest() )
            {
                CTILOG_DEBUG(dout, getName() <<" CTS has not come high. Check your communications channel timings");
            }

            /* Remember when we started writing */
            MilliTime (&StartWrite);

            setPortWriteTimeOut( (10000L * Xfer.getOutCount()) / getTablePortSettings().getBaudRate() + 500 );

            if(CTIWrite (getHandle(), Xfer.getOutBuffer(), Xfer.getOutCount(), &Written) || Written != Xfer.getOutCount())
            {
                close(false);
                status = ClientErrors::PortWrite;
            }

            if(status == ClientErrors::None)
            {
                /* if software queue is not empty wait for it to be */
                for(int cnt=0; cnt < 5; cnt++)
                {
                    if((ByteCount = getPortOutQueueCount()) != 0)
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
        }

        /* Check if we need to do a trace */
        traceOut(Xfer, traceList, Dev, status);
    }

    return status;
}

string CtiPortDirect::getPhysicalPort() const
{
    return _localSerial.getPhysicalPort();
}


INT CtiPortDirect::close(INT trace)
{
    INT status = ClientErrors::None;

    if(getHandle() != NULL)
    {
        if(_dialable)
        {
            _dialable->disconnect(CtiDeviceSPtr(), trace);
        }

        CTILOG_INFO(dout, getName() <<" releasing port handle");

        status = CTIClose(getHandle());
    }

    return status;
}

CtiPortDirect::CtiPortDirect() :
_dialable(0),
_portHandle(0)
{
    memset(&_dcb, 0, sizeof(_dcb));
    memset(&_cto, 0, sizeof(_cto));
}

CtiPortDirect::CtiPortDirect(CtiPortDialable *dial) :
_dialable(dial),
_portHandle(0)
{
    memset(&_dcb, 0, sizeof(_dcb));
    memset(&_cto, 0, sizeof(_cto));

    if(_dialable != 0)
    {
        _dialable->setSuperPort(this);
    }
}

CtiPortDirect::~CtiPortDirect()
{
    CtiPortDirect::close(false);  //  prevent virtual dispatch
    if(_dialable)
    {
        delete _dialable;
        _dialable = 0;
    }
}

string CtiPortDirect::getSQLCoreStatement()
{
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                   "YP.disableflag, CP.alarminhibit, CP.commonprotocol, CP.performancealarm, "
                                   "CP.performthreshold, CP.sharedporttype, CP.sharedsocketnumber, "
                                   "PST.baudrate, PST.cdwait, PST.linesettings, TMG.pretxwait, TMG.rtstotxwait, "
                                   "TMG.posttxwait, TMG.receivedatawait, TMG.extratimeout, PLS.physicalport "
                               "FROM YukonPAObject YP, CommPort CP, PortSettings PST, PortTiming TMG, "
                                   "PortLocalSerial PLS "
                               "WHERE YP.paobjectid = CP.portid AND YP.paobjectid = PST.portid AND "
                                   "YP.paobjectid = TMG.portid AND YP.paobjectid = PLS.portid";

    return sql;
}

void CtiPortDirect::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
    _localSerial.DecodeDatabaseReader(rdr);       // get the base class handled
}

void CtiPortDirect::DecodeDialableDatabaseReader(Cti::RowReader &rdr)
{
    if(_dialable)
    {
        _dialable->DecodeDatabaseReader(rdr);
    }
}

YukonError_t CtiPortDirect::setPortReadTimeOut(USHORT millitimeout)
{
    _cto.ReadIntervalTimeout = 0;
    _cto.ReadTotalTimeoutMultiplier = 0;
    _cto.ReadTotalTimeoutConstant = (millitimeout);

    return(SetCommTimeouts(_portHandle, &_cto) ? ClientErrors::None : ClientErrors::SystemRelated);
}

YukonError_t CtiPortDirect::setPortWriteTimeOut(USHORT millitimeout)
{
    _cto.WriteTotalTimeoutMultiplier = 0;
    _cto.WriteTotalTimeoutConstant = (millitimeout);

    return(SetCommTimeouts(_portHandle, &_cto) ? ClientErrors::None : ClientErrors::SystemRelated);
}



YukonError_t CtiPortDirect::waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    YukonError_t status = ClientErrors::BadPort;

    if(_dialable)
    {
        status = _dialable->waitForResponse(ResponseSize,Response,Timeout,ExpectedResponse);
    }
    else
    {
        CTILOG_ERROR(dout, "_dialable is NULL");
    }

    return status;
}

INT CtiPortDirect::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    setPortWriteTimeOut( timeout * 1000 );
    return CTIWrite(getHandle(),pBuf,BufLen,pBytesWritten);
}

INT CtiPortDirect::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    return CTIRead(getHandle(),pBuf,BufLen,pBytesRead);
}

bool CtiPortDirect::isViable()
{
    return(getHandle() != NULL);
}


YukonError_t CtiPortDirect::reset(INT trace)
{
    YukonError_t status = ClientErrors::None;

    if(_dialable)
    {
        status = _dialable->reset(trace);
    }

    return status;
}

YukonError_t CtiPortDirect::setup(INT trace)
{
    if(_dialable)
    {
        _dialable->setup(trace);
    }

    return ClientErrors::None;
}

YukonError_t CtiPortDirect::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    YukonError_t status = ClientErrors::None;

    if(_dialable)
    {
        status = _dialable->connectToDevice(Device, LastDeviceId,trace);
    }
    else
    {
        status = Inherited::connectToDevice(Device, LastDeviceId, trace);
    }

    return status;
}

INT  CtiPortDirect::disconnect(CtiDeviceSPtr Device, INT trace)
{
    int status = ClientErrors::None;

    status = Inherited::disconnect(Device,trace);
    if(!status && (_dialable || gConfigParms.isOpt("PORTER_RELEASE_IDLE_PORTS", "true")) )
    {
        status = close(trace);                           // Release the port handle
    }

    return status;
}

BOOL CtiPortDirect::connected()
{
    if(_dialable)
    {
        if(getTablePortSettings().getCDWait() != 0 )
        {
            if(getConnectedDevice() > 0 && !dcdTest())    // No DCD and we think we are connected!  This is BAD.
            {
                disconnect(CtiDeviceSPtr(), FALSE);
            }
            else if (getConnectedDevice() <= 0 && dcdTest())
            {
                disconnect(CtiDeviceSPtr(), FALSE);
            }
        }
    }

    return Inherited::connected();
}

BOOL CtiPortDirect::shouldDisconnect() const
{
    BOOL bRet = Inherited::shouldDisconnect();

    if(_dialable)
    {
        bRet = _dialable->shouldDisconnect();
    }

    return bRet;
}

CtiPort& CtiPortDirect::setShouldDisconnect(BOOL b)
{
    if(_dialable)
    {
        _dialable->setShouldDisconnect(b);
    }

    return *this;
}


int CtiPortDirect::initPrivateStores()
{
    if(_portHandle) GetCommState( _portHandle, &_dcb );

    _dcb.fOutxCtsFlow = FALSE;    // CTS is not monitored for flow control
    _dcb.fOutxDsrFlow = FALSE;    // DSR is not monitored for flow control
    _dcb.fOutX        = FALSE;
    _dcb.fInX         = FALSE;
    _dcb.fErrorChar   = FALSE;
    _dcb.fNull        = FALSE;
    _dcb.fRtsControl  = RTS_CONTROL_DISABLE;

    if(_portHandle)
    {
    SetCommState( _portHandle, &_dcb );
    GetCommTimeouts( _portHandle, &_cto );
    }

    return ClientErrors::None;
}

int CtiPortDirect::enableXONXOFF()
{
    _dcb.fOutX  = TRUE;
    _dcb.fInX   = TRUE;

    INT scsret = ClientErrors::None;
    if(_portHandle) scsret = SetCommState(_portHandle, &_dcb) ? ClientErrors::None : ClientErrors::SystemRelated;
    return scsret;
}
int CtiPortDirect::disableXONXOFF()
{
    _dcb.fOutX  = FALSE;
    _dcb.fInX   = FALSE;

    INT scsret = ClientErrors::None;
    if(_portHandle) scsret = SetCommState(_portHandle, &_dcb) ? ClientErrors::None : ClientErrors::SystemRelated;
    return scsret;
}

int CtiPortDirect::enableRTSCTS()
{
    _dcb.fOutxCtsFlow = 1;
    _dcb.fRtsControl = RTS_CONTROL_HANDSHAKE;

    INT scsret = ClientErrors::None;
    if(_portHandle) scsret = SetCommState(_portHandle, &_dcb) ? ClientErrors::None : ClientErrors::SystemRelated;
    return scsret;
}
int CtiPortDirect::disableRTSCTS()
{
    _dcb.fOutxCtsFlow = 0;
    _dcb.fRtsControl = RTS_CONTROL_DISABLE;

    INT scsret = ClientErrors::None;
    if(_portHandle) scsret = SetCommState(_portHandle, &_dcb) ? ClientErrors::None : ClientErrors::SystemRelated;
    return scsret;
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

void CtiPortDirect::clearPortCommError()
{
    DWORD   error;
    ClearCommError(_portHandle, &error, NULL);
}

