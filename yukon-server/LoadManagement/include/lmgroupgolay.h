#pragma once

#include "lmgroupbase.h"

class CtiLMGroupGolay : public CtiLMGroupBase
{
public:

DECLARE_COLLECTABLE( CtiLMGroupGolay );

    CtiLMGroupGolay();
    CtiLMGroupGolay(Cti::RowReader &rdr);
    CtiLMGroupGolay(const CtiLMGroupGolay& groupexp);

    virtual ~CtiLMGroupGolay();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    int getNominalTimeout() const;
    CtiLMGroupGolay& setNominalTimeout(int nominal_timeout);

    CtiLMGroupGolay& operator=(const CtiLMGroupGolay& right);

    std::size_t getFixedSize() const override   { return sizeof( *this ); }

    /* Static Members */

private:

    int _nominal_timeout;

    void restore(Cti::RowReader &rdr);
};
