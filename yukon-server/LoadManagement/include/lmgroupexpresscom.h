#pragma once

#include "lmgroupbase.h"
#include "BeatThePeakControlInterface.h"

class CtiLMGroupExpresscom : public CtiLMGroupBase, public Cti::LoadManagement::BeatThePeakControlInterface
{

public:

DECLARE_COLLECTABLE( CtiLMGroupExpresscom );

    CtiLMGroupExpresscom();
    CtiLMGroupExpresscom(Cti::RowReader &rdr);
    CtiLMGroupExpresscom(const CtiLMGroupExpresscom& groupexp);

    virtual ~CtiLMGroupExpresscom();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createTargetCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority, DOUBLE kwh, CtiTime ctrlStartTime, const std::string& additionalInfo) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    virtual CtiRequestMsg* createSetPointRequestMsg(const CtiLMProgramThermoStatGear & gear, int priority) const;
    virtual CtiRequestMsg* createSetPointSimpleMsg(const CtiLMProgramThermoStatGear & gear, LONG totalTime, LONG minutesFromBegin, int priority) const;

    virtual bool sendBeatThePeakControl(Cti::BeatThePeak::AlertLevel level, int timeout);
    virtual bool sendBeatThePeakRestore();

    CtiLMGroupExpresscom& operator=(const CtiLMGroupExpresscom& right);

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
    bool sendBeatThePeakCommandToPorter(std::string command);
};
