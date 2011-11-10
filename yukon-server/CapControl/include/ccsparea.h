#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "StrategyManager.h"
#include "cctypes.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "Controllable.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCSpecial : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSpecial )

    CtiCCSpecial();
    CtiCCSpecial(StrategyManager * strategyManager);
    CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;

    BOOL getOvUvDisabledFlag() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;

    CtiCCSpecial& setVoltReductionControlPointId(LONG pointId);
    CtiCCSpecial& setVoltReductionControlValue(BOOL flag);

    CtiCCSpecial& setOvUvDisabledFlag(BOOL flag);
    CtiCCSpecial& setPFactor(DOUBLE pfactor);
    CtiCCSpecial& setEstPFactor(DOUBLE estpfactor);

    Cti::CapControl::PaoIdList* getSubstationIds() {return &_substationIds;};
    Cti::CapControl::PointIdList* getPointIds() {return &_pointIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    BOOL isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSpecial& operator=(const CtiCCSpecial& right);

    CtiCCSpecial* replicate() const;

private:

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;

    Cti::CapControl::PaoIdList _substationIds;
    BOOL _isSpecial;

    Cti::CapControl::PointIdList _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

   //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(Cti::RowReader& rdr);

};

typedef CtiCCSpecial* CtiCCSpecialPtr;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;
