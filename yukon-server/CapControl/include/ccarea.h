#pragma once

#include "ccAreaBase.h"


class CtiCCArea : public CtiCCAreaBase
{
public:

    DECLARE_COLLECTABLE( CtiCCArea )

    CtiCCArea(StrategyManager * strategyManager = nullptr);
    CtiCCArea(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCArea() = default;

    bool getReEnableAreaFlag() const;
    bool getChildVoltReductionFlag() const;

    void setReEnableAreaFlag(const bool flag);
    void setChildVoltReductionFlag(const bool flag);

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    void checkAndUpdateChildVoltReductionFlags();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCArea* replicate() const;

private:

    bool _reEnableAreaFlag;
    bool _childVoltReductionFlag;

    void restoreDynamicData(Cti::RowReader& rdr);

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    void handleSpecializedPointData( const CtiPointDataMsg & message ) override;

    CtiCCArea(const CtiCCArea& area) = default;
    CtiCCArea& operator=(const CtiCCArea& right) = delete;
};

typedef CtiCCArea* CtiCCAreaPtr;
typedef std::vector<CtiCCAreaPtr> CtiCCArea_vec;
typedef std::set<CtiCCAreaPtr> CtiCCArea_set;

