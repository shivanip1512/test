

/*---------------------------------------------------------------------------
        Filename:  ccpointresponse.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCPointResponse.
                        CtiCCPointResponse maintains the point's current value.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "ccpointresponse.h"
#include "ccid.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"

extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCPointResponse, CTICCPOINTRESPONSE_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCPointResponse::CtiCCPointResponse()
{
}

CtiCCPointResponse::CtiCCPointResponse(RWDBReader& rdr)
{
    restore(rdr);
}

CtiCCPointResponse::CtiCCPointResponse(const CtiCCPointResponse& point)
{
    operator=(point);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCPointResponse::~CtiCCPointResponse()
{
}
/*---------------------------------------------------------------------------
    getBankId

    Returns the unique id of the monitor point
---------------------------------------------------------------------------*/
LONG CtiCCPointResponse::getBankId() const
{
    return _bankId;
}

/*---------------------------------------------------------------------------
    getPointId

    Returns the unique id of the monitor point
---------------------------------------------------------------------------*/
LONG CtiCCPointResponse::getPointId() const
{
    return _pointId;
}
/*---------------------------------------------------------------------------
    getValue

    Returns the value of the monitor point
---------------------------------------------------------------------------*/
DOUBLE CtiCCPointResponse::getDelta() const
{
    return _delta;
}


/*---------------------------------------------------------------------------
    getPreOpValue

    Returns the PreOpValue of the monitor point
---------------------------------------------------------------------------*/
DOUBLE CtiCCPointResponse::getPreOpValue() const
{
    return _preOpValue;
}
/*---------------------------------------------------------------------------
    setPointId

    Sets the pointId of the monitorPoint- use with caution
---------------------------------------------------------------------------*/
CtiCCPointResponse& CtiCCPointResponse::setBankId(LONG bankId)
{
    _bankId = bankId;
    //do not notify observers of this !
    return *this;
}
/*---------------------------------------------------------------------------
    setPointId

    Sets the pointId of the monitorPoint- use with caution
---------------------------------------------------------------------------*/
CtiCCPointResponse& CtiCCPointResponse::setPointId(LONG pointId)
{
    _pointId = pointId;
    //do not notify observers of this !
    return *this;
}


/*---------------------------------------------------------------------------
    setDelta

    Sets the delta of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCPointResponse& CtiCCPointResponse::setDelta(DOUBLE delta)
{
    if (_delta != delta)
    {
        _dirty = TRUE;
    }
    _delta = delta;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setPreOpValue

    Sets the preOpValue of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCPointResponse& CtiCCPointResponse::setPreOpValue(DOUBLE value)
{
    if (_preOpValue != value)
    {
        _dirty = TRUE;
    }
    _preOpValue = value;

    //do not notify observers of this !
    return *this;
}



/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCPointResponse::restoreGuts(RWvistream& istrm)
{
    RWCollectable::restoreGuts( istrm );

    istrm >> _pointId
        >> _preOpValue
        >> _delta;
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCPointResponse::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );

    ostrm << _pointId
        << _preOpValue
        << _delta;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCPointResponse& CtiCCPointResponse::operator=(const CtiCCPointResponse& right)
{
    if( this != &right )
    {
        _pointId = right._pointId;
        _preOpValue = right._preOpValue;
        _delta = right._delta;

        _dirty = right._dirty;
        _insertDynamicDataFlag = right._insertDynamicDataFlag;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCPointResponse::operator==(const CtiCCPointResponse& right) const
{
    return (getPointId() == right.getPointId());
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCPointResponse::operator!=(const CtiCCPointResponse& right) const
{
    return (getPointId() != right.getPointId());
}


/*---------------------------------------------------------------------------
    restore

    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCPointResponse::restore(RWDBReader& rdr)
{
    rdr["bankid"] >> _bankId;
    rdr["pointid"] >> _pointId;
    _preOpValue = 0;
    _delta = 0;
    //rdr["preopvalue"] >> _preOpValue;
    //rdr["delta"] >> _delta;
    _insertDynamicDataFlag = TRUE;
    /*{
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }*/
    _dirty = TRUE;

}
/*---------------------------------------------------------------------------
    restore

    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCPointResponse::setDynamicData(RWDBReader& rdr)
{
    LONG temp1;
    LONG temp2;
    rdr["bankid"]  >> temp1;
    rdr["pointid"] >> temp2;
    rdr["preopvalue"] >> _preOpValue;
    rdr["delta"] >> _delta;
    _insertDynamicDataFlag = FALSE;

    _dirty = FALSE;

}



/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCPointResponse* CtiCCPointResponse::replicate() const
{
    return(new CtiCCPointResponse(*this));
}


/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCPointResponse::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCPointResponse::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCPointResponse::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCMonitorPointResponseTable = getDatabase().table( "dynamicccmonitorpointresponse" );
        if( !_insertDynamicDataFlag )
        {


            RWDBUpdater updater = dynamicCCMonitorPointResponseTable.updater();

            updater.where(dynamicCCMonitorPointResponseTable["bankid"]==_bankId &&
                          dynamicCCMonitorPointResponseTable["pointid"]==_pointId );

            updater << dynamicCCMonitorPointResponseTable["preopvalue"].assign( _preOpValue )
                    << dynamicCCMonitorPointResponseTable["delta"].assign( _delta );

            /*{
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << updater.asString().data() << endl;
            }*/
            updater.execute( conn );

            if(updater.status().errorCode() == RWDBStatus::ok)    // No error occured!
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << updater.asString() << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted Point into dynamicCCMonitorPointResponseTable: " << endl;
            }
            RWDBInserter inserter = dynamicCCMonitorPointResponseTable.inserter();

            inserter <<  _bankId
                << _pointId
                << _preOpValue
                << _delta;

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << inserter.asString().data() << endl;
            }

            inserter.execute( conn );

            if(inserter.status().errorCode() == RWDBStatus::ok)    // No error occured!
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  " << inserter.asString() << endl;
                }
            }
        }
    }
}

