/*-----------------------------------------------------------------------------
    Filename:  loadmanager.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for CtiLoadManager
                    Once started CtiLoadManager is reponsible
                    for determining if and when to run the
                    programs provided in the CtiLMControlAreaStore.
                        
    Initial Date:  2/6/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
 
#ifndef CTILOADMANAGER_H
#define CTILOADMANAGER_H

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
#include "loadmanager.h"
#include "lmcontrolareastore.h"
#include "executor.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"
#include "queue.h"
                       
class CtiLMControlArea;
class CtiLMExecutor;

class CtiLoadManager
{
public:
    
    static CtiLoadManager* getInstance();

    void start();
    void stop();

    void handleMessage(CtiMessage* msg);
    
    void sendMessageToDispatch(CtiMessage* message);
    void sendMessageToPIL(CtiMessage* message);
    void sendMessageToNotification(CtiMessage* message);
    void sendMessageToClients(CtiMessage* message);
    
private:
    
    CtiLoadManager();
    virtual ~CtiLoadManager();

    void controlLoop();

    CtiConnection* getPILConnection();
    CtiConnection* getDispatchConnection();
    CtiConnection* getNotificationConnection();
	
    void checkDispatch(ULONG secondsFrom1901);
    void checkPIL(ULONG secondsFrom1901);
    void registerForPoints(const RWOrdered& controlAreas);
    void parseMessage( RWCollectable *message, ULONG secondsFrom1901 );
    void pointDataMsg( long pointID, double value, unsigned quality, unsigned tags, RWTime& timestamp, ULONG secondsFrom1901 );
    void porterReturnMsg( long deviceId, RWCString commandString, int status, RWCString resultString, ULONG secondsFrom1901 );
    void signalMsg( long pointID, unsigned tags, RWCString text, RWCString additional, ULONG secondsFrom1901 );

    void loadControlLoopCParms();
    
    static CtiLoadManager* _instance;
    RWThread _loadManagerThread;

    CtiQueue< CtiMessage, less< CtiMessage > > _main_queue;
    
    CtiConnection* _pilConnection;
    CtiConnection* _dispatchConnection;
    CtiConnection* _notificationConnection;
    
    int control_loop_delay;
    int control_loop_inmsg_delay;
    int control_loop_outmsg_delay;
    
    mutable RWRecursiveLock<RWMutexLock> _mutex;
};
#endif
