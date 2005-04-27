/*-----------------------------------------------------------------------------
    Filename:  guard.h

    Programmer:  Aaron Lauinger

    Description:    Header file for CtiLockGuard

                    Use to acquire and release a resource in an
                    exception safe way.

                    Example:

                    CtiMutex mux;

                    {
                        CtiLockGuard<CtiMutex> guard(mux);
                        // mux is acquired here
                        // and will be released when guard is
                        // destroyed
                    }

    Initial Date:  11/7/00

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef __CTILOCKGUARD_H__
#define __CTILOCKGUARD_H__

#include <windows.h>
#include <iostream>
using namespace std;

#include "dlldefs.h"


extern IM_EX_CTIBASE void autopsy(char *calleefile, int calleeline);       // Usage is: autopsy( __FILE__, __LINE__);

template<class T>
class IM_EX_CTIBASE CtiLockGuard
{
public:
    CtiLockGuard(T& resource) :  _res(resource)
    {
        #ifdef _DEBUG
        while(!(_acquired = _res.acquire(900000)))
        {
            cerr << " guard is unable to lock resource FOR thread id: " << GetCurrentThreadId() << endl;
        }
        #else
        _res.acquire();
        #endif
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

#endif

