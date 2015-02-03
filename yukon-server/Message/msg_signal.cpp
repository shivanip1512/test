#include "precompiled.h"

#include "collectable.h"
#include "logger.h"
#include "msg_signal.h"
#include "msg_pdata.h"
#include "utility.h"

using namespace std;

DEFINE_COLLECTABLE( CtiSignalMsg, MSG_SIGNAL );

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
        CTILOG_WARN(dout, "millis = " << millis << " > 999 for ID " << getId());
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

std::string CtiSignalMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiSignalMsg";
    itemList.add("Id")                   << getId();
    itemList.add("Log Type")             << getLogType();
    itemList.add("Condition")            << getCondition();
    itemList.add("Signal Group")         << getSignalCategory();
    itemList.add("Text")                 << getText();
    itemList.add("Additional Info Text") << getAdditionalInfo();
    itemList.add("Milliseconds")         << getSignalMillis();
    itemList.add("Log ID (if inserted)") << _logid;
    itemList.add("Tags")                 << explainTags(getTags()) <<" ("<< CtiNumStr(getTags()).xhex().zpad(8) <<")";

    return (Inherited::toString() += itemList.toString());
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


