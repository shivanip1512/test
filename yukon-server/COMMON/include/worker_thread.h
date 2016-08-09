#pragma once

#include "dlldefs.h"
#include "timing_util.h"
#include "ctitime.h"

#include "concurrentSet.h"

#include <boost/noncopyable.hpp>
#include <boost/thread.hpp>
#include <boost/optional.hpp>

#include <string>
#include <atomic>

namespace Cti {

class IM_EX_CTIBASE WorkerThread : private boost::noncopyable
{
public:

    class Function
    {
        void init()
        {
            assert( _function );
        }

    public:
        const std::function<void ()> _function;

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

        Function& name( const std::string &name )
        {
            _name = name + " (" + CtiTime{}.asString() + ")";
            return *this;
        }

        Function& priority( int priority )
        {
            _priority = priority;
            return *this;
        }
    };

    typedef boost::thread_interrupted Interrupted;

    WorkerThread( const Function &function );
    virtual ~WorkerThread();

    bool isRunning();
    void start();
    void interrupt();
    void terminateThread();

    void join();
    bool tryJoinFor( const Timing::Chrono &duration );
    void tryJoinOrTerminateFor( const Timing::Chrono &duration );

    static void interruptionPoint();
    static void sleepFor( const Timing::Chrono &duration );

protected:

    static bool isFailedTermination();

private:

    mutable boost::thread       _thread;
    Function                    _function;

    static ConcurrentSet<boost::thread::id> _failed_terminations;

    void executeWrapper();
};

} // namespace Cti
