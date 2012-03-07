
/*---------------------------------------------------------------------------
        Filename:  ccmonitorpoint.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCMonitorPoint.
                        CtiCCMonitorPoint maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "precompiled.h"

#include "dbaccess.h"
#include "ccid.h"
#include "ccutil.h"
#include "ccmonitorpoint.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "utility.h"
#include "database_writer.h"

using namespace std;
extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCMonitorPoint, CTICCMONITORPOINT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCMonitorPoint::CtiCCMonitorPoint() :
_phase(Cti::CapControl::Phase_Unknown),
_pointId(0),
_deviceId(0),
_displayOrder(0),
_scannable(false),
_nInAvg(0),
_upperBW(0),
_lowerBW(0),
_value(0),
_scanInProgress(false),
_insertDynamicDataFlag(false),
_dirty(false)
{
}

CtiCCMonitorPoint::CtiCCMonitorPoint(Cti::RowReader& rdr)
{
    restore(rdr);
}

CtiCCMonitorPoint::CtiCCMonitorPoint(const CtiCCMonitorPoint& point)
{
    operator=(point);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCMonitorPoint::~CtiCCMonitorPoint()
{
}

/*---------------------------------------------------------------------------
    getPointId

    Returns the unique id of the monitor point
---------------------------------------------------------------------------*/
LONG CtiCCMonitorPoint::getPointId() const
{
    return _pointId;
}
/*---------------------------------------------------------------------------
    getBankId

    Returns the bankId of the monitor point
---------------------------------------------------------------------------*/
LONG CtiCCMonitorPoint::getDeviceId() const
{
    return _deviceId;
}
/*---------------------------------------------------------------------------
    getValue

    Returns the value of the monitor point
---------------------------------------------------------------------------*/
DOUBLE CtiCCMonitorPoint::getValue() const
{
    return _value;
}

/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the displayOrder of the monitor point
---------------------------------------------------------------------------*/
LONG CtiCCMonitorPoint::getDisplayOrder() const
{
    return _displayOrder;
}

/*---------------------------------------------------------------------------
    isScannable()

    Returns the scannability of the monitor point
---------------------------------------------------------------------------*/
BOOL CtiCCMonitorPoint::isScannable() const
{
    return _scannable;
}
/*---------------------------------------------------------------------------
    getNInAvg()

    Returns the number n the value of the monitor point is averaged by
---------------------------------------------------------------------------*/
LONG CtiCCMonitorPoint::getNInAvg() const
{
    return _nInAvg;
}
/*---------------------------------------------------------------------------
    getUpperBandwidth

    Returns the upper bandwidth for the monitor point
---------------------------------------------------------------------------*/
DOUBLE CtiCCMonitorPoint::getUpperBandwidth() const
{
    return _upperBW;
}
/*---------------------------------------------------------------------------
    getLowerBandwidth

    Returns the lower bandwidth for the monitor point
---------------------------------------------------------------------------*/
DOUBLE CtiCCMonitorPoint::getLowerBandwidth() const
{
    return _lowerBW;
}

/*---------------------------------------------------------------------------
    getTimeStamp

    Returns the the timeStamp of the value of the monitor point
---------------------------------------------------------------------------*/
CtiTime CtiCCMonitorPoint::getTimeStamp() const
{
    return _timeStamp;
}


/*---------------------------------------------------------------------------
    getScanInProgress()

    Returns the scanInProgress Flag of the monitor point
---------------------------------------------------------------------------*/
BOOL CtiCCMonitorPoint::getScanInProgress() const
{
    return _scanInProgress;
}
/*---------------------------------------------------------------------------
    setPointId

    Sets the pointId of the monitorPoint- use with caution
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setPointId(LONG pointId)
{
    _pointId = pointId;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setBankId

    Sets the bankId of the monitorPoint- use with caution
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setDeviceId(LONG devId)
{
    _deviceId = devId;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setValue

    Sets the value of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setValue(DOUBLE value)
{
    if (_value != value)
    {
        _dirty = TRUE;
    }
    _value = value;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the display order of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setDisplayOrder(LONG displayOrder)
{
    if (_displayOrder != displayOrder)
    {
        _dirty = TRUE;
    }
    _displayOrder = displayOrder;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setScannable

    Sets the scannable flag of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setScannable(BOOL flag)
{
    if (_scannable != flag)
    {
        _dirty = TRUE;
    }
    _scannable = flag;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setNInAvg

    Sets the Number N of points to be averaged together in the value of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setNInAvg(LONG n)
{
    if (_nInAvg != n)
    {
        _dirty = TRUE;
    }
    _nInAvg = n;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setUpperBandwidth

    Sets the UpperBandwidth of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setUpperBandwidth(DOUBLE upperBW)
{
    if (_upperBW != upperBW)
    {
        _dirty = TRUE;
    }
    _upperBW = upperBW;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setLowerBandwidth

    Sets the LowerBandwidth of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setLowerBandwidth(DOUBLE lowerBW)
{
    if (_lowerBW != lowerBW)
    {
        _dirty = TRUE;
    }
    _lowerBW = lowerBW;

    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setTimeStamp

    Sets the timeStamp on the value of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setTimeStamp(CtiTime timeStamp)
{
    if (_timeStamp != timeStamp)
    {
        _dirty = TRUE;
    }
    _timeStamp = timeStamp;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setScanInProgress

    Sets the scanInProgress flag of the monitorPoint-
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::setScanInProgress(BOOL flag)
{
    if (_scanInProgress != flag)
    {
        _dirty = TRUE;
    }
    _scanInProgress = flag;

    //do not notify observers of this !
    return *this;
}
/*---------------------------------------------------------------------------

/*-------------------------------------------------------------------------
    restoreGuts

    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiCCMonitorPoint::restoreGuts(RWvistream& istrm)
{
    LONG tempTime1;
    RWCollectable::restoreGuts( istrm );

    istrm >> _pointId
        >> _deviceId
        >> _value
        >> tempTime1
        >> _scanInProgress;

    _timeStamp = CtiTime(tempTime1);
}

/*---------------------------------------------------------------------------
    saveGuts

    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::saveGuts(RWvostream& ostrm ) const
{
    RWCollectable::saveGuts( ostrm );
    LONG tempTime = _timeStamp.seconds();
    ostrm << _pointId
        << _deviceId
        << _value
        << tempTime
        << _scanInProgress;


}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::operator=(const CtiCCMonitorPoint& rght)
{
    if( this != &rght )
    {
        _phase = rght._phase;
        _pointId = rght._pointId;
        _deviceId = rght._deviceId;
        _displayOrder = rght._displayOrder;
        _scannable = rght._scannable;
        _nInAvg = rght._nInAvg;
        _upperBW = rght._upperBW;
        _lowerBW = rght._lowerBW;
        _value = rght._value;

        _timeStamp = rght._timeStamp;
        _scanInProgress = rght._scanInProgress;

        _dirty = rght._dirty;
        _insertDynamicDataFlag = rght._insertDynamicDataFlag;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::operator==(const CtiCCMonitorPoint& rght) const
{
    return (getPointId() == rght.getPointId() &&
            getDeviceId() == rght.getDeviceId());
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::operator!=(const CtiCCMonitorPoint& rght) const
{
    return (getPointId() != rght.getPointId() ||
            getDeviceId() != rght.getDeviceId());
}


/*---------------------------------------------------------------------------
    restore

    Restores self's state given a Reader
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::restore(Cti::RowReader& rdr)
{
    CtiTime currentDateTime = CtiTime();

    CtiTime dynamicTimeStamp;
    string tempBoolString;

    if ( ! rdr["Phase"].isNull() )
    {
        std::string phaseStr;

        rdr["Phase"] >> phaseStr;
        CtiToUpper(phaseStr);
        _phase = Cti::CapControl::resolvePhase(phaseStr);
    }

    rdr["deviceid"] >> _deviceId;
    rdr["pointid"] >> _pointId;
    rdr["displayorder"] >> _displayOrder;
    rdr["scannable"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setScannable(tempBoolString=="y"?TRUE:FALSE);
    rdr["ninavg"] >> _nInAvg;
    rdr["upperbandwidth"] >> _upperBW;
    rdr["lowerbandwidth"] >> _lowerBW;


    _value = 0;
    _timeStamp = CtiTime();
    _scanInProgress = FALSE;
    _insertDynamicDataFlag = TRUE;
    /*{
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " - _dirty = TRUE  " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }*/
    _dirty = TRUE;

}

void CtiCCMonitorPoint::setDynamicData(Cti::RowReader& rdr)
{
    string tempBoolString;

    rdr["value"] >> _value;
    rdr["datetime"] >> _timeStamp;
    rdr["scaninprogress"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    setScanInProgress(tempBoolString=="y"?TRUE:FALSE);

    _insertDynamicDataFlag = FALSE;
    _dirty = FALSE;

}


/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
boost::shared_ptr<CtiCCMonitorPoint> CtiCCMonitorPoint::replicate() const
{
    return(CtiCCMonitorPointPtr(new CtiCCMonitorPoint(*this)));
}

/*---------------------------------------------------------------------------
    compareTo

    Used for ordering cap banks within a feeder by control order.
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::compareTo(const RWCollectable* rght) const
{
    return 1;// _controlorder == ((CtiCCMonitorPointPtr)rght)->getDisplayOrder() ? 0 : (_controlorder > ((CtiCCMonitorPointPtr)rght)->getControlOrder() ? 1 : -1);
}

/*---------------------------------------------------------------------------
    isDirty

    Returns the dirty flag of the cap bank
---------------------------------------------------------------------------*/
BOOL CtiCCMonitorPoint::isDirty() const
{
    return _dirty;
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
        if( !_insertDynamicDataFlag )
        {
            static const string updateSql = "update dynamicccmonitorbankhistory set "
                                            "value = ?, "
                                            "datetime = ?, "
                                            "scaninprogress = ? "
                                            " where "
                                            "deviceid = ? AND "
                                            "pointid = ?";

            Cti::Database::DatabaseWriter updater(conn, updateSql);

            updater
            << _value
            << _timeStamp
            << (string)(_scanInProgress?"Y":"N")
            << _deviceId
            << _pointId;

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Inserted Monitor Point into dynamicCCMonitorBankHistory: " << endl;
            }


            static const string insertSql = "insert into dynamicccmonitorbankhistory values( "
                                            "?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            dbInserter << _deviceId
            <<  _pointId
            << _value
            << _timeStamp
            << (string)(_scanInProgress?"Y":"N");

            if( _CC_DEBUG & CC_DEBUG_DATABASE )
            {
                string loggedSQLstring = dbInserter.asString();
                {
                    CtiLockGuard<CtiLogger> logger_guard(dout);
                    dout << CtiTime() << " - " << loggedSQLstring << endl;
                }
            }

            if(dbInserter.execute())    // No error occured!
            {
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = dbInserter.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
                }
            }
        }
    }
}


Cti::CapControl::Phase  CtiCCMonitorPoint::getPhase() const
{
    return _phase;
}


CtiCCMonitorPoint &  CtiCCMonitorPoint::setPhase( const Cti::CapControl::Phase & phase )
{
    if ( _phase != phase )
    {
        _phase = phase;
        _dirty = TRUE;
    }

    return *this;
}

