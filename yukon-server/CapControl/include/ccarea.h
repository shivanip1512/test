#pragma once

#include "ccAreaBase.h"


class CtiCCArea : public CtiCCAreaBase
{
public:
    DECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea();
    CtiCCArea(StrategyManager * strategyManager);
    CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCArea(const CtiCCArea& area);

    virtual ~CtiCCArea();

    bool getReEnableAreaFlag() const;
    bool getChildVoltReductionFlag() const;

    void deleteCCSubs(long subId);

    void setReEnableAreaFlag(bool flag);
    void setChildVoltReductionFlag(bool flag);

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    void checkAndUpdateChildVoltReductionFlags();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    CtiCCArea& operator=(const CtiCCArea& right);

    CtiCCArea* replicate() const;

private:

    bool _reEnableAreaFlag;
    bool _childVoltReductionFlag;

       //don't stream
    bool _insertDynamicDataFlag;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCArea* CtiCCAreaPtr;
typedef std::vector<CtiCCAreaPtr> CtiCCArea_vec;
typedef std::set<CtiCCAreaPtr> CtiCCArea_set;
