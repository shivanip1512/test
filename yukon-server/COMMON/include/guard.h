#pragma once

#include "utility.h"
#include "dlldefs.h"

#include <iostream>
#include <sstream>

#include "dbghelp.h"


#pragma pack(push, LockGuardPack, 8)
template<class T>
class CtiLockGuard
{
public:
    CtiLockGuard(T& resource) :  _res(resource)
    {
        static bool hasDumped = false;
        while(!(_acquired = _res.acquire(900000)))
        {
            std::cerr << " guard is unable to lock resource FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID() << std::endl;
            if( !hasDumped )
            {
                hasDumped = true;

                std::ostringstream os;

                os << "lockguard-" << GetCurrentThreadId();

                CreateMiniDump(os.str());
            }
        }
        _acquired = true;
    }

    CtiLockGuard(T& resource, unsigned long millis ) : _res(resource)
    {
        _acquired = _res.acquire(millis);
    }

    ~CtiLockGuard()
    {
        if(_acquired)
            _res.release();
    }

    bool isAcquired() const { return _acquired;}
    bool tryAcquire(unsigned long millis)
    {
        if(!_acquired)
        {
            _acquired = _res.acquire(millis);
        }

        return _acquired;
    }


private:

    bool _acquired;
    T& _res;
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
            std::cerr << " guard is unable to lock resource FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID() << std::endl;
            if( !hasDumped )
            {
                hasDumped = true;

                std::ostringstream os;

                os << "lockguard-" << GetCurrentThreadId();

                CreateMiniDump(os.str());
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

template<class T>
class IM_EX_CTIBASE CtiUnlockGuard
{
public:
    CtiUnlockGuard(T& resource) :  _res(resource)
    {
        _res.release();
    }

    ~CtiUnlockGuard()
    {
        _res.aquire();
    }

private:

    T& _res;
};

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

#pragma pack(pop, LockGuardPack)
