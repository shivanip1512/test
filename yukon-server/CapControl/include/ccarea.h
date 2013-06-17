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
#include "ccareabase.h"

class CtiCCArea : public CtiCCAreaBase
{

public:

RWDECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(StrategyManager * strategyManager);
    CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

    bool getReEnableAreaFlag() const;
    bool getChildVoltReductionFlag() const;
    bool getAreaUpdatedFlag() const;

    void deleteCCSubs(long subId);

    CtiCCArea& setReEnableAreaFlag(bool flag);
    CtiCCArea& setChildVoltReductionFlag(bool flag);
    CtiCCArea& setAreaUpdatedFlag(bool flag);

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCArea& checkAndUpdateChildVoltReductionFlags();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCArea& operator=(const CtiCCArea& right);

    CtiCCArea* replicate() const;

private:

    bool _reEnableAreaFlag;
    bool _childVoltReductionFlag;

       //don't stream
    bool _insertDynamicDataFlag;
    bool _areaUpdatedFlag;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCArea* CtiCCAreaPtr;
typedef std::vector<CtiCCAreaPtr> CtiCCArea_vec;
typedef std::set<CtiCCAreaPtr> CtiCCArea_set;
