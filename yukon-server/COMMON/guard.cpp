#include "precompiled.h"
#include "readers_writer_lock.h"
#include "mutex.h"
#include "guard.h"
#include "dllbase.h"

template<class T>
CtiLockGuard<T>::CtiLockGuard( T& resource, char *resourceName, char *file, char *func, int line ) : _res( resource )
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
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
    }

    while( !(_acquired = _res.acquire(900000)) )  //  try to acquire for up to 15 minutes
    {
        CTILOG_WARN(dout, "guard is unable to lock resource FOR thread id: " << GetCurrentThreadId() << " resource is owned by " << _res.lastAcquiredByTID());

        if( !hasDumped )
        {
            hasDumped = true;

            std::ostringstream os;

            os << "lockguard-" << GetCurrentThreadId();

            CreateMiniDump(os.str());
        }
    }

    if( file != 0 && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
    }
}

template<class T>
CtiLockGuard<T>::CtiLockGuard( T& resource, unsigned long millis, char *resourceName, char *file, char *func, int line ) : _res( resource )
{
    _file = file;
    _func = func;
    _line = line;
    _resourceName = resourceName;

    if( file != 0 && (DebugLevel & DEBUGLEVEL_GUARD) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquiring lock for " << _resourceName << " @ " << std::hex << &_res;
        logStream_ << " owned by " << _res.lastAcquiredByTID();
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
    }

    _acquired = _res.acquire( millis );

    if( file != 0 && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
    }
}

template<class T>
CtiLockGuard<T>::~CtiLockGuard()
{
    if( _acquired )
        _res.release();

    _acquired = false;

    if( _file != 0 && (DebugLevel & DEBUGLEVEL_GUARD) &&  dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Released lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _file, _func, _line );
    }
}

template<class T>
bool CtiLockGuard<T>::isAcquired() const
{
    return _acquired;
}

template<class T>
bool CtiLockGuard<T>::tryAcquire(unsigned long millis)
{
    if( !_acquired )
    {
        _acquired = _res.acquire(millis);
    }

    return _acquired;
}

//  Full default instantiation for CtiMutex and Cti::readers_writer_lock_t
template class CtiLockGuard<CtiMutex>;
template class CtiLockGuard<Cti::readers_writer_lock_t>;

//  Specialization for CtiCriticalSection

template<>
CtiLockGuard<CtiCriticalSection>::CtiLockGuard( CtiCriticalSection& resource, char *resourceName, char *file, char *func, int line ) : _res( resource )
{
    //  CtiCriticalSection has an internal timeout controlled by
    //        HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\CriticalSectionTimeout
    //    after which it should raise EXCEPTION_POSSIBLE_DEADLOCK (0xC0000194).
    //
    //  See https://msdn.microsoft.com/en-us/library/windows/desktop/ms682608.aspx
    _res.acquire();

    _acquired = true;
}

//  Do not implement the timed constructor for CtiCriticalSection, since it does not implement a timed acquire

template<>
CtiLockGuard<CtiCriticalSection>::~CtiLockGuard()
{
    _res.release();
}

