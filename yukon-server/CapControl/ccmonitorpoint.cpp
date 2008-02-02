
/*---------------------------------------------------------------------------
        Filename:  ccmonitorpoint.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCMonitorPoint.
                        CtiCCMonitorPoint maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "ccid.h"
#include "ccmonitorpoint.h"
#include "pointdefs.h"
#include "logger.h"
#include "resolvers.h"
#include "utility.h"

using namespace std;
extern ULONG _CC_DEBUG;

RWDEFINE_COLLECTABLE( CtiCCMonitorPoint, CTICCMONITORPOINT_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCMonitorPoint::CtiCCMonitorPoint()
{
}

CtiCCMonitorPoint::CtiCCMonitorPoint(RWDBReader& rdr)
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
LONG CtiCCMonitorPoint::getBankId() const
{
    return _bankId;
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
CtiCCMonitorPoint& CtiCCMonitorPoint::setBankId(LONG bankId)
{
    _bankId = bankId;
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
        >> _bankId
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
        << _bankId
        << _value
        << tempTime
        << _scanInProgress;


}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiCCMonitorPoint& CtiCCMonitorPoint::operator=(const CtiCCMonitorPoint& right)
{
    if( this != &right )
    {
        _pointId = right._pointId;
        _bankId = right._bankId;
        _displayOrder = right._displayOrder;
        _scannable = right._scannable;
        _nInAvg = right._nInAvg;
        _upperBW = right._upperBW;
        _lowerBW = right._lowerBW;
        _value = right._value;

        _timeStamp = right._timeStamp;
        _scanInProgress = right._scanInProgress;

        _dirty = right._dirty;
        _insertDynamicDataFlag = right._insertDynamicDataFlag;
    }
    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::operator==(const CtiCCMonitorPoint& right) const
{
    return (getPointId() == right.getPointId() &&
            getBankId() == right.getBankId());
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::operator!=(const CtiCCMonitorPoint& right) const
{
    return (getPointId() != right.getPointId() ||
            getBankId() != right.getBankId());
}


/*---------------------------------------------------------------------------
    restore

    Restores self's state given a RWDBReader
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::restore(RWDBReader& rdr)
{
    RWDBNullIndicator isNull;
    CtiTime currentDateTime = CtiTime();

    CtiTime dynamicTimeStamp;
    string tempBoolString;

    rdr["bankid"] >> _bankId;
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

void CtiCCMonitorPoint::setDynamicData(RWDBReader& rdr)
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
CtiCCMonitorPoint* CtiCCMonitorPoint::replicate() const
{
    return(new CtiCCMonitorPoint(*this));
}

/*---------------------------------------------------------------------------
    compareTo

    Used for ordering cap banks within a feeder by control order.
---------------------------------------------------------------------------*/
int CtiCCMonitorPoint::compareTo(const RWCollectable* right) const
{
    return 1;// _controlorder == ((CtiCCMonitorPoint*)right)->getDisplayOrder() ? 0 : (_controlorder > ((CtiCCMonitorPoint*)right)->getControlOrder() ? 1 : -1);
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

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::dumpDynamicData()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    dumpDynamicData(conn,CtiTime());
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc cap bank.
---------------------------------------------------------------------------*/
void CtiCCMonitorPoint::dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime)
{
    {
        RWDBTable dynamicCCMonitorBankHistoryTable = getDatabase().table( "dynamicccmonitorbankhistory" );
        if( !_insertDynamicDataFlag )
        {


            RWDBUpdater updater = dynamicCCMonitorBankHistoryTable.updater();

            updater.where(dynamicCCMonitorBankHistoryTable["bankid"]==_bankId &&
                          dynamicCCMonitorBankHistoryTable["pointid"]==_pointId );

            updater << dynamicCCMonitorBankHistoryTable["value"].assign( _value )
            << dynamicCCMonitorBankHistoryTable["datetime"].assign( toRWDBDT((CtiTime)_timeStamp) )
            << dynamicCCMonitorBankHistoryTable["scaninprogress"].assign( (_scanInProgress?"Y":"N") );

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
                dout << CtiTime() << " - Inserted Monitor Point into dynamicCCMonitorBankHistoryTable: " << endl;
            }
            RWDBInserter inserter = dynamicCCMonitorBankHistoryTable.inserter();
            //LONG tempTime = toRWDBDT((CtiTime)_timeStamp);

            inserter << _bankId
            <<  _pointId
            << _value
            << toRWDBDT((CtiTime)_timeStamp)
            << (_scanInProgress?"Y":"N");

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

