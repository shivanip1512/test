#include "precompiled.h"

#include "CalcWorkerThread.h"
#include "logger.h"



namespace Cti
{
namespace CalcLogic
{

CalcWorkerThread::CalcWorkerThread( const Function & function )
    :   WorkerThread( function ),
        _name( function._name ),
        _isPaused( false )
{
    // empty
}

CalcWorkerThread::~CalcWorkerThread()
{
    // empty
}

void CalcWorkerThread::pause()
{
    CTILOG_DEBUG( dout, "Pausing thread: " << _name );

    setPausedState( true );
}

void CalcWorkerThread::resume()
{
    CTILOG_DEBUG( dout, "Resuming thread: " << _name );

    setPausedState( false );
}

void CalcWorkerThread::waitForResume()
{
    if( isFailedTermination() )
    {
        throw WorkerThread::Interrupted();
    }

    boost::unique_lock<boost::mutex> lock( _pauseMutex );

    while ( _isPaused )
    {
        _pauseCond.wait( lock );
    }
}

void CalcWorkerThread::setPausedState( const bool isPaused )
{
    if( isFailedTermination() )
    {
        throw WorkerThread::Interrupted();
    }

    {
        boost::unique_lock<boost::mutex> lock( _pauseMutex );

        _isPaused = isPaused;
    }

    _pauseCond.notify_all();
}

}
}

