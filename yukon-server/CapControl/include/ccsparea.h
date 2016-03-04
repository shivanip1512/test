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

    virtual bool isSpecial() {return true;};

    CtiCCSpecial* replicate() const;

private:

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);

    CtiCCSpecial(const CtiCCSpecial& area);
    CtiCCSpecial& operator=(const CtiCCSpecial& right);
};

typedef CtiCCSpecial* CtiCCSpecialPtr;
typedef std::set<CtiCCSpecialPtr> CtiCCSpArea_set;
typedef std::vector<CtiCCSpecialPtr> CtiCCSpArea_vec;
