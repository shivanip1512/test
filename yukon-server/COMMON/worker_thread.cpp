#include "precompiled.h"

#include "utility.h"
#include "logger.h"
#include "worker_thread.h"
#include "win_helper.h"


namespace Cti {

ConcurrentSet<boost::thread::id> WorkerThread::_failed_terminations;

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

    // Don't try joining ourself
    if (_thread.get_id() == boost::this_thread::get_id())
    {
        return true;    //self is definitely running
    }

    return ! tryJoinFor( Timing::Chrono::milliseconds(0) );
}

/**
 * Check to see if this thread was unsuccessfully terminated
 */
bool WorkerThread::isFailedTermination()
{
    return _failed_terminations.contains(boost::this_thread::get_id());
}

/**
 * start the worker thread if the thread is not currently running
 * thread can be started multiple times
 */
void WorkerThread::start()
{
    // make sure the thread is not running
    assert( ! isRunning() );

    CTILOG_INFO(dout, "Creating thread : " << _function._name);

    // create and start the thread (boost:thread uses boost::bind implicitly)
    _thread = boost::thread( &WorkerThread::executeWrapper, this );

    _failed_terminations.erase(_thread.get_id());
}

/**
 * request that the thread will be interrupted
 */
void WorkerThread::interrupt()
{
    CTILOG_INFO(dout, "Interrupting thread " << _function._name);

    _thread.interrupt();
}

/**
 * force termination of the thread
 */
void WorkerThread::terminateThread()
{
    if( TerminateThread(_thread.native_handle(), EXIT_SUCCESS) )
    {
        CTILOG_INFO(dout, "Successfully terminated thread " << _function._name);
    }
    else
    {
        const auto errorCode = GetLastError();

        CTILOG_ERROR(dout, "TerminateThread failed to terminate thread " << _function._name <<
                           ", error " << errorCode << ": " << getSystemErrorMessage(errorCode));
    }
}

/**
 * wait for thread execution to complete
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::join()
{
    CTILOG_INFO(dout, "Joining thread " << _function._name);

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
    if (_thread.get_id() == boost::thread::id())
    {
        CTILOG_INFO(dout, "Thread " << _function._name << " no longer exists.");
        return true;
    }

    CTILOG_INFO(dout, "Trying to join thread " << _function._name);

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

        terminateThread();
    }
}
/**
 * check if interruption as been requested
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::interruptionPoint()
{
    if( isFailedTermination() )
    {
        CTILOG_ERROR(dout, "Current thread is a failed termination, interrupting!");

        throw boost::thread_interrupted();
    }

    boost::this_thread::interruption_point();
}

/**
 * sleep for milliseconds, can be interrupted
 *
 * @throw  boost::thread_interrupted
 */
void WorkerThread::sleepFor( const Timing::Chrono &duration )
{
    if( isFailedTermination() )
    {
        CTILOG_ERROR(dout, "Current thread is a failed termination, interrupting!");

        throw boost::thread_interrupted();
    }

    boost::this_thread::sleep_for(boost::chrono::milliseconds(duration.milliseconds()));
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

    //  Grab a copy in case the WorkerThread is destroyed while were're executing
    auto functionName = _function._name;

    if( !_function._name.empty() )
    {
        SetThreadName( -1, _function._name.c_str() );
    }

    CTILOG_INFO(dout, "Thread starting : " << functionName);

    try
    {
        _function._function();
    }
    catch( const Interrupted& )
    {
        CTILOG_WARN(dout, "Thread interrupted: " << functionName);
    }
    catch( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Thread aborting due to unhandled exception: " << functionName);
    }

    CTILOG_INFO(dout, "Thread exiting: " << functionName);
}

} // namespace Cti

