#pragma once

#include "ccAreaBase.h"


class CtiCCSpecial : public CtiCCAreaBase
{
public:
    DECLARE_COLLECTABLE( CtiCCSpecial );

    CtiCCSpecial();
    CtiCCSpecial(StrategyManager * strategyManager);
    CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCSpecial();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCSpecial* replicate() const;

private:

    void restoreStaticData(Cti::RowReader& rdr);
    void restoreDynamicData(Cti::RowReader& rdr);

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    CtiCCSpecial(const CtiCCSpecial& area);
    CtiCCSpecial& operator=(const CtiCCSpecial& right);
};

typedef CtiCCSpecial* CtiCCSpecialPtr;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;
