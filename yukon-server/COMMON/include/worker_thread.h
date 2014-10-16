#pragma once

#include <string>

#include "boost/noncopyable.hpp"
#include "boost/thread/thread.hpp"
#include "boost/thread/condition_variable.hpp"
#include "boost/optional/optional.hpp"

#include "dlldefs.h"
#include "timing_util.h"

namespace Cti {

class IM_EX_CTIBASE WorkerThread : private boost::noncopyable
{
public:

    class Function
    {
        void init()
        {
            assert( _function );
            _verbose = true;
        }

    public:
        const boost::function<void ()> _function;

        template <typename F>
        Function( F f ) : _function(f)
        {
            init();
        }

        template <typename F, typename A1>
        Function( F f, A1 a1 ) : _function( boost::bind( f, a1 ))
        {
            init();
        }

        template <typename F, typename A1, typename A2>
        Function( F f, A1 a1, A2 a2 ) : _function( boost::bind( f, a1, a2 ))
        {
            init();
        }

        template <typename F, typename A1, typename A2, typename A3>
        Function( F f, A1 a1, A2 a2, A3 a3 ) : _function( boost::bind( f, a1, a2, a3 ))
        {
            init();
        }

        // optional parameters
        std::string                  _name;
        boost::optional<int>         _priority;
        bool                         _verbose;

        Function& name( const std::string &name )
        {
            _name = name;
            return *this;
        }

        Function& priority( int priority )
        {
            _priority = priority;
            return *this;
        }

        Function& verbose( bool verbose )
        {
            _verbose = verbose;
            return *this;
        }
    };

    typedef boost::thread_interrupted Interrupted;

    WorkerThread( const Function &function );
    virtual ~WorkerThread();

    bool isRunning();
    void start();
    void interrupt();
    void terminate();

    void join();
    bool tryJoinFor( const Timing::Chrono &duration );
    void tryJoinOrTerminateFor( const Timing::Chrono &duration );

    void pause();
    bool tryForDurationToPause( const Timing::Chrono &duration );
    void resume();
    bool waitForResume();

    static void interruptionPoint();
    static void sleepFor( const Timing::Chrono &duration );
    
private:

    mutable boost::thread       _thread;
    Function                    _function;

    bool volatile               _pause;
    boost::mutex                _pauseMutex;
    boost::condition_variable   _pauseCond;
    boost::condition_variable   _resumeCond;

    void executeWrapper();
};

} // namespace Cti
