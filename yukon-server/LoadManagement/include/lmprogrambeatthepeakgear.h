#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"
#include "BeatThePeakAlertLevel.h"

class CtiLMProgramBeatThePeakGear : public SmartGearBase, public CtiLMProgramDirectGear
{
public:

    CtiLMProgramBeatThePeakGear(Cti::RowReader &rdr);

    virtual CtiLMProgramDirectGear * replicate() const;

    void restore(Cti::RowReader &rdr);

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);

    std::size_t getFixedSize() const override    { return sizeof( *this ); }

    Cti::BeatThePeak::AlertLevel getAlertLevel() const;

private:
    Cti::BeatThePeak::AlertLevel _alertLevel;
};

inline bool operator==( const CtiLMProgramBeatThePeakGear & lhs, const CtiLMProgramBeatThePeakGear & rhs )
{
    return lhs.getProgramPAOId() == rhs.getProgramPAOId()
        && lhs.getAlertLevel() == rhs.getAlertLevel();
}

inline bool operator!=( const CtiLMProgramBeatThePeakGear & lhs, const CtiLMProgramBeatThePeakGear & rhs )
{
    return ! ( lhs == rhs );
}

