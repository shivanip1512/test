#pragma once

#include "MsgCapControlCommand.h"

#include "ctitime.h"

#include "message.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "ccsparea.h"
#include "ccstate.h"



#include "MsgItemCommand.h"

#include "EventTypes.h"


#include "MsgBankMove.h"
#include "MsgObjectMove.h"
#include "MsgSubstationBus.h"
#include "MsgCapControlEventLog.h"
#include "MsgCapBankStates.h"
#include "MsgAreas.h"
#include "MsgSpecialAreas.h"
#include "MsgSubstations.h"
#include "MsgVoltageRegulator.h"
#include "MsgDeleteItem.h"
#include "MsgSystemStatus.h"
#include "MsgCapControlServerResponse.h"

namespace Cti
{
    namespace CapControl
    {
        class VoltageRegulator;
    }
}

class CtiCCSubstationVerificationMsg : public CtiMessage
{
    RWDECLARE_COLLECTABLE( CtiCCSubstationVerificationMsg )

    public:
        typedef CtiMessage Inherited;

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



class CtiPAOScheduleMsg : public CtiMessage
{
RWDECLARE_COLLECTABLE( CtiPAOScheduleMsg )

public:
    typedef CtiMessage Inherited;

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


class CtiCCShutdown : public CtiMessage
{
    RWDECLARE_COLLECTABLE( CtiCCShutdown )

    public:
        typedef CtiMessage Inherited;

        CtiCCShutdown() : Inherited() { } ;

        void restoreGuts( RWvistream& );
        void saveGuts( RWvostream&) const;
};
