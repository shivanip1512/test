#pragma once

#include "connection_client.h"
#include "cservice.h"

#include "calcthread.h"

//ecs 1/4/2005
#include "thread_monitor.h"
#include "thread_register_data.h"
//
#include "msg_dbchg.h"
#include <queue>

class CtiCommandMsg;
class CtiSignalMsg;

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
    bool isDatabaseCalcPointID(const long aPointID);
    void parseMessage( const CtiMessage &message, CtiCalculateThread &calcThread );
    void terminateThreads( );
    void pauseInputThread();
    void resumeInputThread();
    void loadConfigParameters( );
    bool pointNeedsReload( long pointID );
    void reloadPointAttributes(long pointID);
    void updateCalcData();

private:

    RWThreadFunction _inputFunc;
    RWThreadFunction _outputFunc;

    CtiTime _dispatchPingedFailed, _lastDispatchMessageTime;
    bool _dispatchConnectionBad;
    bool _ok, _restart, _update;
    bool _threadsStarted;

    typedef std::queue<CtiDBChangeMsg> messageQueue;
    messageQueue _dbChangeMessages;

    CtiCalculateThread::CtiCalcThreadInterruptReason _interruptReason;

    boost::scoped_ptr<CtiCalculateThread>  calcThread;
    boost::scoped_ptr<CtiClientConnection> dispatchConnection;

    void _inputThread();
    void _outputThread();

    void _registerForPoints();

    void handleDbChangeMsg ( const CtiDBChangeMsg  &, CtiCalculateThread & );
    void handleCommandMsg  ( const CtiCommandMsg   & );
    void handlePointDataMsg( const CtiPointDataMsg &, CtiCalculateThread & );
    void handleMultiMsg    ( const CtiMultiMsg     &, CtiCalculateThread & );
    void handleSignalMsg   ( const CtiSignalMsg    &, CtiCalculateThread & );

    static void sendUserQuit( const std::string & who );

    time_t   _nextCheckTime;

};
