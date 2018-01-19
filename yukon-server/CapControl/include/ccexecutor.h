#pragma once


#include "ccmessage.h"

#include "msg_signal.h"
#include "msg_multi.h"
#include "AttributeService.h"
#include "ccutil.h"
#include "VoltageRegulatorManager.h"

class CtiCCExecutor
{
    public:
        virtual ~CtiCCExecutor() {};

        virtual void execute() {};

    protected:
        CtiCCExecutor() {};

        bool moveCapBank(int permanentFlag, long oldFeederId, long movedCapBankId, long newFeederId, float capSwitchingOrder, float closeOrder, float tripOrder);
        void moveFeeder(bool permanentFlag, long oldSubBusId, long movedFeederId, long newSubBusId, float fdrSwitchingOrder);
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

        CtiCCCommandExecutor(CapControlCommand* command)
        {
            init();

            _command = command;
            _itemId = 0;
        };

        CtiCCCommandExecutor(ItemCommand* command)
        {
            init();

            _command = command;
            _itemId = command->getItemId();
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

        void init()
        {
            _attributeService = new AttributeService();
        };

        //Command Functions
        void EnableSystem();
        void DisableSystem();
        void EnableArea();
        void DisableArea();
        void EnableSubstation();
        void DisableSubstation();
        void EnableSubstationBus(long subBusId);
        void DisableSubstationBus(long subBusId);
        void EnableFeeder();
        void DisableFeeder();
        void EnableCapBank();
        void DisableCapBank();

        void OpenCapBank(long bankId, bool confirmImmediately = false);
        void CloseCapBank(long bankId, bool confirmImmediately = false);
        void ConfirmOpen();
        void ConfirmClose();
        void doConfirmImmediately(CtiCCSubstationBus* currentSubstationBus, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, long controllerId);
        void SendAllData();
        void ReturnCapToOriginalFeeder();
        void ReturnFeederToOriginalSubBus();
        void ResetDailyOperations();
        void ConfirmFeeder();
        void ResetAllSystemOpCounts();
        void ConfirmSubstation();
        void ConfirmSubstationBus();
        void ConfirmArea();
        void Scan2WayDevice(long bankId);
        void Flip7010Device();
        void SendAllCapBankCommands();
        void SendTimeSync();

        void AutoEnableOvUv();
        void AutoDisableOvUv();
        void setAutoControlOvUvFlags(CtiCCSubstationBusPtr currentSubBus, bool disableFlag);
        bool checkForCommandRefusal(CtiCCFeeder* feeder);
        void syncCbcAndCapBankStates(long bankId);

        void queueCapBankTimeSyncPilMessages(CtiMultiMsg_vec& pilMessages, CapBankList capBanks);

        // Local Control Command Funtions
        void sendLocalControl();
        void enableOvUv        (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void disableOvUv       (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void enableTempControl (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void disableTempControl(long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void enableVarControl  (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void disableVarControl (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void enableTimeControl (long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);
        void disableTimeControl(long bankId, std::vector<CtiSignalMsg*>& signals, Cti::CapControl::EventLogEntries &events, std::vector<CtiRequestMsg*>& requests);

        void sendVoltageRegulatorCommands(const long command, const std::string & user);

    private:
        //Helper Functions
        void setParentOvUvFlags(int paoId, Cti::CapControl::CapControlType type, bool ovuvFlag, CtiCCSubstationBus_vec& modifiedSubBuses); //TODO CBM
        void printOutEventLogsByIdAndType(int paoId, Cti::CapControl::CapControlType type, const std::string& actionText, const std::string& userName,
                                          CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents);

        AttributeService* _attributeService;

        CapControlCommand* _command;
        int _itemId;
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


class DeleteItemExecutor : public CtiCCExecutor
{
    public:
        DeleteItemExecutor(DeleteItem* deleteMsg) : _deleteItem(deleteMsg) {};
        virtual ~DeleteItemExecutor() { delete _deleteItem;};

        virtual void execute();

    private:

        DeleteItem* _deleteItem;
};

class SystemStatusExecutor : public CtiCCExecutor
{
    public:
        SystemStatusExecutor(SystemStatus* systemStatus) : _systemStatus(systemStatus) {};
        virtual ~SystemStatusExecutor() { delete _systemStatus;};

        virtual void execute();

    private:

        SystemStatus* _systemStatus;
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

class TriggerDmvTestExecutor : public CtiCCExecutor
{
public:

    TriggerDmvTestExecutor( MsgTriggerDmvTest * msg)
        : _message( msg )
    {   }

    virtual ~TriggerDmvTestExecutor()
    { 
        delete _message;
    }

    virtual void execute();

private:

    MsgTriggerDmvTest * _message;
};

