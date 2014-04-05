#include "precompiled.h"

#include "utility.h"
#include "logger.h"
#include "worker_thread.h"


namespace Cti {

/**
 * class constructor
 *
 * @param function thread function to run
 */
WorkerThread::WorkerThread( const Function &function ) :
    _function(function),
    _pause(false)
{
}

/**
 * class destructor, assert that the thread is not running
 */
WorkerThread::~WorkerThread()
{
    assert( ! isRunning() );
}

/**
 * check if the thread is running
 *
 * @return true  if the thread is currently running,
 *         false if the thread is not started or terminated
 * @throw  boost::thread_interrupted
 */
bool WorkerThread::isRunning()
{
    return ! tryJoinFor( Timing::Chrono::milliseconds(0) );
}

/**
 * start the worker thread if the thread is not currently running
 * thread can be started multiple times
 */
void WorkerThread::start()
{
    // make sure the thread is not running
    assert( ! isRunning() );

    // create and start the thread (boost:thread uses boost::bind implicitly)
    _thread = boost::thread( &WorkerThread::executeWrapper, this );
}

/**
 * request that the thread will be interrupted
 */
void WorkerThread::interrupt()
{
    _thread.interrupt();
}

/**
 * force termination of the thread
 */
void WorkerThread::terminate()
{
    TerminateThread( _thread.native_handle(), EXIT_SUCCESS );
}

/**
 * wait for thread execution to complete
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::join()
{
    _thread.join();
}

/**
 * try to wait for thread execution to complete
 *
 * @param  duration
 * @return true if thread execution as completed,
 *         false otherwise
 * @throw  boost::thread_interrupted
 */
bool WorkerThread::tryJoinFor( const Timing::Chrono &duration )
{
    // try_join_for() is available in boost 1.50+
    // return _thread.try_join_for( boost::chrono::milliseconds( duration.milliseconds() ));
    return _thread.timed_join( boost::posix_time::milliseconds( duration.milliseconds() ));
}

/**
 * try to join and terminate if there's a timeout
 *
 * @param  duration
 * @throw  boost::thread_interrupted
 */
void WorkerThread::tryJoinOrTerminateFor( const Timing::Chrono &duration )
{
    if( ! tryJoinFor( duration ))
    {
        logEvent( "**** WARNING **** Join did not complete for " + duration.toString() + ", terminating", __FILE__, __LINE__ );

        terminate();
    }
}

/**
 * request the thread to pause
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::pause()
{
    boost::unique_lock<boost::mutex> lock( _pauseMutex );

    _pause = true;

    _pauseCond.wait( lock );
}

/**
 * try to request the thread to pause using a timeout in milliseconds,
 * cancel the pause request and return
 *
 * @param  duration
 * @return true if thread is paused,
 *         false otherwize
 */
bool WorkerThread::tryForDurationToPause( const Timing::Chrono &duration )
{
    boost::unique_lock<boost::mutex> lock( _pauseMutex );

    _pause = true;

    // wait_for() is available in boost 1.50+
    // if( !_pausedCond.wait_for( lock, boost::chrono::milliseconds( duration.milliseconds() )))
    if( !_pauseCond.timed_wait( lock, boost::posix_time::milliseconds( duration.milliseconds() )))
    {
        _pause = false;
    }

    return _pause;
}

/**
 * request the thread to resume execution
 * call only if pause(), or if tryPauseForMillis() has succeeded
 *
 * @return true if thread is paused,
 *         false otherwize
 */
void WorkerThread::resume()
{
    {
        boost::unique_lock<boost::mutex> lock( _pauseMutex );

        _pause = false;
    }

    _resumeCond.notify_one();
}

/**
 * block if paused and wait for call on resume()
 *
 * @return true if thread as paused, and as been resumed,
 *         false otherwise
 * @throw  boost::thread_interrupted
 */
bool WorkerThread::waitForResume()
{
    {
        boost::unique_lock<boost::mutex> lock( _pauseMutex );

        if( _pause )
        {
            _pauseCond.notify_one();

            _resumeCond.wait( lock ); // throws boost::thread_interrupted

            return true;
        }
    }

    interruptionPoint(); // throws boost::thread_interrupted

    return false;
}

/**
 * check if interruption as been requested
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::interruptionPoint()
{
    boost::this_thread::interruption_point();
}

/**
 * sleep for milliseconds, can be interrupted
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::sleepFor( const Timing::Chrono &duration )
{
    // sleep_for() is available in boost 1.50+
    //boost::this_thread::sleep_for( boost::chrono::milliseconds( duration.milliseconds() ));
    boost::this_thread::sleep( boost::posix_time::milliseconds( duration.milliseconds() ));
}

/**
 * wrapper function
 */
void WorkerThread::executeWrapper()
{
    if( _function._priority )
    {
        SetThreadPriority( GetCurrentThread(), *_function._priority );
    }

    if( _function._name )
    {
        SetThreadName( -1, _function._name->c_str() );
    }

    // verbose is enable by default
    if( _function._verbose )
    {
        logEvent( "Started", __FILE__, __LINE__ );
    }

    try
    {
        _function._function();
    }
    catch( const Interrupted& )
    {
        // verbose is enable by default
        if( _function._verbose )
        {
            logEvent( "Caught interrupted exception", __FILE__, __LINE__ );
        }
    }
    catch( ... )
    {
        logEvent( "**** EXCEPTION **** Caught unknown exception", __FILE__, __LINE__ );
    }

    if( _function._verbose )
    {
        logEvent( "Terminating", __FILE__, __LINE__ );
    }
}

/**
 * Wrapper function to log events
 * @param note
 * @param file
 * @param line
 */
inline void WorkerThread::logEvent( const std::string &note, const std::string &file, int line )
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << CtiTime() << " " << note << " thread TID:" << GetCurrentThreadId();

    if( _function._name )
    {
        dout << " \"" << *_function._name << "\"";
    }

    dout << " " << file << " (" << line << ")" << std::endl;
}

} // namespace Cti

