#pragma once

#include "worker_thread.h"



namespace Cti
{
namespace CalcLogic
{

class CalcWorkerThread : public WorkerThread
{
public:

    CalcWorkerThread( const Function & function );

    virtual ~CalcWorkerThread();

    void pause();
    void resume();
    void waitForResume();

private:

    void setPausedState( const bool isPaused );

    bool                        _isPaused;
    boost::mutex                _pauseMutex;
    boost::condition_variable   _pauseCond;

    std::string                 _name;
};

}
}

