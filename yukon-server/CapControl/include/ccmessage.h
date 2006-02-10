/*-----------------------------------------------------------------------------
    Filename:  ccmessage.h
    
    Programmer:  Josh Wolberg
    
    Description:    Header file for message classes.

    Initial Date:  8/30/2001
    
    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 2001
-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
   
#ifndef CCMESSAGE_H
#define CCMESSAGE_H

#include <rw/collstr.h>
#include <rw/collect.h>
#include <rw/vstream.h>
#include "ctitime.h"

#include "message.h"
#include "ccsubstationbus.h"
#include "ccstate.h"
#include "rwutil.h"

typedef std::vector<CtiCCSubstationBus*> CtiCCSubstationBus_vec;
typedef std::vector<RWCollectableString*> CtiCCGeoArea_vec;
typedef std::vector<CtiCCState*> CtiCCState_vec;
enum CtiCCEventType_t
{
    capBankStateUpdate = 0,
    capControlCommandSent,
    capControlManualCommand,
    capControlPointOutsideOperatingLimits,
    capControlSetOperationCount,
    capControlEnable,
    capControlDisable,
    capControlEnableVerification,
    capControlDisableVerification,
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

private:
    string _message;
};

class CtiCCCommand : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCCommand )

public:

    enum 
    {
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
        WAIVE_SUBSTATION_BUS,//13
        UNWAIVE_SUBSTATION_BUS,//14
        WAIVE_FEEDER,//15
        UNWAIVE_FEEDER,//16
        ENABLE_OVUV,//17 
        DISABLE_OVUV,//18
        DELETE_ITEM //19
       // DISABLE_SUBSTATION_BUS_VERIFICATION//20
    };

    CtiCCCommand(LONG command);
    CtiCCCommand(LONG command, LONG id);
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
    /*CtiCCCapBankMoveMsg(LONG command);
    CtiCCCapBankMoveMsg(LONG command, LONG id);
    CtiCCCapBankMoveMsg(const CtiCCCommand& commandMsg);*/
    
    virtual ~CtiCCCapBankMoveMsg();

    INT getPermanentFlag() const;
    LONG getOldFeederId() const;
    LONG getCapBankId() const;
    LONG getNewFeederId() const;
    LONG getCapSwitchingOrder() const;

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    CtiCCCapBankMoveMsg& operator=(const CtiCCCapBankMoveMsg& right);
private:
    CtiCCCapBankMoveMsg() { }; //provided for polymorphic persitence only
    
    INT _permanentflag;
    LONG _oldfeederid;
    LONG _capbankid;
    LONG _newfeederid;
    LONG _capswitchingorder;
};


class CtiCCSubstationVerificationMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCSubstationVerificationMsg )

public:

    enum
    {
        ENABLE_SUBSTATION_BUS_VERIFICATION, //0
        DISABLE_SUBSTATION_BUS_VERIFICATION //1
    };

    virtual ~CtiCCSubstationVerificationMsg();

    CtiCCSubstationVerificationMsg(LONG action, LONG id, LONG strategy) : _action(action), _id(id), _strategy(strategy), _cbInactivityTime(-1) { }; //provided for polymorphic persitence only
    CtiCCSubstationVerificationMsg(LONG action, LONG id, LONG strategy, LONG inactivityTime) : _action(action), _id(id), _strategy(strategy), _cbInactivityTime(inactivityTime) { };

    LONG getStrategy() const { return _strategy; };
    LONG getAction() const { return _action; };
    LONG getSubBusId() const { return _id; };
    LONG getInactivityTime() const {return _cbInactivityTime; };

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

};



class CtiCCEventLogMsg : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCEventLogMsg )

public:

    virtual ~CtiCCEventLogMsg();

    CtiCCEventLogMsg(LONG logId, LONG pointId, LONG subId, LONG feederId, LONG eventType, LONG seqId, LONG value, string text, string userName ) : 
        _logId(logId), _timeStamp(CtiTime()), _pointId(pointId), _subId(subId),
        _feederId(feederId), _eventType(eventType), _seqId(seqId), _value(value), _text(text), _userName(userName) { }; //provided for polymorphic persitence only

    LONG getLogId() const { return _logId; };
    CtiTime getTimeStamp() const { return _timeStamp; };
    LONG getPointId() const {return _pointId; };
    LONG getSubId() const { return _subId; };
    LONG getFeederId() const { return _feederId; };
    LONG getEventType() const { return _eventType; };
    LONG getSeqId() const { return _seqId; };
    LONG getValue() const { return _value; };
    string getText() const { return _text; };
    string getUserName() const { return _userName; };

    void setLogId(LONG id) { _logId = id; return;};

    void restoreGuts(RWvistream&);
    void saveGuts(RWvostream&) const;

    virtual CtiMessage* replicateMessage() const;

    CtiCCEventLogMsg& operator=(const CtiCCEventLogMsg& right);
private:

    CtiCCEventLogMsg() { }; //provided for polymorphic persitence only

    LONG _logId; 
    CtiTime _timeStamp; 
    LONG _pointId;
    LONG _subId;
    LONG _feederId;
    LONG _eventType;
    LONG _seqId;
    LONG _value;
    string _text;
    string _userName;
    
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
    CtiCCSubstationBusMsg(const CtiCCSubstationBusMsg& substationBusesMsg);

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
    CtiCCGeoAreasMsg(CtiCCGeoArea_vec& areaList);
    CtiCCGeoAreasMsg(const CtiCCGeoAreasMsg& ccGeoAreas);

    virtual ~CtiCCGeoAreasMsg();

    CtiCCGeoArea_vec* getCCGeoAreas() const     { return _ccGeoAreas; }

    virtual CtiMessage* replicateMessage() const;

    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;

    CtiCCGeoAreasMsg& operator=(const CtiCCGeoAreasMsg& right);
private:
    CtiCCGeoAreasMsg() : CtiCCMessage("CCGeoAreas"), _ccGeoAreas(NULL){};
    
    CtiCCGeoArea_vec* _ccGeoAreas;
};

class CtiCCShutdown : public CtiCCMessage
{
RWDECLARE_COLLECTABLE( CtiCCShutdown )

public:
    CtiCCShutdown() : CtiCCMessage("Shutdown") { } ;
    
    void restoreGuts( RWvistream& );
    void saveGuts( RWvostream&) const;
};

#endif

