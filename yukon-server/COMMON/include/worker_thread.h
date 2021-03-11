#pragma once

#include "dlldefs.h"
#include "timing_util.h"
#include "ctitime.h"

#include "concurrentSet.h"

#include <boost/noncopyable.hpp>
#include <boost/thread.hpp>
#include <boost/optional.hpp>
#include <boost/bind/bind.hpp>

#include <string>
#include <atomic>

namespace Cti::CalcLogic {
    class CalcWorkerThread;
}

namespace Cti {

class IM_EX_CTIBASE WorkerThread : private boost::noncopyable
{
public:

    class FunctionImpl
    {
    private:
        void init()
        {
            assert( _function );
        }

        const std::function<void ()> _function;

        template <typename F>
        FunctionImpl( F f ) : _function(f)
        {
            init();
        }

        template <typename F, typename A1>
        FunctionImpl( F f, A1 a1 ) : _function( boost::bind( f, a1 ))
        {
            init();
        }

        template <typename F, typename A1, typename A2>
        FunctionImpl( F f, A1 a1, A2 a2 ) : _function( boost::bind( f, a1, a2 ))
        {
            init();
        }

        template <typename F, typename A1, typename A2, typename A3>
        FunctionImpl( F f, A1 a1, A2 a2, A3 a3 ) : _function( boost::bind( f, a1, a2, a3 ))
        {
            init();
        }

        // optional parameters
        std::string                  _name;
        boost::optional<int>         _priority;

        FunctionImpl& name( const std::string& function_name)
        {
            _name = function_name + " (" + CtiTime{}.asString() + ")";
            return *this;
        }
    public:
        FunctionImpl& priority( int priority )
        {
            _priority = priority;
            return *this;
        }

        friend class WorkerThread;
        friend class Cti::CalcLogic::CalcWorkerThread;
    };

    typedef boost::thread_interrupted Interrupted;

    WorkerThread( const FunctionImpl& function );
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

    //  Require callers to provide a name before we expose FunctionImpl
    struct FunctionBuilder {
        FunctionImpl function;
        FunctionImpl name(const std::string& function_name) {
            return function.name(function_name);
        }
    };

    template <typename... Ts>
    static FunctionBuilder Function(Ts... args) {
        return { FunctionImpl(args...) };
    }

protected:

    static bool isFailedTermination();

private:

    mutable boost::thread       _thread;
    FunctionImpl                _function;

    static ConcurrentSet<boost::thread::id> _failed_terminations;

    void executeWrapper();
};

} // namespace Cti
