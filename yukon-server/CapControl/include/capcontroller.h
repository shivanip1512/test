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
//#include "CtiPCPtrQueue.h"
                       
class CtiCapController
{
public:
    
    static CtiCapController* getInstance();

    void start();
    void stop();

    void sendMessageToDispatch(CtiMessage* message);
    void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg);
    void confirmCapBankControl(CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg);
    CtiPCPtrQueue< RWCollectable > &getInClientMsgQueueHandle();
    CtiPCPtrQueue< RWCollectable > &getOutClientMsgQueueHandle();
    CtiPCPtrQueue< RWCollectable > &getCCEventMsgQueueHandle();

    void loadControlLoopCParms();
    void refreshCParmGlobals(bool force);

private:
    
    CtiCapController();
    virtual ~CtiCapController();

    void controlLoop();
    //void processCCEventMsgs(CtiMultiMsg* msgMulti);
    void processCCEventMsgs();

    CtiConnection* getPILConnection();
    CtiConnection* getDispatchConnection();
    void checkDispatch(ULONG secondsFrom1901);
    void checkPIL(ULONG secondsFrom1901);
    void registerForPoints(const CtiCCSubstationBus_vec& subBuses);
    void updateAllPointQualities(long quality, ULONG secondsFrom1901);
    
    void parseMessage(RWCollectable* message, ULONG secondsFrom1901);
    void pointDataMsg(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgBySubBus(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgByFeeder(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgByCapBank(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgByArea(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgBySpecialArea(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901);
    void pointDataMsgBySubstation( long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp, ULONG secondsFrom1901 );
    void porterReturnMsg(long deviceId, const string& commandString, int status, const string& resultString, ULONG secondsFrom1901);
    void signalMsg(long pointID, unsigned tags, const string& text, const string& additional, ULONG secondsFrom1901);

    static CtiCapController* _instance;
    RWThread _substationBusThread;
    //RWThread _ccEventMsgThread;

    CtiConnection* _pilConnection;
    CtiConnection* _dispatchConnection;
    mutable RWRecursiveLock<RWMutexLock> _mutex;

    CtiPCPtrQueue< RWCollectable > _inClientMsgQueue;
    CtiPCPtrQueue< RWCollectable > _outClientMsgQueue;

    CtiPCPtrQueue< RWCollectable > _ccEventMsgQueue;

    int control_loop_delay;
    int control_loop_inmsg_delay;
    int control_loop_outmsg_delay;


};
#endif
