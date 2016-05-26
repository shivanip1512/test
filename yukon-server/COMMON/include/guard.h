#pragma once

#include "utility.h"
#include "dlldefs.h"
#include "logger.h"

#include <iostream>
#include <sstream>

#include "dbghelp.h"

#define CTILOCKGUARD(type, guard, resource) \
    CtiLockGuard<type> guard( resource, #resource, __FILE__, __FUNCSIG__, __LINE__);

#define CTILOCKGUARD2(type, guard, resource, millis) \
    CtiLockGuard<type> guard( resource, millis, #resource, __FILE__, __FUNCSIG__, __LINE__);

#pragma pack(push, LockGuardPack, 8)
template<class T>
class IM_EX_CTIBASE CtiLockGuard
{
public:
    CtiLockGuard( T& resource, char *resourceName = 0, char *file=0, char *func=0, int line=0 );
    CtiLockGuard( T& resource, unsigned long millis, char *resourceName = 0, char *file = 0, char *func = 0, int line = 0 );
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
    CtiReadLockGuard(T& resource) :  _res(resource)
    {
        static bool hasDumped = false;
        while(!(_acquired = _res.acquireRead(900000)))
        {
            CTILOG_WARN(dout, "guard is unable to lock resource FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID());

            if( !hasDumped )
            {
                hasDumped = true;

                std::ostringstream os;

                os << "lockguard-" << GetCurrentThreadId();

                RaiseException(0xe0000001, 0, 0, 0);
            }
        }
        _acquired = true;
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
