#include "yukon.h"

#include "fdrio.h"

template <class T> 
void CtiFDRIO<T>::startIO( void )
{
    _inThreadFunc  = rwMakeThreadFunction( *this, &CtiFDRIO::_inThread );
    _outThreadFunc = rwMakeThreadFunction( *this, &CtiFDRIO::_outThread );
    _inThreadFunc.start( );
    _outThreadFunc.start( );
}


template <class T> 
void CtiFDRIO<T>::stopIO( void )
{
    _inThreadFunc.requestCancellation( );
    _outThreadFunc.requestCancellation( );
    _inThreadFunc.join( );
    _outThreadFunc.join( );
}


//  these are the protected interface functions, which are used in the _in- and _outThread
//    threads.  they run the backend of what the public interfaces see.
template <class T> 
void CtiFDRIO<T>::_postInput( T *toSend )
{
    RWMutexLock::LockGuard inboxGuard(_inboxMux);
    _inbox.push( toSend );
    _inputWaiting.release( );
}


template <class T> 
T *CtiFDRIO<T>::_grabOutput( long milliseconds )
{
    T *tmp = NULL;
    if( _outputWaiting.acquire( milliseconds ) != RW_THR_TIMEOUT )
    {
        RWMutexLock::LockGuard outboxGuard(_outboxMux);
        //  unnecessary precaution, but what the heck.
        if( _outbox.size( ) )
        {
            tmp = _outbox.front( );
            _outbox.pop( );
        }
    }
    return tmp;
}


//  these are the public interface methods, seen by the FDRInterface-derived classes.
template <class T> 
void CtiFDRIO<T>::send( T *toSend )
{
    RWMutexLock::LockGuard outboxGuard(_outboxMux);
    _outbox.push( toSend );
    _outputWaiting.signal( );
}


template <class T> 
T *CtiFDRIO<T>::recv( long milliseconds )
{
    T *tmp = NULL;
    cout << "trying to receive" << endl;
    if( _inputWaiting.acquire( milliseconds ) != RW_THR_TIMEOUT )
    {
        RWMutexLock::LockGuard inboxGuard(_inboxMux);
        //  unnecessary precaution, but what the heck.
        if( _inbox.size( ) )
        {
            tmp = _inbox.front( );
            _inbox.pop( );
        }
    }
    return tmp;
}

