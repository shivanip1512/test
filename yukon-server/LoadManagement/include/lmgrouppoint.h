#pragma once

#include "lmgroupbase.h"
#include "msg_cmd.h"

class CtiLMGroupPoint : public CtiLMGroupBase
{

public:

DECLARE_COLLECTABLE( CtiLMGroupPoint );

    CtiLMGroupPoint();
    CtiLMGroupPoint(Cti::RowReader &rdr);
    CtiLMGroupPoint(const CtiLMGroupPoint& grouppoint);

    virtual ~CtiLMGroupPoint();

    LONG getDeviceIdUsage() const;
    LONG getPointIdUsage() const;
    LONG getStartControlRawState() const;

    CtiLMGroupPoint& setDeviceIdUsage(LONG deviduse);
    CtiLMGroupPoint& setPointIdUsage(LONG pointiduse);
    CtiLMGroupPoint& setStartControlRawState(LONG startcontrolstate);

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    CtiLMGroupPoint& operator=(const CtiLMGroupPoint& right);

    std::size_t getFixedSize() const override   { return sizeof( *this ); }

    /* Static Members */

private:

    LONG _deviceidusage;
    LONG _pointidusage;
    LONG _startcontrolrawstate;

    void restore(Cti::RowReader &rdr);
};
