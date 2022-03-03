#include "precompiled.h"

#include "CalcWorkerThread.h"
#include "logger.h"

namespace Cti {
namespace CalcLogic {

CalcWorkerThread::CalcWorkerThread( const FunctionImpl & function )
    :   WorkerThread( function ),
        _name( function._name ),
        _isPaused( false ),
        _isWaiting( false ),
        _pauseCount{ 0 }
{
    // empty
}

CalcWorkerThread::~CalcWorkerThread()
{
    // empty
}

//  Called by parent thread
void CalcWorkerThread::pause()
{
    CTILOG_DEBUG( dout, "Pausing thread: " << _name );

    setPausedState( true );
}

//  Called by parent thread
void CalcWorkerThread::resume()
{
    CTILOG_DEBUG( dout, "Resuming thread: " << _name );

    setPausedState( false );
}

size_t CalcWorkerThread::waitForResume()
{
    if( isFailedTermination() )
    {
        CTILOG_ERROR(dout, "Current thread is a failed termination, interrupting!");

        throw WorkerThread::Interrupted();
    }

    boost::unique_lock<boost::mutex> lock( _pauseMutex );

    _isWaiting = true;

    while ( _isPaused )
    {
        _pauseCond.wait( lock );
    }

    _isWaiting = false;

    return getPauseCount();
}

size_t CalcWorkerThread::getPauseCount() const
{
    return _pauseCount;
}

//  Called by parent thread via pause/resume
void CalcWorkerThread::setPausedState(const bool isPaused)
{
    {
        boost::unique_lock<boost::mutex> lock( _pauseMutex );

        if( isPaused )
        {
            ++_pauseCount;
        }

        _isPaused = isPaused;
    }

    _pauseCond.notify_all();
}

}
}

