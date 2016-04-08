#include "precompiled.h"

#include "ccsubstation.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"
#include "MsgVerifyBanks.h"

using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::deserializeFlag;
using Cti::CapControl::serializeFlag;
using Cti::CapControl::PaoIdVector;
using Cti::CapControl::setVariableIfDifferent;

DEFINE_COLLECTABLE( CtiCCSubstation, CTICCSUBSTATION_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSubstation::CtiCCSubstation() :
_parentId(0),
_displayOrder(0),
_ovUvDisabledFlag(false),
_voltReductionFlag(false),
_recentlyControlledFlag(false),
_stationUpdatedFlag(false),
_childVoltReductionFlag(false),
_pfactor(0),
_estPfactor(0),
_saEnabledFlag(false),
_saEnabledId(0),
_voltReductionControlId(0)
{
}

CtiCCSubstation::CtiCCSubstation( Cti::RowReader & rdr )
    :   CapControlPao( rdr ),
        _parentId( 0 ),
        _displayOrder( 0 ),
        _ovUvDisabledFlag( false ),
        _voltReductionFlag( false ),
        _recentlyControlledFlag( false ),
        _stationUpdatedFlag( false ),
        _childVoltReductionFlag( false ),
        _pfactor( -1 ),
        _estPfactor( -1 ),
        _saEnabledFlag( false ),
        _saEnabledId( 0 ),
        _voltReductionControlId( 0 )
{
    restoreStaticData( rdr );
    if ( hasDynamicData( rdr["AdditionalFlags"] ) )
    {
        restoreDynamicData( rdr );
    }
}

CtiCCSubstation::CtiCCSubstation(const CtiCCSubstation& substation)
{
    operator=(substation);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSubstation::~CtiCCSubstation()
{
    try
    {
        _subBusIds.clear();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSubstation& CtiCCSubstation::operator=(const CtiCCSubstation& right)
{
    if( this != &right )
    {
        CapControlPao::operator=(right);

        _parentName = right._parentName;
        _parentId = right._parentId;
        _displayOrder = right._displayOrder;

        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _voltReductionFlag = right._voltReductionFlag;
        _voltReductionControlId = right._voltReductionControlId;
        _childVoltReductionFlag = right._childVoltReductionFlag;

        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;
        _saEnabledFlag = right._saEnabledFlag;
        _saEnabledId = right._saEnabledId;
        _recentlyControlledFlag = right._recentlyControlledFlag;
        _stationUpdatedFlag = right._stationUpdatedFlag;

        _subBusIds.clear();
        _subBusIds.assign(right._subBusIds.begin(), right._subBusIds.end());

    }
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSubstation* CtiCCSubstation::replicate() const
{
    return new CtiCCSubstation( *this );
}

void CtiCCSubstation::restoreStaticData( Cti::RowReader & rdr )
{
    rdr["VoltReductionPointId"] >> _voltReductionControlId;
}

void CtiCCSubstation::restoreDynamicData( Cti::RowReader & rdr )
{
    std::string flags;

    rdr["AdditionalFlags"] >> flags;

    _ovUvDisabledFlag       = deserializeFlag( flags, Index_OvUvDisabled );
    _saEnabledFlag          = deserializeFlag( flags, Index_SAEnabled );
    _recentlyControlledFlag = deserializeFlag( flags, Index_RecentControl );
    _stationUpdatedFlag     = deserializeFlag( flags, Index_StationUpdated );
    _childVoltReductionFlag = deserializeFlag( flags, Index_ChildVReduction );

    if ( _voltReductionControlId > 0 )
    {
        _voltReductionFlag  = deserializeFlag( flags, Index_VoltReduction );
    }

    rdr["SAEnabledID"] >> _saEnabledId;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSubstation::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn, currentDateTime );

    getOperationStats().dumpDynamicData(conn, currentDateTime);
}


/*
    Generate the additionalflags string for the dynamic data
*/
std::string CtiCCSubstation::formatFlags() const
{
    std::string flags( Length_DynamicFlags, serializeFlag( false ) );

    flags[ Index_OvUvDisabled ]    = serializeFlag( _ovUvDisabledFlag );
    flags[ Index_SAEnabled ]       = serializeFlag( _saEnabledFlag );
    flags[ Index_VoltReduction ]   = serializeFlag( _voltReductionFlag );
    flags[ Index_RecentControl ]   = serializeFlag( _recentlyControlledFlag );
    flags[ Index_StationUpdated ]  = serializeFlag( _stationUpdatedFlag );
    flags[ Index_ChildVReduction ] = serializeFlag( _childVoltReductionFlag );

    return flags;
}

bool CtiCCSubstation::updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "UPDATE "
            "DYNAMICCCSUBSTATION "
        "SET "
            "AdditionalFlags = ?, "
            "SAEnabledID = ? "
        "WHERE "
            "SubStationID = ?";

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << formatFlags()
        << getSaEnabledId()
        << getPaoId();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__ );
}

bool CtiCCSubstation::insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "INSERT INTO "
            "DYNAMICCCSUBSTATION "
        "VALUES "
            "(?, ?, ?)";

    CTILOG_INFO( dout, "Inserting substation into DYNAMICCCSUBSTATION: " << getPaoName() );

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << formatFlags()
        << getSaEnabledId();

    return Cti::Database::executeCommand( writer, __FILE__, __LINE__, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
}

/*
    Accessors
*/
bool CtiCCSubstation::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

bool CtiCCSubstation::getVoltReductionFlag() const
{
    return _voltReductionFlag;
}

bool CtiCCSubstation::getChildVoltReductionFlag() const
{
    return _childVoltReductionFlag;
}

long CtiCCSubstation::getVoltReductionControlId() const
{
    return _voltReductionControlId;
}

long CtiCCSubstation::getParentId() const
{
    return _parentId;
}

const string& CtiCCSubstation::getParentName() const
{
    return _parentName;
}

long CtiCCSubstation::getDisplayOrder() const
{
    return _displayOrder;
}

double CtiCCSubstation::getPFactor() const
{
    return _pfactor;
}

double CtiCCSubstation::getEstPFactor() const
{
    return _estPfactor;
}

bool CtiCCSubstation::getSaEnabledFlag() const
{
    return _saEnabledFlag;
}

bool CtiCCSubstation::getRecentlyControlledFlag() const
{
    return _recentlyControlledFlag;
}

bool CtiCCSubstation::getStationUpdatedFlag() const
{
    return _stationUpdatedFlag;
}

long CtiCCSubstation::getSaEnabledId() const
{
    return _saEnabledId;
}

/*
    Mutators
        * static data should be updated through updateStaticValue().
        * dynamic data should be updated through updateDynamicValue() to maintain the state of the _dirty
            flag ensuring proper DB serialization.
        * anything that changes a value on the web display should set _stationUpdatedFlag to make sure the
            substation message is sent to the UI.
*/
void CtiCCSubstation::setOvUvDisabledFlag(bool flag)
{
    updateDynamicValue( _ovUvDisabledFlag, flag );
}

void CtiCCSubstation::setVoltReductionFlag(bool flag)
{
    updateDynamicValue( _voltReductionFlag, flag );
}

void CtiCCSubstation::setChildVoltReductionFlag(bool flag)
{
    _stationUpdatedFlag |= updateDynamicValue( _childVoltReductionFlag, flag );
}

void CtiCCSubstation::setVoltReductionControlId(long pointid)
{
    updateStaticValue( _voltReductionControlId, pointid );
}

void CtiCCSubstation::setParentId(long parentId)
{
    updateStaticValue( _parentId, parentId );
}

void CtiCCSubstation::setParentName(const string& parentName)
{
    updateDynamicValue( _parentName, parentName );
}

void CtiCCSubstation::setDisplayOrder(long displayOrder)
{
    updateStaticValue( _displayOrder, displayOrder );
}

void CtiCCSubstation::setPFactor(double pfactor)
{
    updateDynamicValue( _pfactor, pfactor );
}

void CtiCCSubstation::setEstPFactor(double estpfactor)
{
    _stationUpdatedFlag |= updateDynamicValue( _estPfactor, estpfactor );
}

void CtiCCSubstation::setSaEnabledFlag(bool flag)
{
    updateDynamicValue( _saEnabledFlag, flag );
}

void CtiCCSubstation::setRecentlyControlledFlag(bool flag)
{
    _stationUpdatedFlag |= updateDynamicValue( _recentlyControlledFlag, flag );
}

void CtiCCSubstation::setStationUpdatedFlag(bool flag)
{
    updateDynamicValue( _stationUpdatedFlag, flag );
}

void CtiCCSubstation::setSaEnabledId(long saId)
{
    updateDynamicValue( _saEnabledId, saId );
}


void CtiCCSubstation::checkForAndStopVerificationOnChildSubBuses(CtiMultiMsg_vec& capMessages)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());

    for ( const long busId : getCCSubIds() )
    {
        CtiCCSubstationBusPtr currentSubstationBus = store->findSubBusByPAObjectID( busId );

        if ( currentSubstationBus && currentSubstationBus->getVerificationFlag() )
        {
            try
            {
                //reset VerificationFlag
                capMessages.push_back(new VerifyBanks(currentSubstationBus->getPaoId(),currentSubstationBus->getVerificationDisableOvUvFlag(), CapControlCommand::STOP_VERIFICATION));
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
}

void CtiCCSubstation::checkAndUpdateRecentlyControlledFlag()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());

    setRecentlyControlledFlag(
        std::any_of( _subBusIds.begin(), _subBusIds.end(),
                     [ & ]( const long busID ) -> bool
                     {
                         CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( busID );

                         return bus
                                ? bus->getRecentlyControlledFlag() || bus->getPerformingVerificationFlag()
                                : false;
                     } ) );
}

void CtiCCSubstation::checkAndUpdateChildVoltReductionFlags()
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard(store->getMux());

    setChildVoltReductionFlag(
        std::any_of( _subBusIds.begin(), _subBusIds.end(),
                     [ & ]( const long busID ) -> bool
                     {
                         CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( busID );

                         return bus
                                ? bus->getVoltReductionFlag()
                                : false;
                     } ) );
}

void CtiCCSubstation::getPowerFactorData( double & watts, double & vars, double & estimatedVars )
{
    watts         = 0.0;
    vars          = 0.0;
    estimatedVars = 0.0;

    CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
    CtiLockGuard<CtiCriticalSection>  guard( store->getMux() );

    for ( const long busID : getCCSubIds() )
    {
        if ( CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( busID ) )
        {
            watts         += bus->getCurrentWattLoadPointValue();
            vars          += bus->getTotalizedVarLoadPointValue();
            estimatedVars += bus->getEstimatedVarLoadPointValue();
        }
    }
}

void CtiCCSubstation::updatePowerFactorData()
{
    double  totalWatts,
            totalVars,
            totalEstimatedVars;

    getPowerFactorData( totalWatts, totalVars, totalEstimatedVars );

    setPFactor( Cti::CapControl::calculatePowerFactor( totalVars, totalWatts ) );
    setEstPFactor( Cti::CapControl::calculatePowerFactor( totalEstimatedVars, totalWatts ) );
}

