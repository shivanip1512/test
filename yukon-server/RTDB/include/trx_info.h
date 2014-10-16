#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "logger.h"
#include "queues.h"
#include "critical_section.h"
#include "porter.h"
#include "string_utility.h"


class CtiTransmitterInfo : private boost::noncopyable
{
public:

    //  eventually, we should move this to be a private variable
    REMOTESEQUENCE   RemoteSequence;                   // Used by WELCORTUs to track sequencing...?

private:

    CtiCriticalSection _statusMux;

    UINT          _status;

    ULONG         _stageTime;
    USHORT        _fiveMinuteCount;
    ULONG         _lcuFlags;

    ULONG         _nextCommandTime;

    OUTMESS       *_controlOutMessage;

    unsigned long _inlgrpq_expiration,
                  _inlgrpq_warning,
                  _inrcolq_expiration;

    enum
    {
        INLGRPQ_Timeout = 900,
        INLGRPQ_Warning = 300,
        INRCOLQ_Timeout = 300,
    };

public:

    CtiTransmitterInfo(int type = -1) :
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

    virtual ~CtiTransmitterInfo()
    {
        if( _controlOutMessage )
        {
            delete _controlOutMessage;
            _controlOutMessage = NULL;
        }
    }

    //  Routine to set a status bit (mutex-protected)
    void setStatus( USHORT mask )
    {
        CtiLockGuard< CtiCriticalSection > guard(_statusMux);

        if( mask == INRCOLQ )
        {
            _inrcolq_expiration = CtiTime::now().seconds() + INRCOLQ_Timeout;
        }
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
        CtiLockGuard< CtiCriticalSection > guard(_statusMux);

        _status &= ~mask;
    }

    //  Routine to get a status bit (mutex-protected)
    INT getStatus( USHORT mask = std::numeric_limits<USHORT>::max() )
    {
        CtiLockGuard< CtiCriticalSection > guard(_statusMux);

        unsigned long now_seconds = CtiTime::now().seconds();

        if( (_status & INRCOLQ) && (_inrcolq_expiration < now_seconds) )
        {
            _status &= ~INRCOLQ;
            _inrcolq_expiration = YUKONEOT;

            CTILOG_WARN(dout, "INRCOLQ expired");
        }

        if( (_status & INLGRPQ) && (_inlgrpq_expiration < now_seconds) )
        {
            _status &= ~INLGRPQ;
            _inlgrpq_expiration = YUKONEOT;
            _inlgrpq_warning    = YUKONEOT;

            CTILOG_WARN(dout, "INLGRPQ expired");
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
