#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "lmgroupbase.h"
                
class CtiLMGroupSADigital : public CtiLMGroupBase
{
public:

RWDECLARE_COLLECTABLE( CtiLMGroupSADigital )

    CtiLMGroupSADigital();
    CtiLMGroupSADigital(Cti::RowReader &rdr);
    CtiLMGroupSADigital(const CtiLMGroupSADigital& groupexp);

    virtual ~CtiLMGroupSADigital();

    virtual CtiLMGroupBase* replicate() const;
    virtual CtiRequestMsg* createTrueCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createTimeRefreshRequestMsg(LONG refreshRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createSmartCycleRequestMsg(LONG percent, LONG period, LONG defaultCount, bool no_ramp, int priority) const;
    virtual CtiRequestMsg* createRotationRequestMsg(LONG sendRate, LONG shedTime, int priority) const;
    virtual CtiRequestMsg* createMasterCycleRequestMsg(LONG offTime, LONG period, int priority) const;

    int getNominalTimeout() const;
    CtiLMGroupSADigital& setNominalTimeout(int nominal_timeout);
    
    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupSADigital& operator=(const CtiLMGroupSADigital& right);

    int operator==(const CtiLMGroupSADigital& right) const;
    int operator!=(const CtiLMGroupSADigital& right) const;

    /* Static Members */

private:
    int _nominal_timeout;
    
    void restore(Cti::RowReader &rdr);
};
