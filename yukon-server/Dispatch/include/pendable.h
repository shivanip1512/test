#pragma once

#include "pending_info.h"

class CtiPendable final
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

    const long _pointID;
    const int _action;                        // This is add, remove,...
    const std::unique_ptr<CtiPendingPointOperations> _ppo;
    const CtiTime _time;

    int _limit;                         // Used only if the action is CtiPendableAction_RemoveLimit.
    double _value;                      // Point value.
    unsigned _tags;

    CtiPendable(LONG pid, int action, std::unique_ptr<CtiPendingPointOperations>&& ppo = nullptr, CtiTime ppotm = CtiTime()) :
        _pointID(pid),
        _action(action),
        _ppo(std::move(ppo)),
        _time(ppotm),
        _value(0.0),
        _tags(0)
    {}

    CtiPendable(const CtiPendable& aRef) = delete;
    CtiPendable& operator=(const CtiPendable& aRef) = delete;

    bool operator<(const CtiPendable &rhs) const
    {
        return _time < rhs._time;
    }

    int getAction() const { return _action; }
};
