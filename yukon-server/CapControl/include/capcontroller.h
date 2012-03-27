#pragma once

#include <rw/thr/thrfunc.h>
#include <rw/thr/runfunc.h>
#include <rw/thr/srvpool.h>
#include <rw/thr/thrutil.h>
#include <rw/thr/countptr.h>
#include <rw/collect.h>

#include "dbaccess.h"
#include "connection.h"
#include "DispatchConnection.h"
#include "CapControlDispatchConnection.h"
#include "MessageListener.h"
#include "message.h"
#include "msg_multi.h"
#include "msg_cmd.h"
#include "msg_dbchg.h"
#include "msg_reg.h"
#include "msg_pdata.h"
#include "msg_ptreg.h"
#include "pointtypes.h"
#include "ccsubstationbusstore.h"
#include "ccexecutor.h"
#include "ctibase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"
//#include "CtiPCPtrQueue.h"

class CtiCapController : public MessageListener
{
    public:

        CtiCapController();
        virtual ~CtiCapController();
        static CtiCapController* getInstance();
        static void setInstance(CtiCapController* controller);
        static void deleteInstance();

        void start();
        void stop();

        virtual void sendMessageToDispatch(CtiMessage* message);
        virtual void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg = NULL);
        virtual void sendEventLogMessage(CtiMessage* message);

        void confirmCapBankControl(CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg);
        CtiPCPtrQueue< RWCollectable > &getInClientMsgQueueHandle();
        CtiPCPtrQueue< RWCollectable > &getOutClientMsgQueueHandle();
        CtiPCPtrQueue< RWCollectable > &getCCEventMsgQueueHandle();

        void loadControlLoopCParms();
        void handleUnsolicitedMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints* twoWayPts);
        void handleUnexpectedUnsolicitedMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints* twoWayPts);
        void handleRejectionMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints* twoWayPts);

        void analyzeVerificationBus(CtiCCSubstationBus* currentSubstationBus, const CtiTime& currentDateTime,
                                CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages,
                                CtiMultiMsg_vec& capMessages);

        void broadcastMessagesToClient(CtiCCSubstationBus_vec& substationBusChanges, CtiCCSubstation_vec& stationChanges,
                                       CtiCCArea_vec& areaChanges, long broadCastMask);
        void readClientMsgQueue();
        void checkBusForNeededControl(CtiCCArea* currentArea, CtiCCSubstation* currentSubstation, CtiCCSubstationBus* currentSubstationBus, const CtiTime& currentDateTime,
                                CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);

        DispatchConnectionPtr getDispatchConnection();
        CtiConnectionPtr getPILConnection();

        void processNewMessage(CtiMessage* message);

        void adjustAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentBus);
        void handleAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentSubstationBus);

    private:


        //Thread Functions
        void controlLoop();
        void messageSender();
        void outClientMsgs();
        void processCCEventMsgs();
        void incomingMessageProcessor();

        void checkDispatch();
        void checkPIL();

        void registerForPoints(const CtiCCSubstationBus_vec& subBuses);
        void updateAllPointQualities(long quality);

        void parseMessage(RWCollectable* message);
        void pointDataMsg(CtiPointDataMsg* message);
        void pointDataMsgBySubBus(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgByFeeder(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgByCapBank(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp);
        void pointDataMsgByArea(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgBySpecialArea(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgBySubstation( long pointID, double value, unsigned quality, CtiTime& timestamp);
        void porterReturnMsg(long deviceId, const string& commandString, int status, const string& resultString);
        void signalMsg(long pointID, unsigned tags, const string& text, const string& additional);
        void checkDisablePaoPoint(CapControlPao* pao, long pointID, bool disable, long enableCommand, long disableCommand);

        bool isCbcDbChange(const CtiDBChangeMsg* dbChange);

        static CtiCapController* _instance;
        RWThread _substationBusThread;
        RWThread _outClientMsgThread;
        RWThread _messageSenderThread;
        RWThread _incomingMessageProcessorThread;

        CtiConnectionPtr _pilConnection;
        DispatchConnectionPtr _dispatchConnection;

        CtiPCPtrQueue< CtiMessage > _incomingMessageQueue;
        CtiPCPtrQueue< RWCollectable > _inClientMsgQueue;
        CtiPCPtrQueue< RWCollectable > _outClientMsgQueue;

        CtiPCPtrQueue< RWCollectable > _ccEventMsgQueue;

        int control_loop_delay;
        int control_loop_inmsg_delay;
        int control_loop_outmsg_delay;
};
