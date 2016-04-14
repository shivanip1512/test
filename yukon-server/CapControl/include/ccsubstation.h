#pragma once

#include "CapControlPao.h"
#include "DynamicData.h"


class CtiCCSubstation : public CapControlPao, public DynamicData
{
public:
    DECLARE_COLLECTABLE( CtiCCSubstation );

    CtiCCSubstation();
    CtiCCSubstation(Cti::RowReader& rdr);

    virtual ~CtiCCSubstation() = default;

    bool getOvUvDisabledFlag() const;
    bool getVoltReductionFlag() const;
    long getParentId() const;
    double getPFactor() const;
    double getEstPFactor() const;
    bool getSaEnabledFlag() const;
    bool getRecentlyControlledFlag() const;
    bool getStationUpdatedFlag() const;
    long getSaEnabledId() const;
    long getVoltReductionControlId() const;
    bool getChildVoltReductionFlag() const;

    Cti::CapControl::PaoIdVector getCCSubIds() const;
    void addCCSubId(const long busId);

    void setOvUvDisabledFlag(const bool flag);
    void setVoltReductionFlag(const bool flag);
    void setParentId(const long parentId);
    void setPFactor(const double pfactor);
    void setEstPFactor(const double estpfactor);
    void setSaEnabledFlag(const bool flag);
    void setRecentlyControlledFlag(const bool flag);
    void setStationUpdatedFlag(const bool flag);
    void setSaEnabledId(const long saId);
    void setVoltReductionControlId(const long pointid);
    void setChildVoltReductionFlag(const bool flag);

    void getPowerFactorData( double & watts, double & vars, double & estimatedVars );
    void updatePowerFactorData();

    void checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages);
    void checkAndUpdateRecentlyControlledFlag();
    void checkAndUpdateChildVoltReductionFlags();
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCSubstation* replicate() const;

protected:

    CtiCCSubstation( const CtiCCSubstation & substation ) = default;

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


    long _parentId;

    bool _ovUvDisabledFlag;
    bool _voltReductionFlag;
    bool _recentlyControlledFlag;
    bool _stationUpdatedFlag;
    bool _childVoltReductionFlag;

    bool _saEnabledFlag;
    long _saEnabledId;

    long _voltReductionControlId;

    double _pfactor;
    double _estPfactor;

    Cti::CapControl::PaoIdVector _subBusIds;

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    void restoreStaticData(Cti::RowReader& rdr);
    void restoreDynamicData(Cti::RowReader& rdr);

    void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) override;
    void handleSpecializedPointData( const CtiPointDataMsg & message ) override;

    CtiCCSubstation& operator=( const CtiCCSubstation& right ) = delete;
};

typedef CtiCCSubstation* CtiCCSubstationPtr;
typedef std::vector<CtiCCSubstationPtr> CtiCCSubstation_vec;
typedef std::set<CtiCCSubstationPtr> CtiCCSubstation_set;
