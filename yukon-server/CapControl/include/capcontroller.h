#pragma once

#include "dbaccess.h"
#include "connection_client.h"
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
#include "dllbase.h"
#include "logger.h"
#include "yukon.h"
#include "ctdpcptrq.h"

#include <boost/thread.hpp>

class CtiReturnMsg;

#define CAPCONTROL_APPLICATION_NAME  "CapControl Server"

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

        static void sendCapBankRequestAndPoints(std::auto_ptr<CtiRequestMsg> msg, CtiMultiMsg* multiMsg = NULL);

        virtual void sendMessageToDispatch(CtiMessage* message, Cti::CallSite cs);
        virtual void manualCapBankControl(CtiRequestMsg* pilRequest, CtiMultiMsg* multiMsg = NULL);
        static void submitEventLogEntry(const Cti::CapControl::EventLogEntry &event);
        static void submitEventLogEntries(const Cti::CapControl::EventLogEntries &events);

        virtual void confirmCapBankControl(CtiMultiMsg* pilMultiMsg, CtiMultiMsg* multiMsg);
        static CtiConnection::Que_t& getInClientMsgQueueHandle();
        CtiPCPtrQueue< CtiMessage > &getOutClientMsgQueueHandle();

        void handleUnsolicitedMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints & twoWayPts);
        void handleUnexpectedUnsolicitedMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints & twoWayPts);
        void handleRejectionMessaging(CtiCCCapBank* currentCapBank, CtiCCFeeder* currentFeeder,
                                        CtiCCSubstationBus* currentSubstationBus, CtiCCTwoWayPoints & twoWayPts);

        void analyzeVerificationBus(CtiCCSubstationBus* currentSubstationBus, const CtiTime& currentDateTime,
                                CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages,
                                CtiMultiMsg_vec& capMessages);

        void broadcastMessagesToClient(CtiCCSubstationBus_vec& substationBusChanges, CtiCCSubstation_vec& stationChanges,
                                       CtiCCArea_vec& areaChanges, long broadCastMask);
        void readClientMsgQueue();
        void checkBusForNeededControl(CtiCCAreaPtr currentArea, CtiCCSubstation* currentSubstation, CtiCCSubstationBus* currentSubstationBus, const CtiTime& currentDateTime,
                                CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);

        DispatchConnectionPtr getDispatchConnection();
        boost::shared_ptr<CtiClientConnection> getPorterConnection();

        void processNewMessage(CtiMessage* message);

        void adjustAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentBus);
        void handleAlternateBusModeValues(long pointID, double value, CtiCCSubstationBusPtr currentSubstationBus);

    protected:

        void porterReturnMsg(const CtiReturnMsg &retMsg);

    private:

        //Thread Functions
        void controlLoop();
        void messageSender();
        void outClientMsgs();
        virtual void enqueueEventLogEntry(const Cti::CapControl::EventLogEntry &event);
        void enqueueEventLogEntries(const Cti::CapControl::EventLogEntries &events);
        void writeEventLogsToDatabase();
        void incomingMessageProcessor();

        void checkDispatch();
        void checkPIL();

        enum class RegistrationMethod {
            ReRegisterAllPoints,
            RegisterOnlyNewPoints
        };
        void registerForPoints( const RegistrationMethod m = RegistrationMethod::RegisterOnlyNewPoints );

        void updateAllPointQualities(long quality);

        void parseMessage(CtiMessage* message);
        void pointDataMsg(CtiPointDataMsg* message);
        void pointDataMsgBySubBus(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgByFeeder(long pointID, double value, unsigned quality, CtiTime& timestamp);
        void pointDataMsgByCapBank(long pointID, double value, unsigned quality, unsigned tags, CtiTime& timestamp);
        void pointDataMsgBySubstation( long pointID, double value, unsigned quality, CtiTime& timestamp);
        void signalMsg(long pointID, unsigned tags, const std::string& text, const std::string& additional);
        void checkDisablePaoPoint(CapControlPao* pao, long pointID, bool disable, long enableCommand, long disableCommand);

        bool isCbcDbChange(const CtiDBChangeMsg* dbChange);
        CcDbReloadInfo resolveCapControlType(CtiDBChangeMsg *dbChange);
        CcDbReloadInfo resolveCapControlTypeByDataBase(CtiDBChangeMsg *dbChange);

        static CtiCapController* _instance;

        boost::thread   _substationBusThread;
        boost::thread   _outClientMsgThread;
        boost::thread   _messageSenderThread;
        boost::thread   _incomingMessageProcessorThread;

        boost::shared_ptr<CtiClientConnection> _porterConnection;
        DispatchConnectionPtr _dispatchConnection;

        typedef Cti::readers_writer_lock_t Lock;
        typedef Lock::reader_lock_guard_t  ReaderGuard;
        typedef Lock::writer_lock_guard_t  WriterGuard;

        mutable Lock _porterConnectionLock;
        mutable Lock _dispatchConnectionLock;

        CtiPCPtrQueue< CtiMessage > _incomingMessageQueue;
        static CtiConnection::Que_t _inClientMsgQueue;
        CtiPCPtrQueue< CtiMessage > _outClientMsgQueue;

        CtiValueQueue<Cti::CapControl::EventLogEntry> _eventLogs;
};
