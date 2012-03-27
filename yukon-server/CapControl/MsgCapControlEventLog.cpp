#include "precompiled.h"

#include "MsgCapControlEventLog.h"
#include "ccid.h"

#include "pointtypes.h"
using std::string;

RWDEFINE_COLLECTABLE( CtiCCEventLogMsg, CTICCEVENTLOG_ID )

CtiCCEventLogMsg::CtiCCEventLogMsg()
{

}

CtiCCEventLogMsg::CtiCCEventLogMsg(long logId, long pointId, long spAreaId, long areaId, long stationId, long subId, long feederId, long eventType, long seqId, long value,
                                       string text, string userName, double kvarBefore, double kvarAfter, double kvarChange,
                                       string ipAddress, long actionId, string stateInfo,
                                       double aVar, double bVar, double cVar,  int regulatorId) :
_logId(logId), _timeStamp(CtiTime()), _pointId(pointId), _spAreaId(spAreaId),_areaId(areaId),_stationId(stationId),_subId(subId),
_feederId(feederId), _eventType(eventType), _seqId(seqId), _value(value), _text(text), _userName(userName),
_kvarBefore(kvarBefore), _kvarAfter(kvarAfter), _kvarChange(kvarChange), _ipAddress(ipAddress),
_actionId(actionId), _stateInfo(stateInfo), _aVar(aVar), _bVar(bVar), _cVar(cVar), _regulatorId(regulatorId)
{

}

CtiCCEventLogMsg::CtiCCEventLogMsg(string text, int regulatorId, long eventType)
: _userName("cap control"), _text(text), _logId(0),
_pointId(SYS_PID_CAPCONTROL), _spAreaId(0),_areaId(0),_stationId(0),_subId(0),
_feederId(0), _eventType(eventType), _seqId(0), _value(0),
_kvarBefore(0), _kvarAfter(0), _kvarChange(0), _ipAddress("(N/A)"),
_actionId(0), _stateInfo("(N/A)"), _aVar(0), _bVar(0), _cVar(0), _regulatorId(regulatorId)
{

}

CtiCCEventLogMsg::~CtiCCEventLogMsg()
{

}

CtiCCEventLogMsg::CtiCCEventLogMsg (const CtiCCEventLogMsg& aRef)
{
    operator=(aRef);
}

long CtiCCEventLogMsg::getLogId() const
{
    return _logId;
}

void CtiCCEventLogMsg::setLogId(long logId)
{
    _logId = logId;
}

CtiTime CtiCCEventLogMsg::getTimeStamp() const
{
    return _timeStamp;
}

long CtiCCEventLogMsg::getPointId() const
{
    return _pointId;
}

long CtiCCEventLogMsg::getSubId() const
{
    return _subId;
}

long CtiCCEventLogMsg::getStationId() const
{
    return _stationId;
}

long CtiCCEventLogMsg::getAreaId() const
{
    return _areaId;
}

long CtiCCEventLogMsg::getSpecialAreaId() const
{
    return _spAreaId;
}

long CtiCCEventLogMsg::getFeederId() const
{
    return _feederId;
}

long CtiCCEventLogMsg::getEventType() const
{
    return _eventType;
}

long CtiCCEventLogMsg::getSeqId() const
{
    return _seqId;
}

long CtiCCEventLogMsg::getValue() const
{
    return _value;
}

string CtiCCEventLogMsg::getText() const
{
    return _text;
}

string CtiCCEventLogMsg::getUserName() const
{
    return _userName;
}

double CtiCCEventLogMsg::getKvarBefore() const
{
    return _kvarBefore;
}

double CtiCCEventLogMsg::getKvarAfter() const
{
    return _kvarAfter;
}

double CtiCCEventLogMsg::getKvarChange() const
{
    return _kvarChange;
}

string CtiCCEventLogMsg::getIpAddress() const
{
    return _ipAddress;
}

long CtiCCEventLogMsg::getActionId() const
{
    return _actionId;
}

void CtiCCEventLogMsg::setActionId(long actionId)
{
    _actionId = actionId;
}

string CtiCCEventLogMsg::getStateInfo() const
{
    return _stateInfo;
}

void CtiCCEventLogMsg::setStateInfo(string stateInfo)
{
    _stateInfo = stateInfo;
}

double CtiCCEventLogMsg::getAVar() const
{
    return _aVar;
}

void CtiCCEventLogMsg::setAVar(double val)
{
    _aVar = val;
}

double CtiCCEventLogMsg::getBVar() const
{
    return _bVar;
}

void CtiCCEventLogMsg::setBVar(double val)
{
    _bVar = val;
}

double CtiCCEventLogMsg::getCVar() const
{
    return _cVar;
}

void CtiCCEventLogMsg::setCVar(double val)
{
    _cVar = val;
}

void CtiCCEventLogMsg::setABCVar(double aVal, double bVal, double cVal)
{
    _aVar = aVal;
    _bVar = bVal;
    _cVar = cVal;
}

int CtiCCEventLogMsg::getRegulatorId() const
{
    return _regulatorId;
}

void CtiCCEventLogMsg::restoreGuts(RWvistream& iStream)
{
    Inherited::restoreGuts(iStream);

    iStream  >> _logId
             >> _timeStamp
             >> _pointId
             >> _subId
             >> _feederId
             >> _eventType
             >> _seqId
             >> _value
             >> _text
             >> _userName
             >> _kvarBefore
             >> _kvarAfter
             >> _kvarChange
             >> _ipAddress
             >> _actionId
             >> _aVar
             >> _bVar
             >> _cVar
             >> _stationId
             >> _areaId
             >> _spAreaId
             >> _regulatorId;

      return;
}

void CtiCCEventLogMsg::saveGuts(RWvostream& oStream) const
{
    Inherited::saveGuts(oStream);

    oStream  << _logId
             << _timeStamp
             << _pointId
             << _subId
             << _feederId
             << _eventType
             << _seqId
             << _value
             << _text
             << _userName
             << _kvarBefore
             << _kvarAfter
             << _kvarChange
             << _ipAddress
             << _actionId
             << _aVar
             << _bVar
             << _cVar
             << _stationId
             << _areaId
             << _spAreaId
             << _regulatorId;

    return;
}

CtiMessage* CtiCCEventLogMsg::replicateMessage() const
{
    return new CtiCCEventLogMsg(*this);
}

CtiCCEventLogMsg& CtiCCEventLogMsg::operator=(const CtiCCEventLogMsg& right)
{
    if( this != &right )
    {
        Inherited::operator=(right);
        _logId      = right._logId;
        _timeStamp  = right._timeStamp;
        _pointId    = right._pointId;
        _spAreaId      = right._spAreaId;
        _areaId      = right._areaId;
        _stationId      = right._stationId;
        _subId      = right._subId;
        _feederId   = right._feederId;
        _eventType  = right._eventType;
        _seqId      = right._seqId;
        _value      = right._value;
        _text       = right._text;
        _userName   = right._userName;
        _kvarBefore = right._kvarBefore;
        _kvarAfter  = right._kvarAfter;
        _kvarChange = right._kvarChange;
        _ipAddress  = right._ipAddress;
        _actionId   = right._actionId;
        _stateInfo  = right._stateInfo;

        _aVar = right._aVar;
        _bVar = right._bVar;
        _cVar = right._cVar;

        _regulatorId = right._regulatorId;

    }

    return *this;
}


