#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALC_LOGIC_H__
#define __CALC_LOGIC_H__

#include "connection.h"
#include "cservice.h"

#include "calcthread.h"

//ecs 1/4/2005
#include "thread_monitor.h"
#include "thread_register_data.h"
//

class CtiCalcLogicService : public CService
{
public:
    CtiCalcLogicService(LPCTSTR szName, LPCTSTR szDisplay, DWORD dwType);
    virtual void Run();

    void RunInConsole(DWORD argc, LPTSTR* argv);

    DECLARE_SERVICE(CtiCalcLogicService, CALCLOGIC)

protected:
    virtual void OnStop();
    virtual void Init();
    virtual void DeInit();
    virtual void ParseArgs(DWORD argc, LPTSTR* argv);

    bool readCalcPoints( CtiCalculateThread *calcThread );
    BOOL parseMessage( RWCollectable *message, CtiCalculateThread *calcThread );
    void dropDispatchConnection( );

    //
    // ecs 1/5/2005
    //
    static void mainComplain( void *la );
    static void outComplain( void *la );
    static void inComplain( void *la );

private:

    RWThreadFunction _inputFunc;
    RWThreadFunction _outputFunc;

    RWTime _dispatchPingedFailed;
    bool _ok, _restart;
    RWCString _dispatchMachine;
    INT _dispatchPort;
    CtiCalculateThread *calcThread;
    CtiConnection *_conxion;
    void _inputThread( void );
    void _outputThread( void );
};

#endif //  __CALC__LOGIC_H__
