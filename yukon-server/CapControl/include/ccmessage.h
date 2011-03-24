#pragma once
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <rw/collect.h>
#include <rw/vstream.h>
#include "ctitime.h"

#include "message.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "ccsparea.h"
#include "ccstate.h"
#include "VoltageRegulator.h"

typedef std::vector<CtiCCSubstationPtr> CtiCCSubstation_vec;
typedef std::vector<CtiCCAreaPtr> CtiCCArea_vec;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;
typedef std::vector<CtiCCStatePtr> CtiCCState_vec;

typedef std::set<CtiCCSubstationPtr> CtiCCSubstation_set;
typedef std::set<CtiCCAreaPtr> CtiCCArea_set;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::set<CtiCCSubstationBusPtr> CtiCCSubstationBus_set;

enum CtiCCEventType_t
{
    capBankStateUpdate = 0,  //operation confirmed
    capControlCommandSent = 1, //operation sent
    capControlManualCommand = 2,
    capControlPointOutsideOperatingLimits = 3,
    capControlSetOperationCount = 4,
    capControlEnable = 5,
    capControlDisable = 6,
    capControlEnableVerification = 7,
    capControlDisableVerification = 8,
    capControlVerificationCommandSent = 9,
    capControlSwitchOverUpdate = 10,
    capControlEnableOvUv = 11,
    capControlDisableOvUv = 12,
    capBankStateUpdateFail = 13,
    capControlCommandRetrySent = 14,
    capControlUnexpectedCBCStateReported = 15,
    capControlIvvcCommStatus = 16,
    capControlIvvcMissingPoint = 17,
    capControlIvvcRejectedPoint = 18,
    capControlIvvcTapOperation
};


class CtiCCMessage : public CtiMessage
{
//RWDECLARE_COLLECTABLE( CtiCCMessage )

public:
    CtiCCMessage() { };
    CtiCCMessage(const string& message);

    virtual ~CtiCCMessage() { };

    const string& getMessage() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCMessage& operator=(const CtiCCMessage& right);

private:
    string _message;
};

class CtiCCCommand : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCommand )

public:

    enum
    {
        UNDEFINED = -1,
        ENABLE_SUBSTATION_BUS = 0,
        DISABLE_SUBSTATION_BUS,//1
        ENABLE_FEEDER,//2
        DISABLE_FEEDER,//3
        ENABLE_CAPBANK,//4
        DISABLE_CAPBANK,//5
        OPEN_CAPBANK,//6
        CLOSE_CAPBANK,//7
        CONFIRM_OPEN,//8
        CONFIRM_CLOSE,//9
        REQUEST_ALL_DATA,//10
        RETURN_CAP_TO_ORIGINAL_FEEDER,//11
        RESET_DAILY_OPERATIONS,//12
        //WAIVE_SUBSTATION_BUS, //13  REMOVED
        //UNWAIVE_SUBSTATION_BUS,//14   REMOVED
        CONFIRM_FEEDER = 15,//15
        RESET_SYSTEM_OP_COUNTS=16,//16
        ENABLE_OVUV,//17
        DISABLE_OVUV,//18
        DELETE_ITEM, //19
        CONFIRM_SUB, //20
        CONFIRM_AREA, //21
        ENABLE_AREA,  //22
        DISABLE_AREA,  //23
        SCAN_2WAY_DEVICE,  //24
        ENABLE_SYSTEM,  //25
        DISABLE_SYSTEM,  //26
        FLIP_7010_CAPBANK,  //27
        SYSTEM_STATUS, //28
        SEND_ALL_OPEN, //29
        SEND_ALL_CLOSE, //30
        SEND_ALL_ENABLE_OVUV, //31
        SEND_ALL_DISABLE_OVUV, //32
        SEND_ALL_SCAN_2WAY_DEVICE, //33
        SEND_TIME_SYNC, //34
        CHANGE_OPERATIONALSTATE,  //35
        AUTO_ENABLE_OVUV, //36
        AUTO_DISABLE_OVUV, //37

        RETURN_FEEDER_TO_ORIGINAL_SUBBUS,//38
        SEND_ALL_ENABLE_TEMPCONTROL = 40,
        SEND_ALL_DISABLE_TEMPCONTROL,
        SEND_ALL_ENABLE_VARCONTROL,
        SEND_ALL_DISABLE_VARCONTROL,
        SEND_ALL_ENABLE_TIMECONTROL,
        SEND_ALL_DISABLE_TIMECONTROL,//45
        BANK_ENABLE_TEMPCONTROL,
        BANK_DISABLE_TEMPCONTROL,
        BANK_ENABLE_VARCONTROL,
        BANK_DISABLE_VARCONTROL,
        BANK_ENABLE_TIMECONTROL,//50
        BANK_DISABLE_TIMECONTROL,
        SYNC_CBC_CAPBANK_STATE, //52
        SEND_ALL_SYNC_CBC_CAPBANK_STATE, //53

        VOLTAGE_REGULATOR_INTEGRITY_SCAN = 70,
        VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE,
        VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE,
        VOLTAGE_REGULATOR_TAP_POSITION_RAISE,
        VOLTAGE_REGULATOR_TAP_POSITION_LOWER,
        VOLTAGE_REGULATOR_KEEP_ALIVE,
    };

    CtiCCCommand(LONG commandId);
    CtiCCCommand(LONG commandId, LONG targetId);
    CtiCCCommand(const CtiCCCommand& commandMsg);

    virtual ~CtiCCCommand();

    LONG getCommand() const;
    LONG getId() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCCommand& operator=(const CtiCCCommand& right);

    virtual CtiMessage* replicateMessage() const;
private:

    CtiCCCommand() { }; //provided for polymorphic persitence only
    LONG _command;
    LONG _id;
};

class CtiCCCapBankMoveMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCapBankMoveMsg )

public:

    virtual ~CtiCCCapBankMoveMsg();

    INT getPermanentFlag() const;
    LONG getOldFeederId() const;
    LONG getCapBankId() const;
    LONG getNewFeederId() const;
    float getCapSwitchingOrder() const;
    float getCloseOrder() const;
    float getTripOrder() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCCapBankMoveMsg& operator=(const CtiCCCapBankMoveMsg& right);
private:
    CtiCCCapBankMoveMsg() { }; //provided for polymorphic persitence only

    INT _permanentflag;
    LONG _oldfeederid;
    LONG _capbankid;
    LONG _newfeederid;
    float _capswitchingorder;
    float _closeOrder;
    float _tripOrder;
};

class CtiCCObjectMoveMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCObjectMoveMsg )

public:
    CtiCCObjectMoveMsg(BOOL permanentflag, LONG oldparentid, LONG objectid, LONG newparentid,
                       float switchingorder, float closeOrder = 0, float tripOrder = 0);
    virtual ~CtiCCObjectMoveMsg();

    BOOL getPermanentFlag() const;
    LONG getOldParentId() const;
    LONG getObjectId() const;
    LONG getNewParentId() const;
    float getSwitchingOrder() const;
    float getCloseOrder() const;
    float getTripOrder() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCObjectMoveMsg& operator=(const CtiCCObjectMoveMsg& right);
private:
    CtiCCObjectMoveMsg() { }; //provided for polymorphic persitence only

    BOOL _permanentflag;
    LONG _oldparentid;
    LONG _objectid;
    LONG _newparentid;
    float _switchingorder;
    float _closeOrder;
    float _tripOrder;
};

class CtiCCSubstationVerificationMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSubstationVerificationMsg )

public:

    enum
    {
        ENABLE_SUBSTATION_BUS_VERIFICATION, //0
        DISABLE_SUBSTATION_BUS_VERIFICATION, //1
        FORCE_DISABLE_SUBSTATION_BUS_VERIFICATION //2
    };

    virtual ~CtiCCSubstationVerificationMsg();

    CtiCCSubstationVerificationMsg(LONG action, LONG id, LONG strategy) : _action(action), _id(id), _strategy(strategy), _cbInactivityTime(-1), _disableOvUvFlag(FALSE) { }; //provided for polymorphic persitence only
    CtiCCSubstationVerificationMsg(LONG action, LONG id, LONG strategy, LONG inactivityTime, BOOL flag) : _action(action), _id(id), _strategy(strategy), _cbInactivityTime(inactivityTime), _disableOvUvFlag(flag) { };

    LONG getStrategy() const { return _strategy; };
    LONG getAction() const { return _action; };
    LONG getSubBusId() const { return _id; };
    LONG getInactivityTime() const {return _cbInactivityTime; };
    BOOL getDisableOvUvFlag() const {return _disableOvUvFlag;};

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    virtual CtiMessage* replicateMessage() const;

    CtiCCSubstationVerificationMsg& operator=(const CtiCCSubstationVerificationMsg& right);
private:

    CtiCCSubstationVerificationMsg() { }; //provided for polymorphic persitence only

    LONG _action; //enable or disable...
    LONG _id; //subBusID
    LONG _strategy;
    LONG _cbInactivityTime;
    BOOL _disableOvUvFlag;

};



class CtiCCEventLogMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCEventLogMsg )

public:

    virtual ~CtiCCEventLogMsg();

    CtiCCEventLogMsg(LONG logId, LONG pointId, LONG spAreaId, LONG areaId, LONG stationId, LONG subId, LONG feederId, LONG eventType, LONG seqId, LONG value,
                     string text, string userName, DOUBLE kvarBefore= 0, DOUBLE kvarAfter = 0, DOUBLE kvarChange = 0,
                     string ipAddress = string("(N/A)"), LONG actionId = -1, string stateInfo = string("(N/A)"),
                     DOUBLE aVar = 0, DOUBLE bVar = 0, DOUBLE cVar = 0,  int regulatorId = 0) :
        _logId(logId), _timeStamp(CtiTime()), _pointId(pointId), _spAreaId(spAreaId),_areaId(areaId),_stationId(stationId),_subId(subId),
        _feederId(feederId), _eventType(eventType), _seqId(seqId), _value(value), _text(text), _userName(userName),
        _kvarBefore(kvarBefore), _kvarAfter(kvarAfter), _kvarChange(kvarChange), _ipAddress(ipAddress),
        _actionId(actionId), _stateInfo(stateInfo), _aVar(aVar), _bVar(bVar), _cVar(cVar), _regulatorId(regulatorId) { }; //provided for polymorphic persitence only

    CtiCCEventLogMsg(string text, int regulatorId = 0) : _userName("cap control"), _text(text), _logId(0),
        _pointId(SYS_PID_CAPCONTROL), _spAreaId(0),_areaId(0),_stationId(0),_subId(0),
        _feederId(0), _eventType(capControlIvvcTapOperation), _seqId(0), _value(0),
        _kvarBefore(0), _kvarAfter(0), _kvarChange(0), _ipAddress("(N/A)"),
        _actionId(0), _stateInfo("(N/A)"), _aVar(0), _bVar(0), _cVar(0), _regulatorId(regulatorId) { };

    CtiCCEventLogMsg (const CtiCCEventLogMsg& aRef);

    LONG getLogId() const { return _logId; };
    CtiTime getTimeStamp() const { return _timeStamp; };
    LONG getPointId() const {return _pointId; };
    LONG getSubId() const { return _subId; };
    LONG getStationId() const { return _stationId; };
    LONG getAreaId() const { return _areaId; };
    LONG getSpecialAreaId() const { return _spAreaId; };
    LONG getFeederId() const { return _feederId; };
    LONG getEventType() const { return _eventType; };
    LONG getSeqId() const { return _seqId; };
    LONG getValue() const { return _value; };
    string getText() const { return _text; };
    string getUserName() const { return _userName; };
    DOUBLE getKvarBefore() const { return _kvarBefore; };
    DOUBLE getKvarAfter() const { return _kvarAfter; };
    DOUBLE getKvarChange() const { return _kvarChange; };
    string getIpAddress() const { return _ipAddress; };
    LONG getActionId() const { return _actionId; };
    string getStateInfo() const { return _stateInfo; };
    DOUBLE getAVar() const { return _aVar; };
    DOUBLE getBVar() const { return _bVar; };
    DOUBLE getCVar() const { return _cVar; };
    int getRegulatorId() const { return _regulatorId; };

    void setLogId(LONG id) { _logId = id; return;};
    void setActionId(LONG id) { _actionId = id; return;};
    void setStateInfo(string stateInfo) { _stateInfo = stateInfo; return;};

    void setAVar(DOUBLE val) { _aVar = val; return;};
    void setBVar(DOUBLE val) { _bVar = val; return;};
    void setCVar(DOUBLE val) { _cVar = val; return;};
    void setABCVar(DOUBLE aVal, DOUBLE bVal, DOUBLE cVal) { _aVar = aVal; _bVar = bVal; _cVar = cVal; return;};

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    virtual CtiMessage* replicateMessage() const;

    CtiCCEventLogMsg& operator=(const CtiCCEventLogMsg& right);
private:

    CtiCCEventLogMsg() { }; //provided for polymorphic persitence only

    LONG _logId;
    CtiTime _timeStamp;
    LONG _pointId;
    LONG _spAreaId;
    LONG _areaId;
    LONG _stationId;
    LONG _subId;
    LONG _feederId;
    LONG _eventType;
    LONG _seqId;
    LONG _value;
    string _text;
    string _userName;
    DOUBLE _kvarBefore;
    DOUBLE _kvarAfter;
    DOUBLE _kvarChange;
    string _ipAddress;
    LONG _actionId;
    string _stateInfo;
    int _regulatorId;

    DOUBLE _aVar;
    DOUBLE _bVar;
    DOUBLE _cVar;

};


class CtiPAOScheduleMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiPAOScheduleMsg )

public:

    enum
    {
        ADD_SCHEDULE, //0
        UPDATE_SCHEDULE, //1
        DELETE_SCHEDULE //2
    };

    virtual ~CtiPAOScheduleMsg();

    CtiPAOScheduleMsg(LONG action, LONG id, const CtiTime& nextRunTime, LONG intervalRate) : _action(action), _scheduleId(id), _nextRunTime(nextRunTime), _intervalRate(intervalRate) { }; //provided for polymorphic persitence only


    LONG getAction() const { return _action; };
    LONG getScheduleId() const { return _scheduleId; };


    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiPAOScheduleMsg& operator=(const CtiPAOScheduleMsg& right);
private:

    CtiPAOScheduleMsg() { }; //provided for polymorphic persitence only

    LONG          _action;
    LONG          _scheduleId; //scheduleId...
    CtiTime  _nextRunTime;
    CtiTime  _lastRunTime;
    LONG          _intervalRate;

};



class CtiCCSubstationBusMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSubstationBusMsg )

public:
    CtiCCSubstationBusMsg(CtiCCSubstationBus_vec& buses, ULONG bitMask = 0);
    CtiCCSubstationBusMsg(CtiCCSubstationBus_set& buses, ULONG bitMask = 0);
    CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusesMsg);
    CtiCCSubstationBusMsg(CtiCCSubstationBus* substationBus);

    virtual ~CtiCCSubstationBusMsg();

    ULONG getMsgInfoBitMask() const { return _msgInfoBitMask; };
    CtiCCSubstationBus_vec* getCCSubstationBuses() const     { return _ccSubstationBuses; }
    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCSubstationBusMsg& operator=(const CtiCCSubstationBusMsg& right);

    // Possible bit mask settings
    static ULONG AllSubBusesSent;
    static ULONG SubBusDeleted;
    static ULONG SubBusAdded;
    static ULONG SubBusModified;

private:
    CtiCCSubstationBusMsg() : CtiCCMessage("CCSubstationBuses"), _ccSubstationBuses(NULL), _msgInfoBitMask(0) {};

    ULONG _msgInfoBitMask;
    CtiCCSubstationBus_vec* _ccSubstationBuses;
};

class CtiCCCapBankStatesMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCapBankStatesMsg )

public:
    CtiCCCapBankStatesMsg(CtiCCState_vec& ccCapBankStates);
    CtiCCCapBankStatesMsg(const CtiCCCapBankStatesMsg& ccCapBankStatesMsg);

    virtual ~CtiCCCapBankStatesMsg();

    CtiCCState_vec* getCCCapBankStates() const     { return _ccCapBankStates; }

    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCCapBankStatesMsg& operator=(const CtiCCCapBankStatesMsg& right);
private:
    CtiCCCapBankStatesMsg() : CtiCCMessage("CCCapBankStates"), _ccCapBankStates(NULL){};

    CtiCCState_vec* _ccCapBankStates;
};

class CtiCCGeoAreasMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCGeoAreasMsg )

public:
    CtiCCGeoAreasMsg(CtiCCArea_vec& areaList, ULONG bitMask = 1);
    CtiCCGeoAreasMsg(CtiCCArea_set& areaList, ULONG bitMask = 1);
    CtiCCGeoAreasMsg(CtiCCArea* ccArea);
    CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreas);

    virtual ~CtiCCGeoAreasMsg();

    CtiCCArea_vec* getCCGeoAreas() const     { return _ccGeoAreas; }

    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCGeoAreasMsg& operator=(const CtiCCGeoAreasMsg& right);
    static ULONG AllAreasSent;
    static ULONG AreaDeleted;
    static ULONG AreaAdded;
    static ULONG AreaModified;
private:
    CtiCCGeoAreasMsg() : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL), _msgInfoBitMask(1) {};

    CtiCCArea_vec* _ccGeoAreas;
    ULONG _msgInfoBitMask;
};

class CtiCCSpecialAreasMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSpecialAreasMsg )

public:

    CtiCCSpecialAreasMsg(CtiCCSpArea_vec& areaList);
    CtiCCSpecialAreasMsg(CtiCCSpArea_set& areaList);
    CtiCCSpecialAreasMsg(CtiCCSpecial* ccArea);
    CtiCCSpecialAreasMsg(const CtiCCSpecialAreasMsg& ccSpecialAreas);

    virtual ~CtiCCSpecialAreasMsg();

    CtiCCSpArea_vec* getCCSpecialAreas() const     { return _ccSpecialAreas; }

    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCSpecialAreasMsg& operator=(const CtiCCSpecialAreasMsg& right);
private:
    CtiCCSpecialAreasMsg() : CtiCCMessage("CCSpecialAreas"), _ccSpecialAreas(NULL){};

    CtiCCSpArea_vec* _ccSpecialAreas;
};

class CtiCCSubstationsMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSubstationsMsg )

public:

    CtiCCSubstationsMsg(CtiCCSubstation_vec& substationList, ULONG bitMask = 0);
    CtiCCSubstationsMsg(CtiCCSubstation_set& substationList, ULONG bitMask = 0);
    CtiCCSubstationsMsg(CtiCCSubstation* ccSubstations);
    CtiCCSubstationsMsg(const CtiCCSubstationsMsg& ccSubstations);

    virtual ~CtiCCSubstationsMsg();
    ULONG getMsgInfoBitMask() const { return _msgInfoBitMask; };

    CtiCCSubstation_vec* getCCSubstations() const     { return _ccSubstations; }

    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCSubstationsMsg& operator=(const CtiCCSubstationsMsg& right);

    // Possible bit mask settings
    static ULONG AllSubsSent;
    static ULONG SubDeleted;
    static ULONG SubAdded;
    static ULONG SubModified;


private:
    CtiCCSubstationsMsg() : CtiCCMessage("CCSubstations"), _ccSubstations(NULL), _msgInfoBitMask(0){};


    ULONG _msgInfoBitMask;
    CtiCCSubstation_vec* _ccSubstations;
};



class CtiCCShutdown : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCShutdown )

public:
    CtiCCShutdown() : CtiCCMessage("Shutdown") { } ;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;
};

class CtiCCServerResponse : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCServerResponse )

public:

    enum
    {
        UNDEFINED = -1,
        COMMAND_REFUSED = 1
    };

    CtiCCServerResponse(long responseType, string response);
    CtiCCServerResponse(const CtiCCServerResponse& commandMsg);

    virtual ~CtiCCServerResponse();

    string getResponse() const;
    long getResponseType() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCServerResponse& operator=(const CtiCCServerResponse& right);

    virtual CtiMessage* replicateMessage() const;
private:

    CtiCCServerResponse() { }; //provided for polymorphic persitence only
    long responseType;
    string response;
};

class VoltageRegulatorMessage : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( VoltageRegulatorMessage )

public:
    VoltageRegulatorMessage();
    VoltageRegulatorMessage(const VoltageRegulatorMessage & toCopy);
    VoltageRegulatorMessage & operator=(const VoltageRegulatorMessage & rhs);

    virtual ~VoltageRegulatorMessage();

    virtual CtiMessage * replicateMessage() const;

    void saveGuts(RWvostream & ostrm) const;

    void insert(Cti::CapControl::VoltageRegulator * regulator);

private:

    void cleanup();

    std::vector<Cti::CapControl::VoltageRegulator *> regulators;
};

