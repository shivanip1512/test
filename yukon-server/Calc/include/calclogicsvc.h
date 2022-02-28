#pragma once

#include "connection_client.h"
#include "cservice.h"

#include "calcthread.h"

#include "CalcWorkerThread.h"

#include "msg_dbchg.h"
#include <queue>

class CtiCommandMsg;
class CtiSignalMsg;

#define CALCLOGIC_APPLICATION_NAME  "Calc Server"

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
    void parseMessage( const CtiMessage &message, CtiCalculateThread &calcThread );
    void terminateThreads( );
    void pauseInputThread();
    void resumeInputThread();
    void loadConfigParameters( );
    bool pointNeedsReload( long pointID );
    void reloadPointAttributes(long pointID);
    void updateCalcData();

private:

    Cti::CalcLogic::CalcWorkerThread    _inputFunc;
    Cti::CalcLogic::CalcWorkerThread    _outputFunc;

    CtiTime _lastDispatchMessageTime;
    bool _dispatchConnectionBad;
    bool _ok, _restart, _update;
    bool _threadsStarted;

    typedef std::queue<CtiDBChangeMsg> messageQueue;
    messageQueue _dbChangeMessages;

    boost::scoped_ptr<CtiCalculateThread>  calcThread;
    boost::scoped_ptr<CtiClientConnection> dispatchConnection;

    void _inputThread();
    void _outputThread();

    void _registerForPoints();

    void handleDbChangeMsg ( const CtiDBChangeMsg  &, CtiCalculateThread & );
    void handlePointChange ( const CtiDBChangeMsg  &, CtiCalculateThread & );
    void handlePaoChange   ( const CtiDBChangeMsg  & );

    bool isDatabaseCalcPointID ( const long aPointID );
    bool isDatabaseCalcDeviceID( const long aDeviceID );

    void handleCommandMsg  ( const CtiCommandMsg   & );
    void handlePointDataMsg( const CtiPointDataMsg &, CtiCalculateThread & );
    void handleMultiMsg    ( const CtiMultiMsg     &, CtiCalculateThread & );
    void handleSignalMsg   ( const CtiSignalMsg    &, CtiCalculateThread & );

    static void sendUserQuit( const std::string & who );

    time_t   _nextCheckTime;

};
