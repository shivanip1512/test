#include "precompiled.h"

#include "cctwowaycbcpoints.h"
#include "ccid.h"
#include "database_util.h"
#include "ccutil.h"
#include "std_helper.h"
#include "row_reader.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/filtered.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/copy.hpp>

using std::string;
using std::endl;

extern unsigned long _CC_DEBUG;
using Cti::CapControl::MissingPointAttribute;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints( const long paoid, const std::string & paotype,
                                      std::unique_ptr<LastControlReason>    lastControlReason,
                                      std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   _paoid( paoid ),
        _paotype( paotype ),
        _insertDynamicDataFlag( true ),
        _dirty( true ),
        _lastControlReason( std::move( lastControlReason ) ),
        _ignoredControlReason( std::move( ignoredControlReason ) )
{
    _attributes = AttributePoint
    {
        { PointAttribute::CbcVoltage,                   LitePoint() },
        { PointAttribute::HighVoltage,                  LitePoint() },
        { PointAttribute::LowVoltage,                   LitePoint() },
        { PointAttribute::DeltaVoltage,                 LitePoint() },
        { PointAttribute::AnalogInput1,                 LitePoint() },
        { PointAttribute::Temperature,                  LitePoint() },
        { PointAttribute::RSSI,                         LitePoint() },
        { PointAttribute::IgnoredReason,                LitePoint() },
        { PointAttribute::VoltageControl,               LitePoint() },
        { PointAttribute::UvThreshold,                  LitePoint() },
        { PointAttribute::OvThreshold,                  LitePoint() },
        { PointAttribute::OVUVTrackTime,                LitePoint() },
        { PointAttribute::NeutralCurrentSensor,         LitePoint() },
        { PointAttribute::NeutralCurrentAlarmThreshold, LitePoint() },
        { PointAttribute::TimeTempSeasonOne,            LitePoint() },
        { PointAttribute::TimeTempSeasonTwo,            LitePoint() },
        { PointAttribute::VarControl,                   LitePoint() },
        { PointAttribute::UDPIpAddress,                 LitePoint() },
        { PointAttribute::UDPPortNumber,                LitePoint() },
        { PointAttribute::CapacitorBankState,           LitePoint() },
        { PointAttribute::ReCloseBlocked,               LitePoint() },
        { PointAttribute::ControlMode,                  LitePoint() },
        { PointAttribute::AutoVoltControl,              LitePoint() },
        { PointAttribute::LastControlLocal,             LitePoint() },
        { PointAttribute::LastControlRemote,            LitePoint() },
        { PointAttribute::LastControlOvUv,              LitePoint() },
        { PointAttribute::LastControlNeutralFault,      LitePoint() },
        { PointAttribute::LastControlScheduled,         LitePoint() },
        { PointAttribute::LastControlDigital,           LitePoint() },
        { PointAttribute::LastControlAnalog,            LitePoint() },
        { PointAttribute::LastControlTemperature,       LitePoint() },
        { PointAttribute::OvCondition,                  LitePoint() },
        { PointAttribute::UvCondition,                  LitePoint() },
        { PointAttribute::OpFailedNeutralCurrent,       LitePoint() },
        { PointAttribute::NeutralCurrentFault,          LitePoint() },
        { PointAttribute::BadRelay,                     LitePoint() },
        { PointAttribute::DailyMaxOps,                  LitePoint() },
        { PointAttribute::VoltageDeltaAbnormal,         LitePoint() },
        { PointAttribute::TempAlarm,                    LitePoint() },
        { PointAttribute::DSTActive,                    LitePoint() },
        { PointAttribute::NeutralLockout,               LitePoint() },
        { PointAttribute::IgnoredIndicator,             LitePoint() },
        { PointAttribute::TotalOpCount,                 LitePoint() },
        { PointAttribute::UvCount,                      LitePoint() },
        { PointAttribute::OvCount,                      LitePoint() },
        { PointAttribute::CloseOpCount,                 LitePoint() },
        { PointAttribute::OpenOpCount,                  LitePoint() },
        { PointAttribute::LastControlReason,            LitePoint() }
    };
}

long CtiCCTwoWayPoints::getPAOId() const
{
    return _paoid;
}

void CtiCCTwoWayPoints::setPAOId(long paoId)
{
    _paoid = paoId;
}

void CtiCCTwoWayPoints::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( ! _dirty )
    {
        return;
    }

    int lastControlReason = _lastControlReason->serialize( *this );
    int ignoredControlReason = _ignoredControlReason->serializeReason( *this );
    bool ignoredControlIndicator = _ignoredControlReason->serializeIndicator( *this );

    int condition = 0;
    condition |= 0x01 * !!getPointValueByAttribute(PointAttribute::UvCondition);
    condition |= 0x02 * !!getPointValueByAttribute(PointAttribute::OvCondition);

    if( !_insertDynamicDataFlag )
    {
        static const string updateSql = "update dynamiccctwowaycbc set "
                                        "recloseblocked = ?, "
                                        "controlmode = ?, "
                                        "autovoltcontrol = ?, "
                                        "lastcontrol = ?, "
                                        "condition = ?, "
                                        "opfailedneutralcurrent = ?, "
                                        "neutralcurrentfault = ?, "
                                        "badrelay = ?, "
                                        "dailymaxops = ?, "
                                        "voltagedeltaabnormal = ?, "
                                        "tempalarm = ?, "
                                        "dstactive = ?, "
                                        "neutrallockout = ?, "
                                        "ignoredindicator = ?, "
                                        "voltage = ?, "
                                        "highvoltage = ?, "
                                        "lowvoltage = ?, "
                                        "deltavoltage = ?, "
                                        "analoginputone = ?, "
                                        "temp = ?, "
                                        "rssi = ?, "
                                        "ignoredreason = ?, "
                                        "totalopcount = ?, "
                                        "uvopcount = ?, "
                                        "ovopcount = ?, "
                                        "ovuvcountresetdate = ?, "
                                        "uvsetpoint = ?, "
                                        "ovsetpoint = ?, "
                                        "ovuvtracktime = ?, "
                                        "lastovuvdatetime = ?, "
                                        "neutralcurrentsensor = ?, "
                                        "neutralcurrentalarmsetpoint = ?, "
                                        "ipaddress = ?, "
                                        "udpport = ?"
                                        " where deviceid = ?";

        Cti::Database::DatabaseWriter updater(conn, updateSql);

        updater
            << (string)(getPointValueByAttribute(PointAttribute::ReCloseBlocked)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::ControlMode)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::AutoVoltControl)?"Y":"N")
            << lastControlReason
            << condition
            << (string)(getPointValueByAttribute(PointAttribute::OpFailedNeutralCurrent)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::NeutralCurrentFault)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::BadRelay)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::DailyMaxOps)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::VoltageDeltaAbnormal)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::TempAlarm)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::DSTActive)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::NeutralLockout)?"Y":"N")
            << (string)(ignoredControlIndicator?"Y":"N")
            << getPointValueByAttribute(PointAttribute::CbcVoltage)
            << getPointValueByAttribute(PointAttribute::HighVoltage)
            << getPointValueByAttribute(PointAttribute::LowVoltage)
            << getPointValueByAttribute(PointAttribute::DeltaVoltage)
            << getPointValueByAttribute(PointAttribute::AnalogInput1)
            << getPointValueByAttribute(PointAttribute::Temperature)
            << getPointValueByAttribute(PointAttribute::RSSI)
            << ignoredControlReason
            << getPointValueByAttribute(PointAttribute::TotalOpCount)
            << getPointValueByAttribute(PointAttribute::UvCount)
            << getPointValueByAttribute(PointAttribute::OvCount)
            << _ovuvCountResetDate
            << getPointValueByAttribute(PointAttribute::UvThreshold)
            << getPointValueByAttribute(PointAttribute::OvThreshold)
            << getPointValueByAttribute(PointAttribute::OVUVTrackTime)
            << _lastOvUvDateTime
            << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor)
            << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmThreshold)
            << getPointValueByAttribute(PointAttribute::UDPIpAddress)
            << getPointValueByAttribute(PointAttribute::UDPPortNumber)
            << _paoid;

        if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
        {
            _dirty = false; // No error occured!
        }
    }
    else
    {
        CTILOG_INFO(dout, "Inserted TwoWay CBC data into DynamicCCTwoWayCBC: " << getPAOId());
        static const string insertSql = "insert into dynamiccctwowaycbc values ( "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

        dbInserter << _paoid
                 << (string)(getPointValueByAttribute(PointAttribute::ReCloseBlocked)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::ControlMode)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::AutoVoltControl)?"Y":"N")
                 << lastControlReason
                 << condition
                 << (string)(getPointValueByAttribute(PointAttribute::OpFailedNeutralCurrent)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::NeutralCurrentFault)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::BadRelay)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::DailyMaxOps)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::VoltageDeltaAbnormal)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::TempAlarm)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::DSTActive)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::NeutralLockout)?"Y":"N")
                 << (string)(ignoredControlIndicator?"Y":"N")
                 << getPointValueByAttribute(PointAttribute::CbcVoltage)
                 << getPointValueByAttribute(PointAttribute::HighVoltage)
                 << getPointValueByAttribute(PointAttribute::LowVoltage)
                 << getPointValueByAttribute(PointAttribute::DeltaVoltage)
                 << getPointValueByAttribute(PointAttribute::AnalogInput1)
                 << getPointValueByAttribute(PointAttribute::Temperature)
                 << getPointValueByAttribute(PointAttribute::RSSI)
                 << ignoredControlReason
                 << getPointValueByAttribute(PointAttribute::TotalOpCount)
                 << getPointValueByAttribute(PointAttribute::UvCount)
                 << getPointValueByAttribute(PointAttribute::OvCount)
                 << _ovuvCountResetDate
                 << getPointValueByAttribute(PointAttribute::UvThreshold)
                 << getPointValueByAttribute(PointAttribute::OvThreshold)
                 << getPointValueByAttribute(PointAttribute::OVUVTrackTime)
                 << _lastOvUvDateTime
                 << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor)
                 << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmThreshold)
                 << getPointValueByAttribute(PointAttribute::UDPIpAddress)
                 << getPointValueByAttribute(PointAttribute::UDPPortNumber);

        if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
        {
            _insertDynamicDataFlag = false;
            _dirty = false; // No error occured!
        }
    }
}

LitePoint CtiCCTwoWayPoints::getPointByAttribute(const PointAttribute & attribute) const
{
    if ( boost::optional<LitePoint> lookup = Cti::mapFind( _attributes, attribute ) )
    {
        return *lookup;
    }
    throw MissingPointAttribute( _paoid, attribute, _paotype );
}

int CtiCCTwoWayPoints::getPointIdByAttribute(const PointAttribute & attribute) const
{
    try
    {
        return getPointByAttribute(attribute).getPointId();
    }
    catch ( const MissingPointAttribute & missingAttribute )
    {
        CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
        return 0;
    }
}

PointAttribute CtiCCTwoWayPoints::getAttribute(int pointtype, int offset)
{
    OffsetAttributeMappings * selectedMap = 0;

    switch ( pointtype )
    {
        case StatusPointType:
        {
            selectedMap = &_statusOffsetAttribute;
            break;
        }
        case AnalogPointType:
        {
            selectedMap = &_analogOffsetAttribute;
            break;
        }
        case PulseAccumulatorPointType:
        {
            selectedMap = &_accumulatorOffsetAttribute;
            break;
        }
    }

    if ( selectedMap )
    {
        if ( boost::optional<PointAttribute> lookup = Cti::mapFind( *selectedMap, offset ) )
        {
            return *lookup;
        }
    }

    return PointAttribute::Unknown;
}

bool CtiCCTwoWayPoints::setTwoWayPointId(CtiPointType_t pointtype, int offset, long pointId, int stateGroupId)
{
    PointAttribute pa = getAttribute(pointtype, offset);
    if (pa == PointAttribute::Unknown)
    {
        return false;
    }
    try
    {
        LitePoint p = getPointByAttribute(pa);
        p.setPointId(pointId);
        p.setPointOffset(offset);
        p.setPointType(pointtype);
        p.setStateGroupId(stateGroupId);
        _attributes[pa] = p;
        _pointidPointtypeMap.insert(std::make_pair(pointId, pointtype));
    }
    catch ( const MissingPointAttribute & missingAttribute )
    {
        CTILOG_EXCEPTION_ERROR(dout, missingAttribute);
        return false;
    }
    return true;
}

double CtiCCTwoWayPoints::getPointValueByAttribute(PointAttribute attribute, const double sentinel) const
{
    double value = sentinel;
    _pointValues.getPointValue(getPointIdByAttribute(attribute), value);
    return value;
}

bool CtiCCTwoWayPoints::isTimestampNew(long pointID, CtiTime timestamp)
{
    CtiTime prevTime = gInvalidCtiTime;
    return ! (_pointValues.getPointTime(pointID, prevTime) && timestamp <= prevTime);
}

bool CtiCCTwoWayPoints::setTwoWayPointValue(long pointID, double value, CtiPointType_t type, CtiTime timestamp)
{
    boost::optional<CtiPointType_t> lookupType = Cti::mapFind( _pointidPointtypeMap, pointID );
    if ( lookupType && *lookupType == type && isTimestampNew( pointID, timestamp ) )
    {
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
        return true;
    }
    return false;
}

bool CtiCCTwoWayPoints::setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, StatusPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, AnalogPointType, timestamp );
}

bool CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp)
{
    return setTwoWayPointValue( pointID, value, PulseAccumulatorPointType, timestamp );
}

void CtiCCTwoWayPoints::addAllCBCPointsToRegMsg(std::set<long>& pointList)
{
    boost::copy( _attributes
                    | boost::adaptors::map_values
                    | boost::adaptors::transformed( std::mem_fun_ref( &LitePoint::getPointId ) )
                    | boost::adaptors::filtered( std::bind2nd( std::greater<long>(), 0 ) ),
                 std::inserter( pointList, pointList.begin() ) );
}

std::string CtiCCTwoWayPoints::getLastControlText()
{
    return _lastControlReason->getText( *this );
}

std::string CtiCCTwoWayPoints::getIgnoredControlText()
{
    return _ignoredControlReason->getText( *this );
}

bool CtiCCTwoWayPoints::controlRejectedByVoltageLimits()
{
    return _ignoredControlReason->controlRejectedByVoltageLimits( *this );
}

bool CtiCCTwoWayPoints::checkDeltaVoltageRejection()
{
    return _ignoredControlReason->checkDeltaVoltageRejection( *this );
}

bool CtiCCTwoWayPoints::isControlAccepted()
{
    return _ignoredControlReason->checkControlAccepted( *this );
}

struct ColumnMapping
{
    enum
    {
        Boolean,
        Long,
        Double

    } type;
    const char *name;
    const PointAttribute &attribute;
}
static const TwoWayColumns[] =
{
    { ColumnMapping::Boolean, "recloseblocked",         PointAttribute::ReCloseBlocked         },
    { ColumnMapping::Boolean, "controlmode",            PointAttribute::ControlMode            },
    { ColumnMapping::Boolean, "autovoltcontrol",        PointAttribute::AutoVoltControl        },
    { ColumnMapping::Boolean, "opfailedneutralcurrent", PointAttribute::OpFailedNeutralCurrent },
    { ColumnMapping::Boolean, "neutralcurrentfault",    PointAttribute::NeutralCurrentFault    },
    { ColumnMapping::Boolean, "badrelay",               PointAttribute::BadRelay               },
    { ColumnMapping::Boolean, "dailymaxops",            PointAttribute::DailyMaxOps            },
    { ColumnMapping::Boolean, "voltagedeltaabnormal",   PointAttribute::VoltageDeltaAbnormal   },
    { ColumnMapping::Boolean, "tempalarm",              PointAttribute::TempAlarm              },
    { ColumnMapping::Boolean, "dstactive",              PointAttribute::DSTActive              },
    { ColumnMapping::Boolean, "neutrallockout",         PointAttribute::NeutralLockout         },
    { ColumnMapping::Double,  "voltage",                PointAttribute::CbcVoltage             },
    { ColumnMapping::Double,  "highvoltage",            PointAttribute::HighVoltage            },
    { ColumnMapping::Double,  "lowvoltage",             PointAttribute::LowVoltage             },
    { ColumnMapping::Double,  "deltavoltage",           PointAttribute::DeltaVoltage           },
    { ColumnMapping::Long,    "analoginputone",         PointAttribute::AnalogInput1           },
    { ColumnMapping::Double,  "temp",                   PointAttribute::Temperature            },
    { ColumnMapping::Long,    "rssi",                   PointAttribute::RSSI                   },
    { ColumnMapping::Long,    "totalopcount",           PointAttribute::TotalOpCount           },
    { ColumnMapping::Long,    "uvopcount",              PointAttribute::UvCount                },
    { ColumnMapping::Long,    "ovopcount",              PointAttribute::OvCount                },
    { ColumnMapping::Double,  "uvsetpoint",             PointAttribute::UvThreshold            },
    { ColumnMapping::Double,  "ovsetpoint",             PointAttribute::OvThreshold            },
    { ColumnMapping::Long,    "ovuvtracktime",          PointAttribute::OVUVTrackTime          },
    { ColumnMapping::Long,    "neutralcurrentsensor",        PointAttribute::NeutralCurrentSensor         },
    { ColumnMapping::Double,  "neutralcurrentalarmsetpoint", PointAttribute::NeutralCurrentAlarmThreshold },
    { ColumnMapping::Long,    "ipaddress",              PointAttribute::UDPIpAddress           },
    { ColumnMapping::Long,    "udpport",                PointAttribute::UDPPortNumber          },
};

void CtiCCTwoWayPoints::setDynamicData(Cti::RowReader& rdr, LONG cbcState, CtiTime timestamp)
{
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::CapacitorBankState), cbcState, timestamp);

    for each( const ColumnMapping &cm in TwoWayColumns )
    {
        double value;

        if( cm.type == ColumnMapping::Boolean )
        {
            string tempBoolString;
            rdr[cm.name] >> tempBoolString;
            value = ciStringEqual(tempBoolString, "y");
        }
        else if( cm.type == ColumnMapping::Long )
        {
            long tempLong;
            rdr[cm.name] >> tempLong;
            value = tempLong;
        }
        else  //  cm.type == ColumnMapping::Double
        {
            rdr[cm.name] >> value;
        }

        _pointValues.addPointValue(getPointIdByAttribute(cm.attribute), value, timestamp);
    }

    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd

    int lastControlReason;
    rdr["lastcontrol"] >> lastControlReason;
    _lastControlReason->deserialize( *this, lastControlReason, timestamp );

    std::string ignoredControlIndicator;
    rdr["ignoredindicator"] >> ignoredControlIndicator;
    _ignoredControlReason->deserializeIndicator( *this, ciStringEqual(ignoredControlIndicator, "Y"), timestamp );

    int ignoredControlReason;
    rdr["ignoredreason"] >> ignoredControlReason;
    _ignoredControlReason->deserializeReason( *this, ignoredControlReason, timestamp );

    int condition;
    rdr["condition"] >> condition;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvCondition), !!(condition & 0x01), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvCondition), !!(condition & 0x02), timestamp);

    _insertDynamicDataFlag = false;
    _dirty = false;
}


// ------------------------------


CtiCCTwoWayPointsCbcDnp::CtiCCTwoWayPointsCbcDnp( const long paoid, const std::string & paotype,
                                                  std::unique_ptr<LastControlReason>    lastControlReason,
                                                  std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    _statusOffsetAttribute = OffsetAttributeMappings
    {
        {     1, PointAttribute::CapacitorBankState             }
    };
}


// ------------------------------


CtiCCTwoWayPointsCbc702x::CtiCCTwoWayPointsCbc702x( const long paoid, const std::string & paotype,
                                                    std::unique_ptr<LastControlReason>    lastControlReason,
                                                    std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    _analogOffsetAttribute = OffsetAttributeMappings
    {
        {     5, PointAttribute::CbcVoltage                     },
        {     6, PointAttribute::HighVoltage                    },
        {     7, PointAttribute::LowVoltage                     },
        {     8, PointAttribute::DeltaVoltage                   },
        {     9, PointAttribute::AnalogInput1                   },
        {    10, PointAttribute::Temperature                    },
        {    13, PointAttribute::RSSI                           },
        {    14, PointAttribute::IgnoredReason                  },
        { 10001, PointAttribute::VoltageControl                 },
        { 10002, PointAttribute::UvThreshold                    },
        { 10003, PointAttribute::OvThreshold                    },
        { 10004, PointAttribute::OVUVTrackTime                  },
        { 10010, PointAttribute::NeutralCurrentSensor           },
        { 10011, PointAttribute::NeutralCurrentAlarmThreshold   },
        { 10026, PointAttribute::TimeTempSeasonOne              },
        { 10042, PointAttribute::TimeTempSeasonTwo              },
        { 10068, PointAttribute::VarControl                     },
        { 20001, PointAttribute::UDPIpAddress                   },
        { 20002, PointAttribute::UDPPortNumber                  }
    };

    _statusOffsetAttribute = OffsetAttributeMappings
    {
        {     1, PointAttribute::CapacitorBankState             },
        {     2, PointAttribute::ReCloseBlocked                 },
        {     3, PointAttribute::ControlMode                    },
        {     4, PointAttribute::AutoVoltControl                },
        {     5, PointAttribute::LastControlLocal               },
        {     6, PointAttribute::LastControlRemote              },
        {     7, PointAttribute::LastControlOvUv                },
        {     8, PointAttribute::LastControlNeutralFault        },
        {     9, PointAttribute::LastControlScheduled           },
        {    10, PointAttribute::LastControlDigital             },
        {    11, PointAttribute::LastControlAnalog              },
        {    12, PointAttribute::LastControlTemperature         },
        {    13, PointAttribute::OvCondition                    },
        {    14, PointAttribute::UvCondition                    },
        {    15, PointAttribute::OpFailedNeutralCurrent         },
        {    16, PointAttribute::NeutralCurrentFault            },
        {    24, PointAttribute::BadRelay                       },
        {    25, PointAttribute::DailyMaxOps                    },
        {    26, PointAttribute::VoltageDeltaAbnormal           },
        {    27, PointAttribute::TempAlarm                      },
        {    28, PointAttribute::DSTActive                      },
        {    29, PointAttribute::NeutralLockout                 },
        {    34, PointAttribute::IgnoredIndicator               }
    };

    _accumulatorOffsetAttribute = OffsetAttributeMappings
    {
        {     1, PointAttribute::TotalOpCount                   },
        {     2, PointAttribute::UvCount                        },
        {     3, PointAttribute::OvCount                        }
    };
}


// ------------------------------


CtiCCTwoWayPointsCbc802x::CtiCCTwoWayPointsCbc802x( const long paoid, const std::string & paotype,
                                                    std::unique_ptr<LastControlReason>    lastControlReason,
                                                    std::unique_ptr<IgnoredControlReason> ignoredControlReason )
    :   CtiCCTwoWayPoints( paoid, paotype, std::move( lastControlReason ), std::move( ignoredControlReason ) )
{
    _analogOffsetAttribute = OffsetAttributeMappings
    {
        {     2, PointAttribute::LastControlReason              },
        {    12, PointAttribute::CbcVoltage                     },
        {   114, PointAttribute::IgnoredReason                  },
        { 10001, PointAttribute::OvThreshold                    },
        { 10002, PointAttribute::UvThreshold                    },
        { 10318, PointAttribute::VoltageControl                 }
    };

    _statusOffsetAttribute = OffsetAttributeMappings
    {
        {     1, PointAttribute::CapacitorBankState             },
        {     3, PointAttribute::NeutralLockout                 },
        {    72, PointAttribute::VoltageDeltaAbnormal           },
        {    84, PointAttribute::ReCloseBlocked                 },
        {    86, PointAttribute::ControlMode                    },
        {    89, PointAttribute::AutoVoltControl                }
    };

    _accumulatorOffsetAttribute = OffsetAttributeMappings
    {
        {     1, PointAttribute::TotalOpCount                   },
        {     2, PointAttribute::OvCount                        },
        {     3, PointAttribute::UvCount                        },
        {     4, PointAttribute::CloseOpCount                   },
        {     5, PointAttribute::OpenOpCount                    }
    };
}


// ------------------------------


CtiCCTwoWayPoints * CtiCCTwoWayPointsFactory::Create( const long paoID, const std::string & paoType )
{
    if ( stringContainsIgnoreCase( paoType, "CBC 702" ) )
    {
        return new CtiCCTwoWayPointsCbc702x( paoID, paoType,
                                             std::make_unique<LastControlReasonCbc702x>(),
                                             std::make_unique<IgnoredControlReasonCbc702x>() );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC 802" ) )
    {
        return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                             std::make_unique<LastControlReasonCbc802x>(),
                                             std::make_unique<IgnoredControlReasonCbc802x>() );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC DNP" ) )
    {
        return new CtiCCTwoWayPointsCbcDnp( paoID, paoType,
                                            std::make_unique<LastControlReasonCbcDnp>(),
                                            std::make_unique<IgnoredControlReasonCbcDnp>() );
    }

    // Apparently 1-way devices need one of these guys even though they don't use it for anything,
    // returning a NULL here gives null pointer exceptions in 1-way code. Original behavior gave a set
    //  of CBC8000 points to 1-way devices, so maintain that behavior.
    // return 0;
    return new CtiCCTwoWayPointsCbc802x( paoID, paoType,
                                         std::make_unique<LastControlReasonCbc802x>(),
                                         std::make_unique<IgnoredControlReasonCbc802x>() );
}

