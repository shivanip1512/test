#pragma once

#include "Controllable.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"

namespace Cti
{
    class RowReader;
}



class CtiCCAreaBase : public Controllable
{
public:
    DECLARE_COLLECTABLE( CtiCCAreaBase );

public:
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
    Cti::CapControl::PaoIdVector::iterator removeSubstationId(Cti::CapControl::PaoIdVector::iterator subIter);
    void removeSubstationId(long subId);
    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    CtiCCAreaBase& setVoltReductionControlPointId(long pointId);
    CtiCCAreaBase& setVoltReductionControlValue(bool flag);
    CtiCCAreaBase& setOvUvDisabledFlag(bool flag);

    void updatePowerFactorData();

    void setDirty(bool flag);
    bool isDirty() {return _dirty;}
    virtual bool isSpecial() {return false;}


    void setDynamicData(Cti::RowReader& rdr);
    void restore(Cti::RowReader& rdr);

    CtiCCAreaBase& operator=(const CtiCCAreaBase& right);

    CtiCCAreaBase& setAreaUpdatedFlag(bool flag);
    bool getAreaUpdatedFlag() const;


public:

    double getPFactor() const;
    double getEstPFactor() const;
    CtiCCAreaBase& setPFactor(double pfactor);
    CtiCCAreaBase& setEstPFactor(double estPfactor);

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

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;


};

typedef CtiCCAreaBase* CtiCCAreaBasePtr;

