
#include "precompiled.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccareabase.h"
#include "ccid.h"
#include "cccapbank.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "capcontroller.h"
#include "resolvers.h"
#include "utility.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "database_writer.h"

using std::endl;
using namespace Cti::CapControl;

extern unsigned long _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCAreaBase, CTICCAREABASE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCAreaBase::CtiCCAreaBase()
    : Controllable(0),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
      _pfactor(0),
      _estPfactor(0),
      _ovUvDisabledFlag(false),
      _dirty(false)
{
}

CtiCCAreaBase::CtiCCAreaBase(StrategyManager * strategyManager)
    : Controllable(strategyManager),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
      _pfactor(0),
      _estPfactor(0),
      _ovUvDisabledFlag(false),
      _dirty(false)
{
}

CtiCCAreaBase::CtiCCAreaBase(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : Controllable(rdr, strategyManager)
{
    restore(rdr);
    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
}

CtiCCAreaBase::CtiCCAreaBase(const CtiCCAreaBase& base)
    : Controllable(base)
{
    operator=(base);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCAreaBase::~CtiCCAreaBase()
{
    if (!_subStationIds.empty())
    {
        _subStationIds.clear();
    }
}

CtiCCOperationStats& CtiCCAreaBase::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCAreaBase::getConfirmationStats()
{
    return _confirmationStats;
}


/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::operator=(const CtiCCAreaBase& right)
{
    Controllable::operator=(right);

    if( this != &right )
    {
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;

        _voltReductionControlPointId = right._voltReductionControlPointId;
        _voltReductionControlValue = right._voltReductionControlValue;
        _additionalFlags = right._additionalFlags;

        _subStationIds = right._subStationIds;

        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;
        _dirty = right._dirty;

    }
    return *this;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCAreaBase::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );
    CapControlPao::saveGuts(ostrm);
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCAreaBase::restore(Cti::RowReader& rdr)
{
    rdr["voltreductionpointid"] >> _voltReductionControlPointId;
    if (_voltReductionControlPointId <= 0)
    {
        setVoltReductionControlValue(false);
    }
    setOvUvDisabledFlag(false);
    setPFactor(-1);
    setEstPFactor(-1);
    setDirty(false);
}
void CtiCCAreaBase::setDynamicData(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);

    _ovUvDisabledFlag = (_additionalFlags[0]=='y');
}

void CtiCCAreaBase::setDirty(bool flag)
{
    _dirty = flag;
}

string CtiCCAreaBase::getAdditionalFlags() const
{
    return _additionalFlags;
}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the controlPoint Id of the area
---------------------------------------------------------------------------*/
long CtiCCAreaBase::getVoltReductionControlPointId() const
{
    return _voltReductionControlPointId;
}

/*---------------------------------------------------------------------------
    getControlValue

    Returns the ControlValue flag of the area
---------------------------------------------------------------------------*/
bool CtiCCAreaBase::getVoltReductionControlValue() const
{
    return _voltReductionControlValue;
}



/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
bool CtiCCAreaBase::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

/*---------------------------------------------------------------------------
    getPFactor

    Returns the getPFactor of the area
---------------------------------------------------------------------------*/
double CtiCCAreaBase::getPFactor() const
{
    return _pfactor;
}
/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the getEstPFactor of the area
---------------------------------------------------------------------------*/
double CtiCCAreaBase::getEstPFactor() const
{
    return _estPfactor;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the ControlPointId of the area
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::setVoltReductionControlPointId(long pointId)
{
    _voltReductionControlPointId = pointId;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlValue

    Sets the ControlValue flag of the area
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::setVoltReductionControlValue(bool flag)
{
    _dirty = setVariableIfDifferent(_voltReductionControlValue, flag);
    return *this;
}


/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::setOvUvDisabledFlag(bool flag)
{
    _dirty = setVariableIfDifferent(_ovUvDisabledFlag, flag);
    return *this;
}

/*---------------------------------------------------------------------------
    setPFactor

    Sets the PFactor of the area
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::setPFactor(double pfactor)
{
    _pfactor = pfactor;
    return *this;
}
/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estPFactor of the area
---------------------------------------------------------------------------*/
CtiCCAreaBase& CtiCCAreaBase::setEstPFactor(double estpfactor)
{
    _estPfactor = estpfactor;
    return *this;
}

void CtiCCAreaBase::addSubstationId(long subId)
{
    _subStationIds.push_back(subId);
}
PaoIdVector::iterator CtiCCAreaBase::removeSubstationId(PaoIdVector::iterator subIter)
{
    return _subStationIds.erase(subIter);
}


void CtiCCAreaBase::removeSubstationId(long subId)
{
    _subStationIds.erase(remove(_subStationIds.begin(), _subStationIds.end(), subId), _subStationIds.end());
}
            
