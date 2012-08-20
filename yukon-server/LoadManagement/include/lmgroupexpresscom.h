#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/recursiv.h> 

#include "lmgroupbase.h"
#include "observe.h"
#include "BtpControlInterface.h"
                
class CtiLMGroupExpresscom : public CtiLMGroupBase, public Cti::LoadManagement::BtpControlInterface
{

public:

RWDECLARE_COLLECTABLE( CtiLMGroupExpresscom )

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
    virtual CtiRequestMsg* createSetPointRequestMsg(std::string settings, LONG minValue, LONG maxValue,
                                                    LONG valueB, LONG valueD, LONG valueF, LONG random,
                                                    LONG valueTA, LONG valueTB, LONG valueTC, LONG valueTD,
                                                    LONG valueTE, LONG valueTF, int priority) const;
    virtual CtiRequestMsg* createSetPointSimpleMsg(std::string settings, LONG minValue, LONG maxValue,
                                                    LONG precoolTemp, LONG random, float rampRate,
                                                    LONG precoolTime, LONG precoolHoldTime, LONG maxTempChange,
                                                    LONG totalTime, LONG rampOutTime, LONG minutesFromBegin,
                                                    int priority) const;
    virtual bool sendBtpControl(int tier, int timeout);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiLMGroupExpresscom& operator=(const CtiLMGroupExpresscom& right);

    int operator==(const CtiLMGroupExpresscom& right) const;
    int operator!=(const CtiLMGroupExpresscom& right) const;

    /* Static Members */

private:

    void restore(Cti::RowReader &rdr);
};
