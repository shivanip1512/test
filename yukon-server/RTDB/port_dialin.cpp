

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   port_dialin
*
* Date:   12/03/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision $
* DATE         :  $Date: 2002/12/11 23:37:31 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dsm2.h"
#include "logger.h"
#include "port_dialin.h"

CtiPortDialin::CtiPortDialin() :
_shouldDisconnect(FALSE)
{}

CtiPortDialin::CtiPortDialin(const CtiPortDialin& aRef)
{
    *this = aRef;
}

CtiPortDialin::~CtiPortDialin()
{
}

CtiPortDialin& CtiPortDialin::setSuperPort(CtiPort *port)
{
    _superPort = port;
    return *this;
}

RWCString CtiPortDialin::getDialedUpNumber() const
{
    return _dialedUpNumber;
}
RWCString& CtiPortDialin::getDialedUpNumber()
{
    return _dialedUpNumber;
}

RWCString  CtiPortDialin::getModemInit() const
{
    return _tblPortDialup.getModemInitString();
}

RWCString& CtiPortDialin::getModemInit()
{
    return _tblPortDialup.getModemInitString();
}


CtiTablePortDialup CtiPortDialin::getTablePortDialup() const
{
    return _tblPortDialup;
}

CtiTablePortDialup& CtiPortDialin::getTablePortDialup()
{
    return _tblPortDialup;
}

CtiPortDialin& CtiPortDialin::setTablePortDialup(const CtiTablePortDialup& aRef)
{
    _tblPortDialup = aRef;
    return *this;
}

INT CtiPortDialin::connectToDevice(CtiDevice *Device, INT trace)
{
    INT status     = NORMAL;
    ULONG DeviceCRC = Device->getUniqueIdentifier();

    /*
     *  This next block Makes sure we hang up if we are connected to a different device's PhoneCRC
     *
     *  If getCDWait is zero, we presume that the device does NOT provide DCD feedback.  In that case,
     *  we must assume that we still are connected and should send a hangup.
     */

    if(_superPort->isViable() && _superPort->connected() && !_superPort->connectedTo(DeviceCRC))      // This port connected to a device, and is not connected to this device.
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        // If we cannot use dcd to tell if we are online, we must do a hangup
        // dcdTest indicates that we ARE on/off line if !FALSE, we must hangup.
        if( _superPort->getTablePortSettings().getCDWait() == 0  || _superPort->dcdTest() )
        {
            _superPort->disconnect(Device, trace);
        }
    }

    /*
     *  At this point we either connected to the device we need (Device), or are not connected to any device.
     */

    /*
     *  Again, the timing setting for DCD is relevant.
     *  If the port has a zero DCD time, we must assume that the only time we need to
     *  connect is if the "to-be-dialed" device represented by DeviceCRC, is not listed as the
     *  currently connected device.
     */

    if( !_superPort->connectedTo(DeviceCRC) )
    {
        if( (status = _superPort->checkCommStatus(trace)) == NORMAL)
        {
            RWCString number = getTablePortDialup().getPrefixString() + Device->getPhoneNumber();
            /*  Now Dial */
            if(modemConnect((char*)number.data(), trace, _superPort->getTablePortSettings().getCDWait() != 0))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error Dialing Modem on Port " << _superPort->getPortID() << ":  " << _superPort->getName() << endl;
                }

                _superPort->disconnect(Device, trace);    // Poke the handle down.

                status = DIALUPERROR;
            }
        }
    }

    if(status == NORMAL)
    {
        /* Make sure the remotes match */
        _superPort->setConnectedDevice( Device->getID() );
        _superPort->setConnectedDeviceUID( DeviceCRC );
        setDialedUpNumber( Device->getPhoneNumber() );
    }

    return status;
}

INT CtiPortDialin::disconnect(CtiDevice *Device, INT trace)
{
    INT status = NORMAL;

    try
    {
        status = modemHangup( trace );

        if(status)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << _superPort->getName() << " modemHangup reported status " << status << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return status;
}

INT CtiPortDialin::openPort()
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return status;
}

INT CtiPortDialin::writePort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesWritten)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

INT CtiPortDialin::readPort(PVOID pBuf, ULONG BufLen, ULONG timeout, PULONG pBytesRead)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}

INT CtiPortDialin::close(INT trace)
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return status;
}

INT CtiPortDialin::reset(INT trace)
{
    setDialedUpNumber(RWCString());
    return modemReset(_superPort->getPortID(), trace);
}

INT CtiPortDialin::setup(INT trace)
{
    setDialedUpNumber(RWCString());
    return modemSetup(trace, (_superPort->getTablePortSettings().getCDWait() != 0));
}

CtiPortDialin& CtiPortDialin::operator=(const CtiPortDialin& aRef)
{
    if(this != &aRef)
    {
        _tblPortDialup = aRef.getTablePortDialup();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

void CtiPortDialin::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    CtiTablePortDialup::getSQL(db, keyTable, selector);
}

void CtiPortDialin::DecodeDatabaseReader(RWDBReader &rdr)
{
    _tblPortDialup.DecodeDatabaseReader(rdr);       // get the base class handled
}

/* Routine to force the reset of modem */
INT CtiPortDialin::modemReset(USHORT Trace, BOOL dcdTest)
{
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG BytesWritten;
    ULONG i = 0;
    static ULONG tCount = 0;

    if(!_superPort->isViable())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        return BADPORT;
    }

    /* set the timeout on read to 1 second */
    _superPort->setPortReadTimeOut(1);
    _superPort->lowerRTS();
    _superPort->lowerDTR();
    CTISleep( 500L );
    _superPort->raiseDTR();
    _superPort->raiseRTS();
    CTISleep( 500L );

    /* If we do not have CTS it is a problem */
    if(!(_superPort->isCTS()))
    {
        if(!(++tCount % 300))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "  Port " << _superPort->getName() << " No Modem CTS.  Modem may be off or configured wrong" << endl;
        }

        _superPort->lowerDTR();
        CTISleep( 500L );
        _superPort->raiseDTR();
        _superPort->raiseRTS();
        CTISleep( 500L );

        if(!(_superPort->isCTS()))
        {
            return READTIMEOUT;
        }
    }

    /* Try five times to intialize modem */
    for(i = 0; i < 5; i++)
    {
        /* Wait for CTS */
        if(!(_superPort->isCTS()))
        {
            if(!(++tCount % 300))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Port " << _superPort->getName() << " No Modem CTS..." << endl;
                }
            }

            _superPort->lowerDTR();
            CTISleep( 500L );
            _superPort->raiseDTR();
            _superPort->raiseRTS();
            CTISleep( 500L );

            if(!(_superPort->isCTS()))
            {
                return READTIMEOUT;
            }
        }

        /* Clear the buffer */
        _superPort->inClear();
        ResponseSize = sizeof (Response);

        /* Wait to see if we get the no carrier message */
        if( !(_superPort->waitForPortResponse(&ResponseSize, Response, 1)) )      // See if we get something back from this guy.
        {
            if(Trace)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << _superPort->getName() << " Received from Modem:  " << Response <<  endl;
            }
        }

        ResponseSize = sizeof (Response);
        // Make an attempt to determine if we are in the command mode
        _superPort->writePort("AT\r", 3, 1, &BytesWritten);    // Attention to the modem.
        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  AT" << endl;
        }

        /* Wait for a response or till we time out */
        if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
        {
            // Just in case we are out to lunch here...
            _superPort->writePort("+++", 3, 1, &BytesWritten);    // Attention to the modem.

            if(Trace)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  +++" << endl;
            }

            ResponseSize = sizeof (Response);

            /* Wait for a response or till we time out */
            if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
            {
                if(Trace)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Port " << _superPort->getName() << " Modem Response Timeout" << endl;
                }

                _superPort->writePort("\r", 1, 1, &BytesWritten);    // Get rid of the trash that didn't give a good return.
            }
        }
        else if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Received from Modem:  " << Response <<  endl;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << _superPort->getName() << " modem reset" << endl;
        }
        if(gLogPorts)
        {
            CtiLockGuard<CtiLogger> portlog_guard(_superPort->getPortLog());
            _superPort->getPortLog() << RWTime() << " " << _superPort->getName() << " modem reset" << endl;
        }

        /* Make sure that we got OK */
        if(!(strnicmp(Response, "OK", 2)))
        {
            return(NORMAL);
        }
    }

    return(!NORMAL);
}



/* Routine to send setup string(s) to the modem */
INT CtiPortDialin::modemSetup(USHORT Trace, BOOL dcdTest)
{
    ULONG BytesWritten;
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG i, j;

    // Try up to five times
    for(j = 0; j < 5; j++)
    {
        CTISleep (250L);

        /* Flush the input buffer */
        _superPort->inClear();

        if(_superPort->writePort((PVOID)(getModemInit().data()), strlen((char*)getModemInit().data()), 1, &BytesWritten) || BytesWritten != strlen ((char*)getModemInit().data()))
        {
            return(!NORMAL);
        }

        /* Send the CR */
        _superPort->writePort("\r", 1, 1, &BytesWritten);

        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  " << getModemInit() << endl;
        }

        ResponseSize = sizeof (Response);

        /* Wait for a response or till we time out */
        if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
        {
            if(Trace)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << _superPort->getName() << " Modem Response Timeout" << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            continue;
        }

        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Received from Modem:  " << Response << endl;
        }

        /* Make sure that we got OK */
        if(!(strnicmp (Response, "OK", 2)))
        {
            break;
        }
        else if(!(strnicmp (Response, "ERROR", 5)))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Port " << _superPort->getName() << " Received from Modem:  " << Response << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  The modem init string \"" << getModemInit() << "\" was rejected." << endl;
            }
            return(DIALUPERROR);
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << _superPort->getName() << " modem setup " << (j >= 5 ? "failed" : "successful") << endl;
    }

    if(gLogPorts)
    {
        CtiLockGuard<CtiLogger> portlog_guard(_superPort->getPortLog());
        _superPort->getPortLog() << RWTime() << " " << _superPort->getName() << " modem setup " << (j >= 5 ? "failed" : "successful") << endl;
    }

    if(j >= 5)
    {
        // _superPort->close(TRUE);
        return(!NORMAL);
    }

    return(NORMAL);
}

/* Routine to establish modem connection */
INT CtiPortDialin::modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest)
{
    INT status = NORMAL;
    ULONG BytesWritten, i;
    CHAR MyMessage[100];
    CHAR Response[100];
    ULONG ResponseSize;

    /* set the timeout on read to 1 second */
    _superPort->setPortReadTimeOut(1);

    if(Message[0] == 'A' || Message[0] == 'a')
    {
        strcpy (MyMessage, Message);
    }
    else
    {
        strcpy (MyMessage, "ATDT");
        strcat (MyMessage, Message);
    }

    /* Send the CR */
    strcat (MyMessage, "\r");

    CTISleep ( 250L);

    /* Flush the input buffer */
    _superPort->inClear();

    /* Perform the dial */
    if(_superPort->writePort(MyMessage, strlen(MyMessage), 1, &BytesWritten) || BytesWritten != strlen(MyMessage))
    {
        return(!NORMAL);
    }

    if(Trace)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  " << MyMessage << endl;
    }

    ResponseSize = sizeof (Response);

    /* Wait for a response from the modem */
    if(_superPort->waitForPortResponse(&ResponseSize, Response, ModemConnectionTimeout, "CONNECT"))
    {
        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Modem Response Timeout" << endl;
        }
        status = READTIMEOUT;
    }
    else
    {
        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Received from Modem:  " << Response <<  endl;
        }

        /* We have a response so see what it is */
        if(!(strnicmp (Response, "CONNECT", 7)))
        {
            /* Wait up to 2 seconds for CD */
            if(dcdTest == TRUE)
            {
                for(i = 0; i < 20; i++)
                {
                    if(_superPort->isDCD())
                    {
                        /* Raise RTS */
                        _superPort->raiseRTS();
                        CTISleep( 200L);       // CTISleep ( 2000L );
                        _superPort->inClear();

                        status = NORMAL;
                    }
                    else
                    {
                        CTISleep (100L);
                    }
                }
            }
            else     // Proceed, ignoring DCD
            {
                /* Raise RTS */
                _superPort->raiseRTS();
                CTISleep ( 200L);       // CTISleep ( 2000L );
                _superPort->inClear();
                status = NORMAL;
            }
        }
        else if(!(strnicmp (Response, "NO CARRIER", 10)))
        {
            status = !NORMAL;
        }
        else if(!(strnicmp (Response, "ERROR", 5)))
        {
            status = !NORMAL;
        }
        else if(!(strnicmp (Response, "NO DIAL TONE", 12)))
        {
            status = !NORMAL;
        }
        else if(!(strnicmp (Response, "NO ANSWER", 9)))
        {
            status = !NORMAL;
        }
        else if(!(strnicmp (Response, "BUSY", 4)))
        {
            status = !NORMAL;
        }
        else if(!(strnicmp (Response, "NO DIALTONE", 12)))
        {
            status = !NORMAL;
        }
        else
        {
            status = !NORMAL;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Modem responded " << Response << endl;
            }
        }
    }

    if(gLogPorts)
    {
        RWCString MdmResponse;

        if(Response[0] != '\0')
        {
            MdmResponse = RWCString("Modem responded with ") + RWCString(Response);
        }
        else
        {
            MdmResponse = RWCString("Modem did not respond");
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << _superPort->getName() << " modem connect to " << Message << ". " << MdmResponse << endl;
        }

        CtiLockGuard<CtiLogger> portlog_guard(_superPort->getPortLog());
        _superPort->getPortLog() << RWTime() << " " << _superPort->getName() << " modem connect to " << Message << ". " << MdmResponse << endl;
    }

    return(status);
}


INT CtiPortDialin::modemHangup(USHORT Trace, BOOL dcdTest)
{
    INT status = NORMAL;
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG BytesWritten;
    ULONG i;

    Trace = TRUE;

    if(_superPort->isViable())
    {
        _superPort->lowerDTR();
        _superPort->lowerRTS();
        CTISleep( 250L );
    }

    return status;
}

CtiPortDialin& CtiPortDialin::setDialedUpNumber(const RWCString &str)
{
    _dialedUpNumber = str;
    return *this;
}

BOOL CtiPortDialin::shouldDisconnect() const
{
    return _shouldDisconnect;
}

void CtiPortDialin::setShouldDisconnect(BOOL b)
{
    _shouldDisconnect = b;
}


/* Routine to wait for a response or a timeout */
INT CtiPortDialin::waitForResponse(PULONG ResponseSize, PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse)
{
    ULONG   i , j;
    ULONG   BytesRead;
    INT     status = READTIMEOUT;

    i = 0;        // i represents the count of bytes in response.
    j = 0;

    if(!_superPort->isViable())
    {
        return(NOTNORMAL);
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    /* Set the timeout on read to 1 second */
    // 012201 CGP This clears the buffer on WIN32.//  SetReadTimeOut (PortHandle, 1);

    while(j <= Timeout)
    {
        if(_superPort->readPort(&Response[i], 1, 1, &BytesRead) || BytesRead == 0) // Reads one byte into the buffer
        {
            j++;        // Fails once per second if no bytes read
            continue;
        }

        if(i == 0)                                            // Special case for the zeroeth byte
        {
            if(Response[i] == 0x0a || Response[i] == 0x0d || Response[i] == 0x00)    // Make sure it is not a CR or LF or null
            {
                continue;
            }
        }

        /* Check what this is */
        if(Response[i] == '\r')
        {
            // null out any CR LF
            Response[i] = '\0';

            // handle non-verbal OK
            if(!(strcmp(ExpectedResponse, "OK")) && !(strcmp(Response, "0")))
            {
                // 0 is the same as OK for none verbal
                strcpy(Response,"OK");
            }

            // check for expected return
            if(ExpectedResponse == NULL || strstr(Response, ExpectedResponse) != NULL)
            {
                // it was the command we wanted or we did not want a specific response
                *ResponseSize = i;
                status = NORMAL;
                break; // the while
            }
            else if(validModemResponse(Response))
            {
                // this is valid (unexpected response), or we did not specify.
                *ResponseSize = strlen(Response);
                status = NORMAL;
                break; // the while
            }

            // look for CTIDBG_new message
            Response[0] = '\0';
            i = 0;
        }
        else
        {
            i++;

            if(i + 2 > *ResponseSize)     // are we still within the size limit.
            {
                status = NOTNORMAL;
                break; // the while
            }
        }
    }

    return status;
}

// known valid modem returns -- Note 0 is the same as OK when not in verbal mode
/* returns TRUE if a valid modem return is found */
BOOL CtiPortDialin::validModemResponse (PCHAR Response)
{
    static PCHAR responseText[] = {
        "OK",
        "ERROR",
        "BUSY",
        "NO CARRIER",
        "NO DIALTONE",
        "NO ANSWER",
        "NO DIAL TONE",
        NULL};

   BOOL isValid = FALSE;
   int count;

   for(count = 0; responseText[count] != NULL; count++)
   {
      if( strstr(Response, responseText[count]) != NULL )
      {
         // Valid modem return
         strcpy(Response, responseText[count]);
         isValid = TRUE;
         break;
      }
   }

   if( isValid != TRUE )
   {
      if(!strcmp(Response, "0"))      // Zero is a special case of OK
      {
         // Valid modem return
         strcpy(Response, "OK");
         isValid = TRUE;
      }
   }

   return(isValid);
}


CtiHayesModem& CtiPortDialin::getModem()
{
    return modem.setPort( _superPort );
}

