#pragma once

#include "lmprogramdirect.h"
#include "smartgearbase.h"

class CtiLMProgramBeatThePeakGear : public SmartGearBase, public CtiLMProgramDirectGear
{
public:
    typedef CtiLMProgramDirectGear Inherited;
    CtiLMProgramBeatThePeakGear(Cti::RowReader &rdr);
    CtiLMProgramBeatThePeakGear(const CtiLMProgramBeatThePeakGear& gear);

    virtual CtiLMProgramDirectGear * replicate() const;

    CtiLMProgramBeatThePeakGear& CtiLMProgramBeatThePeakGear::operator=(const CtiLMProgramBeatThePeakGear& right);
    int CtiLMProgramBeatThePeakGear::operator==(const CtiLMProgramBeatThePeakGear& right) const;
    int CtiLMProgramBeatThePeakGear::operator!=(const CtiLMProgramBeatThePeakGear& right) const;

    void CtiLMProgramBeatThePeakGear::restore(Cti::RowReader &rdr);

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced);
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup);
    virtual unsigned long estimateOffTime(long controlSeconds);

    int getTier() const;

private:
    int _tier;
};
