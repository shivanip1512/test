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
* DATE         :  $Date: 2005/12/20 17:20:28 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dsm2.h"
#include "logger.h"
#include "port_dialin.h"

using std::iostream;
using std::endl;
using std::string;

CtiPortDialin::CtiPortDialin()
{}

YukonError_t CtiPortDialin::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    YukonError_t status = ClientErrors::Abnormal;

    LastDeviceId = 0L;

    CTILOG_ERROR(dout, "Dialable cannot be dialin");

    return status;
}


INT CtiPortDialin::close(INT trace)
{
    INT status = ClientErrors::None;

    CTILOG_ERROR(dout, "Dialable cannot be closed");

    return status;
}

YukonError_t CtiPortDialin::reset(INT trace)
{
    YukonError_t status = ClientErrors::None;

    try
    {
        setDialedUpNumber(string());
        if(_superPort) status = modemReset(_superPort->getPortID(), trace);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return status;
}

INT CtiPortDialin::setup(INT trace)
{
    try
    {
        while(!_superPort)
        {
            CTILOG_ERROR(dout, "Cannot initialize Dialin Comm channel. Port specification incomplete.")

            Sleep(2500);
        }

        _superPort->enableRTSCTS();
        _modem.setPort(_superPort);
        _modem.waitForOK( 500, "OK" );
        _modem.reset();
        _modem.sendString(getTablePortDialup().getModemInitString().c_str());
        _modem.setAutoAnswerRingCount(1);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}

/* Routine to force the reset of modem */
YukonError_t CtiPortDialin::modemReset(USHORT Trace, BOOL dcdTest)
{
    CHAR Response[100];
    ULONG ResponseSize;
    ULONG BytesWritten;
    ULONG i = 0;
    static ULONG tCount = 0;

    if(!_superPort->isViable())
    {
        CTILOG_ERROR(dout, "modem cannot be reset, super port is not viable")

        return ClientErrors::BadPort;
    }

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
            CTILOG_ERROR(dout, "Port "<< _superPort->getName() <<" No Modem CTS.  Modem may be off or configured wrong");
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
        if( !(_superPort->waitForPortResponse(&ResponseSize, Response, 1)) )      // See if we get something back from this guy.
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  "<< Response);
        }

        ResponseSize = sizeof (Response);
        // Make an attempt to determine if we are in the command mode
        _superPort->writePort("AT\r", 3, 1, &BytesWritten);    // Attention to the modem.
        if(Trace)
        {
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Sent to Modem:  AT");
        }

        /* Wait for a response or till we time out */
        if(_superPort->waitForPortResponse(&ResponseSize, Response, 3, "OK"))
        {
            // Just in case we are out to lunch here...
            _superPort->writePort("+++", 3, 1, &BytesWritten);    // Attention to the modem.

            if(Trace)
            {
                CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Sent to Modem:  +++");
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
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  "<< Response);
        }

        CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" modem reset");

        if(gLogPorts)
        {
            CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() << " modem reset");
        }

        /* Make sure that we got OK */
        if(!(strnicmp(Response, "OK", 2)))
        {
            return ClientErrors::None;
        }
    }

    return ClientErrors::Abnormal;
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

        size_t initlen = getTablePortDialup().getModemInitString().length();

        if(_superPort->writePort((PVOID)(getTablePortDialup().getModemInitString().c_str()),
                                 initlen,
                                 1, &BytesWritten) || BytesWritten != initlen)
        {
            return ClientErrors::Abnormal;
        }

        /* Send the CR */
        _superPort->writePort("\r", 1, 1, &BytesWritten);

        CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Sent to Modem:  " << getTablePortDialup().getModemInitString());

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
            CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" Received from Modem:  " << Response);
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

    CTILOG_INFO(dout, "Port "<< _superPort->getName() <<" modem setup " << (j >= 5 ? "failed" : "successful"));

    if(gLogPorts)
    {
        CTILOG_INFO(_superPort->getPortLog(), _superPort->getName() <<" modem setup " << (j >= 5 ? "failed" : "successful"));
    }

    if(j >= 5)
    {
        // _superPort->close(TRUE);
        return ClientErrors::Abnormal;
    }

    return ClientErrors::None;
}


