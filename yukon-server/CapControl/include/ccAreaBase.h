#pragma once

#include "Controllable.h"
#include "DynamicData.h"

namespace Cti
{
    class RowReader;
}



class CtiCCAreaBase : public Controllable, public DynamicData
{
public:
    DECLARE_COLLECTABLE( CtiCCAreaBase );

    CtiCCAreaBase();
    CtiCCAreaBase(StrategyManager * strategyManager);
    CtiCCAreaBase(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCAreaBase();

    long getVoltReductionControlPointId() const;
    bool getVoltReductionControlValue() const;
    bool getOvUvDisabledFlag() const;

    Cti::CapControl::PaoIdVector getSubstationIds() const {return _subStationIds;}
    void addSubstationId(long subId);
    void removeSubstationId(long subId);

    void setVoltReductionControlPointId(long pointId);
    void setVoltReductionControlValue(bool flag);
    void setOvUvDisabledFlag(bool flag);

    void updatePowerFactorData();

    bool isSpecial() const;

    void setAreaUpdatedFlag(bool flag);
    bool getAreaUpdatedFlag() const;

    double getPFactor() const;
    double getEstPFactor() const;
    void setPFactor(double pfactor);
    void setEstPFactor(double estPfactor);

private:

    long _voltReductionControlPointId;
    bool _voltReductionControlValue;
    bool _ovUvDisabledFlag;
    bool _areaUpdatedFlag;
    double _pfactor;
    double _estPfactor;

    Cti::CapControl::PaoIdVector _subStationIds;

    void restoreStaticData(Cti::RowReader& rdr);
    void restoreDynamicData(Cti::RowReader& rdr);

protected:

    CtiCCAreaBase(const CtiCCAreaBase& area);
    CtiCCAreaBase& operator=(const CtiCCAreaBase& right);
};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

