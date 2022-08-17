#pragma once

#include "worker_thread.h"

namespace Cti {
namespace CalcLogic {

class CalcWorkerThread : public WorkerThread
{
public:

    CalcWorkerThread( const Function & function );

    virtual ~CalcWorkerThread();

    void pause();
    void resume();
    size_t waitForResume();  //  returns pause count

    size_t getPauseCount() const;

private:

    void setPausedState( const bool isPaused );

    bool                        _isPaused;
    boost::mutex                _pauseMutex;
    boost::condition_variable   _pauseCond;
    std::atomic<size_t>         _pauseCount;

    std::string                 _name;
};

}
}

