#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   msg_lmcontrolhistory
*
* Date:   9/24/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_lmcontrolhistory.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_lmcontrolhistory.h"

RWDEFINE_COLLECTABLE( CtiLMControlHistoryMsg, MSG_LMCONTROLHISTORY );

unsigned int CtiLMControlHistoryMsg::_instanceCount = 0;

void CtiLMControlHistoryMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);
    aStream <<
        getPAOId() <<
        getPointId() <<
        getRawState() <<
        getStartDateTime() <<
        getControlDuration() <<
        getReductionRatio() <<
        getControlType() <<
        getActiveRestore() <<
        getReductionValue();
}

void CtiLMControlHistoryMsg::restoreGuts(RWvistream& aStream)
{
    Inherited::restoreGuts(aStream);

    aStream >>
        _paoId >>
        _pointId >>
        _rawState >>
        _startDateTime >>
        _controlDuration >>
        _reductionRatio >>
        _controlType >>
        _activeRestore >>
        _reductionValue;
}

long CtiLMControlHistoryMsg::getPAOId() const
{
    return _paoId;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setPAOId( const long a_id )
{
    _paoId = a_id;
    return *this;
}

long CtiLMControlHistoryMsg::getPointId() const
{
    return _pointId;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setPointId( const long a_id )
{
    _pointId = a_id;
    return *this;
}

int CtiLMControlHistoryMsg::getRawState() const
{
    return _rawState;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setRawState( const int rs )
{
    _rawState = rs;
    return *this;
}

const RWTime& CtiLMControlHistoryMsg::getStartDateTime() const
{
    return _startDateTime;
}
CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setStartDateTime(const RWTime& time)
{
    _startDateTime = time;
    return *this;
}

int CtiLMControlHistoryMsg::getControlDuration() const
{
    return _controlDuration;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setControlDuration( const int duration )
{
    _controlDuration = duration;
    return *this;
}

int CtiLMControlHistoryMsg::getReductionRatio() const
{
    return _reductionRatio;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setReductionRatio( const int redrat )
{
    _reductionRatio = redrat;
    return *this;
}

const RWCString& CtiLMControlHistoryMsg::getControlType() const
{
    return _controlType;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setControlType(const RWCString& string)
{
    _controlType = string;
    return *this;
}

const RWCString& CtiLMControlHistoryMsg::getActiveRestore() const
{
    return _activeRestore;
}
CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setActiveRestore(const RWCString& string)
{
    _activeRestore = string;
    return *this;
}

double CtiLMControlHistoryMsg::getReductionValue() const
{
    return _reductionValue;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setReductionValue( const double value )
{
    _reductionValue = value;
    return *this;
}


void CtiLMControlHistoryMsg::dump() const
{
    Inherited::dump();

    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << " PAO Id                        " << getPAOId() << endl;
    dout << " Point Id                      " << getPointId() << endl;
    dout << " Raw Control State             " << getRawState() << endl;
    dout << " Start Date Time               " << getStartDateTime() << endl;
    dout << " Control Duration              " << getControlDuration() << endl;
    dout << " Reduction Ratio               " << getReductionRatio() << endl;
    dout << " Control Type                  " << getControlType() << endl;
    dout << " Active Restore                " << getActiveRestore() << endl;
    dout << " Reduction Value               " << getReductionValue() << endl;
}


// Return a new'ed copy of this message!
CtiMessage* CtiLMControlHistoryMsg::replicateMessage() const
{
    CtiLMControlHistoryMsg *ret = CTIDBG_new CtiLMControlHistoryMsg(*this);

    return( (CtiMessage*)ret );
}


CtiLMControlHistoryMsg::CtiLMControlHistoryMsg(long paoid, long pointid, int raw, RWTime start,
                                               int dur, int redrat,  RWCString type, RWCString restore,
                                               double reduce, int pri) :
Inherited(pri), _paoId(paoid), _pointId(pointid), _rawState(raw), _startDateTime(start),
_controlDuration(dur), _reductionRatio(redrat), _controlType(type), _activeRestore(restore),
_reductionValue(reduce)
{
    _instanceCount++;
}

CtiLMControlHistoryMsg::CtiLMControlHistoryMsg(const CtiLMControlHistoryMsg& aRef)
{
    _instanceCount++;
    *this = aRef;
}

CtiLMControlHistoryMsg::~CtiLMControlHistoryMsg()
{
    _instanceCount--;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::operator=(const CtiLMControlHistoryMsg& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _paoId              = aRef.getPAOId();
        _pointId            = aRef.getPointId();
        _rawState           = aRef.getRawState();
        _startDateTime      = aRef.getStartDateTime();
        _controlDuration    = aRef.getControlDuration();
        _reductionRatio     = aRef.getReductionRatio();
        _controlType        = aRef.getControlType();
        _activeRestore      = aRef.getActiveRestore();
        _reductionValue     = aRef.getReductionValue();
    }
    return *this;
}

bool CtiLMControlHistoryMsg::isValid()
{
    return _startDateTime.isValid();
}
