#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccsubstationbus.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "StrategyManager.h"
#include "ccmonitorpoint.h"
#include "Controllable.h"

class CtiCCAreaBase : public Controllable, public RWCollectable
{

public:
    RWDECLARE_COLLECTABLE( CtiCCAreaBase )
    CtiCCAreaBase();
    CtiCCAreaBase(StrategyManager * strategyManager);
    CtiCCAreaBase(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCAreaBase(const CtiCCAreaBase& area);

    virtual ~CtiCCAreaBase();

    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;
    BOOL getOvUvDisabledFlag() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    string getAdditionalFlags() const;

    Cti::CapControl::PaoIdVector getSubstationIds() const {return _subStationIds;};
    void addSubstationId(long subId);
    Cti::CapControl::PaoIdVector::iterator removeSubstationId(Cti::CapControl::PaoIdVector::iterator subIter);
    void removeSubstationId(long subId);
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    CtiCCAreaBase& setVoltReductionControlPointId(LONG pointId);
    CtiCCAreaBase& setVoltReductionControlValue(BOOL flag);
    CtiCCAreaBase& setOvUvDisabledFlag(BOOL flag);
    CtiCCAreaBase& setPFactor(DOUBLE pfactor);
    CtiCCAreaBase& setEstPFactor(DOUBLE estPfactor);

    void setDirty(bool flag);
    bool isDirty() {return _dirty;};
    virtual bool isSpecial() {return false;};


    void setDynamicData(Cti::RowReader& rdr);
    void restore(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCAreaBase& operator=(const CtiCCAreaBase& right);

private:

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;
    DOUBLE _pfactor;
    DOUBLE _estPfactor;
    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    bool _dirty;

    Cti::CapControl::PaoIdVector _subStationIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;


};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

