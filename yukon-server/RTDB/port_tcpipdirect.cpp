#include "precompiled.h"

#include <iostream>

#include "cparms.h"
#include "logger.h"
#include "port_tcpipdirect.h"
#include "utility.h"
#include "socket_helper.h"

#include "boost/scoped_array.hpp"

using namespace boost;
using std::string;
using std::endl;
using std::list;
using std::pair;

CtiPortTCPIPDirect::CtiPortTCPIPDirect() :
_dialable(0),
_socket(INVALID_SOCKET),
_lastConnect(0UL)
{
}

CtiPortTCPIPDirect::CtiPortTCPIPDirect(CtiPortDialable *dial) :
_dialable(dial),
_socket(INVALID_SOCKET),
_lastConnect(0UL)
{
    if(_dialable != 0)
    {
        _dialable->setSuperPort(this);
    }
}

CtiPortTCPIPDirect::~CtiPortTCPIPDirect()
{
    CtiPortTCPIPDirect::close(false);
    if(_dialable)
    {
        delete _dialable;
        _dialable = 0;
    }
}

const string &CtiPortTCPIPDirect::getIPAddress() const  {  return _tcpIpInfo.getIPAddress();  }
INT           CtiPortTCPIPDirect::getIPPort()    const  {  return _tcpIpInfo.getIPPort();     }


YukonError_t CtiPortTCPIPDirect::openPort(INT rate, INT bits, INT parity, INT stopbits)
{
    if( isSimulated() )
    {
        return NORMAL;
    }

    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(_socket != INVALID_SOCKET)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if(isViable())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port " << getName() << " closing port " << _socket << endl;
        }
        close(FALSE);

        Sleep(1000);
    }

    if(isInhibited())
    {
        return PORTINHIBITED;
    }

    if(isViable())
    {
        return NORMAL;
    }

    Cti::AddrInfo pAddrInfo = Cti::makeTcpClientSocketAddress(getIPAddress(), getIPPort());
    if( !pAddrInfo )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port " << getName() << " could not resolve IP for DNS name \"" << getIPAddress() << "\"" << endl;
        }
        return ErrorDnsLookupFailed;
    }

    YukonError_t status = NORMAL;

    /* get a stream socket. */
    if((_socket = socket(pAddrInfo->ai_family, pAddrInfo->ai_socktype, pAddrInfo->ai_protocol)) == INVALID_SOCKET)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error getting Socket for Terminal Server:  " << WSAGetLastError() << " " << getName() << endl;
        }
        shutdownClose(__FILE__, __LINE__);
        status = TCPCONNECTERROR;
    }
    else
    {
        //  this delay is here for ports that allow connections but then immediately disconnect;
        //    if we connected more than PORTER_TCP_CONNECTION_DELAY seconds ago (default 15), we can connect immediately
        int reconnectRate = 15;
        const std::string cparmReconnectKey = "PORTER_TCP_CONNECTION_DELAY";
        if(gConfigParms.isOpt(cparmReconnectKey))
        {
            const int cparmReconnectValue = gConfigParms.getValueAsInt(cparmReconnectKey,-1);
            if( 1 <= cparmReconnectValue && cparmReconnectValue <=3600 )
            {
                reconnectRate = cparmReconnectValue;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "- Invalid reconnect rate " << cparmReconnectValue << ". " << cparmReconnectKey << "must be between 1 and 3600 (1 hr). Setting to " << reconnectRate << " seconds." << endl;
            }
        }

        CtiTime nextConnect = _lastConnect + reconnectRate;

        int connect_delay = nextConnect.seconds() - CtiTime::now().seconds();

        if( connect_delay > 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port " << getName() << " next connect at " << nextConnect << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            CTISleep(connect_delay * 1000);
        }

        if( connect(_socket, pAddrInfo->ai_addr, pAddrInfo->ai_addrlen) == SOCKET_ERROR )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error Connecting to Terminal Server:  " << WSAGetLastError() << " " << getName() << endl;
            }
            shutdownClose(__FILE__, __LINE__);
            return(TCPCONNECTERROR);
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port " << getName() << " acquiring socket handle " << _socket << endl;
        }

        /* Make sure we time out on our writes after 5 seconds */
        const int socketWriteTimeout = gConfigParms.getValueAsInt("PORTER_SOCKET_WRITE_TIMEOUT", 5);
        if(setsockopt (_socket, SOL_SOCKET, SO_SNDTIMEO, (const char *) &socketWriteTimeout, sizeof (socketWriteTimeout)))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error setting KeepAlive Mode for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
        }

        /* Turn on the keepalive timer */
        const int keepaliveTimer = 1;
        if(setsockopt (_socket, SOL_SOCKET, SO_KEEPALIVE, (const char *) &keepaliveTimer, sizeof (keepaliveTimer)))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error setting KeepAlive Mode for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
        }

        LINGER ling;
        ling.l_onoff = 1;
        ling.l_linger = 0;

        if(setsockopt (_socket, SOL_SOCKET, SO_LINGER, (const char *)&ling, sizeof(ling)))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error setting Linger Mode (Hard) for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
        }

        unsigned long nonblocking = 1;

        if(ioctlsocket(_socket, FIONBIO, &nonblocking))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error setting nonblocking mode for Terminal Server Socket:  " << WSAGetLastError() << " " << getName() << endl;
        }

        _lastConnect = CtiTime::now();
    }

    if((status = reset(true)) != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error resetting port for dialup on " << getName() << endl;
    }

    /* set the modem parameters */
    if((status = setup(true)) != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error setting port for dialup modem on " << getName() << endl;
    }

    return status;
}


INT CtiPortTCPIPDirect::close(INT trace)
{
    if(_dialable)
    {
        _dialable->disconnect(CtiDeviceSPtr(), trace);
    }

    return shutdownClose(__FILE__, __LINE__);
}

YukonError_t CtiPortTCPIPDirect::inClear() const
{
    if( _socket == INVALID_SOCKET )
    {
        return NORMAL;
    }

    fd_set read_sockets;

    FD_ZERO(&read_sockets);
    FD_SET(_socket, &read_sockets);

    timeval tv = {0, 0};

    switch( select(0, &read_sockets, 0, 0, &tv) )
    {
        case 0:             return NORMAL;
        case 1:             break;

        default:
        case SOCKET_ERROR:  return SYSTEM;
    }

    // How many are available ??
    ULONG ulTemp;
    if( ioctlsocket(_socket, FIONREAD, &ulTemp) == SOCKET_ERROR )
    {
        return SYSTEM;
    }

    scoped_array<char> buf(new char[ulTemp]);

    if( recv(_socket, buf.get(), (int)ulTemp, 0) <= 0 )
    {
        return TCPREADERROR;
    }

    return NORMAL;
}

INT CtiPortTCPIPDirect::outClear() const
{
    return NORMAL;
}


YukonError_t CtiPortTCPIPDirect::inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, list< CtiMessage* > &traceList)
{
    YukonError_t status = NORMAL;

    BYTE     SomeMessage[300];
    ULONG    DCDCount    = 0;
    ULONG    SomeRead    = 0;
    ULONG    Told, Tnew, Tmot;
    LONG     byteCount   = 0;

    CtiLockGuard<CtiMutex> guard(_classMutex);

    BYTE     *Message    = Xfer.getInBuffer();      // Local alias for ease of use!

    Xfer.setInCountActual( (ULONG)0 );     // Mark it as zero to prevent any "lies"

    if( (Xfer.getInCountExpected() > 0) && isSimulated() )
    {
        //  simulate the inbound delay as best we can
        CTISleep(byteTime(Xfer.getInCountExpected()) * 1000);

        return ErrPortSimulated;
    }

    if(Xfer.getNonBlockingReads())         // We need to get all that are out there.
    {
        ULONG bytesavail = 0;
        INT   lpcnt = 0;
        ULONG expected = Xfer.getInCountExpected();

        while( Xfer.getInTimeout() * 4 >= lpcnt++ )  // Must do this at least once.
        {
            Sleep(250);

            bytesavail = 0;
            if(_socket != INVALID_SOCKET)
            {
                ioctlsocket (_socket, FIONREAD, &bytesavail);
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

        if(bytesavail <= Xfer.getInCountExpected())  // Make sure we don't acquire more data than we have space for!
        {
            Xfer.setInCountExpected( bytesavail );
        }
    }

    /* If getInCountExpected() is 0 just return */
    if(Xfer.getInCountExpected() == 0)  // Don't ask me for it then!
    {
        return(NORMAL);
    }

    /* set the read timeout */
    Told = (Xfer.getInTimeout() + getDelay(EXTRA_DELAY));
    Tnew = (byteTime(Xfer.getInCountExpected()) + getDelay(EXTRA_DELAY) );
    Tmot = (Told > Tnew) ? Told : Tnew;

    if(Xfer.isMessageStart())           // Are we the initial request?
    {
        if(getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY))
        {
            CTISleep ((ULONG) getDelay(DATA_OUT_TO_INBUFFER_FLUSH_DELAY));
            status = inClear();

            if( status != NORMAL )
            {
                shutdownClose(__FILE__, __LINE__);
            }
        }

        if(status == NORMAL && getTablePortSettings().getCDWait() != 0)
        {
            status = NODCD;
            /* Check if we have DCD */
            while(!(dcdTest()) && DCDCount < getTablePortSettings().getCDWait())
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

    if(status == NORMAL)
    {
        /* If neccesary wait for IDLC flag character */
        if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Xfer.isMessageStart())
        {
            do
            {
                if((status = receiveData(Message, 1, Tmot, &byteCount)) || byteCount != 1)
                {
                    break;
                }

                SomeMessage[SomeRead] = Message[0];
                SomeRead++;

                if(SomeRead == sizeof(SomeMessage))
                {
                    // oh no we stomped memory
                    status = FRAMEERR;
                    break;               // the while loop
                }
            }  while(Message[0] != 0x7e && Message[0] != 0xfc);

            if(status != NORMAL && SomeRead)
            {
                ::memcpy (Message, SomeMessage, SomeRead);
                byteCount = SomeRead;
            }

            if(status == NORMAL)
            {
                if(_tblPortBase.getProtocol() == ProtocolWrapIDLC && Message[0] == 0xfc)
                {
                    Message[0] = 0x7e;
                }

                if((status = receiveData(&Message[1], Xfer.getInCountExpected() - 1, Tmot, &byteCount)) != NORMAL)
                {
                    if(status == BADSOCK)
                    {
                        shutdownClose(__FILE__, __LINE__);
                    }
                }

                if(status == NORMAL)
                {
                    byteCount += 1;  // Add the 7e byte into the count
                }
            }
        }
        else
        {
            if((status = receiveData(Message, Xfer.getInCountExpected(), Tmot, &byteCount)) != NORMAL)
            {
                if(status == BADSOCK)
                {
                    shutdownClose(__FILE__, __LINE__);
                }
            }
        }
    }

    if(status == NORMAL)
    {
        if(byteCount != Xfer.getInCountExpected())
        {
            INT oldcount = byteCount;

            if((status = receiveData(Message + byteCount, Xfer.getInCountExpected() - byteCount, Tmot, &byteCount)) != NORMAL)
            {
                if(status == BADSOCK)
                {
                    shutdownClose(__FILE__, __LINE__);
                }
            }

            if(byteCount != Xfer.getInCountExpected())
            {
                byteCount += oldcount;
                status = READTIMEOUT;
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
            status = BADCRC;
        }
    }

    return status;
}

YukonError_t CtiPortTCPIPDirect::outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, list< CtiMessage* > &traceList)
{
    YukonError_t status = NORMAL;

    ULONG    Written;
    ULONG    MSecs;
    ULONG    StartWrite;
    ULONG    ReturnWrite;

    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(!isSimulated() && !isViable())
    {
        checkCommStatus(Dev, TRUE);
    }


    if(_socket == INVALID_SOCKET && !isSimulated())
    {
        status = BADSOCK;        // Invalid Handle really
    }
    else if(Xfer.getOutCount() > 0)
    {
        if(Xfer.getOutCount() > 4096)
        {
            std::cerr << " *** ERROR *** to attempt an OutMess of " << Xfer.getOutCount() << " bytes" << endl;
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
            if( inClear() != NORMAL )
            {
                shutdownClose(__FILE__, __LINE__);
            }

            /* Key the radio */
            raiseRTS();
            /* get the present time */
            MilliTime (&MSecs);

            if(getDelay(RTS_TO_DATA_OUT_DELAY))
            {
                CTISleep (getDelay(RTS_TO_DATA_OUT_DELAY));
            }

            /* Remember when we started writing */
            MilliTime (&StartWrite);

            if( sendData(Xfer.getOutBuffer(), Xfer.getOutCount(), &Written) || Written != Xfer.getOutCount())
            {
                shutdownClose(__FILE__, __LINE__);
                status = PORTWRITE;
            }

            if(status == NORMAL)
            {
                /* Time to do the RTS thing */
                if(getDelay(DATA_OUT_TO_RTS_DOWN_DELAY))
                {
                    /* Now outwait the hardware queue if neccessary */
                    if(Xfer.getOutCount() > 2)
                    {
                        MilliTime (&ReturnWrite);
                        if(ReturnWrite < (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / getTablePortSettings().getBaudRate())))
                        {
                            CTISleep (StartWrite + (((ULONG) (Xfer.getOutCount() - 2) * 10000L) / getTablePortSettings().getBaudRate()) - ReturnWrite);
                        }
                    }

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


INT CtiPortTCPIPDirect::shutdownClose(PCHAR Label, ULONG Line)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    INT   iRet = 0;

    if(_socket != INVALID_SOCKET)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port " << getName() << " closing socket " << _socket;

            if(Label != NULL)
            {
                dout << " from " << Label << " (" << Line << ") ";
            }

            dout << endl;
        }

        shutdown(_socket, 2);

        if(closesocket(_socket))
        {
            iRet = WSAGetLastError();

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Socket close failed. Error = " << iRet << endl;
            }
        }
    }

    _socket = INVALID_SOCKET;

    if(isDialup() && gConfigParms.isOpt("PORTER_TCPIP_DIALOUT_MS_CLOSE_DELAY"))
    {
        Sleep( atoi(gConfigParms.getValueAsString("PORTER_TCPIP_DIALOUT_MS_CLOSE_DELAY").c_str()) );
    }

    return(iRet);
}


YukonError_t CtiPortTCPIPDirect::receiveData(PBYTE Message, LONG Length, ULONG TimeOut, PLONG ReceiveLength)
{
    int WaitCount = 0;
    ULONG bytes_available = 0;

    *ReceiveLength = 0;  // no lies here

    if(_socket == INVALID_SOCKET)
    {
        return BADSOCK;
    }

    /* Wait up to timeout for characters to be available */
    while((ULONG)WaitCount++ <= ((TimeOut * 1000L) / 50L))
    {
        /* Find out if we have any bytes */
        ioctlsocket (_socket, FIONREAD, &bytes_available);

        /* if a specific length specified wait for at least that much */
        if(Length > 0)
        {
            if((LONG)bytes_available < Length)
            {
                CTISleep (50L);
            }
            else
            {
                break;                     // the while loop ends now.
            }
        }
        else                             // Otherwise any length will do
        {
            if(bytes_available == 0)               // Wait for something, or the timeout.
            {
                CTISleep (50L);
            }
            else
            {
                break;                     // the while loop ends now.
            }
        }
    }

    if( bytes_available == 0 )
    {
        return READTIMEOUT;
    }

    if(Length > 0)
    {
        /* Go ahead and actually read some bytes */
        if((*ReceiveLength = recv(_socket, (CHAR*)Message, Length, 0)) <= 0)
        {
            shutdownClose(__FILE__, __LINE__);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Read from Terminal Server failed:  " << WSAGetLastError() << endl;
            }
            return(TCPREADERROR);
        }

        if(*ReceiveLength < Length)
        {
            if(bytes_available >= Length)     // The stupid thing told us the bytes were there!
            {
                int bytesrecv = 0;

                while(*ReceiveLength < Length)
                {
                    bytesrecv = recv(_socket, (CHAR*)&Message[*ReceiveLength], Length - *ReceiveLength, 0);

                    if(bytesrecv > 0)
                    {
                        *ReceiveLength += bytesrecv;
                    }
                }
            }
            else
            {
                return READTIMEOUT;
            }
        }
    }
    else
    {
        /* Go ahead and actually read some bytes */
        if((*ReceiveLength = recv(_socket, (CHAR*)Message, -Length, 0)) <= 0)
        {
            shutdownClose(__FILE__, __LINE__);
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Read from Terminal Server failed:  " << WSAGetLastError() << endl;
            }
            return TCPREADERROR;
        }
    }

    return NORMAL;
}



/* Routine to send a message to a TCP/IP Terminal Server port */
INT CtiPortTCPIPDirect::sendData(PBYTE Message, ULONG Length, PULONG Written)
{
    INT bytesSent;

    if(_socket == INVALID_SOCKET)
    {
        openPort();
    }

    if( (bytesSent = send (_socket, (CHAR*)Message, Length, 0)) == SOCKET_ERROR )
    {
        shutdownClose(__FILE__, __LINE__);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Error Sending Message to Terminal Server:  " << WSAGetLastError() << endl;
        }
        return TCPWRITEERROR;
    }

    *Written = bytesSent;

    /* On normal terminal server it does not matter if we sit */
    return NORMAL;
}

void CtiPortTCPIPDirect::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    try
    {
        Inherited::DecodeDatabaseReader(rdr);
        _tcpIpInfo.DecodeDatabaseReader(rdr);       // get the base class handled
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiPortTCPIPDirect::DecodeDialableDatabaseReader(Cti::RowReader &rdr)
{
    if(_dialable)
    {
        _dialable->DecodeDatabaseReader(rdr);
    }
}

string CtiPortTCPIPDirect::getSQLCoreStatement()
{
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                   "CP.alarminhibit, CP.commonprotocol, CP.performancealarm, CP.performthreshold, "
                                   "CP.sharedporttype, CP.sharedsocketnumber, PST.baudrate, PST.cdwait, PST.linesettings, "
                                   "TMG.pretxwait, TMG.rtstotxwait, TMG.posttxwait, TMG.receivedatawait, TMG.extratimeout, "
                                   "TSV.ipaddress, TSV.socketportnumber, TSV.encodingkey, TSV.encodingtype "
                               "FROM YukonPAObject YP, CommPort CP, PortSettings PST, PortTiming TMG, PortTerminalServer TSV "
                               "WHERE YP.paobjectid = CP.portid AND YP.paobjectid = PST.portid AND "
                                   "YP.paobjectid = TMG.portid AND YP.paobjectid = TSV.portid";

    return sql;
}

YukonError_t CtiPortTCPIPDirect::waitForPortResponse(PULONG ResponseSize,  PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    YukonError_t status = BADPORT;

    if(_dialable)
    {
        status = _dialable->waitForResponse(ResponseSize,Response,Timeout,ExpectedResponse);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiPortTCPIPDirect::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    return sendData( (PBYTE)pBuf, BufLen, pBytesWritten );
}

INT CtiPortTCPIPDirect::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    LONG rl = 0;

    INT status = receiveData( (PBYTE)pBuf, BufLen, timeout, &rl );

    *pBytesRead = rl;

    return status;
}

bool CtiPortTCPIPDirect::isViable()
{
    if( isSimulated() )
    {
        return true;
    }

    if( isSocketBroken() )
    {
        shutdownClose(__FILE__, __LINE__);
    }

    return _socket != INVALID_SOCKET;
}

bool CtiPortTCPIPDirect::isSocketBroken() const
{
    if( _socket == INVALID_SOCKET )
    {
        return false;
    }

    fd_set read_sockets;

    FD_ZERO(&read_sockets);
    FD_SET(_socket, &read_sockets);

    timeval tv = {0, 0};

    switch( select(0, &read_sockets, 0, 0, &tv) )
    {
        case 0:             return false;
        case 1:             break;

        default:
        case SOCKET_ERROR:  return true;
    }

    // How many are available ??
    ULONG ulTemp;
    if( ioctlsocket(_socket, FIONREAD, &ulTemp) == SOCKET_ERROR )
    {
        return true;
    }

    scoped_array<char> buf(new char[ulTemp]);

    if( recv(_socket, buf.get(), (int)ulTemp, MSG_PEEK) <= 0 )
    {
        return true;
    }

    return false;
}

YukonError_t CtiPortTCPIPDirect::reset(INT trace)
{
    if(_dialable)
    {
        _dialable->reset(trace);
    }

    return NORMAL;
}

YukonError_t CtiPortTCPIPDirect::setup(INT trace)
{
    if(_dialable)
    {
        _dialable->setup(trace);
    }

    return NORMAL;
}

YukonError_t CtiPortTCPIPDirect::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    YukonError_t status = NORMAL;

    pair< bool, YukonError_t > portpair = checkCommStatus(Device, trace);

    status = portpair.second;

    if( portpair.first && status == NORMAL )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " opened for connect." << endl;
        }
    }

    if(_dialable)
    {
        status = _dialable->connectToDevice(Device, LastDeviceId,trace);
    }
    else
    {
        status = Inherited::connectToDevice(Device, LastDeviceId,trace);
    }
    return status;
}

INT  CtiPortTCPIPDirect::disconnect(CtiDeviceSPtr Device, INT trace)
{
    INT status = NORMAL;

    status = Inherited::disconnect(Device,trace);

    if(!status && (_dialable || gConfigParms.isOpt("PORTER_RELEASE_IDLE_PORTS", "true")) )
    {
        close(trace);                           // Release the port handle
    }

    return status;
}

BOOL CtiPortTCPIPDirect::connected()
{
    if(_dialable && getTablePortSettings().getCDWait() != 0 && getConnectedDevice() > 0)
    {
        if(!dcdTest())    // No DCD and we think we are connected!  This is BAD.
        {
            disconnect(CtiDeviceSPtr(), FALSE);
        }
    }

    return Inherited::connected();
}

BOOL CtiPortTCPIPDirect::shouldDisconnect() const
{
    BOOL bRet = Inherited::shouldDisconnect();

    if(_dialable)
    {
        bRet = _dialable->shouldDisconnect();
    }

    return bRet;
}

CtiPort& CtiPortTCPIPDirect::setShouldDisconnect(BOOL b)
{
    if(_dialable)
    {
        _dialable->setShouldDisconnect(b);
    }

    return *this;
}

