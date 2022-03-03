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
    virtual CtiRequestMsg* createStopCycleMsg(LONG period, CtiTime &currentTime);

    virtual CtiRequestMsg* createSetPointRequestMsg(const CtiLMProgramThermostatGear & gear, int priority, std::string & logMessage) const;
    virtual CtiRequestMsg* createSetPointSimpleMsg(const CtiLMProgramThermostatGear & gear, LONG totalTime, LONG minutesFromBegin, int priority, std::string & logMessage) const;

    virtual bool sendBeatThePeakControl(Cti::BeatThePeak::AlertLevel level, int timeout);
    virtual bool sendBeatThePeakRestore();

    CtiLMGroupExpresscom& operator=(const CtiLMGroupExpresscom& right);

    std::size_t getFixedSize() const override   { return sizeof( *this ); }

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
    bool sendBeatThePeakCommandToPorter(std::string command);
};
