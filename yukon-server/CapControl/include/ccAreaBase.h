#pragma once

#include "Controllable.h"

namespace Cti
{
    class RowReader;
}



class CtiCCAreaBase : public Controllable
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

    void setDirty(bool flag);
    bool isDirty() {return _dirty;}
    virtual bool isSpecial() {return false;}

    void setAreaUpdatedFlag(bool flag);
    bool getAreaUpdatedFlag() const;

    double getPFactor() const;
    double getEstPFactor() const;
    void setPFactor(double pfactor);
    void setEstPFactor(double estPfactor);

private:

    long _voltReductionControlPointId;
    bool _voltReductionControlValue;
    double _pfactor;
    double _estPfactor;
    bool _ovUvDisabledFlag;
    bool _areaUpdatedFlag;

    bool _dirty;

    Cti::CapControl::PaoIdVector _subStationIds;

    void restore(Cti::RowReader& rdr);
    void setDynamicData(Cti::RowReader& rdr);

protected:

    bool _insertDynamicDataFlag;

    CtiCCAreaBase(const CtiCCAreaBase& area);
    CtiCCAreaBase& operator=(const CtiCCAreaBase& right);
};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

