
/*---------------------------------------------------------------------------
        Filename:  ccsparea.cpp

        Programmer:  Josh Wolberg

        Description:    Source file for CtiCCSpecial.
                        CtiCCSpecial maintains the state and handles
                        the persistence of substation buses for Cap Control.

        Initial Date:  8/28/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/tpsrtvec.h>

#include "dbaccess.h"
#include "msg_signal.h"

#include "ccsparea.h"
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

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCSpecial, CTICCSPECIALAREA_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCSpecial::CtiCCSpecial()
    : Controllable(0),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
      _pfactor(0),
      _estPfactor(0),
      _ovUvDisabledFlag(false),
      _isSpecial(true),
      _insertDynamicDataFlag(false),
      _dirty(false)
{
}

CtiCCSpecial::CtiCCSpecial(StrategyManager * strategyManager)
    : Controllable(strategyManager),
      _voltReductionControlPointId(0),
      _voltReductionControlValue(0),
      _pfactor(0),
      _estPfactor(0),
      _ovUvDisabledFlag(false),
      _isSpecial(true),
      _insertDynamicDataFlag(false),
      _dirty(false)
{
}

CtiCCSpecial::CtiCCSpecial(Cti::RowReader& rdr, StrategyManager * strategyManager)
    : Controllable(rdr, strategyManager)
{
    restore(rdr);
    _operationStats.setPAOId(getPaoId());
    _confirmationStats.setPAOId(getPaoId());
}

CtiCCSpecial::CtiCCSpecial(const CtiCCSpecial& special)
    : Controllable(special)
{
    operator=(special);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCSpecial::~CtiCCSpecial()
{

    _pointIds.clear();
    if (!_substationIds.empty())
    {
        _substationIds.clear();
    }
}

CtiCCOperationStats& CtiCCSpecial::getOperationStats()
{
    return _operationStats;
}

CtiCCConfirmationStats& CtiCCSpecial::getConfirmationStats()
{
    return _confirmationStats;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCSpecial::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );
    CapControlPao::saveGuts(ostrm);

    ostrm << _substationIds.size();
    Cti::CapControl::PaoIdList::const_iterator iter = _substationIds.begin();

    for(LONG i=0;i<_substationIds.size();i++)
    {
        ostrm << (LONG)*iter;
        iter++;
    }
    double pfDisplayValue = _pfactor;
    double estPfDisplayValue = _estPfactor;

    // Modifying the display value of pFactor to represent +100% values as a negative value.
    if (_pfactor > 1)
    {
        pfDisplayValue -= 2;
    }
    if (_estPfactor > 1)
    {
        estPfDisplayValue -= 2;
    }

    ostrm << _ovUvDisabledFlag
        << pfDisplayValue
        << estPfDisplayValue
        << _voltReductionControlValue;

}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::operator=(const CtiCCSpecial& right)
{
    Controllable::operator=(right);

    if( this != &right )
    {
        _ovUvDisabledFlag = right._ovUvDisabledFlag;
        _pfactor = right._pfactor;
        _estPfactor = right._estPfactor;

        _voltReductionControlPointId = right._voltReductionControlPointId;
        _voltReductionControlValue = right._voltReductionControlValue;

        _substationIds.clear();
        _substationIds.assign(right._substationIds.begin(), right._substationIds.end());


        _operationStats = right._operationStats;
        _confirmationStats = right._confirmationStats;

       _dirty = right._dirty;
       _insertDynamicDataFlag = right._insertDynamicDataFlag;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCSpecial* CtiCCSpecial::replicate() const
{
    return(new CtiCCSpecial(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiCCSpecial::restore(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["voltreductionpointid"] >> _voltReductionControlPointId;

    if (_voltReductionControlPointId <= 0)
    {
        setVoltReductionControlValue(FALSE);
    }

    setPFactor(-1);
    setEstPFactor(-1);

    setVoltReductionControlValue(FALSE);

    _dirty = TRUE;
    _insertDynamicDataFlag = TRUE;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this substation bus.
---------------------------------------------------------------------------*/
void CtiCCSpecial::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
            

            unsigned char addFlags[] = {'N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N','N'};
            addFlags[0] = (_ovUvDisabledFlag?'Y':'N');
            _additionalFlags = string(char2string(*addFlags));
            _additionalFlags.append("NNNNNNNNNNNNNNNNNNN");

            static const string updaterSql = "update dynamicccspecialarea set additionalflags = ?, controlvalue = ? "
                                             " where areaid = ?";
            Cti::Database::DatabaseWriter updater(conn, updaterSql);
            
            updater << _additionalFlags << _voltReductionControlValue << getPaoId();

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << updaterSql << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted area into dynamicCCSpecialArea: " << getPaoName() << endl;
            }
            string addFlags ="NNNNNNNNNNNNNNNNNNNN";

            static const string inserterSql = "insert into dynamicccspecialarea values (?, ?, ?)";
            Cti::Database::DatabaseWriter inserter(conn, inserterSql);

            inserter << getPaoId() << addFlags << _voltReductionControlValue;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << inserterSql << endl;
                }
            }

            if(inserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                /*{
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }*/
                _dirty = TRUE;
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << inserterSql << endl;
                    }
                }
            }
        }

        if (getOperationStats().isDirty())
            getOperationStats().dumpDynamicData(conn, currentDateTime);
    }
}
void CtiCCSpecial::setDynamicData(Cti::RowReader& rdr)
{
    rdr["additionalflags"] >> _additionalFlags;
    std::transform(_additionalFlags.begin(), _additionalFlags.end(), _additionalFlags.begin(), tolower);

    _ovUvDisabledFlag = (_additionalFlags[0]=='y'?TRUE:FALSE);

    rdr["controlvalue"] >> _voltReductionControlValue;
    if (_voltReductionControlPointId <= 0)
    {
        setVoltReductionControlValue(FALSE);
    }

    _insertDynamicDataFlag = FALSE;
    _dirty = false;



}

/*---------------------------------------------------------------------------
    getControlPointId

    Returns the controlPoint Id of the area
---------------------------------------------------------------------------*/
LONG CtiCCSpecial::getVoltReductionControlPointId() const
{
    return _voltReductionControlPointId;
}

/*---------------------------------------------------------------------------
    getControlValue

    Returns the ControlValue flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSpecial::getVoltReductionControlValue() const
{
    return _voltReductionControlValue;
}

/*---------------------------------------------------------------------------
    isDirty()

    Returns the dirty flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSpecial::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    getOvUvDisabledFlag

    Returns the ovuv disable flag of the area
---------------------------------------------------------------------------*/
BOOL CtiCCSpecial::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

/*---------------------------------------------------------------------------
    getPFactor

    Returns the getPFactor of the area
---------------------------------------------------------------------------*/
DOUBLE CtiCCSpecial::getPFactor() const
{
    return _pfactor;
}
/*---------------------------------------------------------------------------
    getEstPFactor

    Returns the getEstPFactor of the area
---------------------------------------------------------------------------*/
DOUBLE CtiCCSpecial::getEstPFactor() const
{
    return _estPfactor;
}

/*---------------------------------------------------------------------------
    setControlPointId

    Sets the ControlPointId of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setVoltReductionControlPointId(LONG pointId)
{
    _voltReductionControlPointId = pointId;
    return *this;
}
/*---------------------------------------------------------------------------
    setControlValue

    Sets the ControlValue flag of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setVoltReductionControlValue(BOOL flag)
{
    if (_voltReductionControlValue != flag)
        _dirty = TRUE;
    _voltReductionControlValue = flag;
    return *this;
}


/*---------------------------------------------------------------------------
    setOvUvDisabledFlag

    Sets the ovuv disable flag of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setOvUvDisabledFlag(BOOL flag)
{
    if (_ovUvDisabledFlag != flag)
        _dirty = TRUE;

    _ovUvDisabledFlag = flag;
    return *this;
}

/*---------------------------------------------------------------------------
    setPFactor

    Sets the PFactor of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setPFactor(DOUBLE pfactor)
{
    _pfactor = pfactor;
    return *this;
}
/*---------------------------------------------------------------------------
    setEstPFactor

    Sets the estPFactor of the area
---------------------------------------------------------------------------*/
CtiCCSpecial& CtiCCSpecial::setEstPFactor(DOUBLE estpfactor)
{
    _estPfactor = estpfactor;
    return *this;
}

