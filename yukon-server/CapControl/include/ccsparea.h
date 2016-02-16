#pragma once

#include "ccAreaBase.h"


class CtiCCSpecial : public CtiCCAreaBase
{
public:
    DECLARE_COLLECTABLE( CtiCCSpecial );

    CtiCCSpecial();
    CtiCCSpecial(StrategyManager * strategyManager);
    CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCSpecial(const CtiCCSpecial& area);

    virtual ~CtiCCSpecial();

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    virtual bool isSpecial() {return true;};

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
