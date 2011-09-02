#pragma once

#include "row_reader.h"

#include "ctibase.h"
#include "logger.h"
#include "pao_event.h"
#include "dbmemobject.h"
                       
class CtiPAOEvent  : public CtiMemDBObject
{
public:

    CtiPAOEvent();
    CtiPAOEvent(long eventId, long schedId, long paoId, const std::string& command, BOOL disableOvUv);
    CtiPAOEvent(Cti::RowReader& rdr);
    virtual ~CtiPAOEvent();

    long getEventId()const;
    long getScheduleId()const;
    long getPAOId()const;
    const std::string& getEventCommand() const;
    BOOL getDisableOvUvFlag() const;

    void setEventId(long eventId);
    void setScheduleId(long schedId);
    void setPAOId(long paoId);
    void setEventCommand(const std::string& eventCommand);
    void setDisableOvUvFlag(BOOL flag);

    CtiPAOEvent& operator=(const CtiPAOEvent& right);
    int operator==(const CtiPAOEvent& right) const;
    int operator!=(const CtiPAOEvent& right) const;

    BOOL isDirty();
    void setDirty(BOOL flag);

private:
    
    BOOL _dirty;

    long         _eventId;
    long         _scheduleId;
    long         _paoId;
    std::string  _eventCommand;
    BOOL         _disableOvUvFlag;
    
};
