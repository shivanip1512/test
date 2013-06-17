/*-----------------------------------------------------------------------------*
*
* File:   msg_signal
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.15.4.1 $
* DATE         :  $Date: 2008/11/13 17:23:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
#include <iomanip>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "collectable.h"
#include "logger.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "utility.h"

RWDEFINE_COLLECTABLE( CtiSignalMsg, MSG_SIGNAL );

unsigned int CtiSignalMsg::_instanceCount = 0;

CtiSignalMsg::CtiSignalMsg(long pid, int soe, string text, string addl, int lt, unsigned cls, string usr, unsigned tag, int pri, unsigned millis, double point_data) :
Inherited(pri),
_id(pid),
_logType(lt),
_signalCategory(cls),
_text(text),
_additional(addl),
_tags(tag),
_condition(-1),
_logid(0),
_signalMillis(millis),
_point_value(point_data)
{
    _instanceCount++;
    Inherited::setSOE(soe);
    Inherited::setUser(usr);
}

CtiSignalMsg::CtiSignalMsg(const CtiSignalMsg& aRef) :
_point_value(0.0)
{
    _instanceCount++;
    *this = aRef;
}

CtiSignalMsg::~CtiSignalMsg()
{
    _instanceCount--;
}

CtiSignalMsg& CtiSignalMsg::operator=(const CtiSignalMsg& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _id            = aRef.getId();
        _text          = aRef.getText();
        _signalCategory   = aRef.getSignalCategory();
        _tags          = aRef.getTags();
        _logType       = aRef.getLogType();
        _additional    = aRef.getAdditionalInfo();
        _signalMillis  = aRef.getSignalMillis();
        _condition     = aRef.getCondition();
        _logid         = aRef.getLogID();
        _user          = aRef.getUser();

        _point_value   = aRef.getPointValue();
    }
    return *this;
}

void CtiSignalMsg::saveGuts(RWvostream &aStream) const
{
    Inherited::saveGuts(aStream);

    aStream << getId() << getLogType() << getSignalCategory() << getText() << getAdditionalInfo() << getTags() << getCondition() << getSignalMillis() << getPointValue();
}

void CtiSignalMsg::restoreGuts(RWvistream& aStream)
{
    unsigned millis;

    Inherited::restoreGuts(aStream);

    aStream >> _id >> _logType >> _signalCategory >> _text >> _additional >> _tags >> _condition >> millis >> _point_value;

    setSignalMillis(millis);
}

long CtiSignalMsg::getId() const
{
    return _id;
}

CtiSignalMsg& CtiSignalMsg::setId( const long a_id )
{
    _id = a_id;
    return *this;
}

const string& CtiSignalMsg::getText() const
{
    return _text;
}
CtiSignalMsg& CtiSignalMsg::setText(const string& aString)
{
    _text = aString;
    return *this;
}

unsigned CtiSignalMsg::getSignalCategory() const
{
    return _signalCategory;
}
CtiSignalMsg& CtiSignalMsg::setSignalCategory(const unsigned id)
{
    _signalCategory = id;
    return *this;
}

unsigned CtiSignalMsg::getTags() const
{
    return _tags;
}
CtiSignalMsg& CtiSignalMsg::setTags(const unsigned s)
{
    _tags |= s;
    return *this;
}
CtiSignalMsg& CtiSignalMsg::resetTags(const unsigned s)
{
    _tags &= ~(s);
    return *this;
}

unsigned CtiSignalMsg::getSignalMillis() const
{
    return _signalMillis;
}

CtiSignalMsg& CtiSignalMsg::setSignalMillis(unsigned millis)
{
    _signalMillis = millis % 1000;

    if( millis > 999 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - setSignalMillis(), millis = " << millis << " > 999 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return *this;
}

double CtiSignalMsg::getPointValue() const
{
    return _point_value;
}

CtiSignalMsg& CtiSignalMsg::setPointValue(double pval)
{
    _point_value = pval;
    return *this;
}

void CtiSignalMsg::dump() const
{
    Inherited::dump();

    CtiLockGuard<CtiLogger> doubt_guard(dout);

    dout << " Id                            " << getId() << endl;
    dout << " Log Type                      " << getLogType() << endl;
    dout << " Condition                     " << getCondition() << endl;
    dout << " Signal Group                  " << getSignalCategory() << endl;
    dout << " Text                          " << getText() << endl;
    dout << " Additional Info Text          " << getAdditionalInfo() << endl;
    dout << " Milliseconds                  " << getSignalMillis() << endl;
    dout << " Log ID (if inserted)          " << _logid << endl;
    CHAR oldfill = dout.fill();
    dout.fill('0');
    dout << " Tags                          0x" << hex << setw(8) << getTags() << dec << "  " << explainTags(getTags()) << endl;
    dout.fill(oldfill);
}


// Return a new'ed copy of this message!
CtiMessage* CtiSignalMsg::replicateMessage() const
{
    CtiSignalMsg *ret = CTIDBG_new CtiSignalMsg(*this);

    return( (CtiMessage*)ret );
}


BOOL CtiSignalMsg::isAlarm() const
{
    return(_signalCategory > SignalEvent);  // it is indeed an alarm!
}

BOOL CtiSignalMsg::isEvent() const
{
    return(_signalCategory == SignalEvent);
}

int CtiSignalMsg::getLogType() const
{
    return _logType;
}
CtiSignalMsg& CtiSignalMsg::setLogType(const int lt)
{
    _logType = lt;
    return *this;
}

const string& CtiSignalMsg::getAdditionalInfo() const
{
    return _additional;
}

CtiSignalMsg& CtiSignalMsg::setAdditionalInfo(const string& aString)
{
    _additional = aString;
    return *this;
}

int CtiSignalMsg::getCondition() const
{
    return _condition;
}

CtiSignalMsg& CtiSignalMsg::setCondition(const int cnd)
{
    _condition = cnd;
    return *this;
}

unsigned CtiSignalMsg::getLogID() const
{
    return _logid;
}

CtiSignalMsg& CtiSignalMsg::setLogID(const unsigned lid)
{
    _logid = lid;
    return *this;
}


