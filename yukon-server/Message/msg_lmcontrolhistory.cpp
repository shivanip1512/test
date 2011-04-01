/*-----------------------------------------------------------------------------*
*
* File:   msg_lmcontrolhistory
*
* Date:   9/24/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MESSAGE/msg_lmcontrolhistory.cpp-arc  $
* REVISION     :  $Revision: 1.11.6.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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
        getReductionValue() <<
        getControlPriority() <<
        getAssociationKey();
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
        _reductionValue >>
        _controlPriority >>
        _associationKey;
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

int CtiLMControlHistoryMsg::getControlPriority() const
{
    return _controlPriority;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setControlPriority( const int priority )
{
    _controlPriority = priority;
    return *this;
}

int CtiLMControlHistoryMsg::getAssociationKey() const
{
    return _associationKey;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setAssociationKey(const int key)
{
    _associationKey = key;
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

const CtiTime& CtiLMControlHistoryMsg::getStartDateTime() const
{
    return _startDateTime;
}
CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setStartDateTime(const CtiTime& aTime)
{
    _startDateTime = aTime;
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

const string& CtiLMControlHistoryMsg::getControlType() const
{
    return _controlType;
}

CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setControlType(const string& aString)
{
    _controlType = aString;
    return *this;
}

const string& CtiLMControlHistoryMsg::getActiveRestore() const
{
    return _activeRestore;
}
CtiLMControlHistoryMsg& CtiLMControlHistoryMsg::setActiveRestore(const string& aString)
{
    _activeRestore = aString;
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
    dout << " Control Priority              " << getControlPriority() << endl;
    dout << " Association Key               " << getAssociationKey() << endl;
}


// Return a new'ed copy of this message!
CtiMessage* CtiLMControlHistoryMsg::replicateMessage() const
{
    CtiLMControlHistoryMsg *ret = CTIDBG_new CtiLMControlHistoryMsg(*this);

    return( (CtiMessage*)ret );
}


CtiLMControlHistoryMsg::CtiLMControlHistoryMsg(long paoid, long pointid, int raw, CtiTime start,
                                               int dur, int redrat, int ctrlPriority, string type, string restore,
                                               double reduce, int pri, int associationKey) :
Inherited(pri), _paoId(paoid), _pointId(pointid), _rawState(raw), _startDateTime(start),
_controlDuration(dur), _reductionRatio(redrat), _controlType(type), _activeRestore(restore),
_reductionValue(reduce), _controlPriority(ctrlPriority), _associationKey(associationKey)
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
        _controlPriority    = aRef.getControlPriority();
        _associationKey     = aRef.getAssociationKey();
    }
    return *this;
}

bool CtiLMControlHistoryMsg::isValid()
{
    return _startDateTime.isValid();
}
