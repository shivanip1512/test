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

    CtiCCAreaBase(StrategyManager * strategyManager = nullptr);
    CtiCCAreaBase(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCAreaBase() = default;

    long getVoltReductionControlPointId() const;
    bool getVoltReductionControlValue() const;
    bool getOvUvDisabledFlag() const;
    bool getAreaUpdatedFlag() const;
    double getPFactor() const;
    double getEstPFactor() const;

    void setVoltReductionControlPointId(const long pointId);
    void setVoltReductionControlValue(const bool flag);
    void setOvUvDisabledFlag(const bool flag);
    void setAreaUpdatedFlag(const bool flag);
    void setPFactor(const double pfactor);
    void setEstPFactor(const double estPfactor);

    bool isSpecial() const;

    Cti::CapControl::PaoIdVector getSubstationIds() const;
    void addSubstationId(const long subId);
    void removeSubstationId(const long subId);

    void updatePowerFactorData();

protected:

    CtiCCAreaBase(const CtiCCAreaBase& area) = default;
    CtiCCAreaBase& operator=(const CtiCCAreaBase& right) = delete;

    enum DynamicFlagInfo
    {
        Index_OvUvDisabled,
        Index_ReEnableArea,
        Index_ChildVReduction,
        Index_AreaUpdated,

        Length_DynamicFlags = 20
    };

private:

    void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) override;

    long _voltReductionControlPointId;
    bool _voltReductionControlValue;
    bool _ovUvDisabledFlag;
    bool _areaUpdatedFlag;
    double _pfactor;
    double _estPfactor;

    Cti::CapControl::PaoIdVector _subStationIds;

    void restoreStaticData(Cti::RowReader& rdr);
    void restoreDynamicData(Cti::RowReader& rdr);
};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

