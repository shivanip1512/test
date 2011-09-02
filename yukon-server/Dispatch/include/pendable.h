#pragma once

#include "pending_info.h"

class CtiPendable
{
public:

    enum {
        CtiPendableAction_Unspecified = 0,
        CtiPendableAction_Add,                      // Requires CtiPendingPointOperations
        CtiPendableAction_RemoveLimit,              // Requires _pointId and _action.
        CtiPendableAction_RemovePointData,          // Requires _pointId and _action.
        CtiPendableAction_ControlStatusComplete,    // Requires _pointId, _action, _value, and _tags.
        CtiPendableAction_ControlStatusChanged      // Requires _pointId, _action, _value, and _tags.

    };

    LONG _pointID;
    INT _action;                        // This is add, remove,...
    CtiPendingPointOperations *_ppo;
    CtiTime _time;

    INT _limit;                         // Used only if the action is CtiPendableAction_RemoveLimit.
    DOUBLE _value;                      // Point value.
    UINT _tags;


public:

    CtiPendable(LONG pid, int action, CtiPendingPointOperations *ppo = 0, CtiTime ppotm = CtiTime()) :
        _pointID(pid),
        _action(action),
        _ppo(ppo),
        _time(ppotm),
        _value(0.0),
        _tags(0)
    {}

    CtiPendable(const CtiPendable& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiPendable()
    {
        if(_ppo)
        {
            delete _ppo;
        }
    }

    CtiPendable& operator=(const CtiPendable& aRef)
    {
        if(this != &aRef)
        {
            _pointID = aRef._pointID;
            _action = aRef._action;
            _time = aRef._time;

            _limit = aRef._limit;
            _value = aRef._value;
            _tags = aRef._tags;


            if(_ppo)
            {
                delete _ppo;
            }

            if(aRef._ppo)
            {
                _ppo = CTIDBG_new CtiPendingPointOperations(*aRef._ppo);
            }
        }
        return *this;
    }

    bool operator<(const CtiPendable &rhs) const
    {
        return _time < rhs._time;
    }

    INT getAction() const { return _action; }
};
