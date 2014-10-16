#include "precompiled.h"


/*-----------------------------------------------------------------------------*
*
* File:   port_dialout
*
* Date:   9/17/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2005/12/20 17:20:28 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "dsm2.h"
#include "logger.h"
#include "port_dialout.h"

using std::iostream;
using std::pair;
using std::endl;
using std::string;

CtiPortDialout::CtiPortDialout()
{
}

YukonError_t CtiPortDialout::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    YukonError_t status = ClientErrors::None;
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
            Device->setLogOnNeeded(false);
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
        pair< bool, YukonError_t > portpair = _superPort->checkCommStatus(Device, trace);

        status = portpair.second;

        if( status == ClientErrors::None )
        {
            string number = getTablePortDialup().getPrefixString() + Device->getPhoneNumber();
            /*  Now Dial */
            if(ClientErrors::None != (status = modemConnect((char*)number.c_str(), trace, _superPort->getCDWait() != 0)))
            {
                CTILOG_ERROR(dout, "Could not dial Modem on Port "<< _superPort->getPortID() <<":  "<< _superPort->getName());

                _superPort->disconnect(Device, trace);    // Poke the handle down.
            }
        }
    }

    if(status == ClientErrors::None)
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
    INT status = ClientErrors::None;

    status = modemHangup( trace );

    if(status)
    {
        CTILOG_ERROR(dout, _superPort->getName() <<" modemHangup reported status "<< status);
    }

    return status;
}

YukonError_t CtiPortDialout::reset(INT trace)
{
    YukonError_t status = ClientErrors::None;

    setDialedUpNumber(string());

    if(_superPort)
    {
        status = modemReset(_superPort->getPortID(), trace);
    }
    else
    {
        CTILOG_ERROR(dout, "_superPort is Null");
    }

    return status;
}

INT CtiPortDialout::setup(INT trace)
{
    setDialedUpNumber(string());
    return modemSetup(trace, (_superPort->getCDWait() != 0));
}

INT CtiPortDialout::close(INT trace)
{
    CTILOG_ERROR(dout, "CtiPortDialout cannot be closed");

    return ClientErrors::None;
}


/* Routine to force the reset of modem */
YukonError_t CtiPortDialout::modemReset(USHORT Trace, BOOL dcdTest)
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
                CTILOG_ERROR(dout, "Port "<< _superPort->getName() <<" No Modem CTS. Modem may be off or configured wrong");
            }

            _superPort->lowerDTR();
            CTISleep( 500L );
            _superPort->raiseDTR();
            _superPort->raiseRTS();
            CTISleep( 500L );

            if(!(_superPort->ctsTest()))
            {
                return ClientErrors::ReadTimeout;
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
                    CTILOG_ERROR(dout, "Port "<< _superPort->getName() <<" No Modem CTS...");
                }

                _superPort->lowerDTR();
                CTISleep( 500L );
                _superPort->raiseDTR();
                _superPort->raiseRTS();
                CTISleep( 500L );

                if(!(_superPort->ctsTest()))
                {
                    return ClientErrors::ReadTimeout;
                }
            }

            /* Clear the buffer */
            _superPort->inClear();
            ResponseSize = sizeof (Response);

            /* Wait to see if we get the no carrier message */
            if( (_superPort->waitForPortResponse(&ResponseSize, Response, 1)) == ClientErrors::None )      // See if we get something back from this guy.
            {
                if(Trace)
                {
                    CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  " << Response);
                }
            }

            ResponseSize = sizeof (Response);
            // Make an attempt to determine if we are in the command mode
            _superPort->writePort("AT\r", 3, 1, &BytesWritten);    // Attention to the modem.
            if(Trace)
            {
                CTILOG_INFO(dout, "Port " << _superPort->getName() << " Sent to Modem:  AT");
            }

            /* Wait for a response or till we time out */
            if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
            {
                // Just in case we are out to lunch here...
                _superPort->writePort("+++", 3, 1, &BytesWritten);    // Attention to the modem.

                if(Trace)
                {
                    CTILOG_INFO(dout, "Port " << _superPort->getName() << " Sent to Modem:  +++");
                }

                ResponseSize = sizeof (Response);

                /* Wait for a response or till we time out */
                if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
                {
                    if(Trace)
                    {
                        CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Modem Response Timeout");
                    }

                    _superPort->writePort("\r", 1, 1, &BytesWritten);    // Get rid of the trash that didn't give a good return.
                }
            }
            else if(Trace)
            {
                CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  " << Response);
            }

            CTILOG_INFO(dout, _superPort->getName() <<" modem reset");

            if(gLogPorts)
            {
                CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() <<" modem reset");
            }

            /* Make sure that we got OK */
            if(!(strnicmp(Response, "OK", 2)))
            {
                return ClientErrors::None;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::DialupConnectPort;
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

        if(_superPort->writePort((PVOID)(getTablePortDialup().getModemInitString().c_str()),
                                 initlen, 1, &BytesWritten) || BytesWritten != initlen)
        {
            return ClientErrors::DialupConnectPort;
        }

        /* Send the CR */
        _superPort->writePort("\r", 1, 1, &BytesWritten);

        if(Trace)
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Sent to Modem:  "<< getTablePortDialup().getModemInitString());
        }

        ResponseSize = sizeof (Response);

        /* Wait for a response or till we time out */
        if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
        {
            if(Trace)
            {
                CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Modem Response Timeout");
            }
            continue;
        }

        if(Trace)
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  "<< Response);
        }

        /* Make sure that we got OK */
        if(!(strnicmp (Response, "OK", 2)))
        {
            break;
        }
        else if(!(strnicmp (Response, "ERROR", 5)))
        {
            CTILOG_ERROR(dout, "Port "<< _superPort->getName() <<" Received from Modem: "<< Response <<
                    ", The modem init string \""<< getTablePortDialup().getModemInitString() <<"\" was rejected."
                    );

            return ClientErrors::DialupFailed;
        }
    }

    CTILOG_INFO(dout, _superPort->getName() << " modem setup " << (j >= 5 ? "failed" : "successful"));

    if(gLogPorts)
    {
        CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() <<" modem setup " << (j >= 5 ? "failed" : "successful"));
    }

    if(j >= 5)
    {
        // _superPort->close(TRUE);
        return ClientErrors::DialupConnectPort;
    }

    return ClientErrors::None;
}

/* Routine to establish modem connection */
YukonError_t CtiPortDialout::modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest)
{
    YukonError_t status = ClientErrors::None;
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
        return ClientErrors::DialupConnectPort;
    }

    if(Trace)
    {
        CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Sent to Modem:  "<< MyMessage);
    }

    ResponseSize = sizeof (Response);

    /* Wait for a response from the modem */
    if( ClientErrors::None != (status = _superPort->waitForPortResponse(&ResponseSize, Response, ModemConnectionTimeout, "CONNECT")) )
    {
        if(Trace)
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Modem Response Timeout");
        }
    }
    else
    {
        if(Trace)
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  "<< Response);
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

                        status = ClientErrors::None;
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
                status = ClientErrors::None;
            }
        }
        else if(!(strnicmp (Response, "NO CARRIER", 10)))
        {
            status = ClientErrors::DialupConnectDevice;
        }
        else if(!(strnicmp (Response, "ERROR", 5)))
        {
            status = ClientErrors::DialupConnectPort;
        }
        else if(!(strnicmp (Response, "NO DIAL TONE", 12)))
        {
            status = ClientErrors::DialupConnectPort;
        }
        else if(!(strnicmp (Response, "NO ANSWER", 9)))
        {
            status = ClientErrors::DialupConnectDevice;
        }
        else if(!(strnicmp (Response, "BUSY", 4)))
        {
            status = ClientErrors::DialupConnectDevice;
        }
        else if(!(strnicmp (Response, "NO DIALTONE", 12)))
        {
            status = ClientErrors::DialupConnectPort;
        }
        else
        {
            status = ClientErrors::DialupConnectPort;

            CTILOG_ERROR(dout, "Modem responded:  " << Response);
        }
    }

    if(gLogPorts)
    {
        string MdmResponse;

        if(Response[0] != '\0')
        {
            MdmResponse = string("Modem responded with ") + string(Response);
        }
        else
        {
            MdmResponse = string("Modem did not respond");
        }

        // FIXME: change log level according to MdmResponse?

        CTILOG_INFO(dout, _superPort->getName() <<" modem connect to "<< Message <<". "<< MdmResponse);

        CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() <<" modem connect to "<< Message <<". "<< MdmResponse);
    }

    return(status);
}


INT CtiPortDialout::modemHangup(USHORT Trace, BOOL dcdTest)
{
    INT status = ClientErrors::None;
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
            if( ClientErrors::None == (_superPort->waitForPortResponse(&ResponseSize, Response, 1, NULL)) )      // See if we get something back from this guy.
            {
                if(Trace)
                {
                    CTILOG_INFO(dout, _superPort->getName() <<" Received from Modem:  "<< Response);
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
                    CTILOG_INFO(dout, _superPort->getName() <<" Sent to Modem:  +++");
                }

                CTISleep ( 1500L );
                _superPort->writePort("AT\r", 3, 1, &BytesWritten);   // escape does not give response do AT

                if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
                {
                    if(Trace)
                    {
                        CTILOG_INFO(dout, _superPort->getName() <<" Modem Response Timeout (will re-try)");
                    }

                    status = ClientErrors::ReadTimeout;
                    continue;   // We are not getting anything here!
                }
                else if(Trace)
                {
                    CTILOG_INFO(dout, _superPort->getName() <<" Received from Modem:  " << Response);

                    status = ClientErrors::None;
                    break;
                }
            }
            else
            {
                break;   //  We got an OK back on the "AT"
            }
        }

        _superPort->inClear();

        if(status == ClientErrors::None)
        {
            /* Attempt to hangup the modem (on hook) */
            for(i = 0; i < 5; i++)
            {
                CTISleep ( 250L);

                /* Send it the off hook message */
                _superPort->writePort("ATH0\r", 5, 1, &BytesWritten);

                if(Trace)
                {
                    CTILOG_INFO(dout, _superPort->getName() <<" Sent to Modem:  ATH0");
                }

                ResponseSize = sizeof (Response);

                if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
                {
                    if(Trace)
                    {
                        CTILOG_INFO(dout, _superPort->getName() <<" Modem Response Timeout");
                    }

                    status = ClientErrors::ReadTimeout;     // Hangup Failed
                    continue;
                }
                else
                {
                    if(Trace)
                    {
                        CTILOG_INFO(dout, _superPort->getName() <<" Received from Modem:  "<< Response);
                    }

                    status = ClientErrors::None;
                    break;   // Wow, we just went on hook!
                }
            }
        }

        if(status == ClientErrors::None && dcdTest == TRUE)
        {
            /* Wait for carrier to drop */
            i = 0;
            while(i < 20)
            {
                if(!(_superPort->dcdTest()))
                {
                    CTISleep ( 1500L );
                    status = ClientErrors::None;
                    break;
                }

                CTISleep ( 100 );
                i++;
            }
        }


        if( status )
        {
            /* something is wrong so reset and setup the modem */
            modemReset(Trace, dcdTest);
            status = modemSetup(Trace, dcdTest);
        }
    }
    else
    {
        CTILOG_ERROR(dout, "_superPort is not viable");
    }

    CTILOG_INFO(dout, _superPort->getName() <<" modem hangup "<< (getDialedUpNumber().empty() ? "" : string("on ") + getDialedUpNumber()));

    if(gLogPorts)
    {
        CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() <<" modem hangup "<< (getDialedUpNumber().empty() ? "" : string("on ") + getDialedUpNumber()));
    }

    _superPort->setConnectedDevice(0);
    _superPort->setConnectedDeviceUID(-1);
    setDialedUpNumber(string());

    return status;
}
