#pragma once

#include "dbmemobject.h"
                       
namespace Cti
{
class RowReader;
}
                       
                       
class CtiPAOEvent  : public CtiMemDBObject
{
public:

    CtiPAOEvent();
    CtiPAOEvent(long eventId, long schedId, long paoId, const std::string& command, bool disableOvUv);
    CtiPAOEvent(Cti::RowReader& rdr);
    virtual ~CtiPAOEvent();

    long getEventId()const;
    long getScheduleId()const;
    long getPAOId()const;
    const std::string& getEventCommand() const;
    bool getDisableOvUvFlag() const;

    void setEventId(long eventId);
    void setScheduleId(long schedId);
    void setPAOId(long paoId);
    void setEventCommand(const std::string& eventCommand);
    void setDisableOvUvFlag(bool flag);

    CtiPAOEvent& operator=(const CtiPAOEvent& right);
    int operator==(const CtiPAOEvent& right) const;
    int operator!=(const CtiPAOEvent& right) const;

    bool isDirty();
    void setDirty(bool flag);

private:
    
    bool _dirty;

    long         _eventId;
    long         _scheduleId;
    long         _paoId;
    std::string  _eventCommand;
    bool         _disableOvUvFlag;
    
};
