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
* DATE         :  $Date: 2005/02/17 19:02:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dsm2.h"
#include "logger.h"
#include "port_dialin.h"

CtiPortDialin::CtiPortDialin()
{}

CtiPortDialin::CtiPortDialin(const CtiPortDialin& aRef)
{
    *this = aRef;
}

CtiPortDialin::~CtiPortDialin()
{
}

INT CtiPortDialin::connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace)
{
    INT status     = !NORMAL;

    LastDeviceId = 0L;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " Dialable cannot be dialin" << endl;
    }
    return status;
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
    INT status = NORMAL;

    try
    {
        setDialedUpNumber(RWCString());
        if(_superPort) status = modemReset(_superPort->getPortID(), trace);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return status;
}

INT CtiPortDialin::setup(INT trace)
{
    try
    {
        while(!_superPort)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Cannot initialize Dialin Comm channel.  Port specification incomplete." << endl;
            }
            Sleep(2500);
        }

        _superPort->enableRTSCTS();
        _modem.setPort(_superPort);
        _modem.waitForOK( 500, "OK" );
        _modem.reset();
        _modem.sendString(getTablePortDialup().getModemInitString().data());
        _modem.setAutoAnswerRingCount(1);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return NORMAL;
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

        size_t initlen = getTablePortDialup().getModemInitString().length();

        if(_superPort->writePort((PVOID)(getTablePortDialup().getModemInitString().data()),
                                 initlen,
                                 1, &BytesWritten) || BytesWritten != initlen)
        {
            return(!NORMAL);
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
        return(!NORMAL);
    }

    return(NORMAL);
}


