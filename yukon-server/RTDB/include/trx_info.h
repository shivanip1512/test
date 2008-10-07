/*-----------------------------------------------------------------------------*
*
* File:   trx_info
*
* Class:  CtiTransmitterInfo
* Date:   3/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/trx_info.h-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2008/10/07 18:17:35 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __TRX_INFO_H__
#define __TRX_INFO_H__
#pragma warning( disable : 4786)

#include <windows.h>

#include "logger.h"
#include "queues.h"
#include "mutex.h"
#include "porter.h"

#include <limits>

class CtiTransmitterInfo
{
public:

    //  eventually, we should move this to be a private variable
    REMOTESEQUENCE   RemoteSequence;                   // Used by WELCORTUs to track sequencing...?

private:

    CtiMutex      _statMux;

    UINT          _status;

    ULONG         _stageTime;
    USHORT        _fiveMinuteCount;
    ULONG         _lcuFlags;

    ULONG         _nextCommandTime;

    OUTMESS       *_controlOutMessage;

    unsigned long _inlgrpq_expiration,
                  _inlgrpq_warning,
                  _inrcolq_expiration;

    INT           _type;  //  we can probably get rid of this - it's never really used

    enum
    {
        INLGRPQ_Timeout = 900,
        INLGRPQ_Warning = 300,
        INRCOLQ_Timeout = 300,
    };

public:

    CtiTransmitterInfo(int type = -1) :
        _type(type),
        _status(0),
        _stageTime(0),
        _fiveMinuteCount(0),
        _nextCommandTime(0),
        _lcuFlags(0),
        _controlOutMessage(NULL),
        _inlgrpq_expiration(YUKONEOT),
        _inlgrpq_warning(YUKONEOT),
        _inrcolq_expiration(YUKONEOT)
    {
        RemoteSequence.Reply   = 0;
    }

    CtiTransmitterInfo(const CtiTransmitterInfo& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiTransmitterInfo()
    {
        if( _controlOutMessage )
        {
            delete _controlOutMessage;
            _controlOutMessage = NULL;
        }
    }

    CtiTransmitterInfo& operator=(const CtiTransmitterInfo& aRef)
    {
        if(this != &aRef)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        return *this;
    }

    //  Routine to set a status bit (mutex-protected)
    void setStatus( USHORT mask )
    {
        CtiLockGuard< CtiMutex > guard(_statMux);

        if( mask == INRCOLQ )   _inrcolq_expiration = CtiTime::now().seconds() + INRCOLQ_Timeout;
        if( mask == INLGRPQ )
        {
            _inlgrpq_expiration = CtiTime::now().seconds() + INLGRPQ_Timeout;
            _inlgrpq_warning    = CtiTime::now().seconds() + INLGRPQ_Warning;
        }

        _status |= mask;
    }

    //  Routine to clear a status bit (mutex-protected)
    void clearStatus( USHORT mask )
    {
        CtiLockGuard< CtiMutex > guard(_statMux);

        _status &= ~mask;
    }

    //  Routine to get a status bit (mutex-protected)
    INT getStatus( USHORT mask = std::numeric_limits<USHORT>::max() )
    {
        CtiLockGuard< CtiMutex > guard(_statMux);

        unsigned long now_seconds = CtiTime::now().seconds();

        if( (_status & INRCOLQ) && (_inrcolq_expiration < now_seconds) )
        {
            _status &= ~INRCOLQ;
            _inrcolq_expiration = YUKONEOT;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - INRCOLQ expired in CtiTransmitterInfo::getStatus() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if( (_status & INLGRPQ) && (_inlgrpq_expiration < now_seconds) )
        {
            _status &= ~INLGRPQ;
            _inlgrpq_expiration = YUKONEOT;
            _inlgrpq_warning    = YUKONEOT;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - INLGRPQ expired in CtiTransmitterInfo::getStatus() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        return (_status & mask);
    }

    ULONG getNextCommandTime( void )
    {
        return _nextCommandTime;
    }

    void setNextCommandTime( ULONG time )
    {
        _nextCommandTime = time;
    }

    ULONG getINLGRPQExpiration( void )
    {
       return _inlgrpq_expiration;
    }

    ULONG getINLGRPQWarning( void )
    {
       return _inlgrpq_warning;
    }

    void setINLGRPQWarning( ULONG time )
    {
       _inlgrpq_warning = time;
    }

};
#endif // #ifndef __TRX_INFO_H__
