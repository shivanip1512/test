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
