#include "precompiled.h"

#include "ccutil.h"
#include "ccmonitorpoint.h"
#include "ccid.h"
#include "utility.h"
#include "database_util.h"
#include "row_reader.h"
#include "logger.h"


using std::endl;
using std::string;

extern unsigned long _CC_DEBUG;

using Cti::CapControl::setVariableIfDifferent;


CtiCCMonitorPoint::CtiCCMonitorPoint(Cti::RowReader& rdr)
    :   _phase(Cti::CapControl::Phase_Unknown),
        _pointId(0),
        _deviceId(0),
        _displayOrder(0),
        _scannable(false),
        _nInAvg(0),
        _upperBW(0),
        _lowerBW(0),
        _value(0),
        _timeStamp(),
        _overrideStrategy(false),
        _scanInProgress(false),
        _insertDynamicDataFlag(true),
        _dirty(false)
{
    restore(rdr);
    setDynamicData(rdr);
}

/*
    Non-dynamic data accessors
*/
long CtiCCMonitorPoint::getPointId() const
{
    return _pointId;
}

long CtiCCMonitorPoint::getDeviceId() const
{
    return _deviceId;
}

long CtiCCMonitorPoint::getDisplayOrder() const
{
    return _displayOrder;
}

bool CtiCCMonitorPoint::isScannable() const
{
    return _scannable;
}

long CtiCCMonitorPoint::getNInAvg() const
{
    return _nInAvg;
}

double CtiCCMonitorPoint::getUpperBandwidth() const
{
    return _upperBW;
}

double CtiCCMonitorPoint::getLowerBandwidth() const
{
    return _lowerBW;
}

bool CtiCCMonitorPoint::getOverrideStrategy() const
{
    return _overrideStrategy;
}

Cti::CapControl::Phase  CtiCCMonitorPoint::getPhase() const
{
    return _phase;
}

std::string CtiCCMonitorPoint::getDeviceName() const
{
    return _deviceName;
}

std::string CtiCCMonitorPoint::getPointName() const
{
    return _pointName;
}

std::string CtiCCMonitorPoint::getIdentifier() const
{
    return
        _deviceName + " (ID: " + std::to_string(_deviceId) + ") / "
            + _pointName  + " (ID: " + std::to_string(_pointId) + ")";
}

/*
    Dynamic data accessors
*/
double CtiCCMonitorPoint::getValue() const
{
    return _value;
}

CtiTime CtiCCMonitorPoint::getTimeStamp() const
{
    return _timeStamp;
}

bool CtiCCMonitorPoint::getScanInProgress() const
{
    return _scanInProgress;
}

/*
    Dynamic data mutators
*/
void CtiCCMonitorPoint::setValue(const double value)
{
    _dirty |= setVariableIfDifferent(_value, value);
}

void CtiCCMonitorPoint::setTimeStamp(const CtiTime & timeStamp)
{
    _dirty |= setVariableIfDifferent(_timeStamp, timeStamp);
}

void CtiCCMonitorPoint::setScanInProgress(const bool flag)
{
    _dirty |= setVariableIfDifferent(_scanInProgress, flag);
}

/*
    Restores self's state given a Reader
*/
void CtiCCMonitorPoint::restore(Cti::RowReader& rdr)
{
    std::string scratch;

    rdr["DeviceId"] >> _deviceId;
    rdr["PointId"]  >> _pointId;

    rdr["DisplayOrder"] >> _displayOrder;

    rdr["Scannable"] >> scratch;
    CtiToUpper(scratch);
    _scannable = (scratch == "Y");

    rdr["NINAvg"] >> _nInAvg;

    rdr["UpperBandwidth"] >> _upperBW;
    rdr["LowerBandwidth"] >> _lowerBW;

    if ( ! rdr["Phase"].isNull() )
    {
        rdr["Phase"] >> scratch;
        CtiToUpper(scratch);
        _phase = Cti::CapControl::resolvePhase(scratch);
    }

    rdr["OverrideStrategy"] >> scratch;
    CtiToUpper(scratch);
    _overrideStrategy = (scratch == "Y");

    rdr["PAOName"]   >> _deviceName;
    rdr["POINTNAME"] >> _pointName;
}

void CtiCCMonitorPoint::setDynamicData(Cti::RowReader& rdr)
{
    std::string scratch;

    if ( ! rdr["Value"].isNull() )
    {
        rdr["Value"]    >> _value;
        rdr["DateTime"] >> _timeStamp;

        rdr["ScanInProgress"] >> scratch;
        CtiToUpper(scratch);
        _scanInProgress = (scratch == "Y");

        _insertDynamicDataFlag = false;
    }
}

/*
    Writes out the dynamic information for this monitor point.
*/
void CtiCCMonitorPoint::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( _dirty )
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

            if( Cti::Database::executeCommand( updater, CALLSITE ))
            {
                _dirty = false; // No error occurred!
            }
        }
        else
        {
            CTILOG_INFO(dout, "Inserted Monitor Point into dynamicCCMonitorBankHistory: ");


            static const string insertSql = "insert into dynamicccmonitorbankhistory values( "
                                            "?, ?, ?, ?, ?)";

            Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

            dbInserter << _deviceId
            <<  _pointId
            << _value
            << _timeStamp
            << (string)(_scanInProgress?"Y":"N");

            if( Cti::Database::executeCommand( dbInserter, CALLSITE, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
            {
                _insertDynamicDataFlag = false;
                _dirty = false; // No error occurred!
            }
        }
    }
}

void CtiCCMonitorPoint::updateNonDynamicData( const CtiCCMonitorPoint & rhs )
{
    _displayOrder       = rhs._displayOrder;
    _scannable          = rhs._scannable;
    _nInAvg             = rhs._nInAvg;
    _upperBW            = rhs._upperBW;
    _lowerBW            = rhs._lowerBW;
    _phase              = rhs._phase;
    _overrideStrategy   = rhs._overrideStrategy;
    _deviceName         = rhs._deviceName;
    _pointName          = rhs._pointName;
}

