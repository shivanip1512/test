

/*-----------------------------------------------------------------------------*
*
* File:   port_dialout
*
* Date:   9/17/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/05/05 15:31:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "dsm2.h"
#include "logger.h"
#include "port_dialout.h"

CtiPortDialout::CtiPortDialout()
{
}

CtiPortDialout::CtiPortDialout(const CtiPortDialout& aRef)
{
    *this = aRef;
}

CtiPortDialout::~CtiPortDialout()
{
}

CtiPortDialout& CtiPortDialout::operator=(const CtiPortDialout& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

INT CtiPortDialout::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    INT status     = NORMAL;
    ULONG DeviceCRC = Device->getUniqueIdentifier();
    LastDeviceId = 0L;

    /*
     *  This next block Makes sure we hang up if we are connected to a different device's PhoneCRC
     *
     *  If getCDWait is zero, we presume that the device does NOT provide DCD feedback.  In that case,
     *  we must assume that we still are connected and should send a hangup.
     */
    if(_superPort->isViable() && _superPort->connected())
    {
        if(!_superPort->connectedTo(DeviceCRC))      // This port connected to a device, and is not connected to this device.
        {
            // If we cannot use dcd to tell if we are online, we must do a hangup
            // dcdTest indicates that we ARE on/off line if !FALSE, we must hangup.
            if( _superPort->getCDWait() == 0  || _superPort->dcdTest() )
            {
                _superPort->disconnect(Device, trace);
            }
        }
        else if( _superPort->connectedTo(DeviceCRC) && _superPort->getConnectedDevice() != Device->getID() )
        {
            LastDeviceId = _superPort->getConnectedDevice();    // This is a device swap...
            Device->setLogOnNeeded(FALSE);
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
        pair< bool, INT > portpair = _superPort->checkCommStatus(Device, trace);

        status = portpair.second;

        if( status == NORMAL )
        {
            RWCString number = getTablePortDialup().getPrefixString() + Device->getPhoneNumber();
            /*  Now Dial */
            if(NORMAL != (status = modemConnect((char*)number.data(), trace, _superPort->getCDWait() != 0)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Error Dialing Modem on Port " << _superPort->getPortID() << ":  " << _superPort->getName() << endl;
                }

                _superPort->disconnect(Device, trace);    // Poke the handle down.
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

INT CtiPortDialout::disconnect(CtiDeviceSPtr Device, INT trace)
{
    INT status = NORMAL;

    status = modemHangup( trace );

    if(status)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << _superPort->getName() << " modemHangup reported status " << status << endl;
    }

    return status;
}

INT CtiPortDialout::reset(INT trace)
{
    INT status = NORMAL;

    setDialedUpNumber(RWCString());

    if(_superPort)
    {
        status = modemReset(_superPort->getPortID(), trace);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiPortDialout::setup(INT trace)
{
    setDialedUpNumber(RWCString());
    return modemSetup(trace, (_superPort->getCDWait() != 0));
}

INT CtiPortDialout::close(INT trace)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NORMAL;
}


/* Routine to force the reset of modem */
INT CtiPortDialout::modemReset(USHORT Trace, BOOL dcdTest)
{
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG BytesWritten;
    ULONG i = 0;
    static ULONG tCount = 0;

    try
    {
        /* set the timeout on read to 1 second */
        _superPort->setPortReadTimeOut(1000);
        _superPort->lowerRTS();
        _superPort->lowerDTR();
        CTISleep( 500L );
        _superPort->raiseDTR();
        _superPort->raiseRTS();
        CTISleep( 500L );

        /* If we do not have CTS it is a problem */
        if(!(_superPort->ctsTest()))
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

            if(!(_superPort->ctsTest()))
            {
                return READTIMEOUT;
            }
        }

        /* Try five times to intialize modem */
        for(i = 0; i < 5; i++)
        {
            /* Wait for CTS */
            if(!(_superPort->ctsTest()))
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

                if(!(_superPort->ctsTest()))
                {
                    return READTIMEOUT;
                }
            }

            /* Clear the buffer */
            _superPort->inClear();
            ResponseSize = sizeof (Response);

            /* Wait to see if we get the no carrier message */
            if( (_superPort->waitForPortResponse(&ResponseSize, Response, 1)) == NORMAL )      // See if we get something back from this guy.
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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ") " << endl;
    }

    return(ErrPortDialupConnect_Port);
}



/* Routine to send setup string(s) to the modem */
INT CtiPortDialout::modemSetup(USHORT Trace, BOOL dcdTest)
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

        size_t initlen = getTablePortDialup().getModemInitString().length();

        if(_superPort->writePort((PVOID)(getTablePortDialup().getModemInitString().data()),
                                 initlen, 1, &BytesWritten) || BytesWritten != initlen)
        {
            return(ErrPortDialupConnect_Port);
        }

        /* Send the CR */
        _superPort->writePort("\r", 1, 1, &BytesWritten);

        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  " << getTablePortDialup().getModemInitString() << endl;
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
                dout << "  The modem init string \"" << getTablePortDialup().getModemInitString() << "\" was rejected." << endl;
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
        return(ErrPortDialupConnect_Port);
    }

    return(NORMAL);
}

/* Routine to establish modem connection */
INT CtiPortDialout::modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest)
{
    INT status = NORMAL;
    ULONG BytesWritten, i;
    CHAR MyMessage[100];
    CHAR Response[100];
    ULONG ResponseSize;

    /* set the timeout on read to 1 second */
    _superPort->setPortReadTimeOut(1000);

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
        return(ErrPortDialupConnect_Port);
    }

    if(Trace)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Port " << _superPort->getName() << " Sent to Modem:  " << MyMessage << endl;
    }

    ResponseSize = sizeof (Response);

    /* Wait for a response from the modem */
    if( NORMAL != (status = _superPort->waitForPortResponse(&ResponseSize, Response, ModemConnectionTimeout, "CONNECT")) )
    {
        if(Trace)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port " << _superPort->getName() << " Modem Response Timeout" << endl;
        }
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
                    if(_superPort->dcdTest())
                    {
                        /* Raise RTS */
                        _superPort->raiseRTS();
                        CTISleep( 200L);       // CTISleep ( 2000L );
                        _superPort->inClear();

                        status = NORMAL;
                        break;                  // the for loop.  dcd is detected!
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
            status = ErrPortDialupConnect_Device;
        }
        else if(!(strnicmp (Response, "ERROR", 5)))
        {
            status = ErrPortDialupConnect_Port;
        }
        else if(!(strnicmp (Response, "NO DIAL TONE", 12)))
        {
            status = ErrPortDialupConnect_Port;
        }
        else if(!(strnicmp (Response, "NO ANSWER", 9)))
        {
            status = ErrPortDialupConnect_Device;
        }
        else if(!(strnicmp (Response, "BUSY", 4)))
        {
            status = ErrPortDialupConnect_Device;
        }
        else if(!(strnicmp (Response, "NO DIALTONE", 12)))
        {
            status = ErrPortDialupConnect_Port;
        }
        else
        {
            status = ErrPortDialupConnect_Port;

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


INT CtiPortDialout::modemHangup(USHORT Trace, BOOL dcdTest)
{
    INT status = NORMAL;
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG BytesWritten;
    ULONG i;

    if(_superPort->isViable())
    {
        /* set the timeout on read to 1 second */
        _superPort->setPortReadTimeOut(1000);

        _superPort->lowerRTS();
        _superPort->lowerDTR();
        CTISleep( 250L );
        _superPort->raiseDTR();
        _superPort->raiseRTS();

        /* Try five times to verify the modem's attention is ours. */
        for(i = 0; i < 5; i++)
        {
            // Kick it good!
            _superPort->lowerDTR();
            CTISleep( 500L );
            _superPort->raiseDTR();
            _superPort->raiseRTS();
            CTISleep( 500L );

            /* Clear the buffer */
            _superPort->inClear();
            ResponseSize = sizeof (Response);

            /* Wait to see if we get the no carrier message */
            if( NORMAL == (_superPort->waitForPortResponse(&ResponseSize, Response, 1, NULL)) )      // See if we get something back from this guy.
            {
                if(Trace)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << _superPort->getName()  << " Received from Modem:  " << Response <<  endl;
                }
            }

            // Check for command mode by pegging the modem with an AT.
            _superPort->writePort("AT\r", 3, 1, &BytesWritten);

            if(_superPort->waitForPortResponse(&ResponseSize, Response, 1, "OK"))
            {
                // Maybe we are not in command mode...
                _superPort->writePort("+++", 3, 1, &BytesWritten);    // Escape to command mode please
                if(Trace)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << _superPort->getName()  << " Sent to Modem:  +++" << endl;
                }

                CTISleep ( 1500L );
                _superPort->writePort("AT\r", 3, 1, &BytesWritten);   // escape does not give response do AT

                if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
                {
                    if(Trace)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << _superPort->getName()  << " Modem Response Timeout (will re-try)" << endl;
                    }

                    status = READTIMEOUT;
                    continue;   // We are not getting anything here!
                }
                else if(Trace)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << _superPort->getName()  << " Received from Modem:  " << Response <<  endl;

                    status = NORMAL;
                    break;
                }
            }
            else
            {
                break;   //  We got an OK back on the "AT"
            }
        }

        _superPort->inClear();

        if(status == NORMAL)
        {
            /* Attempt to hangup the modem (on hook) */
            for(i = 0; i < 5; i++)
            {
                CTISleep ( 250L);

                /* Send it the off hook message */
                _superPort->writePort("ATH0\r", 5, 1, &BytesWritten);

                if(Trace)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << _superPort->getName()  << " Sent to Modem:  ATH0" << endl;
                }

                ResponseSize = sizeof (Response);

                if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
                {
                    if(Trace)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << _superPort->getName()  << " Modem Response Timeout" << endl;
                    }

                    status = READTIMEOUT;     // Hangup Failed
                    continue;
                }
                else
                {
                    if(Trace)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << _superPort->getName()  << " Received from Modem:  " << Response <<  endl;
                    }

                    status = NORMAL;
                    break;   // Wow, we just went on hook!
                }
            }
        }

        if(status == NORMAL && dcdTest == TRUE)
        {
            /* Wait for carrier to drop */
            i = 0;
            while(i < 20)
            {
                if(!(_superPort->dcdTest()))
                {
                    CTISleep ( 1500L );
                    status = NORMAL;
                    break;
                }

                CTISleep ( 100 );
                i++;
            }
        }


        if(status != NORMAL)
        {
            /* something is wrong so reset and setup the modem */
            modemReset(Trace, dcdTest);
            status = modemSetup(Trace, dcdTest);
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << _superPort->getName() << " modem hangup " << (getDialedUpNumber().isNull ? "" : RWCString("on ") + getDialedUpNumber()) << endl;
    }

    if(gLogPorts)
    {
        CtiLockGuard<CtiLogger> portlog_guard(_superPort->getPortLog());
        _superPort->getPortLog() << RWTime() << " " << _superPort->getName() << " modem hangup " << (getDialedUpNumber().isNull ? "" : RWCString("on ") + getDialedUpNumber()) << endl;
    }

    _superPort->setConnectedDevice(0);
    _superPort->setConnectedDeviceUID(-1);
    setDialedUpNumber(RWCString());

    return status;
}
