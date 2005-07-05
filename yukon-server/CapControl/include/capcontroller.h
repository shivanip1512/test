/*-----------------------------------------------------------------------------
    Filename:  capcontroller.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for CtiCapController
                    Once started CtiCapController is reponsible
                    for determining if and when to adjust the
                    substation buses provided by the CtiCCSubstationBusStore.
                        
    Initial Date:  8/31/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
 
#ifndef CTICAPCONTROLLER_H
#define CTICAPCONTROLLER_H

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
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"
                       
class CtiCapController
{
public:
    
    static CtiCapController* getInstance();

    void start();
    void stop();

    void sendMessageToDispatch(CtiMessage* message);
    void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg);
    void confirmCapBankControl(CtiRequestMsg* pilRequest);
    RWPCPtrQueue< RWCollectable > &getInClientMsgQueueHandle();
    RWPCPtrQueue< RWCollectable > &getOutClientMsgQueueHandle();

    void loadControlLoopCParms();
    
private:
    
    CtiCapController();
    virtual ~CtiCapController();

    void controlLoop();

    CtiConnection* getPILConnection();
    CtiConnection* getDispatchConnection();
    void checkDispatch(ULONG secondsFrom1901);
    void checkPIL(ULONG secondsFrom1901);
    void registerForPoints(const RWOrdered& subBuses);
    void parseMessage(RWCollectable* message, ULONG secondsFrom1901);
    void pointDataMsg(long pointID, double value, unsigned quality, unsigned tags, RWTime& timestamp, ULONG secondsFrom1901);
    void porterReturnMsg(long deviceId, RWCString commandString, int status, RWCString resultString, ULONG secondsFrom1901);
    void signalMsg(long pointID, unsigned tags, RWCString text, RWCString additional, ULONG secondsFrom1901);

    static CtiCapController* _instance;
    RWThread _substationBusThread;

    CtiConnection* _pilConnection;
    CtiConnection* _dispatchConnection;
    mutable RWRecursiveLock<RWMutexLock> _mutex;

    RWPCPtrQueue< RWCollectable > _inClientMsgQueue;
    RWPCPtrQueue< RWCollectable > _outClientMsgQueue;

    int control_loop_delay;
    int control_loop_inmsg_delay;
    int control_loop_outmsg_delay;


};
#endif
