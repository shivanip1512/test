#pragma once

#include "utility.h"
#include "dlldefs.h"
#include "logger.h"
#include "dllbase.h"

#include <iostream>
#include <sstream>

#include "dbghelp.h"

#define CTILOCKGUARD(type, guard, resource) \
    CtiLockGuard<type> guard( resource, #resource, CALLSITE);

#define CTILOCKGUARD2(type, guard, resource, millis) \
    CtiLockGuard<type> guard( resource, millis, #resource, CALLSITE);

#define CTIREADLOCKGUARD(guard, resource) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, #resource, CALLSITE);

#define CTIREADLOCKGUARD2(guard, resource, millis) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, millis, #resource, CALLSITE);

#define CTIWRITELOCKGUARD(guard, resource) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, #resource, CALLSITE);

#define CTIWRITELOCKGUARD2(guard, resource, millis) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, millis, #resource, CALLSITE);

#pragma pack(push, LockGuardPack, 8)
template<class T>
class IM_EX_CTIBASE CtiLockGuard
{
public:
    CtiLockGuard(T& resource);
    CtiLockGuard(T& resource, unsigned long millis);
    CtiLockGuard(T& resource, Cti::CallSite callSite);
    CtiLockGuard(T& resource, const char *resourceName, Cti::CallSite callSite);
    CtiLockGuard(T& resource, unsigned long millis, const char *resourceName, Cti::CallSite callSite);
    ~CtiLockGuard();

    bool isAcquired() const;
    bool tryAcquire(unsigned long millis);

private:

    void acquireLock(unsigned long millis);

    bool _acquired;
    T& _res;
    const char *_resourceName;
    Cti::CallSite _callSite;
};

template<class T>
class CtiReadLockGuard
{
public:
    CtiReadLockGuard(T& resource)
        :   CtiReadLockGuard(resource, nullptr, { "(null)", "(null)", 0 })
    {
    }

    CtiReadLockGuard(T& resource, Cti::CallSite callSite)
        :   CtiReadLockGuard(resource, nullptr, callSite)
    {
    }

    CtiReadLockGuard(T& resource, char *resourceName, Cti::CallSite callSite)
        :   _res(resource),
            _callSite(callSite)
    {
        static bool hasDumped = false;
        _resourceName = resourceName;

        if( callSite.getLine() && (DebugLevel & DEBUGLEVEL_GUARD) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
        {
            Cti::StreamBufferSink logStream_;
            logStream_ << "Acquiring lock for " << _resourceName << " @ " << std::hex << &_res;
            logStream_ << " owned by " << std::hex << _res.lastAcquiredByTID();
            // We call formatAndForceLog directly so that we can insert the caller's context
            dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite );
        }

        while(!(_acquired = _res.acquireRead(900000)))
        {
            CTILOG_WARN(dout, "guard is unable to lock " << (_resourceName!=0?_resourceName:"resource") 
                << " FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID());
            CTILOG_WARN(dout, "Acquiring lock from " << _callSite);

            if( !hasDumped )
            {
                hasDumped = true;

                std::ostringstream os;

                os << "lockguard-" << GetCurrentThreadId();

                CreateMiniDump(os.str());
            }
        }
        _acquired = true;

        if( callSite.getLine() && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
        {
            Cti::StreamBufferSink logStream_;
            logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
            dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite );
        }

    }

    CtiReadLockGuard(T& resource, unsigned long millis ) : _res(resource), _callSite {"(null)", "(null)", 0}
    {
        _acquired = _res.acquireRead(millis);
    }

    ~CtiReadLockGuard()
    {
        if(_acquired)
            _res.releaseRead();
    }

    bool isAcquired() const { return _acquired;}
    bool tryAcquire(unsigned long millis)
    {
        if(!_acquired)
        {
            _acquired = _res.acquireRead(millis);
        }

        return _acquired;
    }


private:

    bool _acquired;
    T& _res;
    char *_resourceName;
    Cti::CallSite _callSite;
};

namespace Cti {

template <class T>
class TryLockGuard
{
    bool _acquired;
    T& _res;

public:
    TryLockGuard(T& resource) : _res(resource)
    {
        _acquired = _res.tryAcquire();
    }

    ~TryLockGuard()
    {
        if( _acquired )
            _res.release();
    }

    bool isAcquired() const
    {
        return _acquired;
    }
};

}

#pragma pack(pop, LockGuardPack)
