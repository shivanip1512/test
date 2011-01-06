#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ControlStrategy.h"
#include "ccmonitorpoint.h"
#include "ccsubstationbus.h"
#include "CapControlPao.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

class CtiCCSubstation : public RWCollectable, public CapControlPao
{

public:

RWDECLARE_COLLECTABLE( CtiCCSubstation )

    CtiCCSubstation();
    CtiCCSubstation(Cti::RowReader& rdr);
    CtiCCSubstation(const CtiCCSubstation& substation);

    virtual ~CtiCCSubstation();

    BOOL getOvUvDisabledFlag() const;
    BOOL getVoltReductionFlag() const;
    const string& getParentName() const;
    LONG getParentId() const;
    LONG getDisplayOrder() const;
    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getSaEnabledFlag() const;
    BOOL getRecentlyControlledFlag() const;
    BOOL getStationUpdatedFlag() const;
    LONG getSaEnabledId() const;
    LONG getVoltReductionControlId() const;
    BOOL getChildVoltReductionFlag() const;

    Cti::CapControl::PaoIdList* getCCSubIds(){return &_subBusIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    Cti::CapControl::PointIdList* getPointIds() {return &_pointIds; };

    CtiCCSubstation& setOvUvDisabledFlag(BOOL flag);
    CtiCCSubstation& setVoltReductionFlag(BOOL flag);
    CtiCCSubstation& setParentName(const string& name);
    CtiCCSubstation& setParentId(LONG parentId);
    CtiCCSubstation& setDisplayOrder(LONG displayOrder);
    CtiCCSubstation& setPFactor(DOUBLE pfactor);
    CtiCCSubstation& setEstPFactor(DOUBLE estpfactor);
    CtiCCSubstation& setSaEnabledFlag(BOOL flag);
    CtiCCSubstation& setRecentlyControlledFlag(BOOL flag);
    CtiCCSubstation& setStationUpdatedFlag(BOOL flag);
    CtiCCSubstation& setSaEnabledId(LONG saId);
    CtiCCSubstation& setVoltReductionControlId(LONG pointid);
    CtiCCSubstation& setChildVoltReductionFlag(BOOL flag);

    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCSubstation& checkAndUpdateRecentlyControlledFlag();
    CtiCCSubstation& checkAndUpdateChildVoltReductionFlags();
    BOOL isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSubstation& operator=(const CtiCCSubstation& right);

    CtiCCSubstation* replicate() const;

    private:

    string _parentName;
    LONG _parentId;
    LONG _displayOrder;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    BOOL _voltReductionFlag;
    BOOL _recentlyControlledFlag;
    BOOL _stationUpdatedFlag;
    BOOL _childVoltReductionFlag;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;
    BOOL _saEnabledFlag;
    LONG _saEnabledId;

    LONG _voltReductionControlId;
    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    Cti::CapControl::PaoIdList _subBusIds;
    Cti::CapControl::PointIdList _pointIds;

    void restore(Cti::RowReader& rdr);


};

typedef CtiCCSubstation* CtiCCSubstationPtr;