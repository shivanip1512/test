#pragma once

#include "ccAreaBase.h"


class CtiCCSpecial : public CtiCCAreaBase
{
public:

    DECLARE_COLLECTABLE( CtiCCSpecial );

    CtiCCSpecial(StrategyManager * strategyManager = nullptr);
    CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCSpecial() = default;

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCSpecial* replicate() const;

protected:

    CtiCCSpecial(const CtiCCSpecial& area) = default;

private:

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    void handleSpecializedPointData( const CtiPointDataMsg & message ) override;

    CtiCCSpecial& operator=(const CtiCCSpecial& right) = delete;
};

typedef CtiCCSpecial* CtiCCSpecialPtr;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;

