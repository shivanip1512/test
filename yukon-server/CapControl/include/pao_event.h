#pragma once

namespace Cti
{
class RowReader;
}
                       
                       
class CtiPAOEvent
{
public:

    CtiPAOEvent();
    CtiPAOEvent(Cti::RowReader& rdr);

    virtual ~CtiPAOEvent();

    long getEventId()const;
    long getScheduleId()const;
    long getPAOId()const;
    const std::string& getEventCommand() const;
    bool getDisableOvUvFlag() const;

private:
    
    long         _eventId;
    long         _scheduleId;
    long         _paoId;
    std::string  _eventCommand;
    bool         _disableOvUvFlag;
};

inline bool operator==( const CtiPAOEvent & lhs, const CtiPAOEvent & rhs )
{
    return lhs.getEventId() == rhs.getEventId();
}

inline bool operator!=( const CtiPAOEvent & lhs, const CtiPAOEvent & rhs )
{
    return ! ( lhs == rhs );
}

