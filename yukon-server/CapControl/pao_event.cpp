#include "precompiled.h"

#include "pao_event.h"
#include "row_reader.h"
#include "ccutil.h"

using Cti::CapControl::setVariableIfDifferent;
using std::string;

/*---------------------------------------------------------------------------
    Constructor
---------------------------------------------------------------------------*/
CtiPAOEvent::CtiPAOEvent() :
_dirty(false),
_eventId(0),
_scheduleId(0),
_paoId(0),
_disableOvUvFlag(false)
{
}


CtiPAOEvent::CtiPAOEvent(long eventId, long schedId, long paoId, const string& command, bool disableOvUv) :
_dirty(false)
{
    _eventId = eventId;
    _scheduleId = schedId;
    _paoId = paoId;
    _eventCommand = command;
    _disableOvUvFlag = disableOvUv;
}


CtiPAOEvent::CtiPAOEvent(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["eventid"] >> _eventId;
    rdr["scheduleid"] >> _scheduleId;
    rdr["paoid"] >> _paoId;
    rdr["command"] >> _eventCommand;
    rdr["disableovuv"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setDisableOvUvFlag(tempBoolString=="y");


    _dirty = false;
}
/*---------------------------------------------------------------------------
    Destructor

---------------------------------------------------------------------------*/
CtiPAOEvent::~CtiPAOEvent()
{

}
CtiPAOEvent& CtiPAOEvent::operator=(const CtiPAOEvent& right)
{
    _eventId      = right._eventId;
    _scheduleId   = right._scheduleId;
    _paoId        = right._paoId;
    _eventCommand = right._eventCommand;
    _disableOvUvFlag = right._disableOvUvFlag;

    _dirty = right._dirty;

    return *this;
}
int CtiPAOEvent::operator==(const CtiPAOEvent& right) const
{
    return _eventId == right._eventId;
}
int CtiPAOEvent::operator!=(const CtiPAOEvent& right) const
{
    return _eventId != right._eventId;
}

long CtiPAOEvent::getEventId() const
{
    return _eventId;
}

long CtiPAOEvent::getScheduleId()  const
{
    return _scheduleId;
}

long CtiPAOEvent::getPAOId() const
{
    return _paoId;
}

const string& CtiPAOEvent::getEventCommand() const
{
    return _eventCommand;
}

bool CtiPAOEvent::getDisableOvUvFlag() const
{
    return _disableOvUvFlag;
}

void CtiPAOEvent::setEventId(long eventId)
{
    _dirty |= setVariableIfDifferent(_eventId, eventId);
}
void CtiPAOEvent::setScheduleId(long schedId)
{
    _dirty |= setVariableIfDifferent(_scheduleId, schedId);
}
void CtiPAOEvent::setPAOId(long paoId)
{
    _dirty |= setVariableIfDifferent(_paoId, paoId);
}
void CtiPAOEvent::setEventCommand(const string& eventCommand)
{
    _dirty |= setVariableIfDifferent(_eventCommand, eventCommand);
}
void CtiPAOEvent::setDisableOvUvFlag(bool flag)
{
    _dirty |= setVariableIfDifferent(_disableOvUvFlag, flag);
}

bool CtiPAOEvent::isDirty()
{
    return _dirty;
}
void CtiPAOEvent::setDirty(bool flag)
{
    _dirty = flag;
}
