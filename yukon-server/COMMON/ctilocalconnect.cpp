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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2006/03/02 16:36:45 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "logger.h"
#include "ctilocalconnect.h"

CtiLocalConnect::~CtiLocalConnect()
{
    DirectDataKeeper* data;
    while( !_outQueue.empty() )
    {
        data = &(_outQueue.front());
        delete [] data->data;
        _outQueue.pop();
    }
}

ULONG CtiLocalConnect::CtiGetNexusState()
{
    return _nexusState;
}

INT CtiLocalConnect::CTINexusClose()
{
    _nexusState = CTINEXUS_STATE_NULL;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ***** CHECKPOINT ***** Close called.." << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return _nexusState;
}

INT CtiLocalConnect::CTINexusWrite(VOID *buf, ULONG len, PULONG BWritten, LONG TimeOut)
{
    int retVal = 1;
    DirectDataKeeper data;

    if( _nexusState && buf && len)
    {
        BYTE *bufStore = new BYTE[len];
        data.data = bufStore;
        data.len = len;

        try
        {
            memcpy(bufStore, buf, len);
            _outQueue.push(data);

            if( BWritten )
            {
                *BWritten = len;
            }

            retVal = 0;
        }
        catch(...)
        {
            retVal = 1;

            if( bufStore )
            {
                delete bufStore;
                bufStore = 0;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** Exception while attempting write" << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        if( BWritten )
        {
            *BWritten = 0;
        }
    }

    return retVal;
}

INT CtiLocalConnect::CTINexusRead(VOID *buf, ULONG len, PULONG BRead, LONG TimeOut)
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

INT CtiLocalConnect::CTINexusPeek(VOID *buf, ULONG len, PULONG BRead)
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

INT CtiLocalConnect::CtiLocalConnectRead(VOID *buf, ULONG len, PULONG BRead, LONG TimeOut, int flags)
{
    int retVal = 1;
    ULONG count = 0;
    DirectDataKeeper *data;

    if( TimeOut < 0 )
    {
        while( _outQueue.empty() )
        {
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

    if( !_outQueue.empty() )
    {
        try
        {
            data = &(_outQueue.front());
            memcpy(buf, data->data, len);
            *BRead = len;
            retVal = 0;

            if( flags != MESSAGE_PEEK )
            {
                delete [] data->data;
                _outQueue.pop();
            }
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
        *BRead = 0;
        retVal = ERR_CTINEXUS_READTIMEOUT;
    }

    return retVal;
}

bool  CtiLocalConnect::CTINexusValid() const
{
    return _nexusState;
}

bool CtiLocalConnect::setMatchingConnection( CtiLocalConnect &connection )
{
    _directConnection = &connection;
    CtiLocalConnectOpen();
    return false;
}

int CtiLocalConnect::CtiLocalConnectOpen()
{
    _nexusState = CTINEXUS_STATE_CONNECTED;
    return 0;
}

