#include "precompiled.h"
#include "readers_writer_lock.h"
#include "mutex.h"
#include "guard.h"
#include "dllbase.h"

template<class T>
void CtiLockGuard<T>::acquireLock( unsigned long millis )
{
    if( _callSite.getLine() && (DebugLevel & DEBUGLEVEL_GUARD) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquiring lock for " << _resourceName << " @ " << std::hex << &_res;
        logStream_ << " owned by " << std::hex << _res.lastAcquiredByTID();
        // We call formatAndForceLog directly so that we can insert the caller's context
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite );
    }

    _acquired = _res.acquire(millis);

    if( _callSite.getLine() && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite );
    }
}

template<class T>
CtiLockGuard<T>::CtiLockGuard(T& resource) : CtiLockGuard(resource, nullptr, { "(null)", "(null)", 0 })
{
}

template<class T>
CtiLockGuard<T>::CtiLockGuard(T& resource, unsigned long millis) : CtiLockGuard(resource, millis, nullptr, { "(null)", "(null)", 0 })
{
}

template<class T>
CtiLockGuard<T>::CtiLockGuard(T& resource, Cti::CallSite callSite) : CtiLockGuard(resource, nullptr, callSite)
{
}

template<class T>
CtiLockGuard<T>::CtiLockGuard( T& resource, const char *resourceName, Cti::CallSite callSite ) : _res( resource ), _resourceName( resourceName ), _callSite( callSite )
{
    static bool hasDumped = false;

    while (acquireLock(900000), _acquired == 0)  //  try to acquire for up to 15 minutes
    {
        CTILOG_WARN(dout, "guard is unable to lock " << (_resourceName?_resourceName:"resource") << " @ " << std::hex << &_res
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
}

template<class T>
CtiLockGuard<T>::CtiLockGuard( T& resource, unsigned long millis, const char *resourceName, Cti::CallSite callSite ) : _res( resource ), _resourceName(resourceName), _callSite(callSite)
{
    acquireLock(millis);
}

template<class T>
CtiLockGuard<T>::~CtiLockGuard()
{
    if( _acquired )
        _res.release();

    _acquired = false;

    if( _callSite.getLine() && (DebugLevel & DEBUGLEVEL_GUARD) &&  dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Released lock for " << _resourceName << " @ " << std::hex << &_res;
        // We call formatAndForceLog directly so that we can insert the caller's context
        dout->formatAndForceLog(Cti::Logging::Logger::Debug, logStream_, _callSite );
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
CtiLockGuard<CtiCriticalSection>::CtiLockGuard( CtiCriticalSection& resource, const char *resourceName, Cti::CallSite callSite ) : _res( resource ), _callSite( callSite )
{
    //  CtiCriticalSection has an internal timeout controlled by
    //        HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\CriticalSectionTimeout
    //    after which it should raise EXCEPTION_POSSIBLE_DEADLOCK (0xC0000194).
    //
    //  See https://msdn.microsoft.com/en-us/library/windows/desktop/ms682608.aspx
    static bool hasDumped = false;
    _resourceName = resourceName;

    if( _callSite.getLine() && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquiring lock for " << _resourceName << " @ " << std::hex << &_res;
        logStream_ << " owned by " << std::hex << _res.lastAcquiredByTID();
        // We call formatAndForceLog directly so that we can insert the caller's context
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite );
    }

    _res.acquire();

    _acquired = true;

    if( _callSite.getLine() && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Acquired lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite);
    }
}

//  Do not implement the timed constructor for CtiCriticalSection, since it does not implement a timed acquire

template<>
CtiLockGuard<CtiCriticalSection>::~CtiLockGuard()
{
    _res.release();

    if( _callSite.getLine() && ( DebugLevel & DEBUGLEVEL_GUARD ) && dout->isLevelEnable( Cti::Logging::Logger::Debug ) )
    {
        Cti::StreamBufferSink logStream_;
        logStream_ << "Released lock for " << _resourceName << " @ " << std::hex << &_res;
        dout->formatAndForceLog( Cti::Logging::Logger::Debug, logStream_, _callSite);
    }
}

template<>
CtiLockGuard<CtiCriticalSection>::CtiLockGuard(CtiCriticalSection& resource, Cti::CallSite callSite) : CtiLockGuard(resource, nullptr, callSite)
{
}

template<>
CtiLockGuard<CtiCriticalSection>::CtiLockGuard(CtiCriticalSection& resource) : CtiLockGuard(resource, nullptr, { "", "", 0 })
{
}

template<>
bool CtiLockGuard<CtiCriticalSection>::isAcquired() const
{
    return _acquired;
}

