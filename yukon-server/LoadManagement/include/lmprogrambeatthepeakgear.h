#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"
#include "BeatThePeakControlInterface.h"
#include "BeatThePeakAlertLevel.h"

class CtiLMProgramBeatThePeakGear : public SmartGearBase, public CtiLMProgramDirectGear
{
public:
    typedef CtiLMProgramDirectGear Inherited;
    CtiLMProgramBeatThePeakGear(Cti::RowReader &rdr);

    virtual CtiLMProgramDirectGear * replicate() const;

    int CtiLMProgramBeatThePeakGear::operator==(const CtiLMProgramBeatThePeakGear& right) const;
    int CtiLMProgramBeatThePeakGear::operator!=(const CtiLMProgramBeatThePeakGear& right) const;

    void CtiLMProgramBeatThePeakGear::restore(Cti::RowReader &rdr);

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);

    Cti::BeatThePeak::AlertLevel getAlertLevel() const;

private:
    Cti::BeatThePeak::AlertLevel _alertLevel;
};
