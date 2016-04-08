#pragma once

#include "CapControlPao.h"
#include "DynamicData.h"


class CtiCCSubstation : public CapControlPao, public DynamicData
{
public:
    DECLARE_COLLECTABLE( CtiCCSubstation );

    CtiCCSubstation();
    CtiCCSubstation(Cti::RowReader& rdr);
    CtiCCSubstation(const CtiCCSubstation& substation);

    virtual ~CtiCCSubstation();

    bool getOvUvDisabledFlag() const;
    bool getVoltReductionFlag() const;
    const std::string& getParentName() const;
    long getParentId() const;
    long getDisplayOrder() const;
    double getPFactor() const;
    double getEstPFactor() const;
    bool getSaEnabledFlag() const;
    bool getRecentlyControlledFlag() const;
    bool getStationUpdatedFlag() const;
    long getSaEnabledId() const;
    long getVoltReductionControlId() const;
    bool getChildVoltReductionFlag() const;

    Cti::CapControl::PaoIdVector getCCSubIds() const {return _subBusIds;};
    void addCCSubId(long busId){_subBusIds.push_back(busId);};

    void setOvUvDisabledFlag(bool flag);
    void setVoltReductionFlag(bool flag);
    void setParentName(const std::string& name);
    void setParentId(long parentId);
    void setDisplayOrder(long displayOrder);
    void setPFactor(double pfactor);
    void setEstPFactor(double estpfactor);
    void setSaEnabledFlag(bool flag);
    void setRecentlyControlledFlag(bool flag);
    void setStationUpdatedFlag(bool flag);
    void setSaEnabledId(long saId);
    void setVoltReductionControlId(long pointid);
    void setChildVoltReductionFlag(bool flag);

    void getPowerFactorData( double & watts, double & vars, double & estimatedVars );
    void updatePowerFactorData();

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    void checkAndUpdateRecentlyControlledFlag();
    void checkAndUpdateChildVoltReductionFlags();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCSubstation& operator=(const CtiCCSubstation& right);

    CtiCCSubstation* replicate() const;

protected:



private:

    enum DynamicFlagInfo
    {
        Index_OvUvDisabled,
        Index_SAEnabled,
        Index_VoltReduction,
        Index_RecentControl,
        Index_StationUpdated,
        Index_ChildVReduction,

        Length_DynamicFlags = 20
    };


    std::string _parentName;
    long _parentId;
    long _displayOrder;

    bool _ovUvDisabledFlag;
    bool _voltReductionFlag;
    bool _recentlyControlledFlag;
    bool _stationUpdatedFlag;
    bool _childVoltReductionFlag;

    double _pfactor;
    double _estPfactor;
    bool _saEnabledFlag;
    long _saEnabledId;

    long _voltReductionControlId;

    Cti::CapControl::PaoIdVector _subBusIds;

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    void restoreStaticData(Cti::RowReader& rdr);
    void restoreDynamicData(Cti::RowReader& rdr);
};

typedef CtiCCSubstation* CtiCCSubstationPtr;
typedef std::vector<CtiCCSubstationPtr> CtiCCSubstation_vec;
typedef std::set<CtiCCSubstationPtr> CtiCCSubstation_set;
