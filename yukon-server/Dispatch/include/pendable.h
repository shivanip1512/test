
/*-----------------------------------------------------------------------------*
*
* File:   pendable
*
* Class:  CtiPendable
* Date:   11/2/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/11/05 17:22:48 $
* HISTORY      :
* $Log: pendable.h,v $
* Revision 1.1  2004/11/05 17:22:48  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PENDABLE_H__
#define __PENDABLE_H__

#include "pending_info.h"

class CtiPendable
{
public:

    enum {
        CtiPendableAction_Unspecified = 0,
        CtiPendableAction_Add,                      // Requires CtiPendingPointOperations
        CtiPendableAction_Remove,
        CtiPendableAction_RemoveLimit,              // Requires _pointId and _action.
        CtiPendableAction_RemovePointData,          // Requires _pointId and _action.
        CtiPendableAction_ControlStatusComplete,    // Requires _pointId, _action, _value, and _tags.
        CtiPendableAction_ControlStatusChanged      // Requires _pointId, _action, _value, and _tags.

    };

    LONG _pointID;
    INT _action;                        // This is add, remove,...
    CtiPendingPointOperations *_ppo;
    RWTime _time;

    INT _limit;                         // Used only if the action is CtiPendableAction_RemoveLimit.
    DOUBLE _value;                      // Point value.
    UINT _tags;


public:

    CtiPendable(LONG pid, int action, CtiPendingPointOperations *ppo = 0, RWTime ppotm = RWTime()) :
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
#endif // #ifndef __PENDABLE_H__
