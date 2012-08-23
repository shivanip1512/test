/*-----------------------------------------------------------------------------*
*
* File:   CtiLocalConnect.cpp
*
* Date:   1/11/2006
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_710.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/08/14 15:57:39 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cparms.h"
#include "logger.h"
#include "ctilocalconnect.h"

using std::endl;

template <class Outbound, class Inbound>
CtiLocalConnect<Outbound, Inbound>::~CtiLocalConnect()
{
    delete_container(_outQueue);

    _outQueue.clear();
}

template <class Outbound, class Inbound>
ULONG CtiLocalConnect<Outbound, Inbound>::CtiGetNexusState()
{
    return _nexusState;
}

template <class Outbound, class Inbound>
INT CtiLocalConnect<Outbound, Inbound>::CTINexusClose()
{
    _nexusState = CTINEXUS_STATE_NULL;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ***** CHECKPOINT ***** Close called.." << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return _nexusState;
}

template <class Outbound, class Inbound>
INT CtiLocalConnect<Outbound, Inbound>::CTINexusWrite(void *buf, ULONG len, PULONG BWritten, LONG TimeOut)
{
    int retVal = 1;

    if( _nexusState && buf && len && len == sizeof(Outbound) )
    {
        Outbound *o = 0;

        try
        {
            Outbound *o = new Outbound(*(static_cast<Outbound *>(buf)));
            /*
            if( gConfigParms.getValueAsULong("DEBUGLEVEL_NEXUS",0, 16) & 0x00000010 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** ctilocalconnect push to queue **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            */

            if( !o )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** CtiLocalConnect::CTINexusWrite() - Could not allocate new Outbound " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                if( BWritten )
                {
                    *BWritten = 0;
                }
            }
            else
            {
                {
                    CtiLockGuard< CtiCriticalSection > g(_crit);
                    _outQueue.insert(o);
                }

                if( BWritten )
                {
                    *BWritten = len;
                }

                retVal = 0;
            }
        }
        catch(...)
        {
            retVal = 1;

            delete o;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** Exception while attempting write" << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        if( len != sizeof(Outbound) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** CtiNexusWrite with len != sizeof(Outbound) (" << len << " != " << sizeof(Outbound) << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( BWritten )
        {
            *BWritten = 0;
        }
    }

    return retVal;
}

template <class Outbound, class Inbound>
INT CtiLocalConnect<Outbound, Inbound>::CTINexusRead(void *buf, ULONG len, PULONG BRead, LONG TimeOut)
{
    int retVal = 0;

    if( BRead )
    {
        *BRead = 0;
    }

    if( _nexusState && buf && len )
    {
        retVal = _directConnection->CtiLocalConnectRead(buf, len, BRead, TimeOut);
    }

    return retVal;
}

template <class Outbound, class Inbound>
INT CtiLocalConnect<Outbound, Inbound>::CTINexusPeek(void *buf, ULONG len, PULONG BRead)
{
    int retVal = 0;

    if( BRead )
    {
        *BRead = 0;
    }

    if( _nexusState && buf && len )
    {
        retVal = _directConnection->CtiLocalConnectRead(buf, len, BRead, 0, MESSAGE_PEEK);
    }

    return retVal;
}

template <class Outbound, class Inbound>
INT CtiLocalConnect<Outbound, Inbound>::CtiLocalConnectRead(void *buf, ULONG len, PULONG BRead, LONG TimeOut, int flags)
{
    int retVal = 1;
    ULONG count = 0;

    if( TimeOut < 0 )
    {
        while( _outQueue.empty() )
        {
            //  Note added August 23, 2012 by Matt Fisher regarding YUK-10530:
            //
            //  This seems like an infinite loop when called from PIL's ConnectionThread:
            //
            //    ConnectionThread(): portentry.cpp, line 182
            //      else if((i = MyNexus->CTINexusRead (OutMessage, sizeof(OUTMESS), &BytesRead, CTINEXUS_INFINITE_TIMEOUT))  || BytesRead != sizeof(OUTMESS))  /* read whatever comes in */
            //
            //    cticonnect.h, line 26
            //      #define CTINEXUS_INFINITE_TIMEOUT LONG_MIN
            //
            //    TimeOut will be negative in that case, and unless something is written to the PilToPorter queue near shutdown, that thread is probably not shutting down.
            //    Perhaps this while loop condition should also be watching PorterQuit?
            //
            Sleep(50);
        };
    }
    else
    {
        while( _outQueue.empty() && count<(TimeOut * 20) )
        {
            count++;
            Sleep(50);
        };
    }

    if( !_outQueue.empty() && len == sizeof(Outbound) )
    {
        try
        {
            {
                CtiLockGuard< CtiCriticalSection > g(_crit);
                memcpy(buf, *(_outQueue.begin()), len);
                *BRead = len;
                retVal = 0;

                if( flags != MESSAGE_PEEK )
                {
                    delete *(_outQueue.begin());
                    _outQueue.erase(_outQueue.begin());
                }
            }
            /*
            if( gConfigParms.getValueAsULong("DEBUGLEVEL_NEXUS",0, 16) & 0x00000020 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** ctilocalconnect pop from queue **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            */
        }
        catch(...)
        {
            retVal = 1;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** Exception while attempting read" << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        if( len != sizeof(Outbound) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** CtiLocalConnectRead with len != sizeof(Outbound) (" << len << " != " << sizeof(Outbound) << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        *BRead = 0;
        retVal = ERR_CTINEXUS_READTIMEOUT;
    }

    return retVal;
}

template <class Outbound, class Inbound>
bool CtiLocalConnect<Outbound, Inbound>::CTINexusValid() const
{
    return _nexusState;
}

template <class Outbound, class Inbound>
bool CtiLocalConnect<Outbound, Inbound>::setMatchingConnection( CtiLocalConnect<Inbound, Outbound> &connection )
{
    _directConnection = &connection;
    CtiLocalConnectOpen();
    return false;
}

template <class Outbound, class Inbound>
int CtiLocalConnect<Outbound, Inbound>::CtiLocalConnectOpen()
{
    _nexusState = CTINEXUS_STATE_CONNECTED;
    return 0;
}

void CtiLocalConnect<OUTMESS, INMESS>::purgeRequest(int request)
{
    CtiLockGuard< CtiCriticalSection > g(_crit);

    queue_t::iterator itr = _outQueue.begin();

    while( itr != _outQueue.end() )
    {
        if( (*itr)->Request.GrpMsgID == request )
        {
            delete(*itr);
            itr = _outQueue.erase(itr);
        }
        else
        {
            itr++;
        }
    }
}


