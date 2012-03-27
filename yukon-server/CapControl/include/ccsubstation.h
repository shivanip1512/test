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

    bool getOvUvDisabledFlag() const;
    bool getVoltReductionFlag() const;
    const string& getParentName() const;
    long getParentId() const;
    long getDisplayOrder() const;
    double getPFactor() const;
    double getEstPFactor() const;
    bool getSaEnabledFlag() const;
    bool getRecentlyControlledFlag() const;
    bool getStationUpdatedFlag() const;
    long getSaEnabledId() const;
    long getVoltReductionControlId() const;
    bool getChildVoltReductionFlag() const;

    Cti::CapControl::PaoIdVector getCCSubIds(){return _subBusIds;};
    void addCCSubId(long busId){_subBusIds.push_back(busId);};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    CtiCCSubstation& setOvUvDisabledFlag(bool flag);
    CtiCCSubstation& setVoltReductionFlag(bool flag);
    CtiCCSubstation& setParentName(const string& name);
    CtiCCSubstation& setParentId(long parentId);
    CtiCCSubstation& setDisplayOrder(long displayOrder);
    CtiCCSubstation& setPFactor(double pfactor);
    CtiCCSubstation& setEstPFactor(double estpfactor);
    CtiCCSubstation& setSaEnabledFlag(bool flag);
    CtiCCSubstation& setRecentlyControlledFlag(bool flag);
    CtiCCSubstation& setStationUpdatedFlag(bool flag);
    CtiCCSubstation& setSaEnabledId(long saId);
    CtiCCSubstation& setVoltReductionControlId(long pointid);
    CtiCCSubstation& setChildVoltReductionFlag(bool flag);

    double calculatePowerFactor(double kvar, double kw);
    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCSubstation& checkAndUpdateRecentlyControlledFlag();
    CtiCCSubstation& checkAndUpdateChildVoltReductionFlags();
    bool isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSubstation& operator=(const CtiCCSubstation& right);

    CtiCCSubstation* replicate() const;

    private:

    string _parentName;
    long _parentId;
    long _displayOrder;

    string _additionalFlags;
    bool _ovUvDisabledFlag;
    bool _voltReductionFlag;
    bool _recentlyControlledFlag;
    bool _stationUpdatedFlag;
    bool _childVoltReductionFlag;

    double _pfactor;
    double _estPfactor;
    bool _saEnabledFlag;
    long _saEnabledId;

    long _voltReductionControlId;
    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    Cti::CapControl::PaoIdVector _subBusIds;

    void restore(Cti::RowReader& rdr);


};

typedef CtiCCSubstation* CtiCCSubstationPtr;
typedef std::vector<CtiCCSubstationPtr> CtiCCSubstation_vec;
typedef std::set<CtiCCSubstationPtr> CtiCCSubstation_set;