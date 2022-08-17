#include "precompiled.h"

#include "collectable.h"
#include "logger.h"
#include "msg_lmcontrolhistory.h"

using namespace std;

DEFINE_COLLECTABLE( CtiLMControlHistoryMsg, MSG_LMCONTROLHISTORY );

unsigned int CtiLMControlHistoryMsg::_instanceCount = 0;

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


std::string CtiLMControlHistoryMsg::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiLMControlHistoryMsg";
    itemList.add("PAO Id")            << getPAOId();
    itemList.add("Point Id")          << getPointId();
    itemList.add("Raw Control State") << getRawState();
    itemList.add("Start Date Time")   << getStartDateTime();
    itemList.add("Control Duration")  << getControlDuration();
    itemList.add("Reduction Ratio")   << getReductionRatio();
    itemList.add("Control Type")      << getControlType();
    itemList.add("Active Restore")    << getActiveRestore();
    itemList.add("Reduction Value")   << getReductionValue();
    itemList.add("Control Priority")  << getControlPriority();
    itemList.add("Association Key")   << getAssociationKey();

    return (Inherited::toString() += itemList.toString());
}


// Return a new'ed copy of this message!
CtiMessage* CtiLMControlHistoryMsg::replicateMessage() const
{
    CtiLMControlHistoryMsg *ret = CTIDBG_new CtiLMControlHistoryMsg(*this);

    return( (CtiMessage*)ret );
}


CtiLMControlHistoryMsg::CtiLMControlHistoryMsg(long paoid, long pointid, int raw, CtiTime start,
                                               int dur, int redrat, int ctrlPriority) :
Inherited(7), _paoId(paoid), _pointId(pointid), _rawState(raw), _startDateTime(start),
_controlDuration(dur), _reductionRatio(redrat), _controlType("N/A"), _activeRestore("N"),
_reductionValue(0.0), _controlPriority(ctrlPriority), _associationKey(0)
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
