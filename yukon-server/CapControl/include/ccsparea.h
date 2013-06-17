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
#include "ccareabase.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCSpecial : public CtiCCAreaBase
{

public:

RWDECLARE_COLLECTABLE( CtiCCSpecial )

    CtiCCSpecial();
    CtiCCSpecial(StrategyManager * strategyManager);
    CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    virtual bool isSpecial() {return true;};

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSpecial& operator=(const CtiCCSpecial& right);

    CtiCCSpecial* replicate() const;

private:

   //don't stream
    bool _insertDynamicDataFlag;

    void restore(Cti::RowReader& rdr);

};

typedef CtiCCSpecial* CtiCCSpecialPtr;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;
