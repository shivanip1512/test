/*-----------------------------------------------------------------------------
    Filename:  controller.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for CtiCController
                    Once started CtiCController is reponsible
                    for determining if and when to run the
                    schedules provided by the CtiCCStrategyStore.
                        
    Initial Date:  8/16/2000
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2000
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
 
#ifndef CTICCONTROLLER_H
#define CTICCONTROLLER_H

#include <rw/thr/thrfunc.h>
#include <rw/thr/runfunc.h>
#include <rw/thr/srvpool.h>
#include <rw/thr/thrutil.h>
#include <rw/thr/countptr.h> 
#include <rw/collect.h>

#include "dbaccess.h"
#include "connection.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "pointtypes.h"
#include "configparms.h"
#include "controller.h"
#include "strategystore.h"
#include "executor.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"
                       
class CtiCCStrategy;
class CtiCCExecutor;

class CtiCController
{
public:
    
    static CtiCController* Instance();

    void start();
    void stop();

    void sendMessageToDispatch(CtiMessage* message);
    void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg);
    void confirmCapBankControl(CtiRequestMsg* pilRequest);
    
private:
    
    CtiCController();
    virtual ~CtiCController();

    void controlLoop();

    CtiConnection* getPILConnection();
    CtiConnection* getDispatchConnection();
    void checkDispatch();
    void checkPIL();
    void registerForPoints(CtiCCStrategyStore* store);
    void parseMessage( RWCollectable *message );
    void pointDataMsg( long pointID, double value, unsigned tags, RWTime& timestamp );
    void porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString );
    void signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional );

    static CtiCController* _instance;
    RWThread _stratthr;

    CtiConnection* _pilConnection;
    CtiConnection* _dispatchConnection;
    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif
