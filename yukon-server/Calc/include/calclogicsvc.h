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
#include "msg_dbchg.h"
#include <queue>

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
    BOOL isANewCalcPointID(const long aPointID);
    BOOL parseMessage( RWCollectable *message, CtiCalculateThread *calcThread );
    void dropDispatchConnection( );
    void pauseInputThread();
    void resumeInputThread();
    void loadConfigParameters( );
    bool pointNeedsReload( long pointID );
    void reloadPointAttributes(long pointID);
    void updateCalcData();
    static void mainComplain( void *la );
    static void outComplain( void *la );
    static void inComplain( void *la );

private:

    RWThreadFunction _inputFunc;
    RWThreadFunction _outputFunc;

    CtiTime _dispatchPingedFailed, _lastDispatchMessageTime;
    bool _dispatchConnectionBad;
    bool _ok, _restart, _update;
    string _dispatchMachine;
    INT _dispatchPort;
    typedef queue<CtiDBChangeMsg> messageQueue;
    messageQueue _dbChangeMessages;
    CtiCalculateThread::CtiCalcThreadInterruptReason _interruptReason;
    CtiCalculateThread *calcThread;
    CtiConnection *_conxion;
    void _inputThread( void );
    void _outputThread( void );

    void _registerForPoints();

    static void sendUserQuit(void *who);

    time_t   _nextCheckTime;

};

#endif //  __CALC__LOGIC_H__
