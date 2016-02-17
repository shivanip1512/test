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
    CtiCCAreaBase(const CtiCCAreaBase& area);

    virtual ~CtiCCAreaBase();

    long getVoltReductionControlPointId() const;
    bool getVoltReductionControlValue() const;
    bool getOvUvDisabledFlag() const;
    std::string getAdditionalFlags() const;

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


    void setDynamicData(Cti::RowReader& rdr);
    void restore(Cti::RowReader& rdr);

    CtiCCAreaBase& operator=(const CtiCCAreaBase& right);

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
    std::string _additionalFlags;
    bool _ovUvDisabledFlag;

    bool _dirty;
    bool _areaUpdatedFlag;

    Cti::CapControl::PaoIdVector _subStationIds;
};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

