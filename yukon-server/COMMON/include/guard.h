#pragma once

#include "utility.h"
#include "dlldefs.h"
#include "logger.h"
#include "dllbase.h"

#include <iostream>
#include <sstream>

#include "dbghelp.h"

#define CTILOCKGUARD(type, guard, resource) \
    CtiLockGuard<type> guard( resource, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTILOCKGUARD2(type, guard, resource, millis) \
    CtiLockGuard<type> guard( resource, millis, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTIREADLOCKGUARD(guard, resource) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTIREADLOCKGUARD2(guard, resource, millis) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, millis, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTIWRITELOCKGUARD(guard, resource) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTIWRITELOCKGUARD2(guard, resource, millis) \
    CtiReadLockGuard<Cti::readers_writer_lock_t> guard( resource, millis, #resource, __FILE__, __FUNCSIG__, __LINE__);

#pragma pack(push, LockGuardPack, 8)
template<class T>
class IM_EX_CTIBASE CtiLockGuard
{
public:
    void acquireLock(T& resource, unsigned long millis, char *resourceName = nullptr, char *file = nullptr, char *func = nullptr, int line = 0);
    CtiLockGuard(T& resource, char *resourceName = nullptr, char *file = nullptr, char *func = nullptr, int line = 0);
    CtiLockGuard(T& resource, unsigned long millis, char *resourceName = nullptr, char *file = nullptr, char *func = nullptr, int line = 0);
    ~CtiLockGuard();

    bool isAcquired() const;
    bool tryAcquire(unsigned long millis);

private:

    bool _acquired;
    T& _res;
    char *_resourceName;
    char *_file;
    char *_func;
    int _line;
};

template<class T>
class CtiReadLockGuard
{
public:
    CtiReadLockGuard(T& resource, char *resourceName = nullptr, char *file = nullptr, char *func = nullptr, int line = 0) : _res(resource)
    {
        static bool hasDumped = false;
        _file = file;
        _func = func;
        _line = line;
        _resourceName = resourceName;

        if( file != 0 && (DebugLevel & DEBUGLEVEL_GUARD) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
        {
            Cti::StreamBufferSink logStream_;
            logStream_ << "Acquiring lock for " << _resourceName << " @ " << std::hex << &_res;
            logStream_ << " owned by " << std::hex << _res.lastAcquiredByTID();
            // We call formatAndForceLog directly so that we can insert the caller's context
            dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
        }

        while(!(_acquired = _res.acquireRead(900000)))
        {
            CTILOG_WARN(dout, "guard is unable to lock " << (_resourceName!=0?_resourceName:"resource") 
                << " FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID());
            CTILOG_WARN(dout, "Acquiring lock from " << (func != nullptr ? func : "(null)") << 
                ":" << (file != nullptr ? file : "(null)") << ":" << line);

            if( !hasDumped )
            {
                hasDumped = true;

                std::ostringstream os;

                os << "lockguard-" << GetCurrentThreadId();

                CreateMiniDump(os.str());
            }
        }
        _acquired = true;

        if( file != 0 && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
        {
            Cti::StreamBufferSink logStream_;
            logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
            dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
        }

    }

    CtiReadLockGuard(T& resource, unsigned long millis ) : _res(resource)
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
    char *_file;
    char *_func;
    int _line;
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
