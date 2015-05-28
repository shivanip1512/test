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
    _function(function)
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
    if( _thread.get_id() == boost::thread::id() )
    {
        return false;
    }

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
    return _thread.try_join_for( boost::chrono::milliseconds( duration.milliseconds() ));
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
        CTILOG_WARN(dout, "Join did not complete for " << duration << ", terminating thread " << _function._name);

        terminate();
    }
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
    boost::this_thread::sleep_for( boost::chrono::milliseconds( duration.milliseconds() ));
}

/**
 * thread wrapper function executed on top of the function provided
 */
void WorkerThread::executeWrapper()
{
    if( _function._priority )
    {
        SetThreadPriority( GetCurrentThread(), *_function._priority );
    }

    if( !_function._name.empty() )
    {
        SetThreadName( -1, _function._name.c_str() );
    }

    // verbose is enable by default
    if( _function._verbose )
    {
        CTILOG_INFO(dout, "Thread starting : " << _function._name);
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
            CTILOG_WARN(dout, _function._name <<"Thread interrupted: "<< _function._name);
        }
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Thread aborting due to unhandled exception: "<< _function._name);
    }

    if( _function._verbose )
    {
        CTILOG_INFO(dout, "Thread exiting: "<< _function._name);
    }
}

} // namespace Cti

