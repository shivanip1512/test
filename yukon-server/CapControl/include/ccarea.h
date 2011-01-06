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

class CtiCCArea : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(StrategyManager * strategyManager);
    CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

    LONG getVoltReductionControlPointId() const;
    BOOL getVoltReductionControlValue() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getReEnableAreaFlag() const;

    DOUBLE getPFactor() const;
    DOUBLE getEstPFactor() const;
    BOOL getChildVoltReductionFlag() const;
    Cti::CapControl::PaoIdList* getSubStationList(){return &_subStationIds;};
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    Cti::CapControl::PointIdList* getPointIds() {return &_pointIds;};
    BOOL getAreaUpdatedFlag() const;

    void deleteCCSubs(long subId);

    CtiCCArea& setVoltReductionControlPointId(LONG pointId);
    CtiCCArea& setVoltReductionControlValue(BOOL flag);
    CtiCCArea& setOvUvDisabledFlag(BOOL flag);
    CtiCCArea& setReEnableAreaFlag(BOOL flag);

    CtiCCArea& setPFactor(DOUBLE pfactor);
    CtiCCArea& setEstPFactor(DOUBLE estPfactor);
    CtiCCArea& setChildVoltReductionFlag(BOOL flag);
    CtiCCArea& setAreaUpdatedFlag(BOOL flag);

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    CtiCCArea& checkAndUpdateChildVoltReductionFlags();

    BOOL isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCArea& operator=(const CtiCCArea& right);

    CtiCCArea* replicate() const;

private:

    LONG _voltReductionControlPointId;
    BOOL _voltReductionControlValue;

    DOUBLE _pfactor;
    DOUBLE _estPfactor;

    string _additionalFlags;
    BOOL _ovUvDisabledFlag;
    BOOL _reEnableAreaFlag;
    BOOL _childVoltReductionFlag;

    Cti::CapControl::PaoIdList _subStationIds;
    Cti::CapControl::PointIdList _pointIds;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

       //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;
    BOOL _areaUpdatedFlag;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCArea* CtiCCAreaPtr;
