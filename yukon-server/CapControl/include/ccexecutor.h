#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#pragma once

#include "ccmessage.h"
#include "msg_signal.h"
#include "AttributeService.h"
#include "ccutil.h"
#include "VoltageRegulatorManager.h"

#include <rw/thr/countptr.h>
#include <rw/thr/thrfunc.h>
#include <rw/thr/barrier.h>

class CtiCCExecutor
{
    public:
        virtual ~CtiCCExecutor() {};

        virtual void execute() {};

    protected:
        CtiCCExecutor() {};
        void moveCapBank(INT permanentFlag, LONG oldFeederId, LONG movedCapBankId, LONG newFeederId, float capSwitchingOrder, float closeOrder, float tripOrder);
        void moveFeeder(BOOL permanentFlag, LONG oldSubBusId, LONG movedFeederId, LONG newSubBusId, float fdrSwitchingOrder);
};

class CtiCCClientMsgExecutor : public CtiCCExecutor
{
    public:
        CtiCCClientMsgExecutor(CtiMessage* ccMsg) : _ccMsg(ccMsg){};
        virtual ~CtiCCClientMsgExecutor(){delete _ccMsg;};

        virtual void execute();

    private:

        CtiMessage* _ccMsg;
};

class CtiCCCommandExecutor : public CtiCCExecutor
{
    public:
        CtiCCCommandExecutor(CtiCCCommand* command) : _command(command)
        {
            _attributeService = new AttributeService();
        };

        virtual ~CtiCCCommandExecutor()
        {
            if (_attributeService != NULL)
            {
                delete _attributeService;
            }
            delete _command;
        };

        virtual void execute();

        void setAttributeService(AttributeService* attributeService);

    private:
        //Command Functions
        void EnableSubstationBus();
        void DisableSubstationBus();
        void EnableFeeder();
        void DisableFeeder();
        void EnableCapBank();
        void DisableCapBank();
        void OpenCapBank();
        void CloseCapBank();
        void ConfirmOpen();
        void ConfirmClose();
        void doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG bankId);
        void SendAllData();
        void ReturnCapToOriginalFeeder();
        void ReturnFeederToOriginalSubBus();
        void ResetDailyOperations();
        void ConfirmFeeder();
        void ResetAllSystemOpCounts();
        void DeleteItem();
        void ConfirmSub();
        void ConfirmArea();
        void EnableArea();
        void DisableArea();
        void EnableSystem();
        void DisableSystem();
        void Scan2WayDevice();
        void Flip7010Device();
        void SendSystemStatus();
        void SendAllCapBankCommands();
        void ControlAllCapBanksByFeeder(LONG feederId, int control, CtiMultiMsg_vec& pilMessages,
                                       CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
        void SendTimeSync();
        void changeBankOperationalState();
        void AutoEnableOvUv();
        void AutoDisableOvUv();
        void AutoEnableOvUvByArea();
        void AutoDisableOvUvByArea();
        void AutoControlOvUvBySubstation(BOOL disableFlag);
        void AutoControlOvUvBySubBus(BOOL disableFlag);
        bool checkForCommandRefusal(CtiCCFeeder* feeder);
        void ControlAllCapBanks(LONG paoId, int control);
        void syncCbcAndCapBankStates();

        void queueCapBankTimeSyncPilMessages(CtiMultiMsg_vec& pilMessages, CapBankList capBanks);

        // Local Control Command Funtions
        void sendLocalControl();
        void enableOvUv(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void disableOvUv(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void enableTempControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void disableTempControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void enableVarControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void disableVarControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void enableTimeControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);
        void disableTimeControl(std::vector<CtiSignalMsg*>& signals, std::vector<CtiCCEventLogMsg*>& events, std::vector<CtiRequestMsg*>& requests);

        // Voltage Regulator Commands
        void sendVoltageRegulatorCommands   (const LONG command);
    public:
        // Voltage Regulator Commands
        // Public for unit tests. This must stand until the message sending can be/is refactored.
        // These should never be called outside the class
        void scanVoltageRegulatorIntegrity    (const LONG commandType, std::vector<CtiMessage*> &toDispatch, std::vector<CtiCCEventLogMsg*> &events, std::vector<CtiRequestMsg*> &requests);
        void sendVoltageRegulatorRemoteControl(const LONG commandType, std::vector<CtiMessage*> &toDispatch, std::vector<CtiCCEventLogMsg*> &events, std::vector<CtiRequestMsg*> &requests);
        void sendVoltageRegulatorTapPosition  (const LONG commandType, std::vector<CtiMessage*> &toDispatch, std::vector<CtiCCEventLogMsg*> &events, std::vector<CtiRequestMsg*> &requests);
        void sendVoltageRegulatorKeepAlive    (const LONG commandType, std::vector<CtiMessage*> &toDispatch, std::vector<CtiRequestMsg*> &requests);

    private:
        //Helper Functions
        void voltageRegulatorKeepAliveHelper(Cti::CapControl::VoltageRegulatorManager::SharedPtr regulator, const int keepAliveTime, std::vector<CtiMessage*> &toDispatch, std::vector<CtiRequestMsg*> &requests);
        void setParentOvUvFlags(int paoId, Cti::CapControl::CapControlType type, bool ovuvFlag, CtiMultiMsg_vec& modifiedSubBuses);
        void printOutEventLogsByIdAndType(int paoId, Cti::CapControl::CapControlType type, const string& actionText, const string& userName,
                                          CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);

        AttributeService* _attributeService;
        CtiCCCommand* _command;
};

class CtiCCCapBankMoveExecutor : public CtiCCExecutor
{
    public:
        CtiCCCapBankMoveExecutor(CtiCCCapBankMoveMsg* capMoveMsg) : _capMoveMsg(capMoveMsg) {};
        virtual ~CtiCCCapBankMoveExecutor() { delete _capMoveMsg;};

        virtual void execute();

    private:

        CtiCCCapBankMoveMsg* _capMoveMsg;
};


class CtiCCFeederMoveExecutor : public CtiCCExecutor
{
    public:
        CtiCCFeederMoveExecutor(CtiCCObjectMoveMsg* fdrMoveMsg) : _fdrMoveMsg(fdrMoveMsg) {};
        virtual ~CtiCCFeederMoveExecutor() { delete _fdrMoveMsg;};

        virtual void execute();

    private:

        CtiCCObjectMoveMsg* _fdrMoveMsg;
};

class CtiCCSubstationVerificationExecutor : public CtiCCExecutor
{
    public:
        CtiCCSubstationVerificationExecutor(CtiCCSubstationVerificationMsg* subVerificationMsg) : _subVerificationMsg(subVerificationMsg) {};
        virtual ~CtiCCSubstationVerificationExecutor() { delete _subVerificationMsg;};

        virtual void execute();

    private:

        void EnableSubstationBusVerification();
        void DisableSubstationBusVerification(bool forceStopImmediately = false);

        CtiCCSubstationVerificationMsg* _subVerificationMsg;
};

class CtiCCPointDataMsgExecutor : public CtiCCExecutor
{
    public:
        CtiCCPointDataMsgExecutor(CtiPointDataMsg* pointMsg) : _pointDataMsg(pointMsg) {};
        virtual ~CtiCCPointDataMsgExecutor() { delete _pointDataMsg;};

        virtual void execute();

    private:
        CtiPointDataMsg* _pointDataMsg;
};

class CtiCCForwardMsgToDispatchExecutor : public CtiCCExecutor
{
    public:
        CtiCCForwardMsgToDispatchExecutor(CtiMessage* ctiMsg) : _ctiMessage(ctiMsg) {};
        virtual ~CtiCCForwardMsgToDispatchExecutor() { delete _ctiMessage;};

        virtual void execute();

    private:
        CtiMessage* _ctiMessage;
};

class CtiCCMultiMsgExecutor : public CtiCCExecutor
{
    public:
        CtiCCMultiMsgExecutor(CtiMultiMsg* multiMsg) : _multiMsg(multiMsg) {};
        virtual ~CtiCCMultiMsgExecutor() { delete _multiMsg;};

        virtual void execute();

    private:
        CtiMultiMsg* _multiMsg;
};

class CtiCCShutdownExecutor : public CtiCCExecutor
{
    public:
        CtiCCShutdownExecutor() {};
        virtual ~CtiCCShutdownExecutor() {};

        virtual void execute();
};

class NoOpExecutor : public CtiCCExecutor
{
    public:
        NoOpExecutor() {};
        virtual ~NoOpExecutor() {};

        virtual void execute(){};
};

class CtiCCExecutorFactory
{
    public:
        static std::auto_ptr<CtiCCExecutor> createExecutor(const CtiMessage* message);
};
